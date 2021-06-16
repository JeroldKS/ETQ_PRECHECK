package MMP_396_MySQL_Database_Check;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.poi.hpsf.Array;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.apache.commons.collections.map.StaticBucketMap;
import org.apache.log4j.*;

public class MMP_396_MySQL_Database_Check_1 extends Base {
	static Logger log = Logger.getLogger(MMP_396_MySQL_Database_Check_1.class.getName());

	@Test(enabled=false)
	public void tc_04_DB_check() throws Exception {
		// DB_checks(DB_version,DB_User_count,core_schema_size,non_core_schema_size,utilized_DB_size,engine_version)
		log.info("DB check started....................");
		Properties prop_xpath = new Properties();
		FileInputStream file_xpath = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\test\\resources\\xpath.properties");
		prop_xpath.load(file_xpath);
		Properties prop = new Properties();
		FileInputStream file = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\test\\resources\\MMP_396_MySQL_Database_Check.properties");
		prop.load(file);
		text = xtext(prop_xpath.getProperty("DB_version"));
		source_Query = query(prop.getProperty("DB_version"));
		source_Query.next();
		System.out.println(text  +"  "+source_Query.getObject(1).toString());
		assertEquals(source_Query.getObject(1).toString(), text);
		text = xtext(prop_xpath.getProperty("DB_User_count"));
		source_Query = query(prop.getProperty("DB_User_count"));
		source_Query.next();
		System.out.println(text  +"  "+source_Query.getObject(1).toString());
		assertEquals(source_Query.getObject(1).toString(), text);
		text = xtext(prop_xpath.getProperty("core_schema_size"));
		source_Query = query(prop.getProperty("core_schema_size"));
		source_Query.next();
		System.out.println(text  +"  "+source_Query.getObject(1).toString()+"GB");
		assertEquals(source_Query.getObject(1).toString()+"GB", text);
		text = xtext(prop_xpath.getProperty("non_core_schema_size"));
		source_Query = query(prop.getProperty("non_core_schema_size"));
		source_Query.next();
		System.out.println(text  +"  "+source_Query.getObject(1).toString()+"GB");
		assertEquals(source_Query.getObject(1).toString()+"GB", text);
		text = xtext(prop_xpath.getProperty("utilized_DB_size"));
		source_Query = query(prop.getProperty("utilized_DB_size"));
		source_Query.next();
		System.out.println(text  +"  "+source_Query.getObject(1).toString()+"GB");
		assertEquals(source_Query.getObject(1).toString()+"GB", text);
		text = xtext(prop_xpath.getProperty("engine_version"));
		source_Query = query(prop.getProperty("engine_version"));
		while (source_Query.next()) {
			System.out.println(text  +"  "+source_Query.getObject(1).toString());
		}
		//assertEquals(source_Query.getObject(1).toString(), text);

	}

	@Test(enabled=false)
	public void tc_05_count_and_list_of_Database_users() throws SQLException, IOException {
		//count and list of DB user
		log.info("count and list of Database users started....................");
		List<WebElement> user_list_element = driver.findElements(By.xpath("//*[@id='dataframe'][2]/tbody/tr"));
		List<String> R_user_li = new ArrayList<>();
		List<String> DB_user_li = new ArrayList<>();
		for (int i = 0; i < user_list_element.size(); i++) {
			user_list_element.get(i).getText();
			R_user_li.add(user_list_element.get(i).getText());
		}
		System.out.println(R_user_li);
		System.out.println(user_list_element.size());
		prop = load_query_file();
		source_Query = query(prop.getProperty("DB_user"));

		int source_query_size = 0;
		while (source_Query.next()) {
			source_query_size++;
			DB_user_li.add(source_Query.getObject(1).toString());
		}
		System.out.println(DB_user_li);
		assertEquals(DB_user_li, R_user_li);

	}

	@Test(enabled=false)
	public void tc_06_Report_check() throws IOException {
		//validating the report which present all check list
		log.info("Report check started....................");
		texts = xtexts("//h2");
		list_text = list_string();
		System.out.println(list_text);
		prop = load_query_file();
		String[] check_list = { "Database Checks", "DB Users", "OverallCount", "Index Count - Core",
				"Index Count - NonCore:" };

		for (int i = 0; i < check_list.length; i++) {

			assertTrue(list_text.contains(check_list[i]));
		}

	}
	@Test
	private void TC_03_core_schema_index_count() throws SQLException, IOException {
		//core schema index count validation
		texts = xtexts("//*[contains(text(),'Index Count - Core')]/following::tbody[1]/tr");
		List<WebElement> dup_texts=texts;
		List<String> r_core_schema_index_list = new ArrayList<>();
		List<String> db_core_schema_index_list = new ArrayList<>();
		
		for (int i = 0; i < dup_texts.size(); i++) {
			texts=xtexts("//*[contains(text(),'Index Count - Core')]/following::tbody[1]/tr["+(i+1)+"]/td");
			String r_data_combain = "";
			for (int j = 0; j < texts.size(); j++) {
				r_data_combain=r_data_combain+texts.get(j).getText()+".";
			}
			System.out.println(r_data_combain);
			r_core_schema_index_list.add(r_data_combain);
		}
		System.out.println(r_core_schema_index_list.size());
	
		prop = load_query_file();
		source_Query = query(prop.getProperty("core_schema_index"));
		int columnCount = source_Query.getMetaData().getColumnCount();
		System.out.println(columnCount);
		int table_size = 0;
		
		while (source_Query.next()) {
			table_size++;
			String source_data_comain = "";
			for (int i = 1; i <= columnCount; i++) {
				source_data_comain=source_data_comain+source_Query.getObject(i).toString()+".";
			}
			db_core_schema_index_list.add(source_data_comain);
		}
		assertEquals(r_core_schema_index_list.size(), db_core_schema_index_list.size());
		assertTrue(db_core_schema_index_list.contains(r_core_schema_index_list));
	}

	

}
