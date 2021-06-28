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
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP387_IntegrationCheck extends Base{
	
	static Logger log = Logger.getLogger(MMP389_EMDRCheck.class.getName());
  
	@Test
	public static void tc01_isSsoEnabled() throws JSchException, SftpException, Exception {
		log.info("TC 01 Checking if SSO enabled started..............");
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
				boolean flag = false;
				while ((line = br.readLine()) != null) {
					if (line.contains("ssoEnabled") && !line.contains("#")) {
						String ssoEnabled = line.split("=")[1];
						if(ssoEnabled.trim().equals("1")) {
							flag = true;
							listOfWebElement = xtexts("//*[contains(text(),'SSO Authentication')]/../td");
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
							break;
						}
					}
				}
				assertTrue(flag == true);
			}
			log.info("TC 01 Checking if SSO enabled ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	@Test
	public static void tc02_isSsoDisabled() throws JSchException, SftpException, Exception {
		log.info("TC 02 Checking if SSO disabled started..............");
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
				boolean flag = false;
				while ((line = br.readLine()) != null) {
					if (line.contains("ssoEnabled") && !line.contains("#")) {
						String ssoEnabled = line.split("=")[1];
						if(ssoEnabled.trim().equals("0")) {
							flag = true;
							listOfWebElement = xtexts("//*[contains(text(),'SSO Authentication')]/../td");
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
							break;
						}
					}
				}
				assertTrue(flag == true);
			}
			log.info("TC 02 Checking if SSO disabled ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	@Test
	public static void tc03_checkSsoType() throws JSchException, SftpException, Exception {
		log.info("TC 03 Checking if SSO type available started..............");
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
				boolean flag = false;
				String ssoType = null;
				while ((line = br.readLine()) != null) {
					if (line.contains("ssoType") && !line.contains("#")) {
						String[] ssoTypeArray = line.split("=");
						if(ssoTypeArray.length > 1 && !ssoTypeArray[1].trim().isEmpty()) {
							ssoType = ssoTypeArray[1].trim().replaceAll("\"", "");
							flag = true;
						}
						listOfWebElement = xtexts("//*[contains(text(),'SSO Authentication')]/../../tr[3]");
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
						break;
					}
				}
			}
			log.info("TC 03 Checking if SSO type available ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	@Test
	public static void tc09_checkLdapUnavailableEnabled() throws JSchException, SftpException, Exception {
		log.info("TC 09 Checking if ldapUnavailable is enabled started..............");
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
				boolean flag = false;
				while ((line = br.readLine()) != null) {
					if (line.contains("ldapUnavailable") && !line.contains("#")) {
						String ldabUnavailable = line.split("=")[1];
						if(ldabUnavailable.trim().equals("0")) {
							flag = true;
							listOfWebElement = xtexts("//*[contains(text(),'LDAP Authentication')]/../td");
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
							break;
						}
					}
				}
				assertTrue(flag == true);
			}
			log.info("TC 09 Checking if ldapUnavailable is enabled  ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	@Test
	public static void tc10_checkLdapUnavailabledisabled() throws JSchException, SftpException, Exception {
		log.info("TC 10 Checking if ldapUnavailable is disabled started..............");
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
				boolean flag = false;
				while ((line = br.readLine()) != null) {
					if (line.contains("ldapUnavailable") && !line.contains("#")) {
						String ldabUnavailable = line.split("=")[1];
						if(ldabUnavailable.trim().equals("1")) {
							flag = true;
							listOfWebElement = xtexts("//*[contains(text(),'LDAP Authentication')]/../td");
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
							break;
						}
					}
				}
				assertTrue(flag == true);
			}
			log.info("TC 10 Checking if ldapUnavailable is disabled ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
}
