package migrationStories;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP_561_RemoveCoreSchemaForeignKeysfromNonCoreSchema extends Base {

	static Logger log = Logger.getLogger(MMP_561_RemoveCoreSchemaForeignKeysfromNonCoreSchema.class.getName());

	/**
	 * Verify all the foreign keys from Non core schemas that are connected to
	 * Engine schema are deleted
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_VerifyAllTheForeignKeysFromNonCoreSchemasThatAreConnectedToEngineSchemaAreDeleted()
			throws JSchException, SftpException, Exception {
		log.info(
				"TC01_Verify all the foreign keys from Non core schemas that are connected to Engine schema are deleted...is started");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP561_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();
		establishDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> engineForeignKeyList = new ArrayList<>();
		String nonCoreSchemas = "";
		String targetAppendEnvId = envId + "'";
		targetQuery = targetQuery(prop.getProperty("noncoreschematargetquery") + targetAppendEnvId);
		while (targetQuery.next()) {
			nonCoreSchemas += String.valueOf(targetQuery.getObject(1)) + "','";
		}
		nonCoreSchemas = nonCoreSchemas.substring(0, nonCoreSchemas.length() - 2) + ")";
		targetQuery = targetQuery(prop.getProperty("enginefkquery") + nonCoreSchemas);
		while (targetQuery.next()) {
			engineForeignKeyList.add(String.valueOf(targetQuery.getObject(1)));
		}
		Assert.assertEquals(engineForeignKeyList.size(), 0);
		log.info(
				"Verify all the foreign keys from Non core schemas that are connected to Engine schema are deleted.. Ended.......");
	}

	/**
	 * Verify all the foreign keys from Non core schemas that are connected to
	 * Lookup schema are deleted
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_VerifyAllTheForeignKeysFromNonCoreSchemasThatAreConnectedToLookupsSchemaAreDeleted()
			throws JSchException, SftpException, Exception {
		log.info(
				"TC02_Verify all the foreign keys from Non core schemas that are connected to Lookup schema are deleted...is started");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP561_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();
		establishDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> engineForeignKeyList = new ArrayList<>();
		String nonCoreSchemas = "";
		String targetAppendEnvId = envId + "'";
		targetQuery = targetQuery(prop.getProperty("noncoreschematargetquery") + targetAppendEnvId);
		while (targetQuery.next()) {
			nonCoreSchemas += String.valueOf(targetQuery.getObject(1)) + "','";
		}
		nonCoreSchemas = nonCoreSchemas.substring(0, nonCoreSchemas.length() - 2) + ")";
		targetQuery = targetQuery(prop.getProperty("lookupforeignkeyquery") + nonCoreSchemas);
		while (targetQuery.next()) {
			engineForeignKeyList.add(String.valueOf(targetQuery.getObject(1)));
		}
		Assert.assertEquals(engineForeignKeyList.size(), 0);
		log.info(
				"Verify all the foreign keys from Non core schemas that are connected to Lookup schema are deleted... Ended.......");
	}

	/**
	 * Verify the foreign keys from core schemas that are connected to Lookup and
	 * Engine schema are not deleted
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc04_VerifyAllTheForeignKeysFromCoreSchemasThatAreConnectedToEngineAndLookupsSchemaAreDeleted()
			throws JSchException, SftpException, Exception {
		log.info(
				"TC04_Verify the foreign keys from core schemas that are connected to Lookup and Engine schema are not deleted...is started");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP561_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();
		establishDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> NonCoreSchemasInTarget = new ArrayList<>();
		targetQuery = targetQuery(prop.getProperty("coreforeignkeyquery"));
		while (targetQuery.next()) {
			NonCoreSchemasInTarget.add(String.valueOf(targetQuery.getObject(1)));
		}
		Assert.assertNotEquals(NonCoreSchemasInTarget.size(), 0);
		log.info(
				"Verify the foreign keys from core schemas that are connected to Lookup and Engine schema are not deleted... Ended.......");
	}

}