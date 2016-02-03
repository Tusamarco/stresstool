package net.tc.data.generic;

import net.tc.data.common.MultiLanguage;
import net.tc.data.db.*;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class DataObject extends MultiLanguage
{
    public static final String SQL_INSERT_TEMPLATE = "INSERT INTO #TABLE# (#ATTRIBS#) VALUES (#VALUES#) #ON DUPLICATE KEY#" ;
    public static final String SQL_UPDATE_TEMPLATE = "UPDATE #TABLE_CONDITION# SET #ATTRIB_VALUE# #WHERE# #ORDER_BY# #LIMIT#" ;
    public static final String SQL_DELETE_TEMPLATE = "DELETE FROM #TABLE_CONDITION# #WHERE# #ORDER_BY# #LIMIT#" ;
    public static final String SQL_SELECT_TEMPLATE = "SELECT FROM #TABLE_CONDITION# #WHERE# #GROUP_BY# #ORDER_BY# #LIMIT#" ;
 
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
	public int[] executeSqlObject(Connection conn){
	    try {
	      Statement stmt = conn.createStatement();
	      stmt.addBatch("START TRANSACTION");
		    if(SQLContainer != null ){
		      for(int ir = 0 ; ir < getSqlObjects().size(); ir++){
		    	SQLObject mySo = (SQLObject)getSqlObjects().getValueByPosition(ir);
		    	for(int iCo = 0 ; iCo < mySo.getSQLCommands().size(); iCo++){
		    	  String command = (String)(mySo.getSQLCommands().get(iCo)) ;
		    	  stmt.addBatch(command);
		    	  try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug("Add SQL to batch: " + command  );}catch(StressToolConfigurationException e){}
		    	  
		    	}
		      }
		    }
		    stmt.addBatch("COMMIT");
		    int[] rows = stmt.executeBatch();
	      
	      
        } catch (SQLException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
        }

	    
	    
	    return null;
	}

}
