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

	
	
	/* arrays to handle all the wait status currently recognized
*/

	
	static private String[] LWLOCK= new String[] {"LWLock|AddinShmemInitLock","LWLock|async","LWLock|AsyncCtlLock","LWLock|AsyncQueueLock","LWLock|AutoFileLock","LWLock|AutovacuumLock","LWLock|AutovacuumScheduleLock","LWLock|BackendRandomLock","LWLock|BackgroundWorkerLock","LWLock|BtreeVacuumLock","LWLock|buffer_content","LWLock|buffer_io","LWLock|buffer_mapping","LWLock|CheckpointerCommLock","LWLock|CheckpointLock","LWLock|clog","LWLock|CLogControlLock","LWLock|CLogTruncationLock","LWLock|commit_timestamp","LWLock|CommitTsControlLock","LWLock|CommitTsLock","LWLock|ControlFileLock","LWLock|DynamicSharedMemoryControlLock","LWLock|lock_manager","LWLock|LogicalRepWorkerLock","LWLock|multixact_member","LWLock|multixact_offset","LWLock|MultiXactGenLock","LWLock|MultiXactMemberControlLock","LWLock|MultiXactOffsetControlLock","LWLock|MultiXactTruncationLock","LWLock|OidGenLock","LWLock|oldserxid","LWLock|OldSerXidLock","LWLock|OldSnapshotTimeMapLock","LWLock|parallel_query_dsa","LWLock|predicate_lock_manager","LWLock|proc","LWLock|ProcArrayLock","LWLock|RelationMappingLock","LWLock|RelCacheInitLock","LWLock|replication_origin","LWLock|replication_slot_io","LWLock|ReplicationOriginLock","LWLock|ReplicationSlotAllocationLock","LWLock|ReplicationSlotControlLock","LWLock|SerializableFinishedListLock","LWLock|SerializablePredicateLockListLock","LWLock|SerializableXactHashLock","LWLock|ShmemIndexLock","LWLock|SInvalReadLock","LWLock|SInvalWriteLock","LWLock|subtrans","LWLock|SubtransControlLock","LWLock|SyncRepLock","LWLock|SyncScanLock","LWLock|TablespaceCreateLock","LWLock|tbm","LWLock|TwoPhaseStateLock","LWLock|wal_insert","LWLock|WALBufMappingLock","LWLock|WALWriteLock","LWLock|XidGenLock"};
	static private String[] LOCK= new String[] {"Lock|advisory","Lock|extend","Lock|object","Lock|page","Lock|relation","Lock|speculative token","Lock|transactionid","Lock|tuple","Lock|userlock","Lock|virtualxid"};

	static private String[] BUFFERPIN= new String[] {"BufferPin|BufferPin"};

	static private String[] ACTIVITY= new String[] {"Activity|ArchiverMain","Activity|AutoVacuumMain","Activity|BgWriterHibernate","Activity|BgWriterMain","Activity|CheckpointerMain","Activity|LogicalApplyMain","Activity|LogicalLauncherMain","Activity|PgStatMain","Activity|RecoveryWalAll","Activity|RecoveryWalStream","Activity|SysLoggerMain","Activity|WalReceiverMain","Activity|WalSenderMain","Activity|WalWriterMain"};


	static private String[] CLIENT= new String[] {"Client|ClientRead","Client|ClientWrite","Client|LibPQWalReceiverConnect","Client|LibPQWalReceiverReceive","Client|SSLOpenServer","Client|WalReceiverWaitStart","Client|WalSenderWaitForWAL","Client|WalSenderWriteData"};

	static private String[] EXTENSION= new String[] {"Extension|Extension"};

	static private String[] IPC= new String[] {"IPC|BgWorkerShutdown","IPC|BgWorkerStartup","IPC|BtreePage","IPC|ExecuteGather","IPC|LogicalSyncData","IPC|LogicalSyncStateChange","IPC|MessageQueueInternal","IPC|MessageQueuePutMessage","IPC|MessageQueueReceive","IPC|MessageQueueSend","IPC|ParallelBitmapScan","IPC|ParallelFinish","IPC|ProcArrayGroupUpdate","IPC|ReplicationOriginDrop","IPC|ReplicationSlotDrop","IPC|SafeSnapshot","IPC|SyncRep"};

	static private String[] TIMEOUT= new String[] {"Timeout|BaseBackupThrottle","Timeout|PgSleep","Timeout|RecoveryApplyDelay"};

	static private String[] IO= new String[] {"IO|BufFileRead","IO|BufFileWrite","IO|ControlFileRead","IO|ControlFileSync","IO|ControlFileSyncUpdate","IO|ControlFileWrite","IO|ControlFileWriteUpdate","IO|CopyFileRead","IO|CopyFileWrite","IO|DataFileExtend","IO|DataFileFlush","IO|DataFileImmediateSync","IO|DataFilePrefetch","IO|DataFileRead","IO|DataFileSync","IO|DataFileTruncate","IO|DataFileWrite","IO|DSMFillZeroWrite","IO|LockFileAddToDataDirRead","IO|LockFileAddToDataDirSync","IO|LockFileAddToDataDirWrite","IO|LockFileCreateRead","IO|LockFileCreateSync","IO|LockFileCreateWrite","IO|LockFileReCheckDataDirRead","IO|LogicalRewriteCheckpointSync","IO|LogicalRewriteMappingSync","IO|LogicalRewriteMappingWrite","IO|LogicalRewriteSync","IO|LogicalRewriteWrite","IO|RelationMapRead","IO|RelationMapSync","IO|RelationMapWrite","IO|ReorderBufferRead","IO|ReorderBufferWrite","IO|ReorderLogicalMappingRead","IO|ReplicationSlotRead","IO|ReplicationSlotRestoreSync","IO|ReplicationSlotSync","IO|ReplicationSlotWrite","IO|SLRUFlushSync","IO|SLRURead","IO|SLRUSync","IO|SLRUWrite","IO|SnapbuildRead","IO|SnapbuildSync","IO|SnapbuildWrite","IO|TimelineHistoryFileSync","IO|TimelineHistoryFileWrite","IO|TimelineHistoryRead","IO|TimelineHistorySync","IO|TimelineHistoryWrite","IO|TwophaseFileRead","IO|TwophaseFileSync","IO|TwophaseFileWrite","IO|WALBootstrapSync","IO|WALBootstrapWrite","IO|WALCopyRead","IO|WALCopySync","IO|WALCopyWrite","IO|WALInitSync","IO|WALInitWrite","IO|WALRead","IO|WALSenderTimelineHistoryRead","IO|WALSyncMethodAssign","IO|WALWrite"};

	private ArrayList allWAITS = new ArrayList();
	private Map fakeStatus = new SynchronizedMap(0);
	
	

	//	 Sum of wait events by main event
	 String waitEventsSummary="SELECT concat(wait_event_type,'_', wait_event) wait_event,count (wait_event)  FROM pg_stat_activity WHERE wait_event is NOT NULL  group by wait_event_type,wait_event order by wait_event_type,wait_event asc"; 
	 String dbGlobalInfo="";
	 
	 
	 String[] statsToProcess = {waitEventsSummary};
	 
	 private Map eventsNamesInternal = new SynchronizedMap(0);
	 
	 
	public PostgresStatsWaits() {
	 super();
	 this.setStatGroupName("PGSTATS_waits");
	 allWAITS.addAll(Arrays.asList(LWLOCK));
	 allWAITS.addAll(Arrays.asList(LOCK));
	 allWAITS.addAll(Arrays.asList(BUFFERPIN));
	 allWAITS.addAll(Arrays.asList(ACTIVITY));
	 allWAITS.addAll(Arrays.asList(CLIENT));
	 allWAITS.addAll(Arrays.asList(EXTENSION));
	 allWAITS.addAll(Arrays.asList(IPC));
	 allWAITS.addAll(Arrays.asList(TIMEOUT));
	 allWAITS.addAll(Arrays.asList(IO));
	 fillFakeStatus(allWAITS);
	 
    }

	private void fillFakeStatus(ArrayList allWAITSIn) {
		
		Iterator it = allWAITSIn.iterator();
		int internalOrder = 0;
		long time = System.currentTimeMillis();
		
		while(it.hasNext()) {
		
	  	  StatEvent event = new StatEvent();
	      
	      String name = "";
	      String value = "";
	      name = (String) it.next();
	      name = name.replace("|", "_");
//	    		  name.substring(name.indexOf("|")+1);
	      value = "0";
	
	      
	      event.setCollection(this.statGroupName);
	      event.setTime(time);
	      event.setProvider(this.getClass().getCanonicalName());
	      event.setEvent(name);
	      event.setValue(value);
	      event.setId(loopNumber);
	      event.setOrder(internalOrder);
	      
//	      System.out.println(name + " : " + value);
	      
	
	      fakeStatus.put(name,event);
	      internalOrder++;
		}
		
		
	}

	@Override
    public Map collectStatistics(Connection conn) {
        loopNumber++;
        status = new SynchronizedMap(0);
        lastSampleTime = Utility.getHour()+":"+Utility.getMinute()+":"+Utility.getSecond();
        status = getStatus(conn);
        
    
        if(status != null && status.size() >0 && eventsName == null) {
            eventsName = ((SynchronizedMap)status).getKeyasOrderedStringArray();
        }
        else if(status != null && status.size() >0  && eventsName !=null) {
            this.populateEventsNames(status);
        }

        	
        
        if(flushrowonfile && status !=null){
            writeStatsOnFile(status);
        }
            
        return status;
    }	
	
	
	
    private void populateEventsNames(Map status) {
    	String[] eventNameTemp = ((SynchronizedMap)status).getKeyasOrderedStringArray();
    	
    	for(String s : eventNameTemp) {
    		if(!eventsNamesInternal.containsKey(s)) {
//    			eventNameTemp.add(eventsName[i]);
    			eventsNamesInternal.put(s, null);
    		}
    	}
    	eventsName = ((SynchronizedMap)eventsNamesInternal).getKeyasOrderedStringArray();
		
	}

	@Override
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
          
          statusReport.putAll(fakeStatus);
          Iterator ifake = statusReport.keySet().iterator();
          while(ifake.hasNext()) {
        	  StatEvent event = new StatEvent();
        	  String key = (String) ifake.next();
        	  ((StatEvent)statusReport.get(key)).setTime(time);
          }
          
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
//			              event.setOrder(internalOrder);
			              

			              
			              if(statusReport.containsKey(name)) {
			            	  statusReport.put(name,event);
//				              System.out.println(name + " : " + value);			            	  
//			              internalOrder++;
			              }
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
            	long value = ((Long)this.getSumResultByName(current,false)).longValue();
            	if(value > 0) {
            		pw.println(current + " = " + value + " xsec = " + (value/(totalExecutionTime/1000)));
            	}
            	
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
