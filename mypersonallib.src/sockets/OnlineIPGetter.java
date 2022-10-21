package sockets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Misc;

public class OnlineIPGetter {
	
	private static String ip;
	
	public static String getOnlineIP() {
		ip = null;
		SocketEvents sockEvents = new SocketEvents();
		sockEvents.setOnSocketReadEvent((sock, data) -> {
	    Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)");
	    Matcher m = p.matcher(data);
	    if (m.find())
	    ip = m.group(1);
		});
		sockEvents.setOnSocketDisconnectEvent((sock, ex) -> { 
			if (ip == null)
				ip = "Unable to retrieve";
		});
		sockEvents.setOnSocketOpenErrorEvent((sock, ex) -> ip = "Unable to retrieve");
		SockClient socket = new SockClient("checkip.dyndns.org", 80, sockEvents);
		socket.sendData(
			"GET http://checkip.dyndns.org HTTP/1.0\r\n"
			+ "Host: checkip.dyndns.org\r\n"
			+ "User-Agent: Mozilla/4.0\r\n");
		
		while (ip == null)
			Misc.sleep(10);
		return ip;
	}

}
