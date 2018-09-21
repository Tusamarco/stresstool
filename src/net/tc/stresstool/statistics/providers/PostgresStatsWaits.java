package net.tc.stresstool.statistics.providers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import org.postgresql.jdbc.PgResultSetMetaData;

import net.tc.data.db.ConnectionProvider;
import net.tc.stresstool.PerformanceEvaluator;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.actions.StressAction;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolGenericException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.ActionTHElement;
import net.tc.stresstool.statistics.StatEvent;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;
import net.tc.utils.file.FileDataWriter;

public class PostgresStatsWaits extends BaseStatCollector implements StatsProvider, Reporter {
//	 WAL set and flushes
	 String walAndFlush="SELECT * FROM   pg_stat_archiver";

//	 Check pointing info
	 String checkPointInfo="SELECT * FROM   pg_stat_bgwriter"; 
	  
//	 Global info by Database
	 String dbGlobalInfo="select * from pg_stat_database where datname='<db>'";

//	 CONFLICT INFO 
	 String dbConflictInfo="select * from pg_stat_database_conflicts where datname='<db>'"; 
	  
//	 Sum of wait events by main event
	 String waitEventsSummary="SELECT concat(wait_event_type,'_', wait_event) wait_event,count (wait_event)  FROM pg_stat_activity WHERE wait_event is NOT NULL  group by wait_event_type,wait_event order by wait_event_type,wait_event asc"; 

	 
	 
	 String[] statsToProcess = {waitEventsSummary};
	 
	 
	 
	 
	public PostgresStatsWaits() {
	 super();
	 this.setStatGroupName("PGSTATS_waits");
    }

//    @Override
    Map getStatus(Connection conn)  {
		int internalOrder =0;
	    Map statusReport = new SynchronizedMap(0);
//      Map statusReport = new HashMap(0);
      
      Statement stmt = null;
      ResultSet rs = null;
      if(conn == null)
          return null;
      try{
    	  String dbName = null;
    	  dbName= conn.getMetaData().getURL().substring(0,conn.getMetaData().getURL().indexOf('?')).substring(conn.getMetaData().getURL().lastIndexOf('/')+1);
    	  
    	  stmt = conn.createStatement();
//    	  stmt.execute("BEGIN");

          long time = System.currentTimeMillis();
          try{
        	  stmt.execute("/* this commit is only because otherwise tPG will not refresh stats */ commit");

        	  for(int sqlint = 0 ;sqlint < statsToProcess.length; sqlint++) {
        		  dbGlobalInfo = statsToProcess[sqlint];
	        	  dbGlobalInfo=dbGlobalInfo.replaceAll("<db>", dbName);
	        	  
	
	        	  
	        	  
	        	  rs = stmt.executeQuery(dbGlobalInfo);
	              /* TIME must be present in any collected stats
	               * Filling the base Event entity
	               */
	              org.postgresql.jdbc.PgResultSetMetaData rsm = (org.postgresql.jdbc.PgResultSetMetaData) rs.getMetaData();
		          while(rs.next())
		          {
		              
			        	  StatEvent event = new StatEvent();
			              
			              String name = "";
			              String value = "";
			              String nameLabel = rsm.getColumnLabel(1);
			              String valueLabel = rsm.getColumnLabel(2);

			              name = rs.getObject(nameLabel)!=null?(String) rs.getObject(nameLabel).toString():"0";
			              value = rs.getObject(valueLabel)!=null?(String) rs.getObject(valueLabel).toString():"0";

			              
			              event.setCollection(this.statGroupName);
			              event.setTime(time);
			              event.setProvider(this.getClass().getCanonicalName());
			              event.setEvent(name);
			              event.setValue(value);
			              event.setId(loopNumber);
			              event.setOrder(internalOrder);
			              
			              System.out.println(name + " : " + value);
			              
	
			              statusReport.put(name,event);
			              internalOrder++;
		          }
        	  }
	          
          }
          catch(Exception sqlx){
        	 // sqlx.printStackTrace();
              try{
            	  sqlx.printStackTrace();
            	  rs.close();
            	  rs = null;
            	  stmt.close();
            	  stmt = null;
            	  
//              return statusReport;
            	  return null;
              }catch(Throwable th ){return null;}
        }finally {
        	rs.close();
        	stmt.close();
        	rs=null;
        	stmt=null;
        }
         

          	
      }
      catch (Exception eex)
      {
          try {
        	  	throw new StressToolGenericException(eex);
//        	  	eex.printStackTrace();
          	  } catch (StressToolGenericException e) {
          		  	e.printStackTrace();
          	  	}
      }
	      return statusReport;
    }

    @Override
    public StringBuffer printReport(String StatsGroupName,
	    StringBuffer incomingBuffer) {
	// TODO Auto-generated method stub
	return printReport(incomingBuffer);
	
    }
    
    @Override
    protected StringBuffer printReport(StringBuffer sb) {
    	
    	long totalExecutionTime = 0;
    	if(reporterGroup == null) 
    	    return sb;
    	totalExecutionTime = (reporterGroup.getLastSampleTime() - reporterGroup.getStartTime()); 
    	
    	/*
            StressStatsCollectorReporter StatReporter = new StressStatsCollectorReporter(stressStatsCollector);
            StatReporter.setCommonInfo(common);
    */        
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
    //        pw.println();
    //        pw.println();
    //        pw.println("****============================================================================*******");
    //        pw.println("****                        FINAL REPORT    *** GENERAL ***                     *******");
    //        pw.println("****============================================================================*******");
    //        pw.println("-------------------------------- GENERAL TEST INFORMATION ------------------------------------------");
    
    //        pw.println("Runned on = " + TimeTools.GetFullDate(((Long)startReport.get("TIME")).longValue()));
    //        pw.println("------------------------------GENERAL DATABASE INFORMATION -----------------------------------------");
    //        pw.println("Total Number Of query Executed = " + this.getTotalQueryToRun());
    //        pw.println("Total Number Of query Executed for writes = " + this.getTotalQueryToRunWrites());
    //        pw.println("Total Number Of query Executed for reads  = " + this.getTotalQueryToRunReads());
    //        pw.println("Total Number Of query Executed for deletes  = " + this.getTotalQueryToRunDeletes());
       try{
 	   
//    	  Object[] threads = (Object[]) StressTool.getThreadsInfo().values().toArray();
//    	  String[] actions = new String[]{"Insert","Update","Delete","Select"};
//   	   	  pw.println("------------------------------  Threads EXECUTION INFORMATION -----------------------------------------");
//
//    	   for(String action:actions){
//    		   pw.println("------------------------------ " + action);
//    		   
//	    	   for(Object thInfo:threads){
//	    		   if(thInfo == null)
//	    			   continue;
//	    		  if(((ActionTHElement)thInfo).getAction().toUpperCase().equals(action.toUpperCase())){
//	    		   pw.println("ThreadID = " +  ((ActionTHElement)thInfo).getId() 
//	    				   + " Tot Execution time = " +((ActionTHElement)thInfo).getTotalEcecutionTime()
//	    				   + " Max Execution time = " + ((ActionTHElement)thInfo).getMaxExectime() 
//	    				   + " Min Execution time = " + ((ActionTHElement)thInfo).getMinExectime()
//	    				   );
//	    		  }
//	    	   }
//    	   }
//    	   
    	   
    	   
    	   
    	   
            pw.println("------------------------------ "+this.getProviderName()+" DATABASE INFORMATION -----------------------------------------");
            pw.println("Provider  = " + getProviderName());
            pw.println("Start time  = " + Utility.getTimeStamp(reporterGroup.getStartTime(),"yy/MM/dd hh:mm:ss:SSSS a"));
            pw.println("End time  = " + Utility.getTimeStamp(reporterGroup.getLastSampleTime(),"yy/MM/dd hh:mm:ss:SSSS a"));
            pw.println("Total Execution time = " + totalExecutionTime/1000);
            
            String[] headers = this.getHeadersArray(this.getProviderName());
            Arrays.sort(headers);
            for(int printi=0 ; printi < headers.length;printi++) {
            	String current = headers[printi];
            	pw.println(current + " = " + this.getResultByName(current,false) + " xsec = " + (((Long)this.getResultByName(current,false)).longValue()/(totalExecutionTime/1000)));
            	
            }
       }
       catch(Throwable th){
    	   th.printStackTrace();
       }
    
            if(flushrowonfile){
	            writeReportOnFile(sw.toString());
	    }
 
            return sb.append(sw.toString());
    
    	
    }

    @Override
    public boolean validatePermissions(ConnectionProvider connProvider) {
	Logger applicationLogger = null;
	try {
	    applicationLogger = StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION);
	} catch (StressToolConfigurationException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	if(connProvider == null)
	    return false;
	
    	/*Performance evaluation section [tail] start*/
	long performanceTimeStart = 0;
	long performanceTimeEnd = 0;
	try {
	    if (StressTool.getLogProvider()
		    .getLogger(LogProvider.LOG_PERFORMANCE)
		    .isDebugEnabled())
		performanceTimeStart = System.nanoTime();
	} catch (Throwable th) {
	}
	/*Performance evaluation section [tail] END*/
    
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean valid = false;
        String userName="";
        
        
        try {
            conn = connProvider.getSimpleConnection();
//TODO check if needed             conn.setAutoCommit(false);
            stmt = conn.createStatement();

            
            /* checking general privileges
             * 
             */
            try {
		StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION);
	    } catch (StressToolConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    
            applicationLogger.info("Checking Generic permissions on:" + connProvider.getConnInfo().getDatabase());
            //CREATE TABLESPACE,FILE, PROCESS,REPLICATION CLIENT,SHOW DATABASES
            rs = stmt.executeQuery("select Process_priv, Repl_client_priv,Show_db_priv,File_priv,Create_tablespace_priv, CURRENT_USER() from mysql.user where user=substring(CURRENT_USER(),1,locate('@',CURRENT_USER())-1) and host=substring(CURRENT_USER(),locate('@',CURRENT_USER())+1)");
            while(rs.next()){
            	userName = rs.getNString(6);
            	for(int irs = 1 ; irs <= 5; irs++ ){
	            	if(rs.getNString(irs).toLowerCase().equals("y") ){
	            		valid = true;
	            	}
	            	else{
	            	    	valid =  false;
	            	}
            	    
            	}
            }
            rs.close();
            if(valid){
        	applicationLogger.info("Permissions on global seems ok found : CREATE TABLESPACE,FILE, PROCESS,REPLICATION CLIENT,SHOW DATABASES");
            }
            else
            {
        	applicationLogger.error("Permissions on global FAILED expected : CREATE TABLESPACE,FILE, PROCESS,REPLICATION CLIENT,SHOW DATABASES");
            }
            applicationLogger.info("Checking DB permissions at global level");
            /* checking GLOBAL DB privileges
             * 
             */
            String sql = "select Select_priv, " +
            		"Insert_priv," +
            		"Update_priv," +
            		"Delete_priv," +
            		"Create_priv," +
            		"Drop_priv," +
            		"Index_priv," +
            		"Alter_priv," +
            		"Create_tmp_table_priv," +
            		"Lock_tables_priv," +
            		"Create_view_priv," +
            		"Show_view_priv," +
            		"Create_routine_priv," +
            		"Execute_priv," +
            		"Event_priv," +
            		"Trigger_priv " +
            		"from mysql.user " +
            		"where user=substring(CURRENT_USER(),1,locate('@',CURRENT_USER())-1) " +
            		"and host=substring(CURRENT_USER(),locate('@',CURRENT_USER())+1)";
            applicationLogger.debug("SQL for privileges General:" + sql);
            
            rs = stmt.executeQuery(sql);
            while(rs.next()){
        	int iAttribs = rs.getMetaData().getColumnCount();
            	for(int irs = 1 ; irs <= iAttribs; irs++ ){
	            	if(rs.getNString(irs).toLowerCase().equals("y") ){
	            		valid = true;
	            	}
	            	else{
	            	    	valid =  false;
	            	}
            	    
            	}
            }
            rs.close();
            
            if(!valid){
	            /* checking DB privileges
	             * 
	             */
        	applicationLogger.info("Permissions on global failed checking if I can write on the DB");
        	sql= "select Select_priv, " +
            		"Insert_priv," +
            		"Update_priv," +
            		"Delete_priv," +
            		"Create_priv," +
            		"Drop_priv," +
            		"Index_priv," +
            		"Alter_priv," +
            		"Create_tmp_table_priv," +
            		"Lock_tables_priv," +
            		"Create_view_priv," +
            		"Show_view_priv," +
            		"Create_routine_priv," +
            		"Execute_priv," +
            		"Event_priv," +
            		"Trigger_priv " +
            		"from mysql.db " +
            		"where user=substring(CURRENT_USER(),1,locate('@',CURRENT_USER())-1) " +
            		"and host=substring(CURRENT_USER(),locate('@',CURRENT_USER())+1)" +
            		" and db='" + this.getDefaultSchema() + "'";
        	
        	applicationLogger.debug("SQL for privileges General:" + sql);
        	rs = stmt.executeQuery(sql);
	            
	            while(rs.next()){
	        	int iAttribs = rs.getMetaData().getColumnCount();
	            	for(int irs = 1 ; irs <= iAttribs; irs++ ){
		            	if(rs.getNString(irs).toLowerCase().equals("y") ){
		            		valid = true;
		            	}
		            	else{
		            	    	valid =  false;
		            	}
	            	    
	            	}
	            }
	            rs.close();
            }
        
        
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally 
        {
        	try {
        		rs.close();
        		stmt.close();
        		if(!conn.getAutoCommit())
        			conn.commit();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        if(!valid){
            applicationLogger.info("Permissions on global AND DB failed I cannot write on the DB");
            applicationLogger.error("NOT sufficient  permissions on:" + connProvider.getConnInfo().getDatabase()+ " for user:" + userName );
            applicationLogger.error("The following are expected ensure the user has them:");
            applicationLogger.error("Select_priv,\n" +
	            		"Insert_priv,\n" +
	            		"Update_priv,\n" +
	            		"Delete_priv,\n" +
	            		"Create_priv,\n" +
	            		"Drop_priv,\n" +
	            		"Index_priv,\n" +
	            		"Alter_priv,v" +
	            		"Create_tmp_table_priv,\n" +
	            		"Lock_tables_priv,\n" +
	            		"Create_view_priv,\n" +
	            		"Show_view_priv,\n" +
	            		"Create_routine_priv,\n" +
	            		"Execute_priv\n," +
	            		"Event_priv,\n" +
	            		"Trigger_priv \n" );
            applicationLogger.error("Try to issue: Grant ALTER,ALTER ROUTINE,CREATE,CREATE ROUTINE," +
            		"CREATE TEMPORARY TABLES,CREATE VIEW,DELETE,DROP,EVENT," +
            "EXECUTE,INDEX,INSERT,LOCK TABLES,SELECT,TRIGGER,UPDATE on " + connProvider.getConnInfo().getDatabase()+ ".* to " + connProvider.getConnInfo().getUser() 
            + "@'<host>' identified by '<secret>'");
            
            applicationLogger.error("Try to issue: Grant CREATE TABLESPACE,FILE, PROCESS,REPLICATION CLIENT,SHOW DATABASES on *.* to " + connProvider.getConnInfo().getUser() 
	            + "@'<host>' identified by '<secret>'");
            
            ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
            try {
		throw new StressToolConfigurationException(ExceptionMessages.INVALID_PERMISSIONS);
	    } catch (StressToolConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
            return false;
        }
        
    /*Performance evaluation section [header] start*/
	try {
	    if (StressTool.getLogProvider()
		    .getLogger(LogProvider.LOG_PERFORMANCE)
		    .isDebugEnabled()) {
		performanceTimeEnd = System.nanoTime();
		StressTool
			.getLogProvider()
			.getLogger(LogProvider.LOG_PERFORMANCE)
			.debug(StressTool.getLogProvider().LOG_EXEC_TIME
				+ ":"
				+ PerformanceEvaluator
					.getTimeEvaluationNs(performanceTimeStart));
	    }
	} catch (Throwable th) {
	}
	/*Performance evaluation section [header] END*/

        applicationLogger.info("Permissions seems OK going ahead");
        return true;


    }    
}
