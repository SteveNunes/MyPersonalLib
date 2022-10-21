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
	private PrintStream printStream;
	private Socket socket;
	private SockServer linkedToServer;
	private String socketName;
	private SocketEvents socketEvents;

	public SockClient(Socket socket, String socketName, SocketEvents socketEvents) throws IOException
		{ newSocket(socket, socketName, socketEvents); }
	
	public SockClient(Socket socket, SocketEvents socketEvents) throws IOException
		{ this(socket, null, socketEvents); }

	public SockClient(String ip, int port, String socketName, SocketEvents socketEvents) {
		try {
			newSocket(new Socket(ip, port), socketName, socketEvents);
			if (socketEvents != null && socketEvents.getOnSocketOpen() != null)
				socketEvents.getOnSocketOpen().accept(this);
		}
		catch (IOException e) {
			if (socketEvents != null && socketEvents.getOnSocketOpenError() != null)
				socketEvents.getOnSocketOpenError().accept(this, e);
			else
				e.printStackTrace();
		}
	}
	
	public SockClient(String ip, int port, SocketEvents socketEvents)
		{ this(ip, port, null, socketEvents); }
	
	private void newSocket(Socket socket, String socketName, SocketEvents socketEvents) throws IOException {
  	this.socketName = socketName;
		this.socketEvents = socketEvents;
		this.socket = socket;
		dataToSend = new ArrayList<>();
		linkedToServer = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		printStream = new PrintStream(socket.getOutputStream());
		SockClient sockClient = this;
		new Thread() {
			@Override
	    public void run() {
				while (true) {
					try {
						String data = reader.readLine();
						if (data == null) {
							disconnected(sockClient, new RuntimeException(sockClient + " - connection close by the otherside"));
							break;
						}
						if (socketEvents != null && socketEvents.getOnSocketRead() != null)
							socketEvents.getOnSocketRead().accept(sockClient, data);
					}
					catch (Exception e) {
						disconnected(sockClient, e);
						break;
					}
				}
			}
		}.start();
	}

	private void disconnected(SockClient sockClient, Exception e) {
		if (linkedToServer != null)
			linkedToServer.wasDisconnected(sockClient, e);
		else if (socketEvents != null && socketEvents.getOnSocketDisconnect() != null)
			socketEvents.getOnSocketDisconnect().accept(sockClient, e);
	}

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