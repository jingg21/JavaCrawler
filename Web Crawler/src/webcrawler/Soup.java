package webcrawler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Soup extends WebCrawler{
	private ArrayList<String> blacklist;
	
	//Constructor - place base URL and depth to start
	public Soup(String base, int depth) {
		super(base, depth);
		this.blacklist = new ArrayList<String>();
	}
	
	
	//get list of URL that do not include the tag
	public ArrayList<String> getBlacklist() {
		return this.blacklist;
	}
	
	//add to blacklist
	public void addBlacklist(String ...url) {
		for(String key:url)
		this.blacklist.add(key);
	}
	
	//crawl base URL provided
	public void crawl(String url, int lowerDepth) {
		//checks list if already crawled, lesser than MAX_DEPTH, and if base link is present
		if(!this.getURLs().contains(url) && lowerDepth < this.getDepth() && url.contains(this.getBaseLink())) {
			//Grabs links from sites recursively
			try {
				for(String word:this.getBlacklist()) {
				if(url.contains(word)) {
					return;
					}
				}
				//add current URL to ArrayList - prevent infinite loop
				this.addURLs(url);
				Document site = Jsoup.connect(url).get();
				System.out.printf("Depth %d: Crawling %s\n", lowerDepth, site.title());
				System.out.printf("%s\n", url);
				//select all <a> tags
				Elements linksOnPage = site.select("a[href]");
				lowerDepth++;
				for(Element page : linksOnPage) {
					//pass links into crawl()
					crawl(page.attr("abs:href"), lowerDepth);
				}
			}
			catch(IOException e) {
				System.err.println("Exception while crawling: "+ e.getMessage() + " - " + url);
				}
		}
	}
	
	//grab data from current URL site with reference to keyword and the tags
	//FILTER ?COMMENTS=1
	public void getData(String keyword, String tag) {
		ArrayList<String> parsedList = this.getURLs();
		//loop through parsedList to scrape data
		for(String url : parsedList)
			try {
				Document site = Jsoup.connect(url).get();
				Elements data = site.select(tag);
				//if tag does not exist, blacklist the URL and move to next
				if(data == null) {
					this.addBlacklist(url);
					throw new IOException("Tag not found - Adding to blacklist");
				}
				else if(!blacklist.contains(url)) {
					//if data does not contain keyword, throw exception
					if(data.text().contains(keyword)) {
						writeToFile(url);
						writeToFile(site.title());
						writeToFile(data.text());
					}
					else {
						throw new IOException("Keyword not found within comments");
					}
				}
				else {
					throw new IOException("Skipping blacklisted site: " + url);
				}
			}
			catch(IOException e) {
				System.err.println("Exception while parsing: " + e.getMessage());
			}
	}
	
	//creates an output file and writes to it
	//ARRANGE IT - bufferedreader
	public void writeToFile(String s) {
		PrintWriter text = null;
		try {
			text = new PrintWriter("output.txt");
			text.println(s);
		}
		catch(FileNotFoundException e) {
			System.err.println("Exception while writing to file: " + e.getMessage());
		}
		finally {
			text.close();
		}
	}
}
