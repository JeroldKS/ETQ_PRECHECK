package precheckStories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP531_ExternalDatabaseProfiles extends Base {
	
	static Logger log = Logger.getLogger(MMP531_ExternalDatabaseProfiles.class.getName());
	
	/**
	 * Identify External Database Profile query executable
	 * @throws SQLException
	 * @throws Exception
	 */
	@Test
	public static void tc01_identifyExternalDatabaseProfile() throws SQLException, Exception {
		log.info("TC 01 Identifying External Database Profile started..............");
		loadHighLevelReportInBrowser();
		establishDatabaseconnection();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP531_query.properties");
		sourceQuery = query(prop.getProperty("identify_external_db"));
		Assert.assertNotNull(sourceQuery);
		log.info("TC 01 Identifying External Database Profile ended..............");
		dbConnection.close();
	}
	
	/**
	 * Verify the External Database from DB captured in Report
	 * @throws SQLException
	 * @throws Exception
	 */
	@Test
	public static void tc02_verifyExternalDatabaseInReport() throws SQLException, Exception {
		log.info("TC 02 Verifying External Databases captured in Report started..............");
		loadHighLevelReportInBrowser();
		establishDatabaseconnection();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP531_query.properties");
		sourceQuery = query(prop.getProperty("external_db_link"));
		List<String> externalDatabasesInDB = new ArrayList<>();
		List<String> externalDatabasesInReport = new ArrayList<>();
		while (sourceQuery.next()) {
			externalDatabasesInDB.add(sourceQuery.getObject(1).toString());
		}
		Assert.assertNotNull(sourceQuery);
		listOfWebElement = xtexts(xpathProperties.getProperty("external_database_profiles"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(externalDatabasesInDB.size() == 0) {
			Assert.assertEquals("No External Databses found", "External Database available");
		} else {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'External Database Profiles')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					listOfWebElement = xtexts(xpathProperties.getProperty("external_database_profiles_list"));
					for (int j = 0; j < listOfWebElement.size(); j++) {
						text = xtext("//*[contains(text(),'External Database Profiles')]/../td/ul/li[" + (j + 1) + "]");
						externalDatabasesInReport.add(text);
	
					}
					Collections.sort(externalDatabasesInDB);
					Collections.sort(externalDatabasesInReport);
					Assert.assertEquals(externalDatabasesInReport, externalDatabasesInDB);
				}
				if (i == 2) {
					Assert.assertEquals(listDataList.get(0).getText(), "List of the database profiles in the administration center");
				}
			}
		}
		dbConnection.close();
		log.info("TC 02 Verifying External Databases captured in Report ended..............");
	}
	
	/**
	 * Verify if no External Database present, it should not captured in report 
	 * @throws SQLException
	 * @throws Exception
	 */
	@Test
	public static void tc03_verifyNoExternalDatabaseInReport() throws SQLException, Exception {
		log.info("TC 03 Verify If no External Databases captured in Report started..............");
		loadHighLevelReportInBrowser();
		establishDatabaseconnection();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP531_query.properties");
		sourceQuery = query(prop.getProperty("external_db_link"));
		List<String> externalDatabasesInDB = new ArrayList<>();
		while (sourceQuery.next()) {
			externalDatabasesInDB.add(sourceQuery.getObject(1).toString());
		}
		Assert.assertNotNull(sourceQuery);
		listOfWebElement = xtexts("//*[contains(text(),'External Database Profiles')]/../td");
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(externalDatabasesInDB.size() == 0) {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'External Database Profiles')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					Assert.assertEquals(listDataList.get(0).getText(), "N/A");
				}
				if (i == 2) {
					Assert.assertEquals(listDataList.get(0).getText(), "Good to Migrate");
				}
			}
		} else {
			Assert.assertEquals("External Database available", "No External Databses found");
		}
		dbConnection.close();
		log.info("TC 03 Verify If no External Databases captured in Report ended..............");
	}
}
