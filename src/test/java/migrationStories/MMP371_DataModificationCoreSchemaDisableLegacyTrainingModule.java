package migrationStories;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP371_DataModificationCoreSchemaDisableLegacyTrainingModule extends Base {
	static Logger log = Logger.getLogger(MMP371_DataModificationCoreSchemaDisableLegacyTrainingModule.class.getName());

	/**
	 * This method is to validate \"DISPLAY_IN_HOMEPAGE\" column is set to ZERO for employee training application
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc01_IsDISPLAYINHOMEPAGEColumnIsSetZEROForEmployeeTrainingApplication() throws Exception {
		log.info("TC 01 \"DISPLAY_IN_HOMEPAGE\" column is set to ZERO for employee training application validation started.......");
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		targetQuery = targetQuery(
				"select DISPLAY_IN_HOMEPAGE, application_name, environment_uuid from engine.application_settings where application_name = 'TRAINING' and environment_uuid ='"
						+ envId + "';");
		while (targetQuery.next()) {
			Assert.assertEquals(String.valueOf(targetQuery.getObject(1)), "0");
		}
		log.info("TC 01 \"DISPLAY_IN_HOMEPAGE\" column is set to ZERO for employee training application validation ended.......");
	}

	/**
	 * This method is to validate rest(! DISPLAY_IN_HOMEPAGE) of records in application settings table are not updated
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc02_IsRestOfRecordsInApplicationSettingsTableAreNotUpdated() throws Exception {
		log.info("TC 02 rest of records in application settings table are not updated validation started.......");
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		ArrayList<String> targetColumnNameList = new ArrayList<String>();
		targetQuery = targetQuery(
				"select * from engine.application_settings where application_name = 'TRAINING' and environment_uuid ='"
						+ envId + "';");
		int targetColumnCount = targetQuery.getMetaData().getColumnCount();
		for (int i = 0; i < targetColumnCount; i++) {
			targetColumnNameList.add(targetQuery.getMetaData().getColumnName(i + 1));
		}
		establishDatabaseconnection();
		List<String> sourceColumnNameList = new ArrayList<>();
		sourceQuery = query("select * from engine.application_settings where application_name = 'TRAINING' ");
		int sourceColumnCount = sourceQuery.getMetaData().getColumnCount();
		for (int i = 0; i < sourceColumnCount; i++) {
			sourceColumnNameList.add(sourceQuery.getMetaData().getColumnName(i + 1));
		}
		ArrayList<String> copyTargetColumnNameList = new ArrayList<>();
		copyTargetColumnNameList.addAll(targetColumnNameList);
		copyTargetColumnNameList.removeAll(sourceColumnNameList);
		targetColumnNameList.removeAll(copyTargetColumnNameList);
		targetColumnNameList.remove("DISPLAY_IN_HOMEPAGE");
		ArrayList<String> TargetColumndataList = new ArrayList<>();
		while (targetQuery.next()) {
			String TargetColumndata = "";
			for (int i = 0; i < targetColumnNameList.size(); i++) {
				TargetColumndata = TargetColumndata + String.valueOf(targetQuery.getObject(targetColumnNameList.get(i)))
						+ ".";
			}
			TargetColumndataList.add(TargetColumndata);
		}
		ArrayList<String> sourceColumndataList = new ArrayList<>();
		while (sourceQuery.next()) {
			String sourceColumndata = "";
			for (int i = 0; i < targetColumnNameList.size(); i++) {
				sourceColumndata = sourceColumndata + String.valueOf(sourceQuery.getObject(targetColumnNameList.get(i)))
						+ ".";
			}
			sourceColumndataList.add(sourceColumndata);
		}
		Assert.assertEquals(sourceColumndataList, TargetColumndataList);
		log.info("TC 02 rest of records in application settings table are not updated validation ended.......");
	}
	
}
