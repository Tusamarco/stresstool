package net.tc.stresstool.actions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import java.sql.Connection;

import net.tc.data.db.ConnectionProvider;
import net.tc.data.db.DbType;
import net.tc.data.db.Schema;
import net.tc.stresstool.PerformanceEvaluator;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.config.ConfigurationImplementation;
import net.tc.stresstool.config.Configurator;
import net.tc.stresstool.exceptions.StressToolActionException;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.ActionTHElement;
import net.tc.utils.Utility;

public class StressActionBase implements StressAction, Runnable {
    private Configurator config;
    private String tablePrimaryName = "tbTestPrimary";
    private String tableSecondaryName = "tbTestSecondary";
    private ConfigurationImplementation configuration = null;
    private Map connectionInformation = null;
    private DbType dbType = null; 
    private String dbTypeName = ""; 
    private long id = 0;
    private int currentLoop = 0;
    
    private boolean createTable=true ;
//    private dbType=MySQL; 
    private boolean doDelete=false; 
    private int batchSize = 0; 
    private int doBatch=1;
    private boolean doReport=true;
    private boolean doSimple=false ; 
    private boolean doSimplePk=false; 
    private boolean droptable=false;
    private boolean ignoreBinlog=false;
    private boolean operationShort=true;  
    private int poolNumber=10;
    private int pctInsert=100; 
    private int pctSelect=0; 
    private int pctDelete=0; 
    private int repeatNumber=100;
//    private int sleepFor=0;
//    private int sleepWrite=0;
//    private int sleepSelect=0; 
//    private int sleepDelete=0;
// table engine comma separated with % value; InnoDB|60,MyISAM|40 
    private String tableEngine= null;
    private boolean truncate=false;
    private String actionType = null;
    private int actionCode = 0;

    private boolean isActive = false;
    private ActionStatus actionStatus = null; 
    private long lastExecutionTime = 0;
    private long lastThinkTime = 0;    
    private CountDownLatch latch = null;
    private ActionTHElement thInfo = null;
    private ConnectionProvider connProvider = null;
    private Schema currentSchema = null;
    private boolean usePrepareStatement=false ;
    private int lazyLoopCounter = 50;
    private Connection ActiveConnection = null;
    private boolean stickyconnection = true;
    private boolean FKEnable = false;
    private int sleepingTimeNs =0 ;
    
//    public static String ACTION_TYPE_Select = "Select";
//    public static String ACTION_TYPE_Insert = "Insert";
//    public static String ACTION_TYPE_Delete = "Delete";
//    public static String ACTION_TYPE_Create = "Create";
//    public static String ACTION_TYPE_Drop = "Drop";
//    public static String ACTION_TYPE_Truncate = "Truncate";
    

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#ExecuteAction()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#ExecuteAction()
	 */
    @Override
	public void ExecuteAction() throws StressToolActionException {
    	
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getActionStatus()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getActionStatus()
	 */
    @Override
	public ActionStatus getActionStatus() {
	//TODO set the status before returning the object
	return actionStatus;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isCreatetable()
	 */
    @Override
	public boolean isCreatetable() {
        return createTable;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setCreatetable(boolean)
	 */
    @Override
	public void setCreatetable(boolean createTable) {
        this.createTable = createTable;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isDoDelete()
	 */
    @Override
	public boolean isDoDelete() {
        return doDelete;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDoDelete(boolean)
	 */
    @Override
	public void setDoDelete(boolean doDelete) {
        this.doDelete = doDelete;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getDoBatch()
	 */
    @Override
	public int getDoBatch() {
        return doBatch;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDoBatch(int)
	 */
    @Override
	public void setDoBatch(int doBatch) {
        this.doBatch = doBatch;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isDoReport()
	 */
    @Override
	public boolean isDoReport() {
        return doReport;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDoReport(boolean)
	 */
    @Override
	public void setDoReport(boolean doReport) {
        this.doReport = doReport;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isDoSimple()
	 */
    @Deprecated
    @Override
	public boolean isDoSimple() {
        return doSimple;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDoSimple(boolean)
	 */
    @Override
    @Deprecated
	public void setDoSimple(boolean doSimple) {
        this.doSimple = doSimple;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isDoSimplePk()
	 */
    @Override
    @Deprecated
	public boolean isDoSimplePk() {
        return doSimplePk;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDoSimplePk(boolean)
	 */
    @Override
    @Deprecated
	public void setDoSimplePk(boolean doSimplePk) {
        this.doSimplePk = doSimplePk;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isDroptable()
	 */
    @Override
	public boolean isDroptable() {
        return droptable;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDroptable(boolean)
	 */
    @Override
	public void setDroptable(boolean droptable) {
        this.droptable = droptable;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isIgnoreBinlog()
	 */
    @Override
	public boolean isIgnoreBinlog() {
        return ignoreBinlog;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setIgnoreBinlog(boolean)
	 */
    @Override
	public void setIgnoreBinlog(boolean ignoreBinlog) {
        this.ignoreBinlog = ignoreBinlog;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isOperationShort()
	 */
    @Override
    @Deprecated
	public boolean isOperationShort() {
        return operationShort;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setOperationShort(boolean)
	 */
    @Override
    @Deprecated
	public void setOperationShort(boolean operationShort) {
        this.operationShort = operationShort;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getPoolNumber()
	 */
    @Override
	public int getPoolNumber() {
        return poolNumber;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setPoolNumber(int)
	 */
    @Override
	public void setPoolNumber(int poolNumber) {
        this.poolNumber = poolNumber;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getPctInsert()
	 */
    @Override
	public int getPctInsert() {
        return pctInsert;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setPctInsert(int)
	 */
    @Override
	public void setPctInsert(int pctInsert) {
        this.pctInsert = pctInsert;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getPctSelect()
	 */
    @Override
	public int getPctSelect() {
        return pctSelect;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setPctSelect(int)
	 */
    @Override
	public void setPctSelect(int pctSelect) {
        this.pctSelect = pctSelect;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getPctDelete()
	 */
    @Override
	public int getPctDelete() {
        return pctDelete;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setPctDelete(int)
	 */
    @Override
	public void setPctDelete(int pctDelete) {
        this.pctDelete = pctDelete;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getRepeatNumber()
	 */
    @Override
	public int getRepeatNumber() {
        return repeatNumber;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setRepeatNumber(int)
	 */
    @Override
	public void setRepeatNumber(int repeatNumber) {
        this.repeatNumber = repeatNumber;
    }

//    /* (non-Javadoc)
//	 * @see net.tc.stresstool.actions.StressAction#getSleepFor()
//	 */
//    @Override
//	public int getSleepFor() {
//        return sleepFor;
//    }
//
//    /* (non-Javadoc)
//	 * @see net.tc.stresstool.actions.StressAction#setSleepFor(int)
//	 */
//    @Override
//	public void setSleepFor(int sleepFor) {
//        this.sleepFor = sleepFor;
//    }
//
//    /* (non-Javadoc)
//	 * @see net.tc.stresstool.actions.StressAction#getSleepWrite()
//	 */
//    @Override
//	public int getSleepWrite() {
//        return sleepWrite;
//    }
//
//    /* (non-Javadoc)
//	 * @see net.tc.stresstool.actions.StressAction#setSleepWrite(int)
//	 */
//    @Override
//	public void setSleepWrite(int sleepWrite) {
//        this.sleepWrite = sleepWrite;
//    }
//
//    /* (non-Javadoc)
//	 * @see net.tc.stresstool.actions.StressAction#getSleepSelect()
//	 */
//    @Override
//	public int getSleepSelect() {
//        return sleepSelect;
//    }
//
//    /* (non-Javadoc)
//	 * @see net.tc.stresstool.actions.StressAction#setSleepSelect(int)
//	 */
//    @Override
//	public void setSleepSelect(int sleepSelect) {
//        this.sleepSelect = sleepSelect;
//    }
//
//    /* (non-Javadoc)
//	 * @see net.tc.stresstool.actions.StressAction#getSleepDelete()
//	 */
//    @Override
//	public int getSleepDelete() {
//        return sleepDelete;
//    }
//
//    /* (non-Javadoc)
//	 * @see net.tc.stresstool.actions.StressAction#setSleepDelete(int)
//	 */
//    @Override
//	public void setSleepDelete(int sleepDelete) {
//        this.sleepDelete = sleepDelete;
//    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getTableEngine()
	 */
    @Override
	public String getTableEngine() {
        return tableEngine;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setTableEngine(java.lang.String)
	 */
    @Override
	public void setTableEngine(String tableEngine) {
        this.tableEngine = tableEngine;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isTruncate()
	 */
    @Override
	public boolean isTruncate() {
        return truncate;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setTruncate(boolean)
	 */
    @Override
	public void setTruncate(boolean truncate) {
        this.truncate = truncate;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getActionType()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getActionType()
	 */
    @Override
	public String getActionType() {
	
    	return this.actionType;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getClassName()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getClassName()
	 */
    @Override
	public String getClassName() {
    	
    	return this.getClass().getName();
    }

 
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getConfiguration()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getConfiguration()
	 */
    @Override
	public ConfigurationImplementation getConfiguration() {
	
    	return this.configuration;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getCurrentLoop()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getCurrentLoop()
	 */
    @Override
	public int getCurrentLoop() {
    	return currentLoop;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getId()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getId()
	 */
    @Override
	public long getId() {
    	return this.id;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLastExecutionTime()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLastExecutionTime()
	 */
    @Override
	public long getLastExecutionTime() {
    	return this.lastExecutionTime;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLastThinkTime()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLastThinkTime()
	 */
    @Override
	public long getLastThinkTime() {
    	return this.lastThinkTime;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLoops()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getLoops()
	 */
    @Override
	public int getLoops() {
    	return this.repeatNumber;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getSleepTime()
	 */
//    /* (non-Javadoc)
//	 * @see net.tc.stresstool.actions.StressAction#getSleepTime()
//	 */
//    @Override
//	public int getSleepTime() {
//    	return sleepFor;
//    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isActive()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#isActive()
	 */
    @Override
	public boolean isActive() {
    	return false;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#run()
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#run()
	 */
    @Override
	public void run() {
        	try {
        		
        		if (!ExecutePreliminaryAction())
        			 new StressToolException("Action failed to initialize");
       		
        	    latch.await();
        	    /**
        	     * run action loop here
        	     * the run action can be Override on each action class
        	     */

//        	    /*
//        	     * if db connection can be sticky is set here, otherwise at each EXECUTION implementing class must get connection 
//        	     * when running
//        	     */
//				if (isStickyconnection() && getActiveConnection() == null) {
//					try {
//						setActiveConnection(getConnProvider().getConnection());
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						try {
//							ByteArrayOutputStream baos = new ByteArrayOutputStream();
//							PrintStream ps = new PrintStream(baos);
//							e.printStackTrace(ps);
//							String s = new String(baos.toByteArray());
//							StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
//							System.exit(1);
//						} catch (Exception ex) {
//							ex.printStackTrace();
//						}
//	
//					}
//				}

        	    long startTime = System.nanoTime();
        	    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== ACTION "+ this.getTHInfo().getAction() +" Thread internal Id "+ this.getTHInfo().getId() +" Sys Thread Id "+ this.getTHInfo().getThId()+" started ===="  );}catch(StressToolConfigurationException e){}
        	    for(int i = 0 ; i  < this.getLoops(); i++){
            	    setSleepingTimeNs(0);
        	    	long startLatency = System.nanoTime();
        	    	long startRunTime=0;
        	    	long endRunTime = 0;
        	    	long connectionTimens=System.nanoTime();
        	    	this.getTHInfo().setCurrentLoop(i+1);
        	    	/*
            	     * if db connection can be sticky is set here, otherwise at each EXECUTION implementing class must get connection 
            	     * when running
            	     */
        	    	if (!isStickyconnection() || getActiveConnection() == null) {
    					try {
    						
    						setActiveConnection(getConnProvider().getConnection());
    						connectionTimens=(System.nanoTime() - connectionTimens);
    					} catch (SQLException e) {
    						// TODO Auto-generated catch block
    						try {
    							ByteArrayOutputStream baos = new ByteArrayOutputStream();
    							PrintStream ps = new PrintStream(baos);
    							e.printStackTrace(ps);
    							String s = new String(baos.toByteArray());
    							StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
    							System.exit(1);
    						} catch (Exception ex) {
    							ex.printStackTrace();
    						}
    	
    					}
    				}
        	    	else{
        	    		getActiveConnection();
        	    		connectionTimens=(System.nanoTime() - connectionTimens);
        	    	}
        	    	this.getTHInfo().setConnectionTime(connectionTimens);

        	    	try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== ACTION "+ this.getTHInfo().getAction() +" Thread internal Id "+ this.getTHInfo().getId() +" running "+ i );}catch(StressToolConfigurationException e){}
        			try {
        			    
        			    try {
        			    		startRunTime = System.nanoTime();
        			      		this.ExecuteAction();
        			      		endRunTime = System.nanoTime();
				    } catch (StressToolActionException e) {
					// TODO Auto-generated catch block
						try{					
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							PrintStream ps = new PrintStream(baos);				
							e.printStackTrace(ps);
							String s =new String(baos.toByteArray());
							StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
							System.exit(1)  ;
					}catch(Exception ex){ex.printStackTrace();}

				    }
        			    // for debug only Thread.sleep(Utility.getNumberFromRandomMinMax(10,500));
        			} catch (Exception e) {
        			    // TODO Auto-generated catch block
        				e.printStackTrace();
        				//        				try{
//        					String s =new String();
//        					PrintWriter pw = new PrintWriter(s);
//        					e.printStackTrace(pw);
//        					StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
//        			}catch(Exception exx){exx.printStackTrace();}
        				
        			}
        			finally{
 
        			}
        			
        			
        			this.getTHInfo().setExecutionTime(endRunTime - startRunTime);
        			this.getTHInfo().setTotalEcecutionTime(PerformanceEvaluator.getTimeEvaluationSec(startTime));
        			
        			StressTool.getThreadsInfo().put(this.getTHInfo().getId(), this.getTHInfo());

        			if(!isStickyconnection()){
            	    	getConnProvider().returnConnection(getActiveConnection());
            	    	setActiveConnection(null);
            	    }

        			
        			if(!StressTool.isStressToolRunning())
        			    break;

        			long endLatency = System.nanoTime();
    			    this.getTHInfo().setLatency(endLatency-startLatency);
    			    
    			    try {if (StressTool.getLogProvider().getLogger(LogProvider.LOG_PERFORMANCE).isInfoEnabled()) {
    			    	StressTool
						.getLogProvider()
						.getLogger(LogProvider.LOG_PERFORMANCE)
						.info(StressTool.getLogProvider().LOG_EXEC_TIME
							+ this.getTHInfo().getId() + " "
							+ " Thread Time (ns): "
							+ PerformanceEvaluator.getTimeEvaluationNs(startLatency));
    			    	}} catch (Throwable e1) {}
    			    
// TODO This can become a memory leak shit 
    			     /* 
    			     * I need to think about it before release it
    			     */
//    			    this.getTHInfo().getGetConnectionTime().add(connectionTimens);
    			    /*Performance evaluation section [header] start*/
    				try {
    				    if (StressTool.getLogProvider()
    					    .getLogger(LogProvider.LOG_PCONNECTION)
    					    .isInfoEnabled()) {

    					StressTool
    						.getLogProvider()
    						.getLogger(LogProvider.LOG_PCONNECTION)
    						.info(StressTool.getLogProvider().LOG_EXEC_TIME
    							+ " Connection Time (ns): "
    							+ connectionTimens
    							+ " ms: " + PerformanceEvaluator.getMSFromNano(connectionTimens)
    							);
    				    }
    				} catch (Throwable th) {
    				}
    				/*Performance evaluation section [header] END*/

            	    /*
            	     * sleeping beauty time
            	     */
            	    
            	    if(this.getSleepingTimeNs() > 0 )
            	    	goToSleep(this.getSleepingTimeNs());

    			    
        	    }
        	    
        	    
        	    this.getTHInfo().setBatchSize(this.getBatchSize());
        	    this.getTHInfo().setTotalEcecutionTime(PerformanceEvaluator.getTimeEvaluationSec(startTime));
        	    this.getTHInfo().setReady(ActionTHElement.SEMAPHORE_RED);
        	    
        	    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== ACTION "+ this.getTHInfo().getAction() +" Thread internal Id "+ this.getTHInfo().getId() +" Sys Thread Id "+ this.getTHInfo().getThId()+" ended ===="  );}catch(StressToolConfigurationException e){}


        	} catch (InterruptedException e) {
        	    // TODO Auto-generated catch block
				try{					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos);				
					e.printStackTrace(ps);
					String s =new String(baos.toByteArray());
					StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
					System.exit(1)  ;
			}catch(Exception ex){ex.printStackTrace();}


        	} catch (StressToolActionException e1) {
				// TODO Auto-generated catch block
				try{					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos);				
					e1.printStackTrace(ps);
					String s =new String(baos.toByteArray());
					StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
					System.exit(1)  ;
			}catch(Exception ex){ex.printStackTrace();}


			} 
        	
        	    
	
    }

    void goToSleep(int seepingNs) {
		/*
		 * 
		 *     Sleeping beauty time
		 *     	
		 */
    	
	  	try {
			Thread.sleep(seepingNs);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionTablePrimary(java.lang.String)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionTablePrimary(java.lang.String)
	 */
    @Override
	public void setActionTablePrimary(String tableName) {
	this.tablePrimaryName = tableName;

    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionTableSecondary(java.lang.String)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionTableSecondary(java.lang.String)
	 */
    @Override
	public void setActionTableSecondary(String tableName) {
	this.tableSecondaryName = tableName;
	
    }

    

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setConfiguration(net.tc.stresstool.config.ConfigurationImplementation)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setConfiguration(net.tc.stresstool.config.ConfigurationImplementation)
	 */
    @Override
	public void setConfiguration(ConfigurationImplementation configurationIn) {
	configuration = configurationIn;

    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setConnectionInformation(java.util.Map)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setConnectionInformation(java.util.Map)
	 */
    @Override
	public void setConnectionInformation(Map connectionInformationIn) {
	connectionInformation = connectionInformationIn;

    } 

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getConnectionInformation()
	 */
	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getConnectionInformation()
	 */
	@Override
	public final Map getConnectionInformation() {
        return connectionInformation;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getDbType()
	 */
	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#getDbType()
	 */
	@Override
	public  DbType getDbType() {
        return dbType;
    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDBType(net.tc.stresstool.DbType)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setDBType(net.tc.stresstool.DbType)
	 */
    @Override
	public void setdbType(String dbTypeIn) {
    	
    	dbType = new DbType(dbTypeIn);

    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setId(long)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setId(long)
	 */
    @Override
	public void setId(long idIn) {
	id = idIn ; 

    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setLoops(int)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setLoops(int)
	 */
    @Override
	public void setLoops(int loopsIn) {
    	this.setRepeatNumber(loopsIn); 

    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setSleepTime(int)
	 */
    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setSleepTime(int)
	 */
//    @Override
//	public void setSleepTime(int setSleepTime) {
//    	sleepFor = setSleepTime;
//	
//    }

    /* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionType(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see net.tc.stresstool.actions.StressAction#setActionType(java.lang.String)
	 */
	@Override
	public void setActionType(String actionType) {
        this.actionType = actionType;
    }

	/**
	 * @return the actionCode
	 */
	public int getActionCode() {
	    return actionCode;
	}

	/**
	 * @param actionCode the actionCode to set
	 */
	public void setActionCode(int actionCode) {
	    this.actionCode = actionCode;
	}

	/**
	 * @return the latch
	 */
	public CountDownLatch getLatch() {
	    return latch;
	}

	/**
	 * @param latch the latch to set
	 */
	public void setLatch(CountDownLatch latch) {
	    this.latch = latch;
	}

	@Override
	public void setTHInfo(ActionTHElement thInfo) {
	    this.thInfo = thInfo;
	    
	}

	@Override
	public ActionTHElement getTHInfo() {
	   
	    return thInfo;
	}

	/**
	 * @return the connProvider
	 */
	public ConnectionProvider getConnProvider() {
	    return connProvider;
	}

	/**
	 * @param connProvider the connProvider to set
	 */
	public void setConnProvider(ConnectionProvider connProvider) {
	    this.connProvider = connProvider;
	}

	@Override
	public Schema getSchema() {
	    
	    return this.currentSchema;
	}

	@Override
	public void setSchema(Schema currentSchema) {
	    this.currentSchema = currentSchema;
	    
	}

	/**
	 * @return the usePrepareStatement
	 */
	public boolean isUsePrepareStatement() {
	    return usePrepareStatement;
	}

	/**
	 * @param usePrepareStatement the usePrepareStatement to set
	 */
	public void setUsePrepareStatement(boolean usePrepareStatement) {
	    this.usePrepareStatement = usePrepareStatement;
	}

	/**
	 * @return the lazyLoopCounter
	 */
	public int getLazyLoopCounter() {
	    return lazyLoopCounter;
	}

	/**
	 * @param lazyLoopCounter the lazyLoopCounter to set
	 */
	public void setLazyLoopCounter(int lazyLoopCounter) {
	    this.lazyLoopCounter = lazyLoopCounter;
	}

	@Override
	public boolean ExecutePreliminaryAction() throws StressToolActionException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return the activeConnection
	 */
	public Connection getActiveConnection() {
	  return ActiveConnection;
	}

	/**
	 * @param activeConnection the activeConnection to set
	 */
	public void setActiveConnection(Connection activeConnection) {
	  ActiveConnection = activeConnection;
	}

	/**
	 * @return the stickyConnection
	 */
	public boolean isStickyconnection() {
	  return stickyconnection;
	}

	/**
	 * @param stickyConnection the stickyConnection to set
	 */
	public void setStickyconnection(boolean stickyConnection) {
	  this.stickyconnection = stickyConnection;
	}

	/**
	 * @return the fKEnable
	 */
	public boolean isFKEnable() {
	  return FKEnable;
	}

	/**
	 * @param fKEnable the fKEnable to set
	 */
	public void setFKEnable(boolean fKEnable) {
	  FKEnable = fKEnable;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public String getDbTypeName() {
		return dbTypeName;
	}
	 @Override
	public void setDbTypeName(String dbTypeName) {
		this.dbTypeName = dbTypeName;
		this.setdbType(dbTypeName);
	}

	int getSleepingTimeNs() {
		return sleepingTimeNs;
	}

	void setSleepingTimeNs(int sleepingTimeNs) {
		this.sleepingTimeNs = sleepingTimeNs;
	}
	

}
