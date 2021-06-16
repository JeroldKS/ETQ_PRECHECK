package MMP_396_MySQL_Database_Check;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class Base {
	public static Properties login;
	public static Connection mysql_Connection;
	public static WebDriver driver;
	public static String text;
	public static List<WebElement> texts;
	public static Statement mysql_statement_first;
	public static ResultSet source_Query;
	public static Properties prop;
	public static List<String> list_text;
	static Logger log = Logger.getLogger(Base.class.getName());

	@BeforeTest
	/**
	 * 
	 * @return driver
	 * @throws Exception
	 */
	public static WebDriver startbrowser() throws Exception {
		log.info("browser launch started....................");
		Properties prop = new Properties();
		FileInputStream file = new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\property");
		prop.load(file);
		String browsername = prop.getProperty("browser");
		String url = prop.getProperty("url");

		if (browsername.equals("chrome")) {
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\Driver\\chromedriver.exe");
			driver = new ChromeDriver();
			driver.get(url);
			log.info("chrome browser launched....................");
		} else {
			System.out.println("Not a chrome browser");
		}
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		return driver;

	}

	@BeforeTest
	/**
	 *  The method used to connect DataBase
	 * @throws Exception
	 */
	public static void db_connection() throws Exception {
		log.info("db connection started....................");
		login = new Properties();
		// fetching a DB name from property file
		try (FileInputStream in = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\main\\resources\\cred.properties")) {
			login.load(in);
		}
		String find_db_name = login.getProperty("DB_name");
		if (find_db_name.equals("mysql_source_db")) {
			Class.forName("com.mysql.jdbc.Driver");
			mysql_Connection = DriverManager.getConnection(
					"jdbc:mysql://" + login.getProperty("ip70") + ":" + login.getProperty("port70") + "/",
					login.getProperty("user70"), login.getProperty("pass70"));
			log.info("MYSQL DB connected....................");
		} else {
			System.out.println("Not a mysql source db");
		}
	}

	@AfterTest
	public static void connection_close() throws Exception {
		driver.close();
		mysql_Connection.close();
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

	public List<String> list_string() {
		list_text = new ArrayList<>();
		for (int i = 0; i < texts.size(); i++) {
			texts.get(i).getText();

			list_text.add(texts.get(i).getText());
		}
		return list_text;
	}

	public static ResultSet query(String query) throws SQLException {
		mysql_statement_first = mysql_Connection.createStatement();
		source_Query = mysql_statement_first.executeQuery(query);
		return source_Query;

	}

	public static Properties load_query_file() throws IOException {
		prop = new Properties();
		FileInputStream file = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\test\\resources\\MMP_396_MySQL_Database_Check.properties");
		prop.load(file);
		return prop;

	}

	@BeforeSuite
	public void log4j() {
		String log4jConfPath = System.getProperty("user.dir") + "\\src\\main\\resources\\log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);

	}

}
