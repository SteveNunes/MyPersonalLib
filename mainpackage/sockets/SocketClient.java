package sockets;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import util.Misc;
import util.Timer;

/**
 * Classe cliente para comunicação via socket. Gerencia a conexão, envio e
 * recebimento de dados de forma assíncrona. Suporta tanto envio de texto quanto
 * de dados genéricos (bytes).
 */
public class SocketClient {

	private static ExecutorService executorService = null;

	private Map<String, String> mark = new HashMap<>();
	private Socket socket;
	private DataInputStream dis; // Declara o DataInputStream como campo da classe
	private DataOutputStream dos; // Declara o DataOutputStream como campo da classe
	private SocketStatus socketStatus;

	private Consumer<SocketClient> onConnect;
	private Consumer<Exception> onConnectError;
	private Consumer<SocketClient> onDisconnect;
	private Consumer<Exception> onDisconnectError;

	private Consumer<String> onDataReceive;
	private Consumer<Exception> onDataReceiveError;
	private Consumer<String> onDataSent;
	private Consumer<Exception> onDataSendError;

	private PrintWriter writer;

	static {
		Misc.addShutdownEvent(SocketClient::close);
	}

	public SocketClient() {
		if (executorService == null)
			executorService = Executors.newCachedThreadPool();
		socketStatus = SocketStatus.DISCONNECTED;
	}

	/**
	 * Construtor para usar um socket existente (modo servidor).
	 */
	public SocketClient(Socket socket) {
		if (executorService == null)
			executorService = Executors.newCachedThreadPool();
		this.socket = socket;
		executorServiceExecute(() -> setSocket(socket));
	}

	private static void executorServiceExecute(Runnable runnable) {
		if (executorService != null && !executorService.isTerminated() && !executorService.isShutdown())
			executorService.execute(runnable);
	}

	public void connect(String address, int port) {
		connect(address, port, 0);
	}

	public void connect(String address, int port, int timeoutInMs) {
		if (socketStatus == SocketStatus.CONNECTING)
			throw new RuntimeException("SocketClient is connecting");
		if (socketStatus == SocketStatus.CONNECTED)
			throw new RuntimeException("SocketClient is already connected");
		executorServiceExecute(() -> {
			socketStatus = SocketStatus.CONNECTING;
			try {
				if (timeoutInMs > 0) {
					socket = new Socket();
					socket.connect(new InetSocketAddress(address, port), timeoutInMs);
				}
				else {
					socket = new Socket(address, port);
				}
				setSocket(socket);
			}
			catch (Exception e) {
				socketStatus = SocketStatus.DISCONNECTED;
				if (!executeConsumer(onConnectError, e))
					handleError("Unable to connect", e);
			}
		});
	}

	private void setSocket(Socket socket) {
		try {
			// Ensure socket options to help detect dead peers
			//socket.setKeepAlive(true);
			//socket.setTcpNoDelay(true);
			// Set a read timeout so we can react periodically (e.g. to send a ping) instead of blocking forever
			//socket.setSoTimeout(45000); // 45 seconds

			writer = new PrintWriter(socket.getOutputStream(), true);
			socketStatus = SocketStatus.CONNECTED;
			executeConsumer(onConnect, this);
			// Create a single periodic ping timer for this connection
			ping();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				String line;
				while (socketStatus == SocketStatus.CONNECTED) {
					try {
						ping();
						if ((line = reader.readLine()) != null) {
							executeConsumer(onDataReceive, line);
						}
						else {
							// peer closed stream
							connectionLost();
							break;
						}
					}
					catch (SocketTimeoutException ste) {
						// read timed out - use this opportunity to check the socket and send a ping
						if (socket == null || socket.isClosed() || !socket.isConnected()) {
							connectionLost();
							break;
						}
						ping();
						continue; // keep waiting for data
					}
				}
			}
			catch (IOException e) {
				// IOException during read -> connection lost
				connectionLost();
			}
			catch (Exception e) {
				if (!executeConsumer(onDataReceiveError, e))
					handleError("Error on receiving data", e);
			}
		}
		catch (Exception e) {
			try {
				socket.close();
			}
			catch (Exception ex) {}
			socketStatus = SocketStatus.DISCONNECTED;
			if (!executeConsumer(onConnectError, e))
				handleError("Unable to create writer", e);
			return;
		}
	}

	private void connectionLost() {
		// mark as disconnected and close underlying resources
		socketStatus = SocketStatus.DISCONNECTED;
		try {
			if (writer != null) {
				writer.close();
				writer = null;
			}
		}
		catch (Exception e) {}
		try {
			if (socket != null && !socket.isClosed())
				socket.close();
			socket = null;
		}
		catch (Exception e) {}
		executeConsumer(onDisconnect, this);
	}

	private void handleError(String message, Exception e) {
		throw new RuntimeException(message);
	}

	private <T> boolean executeConsumer(Consumer<T> consumer, T param) {
		if (consumer != null) {
			consumer.accept(param);
			return true;
		}
		if (param instanceof Exception)
			((Exception) param).printStackTrace();
		return false;
	}

	private void ping() {
		Timer.createTimer("socketPing@" + hashCode(), Duration.ofSeconds(15), () -> {
			executorServiceExecute(() -> {
				try {
					if (socket != null && !socket.isClosed() && socket.isConnected()) {
						socket.getOutputStream().write(new byte[0]);
						socket.getOutputStream().flush();
						Timer.createTimer("socketPing2@" + hashCode(), Duration.ofSeconds(1), () -> ping());
					}
				}
				catch (Exception e) {
					connectionLost();
				}
			});
		});
	}
	
	public void sendData(String data) {
		if (socketStatus != SocketStatus.CONNECTED)
			throw new RuntimeException("Unable to send data because socket is not connected");
		// refresh ping schedule
		ping();
		executorServiceExecute(() -> {
			try {
				if (writer != null) {
					writer.println(data);
					writer.flush();
					if (onDataSent != null)
						onDataSent.accept(data);
				}
				else if (!executeConsumer(onDataSendError, new RuntimeException("Writer is null, cannot send data")))
					handleError("Unable to send data due to null writer", new RuntimeException("Writer is null"));
			}
			catch (Exception e) {
				// mark connection lost when send fails
				connectionLost();
				if (!executeConsumer(onDataSendError, e))
					handleError("Unable to send data", e);
			}
		});
	}

	public void disconnect() {
		if (socketStatus == SocketStatus.DISCONNECTED) {
			throw new RuntimeException("Already disconnected");
		}
		socketStatus = SocketStatus.DISCONNECTED;
		executorServiceExecute(() -> {
			try {
				if (socket != null && !socket.isClosed()) {
					if (dos != null)
						dos.close();
					if (dis != null)
						dis.close();
					if (writer != null)
						writer.close();
					socket.close();
					executeConsumer(onDisconnect, this);
				}
			}
			catch (Exception e) {
				if (!executeConsumer(onDisconnectError, e))
					handleError("Unable to disconnect", e);
			}
		});
	}

	public SocketStatus getSocketStatus() {
		return socketStatus;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setOnDataReceiveEvent(Consumer<String> onDataReceive) {
		this.onDataReceive = onDataReceive;
	}

	public void setOnDataReceiveErrorEvent(Consumer<Exception> onDataReceiveError) {
		this.onDataReceiveError = onDataReceiveError;
	}

	public void setOnDataSentEvent(Consumer<String> onDataSent) {
		this.onDataSent = onDataSent;
	}

	public void setOnDataSendErrorEvent(Consumer<Exception> onDataSendError) {
		this.onDataSendError = onDataSendError;
	}

	// Eventos principais
	public void setOnConnectEvent(Consumer<SocketClient> onConnect) {
		this.onConnect = onConnect;
	}

	public void setOnConnectErrorEvent(Consumer<Exception> onConnectError) {
		this.onConnectError = onConnectError;
	}

	public void setOnDisconnectEvent(Consumer<SocketClient> onDisconnect) {
		this.onDisconnect = onDisconnect;
	}

	public void setOnDisconnectErrorEvent(Consumer<Exception> onDisconnectError) {
		this.onDisconnectError = onDisconnectError;
	}

	private static void close() {
		if (executorService != null && !executorService.isShutdown() && !executorService.isTerminated())
			executorService.shutdownNow();
	}

	public void setMark(String mark) {
		addMark("DefaultMark", mark);
	}

	public void addMark(String name, String mark) {
		this.mark.put(name, mark);
	}

	public String getMark() {
		return getMark("DefaultMark");
	}

	public String getMark(String name) {
		return mark.get(name);
	}

	public void removeMark() {
		removeMark("DefaultMark");
	}

	public void removeMark(String name) {
		mark.remove(name);
	}
	
}
