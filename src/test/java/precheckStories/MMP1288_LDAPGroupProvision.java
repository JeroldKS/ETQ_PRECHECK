package precheckStories;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP1288_LDAPGroupProvision extends Base {
	
	static Logger log = Logger.getLogger(MMP1288_LDAPGroupProvision.class.getName());
  
	/**
	 * Check if LDAP Group Provisioning Query Returns any row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_checkIfLDAPGroupQueryReturnAnyRow() throws JSchException, SftpException, Exception {
		log.info("TC 01 Checking If the LDAP Group Provisioning query Return Any Row started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP1288_query.properties");
		List<String> ldapGroupQuery = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("ldap_group_query"));
		while (sourceQuery.next()) {
			ldapGroupQuery.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("ldap_group_sync"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(ldapGroupQuery.size() > 0) {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'LDAP Group Synchronization')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "This synchronization is not supported in NXG");
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the synchronization to a supported option");
				} 
			}
		} else {
			log.info("This Test Case will work only if LDAP Group Provisioning query retruns any row");
		}
		dbConnection.close();
		log.info("TC 01 Checking If the LDAP Group Provisioning query Return Any Row Ended..............");
	}
	
	/**
	 * Check if LDAP Group Provisioning Query Returns Empty row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_checkIfLDAPGroupQueryReturnNoRow() throws JSchException, SftpException, Exception {
		log.info("TC 02 Checking If the LDAP Group Provisioning query Return Empty Row. Started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP1288_query.properties");
		List<String> ldapGroupQuery = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("ldap_group_query"));
		while (sourceQuery.next()) {
			ldapGroupQuery.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("ldap_group_sync"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(ldapGroupQuery.size() == 0) {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'LDAP Group Synchronization')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "N/A");
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "Good to Migrate");
				} 
			}
		} else {
			log.info("This Test Case will work only if LDAP Group Provisioning query retruns Empty row");
		}
		dbConnection.close();
		log.info("TC 02 Checking If the LDAP Group Provisioning query Return Empty Row. Ended..............");
	}
	
	/**
	 * Check if LDAP Group Provisioning Query Returns any row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_checkIfLDAPUserQueryReturnAnyRow() throws JSchException, SftpException, Exception {
		log.info("TC 03 Checking If the LDAP User Provisioning query Return Any Row started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP1288_query.properties");
		List<String> ldapUserQuery = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("ldap_user_query"));
		while (sourceQuery.next()) {
			ldapUserQuery.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("ldap_user_sync"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(ldapUserQuery.size() > 0) {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'LDAP User Synchronization')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				if (i == 1) {
					assertEquals(listDataList.get(0).getText(), "This synchronization is not supported in NXG");
				} else if (i == 2) {
					assertEquals(listDataList.get(0).getText(), "Reconfigure the synchronization to a supported option");
				} 
			}
		} else {
			log.info("This Test Case will work only if LDAP User Provisioning query retruns any row");
		}
		dbConnection.close();
		log.info("TC 03 Checking If the LDAP User Provisioning query Return Any Row Ended..............");
	}
	
	/**
	 * Check if LDAP Group Provisioning Query Returns Empty row
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc04_checkIfLDAPUserQueryReturnNoRow() throws JSchException, SftpException, Exception {
		log.info("TC 04 Checking If the LDAP User Provisioning query Return Empty Row. Started..............");
		loadHighLevelReportInBrowser();
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP1288_query.properties");
		List<String> ldapUserQuery = new ArrayList<>();
		establishDatabaseconnection();
		sourceQuery = query(prop.getProperty("ldap_user_query"));
		while (sourceQuery.next()) {
			ldapUserQuery.add(sourceQuery.getObject(1).toString());
		}
		listOfWebElement = xtexts(xpathProperties.getProperty("ldap_user_sync"));
		List<WebElement> listOfWebElementCopy = listOfWebElement;
		if(ldapUserQuery.size() == 0) {
			for (int i = 0; i < listOfWebElementCopy.size(); i++) {
				listOfWebElement = xtexts("//*[contains(text(),'LDAP User Synchronization')]/../td[" + (i + 1) + "]");
				List<WebElement> listDataList = listOfWebElement;
				
					if (i == 1) {
						assertEquals(listDataList.get(0).getText(), "N/A");
					} else if (i == 2) {
						assertEquals(listDataList.get(0).getText(), "Good to Migrate");
					} 
			}
		} else {
			log.info("This Test Case will work only if LDAP User Provisioning query retruns Empty row");
		}
		dbConnection.close();
		log.info("TC 04 Checking If the LDAP User Provisioning query Return Empty Row. Ended..............");
	}
}
