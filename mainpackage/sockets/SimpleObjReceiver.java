package sockets;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import util.Misc;

public class SimpleObjReceiver {

	private Object object;
	private Boolean finished;
	private Boolean error;

	public Object getObject() {
		return object;
	}

	public Boolean isFinished() {
		return finished;
	}

	public Boolean hasError() {
		return error;
	}

	public SimpleObjReceiver() {
		finished = false;
		error = false;
	}

	// Construtor onde o receptor aguarda a conexão de quem vai enviar o objeto
	public SimpleObjReceiver(int listenPort) {
		this();
		new Thread() {
			@Override
			public void run() {
				try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
					initTransfer(serverSocket.accept());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	// Construtor onde o receptor se conecta á quem vai enviar o objeto
	public SimpleObjReceiver(String ip, int port) {
		this();
		new Thread() {
			@Override
			public void run() {
				try {
					initTransfer(new Socket(ip, port));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void initTransfer(Socket socket) {
		try {
			InputStream inputStream = socket.getInputStream();
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			object = objectInputStream.readObject();
			socket.close();
		}
		catch (Exception e) {
			error = true;
			e.printStackTrace();
		}
		finished = true;
	}

	public void waitUntilTransferEnds() {
		while (!finished)
			Misc.sleep(50);
	}

}