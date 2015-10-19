package net.tc.stresstool.actions;

import java.util.Map;

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
	@Override
	public void run(){
		
		
		
	}

}
