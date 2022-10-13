package sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SockServer {
	
	private List<SockClient> clients = new ArrayList<>();
	private ServerSocket serverSocket;
	private int serverPort;
  private Boolean isClosed;
  private Boolean isListening;
  private String socketName;
  @SuppressWarnings("unused")
	private SocketEvents socketEvents;
  
  public SockServer(int listeningPort, String socketName, SocketEvents socketEvents) throws IOException {
  	this.socketName = socketName;
		this.socketEvents = socketEvents;
		serverPort = listeningPort;
		startListening();
	}

  public SockServer(int listeningPort, SocketEvents socketEvents) throws IOException
  	{ this(listeningPort, null, socketEvents); }
  
	public String getSocketName()
		{ return socketName; }
	
	public void setSocketName(String socketName)
		{ this.socketName = socketName; }
	
	void wasDisconnected(SockClient client) {
		if (clients.contains(client)) {
			if (socketEvents.getOnSocketClose() != null)
				socketEvents.getOnSocketClose().accept(client);
			clients.remove(client);
		}
	}

  private void startListening() {
		isClosed = false;
		isListening = true;
		final SockServer thisServer = this;
		
		new Thread() {
			@Override
	    public void run() {
				try
					{ serverSocket = new ServerSocket(serverPort); }
				catch (IOException e) {
					if (socketEvents.getOnSocketListenError() != null)
						socketEvents.getOnSocketListenError().accept(serverSocket, e);
					else {
						System.err.println("Unable to create listener socket at port " + serverPort);
						e.printStackTrace();
					}
					return;
				  /*
					 * server.bind(new InetSocketAddress("192.168.0.1", 0));
					 * Associa o socket aceito á um IP.
					 * O .bind() já é feito automaticamente com o .accept().
					 * Só deve ser usado quando necessário (ainda não sei o caso)
				  */
				}
				while(!isClosed && isListening) {
					SockClient sockClient = null;
					try {
						Socket client = serverSocket.accept();
						sockClient = new SockClient(client, socketEvents);
						sockClient.linkToSockServer(thisServer);
						clients.add(sockClient);
						if (socketEvents.getOnSocketAccept() != null)
							socketEvents.getOnSocketAccept().accept(sockClient);
					}
					catch (IOException e) {
						if (socketEvents.getOnSocketAcceptError() != null)
							socketEvents.getOnSocketAcceptError().accept(sockClient, e);
						else {
							System.err.println("Unable to accept socket: " + sockClient);
							e.printStackTrace();
						}
					}
				}
				isListening = false;
	    }
		}.start();
	}

  public void stopListening()
  	{ isListening = false; }

	public void closeServer() throws IOException {
  	if (!isClosed) {
  		isClosed = true;
  		while (isListening) // Aguarda a thread de listening parar
				try
  				{ Thread.sleep(100); }
				catch (Exception e) {}
  		for (SockClient sc : clients)
  			sc.close();
  		clients.clear();
  	}
  }
	  
  public ServerSocket getServerSocket()
  	{ return serverSocket; }
  
  public List<SockClient> getUnmodifiableClientList()
  	{ return Collections.unmodifiableList(clients); }
  
  public int getTotalClients()
  	{ return clients.size(); }
  
  public int getServerPort()
  	{ return serverPort; }
  
  public void appendDataToSendTo(SockClient sock, String data)
  	{ sock.appendDataToSend(data); }
  
  public void sendAppendedData(SockClient sock)
		{ sock.sendAppendedData(); }

  public void sendDataTo(SockClient sock, String data)
  	{ sock.sendData(data); }
  
  public void sendDataToAll(String data) {
  	if (!isClosed)
	  	for (SockClient sock : clients)
	  		sendDataTo(sock, data);
  }

	@Override
	public String toString()
		{ return "[SockServer name=" + socketName + "]"; }

}