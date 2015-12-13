package net.tc.stresstool.actions;

import net.tc.stresstool.statistics.ActionTHElement;

public interface UpdateAction {
    public abstract void setUseAutoIncrement(boolean aoutoincrement);
    public abstract boolean isUseAutoIncrement();
//    public abstract boolean getuseAutoincrement();
    public abstract ActionTable getInsertValueProvider();
    public abstract void setLazyness(boolean lazy);
    public abstract boolean islazyCreation();
    public abstract void setLazyInterval(int interval);
    public abstract int getLazyInterval();
	public int getSleepWrite();
	public void setSleepWrite(int sleepWrite);
	
	public void setNumberOfprimaryTables(int tableNumbers);
	public int getNumberOfprimaryTables();
	public void setNumberOfSecondaryTables(int tableNumbers);
	public int getNumberOfSecondaryTables();

	public void setBatchSize(int batchSize);
	public int getBatchSize();
	public ActionTHElement getTHInfo();
	public void setTHInfo(ActionTHElement thInfo);
}
