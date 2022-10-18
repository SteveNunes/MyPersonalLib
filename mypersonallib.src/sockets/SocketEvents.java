package sockets;

import java.net.ServerSocket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SocketEvents {

	private BiConsumer<ServerSocket, Exception> onSocketListenError;
	private Consumer<SockClient> onSocketOpen;
	private BiConsumer<SockClient, Exception> onSocketOpenError;
	private Consumer<SockClient> onSocketDisconnect;
	private Consumer<SockClient> onSocketAccept;
	private BiConsumer<SockClient, String> onSocketRead;
	
	public SocketEvents() {
		onSocketOpen = null;
		onSocketOpenError = null;
		onSocketDisconnect = null;
		onSocketAccept = null;
		onSocketRead = null;
	}
	
	public void setOnSocketListenErrorEvent(BiConsumer<ServerSocket, Exception> consumer)
		{ onSocketListenError = consumer; }

	public void setOnSocketOpenEvent(Consumer<SockClient> consumer)
		{ onSocketOpen = consumer; }
	
	public void setOnSocketOpenErrorEvent(BiConsumer<SockClient, Exception> consumer)
		{ onSocketOpenError = consumer; }

	public void setOnSocketDisconnectEvent(Consumer<SockClient> consumer)
		{ onSocketDisconnect = consumer; }

	public void setOnSocketAcceptEvent(Consumer<SockClient> consumer)
		{ onSocketAccept = consumer; }

	public void setOnSocketReadEvent(BiConsumer<SockClient, String> consumer)
		{ onSocketRead = consumer; }

	public BiConsumer<ServerSocket, Exception> getOnSocketListenError()
		{ return onSocketListenError; }

	public Consumer<SockClient> getOnSocketOpen()
		{ return onSocketOpen; }
	
	public BiConsumer<SockClient, Exception> getOnSocketOpenError()
		{ return onSocketOpenError; }

	public Consumer<SockClient> getOnSocketDisconnect()
		{ return onSocketDisconnect; }
	
	public Consumer<SockClient> getOnSocketAccept()
		{ return onSocketAccept; }
	
	public BiConsumer<SockClient, String> getOnSocketRead()	
		{ return onSocketRead; }
	
}