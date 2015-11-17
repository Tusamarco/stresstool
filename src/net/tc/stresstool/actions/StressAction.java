package net.tc.stresstool.actions;

import java.util.Map;

import net.tc.stresstool.DbType;
import net.tc.stresstool.config.ConfigurationImplementation;

public interface StressAction {
	 public static String ACTION_TYPE_Select = "Select";
	    public static String ACTION_TYPE_Insert = "Insert";
	    public static String ACTION_TYPE_Update = "Update";
	    public static String ACTION_TYPE_Delete = "Delete";
	    public static String ACTION_TYPE_Create = "Create";
	    public static String ACTION_TYPE_Drop = "Drop";
	    public static String ACTION_TYPE_Truncate = "Truncate";
	    
	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#ExecuteAction()
	 */
	public abstract void ExecuteAction();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getActionStatus()
	 */
	public abstract ActionStatus getActionStatus();

	/**
	 * @return the createtable
	 */
	public abstract boolean isCreatetable();

	/**
	 * @param createtable the createtable to set
	 */
	public abstract void setCreatetable(boolean createTable);

	/**
	 * @return the doDelete
	 */
	public abstract boolean isDoDelete();

	/**
	 * @param doDelete the doDelete to set
	 */
	public abstract void setDoDelete(boolean doDelete);

	/**
	 * @return the doBatch
	 */
	public abstract int getDoBatch();

	/**
	 * @param doBatch the doBatch to set
	 */
	public abstract void setDoBatch(int doBatch);

	/**
	 * @return the doReport
	 */
	public abstract boolean isDoReport();

	/**
	 * @param doReport the doReport to set
	 */
	public abstract void setDoReport(boolean doReport);

	/**
	 * @return the doSimple
	 */
	public abstract boolean isDoSimple();

	/**
	 * @param doSimple the doSimple to set
	 */
	public abstract void setDoSimple(boolean doSimple);

	/**
	 * @return the doSimplePk
	 */
	public abstract boolean isDoSimplePk();

	/**
	 * @param doSimplePk the doSimplePk to set
	 */
	public abstract void setDoSimplePk(boolean doSimplePk);

	/**
	 * @return the droptable
	 */
	public abstract boolean isDroptable();

	/**
	 * @param droptable the droptable to set
	 */
	public abstract void setDroptable(boolean droptable);

	/**
	 * @return the ignoreBinlog
	 */
	public abstract boolean isIgnoreBinlog();

	/**
	 * @param ignoreBinlog the ignoreBinlog to set
	 */
	public abstract void setIgnoreBinlog(boolean ignoreBinlog);

	/**
	 * @return the operationShort
	 */
	public abstract boolean isOperationShort();

	/**
	 * @param operationShort the operationShort to set
	 */
	public abstract void setOperationShort(boolean operationShort);

	/**
	 * @return the poolNumber
	 */
	public abstract int getPoolNumber();

	/**
	 * @param poolNumber the poolNumber to set
	 */
	public abstract void setPoolNumber(int poolNumber);

	/**
	 * @return the pctInsert
	 */
	public abstract int getPctInsert();

	/**
	 * @param pctInsert the pctInsert to set
	 */
	public abstract void setPctInsert(int pctInsert);

	/**
	 * @return the pctSelect
	 */
	public abstract int getPctSelect();

	/**
	 * @param pctSelect the pctSelect to set
	 */
	public abstract void setPctSelect(int pctSelect);

	/**
	 * @return the pctDelete
	 */
	public abstract int getPctDelete();

	/**
	 * @param pctDelete the pctDelete to set
	 */
	public abstract void setPctDelete(int pctDelete);

	/**
	 * @return the repeatNumber
	 */
	public abstract int getRepeatNumber();

	/**
	 * @param repeatNumber the repeatNumber to set
	 */
	public abstract void setRepeatNumber(int repeatNumber);

	/**
	 * @return the sleepFor
	 */
	public abstract int getSleepFor();

	/**
	 * @param sleepFor the sleepFor to set
	 */
	public abstract void setSleepFor(int sleepFor);

	/**
	 * @return the sleepWrite
	 */
	public abstract int getSleepWrite();

	/**
	 * @param sleepWrite the sleepWrite to set
	 */
	public abstract void setSleepWrite(int sleepWrite);

	/**
	 * @return the sleepSelect
	 */
	public abstract int getSleepSelect();

	/**
	 * @param sleepSelect the sleepSelect to set
	 */
	public abstract void setSleepSelect(int sleepSelect);

	/**
	 * @return the sleepDelete
	 */
	public abstract int getSleepDelete();

	/**
	 * @param sleepDelete the sleepDelete to set
	 */
	public abstract void setSleepDelete(int sleepDelete);

	/**
	 * @return the tableEngine
	 */
	public abstract String getTableEngine();

	/**
	 * @param tableEngine the tableEngine to set
	 */
	public abstract void setTableEngine(String tableEngine);

	/**
	 * @return the truncate
	 */
	public abstract boolean isTruncate();

	/**
	 * @param truncate the truncate to set
	 */
	public abstract void setTruncate(boolean truncate);

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getActionType()
	 */
	public abstract String getActionType();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getClassName()
	 */
	public abstract String getClassName();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getConfiguration()
	 */
	public abstract ConfigurationImplementation getConfiguration();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getCurrentLoop()
	 */
	public abstract int getCurrentLoop();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getId()
	 */
	public abstract long getId();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLastExecutionTime()
	 */
	public abstract long getLastExecutionTime();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLastThinkTime()
	 */
	public abstract long getLastThinkTime();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLoops()
	 */
	public abstract int getLoops();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getSleepTime()
	 */
	public abstract int getSleepTime();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isActive()
	 */
	public abstract boolean isActive();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#run()
	 */
	public abstract void run();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionTablePrimary(java.lang.String)
	 */
	public abstract void setActionTablePrimary(String tableName);

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionTableSecondary(java.lang.String)
	 */
	public abstract void setActionTableSecondary(String tableName);

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setConfiguration(net.tc.stresstool.config.ConfigurationImplementation)
	 */
	public abstract void setConfiguration(
			ConfigurationImplementation configurationIn);

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setConnectionInformation(java.util.Map)
	 */
	public abstract void setConnectionInformation(Map connectionInformationIn);

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getConnectionInformation()
	 */
	public abstract Map getConnectionInformation();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getDbType()
	 */
	public abstract DbType getDbType();

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDBType(net.tc.stresstool.DbType)
	 */
	public abstract void setDBType(DbType dbTypeIn);

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setId(long)
	 */
	public abstract void setId(long idIn);

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setLoops(int)
	 */
	public abstract void setLoops(int loopsIn);

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setSleepTime(int)
	 */
	public abstract void setSleepTime(int setSleepTime);

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionType(java.lang.String)
	 */
	public abstract void setActionType(String actionType);

}