package migrationStories;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;

import precheck.Base;

public class MMP372_MigrationDataModificationCoreSchemaEnableAauditTrailLog extends Base {
	static Logger log = Logger
			.getLogger(MMP372_MigrationDataModificationCoreSchemaEnableAauditTrailLog.class.getName());

	/**
	 * This method is to validate input config variable is editable or not
	 * 
	 * @throws Exception
	 */
	@Test
	public void tc01_IsAbleToInputConfigVariable() throws Exception {
		log.info("TC 01 is able to input config variable is editable or not validation started..............");
		establishSshConnectionForSourceInstance();
		String newLine = System.getProperty("line.separator");
		String commandOutput = null;
		String commandtofindPathExistance = null;
		if (osUserInput.equalsIgnoreCase("linux")) {
			commandtofindPathExistance = "sudo test -w " + fileProperties.getProperty("propertyToml_migration_linux")
					+ " && echo 'True' || echo 'False'";
		} else if (osUserInput.equalsIgnoreCase("windows")) {
			String windowsFilePath = fileProperties.getProperty("propertyToml_migration_windows").substring(1)
					.replaceAll("/", "\\\\");
			commandtofindPathExistance = "powershell.exe \"Test-Path " + windowsFilePath + " \"";
		}
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(commandtofindPathExistance);
		InputStream inputStream = channel.getInputStream();
		channel.connect();
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
			commandOutput = lines.collect(Collectors.joining(newLine));
		}
		assertEquals(commandOutput, "True", "config variable file path is not available");
		channel.disconnect();
		sftpChannel.disconnect();
		log.info("TC 01 is able to input config variable is editable or not validation ended..............");
	}

	/**
	 * This method is to validate update query triggered when config variable TRUE Or 1
	 * @throws Exception
	 */
	@Test
	public void tc02_IsUpdateQueryTriggeredWhenConfigVariableTRUEOr1() throws Exception {
		log.info("TC 02 The Update Query triggered when config variable TRUE or 1 validation started..............");
		establishSshConnectionForSourceInstance();
		establishTargetDatabaseconnection();
		InputStream stream = null;
		if (osUserInput.equalsIgnoreCase("linux")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_migration_linux"));
		} else if (osUserInput.equalsIgnoreCase("windows")) {
			stream = sftpChannel.get(fileProperties.getProperty("propertyToml_migration_windows"));
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String lifeScienceCustomer = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("is_life_science_customer") && !line.contains("#")) {
					lifeScienceCustomer = line.split("=")[1].replaceAll("\"", "").replaceAll("\'", "").trim();
				}
			}
			if (null != lifeScienceCustomer && (lifeScienceCustomer == "1" || lifeScienceCustomer == "TRUE")) {
				prop = loadQueryFile(
						"\\src\\test\\resources\\migration\\queries\\MMP372_MigrationDataModificationCoreSchemaEnableAauditTrailLog.properties");
				targetQuery = targetQuery(prop.getProperty("historyType"));
				ArrayList<String> historyTypeList = new ArrayList<String>();
				while (targetQuery.next()) {
					historyTypeList.add(String.valueOf(targetQuery.getObject(1)));
				}
				for (int i = 0; i < historyTypeList.size(); i++) {
					Assert.assertEquals(historyTypeList.get(i), "2",
							"Life Science Customer is enabled but HISTORY_TYPE is not updated to 2");
				}
			} else {
				log.info("Life Science Customer value is 0 or FALSE (Not Enabled)");
			}
			sftpChannel.disconnect();
			session.disconnect();
			log.info("TC 02 The Update Query triggered when config variable TRUE or 1 validation ended..............");
		} catch (IOException io) {
			log.error("Exception occurred during reading file from SFTP server due to " + io.getMessage());
			io.getMessage();
		}

	}

}
