package precheckStories;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.jcraft.jsch.ChannelExec;

import precheck.Base;

public class MMP512_WindowsInfrastructureChecks extends Base {
	static Logger log = Logger.getLogger(MMP512_WindowsInfrastructureChecks.class.getName());

	/**
	 * To verify Windows infrastructure checks matches report
	 * 
	 * @throws Exception
	 */
	@Test
	public static void tc01_IsWindowsInfrastructureChecksMatchesReport() throws Exception {
		log.info("TC_01 Windows infrastructure checks matches report validation started..............");
		establishSshConnectionForSourceInstance();
		loadLowLevelReportInBrowser();
		String newLine = System.getProperty("line.separator");
		String commandOutput = null;
		ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
		channelExec.setCommand("powershell.exe  (Get-WmiObject -class Win32_OperatingSystem).Caption");
		channelExec.setErrStream(System.err);
		InputStream in = channelExec.getInputStream();
		channelExec.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(in)).lines()) {
			commandOutput = lines.collect(Collectors.joining(newLine));
		}
		text = xtext("//*[contains(text(),'Operating System')]/following::td[1]");
		String windows="Windows";
		Assert.assertTrue(commandOutput.toLowerCase().contains(windows.toLowerCase()),commandOutput+" != "+text);
		Assert.assertTrue(text.toLowerCase().contains(windows.toLowerCase()),commandOutput+" != "+text);

		channelExec = (ChannelExec) session.openChannel("exec");
		channelExec.setCommand(
				"powershell.exe  \"(Get-CimInstance Win32_PhysicalMemory | Measure-Object -Property capacity -Sum).sum /1gb | % {write-output $_'GB'}\"");
		channelExec.setErrStream(System.err);
		in = channelExec.getInputStream();
		channelExec.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(in)).lines()) {
			commandOutput = lines.collect(Collectors.joining(newLine));
		}
		text = xtext("//*[contains(text(),'System Memory')]/following::td[1]");
		Assert.assertEquals(commandOutput.trim(), text.trim(),commandOutput+" != "+text);
		
		channelExec = (ChannelExec) session.openChannel("exec");
		channelExec.setCommand(
				"powershell.exe  \"Get-WmiObject -class Win32_processor | select NumberOfCores | ConvertTo-Json\"");
		channelExec.setErrStream(System.err);
		in = channelExec.getInputStream();
		channelExec.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(in)).lines()) {
			commandOutput = lines.collect(Collectors.joining(newLine));
		}
		text = xtext("//*[contains(text(),'CPU Core Count')]/following::td[1]");
		Assert.assertTrue(commandOutput.contains(text),commandOutput+" != "+text );

		channelExec = (ChannelExec) session.openChannel("exec");
		channelExec.setCommand("powershell.exe  [System.Security.Principal.WindowsIdentity]::GetCurrent().Name");
		channelExec.setErrStream(System.err);
		in = channelExec.getInputStream();
		channelExec.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(in)).lines()) {
			commandOutput = lines.collect(Collectors.joining(newLine));
		}
		text = xtext("//*[contains(text(),'User ID')]/following::td[1]");
		Assert.assertTrue(commandOutput.toLowerCase().contains(text.toLowerCase()),commandOutput+" != "+text);

		channelExec = (ChannelExec) session.openChannel("exec");
		channelExec.setCommand(
				"powershell.exe  \"([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)\"");
		channelExec.setErrStream(System.err);
		in = channelExec.getInputStream();
		channelExec.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(in)).lines()) {
			commandOutput = lines.collect(Collectors.joining(newLine));
		}
		text = xtext("//*[contains(text(),'Admin Privileges')]/following::td[1]");
		if (commandOutput.equalsIgnoreCase("True")) {
			Assert.assertEquals(text,"Yes",commandOutput+" != "+text);
		}else if (commandOutput.equalsIgnoreCase("False")) {
			Assert.assertEquals(text,"No",commandOutput+" != "+text);
		}
		log.info("TC_01 Windows infrastructure checks matches report validation ended..............");
	}

	/**
	 * To verify Report capture infrastructure check
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc02_IsReportCaptureInfraCheck() throws Exception {
		log.info("TC_02 Report capture infra check validation started..............");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("infraCheckList"));
		listOfText = listString();
		System.out.println(listOfText);
		String[] checkList = { "Operating System", "User ID", "Admin Privileges", "System Memory", "CPU Core Count" };
		for (int i = 0; i < checkList.length; i++) {
			 Assert.assertTrue(listOfText.contains(checkList[i]), checkList[i] + " : Expected is not capture");
		}
		log.info("TC_02 Report capture infra check validation ended..............");
	}

	/**
	 * To verify Report capture if user privileges false
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc03_IsReportCaptureIfUserPrivilegesFalse() throws Exception {
		log.info("TC_03 Report capture if user privileges false validation started..............");
		establishSshConnectionForSourceInstance();
		String newLine = System.getProperty("line.separator");
		String commandOutput = null;
		ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
		channelExec.setCommand("powershell.exe  (Get-WmiObject -class Win32_OperatingSystem).Caption");
		channelExec.setErrStream(System.err);
		InputStream in = channelExec.getInputStream();
		channelExec.connect();

		channelExec = (ChannelExec) session.openChannel("exec");
		channelExec.setCommand(
				"powershell.exe  \"([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)\"");
		channelExec.setErrStream(System.err);
		in = channelExec.getInputStream();
		channelExec.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(in)).lines()) {
			commandOutput = lines.collect(Collectors.joining(newLine));
		}
		if (commandOutput.toString() == "False") {
			loadLowLevelReportInBrowser();
			xpathProperties = loadXpathFile();
			listOfWebElement = xtexts(xpathProperties.getProperty("infraCheckList"));
			listOfText = listString();
			int validateCount = 0;
			for (int i = 0; i < listOfText.size(); i++) {
				if (listOfText.get(i).equalsIgnoreCase("User has Sudo Privileges")) {
					validateCount++;
					text = xtext("//*[contains(text(),'Source Infrastructure Details')]/following::tbody[1]/tr["
							+ (i + 2) + "]/td[3]");
					Assert.assertEquals(text, "Failed");
				}
			}
			if (validateCount == 0) {
				Assert.assertTrue(false, "User Privileges not validated");
			}
		} else {
			log.info("For negative scenario, In this case need to fail the admin privileges");
		}
		log.info("TC_03 Report capture if user privileges false validation ended..............");
	}
}
