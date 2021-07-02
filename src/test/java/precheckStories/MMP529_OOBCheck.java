package precheckStories;

import static org.testng.Assert.assertTrue;
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
	
	static Logger log = Logger.getLogger(MMP396_MySQLDatabaseCheck.class.getName());
	
	@Test
	public static void tc01_checkIfOOBDesignListPresent() throws JSchException, SftpException, Exception {
		loadHighLevelReportInBrowser();
		establishSshConnection();
		InputStream stream = sftpChannel.get("/home/ec2-user/QA_testing/migration-tool/src/precheck/const/common_constants.py");
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
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	@Test
	public static void tc02_checkforUnmatchedDataCaptured() throws JSchException, SftpException, Exception {
		loadHighLevelReportInBrowser();
		establishSshConnection();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP529_query.properties");
		InputStream stream = sftpChannel.get("/home/ec2-user/QA_testing/migration-tool/src/precheck/const/common_constants.py");
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
					if(!line.endsWith(")")) {
						designList = designList + br.readLine();
					}
					sourceQuery = query(prop.getProperty("application_settings_unmatched_data")+" "+designList);
					while (sourceQuery.next()) {
						unmatchedDesignListInDB.add(sourceQuery.getObject(1).toString());
					}
					listOfWebElement = xtexts("//*[contains(text(),'Custom Applications')]/../td[3]/ul/li");
					List<WebElement> listOfWebElementCopy = listOfWebElement;
					for (int i = 0; i < listOfWebElementCopy.size(); i++) {
						unmatchedDesignListInReport.add(listOfWebElementCopy.get(i).getText());
					}
					Collections.sort(unmatchedDesignListInDB);
					Collections.sort(unmatchedDesignListInReport);
					assertEquals(unmatchedDesignListInDB, unmatchedDesignListInReport);
				}
			}
			Assert.assertEquals(oobDesignList, "OOB Design Name List Available");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	@Test
	public static void tc03_checkforMatchedDataNotCaptured() throws JSchException, SftpException, Exception {
		loadHighLevelReportInBrowser();
		establishSshConnection();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP529_query.properties");
		InputStream stream = sftpChannel.get("/home/ec2-user/QA_testing/migration-tool/src/precheck/const/common_constants.py");
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
					if(!line.endsWith(")")) {
						designList = designList + br.readLine();
					}
					sourceQuery = query(prop.getProperty("application_settings_matched_data")+" "+designList);
					while (sourceQuery.next()) {
						matchedDesignListInDB.add(sourceQuery.getObject(1).toString());
					}
					listOfWebElement = xtexts("//*[contains(text(),'Custom Applications')]/../td[3]/ul/li");
					List<WebElement> listOfWebElementCopy = listOfWebElement;
					for (int i = 0; i < listOfWebElementCopy.size(); i++) {
						matchedDesignListInReport.add(listOfWebElementCopy.get(i).getText());
					}
					Collections.sort(matchedDesignListInDB);
					Collections.sort(matchedDesignListInReport);
					Assert.assertNotEquals(matchedDesignListInDB, matchedDesignListInReport);
				}
			}
			Assert.assertEquals(oobDesignList, "OOB Design Name List Available");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	@Test
	public static void tc04_checkforCaseSensitive() throws JSchException, SftpException, Exception {
		loadHighLevelReportInBrowser();
		establishSshConnection();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP529_query.properties");
		InputStream stream = sftpChannel.get("/home/ec2-user/QA_testing/migration-tool/src/precheck/const/common_constants.py");
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
					if(!line.endsWith(")")) {
						designList = designList + br.readLine();
					}
					sourceQuery = query(prop.getProperty("application_settings_matched_data")+" "+designList);
					while (sourceQuery.next()) {
						matchedDesignListInDB.add(sourceQuery.getObject(1).toString());
					}
					listOfWebElement = xtexts("//*[contains(text(),'Custom Applications')]/../td[3]/ul/li");
					List<WebElement> listOfWebElementCopy = listOfWebElement;
					for (int i = 0; i < listOfWebElementCopy.size(); i++) {
						matchedDesignListInReport.add(listOfWebElementCopy.get(i).getText());
					}
					Collections.sort(matchedDesignListInDB);
					Collections.sort(matchedDesignListInReport);
					assertNotEquals(matchedDesignListInDB, matchedDesignListInReport);
				}
			}
			Assert.assertEquals(oobDesignList, "OOB Design Name List Available");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
}
