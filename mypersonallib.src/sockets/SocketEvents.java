package sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SocketEvents {

	private BiConsumer<ServerSocket, IOException> onSocketListenError;
	private Consumer<SockClient> onSocketOpen;
	private BiConsumer<SockClient, IOException> onSocketOpenError;
	private Consumer<SockClient> onSocketClose;
	private Consumer<SockClient> onSocketAccept;
	private BiConsumer<SockClient, IOException> onSocketAcceptError;
	private BiConsumer<SockClient, String> onSocketRead;
	private BiConsumer<SockClient, IOException> onSocketReadError;
	
	public SocketEvents() {
		onSocketOpen = null;
		onSocketOpenError = null;
		onSocketClose = null;
		onSocketAccept = null;
		onSocketAcceptError = null;
		onSocketRead = null;
		onSocketReadError = null;
	}
	
	public void setOnSocketListenErrorEvent(BiConsumer<ServerSocket, IOException> consumer)
		{ onSocketListenError = consumer; }

	public void setOnSocketOpenEvent(Consumer<SockClient> consumer)
		{ onSocketOpen = consumer; }
	
	public void setOnSocketOpenErrorEvent(BiConsumer<SockClient, IOException> consumer)
		{ onSocketOpenError = consumer; }

	public void setOnSocketCloseEvent(Consumer<SockClient> consumer)
		{ onSocketClose = consumer; }

	public void setOnSocketAcceptEvent(Consumer<SockClient> consumer)
		{ onSocketAccept = consumer; }

	public void setOnSocketAcceptErrorEvent(BiConsumer<SockClient, IOException> consumer)
		{ onSocketAcceptError = consumer; }

	public void setOnSocketReadEvent(BiConsumer<SockClient, String> consumer)
		{ onSocketRead = consumer; }

	public void setOnSocketReadErrorEvent(BiConsumer<SockClient, IOException> consumer)
		{ onSocketReadError = consumer; }

	public BiConsumer<ServerSocket, IOException> getOnSocketListenError()
		{ return onSocketListenError; }

	public Consumer<SockClient> getOnSocketOpen()
		{ return onSocketOpen; }
	
	public BiConsumer<SockClient, IOException> getOnSocketOpenError()
		{ return onSocketOpenError; }

	public Consumer<SockClient> getOnSocketClose()
		{ return onSocketClose; }
	
	public Consumer<SockClient> getOnSocketAccept()
		{ return onSocketAccept; }
	
	public BiConsumer<SockClient, IOException> getOnSocketAcceptError()
		{ return onSocketAcceptError; }

	public BiConsumer<SockClient, String> getOnSocketRead()	
		{ return onSocketRead; }
	
	public BiConsumer<SockClient, IOException> getOnSocketReadError()	
		{ return onSocketReadError; }

}
