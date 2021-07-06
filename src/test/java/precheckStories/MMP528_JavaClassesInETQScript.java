package precheckStories;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

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
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP529_query.properties");
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
}
