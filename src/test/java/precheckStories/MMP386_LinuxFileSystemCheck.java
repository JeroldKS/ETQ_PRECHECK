package precheckStories;

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

public class MMP386_LinuxFileSystemCheck extends Base{
	
	static Logger log = Logger.getLogger(MMP386_LinuxFileSystemCheck.class.getName());
  
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
		InputStream stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String mountPath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("mount_path") && !line.contains("#")) {
					mountPath = line.split("=")[1].replaceAll("\"", "");
					break;
				}
			}
			if(null != mountPath  && !mountPath.isEmpty()) {
				String newLine = System.getProperty("line.separator");
				String commandOutput = null;
			    Channel channel = session.openChannel("exec");
		 		String commandtofindFileSystemSize = "sudo df -kh "+mountPath;
		 		((ChannelExec) channel).setCommand(commandtofindFileSystemSize);
		 		InputStream inputStream = channel.getInputStream();
		 		channel.connect();
		 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
		 			commandOutput = lines.collect(Collectors.joining(newLine));
		 		}
		 		channel.disconnect();
		 		if(null != commandOutput) {
					String isFileAvailable = commandOutput.contains("Filesystem") == true ? "True" : "False";
					String output = commandOutput.split("\n")[1].replaceAll("\\s{2,}", " ").trim();
					String availableSize = output.split(" ")[3];
					if(availableSize.contains("T")) {
						availableSize = output.split(" ")[3].replace("T", " TB");
					} else if(availableSize.contains("G")) {
						availableSize = output.split(" ")[3].replace("G", " GB");
					} else if(availableSize.contains("M")) {
						availableSize = output.split(" ")[3].replace("M", " MB");
					} else if(availableSize.contains("K")) {
						availableSize = output.split(" ")[3].replace("K", " KB");
					}
					listOfWebElement = xtexts(xpathProperties.getProperty("file_system_details"));
					List<WebElement> listOfWebElementCopy = listOfWebElement;
					for (int i = 0; i < listOfWebElementCopy.size(); i++) {
						listOfWebElement = xtexts("//*[contains(text(),'Filesystem Details')]/following::table[1]/tbody[1]/tr[2]/td[" + (i + 1) + "]");
						List<WebElement> listDataList = listOfWebElement;
						if (i == 0) {
							Assert.assertEquals(listDataList.get(0).getText(), mountPath);
						} else if (i == 1) {
							Assert.assertEquals(listDataList.get(0).getText(), isFileAvailable);
						} else if (i == 2) {
							Assert.assertEquals(listDataList.get(0).getText(), availableSize);
						}
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
			InputStream stream = sftpChannel.get(fileProperties.getProperty("fs_constants_linux"));
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String excludedFoldersinFile = null;
			String output;
			String nextLine;
			while ((nextLine = br.readLine()) != null) {
				if (nextLine.contains("EXCLUSION_FOLDERS") && !nextLine.contains("#")) {
					excludedFoldersinFile = nextLine.split("=")[1].replaceAll("\"", "").replaceAll("[\\[\\]]", "").replaceAll(" ", "");
				}
			}
			List<String> excluedFoldersListInFile = Arrays.asList(excludedFoldersinFile.split(","));
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
			br = new BufferedReader(new InputStreamReader(stream));
			String webResourcePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("web_resource_path") && !line.contains("#")) {
					webResourcePath = line.split("=")[1].replaceAll("\"", "");
				}
			}
			String newLine = System.getProperty("line.separator");
			String folders = null;
		    Channel channel = session.openChannel("exec");
	 		String commandtoListFileSystem = "ls -d "+webResourcePath+"*/";
	 		((ChannelExec) channel).setCommand(commandtoListFileSystem);
	 		InputStream inputStream = channel.getInputStream();
	 		channel.connect();
	 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
	 			folders = lines.collect(Collectors.joining(newLine));
	 		}
	 		channel.disconnect();
	 		List<String> folderList = Arrays.asList(folders.split("\n"));
	 		List<String> folderNameList = new ArrayList<String>();
	 		for(String folder : folderList) {
	 			String folderName =  folder.replace(webResourcePath, "").replaceAll("/", "");
	 			folderNameList.add(folderName);
	 		}
	 		folderNameList.removeAll(excluedFoldersListInFile);
	 		
	 		List<String> objectCountListInFile = new ArrayList<String>();
	 		for(String folderName : folderNameList) {
		 		channel = session.openChannel("exec");
		 		String commandtofindFileSystemSize = "sudo find "+webResourcePath+folderName+"/ -type f | wc -l";
		 		((ChannelExec) channel).setCommand(commandtofindFileSystemSize);
		 		inputStream = channel.getInputStream();
		 		channel.connect();
		 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
		 			output = lines.collect(Collectors.joining(newLine));
			 		objectCountListInFile.add(output);
		 		}
		 		channel.disconnect();
	 		}
	 		List<String> folderSizeListInFile = new ArrayList<String>();
	 		for(String folderName : folderNameList) {
		 		channel = session.openChannel("exec");
		 		String commandtofindFileSystemSize = "sudo du "+webResourcePath+folderName+"/ -sh";
		 		((ChannelExec) channel).setCommand(commandtofindFileSystemSize);
		 		inputStream = channel.getInputStream();
		 		channel.connect();
		 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
		 			output = lines.collect(Collectors.joining(newLine));
		 			if(null != output) {
			 			String[] folderSizeArray = output.replaceAll("[\t]+", " ").trim().split(" ");
				 		String folderSize = folderSizeArray[0];
						if(folderSize.contains("T")) {
							folderSize = folderSizeArray[0].replace("T", " TB");
						} else if(folderSize.contains("G")) {
							folderSize = folderSizeArray[0].replace("G", " GB");
						} else if(folderSize.contains("M")) {
							folderSize = folderSizeArray[0].replace("M", " MB");
						} else if(folderSize.contains("K")) {
							folderSize = folderSizeArray[0].replace("K", " KB");
						}
				 		folderSizeListInFile.add(folderSize);
		 			}
		 		}
		 		channel.disconnect();
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
			log.info("TC 02 Verify the size and Number of the file system Directories are captured in report. Ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	/**
	 * Verify the ignored files are captured in Report
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_verifyIgnoredFiles() throws JSchException, SftpException, Exception {
		log.info("TC 03 Verify the ignored files are captured in Report. Started.............");
		loadLowLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = sftpChannel.get(fileProperties.getProperty("fs_constants_linux"));
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String excludedFoldersinFile = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("EXCLUSION_FOLDERS") && !line.contains("#")) {
					excludedFoldersinFile = line.split("=")[1].replaceAll("\"", "").replaceAll("[\\[\\]]", "").replaceAll(" ", "");
				}
			}
			
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
			br = new BufferedReader(new InputStreamReader(stream));
			String webResourcePath = null;
			String line1;
			while ((line1 = br.readLine()) != null) {
				if (line1.contains("web_resource_path") && !
						line1.contains("#")) {
					webResourcePath = line1.split("=")[1].replaceAll("\'", "").replaceAll("\"", "").trim();
				}
			}
			String newLine = System.getProperty("line.separator");
			String folders = null;
			Channel channel = session.openChannel("exec");
			String commandtoListFolders = "ls -d " + webResourcePath+"*";
			((ChannelExec) channel).setCommand(commandtoListFolders);
			InputStream inputStream = channel.getInputStream();
			channel.connect();
			try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
				folders = lines.collect(Collectors.joining(newLine));
			}
			channel.disconnect();
			
			
			List<String> excluedFoldersListInFile = Arrays.asList(excludedFoldersinFile.split(","));
			List<String> excluedFoldersListInReport = new ArrayList<String>();
			
			List<String> excluedFoldersListInFileFinal = new ArrayList<String>();
			for(String exl : excluedFoldersListInFile) {
				exl = exl.replaceAll("'", "").replaceAll("\"", "");
				if(folders.contains(exl)) {
					excluedFoldersListInFileFinal.add(exl);
				}
			}
			
			listOfWebElement = xtexts(xpathProperties.getProperty("excluded_folder_details"));
			for (int i = 1; i < listOfWebElement.size(); i++) {
				excluedFoldersListInReport.add(listOfWebElement.get(i).getText());
			}
			Collections.sort(excluedFoldersListInReport);
			Collections.sort(excluedFoldersListInFileFinal);
			Assert.assertEquals(excluedFoldersListInReport.size(), excluedFoldersListInFileFinal.size());
			Assert.assertEquals(excluedFoldersListInReport, excluedFoldersListInFileFinal);
			sftpChannel.disconnect();
	 		session.disconnect();
			log.info("TC 03 Verify the ignored files are captured in Report. Ended.............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	/**
	 * Verify Attachment Root Directory size, availability
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc04_verifyAttachementRoot() throws JSchException, SftpException, Exception {
		log.info("TC 04 Verify attachment root path is captured in report. started..............");
		loadLowLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String propsFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					propsFilePath = line.split("=")[1].replaceAll("\"", "");
				}
			}
			if(null != propsFilePath) {
				propsFilePath = propsFilePath + "/config.properties";
				stream = sftpChannel.get(propsFilePath);
				br = new BufferedReader(new InputStreamReader(stream));
				String attachmentRootAvailability = "Attachment Root Not Available";
				while ((line = br.readLine()) != null) {
					if (line.contains("attachmentRoot") && !line.contains("#")) {
						String attachmentRoot = line.split("=")[1];
						attachmentRootAvailability = (null != attachmentRoot && !attachmentRoot.trim().isEmpty()) ?
								"Attachment Root Available" : "Attachment Root Not Available";
						String newLine = System.getProperty("line.separator");
						String rootFileSystemSize = null;
					    Channel channel = session.openChannel("exec");
				 		String commandtofindFileSystemSize = "sudo du "+attachmentRoot+" -sh";
				 		((ChannelExec) channel).setCommand(commandtofindFileSystemSize);
				 		InputStream inputStream = channel.getInputStream();
				 		channel.connect();
				 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
				 			rootFileSystemSize = lines.collect(Collectors.joining(newLine));
				 		}
				 		channel.disconnect();
				 		
				 		String rootFileCount = null;
					    channel = session.openChannel("exec");
				 		String commandtofindRootFileCount = "sudo find "+attachmentRoot+" -type f | wc -l";
				 		((ChannelExec) channel).setCommand(commandtofindRootFileCount);
				 		inputStream = channel.getInputStream();
				 		channel.connect();
				 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
				 			rootFileCount = lines.collect(Collectors.joining(newLine));
				 		}
				 		channel.disconnect();
				 		String rootDirectoryAvailability = "False";
				 		String rootDirectorySize = null;
				 		if(null != rootFileSystemSize) {
					 		String[] rootDirectoryArray = rootFileSystemSize.replaceAll("[\t]+", " ").trim().split(" ");
					 		rootDirectorySize = rootDirectoryArray[0];
							if(rootDirectorySize.contains("T")) {
								rootDirectorySize = rootDirectoryArray[0].replace("T", " TB");
							} else if(rootDirectorySize.contains("G")) {
								rootDirectorySize = rootDirectoryArray[0].replace("G", " GB");
							} else if(rootDirectorySize.contains("M")) {
								rootDirectorySize = rootDirectoryArray[0].replace("M", " MB");
							} else if(rootDirectorySize.contains("K")) {
								rootDirectorySize = rootDirectoryArray[0].replace("K", " KB");
							}
							rootDirectoryAvailability =  "True";
				 		}
						listOfWebElement = xtexts(xpathProperties.getProperty("attachment_folder_details"));
						List<WebElement> listOfWebElementCopy = listOfWebElement;
						for (int i = 0; i < listOfWebElementCopy.size(); i++) {
							listOfWebElement = xtexts("//*[contains(text(),'Attachment Folder Details')]/following::table[1]/tbody[1]/tr[" + (i + 1) + "]/td");
							System.out.println(i+":::"+listOfWebElement.get(0).getText());
							if (i == 0) {
								Assert.assertEquals(listOfWebElement.get(0).getText(), rootFileCount);
							}
							if (i == 1) {
								Assert.assertEquals(listOfWebElement.get(0).getText(), rootDirectorySize);
							}
							if (i == 2) {
								Assert.assertEquals(listOfWebElement.get(0).getText(), attachmentRoot);
							}
							if (i == 3) {
								Assert.assertEquals(listOfWebElement.get(0).getText(), rootDirectoryAvailability);
							}
						}
					}
				}
				Assert.assertEquals(attachmentRootAvailability, "Attachment Root Available");
			}
			sftpChannel.disconnect();
	 		session.disconnect();
			log.info("TC 04 Verify attachment root path is captured in report. Ended..............");
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
		InputStream stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String logFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					logFilePath = line.split("=")[1].replaceAll("\"", "");
				}
			}
			if(null != logFilePath) {
				logFilePath = logFilePath + "/log4j2.properties";
				stream = sftpChannel.get(logFilePath);
				br = new BufferedReader(new InputStreamReader(stream));
				String logDirectoryAvailability = "Log Directory Not Available";
				while ((line = br.readLine()) != null) {
					if (line.contains("property.LOG_DIR") && !line.contains("#")) {
						String logDirectory = line.split("=")[1];
						logDirectoryAvailability = (null != logDirectory && !logDirectory.trim().isEmpty()) ?
								"Log Directory Available" : "Log Directory Not Available";
						String newLine = System.getProperty("line.separator");
						String logDirectorySize = null;
					    Channel channel = session.openChannel("exec");
				 		String commandtofindLogDirectorySize = "sudo du "+logDirectory+" -sh";
				 		((ChannelExec) channel).setCommand(commandtofindLogDirectorySize);
				 		InputStream inputStream = channel.getInputStream();
				 		channel.connect();
				 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
				 			logDirectorySize = lines.collect(Collectors.joining(newLine));
				 		}
				 		channel.disconnect();
				 		
				 		String logDirectorySizeInKB = null;
				 		String logDirectoryAvailable = "False";
				 		if(null != logDirectorySize) {
				 			String[] logDirectoryArray = logDirectorySize.replaceAll("[\t]+", " ").trim().split(" ");
				 			logDirectorySizeInKB = logDirectoryArray[0];
							if(logDirectorySizeInKB.contains("T")) {
								logDirectorySizeInKB = logDirectoryArray[0].replace("T", " TB");
							} else if(logDirectorySizeInKB.contains("G")) {
								logDirectorySizeInKB = logDirectoryArray[0].replace("G", " GB");
							} else if(logDirectorySizeInKB.contains("M")) {
								logDirectorySizeInKB = logDirectoryArray[0].replace("M", " MB");
							} else if(logDirectorySizeInKB.contains("K")) {
								logDirectorySizeInKB = logDirectoryArray[0].replace("K", " KB");
							}
							logDirectoryAvailable = "True";
				 		}
						
						listOfWebElement = xtexts(xpathProperties.getProperty("application_log_details"));
						List<WebElement> listOfWebElementCopy = listOfWebElement;
						for (int i = 0; i < listOfWebElementCopy.size(); i++) {
							listOfWebElement = xtexts("//*[contains(text(),'Application Log details')]/following::table[1]/tbody[1]/tr[" + (i + 1) + "]/td");
							System.out.println(i+":::"+listOfWebElement.get(0).getText());
							if (i == 0) {
								Assert.assertEquals(listOfWebElement.get(0).getText(), logDirectorySizeInKB);
							}
							if (i == 1) {
								Assert.assertEquals(listOfWebElement.get(0).getText(), logDirectory);
							}
							if (i == 2) {
								Assert.assertEquals(listOfWebElement.get(0).getText(), logDirectoryAvailable);
							}
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
	
	/**
	 * Check whether the report captured the sufficient details or not
	 * @throws Exception
	 */
	@Test
	public void tc07_ReportCheck() throws Exception {
		log.info("TC 07 Report check started....................");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		String[] checkList = { "Filesystem Details", "Folders Details", "Excluded Folder Details", 
				"Application Log details"};
		for (int i = 0; i < checkList.length; i++) {
			String web = driver.getPageSource().contains(checkList[i]) ? checkList[i]+" Available" : checkList[i]+" Not Available";
			Assert.assertEquals(web, checkList[i]+" Available");
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
		InputStream stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String mountPath = null;
			String webResourcePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("mount_path") && !line.contains("#")) {
					mountPath = line.split("=")[1].replaceAll("\'", "").replaceAll("\"", "");
				}
				if (line.contains("web_resource_path") && !line.contains("#")) {
					webResourcePath = line.split("=")[1].replaceAll("\'", "").replaceAll("\"", "");
				}
			}
			String newLine = System.getProperty("line.separator");
			String output = null;
		    Channel channel = session.openChannel("exec");
	 		String commandtofindFileSystemExistance = "test -d  "+mountPath+" && echo \"Exists\" || echo \"Not Exists\"";
	 		((ChannelExec) channel).setCommand(commandtofindFileSystemExistance);
	 		InputStream inputStream = channel.getInputStream();
	 		channel.connect();
	 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
	 			output = lines.collect(Collectors.joining(newLine));
	 		}
	 		channel.disconnect();
	 		if(output.equals("Not Exists")) {
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
	 			log.info("This test case works only if Given Mount Directory Path is wrong");
	 		}
	 		
	 		channel = session.openChannel("exec");
	 		String commandtofindWebResource = "test -d  "+webResourcePath+" && echo \"Exists\" || echo \"Not Exists\"";
	 		((ChannelExec) channel).setCommand(commandtofindWebResource);
	 		inputStream = channel.getInputStream();
	 		channel.connect();
	 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
	 			output = lines.collect(Collectors.joining(newLine));
	 		}
	 		channel.disconnect();
	 		if(output.equals("Not Exists")) {
	 			text = xtext(xpathProperties.getProperty("web_resources_no_data"));
	 			Assert.assertEquals(text, "Data not found");
	 			text = xtext(xpathProperties.getProperty("folder_details_no_data"));
	 			Assert.assertEquals(text, "Data not found");
	 		} else {
	 			Assert.assertEquals("Web Resource Directory exists", "Web Resource Directory not exists");
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
		InputStream stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String mountPath = null;
			String webResourcePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("mount_path") && !line.contains("#") && null == mountPath) {
					mountPath = line.split("=")[1].replaceAll("\'", "").replaceAll("\"", "");
				}
				if (line.contains("web_resource_path") && !line.contains("#")) {
					webResourcePath = line.split("=")[1].replaceAll("\'", "").replaceAll("\"", "");
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
	 			log.info("This test case works only if No Path is Given as Mount Directory");
	 		}
	 		if(webResourcePath.equals("")) {
	 			text = xtext(xpathProperties.getProperty("web_resources_no_data"));
	 			Assert.assertEquals(text, "Data not found");
	 			text = xtext(xpathProperties.getProperty("folder_details_no_data"));
	 			Assert.assertEquals(text, "Data not found");
	 		} else {
	 			log.info("This test case works only if No Path is Given as Web Resource Directory");
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
