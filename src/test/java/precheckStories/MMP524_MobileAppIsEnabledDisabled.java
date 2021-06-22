package precheckStories;

import static org.testng.Assert.assertEquals;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import precheck.Base;


public class MMP524_MobileAppIsEnabledDisabled extends Base {
	static Logger log = Logger.getLogger(MMP524_MobileAppIsEnabledDisabled.class.getName());
	
	/**
	 * This method is to find mobile app enabled and compare with report
	 * 
	 * @throws SQLException
	 */
	@Test
	public void tc01_IsMobileAppEnabled() throws SQLException {
		log.info("mobile_app_enabled_started....................");
		// source_Query = query(prop.getProperty("mysql_db_meta_info"));
		sourceQuery = query("SELECT form_name FROM engine.form_settings WHERE MOBILIZED_FORM_ID = 1;");

		try {
			List<String> dbMobileEnabledList = new ArrayList<>();
			List<String> reportMobileEnabledList = new ArrayList<>();
			while (sourceQuery.next()) {
				dbMobileEnabledList.add(sourceQuery.getObject(1).toString());
			}
			if (dbMobileEnabledList.size() == 0) {
				throw new NullPointerException();
			}
			// listOfWebElement = xtexts(prop_xpath.getProperty("mysql_meta_body"));
			listOfWebElement = xtexts("//*[contains(text(),'Mobile App')]/../td");
			List<WebElement> dup_texts = listOfWebElement;
			for (int i = 0; i < dup_texts.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'Mobile App')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 0) {
					assertEquals(listDataList.get(0).getText(), "Mobile App");
				}
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "Action needed");
				}
				if (i == 2) {
					listOfWebElement = xtexts("//*[contains(text(),'Mobile App')]/../td/ul/li");
					for (int j = 0; j < listOfWebElement.size(); j++) {
						text = xtext("//*[contains(text(),'Mobile App')]/../td/ul/li[" + (j + 1) + "]");
						reportMobileEnabledList.add(text);

					}
					Collections.sort(dbMobileEnabledList);
					Collections.sort(reportMobileEnabledList);
					assertEquals(dbMobileEnabledList, reportMobileEnabledList);
				}
				if (i == 3) {
					assertEquals(listDataList.get(0).getText(), "Wait until this feature is available");
				}
			}

		} catch (Exception e) {
			log.info(
					"mobile app is now disable, to validate this TC need to enable the mobile app....................");

		}
	}
	
	/**
	 * This method is to find mobile app disabled and compare with report
	 * @throws SQLException
	 */
	@Test
	public void tc02_IsMobileAppDisabled() throws SQLException {
		log.info("mobile_app_disabled_started....................");
		// source_Query = query(prop.getProperty("mysql_db_meta_info"));
		sourceQuery = query(
				"SELECT form_name, form_id, MOBILIZED_FORM_ID FROM engine.form_settings WHERE MOBILIZED_FORM_ID = 1;");
		try {
			sourceQuery.next();
			log.info("Mobile app eabled = " + sourceQuery.getObject(1).toString());

			log.info(
					"mobile app is now enable, to validate this TC need to disable the mobile app....................");
		} catch (Exception e) {
			// listOfWebElement = xtexts(prop_xpath.getProperty("mysql_meta_body"));
			listOfWebElement = xtexts("//*[contains(text(),'Mobile App')]/../td");
			List<WebElement> listOfTexts = listOfWebElement;
			for (int i = 0; i < listOfTexts.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'Mobile App')]/../td[" + (i + 1) + "]");
				if (i == 0) {
					assertEquals(listOfWebElement.get(0).getText(), "Mobile App");
				}
				if (i == 1) {
					assertEquals(listOfWebElement.get(0).getText(), "N/A");
				}
				if (i == 2) {
					assertEquals(listOfWebElement.get(0).getText(), "N/A");
				}
				if (i == 3) {
					assertEquals(listOfWebElement.get(0).getText(), "Good to Migrate");
				}
			}
		}

	}


	/**
	 * This method is to find the report contains form name or not
	 * @throws SQLException
	 */
	@Test
	public void tc04_IsReportContainsFormName() throws SQLException,Exception {
		loadHighLevelReportInBrowser();
		log.info("Verifying the report contains the form name....................");
		sourceQuery = query(
				"SELECT form_name FROM engine.form_settings WHERE MOBILIZED_FORM_ID = 1;");
		try {
			List<String> enabledFormNamesInDb = new ArrayList<>();
			List<String> enabledFormNameInReport = new ArrayList<>();
			while (sourceQuery.next()) {
				enabledFormNamesInDb.add(sourceQuery.getObject(1).toString());
			}
			if (enabledFormNamesInDb.size()==0) {
				throw new NullPointerException();
			}
			listOfWebElement = xtexts("//*[contains(text(),'Mobile App')]/../td[3]/ul/li");
			List<WebElement> dup_texts = listOfWebElement;
			for(int i=0; i< dup_texts.size(); i++) {
				enabledFormNameInReport.add(dup_texts.get(i).getText());
			}
			Collections.sort(enabledFormNamesInDb);
			Collections.sort(enabledFormNameInReport);
			assertEquals(enabledFormNamesInDb, enabledFormNameInReport);
		} catch (Exception e) {
			log.info("mobile app is now disable, to validate this TC need to enable the mobile app....................");

		}
	}
}
