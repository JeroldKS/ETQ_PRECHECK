package precheckStories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP382_MSSQLDatabaseCheck extends Base {

	static Logger log = Logger.getLogger(MMP382_MSSQLDatabaseCheck.class.getName());

	/**
	 * To verify DB Meta Information validation
	 * @throws Exception
	 */
	@Test
	public void tc01_IsDatabaseMetaInformationIncludesAllDetails() throws Exception {
		log.info("TC 01 DB Meta Info validation started....................");
		establishDatabaseconnection();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP382_MSSQLDatabaseCheck.properties");
		ArrayList<String> checkList = new ArrayList<>();
		ArrayList<String> sourecColumnName = new ArrayList<>();
		String[] checkListArray = new String[] { "schema_name", "table_count", "column_count", "view_count",
				"function_count", "sp_count", "trigger_count", "record_count", "max_index" };
		for (int i = 0; i < checkListArray.length; i++) {
			checkList.add(checkListArray[i]);
		}
		sourceQuery = query(prop.getProperty("metaData"));
		int metacolumnCount = sourceQuery.getMetaData().getColumnCount();
		for (int i = 1; i <= metacolumnCount; i++) {
			String columnClassName = sourceQuery.getMetaData().getColumnName(i);
			sourecColumnName.add(columnClassName.toString());
		}
		ArrayList<String> copyCheckList = new ArrayList<>();
		copyCheckList.addAll(checkList);
		copyCheckList.removeAll(sourecColumnName);
		Assert.assertTrue(sourecColumnName.containsAll(checkList), copyCheckList + " = This column is not reported");
	}

	/**
	 * To verify Mssql data types fetched are supports or unsupports for Mysql
	 * @throws Exception
	 */
	@Test
	public void tc02_IsMssqlDataTypesFetchedAreSupportedOrUnsupportedForMysql() throws Exception {
		log.info(
				"TC 02 Mssql data types fetched are supported or unsupported for Mysql validation started....................");
		establishDatabaseconnection();
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP382_MSSQLDatabaseCheck.properties");
		String[] supportedDataTypes = { "GEOMETRY", "POINT", "LINESTRING", "POLYGON", "GEOMETRYCOLLECTION",
				"MULTILINESTRING", "MULTIPOINT", "MULTIPOLYGON", "DATE", "TIME", "DATETIME", "TIMESTAMP", "YEAR",
				"CHAR", "VARCHAR", "BINARY", "VARBINARY", "TINYBLOB", "BLOB", "MEDIUMBLOB", "LONGBLOB", "TINYTET",
				"TEXT", "MEDIUMTEXT", "LONGTEXT", "ENUM", "SET", "TINYINT", "SMALLINT", "MEDIUMINT", "INT", "BIGINT",
				"DECIMAL", "FLOAT", "DOUBLE", "BIT" };
		ArrayList<String> dbDataTypes = new ArrayList<>();
		sourceQuery = query(prop.getProperty("dataTypes"));
		while (sourceQuery.next()) {
			dbDataTypes.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("dataTypesList"));
		listOfText = listString();
		Collections.sort(dbDataTypes);
		Collections.sort(listOfText);
		Assert.assertEquals(listOfText, dbDataTypes);
		Arrays.toString(supportedDataTypes);
		List<String> supportedDataTypesList = new ArrayList<String>();
		for (String lang : supportedDataTypes) {
			supportedDataTypesList.add(lang.toLowerCase());
		}
		if (dbDataTypes.containsAll(supportedDataTypesList) == false) {
			dbDataTypes.removeAll(supportedDataTypesList);
			log.info("While migration need to convert the following datatypes = " + dbDataTypes);
		}
		log.info(
				"TC 02 Mssql data types fetched are supported or unsupported for Mysql validation ended....................");
	}

	/**
	 * To verify Indexes created in Mssql is less than 64
	 * @throws Exception
	 */
	@Test
	public void tc03_IsIndexesCreatedInMssqlIsLessThan64() throws Exception {
		log.info("TC 03 Indexes created in Mssql is less than 64 validation started....................");
		establishDatabaseconnection();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP382_MSSQLDatabaseCheck.properties");
		sourceQuery = query(prop.getProperty("indexCount"));
		ArrayList<String> identifiers = new ArrayList<>();
		while (sourceQuery.next()) {

			identifiers.add(String.valueOf(sourceQuery.getObject(1)) + "." + String.valueOf(sourceQuery.getObject(2))
					+ "." + String.valueOf(sourceQuery.getObject(3)));
		}
		if (identifiers.size() != 0) {
			for (int i = 0; i < identifiers.size(); i++) {
				log.error("Inedx created in MSSQL is grater than 64 = " + identifiers.get(i));
			}
		} else {
			log.info("Index created in MSSQL is less than 64 :: Good to Migrate");
		}
		Assert.assertEquals(identifiers.size(), 0, "Row count of index created in MSSQL is grater than 64 : ");
		log.info("TC 03 Indexes created in Mssql is less than 64 validation ended....................");
	}

	/**
	 * to verify Indexes created in Mssql is greater than 64
	 * @throws Exception
	 */
	@Test
	public void tc04_IsIndexCountGreaterThan64() throws Exception {
		log.info("TC 04 Indexes created in Mssql is greater than 64 validation started....................");
		establishDatabaseconnection();
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP382_MSSQLDatabaseCheck.properties");
		sourceQuery = query(prop.getProperty("indexCount"));
		ArrayList<String> indexCount = new ArrayList<>();
		ArrayList<String> reportIndex = new ArrayList<>();
		while (sourceQuery.next()) {
			indexCount.add(String.valueOf(sourceQuery.getObject(1)) + "." + String.valueOf(sourceQuery.getObject(2))
					+ "." + String.valueOf(sourceQuery.getObject(3)));
		}
		if (indexCount.size() != 0) {
			Assert.assertNotEquals(indexCount.size(), 0, "Row count of index created in MSSQL is grater than 64 : ");
			List<WebElement> reportIndexRow = xtexts(xpathProperties.getProperty("reportIndexCount"));
			for (int i = 0; i < reportIndexRow.size(); i++) {
				listOfWebElement = xtexts(
						"//*[contains(text(),'Index Information (>64)')]/following::tbody[1]/tr[" + (i + 1) + "]/td");
				listOfText = listString();
				String combainIndexColumn = "";
				for (int j = 0; j < listOfText.size(); j++) {
					if (listOfText.size() - 1 != j) {
						combainIndexColumn = combainIndexColumn + listOfText.get(j) + ".";
					} else {
						combainIndexColumn = combainIndexColumn + listOfText.get(j);
					}
				}
				reportIndex.add(combainIndexColumn);
			}
			Collections.sort(indexCount);
			Collections.sort(reportIndex);
			Assert.assertEquals(reportIndex, indexCount);

			for (int i = 0; i < indexCount.size(); i++) {
				log.info("Inedx created in MSSQL DB is grater than 64 = " + indexCount.get(i));
			}
		} else {
			log.info("In this case for negative scenario any index created in MSSQL is should be grater than 64");
		}
		log.info("TC 04 Indexes created in Mssql is greater than 64 validation ended....................");
	}

	/**
	 * To verify Identifiers created in Mssql is less than 64
	 * @throws Exception
	 */
	@Test
	public void tc05_IsIdentifiersCreatedInMssqlIsLessThan64() throws Exception {
		log.info("TC 05 Identifiers created in Mssql is less than 64 validation started....................");
		establishDatabaseconnection();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP382_MSSQLDatabaseCheck.properties");
		sourceQuery = query(prop.getProperty("identifiers"));
		ArrayList<String> identifiers = new ArrayList<>();
		while (sourceQuery.next()) {
			identifiers.add(String.valueOf(sourceQuery.getObject(1)) + "." + String.valueOf(sourceQuery.getObject(2))
					+ "." + String.valueOf(sourceQuery.getObject(3)));
		}
		if (identifiers.size() != 0) {
			System.out.println(identifiers.size());
			for (int i = 0; i < identifiers.size(); i++) {
				log.error("Identifiers created in MSSQL is grater than 64 = " + identifiers.get(i));
			}
		} else {
			log.info("Identifiers created in MSSQL is less than 64 :: Good to Migrate");
		}
		Assert.assertEquals(identifiers.size(), 0, "Row count of identifiers created in MSSQL is grater than 64 : ");
		log.info("TC 05 Identifiers created in Mssql is less than 64 validation ended....................");
	}

	/**
	 * To verify Maximum identifiers are listed greater than 64
	 * @throws Exception
	 */
	@Test
	public void tc06_IsMaximumIdentifiersAreListedGreaterThan64() throws Exception {
		log.info("TC 06 Maximum identifiers are listed greater than 64 validation started....................");
		establishDatabaseconnection();
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP382_MSSQLDatabaseCheck.properties");
		sourceQuery = query(prop.getProperty("identifiers"));
		ArrayList<String> identifiers = new ArrayList<>();
		ArrayList<String> reportIdentifiers = new ArrayList<>();
		while (sourceQuery.next()) {
			try {
				identifiers.add(sourceQuery.getObject(1).toString() + "." + sourceQuery.getObject(2).toString() + "."
						+ sourceQuery.getObject(3).toString());
			} catch (NullPointerException e) {
				String nullIdentifiers = "";
				try {
					if (sourceQuery.getObject(1).toString() != null) {
						nullIdentifiers = sourceQuery.getObject(1).toString() + ".";
					}
				} catch (NullPointerException e2) {
					nullIdentifiers = nullIdentifiers + "None" + ".";
				}
				try {
					if (sourceQuery.getObject(2).toString() != null) {
						nullIdentifiers = nullIdentifiers + sourceQuery.getObject(2).toString() + ".";
					}
				} catch (NullPointerException e2) {
					nullIdentifiers = nullIdentifiers + "None" + ".";
				}
				try {
					if (sourceQuery.getObject(3).toString() != null) {
						nullIdentifiers = nullIdentifiers + sourceQuery.getObject(3).toString();
					}
				} catch (NullPointerException e2) {
					nullIdentifiers = nullIdentifiers + "None";
				}
				identifiers.add(nullIdentifiers);
			}
		}
		if (identifiers.size() != 0) {
			Assert.assertNotEquals(identifiers.size(), 0,
					"Row count of identifiers created in MSSQL is grater than 64 : ");
			List<WebElement> reportIdentifiersRow = xtexts(xpathProperties.getProperty("reportIdentifiersrow"));
			for (int i = 0; i < reportIdentifiersRow.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'DB Maximum Identifiers (>64)')]/following::tbody[1]/tr["
						+ (i + 1) + "]/td");
				listOfText = listString();
				String combainIdentifiersColumn = "";
				for (int j = 0; j < listOfText.size(); j++) {
					if (listOfText.size() - 1 != j) {
						combainIdentifiersColumn = combainIdentifiersColumn + listOfText.get(j) + ".";
					} else {
						combainIdentifiersColumn = combainIdentifiersColumn + listOfText.get(j);
					}
				}
				reportIdentifiers.add(combainIdentifiersColumn);
			}
			Collections.sort(identifiers);
			Collections.sort(reportIdentifiers);
			Assert.assertEquals(reportIdentifiers, identifiers);
			for (int i = 0; i < identifiers.size(); i++) {
				log.info("Identifiers created in MSSQL DB is grater than 64 = " + identifiers.get(i));
			}
		} else {
			log.info("In this case for negative scenario any identifiers created in MSSQL is should be grater than 64");
		}
		log.info("TC 06 Maximum identifiers are listed greater than 64 validation ended....................");
	}

	/**
	 * To verify DataBase check
	 * @throws Exception
	 */
	@Test
	public void tc07_DataBaseChecks() throws Exception {
		log.info("TC 07 DataBase check validation started....................");
		establishDatabaseconnection();
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP382_MSSQLDatabaseCheck.properties");
		// Total utilized size of the database
		sourceQuery = query(prop.getProperty("dbUtilizedSize"));
		sourceQuery.next();
		text = xtext(xpathProperties.getProperty("dbUtilizedSize"));
		Assert.assertEquals(text, sourceQuery.getObject(2).toString(),
				"Total utilized size of the database is mismatch : ");
		// Core schema size of the database
		sourceQuery = query(prop.getProperty("coreSchemaSize"));
		sourceQuery.next();
		text = xtext(xpathProperties.getProperty("coreSchemaSize"));
		Assert.assertEquals(text, sourceQuery.getObject(2).toString(),
				"Core schema size of the database is mismatch : ");
		// Non-Core schema size of the database
		sourceQuery = query(prop.getProperty("NoncoreSchema"));
		sourceQuery.next();
		text = xtext(xpathProperties.getProperty("NoncoreSchema"));
		Assert.assertEquals(text, sourceQuery.getObject(2).toString(),
				"Non-Core schema size of the database is mismatch : ");
		// DB Version
		sourceQuery = query(prop.getProperty("dbVersion"));
		sourceQuery.next();
		String perCellData = sourceQuery.getObject(1).toString();
		String replaceAllLine = perCellData.replaceAll("[\t\n]+", " ");
		String replaceAllSpace = replaceAllLine.replaceAll("\\s{2,}", " ").trim();
		text = xtext(xpathProperties.getProperty("dbVersion"));
		Assert.assertEquals(text, replaceAllSpace, "DB Version of the database is mismatch : ");
		// Engine version of the database
		sourceQuery = query(prop.getProperty("engineVersion"));
		sourceQuery.next();
		text = xtext(xpathProperties.getProperty("engineVersion"));
		Assert.assertEquals(text, sourceQuery.getObject(1).toString(), "Engine version of the database is mismatch : ");
		// Database admin privilege validation
		sourceQuery = query(prop.getProperty("privilegeValidation"));
		sourceQuery.next();
		String privilegeValidation = "";
		if (sourceQuery.getObject(1).toString().equals("1")) {
			privilegeValidation = "Successful";
		} else {
			privilegeValidation = "Failed";
		}
		text = xtext(xpathProperties.getProperty("privilegeValidation"));
		Assert.assertEquals(text.trim(), privilegeValidation, "Database user has not have admin privilege : ");
		log.info("TC 07 DataBase check validation ended....................");
	}

	/**
	 * To verify Report captured all checks
	 * @throws Exception
	 */
	@Test
	public void tc08_IsReportCapturedAllChecks() throws Exception {
		log.info("TC 08 Report captured all checks validation started....................");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("reportCapturedAllChecks"));
		listOfText = listString();
		String[] reportCapturedAllChecks = { "DB Meta Information", "Datatypes Present in Current DB",
				"Index Information (>64)", "DB Maximum Identifiers (>64)", "DB Materialized views(Only Oracle)",
				"DB Package List(Only Oracle)", "Database Checks" };
		for (int i = 0; i < reportCapturedAllChecks.length; i++) {
			if (!reportCapturedAllChecks[i].equals("Database Checks")) {
				Assert.assertTrue(listOfText.contains(reportCapturedAllChecks[i]));
			} else {
				List<WebElement> dbChecks = driver.findElements(By.xpath(xpathProperties.getProperty("dbChecks")));
				ArrayList<String> reportDBChecksList = new ArrayList<>();
				for (int j = 0; j < dbChecks.size(); j++) {
					reportDBChecksList.add(dbChecks.get(j).getText().toString());
				}
				String[] dbChecksList = { "Utilized DB size", "Core Schema's size", "Noncore Schema's size",
						"Database Version", "Engine Version", "DB Privileges Validation" };
				for (int j = 0; j < dbChecksList.length; j++) {
					Assert.assertTrue(reportDBChecksList.contains(dbChecksList[j]));
				}

			}
		}
		log.info("TC 08 Report captured all checks validation ended....................");
	}

}
