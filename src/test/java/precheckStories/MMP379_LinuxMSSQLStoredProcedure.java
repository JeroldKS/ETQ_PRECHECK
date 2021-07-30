package precheckStories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP379_LinuxMSSQLStoredProcedure extends Base{
	static Logger log = Logger.getLogger(MMP379_LinuxMSSQLStoredProcedure.class.getName());
	
	/**
	 * This method is for Report Headings validation
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc01_ReportHeadingsvalidation() throws Exception {
		log.info("TC 01 Report Headings validation started....................");
		establishDatabaseconnection();
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP379_LinuxMSSQLStoredProcedure.properties");
		xpathProperties = loadXpathFile();

		listOfWebElement = xtexts(xpathProperties.getProperty("reportHeadingList"));
		listOfText = listString();
		String[] checkList = { "Schema Name", "Stored Procedure Name", "Stored Procedure definition" };
		for (int i = 0; i < checkList.length; i++) {
			Assert.assertTrue(listOfText.contains(checkList[i]));
		}
		log.info("TC 01 Report Headings validation ended....................");
	}

	/**
	 * This method is to validating the report is generated StoredProcedure Count
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc02_IsReportCaptureStoredProcedureCountMatchesSource() throws Exception {
		log.info("TC 02 Report capture stored procedure count matches with Source validation started....................");
		establishDatabaseconnection();
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP379_LinuxMSSQLStoredProcedure.properties");
		xpathProperties = loadXpathFile();

		listOfWebElement = xtexts(xpathProperties.getProperty("reportSPCount"));
		listOfText = listString();
		int storedProcedureCount = 0;
		for (int i = 0; i < listOfText.size(); i++) {
			int ProcedureCount = Integer.parseInt(listOfText.get(i));
			storedProcedureCount = storedProcedureCount + ProcedureCount;
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("reportSPList"));
		listOfText = listString();
		Assert.assertEquals(listOfText.size(), storedProcedureCount);
		sourceQuery = query(prop.getProperty("storedProcedureCount"));
		int dbStoredProcedureCount=0;
		while (sourceQuery.next()) {
			dbStoredProcedureCount=dbStoredProcedureCount+Integer.parseInt(String.valueOf(sourceQuery.getObject(2)));
		}
		Assert.assertEquals(storedProcedureCount,dbStoredProcedureCount);
		log.info("TC 02 Report capture stored procedure count matches with Source validation ended....................");
	}

	/**
	 * This method is to validating the report is generated StoredProcedure
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc03_IsReportGenerateExpectedStoredProcedureFormat() throws Exception {
		log.info("TC 03 Report generate expected stored procedure format validation started....................");
		establishDatabaseconnection();
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP379_LinuxMSSQLStoredProcedure.properties");
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("spDataList"));
		List<WebElement> spDataList = listOfWebElement;
		try {
			for (int i = 0; i < spDataList.size(); i++) {
				listOfWebElement = xtexts(xpathProperties.getProperty("spEachDataList"));
				for (int j = 0; j < listOfWebElement.size(); j++) {
					text = xtext("//*[contains(text(),'Stored Procedures')]/following::tbody[1]/tr[" + (i + 1) + "]/td["
							+ (j + 1) + "]");
					Assert.assertNotNull(text);
				}
			}
		} catch (Exception getcatch) {
			log.error("No SP found in DataBase :: " + getcatch.getMessage());
		}
		log.info("TC 03 Report generate expected stored procedure format validation ended....................");
	}

	/**
	 * This method is to validating the report generated StoredProcedure matches
	 * with source
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc04_IsReportStoredProcedureMatchesSource() throws Exception {
		log.info("TC 04 Report stored procedure matches source validation started....................");
		establishDatabaseconnection();
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP379_LinuxMSSQLStoredProcedure.properties");
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("spData"));
		List<WebElement> dup_texts = listOfWebElement;
		List<String> reportSPList = new ArrayList<>();
		List<String> dbSPlList = new ArrayList<>();
		try {
			for (int i = 0; i < dup_texts.size(); i++) {
				listOfWebElement = xtexts(
						"//*[contains(text(),'Stored Procedures')]/following::tbody[1]/tr[" + (i + 1) + "]/td");
				String r_data_combain = "";
				for (int j = 0; j < listOfWebElement.size(); j++) {
					r_data_combain = r_data_combain + listOfWebElement.get(j).getText() + ".";
				}
				reportSPList.add(r_data_combain);
			}
			sourceQuery = query(prop.getProperty("storedProcedureList"));
			int columnCount = sourceQuery.getMetaData().getColumnCount();
			while (sourceQuery.next()) {
				String sourceDataCombain = "";
				for (int i = 1; i <= columnCount; i++) {
					String perCellData = sourceQuery.getObject(i).toString();
					String replaceAllLine = perCellData.replaceAll("[\t\n]+", " ");
					String replaceAllSpace = replaceAllLine.replaceAll("\\s{2,}", " ").trim();
					sourceDataCombain = sourceDataCombain + replaceAllSpace + ".";
				}
				dbSPlList.add(sourceDataCombain);
			}
			List<String> dbStoredProcedur = dbSPlList;
			if (reportSPList.size() != dbSPlList.size()) {
				dbStoredProcedur.removeAll(reportSPList);
			}
			Collections.sort(reportSPList);
			Collections.sort(dbSPlList);
			Assert.assertEquals(reportSPList.size(), dbSPlList.size());
			Assert.assertEquals(reportSPList, dbSPlList);
		} catch (Exception getcatch) {
			log.error("No SP found in DataBase OR Query format invalid in SP :: " + getcatch.getMessage());
		}
		log.info("TC 04 Report stored procedure matches source validation ended....................");
	}

	/**
	 * This method is to validating the report generated StoredProcedure count matches with source
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc06_IsReportCaptureStoredProcedureCount() throws Exception {
		log.info("TC 06 Report capture stored procedure count validation started....................");
		establishDatabaseconnection();
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP379_LinuxMSSQLStoredProcedure.properties");
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("reportSPCount"));
		listOfText = listString();
		int storedProcedureCount = 0;
		for (int i = 0; i < listOfText.size(); i++) {
			int ProcedureCount = Integer.parseInt(listOfText.get(i));
			storedProcedureCount = storedProcedureCount + ProcedureCount;
		}
		sourceQuery = query(prop.getProperty("storedProcedureCount"));
		int dbStoredProcedureCount=0;
		while (sourceQuery.next()) {
			dbStoredProcedureCount=dbStoredProcedureCount+Integer.parseInt(String.valueOf(sourceQuery.getObject(2)));
		}
		Assert.assertEquals(storedProcedureCount, dbStoredProcedureCount);
		log.info("TC 06 Report capture stored procedure count validation ended....................");
	}
	

}
