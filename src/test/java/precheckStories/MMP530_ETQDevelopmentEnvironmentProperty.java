package precheckStories;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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

public class MMP530_ETQDevelopmentEnvironmentProperty extends Base{
	
	static Logger log = Logger.getLogger(MMP530_ETQDevelopmentEnvironmentProperty.class.getName());
  
	@Test
	public static void tc01_checkForisEtQDevelopmentEnvironmentAvailable() throws JSchException, SftpException, Exception {
		log.info("TC 01 Checking if the isEtQDevelopmentEnvironment is available started..............");
		//loadHighLevelReportInBrowser();
		establishSshConnection();
		InputStream stream = sftpChannel.get("/home/ec2-user/QA_testing/migration-tool/src/precheck/Property.toml");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String propsFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					propsFilePath = line.split("=")[1].replaceAll("\"", "");
					break;
				}
			}
			establishSshConnection();
			if(null != propsFilePath) {
				propsFilePath = propsFilePath + "/config.properties";
				stream = sftpChannel.get(propsFilePath);
				br = new BufferedReader(new InputStreamReader(stream));
				String isEtQDevelopmentEnvironmentAvailable = "isEtQDevelopmentEnvironment not availabe";
				while ((line = br.readLine()) != null) {
					if (line.contains("isEtQDevelopmentEnvironment") && !line.contains("#")) {
						isEtQDevelopmentEnvironmentAvailable = "isEtQDevelopmentEnvironment availabe";
					}
				}
				assertEquals(isEtQDevelopmentEnvironmentAvailable, "isEtQDevelopmentEnvironment availabe");
			}
			log.info("TC 01 Checking if the isEtQDevelopmentEnvironment is available ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	@Test
	public static void tc02_checkForisEtQDevelopmentEnvironmentEnabled() throws JSchException, SftpException, Exception {
		log.info("TC 02 Checking if isEtQDevelopmentEnvironment Enabled started..............");
		loadHighLevelReportInBrowser();
		establishSshConnection();
		InputStream stream = sftpChannel.get("/home/ec2-user/QA_testing/migration-tool/src/precheck/Property.toml");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String propsFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					propsFilePath = line.split("=")[1].replaceAll("\"", "");
					break;
				}
			}
			establishSshConnection();
			if(null != propsFilePath) {
				propsFilePath = propsFilePath + "/config.properties";
				stream = sftpChannel.get(propsFilePath);
				br = new BufferedReader(new InputStreamReader(stream));
				while ((line = br.readLine()) != null) {
					if (line.contains("isEtQDevelopmentEnvironment") && !line.contains("#")) {
						String isEtQDevelopmentEnvironment = line.split("=")[1];
						if(isEtQDevelopmentEnvironment.trim().equals("1")) {
							listOfWebElement = xtexts("//*[contains(text(),'EtQDevelopment Environment')]/../td");
							List<WebElement> listOfWebElementCopy = listOfWebElement;
							for (int i = 0; i < listOfWebElementCopy.size(); i++) {
								listOfWebElement = xtexts("//*[contains(text(),'EtQDevelopment Environment')]/../td[" + (i + 1) + "]");
								List<WebElement> listDataList = listOfWebElement;
								if (i == 1) {
									assertEquals(listDataList.get(0).getText(), "ETQ Development Environment is enabled");
								} else if (i == 2) {
									assertEquals(listDataList.get(0).getText(), "Property is enabled but not supported on NXG");
								} 
							}
							break;
						}
					}
				}
			}
			log.info("TC 02 Checking if isEtQDevelopmentEnvironment Enabled ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	@Test
	public static void tc03_checkForisEtQDevelopmentEnvironmentDisabled() throws JSchException, SftpException, Exception {
		log.info("TC 03 Checking if isEtQDevelopmentEnvironment Disabled started..............");
		loadHighLevelReportInBrowser();
		establishSshConnection();
		InputStream stream = sftpChannel.get("/home/ec2-user/QA_testing/migration-tool/src/precheck/Property.toml");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String propsFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					propsFilePath = line.split("=")[1].replaceAll("\"", "");
					break;
				}
			}
			establishSshConnection();
			if(null != propsFilePath) {
				propsFilePath = propsFilePath + "/config.properties";
				stream = sftpChannel.get(propsFilePath);
				br = new BufferedReader(new InputStreamReader(stream));
				while ((line = br.readLine()) != null) {
					if (line.contains("isEtQDevelopmentEnvironment") && !line.contains("#")) {
						String isEtQDevelopmentEnvironment = line.split("=")[1];
						if(isEtQDevelopmentEnvironment.trim().equals("0")) {
							listOfWebElement = xtexts("//*[contains(text(),'EtQDevelopment Environment')]/../td");
							List<WebElement> listOfWebElementCopy = listOfWebElement;
							for (int i = 0; i < listOfWebElementCopy.size(); i++) {
								listOfWebElement = xtexts("//*[contains(text(),'EtQDevelopment Environment')]/../td[" + (i + 1) + "]");
								List<WebElement> listDataList = listOfWebElement;
								if (i == 1) {
									assertEquals(listDataList.get(0).getText(), "N/A");
								} else if (i == 2) {
									assertEquals(listDataList.get(0).getText(), "Good to migrate");
								} 
							}
							break;
						}
					}
				}
			}
			log.info("TC 02 Checking if isEtQDevelopmentEnvironment Disabled ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
}
