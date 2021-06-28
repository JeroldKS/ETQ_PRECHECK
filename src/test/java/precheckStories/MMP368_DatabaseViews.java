package precheckStories;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;
import precheck.Base;

public class MMP368_DatabaseViews extends Base {
	/**
	 * This method is to validating the total number of custom views, total number of passed views and total number of failed views are captured in the report and the count matches with source
	 * @throws Exception
	 */
	@Test
	public void tc06_IsTotalViewsAndPassedFailedViewsCapturedInReportAndCountMatchesWithSource() throws Exception {
		loadLowLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP368_DatabaseViews.properties");
		xpathProperties = loadXpathFile();
		int tableQueryCount = 0;
			sourceQuery = query(prop.getProperty("viewsCount"));
			while (sourceQuery.next()) {
				String QueryCount = sourceQuery.getObject(1).toString();
			    tableQueryCount = Integer.parseInt(QueryCount);
		}
		text = xtext(xpathProperties.getProperty("totalViews"));
		int totalQueryProcessed = Integer.parseInt(text);
		text = xtext(xpathProperties.getProperty("PassedViews"));
		int totalQueryPassed = Integer.parseInt(text);
		text = xtext(xpathProperties.getProperty("FailedViews"));
		int totalQueryFailed = Integer.parseInt(text);
		assertEquals(totalQueryProcessed, tableQueryCount);
		assertEquals((totalQueryPassed + totalQueryFailed), tableQueryCount);

	}

}
