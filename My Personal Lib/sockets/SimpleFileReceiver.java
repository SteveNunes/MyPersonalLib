package sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import util.Misc;

public class SimpleFileReceiver {

  private long receivedBytes;
  private long fileSize;
  private Boolean finished;
	private Boolean error;
	
	public long getTotalReceivedBytes()
		{ return receivedBytes; }
	
	public long getFileSize()
		{ return fileSize; }
	
	public Boolean isFinished()
		{ return finished; }
	
	public Boolean hasError()
		{ return error; }
	
	public SimpleFileReceiver() {
    finished = false;
    error = false;
		receivedBytes = 0;
		fileSize = 0;
	}
	
	// Construtor onde o receptor aguarda a conexão de quem vai enviar o arquivo
	public SimpleFileReceiver(int listenPort, String receiveFileFolder) {
		this();
		new Thread() {
			@Override
			public void run() {
		  	try (ServerSocket serverSocket = new ServerSocket(listenPort))
	  		{ initTransfer(serverSocket.accept(), receiveFileFolder); }
	  	catch (Exception e)
	  		{ e.printStackTrace(); }
			}
		}.start();
	}	
	
	// Construtor onde o receptor se conecta á quem vai enviar o arquivo
	public SimpleFileReceiver(String ip, int port, String receiveFileFolder) {
		this();
		new Thread() {
			@Override
			public void run() {
		  	try
					{ initTransfer(new Socket(ip, port), receiveFileFolder); }
				catch (Exception e)
					{ e.printStackTrace(); }
			}
		}.start();
	}

	private void initTransfer(Socket socket, String receiveFileFolder) {
  	try {
      DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
      DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
      int bytes = 0;
      @SuppressWarnings("deprecation")
  		String[] split = dataInputStream.readLine().split(" ");
      int bufferSize = Integer.parseInt(split[0]);
      fileSize = Long.parseLong(split[1]);
      String filePath = receiveFileFolder + Misc.arrayToString(split, 2);
      new File(new File(filePath).getParent() + "\\").mkdirs();
      FileOutputStream fileOutputStream = new FileOutputStream(filePath);
      byte[] buffer = new byte[bufferSize];
      
      while (receivedBytes < fileSize &&
      		(bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1) {
		        fileOutputStream.write(buffer, 0, bytes);
		        receivedBytes += bytes;
      }
      fileOutputStream.close();
      dataInputStream.close();
      dataOutputStream.close();
      socket.close();
    }
    catch (Exception e) {
	  	error = true;
	  	e.printStackTrace();
    }
    finished = true;
    error = error || receivedBytes < fileSize;
  }

	public void waitUntilTransferEnds() {
		while (!finished)
			Misc.sleep(50);
	}

}