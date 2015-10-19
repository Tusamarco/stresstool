package net.tc.stresstool.actions;

public class ActionStatus {

    public ActionStatus() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @uml.property name="id"
     */
    private long id;

    /**
     * Getter of the property <tt>id</tt>
     * 
     * @return Returns the id.
     * @uml.property name="id"
     */
    public long getId() {
	return id;
    }

    /**
     * Setter of the property <tt>id</tt>
     * 
     * @param id
     *            The id to set.
     * @uml.property name="id"
     */
    public void setId(long id) {
	this.id = id;
    }

    /**
     * @uml.property name="currentLoop"
     */
    private String currentLoop;

    /**
     * Getter of the property <tt>currentLoop</tt>
     * 
     * @return Returns the currentLoop.
     * @uml.property name="currentLoop"
     */
    public String getCurrentLoop() {
	return currentLoop;
    }

    /**
     * Setter of the property <tt>currentLoop</tt>
     * 
     * @param currentLoop
     *            The currentLoop to set.
     * @uml.property name="currentLoop"
     */
    public void setCurrentLoop(String currentLoop) {
	this.currentLoop = currentLoop;
    }

    /**
    	 */
    public boolean isActive() {
	return false;
    }

    /**
     * @uml.property name="lastReponseTime"
     */
    private long lastReponseTime;

    /**
     * Getter of the property <tt>lastReponseTime</tt>
     * 
     * @return Returns the lastReponseTime.
     * @uml.property name="lastReponseTime"
     */
    public long getLastReponseTime() {
	return lastReponseTime;
    }

    /**
     * Setter of the property <tt>lastReponseTime</tt>
     * 
     * @param lastReponseTime
     *            The lastReponseTime to set.
     * @uml.property name="lastReponseTime"
     */
    public void setLastReponseTime(long lastReponseTime) {
	this.lastReponseTime = lastReponseTime;
    }

    /**
     * @uml.property name="averageResponseTime"
     */
    private long averageResponseTime;

    /**
     * Getter of the property <tt>averageResponseTime</tt>
     * 
     * @return Returns the averageResponseTime.
     * @uml.property name="averageResponseTime"
     */
    public long getAverageResponseTime() {
	return averageResponseTime;
    }

    /**
     * Setter of the property <tt>averageResponseTime</tt>
     * 
     * @param averageResponseTime
     *            The averageResponseTime to set.
     * @uml.property name="averageResponseTime"
     */
    public void setAverageResponseTime(long averageResponseTime) {
	this.averageResponseTime = averageResponseTime;
    }

    /**
     * @uml.property name="lastThinkingTime"
     */
    private boolean lastThinkingTime;

    /**
     * Getter of the property <tt>lastThinkingTime</tt>
     * 
     * @return Returns the lastThinkingTime.
     * @uml.property name="lastThinkingTime"
     */
    public boolean isLastThinkingTime() {
	return lastThinkingTime;
    }

    /**
     * Setter of the property <tt>lastThinkingTime</tt>
     * 
     * @param lastThinkingTime
     *            The lastThinkingTime to set.
     * @uml.property name="lastThinkingTime"
     */
    public void setLastThinkingTime(boolean lastThinkingTime) {
	this.lastThinkingTime = lastThinkingTime;
    }

    /**
     * @uml.property name="averageThinkingTime"
     */
    private long averageThinkingTime;

    /**
     * Getter of the property <tt>averageThinkingTime</tt>
     * 
     * @return Returns the averageThinkingTime.
     * @uml.property name="averageThinkingTime"
     */
    public long getAverageThinkingTime() {
	return averageThinkingTime;
    }

    /**
     * Setter of the property <tt>averageThinkingTime</tt>
     * 
     * @param averageThinkingTime
     *            The averageThinkingTime to set.
     * @uml.property name="averageThinkingTime"
     */
    public void setAverageThinkingTime(long averageThinkingTime) {
	this.averageThinkingTime = averageThinkingTime;
    }

    /**
     * @uml.property name="totalExecutionTime"
     */
    private long totalExecutionTime;

    /**
     * Getter of the property <tt>totalExecutionTime</tt>
     * 
     * @return Returns the totalExecutionTime.
     * @uml.property name="totalExecutionTime"
     */
    public long getTotalExecutionTime() {
	return totalExecutionTime;
    }

    /**
     * Setter of the property <tt>totalExecutionTime</tt>
     * 
     * @param totalExecutionTime
     *            The totalExecutionTime to set.
     * @uml.property name="totalExecutionTime"
     */
    public void setTotalExecutionTime(long totalExecutionTime) {
	this.totalExecutionTime = totalExecutionTime;
    }

    /**
     * @uml.property name="lastRowAffected"
     */
    private int lastRowAffected;

    /**
     * Getter of the property <tt>lastRowAffected</tt>
     * 
     * @return Returns the lastRowAffected.
     * @uml.property name="lastRowAffected"
     */
    public int getLastRowAffected() {
	return lastRowAffected;
    }

    /**
     * Setter of the property <tt>lastRowAffected</tt>
     * 
     * @param lastRowAffected
     *            The lastRowAffected to set.
     * @uml.property name="lastRowAffected"
     */
    public void setLastRowAffected(int lastRowAffected) {
	this.lastRowAffected = lastRowAffected;
    }

    /**
     * @uml.property name="averageRowsAffected"
     */
    private int averageRowsAffected;

    /**
     * Getter of the property <tt>averageRowsAffected</tt>
     * 
     * @return Returns the averageRowsAffected.
     * @uml.property name="averageRowsAffected"
     */
    public int getAverageRowsAffected() {
	return averageRowsAffected;
    }

    /**
     * Setter of the property <tt>averageRowsAffected</tt>
     * 
     * @param averageRowsAffected
     *            The averageRowsAffected to set.
     * @uml.property name="averageRowsAffected"
     */
    public void setAverageRowsAffected(int averageRowsAffected) {
	this.averageRowsAffected = averageRowsAffected;
    }

}
