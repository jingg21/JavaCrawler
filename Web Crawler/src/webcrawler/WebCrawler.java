package webcrawler;

import java.util.ArrayList;

public abstract class WebCrawler {
	private int depth;
	private ArrayList <String> URLs;
	private String baseLink;
	
	public ArrayList <String> getURLs(){
		return this.URLs;
	}
	
	public void addURLs(String s){
		URLs.add(s);
	}
	
	public int getDepth() {
		return this.depth;
	}
	
	public void setDepth(int n) {
		this.depth = n;
	}
	
	public String getBaseLink() {
		return this.baseLink;
	}
	
	public void setBaseLink(String s) {
		this.baseLink = s;
	}
}
