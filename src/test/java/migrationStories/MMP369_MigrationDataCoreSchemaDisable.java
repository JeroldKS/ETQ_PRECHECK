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
		establishSshConnectionForSourceInstance();
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP369_query.properties");
		List<String> indexesInDB = new ArrayList<>();
		targetQuery = targetQuery(prop.getProperty("list_of_tasks")+"\'"+envId+"\'");
		while (targetQuery.next()) {
			indexesInDB.add(targetQuery.getObject(1).toString());
		}
		Assert.assertTrue(!indexesInDB.isEmpty());
		targetDBConnection.close();
		log.info("TC 01 Checking whether Engine schema is successfully migrated. Ended.......");
	}
	
	/**
	 * Checking whether the engine schema is migrated or not
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_verifyisEnabledSetTo0() throws JSchException, SftpException, Exception {
		log.info("TC 02 Verify whether IS_ENABLED column is set to 0 in the task_settings table. Started.......");
		establishSshConnectionForSourceInstance();
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP369_query.properties");
		List<String> indexesInDB = new ArrayList<>();
		targetQuery = targetQuery(prop.getProperty("list_of_tasks")+"\'"+envId+"\'");
		while (targetQuery.next()) {
			indexesInDB.add(targetQuery.getObject(3).toString());
		}
		Assert.assertTrue(!indexesInDB.isEmpty());
		Assert.assertTrue(!indexesInDB.contains("1"));
		targetDBConnection.close();
		log.info("TC 02 Verify whether IS_ENABLED column is set to 0 in the task_settings table. Ended.......");
	}
}
