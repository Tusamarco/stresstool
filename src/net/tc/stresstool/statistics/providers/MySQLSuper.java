package net.tc.stresstool.statistics.providers;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.log4j.Logger;

import net.tc.stresstool.PerformanceEvaluator;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.config.Configuration;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolGenericException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.EventCollection;
import net.tc.stresstool.statistics.StatEvent;
import net.tc.stresstool.statistics.StatsGroups;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;
import net.tc.utils.file.FileHandler;

/**
 * @author  tusa
 * V2
 */
public class MySQLSuper implements StatsProvider, Reporter {

    /**
     * @uml.property  name="loopNumber"
     */
    protected int loopNumber = 0;
    /**
     * @uml.property  name="statGroupName"
     */
    protected final String statGroupName = "STATUS";
    /**
     * @uml.property  name="statsFile"
     * @uml.associationEnd  
     */
    protected FileHandler statsFile = null;
    /**
     * @uml.property  name="csvFile"
     * @uml.associationEnd  
     */
    protected FileHandler csvFile = null;
    /**
     * @uml.property  name="flushrowonfile"
     */
    protected boolean flushrowonfile = false;
    /**
     * @uml.property  name="eventsName"
     */
    protected String[] eventsName = null;
    /**
     * @uml.property  name="lastSampleTime"
     */
    protected String lastSampleTime = null;
    /**
     * @uml.property  name="status"
     * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="java.lang.Object" qualifier="s:java.lang.String java.lang.Object"
     */
    protected Map status = null;
    /**
     * @uml.property  name="reporterGroup"
     * @uml.associationEnd  
     */
    protected  StatsGroups reporterGroup = null;

    public MySQLSuper() {
	super();
    }

    public Map collectStatistics(Connection conn) {
        loopNumber++;
        status = new SynchronizedMap(0);
        lastSampleTime = Utility.getHour()+":"+Utility.getMinute()+":"+Utility.getSecond();
        status = getStatus(conn);
    
        if(eventsName == null)
            eventsName = ((SynchronizedMap)status).getKeyasOrderedStringArray();
        
        if(flushrowonfile){
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
        	statsFile = new FileHandler(rootPath + "/" +getStatGroup()+"_"+Utility.getTimestamp()+".txt");
        	
        	if(flushrowonfile){
        	    csvFile = new FileHandler(rootPath + "/" +getStatGroup()+"_"+Utility.getTimestamp()+".csv");
        	    
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
                     endValue = Long.parseLong((String)eventColl.getCollection().get(new Long(2)));
                     startValue = Long.parseLong((String)eventColl.getCollection().get(new Long(eventColl.getCollection().size())));
                	}
                	catch(NullPointerException e){
                		return "WARNING - NULL VALUE";
                	}
                	finally{
        			try{
        			    return (Long)(endValue - startValue);
        			}
        			catch(final NullPointerException en){
    //            				System.out.println("WARNING -- RETURNING NULL VALUE FOR STATUS KEY NAME =" + varName);
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

    @Override
    public boolean validatePermissions(Map connectionConfiguration) {
	Logger applicationLogger = null;
	try {
	    applicationLogger = StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION);
	} catch (StressToolConfigurationException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	if(connectionConfiguration == null)
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
            conn = initConnection(connectionConfiguration);
            conn.setAutoCommit(false);
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
	    
            applicationLogger.info("Checking Generic permissions on:" + connectionConfiguration.get("database"));
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
            		" and db='test'";
        	
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
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        if(!valid){
            applicationLogger.info("Permissions on global AND DB failed I cannot write on the DB");
            applicationLogger.error("NOT sufficient  permissions on:" + connectionConfiguration.get("database")+ " for user:" + userName );
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
            "EXECUTE,INDEX,INSERT,LOCK TABLES,SELECT,TRIGGER,UPDATE on " + connectionConfiguration.get("database")+ ".* to " + connectionConfiguration.get("user") 
            + "@'<host>' identified by '<secret>'");
            
            applicationLogger.error("Try to issue: Grant CREATE TABLESPACE,FILE, PROCESS,REPLICATION CLIENT,SHOW DATABASES on *.* to " + connectionConfiguration.get("user") 
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
					.getTimeEvaluation(performanceTimeStart));
	    }
	} catch (Throwable th) {
	}
	/*Performance evaluation section [header] END*/

        applicationLogger.info("Permissions seems OK going ahead");
        return true;


    }
    
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


}