package sockets;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import util.Misc;

public class SimpleFileSender {
	
	static ExecutorService executorService;

	private static BiConsumer<SimpleFileSender, Exception> onTransferError;
	private static Consumer<SimpleFileSender> onTransferInit;
	private static Consumer<SimpleFileSender> onTransferEnd;
	private static BiConsumer<SimpleFileSender, Long> onPartialFileSend;

	File file;
	int port;
	
	static void createExecutorService() {
		if (executorService == null) {
			executorService = Executors.newCachedThreadPool();
			Misc.addShutdownEvent(() -> executorService.shutdownNow());
		}
	}

	public SimpleFileSender(int port) {
		createExecutorService();
		this.port = port;
	}
	
	public static void setOnTransferErrorEvent(BiConsumer<SimpleFileSender, Exception> onTransferError) {
		SimpleFileSender.onTransferError = onTransferError;
	}

	public static void setOnTransferInitEvent(Consumer<SimpleFileSender> onTransferInit) {
		SimpleFileSender.onTransferInit = onTransferInit;
	}

	public static void setOnTransferEndEvent(Consumer<SimpleFileSender> onTransferEnd) {
		SimpleFileSender.onTransferEnd = onTransferEnd;
	}

	public static void setOnPartialFileSendEvent(BiConsumer<SimpleFileSender, Long> onPartialFileSend) {
		SimpleFileSender.onPartialFileSend = onPartialFileSend;
	}

	public File getFile() {
		return file;
	}
	
	public void sendFile(File file) throws IOException {
		this.file = file;
		
		if (file == null)
			throw new NullPointerException("file is null");
		if (!file.exists())
			throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
		if (!file.canRead())
			throw new IOException("Unable to read file: " + file.getAbsolutePath());
		
		executorService.execute(() -> {
			try (ServerSocket serverSocket = new ServerSocket(port);
					 Socket clientSocket = serverSocket.accept();
					 DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
					 FileInputStream fis = new FileInputStream(file)) {
							dos.writeLong(file.length());
							byte[] buffer = new byte[4096];
							int bytesLidos;
							long bytesEnviados = 0;
							if (onTransferInit != null)
								onTransferInit.accept(this);
							while ((bytesLidos = fis.read(buffer)) != -1) {
								dos.write(buffer, 0, bytesLidos);
								bytesEnviados += bytesLidos;
								if (onPartialFileSend != null)
									onPartialFileSend.accept(this, bytesEnviados);
							}
							dos.flush();
							if (onTransferEnd != null)
								onTransferEnd.accept(this);
			}
			catch (IOException e) {
				if (onTransferError != null)
					onTransferError.accept(this, e);
				else {
					e.printStackTrace();
					throw new RuntimeException("Error sending file: " + file.getAbsolutePath() + "\nError: " + e.getMessage());
				}
			}
		});
	}
}
