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
import java.util.List;
import java.util.Map;
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
		establishSshConnection();
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
		establishSshConnection();
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
	 * Checking whether the precheck report captures getValue("Description") in webapp folder
	 * @throws SQLException
	 * @throws Exception
	 */
	@Test
	public void tc04_checkPrecheckReportCapturesKeyword() throws SQLException, Exception {
		log.info("TC 04 Checking whether the Precheck Report captures Python files contain Java Keywords. Started....");
		loadHighLevelReportInBrowser();
		establishSshConnection();
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
		Channel channel = null;
		InputStream inputStream = null;
		Map<String,String> keywordFileMap = new HashMap<String,String>();
		for(String expectedKeyword : expectedKeyWordList) {
			channel = session.openChannel("exec");
		    String commandtoFetchFilesContainsKeyword = "grep -Rw /home/ec2-user/webapps/Tomcat/ -e '"+expectedKeyword+"' --include=*.py";
	 		((ChannelExec) channel).setCommand(commandtoFetchFilesContainsKeyword);
	 		inputStream = channel.getInputStream();
	 		channel.connect();
	 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
	 			commandOutput = lines.collect(Collectors.joining(newLine));
	 		}
	 		List<String> commandOutputasList = Arrays.asList(commandOutput.split("\n"));
	 		for(String fileKeyword : commandOutputasList) {
	 			if(null != fileKeyword && fileKeyword != "") {
		 			String pythonFile = fileKeyword.split(":")[0].trim();
		 			String keyword = fileKeyword.split(":")[1].trim();
		 			keywordFileMap.put(keyword, pythonFile);
	 			}
	 		}
	 		System.out.println("keywordFileMap::::"+keywordFileMap);
	 		channel.disconnect();
	    }
 		log.info("TC 04 Checking whether the Precheck Report captures Python files contain Java Keywords. Ended....");
	}
}
