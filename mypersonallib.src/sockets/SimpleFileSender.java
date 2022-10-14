package sockets;

import java.io.*;
import java.net.Socket;

public class SimpleFileSender {
	
  private long sentBytes;
  private File file;
  private Boolean finished;
	private Boolean error;
	
	public long getTotalSentBytes()
		{ return sentBytes; }
	
	public File getFile()
		{ return file; }
	
	public long getFileSize()
		{ return file != null ? file.length() : -1; }
	
	public Boolean isFinished()
		{ return finished; }
	
	public Boolean hasError()
		{ return error; }
	
  public SimpleFileSender(String ip, int port, int sendBufferSize, String filePath) {
    file = new File(filePath);
    if (!file.exists()) {
    	error = true;
    	throw new RuntimeException("File not exists - \"" + filePath + "\"");
    }
    finished = false;
    error = false;
  	sentBytes = 0;
    
		new Thread() {
			@Override
	    public void run() {
			  try (Socket socket = new Socket(ip, port)) {
			  	DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
			    FileInputStream fileInputStream = new FileInputStream(file);
			    int bytes = 0;
			    byte[] buffer = new byte[sendBufferSize];
			    dataOutputStream.writeBytes(sendBufferSize + " " + file.length() + " " + file.getName() + "\n");  
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
  	}.start();
  }

}