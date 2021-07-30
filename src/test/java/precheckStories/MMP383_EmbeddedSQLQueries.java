package precheckStories;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP383_EmbeddedSQLQueries extends Base {
	static Logger log = Logger.getLogger(MMP383_EmbeddedSQLQueries.class.getName());

	/**
	 * This method is Able To Fetch Source Embedded Queries
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc01_IsAbleToFetchSourceEmbeddedQueries() throws Exception {
		log.info("TC_01 Is able to fetch source embedded queries validation started....................");
		establishDatabaseconnection();
		loadHighLevelReportInBrowser();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//precheck//BusinessRules//MMP383_EmbeddedSQLQueries.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONArray jsonArray = (JSONArray) parse;
		for (int i = 0; i < jsonArray.size(); i++) {
			Object object = jsonArray.get(i);
			JSONObject parseStep1 = (JSONObject) object;
			sourceQuery = query("select count(" + parseStep1.get("field") + ")" + " from " + parseStep1.get("schema")
					+ "." + parseStep1.get("table") + " where " + parseStep1.get("field") + " is not null");
			assertNotNull(sourceQuery.next());
		}
		log.info("TC_01 Is able to fetch source embedded queries validation ended....................");
	}

	/**
	 * This method is to validating the Total Queries Count Matches with Sum Of
	 * Passed And Failed Query
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc06_IsTotalQueriesCountMatchesSumOfPassedAndFailedQuery() throws Exception {
		log.info("TC_06 Total queries count matches sum of passed and failed query validation started....................");
		establishDatabaseconnection();
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//precheck//BusinessRules//MMP383_EmbeddedSQLQueries.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONArray jsonArray = (JSONArray) parse;
		int embeddQueryCount = 0;
		for (int i = 0; i < jsonArray.size(); i++) {
			Object object = jsonArray.get(i);
			JSONObject parseStep1 = (JSONObject) object;
			sourceQuery = query("select count(" + parseStep1.get("field") + ")" + " from " + parseStep1.get("schema")
					+ "." + parseStep1.get("table") + " where " + parseStep1.get("field") + " is not null");
			while (sourceQuery.next()) {
				String QueryCount = sourceQuery.getObject(1).toString();
				int tableQueryCount = Integer.parseInt(QueryCount);
				embeddQueryCount = embeddQueryCount + tableQueryCount;
			}
		}
		text = xtext(xpathProperties.getProperty("totalQueryProcessed"));
		int totalQueryProcessed = Integer.parseInt(text);
		text = xtext(xpathProperties.getProperty("totalQueryPassed"));
		int totalQueryPassed = Integer.parseInt(text);
		text = xtext(xpathProperties.getProperty("totalQueryFailed"));
		int totalQueryFailed = Integer.parseInt(text);
		assertEquals(totalQueryProcessed, embeddQueryCount);
		assertEquals((totalQueryPassed + totalQueryFailed), embeddQueryCount);
		log.info("TC_06 Total queries count matches sum of passed and failed query validation ended....................");
	}

	/**
	 * This method is to validating the Report Captured Over-all Process Duration
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc07_IsReportCaptureOverallProcessDuration() throws Exception {
		log.info("TC_07 Report capture overall process duration validation started....................");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		text = xtext(xpathProperties.getProperty("processedDuration"));
		Double processedDuration = new Double(text);
		assertNotNull(processedDuration);
		log.info("TC_07 Report capture overall process duration validation ended....................");
	}

}
