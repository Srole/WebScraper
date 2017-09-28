package WebElements;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import connection.WebConnection;

public class Scraper {

	transient boolean follow;
	transient int depth;
	transient int depthCounter = 0;

	transient boolean getTables;
	transient boolean getMails;
	transient boolean getImages;
	transient boolean getLinks;
	transient boolean internal;
	transient boolean external;
	transient boolean getText;

	Set<WebTable> tables = new HashSet<>();
	Set<String> emails = new HashSet<>();
	Set<String> imgSrc = new HashSet<>();
	Set<String> links = new HashSet<>();
	Set<String> internalLinks = new HashSet<>();
	Set<String> externalLinks = new HashSet<>();
	Set<String> text = new HashSet<>();


	public Scraper(boolean followExternal, boolean getTables, boolean getMails, boolean getImages,
			boolean getLinks, boolean internalLinks, boolean externalLinks, boolean getText) {
		this.follow = followExternal;
		this.getTables = getTables;
		this.getMails = getMails;
		this.getImages = getImages;
		this.getLinks = getLinks;
		this.internal = internalLinks;
		this.external = externalLinks;
		this.getText = getText;
	}

	public void scrape(WebConnection con) {

			if (getTables) {
				addToCollection(con.getTables(), this.tables);
			}
			if (getMails) {
				try {
					addToCollection(con.getEmails(), this.emails);
				} catch (IOException e) {
					// TODO: Implement log
				}
			}
			if (getLinks) {
				if (internal) {
					addToCollection(con.getInternalLinks(), this.internalLinks);
				}
				if (external) {
					addToCollection(con.getExternalLinks(), this.externalLinks);
				} else {
					try {
						addToCollection(con.getLinks(), this.links);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			if (getImages) {
				addToCollection(con.getImagesSources(), this.imgSrc);
			}

			if (getText) {
				this.text.add(con.getText());
			}

			this.imgSrc.forEach(x -> {
				System.out.println(x.toString());
			});
			
			con.getImagesSources().forEach(x -> {
				System.out.println(x.toString()  + "  con");
			});
			
	}

	private static <T> void addToCollection(Collection<T> col1, Collection<T> col2) {
		col1.forEach(x -> {
			col2.add(x);
		});
	}

	public static void main(String[] args) {

	}

}
