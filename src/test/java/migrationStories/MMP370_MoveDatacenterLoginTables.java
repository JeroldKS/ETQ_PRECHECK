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

public class MMP370_MoveDatacenterLoginTables extends Base {
 
	static Logger log = Logger.getLogger(MMP370_MoveDatacenterLoginTables.class.getName());
	
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
		envId = envId.replaceAll("-", "");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP370_query.properties");
		List<String> loginTablesInTarget = new ArrayList<>();
		String loginTables = " ('login_tracker_"+envId+"','login_related_groups_"+envId+"','user_login_counters_"+envId+"')";
		targetQuery = targetQuery(prop.getProperty("login_tables")+loginTables);
		while (targetQuery.next()) {
			loginTablesInTarget.add(String.valueOf(targetQuery.getObject(3)));
		}
		Collections.sort(loginTablesInTarget);
		Assert.assertEquals(loginTablesInTarget.size(),3, "login_related_groups, login_tracker, user_login_counters"
				+ " tables are not moved to target for the env Id: "+envId);
		Assert.assertEquals(loginTablesInTarget.get(0),"login_related_groups_"+envId+"","login_related_groups"
				+ " table is not moved to target for the env Id: "+envId);
		Assert.assertEquals(loginTablesInTarget.get(1),"login_tracker_"+envId+"","login_tracker"
				+ " table is not moved to target for the env Id: "+envId);
		Assert.assertEquals(loginTablesInTarget.get(2),"user_login_counters_"+envId+"", "user_login_counters"
				+ " table is not moved to target for the env Id: "+envId);
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
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		envId = envId.replaceAll("-", "");
		List<String> loginTablesInTarget = new ArrayList<>();
		String loginTables = " ('login_tracker_"+envId+"')";
		targetQuery = targetQuery(prop.getProperty("login_tables")+loginTables);
		while (targetQuery.next()) {
			loginTablesInTarget.add(String.valueOf(targetQuery.getObject(3)));
		}
		Assert.assertEquals(loginTablesInTarget.size(),1, "login_tracker"
				+ " table is not moved to target for the env Id: "+envId);
		Assert.assertEquals(loginTablesInTarget.get(0),"login_tracker_"+envId+"","login_tracker"
				+ " table is not moved to target for the env Id: "+envId);
		
		establishDatabaseconnection();
		List<String> loginTrackerInSource = new ArrayList<>();
		sourceQuery = query(prop.getProperty("datacenter_login_tracker_source"));
		int columnCount = sourceQuery.getMetaData().getColumnCount();
		while (sourceQuery.next()) {
			for (int i = 1; i <= columnCount; i++) {
				loginTrackerInSource.add(String.valueOf(sourceQuery.getObject(i)));
			}
		}
		
		List<String> loginTrackerInTarget = new ArrayList<>();
		String appendEnvId = envId+"` LIMIT 5";
		targetQuery = targetQuery(prop.getProperty("env_login_counters_login_tracker_target")+appendEnvId);
		
		while (targetQuery.next()) {
			for (int i = 1; i <= columnCount; i++) {
				loginTrackerInTarget.add(String.valueOf(targetQuery.getObject(i)));
			}
		}
		dbConnection.close();
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
		
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		envId = envId.replaceAll("-", "");
		List<String> loginTablesInTarget = new ArrayList<>();
		String loginTables = " ('login_related_groups_"+envId+"')";
		targetQuery = targetQuery(prop.getProperty("login_tables")+loginTables);
		while (targetQuery.next()) {
			loginTablesInTarget.add(String.valueOf(targetQuery.getObject(3)));
		}
		Assert.assertEquals(loginTablesInTarget.size(),1, "login_related_groups"
				+ " table is not moved to target for the env Id: "+envId);
		Assert.assertEquals(loginTablesInTarget.get(0),"login_related_groups_"+envId+"","login_related_groups"
				+ " table is not moved to target for the env Id: "+envId);
		
		establishDatabaseconnection();
		List<String> loginRelatedGroupsInSource = new ArrayList<>();
		sourceQuery = query(prop.getProperty("datacenter_login_related_groups_source"));
		int columnCount = sourceQuery.getMetaData().getColumnCount();
		while (sourceQuery.next()) {
			for (int i = 1; i <= columnCount; i++) {
				loginRelatedGroupsInSource.add(String.valueOf(sourceQuery.getObject(i)));
			}
		}
		dbConnection.close();
		List<String> loginRelatedGroupsInTarget = new ArrayList<>();
		String appendEnvId = envId+"` LIMIT 5";
		targetQuery = targetQuery(prop.getProperty("env_login_counters_login_tracker_target")+appendEnvId);
		
		while (targetQuery.next()) {
			for (int i = 1; i <= columnCount; i++) {
				loginRelatedGroupsInTarget.add(String.valueOf(targetQuery.getObject(i)));
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
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP370_query.properties");
		
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		envId = envId.replaceAll("-", "");
		List<String> loginTablesInTarget = new ArrayList<>();
		String loginTables = " ('user_login_counters_"+envId+"')";
		targetQuery = targetQuery(prop.getProperty("login_tables")+loginTables);
		while (targetQuery.next()) {
			loginTablesInTarget.add(String.valueOf(targetQuery.getObject(3)));
		}
		Assert.assertEquals(loginTablesInTarget.size(),1, "user_login_counters"
				+ " table is not moved to target for the env Id: "+envId);
		Assert.assertEquals(loginTablesInTarget.get(0),"user_login_counters_"+envId+"","user_login_counters_"
				+ " table is not moved to target for the env Id: "+envId);
		
		List<String> userLoginCountersData = new ArrayList<>();
		String envIdToAppend = envId+"`;";
		targetQuery = targetQuery(prop.getProperty("user_login_counters_data")+envIdToAppend);
		while (targetQuery.next()) {
			userLoginCountersData.add(String.valueOf(targetQuery.getObject(1)));
		}
		Assert.assertEquals(userLoginCountersData.size(), 0);
		targetDBConnection.close();
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
		envId = envId.replaceAll("-", "");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP370_query.properties");
		List<String> dataCenterLoginTablesInTarget = new ArrayList<>();
		String loginTables = " ('login_tracker_"+envId+"','login_related_groups_"+envId+"')";
		targetQuery = targetQuery(prop.getProperty("datacenter_login_tables")+loginTables);
		while (targetQuery.next()) {
			dataCenterLoginTablesInTarget.add(String.valueOf(targetQuery.getObject(1)));
		}
		Assert.assertEquals(dataCenterLoginTablesInTarget.size(),0);
		targetDBConnection.close();
		log.info("TC 05 Verifying that the \"datacenter.login_tracker\" and  \"datacenter.login_related_groups\" tables are not created in the target database. Ended.......");
	}
}
