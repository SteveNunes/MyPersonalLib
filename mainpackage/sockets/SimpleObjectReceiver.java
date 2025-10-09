package sockets;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class SimpleObjectReceiver {

	private String hostAddress;
	private int port;
	
	private static BiConsumer<SimpleObjectReceiver, Exception> onTransferError;
	private static BiConsumer<SimpleObjectReceiver, Object> onTransferEnd;
	
	public SimpleObjectReceiver(String hostAddress, int port) {
		SimpleFileSender.createExecutorService();
		this.hostAddress = hostAddress;
		this.port = port;
	}
	
	public static void setOnTransferErrorEvent(BiConsumer<SimpleObjectReceiver, Exception> onTransferError) {
		SimpleObjectReceiver.onTransferError = onTransferError;
	}

	public static void setOnTransferEndEvent(BiConsumer<SimpleObjectReceiver, Object> onTransferEnd) {
		SimpleObjectReceiver.onTransferEnd = onTransferEnd;
	}
	
	public void receiveObject() {
		if (onTransferEnd == null)
			throw new RuntimeException("You must call 'setOnTransferEndEvent()' before");
		CompletableFuture.supplyAsync(() -> {
			try (Socket socket = new Socket(hostAddress, port);
					 InputStream is = socket.getInputStream();
					 ObjectInputStream ois = new ObjectInputStream(is)) {
							Object object = ois.readObject();
							onTransferEnd.accept(this, object);
					 		return null;
			}
			catch (Exception e) {
				throw new RuntimeException("Error receiving object: " + e.getMessage());
			}
		}).whenComplete((obj, ex) -> {
			if (ex != null) {
				if (onTransferError != null)
					onTransferError.accept(this, new RuntimeException(ex.getMessage()));
				else
					ex.printStackTrace();
			}
		});
	}

}