package precheckStories;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP481_RelianceVersionCheck  extends Base{

	
	static Logger log = Logger.getLogger(MMP481_RelianceVersionCheck.class.getName());
  
	@Test
	public static void tc01_checkWhetherFileIsEditable() throws JSchException, SftpException, Exception {
		log.info("TC 01 Checking whether the Property.toml file is editable or not started..............");
		loadLowLevelReportInBrowser();
		establishSshConnection();
		String newLine = System.getProperty("line.separator");
		String commandOutput = null;
		
	    Channel channel = session.openChannel("shell");
 		String commandForuserId = "sudo test -w /home/ec2-user/QA_testing/migration-tool/src/precheck/Property.toml && echo 'Editable' || echo 'Not Editable'";
 		((ChannelExec) channel).setCommand(commandForuserId);
 		InputStream inputStream = channel.getInputStream();
 		channel.connect();
 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 		}
 		assertEquals(commandOutput, "Editable");
 		channel.disconnect();
 		log.info("TC 01 Checking whether the Property.toml file is editable or not started..............");
	}
	
	@Test
	public static void tc02_checkForValidRelianceVersion() throws JSchException, SftpException, Exception {
		log.info("TC 02 Check for valid reliance version started..............");
		loadLowLevelReportInBrowser();
		establishSshConnection();
		InputStream stream = sftpChannel.get("/home/ec2-user/QA_testing/migration-tool/src/precheck/Property.toml");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String relianceVersioninProperty = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("reliance_version") && !line.contains("#")) {
					relianceVersioninProperty = line.split("=")[1].replaceAll("\"", "").trim();
					break;
				}
			}
			establishSshConnection();
			stream = sftpChannel.get("/home/ec2-user/precheck/const/common_constants.py");
			br = new BufferedReader(new InputStreamReader(stream));
			while ((line = br.readLine()) != null) {
				if (line.contains("RELIANCE_VERSIONS") && !line.contains("#")) {
					String relianceVersionInConstFile = line.split("=")[1].replaceAll("\'", "").replaceAll("[\\[\\]]", "");
					String[] relianceVersionArray = relianceVersionInConstFile.split(",");
					List<String> relianceVersionList = new ArrayList<>(Arrays.asList(relianceVersionArray));
					String validationStatus = relianceVersionList.contains(relianceVersioninProperty) ? "PASSED" : "FAILED";
					assertEquals(validationStatus, "PASSED");
					text = xtext("//*[contains(text(),'Reliance Version Validation')]/../../tr[2]/td");
					assertEquals(validationStatus, text);
					text = xtext("//*[contains(text(),'Reliance Version')]/../../tr[4]/td");
					assertEquals(relianceVersioninProperty, text);
				}
			}
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
		log.info("TC 02 Check for valid reliance version ended..............");
	}
	
	@Test
	public static void tc03_validateEligibleSourceVersion() throws JSchException, SftpException, Exception {
		log.info("TC 03 Check for invalid reliance version started..............");
		loadLowLevelReportInBrowser();
		establishSshConnection();
		InputStream stream = sftpChannel.get("/home/ec2-user/QA_testing/migration-tool/src/precheck/Property.toml");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String relianceVersioninProperty = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("reliance_version") && !line.contains("#")) {
					relianceVersioninProperty = line.split("=")[1].replaceAll("\"", "").trim();
					break;
				}
			}
			establishSshConnection();
			stream = sftpChannel.get("/home/ec2-user/precheck/const/common_constants.py");
			br = new BufferedReader(new InputStreamReader(stream));
			while ((line = br.readLine()) != null) {

				if (line.contains("RELIANCE_VERSIONS") && !line.contains("#")) {
					String relianceVersionInConstFile = line.split("=")[1].replaceAll("\'", "").replaceAll("[\\[\\]]", "");
					String[] relianceVersionArray = relianceVersionInConstFile.split(",");
					List<String> relianceVersionList = new ArrayList<>(Arrays.asList(relianceVersionArray));
					String validationStatus = relianceVersionList.contains(relianceVersioninProperty) ? "PASSED" : "FAILED";
					assertEquals(validationStatus, "FAILED");
					text = xtext("//*[contains(text(),'Reliance Version Validation')]/../../tr[2]/td");
					assertEquals(validationStatus, text);
					text = xtext("//*[contains(text(),'Reliance Version')]/../../tr[4]/td");
					assertEquals(relianceVersioninProperty, text);
				}
			
			}
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
		log.info("TC 03 Check for invalid reliance version ended..............");
	}
}
