package precheckStories;

import static org.testng.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP381_OracleDatabaseViews extends Base {
	static Logger log = Logger.getLogger(MMP381_OracleDatabaseViews.class.getName());

	/**
	 * This method is to validating the total number of custom views, total number
	 * of passed views and total number of failed views are captured in the report
	 * and the count matches with source
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc07_IsTotalViewsAndPassedFailedViewsCapturedInReportAndCountMatchesWithSource() throws Exception {
		log.info(
				"TC_07 Total views and passed failed views captured in report and count matches with source validation started....................");
		loadLowLevelReportInBrowser();
		establishDatabaseconnection();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP381_OracleDatabaseViews.properties");
		xpathProperties = loadXpathFile();
		sourceQuery = query(prop.getProperty("viewsCount"));
		sourceQuery.next();
		int dbViewCount = 0;
		sourceQuery = query(prop.getProperty("viewsCount"));
		while (sourceQuery.next()) {
			dbViewCount++;
		}
		text = xtext(xpathProperties.getProperty("oracleTotalViews"));
		int totalQueryProcessed = Integer.parseInt(text);
		text = xtext(xpathProperties.getProperty("oraclePassedViews"));
		int totalQueryPassed = Integer.parseInt(text);
		text = xtext(xpathProperties.getProperty("oracleFailedViews"));
		int totalQueryFailed = Integer.parseInt(text);
		assertEquals(totalQueryProcessed, dbViewCount);
		assertEquals((totalQueryPassed + totalQueryFailed), dbViewCount);
		log.info(
				"TC_07 Total views and passed failed views captured in report and count matches with source validation ended....................");
	}

}
