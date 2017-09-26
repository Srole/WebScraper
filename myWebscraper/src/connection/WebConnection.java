package connection;


import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
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

import javax.imageio.ImageIO;

public class WebConnection {

	private URL url;
	private boolean hasMetaRefresh = false;
	private Document doc;
	private Set<URL> internalLinks = new HashSet<>();

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
		
		try {
			Response respone = Jsoup.connect(getUrlString()).followRedirects(true).execute();
			doc = Jsoup.connect(respone.url().toExternalForm()).get();
			this.url = respone.url();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public WebConnection(URL url) {
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
		String urlString= getUrlString();
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
	
	public <T> Collection<String> getImagesSources() {
		Set<String> imgSource = new HashSet<>();
		Elements img = doc.getElementsByTag("img");
		String src="";
		for (Element e : img) {
			src = e.absUrl("src");
			if (src.lastIndexOf("/") == src.length()) { //remove slashes at the end of url
				src = src.substring(1, src.lastIndexOf("/"));
			}
			imgSource.add(e.absUrl(src));
		}
		
		return (Collection<String>)imgSource;
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
	
	public BufferedImage cImage(String src) {
		String name = getImageName(src);
		
		URL url = null;
		InputStream in = null;
		BufferedImage img = null;
		try {
			url = new URL(src);
			in = url.openStream();
			img = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	
	private <T> Collection<String> getLinks() throws IOException {
		Set<String> noDuplicates = new HashSet<>();
		String path = System.getProperty("user.home") + "\\Documents\\test\\";
		//TODO: Exclude mails
		
		Elements links = doc.select("a");
		String absUrl = "";
		for (Element e : links) {
			absUrl = e.absUrl("href");
			noDuplicates.add(absUrl);
		}
		
		return (Collection<String>)noDuplicates;
		
		
//		BufferedWriter bw = new BufferedWriter(new FileWriter(path + "links.txt", false));
//		noDuplicates.forEach(x -> {
//			try {
//				bw.write(x);
//				bw.newLine();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//		});
//		bw.close();
	}
	
	private <T> Collection<String> getExternalLinks(){
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
	
	public <T> Collection<String> getInternalLinks(){
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
	
	public <T> Collection<String> getEmails() throws IOException{
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
//		wc.getLinks();
//		
//		Response respone = Jsoup.connect(wc.getUrlString()).followRedirects(true).execute();
//		Document doc = Jsoup.connect(respone.url().toExternalForm()).get();
//
//		Elements img = doc.getElementsByTag("img");
//
//		for (Element e : img) {
//			String src = e.absUrl("src");
//			getImage(src);
//		}
//		
		Collection<String> internal = wc.getInternalLinks();
		Collection<String> external = wc.getExternalLinks();
		Collection<String> links = wc.getLinks();
		Collection<String> mails = wc.getEmails();
		
		links.forEach(x -> System.out.println(x));
		System.out.println("internal: \n");
		internal.forEach(x -> System.out.println(x));
		System.out.println("external: \n");
		external.forEach(x -> System.out.println(x));
		System.out.println("emails: \n");
		mails.forEach(x->System.out.println(x));

	}

}
