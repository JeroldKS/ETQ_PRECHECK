package precheckStories;

import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import precheck.Base;

public class MMP396_MySQLDatabaseCheck extends Base {
	static Logger log = Logger.getLogger(MMP396_MySQLDatabaseCheck.class.getName());
	
	/**
	 * This method is to validate meta info check
	 * @throws Exception 
	 */
	@Test
	public void tc01_DBMetaInfo() throws Exception {
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP396_query.properties");
		xpathProperties = loadXpathFile();
		loadLowLevelReportInBrowser();
		listOfWebElement = xtexts(xpathProperties.getProperty("mysql_meta_body"));
		List<WebElement> list_texts = listOfWebElement;
		List<String> r_meta_info_list = new ArrayList<>();
		List<String> db_meta_info_index_list = new ArrayList<>();
		for (int i = 0; i < list_texts.size(); i++) {
			listOfWebElement = xtexts("//*[contains(text(),'OverallCount')]/following::tbody[1]/tr["+( i+1 )+"]/td");
			String r_data_combain = "";
			for (int j = 0; j < listOfWebElement.size(); j++) {
				r_data_combain = r_data_combain + listOfWebElement.get(j).getText() + ".";
				
			}
			System.out.println(i+1);
			r_meta_info_list.add(r_data_combain);
		}System.out.println(r_meta_info_list);
		sourceQuery = query(prop.getProperty("mysql_db_meta_info"));
		int columnCount = sourceQuery.getMetaData().getColumnCount();
		while (sourceQuery.next()) {
			String source_data_comain = "";
			for (int i = 1; i <= columnCount; i++) {
				source_data_comain = source_data_comain + sourceQuery.getObject(i).toString() + ".";
			}
			db_meta_info_index_list.add(source_data_comain);
		}
		
		Collections.sort(r_meta_info_list);
		System.out.println(r_meta_info_list);
		Collections.sort(db_meta_info_index_list);
		System.out.println(db_meta_info_index_list);
		Assert.assertEquals(r_meta_info_list.size(), db_meta_info_index_list.size());
		Assert.assertEquals(r_meta_info_list, db_meta_info_index_list);
		
		List<String> dup_list = db_meta_info_index_list;
		if (r_meta_info_list.size() != db_meta_info_index_list.size()) {
			dup_list.removeAll(r_meta_info_list);
			System.out.println(dup_list.size());
			log.error("Meta Info not match : " + dup_list);
		}
	}

	/**
	 * This method is to validate non core schema index count
	 * @throws Exception 
	 */
	@Test
	public void tc02_NonCoreSchemaIndexCount() throws Exception {
		log.info("non_core_schema_index_count_started....................");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP396_query.properties");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("non_core_body"));
		List<WebElement> dup_texts = listOfWebElement;
		for (int i = 0; i < 10; i++) {
			int generate = Base.generate(dup_texts.size());
			System.out.println(generate);
			listOfWebElement = xtexts("//*[contains(text(),'Index Count - NonCore:')]/following::tbody[1]/tr[" + generate + "]/td");
			String r_table_name = listOfWebElement.get(0).getText();
			String r_index_count = listOfWebElement.get(1).getText();
			String r_schema_name = listOfWebElement.get(2).getText();
			sourceQuery = query("select count(COLUMN_NAME) FROM INFORMATION_SCHEMA.STATISTICS where TABLE_NAME = '"
					+ r_table_name + "' and TABLE_SCHEMA = '" + r_schema_name + "'");
			sourceQuery.next();
			String db_index_count = sourceQuery.getObject(1).toString();
			System.out.println("select COUNT(INDEX_NAME) FROM INFORMATION_SCHEMA.STATISTICS where TABLE_NAME = '"
					+ r_table_name + "' and TABLE_SCHEMA = '" + r_schema_name + "'");
			System.out.println(db_index_count);
			System.out.println(r_table_name + "   " + r_index_count + "  " + r_schema_name);
			Assert.assertEquals(r_index_count, db_index_count,
					"select COUNT(INDEX_NAME) FROM INFORMATION_SCHEMA.STATISTICS where TABLE_NAME = '" + r_table_name
							+ "' and TABLE_SCHEMA = '" + r_schema_name + "'");
		}

	}
	
	/**
	 * This method is to validate core schema index count
	 * @throws Exception 
	 */
	@Test
	public void tc03_CoreSchemaIndexCount() throws Exception {
		log.info("core_schema_index_count_started....................");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP396_query.properties");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("core_body"));
		List<WebElement> dup_texts = listOfWebElement;
		for (int i = 0; i < 10; i++) {
			int generate = Base.generate(dup_texts.size());
			System.out.println(generate);
			listOfWebElement = xtexts("//*[contains(text(),'Index Count - Core')]/following::tbody[1]/tr[" + generate + "]/td");
			System.out.println(xpathProperties.getProperty("core_data"));
			String r_table_name = listOfWebElement.get(0).getText();
			String r_index_count = listOfWebElement.get(1).getText();
			String r_schema_name = listOfWebElement.get(2).getText();
			sourceQuery = query("select count(COLUMN_NAME) FROM INFORMATION_SCHEMA.STATISTICS where TABLE_NAME = '"
					+ r_table_name + "' and TABLE_SCHEMA = '" + r_schema_name + "'");
			sourceQuery.next();
			String db_index_count = sourceQuery.getObject(1).toString();
			System.out.println("select COUNT(INDEX_NAME) FROM INFORMATION_SCHEMA.STATISTICS where TABLE_NAME = '"
					+ r_table_name + "' and TABLE_SCHEMA = '" + r_schema_name + "'");
			System.out.println(db_index_count);
			System.out.println(r_table_name + "   " + r_index_count + "  " + r_schema_name);
			Assert.assertEquals(r_index_count, db_index_count,
					"select COUNT(INDEX_NAME) FROM INFORMATION_SCHEMA.STATISTICS where TABLE_NAME = '" + r_table_name
							+ "' and TABLE_SCHEMA = '" + r_schema_name + "'");
		}

	}

	/**
	 * This method is for DB
	 * checks(DB_version,DB_User_count,core_schema_size,non_core_schema_size,utilized_DB_size,engine_version)
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc04_DBCheck() throws Exception {
		log.info("DB check started....................");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP396_query.properties");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		text = xtext(xpathProperties.getProperty("DB_version"));
		sourceQuery = query(prop.getProperty("DB_version"));
		sourceQuery.next();
		System.out.println(text + "  " + sourceQuery.getObject(1).toString());
		Assert.assertEquals(sourceQuery.getObject(1).toString(), text);
		text = xtext(xpathProperties.getProperty("DB_User_count"));
		sourceQuery = query(prop.getProperty("DB_User_count"));
		sourceQuery.next();
		System.out.println(text + "  " + sourceQuery.getObject(1).toString());
		Assert.assertEquals(sourceQuery.getObject(1).toString(), text);
		text = xtext(xpathProperties.getProperty("core_schema_size"));
		sourceQuery = query(prop.getProperty("core_schema_size"));
		sourceQuery.next();
		System.out.println(text + "  " + sourceQuery.getObject(1).toString() + "GB");
		Assert.assertEquals(sourceQuery.getObject(1).toString() + "GB", text);
		text = xtext(xpathProperties.getProperty("non_core_schema_size"));
		sourceQuery = query(prop.getProperty("non_core_schema_size"));
		sourceQuery.next();
		System.out.println(text + "  " + sourceQuery.getObject(1).toString() + "GB");
		Assert.assertEquals(sourceQuery.getObject(1).toString() + "GB", text);
		text = xtext(xpathProperties.getProperty("utilized_DB_size"));
		sourceQuery = query(prop.getProperty("utilized_DB_size"));
		sourceQuery.next();
		System.out.println(text + "  " + sourceQuery.getObject(1).toString() + "GB");
		Assert.assertEquals(sourceQuery.getObject(1).toString() + "GB", text);
		text = xtext(xpathProperties.getProperty("engine_version"));
		sourceQuery = query(prop.getProperty("engine_version"));
		while (sourceQuery.next()) {
			System.out.println(text + "  " + sourceQuery.getObject(1).toString());
		}
		// assertEquals(sourceQuery.getObject(1).toString(), text);

	}

	/**
	 * This method is to validate count and list of database users with low level
	 * report
	 * @throws Exception 
	 */
	@Test
	public void tc05_CountOfDatabaseUsers() throws Exception {
		log.info("count and list of Database users started....................");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP396_query.properties");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		List<WebElement> userList = driver.findElements(By.xpath(xpathProperties.getProperty("list_db_user")));
		List<String> reportUserList = new ArrayList<>();
		List<String> dbUserList = new ArrayList<>();
		for (int i = 0; i < userList.size(); i++) {
			userList.get(i).getText();
			reportUserList.add(userList.get(i).getText());
		}
		System.out.println(reportUserList);
		System.out.println(userList.size());
		sourceQuery = query(prop.getProperty("DB_user"));
		while (sourceQuery.next()) {
			dbUserList.add(sourceQuery.getObject(1).toString());
		}

		System.out.println(dbUserList);
		Assert.assertEquals(dbUserList, reportUserList);

	}

	/**
	 * This method is to validating the report which present all check list
	 * @throws Exception 
	 */
	@Test
	public void tc_06_ReportCheck() throws Exception {
		log.info("Report check started....................");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP396_query.properties");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		listOfWebElement = xtexts(xpathProperties.getProperty("report_validation"));
		listOfText = listString();
		System.out.println(listOfText);
		String[] checkList = { "Database Checks", "DB Users", "OverallCount", "Index Count - Core",
				"Index Count - NonCore:" };

		for (int i = 0; i < checkList.length; i++) {

			Assert.assertTrue(listOfText.contains(checkList[i]));
		}

	}

	

	
	
}