package precheckStories;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP392_EmbeddedSQLQueriesAliasHandling extends Base {
	static Logger log = Logger.getLogger(MMP392_EmbeddedSQLQueriesAliasHandling.class.getName());

	/**
	 * This method is to validate id and description keyword match count in source
	 * environment query
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc01_IDAndDescriptionKeywordMatchCount() throws Exception {
		log.info("tc01_ID And Description Keyword Match Count started..............");
		establishDatabaseconnection();
		prop = loadQueryFile(
				"//src//test//resources//precheck//queries//MMP392_EmbeddedSQLQueriesAliasHandling.properties");
		sourceQuery = query(prop.getProperty("lookupSqlQueryList"));
		int lookupSqlQueryCount = 0;
		int keywordFormulaList = 0;
		try {
			while (sourceQuery.next()) {
				lookupSqlQueryCount++;
				String perCellData = sourceQuery.getObject(1).toString();
				String replaceAllLine = perCellData.replaceAll("[\t\n]+", " ");
				String replaceAllSpace = replaceAllLine.replaceAll("\\s{2,}", " ").trim();
				Assert.assertTrue(replaceAllSpace.toLowerCase().contains("lookups."),
						"The query not contains Lookup = " + sourceQuery.getObject(1).toString().contains("lookups."));
				Assert.assertNotNull(replaceAllSpace);
				Assert.assertNotNull(sourceQuery.getObject(2).toString());
			}
		} catch (NullPointerException ex) {
			log.error("Exception occurred while executing the query due to " + ex.getMessage());
		}
		sourceQuery = query(prop.getProperty("keywordFormulaList"));
		try {
			while (sourceQuery.next()) {
				keywordFormulaList++;
				String perCellData = sourceQuery.getObject(1).toString();
				String replaceAllLine = perCellData.replaceAll("[\t\n]+", " ");
				String replaceAllSpace = replaceAllLine.replaceAll("\\s{2,}", " ").trim();
				Assert.assertTrue(replaceAllSpace.toLowerCase().contains("lookups."),
						"The query not contains Lookup = " + sourceQuery.getObject(1).toString().contains("lookups."));
				Assert.assertNotNull(replaceAllSpace);
				Assert.assertNotNull(sourceQuery.getObject(2).toString());
			}
		} catch (NullPointerException ex) {
			log.info("Exception occurred while executing the query due to " + ex.getMessage());
		}
		log.info("LOOKUP_SQL_QUERY have " + lookupSqlQueryCount + " sql queries that refers to the lookup schema");
		log.info("KEYWORD_FORMULA have " + keywordFormulaList + " sql queries that refers to the lookup schema");
		log.info("tc01_ID And Description Keyword Match Count ended................");
	}

	/**
	 * This method is to validate is the report captured query path with primary key
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc04_IsReportCapturedQueryPathWithPrimaryKey() throws Exception {
		log.info("tc04_Report captured Query Path along with the Primary key validation started..............");
		establishDatabaseconnection();
		loadHighLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("queryPathList"));
		listOfText = listString();
		for (int i = 0; i < listOfText.size(); i++) {
			String[] keywordPathSplit = listOfText.get(i).split("[.]");

			sourceQuery = query("select " + keywordPathSplit[2] + " from " + keywordPathSplit[0] + "."
					+ keywordPathSplit[1] + " where  FIELD_ID = '" + keywordPathSplit[3] + "'");
			if (!sourceQuery.next()) {
				Assert.assertTrue(false,
						"Report captured path is invalid format : " + "select " + keywordPathSplit[2] + " from "
								+ keywordPathSplit[0] + "." + keywordPathSplit[1] + " where  FIELD_ID = ' "
								+ keywordPathSplit[3] + " '");
			}
		}
		log.info("tc04_Report captured Query Path along with the Primary key validation ended..............");
	}

	/**
	 * This method is to validate report title captured as excepted "APPENDIX: SQL
	 * Query references to all Lookup Schema"
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc06_ReportTitleCapturedAsExcepted() throws Exception {
		log.info("tc06_report Title captured as excepted validation started..............");
		loadHighLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		text = xtext(xpathProperties.getProperty("appendixReference"));
		Assert.assertEquals(text, "APPENDIX: SQL Query references to all Lookup Schema");
		text = xtext(xpathProperties.getProperty("sqlQueryReference"));
		Assert.assertEquals(text, "SQL Query references to all Lookup Schema");
		log.info("tc06_report Title captured as excepted validation ended................");
	}

	/**
	 * This method is to validate the report captured valid query path
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc07_IsReportCapturedValidQueryPath() throws Exception {
		log.info("tc07_captured Valid Query Path validation started..............");
		establishDatabaseconnection();
		loadHighLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("queryPathList"));
		listOfText = listString();
		for (int i = 0; i < listOfText.size(); i++) {
			String[] keywordPathSplit = listOfText.get(i).split("[.]");

			sourceQuery = query("select " + keywordPathSplit[2] + " from " + keywordPathSplit[0] + "."
					+ keywordPathSplit[1] + " where  FIELD_ID = '" + keywordPathSplit[3] + "'");
			if (!sourceQuery.next()) {
				Assert.assertTrue(false,
						"Report captured path is invalid : " + "select " + keywordPathSplit[2] + " from "
								+ keywordPathSplit[0] + "." + keywordPathSplit[1] + " where  FIELD_ID = ' "
								+ keywordPathSplit[3] + " '");
			} else {
				Assert.assertNotNull(sourceQuery.getObject(1).toString(),
						"Report captured path is invalid : " + "select " + keywordPathSplit[2] + " from "
								+ keywordPathSplit[0] + "." + keywordPathSplit[1] + " where  FIELD_ID = '"
								+ keywordPathSplit[3] + "'");
			}
		}
		log.info("tc07_Report captured Valid Query Path validation ended..............");
	}

	/**
	 * This method is to validate the report query matches with source
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc08_IsReportQueryMatchesWithSource() throws Exception {
		log.info("tc08_Report Query Matches With Source validation started..............");
		establishDatabaseconnection();
		prop = loadQueryFile(
				"//src//test//resources//precheck//queries//MMP392_EmbeddedSQLQueriesAliasHandling.properties");
		ArrayList<Object> sourceQueryList = new ArrayList<>();
		try {
			sourceQuery = query(prop.getProperty("lookupSqlQuery"));
			while (sourceQuery.next()) {
				String perCellData = sourceQuery.getObject(1).toString();
				String replaceAllLine = perCellData.replaceAll("[\t\n]+", " ");
				String replaceAllSpace = replaceAllLine.replaceAll("\\s{2,}", " ").trim();
				sourceQueryList.add(replaceAllSpace);
			}
			sourceQuery = query(prop.getProperty("keywordFormulaQuery"));
			while (sourceQuery.next()) {
				String perCellData = sourceQuery.getObject(1).toString();
				String replaceAllLine = perCellData.replaceAll("[\t\n]+", " ");
				String replaceAllSpace = replaceAllLine.replaceAll("\\s{2,}", " ").trim();
				sourceQueryList.add(replaceAllSpace);
			}
		} catch (NullPointerException ex) {
			log.error("Exception occurred while executing the query due to " + ex.getMessage());
		}
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("reportQueryList"));
		listOfText = listString();
		for (int i = 0; i < listOfText.size(); i++) {
			Assert.assertTrue(sourceQueryList.contains(listOfText.get(i)),
					"The report query not matches with source : " + listOfText.get(i));
		}
		log.info("tc08_Report Query Matches With Source validation ended..............");
	}

	/**
	 * This method is to validate the path returned query matches source
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc09_IsPathReturnedQueryMatchesSource() throws Exception {
		log.info("tc09_Path Returned Query Matches Source validation started..............");
		establishDatabaseconnection();
		loadHighLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("reportQuery"));
		listOfText = listString();
		List<String> copyListOfText = listOfText;
		listOfWebElement = xtexts(xpathProperties.getProperty("reportQueryPathList"));
		listOfText = listString();
		for (int i = 0; i < listOfText.size(); i++) {
			String[] keywordPathSplit = listOfText.get(i).split("[.]");
			sourceQuery = query("select " + keywordPathSplit[2] + " from " + keywordPathSplit[0] + "."
					+ keywordPathSplit[1] + " where  FIELD_ID = '" + keywordPathSplit[3] + "'");
			while (sourceQuery.next()) {
				String perCellData = sourceQuery.getObject(1).toString();
				String replaceAllLine = perCellData.replaceAll("[\t\n]+", " ");
				String replaceAllSpace = replaceAllLine.replaceAll("\\s{2,}", " ").trim();
				Assert.assertEquals(copyListOfText.get(i), replaceAllSpace,
						"The report query not matches with source : ");
			}
		}
		log.info("tc09_Path Returned Query Matches Source validation ended..............");
	}

}
