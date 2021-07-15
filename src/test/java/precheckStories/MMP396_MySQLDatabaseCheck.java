package precheckStories;

import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import precheck.Base;

public class MMP396_MySQLDatabaseCheck extends Base {
	static Logger log = Logger.getLogger(MMP396_MySQLDatabaseCheck.class.getName());
	
	/**
	 * This method is to validate meta info check
	 * @throws Exception 
	 */
	@Test
	public void tc01_DBMetaInfo() throws Exception {
		log.info("TC 01 DB Meta Info validation started....................");
		establishDatabaseconnection("mysqlSource");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP396_query.properties");
		xpathProperties = loadXpathFile();
		loadLowLevelReportInBrowser();
		listOfWebElement = xtexts(xpathProperties.getProperty("mysql_meta_body"));
		List<WebElement> listOfText = listOfWebElement;
		List<String> reportMetaInfoList = new ArrayList<>();
		List<String> dbMetaInfoIndexList = new ArrayList<>();
		for (int i = 0; i < listOfText.size(); i++) {
			listOfWebElement = xtexts("//*[contains(text(),'OverallCount')]/following::tbody[1]/tr["+( i+1 )+"]/td");
			String reportCombinedData = "";
			for (int j = 0; j < listOfWebElement.size(); j++) {
				reportCombinedData = reportCombinedData + listOfWebElement.get(j).getText() + ".";
			}
			reportMetaInfoList.add(reportCombinedData);
		}
		sourceQuery = query(prop.getProperty("mysql_db_meta_info"));
		int columnCount = sourceQuery.getMetaData().getColumnCount();
		while (sourceQuery.next()) {
			String sourceCombainedData = "";
			for (int i = 1; i <= columnCount; i++) {
				sourceCombainedData = sourceCombainedData + sourceQuery.getObject(i).toString() + ".";
			}
			dbMetaInfoIndexList.add(sourceCombainedData);
		}
		
		Collections.sort(reportMetaInfoList);
		Collections.sort(dbMetaInfoIndexList);
		Assert.assertEquals(reportMetaInfoList.size(), dbMetaInfoIndexList.size());
		Assert.assertEquals(reportMetaInfoList, dbMetaInfoIndexList);
		
		List<String> dbMetaInfoIndexListCopy = dbMetaInfoIndexList;
		if (reportMetaInfoList.size() != dbMetaInfoIndexList.size()) {
			dbMetaInfoIndexListCopy.removeAll(reportMetaInfoList);
			log.error("Meta Info not match : " + dbMetaInfoIndexListCopy);
		}
		log.info("TC 01 DB Meta Info validation ended....................");
	}

	/**
	 * This method is to validate non core schema index count
	 * @throws Exception 
	 */
	@Test
	public void tc02_NonCoreSchemaIndexCount() throws Exception {
		log.info("TC 02 Non Core Schema Index Count Started....................");
		establishDatabaseconnection("mysqlSource");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP396_query.properties");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("non_core_body"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		for (int i = 0; i < 5; i++) {
			int generate = Base.generate(listOfWebElementCopy.size());
			listOfWebElement = xtexts("//*[contains(text(),'Index Count - NonCore:')]/following::tbody[1]/tr[" + generate + "]/td");
			String reportTableName = listOfWebElement.get(0).getText();
			String reportIndexCount = listOfWebElement.get(1).getText();
			String reportSchemaName = listOfWebElement.get(2).getText();
			sourceQuery = query("select count(COLUMN_NAME) FROM INFORMATION_SCHEMA.STATISTICS where TABLE_NAME = '"
					+ reportTableName + "' and TABLE_SCHEMA = '" + reportSchemaName + "'");
			sourceQuery.next();
			String dbIndexCount = sourceQuery.getObject(1).toString();
			Assert.assertEquals(reportIndexCount, dbIndexCount,
					"select COUNT(INDEX_NAME) FROM INFORMATION_SCHEMA.STATISTICS where TABLE_NAME = '" + reportTableName
							+ "' and TABLE_SCHEMA = '" + reportSchemaName + "'");
		}
		log.info("TC 02 Non Core Schema Index Count Ended....................");
	}
	
	/**
	 * This method is to validate core schema index count
	 * @throws Exception 
	 */
	@Test
	public void tc03_CoreSchemaIndexCount() throws Exception {
		log.info("TC 03 Core Schema Index Count Started....................");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP396_query.properties");
		establishDatabaseconnection("mysqlSource");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("core_body"));
		List<WebElement> dup_texts = listOfWebElement;
		for (int i = 0; i < 5; i++) {
			int generate = Base.generate(dup_texts.size());
			listOfWebElement = xtexts("//*[contains(text(),'Index Count - Core')]/following::tbody[1]/tr[" + generate + "]/td");
			String reportTableName = listOfWebElement.get(0).getText();
			String reportIndexCount = listOfWebElement.get(1).getText();
			String reportSchemaName = listOfWebElement.get(2).getText();
			sourceQuery = query("select count(COLUMN_NAME) FROM INFORMATION_SCHEMA.STATISTICS where TABLE_NAME = '"
					+ reportTableName + "' and TABLE_SCHEMA = '" + reportSchemaName + "'");
			sourceQuery.next();
			String dbIndexCount = sourceQuery.getObject(1).toString();
			Assert.assertEquals(reportIndexCount, dbIndexCount,
					"select COUNT(INDEX_NAME) FROM INFORMATION_SCHEMA.STATISTICS where TABLE_NAME = '" + reportTableName
							+ "' and TABLE_SCHEMA = '" + reportSchemaName + "'");
		}
		log.info("TC 03 Core Schema Index Count Ended....................");
	}

	/**
	 * This method is for DB
	 * checks(DB_version,DB_User_count,core_schema_size,non_core_schema_size,utilized_DB_size,engine_version)
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc04_DBCheck() throws Exception {
		log.info("TC 04 DB check started....................");
		establishDatabaseconnection("mysqlSource");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP396_query.properties");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		text = xtext(xpathProperties.getProperty("DB_version"));
		sourceQuery = query(prop.getProperty("DB_version"));
		sourceQuery.next();
		Assert.assertEquals(sourceQuery.getObject(1).toString(), text);
		text = xtext(xpathProperties.getProperty("DB_User_count"));
		sourceQuery = query(prop.getProperty("DB_User_count"));
		sourceQuery.next();
		Assert.assertEquals(sourceQuery.getObject(1).toString(), text);
		text = xtext(xpathProperties.getProperty("core_schema_size"));
		sourceQuery = query(prop.getProperty("core_schema_size"));
		sourceQuery.next();
		Assert.assertEquals(sourceQuery.getObject(1).toString() + " GB", text);
		text = xtext(xpathProperties.getProperty("non_core_schema_size"));
		sourceQuery = query(prop.getProperty("non_core_schema_size"));
		sourceQuery.next();
		Assert.assertEquals(sourceQuery.getObject(1).toString() + " GB", text);
		text = xtext(xpathProperties.getProperty("utilized_DB_size"));
		sourceQuery = query(prop.getProperty("utilized_DB_size"));
		sourceQuery.next();
		Assert.assertEquals(sourceQuery.getObject(1).toString() + " GB", text);
		text = xtext(xpathProperties.getProperty("engine_version"));
		sourceQuery = query(prop.getProperty("engine_version"));
		/*while (sourceQuery.next()) {
			System.out.println(text + "  " + sourceQuery.getObject(1).toString());
		}*/
		// assertEquals(sourceQuery.getObject(1).toString(), text);
		log.info("TC 04 DB check Ended....................");

	}

	/**
	 * This method is to validate count and list of database users with low level
	 * report
	 * @throws Exception 
	 */
	@Test
	public void tc05_CountOfDatabaseUsers() throws Exception {
		log.info("TC 05 count and list of Database users started....................");
		establishDatabaseconnection("mysqlSource");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP396_query.properties");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		List<WebElement> userList = driver.findElements(By.xpath(xpathProperties.getProperty("list_db_user")));
		List<String> reportUserList = new ArrayList<>();
		List<String> dbUserList = new ArrayList<>();
		for (int i = 0; i < userList.size(); i++) {
			userList.get(i).getText();
			reportUserList.add(userList.get(i).getText());
		}
		sourceQuery = query(prop.getProperty("DB_user"));
		while (sourceQuery.next()) {
			dbUserList.add(sourceQuery.getObject(1).toString());
		}
		Assert.assertEquals(dbUserList, reportUserList);
		log.info("TC 05 count and list of Database users ended....................");
	}

	/**
	 * This method is to validating the report which present all check list
	 * @throws Exception 
	 */
	@Test
	public void tc06_ReportCheck() throws Exception {
		log.info("TC 06 Report check started....................");
		establishDatabaseconnection("mysqlSource");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP396_query.properties");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("report_validation"));
		listOfText = listString();
		String[] checkList = { "Database Checks", "DB Users", "OverallCount", "Index Count - Core",
				"Index Count - NonCore:" };
		for (int i = 0; i < checkList.length; i++) {
			Assert.assertTrue(listOfText.contains(checkList[i]),checkList[i]+" is not captured in low level report");
		}
		log.info("TC 06 Report check ended....................");
	}
}