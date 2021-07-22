package precheckStories;

import static org.testng.Assert.assertEquals;

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
import com.jcraft.jsch.ChannelSftp;

import precheck.Base;

public class MMP845_WindowsFileSystemCheck extends Base {
	static Logger log = Logger.getLogger(MMP386_LinuxFileSystemCheck.class.getName());
	/*
	 * tc01_VerifyOverallFileSystemSize 
	 * tc02_verifySizeandNumberOfFolders
	 */

	@Test
	public void tc03_verifyIgnoredFiles() throws Exception {
		log.info("TC 03 Verify the ignored files are captured in Report validation Started.............");
		loadLowLevelReportInBrowser();
		establishWindowsSshConnection();
		sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
		sftpChannel.connect();
		InputStream stream = sftpChannel
				.get("/D:/precheck_windows/v7/PrecheckWindows_21_07_2021/precheck/const/fs_constants.py");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String excludedFoldersinFile = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("EXCLUSION_FOLDERS") && !line.contains("#")) {
					excludedFoldersinFile = line.split("=")[1].replaceAll("\'", "").replaceAll("[\\[\\]]", "")
							.replaceAll(" ", "");
				}
			}
			List<String> excluedFoldersListInFile = Arrays.asList(excludedFoldersinFile.split(","));
			List<String> excluedFoldersListInReport = new ArrayList<String>();

			listOfWebElement = xtexts(
					"//*[contains(text(),'Excluded Folder Details')]/following::table[1]/tbody[1]/tr");
			for (int i = 1; i < listOfWebElement.size(); i++) {
				excluedFoldersListInReport.add(listOfWebElement.get(i).getText());
			}
			Collections.sort(excluedFoldersListInReport);
			Collections.sort(excluedFoldersListInFile);
			Assert.assertEquals(excluedFoldersListInReport.size(), excluedFoldersListInFile.size());
			Assert.assertEquals(excluedFoldersListInReport, excluedFoldersListInFile);

			dbConnection.close();
			log.info("TC 03 Verify the ignored files are captured in Report validation Ended.............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}

	}

	@Test
	public void tc04_verifyAttachementRoot() throws Exception {
		log.info("TC 04 Verify attachment root path is captured in report validation started..............");
		loadLowLevelReportInBrowser();
		establishWindowsSshConnection();
		sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
		sftpChannel.connect();
		InputStream stream = sftpChannel
				.get("/D:/precheck_windows/v7/PrecheckWindows_21_07_2021/precheck/Property.toml");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String propsFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					propsFilePath = line.split("=")[1].replaceAll("'", "").replaceAll("\\\\", "/");
				}
			}
			establishWindowsSshConnection();
			sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
			sftpChannel.connect();
			if (null != propsFilePath) {
				propsFilePath = "/" + propsFilePath + "/config.properties";
				stream = sftpChannel.get(propsFilePath);
				br = new BufferedReader(new InputStreamReader(stream));
				String attachmentRootAvailability = "Attachment Root Not Available";
				while ((line = br.readLine()) != null) {
					if (line.contains("attachmentRoot") && !line.contains("#")) {
						String attachmentRoot = line.split("=")[1].replaceAll("\\\\", "/");
						attachmentRootAvailability = (null != attachmentRoot && !attachmentRoot.trim().isEmpty())
								? "Attachment Root Available"
								: "Attachment Root Not Available";
						String newLine = System.getProperty("line.separator");
						String rootFileSystem = null;
						Channel channel = jschSession.openChannel("exec");
						String commandtofindFileSystemSize = "powershell.exe \"Get-ChildItem " + attachmentRoot
								+ " -Recurse | Measure-Object -Property Length -sum\"";
						((ChannelExec) channel).setCommand(commandtofindFileSystemSize);
						InputStream inputStream = channel.getInputStream();
						channel.connect();
						try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
							rootFileSystem = lines.collect(Collectors.joining(newLine));
						}
						channel.disconnect();
						String DirectorySize = "";
						double kb = 0;
						double mb = 0;
						double gb = 0;
						double tb = 0;
						String countOfObject = rootFileSystem.trim().split("\n")[0].trim().split(":")[1].trim();
						String DirectorySizeInByte = rootFileSystem.trim().split("\n")[2].trim().split(":")[1].trim();
						DirectorySize = String.format("", Integer.valueOf(DirectorySizeInByte)) + " Byte";
						if (Double.parseDouble(DirectorySizeInByte) >= 1024) {
							kb = Double.parseDouble(DirectorySizeInByte) / 1024;
							DirectorySize = String.format("%.2f", kb);
						}
						if (kb >= 1024) {
							mb = kb / 1024;
							DirectorySize = String.format("%.2f", mb) + " MB";
						}
						if (mb >= 1024) {
							gb = mb / 1024;
							DirectorySize = String.format("%.2f", gb);
						}
						if (gb >= 1024) {
							tb = gb / 1024;
							DirectorySize = String.format("%.2f", tb);
						}
						int DirectorySizeCharCount = DirectorySize.length();
						String DirectoryAvailability = DirectorySizeCharCount != 0 ? "True" : "False";
						listOfWebElement = xtexts(
								"//*[contains(text(),'Attachment Folder Details')]/following::table[1]/tbody[1]/tr/td");
						List<WebElement> listOfWebElementCopy = listOfWebElement;
						for (int i = 0; i < listOfWebElementCopy.size(); i++) {
							listOfWebElement = xtexts(
									"//*[contains(text(),'Attachment Folder Details')]/following::table[1]/tbody[1]/tr["
											+ (i + 1) + "]/td");
							if (i == 0) {
								assertEquals(listOfWebElement.get(0).getText(), countOfObject);
							}
							if (i == 1) {
								assertEquals(listOfWebElement.get(0).getText(), DirectorySize);
							}
							if (i == 2) {
								assertEquals(listOfWebElement.get(0).getText(), attachmentRoot.replaceAll("/", "\\\\"));
							}

							if (i == 3) {
								assertEquals(listOfWebElement.get(0).getText(), DirectoryAvailability);
							}

						}
					}
				}
				assertEquals(attachmentRootAvailability, "Attachment Root Available");
			}
			log.info("TC 04 Verify attachment root path is captured in report validation ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}

	}
	// tc06_verifyApplicationLog

	@Test
	public void tc07_ReportCheck() throws Exception {
		log.info("TC 07 Report check started....................");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		String[] checkList = { "Filesystem Details", "Folders Details", "Excluded Folder Details",
				"Application Log details" };
		for (int i = 0; i < checkList.length; i++) {
			String web = driver.getPageSource().contains(checkList[i]) ? checkList[i] + " Available"
					: checkList[i] + " Not Available";
			Assert.assertEquals(web, checkList[i] + " Available");
		}
		log.info("TC 07 Report check ended....................");

	}

}
