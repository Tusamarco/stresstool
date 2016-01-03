package net.tc.stresstool.actions;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import net.tc.data.db.Attribute;
import net.tc.data.db.Schema;
import net.tc.data.db.Table;
import net.tc.data.generic.DataObject;
import net.tc.jsonparser.StructureDefinitionParser;
import net.tc.jsonparser.StructureDefinitionParserMySQL;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolActionException;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;

import org.ini4j.jdk14.edu.emory.mathcs.backport.java.util.Arrays;
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
		    Connection conn = this.getConnProvider().getSimpleConnection();
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
	public Schema  CreateSchema(boolean createSchema) {
	    	Schema schema = null;
	    	try{

			if(this.getJsonFile() == null){
				StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn("Table structure definition from Json file is null for Insert Class");
				StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn("check parameter : jsonfile");
				return null;
			}

			StringBuffer sbCreate = new StringBuffer();

			
			
			JSONParser parser = new JSONParser();
			StructureDefinitionParser strParser = new StructureDefinitionParserMySQL();
			FileReader fr = new FileReader(this.getJsonFile());
			schema = strParser.parseSchema(parser, fr);

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
			if(schema != null 
				&& createSchema){
			    sbCreate.append(schema.deploySchema((Map)tableInstances));
			    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Schema Definition SQL = [\n" + sbCreate.toString() + "\n ]"  );}catch(StressToolConfigurationException e){}
			    try {
				Connection conn = getConnProvider().getSimpleConnection();
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
		    return schema;
		}

		
		
	}
	@Override
	public boolean  DropSchema(String[] tables) {
	    Map connectionMap = super.getConnectionInformation();
	    StringBuffer sbTableDrop = new StringBuffer();
	    
	    if(tables !=null
		    && tables.length > 0){
		
		try {
		    Connection conn = getConnProvider().getSimpleConnection();
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
	 * @throws StressToolActionException 
	 */
	@Override
	public void ExecuteAction() throws StressToolActionException {
        	/**
        	 * Db actions
        	 */
	    /**
	     * Initialize the DataObject representing the SQL action
	     */
	    DataObject thisSQLObject = new DataObject();
	     
	    thisSQLObject =  inizializeDataObject(thisSQLObject);
	    thisSQLObject.isInizialized();
	     
	}
	public DataObject inizializeDataObject(DataObject thisSQLObject)
		throws StressToolActionException {
	     thisSQLObject.setSqlType(DataObject.SQL_CREATE);
	     thisSQLObject.setBatchLoopLimit(this.getBatchSize());
	     thisSQLObject.setLazy(this.lazyCreation);
	     thisSQLObject.setLazyInterval(lazyInterval);
	     
	     String onDuplicateKey = null;
	     String filter =null;
	     String sqlTemplate = DataObject.SQL_INSERT_TEMPLATE;

	     
	     /**
	      * navigate the schema object for tables
	      * then use each table to retrieve the list of attributes
	      * if a filter exists then it is apply and only the attributes matching it are evaluated
	      * on the base of the list of attributes retrieved the value for each will be generated
	      * 
	      * If batch insert is active the list of values will be product on that base
	      * Also lazy insert may be activated, as such the value creation for some fields will have repeated value.
	      * 
	      * From lazy insert PK UK TIMESTAMP are excluded, all of them always contains new values.
	      */
	     
	     thisSQLObject.setTables((Table[])getTables());
	     
	     StringBuffer sbTables = new StringBuffer();
	     StringBuffer sbAttribs = new StringBuffer();
	     StringBuffer sbValues = new StringBuffer();
	     
	     
	     sbValues.append("(");
	     
	     for(Object table:thisSQLObject.getTables()){
		 if(sbTables.length()> 0 )
		     sbTables.append(",");
		
		    sbTables.append(((Table)table).getName());
        
        	    thisSQLObject.setAttribs(getAttribs((Table) table, filter));
        	    
    		for (int iAttrib = 0; iAttrib < thisSQLObject.getAttribs().length; iAttrib++) {
		    if (iAttrib > 0)
			sbAttribs.append(",");
			
		    sbAttribs.append(((Attribute) thisSQLObject.getAttribs()[iAttrib]).getName());
		}

        	    
//        	    sbAttribs.append("(");
//        	    for (thisSQLObject.setCurrentBatchLoop(0); 
//        		    thisSQLObject.getCurrentBatchLoop() <= thisSQLObject.getBatchLoopLimit(); 
//        		    thisSQLObject.setBatchLoopLimit(thisSQLObject.getCurrentBatchLoop()+ 1)
//        		    ) {
//        		if (thisSQLObject.getCurrentBatchLoop() > 0)
//        		    sbValues.append(",");
//        
//        		sbValues.append("(");
//        
//        		sbValues.append(")");
//        	    }     
//        	    sbAttribs.append(")");
	     }
//	     sbValues.append(")");
//	     "INSERT INTO #TABLE# (#ATTRIBS#) VALUES (#VALUES#) #ON DUPLICATE KEY#" ;
	     
	     if(sbTables != null && sbTables.length() > 0){
		 	sqlTemplate.replace("#TABLE#", sbTables.toString());
	     }
	     else{
		 throw new StressToolActionException("INSERT SQL SYNTAX ISSUE table name null");
	     }
	     
	     if(sbValues !=null && sbValues.length() > 0){
		 	sqlTemplate.replace("#ATTRIBS#", sbValues.toString());
	     }else{
		 throw new StressToolActionException("INSERT SQL SYNTAX ISSUE attribs names not valid or Null");
	     }
	     
//	     if(sbValues !=null && sbValues.length()> 0){
//		 	sqlTemplate.replace("#VALUES#", sbValues.toString());
//	     }else{
//		 throw new StressToolActionException("INSERT SQL SYNTAX ISSUE values not valid or Null");
//	     }
	     
	     if(onDuplicateKey == null){
		 	sqlTemplate.replace("#ON DUPLICATE KEY#","");
	     }else{
		 sqlTemplate.replace("#ON DUPLICATE KEY#",onDuplicateKey);
	     }
	     
	     
//	     thisSQLObject.setSQL(sqlTemplate);
	     thisSQLObject.setInizialized(true);
	     return thisSQLObject;
	}
	private String getValues(Attribute attribute) {
	    // TODO Auto-generated method stub
	    return null;
	}
	private Attribute[] getAttribs(Table table,String filter) {
	    // TODO Auto-generated method stub
	   if(table != null 
		   && table.getMetaAttributes()!=null
		   && table.getMetaAttributes().size() >0 ){
	       return (Attribute[]) table.getMetaAttributes().values().toArray(new Attribute[table.getMetaAttributes().size()]);
	   }
	    
	    return null;
	}
	private Object[] getTables() {
	    if(getSchema() !=null 
		   && getSchema().getTables() != null){
		
		return (Table[]) getSchema().getTables().values().toArray(new Table[getSchema().getTables().size()]);
	    }
	    
	    return null;
	}
	
	

}
