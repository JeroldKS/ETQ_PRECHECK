package migrationStories;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.List;
import java.util.logging.Logger;


import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP373_SkipNonCoreTrainingSchema extends Base {

	static Logger log = Logger.getLogger(MMP373_SkipNonCoreTrainingSchema.class.getName());

	/**
	 * Checking whether the Non core Schema TRAINING is not Migrated from Source to Target
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */

	@Test(enabled = false)
	public static void tc01_VerifyNoncoreSchemaTRAININGNotMigrated() throws JSchException, SftpException, Exception {

		log.info("TC 01 Checking whether Non core Schema TRAINING is not Migrated. Started.......");
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");

		prop = loadQueryFile("//src//test//resources//migration//queries//MMP373_query.properties");
		List<String> schemaCount = new ArrayList<>();
		targetQuery = targetQuery(prop.getProperty("list_of_tasks") + "\'" + envId + "\'");
		while (targetQuery.next()) {
			schemaCount.add(targetQuery.getObject(1).toString());
			Assert.assertTrue(schemaCount.isEmpty());

		}

		log.info("TC 01 Checking whether Engine schema is successfully migrated. Ended.......");
	}

	/**
	 * Checking whether All Non-core Schemas Except TRAINING Migrated from Source to Target
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */

	@Test(enabled = false)
	public static void tc02_VerifyAllNoncoreSchemasExceptTRAININGMigrated()
			throws JSchException, SftpException, Exception {

		log.info("TC 02 Verify All Non core Schemas Except TRAINING Migrated. Started.......");
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP373_query.properties");
		List<String> target = new ArrayList<>();
		targetQuery = targetQuery(prop.getProperty("tc02db1")+"\'" + envId + "\'");
		while (targetQuery.next()) {
			target.add(targetQuery.getObject(1).toString());
		}
		Assert.assertTrue(!target.isEmpty());
		for (String schema : target) {
			System.out.println(schema);
			if (schema.startsWith("training")) {
				System.out.println(schema);
				Assert.fail("Error: Training schema migrated");

			}
		}

		log.info("TC 02 Verify All Non core Schemas Except TRAINING Migrated. Ended.......");

	}

	/**
	 * Verify New Config Variable (non_core_schema_exclusion) is added to the non-core schema, along with the core schema exclusion list
	 * 
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */

	@Test(enabled = true)
	public static void tc03_VerifyNewConfigVariableAddedToTheNonCoreSchema() throws Exception {

		log.info("TC 03 Verify if new config variable (non_core_schema_exclusion) is added to the non-core schema, along with the core schema exclusion list. Started.......");
		String connectionStatus = establishTargetDatabaseconnection();

		Assert.assertEquals(connectionStatus, "Connection Success");
		String exclusionlist = toGetAnyValue("non_core_schema_exclusion");
		Assert.assertEquals(exclusionlist, "[training]");
		log.info("TC 03 Verify if new config variable (non_core_schema_exclusion) is added to the non-core schema, along with the core schema exclusion list. Ended.......");
	}

	public static String toGetAnyValue(String Key) throws JSchException, SftpException, Exception {
//		String connectionStatus = null;
//		String host = null;
//		String username = null;
//		String password = null;
//		String port = null;
		String noncoreschemaexclusion = null;
		List<String> targetDBCredentials = new ArrayList<String>();
		establishSshConnectionForSourceInstance();
		InputStream stream = null;
		if (osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_migration_linux"));
		} else if (osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_migration_windows"));
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("db.target") && !line.contains("#")) {
					int count = 0;
					while (count < 4) {
						targetDBCredentials.add(br.readLine());
						count++;
					}
				}
				if (line.contains(Key) && !line.contains("#")) {
					noncoreschemaexclusion = line.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
			}
//			for (String credentials : targetDBCredentials) {
//				if (credentials.contains("host")) {
//					host = credentials.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
//				}
//				if (credentials.contains("username")) {
//					username = credentials.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
//				}
//				if (credentials.contains("password")) {
//					password = credentials.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
//				}
//				if (credentials.contains("port")) {
//					port = credentials.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
//				}
//			}
//			if (null != host && null != username && null != password && null != port && !host.isEmpty()
//					&& !username.isEmpty() && !password.isEmpty() && !port.isEmpty()) {
//				connectionStatus = establishDestinationDatabaseconnection(host, username, password, port);
//			}
		} catch (IOException io) {

		}
		return noncoreschemaexclusion;

	}
}
