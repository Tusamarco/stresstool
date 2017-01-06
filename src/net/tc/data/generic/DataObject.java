package net.tc.data.generic;

import net.tc.data.common.MultiLanguage;
import net.tc.data.db.*;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.actions.StressAction;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

public class DataObject extends MultiLanguage
{
    public static final String SQL_INSERT_TEMPLATE = "INSERT INTO #TABLE# (#ATTRIBS#) VALUES (#VALUES#) #ON DUPLICATE KEY#" ;
    public static final String SQL_UPDATE_TEMPLATE = "UPDATE #TABLE_CONDITION# SET #ATTRIB_VALUE# #WHERE# #ORDER_BY# #LIMIT#" ;
    public static final String SQL_DELETE_TEMPLATE = "DELETE FROM #TABLE_CONDITION# #WHERE# #ORDER_BY# #LIMIT#" ;
    public static final String SQL_SELECT_TEMPLATE = "SELECT #TABLE_CONDITION# #WHERE# #GROUP_BY# #ORDER_BY# #LIMIT#" ;
 
    public static final int SQL_INSERT = 1000;
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
		case StressAction.UPDATE_ID_CONST:return executeDML(conn);
		case StressAction.DELETE_ID_CONST:return executeDML(conn);
		
		
	  }
	  return null;
	  
	}

//	private int[] executeDelete(Connection conn) {
//		int[] lines = new int[1];
//		  if(this.getSqlObjects() != null  
//				  && this.getSqlObjects().getValueByPosition(0) != null){
//					SQLObject sqlu = (SQLObject) this.getSqlObjects().getValueByPosition(0);
//					sqlu.setSQLCommandType(SQL_DELETE);
//					try{
//						 Statement stmt = (Statement) conn.createStatement();
//						 try{StressTool.getLogProvider().getLogger(LogProvider.LOG_SQL).info((String)sqlu.getSQLCommands().get(0)  );}catch(StressToolConfigurationException e){}
//						 lines[0] = stmt.executeUpdate((String)sqlu.getSQLCommands().get(0));
//						 try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info("Execute SQL command(s) N lines modified= "+ lines[0]  +"  : " + (String)sqlu.getSQLCommands().get(0)  );}catch(StressToolConfigurationException e){}
//						 return lines;	
//						}catch(Exception ex)
//						{ex.printStackTrace();}
//		  
//		  }
//	  return lines;
//    }

	private int[] executeDML(Connection conn) {
		
		  if(this.getSqlObjects() != null  
				  && this.getSqlObjects().getValueByPosition(0) != null){
			  
			  SQLObject sqlu = (SQLObject) this.getSqlObjects().getValueByPosition(0);
			  int[] lines = new int[sqlu.getSQLCommands().size()];
			  String[] commands = new String[sqlu.getSQLCommands().size()];
			  try{
				  Statement stmt = (Statement) conn.createStatement();
			      stmt.addBatch("START TRANSACTION");
			      
				  for(int iCount = 0; iCount < sqlu.getSQLCommands().size();iCount++){
						 try{StressTool.getLogProvider().getLogger(LogProvider.LOG_SQL).info((String)sqlu.getSQLCommands().get(iCount) );}catch(StressToolConfigurationException e){}
						 commands[iCount] = (String)sqlu.getSQLCommands().get(iCount);
						 stmt.addBatch(commands[iCount]);
//						 lines[iCount] = stmt.executeUpdate((String)sqlu.getSQLCommands().get(iCount));
			  		}
	
				    stmt.addBatch("COMMIT");
				    lines = executeSQL(stmt);
				    
				    if(StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).isInfoEnabled()){
				    	for(int ic =0 ; ic < commands.length; ic++){
				    		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS)
				    			.info("Execute SQL command(s) N lines modified= "+ lines[ic]  
				    					+"  : " + commands[ic]  );
				    		}catch(Exception e){}
				    	}
				    }
				    stmt.clearBatch();
			  }catch(Exception ex)
				{	System.out.println(sqlu.getSQLCommands().get(0));
					ex.printStackTrace();
					
				}
			  return lines;
		  }
	  return new int[0];
    }

	private int[] executeSelect(Connection conn) {
	  if(this.getSqlObjects() != null  
		  && this.getSqlObjects().getValueByPosition(0) != null){
			
			SQLObject sqlo = (SQLObject) this.getSqlObjects().getValueByPosition(0);
			sqlo.setSQLCommandType(SQL_READ);
			ArrayList<String> commands = sqlo.getSQLCommands();
			int[] lines = new int[commands.size()];
			
			try{
			 Statement stmt = (Statement) conn.createStatement();
			for(int ac = 0 ; ac < commands.size(); ac++){
				try{StressTool.getLogProvider().getLogger(LogProvider.LOG_SQL).info(commands.get(ac)  );}catch(StressToolConfigurationException e){}	
			  ResultSet rs = null;
			  try{
				  rs = stmt.executeQuery(commands.get(ac));
				  rs.last();
				  lines[ac] = rs.getRow();
			  }catch(Exception mx){
				  System.out.println(commands.get(ac));mx.printStackTrace();
				  }
			  
			  try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info("Execute SQL command(s) Retrived Rows:"+lines[ac] +  "  : " + commands.get(ac)  );}catch(StressToolConfigurationException e){}	
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
	      
	      try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug("******************* RESET? "+ this.getLazyExecCount() + " ***********************" );}catch(StressToolConfigurationException e){}
	    	
	    	
	      Statement stmt = (Statement) conn.createStatement();
//	      stmt.execute("BEGIN");
	      stmt.addBatch("START TRANSACTION");
		    if(SQLContainer != null ){
		      for(int ir = 0 ; ir < getSqlObjects().size(); ir++){
		    	SQLObject mySo = (SQLObject)getSqlObjects().getValueByPosition(ir);
		    	mySo.setSQLCommandType(SQL_INSERT);

		    	/*
		    	 * Analyze and set lazy 
		    	 */
		    	setLazyExecCount((getLazyExecCount()+1));
		    	
		    	if(getLazyInterval() < getLazyExecCount()){
		    	  mySo.setResetLazy(true);
			      try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug("******************* RESET LAZY ***********************" );}catch(StressToolConfigurationException e){}
		    	}
		    	mySo.getValues();		    	
		    	
		    	if(mySo.isResetLazy()){
		    	  mySo.setResetLazy(false);
		    	}
		    	
		    	for(int iCo = 0 ; iCo < mySo.getSQLCommands().size(); iCo++){
		    	  String command = (String)(mySo.getSQLCommands().get(iCo)) ;

//		    	  stmt.execute(command);
		    	  stmt.addBatch(command);
		    	  try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info("Add SQL to batch: " + command  );}catch(StressToolConfigurationException e){}
//		    	  System.out.println("Add SQL to batch: " + command  );
		    	  try{StressTool.getLogProvider().getLogger(LogProvider.LOG_SQL).info( command  );}catch(StressToolConfigurationException e){}
		    	  
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
    		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info("Lock Found for thread = " + Thread.currentThread().getId() + " repeat N: " + lockRetry + " OF 3");}catch(StressToolConfigurationException e){}
		    
		    iLine = executeSQL(stmt);
		    try {
		    	iLine = executeSQL(stmt);
		    	Thread.sleep(500);
		    } catch (InterruptedException e) {
		    		try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).warn("Error from JDBC for thread = " + Thread.currentThread().getId() +" | " + sqle.getErrorCode() + " : " +sqle.getMessage()+ "\n Reducing the FLOW and try to recover transaction");}catch(StressToolConfigurationException ee){}
		    		Thread.sleep(500);
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
		    			Thread.sleep(500);
		    } catch (InterruptedException e) {
		    	try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).warn("Error from JDBC for thread = " + Thread.currentThread().getId() +" | " + sqle.getErrorCode() + " : " +sqle.getMessage()+ "\n Reducing the FLOW and try to recover transaction");}catch(StressToolConfigurationException ew){}
		    	Thread.sleep(500);
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
