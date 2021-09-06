package precheckStories;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP1399_ReviewFeedBack extends Base{
	
	static Logger log = Logger.getLogger(MMP1399_ReviewFeedBack.class.getName());
	
	/**
	 * Verification of items names on PreCheck Report
	 * @throws Exception
	 */
	@Test
	public void tc01_verifytheReportItemsDisplayed() throws Exception {
		log.info("TC 01 Verification of items names on PreCheck Report. Started...................");
		loadHighLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("report_validation"));
		String[] checkList = { "Keyword 'Description' references in ETQScript files", "Java Classes references in ETQScript files",
				"Lookups Alias / Column Reference in ETQScript files", "SQL Statements causing Java Search",
				"Stored Procedures to be Retired", "SQL Query references to old Lookups Schema"};
		for (int i = 0; i < checkList.length; i++) {
			String web = driver.getPageSource().contains(checkList[i]) ? checkList[i]+" Available" : checkList[i]+" Not Available";
			Assert.assertEquals(web, checkList[i]+" Available");
		}
		log.info("TC 01 Verification of items names on PreCheck Report. Ended...................");
	}
	
	/**
	 * Verify that the respective resolution is captured when Java Search SQL exists in source
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc02_verifyJavaSearchSQL() throws Exception {
		log.info("TC 02 Verify that the respective resolution is captured when Java Search SQL exists in source. Started............");
		loadHighLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("javaKeywordOwner"));
		listOfText = listString();
		boolean isJavaSearchSQLPresent = false;
		if(listOfText.size() == 1) {
			String  sqlStmtMessage = xtext(xpathProperties.getProperty("sql_stmt_message"));
			String  sqlStmtResolution = xtext(xpathProperties.getProperty("sql_stmt_resolution"));
			if(!sqlStmtMessage.equals("N/A") && !sqlStmtResolution.equals("N/A")) {
				isJavaSearchSQLPresent = true;
			}
		} else if(listOfText.size() > 1) {
			isJavaSearchSQLPresent = true;
		}
		if(isJavaSearchSQLPresent) {
			text = xtext(xpathProperties.getProperty("sql_stmt_message"));
			Assert.assertEquals(text, "See associated appendix for failures");
			text = xtext(xpathProperties.getProperty("sql_stmt_resolution"));
			Assert.assertEquals(text, "Update the incompatible SQL statements - Remove multiple UNION keyword");
		} else {
			log.info("This case will work only if SQL Statements causing Java Search elements present");
		}
		log.info("TC 02 Verify that the respective resolution is captured when Java Search SQL exists in source. Ended............");
	}
	
	/**
	 * Verification of Appendix headers on PreCheck Report
	 * @throws Exception
	 */
	@Test
	public void tc03_verifytheReportHeadersDisplayed() throws Exception {
		log.info("TC 03 Verification of Appendix headers on PreCheck Report. Started...................");
		loadHighLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("report_validation"));
		String[] checkList = { "APPENDIX: Keyword 'Description' references in ETQScript files", "APPENDIX: Java Classes references in ETQScript files",
				"APPENDIX: Lookups Alias / Column Reference in ETQScript files", "APPENDIX: SQL Statements causing Java Search",
				"APPENDIX: Stored Procedures to be Retired", "APPENDIX: SQL Query references to old Lookups Schema"};
		for (int i = 0; i < checkList.length; i++) {
			String web = driver.getPageSource().contains(checkList[i]) ? checkList[i]+" Available" : checkList[i]+" Not Available";
			Assert.assertEquals(web, checkList[i]+" Available");
		}
		log.info("TC 03Verification of Appendix headers on PreCheck Report. Ended...................");
	}
}
