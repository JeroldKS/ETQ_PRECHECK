package precheckStories;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP385_LinuxInfraCheck extends Base {
	static Logger log = Logger.getLogger(MMP524_MobileAppIsEnabledDisabled.class.getName());

	/**
	 * Verify if the user is able to perform Linux Infra check
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_verifyInfraChecks() throws JSchException, SftpException, Exception {
		log.info("TC 01 Verifying if the user is able to perform Linux Infra Check. Started.....");
		loadLowLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		String newLine = System.getProperty("line.separator");
		String commandOutput = null;
	    Channel channel = session.openChannel("exec");
	    // Get User ID
 		String commandForuserId = "sudo id";
 		((ChannelExec) channel).setCommand(commandForuserId);
 		InputStream inputStream = channel.getInputStream();
 		channel.connect();
 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 		}
 		Assert.assertNotNull(commandOutput);
 		channel.disconnect();

		//Get OS Version
		channel = session.openChannel("exec");
		String commandForOSVersion = "grep '^PRETTY_NAME' /etc/os-release";
		((ChannelExec) channel).setCommand(commandForOSVersion);
		inputStream = channel.getInputStream();
		channel.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 		}
 		Assert.assertNotNull(commandOutput);
		channel.disconnect();
		
		// Get Kernal Version
		channel = session.openChannel("exec");
		String commandForkernalVersion = "uname -vr";
		((ChannelExec) channel).setCommand(commandForkernalVersion);
		inputStream = channel.getInputStream();
		channel.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 		}
 		Assert.assertNotNull(commandOutput);
		channel.disconnect();

		// Check User has Sudo Privileges
		channel = session.openChannel("exec");
		String checkUserHasSudoPrivilege = "sudo -n true 2>/dev/null && echo \"Yes\" || echo \"No\"";
		((ChannelExec) channel).setCommand(checkUserHasSudoPrivilege);
		inputStream = channel.getInputStream();
		channel.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 		}
 		Assert.assertNotNull(commandOutput);
		channel.disconnect();

		//Get memory
		channel = session.openChannel("exec");
		String memory = "awk '{ printf \"%.2f GB\", $2/1024/1024 ; exit}' /proc/meminfo";
		((ChannelExec) channel).setCommand(memory);
		inputStream = channel.getInputStream();
		channel.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 		}
 		Assert.assertNotNull(commandOutput);
		channel.disconnect();

		//Get CPU core count
		channel = session.openChannel("exec");
		String commandForCPUCoreCount = "echo $((`cat /sys/devices/system/cpu/present | sed 's/0-//'` + 1))";
		((ChannelExec) channel).setCommand(commandForCPUCoreCount);
		inputStream = channel.getInputStream();
		channel.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 		}
 		Assert.assertNotNull(commandOutput);
		channel.disconnect();
		log.info("TC 01 Verifying if the user is able to perform Linux Infra Check. Ended.....");
	}
	
	/**
	 * Verify the report captures linux infra check details
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_verifyInfraChecks() throws JSchException, SftpException, Exception {
		log.info("TC 03 Verifying the report caputures Linux Infra Check. Started.....");
		loadLowLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		String newLine = System.getProperty("line.separator");
		String commandOutput = null;
	    Channel channel = session.openChannel("exec");
	    // Check User ID
 		String commandForuserId = "sudo id";
 		((ChannelExec) channel).setCommand(commandForuserId);
 		InputStream inputStream = channel.getInputStream();
 		channel.connect();
 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 			text = xtext("//*[contains(text(),'User ID')]/../td[2]");
 			Assert.assertEquals(commandOutput, text);
 		}
 		channel.disconnect();

		//Check OS Version
		channel = session.openChannel("exec");
		String commandForOSVersion = "grep '^PRETTY_NAME' /etc/os-release";
		((ChannelExec) channel).setCommand(commandForOSVersion);
		inputStream = channel.getInputStream();
		channel.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 			String osType = commandOutput.split("=")[1].replaceAll("\"", "");
 			text = xtext("//*[contains(text(),'Operating System')]/../td[2]");
 			Assert.assertEquals(osType, text);
 		}
		channel.disconnect();
		
		// Check Kernal Version
		channel = session.openChannel("exec");
		String commandForkernalVersion = "uname -vr";
		((ChannelExec) channel).setCommand(commandForkernalVersion);
		inputStream = channel.getInputStream();
		channel.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 			text = xtext("//*[contains(text(),'Kernel Version')]/../td[2]");
 			Assert.assertEquals(commandOutput, text);
 		}
		channel.disconnect();

		// Check User has Sudo Privileges
		channel = session.openChannel("exec");
		String checkUserHasSudoPrivilege = "sudo -n true 2>/dev/null && echo \"Yes\" || echo \"No\"";
		((ChannelExec) channel).setCommand(checkUserHasSudoPrivilege);
		inputStream = channel.getInputStream();
		channel.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 			text = xtext("//*[contains(text(),'Sudo Privileges')]/../td[2]");
 			Assert.assertEquals(commandOutput, text);
 		}
		channel.disconnect();

		//Check memory
		channel = session.openChannel("exec");
		String commandFormemory = "awk '{ printf \"%.2f GB\", $2/1024/1024 ; exit}' /proc/meminfo";
		((ChannelExec) channel).setCommand(commandFormemory);
		inputStream = channel.getInputStream();
		channel.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 			text = xtext("//*[contains(text(),'Memory')]/../td[2]");
 			Assert.assertEquals(commandOutput, text);
 		}
		channel.disconnect();

		//Check CPU core count
		channel = session.openChannel("exec");
		String commandForCPUCoreCount = "echo $((`cat /sys/devices/system/cpu/present | sed 's/0-//'` + 1))";
		((ChannelExec) channel).setCommand(commandForCPUCoreCount);
		inputStream = channel.getInputStream();
		channel.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 			text = xtext("//*[contains(text(),'CPU Core Count')]/../td[2]");
 			Assert.assertEquals(commandOutput, text);
 		}
		channel.disconnect();
		log.info("TC 03 Verifying the report caputures Linux Infra Check. Ended.....");
	}
}
