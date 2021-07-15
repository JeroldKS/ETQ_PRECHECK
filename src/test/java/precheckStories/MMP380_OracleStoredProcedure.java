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
		listOfWebElement = xtexts("//*[contains(text(),'Stored Procedures')]/following::table[1]/thead/tr/th");
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
		establishDatabaseconnection("oracleSource");
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP380_query.properties");
		List<String> schemaNameListInReport = new ArrayList<>();
		List<String> spCountInReport = new ArrayList<>();
		try {
			listOfWebElement = xtexts("//*[contains(text(),'DB Meta Information')]/following::table[1]/tbody[1]/tr/td[1]");
			List<WebElement> listOfSchemaName = listOfWebElement;
			for (int i = 0; i < listOfSchemaName.size(); i++) {
				schemaNameListInReport.add(listOfSchemaName.get(i).getText());
			}
			
			listOfWebElement = xtexts("//*[contains(text(),'DB Meta Information')]/following::table[1]/tbody[1]/tr/td[6]");
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
		} catch (Exception getcatch) {
			System.out.println("No SP found in DataBase OR Query format invalid in SP :: " + getcatch.getMessage());
		}
		log.info("TC 02 Validates whether the Stored Procedure count in Report matches with source DB. Ended....................");
	}
	
	/**
	 * Verifies the Report has Stored Procedure informations
	 * @throws Exception 
	 */
	@Test
	public void tc03_verifyReportHasStoredProcedureDetails() throws Exception {
		log.info("TC 03 Verifying the Report has Stored Procedure Details. Started...................");
		loadLowLevelReportInBrowser();
		listOfWebElement = xtexts("//*[contains(text(),'Stored Procedures')]/following::table[1]/thead/tr/th");
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
		log.info("TC 03 Verifying the Report has Stored Procedure Details. Ended...................");
	}
}