package net.tc.stresstool.actions;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.ActionTHElement;
import net.tc.utils.Utility;

public class SelectBase extends StressActionBase implements ReadAction{

	
	private int numberOfJoinTables=0;
	private String joinField = "";
	private boolean forceIndex = false;
	private String indexName= "";
	private int numberOfIntervalKeys =0 ;
	private String selectFilterMethod = "range"; // #range|in|match
	private int sleepSelect = 0; 

	private int numberOfprimaryTables=1;
	private int numberOfSecondaryTables=0;
	private boolean lazySelect=true;
	private int lazyInterval=5000;
	private String tableEngine = "InnODB";
	
    

    
    private Map<String, ActionTable> dataProviders = null;
    
    public int getNumberOfJoinTables() {
		return numberOfJoinTables;
	}
	public void setNumberOfJoinTables(int numberOfJoinTables) {
		this.numberOfJoinTables = numberOfJoinTables;
	}
	public String getJoinField() {
		return joinField;
	}
	public void setJoinField(String joinField) {
		this.joinField = joinField;
	}
	public boolean isForceIndex() {
		return forceIndex;
	}
	public void setForceIndex(boolean forceIndex) {
		this.forceIndex = forceIndex;
	}
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public int getNumberOfIntervalKeys() {
		return numberOfIntervalKeys;
	}
	public void setNumberOfIntervalKeys(int numberOfIntervalKeys) {
		this.numberOfIntervalKeys = numberOfIntervalKeys;
	}
	public String getSelectFilterMethod() {
		return selectFilterMethod;
	}
	public void setSelectFilterMethod(String selectFilterMethod) {
		this.selectFilterMethod = selectFilterMethod;
	}
	public int getSleepSelect() {
		return sleepSelect;
	}
	public void setSleepSelect(int sleepSelect) {
		this.sleepSelect = sleepSelect;
	}
	public int getNumberOfprimaryTables() {
		return numberOfprimaryTables;
	}
	public void setNumberOfprimaryTables(int numberOfprimaryTables) {
		this.numberOfprimaryTables = numberOfprimaryTables;
	}
	public int getNumberOfSecondaryTables() {
		return numberOfSecondaryTables;
	}
	public void setNumberOfSecondaryTables(int numberOfSecondaryTables) {
		this.numberOfSecondaryTables = numberOfSecondaryTables;
	}
	@Override
	public void setDataSelectorProvider(Map<String, ActionTable> dataProviderIn) {
		dataProviders =dataProviderIn;
		
	}
	@Override
	public void setLazyness(boolean lazy) {
		lazySelect = lazy;
		
	}
	@Override
	public boolean islazyCreation() {
		return lazySelect;
	}
	@Override
	public void setLazyInterval(int interval) {
		lazyInterval = interval;
		
	}
	@Override
	public int getLazyInterval() {
		return lazyInterval;
	}
	@Override
	public int getSleepRead() {
		return sleepSelect;
	}
	@Override
	public void setSleepRead(int sleepRead) {
		sleepSelect = sleepRead;
		
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
            	/**
            	 * Db actions
            	 */
            	try {
            	Thread.sleep(Utility.getNumberFromRandomMinMax(1, 50));
        		} catch (InterruptedException e) {
        		    // TODO Auto-generated catch block
        		    e.printStackTrace();
        		}

	    }


    
}
