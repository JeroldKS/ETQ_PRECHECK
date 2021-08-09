package precheckStories;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP481_RelianceVersionCheck  extends Base{

	
	static Logger log = Logger.getLogger(MMP481_RelianceVersionCheck.class.getName());
  
	/**
	 * Checking whether the Property.toml file is editable or not
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_checkWhetherFileIsEditable() throws JSchException, SftpException, Exception {
		log.info("TC 01 Checking whether the Property.toml file is editable or not started..............");
		loadLowLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		String newLine = System.getProperty("line.separator");
		String commandOutput = null;
		String commandtofindPathExistance = null;
		
		if(osUserInput.equalsIgnoreCase("linux")) {
			commandtofindPathExistance = "sudo test -w "+fileProperties.getProperty("propertyToml_linux")+" && echo 'True' || echo 'False'";
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			String windowsFilePath = fileProperties.getProperty("propertyToml_windows").substring(1).replaceAll("/", "\\\\");
			commandtofindPathExistance = "powershell.exe \"Test-Path " + windowsFilePath + " \"";
		}
		
	    Channel channel = session.openChannel("exec");
 		((ChannelExec) channel).setCommand(commandtofindPathExistance);
 		InputStream inputStream = channel.getInputStream();
 		channel.connect();
 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 		}
 		assertEquals(commandOutput, "True");
 		channel.disconnect();
 		sftpChannel.disconnect();
 		log.info("TC 01 Checking whether the Property.toml file is editable or not started..............");
	}
	
	/**
	 * Check for valid reliance version
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_checkForValidRelianceVersion() throws JSchException, SftpException, Exception {
		log.info("TC 02 Check for valid reliance version started..............");
		loadLowLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String relianceVersioninProperty = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("reliance_version") && !line.contains("#")) {
					relianceVersioninProperty = line.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
			}
			if(osUserInput.equalsIgnoreCase("linux")) {
				stream = sftpChannel.get(fileProperties.getProperty("common_constants_linux"));
			} else if(osUserInput.equalsIgnoreCase("windows")) {
				stream = sftpChannel.get(fileProperties.getProperty("common_constants_windows"));
			}
			br = new BufferedReader(new InputStreamReader(stream));
			while ((line = br.readLine()) != null) {
				if (line.contains("RELIANCE_VERSIONS") && !line.contains("#")) {
					String relianceVersionInConstFile = line.split("=")[1].replaceAll("\'", "").replaceAll("[\\[\\]]", "");
					String[] relianceVersionArray = relianceVersionInConstFile.split(",");
					List<String> relianceVersionList = new ArrayList<>(Arrays.asList(relianceVersionArray));
					String validationStatus = relianceVersionList.contains(relianceVersioninProperty) ? "PASS" : "FAIL";
					if(validationStatus.equals("PASS")) {
						text = xtext(xpathProperties.getProperty("reliance_version_validation"));
						assertEquals(validationStatus, text);
						text = xtext(xpathProperties.getProperty("reliance_version"));
						assertEquals(relianceVersioninProperty, text);
					} else {
						log.info("This Test case works only if Reliance validation pass");
					}
				}
			}
			sftpChannel.disconnect();
			session.disconnect();
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
		log.info("TC 02 Check for valid reliance version ended..............");
	}
	
	/**
	 * Check for eligible source version
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_validateEligibleSourceVersion() throws JSchException, SftpException, Exception {
		log.info("TC 03 Check for invalid reliance version started..............");
		loadLowLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String relianceVersioninProperty = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("reliance_version") && !line.contains("#")) {
					relianceVersioninProperty = line.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
			}
			if(osUserInput.equalsIgnoreCase("linux")) {
				stream = sftpChannel.get(fileProperties.getProperty("common_constants_linux"));
			} else if(osUserInput.equalsIgnoreCase("windows")) {
				stream = sftpChannel.get(fileProperties.getProperty("common_constants_windows"));
			}
			br = new BufferedReader(new InputStreamReader(stream));
			while ((line = br.readLine()) != null) {
				if (line.contains("RELIANCE_VERSIONS") && !line.contains("#")) {
					String relianceVersionInConstFile = line.split("=")[1].replaceAll("\'", "").replaceAll("[\\[\\]]", "");
					String[] relianceVersionArray = relianceVersionInConstFile.split(",");
					List<String> relianceVersionList = new ArrayList<>(Arrays.asList(relianceVersionArray));
					String validationStatus = relianceVersionList.contains(relianceVersioninProperty) ? "PASS" : "FAIL";
					//assertEquals(validationStatus, "FAILED");
					if(validationStatus.equals("FAIL")) {
						text = xtext(xpathProperties.getProperty("reliance_version_validation"));
						assertEquals(validationStatus, text);
						text = xtext(xpathProperties.getProperty("reliance_version"));
						assertEquals(relianceVersioninProperty, text);
					} else {
						log.info("This Test case works only if Reliance validation pass");
					}
				}
			}
			sftpChannel.disconnect();
			session.disconnect();
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
		log.info("TC 03 Check for invalid reliance version ended..............");
	}
}
