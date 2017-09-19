package connection;

import java.io.IOException;
import java.net.*;

public class WebConnection {

	public static void main(String[] args) {
		try {
			URL url = new URL("http://example.com");
			URLConnection conn = url.openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
