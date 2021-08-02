package precheckStories;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP389_EMDRCheck extends Base {
	static Logger log = Logger.getLogger(MMP389_EMDRCheck.class.getName());
	
	/**
	 * Checking whether AS2_SUBMISSION_ENABLED key is available in emdr config file or not
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_isAS2SubmissionEnabled() throws JSchException, SftpException, Exception {
		log.info("TC_01 Verify if the user is able navigate and able to access the  File emdr_user_configuration.properties along with the Property AS2_SUBMISSION_ENABLED.started....................");
		loadHighLevelReportInBrowser();
		InputStream stream = null;
		establishSshConnectionForSourceInstance();
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("emdr_file_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("emdr_file_windows"));
		}
		try {
			String isAs2SubmissionEnabledAvailable = "AS2_SUBMISSION_ENABLED Not Available";
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("AS2_SUBMISSION_ENABLED") && !line.contains("#")) {
					isAs2SubmissionEnabledAvailable = "AS2_SUBMISSION_ENABLED Available";
				}
			}
			Assert.assertEquals(isAs2SubmissionEnabledAvailable, "AS2_SUBMISSION_ENABLED Available");
			sftpChannel.disconnect();
			session.disconnect();
			log.info("TC_01 Verify if the user is able navigate and able to access the  File emdr_user_configuration.properties along with the Property AS2_SUBMISSION_ENABLED.Ended....................");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}

	/**
	 * Checking if emdr is enabled or not  
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_checkIfEMDREnabled() throws JSchException, SftpException, Exception {
		log.info("TC_02 Verify if the Message \"eMDR is not supported yet for Migration\" and Resolution \"Wait untill this feature is available\" are captured in the report when eMDR is enabled. Started.......");
		loadHighLevelReportInBrowser();
		InputStream stream = null;
		establishSshConnectionForSourceInstance();
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("emdr_file_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("emdr_file_windows"));
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			String isAs2SubmissionEnabledAvailable = "AS2_SUBMISSION_ENABLED Not Available";
			while ((line = br.readLine()) != null) {
				if (line.contains("AS2_SUBMISSION_ENABLED") && !line.contains("#")) {
					isAs2SubmissionEnabledAvailable = "AS2_SUBMISSION_ENABLED Available";
					String isAs2SubmissionEnabled = line.split("=")[1];
					if(isAs2SubmissionEnabled.equals("1")) {
						listOfWebElement = xtexts(xpathProperties.getProperty("eMDR"));
						List<WebElement> listOfWebElementCopy = listOfWebElement;
						for (int i = 0; i < listOfWebElementCopy.size(); i++) {
							listOfWebElement = xtexts("//*[contains(text(),'eMDR')]/../td[" + (i + 1) + "]");
							List<WebElement> listDataList = listOfWebElement;
							if (i == 0) {
								assertEquals(listDataList.get(0).getText(), "eMDR");
							} else if (i == 1) {
								assertEquals(listDataList.get(0).getText(), "Action needed");
							} else if (i == 2) {
								assertEquals(listDataList.get(0).getText(), "eMDR is not supported yet for Migration");
							} else if (i == 3) {
								assertEquals(listDataList.get(0).getText(), "Wait until this feature is available");
							}
						}
					} else {
						Assert.assertEquals("AS2_SUBMISSION_ENABLED = 0", "AS2_SUBMISSION_ENABLED = 1");
					}
				}
			}
			Assert.assertEquals(isAs2SubmissionEnabledAvailable, "AS2_SUBMISSION_ENABLED Available");
			sftpChannel.disconnect();
			session.disconnect();
			log.info("TC_02 Verify if the Message \"eMDR is not supported yet for Migration\" and Resolution \"Wait untill this feature is available\" are captured in the report when eMDR is enabled. Ended.......");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	/**
	 * Checking if emdr is disabled or not  
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_checkIfEMDRdisabled() throws JSchException, SftpException, Exception {
		log.info("TC_03 Verify if the Resolution \"Good to Migrate\" is captured in th ereport when eMDR is disabled. Started.....");
		loadHighLevelReportInBrowser();
		InputStream stream = null;
		establishSshConnectionForSourceInstance();
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("emdr_file_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("emdr_file_windows"));
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			String isAs2SubmissionEnabledAvailable = "AS2_SUBMISSION_ENABLED Not Available";
			while ((line = br.readLine()) != null) {
				if (line.contains("AS2_SUBMISSION_ENABLED") && !line.contains("#")) {
					isAs2SubmissionEnabledAvailable = "AS2_SUBMISSION_ENABLED Available";
					String isAs2SubmissionEnabled = line.split("=")[1];
					listOfWebElement = xtexts(xpathProperties.getProperty("eMDR"));
					List<WebElement> listOfWebElementCopy = listOfWebElement;
					if(isAs2SubmissionEnabled.equals("0")) {
						for (int i = 0; i < listOfWebElementCopy.size(); i++) {
							listOfWebElement = xtexts("//*[contains(text(),'eMDR')]/../td[" + (i + 1) + "]");
							List<WebElement> listDataList = listOfWebElement;
							if (i == 0) {
								assertEquals(listDataList.get(0).getText(), "eMDR");
							} else if (i == 1) {
								assertEquals(listDataList.get(0).getText(), "N/A");
							} else if (i == 2) {
								assertEquals(listDataList.get(0).getText(), "N/A");
							} else if (i == 3) {
								assertEquals(listDataList.get(0).getText(), "Good to Migrate");
							}
						}
					} else {
						Assert.assertEquals("AS2_SUBMISSION_ENABLED = 1", "AS2_SUBMISSION_ENABLED = 0");
					}
				}
			}
			Assert.assertEquals(isAs2SubmissionEnabledAvailable, "AS2_SUBMISSION_ENABLED Available");
			sftpChannel.disconnect();
			session.disconnect();
			log.info("TC_03 Verify if the Resolution \"Good to Migrate\" is captured in th ereport when eMDR is disabled. Ended.....");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
}
