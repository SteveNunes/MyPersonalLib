package sockets;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleFileReceiver {

	private File file;
	private String hostAddress;
	private int port;
	
	private static BiConsumer<SimpleFileReceiver, Exception> onTransferError;
	private static Consumer<SimpleFileReceiver> onTransferInit;
	private static Consumer<SimpleFileReceiver> onTransferEnd;
	private static BiConsumer<SimpleFileReceiver, Long> onPartialFileSend;

	public SimpleFileReceiver(String hostAddress, int port) {
		SimpleFileSender.createExecutorService();
		this.port = port;
		this.hostAddress = hostAddress;
	}
	
	public static void setOnTransferErrorEvent(BiConsumer<SimpleFileReceiver, Exception> onTransferError) {
		SimpleFileReceiver.onTransferError = onTransferError;
	}

	public static void setOnTransferInitEvent(Consumer<SimpleFileReceiver> onTransferInit) {
		SimpleFileReceiver.onTransferInit = onTransferInit;
	}

	public static void setOnTransferEndEvent(Consumer<SimpleFileReceiver> onTransferEnd) {
		SimpleFileReceiver.onTransferEnd = onTransferEnd;
	}

	public static void setOnPartialFileSendEvent(BiConsumer<SimpleFileReceiver, Long> onPartialFileSend) {
		SimpleFileReceiver.onPartialFileSend = onPartialFileSend;
	}

	public File getFile() {
		return file;
	}
	
	public void receiveFile(File file) throws IOException {
		this.file = file;
		
		if (file == null)
			throw new NullPointerException("file is null");
		if (file.exists())
			throw new FileNotFoundException("File already exists: " + file.getAbsolutePath());
		
		SimpleFileSender.executorService.execute(() -> {
			try (Socket socket = new Socket(hostAddress, port);
					 DataInputStream dis = new DataInputStream(socket.getInputStream());
					 FileOutputStream fos = new FileOutputStream(file.getAbsolutePath())) {
							long tamanhoArquivo = dis.readLong();
							byte[] buffer = new byte[4096];
							int bytesLidos;
							long bytesRecebidos = 0;
							if (onTransferInit != null)
								onTransferInit.accept(this);
							while (bytesRecebidos < tamanhoArquivo && (bytesLidos = dis.read(buffer)) != -1) {
								fos.write(buffer, 0, bytesLidos);
								bytesRecebidos += bytesLidos;
								if (onPartialFileSend != null)
									onPartialFileSend.accept(this, bytesRecebidos);
							}
							if (onTransferEnd != null)
								onTransferEnd.accept(this);
			}
			catch (UnknownHostException e) {
				if (onTransferError != null)
					onTransferError.accept(this, e);
				else {
					e.printStackTrace();
					throw new RuntimeException("Error receiving file: " + file.getAbsolutePath() + "\nError: " + e.getMessage());
				}
			}
			catch (IOException e) {
				if (onTransferError != null)
					onTransferError.accept(this, e);
				else {
					e.printStackTrace();
					throw new RuntimeException("Error receiving file: " + file.getAbsolutePath() + "\nError: " + e.getMessage());
				}
			}
		});
	}

}