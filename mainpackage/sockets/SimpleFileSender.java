package sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import util.Misc;

public class SimpleFileSender {

	private long sentBytes;
	private File file;
	private Boolean finished;
	private Boolean error;

	public long getTotalSentBytes() {
		return sentBytes;
	}

	public File getFile() {
		return file;
	}

	public long getFileSize() {
		return file != null ? file.length() : -1;
	}

	public Boolean isFinished() {
		return finished;
	}

	public Boolean hasError() {
		return error;
	}

	public SimpleFileSender() {
		finished = false;
		error = false;
		sentBytes = 0;
	}

	// Construtor onde o fornecedor aguarda a conexão de quem vai receber o arquivo
	public SimpleFileSender(int listenPort, String filePath, int bufferSize) {
		this();
		new Thread() {
			@Override
			public void run() {
				try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
					initTransfer(serverSocket.accept(), filePath, bufferSize);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	// Construtor onde o fornecedor se conecta á quem vai receber o arquivo
	public SimpleFileSender(String ip, int port, String filePath, int bufferSize) {
		this();
		new Thread() {
			@Override
			public void run() {
				try {
					initTransfer(new Socket(ip, port), filePath, bufferSize);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void initTransfer(Socket socket, String filePath, int bufferSize) {
		file = new File(filePath);
		if (!file.exists()) {
			error = true;
			throw new RuntimeException("File not exists - \"" + filePath + "\"");
		}
		try {
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
			FileInputStream fileInputStream = new FileInputStream(file);
			int bytes = 0;
			byte[] buffer = new byte[bufferSize];
			dataOutputStream.writeBytes(bufferSize + " " + file.length() + " " + file.getName() + "\n");
			while ((bytes = fileInputStream.read(buffer)) != -1) {
				sentBytes += bytes;
				dataOutputStream.write(buffer, 0, bytes);
				dataOutputStream.flush();
			}
			fileInputStream.close();
			dataOutputStream.close();
			dataInputStream.close();
		}
		catch (Exception e) {
			error = true;
			e.printStackTrace();
		}
		finished = true;
		error = error || sentBytes < file.length();
	}

	public void waitUntilTransferEnds() {
		while (!finished)
			Misc.sleep(50);
	}

}