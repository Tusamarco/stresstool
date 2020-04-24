package net.tc.stresstool.actions;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.tc.data.db.Attribute;
import net.tc.data.db.DbType;
import net.tc.data.db.Schema;
import net.tc.data.db.Table;
import net.tc.data.generic.DataObject;
import net.tc.data.generic.SQLObject;
import net.tc.jsonparser.StructureDefinitionParser;
import net.tc.jsonparser.StructureDefinitionParserMySQL;
import net.tc.jsonparser.StructureDefinitionParserOracle;
import net.tc.jsonparser.StructureDefinitionParserPostgres;
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
//    private int batchSize = 0; 
    private String jsonFile = "";
    private String tableEngine = "InnoDB";
    
    private DataObject myDataObject =null;
    
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
	public void TruncateTables(Table[] tables) {
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
        		for(Table tb : tables){
        		    String drop = "TRUNCATE TABLE  "+tb.getSchemaName() +"." + tb.getName() + " ;" ;
        		    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(drop);}catch(StressToolConfigurationException e){}
        		    stmt.execute(drop);
        		}
		    }
		    conn.close();
		    conn = null;
		} catch (SQLException e1) {
			try{					
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);				
				e1.printStackTrace(ps);
				String s =new String(baos.toByteArray());
				StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
				System.exit(1)  ;
		}catch(Exception ex){ex.printStackTrace();}

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

	    Map tableInstances = new SynchronizedMap(0);
		tableInstances.put(Table.TABLE_PARENT, getNumberOfprimaryTables());
		tableInstances.put(Table.TABLE_CHILD, getNumberOfSecondaryTables());			

	    	
	    	try{

			if(this.getJsonFile() == null){
				StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn("Table structure definition from Json file is null for Insert Class");
				StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn("check parameter : jsonfile");
				return null;
			}

			StringBuffer sbCreate = new StringBuffer();
			
			
			JSONParser parser = new JSONParser();
			StructureDefinitionParser strParser = null;
			/**
			 * NOTE common place used to switch by DB type.
			 * Need to make it more obj oriented though 
			 */
			switch(this.getDbType().getName().toLowerCase()){
				case "mysql":    strParser = new StructureDefinitionParserMySQL();    break;
				case "postgres": strParser = new StructureDefinitionParserPostgres();    break;
				case "oracle": strParser = new StructureDefinitionParserOracle();    break;
			    default:         strParser = new StructureDefinitionParserMySQL();    break;
			
			}
			
			FileReader fr = new FileReader(this.getJsonFile());
			schema = strParser.parseSchema(parser, fr,tableInstances);

			
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
//				    stmt.executeQuery(sbCreate.toString());
				    String[] sqlAr = sbCreate.toString().replaceAll("\n", "").split(";");
				    for (String sql : sqlAr){
				    	
				    if(sql.length() >2){
				    		sql=sql.replace('@', ';');

				    		if(this.getDbType().getName().toLowerCase().equals("oracle")) {
				    			try {
				    				stmt.execute(sql);
				    			}catch(Throwable tw) {
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									PrintStream ps = new PrintStream(baos);	
									tw.printStackTrace(ps);
									String s =new String(baos.toByteArray());
									StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(tw.getMessage());
				    			}
				    		}
				    		else {
					    		stmt.addBatch(sql);
				    		}
				    		
				    	}
				    }
				    try {StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug( sbCreate.toString());} catch (StressToolConfigurationException e) {}
				    if(!this.getDbType().getName().toLowerCase().equals("oracle")) {
				    	stmt.executeBatch();
				    }
				}
				conn.close();
				conn = null;
			    } catch (SQLException e1) {
					try{		
						e1.printStackTrace();
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						PrintStream ps = new PrintStream(baos);				
						e1.printStackTrace(ps);
						String s =new String(baos.toByteArray());
						StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
						System.exit(1)  ;
				}catch(Exception ex){ex.printStackTrace();}

			    }
			}			
				
		}
		catch(Exception e){
				try{					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos);				
					e.printStackTrace(ps);
					String s =new String(baos.toByteArray());
					StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
					System.exit(1)  ;
			}catch(Exception ex){ex.printStackTrace();}
		}
		finally{
		    return schema;
		}

		
		
	}
	@Override
	public boolean  DropSchema(Table[] tables) {
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
        		for(Table tb : tables){
        		    String drop = "DROP TABLE IF EXISTS "+ tb.getSchemaName()+"." + tb.getName() + " ;" ;
        		    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(drop);}catch(StressToolConfigurationException e){}
        		    stmt.execute(drop);
        		}
        		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(" ==== Drop Tables END ===="  );}catch(StressToolConfigurationException e){}
		    }
		    conn.close();
		    conn = null;
		} catch (SQLException e1) {
			try{					
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);				
				e1.printStackTrace(ps);
				String s =new String(baos.toByteArray());
				StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
				System.exit(1)  ;
		}catch(Exception ex){ex.printStackTrace();}

		}

		
	    }
	    
	    
	    return false;
		
	}
//	@Override
//	public void setBatchSize(int batchSize) {
//		this.batchSize = batchSize;
//		
//	}
//	@Override
//	public int getBatchSize() {
//		return this.batchSize;
//	}
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
	@Deprecated
	public String getTableEngine() {
	    return tableEngine;
	}
	/**
	 * @param tableEngine the tableEngine to set
	 */
	@Deprecated
	public void setTableEngine(String tableEngine) {
	    this.tableEngine = tableEngine;
	}
	
	@Override
	public void ExecuteAction() throws StressToolActionException {
    	/**
    	 * Db actions
    	 */
	  Connection conn = null;
	  if(this.getActiveConnection()==null){
//		try {
	      conn = this.getActiveConnection();//  this.getConnProvider().getConnection();
//        } catch (SQLException e) {
//			try{					
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				PrintStream ps = new PrintStream(baos);				
//				e.printStackTrace(ps);
//				String s =new String(baos.toByteArray());
//				StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
//				System.exit(1)  ;
//		}catch(Exception ex){ex.printStackTrace();}


//        }
	  }
	  else
		conn = this.getActiveConnection();
		
	  /*
	   * now run the show
	   */
	  	
//	  System.out.println("EXECUTINg ACTION A FOR ID " + this.getTHInfo().getId() + " Action code " + this.getActionCode());
	  this.myDataObject.executeSqlObject(this.getActionCode(),(java.sql.Connection) conn);
		
	  if(!this.isStickyconnection()){
		
		getConnProvider().returnConnection((java.sql.Connection)conn);
	  }
		/*
		 * Sleeping beauty
		 */
		this.setSleepingTimeNs(this.getSleepWrite());
	}
	
	/**
	 * this is actually executing what the action is suppose to do
	 * @throws StressToolActionException 
	 */
	@Override
	public boolean ExecutePreliminaryAction() {
	    /**
	     * Initialize the DataObject representing the SQL action
	     */
//	  System.out.println("EXECUTINg PRELIMINARY A FOR ID " + this.getTHInfo().getId());
	    try {
	      this.myDataObject =  inizializeDataObject(new DataObject());
        } catch (StressToolActionException e) {
			try{					
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);				
				e.printStackTrace(ps);
				String s =new String(baos.toByteArray());
				StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
				System.exit(1)  ;
		}catch(Exception ex){ex.printStackTrace();}


        }
     	if(this.myDataObject.isInizialized()){
//     	 System.out.println(myDataObject.getSqlObjects().keySet().toString());
//     	 Iterator it = myDataObject.getSqlObjects().keySet().iterator();
//     	 while(it.hasNext()){
//     	  SQLObject obj  = (SQLObject)myDataObject.getSqlObjects().get(it.next());
//     	 System.out.println( "SQL Commands " + obj.getSQLCommands().toString());
//     	 }
     	 
     	 
     	  return true;
     	}
	  // TODO I am here need to do the value generation using the Sql object and the Data Object  
	  
	    return false;  
	     
	}

    public DataObject inizializeDataObject(DataObject thisSQLObject)
	    throws StressToolActionException {
	thisSQLObject.setSqlType(DataObject.SQL_INSERT);
	thisSQLObject.setBatchLoopLimit(this.getBatchSize());
	thisSQLObject.setLazy(this.lazyCreation);
	thisSQLObject.setLazyInterval(lazyInterval);
	thisSQLObject.setCurrentRunLoop(-1);

	String onDuplicateKey = null;
	String filter = null;
	String sqlTemplate = DataObject.SQL_INSERT_TEMPLATE;
	SynchronizedMap SQLObjectContainer = new SynchronizedMap();


	/**
	 * navigate the schema object for tables then use each table to retrieve
	 * the list of attributes if a filter exists then it is apply and only
	 * the attributes matching it are evaluated on the base of the list of
	 * attributes retrieved the value for each will be generated
	 * 
	 * If batch insert is active the list of values will be product on that
	 * base Also lazy insert may be activated, as such the value creation
	 * for some fields will have repeated value.
	 * 
	 * From lazy insert PK UK TIMESTAMP are excluded, all of them always
	 * contains new values.
	 */

	
	thisSQLObject.setTables((Table[]) getTables());

//	StringBuffer sbTables = new StringBuffer();
	StringBuffer sbAttribs = new StringBuffer();
	StringBuffer sbValues = new StringBuffer();

	sbValues.append("(");
	// TODO from here this must become a method for the Dataobject
/*
 * the SQLObjectContainer will be used to store the SQLObject referencing to each table<>SQL statement 
 * SQLObject has the a counter with the number of execution done (lazyExecCount), that will be used to refresh the 
 * values in accordance to the lazyInterval value 
 */
	for (Object inTable : thisSQLObject.getTables()) {
	    if(sbAttribs.length() > 0) sbAttribs.delete(0, sbAttribs.length());

	    if(((Table)inTable).isReadOnly())
	    	continue;
	     
	    Table table = ((Table) inTable);
	    
	    int iNumTables = 0;

	    thisSQLObject.setAttribs(getAttribs((Table) table, filter));

	    for (int iAttrib = 0; iAttrib < thisSQLObject.getAttribs().length; iAttrib++) {
		if (iAttrib > 0 && sbAttribs.length() >2)
		    sbAttribs.append(",");
		if(!((Attribute) thisSQLObject.getAttribs()[iAttrib]).isAutoIncrement())
			sbAttribs.append(((Attribute) thisSQLObject.getAttribs()[iAttrib]).getName());
		
	    }

//TODO this section is not used anymore [START]
	    if (((Table) table).getParentTable() == null) {
		iNumTables = this.getNumberOfprimaryTables();
	    } else {
		iNumTables = this.getNumberOfSecondaryTables();
	    }
//TODO  [END]
	    
		SQLObject lSQLObj = new SQLObject();
		lSQLObj.setBatched(this.getBatchSize() > 1 ? true : false);
		lSQLObj.setPreparedStatment(false);
		lSQLObj.setSQLCommandType(DataObject.SQL_INSERT);
		lSQLObj.setBatchLoops(this.getBatchSize());
		lSQLObj.addSourceTables((Table)table);
		
		
		String localSQLTemplate = sqlTemplate;
		if (((Table) table).getName() != null && ((Table) table).getName().length() > 0) {
		    localSQLTemplate = localSQLTemplate.replace("#TABLE#", ((Table) table).getName());
		} else {
		    throw new StressToolActionException(
			    "INSERT SQL SYNTAX ISSUE table name null");
		}

		if (sbAttribs != null && sbAttribs.length() > 0) {
		    localSQLTemplate = localSQLTemplate.replace("#ATTRIBS#", sbAttribs.toString());
		    table.setInsertAttributes(sbAttribs.toString());
		} else {
		    throw new StressToolActionException(
			    "INSERT SQL SYNTAX ISSUE attribs names not valid or Null");
		}
		if (onDuplicateKey == null) {
		    localSQLTemplate = localSQLTemplate.replace("#ON DUPLICATE KEY#", "");
		} else {
		    localSQLTemplate = localSQLTemplate.replace("#ON DUPLICATE KEY#", onDuplicateKey);
		}
		
		lSQLObj.setSqlLocalTemplate(localSQLTemplate);
		// TODO here
			lSQLObj.setResetLazy(true);
			if(!lSQLObj.getValues().equals("")) 
				SQLObjectContainer.put(((Table) table).getName(), lSQLObj);
				
	    
	}
	// if(sbValues !=null && sbValues.length()> 0){
	// sqlTemplate.replace("#VALUES#", sbValues.toString());
	// }else{
	// throw new
	// StressToolActionException("INSERT SQL SYNTAX ISSUE values not valid or Null");
	// }

	thisSQLObject.setSQL((SynchronizedMap) SQLObjectContainer);
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
		   SynchronizedMap attr = table.getMetaAttributes();
		   Attribute[] attrR = new Attribute[attr.size()];
		   for(int i=0; i< attr.size();i++){
			   attrR[i]= (Attribute) attr.getValueByPosition(i);
		   }
		   
		   return attrR;
//	       return (Attribute[]) table.getMetaAttributes().getValuesAsArrayOrderByKey(new Attribute[table.getMetaAttributes().size()]);
//	    		   .values().toArray(new Attribute[table.getMetaAttributes().size()]);
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
