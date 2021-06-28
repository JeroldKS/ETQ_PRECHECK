package precheckStories;

import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP389_EMDRCheck extends Base {
	@BeforeTest
	private void open() throws Exception {
		loadHighLevelReportInBrowser();
	}

	@Test
	public static void tc01_isAS2SubmissionEnabled() throws JSchException, SftpException, Exception {
		
		establishSshConnection();
		// run stuff
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
			assertTrue(flag == true);
		} catch (IOException io) {
			System.out.println("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}

	@Test
	public static void tc02_checkIfEMDREnabled() throws JSchException, SftpException, Exception {
		
		establishSshConnection();
		InputStream stream = sftpChannel.get("/home/ec2-user/props/emdr_user_configuration.properties");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			boolean flag = false;
			while ((line = br.readLine()) != null) {
				if (line.contains("AS2_SUBMISSION_ENABLED") && !line.contains("#")) {
					flag = true;
					System.out.println(line);
					String isAs2SubmissionEnabled = line.split("=")[1];
					System.out.println("AS2_SUBMISSION_ENABLED:::" + isAs2SubmissionEnabled);
					listOfWebElement = xtexts("//*[contains(text(),'eMDR')]/../td");
					List<WebElement> dup_texts = listOfWebElement;
					for (int i = 0; i < dup_texts.size(); i++) {
						listOfWebElement = xtexts("//*[contains(text(),'eMDR')]/../td[" + (i + 1) + "]");
						List<WebElement> listDataList = listOfWebElement;
						if (i == 0) {
							AssertJUnit.assertEquals(listDataList.get(0).getText(), "eMDR");
						} else if (i == 1 && isAs2SubmissionEnabled.equals("0")) {
							AssertJUnit.assertEquals(listDataList.get(0).getText(), "N/A");
						} else if (i == 1 && isAs2SubmissionEnabled.equals("1")) {
							AssertJUnit.assertEquals(listDataList.get(0).getText(), "Action needed");
						} else if (i == 2 && isAs2SubmissionEnabled.equals("0")) {
							AssertJUnit.assertEquals(listDataList.get(0).getText(), "N/A");
						} else if (i == 2 && isAs2SubmissionEnabled.equals("1")) {
							AssertJUnit.assertEquals(listDataList.get(0).getText(), "eMDR is not supported yet for Migration");
						} else if (i == 3 && isAs2SubmissionEnabled.equals("0")) {
							AssertJUnit.assertEquals(listDataList.get(0).getText(), "Good to Migrate");
						} else if (i == 3 && isAs2SubmissionEnabled.equals("1")) {
							AssertJUnit.assertEquals(listDataList.get(0).getText(), "Wait until this feature is available");
						}
					}
				}
				assertTrue(flag == true);
			}
		} catch (IOException io) {
			System.out.println("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
}
