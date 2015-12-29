package net.tc.stresstool.actions;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.ActionTHElement;
import net.tc.utils.Utility;

public class DeleteBase extends StressActionBase implements DeleteAction{
	
    private boolean lazyDelete=true;
    private int lazyInterval=5000;
    private Map<String, ActionTable> dataProviders = null;
    private int deleteInterval = 1;
    private int deleteRange = 1;
    private int sleepDelete = 0;
    
    
	@Override
	public void setDataSelectorProvider(Map<String, ActionTable> dataProviderIn) {
		dataProviders = dataProviderIn;
		
	}
	@Override
	public void setInterval(int interval) {
		deleteInterval = interval;
		
	}
	@Override
	public void setActionRange(int range) {
		deleteRange = range;
		
	}
	public int getSleepDelete() {
		return sleepDelete;
	}
	public void setSleepDelete(int sleepDelete) {
		this.sleepDelete = sleepDelete;
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
