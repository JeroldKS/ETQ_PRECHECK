package precheckStories;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP383_EmbeddedSQLQueries extends Base {
	
	/**
	 * This method is Able To Fetch Source Embedded Queries
	 * @throws Exception
	 */
	@Test
	public void tc01_IsAbleToFetchSourceEmbeddedQueries() throws Exception {
		loadHighLevelReportInBrowser();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//precheck//BusinessRules//MMP383_EmbeddedSQLQueries.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONArray jsonArray = (JSONArray) parse;
		System.out.println(jsonArray.size());
		for (int i = 0; i < jsonArray.size(); i++) {
			Object object = jsonArray.get(i);
			System.out.println(object.toString());
			JSONObject parseStep1 = (JSONObject) object;
			System.out.println(parseStep1.get("schema"));
			System.out.println(parseStep1.get("table"));
			System.out.println(parseStep1.get("field"));
			System.out.println("select count(" + parseStep1.get("field") + ") from " + parseStep1.get("schema") + "."
					+ parseStep1.get("table") + " where " + parseStep1.get("field") + " is not null");
			sourceQuery = query("select count(" + parseStep1.get("field") + ")" + " from " + parseStep1.get("schema")
					+ "." + parseStep1.get("table") + " where " + parseStep1.get("field") + " is not null");

			while (sourceQuery.next()) {
				System.out.println(sourceQuery.getObject(1).toString());
			}
			assertNotNull(sourceQuery.next());
		}
	}

	/**
	 * This method is to validating the Total Queries Count Matches with Sum Of Passed And Failed Query
	 * @throws Exception
	 */
	@Test
	public void tc06_IsTotalQueriesCountMatchesSumOfPassedAndFailedQuery() throws Exception {
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
		System.out.println(embeddQueryCount);
		text = xtext(xpathProperties.getProperty("totalQueryProcessed"));
		int totalQueryProcessed = Integer.parseInt(text);
		text = xtext(xpathProperties.getProperty("totalQueryPassed"));
		int totalQueryPassed = Integer.parseInt(text);
		text = xtext(xpathProperties.getProperty("totalQueryFailed"));
		int totalQueryFailed = Integer.parseInt(text);
		assertEquals(totalQueryProcessed, embeddQueryCount);
		assertEquals((totalQueryPassed + totalQueryFailed), embeddQueryCount);
	}

	/**
	 * This method is to validating the Report Captured Over-all Process Duration
	 * @throws Exception
	 */
	@Test
	public void tc07_IsReportCaptureOverallProcessDuration() throws Exception {
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		text = xtext(xpathProperties.getProperty("processedDuration"));
		Double processedDuration = new Double(text);
		assertNotNull(processedDuration);
		

	}

}
