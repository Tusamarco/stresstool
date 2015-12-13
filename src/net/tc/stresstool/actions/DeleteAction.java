package net.tc.stresstool.actions;

import java.util.Map;

import net.tc.stresstool.statistics.ActionTHElement;

public interface DeleteAction {

    /**
	 */
	public void setDataSelectorProvider(Map<String, ActionTable> dataProviderIn);

    /**
		 */
    public abstract void setInterval(int interval);

    /**
			 */
    public abstract void setActionRange(int range);
    public ActionTHElement getTHInfo();
    public void setTHInfo(ActionTHElement thInfo);
}
