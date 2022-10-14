package sockets;

import java.net.ServerSocket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SocketEvents {

	private BiConsumer<ServerSocket, Exception> onSocketListenError;
	private Consumer<SockClient> onSocketOpen;
	private BiConsumer<SockClient, Exception> onSocketOpenError;
	private Consumer<SockClient> onSocketClose;
	private Consumer<SockClient> onSocketAccept;
	private BiConsumer<SockClient, Exception> onSocketAcceptError;
	private BiConsumer<SockClient, String> onSocketRead;
	
	public SocketEvents() {
		onSocketOpen = null;
		onSocketOpenError = null;
		onSocketClose = null;
		onSocketAccept = null;
		onSocketAcceptError = null;
		onSocketRead = null;
	}
	
	public void setOnSocketListenErrorEvent(BiConsumer<ServerSocket, Exception> consumer)
		{ onSocketListenError = consumer; }

	public void setOnSocketOpenEvent(Consumer<SockClient> consumer)
		{ onSocketOpen = consumer; }
	
	public void setOnSocketOpenErrorEvent(BiConsumer<SockClient, Exception> consumer)
		{ onSocketOpenError = consumer; }

	public void setOnSocketCloseEvent(Consumer<SockClient> consumer)
		{ onSocketClose = consumer; }

	public void setOnSocketAcceptEvent(Consumer<SockClient> consumer)
		{ onSocketAccept = consumer; }

	public void setOnSocketAcceptErrorEvent(BiConsumer<SockClient, Exception> consumer)
		{ onSocketAcceptError = consumer; }

	public void setOnSocketReadEvent(BiConsumer<SockClient, String> consumer)
		{ onSocketRead = consumer; }

	public BiConsumer<ServerSocket, Exception> getOnSocketListenError()
		{ return onSocketListenError; }

	public Consumer<SockClient> getOnSocketOpen()
		{ return onSocketOpen; }
	
	public BiConsumer<SockClient, Exception> getOnSocketOpenError()
		{ return onSocketOpenError; }

	public Consumer<SockClient> getOnSocketClose()
		{ return onSocketClose; }
	
	public Consumer<SockClient> getOnSocketAccept()
		{ return onSocketAccept; }
	
	public BiConsumer<SockClient, Exception> getOnSocketAcceptError()
		{ return onSocketAcceptError; }

	public BiConsumer<SockClient, String> getOnSocketRead()	
		{ return onSocketRead; }
	
}
