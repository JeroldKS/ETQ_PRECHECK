package precheckStories;

import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openqa.selenium.WebElement;
import precheck.Base;

public class MMP378_MySQLStoredProcedure extends Base {
	
/**
 * This method is for Report Headings validation
 * @throws Exception
 */
	@Test
	public void tc01_ReportHeadingsvalidation() throws Exception {
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP378_MySQLStoredProcedure.properties");
		xpathProperties = loadXpathFile();

		listOfWebElement = xtexts(xpathProperties.getProperty("heading_list"));
		listOfText = listString();
		System.out.println(listOfText);
		String[] checkList = { "Schema Name", "Stored Procedure Name", "Stored Procedure definition" };
		for (int i = 0; i < checkList.length; i++) {
			Assert.assertTrue(listOfText.contains(checkList[i]));
		}

	}
/**
 * This method is to validating the report is generated StoredProcedure Count
 * @throws Exception
 */
	@Test
	public void tc02_IsStoredProcedureCountNotNull() throws Exception {
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP378_MySQLStoredProcedure.properties");
		xpathProperties = loadXpathFile();

		listOfWebElement = xtexts(xpathProperties.getProperty("sp_count"));
		listOfText = listString();
		int storedProcedureCount = 0;
		for (int i = 0; i < listOfText.size(); i++) {
			int ProcedureCount = Integer.parseInt(listOfText.get(i));
			storedProcedureCount = storedProcedureCount + ProcedureCount;
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("sp_list"));
		listOfText = listString();
		Assert.assertEquals(listOfText.size(), storedProcedureCount);
		Assert.assertNotNull(storedProcedureCount);
	}
/**
 * This method is to validating the report is generated StoredProcedure
 * @throws Exception 
 */
	@Test
	public void tc03_IsReportGenerateStoredProcedure() throws Exception {
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP378_MySQLStoredProcedure.properties");
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("sp_data_list"));
		List<WebElement> spDataList = listOfWebElement;
		try {
			for (int i = 0; i < spDataList.size(); i++) {
				listOfWebElement = xtexts(xpathProperties.getProperty("sp_each_data_list"));
				for (int j = 0; j < listOfWebElement.size(); j++) {
					text = xtext("//*[contains(text(),'Stored Procedures')]/following::tbody[1]/tr[" + (i + 1) + "]/td["
							+ (j + 1) + "]");
					Assert.assertNotNull(text);
				}
			}
		} catch (Exception getcatch) {
			System.out.println("No SP found in DataBase :: " + getcatch.getMessage());
		}
	}
	/**
	 * This method is to validating the report generated StoredProcedure matches with source
	 * @throws Exception 
	 */
	@Test
	public void tc04_IsReportStoredProcedureMatchesSource() throws Exception {
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP378_MySQLStoredProcedure.properties");
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("sp_data"));
		List<WebElement> dup_texts = listOfWebElement;
		List<String> reportSPList = new ArrayList<>();
		List<String> dbSPlList = new ArrayList<>();
		try {
		for (int i = 0; i < dup_texts.size(); i++) {
			listOfWebElement = xtexts("//*[contains(text(),'Stored Procedures')]/following::tbody[1]/tr["+(i+1)+"]/td");
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
				sourceDataCombain = sourceDataCombain + replaceAllSpace+ ".";
			}
			dbSPlList.add(sourceDataCombain);
		}
		List<String> dbStoredProcedur = dbSPlList;
		if (reportSPList.size() != dbSPlList.size()) {
			dbStoredProcedur.removeAll(reportSPList);
			System.err.println(dbStoredProcedur);
		}
		Collections.sort(reportSPList);
		Collections.sort(dbSPlList);
		Assert.assertEquals(reportSPList.size(), dbSPlList.size());
		Assert.assertEquals(reportSPList, dbSPlList);

	}catch (Exception getcatch) {
		System.out.println("No SP found in DataBase OR Query format invalid in SP :: " + getcatch.getMessage());
	}
	}

	
/**
 * This method is to validating the report generated StoredProcedure count matches with source
 * @throws Exception 
 */
	@Test
	public void tc06_CountOfStoredProcedure() throws Exception {
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP378_MySQLStoredProcedure.properties");
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("sp_count"));
		listOfText = listString();
		int storedProcedureCount = 0;
		for (int i = 0; i < listOfText.size(); i++) {
			int ProcedureCount = Integer.parseInt(listOfText.get(i));
			storedProcedureCount = storedProcedureCount + ProcedureCount;
		}
		sourceQuery = query(prop.getProperty("storedProcedureCount"));
		sourceQuery.next();
		String dbSPCount = sourceQuery.getObject(1).toString();
		int dataBaseCount = Integer.parseInt(dbSPCount);
		Assert.assertEquals(storedProcedureCount, dataBaseCount);
	}

}
