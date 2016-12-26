package net.tc.stresstool.actions;

import java.util.Map;

public interface ReadAction {
	public void setDataSelectorProvider(Map<String, ActionTable> dataProviderIn);
//    public abstract String setSelectMethod();
//    public abstract void getSelectMethod(String method);

    
    public abstract void setLazySelect(boolean lazy);
    public abstract boolean isLazySelect();
    public abstract void setLazyInterval(int interval);
    public abstract int getLazyInterval();

    public int getSleepRead();
	public void setSleepRead(int sleepRead);
	
//	public void setNumberOfprimaryTables(int tableNumbers);
//	public int getNumberOfprimaryTables();
//	public void setNumberOfSecondaryTables(int tableNumbers);
//	public int getNumberOfSecondaryTables();
	
	
	
}
