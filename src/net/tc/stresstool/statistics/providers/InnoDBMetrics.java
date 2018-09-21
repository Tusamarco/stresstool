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

import net.tc.stresstool.StressTool;
import net.tc.stresstool.actions.StressAction;
import net.tc.stresstool.exceptions.StressToolGenericException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.ActionTHElement;
import net.tc.stresstool.statistics.StatEvent;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;
import net.tc.utils.file.FileDataWriter;

public class InnoDBMetrics extends BaseStatCollector implements StatsProvider, Reporter {
    public InnoDBMetrics() {
	 super();
	 this.setStatGroupName("INNODB_METRICS");  
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
          rs = stmt.executeQuery("select name,COUNT,MAX_COUNT,MIN_COUNT,AVG_COUNT from INFORMATION_SCHEMA.INNODB_METRICS where name like 'index%';");
          /* TIME must be present in any collected stats
           * Filling the base Event entity
           */
          long time = System.currentTimeMillis();
          while(rs.next())
          {
              StatEvent event = new StatEvent();
              
              String name = "";
              String value = "";
              name = rs.getString("name");
              value = rs.getString("COUNT");
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
      finally
      {
          try {
              rs.close();
              rs = null;
              stmt.close();
              stmt = null;
              return statusReport;
              
          } catch (SQLException ex) {
  			ex.printStackTrace();
          }

      }
      return null;
    }

    @Override
    public StringBuffer printReport(String StatsGroupName,
	    StringBuffer incomingBuffer) {
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
 	   
    	  Object[] threads = (Object[]) StressTool.getThreadsInfo().values().toArray();
    	  String[] actions = new String[]{"Insert","Update","Delete","Select"};
   	   	  pw.println("------------------------------  Threads EXECUTION INFORMATION -----------------------------------------");

    	   for(String action:actions){
    		   pw.println("------------------------------ " + action);
    		   
	    	   for(Object thInfo:threads){
	    		   if(thInfo == null)
	    			   continue;
	    		  if(((ActionTHElement)thInfo).getAction().toUpperCase().equals(action.toUpperCase())){
	    		   pw.println("ThreadID = " +  ((ActionTHElement)thInfo).getId() 
	    				   + " Tot Execution time = " +((ActionTHElement)thInfo).getTotalEcecutionTime()
	    				   + " Max Execution time = " + ((ActionTHElement)thInfo).getMaxExectime() 
	    				   + " Min Execution time = " + ((ActionTHElement)thInfo).getMinExectime()
	    				   );
	    		  }
	    	   }
    	   }
    	   
    	   
    	   
    	   
    	   
            pw.println("------------------------------ "+this.getProviderName()+" DATABASE INFORMATION -----------------------------------------");
            pw.println("Provider  = " + getProviderName());
            pw.println("Start time  = " + Utility.getTimeStamp(reporterGroup.getStartTime(),"yy/MM/dd hh:mm:ss:SSSS a"));
            pw.println("End time  = " + Utility.getTimeStamp(reporterGroup.getLastSampleTime(),"yy/MM/dd hh:mm:ss:SSSS a"));
            String[] names = reporterGroup.getEventsCollactionNames();
            
            pw.println("Total Kbyte IN  = " + getResultByName("Bytes_sent",false) + " xsec = " + (((Long)this.getResultByName("Bytes_sent",false)).longValue()/(totalExecutionTime/1000)));
       }
       catch(Throwable th){
    	   th.printStackTrace();
       }
    
            if(flushrowonfile){
	            writeReportOnFile(sw.toString());
	    }
 
            return sb.append(sw.toString());
    
    	
        }
}
