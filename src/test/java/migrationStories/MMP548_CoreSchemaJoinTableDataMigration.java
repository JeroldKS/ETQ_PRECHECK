package migrationStories;

import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import precheck.Base;

public class MMP548_CoreSchemaJoinTableDataMigration extends Base {
	static Logger log = Logger.getLogger(MMP548_CoreSchemaJoinTableDataMigration.class.getName());

	@Test
	public void tc01_IsJoinTablesTransferBasedOnMTTYPEAndKeyColumnName() throws SecurityException, IOException {

	}

	/**
	 * This method is to validate Join Tables not migrated for tables mentioned as
	 * SKIP
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc02_IsJoinTablesNotMigratedForTablesMentionedAsSKIP() throws Exception {
		log.info("TC 02 Join Tables not migrated for tables mentioned as SKIP validation started.......");
		establishDatabaseconnection();
		establishTargetDatabaseconnection();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//migration//BussinessRules//MMP548_CoreSchemaJoinTableDataMigration.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONObject jsonObject = (JSONObject) parse;
		Object object = jsonObject.get("skip");
		JSONArray jsonArray = (JSONArray) object;
		for (int i = 0; i < jsonArray.size(); i++) {
			Object innerObject = jsonArray.get(i);
			JSONObject parseStep1 = (JSONObject) innerObject;
			log.info((String) parseStep1.get("schema"));
			log.info((String) parseStep1.get("table"));
			targetQuery = targetQuery("select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
					+ " where ENVIRONMENT_UUID = '" + envId + "'");
			if (targetQuery.next() != false) {
				Assert.assertTrue(false,
						" The Table is not skipped and against Bussiness Rules. Following query states the error \" "
								+ "select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
								+ " where ENVIRONMENT_UUID = '" + envId + "' \" ");
			}
		}
		log.info("TC 02 Join Tables not migrated for tables mentioned as SKIP validation ended.......");
	}

	/**
	 * This method is to validate Join Table excluded record where application ID
	 * returns
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc03_IsJoinTableExcludedRecordWhereApplicationIDReturns() throws Exception {
		log.info("TC 03 Join Table excluded record where application ID returns validation started.......");
		establishDatabaseconnection();
		establishTargetDatabaseconnection();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+"//src//test//resources//migration//BussinessRules//MMP548_CoreSchemaJoinTableDataMigration.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONObject jsonObject = (JSONObject) parse;
		Object object = jsonObject.get("exclude");
		JSONArray jsonArray = (JSONArray) object;
		System.err.println(jsonArray.size());
		for (int i = 0; i < jsonArray.size(); i++) {
			Object innerObject = jsonArray.get(i);
			JSONObject parseStep1 = (JSONObject) innerObject;
			log.info((String) parseStep1.get("schema"));
			log.info((String) parseStep1.get("table"));
			log.info((String) parseStep1.get("field"));
			log.info((String) parseStep1.get("query"));
			ArrayList<String> sourceColumnValue = new ArrayList<String>();
			ArrayList<String> targetColumnValue = new ArrayList<String>();
			sourceQuery = query(parseStep1.get("query").toString());
			while (sourceQuery.next()) {
				sourceColumnValue.add(String.valueOf(sourceQuery.getObject(1)));
			}
			System.out.println(sourceColumnValue.size());// System.out.println(sourceColumnValue);
			targetQuery = targetQuery("select " + parseStep1.get("field") + " from " + parseStep1.get("schema") + "."
					+ parseStep1.get("table") + " where ENVIRONMENT_UUID = '" + envId + "'");
			while (targetQuery.next()) {
				targetColumnValue.add(String.valueOf(targetQuery.getObject(1)));
			}
			System.out.println(targetColumnValue.size() + " " + targetColumnValue);
			ArrayList<String> copyTargetColumnValue = new ArrayList<String>();
			copyTargetColumnValue.addAll(targetColumnValue);
			copyTargetColumnValue.removeAll(sourceColumnValue);
			Assert.assertEquals(copyTargetColumnValue.size(), targetColumnValue.size(), copyTargetColumnValue
					+ " This list values are migrated against Bussiness Rules. Following query states the error \" "
					+ "select " + parseStep1.get("field") + " from " + parseStep1.get("schema") + "."
					+ parseStep1.get("table") + " where ENVIRONMENT_UUID = '" + envId + "' \" ");

			for (int j = 0; j < 5; j++) {
				String dynamicColumnValue = sourceColumnValue.get(generate(sourceColumnValue.size() - 1));
				System.out.println("select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
						+ " where ENVIRONMENT_UUID = '" + envId + "' and " + parseStep1.get("field") + " = '"
						+ dynamicColumnValue);
				targetQuery = targetQuery("select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
						+ " where ENVIRONMENT_UUID = '" + envId + "' and " + parseStep1.get("field") + " = '"
						+ dynamicColumnValue + "'");
				if (targetQuery.next() != false) {
					Assert.assertTrue(false,
							" The values are migrated against Bussiness Rules. Following query states the error \" "
									+ "select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
									+ " where ENVIRONMENT_UUID = '" + envId + "' and " + parseStep1.get("field")
									+ " = '" + dynamicColumnValue + "'\" ");
				}
			}
		}
		log.info("TC 03 Join Table excluded record where application ID returns validation ended.......");
	}

	/**
	 * This method is to validate Join Table Migrated schema with views only with
	 * defined ids
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc04_IsJoinTableMigratedSchemaWithViewsOnlyWithDefinedIds() throws Exception {
		log.info("TC 04 Join Table Migrated schema with views only with defined ids validation started.......");
		establishDatabaseconnection();
		establishTargetDatabaseconnection();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//migration//BussinessRules//MMP548_CoreSchemaJoinTableDataMigration.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONObject jsonObject = (JSONObject) parse;
		Object object = jsonObject.get("include");
		JSONArray jsonArray = (JSONArray) object;
		System.err.println(jsonArray.size());
		for (int i = 0; i < jsonArray.size(); i++) {
			Object innerObject = jsonArray.get(i);
			JSONObject parseStep1 = (JSONObject) innerObject;
			log.info((String) parseStep1.get("schema"));
			log.info((String) parseStep1.get("table"));
			log.info((String) parseStep1.get("field"));
			log.info((String) parseStep1.get("query"));
			ArrayList<String> sourceColumnValue = new ArrayList<String>();
			ArrayList<String> targetColumnValue = new ArrayList<String>();
			sourceQuery = query(parseStep1.get("query").toString());
			while (sourceQuery.next()) {
				sourceColumnValue.add(String.valueOf(sourceQuery.getObject(1)));
			}
			System.out.println(sourceColumnValue.size());// System.out.println(sourceColumnValue);
			targetQuery = targetQuery("select " + parseStep1.get("field") + " from " + parseStep1.get("schema") + "."
					+ parseStep1.get("table") + " where ENVIRONMENT_UUID = '" + envId + "'");
			while (targetQuery.next()) {
				targetColumnValue.add(String.valueOf(targetQuery.getObject(1)));
			}
			System.out.println(targetColumnValue.size() + " " + targetColumnValue);
			ArrayList<String> copyTargetColumnValue = new ArrayList<String>();
			copyTargetColumnValue.addAll(targetColumnValue);
			copyTargetColumnValue.removeAll(sourceColumnValue);
			Assert.assertEquals(copyTargetColumnValue.size(), 0, copyTargetColumnValue
					+ " This list values are migrated against Bussiness Rules. Following query states the error \" "
					+ "select " + parseStep1.get("field") + " from " + parseStep1.get("schema") + "."
					+ parseStep1.get("table") + " where ENVIRONMENT_UUID = '" + envId + "' \" ");
			ArrayList<String> targetColumnNameList = new ArrayList<String>();
			targetQuery = targetQuery("select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
					+ " where ENVIRONMENT_UUID = '" + envId + "' limit 1");
			int targetColumnCount = targetQuery.getMetaData().getColumnCount();
			for (int i1 = 0; i1 < targetColumnCount; i1++) {
				targetColumnNameList.add(targetQuery.getMetaData().getColumnName(i1 + 1));
			}
			List<String> sourceColumnNameList = new ArrayList<>();
			sourceQuery = query(
					"select * from " + parseStep1.get("schema") + "." + parseStep1.get("table") + " limit 1");
			int sourceColumnCount = sourceQuery.getMetaData().getColumnCount();
			for (int i1 = 0; i1 < sourceColumnCount; i1++) {
				sourceColumnNameList.add(sourceQuery.getMetaData().getColumnName(i1 + 1));
			}
			ArrayList<String> copyTargetColumnNameList = new ArrayList<>();
			copyTargetColumnNameList.addAll(targetColumnNameList);
			copyTargetColumnNameList.removeAll(sourceColumnNameList);
			targetColumnNameList.removeAll(copyTargetColumnNameList);

			for (int j = 0; j < 5; j++) {
				System.out.println(generate(targetColumnValue.size() - 1));
				String dynamicColumnValue = targetColumnValue.get(generate(targetColumnValue.size() - 1));
				System.err.println("select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
						+ " where " + parseStep1.get("field") + " = " + dynamicColumnValue);

				targetQuery = targetQuery("select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
						+ " where " + parseStep1.get("field") + " = " + dynamicColumnValue + " and ENVIRONMENT_UUID = '"
						+ envId + "'");
				sourceQuery = query("select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
						+ " where " + parseStep1.get("field") + " = " + dynamicColumnValue);

				ArrayList<String> TargetColumndataList = new ArrayList<>();
				while (targetQuery.next()) {
					String TargetColumndata = "";
					for (int i1 = 0; i1 < targetColumnNameList.size(); i1++) {
						TargetColumndata = TargetColumndata
								+ String.valueOf(targetQuery.getObject(targetColumnNameList.get(i1))) + ".";
					}
					TargetColumndataList.add(TargetColumndata);
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
				Assert.assertEquals(sourceColumndataList, TargetColumndataList);
			}
		}
		log.info("TC 04 Join Table Migrated schema with views only with defined ids validation ended.......");
	}

	/**
	 * This method is to validate all record migrated in MT Type 3 form source to
	 * target
	 * 
	 * @throws Exception
	 */
	@Test
	public void isAllRecordMigratedInMTType3() throws Exception {

		establishDatabaseconnection();
		establishTargetDatabaseconnection();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//migration//BussinessRules//MMP548_CoreSchemaJoinTableDataMigration.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONObject jsonObject = (JSONObject) parse;
		Object object = jsonObject.get("migrateAllMT3");
		JSONArray jsonArray = (JSONArray) object;
		System.err.println(jsonArray.size());

		for (int i = 0; i < jsonArray.size(); i++) {
			Object innerObject = jsonArray.get(i);
			JSONObject parseStep1 = (JSONObject) innerObject;
			log.info((String) parseStep1.get("schema"));
			log.info((String) parseStep1.get("table"));
			ArrayList<String> targetColumnNameList = new ArrayList<String>();
			targetQuery = targetQuery("select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
					+ " where ENVIRONMENT_UUID = '" + envId + "' limit 1");
			int targetColumnCount = targetQuery.getMetaData().getColumnCount();
			for (int i1 = 0; i1 < targetColumnCount; i1++) {
				targetColumnNameList.add(targetQuery.getMetaData().getColumnName(i1 + 1));
			}
			List<String> sourceColumnNameList = new ArrayList<>();
			sourceQuery = query(
					"select * from " + parseStep1.get("schema") + "." + parseStep1.get("table") + " limit 1");
			int sourceColumnCount = sourceQuery.getMetaData().getColumnCount();
			ArrayList<String> sourceColumnDataType = new ArrayList<String>();
			ArrayList<String> dateTimeColumnName = new ArrayList<String>();
			for (int i1 = 0; i1 < sourceColumnCount; i1++) {
				sourceColumnNameList.add(sourceQuery.getMetaData().getColumnName(i1 + 1));
				sourceColumnDataType.add(sourceQuery.getMetaData().getColumnTypeName(i1 + 1));
				if ((sourceQuery.getMetaData().getColumnTypeName(i1 + 1)).equals("DATETIME")) {
					dateTimeColumnName.add(sourceQuery.getMetaData().getColumnName(i1 + 1));
				}
			}
			ArrayList<String> copyTargetColumnNameList = new ArrayList<>();
			copyTargetColumnNameList.addAll(targetColumnNameList);
			copyTargetColumnNameList.removeAll(sourceColumnNameList);
			targetColumnNameList.removeAll(copyTargetColumnNameList);
			String columnNameString = "";
			for (int j = 0; j < targetColumnNameList.size(); j++) {
				if (dateTimeColumnName.contains(targetColumnNameList.get(j))) {
					columnNameString = columnNameString + "CAST(" + targetColumnNameList.get(j) + " AS char) as "
							+ targetColumnNameList.get(j);
					if (j < targetColumnNameList.size() - 1) {
						columnNameString = columnNameString + ",";
					}
				} else {
					columnNameString = columnNameString + targetColumnNameList.get(j);
					if (j < targetColumnNameList.size() - 1) {
						columnNameString = columnNameString + ",";
					}
				}

			}
			System.err.println(columnNameString);
			sourceQuery = query("select " + columnNameString + " from " + parseStep1.get("schema") + "."
					+ parseStep1.get("table") + " ORDER BY RAND() LIMIT 1");
			while (sourceQuery.next()) {
				String dynamicQueryGenertor = "";
				for (int j = 0; j < targetColumnNameList.size(); j++) {
					if (String.valueOf(sourceQuery.getObject(j + 1)).equals("null")) {
						dynamicQueryGenertor = dynamicQueryGenertor + " and " + targetColumnNameList.get(j) + " is "
								+ String.valueOf(sourceQuery.getObject(j + 1));

					} else {

						dynamicQueryGenertor = dynamicQueryGenertor + " and " + targetColumnNameList.get(j) + " = '"
								+ String.valueOf(sourceQuery.getObject(j + 1)).replace("'", "''") + "'";

					}
				}
				System.err.println(dynamicQueryGenertor);
				targetQuery = targetQuery("select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
						+ " where ENVIRONMENT_UUID = '" + envId + "' and MT_TYPE = 3 " + dynamicQueryGenertor);
				while (!targetQuery.next()) {
					Assert.assertNotNull(null,
							"Record is not migrate. Refer the query \"" + "select * from " + parseStep1.get("schema")
									+ "." + parseStep1.get("table") + " where ENVIRONMENT_UUID = '" + envId
									+ "' and MT_TYPE = 3 " + dynamicQueryGenertor + "\"");
				}
			}
		}

	}

	/**
	 * This method is to validate Join table migrated schema in MT type 3 with views
	 * only with defined ids
	 * 
	 * @throws Exception
	 */
	@Test
	public void IsJoinTableMigratedSchemaInMTType3WithViewsOnlyWithDefinedIds() throws Exception {
		establishDatabaseconnection();
		establishTargetDatabaseconnection();
		FileReader jsonfile = new FileReader(System.getProperty("user.dir")
				+ "//src//test//resources//migration//BussinessRules//MMP548_CoreSchemaJoinTableDataMigration.json");
		JSONParser jsonParser = new JSONParser();
		Object parse = jsonParser.parse(jsonfile);
		JSONObject jsonObject = (JSONObject) parse;
		Object object = jsonObject.get("migrateIdsIncludeMT3");
		JSONArray jsonArray = (JSONArray) object;
		for (int i = 0; i < jsonArray.size(); i++) {
			Object innerObject = jsonArray.get(i);
			JSONObject parseStep1 = (JSONObject) innerObject;
			log.info((String) parseStep1.get("schema"));
			log.info((String) parseStep1.get("table"));
			log.info((String) parseStep1.get("field"));
			log.info((String) parseStep1.get("query"));
			ArrayList<String> sourceColumnValue = new ArrayList<String>();
			ArrayList<String> targetColumnValue = new ArrayList<String>();
			sourceQuery = query(parseStep1.get("query").toString());
			while (sourceQuery.next()) {
				sourceColumnValue.add(String.valueOf(sourceQuery.getObject(1)));
			}
			targetQuery = targetQuery("select " + parseStep1.get("field") + " from " + parseStep1.get("schema") + "."
					+ parseStep1.get("table") + " where ENVIRONMENT_UUID = '" + envId + "' and MT_TYPE = 3");
			while (targetQuery.next()) {
				targetColumnValue.add(String.valueOf(targetQuery.getObject(1)));
			}
			ArrayList<String> copyTargetColumnValue = new ArrayList<String>();
			copyTargetColumnValue.addAll(targetColumnValue);
			copyTargetColumnValue.removeAll(sourceColumnValue);
			Assert.assertEquals(copyTargetColumnValue.size(), 0, copyTargetColumnValue
					+ " This list values are migrated against Bussiness Rules. Following query states the error \" "
					+ "select " + parseStep1.get("field") + " from " + parseStep1.get("schema") + "."
					+ parseStep1.get("table") + " where ENVIRONMENT_UUID = '" + envId + "' \" ");
			ArrayList<String> targetColumnNameList = new ArrayList<String>();
			targetQuery = targetQuery("select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
					+ " where ENVIRONMENT_UUID = '" + envId + "' and MT_TYPE = 3 limit 1");
			int targetColumnCount = targetQuery.getMetaData().getColumnCount();
			for (int i1 = 0; i1 < targetColumnCount; i1++) {
				targetColumnNameList.add(targetQuery.getMetaData().getColumnName(i1 + 1));
			}
			List<String> sourceColumnNameList = new ArrayList<>();
			sourceQuery = query(
					"select * from " + parseStep1.get("schema") + "." + parseStep1.get("table") + " limit 1");
			int sourceColumnCount = sourceQuery.getMetaData().getColumnCount();
			for (int i1 = 0; i1 < sourceColumnCount; i1++) {
				sourceColumnNameList.add(sourceQuery.getMetaData().getColumnName(i1 + 1));
			}
			ArrayList<String> copyTargetColumnNameList = new ArrayList<>();
			copyTargetColumnNameList.addAll(targetColumnNameList);
			copyTargetColumnNameList.removeAll(sourceColumnNameList);
			targetColumnNameList.removeAll(copyTargetColumnNameList);
			for (int j = 0; j < 5; j++) {
				String dynamicColumnValue = "";
				if (targetColumnValue.size() != 0) {
					dynamicColumnValue = targetColumnValue.get(generate(targetColumnValue.size() - 1));
				} else {
					continue;
				}
				targetQuery = targetQuery("select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
						+ " where " + parseStep1.get("field") + " = " + dynamicColumnValue + " and ENVIRONMENT_UUID = '"
						+ envId + "' and MT_TYPE = 3");
				sourceQuery = query("select * from " + parseStep1.get("schema") + "." + parseStep1.get("table")
						+ " where " + parseStep1.get("field") + " = " + dynamicColumnValue);
				ArrayList<String> TargetColumndataList = new ArrayList<>();
				while (targetQuery.next()) {
					String TargetColumndata = "";
					for (int i1 = 0; i1 < targetColumnNameList.size(); i1++) {
						TargetColumndata = TargetColumndata
								+ String.valueOf(targetQuery.getObject(targetColumnNameList.get(i1))) + ".";
					}
					TargetColumndataList.add(TargetColumndata);
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
				Assert.assertEquals(sourceColumndataList, TargetColumndataList);
			}
		}
	}

}
