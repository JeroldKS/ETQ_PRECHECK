package migrationStories;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP563_CoreLookupSchematablemergeintovaluestable extends Base {

	static Logger log = Logger.getLogger(MMP563_CoreLookupSchematablemergeintovaluestable.class.getName());

	/** Verify if Migration tool has the ability to migrate all the eligible lookup schema tables with data into one lookup table (Table Name: lookup.values) in the target DB
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_VerifyIfSourceLookupsTablesAndDataAreMigratedToASingleValuesTableInTarget()
			throws JSchException, SftpException, Exception {
		log.info("TC01_Verify if Migration tool has the ability to migrate all the eligible lookup schema tables with data into one lookup table (Table Name: lookup.values) in the target DB...is started");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP563_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();
		establishDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		Set<String> sourceMigrateList = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		sourceQuery = query(prop.getProperty("sourcemigratequery"));
		while (sourceQuery.next()) {
			sourceMigrateList.add(String.valueOf(sourceQuery.getObject(1)));
		}
		Set<String> targetMtType0List = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		targetQuery = targetQuery(prop.getProperty("targetmttype0query"));
		while (targetQuery.next()) {
			targetMtType0List.add(String.valueOf(targetQuery.getObject(1)));
		}
		Set<String> targetMt23List = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		String targetAppendEnv = envId + "'";
		targetQuery = targetQuery(prop.getProperty("targetmt23tablequery") + targetAppendEnv);
		while (targetQuery.next()) {
			targetMt23List.add(String.valueOf(targetQuery.getObject(1)));
		}
		sourceMigrateList.removeAll(targetMtType0List);
		sourceMigrateList.retainAll(targetMt23List);
		Assert.assertEquals(sourceMigrateList, targetMt23List);
		log.info("TC01_Verify if Migration tool has the ability to migrate all the eligible lookup schema tables with data into one lookup table (Table Name: lookup.values) in the target DB.. Ended.......");
	}

	/** Verify tables that start with "etq$" as prefix are not migrated from source to Target
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_VerifyTablesThatStartWithEtq$AsPrefixAreNotMigratedFromSourceToTarget()
			throws JSchException, SftpException, Exception {
		log.info("TC01_Verify tables that start with \"etq$\" as prefix are not migrated from source to Target...is started");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP563_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();
		establishDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> targetMigrated = new ArrayList<>();
		String targetAppendEnvId = envId + "'";
		targetQuery = targetQuery(prop.getProperty("migratewithoutetq") + targetAppendEnvId);
		while (targetQuery.next()) {
			targetMigrated.add(String.valueOf(targetQuery.getObject(1)));
		}
		Assert.assertEquals(targetMigrated.size(), 0);
		log.info("TC01_Verify tables that start with \"etq$\" as prefix are not migrated from source to Target.. Ended.......");
	}

	/** Verify if source tables and data are not migrated if the match is found as MT_Type is  0 in the target
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_VerifyIfSourceTablesAndDataAreNotMigratedIfTheMatchIsFoundAsMT_TypeIs0InTheTarget()
			throws JSchException, SftpException, Exception {
		log.info("TC03_Verify if source tables and data are not migrated if the match is found as MT_Type is  0 in the target...is started");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP563_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();
		establishDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> targetTableMigrated = new ArrayList<>();
		String targetAppendEnvId = envId + "'";
		targetQuery = targetQuery(prop.getProperty("migratedtablequery") + targetAppendEnvId);
		while (targetQuery.next()) {
			targetTableMigrated.add(String.valueOf(targetQuery.getObject(1)));
		}
		List<String> targetMtType0List = new ArrayList<>();
		targetQuery = targetQuery(prop.getProperty("targetmttype0query"));
		while (targetQuery.next()) {
			targetMtType0List.add(String.valueOf(targetQuery.getObject(1)));
		}
		List<String> sourceMigrateList = new ArrayList<>();
		sourceQuery = query(prop.getProperty("sourcemigratetable"));
		while (sourceQuery.next()) {
			sourceMigrateList.add(String.valueOf(sourceQuery.getObject(1)));
		}
		List<String> targetMt23List = new ArrayList<>();
		String targetAppendEnv = envId + "'";
		targetQuery = targetQuery(prop.getProperty("targetmt23tablequery") + targetAppendEnv);
		while (targetQuery.next()) {
			targetMt23List.add(String.valueOf(targetQuery.getObject(1)));
		}
		targetTableMigrated.retainAll(targetMtType0List);
		Assert.assertEquals(targetTableMigrated.size(), 0);
		sourceMigrateList.retainAll(targetMtType0List);
		Assert.assertNotEquals(sourceMigrateList, targetMt23List);
		log.info(
				"TC03_Verify if source tables and data are not migrated if the match is found as MT_Type is  0 in the target.. Ended.......");
	}

	/**  Verify records with MT_TYPE as 2 and ENVIRONMENT_UUID are inserted in the target if the table name exist and MT_Type is '1' in the target DB
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc04_verifyIfMtType1TablesAreInsertedAsMttype2InToTarget()
			throws JSchException, SftpException, Exception {
		log.info("TC04_Verify records with MT_TYPE as 2 and ENVIRONMENT_UUID are inserted in the target if the table name exist and MT_Type is '1' in the target DB...is started");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP563_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();
		establishDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		Set<String> targetTableMigrated = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		String targetAppendEnvId = envId + "'";
		targetQuery = targetQuery(prop.getProperty("migratedmttype2query") + targetAppendEnvId);
		while (targetQuery.next()) {
			targetTableMigrated.add(String.valueOf(targetQuery.getObject(1)));
		}
		Set<String> targetMTtype1List = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		targetQuery = targetQuery(prop.getProperty("targetmttype1query"));
		while (targetQuery.next()) {
			targetMTtype1List.add(String.valueOf(targetQuery.getObject(1)));
		}
		Set<String> originalMt1List = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		originalMt1List.addAll(targetMTtype1List);
		Set<String> sourceMigrateList = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		sourceQuery = query(prop.getProperty("sourcemigratetable"));
		while (sourceQuery.next()) {
			sourceMigrateList.add(String.valueOf(sourceQuery.getObject(1)));
		}
		targetMTtype1List.retainAll(targetTableMigrated);
		Assert.assertEquals(targetMTtype1List.size(), targetTableMigrated.size());
		sourceMigrateList.retainAll(targetMTtype1List);
		Assert.assertEquals(sourceMigrateList, targetTableMigrated);
		log.info("TC04_Verify records with MT_TYPE as 2 and ENVIRONMENT_UUID are inserted in the target if the table name exist and MT_Type is '1' in the target DB.. Ended.......");
	}

	/**  Verify records with MT_TYPE as 3 and ENVIRONMENT_UUID are inserted in the target if the table name does not exist
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc05_verifyIfMtType3AreInsertedWhenTheTableNameDoesNotMatch()
			throws JSchException, SftpException, Exception {
		log.info("TC05_Verify records with MT_TYPE as 3 and ENVIRONMENT_UUID are inserted in the target if the table name does not exist...is started");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP563_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();
		establishDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> targeteMigratedMt3 = new ArrayList<>();
		String targetAppendEnvId = envId + "'";
		targetQuery = targetQuery(prop.getProperty("targetmttype3query") + targetAppendEnvId);
		while (targetQuery.next()) {
			targeteMigratedMt3.add(String.valueOf(targetQuery.getObject(1)));
		}
		List<String> targetWithouttMt3List = new ArrayList<>();
		targetQuery = targetQuery(prop.getProperty("targetexcept3query"));
		while (targetQuery.next()) {
			targetWithouttMt3List.add(String.valueOf(targetQuery.getObject(1)));
		}
		List<String> migrateSourceTableList = new ArrayList<>();
		targetQuery = targetQuery(prop.getProperty("lookupstablewithoutetq"));
		while (targetQuery.next()) {
			migrateSourceTableList.add(String.valueOf(targetQuery.getObject(1)));
		}
		Set<String> originalMt3List = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		originalMt3List.addAll(targeteMigratedMt3);
		targeteMigratedMt3.retainAll(targetWithouttMt3List);
		Assert.assertEquals(targeteMigratedMt3.size(), 0);
		migrateSourceTableList.retainAll(targeteMigratedMt3);
		Assert.assertEquals(migrateSourceTableList, targeteMigratedMt3);
		log.info("TC05_Verify records with MT_TYPE as 3 and ENVIRONMENT_UUID are inserted in the target if the table name does not exist.. Ended.......");
	}
	/** Verify if all the records in the source table are inserted in the target lookups.values, striping with ENVIRONMENT_UUID and MT_TYPE as 2 or 3
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc06_VerifyIfAllRecordsInTheSourceAreMigratedToTargetWithMt3ToTarget()
			throws JSchException, SftpException, Exception {
		log.info("TC06_Verify if all the records in the source table are inserted in the target lookups.values, striping with ENVIRONMENT_UUID and MT_TYPE as 2 or 3...is started");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP563_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();
		establishDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		Set<String> targetMtType0List = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		targetQuery = targetQuery(prop.getProperty("targetmttype0query"));
		while (targetQuery.next()) {
			targetMtType0List.add(String.valueOf(targetQuery.getObject(1)));
		}
		Set<String> migrateSourceTableList = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		sourceQuery = query(prop.getProperty("sourcemigratequery"));
		while (sourceQuery.next()) {
			migrateSourceTableList.add(String.valueOf(sourceQuery.getObject(1)));
		}
		migrateSourceTableList.removeAll(targetMtType0List);
		for (String sourceTable : migrateSourceTableList) {
			List<String> sourceDataList = new ArrayList<>();
			sourceQuery = query("SELECT * FROM lookups." + sourceTable);
			while (sourceQuery.next()) {
				sourceDataList.add(String.valueOf(sourceQuery.getObject(1)) + ","
						+ String.valueOf(sourceQuery.getObject("DESCRIPTION")) + ","
						+ String.valueOf(sourceQuery.getObject("ETQ$RECORD_ORDER")) + ","
						+ String.valueOf(sourceQuery.getObject("ETQ$IS_DISABLED")) + "," + sourceTable + ",Varchar");
			}
			List<String> targetDataList = new ArrayList<>();
			targetQuery = targetQuery(
					"select LOOKUP_ID, LOOKUP_NAME, ETQ$RECORD_ORDER, ETQ$IS_DISABLED, LOOKUP_TABLE, LOOKUP_DATA_TYPE FROM lookups.values where ENVIRONMENT_UUID = '"
							+ envId + "' " + "and LOOKUP_TABLE = '" + sourceTable + "' order by LOOKUP_ID");
			while (targetQuery.next()) {
				targetDataList.add(String.valueOf(targetQuery.getObject("LOOKUP_ID")) + ","
						+ String.valueOf(targetQuery.getObject("LOOKUP_NAME")) + ","
						+ String.valueOf(targetQuery.getObject("ETQ$RECORD_ORDER")) + ","
						+ String.valueOf(targetQuery.getObject("ETQ$IS_DISABLED")) + ","
						+ String.valueOf(targetQuery.getObject("LOOKUP_TABLE")) + ","
						+ String.valueOf(targetQuery.getObject("LOOKUP_DATA_TYPE")));

			}
			Assert.assertEquals(sourceDataList, targetDataList, "error due to " + sourceTable);
		}
		log.info("TC06_Verify if all the records in the source table are inserted in the target lookups.values, striping with ENVIRONMENT_UUID and MT_TYPE as 2 or 3.. Ended.......");
	}

	/** Verify if all the columns are mapped properly to the target environment table (lookups.values)
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc07_VerifyIfAllTheColumnMappingAreDoneForTheMigratedDataInTarget()
			throws JSchException, SftpException, Exception {
		log.info("TC07_Verify if all the columns are mapped properly to the target environment table (lookups.values)...is started");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP563_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();
		establishDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		Set<String> targetMtType0List = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		targetQuery = targetQuery(prop.getProperty("targetmttype0query"));
		while (targetQuery.next()) {
			targetMtType0List.add(String.valueOf(targetQuery.getObject(1)));
		}
		Set<String> migrateSourceTableList = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		sourceQuery = query(prop.getProperty("sourcemigratequery"));
		while (sourceQuery.next()) {
			migrateSourceTableList.add(String.valueOf(sourceQuery.getObject(1)));
		}
		migrateSourceTableList.removeAll(targetMtType0List);
		for (String sourceTable : migrateSourceTableList) {
			List<String> sourceDataList = new ArrayList<>();
			sourceQuery = query("SELECT * FROM lookups." + sourceTable);
			while (sourceQuery.next()) {
				sourceDataList.add(String.valueOf(sourceQuery.getObject(1)) + ","
						+ String.valueOf(sourceQuery.getObject("DESCRIPTION")) + ","
						+ String.valueOf(sourceQuery.getObject("ETQ$RECORD_ORDER")) + ","
						+ String.valueOf(sourceQuery.getObject("ETQ$IS_DISABLED")) + "," + sourceTable + ",Varchar");
			}
			List<String> targetDataList = new ArrayList<>();
			targetQuery = targetQuery(
					"select LOOKUP_ID, LOOKUP_NAME, ETQ$RECORD_ORDER, ETQ$IS_DISABLED, LOOKUP_TABLE, LOOKUP_DATA_TYPE FROM lookups.values where ENVIRONMENT_UUID = '"
							+ envId + "' " + "and LOOKUP_TABLE = '" + sourceTable + "' order by LOOKUP_ID");
			while (targetQuery.next()) {
				targetDataList.add(String.valueOf(targetQuery.getObject("LOOKUP_ID")) + ","
						+ String.valueOf(targetQuery.getObject("LOOKUP_NAME")) + ","
						+ String.valueOf(targetQuery.getObject("ETQ$RECORD_ORDER")) + ","
						+ String.valueOf(targetQuery.getObject("ETQ$IS_DISABLED")) + ","
						+ String.valueOf(targetQuery.getObject("LOOKUP_TABLE")) + ","
						+ String.valueOf(targetQuery.getObject("LOOKUP_DATA_TYPE")));
			}
			Assert.assertEquals(sourceDataList, targetDataList, "error due to " + sourceTable);
		}
		log.info("TC07_Verify if all the columns are mapped properly to the target environment table (lookups.values).. Ended.......");
	}

	/** Verify if the over all data count and the data in the Source (excluding tables prefix as '$etq')' match with the data count for the tables under Lookups.Values in the target
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc08_VerifyIfOverAllDataCountMatchesWithSourceAndTargetForMigrtaedRecords()
			throws JSchException, SftpException, Exception {
		log.info("TC08_Verify if the over all data count and the data in the Source (excluding tables prefix as '$etq')' match with the data count for the tables under Lookups.Values in the target...is started");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP563_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();
		establishDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		Set<String> targetMtType0List = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		targetQuery = targetQuery(prop.getProperty("targetmttype0query"));
		while (targetQuery.next()) {
			targetMtType0List.add(String.valueOf(targetQuery.getObject(1)));
		}
		Set<String> migrateSourceTablelist = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		sourceQuery = query(prop.getProperty("sourcemigratequery"));
		while (sourceQuery.next()) {
			migrateSourceTablelist.add(String.valueOf(sourceQuery.getObject(1)));
		}
		Set<String> targetMt23List = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		String targetAppendEnv = envId + "'";
		targetQuery = targetQuery(prop.getProperty("targetmt23tablequery") + targetAppendEnv);
		while (targetQuery.next()) {
			targetMt23List.add(String.valueOf(targetQuery.getObject(1)));
		}
		migrateSourceTablelist.removeAll(targetMtType0List);
		migrateSourceTablelist.retainAll(targetMt23List);
		for (String sourceDataCount : migrateSourceTablelist) {
			Set<String> sourceDataCountList = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			sourceQuery = query("SELECT COUNT(*) FROM lookups." + sourceDataCount);
			while (sourceQuery.next()) {
				sourceDataCountList.add(String.valueOf(sourceQuery.getObject(1)));

				Set<String> targetDataCountList = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
				targetQuery = targetQuery("select COUNT(*) FROM lookups.values where ENVIRONMENT_UUID = '" + envId
						+ "' " + "and LOOKUP_TABLE = '" + sourceDataCount + "'");
				while (targetQuery.next()) {
					targetDataCountList.add(String.valueOf(targetQuery.getObject(1)));

				}
			}
		}
		log.info("TC08_Verify if the over all data count and the data in the Source (excluding tables prefix as '$etq')' match with the data count for the tables under Lookups.Values in the target.. Ended.......");
	}
}
