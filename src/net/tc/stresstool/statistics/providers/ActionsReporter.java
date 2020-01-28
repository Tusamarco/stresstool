package net.tc.stresstool.statistics.providers;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.Map;

import net.tc.data.db.ConnectionProvider;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.statistics.ActionTHElement;
import net.tc.stresstool.statistics.StatEvent;
import net.tc.stresstool.statistics.StatsGroups;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;
import net.tc.stresstool.PerformanceEvaluator;

public class ActionsReporter extends BaseStatCollector implements Reporter, StatsProvider {
    private String statGroupName;

	public ActionsReporter() {
	 super();
	 this.setStatGroupName("ActionReporter");
    } 
	
	 Map getStatus(Connection conn)  {
		 Map statusReport = new SynchronizedMap(0);
		 long time = System.currentTimeMillis();
		 int internalOrder = 0;
		 
		    try{
			 	  Object[] threads = (Object[]) StressTool.getThreadsInfo().values().toArray();
			 	  String[] actions = new String[]{"Insert","Update","Delete","Select"};

		 		   for(String action:actions){
		 			   if(threads.length <= 0 )
		 				   return null;
		 			   
			    	   for(Object thInfo:threads){
			    		   if(thInfo == null)
			    			   return null;
			    		   if(((ActionTHElement)thInfo).getAction().toUpperCase().equals(action.toUpperCase())){
	
			    			  StatEvent event1 = new StatEvent();
				              String name1 = "thread_ID_"+((ActionTHElement)thInfo).getId();
				              long value1 = ((ActionTHElement)thInfo).getId() ;
				              event1.setCollection(this.statGroupName);
				              event1.setTime(time);
				              event1.setProvider(this.getClass().getCanonicalName());
				              event1.setEvent(name1);
				              event1.setValue(value1);
				              event1.setId(loopNumber);
				              event1.setOrder(internalOrder);
				              statusReport.put(name1,event1);
				              internalOrder++;

				              StatEvent event2 = new StatEvent();
				              String name2 = "Execution_time_" +((ActionTHElement)thInfo).getId();
				              long value2 = ((ActionTHElement)thInfo).getExecutionTime() ;
//				              System.out.println("Execution time "+ ((ActionTHElement)thInfo).getId() + "  " + value2);
				              event2.setCollection(this.statGroupName);
				              event2.setTime(time);
				              event2.setProvider(this.getClass().getCanonicalName());
				              event2.setEvent(name2);
				              event2.setValue(value2);
				              event2.setId(loopNumber);
				              event2.setOrder(internalOrder);
				              statusReport.put(name2,event2);
				              internalOrder++;
				              
				              StatEvent event3 = new StatEvent();
				              String name3 = "Connection_latency_" + ((ActionTHElement)thInfo).getId();
				              long value3 = ((ActionTHElement)thInfo).getConnectionTime();
				              event3.setCollection(this.statGroupName);
				              event3.setTime(time);
				              event3.setProvider(this.getClass().getCanonicalName());
				              event3.setEvent(name3);
				              event3.setValue(value3);
				              event3.setId(loopNumber);
				              event3.setOrder(internalOrder);
				              statusReport.put(name3,event3);
				              internalOrder++;

				              StatEvent event4 = new StatEvent();
				              String name4 = "Latency_" + ((ActionTHElement)thInfo).getId();
				              long value4 = ((ActionTHElement)thInfo).getLatency();
				              event4.setCollection(this.statGroupName);
				              event4.setTime(time);
				              event4.setProvider(this.getClass().getCanonicalName());
				              event4.setEvent(name4);
				              event4.setValue(value4);
				              event4.setId(loopNumber);
				              event4.setOrder(internalOrder);
				              statusReport.put(name4,event4);
				              internalOrder++;
				              
				              //Currentloop
				              //Action name
				    		  }
				    	 }
		 		   		}
		 		   		return statusReport;
		    		}
		 		    catch(Throwable th){
		 		    	th.printStackTrace();
		 		 }
		 
		 
		 return null;
	 }
//	void setStatGroupName(String gName) {
//		statGroupName=gName;
//	}
//    
//	@Override
//	public Map collectStatistics(Connection conn) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Map getMetrics() {
//		// TODO Auto-generated method stub
//		return null;
//	}



	@Override
	public boolean validatePermissions(ConnectionProvider connProvider) {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
//	public String getReporterName() {
//		
//		return statGroupName;
//	}
//
//	@Override
//	public String[] getHeadersArray(String StatsGroupName) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String[] getRowDataArray(String[] headersArray) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public StringBuffer getHeaders(StringBuffer sb) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public StringBuffer getRowData(StringBuffer sb) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
    public StringBuffer printReport(String StatsGroupName,
    	    StringBuffer incomingBuffer) {

    	return printReport(incomingBuffer);
    	
    }
        
     protected StringBuffer printReport(StringBuffer sb) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        PerformanceEvaluator per =new PerformanceEvaluator();
        
	    try{
		   
	 	  Object[] threads = (Object[]) StressTool.getThreadsInfo().values().toArray();
	 	  String[] actions = new String[]{"Insert","Update","Delete","Select"};
	 	  long totalExecutionTime = (reporterGroup.getLastSampleTime() - reporterGroup.getStartTime()); 
	 	  long runningThI = 0;	  
		  long runningThU = 0;
		  long runningThS = 0;
		  long runningThD = 0;
	 	  
		   	  pw.println("------------------------------ "+ this.getReporterName()  +" Threads EXECUTION INFORMATION -----------------------------------------");
	          pw.println("Provider  = " + getProviderName());
	          pw.println("Start time  = " + Utility.getTimeStamp(reporterGroup.getStartTime(),"yy/MM/dd hh:mm:ss:SSSS a"));
	          pw.println("End time  = " + Utility.getTimeStamp(reporterGroup.getLastSampleTime(),"yy/MM/dd hh:mm:ss:SSSS a"));
	            
	          pw.println("Total Execution time = " + totalExecutionTime/1000);
		   	  
	          for(String action:actions){
	        	  for(Object thInfo:threads){
	        		  if(thInfo == null)
			    		   continue;
			    	  if(((ActionTHElement)thInfo).getAction().toUpperCase().equals(action.toUpperCase())){
			    		  
			    		   switch (action.toLowerCase()){
			    		   	case "insert":
			    		   		runningThI = ((ActionTHElement)thInfo).getRunningThreads()>runningThI?((ActionTHElement)thInfo).getRunningThreads():runningThI;
			    		   		break;
			    		   	case "select":
			    		   		runningThS = ((ActionTHElement)thInfo).getRunningThreads()>runningThS?((ActionTHElement)thInfo).getRunningThreads():runningThS;
			    		   		break;
			    		   	case "update":
			    		   		runningThU = ((ActionTHElement)thInfo).getRunningThreads()>runningThU?((ActionTHElement)thInfo).getRunningThreads():runningThU;
			    		   		break;
			    		   	case "delete":
			    		   		runningThD = ((ActionTHElement)thInfo).getRunningThreads()>runningThD?((ActionTHElement)thInfo).getRunningThreads():runningThD;
			    		   		break;
			    		   }
			    		  
			    		  }
			    	  }
	          }
	       pw.println("Running threads for Inserts = " + runningThI);
	       pw.println("Running threads for Updates = " + runningThU);
	       pw.println("Running threads for Selects = " + runningThS);
	       pw.println("Running threads for Deletes = " + runningThD);
	       
	          
		   	  
		   long avgExecI = 0;	  
		   long avgExecU = 0;
		   long avgExecS = 0;
		   long avgExecD = 0;
		   
		   long avgConnLatI = 0;
		   long avgConnLatU = 0;
		   long avgConnLatS = 0;
		   long avgConnLatD = 0;
		   
		   long avgExcLatI = 0;
		   long avgExcLatU = 0;
		   long avgExcLatS = 0;
		   long avgExcLatD = 0;
		   
		   long sumEventsI = 0;
		   long sumEventsU = 0;
		   long sumEventsS = 0;
		   long sumEventsD = 0;
		   
		   	  
		   long tSelects = 0;
 		   long tInserts = 0;
 		   long tUpdates = 0;
 		   long tDeletes = 0;
 		   long maxExecI=0;
 		   long maxExecU=0;
 		   long maxExecS=0;
 		   long maxExecD=0;
 		   
 		   long batchI=1;
		   long batchU=1;
		   long batchS=1;
		   long batchD=1;
		   

 		   for(String action:actions){
 			   
	 		   pw.println("------------------------------ " + action);
		    	   for(Object thInfo:threads){
		    		   if(thInfo == null)
		    			   continue;
		    		  if(((ActionTHElement)thInfo).getAction().toUpperCase().equals(action.toUpperCase())){
			    		   pw.println("ThreadID = " +  ((ActionTHElement)thInfo).getId() 
			    				   + " Tot Execution time (Sec) = " + ((ActionTHElement)thInfo).getTotalEcecutionTime()
			    				   + "; MAX|MIN|AVG ms = " + per.getIMSFromNano(((ActionTHElement)thInfo).getMaxExectime()) 
			    				   + "|" + per.getIMSFromNano(((ActionTHElement)thInfo).getMinExectime())
			    				   + "|" + per.getIMSFromNano(((ActionTHElement)thInfo).getAvgExecTime())
//			    				   + "; AVG Th Exec time ms= " + per.getIMSFromNano(((((ActionTHElement)thInfo).getMaxExectime() + ((ActionTHElement)thInfo).getMinExectime())/2))
			    				   
			    				   + "; Conn lat MAX|MIN|AVG ns = "
			    				   		+ ((ActionTHElement)thInfo).getMaxConnectionTime() 
			    				   		+ "|" + ((ActionTHElement)thInfo).getMinConnectionTime()	
			    				   		+ "|" + ((ActionTHElement)thInfo).getAvgConnectionTime() 
			    				   + "; Latency(exec time + Conn + more delay)MAX|MIN|AVG ms "
			    				   		+ per.getIMSFromNano(((ActionTHElement)thInfo).getMaxLatency()) 
			    				   		+ "|" + per.getIMSFromNano(((ActionTHElement)thInfo).getMinLatency())
			    				   		+ "|" + per.getIMSFromNano(((ActionTHElement)thInfo).getAvgLatency())
			    				   + "; Events = " + ((ActionTHElement)thInfo).getCurrentLoop()
		    				     );
			    		   switch (action.toLowerCase()){
			    		   	case "insert":
			    		   		avgExecI = (long) (avgExecI + per.getIMSFromNano(((ActionTHElement)thInfo).getAvgExecTime()));
			    		   		avgConnLatI = (long)(avgConnLatI + ((ActionTHElement)thInfo).getAvgConnectionTime() );
			    		   		avgExcLatI = (long) (avgExcLatI + per.getIMSFromNano(((ActionTHElement)thInfo).getAvgLatency()) );
			    		   		sumEventsI = (long) (sumEventsI + ((ActionTHElement)thInfo).getCurrentLoop());
			    		   		
			    		   		tInserts = tInserts + (((ActionTHElement)thInfo).getCurrentLoop());
			    		   		maxExecI = (((ActionTHElement)thInfo).getTotalEcecutionTime() > maxExecI)?((ActionTHElement)thInfo).getTotalEcecutionTime():maxExecI;  
			    		   		batchI=((ActionTHElement)thInfo).getBatchSize();
			    		   		break;
			    		   	case "select":
			    		   		avgExecS = (long) (avgExecS + per.getIMSFromNano(((ActionTHElement)thInfo).getAvgExecTime()));
			    		   		avgConnLatS = (long)(avgConnLatS + ((ActionTHElement)thInfo).getAvgConnectionTime() );
			    		   		avgExcLatS = (long) (avgExcLatS + per.getIMSFromNano(((ActionTHElement)thInfo).getAvgLatency()) );
			    		   		sumEventsS = (long) (sumEventsS + ((ActionTHElement)thInfo).getCurrentLoop());
			    		   		
			    		   		tSelects = tSelects + (((ActionTHElement)thInfo).getCurrentLoop() ); 
			    		   		maxExecS = (((ActionTHElement)thInfo).getTotalEcecutionTime() > maxExecS)?((ActionTHElement)thInfo).getTotalEcecutionTime():maxExecS;
			    		   		batchS=((ActionTHElement)thInfo).getBatchSize();
			    		   		break;
			    		   	case "update":
			    		   		avgExecU = (long) (avgExecU + per.getIMSFromNano(((ActionTHElement)thInfo).getAvgExecTime()));
			    		   		avgConnLatU = (long)(avgConnLatU + ((ActionTHElement)thInfo).getAvgConnectionTime() );
			    		   		avgExcLatU = (long) (avgExcLatU + per.getIMSFromNano(((ActionTHElement)thInfo).getAvgLatency()) );
			    		   		sumEventsU = (long) (sumEventsU + ((ActionTHElement)thInfo).getCurrentLoop());
			    		   		
			    		   		tUpdates = tUpdates + (((ActionTHElement)thInfo).getCurrentLoop()); 
			    		   		maxExecU = (((ActionTHElement)thInfo).getTotalEcecutionTime() > maxExecU)?((ActionTHElement)thInfo).getTotalEcecutionTime():maxExecU;
			    		   		batchU=((ActionTHElement)thInfo).getBatchSize();
			    		   		break;
			    		   	case "delete":
			    		   		avgExecD = (long) (avgExecD + per.getIMSFromNano(((ActionTHElement)thInfo).getAvgExecTime()));
			    		   		avgConnLatD = (long)(avgConnLatD + ((ActionTHElement)thInfo).getAvgConnectionTime() );
			    		   		avgExcLatD = (long) (avgExcLatD + per.getIMSFromNano(((ActionTHElement)thInfo).getAvgLatency()) );
			    		   		sumEventsD = (long) (sumEventsD + ((ActionTHElement)thInfo).getCurrentLoop());
			    		   		
			    		   		tDeletes = tDeletes + (((ActionTHElement)thInfo).getCurrentLoop() ); 
			    		   		maxExecD = (((ActionTHElement)thInfo).getTotalEcecutionTime() > maxExecD)?((ActionTHElement)thInfo).getTotalEcecutionTime():maxExecD;
			    		   		batchD=((ActionTHElement)thInfo).getBatchSize();
			    		   		break;
			    		   }
		    		   
		    		  }
		    	 }
		    	   
	 	   }
 		   pw.println("------------------------------ CRUD");
 		   if(maxExecS > 0 )pw.println("select tot " + (tSelects * batchS )+ " Sel/S " + (tSelects/maxExecS) * batchS );
 		   if(maxExecI > 0 )pw.println("insert tot " + (tInserts * batchI) + " Ins/S " + (tInserts/maxExecI) *batchI );
 		   if(maxExecU > 0 )pw.println("update tot " + (tUpdates * batchU) + " Up/S " + (tUpdates/maxExecU) * batchU);
 		   if(maxExecD > 0 )pw.println("delete tot " + (tDeletes * batchD) + " Del/S " + (tDeletes/maxExecD)*batchD );
 		   pw.println("------------------------------  Threads csv info AVG  -----------------------------------------");
 		   pw.println("totRunThs,avgExecI(ms),avgExecU(ms),avgExecS(ms),avgExecD(ms),"
 		   		+ "avgConnLatI(ns),avgConnLatU(ns),avgConnLatS(ns),avgConnLatD(ns),"
 		   		+ "avgExcLatI(ms),avgExcLatU(ms),avgExcLatS(ms),avgExcLatD(ms),"
 		   		+ "sumEventsI,sumEventsU,sumEventsS,sumEventsD,"
 		   		+ "Insert_total,Update_total,Select_total,Delete_total,"
 		   		+ "Insert_sec,Update_sec,Select_sec,Delete_sec");
 		   
 		   pw.append((runningThI + runningThU +runningThS + runningThD) + ",");
 		   
 		   if(runningThI > 0){pw.append(avgExecI/runningThI + ",");}else{pw.append( "0,");};	  
 		   if(runningThU > 0){pw.append(avgExecU/runningThU + ",");}else{pw.append( "0,");};
 		   if(runningThS > 0){pw.append(avgExecS/runningThS + ",");}else{pw.append( "0,");};
 		   if(runningThD > 0){pw.append(avgExecD/runningThD + ",");}else{pw.append( "0,");};
		   
 		   if(runningThI > 0){pw.append(avgConnLatI/runningThI + ",");}else{pw.append( "0,");};
 		   if(runningThU > 0){pw.append(avgConnLatU/runningThU + ",");}else{pw.append( "0,");};
 		   if(runningThS > 0){pw.append(avgConnLatS/runningThS + ",");}else{pw.append( "0,");};
 		   if(runningThD > 0){pw.append(avgConnLatD/runningThD + ",");}else{pw.append( "0,");};
		   
 		   if(runningThI > 0){pw.append(avgExcLatI/runningThI + ",");}else{pw.append( "0,");};
 		   if(runningThU > 0){pw.append(avgExcLatU/runningThU + ",");}else{pw.append( "0,");};
 		   if(runningThS > 0){pw.append(avgExcLatS/runningThS + ",");}else{pw.append( "0,");};
 		   if(runningThD > 0){pw.append(avgExcLatD/runningThD + ",");}else{pw.append( "0,");};
		   
 		   pw.append(sumEventsI + ",");
 		   pw.append(sumEventsU + ",");
 		   pw.append(sumEventsS + ",");
 		   pw.append(sumEventsD + ",");
 		   
 		   pw.append(tInserts * batchI +",");
 		   pw.append(tUpdates * batchU +",");
 		   pw.append(tSelects * batchS +",");
 		   pw.append(tDeletes * batchD +",");
 		   
 		   if(tInserts > 0 ){pw.append(((tInserts * batchI)/(totalExecutionTime/1000)) + ",");}else {pw.append("0,");}
 		   if(tUpdates > 0 ){pw.append(((tUpdates * batchU)/(totalExecutionTime/1000)) +",");}else {pw.append("0,");}
 		   if(tSelects > 0 ){pw.append(((tSelects * batchS)/(totalExecutionTime/1000)) +",");}else {pw.append("0,");}
 		   if(tDeletes > 0 ){pw.append(((tDeletes * batchD)/(totalExecutionTime/1000)) +"");}else {pw.append("0");}
 		   pw.append("\n");
 		   
 		   
 		   
 		   pw.println("------------------------------  Threads EXECUTION INFORMATION END -----------------------------------------");
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
