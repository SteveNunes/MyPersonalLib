package sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.Misc;

public class SockServer {
	
	private String mark;
	private List<SockClient> clients = new ArrayList<>();
	private ServerSocket serverSocket;
	private int serverPort;
  private Boolean isClosed;
  private Boolean isListening;
  private String socketName;
	private SocketEvents serverSocketEvents;
	private SocketEvents newClientSocketEvents;
  
  public SockServer(int listeningPort, String socketName, SocketEvents serverSocketEvents, SocketEvents newClientSocketEvents) throws IOException {
  	this.socketName = socketName;
		this.serverSocketEvents = serverSocketEvents;
		this.newClientSocketEvents = newClientSocketEvents;
		mark = null;
		serverPort = listeningPort;
		startListening();
	}

  public SockServer(int listeningPort, SocketEvents serverSocketEvents, SocketEvents newClientSocketEvents) throws IOException
  	{ this(listeningPort, null, serverSocketEvents, newClientSocketEvents); }
  
  public String getMark()
  	{ return mark; }
  
  public void setMark(String mark)
  	{ this.mark = mark; }
  
	public String getSocketName()
		{ return socketName; }
	
	public void setSocketName(String socketName)
		{ this.socketName = socketName; }
	
	void wasDisconnected(SockClient client, Exception e) {
		if (clients.contains(client)) {
			if (serverSocketEvents != null && serverSocketEvents.getOnSocketDisconnect() != null)
				serverSocketEvents.getOnSocketDisconnect().accept(client, e);
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
				catch (Exception e) {
					if (serverSocketEvents != null && serverSocketEvents.getOnSocketListenError() != null)
						serverSocketEvents.getOnSocketListenError().accept(serverSocket, e);
					else
						e.printStackTrace();
					return;
				  /*
					 * server.bind(new InetSocketAddress("192.168.0.1", 0));
					 * Associa o socket aceito á um IP.
					 * O .bind() já é feito automaticamente com o .accept().
					 * Só deve ser usado quando necessário (ainda não sei o caso)
				  */
				}
				while (!isClosed && isListening) {
					try {
						SockClient sockClient = new SockClient(serverSocket.accept(), newClientSocketEvents);
						sockClient.linkToSockServer(thisServer);
						clients.add(sockClient);
						if (serverSocketEvents != null && serverSocketEvents.getOnSocketAccept() != null)
							serverSocketEvents.getOnSocketAccept().accept(sockClient);
					}
					catch (Exception e) {}
				}
				isListening = false;
	    }
		}.start();
		if (serverSocket != null && !serverSocket.isClosed()) {
			try
				{ serverSocket.close(); }
			catch (IOException e)
				{ e.printStackTrace(); }
		}
	}

  public void stopListening()
  	{ isListening = false; }

	public void closeServer() throws IOException {
  	if (!isClosed) {
  		isClosed = true;
  		serverSocket.close();
  		while (isListening) // Aguarda a thread de listening parar
  			Misc.sleep(100);
  		for (SockClient sc : clients)
  			sc.close();
  		clients.clear();
  	}
  }
	  
  public void closeClient(SockClient sock) {
  	if (clients.contains(sock)) {
  		try
  			{ sock.getSocket().close(); }
  		catch (Exception e) {}
  		clients.remove(sock);
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