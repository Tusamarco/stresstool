package net.tc.stresstool;
/**
 * <p>Title: MySQL StressTool</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Marco Tusa Copyright (c) 2007 Marco Tusa Copyright (c) 2013</p>
 * @author Marco Tusa
 * @version 2.0
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * <p>Company: tusacentral</p>
 *
 */

/**
 * V x.0
 */

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;
import org.ini4j.*;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
//import com.googlecode.lanterna.terminal.TerminalSize;
//import com.googlecode.lanterna.terminal.text.FixedTerminalSizeProvider;

import net.tc.*;
import net.tc.stresstool.actions.Launcher;
import net.tc.stresstool.config.Configuration;
import net.tc.stresstool.config.Configurator;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.messages.ApplicationMessages;
import net.tc.stresstool.statistics.StatCollector;
import net.tc.stresstool.statistics.providers.ConsoleStatePrinter;
import net.tc.stresstool.value.ValueProvider;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;

//import com.mysql.stresstool.StressTool;
//import com.mysql.stresstool.Utils;


/**
 * @author  tusa
 * 
 * Main class from where all starts
 * v 1.07
 */
public class StressTool {
	/**
	 * @uml.property  name="stressSettings"
	 * @uml.associationEnd  readOnly="true"
	 */
	private Wini stressSettings;
	private static Configurator config;
	private static boolean StressToolRunning = false;
	/**
	 * @uml.property  name="applicationLogger"
	 * @uml.associationEnd  
	 */
	private Logger applicationLogger ;
	private static LogProvider logProvider;
	private Launcher launcher;
	private static SynchronizedMap threadsInfo = new SynchronizedMap(0);
	/**
	 * @uml.property  name="connectionInformation"
	 */
	private Map connectionInformation;
	private static ValueProvider valueProvider = null;
	
	public StressTool(String[] args) {
        try {
            	/*
            	 * Configuration first 
            	 */
	        if(args.length >= 1 && args[0].indexOf("defaults-file") > 0){
	        		  config = new Configurator();
	        		  config.loadNewConfiguration(args,((String)args[0].split("=")[1]), this.getClass());
	        		  
	        	
	        }
	        else{
			ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
	        	throw new  StressToolConfigurationException(ExceptionMessages.INVALID_CONFIGURATION_REQUEST 
						+ " defaults-file = " + args[0] + "not define,"
						+ " or file not found" );
	        	
	        }
        
	        /* main Initializations
	         * 
	         */
	        init();
	        config.setLog(logProvider.getLogger(LogProvider.LOG_APPLICATION));
	        
	        launcher = new Launcher(config);

	    	long loops = 0;
	    	if(launcher.isUseHardStop()
	    			&& launcher.getHardStopLimit() > 0) {
	    		loops = launcher.getHardStopLimit();
	    		launcher.setRepeatNumber(Integer.MAX_VALUE);
	    	}
	    	else
	    		loops = launcher.getRepeatNumber(); 
	        
	        
	        /*
	         * test connection against the declared url
	         */
	        connectionInformation = launcher.validatePermission(getConfig().getConfiguration(Configurator.MAIN_SECTION_NAME, this.getClass()));
	            
	        
	        
//	        connectionInformation = testConnection(getConfig().getConfiguration(Configurator.MAIN_SECTION_NAME, this.getClass()));
	        
	        config.printConfiguration();
	        
	        StatCollector stats = new StatCollector(config,launcher.getConnProvider());

	        /*
	         * Inizialize  the launcher laoding all class informations
	         * From configuration and creating a base class that will be use by reflection for creating the pool
	         * 
	         */

	        logProvider.getLogger(LogProvider.LOG_APPLICATION).info("Preparing Structure ");
	        if(launcher.CreateStructure()){
	            logProvider.getLogger(LogProvider.LOG_APPLICATION).info("Structure DONE ");
	            
	        }
	        
	        
	        if(launcher.prepareLauncher()){
	            logProvider.getLogger(LogProvider.LOG_APPLICATION).info("*********************************");
	            logProvider.getLogger(LogProvider.LOG_APPLICATION).info("Classes sucessfully initialized");
	            logProvider.getLogger(LogProvider.LOG_APPLICATION).info("*********************************");
	            
	        }
	        
	        
	        if((String) config.getConfiguration(Configurator.MAIN_SECTION_NAME, StressTool.class).getParameter("datafilepath").getValue() !=null){
	           
	            logProvider.getLogger(LogProvider.LOG_APPLICATION).info("Data Load from file datafilepath = "
	        	    + ((String) config.getConfiguration(Configurator.MAIN_SECTION_NAME, StressTool.class).getParameter("datafilepath").getValue())
	        	    + " Starting now"
	        	    );
	            setValueProvider(launcher.LoadData());
	            logProvider.getLogger(LogProvider.LOG_APPLICATION).info("Data Load from file completed ");
	        }
	        
	        /*
	         * Run the launcher to execute the threads
	         * looping for a number of X loop = StatLoops if lower than repeatNumber
	         * Or force the close if UseHardStop is true  
	         */

//	    	long loops = 0;
//	    	if(launcher.isUseHardStop()
//	    			&& launcher.getHardStopLimit() > 0) {
//	    		loops = launcher.getHardStopLimit();
//	    		launcher.setRepeatNumber(Integer.MAX_VALUE);
//	    	}
//	    	else
//	    		loops = launcher.getRepeatNumber();

	    	int timeForLoop =launcher.getStatIntervalMs();
	    	
	    	if(timeForLoop < 1000){
	    		loops = loops * (1000/timeForLoop);
	    	}
	    	else{
	    		loops = loops / (timeForLoop/1000);
	    	}

	        
	        StressTool.setStressToolRunning(true);
	        
	        ConsoleStatePrinter consolePrinter =null;
//	        int launchCountDown = 0 ;
	        if(launcher.getInteractive() >0 ) {
	            consolePrinter = new ConsoleStatePrinter(launcher);
//	            launchCountDown = launcher.getSemaphoreCountdownTime();
	        
	        }

	        
	
	        while(StressTool.isStressToolRunning()){
	          int line =1;
	          float curPct = (float) 0.0;
	          float maxPct = loops;
	          int calendarReset = 10;
//	          int aInsert =  new Float( maxInsert).intValue();
	          
//		      consolePrinter.printCountDown(--launchCountDown);
	          
//	          
//	          if(launcher.getHardStopLimit() > 0  
//		         && launcher.isUseHardStop()) {
//	        	 loops = launcher.getHardStopLimit(); 
//	        	  
//	          }

	          for(int i = 0 ; i < loops; i++ ){
	            
	        	 if(StressTool.isStressToolRunning()) 
	        	  stats.collectStatistics();
	            
	             if(
	            	i > launcher.getHardStopLimit()  
	        	     && launcher.isUseHardStop())
	        	 break;
	             
	             
	             /*
	              * reset calendar date after X number of loops to have consistent date used by the value provider
	              */
	             if(calendarReset == 0){
	            	 launcher.resetValueProviderCalendar();
	            	 calendarReset = 10 ; 
	             }
	             else
	            	 calendarReset--;
	             
	             StressTool.setStressToolRunning(launcher.LaunchActions(consolePrinter));
	             logProvider.getLogger(LogProvider.LOG_APPLICATION).debug("Running loop = " + i);

	             
	             if(launcher.getInteractive() >0  && consolePrinter != null){
	               consolePrinter.printLine(i);
	               //	             }
	               if(launcher.getInteractive()> 0 ){
	            	 if(!consolePrinter.askQuestion("Press \"#\" to stop StressTool", "#",false)){
	            	   StressTool.setStressToolRunning(false);
	            	 };
	               }
	             }
//	             System.out.println(i + ".");
	             
	             if(!StressTool.isStressToolRunning())
	        	 break;
	             
        	     try {
        		 Thread.sleep(launcher.getStatIntervalMs());
        	     } catch (InterruptedException e) {
        		 // TODO Auto-generated catch block
        		 e.printStackTrace();
        	     }
	           }
	          StressTool.setStressToolRunning(false);
	          launcher.terminateThreads();
	         }
	         
	            
	        
	        
	        
	        
	        if(launcher.getInteractive() >1  && consolePrinter != null){
	          while(consolePrinter.askQuestion(ApplicationMessages.End_QUESTION,"y\n",true)){
	        	  //
	          }
	        }
	        if(consolePrinter !=null)
	        	consolePrinter.close();
	        printReport(stats);
	        System.out.println("Close");
	        
            }
        	catch(StressToolException ex){
        		ex.printStackTrace();
        	}
        
	}





	/**
	 * @param args
	 * @throws StressToolConfigurationException 
	 * 
	 * The Main class instantiate a StressTool object and from there all starts
	 */
	public static void main(String[] args)  {

	if(args == null ||(args.length >= 1 && args[0].indexOf("help") > -1 ))
            showHelp();

        StressTool runningStress = new StressTool(args);

        System.exit(ExceptionMessages.getCurrentError());
	}
	
	/**
	 * This method initialize the class loading the configuration and testing the logs defined in the configuration
	 * @throws StressToolException
	 * 
	 */
	private void init() throws StressToolException{
	    Configuration logConf = null;
	    
	    try {
		  logConf = getConfig().getConfiguration("logs", this.getClass());
		  if(logConf ==null){
		      ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
		      throw new StressToolConfigurationException(ExceptionMessages.INVALID_CONFIGURATION_REQUEST 
			      + " Log definition not found in [logs] section");
		  }
	    } catch (StressToolException e) {
		e.printStackTrace();
	    }
	    logProvider = new LogProvider();

	    try {
		logProvider.initLogs(logConf);
		setApplicationLogger(logProvider.getLogger((String)(logConf.getParameter("applicationlog").getValue())));
		getApplicationLogger().info(ApplicationMessages.INIT_TERMINATE_SUCESSFULLY_EN + Utility.getTimestamp());
		
		
	    } catch (StressToolException e) {
		ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
        	throw new  StressToolConfigurationException(ExceptionMessages.INVALID_LOG_REQUEST  
					+ " Logger = applicationlog not define,"
					+ " or log improperly set, review LOG configuration" );

		
	    }
	    
	}
	
	/**
	 * @return    the applicationLogger
	 * @uml.property  name="applicationLogger"
	 */
	public final Logger getApplicationLogger() {
	    return applicationLogger;
	}

	/**
	 * @return the logProvider
	 */
	public static final LogProvider getLogProvider() {
	    return logProvider;
	}





	/**
	 * @param logProvider the logProvider to set
	 */
	public static final void setLogProvider(LogProvider logProvider) {
	    StressTool.logProvider = logProvider;
	}


	/**
	 * @param applicationLogger    the applicationLogger to set
	 * @uml.property  name="applicationLogger"
	 */
	public final void setApplicationLogger(Logger applicationLogger) {
	    this.applicationLogger = applicationLogger;
	}

	/**
	 * @return the config
	 */
	public static final Configurator getConfig() {
	    return config;
	}

	/**
	 * @param config the config to set
	 */
	public static final void setConfig(Configurator config) {
	    StressTool.config = config;
	}

	private static void showHelp() {
		
//	    String[]  arParameters = new String[]{
//	    		"connUrl","truncate","droptable","poolNumber",
//	            "repeatNumber","tableEngine","sleepFor","dbType",
//	            "operationShort","doDelete","doBatch",
//	            "pctInsert","pctSelect","dolog","doReport","InsertDefaultClass","SelectDefaultClass","DeleteDefaultClass","ConnectString","DataBase"};

	    StringBuffer sb = new StringBuffer();
	    sb.append("/**\n");
	    sb.append("value in args must be pass as <section>@parameter=value \n");
	    sb.append("* connUrl jdbc url //jdbc:mysql://127.0.0.1:3320/test?user=test&password=test&autoReconnect=true\n");
	    sb.append("* createtable  [true|false] = Create the tables on test DB\n");
	    sb.append("* truncate  [true|false] = truncate tables on test DB\n");
	    sb.append("* droptable  [true|false] = drop tables on test DB\n");
	    sb.append("* poolNumber (number of threads)\n");
	    sb.append("* repeatNumber the cycle the threads will do(cycles)\n");
	    sb.append("* @Deprecated tableEngine mysql engine (MyISAM; InnoDb; Memory; ndbcluster; mariaDB)\n");
	    sb.append("* sleepFor Thread sleep time (milliseconds default is 0 ms)\n");
	    sb.append("* dolog DON'T USE IT write the log on standard output\n");
	    sb.append("* @Deprecated dbType [Oracle|MySQL] default MySQL\n");
	    sb.append("* doDelete perform Delete operation while inserting\n");
	    sb.append("* doBatch operate insert with batch inserting values(1,2,3),(5,2,6) ... fix number of batch=50 \n");
	    sb.append("* pctInsert %of the total threads to dedicate to Insert \n");
	    sb.append("* pctSelect %of the total threads to dedicate to pctSelect \n");
	    sb.append("* pctDelete %of the total threads to dedicate to Delete \n");
	    sb.append("* insertDefaultClass/selectDefaultClass/deleteDefaultClass you can specify the custome class by action need to implement \n RunnableQuery(Insert|Select|Delete)Interface \n");
	    sb.append("* ignorebinlog instruct the tool to insert the command to IGNORE the flush to binary log,\n this will works only if you a user with SUPER privileages \n");
	    sb.append("* e.g. jdbc:mysql://127.0.0.1:3306/test?user=test&password=test&autoReconnect=true --createtable=true --truncate=false");
	    sb.append(" --droptable=true --poolNumber=1 --repeatNumber=1 --sleepFor=0 --doDelete=false --doBatch=false ");
	    sb.append(" --pctInsert=100 --pctSelect=100 --pctDelete=10 --doReport=true|false (default=true) --ignorebinlog=true (default = false)\n");
	    //sb.append("* e.g. jdbc:oracle:thin:@hostname:1526:orcl\n");

	    sb.append("*/\n");
	    System.out.println(sb.toString());


	    System.exit(0);
	    }



	private void printReport(StatCollector stats){
	    stats.PrintFinalReport();
	    
	}
	
	private boolean prepareLauncer(){
	    launcher.prepareLauncher();
//	    launcher.
	    
	    
	    return false;
	}





	/**
	 * @return the stressToolRunning
	 */
	public static boolean isStressToolRunning() {
	    return StressToolRunning;
	}





	/**
	 * @param stressToolRunning the stressToolRunning to set
	 */
	public static void setStressToolRunning(boolean stressToolRunning) {
	    StressToolRunning = stressToolRunning;
	}





	/**
	 * @return the valueProvider
	 */
	public static ValueProvider getValueProvider() {
	    return valueProvider;
	}





	/**
	 * @param valueProvider the valueProvider to set
	 */
	public static void setValueProvider(ValueProvider valueProviderIn) {
	    valueProvider = valueProviderIn;
	}





	public static SynchronizedMap getThreadsInfo() {
		return threadsInfo;
	}





//	public static void setThreadsInfo(SynchronizedMap threadsInfo) {
//		threadsInfo = threadsInfo;
//	}





}
