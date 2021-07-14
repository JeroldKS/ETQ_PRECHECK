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
}
