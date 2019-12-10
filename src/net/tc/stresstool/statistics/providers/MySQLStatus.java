package net.tc.stresstool.statistics.providers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;

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

public class MySQLStatus extends BaseStatCollector implements StatsProvider, Reporter {
    public MySQLStatus() {
	 super();
	 this.setStatGroupName("STATUS");
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
          stmt = conn.createStatement();

          long time = System.currentTimeMillis();
          try{
              rs = stmt.executeQuery("SHOW GLOBAL STATUS");
              /* TIME must be present in any collected stats
               * Filling the base Event entity
               */
	          while(rs.next())
	          {
	              StatEvent event = new StatEvent();
	              
	              String name = "";
	              String value = "";
	              name = rs.getString("Variable_name");
	              value = rs.getString("Value");
	              if(Utility.isNumeric(value)) {
		              event.setCollection(this.statGroupName);
		              event.setTime(time);
		              event.setProvider(this.getClass().getCanonicalName());
		              event.setEvent(name);
		              event.setValue(value);
		              event.setId(loopNumber);
		              event.setOrder(internalOrder);
		              
		              statusReport.put(name,event);
		              internalOrder++;
	              }

	          }
          }
          catch(Exception sqlx){
        	 // sqlx.printStackTrace();
              try{
            	  rs.close();
            	  rs = null;
            	  stmt.close();
            	  stmt = null;
//              return statusReport;
            	  return null;
              }catch(Throwable th ){return null;}
        }
         
 /*         
          rs = stmt.executeQuery("SELECT * from ndbinfo.counters");
          while(rs.next())
          {
              StatEvent event = new StatEvent();
              String name = "";
              String value = "";
              String id = "" ;
              String block="";
              String instance="";
              name = rs.getString("counter_name");
              value = rs.getString("val");
              id = rs.getString("node_id"); 
              block=rs.getString("block_name");
              instance=rs.getString("block_instance");
              name = name + "_" + block + "_" + id + "_" + instance;
              event.setCollection(this.statGroupName);
              event.setTime(time);
              event.setProvider(this.getClass().getCanonicalName());
              event.setEvent(name);
              event.setValue(value);
              event.setId(loopNumber);
              event.setOrder(internalOrder);
              
              statusReport.put(name,event);
              internalOrder++;
          }
   */       
          	
      }
      catch (Exception eex)
      {
          try {
        	  	throw new StressToolGenericException(eex);
          	  } catch (StressToolGenericException e) {
          		  	e.printStackTrace();
          	  	}
      }
//      finally
//      {
//          try {
//              rs.close();
//              rs = null;
//              stmt.close();
//              stmt = null;
//              return statusReport;
//              
//          } catch (SQLException ex) {
//  			ex.printStackTrace();
//          }
//
//      }
      try {
    	  rs.close();
	      rs = null;
	      stmt.close();
	      stmt = null;
	      return statusReport;
      } catch (SQLException e) {
			e.printStackTrace();
      }

      return null;
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
    	
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
       try{
    	   
            pw.println("------------------------------ "+this.getProviderName()+" DATABASE INFORMATION -----------------------------------------");
            pw.println("Provider  = " + getProviderName());
            pw.println("Start time  = " + Utility.getTimeStamp(reporterGroup.getStartTime(),"yy/MM/dd hh:mm:ss:SSSS a"));
            pw.println("End time  = " + Utility.getTimeStamp(reporterGroup.getLastSampleTime(),"yy/MM/dd hh:mm:ss:SSSS a"));
            
            pw.println("Total Kbyte OUT  = " + getResultByName("Bytes_sent",false).getSumValue() + " xsec = " + ((this.getResultByName("Bytes_sent",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Total Kbyte IN  = " + this.getResultByName("Bytes_received",false).getSumValue() + " xsec = " + ((this.getResultByName("Bytes_received",false).getSumValue())/(totalExecutionTime/1000)));
            
            pw.println("Total Execution time = " + totalExecutionTime/1000);
            pw.println("Inserts = " + this.getResultByName("Com_insert",false).getSumValue() + " xsec = " + ((this.getResultByName("Com_insert",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Replace = " + this.getResultByName("Com_replace",false).getSumValue() + " xsec = " + ((this.getResultByName("Com_replace",false).getSumValue())/(totalExecutionTime/1000)));        
            pw.println("Updates = " + this.getResultByName("Com_update",false).getSumValue() + " xsec = " + ((this.getResultByName("Com_update",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Deletes = " + this.getResultByName("Com_delete",false).getSumValue() + " xsec = " + ((this.getResultByName("Com_delete",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Selects = " + this.getResultByName("Com_select",false).getSumValue() + " xsec = " + ((this.getResultByName("Com_select",false).getSumValue())/(totalExecutionTime/1000)));
    
            pw.println("Begins = " + this.getResultByName("Com_begin",false).getSumValue() + " xsec = " + ((this.getResultByName("Com_begin",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Commits = " + this.getResultByName("Com_commit",false).getSumValue() + " xsec = " + ((this.getResultByName("Com_commit",false).getSumValue())/(totalExecutionTime/1000)));        
            
            
            pw.println("Number of Connections = " + this.getResultByName("Connections",false).getSumValue() + " xsec = " + ((this.getResultByName("Connections",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Number of Temporary table on disk = " + this.getResultByName("Created_tmp_disk_tables",false).getSumValue());
            pw.println("Number of Questions = " + this.getResultByName("Questions",false).getSumValue() + " xsec = " + ((this.getResultByName("Questions",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Number of Max_used_connections = " + this.getResultByName("Max_used_connections",false).getSumValue() + " xsec = " + ((this.getResultByName("Max_used_connections",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Number of Opened_tables = " + this.getResultByName("Opened_tables",false).getSumValue() + " xsec = " + ((this.getResultByName("Opened_tables",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("------------------------------------ DELAYS ------------------------------------------------");
            pw.println("Delayed insert threads   = " + this.getResultByName("Delayed_insert_threads",false).getSumValue() + " xsec = " + ((this.getResultByName("Delayed_insert_threads",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Delayed Errors  = " + this.getResultByName("Delayed_errors",false).getSumValue() + " xsec = " + ((this.getResultByName("Delayed_errors",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Delayed writes  = " + this.getResultByName("Delayed_writes",false).getSumValue() + " xsec = " + ((this.getResultByName("Delayed_writes",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Not flushed delayed rows = " + this.getResultByName("Not_flushed_delayed_rows",false).getSumValue() + " xsec = " + ((this.getResultByName("Not_flushed_delayed_rows",false).getSumValue())/(totalExecutionTime/1000)));
            
            pw.println("------------------------------------ LOCKS ------------------------------------------------");
            pw.println("Number of Table_locks_immediate = " + this.getResultByName("Table_locks_immediate",false).getSumValue() + " xsec = " + ((this.getResultByName("Table_locks_immediate",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Number of Table_locks_waited = " + this.getResultByName("Table_locks_waited",false).getSumValue() + " xsec = " + ((this.getResultByName("Table_locks_waited",false).getSumValue())/(totalExecutionTime/1000)));
            
    //        if(this.engineName.toLowerCase().equals("innodb")){
    		        pw.println("-----------------------------INNODB BUFFER POOL ACTIONS -----------------------------------");
    		        pw.println("Number of Innodb_buffer_pool_pages_flushed = " + this.getResultByName("Innodb_buffer_pool_pages_flushed",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_buffer_pool_pages_flushed",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_buffer_pool_read_ahead_rnd = " + this.getResultByName("Innodb_buffer_pool_read_ahead_rnd",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_buffer_pool_read_ahead_rnd",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_buffer_pool_read_ahead_evicted = " + this.getResultByName("Innodb_buffer_pool_read_ahead_evicted",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_buffer_pool_read_ahead_evicted",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_buffer_pool_read_requests = " + this.getResultByName("Innodb_buffer_pool_read_requests",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_buffer_pool_read_requests",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_buffer_pool_reads = " + this.getResultByName("Innodb_buffer_pool_reads",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_buffer_pool_reads",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_data_fsyncs = " + this.getResultByName("Innodb_data_fsyncs",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_data_fsyncs",false).getSumValue())/(totalExecutionTime/1000)));
    		        
    		        pw.println("-----------------------------INNODB BUFFER POOL Dirty Page -----------------------------------");
    		        pw.println("Number of Innodb_buffer_pool_pages_dirty = " + this.getResultByName("Innodb_buffer_pool_pages_dirty",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_buffer_pool_pages_dirty",false).getSumValue())/(totalExecutionTime/1000)));
    		
    		//        if(this.getResultByName("Innodb_buffer_pool_pages_dirty",false).getSumValue() == 0){
    		//        	pw.println("Number of Innodb_buffer_pool_pages_dirty % = 0");
    		//        }
    		//        else{
    		//        	pw.println("Number of Innodb_buffer_pool_pages_dirty % = " + ((this.getResultByName("Innodb_buffer_pool_pages_dirty",false).getSumValue()/(this.getResultByName("Innodb_buffer_pool_pages_total",false).getSumValue())))*100);
    		//        }
    		
    		        pw.println("--------------------------------- INNODB DATA READS ----------------------------------------");
    		        pw.println("Number of Innodb Reads from BPool (not from disk) = " + ((this.getResultByName("Innodb_buffer_pool_read_requests",false).getSumValue()) - this.getResultByName("Innodb_buffer_pool_reads",false).getSumValue()) + " xsec = " 
    		        		+ ((((this.getResultByName("Innodb_buffer_pool_read_requests",false).getSumValue()) - this.getResultByName("Innodb_buffer_pool_reads",false).getSumValue())/(totalExecutionTime/1000))));		        
    		        
    		        pw.println("Number of Innodb_data_reads = " + this.getResultByName("Innodb_data_reads",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_data_reads",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_rows_read = " + this.getResultByName("Innodb_rows_read",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_rows_read",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_pages_read = " + this.getResultByName("Innodb_pages_read",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_pages_read",false).getSumValue())/(totalExecutionTime/1000)));
    		
    		        pw.println("----------------------------------INNODB DATA WRITES ---------------------------------------");
    		        pw.println("Number of Innodb_data_writes = " + this.getResultByName("Innodb_data_writes",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_data_writes",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_rows_inserted = " + this.getResultByName("Innodb_rows_inserted",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_rows_inserted",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_rows_updated = " + this.getResultByName("Innodb_rows_updated",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_rows_updated",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_rows_deleted = " + this.getResultByName("Innodb_rows_deleted",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_rows_deleted",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_pages_written = " + this.getResultByName("Innodb_pages_written",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_pages_written",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_pages_created = " + this.getResultByName("Innodb_pages_created",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_pages_created",false).getSumValue())/(totalExecutionTime/1000)));
    		
    		        pw.println("-----------------------------------INNODB DBLBUFFER WRITES ---------------------------------");
    		        pw.println("Number of Innodb_dblwr_writes = " + this.getResultByName("Innodb_dblwr_writes",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_dblwr_writes",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_dblwr_pages_written = " + this.getResultByName("Innodb_dblwr_pages_written",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_dblwr_pages_written",false).getSumValue())/(totalExecutionTime/1000)));
    		        
    		        pw.println("-----------------------------------INNODB LOG Activities ---------------------------------");
    		        
    		        pw.println("Number of Innodb_log_waits = " + this.getResultByName("Innodb_log_waits",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_log_waits",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_log_write_requests = " + this.getResultByName("Innodb_log_write_requests",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_log_write_requests",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_log_writes = " + this.getResultByName("Innodb_log_writes",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_log_writes",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_os_log_fsyncs = " + this.getResultByName("Innodb_os_log_fsyncs",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_os_log_fsyncs",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_os_log_pending_fsyncs = " + this.getResultByName("Innodb_os_log_pending_fsyncs",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_os_log_pending_fsyncs",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_os_log_pending_writes = " + this.getResultByName("Innodb_os_log_pending_writes",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_os_log_pending_writes",false).getSumValue())/(totalExecutionTime/1000)));
    		        pw.println("Number of Innodb_os_log_written = " + this.getResultByName("Innodb_os_log_written",false).getSumValue() + " xsec = " + ((this.getResultByName("Innodb_os_log_written",false).getSumValue())/(totalExecutionTime/1000)));
    //        }
         
    //        Including handlers
            pw.println("----------------------------------- Handlers  ---------------------------------------");         
            
            String[] handlers = new String[]{"Handler_commit", "Handler_delete","Handler_discover","Handler_prepare","Handler_read_first",
                                              "Handler_read_key","Handler_read_last","Handler_read_next","Handler_read_prev","Handler_read_rnd",
                                              "Handler_read_rnd_next","Handler_rollback","Handler_savepoint","Handler_savepoint_rollback",
                                              "Handler_update","Handler_write"};
            for(int ihandler = 0; ihandler < handlers.length; ihandler++){
            pw.println("Number of " + handlers[ihandler] + " = " + this.getResultByName(handlers[ihandler],false).getSumValue() + " xsec = " + ((this.getResultByName(handlers[ihandler],false).getSumValue())/(totalExecutionTime/1000)));
            	
            	
            }
            
    //      Including cluster
    //      pw.println("----------------------------------- ndbinfo  ---------------------------------------");         
    //      
    //      Object[] ndbinfo = (Object[])this.ndbinfoNames.toArray();
    //      for(int indbinfo = 0; indbinfo < ndbinfo.length; indbinfo++){
    //      pw.println("Number of " + ndbinfo[indbinfo] + " = " + this.getResultByName((String)ndbinfo[indbinfo],false) + " xsec = " + ((this.getResultByName((String)ndbinfo[indbinfo],false))/(totalExecutionTime/1000)));
    //      	
    //      	
    //      }
    
            
            pw.println("-----------------------------------MyIsam Key Buffer ---------------------------------------");
            pw.println("Number of Key_reads = " + this.getResultByName("Key_reads",false).getSumValue() + " xsec = " + ((this.getResultByName("Key_reads",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Number of Key_writes = " + this.getResultByName("Key_writes",false).getSumValue() + " xsec = " + ((this.getResultByName("Key_writes",false).getSumValue())/(totalExecutionTime/1000)));
            
            pw.println("Number of Key_read_requests = " + this.getResultByName("Key_read_requests",false).getSumValue() + " xsec = " + ((this.getResultByName("Key_read_requests",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Number of Key_write_requests = " + this.getResultByName("Key_write_requests",false).getSumValue() + " xsec = " + ((this.getResultByName("Key_write_requests",false).getSumValue())/(totalExecutionTime/1000)));
    
            pw.println("Number of Key_blocks_not_flushed = " + this.getResultByName("Key_blocks_not_flushed",false).getSumValue() + " xsec = " + ((this.getResultByName("Key_blocks_not_flushed",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Number of Key_blocks_unused = " + this.getResultByName("Key_blocks_unused",false).getSumValue() + " xsec = " + ((this.getResultByName("Key_blocks_unused",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Number of Key_blocks_used = " + this.getResultByName("Key_blocks_used",false).getSumValue() + " xsec = " + ((this.getResultByName("Key_blocks_used",false).getSumValue())/(totalExecutionTime/1000)));
    
            
            pw.println("----------------------------------- QUERY CACHE --------------------------------------------");
            pw.println("Number of Qcache_hits = " + this.getResultByName("Qcache_hits",false).getSumValue() + " xsec = " + ((this.getResultByName("Qcache_hits",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Number of Qcache_inserts = " + this.getResultByName("Qcache_inserts",false).getSumValue() + " xsec = " + ((this.getResultByName("Qcache_inserts",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Number of Qcache_not_cached = " + this.getResultByName("Qcache_not_cached",false).getSumValue() + " xsec = " + ((this.getResultByName("Qcache_not_cached",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Number of Qcache_lowmem_prunes = " + this.getResultByName("Qcache_lowmem_prunes",false).getSumValue() + " xsec = " + ((this.getResultByName("Qcache_lowmem_prunes",false).getSumValue())/(totalExecutionTime/1000)));
    
            pw.println("Number of Qcache_free_memory = " + this.getResultByName("Qcache_free_memory",false).getSumValue() + " xsec = " + ((this.getResultByName("Qcache_free_memory",false).getSumValue())/(totalExecutionTime/1000)));        
            pw.println("Number of Qcache_queries_in_cache = " + this.getResultByName("Qcache_queries_in_cache",false).getSumValue() + " xsec = " + ((this.getResultByName("Qcache_queries_in_cache",false).getSumValue())/(totalExecutionTime/1000)));
            pw.println("Number of Qcache_total_blocks = " + this.getResultByName("Qcache_total_blocks",false).getSumValue() + " xsec = " + ((this.getResultByName("Qcache_total_blocks",false).getSumValue())/(totalExecutionTime/1000)));
            
            pw.println("------------------------------------ THREADS    --------------------------------------------");
            pw.println("Number of Threads_cached = " + this.getResultByName("Threads_cached",false).getSumValue() );
            pw.println("Number of Threads_connected = " + this.getResultByName("Threads_connected",false).getSumValue() );
            pw.println("Number of Threads_created = " + this.getResultByName("Threads_created",false).getSumValue() );
            pw.println("Number of Threads_running = " + this.getMaxResultByName("Threads_running",false) );
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
