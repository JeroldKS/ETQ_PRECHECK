package migrationStories;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP547_CoreSchemaSettingTableDataMigration extends Base {
	static Logger log = Logger.getLogger(MMP547_CoreSchemaSettingTableDataMigration.class.getName());

	/**
	 * Verify the core Schemas are migrated from Source to Target as per the Business rules
	 * @throws Exception
	 */
	@Test
	public void tc01_verifyCoreSchemasMigrated() throws Exception {
		log.info("TC 01 Verify the core Schemas are migrated from Source to Target as per the Business rules. Started");
		establishDatabaseconnection();
		establishTargetDatabaseconnection();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//migration//BussinessRules//MMP547_CoreSchemaSettingTableDataMigration.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONObject jsonObject = (JSONObject) parse;
		Object engineObject = jsonObject.get("settings");
		JSONArray jsonArrayForEngine = (JSONArray) engineObject;
		for (int i = 0; i < jsonArrayForEngine.size() ; i++) {
			Object jsonArrayForEngineObject = jsonArrayForEngine.get(i);
			JSONObject engineKeys = (JSONObject) jsonArrayForEngineObject;
			ArrayList<String> sourceValue = new ArrayList<String>();
			ArrayList<String> targetValueWithEnvId = new ArrayList<String>();
			ArrayList<String> targetValueWithMT0 = new ArrayList<String>();
			ArrayList<String> targetValueWithMT1 = new ArrayList<String>();
			ArrayList<String> targetValueWithMT2 = new ArrayList<String>();
			ArrayList<String> targetValueWithMT3 = new ArrayList<String>();
			ArrayList<String> targetValueWithMT012 = new ArrayList<String>();
			
			//CHECK FOR MT_TYPE = 0. Started
			//Fetching All Data from Source
			sourceQuery = query("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table"));
			while (sourceQuery.next()) {
				sourceValue.add(String.valueOf(sourceQuery.getObject(1)));
			}
			
			//Fetching All Data from Target with ENVIRONMENT_UUID
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where ENVIRONMENT_UUID = '" + envId + "'");
			while (targetQuery.next()) {
				targetValueWithEnvId.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			//Fetching MT_TYPE = 0 records from target without envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE = 0");
			while (targetQuery.next()) {
				targetValueWithMT0.add(String.valueOf(targetQuery.getObject(1)));
			} 
			
			List<String> sourceValueCopy = new ArrayList<String>();
			sourceValueCopy.addAll(sourceValue);
			//targetValueWithMT0 contains matching elements of source and target MT_TYPE=0
			targetValueWithMT0.retainAll(sourceValueCopy);
			
			//targetValueWithEnvId contains matching elements of matching-source and target elements with ENV_ID
			targetValueWithEnvId.retainAll(targetValueWithMT0);
			
			Assert.assertEquals(targetValueWithEnvId.size(), 0, "This following table values are migrated against Bussiness Rules. "
					+ "For the schema:: "+engineKeys.get("schema") +" and table:: "+engineKeys.get("table") + " For MT Type 0");
			//CHECK FOR MT_TYPE = 0. Ended here
			
			
			//CHECK FOR MT_TYPE = 2. Started
			//Fetching MT_TYPE = 1 records from target without envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE = 1");
			while (targetQuery.next()) {
				targetValueWithMT1.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			//Fetching MT_TYPE = 2 records from target with envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE = 2 AND ENVIRONMENT_UUID = '" + envId + "'");
			while (targetQuery.next()) {
				targetValueWithMT2.add(String.valueOf(targetQuery.getObject(1)));
			}
		 
			List<String> copyOfSourceValue = new ArrayList<String>();
			copyOfSourceValue.addAll(sourceValue);
			
			//copyOfSourceValue Contains Matching elements between source and target with MT_TYPE = 1
			copyOfSourceValue.retainAll(targetValueWithMT1);
			
			Collections.sort(targetValueWithMT2);
			Collections.sort(copyOfSourceValue);
			Assert.assertEquals(targetValueWithMT2, copyOfSourceValue, "This following table values are migrated against Bussiness Rules. "
					+ "For the schema:: "+engineKeys.get("schema") +" and table:: "+engineKeys.get("table") + " For MT Type 2");
			
			//CHECK FOR MT_TYPE = 2. Ended here
			
			//CHECK FOR MT_TYPE = 3. Started here
			//Fetching MT_TYPE = 0,1,2 records from target without envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE IN (0,1,2)");
			while (targetQuery.next()) {
				targetValueWithMT012.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			//Fetching MT_TYPE = 3 records from target with envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE = 3 AND ENVIRONMENT_UUID = '" + envId + "'");
			while (targetQuery.next()) {
				targetValueWithMT3.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			//sourceValue contains elements which is not in MT_TYPE = 0,1,2
			sourceValue.removeAll(targetValueWithMT012);
			Collections.sort(targetValueWithMT3);
			Collections.sort(sourceValue);
			Assert.assertEquals(targetValueWithMT3, sourceValue, "This following table values are migrated against Bussiness Rules. "
					+ "For the schema:: "+engineKeys.get("schema") +" and table:: "+engineKeys.get("table") + " For MT Type 3");
			//CHECK FOR MT_TYPE = 3. Ended here
		}
		log.info("TC 01 Verify the core Schemas are migrated from Source to Target as per the Business rules. Started");
	}
	
	/**
	 * Verify the core Schemas Column Name and its corresponding Record are same from Source and Target
	 * @throws Exception
	 */
	@Test
	public void tc02_verifyCoreSchemasColumnAndRecordAreSame() throws Exception {
		log.info("TC 02 Verify the core Schemas Column Name and its corresponding Record are same from Source and Target. Started");
		establishDatabaseconnection();
		establishTargetDatabaseconnection();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//migration//BussinessRules//MMP547_CoreSchemaSettingTableDataMigration.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONObject jsonObject = (JSONObject) parse;
		Object engineObject = jsonObject.get("settings");
		JSONArray jsonArrayForEngine = (JSONArray) engineObject;
		for (int i = 0; i < jsonArrayForEngine.size() ; i++) {
			Object jsonArrayForEngineObject = jsonArrayForEngine.get(i);
			JSONObject engineKeys = (JSONObject) jsonArrayForEngineObject;
			ArrayList<String> sourceValue = new ArrayList<String>();
			ArrayList<String> targetValueWithEnvId = new ArrayList<String>();
			ArrayList<String> targetValueWithMT0 = new ArrayList<String>();
			ArrayList<String> targetValueWithMT1 = new ArrayList<String>();
			ArrayList<String> targetValueWithMT2 = new ArrayList<String>();
			ArrayList<String> targetValueWithMT3 = new ArrayList<String>();
			ArrayList<String> targetValueWithMT012 = new ArrayList<String>();
			
			//CHECK FOR MT_TYPE = 0. Started
			//Fetching All Data from Source
			sourceQuery = query("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table"));
			while (sourceQuery.next()) {
				sourceValue.add(String.valueOf(sourceQuery.getObject(1)));
			}
			
			//Fetching All Data from Target with ENVIRONMENT_UUID
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where ENVIRONMENT_UUID = '" + envId + "'");
			while (targetQuery.next()) {
				targetValueWithEnvId.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			//Fetching MT_TYPE = 0 records from target without envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE = 0");
			while (targetQuery.next()) {
				targetValueWithMT0.add(String.valueOf(targetQuery.getObject(1)));
			} 
			
			List<String> sourceValueCopy = new ArrayList<String>();
			sourceValueCopy.addAll(sourceValue);
			//targetValueWithMT0 contains matching elements of source and target MT_TYPE=0
			targetValueWithMT0.retainAll(sourceValueCopy);
			
			//targetValueWithEnvId contains matching elements of matching-source and target elements with ENV_ID
			targetValueWithEnvId.retainAll(targetValueWithMT0);
			
			Assert.assertEquals(targetValueWithEnvId.size(), 0, "This following table values are migrated against Bussiness Rules. "
					+ "For the schema:: "+engineKeys.get("schema") +"and table:: "+engineKeys.get("table") + "For MT Type 0");
			//CHECK FOR MT_TYPE = 0. Ended here
			
			
			//CHECK FOR MT_TYPE = 2. Started
			//Fetching MT_TYPE = 1 records from target without envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE = 1");
			while (targetQuery.next()) {
				targetValueWithMT1.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			//Fetching MT_TYPE = 2 records from target with envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE = 2 AND ENVIRONMENT_UUID = '" + envId + "'");
			while (targetQuery.next()) {
				targetValueWithMT2.add(String.valueOf(targetQuery.getObject(1)));
			}
		 
			List<String> copyOfSourceValue = new ArrayList<String>();
			copyOfSourceValue.addAll(sourceValue);
			
			//copyOfSourceValue Contains Matching elements between source and target with MT_TYPE = 1
			copyOfSourceValue.retainAll(targetValueWithMT1);
			
			Collections.sort(targetValueWithMT2);
			Collections.sort(copyOfSourceValue);
			Assert.assertEquals(targetValueWithMT2, copyOfSourceValue, "This following table values are migrated against Bussiness Rules. "
					+ "For the schema:: "+engineKeys.get("schema") +"and table:: "+engineKeys.get("table") + "For MT Type 2");
			//CHECK FOR MT_TYPE = 2. Ended here
			
			//Code to fetch Soruce and Target Column Names
			ArrayList<String> targetColumnNameList = new ArrayList<String>();
			targetQuery = targetQuery("select * from " + engineKeys.get("schema") + "." + engineKeys.get("table")
					+ " where ENVIRONMENT_UUID = '" + envId + "' limit 1");
			int targetColumnCount = targetQuery.getMetaData().getColumnCount();
			for (int i1 = 0; i1 < targetColumnCount; i1++) {
				targetColumnNameList.add(targetQuery.getMetaData().getColumnName(i1 + 1));
			}
			List<String> sourceColumnNameList = new ArrayList<>();
			sourceQuery = query(
					"select * from " + engineKeys.get("schema") + "." + engineKeys.get("table") + " limit 1");
			int sourceColumnCount = sourceQuery.getMetaData().getColumnCount();
			for (int i1 = 0; i1 < sourceColumnCount; i1++) {
				sourceColumnNameList.add(sourceQuery.getMetaData().getColumnName(i1 + 1));
			}
			ArrayList<String> copyTargetColumnNameList = new ArrayList<>();
			copyTargetColumnNameList.addAll(targetColumnNameList);
			copyTargetColumnNameList.removeAll(sourceColumnNameList);
			targetColumnNameList.removeAll(copyTargetColumnNameList);
			
			//Data Check for MT_TYPE = 1 case
			if(copyOfSourceValue.size() > 1) {
				for (int j = 0; j < 5; j++) {
					String dynamicColumnValue = copyOfSourceValue.get(generate(copyOfSourceValue.size() - 1));
	
					targetQuery = targetQuery("select * from " + engineKeys.get("schema") + "." + engineKeys.get("table")
							+ " where " + engineKeys.get("field") + " = '" + dynamicColumnValue + "' and MT_TYPE = 2 and "
							+ "ENVIRONMENT_UUID = '"+ envId + "'");
					
					sourceQuery = query("select * from " + engineKeys.get("schema") + "." + engineKeys.get("table")
							+ " where " + engineKeys.get("field") + " = '" + dynamicColumnValue +"'");
	
					ArrayList<String> targetColumndataList = new ArrayList<>();
					while (targetQuery.next()) {
						String targetColumndata = "";
						for (int i1 = 0; i1 < targetColumnNameList.size(); i1++) {
							targetColumndata = targetColumndata
									+ String.valueOf(targetQuery.getObject(targetColumnNameList.get(i1))) + "--";
						}
						targetColumndataList.add(targetColumndata);
					}
					ArrayList<String> sourceColumndataList = new ArrayList<>();
					while (sourceQuery.next()) {
						String sourceColumndata = "";
						for (int i1 = 0; i1 < targetColumnNameList.size(); i1++) {
							sourceColumndata = sourceColumndata
									+ String.valueOf(sourceQuery.getObject(targetColumnNameList.get(i1))) + "--";
						}
						sourceColumndataList.add(sourceColumndata);
					}
					Assert.assertEquals(sourceColumndataList, targetColumndataList, "Data Mismatched for some records with MT_TYPE=1");
				}
			}
			//CHECK FOR MT_TYPE = 2. Ended here
			
			//CHECK FOR MT_TYPE = 3. Started here
			//Fetching MT_TYPE = 0,1,2 records from target without envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE IN (0,1,2)");
			while (targetQuery.next()) {
				targetValueWithMT012.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			//Fetching MT_TYPE = 3 records from target with envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE = 3 AND ENVIRONMENT_UUID = '" + envId + "'");
			while (targetQuery.next()) {
				targetValueWithMT3.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			//sourceValue contains elements which is not in MT_TYPE = 0,1,2
			sourceValue.removeAll(targetValueWithMT012);
			Collections.sort(targetValueWithMT3);
			Collections.sort(sourceValue);
			Assert.assertEquals(targetValueWithMT3, sourceValue);


			if(sourceValue.size() > 1) {
				for (int j = 0; j < 5; j++) {
					String dynamicColumnValue = sourceValue.get(generate(sourceValue.size() - 1));
	
					targetQuery = targetQuery("select * from " + engineKeys.get("schema") + "." + engineKeys.get("table")
							+ " where " + engineKeys.get("field") + " = '" + dynamicColumnValue + "' and MT_TYPE = 3 "
									+ "and ENVIRONMENT_UUID = '"+ envId + "'");
					
					sourceQuery = query("select * from " + engineKeys.get("schema") + "." + engineKeys.get("table")
							+ " where " + engineKeys.get("field") + " = '" + dynamicColumnValue + "'");
	
					ArrayList<String> targetColumndataList = new ArrayList<>();
					while (targetQuery.next()) {
						String targetColumndata = "";
						for (int i1 = 0; i1 < targetColumnNameList.size(); i1++) {
							targetColumndata = targetColumndata
									+ String.valueOf(targetQuery.getObject(targetColumnNameList.get(i1))) + "--";
						}
						targetColumndataList.add(targetColumndata);
					}
					ArrayList<String> sourceColumndataList = new ArrayList<>();
					while (sourceQuery.next()) {
						String sourceColumndata = "";
						for (int i1 = 0; i1 < targetColumnNameList.size(); i1++) {
							sourceColumndata = sourceColumndata
									+ String.valueOf(sourceQuery.getObject(targetColumnNameList.get(i1))) + "--";
						}
						sourceColumndataList.add(sourceColumndata);
					}
					Assert.assertEquals(sourceColumndataList, targetColumndataList);
				}
			}
			//CHECK FOR MT_TYPE = 3. Ended here
		}
		log.info("TC 02 Verify the core Schemas Column Name and its corresponding Record are same from Source and Target. Ended");
	}
	
	/**
	 * Verify the core Schemas Column and its corresponding Record are not migrated in other column from Source and Target
	 * @throws Exception
	 */
	@Test
	public void tc03_verifyCoreSchemasColumnAndRecordAreSame() throws Exception {
		log.info("TC 03 Verify the core Schemas Column and its corresponding Record are not migrated in other column from Source and Target. Started");
		establishDatabaseconnection();
		establishTargetDatabaseconnection();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//migration//BussinessRules//MMP547_CoreSchemaSettingTableDataMigration.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONObject jsonObject = (JSONObject) parse;
		Object engineObject = jsonObject.get("settings");
		JSONArray jsonArrayForEngine = (JSONArray) engineObject;
		for (int i = 0; i < jsonArrayForEngine.size() ; i++) {
			Object jsonArrayForEngineObject = jsonArrayForEngine.get(i);
			JSONObject engineKeys = (JSONObject) jsonArrayForEngineObject;
			ArrayList<String> sourceValue = new ArrayList<String>();
			ArrayList<String> targetValueWithEnvId = new ArrayList<String>();
			ArrayList<String> targetValueWithMT0 = new ArrayList<String>();
			ArrayList<String> targetValueWithMT1 = new ArrayList<String>();
			ArrayList<String> targetValueWithMT2 = new ArrayList<String>();
			ArrayList<String> targetValueWithMT3 = new ArrayList<String>();
			ArrayList<String> targetValueWithMT012 = new ArrayList<String>();
			
			//CHECK FOR MT_TYPE = 0. Started
			//Fetching All Data from Source
			sourceQuery = query("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table"));
			while (sourceQuery.next()) {
				sourceValue.add(String.valueOf(sourceQuery.getObject(1)));
			}
			
			//Fetching All Data from Target with ENVIRONMENT_UUID
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where ENVIRONMENT_UUID = '" + envId + "'");
			while (targetQuery.next()) {
				targetValueWithEnvId.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			//Fetching MT_TYPE = 0 records from target without envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE = 0");
			while (targetQuery.next()) {
				targetValueWithMT0.add(String.valueOf(targetQuery.getObject(1)));
			} 
			
			List<String> sourceValueCopy = new ArrayList<String>();
			sourceValueCopy.addAll(sourceValue);
			//targetValueWithMT0 contains matching elements of source and target MT_TYPE=0
			targetValueWithMT0.retainAll(sourceValueCopy);
			
			//targetValueWithEnvId contains matching elements of matching-source and target elements with ENV_ID
			targetValueWithEnvId.retainAll(targetValueWithMT0);
			
			Assert.assertEquals(targetValueWithEnvId.size(), 0, "This following table values are migrated against Bussiness Rules. "
					+ "For the schema:: "+engineKeys.get("schema") +"and table:: "+engineKeys.get("table") + "For MT Type 0");
			//CHECK FOR MT_TYPE = 0. Ended here
			
			
			//CHECK FOR MT_TYPE = 1. Started
			//Fetching MT_TYPE = 1 records from target without envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE = 1");
			while (targetQuery.next()) {
				targetValueWithMT1.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			//Fetching MT_TYPE = 2 records from target with envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE = 2 AND ENVIRONMENT_UUID = '" + envId + "'");
			while (targetQuery.next()) {
				targetValueWithMT2.add(String.valueOf(targetQuery.getObject(1)));
			}
		 
			List<String> copyOfSourceValue = new ArrayList<String>();
			copyOfSourceValue.addAll(sourceValue);
			
			//copyOfSourceValue Contains Matching elements between source and target with MT_TYPE = 1
			copyOfSourceValue.retainAll(targetValueWithMT1);
			
			Assert.assertEquals(targetValueWithMT2, copyOfSourceValue, "This following table values are migrated against Bussiness Rules. "
					+ "For the schema:: "+engineKeys.get("schema") +"and table:: "+engineKeys.get("table") + "For MT Type 2");
			
			
			//Code to fetch Soruce and Target Column Names
			ArrayList<String> targetColumnNameList = new ArrayList<String>();
			targetQuery = targetQuery("select * from " + engineKeys.get("schema") + "." + engineKeys.get("table")
					+ " where ENVIRONMENT_UUID = '" + envId + "' limit 1");
			int targetColumnCount = targetQuery.getMetaData().getColumnCount();
			for (int i1 = 0; i1 < targetColumnCount; i1++) {
				targetColumnNameList.add(targetQuery.getMetaData().getColumnName(i1 + 1));
			}
			List<String> sourceColumnNameList = new ArrayList<>();
			sourceQuery = query(
					"select * from " + engineKeys.get("schema") + "." + engineKeys.get("table") + " limit 1");
			int sourceColumnCount = sourceQuery.getMetaData().getColumnCount();
			for (int i1 = 0; i1 < sourceColumnCount; i1++) {
				sourceColumnNameList.add(sourceQuery.getMetaData().getColumnName(i1 + 1));
			}
			ArrayList<String> copyTargetColumnNameList = new ArrayList<>();
			copyTargetColumnNameList.addAll(targetColumnNameList);
			copyTargetColumnNameList.removeAll(sourceColumnNameList);
			targetColumnNameList.removeAll(copyTargetColumnNameList);
			
			
			//Data Check for MT_TYPE = 1 case
			for (int j = 0; j < 5; j++) {
				String dynamicColumnValue = copyOfSourceValue.get(generate(copyOfSourceValue.size() - 1));

				targetQuery = targetQuery("select * from " + engineKeys.get("schema") + "." + engineKeys.get("table")
						+ " where " + engineKeys.get("field") + " = " + dynamicColumnValue + " and MT_TYPE = 2 and "
						+ "ENVIRONMENT_UUID = '"+ envId + "'");
				
				sourceQuery = query("select * from " + engineKeys.get("schema") + "." + engineKeys.get("table")
						+ " where " + engineKeys.get("field") + " = " + dynamicColumnValue);

				ArrayList<String> targetColumndataList = new ArrayList<>();
				while (targetQuery.next()) {
					String targetColumndata = "";
					for (int i1 = 0; i1 < targetColumnNameList.size(); i1++) {
						targetColumndata = targetColumndata
								+ String.valueOf(targetQuery.getObject(targetColumnNameList.get(i1))) + ".";
					}
					targetColumndataList.add(targetColumndata);
				}
				ArrayList<String> sourceColumndataList = new ArrayList<>();
				while (sourceQuery.next()) {
					String sourceColumndata = "";
					for (int i1 = 0; i1 < targetColumnNameList.size(); i1++) {
						sourceColumndata = sourceColumndata
								+ String.valueOf(sourceQuery.getObject(targetColumnNameList.get(i1))) + ".";
					}
					sourceColumndataList.add(sourceColumndata);
				}
				Assert.assertEquals(sourceColumndataList, targetColumndataList, "Data Mismatched for some records with MT_TYPE=1");
			}
			//CHECK FOR MT_TYPE = 1. Ended here
			
			//Fetching MT_TYPE = 0,1,2 records from target without envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE IN (0,1,2)");
			while (targetQuery.next()) {
				targetValueWithMT012.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			//Fetching MT_TYPE = 3 records from target with envId 
			targetQuery = targetQuery("SELECT "+engineKeys.get("field")+" FROM "+engineKeys.get("schema")+"."+engineKeys.get("table")+
					" where MT_TYPE = 3 AND ENVIRONMENT_UUID = '" + envId + "'");
			while (targetQuery.next()) {
				targetValueWithMT3.add(String.valueOf(targetQuery.getObject(1)));
			}
			
			//sourceValue contains elements which is not in MT_TYPE = 0,1,2
			sourceValue.removeAll(targetValueWithMT012);
			Assert.assertEquals(targetValueWithMT3, sourceValue);


			
			for (int j = 0; j < 5; j++) {
				String dynamicColumnValue = sourceValue.get(generate(sourceValue.size() - 1));

				targetQuery = targetQuery("select * from " + engineKeys.get("schema") + "." + engineKeys.get("table")
						+ " where " + engineKeys.get("field") + " = " + dynamicColumnValue + " and MT_TYPE = 3"
								+ "and ENVIRONMENT_UUID = '"+ envId + "'");
				
				sourceQuery = query("select * from " + engineKeys.get("schema") + "." + engineKeys.get("table")
						+ " where " + engineKeys.get("field") + " = " + dynamicColumnValue);

				ArrayList<String> targetColumndataList = new ArrayList<>();
				while (targetQuery.next()) {
					String targetColumndata = "";
					for (int i1 = 0; i1 < targetColumnNameList.size(); i1++) {
						targetColumndata = targetColumndata
								+ String.valueOf(targetQuery.getObject(targetColumnNameList.get(i1))) + ".";
					}
					targetColumndataList.add(targetColumndata);
				}
				ArrayList<String> sourceColumndataList = new ArrayList<>();
				while (sourceQuery.next()) {
					String sourceColumndata = "";
					for (int i1 = 0; i1 < targetColumnNameList.size(); i1++) {
						sourceColumndata = sourceColumndata
								+ String.valueOf(sourceQuery.getObject(targetColumnNameList.get(i1))) + ".";
					}
					sourceColumndataList.add(sourceColumndata);
				}
				Assert.assertEquals(sourceColumndataList, targetColumndataList);
			}
			//CHECK FOR MT_TYPE = 3. Ended here
		}
		log.info("TC 03 Verify the core Schemas Column and its corresponding Record are not migrated in other column from Source and Target. Ended");
	}

}
