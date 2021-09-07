package precheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import migrationStories.MMP369_MigrationDataCoreSchemaDisable;

public class Base {
	public static Properties loginProperties;
	public static Connection dbConnection;
	public static Connection targetDBConnection;
	public static Connection oobDBConnection;
	public static WebDriver driver;
	public static String text;
	public static List<WebElement> listOfWebElement;
	public static Statement mysqlStatement;
	public static Statement targetMysqlStatement;
	public static Statement oobMysqlStatement;
	public static ResultSet sourceQuery;
	public static ResultSet targetQuery;
	public static ResultSet oobQuery;
	public static Properties prop;
	public static Properties xpathProperties;
	public static Properties fileProperties = new Properties();
	public static List<String> listOfText;
	public static Session session;
	public static ChannelSftp sftpChannel;
	public static String osUserInput;
	public static String databaseUserInput;
	public static String envId = null;
	static Logger log = Logger.getLogger(Base.class.getName());

	/**
	 * add description about the method
	 * @return driver
	 * @throws Exception
	 */
	//@BeforeTest
	public static WebDriver startbrowser() throws Exception {
		log.info("browser launch started....................");
		Properties browserProperties = new Properties();
		FileInputStream browserFile = new FileInputStream(System.getProperty("user.dir") + "//src//main//resources//properties//browser.properties");
		browserProperties.load(browserFile);
		String browsername = browserProperties.getProperty("browser");
		if (browsername.equals("chrome")) {
			if(System.getProperty("os.name").equalsIgnoreCase("Linux")) {
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + "//src//main//resources//driver//chromedriver");
				driver = new ChromeDriver();
			} else if(System.getProperty("os.name").contains("Windows")){
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + "//src//main//resources//driver//chromedriver.exe");
				driver = new ChromeDriver();
			} else {
				log.error("This Programme will only run in windows/linux");
			}
			log.info("chrome browser launched....................");
		} else {
			log.info("Not a chrome browser");
		}
		return driver;

	}

	public static WebDriver loadLowLevelReportInBrowser() throws Exception {
		startbrowser();
		Properties browserProperties = new Properties();
		FileInputStream browserFile = new FileInputStream(System.getProperty("user.dir") + "//src//main//resources//properties//browser.properties");
		browserProperties.load(browserFile);
		driver.get("file://" + System.getProperty("user.dir") + browserProperties.getProperty("low"));
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		return driver;
	}
	
	public static WebDriver loadHighLevelReportInBrowser() throws Exception {
		startbrowser();
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
	public static void establishDatabaseconnection() throws Exception {
		log.info("db connection started....................");
		loginProperties = new Properties();
		// fetching a DB name from property file
		try (FileInputStream credentialsFile = new FileInputStream(
				System.getProperty("user.dir") + "//src//main//resources//properties//credentials.properties")) {
			loginProperties.load(credentialsFile);
		}
		try (FileInputStream filePaths = new FileInputStream(
				System.getProperty("user.dir") + "//src//main//resources//properties//file.properties")) {
			fileProperties.load(filePaths);
		}
		databaseUserInput = fileProperties.getProperty("database");
		if (databaseUserInput.equals("mysqlSource")) {
			Class.forName("com.mysql.jdbc.Driver");
			dbConnection = DriverManager.getConnection(
					"jdbc:mysql://" + loginProperties.getProperty("ipForMysqlSourceDB") + ":" + loginProperties.getProperty("portForMysqlSourceDB") + "/",
					loginProperties.getProperty("userNameForMysqlSourceDB"), loginProperties.getProperty("passwordForMysqlSourceDB"));
			log.info("Source MYSQL DB connected....................");
		}else if (databaseUserInput.equals("mysqltarget")) {
			Class.forName("com.mysql.jdbc.Driver");
			dbConnection = DriverManager.getConnection(
					"jdbc:mysql://" + loginProperties.getProperty("ipForMysqlTargetDB") + ":" + loginProperties.getProperty("portForTargetDB") + "/",
					loginProperties.getProperty("userNameForTargetDB"), loginProperties.getProperty("passwordForTargetDB"));
			log.info("Target MYSQL DB connected....................");
		} else if (databaseUserInput.equals("mssqlSource")) {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			dbConnection = DriverManager.getConnection(
					loginProperties.getProperty("ipForMssqlSourceDB"),
					loginProperties.getProperty("userNameForMssqlSourceDB"), loginProperties.getProperty("passwordForMssqlSourceDB"));
			log.info("MSSQL Source DB connected....................");
			
		} else if (databaseUserInput.equals("oracleSource")) {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			dbConnection = DriverManager.getConnection("jdbc:oracle:thin:@"+loginProperties.getProperty("ipForWindowsOracleSource")+":"
					+loginProperties.getProperty("portNoForWindowsOracleSource")+"/"+loginProperties.getProperty("serviceNameForWindowsOracleSource"),
			loginProperties.getProperty("userForWindowsOracleSource"), loginProperties.getProperty("passwordForWindowsOracleSource"));
			log.info("Oracle Source DB connected....................");
		}
	}

	/**
	 * The method used to connect OOB DataBase
	 * @throws Exception
	 */
	public static void establishOOBDatabaseconnection() throws Exception {
		log.info("db connection started....................");
		loginProperties = new Properties();
		try (FileInputStream credentialsFile = new FileInputStream(
				System.getProperty("user.dir") + "//src//main//resources//properties//credentials.properties")) {
			loginProperties.load(credentialsFile);
		}
		
		Class.forName("com.mysql.jdbc.Driver");
		oobDBConnection = DriverManager.getConnection(
				"jdbc:mysql://" + loginProperties.getProperty("hostForOOBDB") + ":" + loginProperties.getProperty("portForOOBDB") + "/",
				loginProperties.getProperty("userNameForOOBDB"), loginProperties.getProperty("passwordForOOBDB"));
		log.info("OOB DB connected....................");
	}
	/**
	 * This method is to close DB connection and chrome browser
	 * @throws Exception
	 */
	//@AfterTest
	public static void closeconnection() throws Exception {
		driver.close();
		log.info("all connection closed....................");
	}
	
	/**
	 * The method used to connect EC2 instance
	 * @throws Exception
	 */
	public static void establishSshConnectionForSourceInstance() throws Exception {
		loginProperties = new Properties();
		try (FileInputStream credentialsFile = new FileInputStream(
				System.getProperty("user.dir") + "//src//main//resources//properties//credentials.properties")) {
			loginProperties.load(credentialsFile);
		}
		int port = 22;
		JSch jsch = new JSch();
		try (FileInputStream filePaths = new FileInputStream(
				System.getProperty("user.dir") + "//src//main//resources//properties//file.properties")) {
			fileProperties.load(filePaths);
		}
		osUserInput = fileProperties.getProperty("os");
		if(osUserInput.equalsIgnoreCase("linux")) {
			session = jsch.getSession(loginProperties.getProperty("userNameForPrecheckLinux"),
					loginProperties.getProperty("ipForPrecheckLinux"), port);
			jsch.addIdentity(System.getProperty("user.dir")+"//src//main//resources//properties//ETQTesting.ppk");
			log.info("Linux session connected");
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			session = jsch.getSession(loginProperties.getProperty("userNameForWindowsMssqlSource"),
					loginProperties.getProperty("ipForWindowsMssqlSource"), port);
			session.setPassword(loginProperties.getProperty("passwordForWindowsMssqlSource"));
			log.info("Windows session connected");
		}
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();
		sftpChannel = (ChannelSftp) session.openChannel("sftp");
		sftpChannel.connect();
		log.info("SFTP channel connected");
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
		String user = loginProperties.getProperty("userNameForMysqlSourceDBInstance");
		int port = 22;

		JSch jsch = new JSch();

		session = jsch.getSession(user, host, port);
		jsch.addIdentity(System.getProperty("user.dir")+"//src//main//resources//properties//ETQTesting.ppk");
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();
		sftpChannel = (ChannelSftp) session.openChannel("sftp");
		sftpChannel.connect();
	}
	
	/**
	 * The method used to connect Destination Database
	 * @throws Exception
	 */
	public static String establishDestinationDatabaseconnection(String host, String user, String password, String port) throws Exception {
		log.info("Destination DB connection started....................");
		Class.forName("com.mysql.jdbc.Driver");
		targetDBConnection = DriverManager.getConnection(
				"jdbc:mysql://" + host + ":" + port + "/", user , password);
		log.info("MYSQL Destination DB connected....................");
		return "Connection Success";
	}
	
	public static String establishTargetDatabaseconnection() throws JSchException, SftpException, Exception {
		String connectionStatus = null;
		String host= null;
		String username= null;
		String password= null;
		String port= null;
		List<String> targetDBCredentials = new ArrayList<String>();
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_migration_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_migration_windows"));
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("db.target") && !line.contains("#")) {
					int count = 0;
					while(count < 4) {
						targetDBCredentials.add(br.readLine());
						count++;
					}
				} 
				if(line.contains("env_id") && !line.contains("#")) {
					envId = line.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
			}
			for(String credentials : targetDBCredentials) {
				if(credentials.contains("host")) {
					host = credentials.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
				if(credentials.contains("username")) {
					username = credentials.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
				if(credentials.contains("password")) {
					password = credentials.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
				if(credentials.contains("port")) {
					port = credentials.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
			}
			if(null != host && null != username && null != password && null != port &&
					!host.isEmpty() && !username.isEmpty() && !password.isEmpty() && !port.isEmpty()) {
				connectionStatus = establishDestinationDatabaseconnection(host, username, password, port);
			}
		}catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
		return connectionStatus;
	}
	
	/**
	 * The method used to get current environment ID in target
	 * @return
	 * @throws Exception
	 */
	//@BeforeSuite
	public String getEnvID() throws Exception {
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_migration_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_migration_windows"));
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine()) != null) {
				if(line.contains("env_id") && !line.contains("#")) {
					envId = line.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
			}
		}catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}System.out.println(envId);
		return envId;
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
	 * This method is used for parsing Query to source DataBase
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
	 * This method is used for parsing Query to target DataBase
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet targetQuery(String query) throws SQLException {
		targetMysqlStatement = targetDBConnection.createStatement();
		targetQuery = targetMysqlStatement.executeQuery(query);
		return targetQuery;
	}
	
	/**
	 * This method is used for parsing Query to OOB DataBase
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet oobQuery(String query) throws SQLException {
		oobMysqlStatement = oobDBConnection.createStatement();
		oobQuery = oobMysqlStatement.executeQuery(query);
		return oobQuery;
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
				System.getProperty("user.dir") + "//src//test//resources//precheck//xpath//xpath.properties");
		xpathProperties.load(xpathFile);
		return xpathProperties;

	}

	/**
	 * This method is for Load the log4j(Log) file
	 * @throws Exception 
	 */
	@BeforeSuite
	public void log4j() throws Exception {
		
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