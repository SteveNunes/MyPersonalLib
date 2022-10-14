package sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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

	public SimpleFileReceiver(int listenPort, String receiveFileFolder) {
    finished = false;
    error = false;
		receivedBytes = 0;
		fileSize = 0;
		
		new Thread() {
			@Override
			public void run() {
		  	try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
		      Socket clientSocket = serverSocket.accept();
		      DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
		      DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
		      int bytes = 0;
		      @SuppressWarnings("deprecation")
		  		String[] split = dataInputStream.readLine().split(" ");
		      int bufferSize = Integer.parseInt(split[0]);
		      fileSize = Long.parseLong(split[1]);
		      FileOutputStream fileOutputStream = new FileOutputStream(receiveFileFolder + Misc.arrayToString(split, 2));
		      byte[] buffer = new byte[bufferSize];
		      
		      while (receivedBytes < fileSize &&
		      		(bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1) {
				        fileOutputStream.write(buffer, 0, bytes);
				        receivedBytes += bytes;
		      }
		      fileOutputStream.close();
		      dataInputStream.close();
		      dataOutputStream.close();
		      clientSocket.close();
		    }
		    catch (Exception e) {
			  	error = true;
			  	e.printStackTrace();
		    }
		    finished = true;
		    error = error || receivedBytes < fileSize;
			}
		}.start();
  }

}