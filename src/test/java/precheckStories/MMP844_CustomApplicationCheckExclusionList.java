package precheckStories;



import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP844_CustomApplicationCheckExclusionList extends Base {
	static Logger log = Logger.getLogger(MMP844_CustomApplicationCheckExclusionList.class.getName());

	@Test
	public void tc01_IsCoreSchemasExcludedInCustomData() throws Exception {
		loadHighLevelReportInBrowser();
		establishDatabaseconnection();
		establishSshConnectionForSourceInstance();
		prop = loadQueryFile(
				"//src//test//resources//precheck//queries//MMP844_CustomApplicationCheckExclusionList.properties");
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("common_constants_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("common_constants_windows"));
		}
		

		String oobDesignList = "OOB Design Name List Not Available";
		List<String> unmatchedDesignListInDB = new ArrayList<>();
		List<String> unmatchedDesignListInReport = new ArrayList<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains("OOB_APPLICATION_SETTINGS_NAME") && !line.contains("#")) {
				oobDesignList = "OOB Design Name List Available";
				String designList = line.split("=")[1];
				while (!line.endsWith(")")) {
					line = br.readLine();
					designList = designList + line;
				}
				sourceQuery = query(prop.getProperty("unmatchedDataList") + " " + designList);
				while (sourceQuery.next()) {
					unmatchedDesignListInDB.add(String.valueOf(sourceQuery.getObject(1)));
				}
				System.out.println(unmatchedDesignListInDB);
				listOfWebElement = xtexts(xpathProperties.getProperty("unmatchedReportList"));
				if (unmatchedDesignListInDB.size() != 0) {
					List<WebElement> listOfWebElementCopy = listOfWebElement;
					for (int i = 0; i < listOfWebElementCopy.size(); i++) {
						unmatchedDesignListInReport.add(listOfWebElementCopy.get(i).getText());
					}
					Collections.sort(unmatchedDesignListInDB);
					Collections.sort(unmatchedDesignListInReport);
					Assert.assertEquals(unmatchedDesignListInReport,unmatchedDesignListInDB);
				}
				if (xtext(xpathProperties.getProperty("unmatchedEmptyList")).equals("Good to Migrate")) {
					text = xtext(xpathProperties.getProperty("unmatchedEmptyList"));
					Assert.assertEquals(text, "Good to Migrate");
				}

				System.out.println(unmatchedDesignListInDB);
				System.out.println(unmatchedDesignListInReport);

			}

		}
		Assert.assertEquals(oobDesignList, "OOB Design Name List Available");

	}
}
