package migrationStories;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.j2objc.annotations.Property;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.mysql.cj.protocol.Resultset;


import precheck.Base;

public class MMP564_DataStructureModificationCoreSchemaLookupSchemaTableMergeIntoLocationTable extends Base {

	static Logger log = Logger.getLogger(
			MMP564_DataStructureModificationCoreSchemaLookupSchemaTableMergeIntoLocationTable.class.getName());

	/**
	 * Checking whether the Non core Schema TRAINING is not Migrated from Source to
	 * Target
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */

	@Test(enabled = false)
	public static void tc01_VerifyTablesInLOOKUPSSchemaStartsEtqEndingWith_LOCSAreMergedIntoTargetTableLOOKUPSLOCATIONS()
			throws JSchException, SftpException, Exception {

		log.info(
				"Verify if the tables with data in LOOKUPS Schema Starting with Etq ending with _LOCS are merged into the target table LOOKUPS.LOCATIONS. Started.......");
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");

		prop = loadQueryFile("//src//test//resources//migration//queries//MMP564_query.properties");
		List<String> baseTableStartsEtqEndsLookup = new ArrayList<>();

		List<String> LOCATION_PROFILE_ID = new ArrayList<>();
		List<String> LOOKUP_ID = new ArrayList<>();
		List<String> MASTER_KEYWORD_LIST_ID = new ArrayList<>();
		List<String> ETQ$RECORD_ORDER = new ArrayList<>();

		sourceQuery = query(prop.getProperty("sourcequeryStartsEtqEndsLookup"));
		while (sourceQuery.next()) {
			baseTableStartsEtqEndsLookup.add(sourceQuery.getObject(1).toString());

		}

		for (String tableWithdataName : baseTableStartsEtqEndsLookup) {
			sourceQuery = query(prop.getProperty("sourceQueryToIterateNTables") + tableWithdataName);

			while (sourceQuery.next()) {
				LOCATION_PROFILE_ID.add(sourceQuery.getString("LOCATION_PROFILE_ID"));
				LOOKUP_ID.add(sourceQuery.getString("LOOKUP_ID"));
				MASTER_KEYWORD_LIST_ID.add(sourceQuery.getString("MASTER_KEYWORD_LIST_ID"));
				ETQ$RECORD_ORDER.add(sourceQuery.getString("ETQ$RECORD_ORDER"));

			}

		}

		List<String> TARGETLOCATION_PROFILE_ID = new ArrayList<>();
		List<String> TARGETLOOKUP_ID = new ArrayList<>();
		List<String> TARGETMASTER_KEYWORD_LIST_ID = new ArrayList<>();
		List<String> TARGETETQ$RECORD_ORDER = new ArrayList<>();
		targetQuery = query(prop.getProperty("targetlookups_locationtable"));
		while (targetQuery.next()) {

			TARGETLOCATION_PROFILE_ID.add(targetQuery.getString("LOCATION_PROFILE_ID"));
			TARGETLOOKUP_ID.add(targetQuery.getString("LOOKUP_ID"));
			TARGETMASTER_KEYWORD_LIST_ID.add(targetQuery.getString("MASTER_KEYWORD_LIST_ID"));
			TARGETETQ$RECORD_ORDER.add(targetQuery.getString("ETQ$RECORD_ORDER"));

		}

		Assert.assertTrue(TARGETLOCATION_PROFILE_ID.containsAll(LOCATION_PROFILE_ID));
		Assert.assertTrue(TARGETLOOKUP_ID.containsAll(LOOKUP_ID));
		Assert.assertTrue(TARGETMASTER_KEYWORD_LIST_ID.containsAll(MASTER_KEYWORD_LIST_ID));
		Assert.assertTrue(TARGETETQ$RECORD_ORDER.containsAll(ETQ$RECORD_ORDER));

		// to check unique Lookupuuid

		targetQuery = targetQuery(prop.getProperty("TargetLookUpId"));

		int rowcount = RowCount(targetQuery);
		targetQuery = targetQuery(prop.getProperty("TargetUniqueLookUpId"));

		int rowcount1 = RowCount(targetQuery);

		Assert.assertEquals(rowcount, rowcount1);

		// to check all are mttype3

		targetQuery = targetQuery(prop.getProperty("MtTypeValidation"));

		int mttyperows = RowCount(targetQuery);

		Assert.assertEquals(mttyperows, 0);

		log.info(
				"TC 01 Verify if the tables with data in LOOKUPS Schema Starting with Etq ending with _LOCS are merged into the target table LOOKUPS.LOCATIONS. Ended.......");
	}

	public static int RowCount(ResultSet rs) throws SQLException {

		int count = 0;
		while (rs.next()) {
			count++;
		}
		return count;

	}

	/**
	 * Verify Tables Not Starting With Etq$ Ending With _LOCS Not Migrated To
	 * Location Table in target
	 * 
	 * @throws SQLException
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */

	@Test(enabled = false)
	public static void tc02_VerifyTablesNotStartingWithEtqEndingWithLOCSNotMigratedToLocationsTable()
			throws JSchException, SftpException, Exception {

		log.info(
				"TC_02 Verify if the tables not Starting with Etq and ending with _LOCS are not Migrated to Location table. Started.......");
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");

		prop = loadQueryFile("//src//test//resources//migration//queries//MMP564_query.properties");
		List<String> baseTableExceptStartsEtqEndsLookup = new ArrayList<>();

		List<String> LOCATION_PROFILE_ID = new ArrayList<>();
		List<String> LOOKUP_ID = new ArrayList<>();
		List<String> MASTER_KEYWORD_LIST_ID = new ArrayList<>();
		List<String> ETQ$RECORD_ORDER = new ArrayList<>();

		sourceQuery = query(prop.getProperty("sourcequeryExceptStartsEtqEndsLookup"));
		while (sourceQuery.next()) {
			baseTableExceptStartsEtqEndsLookup.add(sourceQuery.getObject(1).toString());

		}

		for (String tableWithdataName : baseTableExceptStartsEtqEndsLookup) {
			sourceQuery = query(prop.getProperty("sourceQueryToIterateNTables2") + tableWithdataName);

			while (sourceQuery.next()) {
				LOCATION_PROFILE_ID.add(sourceQuery.getString("LOCATION_PROFILE_ID"));
				LOOKUP_ID.add(sourceQuery.getString("LOOKUP_ID"));
				MASTER_KEYWORD_LIST_ID.add(sourceQuery.getString("MASTER_KEYWORD_LIST_ID"));
				ETQ$RECORD_ORDER.add(sourceQuery.getString("ETQ$RECORD_ORDER"));

			}

		}

		List<String> TARGETLOCATION_PROFILE_ID = new ArrayList<>();
		List<String> TARGETLOOKUP_ID = new ArrayList<>();
		List<String> TARGETMASTER_KEYWORD_LIST_ID = new ArrayList<>();
		List<String> TARGETETQ$RECORD_ORDER = new ArrayList<>();
		targetQuery = query(prop.getProperty("targetlookups_locationtable2"));
		while (targetQuery.next()) {
			if (!targetQuery.getString("TARGETLOCATION_PROFILE_ID").equals(null)) {

				TARGETLOCATION_PROFILE_ID.add(targetQuery.getString("LOCATION_PROFILE_ID"));
			}
			if (!targetQuery.getString("TARGETLOOKUP_ID").equals(null)) {
				TARGETLOOKUP_ID.add(targetQuery.getString("LOOKUP_ID"));
			}
			if (!targetQuery.getString("TARGETMASTER_KEYWORD_LIST_ID").equals(null)) {
				TARGETMASTER_KEYWORD_LIST_ID.add(targetQuery.getString("MASTER_KEYWORD_LIST_ID"));
			}
			if (!targetQuery.getString("TARGETETQ$RECORD_ORDER").equals(null)) {
				TARGETETQ$RECORD_ORDER.add(targetQuery.getString("ETQ$RECORD_ORDER"));
			}
		}
//Verify if the records in tables not starting with etq ending with _LOCS are not Migrated to Location table 
		Assert.assertEquals((TARGETLOCATION_PROFILE_ID.containsAll(LOCATION_PROFILE_ID)), false);
		Assert.assertEquals(TARGETLOOKUP_ID.containsAll(LOOKUP_ID), false);
		Assert.assertEquals(TARGETMASTER_KEYWORD_LIST_ID.containsAll(MASTER_KEYWORD_LIST_ID), false);
		Assert.assertEquals(TARGETETQ$RECORD_ORDER.containsAll(ETQ$RECORD_ORDER), false);

		log.info(
				"TC_02 Verify if the tables not Starting with Etq and ending with _LOCS are not Migrated to Location table. Ended.......");
	}

	/**
	 * Verify Tables Not Starting With Etq$ ending with _locs are not migrated to
	 * the Values Table
	 * 
	 * @throws SQLException
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */

	@Test(enabled = true)
	public static void tc03_VerifyTablesStartingWithEtqEndingWithLOCSNotMigratedToValuesTable()
			throws JSchException, SftpException, Exception {

		log.info(
				"TC_03 Verify if the tables starting with etq and ending with _locs are not migrated to the Values Table. Started.......");

		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");

		prop = loadQueryFile("//src//test//resources//migration//queries//MMP564_query.properties");
		List<String> baseTableStartsEtqEndsLookup = new ArrayList<>();

		sourceQuery = query(prop.getProperty("sourcequeryStartsEtqEndsLookupForTc3"));
		while (sourceQuery.next()) {
			baseTableStartsEtqEndsLookup.add(sourceQuery.getObject(1).toString());

		}

		int sourceRowCount = 0;
		int sourceColumnCount = 0;
		int targetRowCount = 0;
		int targetColumnCount = 0;
		for (String tableWithdataName : baseTableStartsEtqEndsLookup) {
			sourceQuery = query(prop.getProperty("sourceQueryToIterateNTablesforTc3") + tableWithdataName);

			sourceRowCount = sourceRowCount + RowCount(sourceQuery);

			ResultSetMetaData columnData = sourceQuery.getMetaData();

			sourceColumnCount = columnData.getColumnCount();

		}

		targetQuery = targetQuery(prop.getProperty("targetlookups_locationtableForTc3"));

		targetRowCount = sourceRowCount + RowCount(sourceQuery);

		ResultSetMetaData targetcolumnData = sourceQuery.getMetaData();

		targetColumnCount = targetcolumnData.getColumnCount();

// Verify if total number of rows in target matches with the number of rows in source	
		Assert.assertEquals(sourceRowCount, targetRowCount);

//	Verify if total number of columns in target matches with the number of columns in source
		Assert.assertEquals(sourceColumnCount, targetColumnCount - 2);

//	Verify if no records are found in Lookup.Values Tables

		targetQuery = targetQuery(prop.getProperty("targetlookups_valuestableForTc3"+"\'" + envId + "\'"));

		Assert.assertEquals(RowCount(targetQuery), 0);

		log.info(
				"TC_03 Verify if the tables starting with etq and ending with _locs are not migrated to the Values Table. Ended.......");
		
	}

	@Test(enabled = true)
	public static void tc04_VerifyOverAllDataCountInSourceMatchesTheCountInTarget()
			throws JSchException, SftpException, Exception {

		log.info(
				"TC_04 Verify if the over all data count and the data in the Source for the Tables ending with _LOCS matches with the data count for the tables under Lookups.locations in the target. Started.......");
	String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");

		prop = loadQueryFile("//src//test//resources//migration//queries//MMP564_query.properties");
		List<String> baseTStartsEtqEndsLookup = new ArrayList<>();

		sourceQuery = query(prop.getProperty("sourcequeryStartsEtqEndsLookupForTc4"));

		while (sourceQuery.next()) {
			baseTStartsEtqEndsLookup.add(sourceQuery.getObject(1).toString());
		}

		int sourceRowCount = 0;
		int targetRowCount = 0;
		for (String tableWithdataName : baseTStartsEtqEndsLookup) {
			sourceQuery = query(prop.getProperty("sourceQueryToIterateNTables2") + tableWithdataName);
			sourceRowCount = sourceRowCount + RowCount(sourceQuery);
		}

		targetQuery = targetQuery(prop.getProperty("targetLookups_LocationTableForTc4"));
		targetRowCount = RowCount(targetQuery);

		Assert.assertEquals(sourceRowCount, targetRowCount);
	
		log.info(
				"TC_04 Verify if the over all data count and the data in the Source for the Tables ending with _LOCS matches with the data count for the tables under Lookups.locations in the target. Ended.......");
	}

}
