package net.tc.stresstool.actions;

public class WriteValueProvider {

    /**
     * @uml.property name="actionTable"
     */
    private ActionTable actionTable;

    /**
     * Getter of the property <tt>actionTable</tt>
     * 
     * @return Returns the actionTable.
     * @uml.property name="actionTable"
     */
    public ActionTable getActionTable() {
	return actionTable;
    }

    /**
     * Setter of the property <tt>actionTable</tt>
     * 
     * @param actionTable
     *            The actionTable to set.
     * @uml.property name="actionTable"
     */
    public void setActionTable(ActionTable actionTable) {
	this.actionTable = actionTable;
    }

    /**
     * @uml.property name="lazy"
     */
    private boolean lazy;

    /**
     * Getter of the property <tt>lazy</tt>
     * 
     * @return Returns the lazy.
     * @uml.property name="lazy"
     */
    public boolean isLazy() {
	return lazy;
    }

    /**
     * Setter of the property <tt>lazy</tt>
     * 
     * @param lazy
     *            The lazy to set.
     * @uml.property name="lazy"
     */
    public void setLazy(boolean lazy) {
	this.lazy = lazy;
    }

    /**
     * @uml.property name="refresh"
     */
    private boolean refresh;

    /**
     * Getter of the property <tt>refresh</tt>
     * 
     * @return Returns the refresh.
     * @uml.property name="refresh"
     */
    public boolean isRefresh() {
	return refresh;
    }

    /**
     * Setter of the property <tt>refresh</tt>
     * 
     * @param refresh
     *            The refresh to set.
     * @uml.property name="refresh"
     */
    public void setRefresh(boolean refresh) {
	this.refresh = refresh;
    }

    /**
     * @uml.property name="batchSize"
     */
    private int batchSize;

    /**
     * Getter of the property <tt>batchSize</tt>
     * 
     * @return Returns the batchSize.
     * @uml.property name="batchSize"
     */
    public int getBatchSize() {
	return batchSize;
    }

    /**
     * Setter of the property <tt>batchSize</tt>
     * 
     * @param batchSize
     *            The batchSize to set.
     * @uml.property name="batchSize"
     */
    public void setBatchSize(int batchSize) {
	this.batchSize = batchSize;
    }

    /**
    	 */
    public boolean fillTable() {
	return false;
    }

    /**
		 */
    public String getSQL() {
	return "";
    }

}
