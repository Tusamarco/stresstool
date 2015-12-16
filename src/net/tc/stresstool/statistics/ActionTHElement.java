package net.tc.stresstool.statistics;
/*
 * This class contain information about the status of the thread
 * the information is reported at EACH action thread loop,
 * ID is incremental 
 * thID is the TH identifier (system)
 * 
 * If the last insert report the Thread as active
 * the endtime will be used to compute IF the Thread is hanging  
 */
/**
 * @author  tusa
 */
public class ActionTHElement {
    public static int SEMAPHORE_NOT_INITIALIZED = 100;
    public static int SEMAPHORE_GREEN = 1;
    public static int SEMAPHORE_YELLOW = 2;
    public static int SEMAPHORE_RED = 3;

    long id = 0;
    long thId =0 ;
    long startTime = 0;
    long endTime = 0;
    String action;
    int currentLoop = 0;
    long executionTime =0;
    long minExectime =0;
    long maxExectime = 0;
    long latency = 0;
    long minLatency = 0;
    long maxLatency = 0;
    int rowsProcessed=0;
    boolean isActive = false;
    int ready = SEMAPHORE_NOT_INITIALIZED;
    
    public ActionTHElement(long Id, boolean isActive, int ready) {
	this.id = Id;
	this.isActive = isActive;
	this.ready = ready;
    }

    public ActionTHElement() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @return    the thId
     * @uml.property  name="thId"
     */
    public  long getThId() {
        return thId;
    }

    /**
     * @param thId    the thId to set
     * @uml.property  name="thId"
     */
    public  void setThId(long thId) {
        this.thId = thId;
    }
    
    /**
     * @return    the id
     * @uml.property  name="id"
     */
    public  long getId() {
        return id;
    }

    /**
     * @param id    the id to set
     * @uml.property  name="id"
     */
    public  void setId(long id) {
        this.id = id;
    }

    /**
     * @return    the startTime
     * @uml.property  name="startTime"
     */
    public  long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime    the startTime to set
     * @uml.property  name="startTime"
     */
    public  void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return    the endTime
     * @uml.property  name="endTime"
     */
    public  long getEndTime() {
        return endTime;
    }

    /**
     * @param endTime    the endTime to set
     * @uml.property  name="endTime"
     */
    public  void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * @return    the action
     * @uml.property  name="action"
     */
    public  String getAction() {
        return action;
    }

    /**
     * @param action    the action to set
     * @uml.property  name="action"
     */
    public  void setAction(String action) {
        this.action = action;
    }

    /**
     * @return    the currentLoop
     * @uml.property  name="currentLoop"
     */
    public  int getCurrentLoop() {
        return currentLoop;
    }

    /**
     * @param currentLoop    the currentLoop to set
     * @uml.property  name="currentLoop"
     */
    public  void setCurrentLoop(int currentLoop) {
        this.currentLoop = currentLoop;
    }

    /**
     * @return    the executionTime
     * @uml.property  name="executionTime"
     */
    public  int getExecutionTime() {
        return executionTime;
    }

    /**
     * @param executionTime    the executionTime to set
     * @uml.property  name="executionTime"
     * 
     * set max min at the same time
     */
    public  void setExecutionTime(long executionTime) {
    	if(executionTime > this.getMaxExectime()){
    		this.maxExectime = executionTime;
    	}
    	else if(executionTime < this.getMinExectime()){
    			this.minExectime = executionTime;
    	}
        this.executionTime = executionTime;
    }

    /**
     * @return    the rowsProcessed
     * @uml.property  name="rowsProcessed"
     */
    public  int getRowsProcessed() {
        return rowsProcessed;
    }

    /**
     * @param rowsProcessed    the rowsProcessed to set
     * @uml.property  name="rowsProcessed"
     */
    public  void setRowsProcessed(int rowsProcessed) {
        this.rowsProcessed = rowsProcessed;
    }

    /**
     * @return    the isActive
     * @uml.property  name="isActive"
     */
    public  boolean isActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public  void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return    the isReady
     * @uml.property  name="isReady"
     */
    public  int getReady() {
        return ready;
    }

    /**
     * @param isReady the isReady to set
     */
    public  void setReady(int Ready) {
        this.ready = Ready;
    }

	/**
	 * @return the minExectime
	 */
	public synchronized int getMinExectime() {
		return minExectime;
	}

	/**
	 * @return the maxExectime
	 */
	public synchronized int getMaxExectime() {
		return maxExectime;
	}

	/**
	 * @return the latency
	 */
	public synchronized int getLatency() {
		return latency;
	}

	/**
	 * @return the minLatency
	 */
	public synchronized int getMinLatency() {
		return minLatency;
	}

	/**
	 * @return the maxLatency
	 */
	public synchronized int getMaxLatency() {
		return maxLatency;
	}

	/**
	 * @param latency the latency to set
	 * set the max min at the same time
	 */
	public synchronized void setLatency(long latency) {
		this.latency = latency;
    	if(latency > this.getMaxLatency()){
    		this.maxLatency = latency;
    	}
    	else if(latency < this.getMinLatency()){
    			this.minLatency = latency;
    	}

	}


}
