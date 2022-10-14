package sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SockClient {
	
	private List<String> dataToSend;
	private InputStreamReader imputStreamReader;
	private PrintStream printStream;
	private Socket socket;
	private SockServer linkedToServer;
	private String socketName;
	@SuppressWarnings("unused")
	private SocketEvents socketEvents;

	public SockClient(Socket socket, String socketName, SocketEvents socketEvents) throws IOException
		{ newSocket(socket, socketName, socketEvents);	}
	
	private void newSocket(Socket socket, String socketName, SocketEvents socketEvents) throws IOException {
  	this.socketName = socketName;
		this.socketEvents = socketEvents;
		this.socket = socket;
		dataToSend = new ArrayList<>();
		linkedToServer = null;
		imputStreamReader = new InputStreamReader(socket.getInputStream());
		printStream = new PrintStream(socket.getOutputStream());
		SockClient sockClient = this;
		
		new Thread() {
			@Override
	    public void run() {
				
				BufferedReader reader = new BufferedReader(imputStreamReader);
				while (true) {
					try {
						String data = reader.readLine();
						if (data == null) {
							if (linkedToServer != null)
								linkedToServer.wasDisconnected(sockClient);
							else if (socketEvents.getOnSocketClose() != null)
								socketEvents.getOnSocketClose().accept(sockClient);
							else
								System.err.println("Socket: " + sockClient + " was disconnected from the server");
							break;
						}
						else if (socketEvents.getOnSocketRead() != null)
							socketEvents.getOnSocketRead().accept(sockClient, data);
					}
					catch (Exception e) {
						if (socketEvents.getOnSocketReadError() != null)
							socketEvents.getOnSocketReadError().accept(sockClient, e);
						else {
							System.err.println("Error on reading data at socket: " + sockClient);
							e.printStackTrace();
						}
						break;
					}
				}
			}
		}.start();
	}

	public SockClient(Socket socket, SocketEvents socketEvents) throws IOException
		{ this(socket, null, socketEvents); }
	
	public SockClient(String ip, int port, String socketName, SocketEvents socketEvents) {
		try {
			newSocket(new Socket(ip, port), socketName, socketEvents);
			if (socketEvents.getOnSocketOpen() != null)
				socketEvents.getOnSocketOpen().accept(this);
		}
		catch (IOException e) {
			if (socketEvents.getOnSocketOpenError() != null)
				socketEvents.getOnSocketOpenError().accept(this, new IOException("Unable to connect to the server"));
			else
				throw new RuntimeException("Unable to connect to the server (IP=" + ip + ", port=" + port + ")");
		}
	}
	
	public SockClient(String ip, int port, SocketEvents socketEvents)
		{ this(ip, port, null, socketEvents); }
	
	public void linkToSockServer(SockServer sockServer)
		{ linkedToServer = sockServer; }

	public String getSocketName()
		{ return socketName; }
	
	public void setSocketName(String socketName)
		{ this.socketName = socketName; }
	
	public InetAddress getLocalAddress()
		{ return socket.getLocalAddress(); }
	
	public InetAddress getInetAddress()
		{ return socket.getInetAddress(); }

	public int getPort()
		{ return socket.getPort(); }

	public int getLocalPort()
		{ return socket.getLocalPort(); }

	public Socket getSocket()
		{ return socket; }
	
	public PrintStream  getPrintStream()
		{ return printStream; }
	
	public void appendDataToSend(String data) 
		{ dataToSend.add(data); }
	
	public void sendAppendedData() {
		for (String s : dataToSend)
			sendData(s);
		dataToSend.clear();
	}
	
	public void sendData(String data)
		{ printStream.println(data); }
	
	public void close() throws IOException
		{ socket.close(); }
	
	@Override
	public String toString()
		{ return "[SockClient name=" + socketName + "]"; }

}
