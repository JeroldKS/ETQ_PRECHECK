package precheckStories;

import static org.testng.Assert.assertEquals;

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

public class MMP530_ETQDevelopmentEnvironmentProperty extends Base{
	
	static Logger log = Logger.getLogger(MMP530_ETQDevelopmentEnvironmentProperty.class.getName());
  
	/**
	 * Check whether isEtQDevelopmentEnvironment variable available in config file or not
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_checkForisEtQDevelopmentEnvironmentAvailable() throws JSchException, SftpException, Exception {
		log.info("TC 01 Checking if the isEtQDevelopmentEnvironment is available started..............");
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
				String isEtQDevelopmentEnvironmentAvailable = "isEtQDevelopmentEnvironment not available";
				while ((line = br.readLine()) != null) {
					if (line.contains("isEtQDevelopmentEnvironment") && !line.contains("#")) {
						isEtQDevelopmentEnvironmentAvailable = "isEtQDevelopmentEnvironment available";
					}
				}
				assertEquals(isEtQDevelopmentEnvironmentAvailable, "isEtQDevelopmentEnvironment available");
			}
			sftpChannel.disconnect();
			log.info("TC 01 Checking if the isEtQDevelopmentEnvironment is available ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	/**
	 * Check whether isEtQDevelopmentEnvironment is enabled
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_checkForisEtQDevelopmentEnvironmentEnabled() throws JSchException, SftpException, Exception {
		log.info("TC 02 Checking if isEtQDevelopmentEnvironment Enabled started..............");
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
				while ((line = br.readLine()) != null) {
					if (line.contains("isEtQDevelopmentEnvironment") && !line.contains("#")) {
						String isEtQDevelopmentEnvironment = line.split("=")[1];
						if(isEtQDevelopmentEnvironment.trim().equals("1")) {
							listOfWebElement = xtexts(xpathProperties.getProperty("etq_development_environment"));
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
						} else {
							log.info("This Test case works only if isEtQDevelopmentEnvironment is 1");
						}
					}
				}
			}
			sftpChannel.disconnect();
			session.disconnect();
			log.info("TC 02 Checking if isEtQDevelopmentEnvironment Enabled ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	/**
	 * Check whether isEtQDevelopmentEnvironment is disabled
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_checkForisEtQDevelopmentEnvironmentDisabled() throws JSchException, SftpException, Exception {
		log.info("TC 03 Checking if isEtQDevelopmentEnvironment Disabled started..............");
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
				while ((line = br.readLine()) != null) {
					if (line.contains("isEtQDevelopmentEnvironment") && !line.contains("#")) {
						String isEtQDevelopmentEnvironment = line.split("=")[1];
						if(isEtQDevelopmentEnvironment.trim().equals("0")) {
							listOfWebElement = xtexts(xpathProperties.getProperty("etq_development_environment"));
							List<WebElement> listOfWebElementCopy = listOfWebElement;
							for (int i = 0; i < listOfWebElementCopy.size(); i++) {
								listOfWebElement = xtexts("//*[contains(text(),'EtQDevelopment Environment')]/../td[" + (i + 1) + "]");
								List<WebElement> listDataList = listOfWebElement;
								if (i == 1) {
									assertEquals(listDataList.get(0).getText(), "N/A");
								} else if (i == 2) {
									assertEquals(listDataList.get(0).getText(), "Good to Migrate");
								} 
							}
						} else {
							log.info("This Test case works only if isEtQDevelopmentEnvironment is 0");
						}
					}
				}
			}
			sftpChannel.disconnect();
			session.disconnect();
			log.info("TC 03 Checking if isEtQDevelopmentEnvironment Disabled ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	/**
	 * Check whether isEtQDevelopmentEnvironment is unavailable
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc04_checkForisEtQDevelopmentEnvironmenUnavailable() throws JSchException, SftpException, Exception {
		log.info("TC 04 Checking if isEtQDevelopmentEnvironment Unavailable started..............");
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
				boolean isEtQDevelopmentEnvironment = false;
				while ((line = br.readLine()) != null) {
					if (line.contains("isEtQDevelopmentEnvironment") && !line.contains("#")) {
						isEtQDevelopmentEnvironment = true;
					}
				}
				if(!isEtQDevelopmentEnvironment) {
					listOfWebElement = xtexts(xpathProperties.getProperty("etq_development_environment"));
					List<WebElement> listOfWebElementCopy = listOfWebElement;
					for (int i = 0; i < listOfWebElementCopy.size(); i++) {
						listOfWebElement = xtexts("//*[contains(text(),'EtQDevelopment Environment')]/../td[" + (i + 1) + "]");
						List<WebElement> listDataList = listOfWebElement;
						if (i == 1) {
							assertEquals(listDataList.get(0).getText(), "N/A");
						} else if (i == 2) {
							assertEquals(listDataList.get(0).getText(), "Review prior to migration, variable \"isEtQDevelopmentEnvironment\" is unavailable");
						} 
					}
				} else {
					log.info("This Test case works only if isEtQDevelopmentEnvironment variable is not available in config.properties");
				}
			}
			sftpChannel.disconnect();
			session.disconnect();
			log.info("TC 04 Checking if isEtQDevelopmentEnvironment Unavailable ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
}
