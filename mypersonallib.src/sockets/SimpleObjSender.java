package sockets;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import util.Misc;

public class SimpleObjSender {
	
  private Boolean finished;
	private Boolean error;
	
	public Boolean isFinished()
		{ return finished; }
	
	public Boolean hasError()
		{ return error; }
	
	public SimpleObjSender() {
    finished = false;
    error = false;
	}
	
	// Construtor onde o fornecedor aguarda a conexão de quem vai receber o objeto
	public SimpleObjSender(int listenPort, Object object) {
		this();
		new Thread() {
			@Override
			public void run() {
		  	try (ServerSocket serverSocket = new ServerSocket(listenPort))
		  		{ initTransfer(serverSocket.accept(), object); }
		  	catch (Exception e)
		  		{ e.printStackTrace(); }
			}
		}.start();
	}	
	
	// Construtor onde o fornecedor se conecta á quem vai receber o objeto
	public SimpleObjSender(String ip, int port, Object object) {
		this();
		new Thread() {
			@Override
			public void run() {
		  	try
					{ initTransfer(new Socket(ip, port), object); }
				catch (Exception e)
					{ e.printStackTrace(); }
			}
		}.start();
	}

	private void initTransfer(Socket socket, Object object) {
	  try {
     OutputStream outputStream = socket.getOutputStream();
     ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
     objectOutputStream.writeObject(object);
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