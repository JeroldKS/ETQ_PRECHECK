package precheckStories;

import org.testng.annotations.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;

import org.testng.Assert;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import precheck.Base;


public class MMP359_PythonScriptCode extends Base {
	static Logger log = Logger.getLogger(MMP359_PythonScriptCode.class.getName());
	
	/**
	 * Fetching occurances of the String getValue("Description") in webapp folder
	 * @throws SQLException
	 * @throws Exception
	 */
	@Test
	public void tc01_fetchCountOfString() throws SQLException, Exception {
		log.info("TC 01 Fetching Count of 'getValue(Description)' String. Started....");
		loadHighLevelReportInBrowser();
		establishSshConnection();
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
	    Channel channel = session.openChannel("exec");
	    String commandtoFetchFilesContainsKeyword = "grep -Rw "+webAppPath+" -e 'getValue(\"Description\")' --include=*.py";
 		((ChannelExec) channel).setCommand(commandtoFetchFilesContainsKeyword);
 		InputStream inputStream = channel.getInputStream();
 		channel.connect();
 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 		}
 		Assert.assertNotNull(commandOutput);
 		channel.disconnect();
 		log.info("TC 01 Fetching Count of 'getValue(\"Description\")' String. Started....");
	}
	
	/**
	 * Checking whether the precheck report captures getValue("Description") in webapp folder
	 * @throws SQLException
	 * @throws Exception
	 */
	@Test
	public void tc02_checkPrecheckReportCapturesKeyword() throws SQLException, Exception {
		log.info("TC 02 Checking whether the Precheck Report captures Python files contain 'getValue(Description)' String. Started....");
		loadHighLevelReportInBrowser();
		establishSshConnection();
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
	    Channel channel = session.openChannel("exec");
 		String commandtoFetchFilesContainsKeyword = "grep -Rw "+webAppPath+" -e 'getValue(\"Description\")' --include=*.py";
 		((ChannelExec) channel).setCommand(commandtoFetchFilesContainsKeyword);
 		InputStream inputStream = channel.getInputStream();
 		channel.connect();
 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 		}
 		Assert.assertNotNull(commandOutput);
 		channel.disconnect();
 		TreeMap<String,String> keywordOccuranceMapInInstance = new TreeMap<String,String>();
 		TreeMap<String,String> keywordOccuranceMapInReport = new TreeMap<String,String>();
 		List<String> commandOutputasList = Arrays.asList(commandOutput.split("\n"));
 		for(String commandOutputLine : commandOutputasList) {
 			String pythonFile = commandOutputLine.split(":")[0].trim();
 			String command = "grep -Rw "+ pythonFile + " -e 'getValue(\"Description\")'  | wc -l";
 			channel = session.openChannel("exec");
 	 		((ChannelExec) channel).setCommand(command);
 	 		inputStream = channel.getInputStream();
 	 		channel.connect();
 	 		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
 	 			commandOutput = lines.collect(Collectors.joining(newLine));
 	 		}
 	 		keywordOccuranceMapInInstance.put(pythonFile, commandOutput);
 	 		channel.disconnect();
 		}
 		listOfWebElement = xtexts("//*[contains(text(),'Keyword Ids in ETQ script')]/../td[3]/table/tbody/tr");
	 		List<WebElement> listOfWebElementCopy = listOfWebElement;
		for (int i = 0; i < listOfWebElementCopy.size(); i++) {
			listOfWebElement = xtexts("//*[contains(text(),'Keyword Ids in ETQ script')]/../td[3]/table/tbody/tr[" + (i + 1) + "]");
			text = xtext("//*[contains(text(),'Keyword Ids in ETQ script')]/../td[3]/table/tbody/tr[" + (i + 1) + "]/td[1]");
			String fileName = text;
			text = xtext("//*[contains(text(),'Keyword Ids in ETQ script')]/../td[3]/table/tbody/tr[" + (i + 1) + "]/td[2]");
			String count = text;
			keywordOccuranceMapInReport.put(fileName, count);
		}
		Assert.assertEquals(keywordOccuranceMapInReport, keywordOccuranceMapInInstance);
 		log.info("TC 02 Checking whether the Precheck Report captures Python files contain 'getValue(Description)' String. Ended....");
	}
	
	/**
	 * Checking whether the precheck report captures getValue("Description") in webapp folder
	 * @throws SQLException
	 * @throws Exception
	 */
	@Test
	public void tc03_checkPrecheckReportNotCapturesTxtFile() throws SQLException, Exception {
		log.info("TC 03 Checking whether the Precheck Report not captures .txt files contain 'getValue(Description)' String. Started....");
		loadHighLevelReportInBrowser();
 		listOfWebElement = xtexts("//*[contains(text(),'Keyword Ids in ETQ script')]/../td[3]/table/tbody/tr");
	 	List<WebElement> listOfWebElementCopy = listOfWebElement;
		for (int i = 0; i < listOfWebElementCopy.size(); i++) {
			listOfWebElement = xtexts("//*[contains(text(),'Keyword Ids in ETQ script')]/../td[3]/table/tbody/tr[" + (i + 1) + "]");
			text = xtext("//*[contains(text(),'Keyword Ids in ETQ script')]/../td[3]/table/tbody/tr[" + (i + 1) + "]/td[1]");
			String fileName = text;
			String isReportContainsTxtFile = fileName.contains(".txt") ? "Report contains .txt file" : "Report should not contains .txt file";
			Assert.assertEquals(isReportContainsTxtFile, "Report should not contains .txt file");
		}
		log.info("TC 03 Checking whether the Precheck Report not captures .txt files contain 'getValue(Description)' String. Ended....");
	}
}
