package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.stream.Collectors;

public class WebConnection {
	
	private URL url;
	private URLConnection con;

	//public WebConnection() {}
	public WebConnection(String url)  {
		
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		try {
			this.con = this.url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public WebConnection(URL url)  {
		this.url = url;
		
		try {
			this.con = this.url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getHtml() {
		InputStream is = null;
		
		try {
			is = this.con.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return is != null ? new BufferedReader(new InputStreamReader(is)).lines().parallel().collect(Collectors.joining("\n")) : "";
	}
	

	public static void main(String[] args) {
		
		WebConnection wc = new WebConnection("http://www.sandriesser.at/");
		String html = wc.getHtml();
		System.out.println(html);

	}

}
