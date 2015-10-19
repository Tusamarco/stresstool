package net.tc.stresstool.actions;

import java.util.Map;

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

}
