package precheckStories;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.testng.annotations.Test;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import precheck.Base;

public class MMP512_WindowsInfrastructureChecks extends Base {
	public static ChannelExec channel;
	public static ChannelExec channelExec;
	private static final String REMOTE_HOST = "34.226.94.226";
    private static final String USERNAME = "etqadmin";
    private static final String PASSWORD = "KO^ElnWy7CFpi#";
    private static final int REMOTE_PORT = 22;
    //private static final int SESSION_TIMEOUT = 10000;
    //private static final int CHANNEL_TIMEOUT = 10000;
	
    @Test
	public static void tc01_IsPerformWindowsInfrastructureChecks() throws Exception {
		//establishWindowsSshConnection();
		
		String newLine = System.getProperty("line.separator");
		String commandOutput = null;
		 Session jschSession = null;
		 JSch jsch = new JSch();
		 jschSession = jsch.getSession(USERNAME, REMOTE_HOST,REMOTE_PORT);
		 jschSession.setPassword(PASSWORD);
         java.util.Properties config = new java.util.Properties();
         config.put("StrictHostKeyChecking", "no");
         jschSession.setConfig(config);
         jschSession.connect();
         System.out.println("session connected");
         ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");
         // run a shell script
         channelExec.setCommand( "powershell.exe  (Get-WmiObject -class Win32_OperatingSystem).Caption" );
         // display errors to System.err
         channelExec.setErrStream(System.err);
         InputStream in = channelExec.getInputStream();
         channelExec.connect();
         try (Stream<String> lines = new BufferedReader(new InputStreamReader(in)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 			System.out.println("OUTPUT : "+commandOutput.toString());
 		}
         
         channelExec = (ChannelExec) jschSession.openChannel("exec");
         channelExec.setCommand( "powershell.exe  \"(Get-CimInstance Win32_PhysicalMemory | Measure-Object -Property capacity -Sum).sum /1gb | % {write-output $_'GB'}\"" );
         channelExec.setErrStream(System.err);
         in = channelExec.getInputStream();
         channelExec.connect();
         try (Stream<String> lines = new BufferedReader(new InputStreamReader(in)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 			System.out.println("OUTPUT : "+commandOutput.toString());
 		}
         
         channelExec = (ChannelExec) jschSession.openChannel("exec");
         channelExec.setCommand( "powershell.exe  \"Get-WmiObject -class Win32_processor | select NumberOfCores | ConvertTo-Json\"" );
         channelExec.setErrStream(System.err);
         in = channelExec.getInputStream();
         channelExec.connect();
         try (Stream<String> lines = new BufferedReader(new InputStreamReader(in)).lines()) {
 			commandOutput = lines.collect(Collectors.joining(newLine));
 			System.out.println("OUTPUT : "+commandOutput.toString());
 		}
		 
        channelExec = (ChannelExec) jschSession.openChannel("exec");
        channelExec.setCommand( "powershell.exe  [System.Security.Principal.WindowsIdentity]::GetCurrent().Name" );
        channelExec.setErrStream(System.err);
        in = channelExec.getInputStream();
        channelExec.connect();
        try (Stream<String> lines = new BufferedReader(new InputStreamReader(in)).lines()) {
			commandOutput = lines.collect(Collectors.joining(newLine));
			System.out.println("OUTPUT : "+commandOutput.toString());
		}
        
        channelExec = (ChannelExec) jschSession.openChannel("exec");
        channelExec.setCommand( "powershell.exe  \"([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)\"" );
        channelExec.setErrStream(System.err);
        in = channelExec.getInputStream();
        channelExec.connect();
        try (Stream<String> lines = new BufferedReader(new InputStreamReader(in)).lines()) {
			commandOutput = lines.collect(Collectors.joining(newLine));
			System.out.println("OUTPUT : "+commandOutput.toString());
		}
		  

	}
}
