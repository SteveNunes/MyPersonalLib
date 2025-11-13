package util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetUtils {

	public static String getHtmlBody(String url) {
		try {
			StringBuilder sb = new StringBuilder();
			HttpClient client = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build();
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			try (Scanner sc = new Scanner(response.body())) {
				while (sc.hasNextLine())
					sb.append(sc.nextLine() + "\n");
			}
			return sb.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<String> getHtmlBodyAsList(String url) {
		List<String> result = new ArrayList<>();
		for (String line : getHtmlBody(url).split("\n"))
			result.add(line);
		return result;
	}
	
	public static CompletableFuture<String> getOnlineIP() {
		return CompletableFuture.supplyAsync(() -> {
			try (Scanner sc = new Scanner(getHtmlBody("http://checkip.dyndns.org/"))) {
				while (sc.hasNext()) {
					Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)");
					Matcher m = p.matcher(sc.next());
					if (m.find())
						return m.group(1);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	public static List<String> readPublicGoogleSheet(String sheetUrl) {
		try {
			String spreadsheetId = sheetUrl.split("/d/")[1].split("/")[0];
			String exportUrl = "https://docs.google.com/spreadsheets/d/" + spreadsheetId + "/export?format=csv";
			if (sheetUrl.contains("gid=")) {
				String gid = sheetUrl.split("gid=")[1];
				exportUrl += "&gid=" + gid;
			}
			return getHtmlBodyAsList(exportUrl);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
