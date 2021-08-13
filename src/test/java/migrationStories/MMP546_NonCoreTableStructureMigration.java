package migrationStories;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP546_NonCoreTableStructureMigration extends Base {
 
	static Logger log = Logger.getLogger(MMP546_NonCoreTableStructureMigration.class.getName());
	/**
	 * Verify if the Core schemas are not appended with Environment ID in the Target after migration
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_verifySchemasExclusionListAvailable() throws JSchException, SftpException, Exception {
		log.info("TC 01 Verifying that the Core schema and Non-Core schemas exclusion list is available in the Config file before Migration. Started.......");
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("mysql_py_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("mysql_py_linux"));
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line;
		String exclusionListAvailability = "Exclusion List Not Available";
		while ((line = br.readLine()) != null) {
			if(line.contains("CORE_SCHEMA") && !line.contains("#")) {
				exclusionListAvailability = "Exclusion List Not Available";
			}
		}
		Assert.assertEquals(exclusionListAvailability,"Exclusion List Not Available");
		session.disconnect();
		log.info("TC 01 Verifying that the Core schema and Non-Core schemas exclusion list is available in the Config file before Migration. Ended.......");
	}
	
	
	/**
	 * Checking whether the Non-core schema Table structures alone are migrated post migration
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_verifyNonCoreSchemaTableStructureMigrated() throws JSchException, SftpException, Exception {
		log.info("TC 03 Verifying that the Non-core Schema Table structures alone are migrated post migration. Started.......");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP546_query.properties");
		establishDatabaseconnection();
		String connectionStatus = establishTargetDatabaseconnection();		
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> nonCoreSchemaTablesInSource = new ArrayList<>();
		List<String> nonCoreSchemaTablesInTarget = new ArrayList<>();
		sourceQuery = query(prop.getProperty("get_non_core_schema_tables_source"));
		while (sourceQuery.next()) {
			nonCoreSchemaTablesInSource.add(sourceQuery.getObject(1).toString());
		}
		String tarappendEnvId = envId+"'";
		targetQuery = targetQuery(prop.getProperty("get_non_core_schema_tables_target")+tarappendEnvId);
		while (targetQuery.next()) {
			nonCoreSchemaTablesInTarget.add(String.valueOf(targetQuery.getObject(1)));
		}
		dbConnection.close();
		targetDBConnection.close();
		Collections.sort(nonCoreSchemaTablesInTarget);
		Collections.sort(nonCoreSchemaTablesInSource);
		Assert.assertEquals(nonCoreSchemaTablesInTarget,nonCoreSchemaTablesInSource, "Some of the Non Core Schema Table Structures are not migrated");
		log.info("TC 03 Verifying that the Non-core schema Table structures alone are migrated post migration. Ended.......");
	}
	
	/**
	 * Verify if the Environment ID is appended only to the Non-Core Schema name in the Target after Migration
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc05_verifyallNonCoreSchemasAppendedwithenvidintheTarget() throws JSchException, SftpException, Exception {
		log.info("TC 05 Verify if the Environment ID is appended only to the Non-Core Schema name in the Target after Migration. Started...");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP546_query.properties");
		establishDatabaseconnection();
		String connectionStatus = establishTargetDatabaseconnection();		
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> nonCoreSchemasInTarget = new ArrayList<>();
		List<String> nonCoreSchemasInSource = new ArrayList<>();
		String tarappendEnvId = envId+"'";
		targetQuery = targetQuery(prop.getProperty("get_non_core_schemas_target")+tarappendEnvId);
		while (targetQuery.next()) {
			nonCoreSchemasInTarget.add(String.valueOf(targetQuery.getObject(1)));
		}
		String srcappendEnvId = "_";
		sourceQuery = query(prop.getProperty("get_non_core_schemas_source"));
		while (sourceQuery.next()) {
			nonCoreSchemasInSource.add(String.valueOf(sourceQuery.getObject(1)) + srcappendEnvId + envId);
		}
		targetDBConnection.close();
		dbConnection.close();
		Collections.sort(nonCoreSchemasInSource);
		Collections.sort(nonCoreSchemasInTarget);
		Assert.assertEquals(nonCoreSchemasInSource, nonCoreSchemasInTarget);
		log.info("TC 05 Verify if the Environment ID is appended only to the Non-Core Schema name in the Target after Migration. Ended...");
	}
	
	/**
	 * Verify if the Core schemas are not appended with Environment ID in the Target after migration
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc06_verifyCoreSchemasNotAppendedWithEnvId() throws JSchException, SftpException, Exception {
		log.info("TC 06 Verifying that the Core schemas are not appended with Environment ID in the Target after migration. Started.......");
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		String coreSchemas = null;
		if(osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("mysql_py_linux"));
		} else if(osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("mysql_py_linux"));
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line;
		while ((line = br.readLine()) != null) {
			if(line.contains("CORE_SCHEMA") && !line.contains("#")) {
				coreSchemas = line.split("=")[1].trim().replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("'", "");
			}
		}
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> coreSchemaList = Arrays.asList(coreSchemas.split(","));
		List<String> coreSchemaListWithEnvId = new ArrayList<String>();
		for(String coreSchema : coreSchemaList) {
			coreSchemaListWithEnvId.add("'"+coreSchema+"_"+envId+"'");
		}
		String coreSchemasWithEnvId = coreSchemaListWithEnvId.toString().replaceAll("[\\[\\]]", "");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP546_query.properties");
		List<String> coreSchemaTables = new ArrayList<>();
		targetQuery = targetQuery(prop.getProperty("get_core_schema_names")+coreSchemasWithEnvId+")");
		while (targetQuery.next()) {
			coreSchemaTables.add(targetQuery.getObject(0).toString());
		}
		Assert.assertEquals(coreSchemaTables.size(),0,"Some of the Core Schemas appended with ENV_ID");
		log.info("TC 06 Verifying that the Core schemas are not appended with Environment ID in the Target after migration. Ended.......");
	}
	
	/**
	 * Verify if the Non-Core Schema Table structure in the Target is with the new schema name appended with Env ID
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc07_verifyNonCoreSchemaTableStructureAppendedWithEnvId() throws JSchException, SftpException, Exception {
		log.info("TC 07 Verifying that the Non-Core Schema Table structure in the Target is with the new schema name appended with Env ID. Started.......");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP546_query.properties");
		establishDatabaseconnection();
		String connectionStatus = establishTargetDatabaseconnection();		
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> nonCoreSchemaTablesInSource = new ArrayList<>();
		List<String> nonCoreSchemaTablesInTarget = new ArrayList<>();
		sourceQuery = query(prop.getProperty("get_non_core_schema_tables_source"));
		while (sourceQuery.next()) {
			nonCoreSchemaTablesInSource.add(sourceQuery.getObject(1).toString());
		}
		String tarappendEnvId = envId+"'";
		targetQuery = targetQuery(prop.getProperty("get_non_core_schema_tables_target")+tarappendEnvId);
		while (targetQuery.next()) {
			nonCoreSchemaTablesInTarget.add(String.valueOf(targetQuery.getObject(1)));
		}
		dbConnection.close();
		targetDBConnection.close();
		Collections.sort(nonCoreSchemaTablesInTarget);
		Collections.sort(nonCoreSchemaTablesInSource);
		Assert.assertEquals(nonCoreSchemaTablesInTarget,nonCoreSchemaTablesInSource,"Non Core Schema Table Structure Not Appended With ENV_ID");
		log.info("TC 07 Verifying that the Non-Core Schema Table structure in the Target is with the new schema name appended with Env ID. Ended.......");
	}
	
	/**
	 * Verify if the transfer of the entire non-core schema table structure as is migrated from source to the target with out any data loss or any modifications
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc08_verifyNonCoreSchemaTableStructureWithoutLoss() throws JSchException, SftpException, Exception {
		log.info("TC 08 Verifying that the transfer of the entire non-core schema table structure as is migrated from source to the target with out any data loss or any modifications. Started.......");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP546_query.properties");
		establishDatabaseconnection();
		String connectionStatus = establishTargetDatabaseconnection();		
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> nonCoreSchemaTablesInSource = new ArrayList<>();
		List<String> nonCoreSchemaTablesInTarget = new ArrayList<>();
		sourceQuery = query(prop.getProperty("get_non_core_schema_tables_source"));
		while (sourceQuery.next()) {
			nonCoreSchemaTablesInSource.add(sourceQuery.getObject(1).toString());
		}
		String tarappendEnvId = envId+"'";
		targetQuery = targetQuery(prop.getProperty("get_non_core_schema_tables_target")+tarappendEnvId);
		while (targetQuery.next()) {
			nonCoreSchemaTablesInTarget.add(String.valueOf(targetQuery.getObject(1)));
		}
		dbConnection.close();
		targetDBConnection.close();
		Collections.sort(nonCoreSchemaTablesInTarget);
		Collections.sort(nonCoreSchemaTablesInSource);
		Assert.assertEquals(nonCoreSchemaTablesInTarget,nonCoreSchemaTablesInSource,"Non Core Schemas Migrated with Data Loss");
		log.info("TC 08 Verifying that the transfer of the entire non-core schema table structure as is migrated from source to the target with out any data loss or any modifications. Ended.......");
	}
}