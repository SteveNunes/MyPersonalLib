package sockets;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleObjectSender {
	
	private int port;
	
	private static BiConsumer<SimpleObjectSender, Exception> onTransferError;
	private static Consumer<SimpleObjectSender> onTransferEnd;
	
	public SimpleObjectSender(int port) {
		SimpleFileSender.createExecutorService();
		this.port = port;
	}
	
	public static void setOnTransferErrorEvent(BiConsumer<SimpleObjectSender, Exception> onTransferError) {
		SimpleObjectSender.onTransferError = onTransferError;
	}

	public static void setOnTransferEndEvent(Consumer<SimpleObjectSender> onTransferEnd) {
		SimpleObjectSender.onTransferEnd = onTransferEnd;
	}
	
	public void sendObject(Object object) {
		if (object == null)
			throw new NullPointerException("object is null");
		SimpleFileSender.executorService.execute(() -> {
			try (ServerSocket server = new ServerSocket(port);
					 Socket client = server.accept();
					 OutputStream os = client.getOutputStream();
					 ObjectOutputStream oos = new ObjectOutputStream(os)) {
					 		oos.writeObject(object);
							if (onTransferEnd != null)
								onTransferEnd.accept(this);
			}
			catch (Exception e) {
				if (onTransferError != null)
					onTransferError.accept(this, e);
				else {
					e.printStackTrace();
					throw new RuntimeException("Error sending object: " + e.getMessage());
				}
			}
		});
	}
}
