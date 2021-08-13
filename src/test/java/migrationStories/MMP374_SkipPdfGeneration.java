package migrationStories;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP374_SkipPdfGeneration extends Base {
 
	static Logger log = Logger.getLogger(MMP374_SkipPdfGeneration.class.getName());
	
	/**
	 * Checking whether datacenter.login_tracker and datacenter.login_related_groups are not created in target
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc02_verifyDataCenterPdfGenerationHasSkipped() throws JSchException, SftpException, Exception {
		log.info("TC 02 Verifying that the table \"datacenter.pdf_generation_messages\" has been skipped. Started.......");
		String connectionStatus = establishTargetDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP374_query.properties");
		List<String> dataCenterPdfGenerationTable = new ArrayList<>();
		sourceQuery = query(prop.getProperty("datacenter_pdf_generation"));
		while (sourceQuery.next()) {
			dataCenterPdfGenerationTable.add(sourceQuery.getObject(0).toString());
		}
		Assert.assertEquals(dataCenterPdfGenerationTable.size(),0);
		log.info("TC 02 Verifying that the table \"datacenter.pdf_generation_messages\" has been skipped. Ended.......");
	}
}
