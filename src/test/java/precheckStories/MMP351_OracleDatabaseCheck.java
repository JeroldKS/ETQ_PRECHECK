package precheckStories;

import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import precheck.Base;

public class MMP351_OracleDatabaseCheck extends Base {
	static Logger log = Logger.getLogger(MMP351_OracleDatabaseCheck.class.getName());
	
	
	/**
	 * Verifies the meta information captured in report
	 * @throws Exception 
	 */
	@Test
	public void tc01_DBMetaInfo() throws Exception {
		log.info("TC 01 DB Meta Info validation started....................");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("oracleSource");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP351_query.properties");
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts("//*[contains(text(),'DB Meta Information')]/following::tbody[1]/tr");
		List<WebElement> listOfText = listOfWebElement;
		List<String> reportMetaInfoList = new ArrayList<>();
		List<String> dbMetaInfoIndexList = new ArrayList<>();
		for (int i = 0; i < listOfText.size(); i++) {
			listOfWebElement = xtexts("//*[contains(text(),'DB Meta Information')]/following::tbody[1]/tr["+( i+1 )+"]/td");
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
				sourceCombainedData = sourceCombainedData + String.valueOf(sourceQuery.getObject(i)) + ".";
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
		dbConnection.close();
		log.info("TC 01 DB Meta Info validation ended....................");
	}
	
	/**
	 * check the incompatible data types captured in report
	 * @throws Exception
	 */
	@Test
	public void tc02_checkDataTypes() throws Exception {
		log.info("TC 02 Check for Unsupported Data types. Started...");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("oracleSource");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP351_query.properties");
		xpathProperties = loadXpathFile();
		List<String> dataTypeInReport = new ArrayList<>();
		List<String> dataTypeInDB = new ArrayList<>();
		listOfWebElement = xtexts("//*[contains(text(),'Datatypes Present in Current DB')]/following::table[1]/tbody[1]/tr");
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		for(int i = 0; i < listOfWebElementCopy.size(); i++) {
			dataTypeInReport.add(listOfWebElementCopy.get(i).getText());
		}
		
		sourceQuery = query(prop.getProperty("get_all_datatypes"));
		while (sourceQuery.next()) {
			dataTypeInDB.add(sourceQuery.getObject(1).toString());
		}
		
		Collections.sort(dataTypeInReport);
		Collections.sort(dataTypeInDB);
		Assert.assertEquals(dataTypeInReport.size(), dataTypeInDB.size());
		Assert.assertEquals(dataTypeInReport, dataTypeInDB);
		dbConnection.close();
		log.info("TC 02 Check for Unsupported Data types. Ended...");
	}
	
	/**
	 * Verify the table/schemas index count should be less than 64
	 * @throws Exception
	 */
	@Test
	public void tc03_verifyIndexCountLessThan64() throws Exception {
		log.info("TC 03 Verify all the table/schema should not have index count >64. Started...");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("oracleSource");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP351_query.properties");
		List<String> indexesInDB = new ArrayList<>();
		sourceQuery = query(prop.getProperty("get_index_count"));
		while (sourceQuery.next()) {
			indexesInDB.add(sourceQuery.getObject(1).toString());
		}
		String result = indexesInDB.size() > 0 ? indexesInDB.size()+" Table/Schemas has > 64 index " : "No Table/Schemas has > 64 index";
		Assert.assertEquals(result, "No Table/Schemas has > 64 index");
		dbConnection.close();
		log.info("TC 03 Verify all the table/schema should not have index count >64. Ended...");
	}
	
	/**
	 * Verify the tables and schemas which has greater than 64 indexes captured in report
	 * @throws Exception
	 */
	@Test
	public void tc04_verifyIndexInformationGreaterThan64() throws Exception {
		log.info("TC 04 Verify the table/schema index count >64 captured in report. Started...");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("oracleSource");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP351_query.properties");
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts("//*[contains(text(),'Index Information (>64)')]/following::tbody[1]/tr");
		List<WebElement> listOfText = listOfWebElement;
		List<String> tableSchemaListInReport = new ArrayList<>();
		List<String> tableSchemaListInDB = new ArrayList<>();
		
		sourceQuery = query(prop.getProperty("get_index_info"));
		int columnCount = sourceQuery.getMetaData().getColumnCount();
		while (sourceQuery.next()) {
			String sourceCombainedData = "";
			for (int i = 1; i <= columnCount; i++) {
				sourceCombainedData = sourceCombainedData + String.valueOf(sourceQuery.getObject(i)) + ".";
			}
			tableSchemaListInDB.add(sourceCombainedData);
		}
		if(tableSchemaListInDB.size() > 0) {
			for (int i = 0; i < listOfText.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'Index Information (>64)')]/following::tbody[1]/tr["+( i+1 )+"]/td");
				String reportCombinedData = "";
				for (int j = 0; j < listOfWebElement.size(); j++) {
					reportCombinedData = reportCombinedData + listOfWebElement.get(j).getText() + ".";
					
				}
				tableSchemaListInReport.add(reportCombinedData);
			}
			
			Collections.sort(tableSchemaListInReport);
			Collections.sort(tableSchemaListInDB);
			Assert.assertEquals(tableSchemaListInReport.size(), tableSchemaListInDB.size());
			Assert.assertEquals(tableSchemaListInReport, tableSchemaListInDB);
			
			List<String> tableSchemaListCopy = tableSchemaListInDB;
			if (tableSchemaListInReport.size() != tableSchemaListInDB.size()) {
				tableSchemaListCopy.removeAll(tableSchemaListInReport);
				log.error("Meta Info not match : " + tableSchemaListCopy);
			}
		} else {
			text = xtext("//*[contains(text(),'Index Information (>64)')]/following::h3[1]");
			Assert.assertEquals(text, "No Maximum Index(>64) found");
		}
		dbConnection.close();
		log.info("TC 04 Verify the table/schema index count >64 captured in report. Ended...");
	}
	
	/**
	 * Check the identifier length should not exceed 64
	 * @throws Exception
	 */
	@Test
	public void tc05_findMaximumIdentifiers() throws Exception {
		log.info("TC 05 Verify maximum length of db identifiers no be >64. Started...");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("oracleSource");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP351_query.properties");
		List<String> identifiersInDB = new ArrayList<>();
		sourceQuery = query(prop.getProperty("get_max_identifiers_count"));
		while (sourceQuery.next()) {
			identifiersInDB.add(sourceQuery.getObject(1).toString());
		}
		String result = identifiersInDB.size() > 0 ? identifiersInDB.size()+" Identifiers has > 64 length " : "No Identifiers has > 64 index";
		Assert.assertEquals(result, "No Identifiers has > 64 index");
		dbConnection.close();
		log.info("TC 05 Verify maximum length of db identifiers no be >64. Ended...");
	}
	
	/**
	 * Check the Identifiers length greater than 64 are captured in report
	 * @throws Exception
	 */
	@Test
	public void tc06_verifyIdentifierLengthGreaterThan64() throws Exception {
		log.info("TC 06 Verify the identifiers length >64 captured in report. Started...");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("oracleSource");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP351_query.properties");
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts("//*[contains(text(),'DB Maximum Identifiers (>64)')]/following::tbody[1]/tr");
		List<WebElement> listOfText = listOfWebElement;
		List<String> identifiersListInReport = new ArrayList<>();
		List<String> identifiersListInDB = new ArrayList<>();
		
		sourceQuery = query(prop.getProperty("get_identifiers_count"));
		int columnCount = sourceQuery.getMetaData().getColumnCount();
		while (sourceQuery.next()) {
			String sourceCombainedData = "";
			for (int i = 1; i <= columnCount; i++) {
				sourceCombainedData = sourceCombainedData + String.valueOf(sourceQuery.getObject(i)) + ".";
			}
			identifiersListInDB.add(sourceCombainedData);
		}
		if(identifiersListInDB.size() > 0) {
			for (int i = 0; i < listOfText.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'DB Maximum Identifiers (>64)')]/following::tbody[1]/tr["+( i+1 )+"]/td");
				String reportCombinedData = "";
				for (int j = 0; j < listOfWebElement.size(); j++) {
					reportCombinedData = reportCombinedData + listOfWebElement.get(j).getText() + ".";
					
				}
				identifiersListInReport.add(reportCombinedData);
			}
			
			Collections.sort(identifiersListInReport);
			Collections.sort(identifiersListInDB);
			Assert.assertEquals(identifiersListInReport.size(), identifiersListInDB.size());
			Assert.assertEquals(identifiersListInReport, identifiersListInDB);
			
			List<String> identifierListCopy = identifiersListInDB;
			if (identifiersListInReport.size() != identifiersListInDB.size()) {
				identifierListCopy.removeAll(identifiersListInReport);
				log.error("Identifiers not match : " + identifierListCopy);
			}
		} else {
			text = xtext("//*[contains(text(),'DB Maximum Identifiers (>64)')]/following::h3[1]");
			Assert.assertEquals(text, "No Data Present");
		}
		dbConnection.close();
		log.info("TC 06 Verify the identifiers length >64 captured in report. Ended...");
	}
	
	/**
	 * Verify Materialized View and Package
	 * @throws Exception
	 */
	@Test
	public void tc07_verifyMaterializedViewAndPackage() throws Exception {
		log.info("TC 07 Verify Materialized views and Packages captured in report. Started...");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("oracleSource");
		
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP351_query.properties");
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts("//*[contains(text(),'DB Materialized views(Oracle)')]/following::tbody[1]/tr");
		List<WebElement> listOfText = listOfWebElement;
		List<String> materializedViewsInReport = new ArrayList<>();
		List<String> materializedViewsInDB = new ArrayList<>();
		
		sourceQuery = query(prop.getProperty("get_materialized_view"));
		int columnCount = sourceQuery.getMetaData().getColumnCount();
		while (sourceQuery.next()) {
			String sourceCombainedData = "";
			for (int i = 1; i <= columnCount; i++) {
				sourceCombainedData = sourceCombainedData + String.valueOf(sourceQuery.getObject(i)) + ".";
			}
			materializedViewsInDB.add(sourceCombainedData);
		}
		if(materializedViewsInDB.size() > 0) {
			for (int i = 0; i < listOfText.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'DB Materialized views(Oracle)')]/following::tbody[1]/tr["+( i+1 )+"]/td");
				String reportCombinedData = "";
				for (int j = 0; j < listOfWebElement.size(); j++) {
					reportCombinedData = reportCombinedData + listOfWebElement.get(j).getText() + ".";
					
				}
				materializedViewsInReport.add(reportCombinedData);
			}
			
			Collections.sort(materializedViewsInReport);
			Collections.sort(materializedViewsInDB);
			Assert.assertEquals(materializedViewsInReport.size(), materializedViewsInDB.size());
			Assert.assertEquals(materializedViewsInReport, materializedViewsInDB);
			
			List<String> materializedViewsCopy = materializedViewsInDB;
			if (materializedViewsInReport.size() != materializedViewsInDB.size()) {
				materializedViewsCopy.removeAll(materializedViewsInReport);
				log.error("Materialized views not match : " + materializedViewsCopy);
			}
		} else {
			text = xtext("//*[contains(text(),'DB Materialized views(Oracle)')]/following::h3[1]");
			Assert.assertEquals(text, "No Data Present");
		}
		
		listOfWebElement = xtexts("//*[contains(text(),'DB Package List(Oracle)')]/following::tbody[1]/tr");
		List<WebElement> listOfElements = listOfWebElement;
		List<String> packagesInReport = new ArrayList<>();
		List<String> packagesInDB = new ArrayList<>();
		
		sourceQuery = query(prop.getProperty("get_packages"));
		int colCount = sourceQuery.getMetaData().getColumnCount();
		while (sourceQuery.next()) {
			String sourceCombainedData = "";
			for (int i = 1; i <= colCount; i++) {
				sourceCombainedData = sourceCombainedData + String.valueOf(sourceQuery.getObject(i)) + ".";
			}
			packagesInDB.add(sourceCombainedData);
		}
		if(packagesInDB.size() > 0) {
			for (int i = 0; i < listOfElements.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'DB Package List(Oracle)')]/following::tbody[1]/tr["+( i+1 )+"]/td");
				String reportCombinedData = "";
				for (int j = 0; j < listOfWebElement.size(); j++) {
					reportCombinedData = reportCombinedData + listOfWebElement.get(j).getText() + ".";
					
				}
				packagesInReport.add(reportCombinedData);
			}
			
			Collections.sort(packagesInReport);
			Collections.sort(packagesInDB);
			Assert.assertEquals(packagesInReport.size(), packagesInDB.size());
			Assert.assertEquals(packagesInReport, packagesInDB);
			
			List<String> packagesCopy = packagesInDB;
			if (packagesInReport.size() != packagesInDB.size()) {
				packagesCopy.removeAll(packagesInReport);
				log.error("Materialized views not match : " + packagesCopy);
			}
		} else {
			text = xtext("//*[contains(text(),'DB Package List(Oracle)')]/following::h3[1]");
			Assert.assertEquals(text, "No Data Present");
		}
		dbConnection.close();
		log.info("TC 07 Verify Materialized views and Packages captured in report. Ended...");
	}
	
	/**
	 * Verifies database parameters
	 * @throws Exception
	 */
	@Test
	public void tc08_dataBaseCheck() throws Exception {
		log.info("TC 08 Database parameters check started....................");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP351_query.properties");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("oracleSource");
		xpathProperties = loadXpathFile();
		
		//Db version
		text = xtext(xpathProperties.getProperty("DB_version"));
		sourceQuery = query(prop.getProperty("DB_version"));
		sourceQuery.next();
		Assert.assertEquals(text, sourceQuery.getObject(1).toString());
		
		//utilized db size
		text = xtext(xpathProperties.getProperty("utilized_DB_size"));
		sourceQuery = query(prop.getProperty("utilized_DB_size"));
		sourceQuery.next();
		Assert.assertEquals(sourceQuery.getObject(2).toString(), text);
		
		//core schema size
		text = xtext(xpathProperties.getProperty("core_schema_size"));
		sourceQuery = query(prop.getProperty("core_schema_size"));
		sourceQuery.next();
		Assert.assertEquals(text, sourceQuery.getObject(2).toString());
		
		//non-core schema size
		text = xtext(xpathProperties.getProperty("non_core_schema_size"));
		sourceQuery = query(prop.getProperty("non_core_schema_size"));
		sourceQuery.next();
		Assert.assertEquals(text, String.valueOf(sourceQuery.getObject(2)));
		
		//Engine version
		text = xtext(xpathProperties.getProperty("engine_version"));
		sourceQuery = query(prop.getProperty("DB_version"));
		sourceQuery.next();
		String engineVersion = String.valueOf(sourceQuery.getObject(1)).split("-")[0].replace("Oracle Database 12c Enterprise Edition Release", "").trim();
		Assert.assertEquals(text, engineVersion);
		dbConnection.close();
		log.info("TC 08 Database parameters check started....................");
	}
	
	/**
	 * Check whether the report captured the sufficient details or not
	 * @throws Exception
	 */
	@Test
	public void tc09_ReportCheck() throws Exception {
		log.info("TC 09 Report check started....................");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection("oracleSource");
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("report_validation"));
		//listOfText = listString();
		String[] checkList = { "DB Meta Information", "Datatypes Present in Current DB", "DB Maximum Identifiers", 
				"Index Information", "DB Materialized views(Oracle)","DB Package List(Oracle)","Utilized DB size",
				"Database Version", "Engine Version", "DB Privileges Validation"};
		for (int i = 0; i < checkList.length; i++) {
			String web = driver.getPageSource().contains(checkList[i]) ? checkList[i]+" Available" : checkList[i]+" Not Available";
			Assert.assertEquals(web, checkList[i]+" Available");
		}
		dbConnection.close();
		log.info("TC 09 Report check ended....................");
	}
}