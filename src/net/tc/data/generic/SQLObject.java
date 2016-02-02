package net.tc.data.generic;

import java.util.ArrayList;

import net.tc.data.db.*;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;


public class SQLObject {
    ArrayList<String> SQLCommands = new ArrayList();
    int SQLCommandType = 0;
    boolean isBatched = false;
    boolean isPreparedStatment = false;
    int lazyExecCount = 0;
    String sqlLocalTemplate = null;
    boolean resetLazy = false;
    int batchLoops = 1;
    
    ArrayList sourceTables = new ArrayList();
    /**
     * @return the sQLCommands
     */
    public ArrayList getSQLCommands() {
        return SQLCommands;
    }

    /**
     * @param sQLCommands the sQLCommands to set
     */
    public void setSQLSingleCommand(String SQLCommand) {
        SQLCommands.add(SQLCommand);
    }
    
    /**
     * @return the sQLCOmmandType
     */
    public int getSQLCommandType() {
        return SQLCommandType;
    }
    /**
     * @param sQLCOmmandType the sQLCOmmandType to set
     */
    public void setSQLCommandType(int sQLCOmmandType) {
        SQLCommandType = sQLCOmmandType;
    }
    /**
     * @return the isBatched
     */
    public boolean isBatched() {
        return isBatched;
    }
    /**
     * @param isBatched the isBatched to set
     */
    public void setBatched(boolean isBatched) {
        this.isBatched = isBatched;
    }
    /**
     * @return the isPreparedStatment
     */
    public boolean isPreparedStatment() {
        return isPreparedStatment;
    }
    /**
     * @param isPreparedStatment the isPreparedStatment to set
     */
    public void setPreparedStatment(boolean isPreparedStatment) {
        this.isPreparedStatment = isPreparedStatment;
    }
    /**
     * @return the sourceTables
     */
    public ArrayList getSourceTables() {
        return sourceTables;
    }
    /**
     * @param sourceTables the sourceTables to set
     */
    public void setSourceTables(ArrayList sourceTables) {
        this.sourceTables = sourceTables;
    }
    
    public void addSourceTables(Table table) {
        this.sourceTables.add(table);
    }
	public synchronized int getLazyExecCount() {
		return lazyExecCount;
	}
	public synchronized void setLazyExecCount(int lazyExecCount) {
		this.lazyExecCount = lazyExecCount;
	}
	public synchronized String getSqlLocalTemplate() {
		return sqlLocalTemplate;
	}
	public synchronized void setSqlLocalTemplate(String sqlLocalTemplate) {
		this.sqlLocalTemplate = sqlLocalTemplate;
	}
	/**
	 * this method is the one that will call the value provider to 
	 * fill the values
	 * looping if batch and passing the datatype 
	 * 
	 * If the attribute is lazy and the lazy counter is lower than the limit value the 
	 * attribs with lazy will have the same values than before
	 * 
	 * @return
	 */
  public String getValues() {

	for (Object table : this.getSourceTables()) {

	  SynchronizedMap Attribs = ((Table) table).getMetaAttributes();
	  try {
		StressTool
		    .getLogProvider()
		    .getLogger(LogProvider.LOG_ACTIONS)
		    .debug(
		        "========================== Processing Table " + ((Table) table).getName() + " ================ [Start]");
	  } catch (StressToolConfigurationException e) {}

	  /*
	   * loops cross batch loops
	   */
	  StringBuffer sqlValues = new StringBuffer();
	  for (int iBatch = 0; iBatch <= this.batchLoops; iBatch++) {
		StringBuffer singleSql = new StringBuffer();
		for (Object attrib : Attribs.getValuesAsArrayOrderByKey()) {
		  if (singleSql.length() > 1)
			singleSql.append(", ");
		  // TODO !!!HERE!!!
		  boolean filling = false;
		  if (this.resetLazy || !((Attribute) attrib).isLazy()) {
			((Attribute) attrib).setValue(StressTool.getValueProvider().provideValue(
					((Attribute) attrib).getDataType(), 
					Utility.getNumberFromRandom((System.currentTimeMillis()/10000))));
			filling = true;
		  }

		  try {StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(
				  (filling ? "" : "NOT")
			  			+ "Filling Attribute "
			            + ((Attribute) attrib).getName()
			            + " DataType: "
			            + DataType.getDataTypeStringByIdentifier(((Attribute) attrib).getDataType().getDataTypeId()) 
			            + " Value : " + ((Attribute)attrib).getValue()
			            + " Lazy = " + ((Attribute) attrib).isLazy());
		  } catch (StressToolConfigurationException e) {e.printStackTrace();}

		  StressTool.getValueProvider().getRandomNumber(1, 2); // HERE !!!;

		}
	  }
	  try {
		StressTool
		    .getLogProvider()
		    .getLogger(LogProvider.LOG_ACTIONS)
		    .debug(
		        "========================== Processing Table " + ((Table) table).getName() + " ================ [End]");
	  } catch (StressToolConfigurationException e) {e.printStackTrace(); }
	}
	return null;
  }
	public  boolean isResetLazy() {
		return this.resetLazy;
	}
	public  void setResetLazy(boolean resetLazy) {
		this.resetLazy = resetLazy;
	}
	public synchronized int getBatchLoops() {
		return batchLoops;
	}
	public synchronized void setBatchLoops(int batchLoops) {
		this.batchLoops = batchLoops;
	}
	public synchronized void setSQLCommands(ArrayList<String> sQLCommands) {
		SQLCommands = sQLCommands;
	}
    
}
