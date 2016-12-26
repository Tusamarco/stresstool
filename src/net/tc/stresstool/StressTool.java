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
	private  Launcher launcher;
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
	        
	        launcher = new Launcher(config);
	        /*
	         * test connection against the declared url
	         */
	        connectionInformation = launcher.validatePermission(getConfig().getConfiguration(Configurator.MAIN_SECTION_NAME, this.getClass()));
	            
	        
	        
//	        connectionInformation = testConnection(getConfig().getConfiguration(Configurator.MAIN_SECTION_NAME, this.getClass()));
	        
	        config.printConfiguration(applicationLogger);
	        
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
	    	int timeForLoop =launcher.getStatIntervalMs();
	    	int loops = launcher.getStatLoops()> launcher.getRepeatNumber()?launcher.getStatLoops():launcher.getRepeatNumber();
	    	if(timeForLoop < 1000){
	    		loops = loops * (1000/timeForLoop);
	    	}
	    	else{
	    		loops = loops / (timeForLoop/1000);
	    	}

	        
	        StressTool.setStressToolRunning(true);
	        
	        ConsoleStatePrinter consolePrinter =null;
	        
	        if(launcher.getInteractive() >0 )
	            consolePrinter = new ConsoleStatePrinter(launcher);

	        
	        while(StressTool.isStressToolRunning()){
	          int line =1;
	          float curPct = (float) 0.0;
	          float maxPct = loops;
	          int calendarReset = 10;
//	          int aInsert =  new Float( maxInsert).intValue();

	          for(int i = 0 ; i < loops; i++ ){
	             stats.collectStatistics();
	             if(launcher.getStatLoops() > loops 
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
	             
	             StressTool.setStressToolRunning(launcher.LaunchActions());
	             logProvider.getLogger(LogProvider.LOG_APPLICATION).debug("Running loop = " + i);

	             
	             if(launcher.getInteractive() >0  && consolePrinter != null){
	               consolePrinter.printLine(i);
	               //	             }
	               if(launcher.getInteractive() ==2 ){
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
//	        	  try {
//	        		   
//					Thread.sleep(launcher.getStatIntervalMs());
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
	          }
	          
	          ;
	           
	        }
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
	    sb.append("* tableEngine mysql engine (MyISAM; InnoDb; Memory; ndbcluster; mariaDB)\n");
	    sb.append("* sleepFor Thread sleep time (milliseconds default is 0 ms)\n");
	    sb.append("* dolog DON'T USE IT write the log on standard output\n");
	    sb.append("* dbType [Oracle|MySQL] default MySQL\n");
	    sb.append("* doDelete perform Delete operation while inserting\n");
	    sb.append("* doBatch operate insert with batch inserting values(1,2,3),(5,2,6) ... fix number of batch=50 \n");
	    sb.append("* pctInsert %of the total threads to dedicate to Insert \n");
	    sb.append("* pctSelect %of the total threads to dedicate to pctSelect \n");
	    sb.append("* pctDelete %of the total threads to dedicate to Delete \n");
	    sb.append("* insertDefaultClass/selectDefaultClass/deleteDefaultClass you can specify the custome class by action need to implement \n RunnableQuery(Insert|Select|Delete)Interface \n");
	    sb.append("* ignorebinlog instruct the tool to insert the command to IGNORE the flush to binary log,\n this will works only if you a user with SUPER privileages \n");
	    sb.append("* e.g. jdbc:mysql://127.0.0.1:3306/test?user=test&password=test&autoReconnect=true --createtable=true --truncate=false");
	    sb.append(" --droptable=true --poolNumber=1 --repeatNumber=1 --tableEngine=InnoDB --sleepFor=0 --dbType=MySQL --doDelete=false --doBatch=false ");
	    sb.append(" --pctInsert=100 --pctSelect=100 --pctDelete=10 --doReport=true|false (default=true) --doSimplePk=true|false (default=false) --ignorebinlog=true (default = false)\n");
	    //sb.append("* e.g. jdbc:oracle:thin:@hostname:1526:orcl\n");

	    sb.append("*/\n");
	    System.out.println(sb.toString());


	    System.exit(0);
	    }


//	private Map testConnection(Configuration configuration) throws StressToolException {
//	    	/*Performance evaluation section [tail] start*/
//		long performanceTimeStart = 0;
//		long performanceTimeEnd = 0;
//		try {
//		    if (StressTool.getLogProvider()
//			    .getLogger(LogProvider.LOG_PERFORMANCE)
//			    .isDebugEnabled())
//			performanceTimeStart = System.nanoTime();
//		} catch (Throwable th) {
//		}
//		/*Performance evaluation section [tail] END*/
//	    
//	        Connection conn = null;
//	        Statement stmt = null;
//	        ResultSet rs = null;
//	        Map connMapcoordinates = new SynchronizedMap(0) ;
//	        boolean valid = false;
//	        String userName="";
//	        
//	        connMapcoordinates.put("jdbcUrl", configuration.getParameter("connUrl").getValue());
//	        connMapcoordinates.put("database", configuration.getParameter("database").getValue());
//	        connMapcoordinates.put("user", configuration.getParameter("user").getValue());
//	        connMapcoordinates.put("password", configuration.getParameter("password").getValue());
//	        connMapcoordinates.put("dbtype", configuration.getParameter("dbtype").getValue());
//	        connMapcoordinates.put("connparameters", configuration.getParameter("connparameters").getValue());
//
//	    	
//
//	        
//	        
//	        try {
//	            conn = initConnection(connMapcoordinates);
//	            conn.setAutoCommit(false);
//	            stmt = conn.createStatement();
//
//	            
//	            /* checking general privileges
//	             * 
//	             */
//	            getApplicationLogger().info("Checking Generic permissions on:" + connMapcoordinates.get("database"));
//	            //CREATE TABLESPACE,FILE, PROCESS,REPLICATION CLIENT,SHOW DATABASES
//	            rs = stmt.executeQuery("select Process_priv, Repl_client_priv,Show_db_priv,File_priv,Create_tablespace_priv, CURRENT_USER() from mysql.user where user=substring(CURRENT_USER(),1,locate('@',CURRENT_USER())-1) and host=substring(CURRENT_USER(),locate('@',CURRENT_USER())+1)");
//	            while(rs.next()){
//	            	userName = rs.getNString(6);
//	            	for(int irs = 1 ; irs <= 5; irs++ ){
//		            	if(rs.getNString(irs).toLowerCase().equals("y") ){
//		            		valid = true;
//		            	}
//		            	else{
//		            	    	valid =  false;
//		            	}
//	            	    
//	            	}
//	            }
//	            rs.close();
//	            if(valid){
//	        	 getApplicationLogger().info("Permissions on global seems ok found : CREATE TABLESPACE,FILE, PROCESS,REPLICATION CLIENT,SHOW DATABASES");
//	            }
//	            else
//	            {
//	        	 getApplicationLogger().error("Permissions on global FAILED expected : CREATE TABLESPACE,FILE, PROCESS,REPLICATION CLIENT,SHOW DATABASES");
//	            }
//	            getApplicationLogger().info("Checking DB permissions at global level");
//	            /* checking GLOBAL DB privileges
//	             * 
//	             */
//	            String sql = "select Select_priv, " +
//	            		"Insert_priv," +
//	            		"Update_priv," +
//	            		"Delete_priv," +
//	            		"Create_priv," +
//	            		"Drop_priv," +
//	            		"Index_priv," +
//	            		"Alter_priv," +
//	            		"Create_tmp_table_priv," +
//	            		"Lock_tables_priv," +
//	            		"Create_view_priv," +
//	            		"Show_view_priv," +
//	            		"Create_routine_priv," +
//	            		"Execute_priv," +
//	            		"Event_priv," +
//	            		"Trigger_priv " +
//	            		"from mysql.user " +
//	            		"where user=substring(CURRENT_USER(),1,locate('@',CURRENT_USER())-1) " +
//	            		"and host=substring(CURRENT_USER(),locate('@',CURRENT_USER())+1)";
//	            getApplicationLogger().debug("SQL for privileges General:" + sql);
//	            
//	            rs = stmt.executeQuery(sql);
//	            while(rs.next()){
//	        	int iAttribs = rs.getMetaData().getColumnCount();
//	            	for(int irs = 1 ; irs <= iAttribs; irs++ ){
//		            	if(rs.getNString(irs).toLowerCase().equals("y") ){
//		            		valid = true;
//		            	}
//		            	else{
//		            	    	valid =  false;
//		            	}
//	            	    
//	            	}
//	            }
//	            rs.close();
//	            
//	            if(!valid){
//		            /* checking DB privileges
//		             * 
//		             */
//	        	getApplicationLogger().info("Permissions on global failed checking if I can write on the DB");
//	        	sql= "select Select_priv, " +
//	            		"Insert_priv," +
//	            		"Update_priv," +
//	            		"Delete_priv," +
//	            		"Create_priv," +
//	            		"Drop_priv," +
//	            		"Index_priv," +
//	            		"Alter_priv," +
//	            		"Create_tmp_table_priv," +
//	            		"Lock_tables_priv," +
//	            		"Create_view_priv," +
//	            		"Show_view_priv," +
//	            		"Create_routine_priv," +
//	            		"Execute_priv," +
//	            		"Event_priv," +
//	            		"Trigger_priv " +
//	            		"from mysql.db " +
//	            		"where user=substring(CURRENT_USER(),1,locate('@',CURRENT_USER())-1) " +
//	            		"and host=substring(CURRENT_USER(),locate('@',CURRENT_USER())+1)" +
//	            		" and db='test'";
//	        	
//	        	getApplicationLogger().debug("SQL for privileges General:" + sql);
//	        	rs = stmt.executeQuery(sql);
//		            
//		            while(rs.next()){
//		        	int iAttribs = rs.getMetaData().getColumnCount();
//		            	for(int irs = 1 ; irs <= iAttribs; irs++ ){
//			            	if(rs.getNString(irs).toLowerCase().equals("y") ){
//			            		valid = true;
//			            	}
//			            	else{
//			            	    	valid =  false;
//			            	}
//		            	    
//		            	}
//		            }
//		            rs.close();
//	            }
//	        
//	        
//	        } catch (SQLException ex) {
//	            ex.printStackTrace();
//	        }
//	        finally 
//	        {
//	        	try {
//	        		rs.close();
//	        		stmt.close();
//					conn.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//	        }
//	        
//	        if(!valid){
//	            getApplicationLogger().info("Permissions on global AND DB failed I cannot write on the DB");
//	            getApplicationLogger().error("NOT sufficient  permissions on:" + connMapcoordinates.get("database")+ " for user:" + userName );
//	            getApplicationLogger().error("The following are expected ensure the user has them:");
//	            getApplicationLogger().error("Select_priv,\n" +
//		            		"Insert_priv,\n" +
//		            		"Update_priv,\n" +
//		            		"Delete_priv,\n" +
//		            		"Create_priv,\n" +
//		            		"Drop_priv,\n" +
//		            		"Index_priv,\n" +
//		            		"Alter_priv,v" +
//		            		"Create_tmp_table_priv,\n" +
//		            		"Lock_tables_priv,\n" +
//		            		"Create_view_priv,\n" +
//		            		"Show_view_priv,\n" +
//		            		"Create_routine_priv,\n" +
//		            		"Execute_priv\n," +
//		            		"Event_priv,\n" +
//		            		"Trigger_priv \n" );
//	            getApplicationLogger().error("Try to issue: Grant ALTER,ALTER ROUTINE,CREATE,CREATE ROUTINE," +
//	            		"CREATE TEMPORARY TABLES,CREATE VIEW,DELETE,DROP,EVENT," +
//	            "EXECUTE,INDEX,INSERT,LOCK TABLES,SELECT,TRIGGER,UPDATE on " + connMapcoordinates.get("database")+ ".* to " + connMapcoordinates.get("user") 
//	            + "@'<host>' identified by '<secret>'");
//	            
//	            getApplicationLogger().error("Try to issue: Grant CREATE TABLESPACE,FILE, PROCESS,REPLICATION CLIENT,SHOW DATABASES on *.* to " + connMapcoordinates.get("user") 
//		            + "@'<host>' identified by '<secret>'");
//	            
//	            ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
//	            throw new StressToolConfigurationException(ExceptionMessages.INVALID_PERMISSIONS);
//	        }
//	        else
//	        {
//	            getApplicationLogger().info("Permissions seems OK going ahead");
//	        }
//	        
//	        /*Performance evaluation section [header] start*/
//		try {
//		    if (StressTool.getLogProvider()
//			    .getLogger(LogProvider.LOG_PERFORMANCE)
//			    .isDebugEnabled()) {
//			performanceTimeEnd = System.nanoTime();
//			StressTool
//				.getLogProvider()
//				.getLogger(LogProvider.LOG_PERFORMANCE)
//				.debug(StressTool.getLogProvider().LOG_EXEC_TIME
//					+ ":"
//					+ PerformanceEvaluator
//						.getTimeEvaluation(performanceTimeStart));
//		    }
//		} catch (Throwable th) {
//		}
//		/*Performance evaluation section [header] END*/
//
//	        return connMapcoordinates;
//	}




@Deprecated 
/**
 * this method is not supported use ConnectionProvider instead
 * @param connMapcoordinates
 * @return
 * @throws SQLException
 */
	public static Connection initConnection(Map connMapcoordinates)
		throws SQLException {
	    Connection conn;
	    if(connMapcoordinates.get("dbtype") != null &&  !((String)connMapcoordinates.get("dbtype")).toLowerCase().equals("MySQL".toLowerCase()))
	    {
	    conn=DriverManager.getConnection((String)connMapcoordinates.get("dbtype"),"test", "test");
	    }
	    else{
	    String connectionString = (String)connMapcoordinates.get("jdbcUrl")
	        	    +"/"+(String)connMapcoordinates.get("database")
	        	    +"?user="+(String)connMapcoordinates.get("user")
	        	    +"&password="+(String)connMapcoordinates.get("password")
	        	    +"&"+(String)connMapcoordinates.get("connparameters");
	    
	    
	    conn= DriverManager.getConnection(connectionString);
	    }
	    return conn;
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
}
