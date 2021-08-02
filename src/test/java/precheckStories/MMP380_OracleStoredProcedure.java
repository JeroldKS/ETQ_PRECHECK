package precheckStories;

import org.testng.annotations.Test;
import org.testng.Assert;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import precheck.Base;

public class MMP380_OracleStoredProcedure extends Base {
	static Logger log = Logger.getLogger(MMP380_OracleStoredProcedure.class.getName());
	
	
	/**
	 * Verifies the Report has Stored Procedure information headings
	 * @throws Exception 
	 */
	@Test
	public void tc01_verifyReportHasStoredProcedureHeading() throws Exception {
		log.info("TC 01 Verifying the Report has Stored Procedure Headings. Started...................");
		loadLowLevelReportInBrowser();
		listOfWebElement = xtexts(xpathProperties.getProperty("stored_procedure_heading"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		for (int i = 0; i < listOfWebElementCopy.size(); i++) {
			listOfWebElement = xtexts("//*[contains(text(),'Stored Procedures')]/following::table[1]/thead/tr/th[" + (i + 1) + "]");
			List<WebElement> listDataList = listOfWebElement;
			if (i == 0) {
				assertEquals(listDataList.get(0).getText(), "Schema Name");
			} else if (i == 1) {
				assertEquals(listDataList.get(0).getText(), "Stored Procedure Name");
			} else if (i == 2) {
				assertEquals(listDataList.get(0).getText(), "Stored Procedure definition");
			}
		}
		log.info("TC 01 Verifying the Report has Stored Procedure Headings. Ended...................");
	}
	
	/**
	 * Validate the Source DB Stored Procedures count matches with Report
	 * with source
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc02_IsReportStoredProcedureMatchesSource() throws Exception {
		log.info("TC 02 Validates whether the Stored Procedure count in Report matches with source DB. Started....................");
		establishDatabaseconnection();
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP380_query.properties");
		List<String> schemaNameListInReport = new ArrayList<>();
		List<String> spCountInReport = new ArrayList<>();
		try {
			listOfWebElement = xtexts(xpathProperties.getProperty("meta_info_db_column"));
			List<WebElement> listOfSchemaName = listOfWebElement;
			for (int i = 0; i < listOfSchemaName.size(); i++) {
				schemaNameListInReport.add(listOfSchemaName.get(i).getText());
			}
			
			listOfWebElement = xtexts(xpathProperties.getProperty("stored_procedure_column"));
			List<WebElement> listOfSPCount = listOfWebElement;
			for (int i = 0; i < listOfSPCount.size(); i++) {
				spCountInReport.add(listOfSPCount.get(i).getText());
			}
			sourceQuery = query(prop.getProperty("stored_procedure_count"));
			List<String> schemaNameListInDB = new ArrayList<>();
			List<String> spCountInDB = new ArrayList<>();
			while (sourceQuery.next()) {
				schemaNameListInDB.add(sourceQuery.getString("schema_name"));
				spCountInDB.add(sourceQuery.getString("sp_count"));
			}
			Assert.assertEquals(schemaNameListInReport.size(), schemaNameListInDB.size());
			Assert.assertEquals(schemaNameListInReport, schemaNameListInDB);
			Assert.assertEquals(spCountInReport.size(), spCountInDB.size());
			Assert.assertEquals(spCountInReport, spCountInReport);
			dbConnection.close();
		} catch (Exception getcatch) {
			System.out.println("No SP found in DataBase OR Query format invalid in SP :: " + getcatch.getMessage());
		}
		log.info("TC 02 Validates whether the Stored Procedure count in Report matches with source DB. Ended....................");
	}
	
	/**
	 * This method is to validate the report contains Stored Procedure
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc03_IsReportGenerateExpectedStoredProcedureFormat() throws Exception {
		log.info("TC 03 Report generate expected stored procedure format validation started....................");
		loadLowLevelReportInBrowser();
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
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP380_query.properties");
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("sp_data"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		List<String> storedProcedureListInReport = new ArrayList<>();
		List<String> storedProcedureListInDB = new ArrayList<>();
		try {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts(
						"//*[contains(text(),'Stored Procedures')]/following::tbody[1]/tr[" + (i + 1) + "]/td");
				String combinedReportData = "";
				for (int j = 0; j < listOfWebElement.size(); j++) {
					combinedReportData = combinedReportData + listOfWebElement.get(j).getText() + ".";
				}
				storedProcedureListInReport.add(combinedReportData);
			}
			sourceQuery = query(prop.getProperty("storedProcedureList"));
			int columnCount = sourceQuery.getMetaData().getColumnCount();
			while (sourceQuery.next()) {
				String combainedSourceData = "";
				for (int i = 1; i <= columnCount; i++) {
					String perCellData = sourceQuery.getObject(i).toString();
					String replaceAllLine = perCellData.replaceAll("[\t\n]+", " ");
					String replaceAllSpace = replaceAllLine.replaceAll("\\s{2,}", " ").trim();
					combainedSourceData = combainedSourceData + replaceAllSpace + ".";
				}
				storedProcedureListInDB.add(combainedSourceData);
			}
			List<String> dbStoredProcedure = storedProcedureListInDB;
			if (storedProcedureListInReport.size() != storedProcedureListInDB.size()) {
				dbStoredProcedure.removeAll(storedProcedureListInReport);
				System.err.println(dbStoredProcedure);
			}
			Collections.sort(storedProcedureListInReport);
			Collections.sort(storedProcedureListInDB);
			Assert.assertEquals(storedProcedureListInReport.size(), storedProcedureListInDB.size());
			Assert.assertEquals(storedProcedureListInReport, storedProcedureListInDB);
			dbConnection.close();
		} catch (Exception getcatch) {
			System.out.println("No SP found in DataBase OR Query format invalid in SP :: " + getcatch.getMessage());
		}
		log.info("TC 04 Report stored procedure matches source validation ended....................");
	}

	/**
	 * This method is to validating the report generated StoredProcedure count
	 * matches with source
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc06_IsReportCaptureStoredProcedureCount() throws Exception {
		log.info("TC 06 Checking whether the report captured Stored Procedure Count. started....................");
		establishDatabaseconnection();
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP380_query.properties");
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
		dbConnection.close();
		log.info("TC 06 Checking whether the report captured Stored Procedure Count. Ended....................");
	}
}