package sockets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Misc;

public class OnlineIPGetter {
	
	private static String ip;
	
	public static String getOnlineIP() {
		ip = null;
		SocketClient socket = new SocketClient();
		socket.setOnConnectErrorEvent(e -> ip = "Unable to retrieve");
		socket.setOnDataReceiveEvent(data -> {
	    Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)");
	    Matcher m = p.matcher(data);
	    if (m.find())
	    ip = m.group(1);
		});
		socket.setOnDisconnectEvent(client -> { 
			if (ip == null)
				ip = "Unable to retrieve";
		});
		socket.connect("checkip.dyndns.org", 80);
		socket.sendData(
			"GET http://checkip.dyndns.org HTTP/1.0\r\n"
			+ "Host: checkip.dyndns.org\r\n"
			+ "User-Agent: Mozilla/4.0\r\n");
		
		while (ip == null)
			Misc.sleep(10);
		return ip;
	}

}
