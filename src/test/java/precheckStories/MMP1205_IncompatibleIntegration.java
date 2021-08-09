package precheckStories;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP1205_IncompatibleIntegration extends Base{
	
	static Logger log = Logger.getLogger(MMP1205_IncompatibleIntegration.class.getName());
  
	/**
	 * Check if XML Connection Profile Return any rows
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_checkXMLConnectionProfileRetrunsRow() throws JSchException, SftpException, Exception {
		log.info("TC 01 Checking if XML Connection Profile Return any rows. Started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP1205_query.properties");
		List<String> xmlConnectionProfileListInDB = new ArrayList<>();
		List<String> xmlConnectionProfileListInReport = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("xml_connection_profile"));
		while (sourceQuery.next()) {
			xmlConnectionProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("xmlConnectionProfile"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(null == xmlConnectionProfileListInDB || xmlConnectionProfileListInDB.isEmpty()) {
			log.info("This test case works only if XML Connection profile return any row");
		} else {
			for (int i = 1; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'XML Connection Profile')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					text = xtext(xpathProperties.getProperty("xmlConnectionProfile_p"));
					assertEquals(text, "The following XML Connection Profiles are not supported in NXG");
					listOfWebElement = xtexts(xpathProperties.getProperty("xmlConnectionProfile_list"));
					List<WebElement> xmlProfileList = listOfWebElement;
					for (int j = 0; j < xmlProfileList.size(); j++) {
						xmlConnectionProfileListInReport.add(xmlProfileList.get(j).getText());
					}
					Collections.sort(xmlConnectionProfileListInDB);
					Collections.sort(xmlConnectionProfileListInReport);
					assertEquals(xmlConnectionProfileListInReport, xmlConnectionProfileListInDB);
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the connection profile to use a REST Web Services profile that uses Cognito authentication");
				}
			}
		}
		dbConnection.close();
		log.info("TC 01 Checking if XML Connection Profile Return any rows. Ended..............");
	}
	
	/**
	 * Check if XML Connection Profile Return Empty row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_checkXMLConnectionProfileRetrunsEmptyRow() throws JSchException, SftpException, Exception {
		log.info("TC 02 Checking if XML Connection Profile Return empty row. Started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP1205_query.properties");
		List<String> xmlConnectionProfileListInDB = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("xml_connection_profile"));
		while (sourceQuery.next()) {
			xmlConnectionProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("xmlConnectionProfile"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(null == xmlConnectionProfileListInDB || xmlConnectionProfileListInDB.isEmpty()) {
			for (int i = 1; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'XML Connection Profile')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
					if (i == 1) {
						assertEquals(listDataList.get(0).getText(), "N/A");
					} else if (i == 2) {
						assertEquals(listDataList.get(0).getText(), "Good to Migrate");
					}
			}
		} else {
			log.info("This test case works only if XML Connection profile return empty row");
		}
		dbConnection.close();
		log.info("TC 02 Checking if XML Connection Profile Return empty row. Ended..............");
	}
	
	/**
	 * Check if CSV Connection Profile Return any rows
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_checkCSVConnectionProfileRetrunsRow() throws JSchException, SftpException, Exception {
		log.info("TC 01 Checking if CSV Connection Profile Return any rows. Started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP1205_query.properties");
		List<String> csvConnectionProfileListInDB = new ArrayList<>();
		List<String> csvConnectionProfileListInReport = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("csv_connection_profile"));
		while (sourceQuery.next()) {
			csvConnectionProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("csvConnectionProfile"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(null == csvConnectionProfileListInDB || csvConnectionProfileListInDB.isEmpty()) {
			log.info("This test case works only if CSV Connection profile return any row");
		} else {
			for (int i = 1; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'CSV Connection Profile')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					text = xtext(xpathProperties.getProperty("csvConnectionProfile_p"));
					assertEquals(text, "The following CSV Connection Profiles are not supported in NXG");
					listOfWebElement = xtexts(xpathProperties.getProperty("csvConnectionProfile_list"));
					List<WebElement> csvProfileList = listOfWebElement;
					for (int j = 0; j < csvProfileList.size(); j++) {
						csvConnectionProfileListInReport.add(csvProfileList.get(j).getText());
					}
					Collections.sort(csvConnectionProfileListInDB);
					Collections.sort(csvConnectionProfileListInReport);
					assertEquals(csvConnectionProfileListInReport, csvConnectionProfileListInDB);
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the connection profile to use a REST Web Services profile that uses Cognito authentication");
				}
			}
		}
		dbConnection.close();
		log.info("TC 03 Checking if CSV Connection Profile Return any rows. Ended..............");
	}
	
	/**
	 * Check if CSV Connection Profile Return Empty row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc04_checkCSVConnectionProfileRetrunsEmptyRow() throws JSchException, SftpException, Exception {
		log.info("TC 02 Checking if CSV Connection Profile Return empty row. Started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP1205_query.properties");
		List<String> csvConnectionProfileListInDB = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("csv_connection_profile"));
		while (sourceQuery.next()) {
			csvConnectionProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("csvConnectionProfile"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(null == csvConnectionProfileListInDB || csvConnectionProfileListInDB.isEmpty()) {
			for (int i = 1; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'CSV Connection Profile')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
					if (i == 1) {
						assertEquals(listDataList.get(0).getText(), "N/A");
					} else if (i == 2) {
						assertEquals(listDataList.get(0).getText(), "Good to Migrate");
					}
			}
		} else {
			log.info("This test case works only if CSV Connection profile return empty row");
		}
		dbConnection.close();
		log.info("TC 02 Checking if CSV Connection Profile Return empty row. Ended..............");
	}
	
	/**
	 * Check if Database Connection Profile returns any row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc05_checkForDatabaseConnectionProfileReturnsAnyRow() throws JSchException, SftpException, Exception {
		log.info("TC 05 Checking if Database Connection Profile Profile returns any row.Started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP387_query.properties");
		List<String> databaseConnectionProfileListInDB = new ArrayList<>();
		List<String> databaseConnectionProfileListInReport = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("database_connection_profile"));
		while (sourceQuery.next()) {
			databaseConnectionProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("databaseConnectionProfile"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(null == databaseConnectionProfileListInDB || databaseConnectionProfileListInDB.isEmpty()) {
			log.info("This Test case works only if Database Connection Profile returns any row");
		} else {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'Database Connection Profile')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					text = xtext(xpathProperties.getProperty("databaseConnectionProfile_p"));
					assertEquals(text, "The following Database Connection Profiles are not supported in NXG");
					listOfWebElement = xtexts(xpathProperties.getProperty("databaseConnectionProfile_list"));
					List<WebElement> databaseConnectionProfileList = listOfWebElement;
					for (int j = 0; j < databaseConnectionProfileList.size(); j++) {
						databaseConnectionProfileListInReport.add(databaseConnectionProfileList.get(j).getText());
					}
					Collections.sort(databaseConnectionProfileListInDB);
					Collections.sort(databaseConnectionProfileListInReport);
					assertEquals(databaseConnectionProfileListInReport, databaseConnectionProfileListInDB);
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the connection profile to use a REST Web Services profile that uses Cognito authentication");
				}
			}
		}
		dbConnection.close();
		log.info("TC 05 Checking if Database Connection Profile Profile returns any row.Ended..............");
	}
	
	/**
	 * Check if Database Connection Profile returns empty row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc06_checkForDatabaseConnectionProfileReturnsEmptyRow() throws JSchException, SftpException, Exception {
		log.info("TC 06 Checking if Database Connection Profile Profile returns empty row.Started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP387_query.properties");
		List<String> databaseConnectionProfileListInDB = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("database_connection_profile"));
		while (sourceQuery.next()) {
			databaseConnectionProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("databaseConnectionProfile"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(null == databaseConnectionProfileListInDB || databaseConnectionProfileListInDB.isEmpty()) {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'Database Connection Profile')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "Good to Migrate");
				} 
			}
		} else {
			log.info("This Test case works only if Database Connection Profile returns empty row");
		}
		dbConnection.close();
		log.info("TC 06 Checking if Database Connection Profile Profile returns empty row.Ended..............");
	}
	
	/**
	 * Check whether the report captured the sufficient details or not
	 * @throws Exception
	 */
	@Test
	public void tc07_checkWSAccessProfileNotCaptured() throws Exception {
		log.info("TC 07 Checking whether WS Access Profile not captured in Report. Started.............");
		loadHighLevelReportInBrowser();
		String web = driver.getPageSource().contains("WS Access Profile") ? "WS Access Profile Available" : "WS Access Profile Not Available";
		Assert.assertEquals(web, "WS Access Profile Not Available");
		dbConnection.close();
		log.info("TC 07 Checking whether WS Access Profile not captured in Report. Ended.............");
	}
	
	/**
	 * Check if WS Restful Profile returns any row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc08_checkIfWSRestfulProfileReturnsRow() throws JSchException, SftpException, Exception {
		log.info("TC 08 Checking For WS Restful Profile returns any row. started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP387_query.properties");
		List<String> wsRestfulProfileListInDB = new ArrayList<>();
		List<String> wsRestfulProfileListInReport = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("ws_restful_profile"));
		while (sourceQuery.next()) {
			wsRestfulProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("wsRestfulProfile"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(null == wsRestfulProfileListInDB || wsRestfulProfileListInDB.isEmpty()) {
			log.info("This Test case works only if WS Restful Profile returns any row");
		} else {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'WS Restful Profile')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					text = xtext(xpathProperties.getProperty("wsRestfulProfile_p"));
					assertEquals(text, "The following Restful Web Service Profiles do not have the correct authentication");
					listOfWebElement = xtexts(xpathProperties.getProperty("wsRestfulProfile_list"));
					List<WebElement> wsProfileList = listOfWebElement;
					for (int j = 0; j < wsProfileList.size(); j++) {
						wsRestfulProfileListInReport.add(wsProfileList.get(j).getText());
					}
					Collections.sort(wsRestfulProfileListInDB);
					Collections.sort(wsRestfulProfileListInReport);
					assertEquals(wsRestfulProfileListInReport, wsRestfulProfileListInDB);
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the endpoints to use Cognito authentication");
				}
			}
		}
		dbConnection.close();
		log.info("TC 08 Checking For WS Restful Profile returns any row. ended..............");
	}
	
	/**
	 * Check if WS Restful Profile returns empty row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc09_checkForWSRestfulProfileReturnseEmptyRow() throws JSchException, SftpException, Exception {
		log.info("TC 08 Checking For WS Restful Profile returns empty row. started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP387_query.properties");
		List<String> wsRestfulProfileListInDB = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("ws_restful_profile"));
		while (sourceQuery.next()) {
			wsRestfulProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("wsRestfulProfile"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(null == wsRestfulProfileListInDB || wsRestfulProfileListInDB.isEmpty()) {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'WS Restful Profile')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "Good to Migrate");
				}
			}
		} else {
			log.info("This test case works only if WS Restful Profile returns empty row");
		}
		dbConnection.close();
		log.info("TC 08 Checking For WS Restful Profile returns empty row. ended..............");
	}
	
	/**
	 * Check for WS Restful Operation Profile returns any row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc10_checkForWSRestfulOperationProfileReturnsAnyRow() throws JSchException, SftpException, Exception {
		log.info("TC 10 Checking For WS Restful Operation Profile returns Any row. started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP387_query.properties");
		List<String> wsRestfulOperationProfileListInDB = new ArrayList<>();
		List<String> wsRestfulOperationProfileListInReport = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("ws_restful_operation_profile"));
		while (sourceQuery.next()) {
			wsRestfulOperationProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("wsRestfulOperation"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(null == wsRestfulOperationProfileListInDB || wsRestfulOperationProfileListInDB.isEmpty()) {
			log.info("This test case works only if WS Restful Operation returns any row");
		} else {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'WS Restful Operation')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					text = xtext(xpathProperties.getProperty("wsRestfulOperation_p"));
					assertEquals(text, "The following Restful Operations do not have the correct authentication");
					listOfWebElement = xtexts(xpathProperties.getProperty("wsRestfulOperation_list"));
					List<WebElement> wsOperationProfileList = listOfWebElement;
					for (int j = 0; j < wsOperationProfileList.size(); j++) {
						wsRestfulOperationProfileListInReport.add(wsOperationProfileList.get(j).getText());
					}
					Collections.sort(wsRestfulOperationProfileListInDB);
					Collections.sort(wsRestfulOperationProfileListInReport);
					assertEquals(wsRestfulOperationProfileListInReport, wsRestfulOperationProfileListInDB);
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the endpoints to use Cognito authentication");
				}
			}
		}
		dbConnection.close();
		log.info("TC 10 Checking For WS Restful Operation Profile returns Any row. Ended..............");
	}
	
	/**
	 * Check for WS Restful Operation Profile returns empty row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc11_checkForWSRestfulOperationProfileReturnsEmptyRow() throws JSchException, SftpException, Exception {
		log.info("TC 11 Checking For WS Restful Operation Profile returns Empty row. started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP387_query.properties");
		List<String> wsRestfulOperationProfileListInDB = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("ws_restful_operation_profile"));
		while (sourceQuery.next()) {
			wsRestfulOperationProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("wsRestfulOperation"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(null == wsRestfulOperationProfileListInDB || wsRestfulOperationProfileListInDB.isEmpty()) {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'WS Restful Operation')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "Good to Migrate");
				}
			}
		}  else {
			log.info("This test case works only if WS Restful Operation returns empty row");
		}
		dbConnection.close();
		log.info("TC 11 Checking For WS Restful Operation Profile returns Empty row. Ended..............");
	}
	
	/**
	 * Check if SOAP WS Profile returns Any row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc12_checkIfSoapWSProfileReturnsAnyRow() throws JSchException, SftpException, Exception {
		log.info("TC 12 Checking For SOAP WS Connection Profile returns any row. started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP387_query.properties");
		List<String> soapProfileListInDB = new ArrayList<>();
		List<String> soapProfileListInReport = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("soap_profile"));
		while (sourceQuery.next()) {
			soapProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("soapConnectionProfile"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(null == soapProfileListInDB || soapProfileListInDB.isEmpty()) {
			log.info("This test case works only if SOAP WS Profile returns any row");
		} else {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'SOAP WS Profile')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					text = xtext(xpathProperties.getProperty("soapConnectionProfile_p"));
					assertEquals(text, "The following SOAP Profiles are not supported in NXG");
					listOfWebElement = xtexts(xpathProperties.getProperty("soapConnectionProfile_list"));
					List<WebElement> soapProfileList = listOfWebElement;
					for (int j = 0; j < soapProfileList.size(); j++) {
						soapProfileListInReport.add(soapProfileList.get(j).getText());
					}
					Collections.sort(soapProfileListInDB);
					Collections.sort(soapProfileListInReport);
					assertEquals(soapProfileListInReport, soapProfileListInDB);
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the connection profile to use a REST Web Services profile that uses Cognito authentication");
				}
			}
		}
		dbConnection.close();
		log.info("TC 12 Checking For SOAP WS Connection Profile returns any row. ended..............");
	}
	
	/**
	 * Check if SOAP WS Profile returns Empty row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc13_checkIfSoapWSProfileReturnsEmptyRow() throws JSchException, SftpException, Exception {
		log.info("TC 13 Checking For SOAP WS Connection Profile returns empty row. started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP387_query.properties");
		List<String> soapProfileListInDB = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("soap_profile"));
		while (sourceQuery.next()) {
			soapProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("soapConnectionProfile"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		
		if(null == soapProfileListInDB || soapProfileListInDB.isEmpty()) {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'SOAP WS Profile')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "Good to Migrate");
				}
			}
		} else {
			log.info("This test case works only if SOAP WS Profile returns empty row");
		}
		dbConnection.close();
		log.info("TC 13 Checking For SOAP WS Connection Profile returns empty row. ended..............");
	}
}
