package precheckStories;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class MMP529_OOBCheck extends Base{
	
	static Logger log = Logger.getLogger(MMP529_OOBCheck.class.getName());
	
	/**
	 * Checking whether the OOB Design List Present or not
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
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
	@Test
	public static void tc02_checkforUnmatchedDataCaptured() throws JSchException, SftpException, Exception {
		log.info("Checking Unmatched OOB List data are not captured in Report. Started");
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
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("OOB_APPLICATION_SETTINGS_NAME") && !line.contains("#")) {
					oobDesignList = "OOB Design Name List Available";
					String designList = line.split("=")[1];
					while (!line.endsWith(")")) {
						line=br.readLine();
						designList=designList+line;
						
					}
					sourceQuery = query(prop.getProperty("application_settings_unmatched_data")+" "+designList);
					while (sourceQuery.next()) {
						unmatchedDesignListInDB.add(sourceQuery.getObject(1).toString());
					}
					listOfWebElement = xtexts(xpathProperties.getProperty("custom_application_list"));
					List<WebElement> listOfWebElementCopy = listOfWebElement;
					for (int i = 0; i < listOfWebElementCopy.size(); i++) {
						unmatchedDesignListInReport.add(listOfWebElementCopy.get(i).getText());
					}
					Collections.sort(unmatchedDesignListInDB);
					Collections.sort(unmatchedDesignListInReport);
					assertEquals(unmatchedDesignListInReport, unmatchedDesignListInDB);
				}
			}
			Assert.assertEquals(oobDesignList, "OOB Design Name List Available");
			dbConnection.close();
			sftpChannel.disconnect();
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
		log.info("Checking Unmatched OOB List data are not captured in Report. Ended");
	}
	
	/**
	 * Check whether the Matched Design List not captured in High level Report
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_checkforMatchedDataNotCaptured() throws JSchException, SftpException, Exception {
		log.info("Checking Matched OOB List data are not captured in Report. Started");
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
			List<String> matchedDesignListInDB = new ArrayList<>();
			List<String> matchedDesignListInReport = new ArrayList<>();
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("OOB_APPLICATION_SETTINGS_NAME") && !line.contains("#")) {
					oobDesignList = "OOB Design Name List Available";
					String designList = line.split("=")[1];
					while (!line.endsWith(")")) {
						line=br.readLine();
						designList=designList+line;
						
					}
					sourceQuery = query(prop.getProperty("application_settings_matched_data")+" "+designList);
					while (sourceQuery.next()) {
						matchedDesignListInDB.add(sourceQuery.getObject(1).toString());
					}
					listOfWebElement = xtexts(xpathProperties.getProperty("custom_application_list"));
					List<WebElement> listOfWebElementCopy = listOfWebElement;
					for (int i = 0; i < listOfWebElementCopy.size(); i++) {
						matchedDesignListInReport.add(listOfWebElementCopy.get(i).getText());
					}
					Collections.sort(matchedDesignListInDB);
					Collections.sort(matchedDesignListInReport);
					Assert.assertNotEquals(matchedDesignListInReport, matchedDesignListInDB);
				}
			}
			Assert.assertEquals(oobDesignList, "OOB Design Name List Available");
			dbConnection.close();
			sftpChannel.disconnect();
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
		log.info("Checking Matched OOB List data are not captured in Report. Ended");
	}
	
	/**
	 * Check OOB List captured in Report are case sensitive
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc04_checkforCaseSensitive() throws JSchException, SftpException, Exception {
		log.info("Checking OOB Design list captured in Report are case sensitive. Started");
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
			List<String> matchedDesignListInDB = new ArrayList<>();
			List<String> matchedDesignListInReport = new ArrayList<>();
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("OOB_APPLICATION_SETTINGS_NAME") && !line.contains("#")) {
					oobDesignList = "OOB Design Name List Available";
					String designList = line.split("=")[1];
					while (!line.endsWith(")")) {
						line=br.readLine();
						designList=designList+line;
						
					}
					sourceQuery = query(prop.getProperty("application_settings_matched_data")+" "+designList);
					while (sourceQuery.next()) {
						matchedDesignListInDB.add(sourceQuery.getObject(1).toString());
					}
					listOfWebElement = xtexts(xpathProperties.getProperty("custom_application_list"));
					List<WebElement> listOfWebElementCopy = listOfWebElement;
					for (int i = 0; i < listOfWebElementCopy.size(); i++) {
						matchedDesignListInReport.add(listOfWebElementCopy.get(i).getText());
					}
					Collections.sort(matchedDesignListInDB);
					Collections.sort(matchedDesignListInReport);
					assertNotEquals(matchedDesignListInReport, matchedDesignListInDB);
				}
			}
			Assert.assertEquals(oobDesignList, "OOB Design Name List Available");
			dbConnection.close();
			sftpChannel.disconnect();
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
		log.info("Checking OOB Design list captured in Report are case sensitive. Ended");
	}
}
