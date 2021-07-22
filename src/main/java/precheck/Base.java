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

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Base {
	public static Properties loginProperties;
	public static Connection dbConnection;
	public static WebDriver driver;
	public static String text;
	public static List<WebElement> listOfWebElement;
	public static Statement mysqlStatement;
	public static ResultSet sourceQuery;
	public static Properties prop;
	public static Properties xpathProperties;
	public static List<String> listOfText;
	public static Session session;
	public static ChannelSftp sftpChannel;
	public static Session jschSession = null;
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
		FileInputStream browserFile = new FileInputStream(System.getProperty("user.dir") + "//src//main//resources//properties//browser.properties");
		browserProperties.load(browserFile);
		String browsername = browserProperties.getProperty("browser");
		if (browsername.equals("chrome")) {
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "//src//main//resources//driver//chromedriver.exe");
			driver = new ChromeDriver();
			
			log.info("chrome browser launched....................");
		} else {
			System.out.println("Not a chrome browser");
		}
		return driver;

	}

	public static WebDriver loadLowLevelReportInBrowser() throws Exception {
		Properties browserProperties = new Properties();
		FileInputStream browserFile = new FileInputStream(System.getProperty("user.dir") + "//src//main//resources//properties//browser.properties");
		browserProperties.load(browserFile);
		driver.get("file://" + System.getProperty("user.dir") + browserProperties.getProperty("low"));
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		return driver;
	}
	
	public static WebDriver loadHighLevelReportInBrowser() throws Exception {
		Properties browserProperties = new Properties();
		FileInputStream browserFile = new FileInputStream(System.getProperty("user.dir") + "//src//main//resources//properties//browser.properties");
		browserProperties.load(browserFile);
		driver.get("file://" + System.getProperty("user.dir") + browserProperties.getProperty("high"));
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		return driver;
	}
	
	/**
	 * The method used to connect DataBase
	 * @throws Exception
	 */
	public static void establishDatabaseconnection(String dbType) throws Exception {
		log.info("db connection started....................");
		loginProperties = new Properties();
		// fetching a DB name from property file
		try (FileInputStream credentialsFile = new FileInputStream(
				System.getProperty("user.dir") + "//src//main//resources//properties//credentials.properties")) {
			loginProperties.load(credentialsFile);
		}
		//String databaseName = loginProperties.getProperty("DB_name");
		if (dbType.equals("mysqlSource")) {
			Class.forName("com.mysql.jdbc.Driver");
			dbConnection = DriverManager.getConnection(
					"jdbc:mysql://" + loginProperties.getProperty("ipForMysqlSourceDB") + ":" + loginProperties.getProperty("port70") + "/",
					loginProperties.getProperty("user70"), loginProperties.getProperty("pass70"));
			log.info("MYSQL DB connected....................");
		} else if (dbType.equals("mssqlSource")) {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			dbConnection = DriverManager.getConnection(
					loginProperties.getProperty("msip266"),
					loginProperties.getProperty("msuser266"), loginProperties.getProperty("mspass266"));
			log.info("MYSQL DB connected....................");
			
		} else if (dbType.equals("oracleSource")) {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			dbConnection = DriverManager.getConnection("jdbc:oracle:thin:@"+loginProperties.getProperty("ipForWindowsOracleSource")+":"
					+loginProperties.getProperty("portNoForWindowsOracleSource")+"/"+loginProperties.getProperty("serviceNameForWindowsOracleSource"),
			loginProperties.getProperty("userForWindowsOracleSource"), loginProperties.getProperty("passwordForWindowsOracleSource"));
			log.info("Oracle DB connected....................");
		}
	}

	/**
	 * This method is to close DB connection and chrome browser
	 * @throws Exception
	 */
	@AfterTest
	public static void closeconnection() throws Exception {
		driver.close();
		//dbConnection.close();
		log.info("all connection closed....................");
	}
	
	/**
	 * The method used to connect EC2 instance
	 * @throws Exception
	 */
	public static void establishSshConnection() throws Exception {
		String user = "ec2-user";
		String host = "54.158.99.31";
		int port = 22;

		JSch jsch = new JSch();

		session = jsch.getSession(user, host, port);
		jsch.addIdentity(System.getProperty("user.dir")+"//src//main//resources//properties//ETQTesting.ppk");
		session.setConfig("StrictHostKeyChecking", "no");
		System.out.println("Establishing Connection...");
		session.connect();
		System.out.println("Connection established.");
		System.out.println("Creating SFTP Channel.");
		sftpChannel = (ChannelSftp) session.openChannel("sftp");
		sftpChannel.connect();
	}
	
	/**
	 * The method used to connect EC2 instance
	 * @throws Exception
	 */
	public static void establishSshConnectionforSourceDB() throws Exception {
		loginProperties = new Properties();
		// fetching a DB name from property file
		try (FileInputStream credentialsFile = new FileInputStream(
				System.getProperty("user.dir") + "//src//main//resources//properties//credentials.properties")) {
			loginProperties.load(credentialsFile);
		}
		String host = loginProperties.getProperty("ipForMysqlSourceDB");
		String user = loginProperties.getProperty("userNameForSourceDB");
		int port = 22;

		JSch jsch = new JSch();

		session = jsch.getSession(user, host, port);
		jsch.addIdentity(System.getProperty("user.dir")+"//src//main//resources//properties//ETQTesting.ppk");
		session.setConfig("StrictHostKeyChecking", "no");
		System.out.println("Establishing Connection...");
		session.connect();
		System.out.println("Connection established.");
		System.out.println("Creating SFTP Channel.");
		sftpChannel = (ChannelSftp) session.openChannel("sftp");
		sftpChannel.connect();
	}
	
	public static void establishWindowsSshConnection() throws Exception {
		loginProperties = new Properties();
		// fetching a DB name from property file
		try (FileInputStream credentialsFile = new FileInputStream(
				System.getProperty("user.dir") + "//src//main//resources//properties//credentials.properties")) {
			loginProperties.load(credentialsFile);
		}
		int port = 22;
		JSch jsch = new JSch();
		jschSession = jsch.getSession(loginProperties.getProperty("windowsuser266"),
				loginProperties.getProperty("windowsip266"), port);
		jschSession.setPassword(loginProperties.getProperty("windowspass266"));
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		jschSession.setConfig(config);
		jschSession.connect();
	}

	/**
	 * This method is for fetching web element's text
	 * @param x
	 * @return
	 */
	public static String xtext(String x) {
		text = driver.findElement(By.xpath(x)).getText();
		return text;
	}
	
	/**
	 * This method is for fetching List of web element
	 * @param x
	 * @return
	 */
	public static List<WebElement> xtexts(String x) {
		listOfWebElement = driver.findElements(By.xpath(x));
		return listOfWebElement;
	}

	/**
	 * This method is for fetching List of web element's text
	 * @param x
	 * @return
	 */
	public List<String> listString() {
		listOfText = new ArrayList<>();
		for (int i = 0; i < listOfWebElement.size(); i++) {
			listOfWebElement.get(i).getText();
			listOfText.add(listOfWebElement.get(i).getText());
		}
		return listOfText;
	}

	/**
	 * This method is used for parsing Query to DataBase
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet query(String query) throws SQLException {
		mysqlStatement = dbConnection.createStatement();
		sourceQuery = mysqlStatement.executeQuery(query);
		return sourceQuery;

	}

	/**
	 * This method is for Load the Query file
	 * @return
	 * @throws IOException
	 */
	public static Properties loadQueryFile(String queryPath) throws IOException {
		prop = new Properties();
			FileInputStream queryFile = new FileInputStream(
					System.getProperty("user.dir") + queryPath);
			prop.load(queryFile);
		return prop;
		

	}

	/**
	 * This method is for Load the Xpath file
	 * @return
	 * @throws IOException
	 */
	@BeforeMethod
	public static Properties loadXpathFile() throws IOException {
		xpathProperties = new Properties();
		FileInputStream xpathFile = new FileInputStream(
				System.getProperty("user.dir") + "//src//test//resources////precheck//xpath//xpath.properties");
		xpathProperties.load(xpathFile);
		return xpathProperties;

	}

	/**
	 * This method is for Load the log4j(Log) file
	 */
	@BeforeSuite
	public void log4j() {
		String log4jConfPath = System.getProperty("user.dir") + "//src//main//resources//properties//log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);

	}

	/**
	 * This method is to generate random 'int' number
	 * @param max
	 * @return
	 */
	public static int generate(int max) {
		// 1999999999 int max
		return 1 + (int) (Math.random() * ((max - 1) + 1));
	}

}