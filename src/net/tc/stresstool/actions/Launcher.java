package net.tc.stresstool.actions;

import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.beanutils.BeanUtils;

import net.tc.data.db.ConnectionProvider;
import net.tc.data.db.Schema;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.config.Configuration;
import net.tc.stresstool.config.Configurator;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.ActionTHElement;
import net.tc.stresstool.statistics.StatCollector;
import net.tc.stresstool.statistics.providers.StatsProvider;
import net.tc.stresstool.value.BasicFileValueProvider;
import net.tc.stresstool.value.BasicValueProvider;
import net.tc.stresstool.value.ValueProvider;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.TimeTools;
import net.tc.utils.file.FileHandler;


/**
 * @author tusa
 * v 1.1
 *This class is the main point from where all the actions are instantiate
 */

public class Launcher {
    private Configurator config;
    private Map connMapcoordinates =null;
    private String deleteClass = null;
    private String insertClass = null;
    private String updateClass = null;
    private String selectClass = null;
    private String valueProvider = null;
//    private WriteAction writeImplementation = null;
//    private WriteAction updateImplementation = null;
//    private ReadAction readImplementation = null;
//    private DeleteAction deleteImplementation = null;
    
    private SynchronizedMap writeImplementationMap = null;
    private SynchronizedMap  updateImplementationMap = null;
    private SynchronizedMap readImplementationMap = null;
    private SynchronizedMap deleteImplementationMap = null;
    
    private int poolNumber =10;
    float pctInsert = 100;
    float pctUpdate = 100;
    float pctSelect = 100;
    float pctDelete = 100;
    private boolean actionInitialized = false;
    private int  HardStopLimit=100;
    private boolean UseHardStop=false;
    private int StatIntervalMs=1000;
    private int StatLoops = 100 ;
    private int repeatNumber =0 ;
    private int SemaphoreCountdownTime = 10;
    private CountDownLatch latch = null;
    private int interactive =1 ; //# Interactive mode [0 no|1 console output|2 console output + not exit until question is answered ] default 1
    private ConnectionProvider connProvider = null;
    private Schema currentSchema = null;
    private Calendar testCalendar = null;
    private Date startDate = null;
    private Date endDate = null ;
    private int daysdiffernce = 0; 
    
    
    public Launcher(Configurator configIn) {
	if(configIn != null){
	    config = configIn;
	    try {
		connMapcoordinates = validatePermission(config.getConfiguration(Configurator.MAIN_SECTION_NAME, StressTool.class));
		init(config.getConfiguration(Configurator.MAIN_SECTION_NAME, StressTool.class));
	    } catch (StressToolException e) {
		// TODO Auto-generated catch block
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}

	    }
	    	    
	}
    }

    /**
     * Main class initialization with basic parameters and 
     * class references.
     * @param configuration
     */
    private void init(Configuration configuration){
    	this.poolNumber = Integer.parseInt((String) configuration.getParameter("poolNumber").getValue());
    	this.pctDelete = Integer.parseInt((String) configuration.getParameter("pctDelete").getValue());
    	this.pctInsert = Integer.parseInt((String) configuration.getParameter("pctInsert").getValue());
    	this.pctSelect = Integer.parseInt((String) configuration.getParameter("pctSelect").getValue());
    	this.pctUpdate = Integer.parseInt((String) configuration.getParameter("pctUpdate").getValue());
    	this.setHardStopLimit( Integer.parseInt((String) configuration.getParameter("HardStopLimit").getValue()));
    	this.setStatIntervalMs( Integer.parseInt((String) configuration.getParameter("StatIntervalMs").getValue()) );
    	this.setStatLoops(  Integer.parseInt((String) configuration.getParameter("StatLoops").getValue()) );
    	this.setUseHardStop(Boolean.parseBoolean((String) configuration.getParameter("UseHardStop").getValue()));
    	this.setRepeatNumber(Integer.parseInt((String) configuration.getParameter("repeatNumber").getValue()) );
    	this.setSemaphoreCountdownTime(Integer.parseInt((String) configuration.getParameter("SemaphoreCountdownTime").getValue()) );
    	this.setInteractive(Integer.parseInt((String) configuration.getParameter("interactive").getValue()));
    	this.setTestCalendar(TimeTools.getCalendar((String)configuration.getParameter("calendardate").getValue()));
    	this.setDaysdiffernce(Integer.parseInt((String) configuration.getParameter("daysdiffernce").getValue()));
    	this.setStartDate(this.getTestCalendar().getTime());
    	this.setEndDate(TimeTools.getCalendarFromCalendarDateAddDays(this.getTestCalendar(), this.getDaysdiffernce()));
    	
    	/**
    	 * Initialize the Launcher configuration related to the actions classes 
    	 */
    	try {
    	    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).info("LOADING Action Parameters");
    	    if(	
    		    config != null && 
    		    config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class) != null  
    		    ){
    			if(config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("InsertClass") != null &&
    				config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("InsertClass").getValue() != null){
    			    this.insertClass =(String) config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("InsertClass").getValue();
    			    
    			}
    			else{
    			    ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
    			    throw new StressToolConfigurationException(" Insert Class not define");
    			    
    			}
    
    			if(config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("UpdateClass") != null &&
    				config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("UpdateClass").getValue() != null){
    			    this.updateClass =(String) config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("UpdateClass").getValue();
    			    
    			}
    			else{
    			    ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
    			    throw new StressToolConfigurationException(" Insert Class not define");
    			    
    			}
    
    			
    			if(config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("SelectClass") != null &&
    				config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("SelectClass").getValue() != null){
    			    this.selectClass =(String) config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("SelectClass").getValue();
    			    
    			}
    			else{
    			    ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
    			    throw new StressToolConfigurationException(" Select Class not define");
    			    
    			}
    			
    			if(config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("DeleteClass") != null &&
    				config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("DeleteClass").getValue() != null){
    			    this.deleteClass =(String) config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("DeleteClass").getValue();
    			    
    			}
    			if(config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("ValueProvider") != null &&
    				config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("ValueProvider").getValue() != null){
    			    this.valueProvider =(String) config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter("ValueProvider").getValue();
    			    
    			}

    			else{
    			    ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
    			    throw new StressToolConfigurationException(" Delete Class not define");
    			    
    			}
    			
    
    	   }
	
	} 
	catch (StressToolConfigurationException e) {
	    // TODO Auto-generated catch block
		try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
		StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
	}catch(Exception xxxxx){}

	}
	catch (StressToolException e) {
	    // TODO Auto-generated catch block
		try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
		StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
	}catch(Exception xxxxx){}

	}

	
	
	
    }
    /**
    */
    public Map validatePermission(Configuration configuration) {
	
	
//        Map connMapcoordinates = new SynchronizedMap(0) ;
        boolean valid = false;
        String userName="";
        Map providers = new SynchronizedMap();
        
        
        this.setConnProvider(new ConnectionProvider(configuration));

	Iterator it = config.getSectionsName();
	
	String currentProviderClassName ="";
	while(it.hasNext()){
	    String sectionName = (String)it.next();
	    Configuration conf = null;
	    try {
		conf = config.getConfiguration(sectionName);

		if(conf != null 
			&& conf.getParameter(StatCollector.PROVIDER_PARAMETER_NAME) != null){

		    String cfv =(String) conf.getParameter(StatCollector.PROVIDER_PARAMETER_NAME).getValue();
		    String[] providerNames = cfv.split(",");
		    StatsProvider sp = null;
		    for(int isp =0 ; isp < providerNames.length;isp++){
			if(providerNames[isp] != null && !providerNames[isp].equals("") ){
			    currentProviderClassName = providerNames[isp];
        		    	sp = (StatsProvider)Class.forName(providerNames[isp].trim()).newInstance();
           		    	
			}
        		    valid = sp.validatePermissions(connProvider);
		    }
		}

	    } catch (StressToolException e) {
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}

	    } catch (InstantiationException e) {
		try {
		    throw new StressToolConfigurationException(e);
		} catch (StressToolConfigurationException e1) {
		    // TODO Auto-generated catch block
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e1.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}

		}
	    } catch (IllegalAccessException e) {
		try {
		    throw new StressToolConfigurationException(e);
		} catch (StressToolConfigurationException e1) {
		    // TODO Auto-generated catch block
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e1.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}

		}
	    } catch (ClassNotFoundException e) {
		try {
		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).error(ExceptionMessages.ERROR_CREATING_PROVIDER + 
		    	" Provider class: " + currentProviderClassName + " invalid or not yet implement;\n " +
		    			"Check the configuration and remove class declaration if Provider is not available.");
		} catch (StressToolConfigurationException e1) {
		    // TODO Auto-generated catch block
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e1.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}

		}
		try {
		    throw new StressToolConfigurationException(e);
		} catch (StressToolConfigurationException e1) {
		    // TODO Auto-generated catch block
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e1.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}

		}
	    }
	}
	if(valid){
	    return connMapcoordinates;
	}
	return null;
	
    }

    /**
     * This is the invocation to create the basic structure
     */
    public boolean CreateStructure() {
	       
        /**
         * Initialize a special class for Schema creation
         * this class will be then declared null after the action is complete
         * 
         */
        try{

            StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Special CLass for schema creation [start] ");
            StressActionBase sa = null;
            SoftReference sr = new SoftReference(sa = (StressActionBase)setParametersClassInsert());
            ((StressActionBase)sr.get()).setConnProvider(this.connProvider);
            ((StressActionBase)sr.get()).setActionType(StressAction.ACTION_TYPE_Insert);
            ((StressActionBase)sr.get()).setActionCode(StressAction.INSERT_ID_CONST);

            boolean createSchema = Boolean.parseBoolean((String) (config.getConfiguration(Configurator.MAIN_SECTION_NAME, StressTool.class).getParameter("createTable").getValue()));
	    this.currentSchema = ((CreateAction) ((StressActionBase)sr.get())).CreateSchema(createSchema);
	    sa = null;
	    sr = null;
	    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Special CLass for schema creation [end] ");
	    
	    return true;
    
	    
	    
        }catch(Exception ex1){				try{String s =new String();PrintWriter pw = new PrintWriter(s);ex1.printStackTrace(pw);
		StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
	}catch(Exception xxxxx){}
}
	
//	if(writeImplementationMap != null 
//		&& writeImplementationMap.size() > 0 
//		&& writeImplementationMap.getValueByPosition(0) != null){
//	    	WriteAction writeImplementation = (WriteAction) writeImplementationMap.getValueByPosition(0);  
//	    	try{
//        	    	if(writeImplementation != null && 
//                		writeImplementation instanceof CreateAction 
//                		&& config.getConfiguration(Configurator.MAIN_SECTION_NAME, StressTool.class).getParameter("createTable").getValue() != null ){
//                	    boolean createSchema = Boolean.parseBoolean((String) (config.getConfiguration(Configurator.MAIN_SECTION_NAME, StressTool.class).getParameter("createTable").getValue()));
//                	    this.currentSchema = ((CreateAction)writeImplementation).CreateSchema(createSchema);
//                	    
//                	    return true;
//                	}
//	    	}
//	    	catch(Exception ex){
//	    	    ex.printStackTrace();
//	    	    return false;
//	    	}
//	}
	
	
	
	return false;
    }

    /**
     * @throws StressToolException 
     */
    public ValueProvider LoadData() throws StressToolException {
		String path = (String) config.getConfiguration(Configurator.MAIN_SECTION_NAME, StressTool.class).getParameter("datafilepath").getValue();
		ValueProvider dataLoader = this.setValueProvider();
		if(path !=null){
		    dataLoader.readText(path, ValueProvider.SPLIT_METHOD_UNIX_END_LINE);
		}
		return dataLoader;

    }

//    /**
//     */
//    public void TruncateTables() {
//    }
//
//    /**
//     */
//    public void DropTables() {
//    }
//
//    /**
//		 */
    public boolean runInsert() {
	
	for(Object id:writeImplementationMap.getKeyasOrderedArray()){
	    StressActionBase sb = (StressActionBase) writeImplementationMap.get(id);
	    ActionTHElement thInfo =  sb.getTHInfo();
	    if(thInfo.getReady() != ActionTHElement.SEMAPHORE_NOT_INITIALIZED){
		continue;
	    }
	    thInfo.setActive(true);
	    thInfo.setReady(ActionTHElement.SEMAPHORE_YELLOW);
	    sb.setTHInfo(thInfo);
	    Thread ths = new Thread((Runnable) sb);
	    sb.getTHInfo().setThId(ths.getId());
            ths.start();
	    writeImplementationMap.put(id, sb);
	} 
	
	return true;
	
    }

    /**
			 */
    public boolean runUpdate() {
	for(Object id:updateImplementationMap.getKeyasOrderedArray()){
	    StressActionBase sb = (StressActionBase) updateImplementationMap.get(id);
	    ActionTHElement thInfo =  sb.getTHInfo();
	    if(thInfo.getReady() != ActionTHElement.SEMAPHORE_NOT_INITIALIZED){
		continue;
	    }
	    thInfo.setActive(true);
	    thInfo.setReady(ActionTHElement.SEMAPHORE_YELLOW);
	    sb.setTHInfo(thInfo);
	    Thread ths = new Thread((Runnable) sb);
	    sb.getTHInfo().setThId(ths.getId());
	    ths.start();
	    updateImplementationMap.put(id, sb);
	} 
	
	return true;
	

    }

    /**
				 */
    public boolean runDelete() {
	for(Object id:deleteImplementationMap.getKeyasOrderedArray()){
	    StressActionBase sb = (StressActionBase) deleteImplementationMap.get(id);
	    ActionTHElement thInfo =  sb.getTHInfo();
	    if(thInfo.getReady() != ActionTHElement.SEMAPHORE_NOT_INITIALIZED){
		continue;
	    }
	    thInfo.setActive(true);
	    thInfo.setReady(ActionTHElement.SEMAPHORE_YELLOW);
	    sb.setTHInfo(thInfo);
	    Thread ths = new Thread((Runnable) sb);
	    sb.getTHInfo().setThId(ths.getId());
            ths.start();
	    deleteImplementationMap.put(id, sb);
	} 
	
	return true;

    }

    /**
					 */
    public boolean runSelects() {
	for(Object id:readImplementationMap.getKeyasOrderedArray()){
	    StressActionBase sb = (StressActionBase) readImplementationMap.get(id);
	    ActionTHElement thInfo =  sb.getTHInfo();
	    if(thInfo.getReady() != ActionTHElement.SEMAPHORE_NOT_INITIALIZED){
		continue;
	    }
	    thInfo.setActive(true);
	    thInfo.setReady(ActionTHElement.SEMAPHORE_YELLOW);
	    sb.setTHInfo(thInfo);
	    Thread ths = new Thread((Runnable) sb);
	    sb.getTHInfo().setThId(ths.getId());
            ths.start();
	    readImplementationMap.put(id, sb);
	} 
	
	return true;

    }

    /**
						 */
    public boolean LaunchActions() {
	int semaphore = this.checkForRunningOrBreak();
	if(this.actionInitialized 
		&& semaphore != ActionTHElement.SEMAPHORE_RED
	){
	    return true;
	}
	else if (semaphore == ActionTHElement.SEMAPHORE_RED){
	    return false; 
	}
	
	if(writeImplementationMap != null) this.actionInitialized=this.runInsert();
	if(updateImplementationMap != null) this.actionInitialized=this.runUpdate();
	if(readImplementationMap != null) this.actionInitialized=this.runSelects();
	if(deleteImplementationMap != null)this.actionInitialized=this.runDelete();
	
	if(!this.actionInitialized)
	    return false;
	
	for(int ic = 0; ic < SemaphoreCountdownTime ; ic++){
	   try {Thread.sleep(1000);
	   StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).info("Countdown to start the threads ("+ this.getSemaphoreCountdownTime() +") current : "+ (getSemaphoreCountdownTime() - ic));
	    latch.countDown();
	   } catch (Exception e) {				try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
		StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
	}catch(Exception xxxxx){}
}
	    
	}
//	this.LaunchActions();
	return true;
    }
    public void terminateThreads() {
	if(writeImplementationMap != null){
	    for(Object key:writeImplementationMap.getKeyasOrderedArray()){
		StressAction sbA = (StressAction)writeImplementationMap.get(key);
		sbA = null;
		writeImplementationMap.remove(key);
	    }
	}

	if(updateImplementationMap != null){
	    for(Object key:updateImplementationMap.getKeyasOrderedArray()){
		StressAction sbA = (StressAction)updateImplementationMap.get(key);
		sbA = null;
		updateImplementationMap.remove(key);
	    }

	}
	if(readImplementationMap != null){
	    for(Object key:readImplementationMap.getKeyasOrderedArray()){
		StressAction sbA = (StressAction)readImplementationMap.get(key);
		sbA = null;
		readImplementationMap.remove(key);
	    }

	}
	if(deleteImplementationMap != null){
	    for(Object key:deleteImplementationMap.getKeyasOrderedArray()){
		StressAction sbA = (StressAction)deleteImplementationMap.get(key);
		sbA = null;
		deleteImplementationMap.remove(key);
	    }

	}

	
    }

    /**
     * we check for the running threads and decide if we want to interrupt the execution or not
     * 
     * @return
     */
    private int checkForRunningOrBreak(){
	int selectSemaphore = ActionTHElement.SEMAPHORE_NOT_INITIALIZED;
	int deleteSemaphore = ActionTHElement.SEMAPHORE_NOT_INITIALIZED;
	int insertSemaphore = ActionTHElement.SEMAPHORE_NOT_INITIALIZED;
	int updateSemaphore = ActionTHElement.SEMAPHORE_NOT_INITIALIZED;
	
	if(writeImplementationMap != null){
        	for(Object thInfo:writeImplementationMap.getKeyasOrderedArray()){
        	    if(
        		    ((StressActionBase)(writeImplementationMap.get(thInfo))).getTHInfo().getReady() > ActionTHElement.SEMAPHORE_GREEN
        		    )
        	    {
        		if(((StressActionBase)(writeImplementationMap.get(thInfo))).getTHInfo().getReady() <= insertSemaphore){
        		    insertSemaphore = ((StressActionBase)(writeImplementationMap.get(thInfo))).getTHInfo().getReady();
        		}
        
        	    }  
        	}
	}
	if(updateImplementationMap != null){	
        	for(Object thInfo:updateImplementationMap.getKeyasOrderedArray()){
        	    if(
        		    ((StressActionBase)(updateImplementationMap.get(thInfo))).getTHInfo().getReady() > ActionTHElement.SEMAPHORE_GREEN){
        		if(((StressActionBase)(updateImplementationMap.get(thInfo))).getTHInfo().getReady() <= updateSemaphore){
        		    updateSemaphore = ((StressActionBase)(updateImplementationMap.get(thInfo))).getTHInfo().getReady();
        		}
        
        	    }  
        	} 
	}

	if(readImplementationMap != null){
        	for(Object thInfo:readImplementationMap.getKeyasOrderedArray()){
        	    if(
        		    ((StressActionBase)(readImplementationMap.get(thInfo))).getTHInfo().getReady() > ActionTHElement.SEMAPHORE_GREEN){
        		if(((StressActionBase)(readImplementationMap.get(thInfo))).getTHInfo().getReady() <= selectSemaphore){
        		    selectSemaphore = ((StressActionBase)(readImplementationMap.get(thInfo))).getTHInfo().getReady();
        		}
        		
        	    }  
        	} 
	}
	if(deleteImplementationMap != null){
        	for(Object thInfo:deleteImplementationMap.getKeyasOrderedArray()){
        	    if(((StressActionBase)(deleteImplementationMap.get(thInfo))).getTHInfo().getReady() > ActionTHElement.SEMAPHORE_GREEN){	  
        		if(((StressActionBase)(deleteImplementationMap.get(thInfo))).getTHInfo().getReady() <= deleteSemaphore){
        		    deleteSemaphore = ((StressActionBase)(deleteImplementationMap.get(thInfo))).getTHInfo().getReady();
        		}
        	    }  
        	} 
	}
	
	if(insertSemaphore < ActionTHElement.SEMAPHORE_RED && selectSemaphore == ActionTHElement.SEMAPHORE_RED )
	    return ActionTHElement.SEMAPHORE_YELLOW;
	else if((insertSemaphore == ActionTHElement.SEMAPHORE_RED || selectSemaphore == ActionTHElement.SEMAPHORE_RED) 
		&& (updateSemaphore >= ActionTHElement.SEMAPHORE_YELLOW || deleteSemaphore >= ActionTHElement.SEMAPHORE_YELLOW))
	    return ActionTHElement.SEMAPHORE_RED;
	
	return ActionTHElement.SEMAPHORE_YELLOW;
    }
    /**
     * 
     * This method load the information into dummy class instance 
     * used afterwards for pool creation with reflection
     * 
     */
    public boolean prepareLauncher() {
	
	
	/*
	 * define the latch for parallel start 
	 */
	latch = new CountDownLatch(SemaphoreCountdownTime);
	

	/*
	 * define how many threads must be run 
	 */
        float maxInsert = poolNumber;
        float maxUpdate = poolNumber;
        float maxSelect = poolNumber;
        float maxDelete = poolNumber;
        
        maxSelect=(maxSelect * (pctSelect / 100));
        maxInsert=(maxInsert * (pctInsert / 100));
        maxUpdate=(maxUpdate * (pctUpdate / 100));
        maxDelete=(maxDelete * (pctDelete / 100));
        
        int aInsert =  new Float( maxInsert).intValue();
        int aUpdate =  new Float( maxUpdate).intValue();
        int aSelect =  new Float( maxSelect).intValue();
        int aDelete =  new Float( maxDelete).intValue();
        
//        threadInfoInsertMap = new HashMap((aInsert > 0? aInsert - 1:0 ));
//        threadInfoSelectMap = new HashMap((aSelect > 0? aSelect - 1:0));
//        threadInfoDeleteMap = new HashMap((aDelete > 0? aDelete - 1:0));

//
//        
//        System.out.println("Thread to run for Insert:" + maxInsert);
//        System.out.println("Thread to run for Select:" + maxSelect);
//        System.out.println("Thread to run for Delete:" + maxDelete);
//        System.out.println("Class handling Insert :" + this.insertDefaultClass);
//        System.out.println("Class handling Select :" + this.selectDefaultClass);
//        System.out.println("Class handling Delete :" + this.deleteDefaultClass);


        if (aInsert >= 1)
            writeImplementationMap =new SynchronizedMap(poolNumber);

        if (aUpdate >= 1)
            updateImplementationMap =new SynchronizedMap(poolNumber);

        if (aSelect >= 1)
            readImplementationMap =new SynchronizedMap(poolNumber);
	
        if (aDelete >= 1)
            deleteImplementationMap =new SynchronizedMap(poolNumber);

 
        
        
	/*
	 * Here parameters are load for each type of class.
	 */
	
	try {
	    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).info("LOADING Action Classes");

		try {
		    
		    /* insert class
		     * Initialization
		     * 
		     */
		    if (aInsert >= 1){
        		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Insert class parameters [start] ");
        		    for(int iA = 0 ; iA < aInsert; iA ++ ){
        			StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Insert class parameters ["+ (iA +1) +"] ");
        			StressActionBase sa = (StressActionBase)setParametersClassInsert();
        			sa.setLatch(latch);
        			sa.setConnProvider(this.connProvider);
        			sa.setSchema(this.getCurrentSchema());
        			sa.setActionType(StressAction.ACTION_TYPE_Insert);
        			sa.setActionCode(StressAction.INSERT_ID_CONST );
        			sa.setTHInfo(new ActionTHElement(StressAction.INSERT_ID_CONST + iA,false,ActionTHElement.SEMAPHORE_NOT_INITIALIZED));
        			sa.getTHInfo().setAction(sa.getActionType());
        			writeImplementationMap.put(new Integer(StressAction.INSERT_ID_CONST + iA), sa);
        		    }
        		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Insert class parameters [end] ");
		    }
		    if (aUpdate >= 1){
        		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Update class parameters [start] ");
        		    for(int iA = 0 ; iA < aUpdate; iA ++ ){
        			StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Update class parameters ["+ (iA +1) +"] ");
        			StressActionBase sa = (StressActionBase)setParametersClassUpdate();
        			sa.setLatch(latch);
        			sa.setConnProvider(this.connProvider);
        			sa.setSchema(this.getCurrentSchema());
        			sa.setActionType(StressAction.ACTION_TYPE_Update);
        			sa.setActionCode(StressAction.UPDATE_ID_CONST );
        			sa.setTHInfo(new ActionTHElement(StressAction.UPDATE_ID_CONST + iA,false,ActionTHElement.SEMAPHORE_NOT_INITIALIZED));
        			sa.getTHInfo().setAction(sa.getActionType());
        			updateImplementationMap.put(new Integer(StressAction.UPDATE_ID_CONST + iA), sa);
        		    }
        		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Update class parameters [end] ");
		    }
		    /*
		     * Select class
		     * Initialization
		     */
		    if (aSelect >= 1){
        		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Select class parameters [start] ");
        
        		    for(int iA = 0 ; iA < aSelect; iA ++ ){
        			StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Select class parameters ["+ (iA +1) +"] ");
        			StressActionBase sa = (StressActionBase)setParametersClassSelect();
        			sa.setLatch(latch);
        			sa.setConnProvider(this.connProvider);
        			sa.setSchema(this.getCurrentSchema());
        			sa.setActionType(StressAction.ACTION_TYPE_Select);
        			sa.setActionCode(StressAction.SELECT_ID_CONST );
        			sa.setTHInfo(new ActionTHElement(StressAction.SELECT_ID_CONST + iA,false,ActionTHElement.SEMAPHORE_NOT_INITIALIZED));
        			sa.getTHInfo().setAction(sa.getActionType());
        			readImplementationMap.put(new Integer(StressAction.SELECT_ID_CONST + iA), sa);
        		    }
        		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Select class parameters [end] ");
		    }
		    /*
		     * Set Delete class
		     * Initialization
		     * 
		     */
		    if (aDelete >= 1){
        		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Delete class parameters [start] ");
        		    for(int iA = 0 ; iA < aDelete; iA ++ ){
        			StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Delete class parameters ["+ (iA +1) +"] ");
        			StressActionBase sa = (StressActionBase)setParametersClassDelete();
        			sa.setLatch(latch);   
        			sa.setConnProvider(this.connProvider);
        			sa.setSchema(this.getCurrentSchema());
        			sa.setActionType(StressAction.ACTION_TYPE_Delete);
        			sa.setActionCode(StressAction.DELETE_ID_CONST );
        			sa.setTHInfo(new ActionTHElement(StressAction.DELETE_ID_CONST + iA,false,ActionTHElement.SEMAPHORE_NOT_INITIALIZED));
        			sa.getTHInfo().setAction(sa.getActionType());
        			deleteImplementationMap.put(new Integer(StressAction.DELETE_ID_CONST + iA), sa);
        		    }
        		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Delete class parameters [end] ");
		    }
		    
		    return true;
		} 
		catch (InvocationTargetException e) {
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}
		}
		catch (NoSuchMethodException e) {
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}

			
		}
		catch (InstantiationException e) {
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}

		    
		} catch (IllegalAccessException e) {
		    
		    ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
		    throw new StressToolConfigurationException(e);
		} catch (ClassNotFoundException e) {
		    ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
		    throw new StressToolConfigurationException(e);
		}
		
    	}catch (StressToolConfigurationException e) {
        	    // TODO Auto-generated catch block
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}

    	}
    	catch (StressToolException e) {
    	    // TODO Auto-generated catch block
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}

    	}
	return false;
    }

    

	/**
	 * Method to set class parameters
	 * 
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws StressToolException
	 * @throws StressToolConfigurationException
	 */
	private StressAction setParametersClassUpdate() throws InstantiationException,
	IllegalAccessException, ClassNotFoundException,
	InvocationTargetException, NoSuchMethodException,
	StressToolException, StressToolConfigurationException {
	    if(updateClass != null && !updateClass.equals("")){
		UpdateAction updateImplementation = (UpdateAction) Class.forName(updateClass).newInstance();
		Map beanProperties = BeanUtils.describe(updateImplementation);
		Iterator<String> it = config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParametersName();

		

		while (it.hasNext()){
		    String propertyName = it.next();
		    if(beanProperties.containsKey(propertyName)){
			if(BeanUtils.getProperty(((StressAction)updateImplementation), propertyName) != null){

			    BeanUtils.setProperty(((StressAction)updateImplementation), propertyName, 
				    config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter(propertyName).getValue());
			    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(updateImplementation.getClass().getName() + " " +
				    propertyName + " = " + config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter(propertyName).getValue());

			}
		    	else{
		    	    	StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(updateImplementation.getClass().getName() + " \"" +
		    		propertyName + "\" method accepting this parameter DO NOT EXIST in this class");
		    	}
			
		    } 	
		}

		it = config.getConfiguration(updateClass,StressTool.class).getParametersName();
		while (it.hasNext()){
		    String propertyName = it.next();
		    if(beanProperties.containsKey(propertyName)){
			if(BeanUtils.getProperty((updateImplementation), propertyName) != null){

			    BeanUtils.setProperty((updateImplementation), propertyName, 
				    config.getConfiguration(updateClass,StressTool.class).getParameter(propertyName).getValue());
			    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(updateImplementation.getClass().getName() + " " +
				    propertyName + " = " + config.getConfiguration(updateClass,StressTool.class).getParameter(propertyName).getValue());

			}
		    	else{
		    	    	StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn(updateImplementation.getClass().getName() + " \"" +
		    		propertyName + "\" method accepting this parameter DO NOT EXIST in this class");
		    	}
			
		    }  	
		}
		((StressAction)updateImplementation).setId(4000);
		((StressAction)updateImplementation).setActionType(StressActionBase.ACTION_TYPE_Update);
		((StressAction)updateImplementation).setConnectionInformation(connMapcoordinates);
		
		return (StressAction) updateImplementation;
		
	    }
	    return null;
	}
	/**
	 * Method to set class parameters
	 * 
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws StressToolException
	 * @throws StressToolConfigurationException
	 */
	private StressAction setParametersClassInsert() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			InvocationTargetException, NoSuchMethodException,
			StressToolException, StressToolConfigurationException {
		if(insertClass != null && !insertClass.equals("")){
		    	WriteAction writeImplementation = (WriteAction) Class.forName(insertClass).newInstance();
			    Map beanProperties = BeanUtils.describe(writeImplementation);
			    Iterator<String> it = config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParametersName();
			    
			    
			    
				while (it.hasNext()){
				String propertyName = it.next();
				if(beanProperties.containsKey(propertyName)){
				    	if(BeanUtils.getProperty(((StressAction)writeImplementation), propertyName) != null){
		    			
					    BeanUtils.setProperty(((StressAction)writeImplementation), propertyName, 
					    	config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter(propertyName).getValue());
					    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(writeImplementation.getClass().getName() + " " +
					    		propertyName + " = " + config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter(propertyName).getValue());
		    			
					}
				    	else{
				    	    	StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(writeImplementation.getClass().getName() + " \"" +
				    		propertyName + "\" method accepting this parameter DO NOT EXIST in this class");
				    	}
				    	
				  } 	
				}
				
				it = config.getConfiguration(insertClass,StressTool.class).getParametersName();
				while (it.hasNext()){
				String propertyName = it.next();
				if(beanProperties.containsKey(propertyName)){
				    	if(BeanUtils.getProperty((writeImplementation), propertyName) != null){
		    			
					    BeanUtils.setProperty((writeImplementation), propertyName, 
					    	config.getConfiguration(insertClass,StressTool.class).getParameter(propertyName).getValue());
					    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(writeImplementation.getClass().getName() + " " +
					    		propertyName + " = " + config.getConfiguration(insertClass,StressTool.class).getParameter(propertyName).getValue());
		    			
					}
				    	else{
				    	    	StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn(writeImplementation.getClass().getName() + " \"" +
				    		propertyName + "\" method accepting this parameter DO NOT EXIST in this class");
				    	}
				    	
				  }  	
				}
				((StressAction)writeImplementation).setId(1000);
				((StressAction)writeImplementation).setActionType(StressActionBase.ACTION_TYPE_Insert);
				((StressAction)writeImplementation).setConnectionInformation(connMapcoordinates);
				
				
				return (StressAction) writeImplementation;
		}
		return null;
	}

	
	
	
	/**
	 * Method to set class parameters
	 * 
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws StressToolException
	 * @throws StressToolConfigurationException
	 */
	private StressAction setParametersClassSelect() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			InvocationTargetException, NoSuchMethodException,
			StressToolException, StressToolConfigurationException {
		if(selectClass != null && !selectClass.equals("")){
			    
				
		    	ReadAction readImplementation = (ReadAction) Class.forName(selectClass).newInstance();
			    Map beanProperties = BeanUtils.describe(readImplementation);
			    Iterator<String> it = config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParametersName();
			    
				while (it.hasNext()){
				String propertyName = it.next();
				if(beanProperties.containsKey(propertyName)){
				    	if(BeanUtils.getProperty(((StressAction)readImplementation), propertyName) != null){
		    			
					    BeanUtils.setProperty(((StressAction)readImplementation), propertyName, 
					    	config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter(propertyName).getValue());
					    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(readImplementation.getClass().getName() + " " +
					    		propertyName + " = " + config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter(propertyName).getValue());
		    			
					}
				    	else{
				    	    	StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(readImplementation.getClass().getName() + " \"" +
				    		propertyName + "\" method accepting this parameter DO NOT EXIST in this class");
				    	}
				    	
				  } 	
				}
				
				it = config.getConfiguration(selectClass,StressTool.class).getParametersName();
				while (it.hasNext()){
				String propertyName = it.next();
				if(beanProperties.containsKey(propertyName)){
				    	if(BeanUtils.getProperty((readImplementation), propertyName) != null){
		    			
					    BeanUtils.setProperty((readImplementation), propertyName, 
					    	config.getConfiguration(selectClass,StressTool.class).getParameter(propertyName).getValue());
					    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(readImplementation.getClass().getName() + " " +
					    		propertyName + " = " + config.getConfiguration(selectClass,StressTool.class).getParameter(propertyName).getValue() + "  "
					    +  BeanUtils.getProperty(readImplementation,propertyName).toString() );
		    			
					}
				    	else{
				    	    	StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn(readImplementation.getClass().getName() + " \"" +
				    		propertyName + "\" method accepting this parameter DO NOT EXIST in this class");
				    	}
				    	
				  }  	
				}
				((StressAction)readImplementation).setId(2000);
				((StressAction)readImplementation).setActionType(StressActionBase.ACTION_TYPE_Insert);
				((StressAction)readImplementation).setConnectionInformation(connMapcoordinates);
				return (StressAction) readImplementation;
				
		}
		return null;
	}
	
	private ValueProvider setValueProvider() {
	  if(valueProvider != null && !valueProvider.equals("")){
		ValueProvider valueProviderInstance;
		
        try {
    	      valueProviderInstance = (ValueProvider) Class.forName(valueProvider).newInstance();
    	      ((BasicValueProvider) valueProviderInstance).setTestCalendar(getTestCalendar());
    	      
    	      return valueProviderInstance;
	      
        } catch (InstantiationException e) {
	      // TODO Auto-generated catch block
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}

        } catch (IllegalAccessException e) {
	      // TODO Auto-generated catch block
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}

        } catch (ClassNotFoundException e) {
	      // TODO Auto-generated catch block
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
			StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
		}catch(Exception xxxxx){}

        }
	  }
	  return null;
	}
	    
		
    
	
	/**
	 * Method to set class parameters
	 * 
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws StressToolException
	 * @throws StressToolConfigurationException
	 */
	private StressAction setParametersClassDelete() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			InvocationTargetException, NoSuchMethodException,
			StressToolException, StressToolConfigurationException {
		if(deleteClass != null && !deleteClass.equals("")){
			    
				
		    DeleteAction deleteImplementation = (DeleteAction) Class.forName(deleteClass).newInstance();
			    Map beanProperties = BeanUtils.describe(deleteImplementation);
			    Iterator<String> it = config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParametersName();
			    
				while (it.hasNext()){
				String propertyName = it.next();
				if(beanProperties.containsKey(propertyName)){
				    	if(BeanUtils.getProperty(((StressAction)deleteImplementation), propertyName) != null){
		    			
					    BeanUtils.setProperty(((StressAction)deleteImplementation), propertyName, 
					    	config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter(propertyName).getValue());
					    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(deleteImplementation.getClass().getName() + " " +
					    		propertyName + " = " + config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter(propertyName).getValue());
		    			
					}
				    	else{
				    	    	StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(deleteImplementation.getClass().getName() + " \"" +
				    		propertyName + "\" method accepting this parameter DO NOT EXIST in this class");
				    	}
				    	
				  } 	
				}
				
				it = config.getConfiguration(deleteClass,StressTool.class).getParametersName();
				while (it.hasNext()){
				String propertyName = it.next();
				if(beanProperties.containsKey(propertyName)){
				    	if(BeanUtils.getProperty((deleteImplementation), propertyName) != null){
		    			
					    BeanUtils.setProperty((deleteImplementation), propertyName, 
					    	config.getConfiguration(deleteClass,StressTool.class).getParameter(propertyName).getValue());
					    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(deleteImplementation.getClass().getName() + " " +
					    		propertyName + " = " + config.getConfiguration(deleteClass,StressTool.class).getParameter(propertyName).getValue());
		    			
					}
				    	else{
				    	    	StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn(deleteImplementation.getClass().getName() + " \"" +
				    		propertyName + "\" method accepting this parameter DO NOT EXIST in this class");
				    	}
				  }  	
				}
				((StressAction)deleteImplementation).setId(3000);
				((StressAction)deleteImplementation).setActionType(StressActionBase.ACTION_TYPE_Insert);
				((StressAction)deleteImplementation).setConnectionInformation(connMapcoordinates);
				return (StressAction) deleteImplementation;
				
		}
		return null;
	}

	/**
	 * @return the writeImplementationMap
	 */
	public SynchronizedMap getWriteImplementationMap() {
	    return writeImplementationMap;
	}


	/**
	 * @return the updateImplementationMap
	 */
	public SynchronizedMap getUpdateImplementationMap() {
	    return updateImplementationMap;
	}

	/**
	 * @return the readImplementationMap
	 */
	public SynchronizedMap getReadImplementationMap() {
	    return readImplementationMap;
	}


	/**
	 * @return the deleteImplementationMap
	 */
	public SynchronizedMap getDeleteImplementationMap() {
	    return deleteImplementationMap;
	}

	/**
	 * @return the hardStopLimit
	 */
	public int getHardStopLimit() {
	    return HardStopLimit;
	}

	/**
	 * @param hardStopLimit the hardStopLimit to set
	 */
	public void setHardStopLimit(int hardStopLimit) {
	    HardStopLimit = hardStopLimit;
	}

	/**
	 * @return the useHardStop
	 */
	public boolean isUseHardStop() {
	    return UseHardStop;
	}

	/**
	 * @param useHardStop the useHardStop to set
	 */
	public void setUseHardStop(boolean useHardStop) {
	    UseHardStop = useHardStop;
	}

	/**
	 * @return the statIntervalMs
	 */
	public int getStatIntervalMs() {
	    return StatIntervalMs;
	}

	/**
	 * @param statIntervalMs the statIntervalMs to set
	 */
	public void setStatIntervalMs(int statIntervalMs) {
	    StatIntervalMs = statIntervalMs;
	}

	/**
	 * @return the statLoops
	 */
	public int getStatLoops() {
	    return StatLoops;
	}

	/**
	 * @param statLoops the statLoops to set
	 */
	public void setStatLoops(int statLoops) {
	    StatLoops = statLoops;
	}

	/**
	 * @return the repeatNumber
	 */
	public int getRepeatNumber() {
	    return repeatNumber;
	}

	/**
	 * @param repeatNumber the repeatNumber to set
	 */
	public void setRepeatNumber(int repeatNumber) {
	    this.repeatNumber = repeatNumber;
	}

	/**
	 * @return the semaphoreCountdownTime
	 */
	public int getSemaphoreCountdownTime() {
	    return SemaphoreCountdownTime;
	}

	/**
	 * @param semaphoreCountdownTime the semaphoreCountdownTime to set
	 */
	public void setSemaphoreCountdownTime(int semaphoreCountdownTime) {
	    SemaphoreCountdownTime = semaphoreCountdownTime;
	}

	/**
	 * @return the interactive
	 */
	public int getInteractive() {
	    return interactive;
	}

	/**
	 * @param interactive the interactive to set
	 */
	private void setInteractive(int interactive) {
	    this.interactive = interactive;
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

	/**
	 * @return the currentSchema
	 */
	public Schema getCurrentSchema() {
	    return currentSchema;
	}

	/**
	 * @param currentSchema the currentSchema to set
	 */
	public void setCurrentSchema(Schema currentSchema) {
	    this.currentSchema = currentSchema;
	}

	/**
	 * @return the testCalendar
	 */
	public Calendar getTestCalendar() {
	  return testCalendar;
	}

	/**
	 * @param testCalendar the testCalendar to set
	 */
	public void setTestCalendar(Calendar testCalendar) {
	  this.testCalendar = testCalendar;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
	  return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
	  this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
	  return endDate;
	}

	/**
	 * @param calendar.getTime() the endDate to set
	 */
	public void setEndDate(Calendar calendar) {
	  this.endDate = calendar.getTime();
	}

	/**
	 * @return the daysdiffernce
	 */
	public int getDaysdiffernce() {
	  return daysdiffernce;
	}

	/**
	 * @param daysdiffernce the daysdiffernce to set
	 */
	public void setDaysdiffernce(int daysdiffernce) {
	  this.daysdiffernce = daysdiffernce;
	}

	/**
	 * @return the valueProvider
	 */
	private String getValueProvider() {
	  return valueProvider;
	}

	/**
	 * @param valueProvider the valueProvider to set
	 */
	private void setValueProvider(String valueProvider) {
	  this.valueProvider = valueProvider;
	}
	
	public void resetValueProviderCalendar(){
		((BasicValueProvider) StressTool.getValueProvider()).resetCalendar(daysdiffernce);
	}
		
}
