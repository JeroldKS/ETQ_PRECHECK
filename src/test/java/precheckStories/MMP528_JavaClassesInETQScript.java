package precheckStories;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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

public class MMP528_JavaClassesInETQScript extends Base{
	
	static Logger log = Logger.getLogger(MMP528_JavaClassesInETQScript.class.getName());
  
	/**
	 * Checking whether the listed import keywords available in common constants
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_checkKeywordsAvailable() throws JSchException, SftpException, Exception {
		log.info("TC 01 Checking if the keywords available in Property file. Started....");
		loadHighLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		InputStream stream = sftpChannel.get("/home/ec2-user/QA_testing/migration-tool/src/precheck/const/common_constants.py");
		try {
			String keywordsAvailability = "Java Keywords not Available";
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			List<String> expectedKeyWordList = new ArrayList<String>(Arrays.asList("import *", "import sys", "import os", "from java.lang", "import Runtime", "System", "from java.io", "import java.lang.Runtime", "import java.io.File"));
			while ((line = br.readLine()) != null) {
				if (line.contains("JAVA_CLASS_KEYWORD") && !line.contains("#")) {
					keywordsAvailability = "Java Keywords Available";
					String keywordList = line.split("=")[1];
					if(!line.endsWith("]")) {
						keywordList = keywordList + br.readLine();
					}
					String[] keywordArray = keywordList.replaceAll(", ", ",").replaceAll("\'", "").replaceAll("[\\[\\]]", "").trim().split(",");
					List<String> actualKeyWordList = new ArrayList<String>(Arrays.asList(keywordArray));
					Collections.sort(expectedKeyWordList);
					Collections.sort(actualKeyWordList);
					Assert.assertEquals(actualKeyWordList, expectedKeyWordList);
				}
			}
			Assert.assertEquals(keywordsAvailability, "Java Keywords Available");
			log.info("TC 01 Checking if the keywords available in Property file. Ended....");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	/**
	 * Checking whether the listed import keywords available in common constants
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_identifyMatchingKeywords() throws JSchException, SftpException, Exception {
		log.info("TC 03 Identifying matching keywords in config file and Python file. Started....");
		loadHighLevelReportInBrowser();
		establishSshConnectionForSourceInstance();
		List<String> expectedKeyWordList = new ArrayList<String>(Arrays.asList("import *", "import sys", "import os", "from java.lang", "import Runtime, System", "from java.io", "import java.lang.Runtime", "import java.io.File"));
		InputStream stream = sftpChannel.get("/home/ec2-user/QA_testing/migration-tool/src/precheck/Property.toml");
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String webAppPath = null;
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains("web_app_file_path") && !line.contains("#")) {
				webAppPath = line.split("=")[1].replaceAll("\"", "");
			}
		}
		String newLine = System.getProperty("line.separator");
		String commandOutput = null;
	    for(String expectedKeyword : expectedKeyWordList) {
	    	Channel channel = session.openChannel("exec");
		    String commandtoFetchFilesContainsKeyword = "grep -Rw "+webAppPath+" -e '"+expectedKeyword+"' --include=*.py";
	 		((ChannelExec) channel).setCommand(commandtoFetchFilesContainsKeyword);
	 		InputStream inputStream = channel.getInputStream();
	 		channel.connect();
	 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
	 			commandOutput = lines.collect(Collectors.joining(newLine));
	 		}
	 		Assert.assertNotNull(commandOutput);
	 		channel.disconnect();
	    }
 		log.info("TC 03 Identifying matching keywords in config file and Python file. Ended....");
	}
	
	/**
	 * Checking whether the precheck report captures expected keywords in webapp folder
	 * @throws SQLException
	 * @throws Exception
	 */
	@Test
	public void tc04_checkPrecheckReportCapturesKeyword() throws SQLException, Exception {
		log.info("TC 04 Checking whether the Precheck Report captures Python files contain Java Keywords. Started....");
		loadHighLevelReportInBrowser();
		establishSshConnectionforSourceDB();
		List<String> expectedKeyWordList = new ArrayList<String>(Arrays.asList("import *", "import sys", "import os", "from java.lang", "import Runtime, System", "from java.io", "import java.lang.Runtime", "import java.io.File"));
		InputStream stream = sftpChannel.get("/home/QA_testing/migration-tool/src/precheck/Property.toml");
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String webAppPath = null;
		String line;
		while ((line = br.readLine()) != null) {
		if (line.contains("web_app_file_path") && !line.contains("#")) {
				webAppPath = line.split("=")[1].replaceAll("\"", "");
			}
		}
		String newLine = System.getProperty("line.separator");
		String commandOutput = null;
		Channel channel = null;
		InputStream inputStream = null;
		List<String> keywordFileList = new ArrayList<String>();
		for(String expectedKeyword : expectedKeyWordList) {
			channel = session.openChannel("exec");
		    String commandtoFetchFilesContainsKeyword = "grep -RF "+webAppPath+" -e '"+expectedKeyword+"' --include=*.py";
	 		((ChannelExec) channel).setCommand(commandtoFetchFilesContainsKeyword);
	 		inputStream = channel.getInputStream();
	 		channel.connect();
	 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
	 			commandOutput = lines.collect(Collectors.joining(newLine));
	 		}
	 		channel.disconnect();
	 		if(null != commandOutput && !commandOutput.isEmpty()) {
		 		String[] commandOutputArray = commandOutput.split("\n");
		 		Set<String> fileAndKeywordSet = new HashSet<>();
		 		if(commandOutputArray.length > 0) {
			        for (String fileAndKeyWord : commandOutputArray) {
			        	fileAndKeywordSet.add(fileAndKeyWord.trim());
			        }	
		 		}
		        if(null != fileAndKeywordSet && fileAndKeywordSet.size() != 0) {
		        	keywordFileList.add(fileAndKeywordSet.stream().findFirst().get());
		        }
	 		}
	    }
		if(null != keywordFileList && !keywordFileList.isEmpty()) {
			TreeMap<String,String> keywordOccuranceMapInInstance = new TreeMap<String,String>();
			TreeMap<String,String> keywordOccuranceMapInReport = new TreeMap<String,String>();
			for(String keywordFile : keywordFileList) {
	 			String pythonFile = keywordFile.split(":")[0].trim();
	 			String keyword = keywordFile.split(":")[1].replaceAll(";", "").trim();
	 			String command = "grep -RF "+webAppPath+" -e '"+keyword+"' --include=*.py  | wc -l";
	 			channel = session.openChannel("exec");
	 	 		((ChannelExec) channel).setCommand(command);
	 	 		inputStream = channel.getInputStream();
	 	 		channel.connect();
	 	 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
	 	 			commandOutput = lines.collect(Collectors.joining(newLine));
	 	 		}
	 	 		keywordOccuranceMapInInstance.put(keyword, pythonFile+":"+commandOutput);
	 	 		channel.disconnect();
	 		}
			text = xtext("//*[contains(text(),'Java Classes in ETQ script')]/../td[2]");
			Assert.assertEquals(text, "Action Required");
			text = xtext("//*[contains(text(),'Java Classes in ETQ script')]/../td[4]");
			Assert.assertEquals(text, "Update the ETQScript formula(s) to remove the imported Java Class");
			listOfWebElement = xtexts("//*[contains(text(),'Java Classes in ETQ script')]/../td[3]/table/tbody/tr");
	 		List<WebElement> listOfWebElementCopy = listOfWebElement;
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'Java Classes in ETQ script')]/../td[3]/table/tbody/tr[" + (i + 1) + "]");
				text = xtext("//*[contains(text(),'Java Classes in ETQ script')]/../td[3]/table/tbody/tr[" + (i + 1) + "]/td[1]");
				String fileName = text;
				text = xtext("//*[contains(text(),'Java Classes in ETQ script')]/../td[3]/table/tbody/tr[" + (i + 1) + "]/td[2]");
				String keyword = text;
				text = xtext("//*[contains(text(),'Java Classes in ETQ script')]/../td[3]/table/tbody/tr[" + (i + 1) + "]/td[3]");
				String count = text;
				keywordOccuranceMapInReport.put(keyword, fileName+":"+count);
			}
			Assert.assertEquals(keywordOccuranceMapInReport.size(), keywordOccuranceMapInInstance.size());
			Assert.assertEquals(keywordOccuranceMapInReport, keywordOccuranceMapInInstance);
		} else {
			Assert.assertEquals("No Matching Keywords Found", "Keywords Found");
		}
 		log.info("TC 04 Checking whether the Precheck Report captures Python files contain Java Keywords. Ended....");
	}
	
	/**
	 * Checking whether the precheck report captures result if no matching import statement found
	 * @throws SQLException
	 * @throws Exception
	 */
	@Test
	public void tc05_checkIfNoMatchingKeywordsFound() throws SQLException, Exception {
		log.info("TC 05 Checking If No matching Import Keywords Found. Started....");
		loadHighLevelReportInBrowser();
		establishSshConnectionforSourceDB();
		List<String> expectedKeyWordList = new ArrayList<String>(Arrays.asList("import *", "import sys", "import os", "from java.lang", "import Runtime, System", "from java.io", "import java.lang.Runtime", "import java.io.File"));
		InputStream stream = sftpChannel.get("/home/QA_testing/migration-tool/src/precheck/Property.toml");
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String webAppPath = null;
		String line;
		while ((line = br.readLine()) != null) {
		if (line.contains("web_app_file_path") && !line.contains("#")) {
				webAppPath = line.split("=")[1].replaceAll("\"", "");
			}
		}
		String newLine = System.getProperty("line.separator");
		String commandOutput = null;
		Channel channel = null;
		InputStream inputStream = null;
		List<String> keywordFileList = new ArrayList<String>();
		for(String expectedKeyword : expectedKeyWordList) {
			channel = session.openChannel("exec");
		    String commandtoFetchFilesContainsKeyword = "grep -RF "+webAppPath+" -e '"+expectedKeyword+"' --include=*.py";
	 		((ChannelExec) channel).setCommand(commandtoFetchFilesContainsKeyword);
	 		inputStream = channel.getInputStream();
	 		channel.connect();
	 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
	 			commandOutput = lines.collect(Collectors.joining(newLine));
	 		}
	 		channel.disconnect();
	 		if(null != commandOutput && !commandOutput.isEmpty()) {
		 		String[] commandOutputArray = commandOutput.split("\n");
		 		Set<String> fileAndKeywordSet = new HashSet<>();
		 		if(commandOutputArray.length > 0) {
			        for (String fileAndKeyWord : commandOutputArray) {
			        	fileAndKeywordSet.add(fileAndKeyWord.trim());
			        }	
		 		}
		        if(null != fileAndKeywordSet && fileAndKeywordSet.size() != 0) {
		        	keywordFileList.add(fileAndKeywordSet.stream().findFirst().get());
		        }
	 		}
	    }
		if(null != keywordFileList && !keywordFileList.isEmpty()) {
			Assert.assertEquals("Keywords Found", "No Matching Keywords Found");
		} else {
			text = xtext("//*[contains(text(),'Java Classes in ETQScripts')]/../td[2]");
			Assert.assertEquals(text, "N/A");
			text = xtext("//*[contains(text(),'Java Classes in ETQScripts')]/../td[3]");
			Assert.assertEquals(text, "Good to Migrate");
		}
 		log.info("TC 05 Checking If No matching Import Keywords Found. Ended....");
	}
}
