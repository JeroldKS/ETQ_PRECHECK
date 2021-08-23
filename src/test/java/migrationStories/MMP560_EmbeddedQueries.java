package migrationStories;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP560_EmbeddedQueries extends Base {
 
	static Logger log = Logger.getLogger(MMP560_EmbeddedQueries.class.getName());
	
	/**
	 * Verify Embedded SQL queries Table and column name list on Source
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_verifyEmbeddedSQLQueries() throws JSchException, SftpException, Exception {
		log.info("TC 01 Verify Embedded SQL queries Table and column name list on Source. Started.......");
		establishDatabaseconnection();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//migration//BussinessRules//MMP560_CoreSchemaEmbeddedQueries.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONObject jsonObject = (JSONObject) parse;
		Object embeddedObject = jsonObject.get("embeddedQueries");
		JSONArray jsonArrayForEmbeddedQueries = (JSONArray) embeddedObject;
		System.out.println(jsonArrayForEmbeddedQueries.size());
		for (int i = 0; i < jsonArrayForEmbeddedQueries.size() ; i++) {
			Object jsonArrayForEmbeddedObject = jsonArrayForEmbeddedQueries.get(i);
			JSONObject embeddedKeys = (JSONObject) jsonArrayForEmbeddedObject;
			sourceQuery = query("SELECT * FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table")+" limit 1");
			ArrayList<String> columnNameList = new ArrayList<String>();
			int columnCount = sourceQuery.getMetaData().getColumnCount();
			for (int j = 0; j < columnCount; j++) {
				columnNameList.add(sourceQuery.getMetaData().getColumnName(j + 1));
			}
			Assert.assertTrue(columnNameList.contains(embeddedKeys.get("field")),embeddedKeys.get("field")
					+" is not available in table: "+embeddedKeys.get("table"));
		}
		log.info("TC 01 Verify Embedded SQL queries Table and column name list on Source. Ended.......");
	}
	
	/**
	 * Verify if there are no migration happened in Target for the MT_Type 0 Records even though the PK name and SQL query of Source and OOB are matched
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_verifyNoMigrationHappenedMTType0() throws JSchException, SftpException, Exception {
		log.info("TC 02 Verify if there are no migration happened in Target for the MT_Type 0 Records even though the PK name and SQL query of Source and OOB are matched. Started.......");
		establishDatabaseconnection();
		establishOOBDatabaseconnection();
		String connectionStatus = establishTargetDatabaseconnection();		
		Assert.assertEquals(connectionStatus, "Connection Success");
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//migration//BussinessRules//MMP560_CoreSchemaEmbeddedQueries.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONObject jsonObject = (JSONObject) parse;
		Object embeddedObject = jsonObject.get("embeddedQueries");
		JSONArray jsonArrayForEmbeddedQueries = (JSONArray) embeddedObject;
		System.out.println(jsonArrayForEmbeddedQueries.size());
		for (int i = 0; i < jsonArrayForEmbeddedQueries.size() ; i++) {
			Object jsonArrayForEmbeddedObject = jsonArrayForEmbeddedQueries.get(i);
			JSONObject embeddedKeys = (JSONObject) jsonArrayForEmbeddedObject;
			
			List<String> embeddedQueryListInSource = new ArrayList<String>();
			List<String> embeddedQueryListInOOB = new ArrayList<String>();
			List<String> embeddedQueryListMTType0 = new ArrayList<String>();
			List<String> embeddedQueryListWithEnvId = new ArrayList<String>();
			
			sourceQuery = query("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table"));
			while(sourceQuery.next()) {
				embeddedQueryListInSource.add(String.valueOf(sourceQuery.getObject(1)));
			}
			
			oobQuery = query("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table"));
			while(oobQuery.next()) {
				embeddedQueryListInOOB.add(String.valueOf(oobQuery.getObject(1)));
			}
			
			embeddedQueryListInSource.retainAll(embeddedQueryListInOOB);
			
			targetQuery = targetQuery("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table")+" WHERE MT_TYPE = 0");
			while (targetQuery.next()) {
				embeddedQueryListMTType0.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			embeddedQueryListMTType0.retainAll(embeddedQueryListInSource);
			
			String tarappendEnvId = envId+"'";
			targetQuery = targetQuery("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table")+" WHERE ENVIRONMENT_UUID = '"+tarappendEnvId);
			while (targetQuery.next()) {
				embeddedQueryListWithEnvId.add(String.valueOf(targetQuery.getObject(1)));
			}
			embeddedQueryListWithEnvId.retainAll(embeddedQueryListMTType0);
			
			Assert.assertEquals(embeddedQueryListMTType0.size(), 0, "This following table values are migrated against Bussiness Rules. "
					+ "For the schema:: "+embeddedKeys.get("schema") +"and table:: "+embeddedKeys.get("table") + "For MT Type 0");
		}
		targetDBConnection.close();
		oobDBConnection.close();
		dbConnection.close();
		log.info("TC 02 Verify if there are no migration happened in Target for the MT_Type 0 Records even though the PK name and SQL query of Source and OOB are matched. Ended.......");
	}
	
	/**
	 * Verify if there are no migration happened in Target for the MT_Type 0 Records even though the PK name and SQL query of Source and OOB are matched
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc03_verifyMigrationHappenedMTType2() throws JSchException, SftpException, Exception {
		log.info("TC 03 Verify if the source environment query is replaced with Target environment query and the Entire source record is migrated to target with associated ENV ID as MT_TYPE 2 in Target. Started.......");
		establishDatabaseconnection();
		establishOOBDatabaseconnection();
		String connectionStatus = establishTargetDatabaseconnection();		
		Assert.assertEquals(connectionStatus, "Connection Success");
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//migration//BussinessRules//MMP560_CoreSchemaEmbeddedQueries.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONObject jsonObject = (JSONObject) parse;
		Object embeddedObject = jsonObject.get("embeddedQueries");
		JSONArray jsonArrayForEmbeddedQueries = (JSONArray) embeddedObject;
		System.out.println(jsonArrayForEmbeddedQueries.size());
		for (int i = 0; i < jsonArrayForEmbeddedQueries.size() ; i++) {
			Object jsonArrayForEmbeddedObject = jsonArrayForEmbeddedQueries.get(i);
			JSONObject embeddedKeys = (JSONObject) jsonArrayForEmbeddedObject;
			List<String> embeddedQueryListInSource = new ArrayList<String>();
			List<String> embeddedQueryListInOOB = new ArrayList<String>();
			List<String> embeddedQueryListTargetWithMTType1 = new ArrayList<String>();
			List<String> embeddedQueryListTargetWithMTType2 = new ArrayList<String>();
			
			sourceQuery = query("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table"));
			while(sourceQuery.next()) {
				embeddedQueryListInSource.add(String.valueOf(sourceQuery.getObject(1)));
			}
			
			oobQuery = query("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table"));
			while(oobQuery.next()) {
				embeddedQueryListInOOB.add(String.valueOf(oobQuery.getObject(1)));
			}
			
			embeddedQueryListInSource.retainAll(embeddedQueryListInOOB);
			
			targetQuery = targetQuery("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table")+" WHERE MT_TYPE = 1");
			while (targetQuery.next()) {
				embeddedQueryListTargetWithMTType1.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			embeddedQueryListTargetWithMTType1.retainAll(embeddedQueryListInSource);
			
			String tarappendEnvId = envId+"'";
			targetQuery = targetQuery("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table")+" WHERE MT_TYPE = 2 and ENVIRONMENT_UUID = '"+tarappendEnvId);
			while (targetQuery.next()) {
				embeddedQueryListTargetWithMTType2.add(String.valueOf(targetQuery.getObject(1)));
			}
			Assert.assertEquals(embeddedQueryListTargetWithMTType2, embeddedQueryListTargetWithMTType1, "This following table values are migrated against Bussiness Rules. "
					+ "For the schema:: "+embeddedKeys.get("schema") +"and table:: "+embeddedKeys.get("table") + "For MT Type 2");
		}
		
		dbConnection.close();
		oobDBConnection.close();
		targetDBConnection.close();
		log.info("TC 03 Verify if the source environment query is replaced with Target environment query and the Entire source record is migrated to target with associated ENV ID as MT_TYPE 2 in Target. Ended.......");
	}
	
	/**
	 * Verify if the Unmatched SQL Query Records are not migrated to Target
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc04_verifyUnmatchedQueriesNotMigrated() throws JSchException, SftpException, Exception {
		log.info("TC 04 Verify if the Unmatched SQL Query Records are not migrated to Target. Started.......");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP560_query.properties");
		establishDatabaseconnection();
		establishOOBDatabaseconnection();
		String connectionStatus = establishTargetDatabaseconnection();		
		Assert.assertEquals(connectionStatus, "Connection Success");
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//migration//BussinessRules//MMP560_CoreSchemaEmbeddedQueries.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONObject jsonObject = (JSONObject) parse;
		Object embeddedObject = jsonObject.get("embeddedQueries");
		JSONArray jsonArrayForEmbeddedQueries = (JSONArray) embeddedObject;
		System.out.println(jsonArrayForEmbeddedQueries.size());
		for (int i = 0; i < jsonArrayForEmbeddedQueries.size() ; i++) {
			Object jsonArrayForEmbeddedObject = jsonArrayForEmbeddedQueries.get(i);
			JSONObject embeddedKeys = (JSONObject) jsonArrayForEmbeddedObject;
			List<String> embeddedQueryListSource = new ArrayList<String>();
			List<String> embeddedQueryListOOB = new ArrayList<String>();
			List<String> embeddedQueryListTarget = new ArrayList<String>();
			sourceQuery = query("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table"));
			while (sourceQuery.next()) {
				embeddedQueryListSource.add(String.valueOf(sourceQuery.getObject(1)));
			}
			
			oobQuery = oobQuery("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table"));
			while (oobQuery.next()) {
				embeddedQueryListOOB.add(String.valueOf(oobQuery.getObject(1)));
			}
			embeddedQueryListSource.removeAll(embeddedQueryListOOB);
			
			String tarappendEnvId = envId+"'";
			targetQuery = targetQuery("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table")+" WHERE ENVIRONMENT_UUID = '"+tarappendEnvId);
			while (targetQuery.next()) {
				embeddedQueryListTarget.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			Assert.assertTrue(!embeddedQueryListSource.containsAll(embeddedQueryListTarget) , "This following table values are migrated against Bussiness Rules. "
					+ "For the schema:: "+embeddedKeys.get("schema") +"and table:: "+embeddedKeys.get("table") + "For Unmatched Datas");
		}
		
		dbConnection.close();
		oobDBConnection.close();
		targetDBConnection.close();
		log.info("TC 03 Verify if the source environment query is replaced with Target environment query and the Entire source record is migrated to target with associated ENV ID as MT_TYPE 2 in Target. Ended.......");
	}
	
	/**
	 * Verify if the SQL Query in Target after migration with MT_TYPE 2 is same as in Source
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc05_verifyMTType2() throws JSchException, SftpException, Exception {
		log.info("TC 05 Verify if the SQL Query in Target after migration with MT_TYPE 2 is same as in Source. Started.......");
		establishDatabaseconnection();
		establishOOBDatabaseconnection();
		String connectionStatus = establishTargetDatabaseconnection();		
		Assert.assertEquals(connectionStatus, "Connection Success");
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//migration//BussinessRules//MMP560_CoreSchemaEmbeddedQueries.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONObject jsonObject = (JSONObject) parse;
		Object embeddedObject = jsonObject.get("embeddedQueries");
		JSONArray jsonArrayForEmbeddedQueries = (JSONArray) embeddedObject;
		System.out.println(jsonArrayForEmbeddedQueries.size());
		for (int i = 0; i < jsonArrayForEmbeddedQueries.size() ; i++) {
			Object jsonArrayForEmbeddedObject = jsonArrayForEmbeddedQueries.get(i);
			JSONObject embeddedKeys = (JSONObject) jsonArrayForEmbeddedObject;
			List<String> embeddedQueryListInSource = new ArrayList<String>();
			List<String> embeddedQueryListInOOB = new ArrayList<String>();
			List<String> embeddedQueryListTargetWithMTType1 = new ArrayList<String>();
			List<String> embeddedQueryListTargetWithMTType2 = new ArrayList<String>();
			
			sourceQuery = query("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table"));
			while(sourceQuery.next()) {
				embeddedQueryListInSource.add(String.valueOf(sourceQuery.getObject(1)));
			}
			
			oobQuery = query("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table"));
			while(oobQuery.next()) {
				embeddedQueryListInOOB.add(String.valueOf(oobQuery.getObject(1)));
			}
			
			embeddedQueryListInSource.retainAll(embeddedQueryListInOOB);
			
			targetQuery = targetQuery("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table")+" WHERE MT_TYPE = 1");
			while (targetQuery.next()) {
				embeddedQueryListTargetWithMTType1.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			embeddedQueryListTargetWithMTType1.retainAll(embeddedQueryListInSource);
			
			String tarappendEnvId = envId+"'";
			targetQuery = targetQuery("SELECT "+embeddedKeys.get("field")+" FROM "+embeddedKeys.get("schema")+"."+embeddedKeys.get("table")+" WHERE MT_TYPE = 2 and ENVIRONMENT_UUID = '"+tarappendEnvId);
			while (targetQuery.next()) {
				embeddedQueryListTargetWithMTType2.add(String.valueOf(targetQuery.getObject(1)));
			}
			Assert.assertEquals(embeddedQueryListTargetWithMTType2, embeddedQueryListTargetWithMTType1, "This following table values are migrated against Bussiness Rules. "
					+ "For the schema:: "+embeddedKeys.get("schema") +"and table:: "+embeddedKeys.get("table") + "For MT Type 2");
		}
		
		dbConnection.close();
		oobDBConnection.close();
		targetDBConnection.close();
		log.info("TC 05 Verify if the SQL Query in Target after migration with MT_TYPE 2 is same as in Source. Ended.......");
	}
	
	/**
	 * Verify if the SQL Query in Target after migration with MT_TYPE 3 is same as in Source for the Source PK Name not matched with OOB PK Name
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc06_verifyMTType3() throws JSchException, SftpException, Exception {
		log.info("TC 06 Verify if the SQL Query in Target after migration with MT_TYPE 3 is same as in Source for the Source PK Name not matched with OOB PK Name. Started.......");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP560_query.properties");
		establishDatabaseconnection();
		establishOOBDatabaseconnection();
		String connectionStatus = establishTargetDatabaseconnection();		
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> embeddedQueryListSource = new ArrayList<String>();
		List<String> embeddedQueryListOOB = new ArrayList<String>();
		List<String> embeddedQueryListTarget = new ArrayList<String>();
		sourceQuery = query(prop.getProperty("get_statement"));
		while (sourceQuery.next()) {
			embeddedQueryListSource.add(String.valueOf(sourceQuery.getObject(1)));
		}
		oobQuery = oobQuery(prop.getProperty("get_statement"));
		while (oobQuery.next()) {
			embeddedQueryListOOB.add(String.valueOf(oobQuery.getObject(1)));
		}
		embeddedQueryListSource.removeAll(embeddedQueryListOOB);
		String tarappendEnvId = envId+"'";
		targetQuery = targetQuery(prop.getProperty("get_statement_mt_type_3")+tarappendEnvId);
		while (targetQuery.next()) {
			embeddedQueryListTarget.add(String.valueOf(targetQuery.getObject(1)));
		}
		dbConnection.close();
		oobDBConnection.close();
		targetDBConnection.close();
		Assert.assertEquals(embeddedQueryListTarget, embeddedQueryListSource);
		log.info("TC 06 Verify if the SQL Query in Target after migration with MT_TYPE 3 is same as in Source for the Source PK Name not matched with OOB PK Name. Ended.......");
	}
}