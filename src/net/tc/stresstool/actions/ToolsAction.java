package net.tc.stresstool.actions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

import net.tc.data.db.*;
import net.tc.stresstool.PerformanceEvaluator;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolActionException;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.ActionTHElement;
import net.tc.stresstool.value.MaxValuesProvider;

public class ToolsAction extends StressActionBase {
	private boolean collectMax = false;
	private int sleepTools = 1000;

	public boolean isCollectMax() {
		return collectMax;
	}

	public void setCollectMax(boolean collectMax) {
		this.collectMax = collectMax;
	}
	
	@Override
	public boolean ExecutePreliminaryAction() throws StressToolActionException {
    	if (!isStickyconnection() || getActiveConnection() == null) {
			try {
				
				setActiveConnection(getConnProvider().getConnection());
				
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
		
		
	   	try {
				if(this.getActiveConnection().isClosed())
					this.setActiveConnection(this.getConnProvider().getConnection());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	
	    	
	    	if(this.isCollectMax()) {
	    		
	    		if(MaxValuesProvider.isTablesEmpty()) {
	        		Schema thisschema =this.getSchema();
	        		Iterator it = thisschema.getTables().iterator();
	        		while (it.hasNext()) {
	        			Table table = thisschema.getTable((String) it.next());
	        			MaxValuesProvider.addTable(table);
	        		}
	        		MaxValuesProvider.prepareTables();
	    			try {
	    				Connection conn = MaxValuesProvider.setAttributeMaxValues(this.getActiveConnection());
		    			if(conn != null)
		    				this.getConnProvider().returnConnection(conn);
	    			}
	    			catch(SQLException ex) {
	    				ex.printStackTrace();
	    			}

	    		}
	    	}

	    	return false;
	}
	
    @Override
	public void run() {
        	try {
        		
        		if (!ExecutePreliminaryAction())
        			 new StressToolException("Action failed to initialize");
//        		getLatch().await();
        	    /**
        	     * run action loop here
        	     * the run action can be Override on each action class
        	     */

        	    
        	    long startTime = System.nanoTime();
        	    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== ACTION "+ this.getTHInfo().getAction() +" Thread internal Id "+ this.getTHInfo().getId() +" Sys Thread Id "+ this.getTHInfo().getThId()+" started ===="  );}catch(StressToolConfigurationException e){}
        	    for(int i = 0 ; i  < this.getLoops(); i++){
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
				    } catch (Exception e) {
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


    			    
        	    }
        	    this.getTHInfo().setBatchSize(this.getBatchSize());
        	    this.getTHInfo().setTotalEcecutionTime(PerformanceEvaluator.getTimeEvaluationSec(startTime));
        	    this.getTHInfo().setReady(ActionTHElement.SEMAPHORE_RED);
        	    
        	    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== ACTION "+ this.getTHInfo().getAction() +" Thread internal Id "+ this.getTHInfo().getId() +" Sys Thread Id "+ this.getTHInfo().getThId()+" ended ===="  );}catch(StressToolConfigurationException e){}


        	} catch (Exception e) {
        	    // TODO Auto-generated catch block
				try{					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos);				
					e.printStackTrace(ps);
					String s =new String(baos.toByteArray());
					StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
					System.exit(1)  ;
			}catch(Exception ex){ex.printStackTrace();}


        	} catch (Throwable e1) {
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

    @Override
	public void ExecuteAction() {
    	try {
			if(this.getActiveConnection().isClosed())
				this.setActiveConnection(this.getConnProvider().getConnection());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	
    	if(this.isCollectMax()) {
    		
    		if(MaxValuesProvider.isTablesEmpty()) {
        		Schema thisschema =this.getSchema();
        		Iterator it = thisschema.getTables().iterator();
        		while (it.hasNext()) {
        			Table table = thisschema.getTable((String) it.next());
        			MaxValuesProvider.addTable(table);
        		}
        		MaxValuesProvider.prepareTables();
    			try {
    				Connection conn = MaxValuesProvider.setAttributeMaxValues(this.getActiveConnection());
	    			if(conn != null)
	    				this.getConnProvider().returnConnection(conn);
    			}
    			catch(SQLException ex) {
    				ex.printStackTrace();
    			}

    		}
    		else {
    			
    			try {
    				Connection conn = MaxValuesProvider.setAttributeMaxValues(this.getActiveConnection());
//	    			if(conn != null)
//	    				this.getConnProvider().returnConnection(conn);
    			}
    			catch(SQLException ex) {
    				ex.printStackTrace();
    			}
    		}
    	}
    	
		/*
		 *     Sleeping beauty time
		 */
    	this.goToSleep(this.getSleepTools());
    	
    }
	
	public int getSleepTools() {
		return sleepTools;
	}

	public void setSleepTools(int sleepTools) {
		this.sleepTools = sleepTools;
	}
    
}

	
