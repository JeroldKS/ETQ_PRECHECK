package precheckStories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP534_DestinationDatabaseCheck extends Base {
	static Logger log = Logger.getLogger(MMP534_DestinationDatabaseCheck.class.getName());

	@Test
	public void tc03_IsENVIDNotPresentInCoreSchemaTableRecordInDestination() throws Exception {
		log.info("tc03 ENV-id not present in core schema table record in destination vaidation started...............");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		text = xtext(xpathProperties.getProperty("envID"));
		establishDatabaseconnection("mysqltarget");
		prop = loadQueryFile("\\src\\test\\resources\\precheck\\queries\\MMP534_DestinationDatabaseCheck.properties");
		sourceQuery = query("select schema_name from information_schema.schemata where schema_name like '%_" + text
				+ "' order by schema_name");
		if (sourceQuery.next() != false) {
			while (sourceQuery.next()) {
				Assert.assertNull(String.valueOf(sourceQuery.getObject(1)),
						"Refer this Query :: select schema_name from information_schema.schemata where schema_name like '%_"
								+ text + "' order by schema_name");
			}
		}
		log.info("tc03 ENV-id not present in core schema table record in destination vaidation ended.................");
	}

	@Test
	public void tc04_SourceEnvironmentNonCoreSchemasNotPresentInDestination() throws Exception {
		log.info("tc04 source environment non-core schemas not present in destination vaidation started...............");
		loadLowLevelReportInBrowser();
		xpathProperties = loadXpathFile();
		text = xtext(xpathProperties.getProperty("envID"));
		establishDatabaseconnection("mysqltarget");
		prop = loadQueryFile("\\src\\test\\resources\\precheck\\queries\\MMP534_DestinationDatabaseCheck.properties");
		sourceQuery = query(prop.getProperty("nonCoreDestnationFetch"));
		ResultSet nonCoreDestnationFetch = sourceQuery;
		List<String> reportMetaInfoList = new ArrayList<>();
		while (nonCoreDestnationFetch.next()) {
			reportMetaInfoList.add(String.valueOf(nonCoreDestnationFetch.getObject(1)) + "."
					+ String.valueOf(nonCoreDestnationFetch.getObject(2)));
		}
		for (int i = 0; i < 10; i++) {
			sourceQuery = query("select * from " + reportMetaInfoList.get(generate(reportMetaInfoList.size()))
					+ " where ENVIRONMENT_UUID = '" + text + "'");
			int columnCount = sourceQuery.getMetaData().getColumnCount();
			for (int j = 0; j < columnCount; j++) {
				if (sourceQuery.next() != false) {
					Assert.assertNull(String.valueOf(sourceQuery.getObject(j + 1)),
							"Refer this Query :: select * from " + String.valueOf(nonCoreDestnationFetch.getObject(1))
									+ "." + String.valueOf(nonCoreDestnationFetch.getObject(2))
									+ " where ENVIRONMENT_UUID = '" + text + "'");
				}
			}
		}
		log.info("tc04 source environment non-core schemas not present in destination vaidation ended.................");
	}
}
