package util;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import enums.SpeechQueue;
import enums.TTSVoice;
import sockets.SocketClient;

public class LocalTTS {
	
	private static int port;
	private static boolean killTaskAtClose;
	private static List<TTSRequest> ttsQueue = new ArrayList<>();
	public static ExecutorService executorService = null;
	private static int delayBetweenQueueedMessages = 1000;
	private static boolean isBusy = false;

  static {
  	Misc.addShutdownEvent(LocalTTS::close);
  }
  
	private static void close() {
		forceToCloseTTSServer(true);
	}

	public static void forceToCloseTTSServer() {
		forceToCloseTTSServer(false);
	}
	
	public static void forceToCloseTTSServer(boolean force) {
		if (!force && !killTaskAtClose)
			return;
		try {
			new ProcessBuilder("taskkill", "/F", "/IM", "JavaTTSServer.exe").start().waitFor();
			if (executorService != null) {
				executorService.shutdownNow();
				executorService = null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setDelayBetweenMessages(int delayInMillis) {
		sendToServer("SET_DELAY " + delayInMillis);
	}
	
	private static void sendToServer(String text) {
		SocketClient socket = new SocketClient();
		socket.connect("localhost", port);
		socket.setOnConnectEvent(s -> s.sendData(text));
		socket.setOnConnectErrorEvent(s -> Timer.createTimer("sendToServerAgain@" + socket.hashCode(), Duration.ofSeconds(1), () -> sendToServer(text)));
		socket.setOnDataReceiveEvent(data -> {
			if (data.equals("PING"))
				socket.sendData("PONG");
		});
	}
	
	public static void initAlternativeTTSServer(int port) {
		SocketClient socket = new SocketClient();
		socket.connect("localhost", port);
		socket.setOnConnectEvent(s -> {
			if (executorService == null)
				executorService = Executors.newCachedThreadPool();
			LocalTTS.port = port;
			killTaskAtClose = false;
			s.disconnect();
		});
		socket.setOnConnectErrorEvent(s -> {
			throw new RuntimeException("Unable to connect to alternative TTS Server at port " + port);
		});
	}
	
	public static void initTTSServer() {
		initTTSServer(12345);
	}
	
	public static void initTTSServer(int port) {
		if (executorService == null)
			executorService = Executors.newCachedThreadPool();
		LocalTTS.port = port;
		if (DesktopUtils.isProcessRunning("javaTTSServer.exe")) {
			killTaskAtClose = false;
			return;
		}
		try {
			killTaskAtClose = true;
			new ProcessBuilder(".\\javaTTSServer.exe", "PORT=" + port, "DEBUG=true").start();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getVoiceDuration(String text, TTSVoice voice, int rate) {
		long l = System.currentTimeMillis();
		speech(text, voice, 0, rate, SpeechQueue.NO_QUEUE, true);
		return (int)(System.currentTimeMillis() - l);
	}

	public static void speech(String text, TTSVoice voice, int gain, int rate, SpeechQueue queue) {
		speech(text, voice, gain, rate, queue, false);
	}

	public static void speech(String text, TTSVoice voice, int gain, int rate, SpeechQueue queue, boolean testDuration) {
		if (text == null || text.isBlank())
			return;
		text = text.replace("’", "").replace("\'", "").replace("\"", "").toLowerCase();
		TTSRequest request = new TTSRequest(text, voice, gain, rate, queue, testDuration);
		if (request.queue == SpeechQueue.PRIORITY || request.queue == SpeechQueue.REGULAR) {
			if (request.queue == SpeechQueue.REGULAR || !isBusy)
				ttsQueue.add(request);
			else
				ttsQueue.add(0, request);
			if (isBusy)
				return;
			isBusy = true;
		}
		speech(request);
	}
	
	public static void stopAll() {
		sendToServer("STOP_ALL");
	}
	
	private static void speech(TTSRequest request) {
		Runnable runnable = () -> {
			int wait = request.testDuration ? -1 : request.queue.getValue();
			sendToServer(wait + "¡" + request.rate + "¡" + request.gain + "¡" + request.voice.getSystemName() + "¡" + request.text);
			Misc.sleep(delayBetweenQueueedMessages);
			if (ttsQueue.contains(request)) {
				ttsQueue.remove(request);
				if (!ttsQueue.isEmpty())
					speech(ttsQueue.get(0));
				else
					isBusy = false;
			}
		};
		if (request.testDuration)
			runnable.run();
		else
			executorServiceExecute(runnable);
	}

	private static void executorServiceExecute(Runnable runnable) {
		if (executorService != null && !executorService.isTerminated() && !executorService.isShutdown())
			executorService.execute(runnable);
	}

}

class TTSRequest {
	
	private static long globalId = 0;
	
	public String text;
	public TTSVoice voice;
	public int gain;
	public int rate;
	public SpeechQueue queue;
	public boolean testDuration;
	private long id;
	
	public TTSRequest(String text, TTSVoice voice, int gain, int rate, SpeechQueue queue, boolean testDuration) {
		this.text = text;
		this.voice = voice;
		this.gain = gain;
		this.rate = rate;
		this.queue = queue;
		this.testDuration = testDuration;
		id = globalId++;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TTSRequest other = (TTSRequest) obj;
		return id == other.id;
	}
	
}
