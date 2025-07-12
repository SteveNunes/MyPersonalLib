package sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SocketServer {

	private static Set<SocketClient> sockets = new HashSet<>();
	
	private Map<String, String> mark = new HashMap<>();
	private ServerSocket serverSocket;
	private SocketClient client;
	private Socket socket;
	private BiConsumer<SocketServer, Exception> onStartServerError;
	private Consumer<Exception> onClientSocketAcceptError;
	private Consumer<SocketClient> onClientConnected;
	private BiConsumer<SocketClient, String> onClientMessageReceived;
	private Consumer<SocketClient> onClientDisconnected;
	private int port;
	private boolean isRunning = false;

	public SocketServer(int port) {
		this.port = port;
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

	public void setOnClientMessageReceived(BiConsumer<SocketClient, String> onClientMessageReceived) {
		this.onClientMessageReceived = onClientMessageReceived;
	}

	public void setOnClientDisconnected(Consumer<SocketClient> onClientDisconnected) {
		this.onClientDisconnected = onClientDisconnected;
	}

	public void start() throws IOException {
		if (isRunning) {
			System.out.println("Servidor já está em execução na porta " + port);
			return;
		}
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Servidor iniciado na porta " + port);
			isRunning = true;
	
			while (isRunning) {
				try {
					Socket c = serverSocket.accept();
					System.out.println("Cliente conectado (" + c.getInetAddress().getHostAddress() + ")");
					client = new SocketClient(c);
					sockets.add(client);
					client.setOnDataReceiveEvent(data -> {
						System.out.println("Dado recebido de " + c.getInetAddress().getHostAddress() + " -> " + data);
						if (onClientMessageReceived != null)
							onClientMessageReceived.accept(client, data);
					});
					client.setOnDisconnectEvent(socketClient -> {
						System.out.println("Cliente desconectado (" + c.getInetAddress().getHostAddress() + ")");
						if (onClientDisconnected != null)
							onClientDisconnected.accept(client);
					});
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
			if (isRunning)
				close();
		}
		catch (Exception e) {
			if (isRunning) {
				if (onStartServerError != null)
					onStartServerError.accept(this, e);
				else
					e.printStackTrace();
			}
		}
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	private void validate() {
		if (!isRunning)
			throw new RuntimeException("O servidor foi encerrado previamente.");
	}
	
	public void close() {
		validate();
		isRunning = false;
		System.out.println("Encerrando servidor...");
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				for (SocketClient client : sockets)
					if (client.getSocketStatus() != SocketStatus.DISCONNECTED)
						client.disconnect();
				serverSocket.close();
				sockets.clear();
			}
		}
		catch (IOException e) {
			System.err.println("Erro ao fechar socket do servidor: " + e.getMessage());
		}
		System.out.println("Servidor encerrado.");
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