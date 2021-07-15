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

public class MMP386_LinuxFileSystemCheck extends Base{
	
	static Logger log = Logger.getLogger(MMP386_LinuxFileSystemCheck.class.getName());
  
	/**
	 * Verify the file system size, availability are captured in Low Level Report
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_verifyFileSystemSize() throws JSchException, SftpException, Exception {
		log.info("TC 02 Verify the overall file system size. Started.............");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("mysqlSource");
		establishSshConnectionforSourceDB();
		InputStream stream = sftpChannel.get("/home/QA_testing/precheck/Property.toml");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String mountPath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("mount_path") && !line.contains("#")) {
					mountPath = line.split("=")[1].replaceAll("\"", "");
				}
			}
			if(null != mountPath) {
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
				String isFileAvailable = commandOutput.contains("Filesystem") == true ? "True" : "False";
				String output = commandOutput.split("\n")[1].replaceAll("\\s{2,}", " ").trim();
				String availableSize = output.split(" ")[3].replace("G", " GB");
				listOfWebElement = xtexts("//*[contains(text(),'Filesystem Details')]/following::table[1]/tbody[1]/tr[2]/td");
				List<WebElement> listOfWebElementCopy = listOfWebElement;
				for (int i = 0; i < listOfWebElementCopy.size(); i++) {
					listOfWebElement = xtexts("//*[contains(text(),'Filesystem Details')]/following::table[1]/tbody[1]/tr[2]/td[" + (i + 1) + "]");
					List<WebElement> listDataList = listOfWebElement;
					if (i == 0) {
						assertEquals(listDataList.get(0).getText(), mountPath);
					} else if (i == 1) {
						assertEquals(listDataList.get(0).getText(), isFileAvailable);
					} else if (i == 2) {
						assertEquals(listDataList.get(0).getText(), availableSize);
					}
				}
			}
			dbConnection.close();
			log.info("TC 02 Verify the overall file system size. Ended.............");
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
	public static void tc03_verifySizeandNumberOfFolders() throws JSchException, SftpException, Exception {
		log.info("TC 03 Verify the size and Number of the file system Directories are captured in report. started..............");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("mysqlSource");
		establishSshConnectionforSourceDB();
		try {
			InputStream stream = sftpChannel.get("/home/QA_testing/precheck/const/fs_constants.py");
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
			stream = sftpChannel.get("/home/QA_testing/precheck/Property.toml");
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
		 			String[] folderSizeArray = output.replaceAll("[\t]+", " ").trim().split(" ");
			 		String folderSize = folderSizeArray[0].replaceAll("G", " GB").replaceAll("K", " KB");
			 		folderSizeListInFile.add(folderSize);
		 		}
		 		channel.disconnect();
	 		}
	 		List<String> folderNameListInReport = new ArrayList<String>();
	 		List<String> objectCountListInReport = new ArrayList<String>();
	 		List<String> folderSizeListInReport = new ArrayList<String>();
	 		listOfWebElement = xtexts("//*[contains(text(),'Folders Details')]/following::table[1]/tbody[1]/tr/td[1]");
			List<WebElement> listOfFolderName = listOfWebElement;
			for (int i = 0; i < listOfFolderName.size(); i++) {
				folderNameListInReport.add(listOfFolderName.get(i).getText());
			}
			listOfWebElement = xtexts("//*[contains(text(),'Folders Details')]/following::table[1]/tbody[1]/tr/td[2]");
			List<WebElement> listOfObjectCount = listOfWebElement;
			for (int i = 0; i < listOfObjectCount.size(); i++) {
				objectCountListInReport.add(listOfObjectCount.get(i).getText());
			}
			listOfWebElement = xtexts("//*[contains(text(),'Folders Details')]/following::table[1]/tbody[1]/tr/td[3]");
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
			log.info("TC 03 Verify the size and Number of the file system Directories are captured in report. Ended..............");
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
	public static void tc04_verifyIgnoredFiles() throws JSchException, SftpException, Exception {
		log.info("TC 04 Verify the ignored files are captured in Report. Started.............");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("mysqlSource");
		establishSshConnectionforSourceDB();
		InputStream stream = sftpChannel.get("/home/QA_testing/precheck/const/fs_constants.py");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String excludedFoldersinFile = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("EXCLUSION_FOLDERS") && !line.contains("#")) {
					excludedFoldersinFile = line.split("=")[1].replaceAll("\"", "").replaceAll("[\\[\\]]", "").replaceAll(" ", "");
				}
			}
			List<String> excluedFoldersListInFile = Arrays.asList(excludedFoldersinFile.split(","));
			List<String> excluedFoldersListInReport = new ArrayList<String>();
			
			listOfWebElement = xtexts("//*[contains(text(),'Excluded Folder Details')]/following::table[1]/tbody[1]/tr");
			for (int i = 1; i < listOfWebElement.size(); i++) {
				excluedFoldersListInReport.add(listOfWebElement.get(i).getText());
			}
			Collections.sort(excluedFoldersListInReport);
			Collections.sort(excluedFoldersListInFile);
			Assert.assertEquals(excluedFoldersListInReport.size(), excluedFoldersListInFile.size());
			Assert.assertEquals(excluedFoldersListInReport, excluedFoldersListInFile);
			dbConnection.close();
			log.info("TC 04 Verify the ignored files are captured in Report. Ended.............");
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
	public static void tc05_verifyAttachementRoot() throws JSchException, SftpException, Exception {
		log.info("TC 05 Verify attachment root path is captured in report. started..............");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("mysqlSource");
		establishSshConnectionforSourceDB();
		InputStream stream = sftpChannel.get("/home/QA_testing/precheck/Property.toml");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String propsFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					propsFilePath = line.split("=")[1].replaceAll("\"", "");
				}
			}
			establishSshConnectionforSourceDB();
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
				 		
				 		String[] rootDirectoryArray = rootFileSystemSize.replaceAll("[\t]+", " ").trim().split(" ");
				 		String rootDirectorySize = rootDirectoryArray[0].replace("G", " GB");
				 		String rootDirectoryAvailability = rootDirectorySize.contains("GB") ? "True" : "False";
						listOfWebElement = xtexts("//*[contains(text(),'Attachment Folder Details')]/following::table[1]/tbody[1]/tr/td");
						List<WebElement> listOfWebElementCopy = listOfWebElement;
						for (int i = 0; i < listOfWebElementCopy.size(); i++) {
							listOfWebElement = xtexts("//*[contains(text(),'Attachment Folder Details')]/following::table[1]/tbody[1]/tr[" + (i + 1) + "]/td");
							System.out.println(i+":::"+listOfWebElement.get(0).getText());
							if (i == 0) {
								assertEquals(listOfWebElement.get(0).getText(), rootFileCount);
							}
							if (i == 1) {
								assertEquals(listOfWebElement.get(0).getText(), rootDirectorySize);
							}
							if (i == 2) {
								assertEquals(listOfWebElement.get(0).getText(), attachmentRoot);
							}
							if (i == 3) {
								assertEquals(listOfWebElement.get(0).getText(), rootDirectoryAvailability);
							}
						}
					}
				}
				assertEquals(attachmentRootAvailability, "Attachment Root Available");
			}
			log.info("TC 01 Checking if SSO enabled ended..............");
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
	public static void tc07_verifyApplicationLog() throws JSchException, SftpException, Exception {
		log.info("TC 05 Verify attachment root path is captured in report. started..............");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("mysqlSource");
		establishSshConnectionforSourceDB();
		InputStream stream = sftpChannel.get("/home/QA_testing/precheck/Property.toml");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String logFilePath = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("props_file_path") && !line.contains("#")) {
					logFilePath = line.split("=")[1].replaceAll("\"", "");
				}
			}
			establishSshConnectionforSourceDB();
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
				 		
				 		String[] logDirectoryArray = logDirectorySize.replaceAll("[\t]+", " ").trim().split(" ");
				 		String logDirectorySizeInKB = logDirectoryArray[0].replace("K", " KB");
				 		String logDirectoryAvailable = logDirectorySizeInKB.contains("KB") ? "True" : "False";
						listOfWebElement = xtexts("//*[contains(text(),'Application Log details')]/following::table[1]/tbody[1]/tr/td");
						List<WebElement> listOfWebElementCopy = listOfWebElement;
						for (int i = 0; i < listOfWebElementCopy.size(); i++) {
							listOfWebElement = xtexts("//*[contains(text(),'Application Log details')]/following::table[1]/tbody[1]/tr[" + (i + 1) + "]/td");
							System.out.println(i+":::"+listOfWebElement.get(0).getText());
							if (i == 0) {
								assertEquals(listOfWebElement.get(0).getText(), logDirectorySizeInKB);
							}
							if (i == 1) {
								assertEquals(listOfWebElement.get(0).getText(), logDirectory);
							}
							if (i == 2) {
								assertEquals(listOfWebElement.get(0).getText(), logDirectoryAvailable);
							}
						}
					}
				}
				assertEquals(logDirectoryAvailability, "Log Directory Available");
			}
			log.info("TC 01 Checking if SSO enabled ended..............");
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
	public void tc08_ReportCheck() throws Exception {
		log.info("TC 08 Report check started....................");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		String[] checkList = { "Filesystem Details", "Folders Details", "Excluded Folder Details", 
				"Application Log details"};
		for (int i = 0; i < checkList.length; i++) {
			String web = driver.getPageSource().contains(checkList[i]) ? checkList[i]+" Available" : checkList[i]+" Not Available";
			Assert.assertEquals(web, checkList[i]+" Available");
		}
		log.info("TC 08 Report check ended....................");
	}
}
