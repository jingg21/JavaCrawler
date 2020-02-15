package webcrawler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Soup extends WebCrawler{
	private ArrayList<String> blacklist;
	private String keyword;
	
	//Constructor - place base URL and depth
	public Soup(String base, int depth) {
		super(base, depth);
		this.blacklist = new ArrayList<String>();
		this.addBlacklist("#", "cars", "civis", "store", "gaming", "video", "staff", 
				"technopaedia", "author", "start=", "uploads", "tag", "mail", "video", 
				"apple", "advert", "reprints", "newsletters", "about-us", "rss", "conde", "reviews",
				"?view", "staff-directory", "?theme", "features", "contact-us");
		this.keyword = new String();
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
	
	public String getKeyword() {
		return this.keyword;
	}
	
	public void setKeyword(String s) {
		this.keyword = s;
	}
	
	//crawl base URL provided
	public void crawl(String url, int lowerDepth) {
		//checks list if already crawled, lesser than MAX_DEPTH, and if base link is present
		if(!this.getURLs().contains(url) && lowerDepth < this.getDepth() && url.contains(this.getBaseLink()) && this.getKeyword() != null) {
			//Grabs links from sites recursively
			try {
				for(String word:this.getBlacklist()) {
					if(url.contains(word)) {
						return;
					}
				}
				//add current URL to URL list
				this.addURLs(url);
				Document site = Jsoup.connect(url).get();
				System.out.printf("Crawling Depth %d: %s\nURL: %s\n", lowerDepth, site.title(), url);
				//grab Data here if site has keyword to avoid going through the whole list a second time
				getData(keyword, site, url);
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
	public void getData(String keyword, Document site, String url) {
	//loop through past URLs to scrape data
		try {
			Elements article = site.select("div.article-content");
			Elements data = site.select("li.comment");
			
			//if article contains keyword, take comments
			if(article.text().contains(keyword) && url.contains("?comments=1")) {
				System.out.println("Parsing " + site.title());
				//append to string array
				int y = countElements(data);
				String[] buffer = new String[y+2];
				buffer[0] = site.title();
				buffer[1] = article.text();
				int x = 2;
				for(Element comment:data) {
					buffer[x] = comment.text();
					x++;
				}
				ArrayList<String[]> CSVbuffer = new ArrayList<String[]>();
				//add to CSV title > article > comments
				CSVbuffer.add(buffer);
				writeToCSV(CSVbuffer);
				//set to null after use
				CSVbuffer = null;
				buffer = null;
			}
			else {
				throw new IOException("Keyword or tag comment not found, moving on to next link");
			}
		}
		catch(IOException e) {
			System.err.println("Exception while parsing: " + e.getMessage());
		}
	}
	
	//creates an output file and writes to it
	public void writeToCSV(ArrayList<String[]> s) throws IOException {
		FileWriter output = new FileWriter("./output.csv", true);
		try {
			PrintWriter pw = new PrintWriter(output);
			s.stream()
				.map(this::convertToCSV)
				.forEach(pw::println);
			pw.close();
		}
		finally {
			
		}
		
	}
	
	//helper function to write to CSV
	public String convertToCSV(String[] data) {
	    return Stream.of(data)
	      .map(this::escapeSpecialCharacters)
	      .collect(Collectors.joining(","));
	}
	
	//used to escape special characters when writing to CSV
	public String escapeSpecialCharacters(String data) {
	    String escapedData = data.replaceAll("\\R", " ");
	    if (data.contains(",") || data.contains("\"") || data.contains("'")) {
	        data = data.replace("\"", "\"\"");
	        escapedData = "\"" + data + "\"";
	    }
	    return escapedData;
	}
	
	//count number of elements
	public int countElements(Elements data) {
		int count = 0;
		for(@SuppressWarnings("unused") Element comment:data) {
			count++;
		}
		return count;
	}
	
}
