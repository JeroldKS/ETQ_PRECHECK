package precheckStories;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
	
	@Test
	public static void tc01_isAS2SubmissionEnabled() throws JSchException, SftpException, Exception {
		loadHighLevelReportInBrowser();
		establishSshConnection();
		InputStream stream = sftpChannel.get("/home/ec2-user/props/emdr_user_configuration.properties");
		try {
			boolean flag = false;
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("AS2_SUBMISSION_ENABLED") && !line.contains("#")) {
					flag = true;
				}
			}
			Assert.assertTrue(flag == true);
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}

	@Test
	public static void tc02_checkIfEMDREnabled() throws JSchException, SftpException, Exception {
		loadHighLevelReportInBrowser();
		establishSshConnection();
		InputStream stream = sftpChannel.get("/home/ec2-user/props/emdr_user_configuration.properties");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			boolean flag = false;
			while ((line = br.readLine()) != null) {
				if (line.contains("AS2_SUBMISSION_ENABLED") && !line.contains("#")) {
					flag = true;
					String isAs2SubmissionEnabled = line.split("=")[1];
					if(isAs2SubmissionEnabled.equals("1")) {
						listOfWebElement = xtexts("//*[contains(text(),'eMDR')]/../td");
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
					}
				}
			}
			assertTrue(flag == true);
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	@Test
	public static void tc03_checkIfEMDRdisabled() throws JSchException, SftpException, Exception {
		loadHighLevelReportInBrowser();
		establishSshConnection();
		InputStream stream = sftpChannel.get("/home/ec2-user/props/emdr_user_configuration.properties");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			boolean flag = false;
			while ((line = br.readLine()) != null) {
				if (line.contains("AS2_SUBMISSION_ENABLED") && !line.contains("#")) {
					flag = true;
					String isAs2SubmissionEnabled = line.split("=")[1];
					listOfWebElement = xtexts("//*[contains(text(),'eMDR')]/../td");
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
					}
				}
			}
			assertTrue(flag == true);
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
}
