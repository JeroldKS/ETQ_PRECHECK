package precheck;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

public class Base {
	public static Properties loginProperties;
	public static Connection mysqlConnection;
	public static WebDriver driver;
	public static String text;
	public static List<WebElement> texts;
	public static Statement mysqlStatement;
	public static ResultSet sourceQuery;
	public static Properties prop;
	public static Properties xpathProperties;
	public static List<String> listOfText;
	static Logger log = Logger.getLogger(Base.class.getName());

	/**
	 * add description about the method
	 * @return driver
	 * @throws Exception
	 */
	@BeforeTest
	public static WebDriver startbrowser() throws Exception {
		log.info("browser launch started....................");
		Properties browserProperties = new Properties();
		FileInputStream file = new FileInputStream(System.getProperty("user.dir") + "//src//main//resources//properties//browser.properties");
		browserProperties.load(file);
		String browsername = browserProperties.getProperty("browser");
		String url = browserProperties.getProperty("url");
		System.out.println("url::"+url);
		if (browsername.equals("chrome")) {
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "//src//main//resources//driver//chromedriver");
			driver = new ChromeDriver();
			
			log.info("chrome browser launched....................");
			if (browserProperties.getProperty("url").equals("low")) {
				driver.get(browserProperties.getProperty("low"));
			} else {
				driver.get(browserProperties.getProperty("high"));
			}
		} else {
			System.out.println("Not a chrome browser");
		}
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		return driver;

	}

	/**
	 * The method used to connect DataBase
	 * @throws Exception
	 */
	@BeforeTest
	public static void establishDatabaseconnection() throws Exception {
		log.info("db connection started....................");
		loginProperties = new Properties();
		// fetching a DB name from property file
		try (FileInputStream credentialsFile = new FileInputStream(
				System.getProperty("user.dir") + "//src//main//resources//properties//credentials.properties")) {
			loginProperties.load(credentialsFile);
		}
		String databaseName = loginProperties.getProperty("DB_name");
		if (databaseName.equals("mysql_source_db")) {
			Class.forName("com.mysql.jdbc.Driver");
			mysqlConnection = DriverManager.getConnection(
					"jdbc:mysql://" + loginProperties.getProperty("ip70") + ":" + loginProperties.getProperty("port70") + "/",
					loginProperties.getProperty("user70"), loginProperties.getProperty("pass70"));
			log.info("MYSQL DB connected....................");
		} else {
			System.out.println("Not a mysql source db");
		}
	}

	@AfterTest
	public static void closeDatabaseconnection() throws Exception {
		driver.close();
		mysqlConnection.close();
		log.info("all connection closed....................");
	}

	public static String xtext(String x) {
		text = driver.findElement(By.xpath(x)).getText();
		return text;
	}

	public static List<WebElement> xtexts(String x) {
		texts = driver.findElements(By.xpath(x));
		return texts;
	}

	public List<String> listString() {
		listOfText = new ArrayList<>();
		for (int i = 0; i < texts.size(); i++) {
			texts.get(i).getText();

			listOfText.add(texts.get(i).getText());
		}
		return listOfText;
	}

	public static ResultSet query(String query) throws SQLException {
		mysqlStatement = mysqlConnection.createStatement();
		sourceQuery = mysqlStatement.executeQuery(query);
		return sourceQuery;

	}

	@BeforeMethod
	public static Properties loadQueryFile() throws IOException {
		prop = new Properties();
		FileInputStream queryFile = new FileInputStream(
				System.getProperty("user.dir") + "//src//test//resources//MMP_396_query.properties");
		prop.load(queryFile);
		return prop;

	}

	@BeforeMethod
	public static Properties loadXpathFile() throws IOException {
		xpathProperties = new Properties();
		FileInputStream xpathFile = new FileInputStream(
				System.getProperty("user.dir") + "//src//test//resources//xpath.properties");
		xpathProperties.load(xpathFile);
		return xpathProperties;

	}

	@BeforeSuite
	public void log4j() {
		String log4jConfPath = System.getProperty("user.dir") + "//src//main//resources//properties//log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);

	}

	public static int generate(int max) {
		// 1999999999 int max
		return 1 + (int) (Math.random() * ((max - 1) + 1));
	}

}