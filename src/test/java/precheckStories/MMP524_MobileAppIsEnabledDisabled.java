package precheckStories;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import precheck.Base;


public class MMP524_MobileAppIsEnabledDisabled extends Base {
	static Logger log = Logger.getLogger(MMP524_MobileAppIsEnabledDisabled.class.getName());
	
	/**
	 * This method is to find mobile app enabled and compare with report
	 * @throws SQLException
	 */
	@Test
	public void tc01_IsMobileAppEnabled() throws SQLException {
		log.info("TC 01 Verifying the Mobile App Enabled. Started...................");
		try {
			loadHighLevelReportInBrowser();
			List<String> dbMobileEnabledList = new ArrayList<>();
			List<String> reportMobileEnabledList = new ArrayList<>();
			establishDatabaseconnection();
			sourceQuery = query(prop.getProperty("mobileapp_enabled"));
			while (sourceQuery.next()) {
				dbMobileEnabledList.add(sourceQuery.getObject(1).toString());
			}
			if (dbMobileEnabledList.size() == 0) {
				throw new NullPointerException();
			}
			listOfWebElement = xtexts(xpathProperties.getProperty("mobile_app"));
			List<WebElement> dup_texts = listOfWebElement;
			for (int i = 0; i < dup_texts.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'Mobile App')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 0) {
					AssertJUnit.assertEquals(listDataList.get(0).getText(), "Mobile App");
				}
				if (i == 1) {
					AssertJUnit.assertEquals(listDataList.get(0).getText(), "Action needed");
				}
				if (i == 2) {
					listOfWebElement = xtexts(xpathProperties.getProperty("mobile_app_enabled_list"));
					for (int j = 0; j < listOfWebElement.size(); j++) {
						text = xtext("//*[contains(text(),'Mobile App')]/../td/ul/li[" + (j + 1) + "]");
						reportMobileEnabledList.add(text);

					}
					Collections.sort(dbMobileEnabledList);
					Collections.sort(reportMobileEnabledList);
					AssertJUnit.assertEquals(dbMobileEnabledList, reportMobileEnabledList);
				}
				if (i == 3) {
					AssertJUnit.assertEquals(listDataList.get(0).getText(), "Wait until this feature is available");
				}
			}
			dbConnection.close();
		} catch (Exception e) {
			log.info("mobile app is now disable, to validate this TC need to enable the mobile app........");

		}
		log.info("TC 01 Verifying the Mobile App Enabled. Ended...................");
	}
	
	/**
	 * This method is to find mobile app disabled and compare with report
	 * @throws SQLException
	 */
	@Test
	public void tc02_IsMobileAppDisabled() throws SQLException {
		log.info("TC 02 Verifying the Mobile App Disabled. Started...................");
		try {
			loadHighLevelReportInBrowser();
			establishDatabaseconnection();
			sourceQuery = query(prop.getProperty("mobileapp_disabled"));
			sourceQuery.next();
			log.info("Mobile app enabled = " + sourceQuery.getObject(1).toString());
			log.info("mobile app is now enable, to validate this TC need to disable the mobile app....................");
			dbConnection.close();
		} catch (Exception e) {
			listOfWebElement = xtexts(xpathProperties.getProperty("mobile_app"));
			List<WebElement> listOfTexts = listOfWebElement;
			for (int i = 0; i < listOfTexts.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'Mobile App')]/../td[" + (i + 1) + "]");
				if (i == 0) {
					AssertJUnit.assertEquals(listOfWebElement.get(0).getText(), "Mobile App");
				}
				if (i == 1) {
					AssertJUnit.assertEquals(listOfWebElement.get(0).getText(), "N/A");
				}
				if (i == 2) {
					AssertJUnit.assertEquals(listOfWebElement.get(0).getText(), "N/A");
				}
				if (i == 3) {
					AssertJUnit.assertEquals(listOfWebElement.get(0).getText(), "Good to Migrate");
				}
			}
		}
		log.info("TC 02 Verifying the Mobile App Disabled. Ended...................");
	}


	/**
	 * This method is to find the report contains form name or not
	 * @throws SQLException
	 */
	@Test
	public void tc04_IsReportContainsFormName() throws SQLException,Exception {
		loadHighLevelReportInBrowser();
		log.info("TC 04 Verifying the report contains the form name. Started...................");
		try {
			List<String> enabledFormNamesInDb = new ArrayList<>();
			List<String> enabledFormNameInReport = new ArrayList<>();
			establishDatabaseconnection();
			sourceQuery = query(prop.getProperty("mobileapp_enabled"));
			while (sourceQuery.next()) {
				enabledFormNamesInDb.add(sourceQuery.getObject(1).toString());
			}
			if (enabledFormNamesInDb.size()==0) {
				throw new NullPointerException();
			}
			listOfWebElement = xtexts(xpathProperties.getProperty("mobile_app_list"));
			List<WebElement> dup_texts = listOfWebElement;
			for(int i=0; i< dup_texts.size(); i++) {
				enabledFormNameInReport.add(dup_texts.get(i).getText());
			}
			Collections.sort(enabledFormNamesInDb);
			Collections.sort(enabledFormNameInReport);
			AssertJUnit.assertEquals(enabledFormNamesInDb, enabledFormNameInReport);
			dbConnection.close();
		} catch (Exception e) {
			log.info("mobile app is now disable, to validate this TC need to enable the mobile app....................");

		}
		log.info("TC 04 Verifying the report contains the form name. Ended...................");
	}
}
