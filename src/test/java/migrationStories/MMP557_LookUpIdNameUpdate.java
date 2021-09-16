package migrationStories;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP557_LookUpIdNameUpdate extends Base {
 
	static Logger log = Logger.getLogger(MMP557_LookUpIdNameUpdate.class.getName());
	
	/**
	 * Verify if the listed queries are executed to update the Lookup_ID and Lookup_Name reference in the target environment after the Migration
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_verifyLookupIdNameUpdated() throws JSchException, SftpException, Exception {
		log.info("TC 01 Verify if the listed queries are executed to update the Lookup_ID and Lookup_Name reference in the target environment after the Migration. Started.......");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP557_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();		
		Assert.assertEquals(connectionStatus, "Connection Success");
		String tarappendEnvId = envId+"'";
		targetQuery = targetQuery(prop.getProperty("get_datacenter_lookups")+tarappendEnvId);
		while (targetQuery.next()) {
			Assert.assertEquals(String.valueOf(targetQuery.getObject(1))+","+String.valueOf(targetQuery.getObject(2)), "LOOKUP_ID,LOOKUP_NAME");
		}
		
		targetQuery = targetQuery(prop.getProperty("get_engine_lookups")+tarappendEnvId);
		while (targetQuery.next()) {
			Assert.assertEquals(String.valueOf(targetQuery.getObject(1)), "LOOKUP_ID");
		}
		targetDBConnection.close();
		log.info("TC 01 Verify if the listed queries are executed to update the Lookup_ID and Lookup_Name reference in the target environment after the Migration. Ended.......");
	}
	
	/**
	 * Verify if the listed queries are executed only for the environment id, configured in property.toml file to update the Lookup_ID and Lookup_Name reference in the target environment.
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_verifyLookupQueryExecutedOnlyForGivenEnvId() throws JSchException, SftpException, Exception {
		log.info("TC 01 Verify if the listed queries are executed only for the environment id, configured in property.toml file to update the Lookup_ID and Lookup_Name reference in the target environment. Started.......");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP557_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();		
		Assert.assertEquals(connectionStatus, "Connection Success");
		targetQuery = targetQuery(prop.getProperty("get_datacenter_lookups_envId"));
		while (targetQuery.next()) {
			Assert.assertEquals(String.valueOf(targetQuery.getObject(1)), envId);
		}
		targetQuery = targetQuery(prop.getProperty("get_engine_lookups_envId"));
		while (targetQuery.next()) {
			Assert.assertEquals(String.valueOf(targetQuery.getObject(1)), envId);
		}
		dbConnection.close();
		targetDBConnection.close();
		log.info("TC 01 Verify if the listed queries are executed only for the environment id, configured in property.toml file to update the Lookup_ID and Lookup_Name reference in the target environment. Ended.......");
	}
	
}