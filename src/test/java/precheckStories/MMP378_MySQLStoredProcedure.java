package precheckStories;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP378_MySQLStoredProcedure extends Base {
	@BeforeTest
	public void loadQueryfile() throws IOException {
		prop = loadQueryFile("\\src\\test\\resources\\MMP378_MySQLStoredProcedure.properties");
		xpathProperties = loadXpathFile();
	}

	@Test
	public void tc01_ReportHeadingsvalidation() throws IOException {
		
		texts = xtexts(xpathProperties.getProperty("heading_list"));
		listOfText = listString();
		System.out.println(listOfText);
		String[] checkList = { "Schema Name", "Stored Procedure Name", "Stored Procedure definition" };
		for (int i = 0; i < checkList.length; i++) {
			assertTrue(listOfText.contains(checkList[i]));
		}

	}

	@Test
	public void tc02_CountOfStoredProcedure() throws IOException, SQLException {
		texts = xtexts(xpathProperties.getProperty("sp_count"));
		listOfText = listString();
		int storedProcedureCount=0;
		for (int i = 0; i < listOfText.size(); i++) {
			int ProcedureCount=Integer.parseInt(listOfText.get(i));  
			 storedProcedureCount=storedProcedureCount+ProcedureCount;
		}
		sourceQuery = query(prop.getProperty("storedProcedureCount"));
		sourceQuery.next();
		String dbSPCount = sourceQuery.getObject(1).toString();
		int dataBaseCount=Integer.parseInt(dbSPCount);
		assertEquals(storedProcedureCount, dataBaseCount);
	}

	@Test
	public void tc03_PrecheckReportGenerateStoredProcedure() {
		

	}

}
