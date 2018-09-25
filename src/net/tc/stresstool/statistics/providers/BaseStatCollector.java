package net.tc.stresstool.statistics.providers;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import net.tc.data.db.ConnectionProvider;
import net.tc.data.db.PartitionDefinition;
import net.tc.stresstool.PerformanceEvaluator;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.config.Configuration;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.stresstool.exceptions.StressToolGenericException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.EventCollection;
import net.tc.stresstool.statistics.StatEvent;
import net.tc.stresstool.statistics.StatsGroups;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;
import net.tc.utils.file.FileHandler;
import net.tc.data.db.*;
/**
 * @author  tusa
 * V4
 */
public class BaseStatCollector implements StatsProvider, Reporter {

    protected int loopNumber = 0;
    protected String statGroupName = "";
    protected FileHandler statsFile = null;
    protected FileHandler csvFile = null;
    protected boolean flushrowonfile = false;
    protected String[] eventsName = null;
    protected String lastSampleTime = null;
    protected Map status = null;
    protected  StatsGroups reporterGroup = null;
    protected String DefaultSchema = null;

    public BaseStatCollector() {
	super();
    }

    public Map collectStatistics(Connection conn) {
        loopNumber++;
        status = new SynchronizedMap(0);
        lastSampleTime = Utility.getHour()+":"+Utility.getMinute()+":"+Utility.getSecond();
        status = getStatus(conn);
    
        if(status != null && eventsName == null)
            eventsName = ((SynchronizedMap)status).getKeyasOrderedStringArray();
        
        if(flushrowonfile && status !=null){
            writeStatsOnFile(status);
        }
            
        return status;
    }

    Map getStatus(Connection conn) { 
	try {
	    throw new StressToolGenericException(ExceptionMessages.ERROR_METHOD_IN_SUPERCLASS);
	} catch (StressToolGenericException e) {
	    
	    e.printStackTrace();
	}

	return null;
    }

    @Override
    public Map getMetrics() {
        return status;
        
    }

    @Override
    public File getStatsOutFile() {
        if(statsFile != null )
            return statsFile.getIn();
        return null;
    }

    /**
     * @return
     * @uml.property  name="eventsName"
     */
    @Override
    public String[] getEventsName() {
        if(this.eventsName != null && eventsName.length >0)
            return eventsName;
        return null;
    }

    @Override
    public void writeStatsOnFile(Map toWrite) {
        if(csvFile != null ){
            StringBuffer sb = new StringBuffer();
            if(this.loopNumber == 1){
        	sb.append("time");
        	csvFile.appendToFile(getHeaders(sb).toString()+"\n");
            }
            if(lastSampleTime != null){
        	    sb.delete(0, sb.length());
        	    sb.append(this.lastSampleTime);
        	    for(String s : eventsName){
        		sb.append("," + ((StatEvent)toWrite.get(s)).getValue().toString());
        	    }
        	    csvFile.appendToFile(sb.toString()+"\n");
            }
            
        }
    }

    @Override
    public void writeReportOnFile(String report) {
        if(statsFile != null && report != null && !report.equals("")){
            statsFile.appendToFile(report);
        }
    }

    @Override
    public String getStatGroup() {
        return getProviderName();
        
    }

    @Override
    public String getProviderName() {
        // 
        return statGroupName;
    }

    @Override
    public String[] getHeadersArray(String StatsGroupName) {
        if(eventsName != null)
            return eventsName;
        return null;
    }

    @Override
    public String[] getRowDataArray(String[] headersArray) {
        if(headersArray != null  && headersArray.length > 0){
            return (String[])((SynchronizedMap)status).getValuesAsArrayOrderByKey(headersArray);
        }
        return null;
    }

    @Override
    public StringBuffer getHeaders(StringBuffer sb) {
        if(eventsName != null && eventsName.length > 0){
             for(String i : eventsName){
        	 sb.append("," + i);
             }
             return sb;
        	 
        }
        return null;
    }

    @Override
    public StringBuffer getRowData(StringBuffer sb) {
            if(lastSampleTime != null && status != null && status.size() > 0){
        	    sb.delete(0, sb.length());
        	    sb.append(this.lastSampleTime);
        	    for(String s : eventsName){
        		sb.append("," + status.get(s));
        	    }
        	    return sb;
    	    }
    
        return null;
    }

    protected StringBuffer printReport(StringBuffer sb) {
		try {
		    throw new StressToolGenericException(ExceptionMessages.ERROR_METHOD_IN_SUPERCLASS);
		} catch (StressToolGenericException e) {
		    
		    e.printStackTrace();
		}
 
		if(flushrowonfile){
	            writeReportOnFile("");
	        }

    		return sb;
        }

    @Override 
    public String getReporterName() {
        return this.getProviderName();
        
    }

    @Override
    public void setStats(StatsGroups stats) {
        if(stats != null){
            reporterGroup = stats;
        }
        else{
            try {
        	throw new StressToolGenericException(" StatsGroup for reporter " + getProviderName() + " Cannot be null");
            } catch (StressToolGenericException e) {
        	e.printStackTrace();
            }
        }
            
        
    }

    @Override
    public void setStatsOutFile(String rootPath) {
        if(rootPath != null && !rootPath.equals("")){
            try{
        	statsFile = new FileHandler(rootPath + "/" +getStatGroup()+"_"+Utility.getTimestamp()+".txt", FileHandler.FILE_FOR_WRITE );
        	
        	if(flushrowonfile){
        	    csvFile = new FileHandler(rootPath + "/" +getStatGroup()+"_"+Utility.getTimestamp()+".csv", FileHandler.FILE_FOR_WRITE);
        	    
        	}
            }
            catch(Throwable ex){
        	new StressToolGenericException(ex);
        	
            }
            
        }
    }

    @Override
    public void setFlushDataOnfile(boolean flushrowonfileIn) {
        flushrowonfile = flushrowonfileIn;
        
    }

    @SuppressWarnings("finally")
    protected Object getResultByName(String varName, boolean isString) {
            if(!isString)
            {
            	Long endValue = null;
            	Long startValue = null;
            	EventCollection eventColl = reporterGroup.getEventCollection(varName);
            	if(!Utility.checkEntryInArray(eventsName,varName))
            		return new Long(0);
            	
            	
            	if (eventColl != null && eventColl.getCollection().size() > 0) {
                	try{
                	 StatEvent eventMin =  (StatEvent)eventColl.getCollection().get(new Long(1));
                	 StatEvent eventMax =  (StatEvent)((SynchronizedMap)eventColl.getCollection()).getValueByPosition(eventColl.getCollection().size()-1);
                	 startValue = Long.parseLong((String)eventMin.getValue());
                     endValue = Long.parseLong((String) eventMax.getValue() );
                	}
                	catch(NullPointerException e){
                		return "WARNING - NULL VALUE";
                	}
                	finally{
	        			try{
	        			    return (Long)(endValue - startValue);
	        			}
	        			catch(final NullPointerException en){
	      				  try {
	      					StressTool.getLogProvider().getLogger(LogProvider.LOG_STATS).warn(ExceptionMessages.ERROR_PORCESSING_STATS 
	      						+ "WARNING -- RETURNING NULL VALUE FOR STATUS KEY NAME =" + varName);
	      				  } catch (StressToolException e) {
	      					//e.printStackTrace();
	      			
	      				  }
	
	        				return new Long(0);
	        			}
                	}
                }
            }
            else
            {
                return "Not yet implemented";
            }
            return "";
        }


    protected Object getSumResultByName(String varName, boolean isString) {
    	Long endValue = null;
    	Long startValue = null;
    	EventCollection eventColl = reporterGroup.getEventCollection(varName);
    	if(!Utility.checkEntryInArray(eventsName,varName))
    		return new Long(0);
    	
    	
    	if (eventColl != null && eventColl.getCollection().size() > 0) {
        	 
             endValue = new Long(eventColl.getSumValue() );
             return endValue;
        }
        else
        {
            return "Not yet implemented";
        }

     }

    
    protected long getMaxResultByName(String varName, boolean isString) {
    
       	EventCollection eventColl = reporterGroup.getEventCollection(varName);
        if (eventColl != null && eventColl.getCollection().size() > 0) 
        {
            return eventColl.getMaxValue();
        }
        return 0;
        
    }

    @Override
    public StringBuffer printReport(String StatsGroupName,
	    StringBuffer incomingBuffer) {
	// TODO Auto-generated method stub
	
	return null;
    }


//    @Deprecated
//    /*
//     * Do not use this, use ConnectionProvider instead from Launcher
//     */
//    public synchronized static Connection initConnection(Map connMapcoordinates)
//	throws SQLException {
//	    Connection conn;
//	    if(connMapcoordinates.get("dbtype") != null &&  !((String)connMapcoordinates.get("dbtype")).toLowerCase().equals("MySQL".toLowerCase()))
//	    {
//	    	conn=DriverManager.getConnection((String)connMapcoordinates.get("dbtype"),"test", "test");
//	    }
//	    else{
//	    String connectionString = (String)connMapcoordinates.get("jdbcUrl")
//	        	    +"/"+(String)connMapcoordinates.get("database")
//	        	    +"?user="+(String)connMapcoordinates.get("user")
//	        	    +"&password="+(String)connMapcoordinates.get("password")
//	        	    +"&"+(String)connMapcoordinates.get("connparameters");
//	    
//	    
//	    conn= DriverManager.getConnection(connectionString);
//	    }
//	    return conn;
//    }

	public String getStatGroupName() {
		return statGroupName;
	}

	void setStatGroupName(String gName) {
		statGroupName=gName;
	}


	public void setDefaultSchema(String defaultSchema) {
		DefaultSchema=defaultSchema;
		
	}


	public String getDefaultSchema() {
		return DefaultSchema;
	}

	
	public boolean validatePermissions(ConnectionProvider connProvider) {

		return true;
	}

	


/*
 * Calculate a partition set, base on the partition definition object
 * 	"partitionDefinition":{
	"partitionBy":"range",
	"subpartition":false,
	"attributes":["date"],
	"function":"to_days",
	"interval":"week",
	"starttime":"2015-11-01",
	"endtime":"2016-01-31",
	"partitions":"0",
	"lists":{"list":[]}
	"ranges":{"range":[]}
 
*/
}

