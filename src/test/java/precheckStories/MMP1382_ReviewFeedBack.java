package precheckStories;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP1382_ReviewFeedBack extends Base{
	
	static Logger log = Logger.getLogger(MMP1382_ReviewFeedBack.class.getName());
	
	/**
	 * Verify that all OOB applications are captured in the config file (common_constants.py)
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test(enabled=false)
	public static void tc01_checkIfOOBDesignListPresent() throws JSchException, SftpException, Exception {
		log.info("Checking OOB Design List available in common constants. Started");
		loadHighLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("common_constants_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("common_constants_windows"));
		}
		try {
			String oobDesignList = "OOB Design Name List Not Available";
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("OOB_APPLICATION_SETTINGS_NAME") && !line.contains("#")
						&& line.contains("=") && line.contains("(")) {
					oobDesignList = "OOB Design Name List Available";
				}
			}
			Assert.assertEquals(oobDesignList, "OOB Design Name List Available");
			sftpChannel.disconnect();
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
		log.info("Checking OOB Design List available in common constants. Ended");
	}
	
	/**
	 * Check whether the unmatched Design List captured in High level Report
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test(enabled=false)
	public static void tc02_checkforUnmatchedDataCaptured() throws JSchException, SftpException, Exception {
		log.info("TC 02 Checking Unmatched OOB List data are captured in Report. Started");
		loadHighLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("common_constants_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("common_constants_windows"));
		}
		establishDatabaseconnection();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP529_query.properties");
		try {
			String oobDesignList = "OOB Design Name List Not Available";
			List<String> unmatchedDesignListInDB = new ArrayList<>();
			List<String> unmatchedDesignListInReport = new ArrayList<>();
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String designList = "";
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("OOB_APPLICATION_SETTINGS_NAME") && !line.contains("#")) {
					oobDesignList = "OOB Design Name List Available";
					designList = line.split("=")[1];
					while (!line.endsWith(")")) {
						line=br.readLine();
						designList=designList+line;
						
					}
				}
			}
			sourceQuery = query(prop.getProperty("application_settings_unmatched_data")+" "+designList);
			while (sourceQuery.next()) {
				unmatchedDesignListInDB.add(sourceQuery.getObject(1).toString());
			}
			if(!unmatchedDesignListInDB.isEmpty()) {
				listOfWebElement = xtexts(xpathProperties.getProperty("custom_application_list"));
				List<WebElement> listOfWebElementCopy = listOfWebElement;
				for (int i = 0; i < listOfWebElementCopy.size(); i++) {
					unmatchedDesignListInReport.add(listOfWebElementCopy.get(i).getText());
				}
				Collections.sort(unmatchedDesignListInDB);
				Collections.sort(unmatchedDesignListInReport);
				assertEquals(unmatchedDesignListInReport, unmatchedDesignListInDB);
			} else {
				log.info("This test case works only if there is No Unmatched OOB List");
			}
			Assert.assertEquals(oobDesignList, "OOB Design Name List Available");
			dbConnection.close();
			sftpChannel.disconnect();
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
		log.info("TC 02 Checking Unmatched OOB List data are captured in Report. Ended");
	}
	
	/**
	 * Verify respective message and notes captured when application_name matches with database and config file
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test(enabled=false)
	public static void tc03_checkforMatchedData() throws JSchException, SftpException, Exception {
		log.info("TC 03 Verify respective message and notes captured when application_name matches with database and config file. Started");
		loadHighLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("common_constants_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("common_constants_windows"));
		}
		establishDatabaseconnection();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP529_query.properties");
		try {
			String oobDesignList = "OOB Design Name List Not Available";
			List<String> unmatchedDesignListInDB = new ArrayList<>();
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			String designList = "";
			while ((line = br.readLine()) != null) {
				if (line.contains("OOB_APPLICATION_SETTINGS_NAME") && !line.contains("#")) {
					oobDesignList = "OOB Design Name List Available";
					designList = line.split("=")[1];
					while (!line.endsWith(")")) {
						line=br.readLine();
						designList=designList+line;
						
					}
				}
			}
			sourceQuery = query(prop.getProperty("application_settings_unmatched_data")+" "+designList);
			while (sourceQuery.next()) {
				unmatchedDesignListInDB.add(sourceQuery.getObject(1).toString());
			}
			if(unmatchedDesignListInDB.isEmpty()) {
				listOfWebElement = xtexts(xpathProperties.getProperty("custom_application_list"));
				List<WebElement> listOfWebElementCopy = listOfWebElement;
					for (int i = 1; i < listOfWebElementCopy.size(); i++) {
						listOfWebElement = xtexts("//*[contains(text(),'Custom Applications')]/../td[" + (i + 1) + "]");
						List<WebElement> listDataList = listOfWebElement;
						if (i == 1) {
							assertEquals(listDataList.get(0).getText(), "N/A");
						} else if (i == 2) {
							assertEquals(listDataList.get(0).getText(), "Good to Migrate");
						}
					}
			} else {
				log.info("This test case works only if there is No Unmatched OOB List");
			}
			Assert.assertEquals(oobDesignList, "OOB Design Name List Available");
			dbConnection.close();
			sftpChannel.disconnect();
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
		log.info("TC 03 Verify respective message and notes captured when application_name matches with database and config file. Ended");
	}
	
	/**
	 * Verify that settings name column for appendix Incompatible SQL Statements and Java Search SQL  is captured with the correct format
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public void tc05_verifySettingNameColumnCapturedInCorrectFormat() throws Exception {
		log.info("TC 05 Verify that settings name column for appendix Incompatible SQL Statements and Java Search SQL  is captured with the correct format. Started.........");
		loadHighLevelReportInBrowser();
		establishDatabaseconnection();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("keywordOwnerFormat"));
		listOfText = listString();
		for (int i = 0; i < listOfText.size(); i++) {
			String[] keywordOwnerSplit = listOfText.get(i).split("[.]");
			sourceQuery = query("select " + keywordOwnerSplit[2] +","+ keywordOwnerSplit[3] + " from " + keywordOwnerSplit[0] + "."
					+ keywordOwnerSplit[1]);
			Assert.assertTrue(sourceQuery.next(), "Setting Column Format mismatch");
		}
		
		listOfWebElement = xtexts(xpathProperties.getProperty("incompatible_sql_setting"));
		listOfText = listString();
		for (int i = 0; i < listOfText.size(); i++) {
			String[] keywordOwnerSplit = listOfText.get(i).split("[.]");
			sourceQuery = query("select " + keywordOwnerSplit[2] +","+ keywordOwnerSplit[3] + " from " + keywordOwnerSplit[0] + "."
					+ keywordOwnerSplit[1]);
			Assert.assertTrue(sourceQuery.next(), "Setting Column Format mismatch");
		}
		log.info("TC 05 Verify that settings name column for appendix Incompatible SQL Statements and Java Search SQL  is captured with the correct format. Ended.........");
	}

	/**
	 * Verify that Incompatible Stored Procedure displays the respective message and resolution in SQL Problems section when stored procedure exists in source
	 * @throws Exception
	 */
	@Test
	public void tc06_verifyInCompatibleSPPresent() throws Exception {
		log.info("TC 06 Verify that Incompatible Stored Procedure displays the respective message and resolution in SQL Problems section when stored procedure exists in source. Started............");
		loadHighLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("sp_to_retired"));
		listOfText = listString();
		boolean isJavaSearchSQLPresent = false;
		if(listOfText.size() == 1) {
			String  sqlStmtMessage = xtext(xpathProperties.getProperty("stored_procedure_message"));
			String  sqlStmtResolution = xtext(xpathProperties.getProperty("stored_procedure_resolution"));
			if(!sqlStmtMessage.equals("N/A") && !sqlStmtResolution.equals("N/A")) {
				isJavaSearchSQLPresent = true;
			}
		} else if(listOfText.size() > 1) {
			isJavaSearchSQLPresent = true;
		}
		if(isJavaSearchSQLPresent) {
			text = xtext(xpathProperties.getProperty("stored_procedure_message"));
			Assert.assertEquals(text, "See associated appendix for failures");
			text = xtext(xpathProperties.getProperty("stored_procedure_resolution"));
			Assert.assertEquals(text, "Dropping the Stored Procedure listed in appendix");
		} else {
			log.info("This case will work only if Stored Procedures to be Retired present");
		}
		log.info("TC 06 Verify that Incompatible Stored Procedure displays the respective message and resolution in SQL Problems section when stored procedure exists in source. Ended............");
	}
	
	/**
	 * Verify that Incompatible Stored Procedure displays the respective message and resolution in SQL Problems section when stored procedure  do not exists in source
	 * @throws Exception
	 */
	@Test
	public void tc07_verifyInCompatibleSPNotPresent() throws Exception {
		log.info("TC 06 Verify that Incompatible Stored Procedure displays the respective message and resolution in SQL Problems section when stored procedure do not exists in source. Started............");
		loadHighLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("sp_to_retired"));
		listOfText = listString();
		boolean isJavaSearchSQLPresent = false;
		if(listOfText.size() == 1) {
			String  sqlStmtMessage = xtext(xpathProperties.getProperty("stored_procedure_message"));
			String  sqlStmtResolution = xtext(xpathProperties.getProperty("stored_procedure_resolution"));
			if(!sqlStmtMessage.equals("N/A") && !sqlStmtResolution.equals("N/A")) {
				isJavaSearchSQLPresent = true;
			}
		} else if(listOfText.size() > 1) {
			isJavaSearchSQLPresent = true;
		}
		if(!isJavaSearchSQLPresent) {
			text = xtext(xpathProperties.getProperty("stored_procedure_message"));
			Assert.assertEquals(text, "N/A");
			text = xtext(xpathProperties.getProperty("stored_procedure_resolution"));
			Assert.assertEquals(text, "Good to Migrate");
		} else {
			log.info("This case will work only if No Stored Procedures to be Retired present");
		}
		log.info("TC 06 Verify that Incompatible Stored Procedure displays the respective message and resolution in SQL Problems section when stored procedure do not exists in source. Ended............");
	}
}
