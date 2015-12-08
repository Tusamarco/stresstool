package net.tc.stresstool.actions;

import java.util.Map;

import net.tc.stresstool.DbType;
import net.tc.stresstool.config.ConfigurationImplementation;
import net.tc.stresstool.config.Configurator;

public class StressActionBase implements StressAction, Runnable {
    private Configurator config;
    private String tablePrimaryName = "tbTestPrimary";
    private String tableSecondaryName = "tbTestSecondary";
    private ConfigurationImplementation configuration = null;
    private Map connectionInformation = null;
    private DbType dbType = null; 
    private long id = 0;
    private int currentLoop = 0;
    
    private boolean createTable=true ;
//    private dbType=MySQL; 
    private boolean doDelete=false; 
    private int doBatch=30;
    private boolean doReport=true;
    private boolean doSimple=false ; 
    private boolean doSimplePk=false; 
    private boolean droptable=false;
    private boolean ignoreBinlog=false;
    private boolean operationShort=true;  
    private int poolNumber=10;
    private int pctInsert=100; 
    private int pctSelect=0; 
    private int pctDelete=0; 
    private int repeatNumber=100;
    private int sleepFor=0;
    private int sleepWrite=0;
    private int sleepSelect=0; 
    private int sleepDelete=0;
// table engine comma separated with % value; InnoDB|60,MyISAM|40 
    private String tableEngine= null;
    private boolean truncate=false;
    private String actionType = null;
    private int actionCode = 0;

    private boolean isActive = false;
    private ActionStatus actionStatus = null; 
    private long lastExecutionTime = 0;
    private long lastThinkTime = 0;    
    
//    public static String ACTION_TYPE_Select = "Select";
//    public static String ACTION_TYPE_Insert = "Insert";
//    public static String ACTION_TYPE_Delete = "Delete";
//    public static String ACTION_TYPE_Create = "Create";
//    public static String ACTION_TYPE_Drop = "Drop";
//    public static String ACTION_TYPE_Truncate = "Truncate";
    
    public StressActionBase() {
	super();
	
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#ExecuteAction()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#ExecuteAction()
	 */
    @Override
	public void ExecuteAction() {
    	this.run();
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getActionStatus()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getActionStatus()
	 */
    @Override
	public ActionStatus getActionStatus() {
	//TODO set the status before returning the object
	return actionStatus;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isCreatetable()
	 */
    @Override
	public boolean isCreatetable() {
        return createTable;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setCreatetable(boolean)
	 */
    @Override
	public void setCreatetable(boolean createTable) {
        this.createTable = createTable;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isDoDelete()
	 */
    @Override
	public boolean isDoDelete() {
        return doDelete;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDoDelete(boolean)
	 */
    @Override
	public void setDoDelete(boolean doDelete) {
        this.doDelete = doDelete;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getDoBatch()
	 */
    @Override
	public int getDoBatch() {
        return doBatch;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDoBatch(int)
	 */
    @Override
	public void setDoBatch(int doBatch) {
        this.doBatch = doBatch;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isDoReport()
	 */
    @Override
	public boolean isDoReport() {
        return doReport;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDoReport(boolean)
	 */
    @Override
	public void setDoReport(boolean doReport) {
        this.doReport = doReport;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isDoSimple()
	 */
    @Override
	public boolean isDoSimple() {
        return doSimple;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDoSimple(boolean)
	 */
    @Override
	public void setDoSimple(boolean doSimple) {
        this.doSimple = doSimple;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isDoSimplePk()
	 */
    @Override
	public boolean isDoSimplePk() {
        return doSimplePk;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDoSimplePk(boolean)
	 */
    @Override
	public void setDoSimplePk(boolean doSimplePk) {
        this.doSimplePk = doSimplePk;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isDroptable()
	 */
    @Override
	public boolean isDroptable() {
        return droptable;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDroptable(boolean)
	 */
    @Override
	public void setDroptable(boolean droptable) {
        this.droptable = droptable;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isIgnoreBinlog()
	 */
    @Override
	public boolean isIgnoreBinlog() {
        return ignoreBinlog;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setIgnoreBinlog(boolean)
	 */
    @Override
	public void setIgnoreBinlog(boolean ignoreBinlog) {
        this.ignoreBinlog = ignoreBinlog;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isOperationShort()
	 */
    @Override
	public boolean isOperationShort() {
        return operationShort;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setOperationShort(boolean)
	 */
    @Override
	public void setOperationShort(boolean operationShort) {
        this.operationShort = operationShort;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getPoolNumber()
	 */
    @Override
	public int getPoolNumber() {
        return poolNumber;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setPoolNumber(int)
	 */
    @Override
	public void setPoolNumber(int poolNumber) {
        this.poolNumber = poolNumber;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getPctInsert()
	 */
    @Override
	public int getPctInsert() {
        return pctInsert;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setPctInsert(int)
	 */
    @Override
	public void setPctInsert(int pctInsert) {
        this.pctInsert = pctInsert;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getPctSelect()
	 */
    @Override
	public int getPctSelect() {
        return pctSelect;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setPctSelect(int)
	 */
    @Override
	public void setPctSelect(int pctSelect) {
        this.pctSelect = pctSelect;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getPctDelete()
	 */
    @Override
	public int getPctDelete() {
        return pctDelete;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setPctDelete(int)
	 */
    @Override
	public void setPctDelete(int pctDelete) {
        this.pctDelete = pctDelete;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getRepeatNumber()
	 */
    @Override
	public int getRepeatNumber() {
        return repeatNumber;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setRepeatNumber(int)
	 */
    @Override
	public void setRepeatNumber(int repeatNumber) {
        this.repeatNumber = repeatNumber;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getSleepFor()
	 */
    @Override
	public int getSleepFor() {
        return sleepFor;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setSleepFor(int)
	 */
    @Override
	public void setSleepFor(int sleepFor) {
        this.sleepFor = sleepFor;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getSleepWrite()
	 */
    @Override
	public int getSleepWrite() {
        return sleepWrite;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setSleepWrite(int)
	 */
    @Override
	public void setSleepWrite(int sleepWrite) {
        this.sleepWrite = sleepWrite;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getSleepSelect()
	 */
    @Override
	public int getSleepSelect() {
        return sleepSelect;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setSleepSelect(int)
	 */
    @Override
	public void setSleepSelect(int sleepSelect) {
        this.sleepSelect = sleepSelect;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getSleepDelete()
	 */
    @Override
	public int getSleepDelete() {
        return sleepDelete;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setSleepDelete(int)
	 */
    @Override
	public void setSleepDelete(int sleepDelete) {
        this.sleepDelete = sleepDelete;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getTableEngine()
	 */
    @Override
	public String getTableEngine() {
        return tableEngine;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setTableEngine(java.lang.String)
	 */
    @Override
	public void setTableEngine(String tableEngine) {
        this.tableEngine = tableEngine;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isTruncate()
	 */
    @Override
	public boolean isTruncate() {
        return truncate;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setTruncate(boolean)
	 */
    @Override
	public void setTruncate(boolean truncate) {
        this.truncate = truncate;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getActionType()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getActionType()
	 */
    @Override
	public String getActionType() {
	
    	return this.actionType;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getClassName()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getClassName()
	 */
    @Override
	public String getClassName() {
    	
    	return this.getClass().getName();
    }

 
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getConfiguration()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getConfiguration()
	 */
    @Override
	public ConfigurationImplementation getConfiguration() {
	
    	return this.configuration;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getCurrentLoop()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getCurrentLoop()
	 */
    @Override
	public int getCurrentLoop() {
    	return currentLoop;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getId()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getId()
	 */
    @Override
	public long getId() {
    	return this.id;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLastExecutionTime()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLastExecutionTime()
	 */
    @Override
	public long getLastExecutionTime() {
    	return this.lastExecutionTime;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLastThinkTime()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLastThinkTime()
	 */
    @Override
	public long getLastThinkTime() {
    	return this.lastThinkTime;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLoops()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLoops()
	 */
    @Override
	public int getLoops() {
    	return this.repeatNumber;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getSleepTime()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getSleepTime()
	 */
    @Override
	public int getSleepTime() {
    	return sleepFor;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isActive()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isActive()
	 */
    @Override
	public boolean isActive() {
    	return false;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#run()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#run()
	 */
    @Override
	public void run() {
	//TODO All action of the class goes here
	
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionTablePrimary(java.lang.String)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionTablePrimary(java.lang.String)
	 */
    @Override
	public void setActionTablePrimary(String tableName) {
	this.tablePrimaryName = tableName;

    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionTableSecondary(java.lang.String)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionTableSecondary(java.lang.String)
	 */
    @Override
	public void setActionTableSecondary(String tableName) {
	this.tableSecondaryName = tableName;
	
    }

    

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setConfiguration(net.tc.stresstool.config.ConfigurationImplementation)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setConfiguration(net.tc.stresstool.config.ConfigurationImplementation)
	 */
    @Override
	public void setConfiguration(ConfigurationImplementation configurationIn) {
	configuration = configurationIn;

    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setConnectionInformation(java.util.Map)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setConnectionInformation(java.util.Map)
	 */
    @Override
	public void setConnectionInformation(Map connectionInformationIn) {
	connectionInformation = connectionInformationIn;

    } 

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getConnectionInformation()
	 */
	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getConnectionInformation()
	 */
	@Override
	public final Map getConnectionInformation() {
        return connectionInformation;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getDbType()
	 */
	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getDbType()
	 */
	@Override
	public final DbType getDbType() {
        return dbType;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDBType(net.tc.stresstool.DbType)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDBType(net.tc.stresstool.DbType)
	 */
    @Override
	public void setDBType(DbType dbTypeIn) {
	dbType = dbTypeIn;

    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setId(long)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setId(long)
	 */
    @Override
	public void setId(long idIn) {
	id = idIn ; 

    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setLoops(int)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setLoops(int)
	 */
    @Override
	public void setLoops(int loopsIn) {
    	this.setRepeatNumber(loopsIn); 

    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setSleepTime(int)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setSleepTime(int)
	 */
    @Override
	public void setSleepTime(int setSleepTime) {
    	sleepFor = setSleepTime;
	
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionType(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionType(java.lang.String)
	 */
	@Override
	public void setActionType(String actionType) {
        this.actionType = actionType;
    }

	/**
	 * @return the actionCode
	 */
	public int getActionCode() {
	    return actionCode;
	}

	/**
	 * @param actionCode the actionCode to set
	 */
	public void setActionCode(int actionCode) {
	    this.actionCode = actionCode;
	}

}
