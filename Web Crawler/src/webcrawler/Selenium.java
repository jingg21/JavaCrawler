package webcrawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Selenium extends WebCrawler{
	private List <WebElement> getSearchLinks;
	private WebDriver driver;
	private WebDriverWait wait;
	private List <WebElement> commentsList;
	private ArrayList<String> comments;
	
	public Selenium(String baseLink, int depth) {
		super(baseLink, depth);
		
		ChromeOptions options = new ChromeOptions();
		options.addExtensions(new File("D:\\Work\\ICT1009\\extension_1_24_4_0.crx"));
		System.setProperty("webdriver.chrome.driver", "D:\\Work\\ICT1009\\chromedriver_win32_test1\\chromedriver.exe");
		driver = new ChromeDriver(options);
		driver.get(baseLink);
		wait = new WebDriverWait(driver, 30);
	}
	
	public void searchTerm(String term) throws InterruptedException {
		driver.findElement(By.name("p")).sendKeys(term);
		driver.findElement(By.name("p")).sendKeys(Keys.ENTER);
		Thread.sleep(1000);
	}
	
	public void getLinks() throws InterruptedException {
		getSearchLinks = new ArrayList<WebElement>();
		getSearchLinks = driver.findElements(By.xpath("/html/body/div[1]/div[3]/div/div/div[1]/div/div/div/div/ol/li[*]/div/ul/li/h4/a"));
		
		for (WebElement link : getSearchLinks) {
			this.addURLs(link.getAttribute("href"));
		}
		
		for (int i = 1; i <= this.getDepth() ; i++) {
			if (i == 1) {
				driver.findElement(By.xpath("/html/body/div[1]/div[3]/div/div/div[1]/div/ol/li/div/div/a[5]")).click();
				Thread.sleep(1500);
			}
			else {
				driver.findElement(By.xpath("/html/body/div[1]/div[3]/div/div/div[1]/div/ol/li/div/div/a[6]")).click();
				Thread.sleep(1500);
			}
			
			getSearchLinks = driver.findElements(By.xpath("/html/body/div[1]/div[3]/div/div/div[1]/div/div/div/div/ol/li[*]/div/ul/li/h4/a"));
			for (WebElement link : getSearchLinks) {
				this.addURLs(link.getAttribute("href"));
			}
		}
		
		for (String i : this.getURLs()) {
			System.out.println(i);
		}
		
		System.out.println("Count of elements = "+ this.getURLs().size());
	}
	
	public void getComments() throws InterruptedException, Exception {
		String verizonXpath;
		String commentsXpath;
		String showMore;
		commentsList = new ArrayList<WebElement>();
		comments = new ArrayList<String>();
		String url;
		for (String links : this.getURLs()) {
			try {
				driver.get(links);
				Thread.sleep(2000);
				
				url = driver.getCurrentUrl();
				
				if (!url.contains("yahoo")) {
					throw new Exception("Not yahoo URL.");
				}
				else {
					verizonXpath = "/html/body/div/div/div/form/div/button[2]";
					Boolean verizonPopUp = driver.findElements(By.xpath(verizonXpath)).size() > 0;
					if (verizonPopUp == true) {
						driver.findElement(By.xpath(verizonXpath)).click();
					}
					
					commentsXpath = "//*[contains(@class, 'comments-title')]";
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(commentsXpath)));
					driver.findElement(By.xpath(commentsXpath)).click();
					
					Thread.sleep(2000);
					
					showMore = "//*[contains(@class, 'showNext D(b)')]";
					Boolean isPresent = driver.findElements(By.xpath(showMore)).size() > 0;
					int i = 0;
					while (isPresent == true) {
						driver.findElement(By.xpath(showMore)).click();
						Thread.sleep(2000);
						i += 1;
						Boolean check = driver.findElements(By.xpath(showMore)).size() > 0;
						if (check == false | i == 10) {
							isPresent = false;
						}
					}
				}
			}
			catch (Exception e) {
				System.out.println(e);	
			}
			finally {
				commentsList = driver.findElements(By.xpath("//*[contains(@class, 'C($c-fuji-grey-l) Mb(2px) Fz(14px) Lh(20px) Pend(8px)')]"));
				for (WebElement link : commentsList) {
					comments.add(link.getText());
				}
			}
			
			for (String i : comments) {
				System.out.println(i);
				System.out.println("===================================");
			}
		}
	}
	public void quit() {
		driver.quit();
	}
}
