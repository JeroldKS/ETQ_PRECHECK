package migrationStories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP370_MoveLoginRecord extends Base {
 
	static Logger log = Logger.getLogger(MMP370_MoveLoginRecord.class.getName());
	
	/**
	 * Checking whether the Login tables are created in target
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_verifyLoginTablesMoved() throws JSchException, SftpException, Exception {
		log.info("TC 01 Verifying that login_tracker, login_related_groups and user_login_counters tables are created in the target DB with environment ID. Started.......");
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP370_query.properties");
		List<String> loginTablesInTarget = new ArrayList<>();
		String loginTables = " ('login_tracker_"+envId+"','login_related_groups_"+envId+"','user_login_counters_"+envId+"')";
		targetQuery = targetQuery(prop.getProperty("login_tables")+loginTables);
		while (targetQuery.next()) {
			loginTablesInTarget.add(targetQuery.getObject(3).toString());
		}
		Collections.sort(loginTablesInTarget);
		Assert.assertEquals(loginTablesInTarget.size(),3);
		Assert.assertEquals(loginTablesInTarget.get(0),"login_related_groups_"+envId+"");
		Assert.assertEquals(loginTablesInTarget.get(1),"login_tracker_"+envId+"");
		Assert.assertEquals(loginTablesInTarget.get(2),"user_login_counters_"+envId+"");
		targetDBConnection.close();
		log.info("TC 01 Verifying that login_tracker, login_related_groups and user_login_counters tables are created in the target DB with environment ID. Ended.......");
	}
	
	/**
	 * Checking whether the Verify that the "datacenter.login_tracker" table data is migrated to target database in the table "env_login_counters.login_tracker_<<ENV_ID>>"
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_verifyLoginTrackerDataMoved() throws JSchException, SftpException, Exception {
		log.info("TC 02 Verifying that that the \"datacenter.login_tracker\" table data is migrated to target database in the table \"env_login_counters.login_tracker_<<ENV_ID>>\". Started.......");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP370_query.properties");
		establishDatabaseconnection();
		List<String> loginTrackerInSource = new ArrayList<>();
		sourceQuery = query(prop.getProperty("datacenter_login_tracker_source"));
		int columnCount = sourceQuery.getMetaData().getColumnCount();
		while (sourceQuery.next()) {
			for (int i = 1; i <= columnCount; i++) {
				loginTrackerInSource.add(sourceQuery.getObject(i).toString());
			}
		}
		dbConnection.close();
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> loginTrackerInTarget = new ArrayList<>();
		String appendEnvId = envId+"` LIMIT 5";
		targetQuery = targetQuery(prop.getProperty("env_login_counters_login_tracker_target")+appendEnvId);
		
		while (targetQuery.next()) {
			for (int i = 1; i <= columnCount; i++) {
				loginTrackerInTarget.add(targetQuery.getObject(i).toString());
			}
		}
		targetDBConnection.close();
		Collections.sort(loginTrackerInSource);
		Collections.sort(loginTrackerInTarget);
		Assert.assertEquals(loginTrackerInTarget, loginTrackerInSource);
		log.info("TC 02 Verifying that that the \"datacenter.login_tracker\" table data is migrated to target database in the table \"env_login_counters.login_tracker_<<ENV_ID>>\". Ended.......");
	}
	
	/**
	 * Checking whether the Verify that the "datacenter.login_related_groups" table data is migrated to target database in the table "env_login_counters.login_related_groups_<<ENV_ID>>"
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_verifyLoginRelatedGroupsDataMoved() throws JSchException, SftpException, Exception {
		log.info("TC 03 Verifying that that the \"datacenter.login_related_groups\" table data is migrated to target database in the table \"env_login_counters.login_related_groups_<<ENV_ID>>\". Started.......");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP370_query.properties");
		establishDatabaseconnection();
		List<String> loginRelatedGroupsInSource = new ArrayList<>();
		sourceQuery = query(prop.getProperty("datacenter_login_related_groups_source"));
		int columnCount = sourceQuery.getMetaData().getColumnCount();
		while (sourceQuery.next()) {
			for (int i = 1; i <= columnCount; i++) {
				loginRelatedGroupsInSource.add(sourceQuery.getObject(i).toString());
			}
		}
		dbConnection.close();
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> loginRelatedGroupsInTarget = new ArrayList<>();
		String appendEnvId = envId+"` LIMIT 5";
		targetQuery = targetQuery(prop.getProperty("env_login_counters_login_tracker_target")+appendEnvId);
		
		while (targetQuery.next()) {
			for (int i = 1; i <= columnCount; i++) {
				loginRelatedGroupsInTarget.add(targetQuery.getObject(i).toString());
			}
		}
		targetDBConnection.close();
		Collections.sort(loginRelatedGroupsInSource);
		Collections.sort(loginRelatedGroupsInTarget);
		Assert.assertEquals(loginRelatedGroupsInTarget, loginRelatedGroupsInSource);
		log.info("TC 03 Verifying that that the \"datacenter.login_tracker\" table data is migrated to target database in the table \"env_login_counters.login_tracker_<<ENV_ID>>\". Ended.......");
	}
	
	/**
	 * Checking whether the env_login_counters.user_login_counters_<<ENV_ID>> table is created with empty data
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc04_verifyNoDataMigratedToUserLoginCounters() throws JSchException, SftpException, Exception {
		log.info("TC 04 Verifying that no data is migrated in the table \"env_login_counters.user_login_counters_<<ENV_ID>>\". Started.......");
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP370_query.properties");
		List<String> loginTablesInTarget = new ArrayList<>();
		String loginTables = " ('user_login_counters_"+envId+"')";
		targetQuery = targetQuery(prop.getProperty("login_tables")+loginTables);
		while (targetQuery.next()) {
			loginTablesInTarget.add(targetQuery.getObject(3).toString());
		}
		Assert.assertEquals(loginTablesInTarget.size(),1);
		List<String> userLoginCountersData = new ArrayList<>();
		String envIdToAppend = envId+"`;";
		targetQuery = targetQuery(prop.getProperty("user_login_counters_data")+envIdToAppend);
		while (targetQuery.next()) {
			userLoginCountersData.add(targetQuery.getObject(1).toString());
		}
		Assert.assertEquals(userLoginCountersData.size(), 0);
		//targetDBConnection.close();
		log.info("TC 04 Verifying that no data is migrated in the table \\\"env_login_counters.user_login_counters_<<ENV_ID>>\\\". Ended.......");
	}
	
	/**
	 * Checking whether datacenter.login_tracker and datacenter.login_related_groups are not created in target
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc05_verifyDataCenterLoginTablesNotCreated() throws JSchException, SftpException, Exception {
		log.info("TC 05 Verifying that the \"datacenter.login_tracker\" and  \"datacenter.login_related_groups\" tables are not created in the target database. Started.......");
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP370_query.properties");
		List<String> dataCenterLoginTablesInTarget = new ArrayList<>();
		targetQuery = targetQuery(prop.getProperty("datacenter_login_tables"));
		while (targetQuery.next()) {
			dataCenterLoginTablesInTarget.add(targetQuery.getObject(0).toString());
		}
		Assert.assertEquals(dataCenterLoginTablesInTarget.size(),0);
		//targetDBConnection.close();
		log.info("TC 05 Verifying that the \"datacenter.login_tracker\" and  \"datacenter.login_related_groups\" tables are not created in the target database. Ended.......");
	}
}
