package precheckStories;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;
import org.testng.annotations.Test;
import precheck.Base;

public class MMP388_EmbeddedSQLQueriesJavaSearch extends Base {
	static Logger log = Logger.getLogger(MMP388_EmbeddedSQLQueriesJavaSearch.class.getName());

	/**
	 * This method is to identify report query have 3 or more UNION keyword
	 * @throws Exception
	 */
	@Test
	public void tc02_IsIdentifyReportQueryHave3OrMoreUNIONKeyword() throws Exception {
		log.info(
				"tc02_Is Identify Report Query Have 3 Or More UNION Keyword validation Started...............................");
		loadHighLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("javaKeywordSearch"));
		listOfText = listString();
		List<String> javaKeywordText = listOfText;
		listOfWebElement = xtexts(xpathProperties.getProperty("javaKeywordOwner"));
		listOfText = listString();
		for (int i = 0; i < javaKeywordText.size(); i++) {
			String javaSqlQuery = javaKeywordText.get(i);
			String javaKeyword = "UNION";
			String spaceSplit[] = javaSqlQuery.split(" ");
			int keywordCount = 0;
			for (int j = 0; j < spaceSplit.length; j++) {
				if (javaKeyword.equals(spaceSplit[j]))
					keywordCount++;
			}
			Assert.assertTrue(keywordCount >= 3, listOfText.get(i));
		}
		log.info("tc07 Report Query Path validation ended......................");
	}

	/**
	 * This method is to identified report query captured in format
	 * @throws Exception
	 */
	@Test
	public void tc03_IdentifiedReportQueryCapturedInFormat() throws Exception {
		log.info(
				"tc03_Identified Report Query Captured In Format validation Started...................................");
		loadHighLevelReportInBrowser();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//precheck//BusinessRules//MMP388_EmbeddedSQLQueriesJavaSearch.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONArray jsonArray = (JSONArray) parse;
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("keywordOwnerFormat"));
		listOfText = listString();
		for (int i = 0; i < listOfText.size(); i++) {
			String[] keywordOwnerSplit = listOfText.get(i).split("[.]");
			for (int j = 0; j < jsonArray.size(); j++) {
				Object jsonobject = jsonArray.get(j);
				JSONObject parseStep1 = (JSONObject) jsonobject;
				if ((parseStep1.get("schema").toString().equalsIgnoreCase(keywordOwnerSplit[0]))
						&& (parseStep1.get("table").toString().equalsIgnoreCase(keywordOwnerSplit[1]))
						&& (parseStep1.get("field").toString().equalsIgnoreCase(keywordOwnerSplit[2]))) {
					sourceQuery = query("select " + keywordOwnerSplit[2] + " from " + keywordOwnerSplit[0] + "."
							+ keywordOwnerSplit[1] + " where " + keywordOwnerSplit[2] + " is not null and "
							+ parseStep1.get("id") + " = '" + keywordOwnerSplit[3] + "'");
					Assert.assertTrue(sourceQuery.next(), "Report Format mismatch");

				}
			}
		}
		log.info("tc03_Identified Report Query Captured In Format validation ended...................................");
	}

	/**
	 * This method is to validate report and source query count same
	 * @throws Exception
	 */
	@Test
	public void tc05_IsReportAndSourceQueryCountSame() throws Exception {
		log.info(
				"tc05 Report and Source Query Count Have 3 Or More UNION Keyword Comparision Started......................................");
		loadHighLevelReportInBrowser();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//precheck//BusinessRules//MMP388_EmbeddedSQLQueriesJavaSearch.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONArray jsonArray = (JSONArray) parse;
		int overallKeywordCount = 0;
		for (int i = 0; i < jsonArray.size(); i++) {
			Object jsonobject = jsonArray.get(i);
			JSONObject parseStep1 = (JSONObject) jsonobject;
			sourceQuery = query("select " + parseStep1.get("field") + " from " + parseStep1.get("schema") + "."
					+ parseStep1.get("table") + " where " + parseStep1.get("field") + " is not null");
			while (sourceQuery.next()) {
				String perCellData = sourceQuery.getObject(1).toString();
				String replaceAllLine = perCellData.replaceAll("[\t\n]+", " ");
				String replaceAllSpace = replaceAllLine.replaceAll("\\s{2,}", " ").trim();
				String javaSqlQuery = replaceAllSpace;
				String javaKeyword = "UNION";
				String spaceSplit[] = javaSqlQuery.split(" ");
				int keywordCount = 0;
				for (int j = 0; j < spaceSplit.length; j++) {
					if (javaKeyword.equals(spaceSplit[j]))
						keywordCount++;
				}
				if (keywordCount >= 3) {
					overallKeywordCount++;
				}
			}
		}
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("overallKeywordCount"));
		Assert.assertEquals(overallKeywordCount, listOfWebElement.size());
		log.info(
				"tc05 Report and Source Query Count Have 3 Or More UNION Keyword Comparision ended......................................");
	}

	/**
	 * This method is to validate Report and Source Query are same
	 * @throws Exception
	 */
	@Test
	public void tc06_ReportAndSourceQueryAreSame() throws Exception {
		log.info("tc06 Repor and Source Query are same validation started......................");
		loadHighLevelReportInBrowser();
		List<String> sourceQueryList = new ArrayList<>();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//precheck//BusinessRules//MMP388_EmbeddedSQLQueriesJavaSearch.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONArray jsonArray = (JSONArray) parse;
		for (int i = 0; i < jsonArray.size(); i++) {
			Object jsonobject = jsonArray.get(i);
			JSONObject parseStep1 = (JSONObject) jsonobject;
			sourceQuery = query("select " + parseStep1.get("field") + " from " + parseStep1.get("schema") + "."
					+ parseStep1.get("table") + " where " + parseStep1.get("field") + " is not null");
			while (sourceQuery.next()) {
				String perCellData = sourceQuery.getObject(1).toString();
				String replaceAllLine = perCellData.replaceAll("[\t\n]+", " ");
				String replaceAllSpace = replaceAllLine.replaceAll("\\s{2,}", " ").trim();
				String javaSqlQuery = replaceAllSpace;
				String javaKeyword = "UNION";
				String spaceSplit[] = javaSqlQuery.split(" ");
				int keywordCount = 0;
				for (int j = 0; j < spaceSplit.length; j++) {
					if (javaKeyword.equals(spaceSplit[j]))
						keywordCount++;
				}
				if (keywordCount >= 3) {
					sourceQueryList.add(javaSqlQuery);
				}
			}
		}
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("javaKeywordSearchQuery"));
		listOfText = listString();
		Collections.sort(sourceQueryList);
		Collections.sort(listOfText);
		Assert.assertEquals(sourceQueryList, listOfText);
		log.info("tc06 Repor and Source Query are same validation ended......................");
	}

	/**
	 * This method is to validate Report Query path is valid
	 * @throws Exception
	 */
	@Test
	public void tc07_IsReportQueryPathValid() throws Exception {
		log.info("tc07 Report Query Path validation started......................");
		loadHighLevelReportInBrowser();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//precheck//BusinessRules//MMP388_EmbeddedSQLQueriesJavaSearch.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONArray jsonArray = (JSONArray) parse;
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("reportQueryPath"));
		listOfText = listString();
		for (int i = 0; i < listOfText.size(); i++) {
			String[] keywordOwnerSplit = listOfText.get(i).split("[.]");
			for (int j = 0; j < jsonArray.size(); j++) {
				Object jsonobject = jsonArray.get(j);
				JSONObject parseStep1 = (JSONObject) jsonobject;
				if ((parseStep1.get("schema").toString().equalsIgnoreCase(keywordOwnerSplit[0]))
						&& (parseStep1.get("table").toString().equalsIgnoreCase(keywordOwnerSplit[1]))
						&& (parseStep1.get("field").toString().equalsIgnoreCase(keywordOwnerSplit[2]))) {
					sourceQuery = query("select " + keywordOwnerSplit[2] + " from " + keywordOwnerSplit[0] + "."
							+ keywordOwnerSplit[1] + " where " + keywordOwnerSplit[2] + " is not null and "
							+ parseStep1.get("id") + " = '" + keywordOwnerSplit[3] + "'");
					Assert.assertTrue(sourceQuery.next(), "Report Path not valid");
				}
			}
		}
		log.info("tc07 Report Query Path validation ended......................");
	}
	
	/**
	 * This method is to validate Report Query associated with settings name path
	 * @throws Exception
	 */
	@Test
	public void tc08_IsReportQueryAssociatedWithSettingsNamePath() throws Exception {
		log.info(
				"tc08 Report Query associated With Settings Name Path validation Started...................................");
		loadHighLevelReportInBrowser();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//precheck//BusinessRules//MMP388_EmbeddedSQLQueriesJavaSearch.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONArray jsonArray = (JSONArray) parse;
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("keywordOwnerFormat"));
		listOfText = listString();
		for (int i = 0; i < listOfText.size(); i++) {
			String[] keywordOwnerSplit = listOfText.get(i).split("[.]");
			for (int j = 0; j < jsonArray.size(); j++) {
				Object jsonobject = jsonArray.get(j);
				JSONObject parseStep1 = (JSONObject) jsonobject;
				if ((parseStep1.get("schema").toString().equalsIgnoreCase(keywordOwnerSplit[0]))
						&& (parseStep1.get("table").toString().equalsIgnoreCase(keywordOwnerSplit[1]))
						&& (parseStep1.get("field").toString().equalsIgnoreCase(keywordOwnerSplit[2]))) {
					sourceQuery = query("select " + keywordOwnerSplit[2] + " from " + keywordOwnerSplit[0] + "."
							+ keywordOwnerSplit[1] + " where " + keywordOwnerSplit[2] + " is not null and "
							+ parseStep1.get("id") + " = '" + keywordOwnerSplit[3] + "'");
					text = xtext("//*[contains(text(),'APPENDIX: Java Search SQL')]/following::tbody[1]/tr["+ (i+1) +"]/td[2]");
					sourceQuery.next();
					String perCellData = sourceQuery.getObject(1).toString();
					String replaceAllLine = perCellData.replaceAll("[\t\n]+", " ");
					String replaceAllSpace = replaceAllLine.replaceAll("\\s{2,}", " ").trim();
					String javaSqlQuery = replaceAllSpace;
					Assert.assertEquals(javaSqlQuery,text );

				}
			}
		}
		log.info("tc08 Report Query associated With Settings Name Path validation ended...................................");	

	}	
	
	
	
}
