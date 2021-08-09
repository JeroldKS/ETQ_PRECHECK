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
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("common_constants_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("common_constants_windows"));
		}
		try {
			String keywordsAvailability = "Java Keywords not Available";
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			List<String> expectedKeyWordList = new ArrayList<String>(Arrays.asList("import *", "import sys", "import os", "from java.lang", "import Runtime", "System", "from java.io", "import java.lang.Runtime", "import java.io.File"));
			while ((line = br.readLine()) != null) {
				if (line.contains("JAVA_CLASS_KEYWORD") && !line.contains("#")) {
					keywordsAvailability = "Java Keywords Available";
					String keywordList = line.split("=")[1];
					while (!line.endsWith("]")) {
						line=br.readLine();
						keywordList=keywordList+line;
						
					}
					String[] keywordArray = keywordList.replaceAll("\\s{2,}", "").replaceAll(", ", ",").replaceAll("\'", "").replaceAll("[\\[\\]]", "").trim().split(",");
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
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_windows"));
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String webAppPath = null;
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains("web_app_file_path") && !line.contains("#")) {
				webAppPath = line.split("=")[1].replaceAll("\"", "").replaceAll("\'", "");
			}
		}
		String newLine = System.getProperty("line.separator");
		String commandOutput = null;
	    for(String expectedKeyword : expectedKeyWordList) {
	    	Channel channel = session.openChannel("exec");
	    	String commandtoFetchFilesContainsKeyword = null;
			if(osUserInput.equalsIgnoreCase("linux")) {
				commandtoFetchFilesContainsKeyword = "grep -RF "+webAppPath+" -e '"+expectedKeyword+"' --include=*.py";
			} else if(osUserInput.equalsIgnoreCase("windows")) {
				commandtoFetchFilesContainsKeyword = "powershell.exe \"Get-ChildItem -Path "+webAppPath+"\\*.py -Recurse | Select-String -Pattern '"+expectedKeyword+"' -CaseSensitive\"";
			}
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
}
