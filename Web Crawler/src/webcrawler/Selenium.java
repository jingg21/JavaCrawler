package webcrawler;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Selenium extends WebCrawler{
	private List <WebElement> getSearchLinks;
	private List <WebElement> commentsList;
	private List <WebElement> getName;
	private List <WebElement> getThumbsUp;
	private List <WebElement> getThumbsDown;
	private List <WebElement> getDatetime;
	private ArrayList<String> article;
	private ArrayList<String> comments;
	private ArrayList<String> user_name;
	private ArrayList<String> dateTime;
	private ArrayList<String> thumbsUp;
	private ArrayList<String> thumbsDown;
	private String verizonXpath;
	private String verizonXpath2;
	private String commentsXpath;
	private String showMore;
	private String topReactions;
	private String latestReactions;
	private String url;
	private WebDriver driver;
	private WebDriverWait wait;
	WebElement articleName;
	private Scanner num_ThumbsUp;
	private Scanner num_ThumbsDown;
	private Scanner extractDateTime;
	private LocalDateTime convertTimeAgo;
	private DateTimeFormatter f_datetime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	
	public Selenium(String baseLink, int depth) {
		super(baseLink, depth);
		
		ChromeOptions options = new ChromeOptions();
		options.addExtensions(new File("D:\\Work\\ICT1009\\extension_1_24_4_0.crx"));
		System.setProperty("webdriver.chrome.driver", "D:\\Work\\ICT1009\\chromedriver_win32_test1\\chromedriver.exe");
		driver = new ChromeDriver(options);
		driver.get(baseLink);
		wait = new WebDriverWait(driver, 20);
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
	
	public void getData() throws InterruptedException, Exception, NoSuchElementException {
		commentsList = new ArrayList<WebElement>();
		comments = new ArrayList<String>();
		user_name = new ArrayList<String>();
		thumbsUp = new ArrayList<String>();
		thumbsDown = new ArrayList<String>();
		dateTime = new ArrayList<String>();
		article = new ArrayList<String>();
		
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
					verizonXpath2 = "//*[contains(@class, 'btn primary')]";
					Boolean verizonPopUp = driver.findElements(By.xpath(verizonXpath)).size() > 0;
					if (verizonPopUp == true) {
						driver.findElement(By.xpath(verizonXpath)).click();
					}
					
					Boolean verizonPopUp2 = driver.findElements(By.xpath(verizonXpath2)).size() > 0;
					if (verizonPopUp2 == true) {
						driver.findElement(By.xpath(verizonXpath2)).click();
					}
					
					commentsXpath = "//*[contains(@class, 'comments-title')]";
					topReactions = "//*[contains(@class, 'sort-filter-button')]";
					latestReactions = "//*[contains(@class, 'sort-by-created')]";
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(commentsXpath)));
					driver.findElement(By.xpath(commentsXpath)).click();
					Thread.sleep(2000);
					Boolean findSort = driver.findElements(By.xpath(topReactions)).size() > 0;
					if (findSort == true) {
						wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(topReactions)));
						driver.findElement(By.xpath(topReactions)).click();
						wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(latestReactions)));
						driver.findElement(By.xpath(latestReactions)).click();
					}
					
					
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
					
					Thread.sleep(3000);
				}
			}
			catch (Exception e) {
				System.out.println(e);
			}
			finally {
				commentsList = driver.findElements(By.xpath("//*[contains(@class, 'C($c-fuji-grey-l) Mb(2px) Fz(14px) Lh(20px) Pend(8px)')]"));
				getName = driver.findElements(By.xpath("//*[contains(@class, 'D(ib) Fw(b) P(0) Bd(0) M(0) Mend(10px) Fz(14px) Ta(start) C($c-fuji-blue-1-a)')]"));
				getThumbsUp = driver.findElements(By.xpath("//*[contains(@aria-label, 'Thumbs Up') or contains(@aria-label, 'Thumbs up')]"));
				getThumbsDown = driver.findElements(By.xpath("//*[contains(@aria-label, 'Thumbs Down') or contains(@aria-label, 'Thumbs down')]"));
				getDatetime = driver.findElements(By.xpath("//*[contains(@class, 'C($c-fuji-grey-g) Fz(12px)')]"));
				try {
					articleName = driver.findElement(By.xpath("//*[contains(@itemprop, 'headline')]"));
				}
				catch (NoSuchElementException e) {
					System.out.println(e);
				}

				int max = commentsList.size();

				String temp_int;
				String temp_string;
				for (int i = 0; i < max; i++) {
					article.add(articleName.getText());
					comments.add(commentsList.get(i).getText().trim().replace("\n", " "));
					user_name.add(getName.get(i).getText());
					num_ThumbsUp = new Scanner(getThumbsUp.get(i).getAttribute("aria-label"));
					num_ThumbsDown = new Scanner(getThumbsDown.get(i).getAttribute("aria-label"));
					extractDateTime = new Scanner(getDatetime.get(i).getText());
					temp_int = extractDateTime.next();
					temp_string = extractDateTime.next();

					thumbsUp.add(num_ThumbsUp.next());
					thumbsDown.add(num_ThumbsDown.next());
					
					if (temp_string.equals("minutes") || temp_string.equals("minute")) {
						convertTimeAgo = LocalDateTime.now().minus(Duration.ofMinutes(Integer.parseInt(temp_int)));
						dateTime.add(f_datetime.format(convertTimeAgo));
					}
					else if (temp_string.equals("hour") || temp_string.equals("hours")) {
						convertTimeAgo = LocalDateTime.now().minus(Duration.ofHours(Integer.parseInt(temp_int)));
						dateTime.add(f_datetime.format(convertTimeAgo));
					}
					else if (temp_string.equals("days") || temp_string.equals("day")) {
						convertTimeAgo = LocalDateTime.now().minus(Duration.ofDays(Integer.parseInt(temp_int)));
						dateTime.add(f_datetime.format(convertTimeAgo));
					}
					else if (temp_string.equals("seconds") || temp_string.equals("second")) {
						convertTimeAgo = LocalDateTime.now().minus(Duration.ofSeconds(Integer.parseInt(temp_int)));
						dateTime.add(f_datetime.format(convertTimeAgo));
					}
				}
			}
			
			System.out.println("Size: " + article.size() + " " + user_name.size() + " " + dateTime.size() + " " + thumbsUp.size()+ " " + thumbsDown.size()+ " " + comments.size());
		}
	}
	
	public void writeToCsv(String fileName) throws IOException, IllegalArgumentException {
		try {
			if (!fileName.contains(".csv")) {
				throw new IllegalArgumentException("Please indicate a correct .csv file");
			}
			
			File output = new File(fileName);
			FileOutputStream fos = new FileOutputStream(output);	 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			bw.write("Article," + "User," + "Date," + "Likes," + "Dislikes," + "Comments");
			bw.newLine();
			int size = user_name.size()
	;		for (int i = 0; i < size; i++) {
				bw.write("\"" + article.get(i) + "\"," + user_name.get(i) + "," + dateTime.get(i) + "," + 
						thumbsUp.get(i) + "," + thumbsDown.get(i) + ",\"" + comments.get(i) + "\"");
				bw.newLine();
			}
		 
			bw.close();
		}
		catch (IOException | IllegalArgumentException e) {
			System.out.println("No such element found.");
		}
		
	}
	
	public void quit() {
		driver.quit();
		System.out.println("Exit Successfully.");
	}
}
