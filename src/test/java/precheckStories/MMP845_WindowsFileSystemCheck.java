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
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP845_WindowsFileSystemCheck extends Base {
	static Logger log = Logger.getLogger(MMP845_WindowsFileSystemCheck.class.getName());
	
	/**
	 * Verify the file system size, availability are captured in Low Level Report
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_verifyFileSystemSize() throws JSchException, SftpException, Exception {
		log.info("TC 01 Verify the overall file system size. Started.............");
		loadLowLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String mountPath = null;
			String mountPathFromFile = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("mount_path") && !line.contains("#")) {
					mountPathFromFile = line.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
					break;
				}
			}
			if(null != mountPathFromFile && !mountPathFromFile.isEmpty()) {
				mountPath = mountPathFromFile.replaceAll("\\\\", "/");
				String newLine = System.getProperty("line.separator");
				String rootFileSystem = null;
				Channel channel = session.openChannel("exec");
				String commandtofindFileSystemSize = "powershell.exe \"Get-ChildItem " + mountPath
						+ " -Recurse | Measure-Object -Property Length -sum\"";
				((ChannelExec) channel).setCommand(commandtofindFileSystemSize);
				InputStream inputStream = channel.getInputStream();
				channel.connect();
				try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
					rootFileSystem = lines.collect(Collectors.joining(newLine));
				}
				channel.disconnect();
				String directorySize = "";
				double kb = 0;
				double mb = 0;
				double gb = 0;
				double tb = 0;
				String directorySizeInByte = rootFileSystem.trim().split("\n")[2].trim().split(":")[1].trim();
				directorySize = String.format("", Integer.valueOf(directorySizeInByte)) + "B";
				if (Double.parseDouble(directorySizeInByte) >= 1024) {
					kb = Double.parseDouble(directorySizeInByte) / 1024;
					directorySize = String.format("%.2f", kb) + " KB";
				}
				if (kb >= 1024) {
					mb = kb / 1024;
					directorySize = String.format("%.2f", mb) + " MB";
				}
				if (mb >= 1024) {
					gb = mb / 1024;
					directorySize = String.format("%.2f", gb) + " GB";
				}
				if (gb >= 1024) {
					tb = gb / 1024;
					directorySize = String.format("%.2f", tb) + " TB";
				}
				String isFileAvailable = directorySize.length() != 0 ? "True" : "False";
				listOfWebElement = xtexts(xpathProperties.getProperty("file_system_details"));
				List<WebElement> listOfWebElementCopy = listOfWebElement;
				for (int i = 0; i < listOfWebElementCopy.size(); i++) {
					listOfWebElement = xtexts("//*[contains(text(),'Filesystem Details')]/following::table[1]/tbody[1]/tr[2]/td[" + (i + 1) + "]");
					List<WebElement> listDataList = listOfWebElement;
					if (i == 0) {
						Assert.assertEquals(listDataList.get(0).getText(), mountPathFromFile);
					} else if (i == 1) {
						Assert.assertEquals(listDataList.get(0).getText(), isFileAvailable);
					} else if (i == 2) {
						Assert.assertEquals(listDataList.get(0).getText(), directorySize);
					}
				}
			} else {
				Assert.assertEquals("mount_path not available", "mount_path available");
			}
			sftpChannel.disconnect();
	 		session.disconnect();
			log.info("TC 01 Verify the overall file system size. Ended.............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	/**
	 * Verify the size and number of web resource folder
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_verifySizeandNumberOfFolders() throws JSchException, SftpException, Exception {
		log.info("TC 02 Verify the size and Number of the file system Directories are captured in report. started..............");
		loadLowLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		try {
			InputStream stream = sftpChannel.get(fileProperties.getProperty("fs_constants_windows"));
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String excludedFoldersinFile = null;
			String output;
			String nextLine;
			while ((nextLine = br.readLine()) != null) {
				if (nextLine.contains("EXCLUSION_FOLDERS") && !nextLine.contains("#")) {
					excludedFoldersinFile = nextLine.split("=")[1].replaceAll("\'", "").replaceAll("[\\[\\]]", "")
							.replaceAll(" ", "");
				}
			}
			List<String> excluedFoldersListInFile = Arrays.asList(excludedFoldersinFile.split(","));
			//establishSshConnectionForSourceInstance();
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
			br = new BufferedReader(new InputStreamReader(stream));
			String webResourcePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("web_resource_path") && !line.contains("#")) {
					webResourcePath = line.split("=")[1].replaceAll("\'", "").replaceAll("\"", "").trim();
				}
			}
			String newLine = System.getProperty("line.separator");
			String folders = null;
			Channel channel = session.openChannel("exec");
			String commandtoListFolders = "powershell.exe \"Get-ChildItem " + webResourcePath
					+ " -Directory | Select FullName \"";
			((ChannelExec) channel).setCommand(commandtoListFolders);
			InputStream inputStream = channel.getInputStream();
			channel.connect();
			try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
				folders = lines.collect(Collectors.joining(newLine));
			}
			channel.disconnect();
			List<String> folderList = Arrays.asList(folders.split("\n"));
	 		List<String> folderNameList = new ArrayList<String>();
	 		for(String folder : folderList) {
	 			if(folder.contains(webResourcePath)) {
		 			String folderName =  folder.replace(webResourcePath, "").replaceAll("\\\\", "").trim();
		 			folderNameList.add(folderName);
	 			}
	 		}
	 		folderNameList.removeAll(excluedFoldersListInFile);
	 		List<String> objectCountListInFile = new ArrayList<String>();
	 		List<String> folderSizeListInFile = new ArrayList<String>();
	 		for(String folderName : folderNameList) {
		 		channel = session.openChannel("exec");
		 		String commandtofindFileSystemSize = "powershell.exe \"Get-ChildItem " + webResourcePath + "\\" 
						+ folderName + " -Recurse | Measure-Object -Property Length -sum\"";
		 		((ChannelExec) channel).setCommand(commandtofindFileSystemSize);
		 		inputStream = channel.getInputStream();
		 		channel.connect();
		 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
		 			output = lines.collect(Collectors.joining(newLine));
		 		}
		 		channel.disconnect();
		 		if(null != output && output != "") {
			 		String directorySize = "";
					double kb = 0;
					double mb = 0;
					double gb = 0;
					double tb = 0;
					String countOfObject = output.trim().split("\n")[0].trim().split(":")[1].trim();
					String DirectorySizeInByte = output.trim().split("\n")[2].trim().split(":")[1].trim();
					directorySize = Integer.valueOf(DirectorySizeInByte) + "B";
					if (Double.parseDouble(DirectorySizeInByte) >= 1024) {
						kb = Double.parseDouble(DirectorySizeInByte) / 1024;
						directorySize = String.format("%.2f", kb) + " KB";
					}
					if (kb >= 1024) {
						mb = kb / 1024;
						directorySize = String.format("%.2f", mb) + " MB";
					}
					if (mb >= 1024) {
						gb = mb / 1024;
						directorySize = String.format("%.2f", gb) + " GB";
					}
					if (gb >= 1024) {
						tb = gb / 1024;
						directorySize = String.format("%.2f", tb) + " TB";
					}
					folderSizeListInFile.add(directorySize);
					objectCountListInFile.add(countOfObject);
		 		}
		 		List<String> folderNameListInReport = new ArrayList<String>();
		 		List<String> objectCountListInReport = new ArrayList<String>();
		 		List<String> folderSizeListInReport = new ArrayList<String>();
		 		listOfWebElement = xtexts(xpathProperties.getProperty("folder_details"));
				List<WebElement> listOfFolderName = listOfWebElement;
				for (int i = 0; i < listOfFolderName.size(); i++) {
					folderNameListInReport.add(listOfFolderName.get(i).getText());
				}
				listOfWebElement = xtexts(xpathProperties.getProperty("count_of_objects"));
				List<WebElement> listOfObjectCount = listOfWebElement;
				for (int i = 0; i < listOfObjectCount.size(); i++) {
					objectCountListInReport.add(listOfObjectCount.get(i).getText());
				}
				listOfWebElement = xtexts(xpathProperties.getProperty("folder_size"));
				List<WebElement> listOfFolderSize = listOfWebElement;
				for (int i = 0; i < listOfFolderSize.size(); i++) {
					folderSizeListInReport.add(listOfFolderSize.get(i).getText());
				}
				Assert.assertEquals(folderNameListInReport.size(),folderNameList.size());
				Assert.assertEquals(folderNameListInReport,folderNameList);
				Assert.assertEquals(objectCountListInFile.size(),objectCountListInReport.size());
				Assert.assertEquals(objectCountListInFile,objectCountListInReport);
				Assert.assertEquals(folderSizeListInFile.size(),folderSizeListInReport.size());
				Assert.assertEquals(folderSizeListInFile,folderSizeListInReport);
				sftpChannel.disconnect();
		 		session.disconnect();
			}
			log.info("TC 02 Verify the size and Number of the file system Directories are captured in report. Ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}

	@Test
	public void tc03_verifyIgnoredFiles() throws Exception {
		log.info("TC 03 Verify the ignored files are captured in Report validation Started.............");
		loadLowLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = sftpChannel.get(fileProperties.getProperty("fs_constants_windows"));
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

			listOfWebElement = xtexts(xpathProperties.getProperty("excluded_folder_details"));
			for (int i = 1; i < listOfWebElement.size(); i++) {
				excluedFoldersListInReport.add(listOfWebElement.get(i).getText());
			}
			Collections.sort(excluedFoldersListInReport);
			Collections.sort(excluedFoldersListInFile);
			Assert.assertEquals(excluedFoldersListInReport.size(), excluedFoldersListInFile.size());
			Assert.assertEquals(excluedFoldersListInReport, excluedFoldersListInFile);
			sftpChannel.disconnect();
	 		session.disconnect();
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
		establishSshConnectionForSourceInstance();
		InputStream stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String propsFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					propsFilePath = line.split("=")[1].replaceAll("'", "").replaceAll("\\\\", "/");
				}
			}
			establishSshConnectionForSourceInstance();
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
						Channel channel = session.openChannel("exec");
						String commandtofindFileSystemSize = "powershell.exe \"Get-ChildItem " + attachmentRoot
								+ " -Recurse | Measure-Object -Property Length -sum\"";
						((ChannelExec) channel).setCommand(commandtofindFileSystemSize);
						InputStream inputStream = channel.getInputStream();
						channel.connect();
						try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
							rootFileSystem = lines.collect(Collectors.joining(newLine));
						}
						channel.disconnect();
						if(null != rootFileSystem && rootFileSystem != "") {
							String DirectorySize = "";
							double kb = 0;
							double mb = 0;
							double gb = 0;
							double tb = 0;
							String countOfObject = rootFileSystem.trim().split("\n")[0].trim().split(":")[1].trim();
							String DirectorySizeInByte = rootFileSystem.trim().split("\n")[2].trim().split(":")[1].trim();
							DirectorySize = String.format("", Integer.valueOf(DirectorySizeInByte)) + "B";
							if (Double.parseDouble(DirectorySizeInByte) >= 1024) {
								kb = Double.parseDouble(DirectorySizeInByte) / 1024;
								DirectorySize = String.format("%.2f", kb) + " KB";
							}
							if (kb >= 1024) {
								mb = kb / 1024;
								DirectorySize = String.format("%.2f", mb) + " MB";
							}
							if (mb >= 1024) {
								gb = mb / 1024;
								DirectorySize = String.format("%.2f", gb) + " GB";
							}
							if (gb >= 1024) {
								tb = gb / 1024;
								DirectorySize = String.format("%.2f", tb) + " TB";
							}
							int DirectorySizeCharCount = DirectorySize.length();
							String DirectoryAvailability = DirectorySizeCharCount != 0 ? "True" : "False";
							listOfWebElement = xtexts(xpathProperties.getProperty("attachment_folder_details"));
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
				}
				assertEquals(attachmentRootAvailability, "Attachment Root Available");
			}
			sftpChannel.disconnect();
	 		session.disconnect();
			log.info("TC 04 Verify attachment root path is captured in report validation ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}

	}
	
	/**
	 * Verify Application log size, availability
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc06_verifyApplicationLog() throws JSchException, SftpException, Exception {
		log.info("TC 06 Verify Application Log captured in report. started..............");
		loadLowLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String logFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					logFilePath = line.split("=")[1].replaceAll("'", "").replaceAll("\\\\", "/");
				}
			}
			//establishSshConnectionForSourceInstance();
			String logDirectory = null;
			String logFile = null;
			if(null != logFilePath) {
				logFilePath = "/" +logFilePath + "/log4j2.properties";
				stream = sftpChannel.get(logFilePath);
				br = new BufferedReader(new InputStreamReader(stream));
				String logDirectoryAvailability = "Log Directory Not Available";
				while ((line = br.readLine()) != null) {
					if (line.contains("property.LOG_DIR") && !line.contains("#")) {
						logDirectory = line.split("=")[1].trim();
					}
					if (line.contains("property.FILENAME") && !line.contains("#")) {
						logFile = line.split("=")[1].trim();
					}
				}
				
				if(null != logDirectory && null != logFile) {
					String logPath = logDirectory + "\\" + logFile;
					logDirectoryAvailability = (null != logPath && !logPath.trim().isEmpty()) ?
							"Log Directory Available" : "Log Directory Not Available";
					String newLine = System.getProperty("line.separator");
					String logFileSize = null;
			 		Channel channel = session.openChannel("exec");
					String commandtofindLogFileSize = "powershell.exe \"Get-ChildItem " + logPath
							+ " -Recurse | Measure-Object -Property Length -sum\"";
			 		((ChannelExec) channel).setCommand(commandtofindLogFileSize);
			 		InputStream inputStream = channel.getInputStream();
			 		channel.connect();
			 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
			 			logFileSize = lines.collect(Collectors.joining(newLine));
			 		}
			 		channel.disconnect();
			 		String directorySize = "";
					double kb = 0;
					double mb = 0;
					double gb = 0;
					double tb = 0;
					String directorySizeInByte = logFileSize.trim().split("\n")[2].trim().split(":")[1].trim();
					directorySize = String.format("", Integer.valueOf(directorySizeInByte)) + "B";
					if (Double.parseDouble(directorySizeInByte) >= 1024) {
						kb = Double.parseDouble(directorySizeInByte) / 1024;
						directorySize = String.format("%.2f", kb) + " KB";
					}
					if (kb >= 1024) {
						mb = kb / 1024;
						directorySize = String.format("%.2f", mb) + " MB";
					}
					if (mb >= 1024) {
						gb = mb / 1024;
						directorySize = String.format("%.2f", gb) + " GB";
					}
					if (gb >= 1024) {
						tb = gb / 1024;
						directorySize = String.format("%.2f", tb) + " TB";
					}
					String logDirectoryAvailable = directorySize.length() != 0 ? "True" : "False";
					listOfWebElement = xtexts(xpathProperties.getProperty("application_log_details"));
					List<WebElement> listOfWebElementCopy = listOfWebElement;
					for (int i = 0; i < listOfWebElementCopy.size(); i++) {
						listOfWebElement = xtexts("//*[contains(text(),'Application Log details')]/following::table[1]/tbody[1]/tr[" + (i + 1) + "]/td");
						if (i == 0) {
							Assert.assertEquals(listOfWebElement.get(0).getText(), directorySize);
						}
						if (i == 1) {
							Assert.assertEquals(listOfWebElement.get(0).getText(), logPath);
						}
						if (i == 2) {
							Assert.assertEquals(listOfWebElement.get(0).getText(), logDirectoryAvailable);
						}
					}
				}
				Assert.assertEquals(logDirectoryAvailability, "Log Directory Available");
			}
			sftpChannel.disconnect();
	 		session.disconnect();
			log.info("TC 06 Verify Application Log captured in report. Ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
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

	/**
	 * Verify if wrong file path has given
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc09_verifyIfWrongPathsGiven() throws JSchException, SftpException, Exception {
		log.info("TC 09 Verify if the given wrong path with the result as False is displayed in the Report when the Precheck is run with the non existing path. Started.............");
		loadLowLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String mountPath = null;
			String webResourcePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("mount_path") && !line.contains("#") && null == mountPath) {
					mountPath = line.split("=")[1].replaceAll("\'", "").replaceAll("\"", "").trim();
				}
				if (line.contains("web_resource_path") && !line.contains("#")) {
					webResourcePath = line.split("=")[1].replaceAll("\'", "").replaceAll("\"", "").trim();
				}
			}
			String newLine = System.getProperty("line.separator");
			String output = null;
			Channel channel = session.openChannel("exec");
			String commandtofindPathExistance = "powershell.exe \"Test-Path " + mountPath + " \"";
			((ChannelExec) channel).setCommand(commandtofindPathExistance);
			InputStream inputStream = channel.getInputStream();
			channel.connect();
			try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
				output = lines.collect(Collectors.joining(newLine));
			}
			channel.disconnect();
	 		if(output.equals("True")) {
	 			Assert.assertEquals("Mount Directory exists", "Mount Directory not exists");
	 		} else {
	 			listOfWebElement = xtexts(xpathProperties.getProperty("file_system_details"));
				List<WebElement> listOfWebElementCopy = listOfWebElement;
				for (int i = 0; i < listOfWebElementCopy.size(); i++) {
					listOfWebElement = xtexts("//*[contains(text(),'Filesystem Details')]/following::table[1]/tbody[1]/tr[2]/td[" + (i + 1) + "]");
					List<WebElement> listDataList = listOfWebElement;
					if (i == 0) {
						Assert.assertEquals(listDataList.get(0).getText(), "");
					} else if (i == 1) {
						Assert.assertEquals(listDataList.get(0).getText(), "False");
					} else if (i == 2) {
						Assert.assertEquals(listDataList.get(0).getText(), "None");
					}
				}
	 		}
	 		
	 		channel = session.openChannel("exec");
			commandtofindPathExistance = "powershell.exe \"Test-Path " + webResourcePath + " \"";
			((ChannelExec) channel).setCommand(commandtofindPathExistance);
			inputStream = channel.getInputStream();
			channel.connect();
			try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
				output = lines.collect(Collectors.joining(newLine));
			}
			channel.disconnect();
			
			if(output.equals("True")) {
				Assert.assertEquals("Web Resource Directory exists", "Web Resource Directory not exists");
	 		} else {
	 			text = xtext(xpathProperties.getProperty("web_resources_no_data"));
	 			Assert.assertEquals(text, "Data not found");
	 			text = xtext(xpathProperties.getProperty("folder_details_no_data"));
	 			Assert.assertEquals(text, "Data not found");
	 		} 
			sftpChannel.disconnect();
	 		session.disconnect();
			log.info("TC 09 Verify if the given wrong path with the result as False is displayed in the Report when the Precheck is run with the non existing path. Ended.............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	/**
	 * Verify if No file path has given
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc10_verifyIfNoPathGiven() throws JSchException, SftpException, Exception {
		log.info("TC 10 Verify if the given no path with the result as False is displayed in the Report when the Precheck is run with the non existing path. Started.............");
		loadLowLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String mountPath = null;
			String webResourcePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("mount_path") && !line.contains("#") && null == mountPath) {
					mountPath = line.split("=")[1].replaceAll("\'", "").replaceAll("\"", "").trim();
				}
				if (line.contains("web_resource_path") && !line.contains("#")) {
					webResourcePath = line.split("=")[1].replaceAll("\'", "").replaceAll("\"", "").trim();
				}
			}
	 		if(mountPath.equals("")) {
		 		listOfWebElement = xtexts(xpathProperties.getProperty("file_system_details"));
				List<WebElement> listOfWebElementCopy = listOfWebElement;
				for (int i = 0; i < listOfWebElementCopy.size(); i++) {
					listOfWebElement = xtexts("//*[contains(text(),'Filesystem Details')]/following::table[1]/tbody[1]/tr[2]/td[" + (i + 1) + "]");
					List<WebElement> listDataList = listOfWebElement;
					if (i == 0) {
						Assert.assertEquals(listDataList.get(0).getText(), "");
					} else if (i == 1) {
						Assert.assertEquals(listDataList.get(0).getText(), "False");
					} else if (i == 2) {
						Assert.assertEquals(listDataList.get(0).getText(), "None");
					}
				}
	 		} else {
	 			Assert.assertEquals("Mount Directory exists", "Mount Directory not exists");
	 		}
	 		if(webResourcePath.equals("")) {
	 			text = xtext(xpathProperties.getProperty("web_resources_no_data"));
	 			Assert.assertEquals(text, "Data not found");
	 			text = xtext(xpathProperties.getProperty("folder_details_no_data"));
	 			Assert.assertEquals(text, "Data not found");
	 		} else {
	 			Assert.assertEquals("Web Resource Directory exists", "Web Resource Directory not exists");
	 		}
	 		sftpChannel.disconnect();
	 		session.disconnect();
			log.info("TC 10 Verify if the given no path with the result as False is displayed in the Report when the Precheck is run with the non existing path. Ended.............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
}
