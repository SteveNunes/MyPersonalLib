package sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import util.Misc;
import util.Timer;

/**
 * Servidor de socket que aceita conexões de clientes. Gerencia o ciclo de vida
 * do servidor, aceita novas conexões e distribui eventos. Permite o tratamento
 * de dados genéricos (bytes) e texto.
 */
public class SocketServer {

	private static ExecutorService executorService = null;
	private static Set<SocketClient> sockets = new HashSet<>();
	private static int pingClientsInterval = 5;
	private static List<SocketServer> servers = new CopyOnWriteArrayList<>();
	private static boolean isShutdown = false;
	
	private Map<String, String> mark = new HashMap<>();
	private ServerSocket serverSocket;
	private Socket socket;

	// Eventos de servidor
	private BiConsumer<SocketServer, Exception> onStartServerError;
	private Consumer<Exception> onClientSocketAcceptError;
	private Consumer<SocketClient> onClientConnected;
	private Consumer<SocketClient> onClientDisconnected;

	private int port;
	private boolean isRunning = false;
	
	static {
		Misc.addShutdownEvent(() -> {
			isShutdown = true;
			for (SocketServer server : servers) {
				try {
					server.close();
				}
				catch (Exception e) {}
			}
			executorService.shutdownNow();
		});
	}

	public SocketServer(int port) {
		if (executorService == null)
			executorService = Executors.newCachedThreadPool();
		this.port = port;
		servers.add(this);
	}
	
	public static int getPingClientsInterval() {
		return pingClientsInterval;
	}

	public static void setPingClientsInterval(int intervalInSecs) {
		pingClientsInterval = intervalInSecs;
	}
	
	private static void executorServiceExecute(Runnable runnable) {
		if (executorService != null && !executorService.isTerminated() && !executorService.isShutdown())
			executorService.execute(runnable);
	}

	public void setOnStartErrorServer(BiConsumer<SocketServer, Exception> onStartServerError) {
		this.onStartServerError = onStartServerError;
	}

	public void setOnClientConnected(Consumer<SocketClient> onClientConnected) {
		this.onClientConnected = onClientConnected;
	}

	public void setOnClientSocketAcceptError(Consumer<Exception> onClientSocketAcceptError) {
		this.onClientSocketAcceptError = onClientSocketAcceptError;
	}

	public void setOnClientDisconnected(Consumer<SocketClient> onClientDisconnected) {
		this.onClientDisconnected = onClientDisconnected;
	}

	public void startListening() throws IOException {
		if (isRunning)
			throw new RuntimeException("Servidor já está em execução na porta " + port);
		executorServiceExecute(() -> {
			try {
				serverSocket = new ServerSocket(port);
				isRunning = true;
	
				pingClients();
				while (isRunning) {
					try {
						Socket socket = serverSocket.accept();
						SocketClient client = new SocketClient(socket);
						sockets.add(client);
						if (onClientConnected != null)
							onClientConnected.accept(client);
					}
					catch (IOException e) {
						if (isRunning) {
							if (onClientSocketAcceptError != null)
								onClientSocketAcceptError.accept(e);
							else
								e.printStackTrace();
						}
					}
				}
			}
			catch (Exception e) {
				if (isRunning) {
					if (onStartServerError != null)
						onStartServerError.accept(this, e);
					else
						e.printStackTrace();
				}
			}
		});
	}

	private void pingClients() {
		executorServiceExecute(() -> {
			synchronized (sockets) {
				sockets.removeIf(c -> {
					try {
						c.getSocket().sendUrgentData(0xFF);
						return false;
					}
					catch (Exception e) {
						if (onClientDisconnected != null)
							onClientDisconnected.accept(c);
						return true;
					}
				});
			}
			if (isRunning)
				Timer.createTimer("pingClients", Duration.ofSeconds(pingClientsInterval), this::pingClients);
		});
	}

	public Socket getSocket() {
		return socket;
	}

	private void validate() {
		if (serverSocket == null)
			throw new RuntimeException("O servidor foi encerrado previamente.");
	}

	public void stopListening() {
		if (isRunning) {
			isRunning = false;
			try {
				serverSocket.close();
			}
			catch (IOException e) {}
			serverSocket = null;
		}
	}

	public void close() {
		validate();
		if (serverSocket != null && !serverSocket.isClosed()) {
			for (SocketClient client : sockets)
				if (client.getSocketStatus() != SocketStatus.DISCONNECTED)
					client.disconnect();
			sockets.clear();
		}
		stopListening();
		if (!isShutdown)
			servers.remove(this);
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
