package net.tc.data.generic;

import net.tc.data.common.MultiLanguage;
import net.tc.data.db.*;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.actions.StressAction;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;










import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class DataObject extends MultiLanguage
{
    public static final String SQL_INSERT_TEMPLATE = "INSERT INTO #TABLE# (#ATTRIBS#) VALUES (#VALUES#) #ON DUPLICATE KEY#" ;
    public static final String SQL_UPDATE_TEMPLATE = "UPDATE #TABLE_CONDITION# SET #ATTRIB_VALUE# #WHERE# #ORDER_BY# #LIMIT#" ;
    public static final String SQL_DELETE_TEMPLATE = "DELETE FROM #TABLE_CONDITION# #WHERE# #ORDER_BY# #LIMIT#" ;
    public static final String SQL_SELECT_TEMPLATE = "SELECT #TABLE_CONDITION# #WHERE# #GROUP_BY# #ORDER_BY# #LIMIT#" ;
 
    public static final int SQL_CREATE = 1000;
    public static final int SQL_READ   = 2000;
    public static final int SQL_UPDATE = 3000;
    public static final int SQL_DELETE = 4000;
    public static final int SQL_DDL = 5000;
    
    private Table[] tables = null;
    private Attribute[] attribs = null;
    private SynchronizedMap values = null;
    private boolean isInizialized = false;
    private int batchLoopLimit = 1;
    private int currentRunLoop = 0;
    private boolean isLazy = false;
    private int lazyInterval = 0;
    private int sqlType = 0;
    private SynchronizedMap SQLContainer = null;
	private int lockRetry;
	private int lazyExecCount = 0;
    
	public DataObject()
	{
	}

	/**
	 * @return the tables
	 */
	public Table[] getTables() {
	    return tables;
	}

	/**
	 * @param tables the tables to set
	 */
	public void setTables(Table[] tables) {
	    this.tables = tables;
	}

	/**
	 * @return the attribs
	 */
	public Attribute[] getAttribs() {
	    return attribs;
	}

	/**
	 * @param attribs the attribs to set
	 */
	public void setAttribs(Attribute[] attribs) {
	    this.attribs = attribs;
	}

	/**
	 * @return the SQLObject for that Table,
	 * In case of select the leading Table is the one in concatenated form with the joins.
	 * 
	 */
	public SynchronizedMap getSqlObjects() {
	    return SQLContainer;
	}
	
	/**
	 * Set the value in the SQL template and store them in a SQLObject each for Table parse
	 * If lazy is active then the refresh of the vaule will happen at the declared loop (LazyInterval) 
	 */

	/**
	 * @param values the values to set
	 */
	public void setValues(SynchronizedMap values) {
	    this.SQLContainer = values;
	}

	/**
	 * @return the isInizialized
	 */
	public boolean isInizialized() {
	    return isInizialized;
	}

	/**
	 * @param isInizialized the isInizialized to set
	 */
	public void setInizialized(boolean isInizialized) {
	    this.isInizialized = isInizialized;
	}

	/**
	 * @return the batchLoopLimit
	 */
	public int getBatchLoopLimit() {
	    return batchLoopLimit;
	}

	/**
	 * @param batchLoopLimit the batchLoopLimit to set
	 */
	public void setBatchLoopLimit(int batchLoopLimit) {
	    this.batchLoopLimit = batchLoopLimit;
	}

	/**
	 * @return the currentBatchLoop
	 */
	public int getCurrentRunLoop() {
	    return currentRunLoop;
	}

	/**
	 * @param currentBatchLoop the currentBatchLoop to set
	 */
	public void setCurrentRunLoop(int currentRunLoop) {
	    this.currentRunLoop = currentRunLoop;
	}

	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
	    return isLazy;
	}

	/**
	 * @param isLazy the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
	    this.isLazy = isLazy;
	}

	/**
	 * @return the lazyInterval
	 */
	public int getLazyInterval() {
	    return lazyInterval;
	}

	/**
	 * @param lazyInterval the lazyInterval to set
	 */
	public void setLazyInterval(int lazyInterval) {
	    this.lazyInterval = lazyInterval;
	}

	/**
	 * @return the sqlType
	 */
	public int getSqlType() {
	    return sqlType;
	}

	/**
	 * @param sqlType the sqlType to set
	 */
	public void setSqlType(int sqlType) {
	    this.sqlType = sqlType;
	}

	/**
	 * @return the sQL
	 */
	public SynchronizedMap getSQL() {
	    return SQLContainer;
	}

	/**
	 * Initialize and set the SQL Object
	 */
	public void setSQL(SynchronizedMap SQLContainer) {
	    this.SQLContainer = SQLContainer;
	}
	public int[] executeSqlObject(int actionType,Connection conn){
	  
	  switch (actionType){
		case StressAction.INSERT_ID_CONST:return executeInsert(conn); 
		case StressAction.SELECT_ID_CONST:return executeSelect(conn);
		case StressAction.UPDATE_ID_CONST:return executeUpdate(conn);
		case StressAction.DELETE_ID_CONST:return executeDelete(conn);
		
		
	  }
	  return null;
	  
	}

	private int[] executeDelete(Connection conn) {
	  // TODO Auto-generated method stub
	  return null;
    }

	private int[] executeUpdate(Connection conn) {
	  // TODO Auto-generated method stub
	  return null;
    }

	private int[] executeSelect(Connection conn) {
	  if(this.getSqlObjects() != null  
		  && this.getSqlObjects().getValueByPosition(0) != null){
			
			SQLObject sqlo = (SQLObject) this.getSqlObjects().getValueByPosition(0);
			ArrayList<String> commands = sqlo.getSQLCommands();
			int[] lines = new int[commands.size()];
			
			try{
			 Statement stmt = (Statement) conn.createStatement();
			for(int ac = 0 ; ac < commands.size(); ac++){
			  	
			  ResultSet rs = stmt.executeQuery(commands.get(ac));
			  lines[ac] = rs.getRow();
			  rs.close();
			  rs = null;
			}
			

            return lines;
			
			}catch(Exception ex)
			{ex.printStackTrace();}

	  }
	  
	  return null;
	}

	public int[] executeInsert(Connection conn) {
	  int[] rows = null;
	    try {
	      
//	      System.out.println("******************* RESET? "+ this.getLazyExecCount() + " ***********************");
	      
	      Statement stmt = (Statement) conn.createStatement();
//	      stmt.execute("BEGIN");
	      stmt.addBatch("START TRANSACTION");
		    if(SQLContainer != null ){
		      for(int ir = 0 ; ir < getSqlObjects().size(); ir++){
		    	SQLObject mySo = (SQLObject)getSqlObjects().getValueByPosition(ir);
		    	
		    	/*
		    	 * Analyze and set lazy 
		    	 */
		    	setLazyExecCount((getLazyExecCount()+1));
		    	
		    	if(getLazyInterval() < getLazyExecCount()){
		    	  mySo.setResetLazy(true);
//		    	  System.out.println("******************* RESET LAZY ***********************");
		    	}
		    	mySo.getValues();		    	
		    	
		    	if(mySo.isResetLazy()){
		    	  mySo.setResetLazy(false);
		    	}
		    	
		    	
		    	
		    	for(int iCo = 0 ; iCo < mySo.getSQLCommands().size(); iCo++){
		    	  String command = (String)(mySo.getSQLCommands().get(iCo)) ;

//		    	  stmt.execute(command);
		    	  stmt.addBatch(command);
//		    	  System.out.println("Add SQL to batch: " + command  );
		    	  try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug("Add SQL to batch: " + command  );}catch(StressToolConfigurationException e){}
		    	  
		    	}
		      }
		    }
		    stmt.addBatch("COMMIT");
		    rows = executeSQL(stmt);
//		    stmt.execute("COMMIT");
		    stmt.clearBatch();
	        
	      
        } catch (Exception e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
        }

	    
	    if(getLazyInterval() < getLazyExecCount()){
	      setLazyExecCount(0);
	    }
	    return rows;
    }

	public int[] executeSQL(Statement stmt) throws Exception {
	    int[] iLine= new int[0];
	    try{
      	    iLine = stmt.executeBatch();
      	    stmt.clearBatch();
	    }
	    catch(SQLException sqle){
		if((sqle.getErrorCode() == 1205) && lockRetry < 4){
		    lockRetry++;
    		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).warn("Lock Found for thread = " + Thread.currentThread().getId() + " repeat N: " + lockRetry + " OF 3");}catch(StressToolConfigurationException e){}
		    
		    iLine = executeSQL(stmt);
		    try {
		    	iLine = executeSQL(stmt);
		    	Thread.sleep(1000);
		    } catch (InterruptedException e) {
		    		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).warn("Error from JDBC for thread = " + Thread.currentThread().getId() +" | " + sqle.getErrorCode() + " : " +sqle.getMessage()+ "\n Reducing the FLOW and try to recover transaction");}catch(StressToolConfigurationException ee){}
		    		Thread.sleep(2000);
		    		executeSQL(stmt);

		    		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).warn("========= Reporting stack trace for debug =========");}catch(StressToolConfigurationException ew){}

		    		e.printStackTrace();
		    }
		}
		else if(sqle.getErrorCode() > 0 
				&& sqle.getErrorCode() != 1452
				&& sqle.getErrorCode() != 1205
				&& lockRetry < 4) {
		    	lockRetry++;

	    		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).warn("ERROR Found for thread = " + Thread.currentThread().getId() + " repeat N: " + lockRetry + " OF 3\n" + sqle.getLocalizedMessage());}catch(StressToolConfigurationException e){}
//		    	sqle.printStackTrace();
		    	
		    	try {
		    			iLine = executeSQL(stmt);
		    			Thread.sleep(1000);
		    } catch (InterruptedException e) {
		    	try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).warn("Error from JDBC for thread = " + Thread.currentThread().getId() +" | " + sqle.getErrorCode() + " : " +sqle.getMessage()+ "\n Reducing the FLOW and try to recover transaction");}catch(StressToolConfigurationException ew){}
		    	Thread.sleep(2000);
		    	executeSQL(stmt);
		    	// TODO Auto-generated catch block
	    		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).warn("========= Reporting stack trace for debug =========");}catch(StressToolConfigurationException ew){}
		    	e.printStackTrace();
		    }
	    	
	    }
		else if(sqle.getErrorCode() == 1452){
			return new int[0];
			//System.out.print("x");
		}
		else if(lockRetry >=4){
			stmt.clearBatch();
			try{stmt.execute("ROLLBACK");}catch(Exception eex){}
			lockRetry=0;
    		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).warn("Error from JDBC  for thread = " + Thread.currentThread().getId() +" | "  + sqle.getErrorCode() + " : " +sqle.getMessage() + " Aborting");}catch(StressToolConfigurationException ew){}
		    //throw new Exception(sqle);
			return iLine;
		}

		
	    }
	    catch (Exception ex){
	    	throw new Exception(ex);
	    	
	    }
	    
	    return iLine;
	}

	public boolean executeSQL(String command,Connection conn) throws Exception {
	    boolean done;
	    try{

	    		Statement stmt = (Statement) conn.createStatement();
	    		done = stmt.execute(command);
	    }
	    catch(SQLException sqle){
		if((sqle.getErrorCode() == 1205 ||sqle.getErrorCode() == 1213 ) && lockRetry < 4){
		    lockRetry++;
		    System.out.println("Lock Found for thread = " + Thread.currentThread().getId() + " repeat N: " + lockRetry + " OF 3");		    
		    done = executeSQL(command,conn);
		    try {
			Thread.sleep(1000);
		    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		    
		}
		else{
		    throw new Exception(sqle);
		}

		
	    }
	    catch (Exception ex){
		throw new Exception(ex);
	    }
	    return done;
	}

	/**
	 * @return the lazyExecCount
	 */
	private int getLazyExecCount() {
	  return lazyExecCount;
	}

	/**
	 * @param lazyExecCount the lazyExecCount to set
	 */
	private void setLazyExecCount(int lazyExecCount) {
	  this.lazyExecCount = lazyExecCount;
	}
	
}
