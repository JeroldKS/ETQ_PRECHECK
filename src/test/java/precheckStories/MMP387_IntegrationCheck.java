package precheckStories;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP387_IntegrationCheck extends Base{
	
	static Logger log = Logger.getLogger(MMP387_IntegrationCheck.class.getName());
  
	/**
	 * Checking whether the ssoEnabled is enabled 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_isSsoEnabled() throws JSchException, SftpException, Exception {
		log.info("TC 01 Checking if SSO enabled started..............");
		loadHighLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String propsFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					propsFilePath = line.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
			}
			if(null != propsFilePath) {
				if(osUserInput.equalsIgnoreCase("linux")) {
					propsFilePath = propsFilePath + "/config.properties";
				} else if(osUserInput.equalsIgnoreCase("windows")) {
					propsFilePath ="/" + propsFilePath.replaceAll("\\\\", "/") + "/config.properties";
				}
				stream = sftpChannel.get(propsFilePath);
				br = new BufferedReader(new InputStreamReader(stream));
				String ssoStatus = "SSO Disabled";
				while ((line = br.readLine()) != null) {
					if (line.contains("ssoEnabled") && !line.contains("#")) {
						String ssoEnabled = line.split("=")[1];
						if(ssoEnabled.trim().equals("1")) {
							ssoStatus = "SSO Enabled";
							listOfWebElement = xtexts(xpathProperties.getProperty("ssoEnabled"));
							List<WebElement> listOfWebElementCopy = listOfWebElement;
							for (int i = 0; i < listOfWebElementCopy.size(); i++) {
								listOfWebElement = xtexts("//*[contains(text(),'SSO Authentication')]/../td[" + (i + 1) + "]");
								List<WebElement> listDataList = listOfWebElement;
								if (i == 1) {
									assertEquals(listDataList.get(0).getText(), "Action required");
								} else if (i == 2) {
									assertEquals(listDataList.get(0).getText(), "This authentication is not supported in NXG");
								} else if (i == 3) {
									assertEquals(listDataList.get(0).getText(), "Reconfigure the authentication to the a supported option");
								}
							}
						}
					}
				}
				assertEquals(ssoStatus, "SSO Enabled");
			}
			sftpChannel.disconnect();
			session.disconnect();
			log.info("TC 01 Checking if SSO enabled ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	/**
	 * Checking whether the ssoEnabled is disabled 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_isSsoDisabled() throws JSchException, SftpException, Exception {
		log.info("TC 02 Checking if SSO disabled started..............");
		loadHighLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String propsFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					propsFilePath = line.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
			}
			if(null != propsFilePath) {
				if(osUserInput.equalsIgnoreCase("linux")) {
					propsFilePath = propsFilePath + "/config.properties";
				} else if(osUserInput.equalsIgnoreCase("windows")) {
					propsFilePath ="/" + propsFilePath.replaceAll("\\\\", "/") + "/config.properties";
				}
				stream = sftpChannel.get(propsFilePath);
				br = new BufferedReader(new InputStreamReader(stream));
				String ssoStatus = "SSO Enabled";
				while ((line = br.readLine()) != null) {
					if (line.contains("ssoEnabled") && !line.contains("#")) {
						String ssoEnabled = line.split("=")[1];
						if(ssoEnabled.trim().equals("0")) {
							ssoStatus = "SSO Disabled";
							listOfWebElement = xtexts(xpathProperties.getProperty("ssoEnabled"));
							List<WebElement> listOfWebElementCopy = listOfWebElement;
							for (int i = 0; i < listOfWebElementCopy.size(); i++) {
								listOfWebElement = xtexts("//*[contains(text(),'SSO Authentication')]/../td[" + (i + 1) + "]");
								List<WebElement> listDataList = listOfWebElement;
								if (i == 1) {
									assertEquals(listDataList.get(0).getText(), "No Action");
								} else if (i == 2) {
									assertEquals(listDataList.get(0).getText(), "N/A");
								} else if (i == 3) {
									assertEquals(listDataList.get(0).getText(), "Good to Migrate");
								}
							}
						}
					}
				}
				assertEquals(ssoStatus, "SSO Disabled");
			}
			sftpChannel.disconnect();
			session.disconnect();
			log.info("TC 02 Checking if SSO disabled ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	/**
	 * Checking whether the SSO type available
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_checkSsoType() throws JSchException, SftpException, Exception {
		log.info("TC 03 Checking if SSO type available started..............");
		loadHighLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String propsFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					propsFilePath = line.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
			}
			if(null != propsFilePath) {
				if(osUserInput.equalsIgnoreCase("linux")) {
					propsFilePath = propsFilePath + "/config.properties";
				} else if(osUserInput.equalsIgnoreCase("windows")) {
					propsFilePath ="/" + propsFilePath.replaceAll("\\\\", "/") + "/config.properties";
				}
				stream = sftpChannel.get(propsFilePath);
				br = new BufferedReader(new InputStreamReader(stream));
				boolean flag = false;
				String ssoType = null;
				while ((line = br.readLine()) != null) {
					if (line.contains("ssoType") && !line.contains("#")) {
						String[] ssoTypeArray = line.split("=");
						if(ssoTypeArray.length > 1 && !ssoTypeArray[1].trim().isEmpty()) {
							ssoType = ssoTypeArray[1].trim().replaceAll("\"", "");
							flag = true;
						}
						listOfWebElement = xtexts(xpathProperties.getProperty("ssoType"));
						List<WebElement> listOfWebElementCopy = listOfWebElement;
						for (int i = 0; i < listOfWebElementCopy.size(); i++) {
							listOfWebElement = xtexts("//*[contains(text(),'SSO Authentication')]/../../tr[3]/td[" + (i + 1) + "]");
							List<WebElement> listDataList = listOfWebElement;
							if (i == 0 && flag == true) {
								assertEquals(listDataList.get(0).getText(), ssoType+" SSO");
							} else if (i == 1 && flag == true) {
								assertEquals(listDataList.get(0).getText(), "Action required");
							} else if (i == 2 && flag == true) {
								assertEquals(listDataList.get(0).getText(), "This authentication is not supported in NXG");
							} else if (i == 3 && flag == true) {
								assertEquals(listDataList.get(0).getText(), "Reconfigure the authentication to the a supported option");
							} else if (i == 0 && flag == false) {
								assertEquals(listDataList.get(0).getText(), "SSO Type");
							} else if (i == 1 && flag == false) {
								assertEquals(listDataList.get(0).getText(), "No Action");
							} else if (i == 2 && flag == false) {
								assertEquals(listDataList.get(0).getText(), "N/A");
							} else if (i == 3 && flag == false) {
								assertEquals(listDataList.get(0).getText(), "Good to Migrate");
							}
						}
					}
				}
			}
			sftpChannel.disconnect();
			session.disconnect();
			log.info("TC 03 Checking if SSO type available ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	/**
	 * Check for Share Point Profile
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc04_checkForSharePointProfile() throws JSchException, SftpException, Exception {
		log.info("TC 04 Checking For Share Point Profile started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP387_query.properties");
		List<String> sharePointProfileListInDB = new ArrayList<>();
		List<String> sharePointProfileListInReport = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("sharepoint_profile"));
		while (sourceQuery.next()) {
			sharePointProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("sharePointProfile"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		for (int i = 0; i < listOfWebElementCopy.size(); i++) {
			listOfWebElement = xtexts("//*[contains(text(),'Sharepoint Connection Profile')]/../td[" + (i + 1) + "]");
			List<WebElement> listDataList = listOfWebElement;
			if(null == sharePointProfileListInDB || sharePointProfileListInDB.isEmpty()) {
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 3) {
					assertEquals(listDataList.get(0).getText(), "Good to Migrate");
				}
			} else {
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "Action Required");
				} else if (i == 2) {
					text = xtext(xpathProperties.getProperty("sharePointProfile_p"));
					assertEquals(text, "This authentication is not supported in NXG");
					listOfWebElement = xtexts(xpathProperties.getProperty("sharePointProfile_list"));
					List<WebElement> sharePointList = listOfWebElement;
					for (int j = 0; j < sharePointList.size(); j++) {
						sharePointProfileListInReport.add(sharePointList.get(j).getText());
					}
					Collections.sort(sharePointProfileListInDB);
					Collections.sort(sharePointProfileListInReport);
					assertEquals(sharePointProfileListInDB, sharePointProfileListInReport);
				} else if (i == 3) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the connection profile to the use REST Web Services instead");
				}
			}
		}
		log.info("TC 04 Checking For Share Point Profile ended..............");
	}
	
	/**
	 * Check for WS Restful Profile
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc05_checkForWSRestfulProfile() throws JSchException, SftpException, Exception {
		log.info("TC 05 Checking For WS Restful Profile Profile started..............");
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
		for (int i = 0; i < listOfWebElementCopy.size(); i++) {
			listOfWebElement = xtexts("//*[contains(text(),'WS Restful Connection Profile')]/../td[" + (i + 1) + "]");
			List<WebElement> listDataList = listOfWebElement;
			if(null == wsRestfulProfileListInDB || wsRestfulProfileListInDB.isEmpty()) {
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 3) {
					assertEquals(listDataList.get(0).getText(), "Good to Migrate");
				}
			} else {
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "Action Required");
				} else if (i == 2) {
					text = xtext(xpathProperties.getProperty("wsRestfulProfile_p"));
					assertEquals(text, "This authentication is not supported in NXG");
					listOfWebElement = xtexts(xpathProperties.getProperty("wsRestfulProfile_list"));
					List<WebElement> wsProfileList = listOfWebElement;
					for (int j = 0; j < wsProfileList.size(); j++) {
						wsRestfulProfileListInReport.add(wsProfileList.get(j).getText());
					}
					Collections.sort(wsRestfulProfileListInDB);
					Collections.sort(wsRestfulProfileListInReport);
					assertEquals(wsRestfulProfileListInDB, wsRestfulProfileListInReport);
				} else if (i == 3) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the connection profile to the use REST Web Services instead");
				}
			}
		}
		tc05_checkForWSRestfulOperationProfile();
		log.info("TC 05 Checking For WS Restful Profile Profile ended..............");
	}
	
	/**
	 * Check for WS Restful Operation Profile
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	public static void tc05_checkForWSRestfulOperationProfile() throws JSchException, SftpException, Exception {
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
		for (int i = 0; i < listOfWebElementCopy.size(); i++) {
			listOfWebElement = xtexts("//*[contains(text(),'WS Restful Operation Connection Profile')]/../td[" + (i + 1) + "]");
			List<WebElement> listDataList = listOfWebElement;
			if(null == wsRestfulOperationProfileListInDB || wsRestfulOperationProfileListInDB.isEmpty()) {
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 3) {
					assertEquals(listDataList.get(0).getText(), "Good to Migrate");
				}
			} else {
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "Action Required");
				} else if (i == 2) {
					text = xtext(xpathProperties.getProperty("wsRestfulOperation_p"));
					assertEquals(text, "This authentication is not supported in NXG");
					listOfWebElement = xtexts(xpathProperties.getProperty("wsRestfulOperation_list"));
					List<WebElement> wsOperationProfileList = listOfWebElement;
					for (int j = 0; j < wsOperationProfileList.size(); j++) {
						wsRestfulOperationProfileListInReport.add(wsOperationProfileList.get(j).getText());
					}
					Collections.sort(wsRestfulOperationProfileListInDB);
					Collections.sort(wsRestfulOperationProfileListInReport);
					assertEquals(wsRestfulOperationProfileListInDB, wsRestfulOperationProfileListInReport);
				} else if (i == 3) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the connection profile to the use REST Web Services instead");
				}
			}
		}
	}
	
	/**
	 * Check for Database Connection Profile
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc06_checkForDatabaseConnectionProfile() throws JSchException, SftpException, Exception {
		log.info("TC 06 Checking For Database Connection Profile Profile started..............");
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
		for (int i = 0; i < listOfWebElementCopy.size(); i++) {
			listOfWebElement = xtexts("//*[contains(text(),'Database Connection Profile')]/../td[" + (i + 1) + "]");
			List<WebElement> listDataList = listOfWebElement;
			if(null == databaseConnectionProfileListInDB || databaseConnectionProfileListInDB.isEmpty()) {
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 3) {
					assertEquals(listDataList.get(0).getText(), "Good to Migrate");
				}
			} else {
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "Action Required");
				} else if (i == 2) {
					text = xtext(xpathProperties.getProperty("databaseConnectionProfile_p"));
					assertEquals(text, "This authentication is not supported in NXG");
					listOfWebElement = xtexts(xpathProperties.getProperty("databaseConnectionProfile_list"));
					List<WebElement> databaseConnectionProfileList = listOfWebElement;
					for (int j = 0; j < databaseConnectionProfileList.size(); j++) {
						databaseConnectionProfileListInReport.add(databaseConnectionProfileList.get(j).getText());
					}
					Collections.sort(databaseConnectionProfileListInDB);
					Collections.sort(databaseConnectionProfileListInReport);
					assertEquals(databaseConnectionProfileListInDB, databaseConnectionProfileListInReport);
				} else if (i == 3) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the connection profile to the use REST Web Services instead");
				}
			}
		}
		log.info("TC 06 Checking For Database Connection Profile Profile ended..............");
	}
	
	/**
	 * Check for WS Access Connection Profile
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc07_checkForwsAccessConnectionProfile() throws JSchException, SftpException, Exception {
		log.info("TC 07 Checking For WS Access Connection Profile Profile started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP387_query.properties");
		List<String> wsAccessConnectionProfileListInDB = new ArrayList<>();
		List<String> wsAccessConnectionProfileListInReport = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("ws_access_connection_profile"));
		while (sourceQuery.next()) {
			wsAccessConnectionProfileListInDB.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("wsAccessConnectionProfile"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		for (int i = 0; i < listOfWebElementCopy.size(); i++) {
			listOfWebElement = xtexts("//*[contains(text(),'WS Access Connection Profile')]/../td[" + (i + 1) + "]");
			List<WebElement> listDataList = listOfWebElement;
			if(null == wsAccessConnectionProfileListInDB || wsAccessConnectionProfileListInDB.isEmpty()) {
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 3) {
					assertEquals(listDataList.get(0).getText(), "Good to Migrate");
				}
			} else {
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "Action Required");
				} else if (i == 2) {
					text = xtext(xpathProperties.getProperty("wsAccessConnectionProfile_p"));
					assertEquals(text, "This authentication is not supported in NXG");
					listOfWebElement = xtexts(xpathProperties.getProperty("wsAccessConnectionProfile_list"));
					List<WebElement> wsAccessConnectionProfileList = listOfWebElement;
					for (int j = 0; j < wsAccessConnectionProfileList.size(); j++) {
						wsAccessConnectionProfileListInReport.add(wsAccessConnectionProfileList.get(j).getText());
					}
					Collections.sort(wsAccessConnectionProfileListInDB);
					Collections.sort(wsAccessConnectionProfileListInReport);
					assertEquals(wsAccessConnectionProfileListInDB, wsAccessConnectionProfileListInReport);
				} else if (i == 3) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the connection profile to the use REST Web Services instead");
				}
			}
		}
		log.info("TC 07 Checking For WS Access Connection Profile Profile ended..............");
	}
	
	/**
	 * Check for SOAP Profile
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc08_checkForsoapProfile() throws JSchException, SftpException, Exception {
		log.info("TC 08 Checking For SOAP Connection Profile Profile started..............");
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
		for (int i = 0; i < listOfWebElementCopy.size(); i++) {
			listOfWebElement = xtexts("//*[contains(text(),'SOAP Connection Profile')]/../td[" + (i + 1) + "]");
			List<WebElement> listDataList = listOfWebElement;
			if(null == soapProfileListInDB || soapProfileListInDB.isEmpty()) {
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 3) {
					assertEquals(listDataList.get(0).getText(), "Good to Migrate");
				}
			} else {
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "Action Required");
				} else if (i == 2) {
					text = xtext(xpathProperties.getProperty("soapConnectionProfile_p"));
					assertEquals(text, "This authentication is not supported in NXG");
					listOfWebElement = xtexts(xpathProperties.getProperty("soapConnectionProfile_list"));
					List<WebElement> soapProfileList = listOfWebElement;
					for (int j = 0; j < soapProfileList.size(); j++) {
						soapProfileListInReport.add(soapProfileList.get(j).getText());
					}
					Collections.sort(soapProfileListInDB);
					Collections.sort(soapProfileListInReport);
					assertEquals(soapProfileListInDB, soapProfileListInReport);
				} else if (i == 3) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the connection profile to the use REST Web Services instead");
				}
			}
		}
		log.info("TC 08 Checking For SOAP Connection Profile Profile ended..............");
	}
	
	/**
	 * Checking whether LDAP Unavailable Enabled
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc09_checkLdapUnavailableEnabled() throws JSchException, SftpException, Exception {
		log.info("TC 09 Checking if ldapUnavailable is enabled started..............");
		loadHighLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String propsFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					propsFilePath = line.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
			}
			if(null != propsFilePath) {
				if(osUserInput.equalsIgnoreCase("linux")) {
					propsFilePath = propsFilePath + "/config.properties";
				} else if(osUserInput.equalsIgnoreCase("windows")) {
					propsFilePath ="/" + propsFilePath.replaceAll("\\\\", "/") + "/config.properties";
				}
				stream = sftpChannel.get(propsFilePath);
				br = new BufferedReader(new InputStreamReader(stream));
				String ldapAvailability = "LDAP Availability Disabled";
				while ((line = br.readLine()) != null) {
					if (line.contains("ldapUnavailable") && !line.contains("#")) {
						String ldabUnavailable = line.split("=")[1];
						if(ldabUnavailable.trim().equals("0")) {
							ldapAvailability = "LDAP Availability Enabled";
							listOfWebElement = xtexts(xpathProperties.getProperty("ldap_authentication"));
							List<WebElement> listOfWebElementCopy = listOfWebElement;
							for (int i = 0; i < listOfWebElementCopy.size(); i++) {
								listOfWebElement = xtexts("//*[contains(text(),'LDAP Authentication')]/../td[" + (i + 1) + "]");
								List<WebElement> listDataList = listOfWebElement;
								if (i == 1) {
									assertEquals(listDataList.get(0).getText(), "Enabled");
								} else if (i == 2) {
									assertEquals(listDataList.get(0).getText(), "This authentication is not supported in NXG");
								} else if (i == 3) {
									assertEquals(listDataList.get(0).getText(), "Reconfigure the authentication to the supported option");
								}
							}
						}
					}
				}
				assertEquals(ldapAvailability, "LDAP Availability Enabled");
			}
			sftpChannel.disconnect();
			session.disconnect();
			log.info("TC 09 Checking if ldapUnavailable is enabled  ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	/**
	 * Checking whether LDAP Unavailable Disabled
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc10_checkLdapUnavailabledisabled() throws JSchException, SftpException, Exception {
		log.info("TC 10 Checking if ldapUnavailable is disabled started..............");
		loadHighLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String propsFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					propsFilePath = line.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
			}
			if(null != propsFilePath) {
				if(osUserInput.equalsIgnoreCase("linux")) {
					propsFilePath = propsFilePath + "/config.properties";
				} else if(osUserInput.equalsIgnoreCase("windows")) {
					propsFilePath ="/" + propsFilePath.replaceAll("\\\\", "/") + "/config.properties";
				}
				stream = sftpChannel.get(propsFilePath);
				br = new BufferedReader(new InputStreamReader(stream));
				String ldapAvailability = "LDAP Availability Enabled";
				while ((line = br.readLine()) != null) {
					if (line.contains("ldapUnavailable") && !line.contains("#")) {
						String ldabUnavailable = line.split("=")[1];
						if(ldabUnavailable.trim().equals("1")) {
							ldapAvailability = "LDAP Availability Disabled";
							listOfWebElement = xtexts(xpathProperties.getProperty("ldap_authentication"));
							List<WebElement> listOfWebElementCopy = listOfWebElement;
							for (int i = 0; i < listOfWebElementCopy.size(); i++) {
								listOfWebElement = xtexts("//*[contains(text(),'LDAP Authentication')]/../td[" + (i + 1) + "]");
								List<WebElement> listDataList = listOfWebElement;
								if (i == 1) {
									assertEquals(listDataList.get(0).getText(), "No Action");
								} else if (i == 2) {
									assertEquals(listDataList.get(0).getText(), "N/A");
								} else if (i == 3) {
									assertEquals(listDataList.get(0).getText(), "Good to Migrate");
								}
							}
						}
					}
				}
				assertEquals(ldapAvailability, "LDAP Availability Disabled");
			}
			sftpChannel.disconnect();
			session.disconnect();
			log.info("TC 10 Checking if ldapUnavailable is disabled ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
}
