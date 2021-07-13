package precheckStories;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import precheck.Base;

public class MMP844_CustomApplicationCheckExclusionList extends Base {

	@Test
	public void tc01_IsCoreSchemasExcludedInCustomData() throws Exception {
		loadHighLevelReportInBrowser();
		establishSshConnection();
		establishDatabaseconnection("mysqlSource");
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP529_query.properties");
		InputStream stream = sftpChannel.get("/home/ec2-user/QA_testing/migration-tool/src/precheck/const/common_constants.py");
		
			String oobDesignList = "OOB Design Name List Not Available";
			List<String> unmatchedDesignListInDB = new ArrayList<>();
			List<String> unmatchedDesignListInReport = new ArrayList<>();
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("OOB_APPLICATION_SETTINGS_NAME") && !line.contains("#")) {
					oobDesignList = "OOB Design Name List Available";
					String designList = line.split("=")[1];
					/*if(!line.endsWith(")")) {
						designList = designList + br.readLine();
					}*/
					while (!line.endsWith(")")) {
						line=br.readLine();
						designList=designList+line;
						
					}
					System.out.println(designList);
					sourceQuery = query(prop.getProperty("application_settings_unmatched_data")+" "+designList);
					while (sourceQuery.next()) {
						unmatchedDesignListInDB.add(sourceQuery.getObject(1).toString());
					}
					System.out.println(unmatchedDesignListInDB);
				
}

}

}
}
