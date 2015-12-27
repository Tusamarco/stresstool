package net.tc.stresstool.actions;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import net.tc.data.db.Schema;
import net.tc.data.db.Table;
import net.tc.jsonparser.*;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.config.Configurator;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolSQLException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.ActionTHElement;
import net.tc.stresstool.statistics.providers.MySQLSuper;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/**
 * v 1.2
 */

public class InsertBase extends StressActionBase implements WriteAction,
	CreateAction {
	
    private int numberOfprimaryTables=1;
    private int numberOfSecondaryTables=0;
    
    private boolean useAutoIncrement=true;
    
    private int sleepWrite=0;
    private boolean lazyCreation=true;
    private int lazyInterval=5000;
    private int batchSize = 0; 
    private String jsonFile = "";
    private String tableEngine = "InnoDB";
    
    public String getJsonFile() {
		return jsonFile;
	}
	public void setJsonFile(String jsonFile) {
		this.jsonFile = jsonFile;
	}
	@Override
	public void LoadData() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void TruncateTables(String[] tables) {
	    Map connectionMap = super.getConnectionInformation();
	    StringBuffer sbTableDrop = new StringBuffer();
	    
	    if(tables !=null
		    && tables.length > 0){
		
		try {
		    Connection conn = MySQLSuper.initConnection(getConnectionInformation());
		    Statement stmt = null;
		    if(conn != null 
			   && !conn.isClosed()){
		    
			stmt =conn.createStatement();
			

        		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(" ==== TRUNCATE Tables, Please wait it may takes time ===="  );}catch(StressToolConfigurationException e){}
        		for(String tb : tables){
        		    String drop = "TRUNCATE TABLE IF EXISTS " + tb + " ;" ;
        		    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(drop);}catch(StressToolConfigurationException e){}
        		    stmt.execute(drop);
        		}
		    }
		    conn.close();
		    conn = null;
		} catch (SQLException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}

		
	    }
	    
	}
	@Override
	public void CreateActionTablePrimary() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void getActionTablePrimary() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setActionTablePrimary() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void getActionTableSecondary() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setActionTableSecondary() {
		// TODO Auto-generated method stub
		
	}
	@SuppressWarnings("finally")
	@Override
	public boolean  CreateSchema() {
		try{

			if(this.getJsonFile() == null){
				StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn("Table structure definition from Json file is null for Insert Class");
				StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn("check parameter : jsonfile");
				return false;
			}

			StringBuffer sbCreate = new StringBuffer();

			
			
			JSONParser parser = new JSONParser();
			StructureDefinitionParser strParser = new StructureDefinitionParserMySQL();
			FileReader fr = new FileReader(this.getJsonFile());
			Schema schema = strParser.parseSchema(parser, fr);

			Map tableInstances = new SynchronizedMap(0);
			tableInstances.put(Table.TABLE_PARENT, getNumberOfprimaryTables());
			tableInstances.put(Table.TABLE_CHILD, getNumberOfSecondaryTables());			
			
			/*
			 * First do the cleanup if set
			 */
			if(super.isDroptable()){
			     this.DropSchema(schema.deployDropTable(tableInstances));
			}
			else if(super.isTruncate())
			{
			    this.TruncateTables(schema.deployDropTable(tableInstances));
			}

			
			
			/*
			 * Once we got the schema from the Json file 
			 * we will create it exploding the tables as indicate from the multiple attribute
			 */
			if(schema != null){
			    sbCreate.append(schema.deploySchema((Map)tableInstances));
			    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Schema Definition SQL = [\n" + sbCreate.toString() + "\n ]"  );}catch(StressToolConfigurationException e){}
			    try {
				Connection conn = MySQLSuper.initConnection(getConnectionInformation());
				Statement stmt = null;
				if (conn != null && !conn.isClosed()) {
				    stmt = conn.createStatement();
				    try {StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(" ==== Creating schema ====");} catch (StressToolConfigurationException e) {}
				    String[] sqlAr = sbCreate.toString().replaceAll("\n", "").split(";");
				    for (String sql : sqlAr){
					stmt.addBatch(sql);
				    }
				    try {StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug( sbCreate.toString());} catch (StressToolConfigurationException e) {}
				    stmt.executeBatch();
				}
				conn.close();
				conn = null;
			    } catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			    }
			}			
				
		}
		catch(Exception e){
			e.printStackTrace();
		    ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
		    try{throw new StressToolConfigurationException(e);}catch(Throwable th){th.printStackTrace();}
		
		}
		finally{
		    return true;
		}

		
		
	}
	@Override
	public boolean  DropSchema(String[] tables) {
	    Map connectionMap = super.getConnectionInformation();
	    StringBuffer sbTableDrop = new StringBuffer();
	    
	    if(tables !=null
		    && tables.length > 0){
		
		try {
		    Connection conn = MySQLSuper.initConnection(getConnectionInformation());
		    Statement stmt = null;
		    if(conn != null 
			   && !conn.isClosed()){
		    
			stmt =conn.createStatement();

        		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(" ==== Dropping Tables, Please wait it may takes time ===="  );}catch(StressToolConfigurationException e){}
        		for(String tb : tables){
        		    String drop = "DROP TABLE IF EXISTS " + tb + " ;" ;
        		    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(drop);}catch(StressToolConfigurationException e){}
        		    stmt.execute(drop);
        		}
        		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(" ==== Drop Tables END ===="  );}catch(StressToolConfigurationException e){}
		    }
		    conn.close();
		    conn = null;
		} catch (SQLException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}

		
	    }
	    
	    
	    return false;
		
	}
	@Override
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
		
	}
	@Override
	public int getBatchSize() {
		return this.batchSize;
	}
	@Override
	public void setUseAutoIncrement(boolean useAutoIncrement) {
		this.useAutoIncrement = useAutoIncrement;
		
	}
	@Override
	public boolean isUseAutoIncrement() {
		return useAutoIncrement;
	}
//	public boolean getuseAutoincrement() {
//		return useAutoIncrement;
//	}

	@Override
	public ActionTable getInsertValueProvider() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setLazyness(boolean lazy) {
		this.lazyCreation = lazy;
		
	}
	@Override
	public boolean islazyCreation() {
		return this.lazyCreation;
	}
	@Override
	public void setLazyInterval(int interval) {
		this.lazyInterval = interval;
		
	}
	@Override
	public int getLazyInterval() {
		return this.lazyInterval;
	}
	@Override
	public void setNumberOfprimaryTables(int tableNumbers) {
		this.numberOfprimaryTables = tableNumbers;
		
	}
	@Override
	public int getNumberOfprimaryTables() {
		return this.numberOfprimaryTables;
	}
	@Override
	public void setNumberOfSecondaryTables(int tableNumbers) {
		this.numberOfSecondaryTables = tableNumbers;
		
	}
	@Override
	public int getNumberOfSecondaryTables() {
		return this.numberOfSecondaryTables;
	} 
	    
    public int getSleepWrite() {
		return sleepWrite;
	}
	public void setSleepWrite(int sleepWrite) {
		this.sleepWrite = sleepWrite;
	}
	/**
	 * @return the lazyCreation
	 */
	public boolean isLazyCreation() {
	    return lazyCreation;
	}
	/**
	 * @param lazyCreation the lazyCreation to set
	 */
	public void setLazyCreation(boolean lazyCreation) {
	    this.lazyCreation = lazyCreation;
	}
	/**
	 * @return the tableEngine
	 */
	public String getTableEngine() {
	    return tableEngine;
	}
	/**
	 * @param tableEngine the tableEngine to set
	 */
	public void setTableEngine(String tableEngine) {
	    this.tableEngine = tableEngine;
	}
	
	/**
	 * this is actually executing what the action is suppose to do
	 */
	@Override
	public void ExecuteAction() {
		long startTime = System.currentTimeMillis();
		
		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(" ==== ACTION "+ this.getTHInfo().getAction() +" Thread internal Id "+ this.getTHInfo().getId() +" Sys Thread Id "+ this.getTHInfo().getThId()+" started ===="  );}catch(StressToolConfigurationException e){}
	    for(int i = 0 ; i  < this.getLoops() -10; i++){
	    	long startRunTime = System.currentTimeMillis();
	    	try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(" ==== ACTION "+ this.getTHInfo().getAction() +" Thread internal Id "+ this.getTHInfo().getId() +" running "+ i );}catch(StressToolConfigurationException e){}
			try {
			    long startLatency = System.currentTimeMillis();
			    /**
			     * Db actions
			     */
			    Thread.sleep(Utility.getNumberFromRandomMinMax(100,500));
			    
			    long endLatency = System.currentTimeMillis();
			    this.getTHInfo().setLatency(endLatency-startLatency);
			    
			    Thread.sleep(Utility.getNumberFromRandomMinMax(1000,5000));
			} catch (InterruptedException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			long endRunTime = System.currentTimeMillis();
			this.getTHInfo().setExecutionTime(endRunTime - startRunTime);
			this.getTHInfo().setCurrentLoop(i);
	    }
	    long endTime = System.currentTimeMillis();
	    this.getTHInfo().setTotalEcecutionTime(endTime - startTime);
	    this.getTHInfo().setReady(ActionTHElement.SEMAPHORE_RED);
	    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(" ==== ACTION "+ this.getTHInfo().getAction() +" Thread internal Id "+ this.getTHInfo().getId() +" Sys Thread Id "+ this.getTHInfo().getThId()+" ended ===="  );}catch(StressToolConfigurationException e){}
	    
	
	}

}
