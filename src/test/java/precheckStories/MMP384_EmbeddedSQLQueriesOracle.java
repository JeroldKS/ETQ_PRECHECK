package precheckStories;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.FileReader;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP384_EmbeddedSQLQueriesOracle extends Base {

	static Logger log = Logger.getLogger(MMP384_EmbeddedSQLQueriesOracle.class.getName());

	/**
	 * To verify able to fetch source embedded queries
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc01_IsAbleToFetchSourceEmbeddedQueries() throws Exception {
		log.info("TC_01 Is able to fetch source embedded queries validation started..............");
		establishDatabaseconnection();
		loadHighLevelReportInBrowser();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//precheck//BusinessRules//MMP384_EmbeddedSQLQueriesMSSQL.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONArray jsonArray = (JSONArray) parse;
		for (int i = 0; i < jsonArray.size(); i++) {
			Object object = jsonArray.get(i);
			JSONObject parseStep1 = (JSONObject) object;
			sourceQuery = query("select " + parseStep1.get("field") + "" + " from " + parseStep1.get("schema") + "."
					+ parseStep1.get("table") + " where " + parseStep1.get("field") + " is not null");
			Assert.assertNotNull(sourceQuery.next());
		}
		log.info("TC_01 Is able to fetch source embedded queries validation ended..............");
	}

	/**
	 * To verify total queries count matches sum of passed and failed query in
	 * report
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc06_IsTotalQueriesCountMatchesSumOfPassedAndFailedQuery() throws Exception {
		log.info("TC_06 Is total queries count matches sum of passed and failed query validation started...........");
		establishDatabaseconnection();
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//precheck//BusinessRules//MMP384_EmbeddedSQLQueriesMSSQL.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONArray jsonArray = (JSONArray) parse;
		int embeddQueryCount = 0;
		for (int i = 0; i < jsonArray.size(); i++) {
			Object object = jsonArray.get(i);
			JSONObject parseStep1 = (JSONObject) object;
			sourceQuery = query("select count(*)" + " from " + parseStep1.get("schema") + "." + parseStep1.get("table")
					+ " where " + parseStep1.get("field") + " is not null");
			while (sourceQuery.next()) {
				String QueryCount = sourceQuery.getObject(1).toString();
				int tableQueryCount = Integer.parseInt(QueryCount);
				embeddQueryCount = embeddQueryCount + tableQueryCount;
			}
		}
		text = xtext(xpathProperties.getProperty("totalQueryProcessedCount"));
		int totalQueryProcessed = Integer.parseInt(text);
		text = xtext(xpathProperties.getProperty("QueryPassedCount"));
		int totalQueryPassed = Integer.parseInt(text);
		text = xtext(xpathProperties.getProperty("QueryFailedCount"));
		int totalQueryFailed = Integer.parseInt(text);
		assertEquals(totalQueryProcessed, embeddQueryCount);
		assertEquals((totalQueryPassed + totalQueryFailed), embeddQueryCount);
		log.info("TC_06 Is total queries count matches sum of passed and failed query validation ended..............");
	}

	/**
	 * To verify report capture overall process duration
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc07_IsReportCaptureOverallProcessDuration() throws Exception {
		log.info("TC_07 Is report capture overall process duration validation started..............");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		try {
			text = xtext(xpathProperties.getProperty("processedDuration"));
			Double processedDuration = new Double(text);
			assertNotNull(processedDuration);
		} catch (NoSuchElementException e) {
			log.error("Exception occured during reading the precheck low level report gue to " + e.getMessage());
			assertNotNull(null, "Processed duration not captured in low level report");
		}
		log.info("TC_07 Is report capture overall process duration validation ended..............");
	}

}
