package connection;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebConnection {

	private URL url;
	private URLConnection con;
	private boolean hasMetaRefresh = false;

	public WebConnection(String url) {
		// assign the passed URL-String to
		// the url variable
		try {
			// if the url has a meta refresh
			// the url has to end with a slash
			// else MalformedURLException
			this.url = new URL(url.endsWith("/") ? url : url + "/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// check if the site is getting redirected
		// by the meta-refresh tag
		try {
			String refreshLink = getMetaRefresh(this.url.toExternalForm());
			if (refreshLink != null) {
				this.url = new URL(refreshLink);
				this.hasMetaRefresh = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// open the connection
		try {
			this.con = this.url.openConnection();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public WebConnection(URL url) {
		this(url.toExternalForm());
	}

	public String getHtml() {
		InputStream is = null;

		try {
			is = this.con.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return is != null ? new BufferedReader(new InputStreamReader(is)).lines().parallel()
				.collect(Collectors.joining("\n")).replace("\n", "").replace("\t", "") : "";
	}

	public URL getUrl() {
		return this.url;
	}

	public String getUrlString() {
		return this.url.toExternalForm();
	}

	private String getMetaRefresh(String url) throws IOException {
		if (url != null) {
			URI uri = URI.create(this.url.toExternalForm());
			Document d = Jsoup.connect(this.url.toExternalForm()).get();

			for (Element refresh : d.select("meta[http-equiv=Refresh]")) {

				Matcher m = Pattern.compile("(?si)\\d+;\\s*url=(.+)|\\d+").matcher(refresh.attr("content"));

				// find the first one that is valid
				if (m.matches()) {
					if (m.group(1) != null)
						d = Jsoup.connect(uri.resolve(m.group(1)).toString()).get();
					return d.baseUri();
				}
			}

		}
		return null;
	}

	@SuppressWarnings("unused")
	private boolean hasMetaRefresh() {
		return this.hasMetaRefresh;
	}
	
	private static void getImage(String src) throws IOException{
		String folder = null;
		int indexName=src.lastIndexOf("/");
		
		if (indexName == src.length()) {
			src = src.substring(1, indexName);
		}
		
		indexName = src.lastIndexOf("/");
		String name = src.substring(indexName, src.length());
		System.out.println(name);
		
		URL url = new URL(src);
		InputStream in = url.openStream();
		OutputStream out = new BufferedOutputStream(new FileOutputStream(
				System.getProperty("user.home") + "\\Documents\\test\\"+name));
		
		for (int i; (i=in.read()) != -1;) {
			out.write(i);
		}
		out.close();
		in.close();
	}
	
	private void getLinks() throws IOException {
		Set<String> noDuplicates = getLinksFromFile();
		Response resp = Jsoup.connect(this.getUrlString()).followRedirects(true).execute();
		Document doc = Jsoup.connect(resp.url().toExternalForm()).get();
		String path = System.getProperty("user.home") + "\\Documents\\test\\";
		//TODO: Exclude mails and duplicated entries
		
		Elements links = doc.select("a");
		String absUrl = "";
		for (Element e : links) {
			absUrl = e.absUrl("href");
			noDuplicates.add(absUrl);
		}
		
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "links.txt", false));
		noDuplicates.forEach(x -> {
			try {
				bw.write(x);
				bw.newLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		bw.close();
	}
	
	private Set<String> getLinksFromFile(){
		Set<String> entries = new HashSet<>();
		String path = System.getProperty("user.home") + "\\Documents\\test\\links.txt";
		try(Scanner sc = new Scanner(new File(path))){
			while (sc.hasNextLine()) {
				entries.add(sc.nextLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return entries;
	}

	public static void main(String[] args) throws IOException {

		WebConnection wc = new WebConnection("http://sandriesser.at");
		System.out.println(wc.getUrlString());
		// String html = wc.getHtml();
		// System.out.println(html);
		wc.getLinks();
		
		Response respone = Jsoup.connect(wc.getUrlString()).followRedirects(true).execute();
		Document doc = Jsoup.connect(respone.url().toExternalForm()).get();

		Elements img = doc.getElementsByTag("img");

		for (Element e : img) {
			String src = e.absUrl("src");
			getImage(src);
		}
		
		

	}

}
