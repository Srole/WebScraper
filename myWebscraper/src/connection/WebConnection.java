package connection;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import WebElements.Scraper;
import WebElements.WebTable;
import WebElements.WebTableData;
import WebElements.WebTableRow;
import io.IO;
import javafx.scene.control.Alert;

import javax.imageio.ImageIO;

public class WebConnection {

	public static final String TEST_PATH = System.getProperty("user.home") + "\\Documents\\";
	
	private URL url;
	private boolean hasMetaRefresh = false;
	private Document doc;
	private Set<URL> internalLinks = new HashSet<>();

	public WebConnection(String url) throws UnknownHostException, MalformedURLException, IOException {
		// assign the passed URL-String to
		// the url variable
		
			// if the url has a meta refresh
			// the url has to end with a slash
			// else MalformedURLException
			int count = url.length() - url.replace(".", "").length();
			this.url = count > 2 ? new URL(url) : new URL(url.endsWith("/") ? url : url + "/");
		

		// check if the site is getting redirected
		// by the meta-refresh tag
	
			String refreshLink = getMetaRefresh(this.url.toExternalForm());
			if (refreshLink != null) {
				this.url = new URL(refreshLink);
				this.hasMetaRefresh = true;
			}
		

		
			Response respone = Jsoup.connect(getUrlString()).followRedirects(true)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.ignoreHttpErrors(true).referrer("http://www.google.com").execute();
			doc = Jsoup.connect(respone.url().toExternalForm()).get();
			this.url = respone.url();
	}

	public WebConnection(URL url) throws UnknownHostException, MalformedURLException, IOException {
		this(url.toExternalForm());
	}

	public String getHtml() {
		return doc.toString();
	}

	public String getHtmlAsPlainText() {
		return getHtml().replaceAll("\\n\\t ", "");
	}

	public URL getUrl() {
		return this.url;
	}

	public String getUrlString() {
		return this.url.toExternalForm();
	}

	public String getBaseUrl() {
		String urlString = getUrlString();
		
		//TODO: Use regex instead of this bullshit
		
		if (urlString.contains("www.")) {
			urlString = urlString.replace("www.", "");
		}
		if (urlString.contains("http://")) {
			urlString = urlString.replace("http://","");
		}
		if (urlString.contains("https://")) {
			urlString = urlString.replace("https://", "");
		}
		
		return urlString.substring(0, urlString.lastIndexOf("/"));
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

	private Document getDocument() {
		return this.doc;
	}
	
	public String getText() {
		return this.doc.text();
	}

	//TODO: investigate why image sources are empty in Scraper class.
	public <T> Collection<String> getImagesSources() {
		Set<String> imgSource = new HashSet<>();
		Elements img = this.getDocument().select("img");
		String src = "";
		for (Element e : img) {
			src = e.absUrl("src");
//			System.out.println("img src: " + src);
//			if (src.lastIndexOf("/") == src.length()) { // remove slashes at the end of url
//				src = src.substring(1, src.lastIndexOf("/"));
//			}
			imgSource.add(e.absUrl(src));
		}

		return imgSource;
	}

	public BufferedImage getImage(String src) {
		String name = getImageName(src);

		URL url = null;
		InputStream in = null;
		BufferedImage img = null;
		try {
			url = new URL(src);
			in = url.openStream();
			img = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return img;

	}

	private String getImageName(String src) {
		int indexName = src.lastIndexOf("/");

		if (indexName == src.length()) {
			src = src.substring(1, indexName);
		}

		indexName = src.lastIndexOf("/");
		String name = src.substring(indexName, src.length());

		return name;
	}

	public <T> Collection<String> getLinks() throws IOException {
		Set<String> noDuplicates = new HashSet<>();

		Elements links = doc.select("a");
		String absUrl = "";
		for (Element e : links) {
			absUrl = e.absUrl("href");
			noDuplicates.add(absUrl);
		}

		return (Collection<String>) noDuplicates;
	}

	public <T> Collection<String> getExternalLinks() {
		Collection<String> links = null;
		try {
			links = getLinks();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Set<String> extLinks = new HashSet<>();

		links.forEach(x -> {
			if (!x.contains(getBaseUrl()) && !x.contains("mailto")) {
				extLinks.add(x);
			}
		});

		return extLinks;
	}

	public <T> Collection<String> getInternalLinks() {
		Collection<String> links = null;
		try {
			links = getLinks();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Set<String> intLinks = new HashSet<>();
		
			
		links.forEach(x -> {
			if (x.contains(getBaseUrl()) && !x.contains("mailto")) {
				intLinks.add(x);
			}
		});

		return intLinks;
	}

	public <T> Collection<String> getEmails() throws IOException {
		Collection<String> m = getLinks();
		Set<String> mails = new HashSet<>();

		m.forEach(x -> {
			String str = "";
			if (x.contains("mailto:")) {
				str = x.replace("mailto:", "");

				if (x.contains("?")) {
					str = str.substring(0, str.lastIndexOf("?"));
				}
			}
			mails.add(str);
		});

		return mails;
	}

	public <T> Collection<WebTable> getTables() {
		ArrayList<WebTable> tbls = new ArrayList<>();

		int cnt = this.getDocument().select("table").size();
		Element table = null;
		Elements row = null;
		WebTable tbl = null;
		WebTableRow rw = null;

		for (int i = 0; i < cnt; i++) {
			tbl = new WebTable();
			table = this.getDocument().select("table").get(i);
			row = table.select("tr");
			for (Element e : row) {
				rw = new WebTableRow();
				for (Element ee : e.children()) {
					rw.addCell(new WebTableData(ee.text()));
				}
				tbl.addTableRow(rw);
			}
			tbls.add(tbl);
		}
		return tbls;
	}

	public static void main(String[] args) throws IOException {

		WebConnection wc = new WebConnection("http://www.sandriesser.at");
		Scraper sc = new Scraper(true, true, true, true, true, true, true, true);
		sc.scrape(wc);
		
		 GsonBuilder builder = new GsonBuilder(); 
	     Gson gson = builder.setPrettyPrinting().create(); 
	     FileWriter writer = new FileWriter(TEST_PATH + "scraper.json");   
	     writer.write(gson.toJson(sc));  
	     writer.close(); 
		
		
		//System.out.println(wc.getText());
//		System.out.println(wc.getUrlString());
//
//		Collection<String> internal = wc.getInternalLinks();
//		Collection<String> external = wc.getExternalLinks();
//		Collection<String> links = wc.getLinks();
//		Collection<String> mails = wc.getEmails();
//
//		links.forEach(x -> System.out.println(x));
//		System.out.println("internal: \n");
//		internal.forEach(x -> System.out.println(x));
//		System.out.println("external: \n");
//		external.forEach(x -> System.out.println(x));
//		System.out.println("emails: \n");
//		mails.forEach(x -> System.out.println(x));

	}

}
