package migrationStories;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP369_MigrationDataCoreSchemaDisable extends Base {
 
	static Logger log = Logger.getLogger(MMP369_MigrationDataCoreSchemaDisable.class.getName());
	
	/**
	 * Checking whether the engine schema is migrated or not
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_verifyEngineSchemaMigrated() throws JSchException, SftpException, Exception {
		log.info("TC 01 Checking whether Engine schema is successfully migrated. Started.......");
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP369_query.properties");
		List<String> indexesInDB = new ArrayList<>();
		String tarappendEnvId = envId+"'";
		targetQuery = targetQuery(prop.getProperty("list_of_tasks")+tarappendEnvId);
		while (targetQuery.next()) {
			indexesInDB.add(String.valueOf(targetQuery.getObject(1)));
		}
		Assert.assertNotEquals(indexesInDB.size(), 0, "Engine schema is not migrated for the Env Id: "+envId);
		targetDBConnection.close();
		log.info("TC 01 Checking whether Engine schema is successfully migrated. Ended.......");
	}
	
	/**
	 * Verify the IS_ENABLED column is set to "0" for the listed "task_name" in table "task_settings" on the target environment with the respective Environment ID
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_verifyisEnabledSetTo0() throws JSchException, SftpException, Exception {
		log.info("TC 02 Verify whether IS_ENABLED column is set to 0 in the task_settings table. Started.......");
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP369_query.properties");
		List<String> indexesInDB = new ArrayList<>();
		String tarappendEnvId = envId+"'";
		targetQuery = targetQuery(prop.getProperty("list_of_tasks")+tarappendEnvId);
		while (targetQuery.next()) {
			indexesInDB.add(String.valueOf(targetQuery.getObject(3)));
			Assert.assertEquals(String.valueOf(targetQuery.getObject(3)), "0", "IS_ENABLED column is not set to 0 for the"
					+ "task name : "+String.valueOf(targetQuery.getObject(1)));
		}
		Assert.assertNotEquals(indexesInDB.size(), 0,  "Engine schema is not migrated for the Env Id: "+envId);
		targetDBConnection.close();
		log.info("TC 02 Verify whether IS_ENABLED column is set to 0 in the task_settings table. Ended.......");
	}
}
