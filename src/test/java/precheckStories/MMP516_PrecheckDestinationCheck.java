package precheckStories;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP516_PrecheckDestinationCheck extends Base{
	
	static Logger log = Logger.getLogger(MMP516_PrecheckDestinationCheck.class.getName());
	
	@Test
	public static void tc01_checkDestinationDBConnection() throws JSchException, SftpException, Exception {
		log.info("TC 01 Checking whether the Destination DB connection is establishing or not. Started.......");
		loadHighLevelReportInBrowser();
		establishSshConnection();
		String connectionStatus = null;
		String host= null;
		String username= null;
		String password= null;
		String port= null;
		List<String> targetDBCredentials = new ArrayList<String>();
		InputStream stream = sftpChannel.get("/home/ec2-user/QA_testing/migration-tool/src/migration/Property.toml");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("db.target") && !line.contains("#")) {
					int count = 0;
					while(count < 4) {
						targetDBCredentials.add(br.readLine());
						count++;
					}
				}
			}
			for(String credentials : targetDBCredentials) {
				if(credentials.contains("host")) {
					host = credentials.split("=")[1].replaceAll("\"", "").trim();
				}
				if(credentials.contains("username")) {
					username = credentials.split("=")[1].replaceAll("\"", "").trim();
				}
				if(credentials.contains("password")) {
					password = credentials.split("=")[1].replaceAll("\"", "").trim();
				}
				if(credentials.contains("port")) {
					port = credentials.split("=")[1].replaceAll("\"", "").trim();
				}
			}
			connectionStatus = establishDestinationDatabaseconnection(host, username, password, port);
			Assert.assertEquals(connectionStatus, "Connection Success");
			log.info("TC 01 Checking whether the Destination DB connection is establishing or not. Ended.......");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}
	}
	
	@Test(enabled=false)
	public static void tc02_checkTheUserHasSuperPrivilege() throws JSchException, SftpException, Exception {
		log.info("TC 02 Checking whether the user has Super Privilege. Started.......");
		loadHighLevelReportInBrowser();
		establishDestinationDatabaseconnection("3.219.199.235", "sushanth", "Ahnha@911", "3306");
		String privilege = null;
		prop = loadQueryFile("//src//test//resources//precheck//queries//MMP516_query.properties");
		sourceQuery = query(prop.getProperty("check_super_privilege"));
		while (sourceQuery.next()) {
			if(sourceQuery.getString("host").equals("localhost")) {
				privilege = sourceQuery.getString("privilege");
			}
		}
		Assert.assertEquals(privilege, "select,index,show_db,execute,show_view,event,trigger,repl_client");
		log.info("TC 02 Checking whether the user has Super Privilege. Ended.......");
	}
}
