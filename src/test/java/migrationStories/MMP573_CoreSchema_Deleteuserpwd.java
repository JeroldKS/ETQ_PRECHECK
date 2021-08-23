package migrationStories;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import precheck.Base;

public class MMP573_CoreSchema_Deleteuserpwd extends Base {

	static Logger log = Logger.getLogger(MMP573_CoreSchema_Deleteuserpwd.class.getName());

	/**  Verify the user able to delete the password against environment ID at the end of migration.
	 *    
	 * @throws JSchException
	 * @throws SftpException
	 * @throws Exception
	 */
	@Test
	public static void tc01_verifyIfTheDelteQueryIsTrigeredInTarget()
			throws JSchException, SftpException, Exception {
		log.info("TC01_Verify the user able to delete the password against environment ID at the end of migration....is started");
		prop = loadQueryFile("//src//test//resources//migration//queries//MMP573_query.properties");
		String connectionStatus = establishTargetDatabaseconnection();
		establishDatabaseconnection();
		Assert.assertEquals(connectionStatus, "Connection Success");
		List<String> queryResult = new ArrayList<>();
		targetQuery = targetQuery(prop.getProperty("deletequery")+envId+"'");
			while (targetQuery.next()) {
				queryResult.add(String.valueOf(targetQuery.getObject(1)));
			}
			Assert.assertEquals(queryResult.size(), 0);
	log.info("TC01_Verify the user able to delete the password against environment ID at the end of migration.... Ended.......");
	}
}
