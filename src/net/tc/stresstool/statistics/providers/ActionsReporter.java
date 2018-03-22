package net.tc.stresstool.statistics.providers;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.Map;

import net.tc.data.db.ConnectionProvider;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.statistics.ActionTHElement;
import net.tc.stresstool.statistics.StatsGroups;
import net.tc.stresstool.PerformanceEvaluator;

public class ActionsReporter implements Reporter, StatsProvider {
    private String statGroupName;

	public ActionsReporter() {
	 
	 this.setStatGroupName("ActionReporter");
    } 
    
	void setStatGroupName(String gName) {
		statGroupName=gName;
	}
    
	@Override
	public Map collectStatistics(Connection conn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map getMetrics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getStatsOutFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getEventsName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeStatsOnFile(Map valuesToWrite) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getProviderName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStatsOutFile(String rootPath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFlushDataOnfile(boolean flushrowonfile) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean validatePermissions(ConnectionProvider connProvider) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setDefaultSchema(String defaultSchema) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDefaultSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReporterName() {
		
		return statGroupName;
	}

	@Override
	public String[] getHeadersArray(String StatsGroupName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getRowDataArray(String[] headersArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuffer getHeaders(StringBuffer sb) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuffer getRowData(StringBuffer sb) {
		// TODO Auto-generated method stub
		return null;
	}

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
		   	  pw.println("------------------------------ "+ this.getReporterName()  +" Threads EXECUTION INFORMATION -----------------------------------------");

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
			    				   + "; Max Th Exec time ms = " + per.getIMSFromNano(((ActionTHElement)thInfo).getMaxExectime()) 
			    				   + "; Min Th Exec time ms= " + per.getIMSFromNano(((ActionTHElement)thInfo).getMinExectime())
			    				   //+ "; AVG Th Exec time ms= " + per.getIMSFromNano(((ActionTHElement)thInfo).getAvgExecTime())
			    				   + "; AVG Th Exec time ms= " + per.getIMSFromNano(((((ActionTHElement)thInfo).getMaxExectime() + ((ActionTHElement)thInfo).getMinExectime())/2))
			    				   
			    				   + "; Conn lat MAX|MIN ns = "
			    				   		+ ((ActionTHElement)thInfo).getMaxConnectionTime() 
			    				   		+ "|" + ((ActionTHElement)thInfo).getMinConnectionTime()	
//			    				   		+ "|" + (((ActionTHElement)thInfo).getMaxConnectionTime() + ((ActionTHElement)thInfo).getMinConnectionTime())/2
			    				   + "; Events = " + ((ActionTHElement)thInfo).getCurrentLoop()
		    				     );
			    		   switch (action.toLowerCase()){
			    		   	case "insert":
			    		   		tInserts = tInserts + (((ActionTHElement)thInfo).getCurrentLoop());
			    		   		maxExecI = (((ActionTHElement)thInfo).getTotalEcecutionTime() > maxExecI)?((ActionTHElement)thInfo).getTotalEcecutionTime():maxExecI;  
			    		   		batchI=((ActionTHElement)thInfo).getBatchSize();
			    		   		break;
			    		   	case "select":
			    		   		tSelects = tSelects + (((ActionTHElement)thInfo).getCurrentLoop() ); 
			    		   		maxExecS = (((ActionTHElement)thInfo).getTotalEcecutionTime() > maxExecS)?((ActionTHElement)thInfo).getTotalEcecutionTime():maxExecS;
			    		   		batchS=((ActionTHElement)thInfo).getBatchSize();
			    		   		break;
			    		   	case "update":
			    		   		tUpdates = tUpdates + (((ActionTHElement)thInfo).getCurrentLoop()); 
			    		   		maxExecU = (((ActionTHElement)thInfo).getTotalEcecutionTime() > maxExecU)?((ActionTHElement)thInfo).getTotalEcecutionTime():maxExecU;
			    		   		batchU=((ActionTHElement)thInfo).getBatchSize();
			    		   		break;
			    		   	case "delete":
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
 		   pw.println("------------------------------  Threads EXECUTION INFORMATION END -----------------------------------------");
	    }   
	    catch(Throwable th){
	    	th.printStackTrace();
	    }
 	   
 	   	 
    	 
		return sb.append(sw.toString());
	}

	@Override
	public void setStats(StatsGroups stats) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getStatGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeReportOnFile(String outString) {
		// TODO Auto-generated method stub

	}

}
