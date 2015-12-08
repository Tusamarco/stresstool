package net.tc.stresstool.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import net.tc.stresstool.StressTool;
import net.tc.stresstool.config.Configuration;
import net.tc.stresstool.config.Configurator;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.StatCollector;
import net.tc.stresstool.statistics.providers.StatsProvider;
import net.tc.stresstool.value.BasicFileValueProvider;
import net.tc.stresstool.value.ValueProvider;
import net.tc.utils.SynchronizedMap;


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
    
    public Launcher(Configurator configIn) {
	if(configIn != null){
	    config = configIn;
	    try {
		connMapcoordinates = validatePermission(config.getConfiguration(Configurator.MAIN_SECTION_NAME, StressTool.class));
		init(config.getConfiguration(Configurator.MAIN_SECTION_NAME, StressTool.class));
	    } catch (StressToolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    	    
	}
    }

    private void init(Configuration configuration){
	this.poolNumber = Integer.parseInt((String) configuration.getParameter("poolNumber").getValue());
	this.pctDelete = Integer.parseInt((String) configuration.getParameter("pctDelete").getValue());
	this.pctInsert = Integer.parseInt((String) configuration.getParameter("pctInsert").getValue());
	this.pctSelect = Integer.parseInt((String) configuration.getParameter("pctSelect").getValue());
	this.pctUpdate = Integer.parseInt((String) configuration.getParameter("pctUpdate").getValue());
	
    }
    /**
    */
    public Map validatePermission(Configuration configuration) {
	
	
        Map connMapcoordinates = new SynchronizedMap(0) ;
        boolean valid = false;
        String userName="";
        Map providers = new SynchronizedMap();
        
        connMapcoordinates.put("jdbcUrl", configuration.getParameter("connUrl").getValue());
        connMapcoordinates.put("database", configuration.getParameter("database").getValue());
        connMapcoordinates.put("user", configuration.getParameter("user").getValue());
        connMapcoordinates.put("password", configuration.getParameter("password").getValue());
        connMapcoordinates.put("dbtype", configuration.getParameter("dbType").getValue());
        connMapcoordinates.put("connparameters", configuration.getParameter("connParameters").getValue());

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
        		    valid = sp.validatePermissions(connMapcoordinates);
		    }
		}

	    } catch (StressToolException e) {
		e.printStackTrace();
	    } catch (InstantiationException e) {
		try {
		    throw new StressToolConfigurationException(e);
		} catch (StressToolConfigurationException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
	    } catch (IllegalAccessException e) {
		try {
		    throw new StressToolConfigurationException(e);
		} catch (StressToolConfigurationException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
	    } catch (ClassNotFoundException e) {
		try {
		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).error(ExceptionMessages.ERROR_CREATING_PROVIDER + 
		    	" Provider class: " + currentProviderClassName + " invalid or not yet implement;\n " +
		    			"Check the configuration and remove class declaration if Provider is not available.");
		} catch (StressToolConfigurationException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
		try {
		    throw new StressToolConfigurationException(e);
		} catch (StressToolConfigurationException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
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
	if(writeImplementationMap != null 
		&& writeImplementationMap.size() > 0 
		&& writeImplementationMap.getValueByPosition(0) != null){
	    	WriteAction writeImplementation = (WriteAction) writeImplementationMap.getValueByPosition(0);  
	    	
	    	if(writeImplementation != null && 
        		writeImplementation instanceof CreateAction ){
        	    
        	    ((CreateAction)writeImplementation).CreateSchema();
        	    /*
        	     * Still here
        	     */
        	    return true;
        	}
	}
	
	
	
	return false;
    }

    /**
     * @throws StressToolException 
     */
    public ValueProvider LoadData() throws StressToolException {
	String path = (String) config.getConfiguration(Configurator.MAIN_SECTION_NAME, StressTool.class).getParameter("datafilepath").getValue();
	if(path !=null){
	    ValueProvider dataLoader = new BasicFileValueProvider();
	    dataLoader.readText(path, ValueProvider.SPLIT_METHOD_UNIX_END_LINE);

	    return dataLoader;
	}
	return null;
    }

    /**
     */
    public void TruncateTables() {
    }

    /**
     */
    public void DropTables() {
    }

    /**
		 */
    public void CreateInsert() {
    }

    /**
			 */
    public void CreateUpdate() {
    }

    /**
				 */
    public void CreateDelete() {
    }

    /**
					 */
    public void CreateSelects() {
    }

    /**
						 */
    public void LaunchActions() {
	
	
	
    }
    
    /**
     * 
     * This method load the information into dummy class instance 
     * used afterwards for pool creation with reflection
     * 
     */
    public boolean prepareLauncher() {
	
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
			else{
			    ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
			    throw new StressToolConfigurationException(" Delete Class not define");
			    
			}
			

	   }
	
	} 
	catch (StressToolConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	catch (StressToolException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

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
        			sa.setActionType(StressAction.ACTION_TYPE_Insert);
        			sa.setActionCode(StressAction.INSERT_ID_CONST + iA);
        			writeImplementationMap.put(StressAction.INSERT_ID_CONST + iA, sa);
        		    }
        		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Insert class parameters [end] ");
		    }
		    if (aUpdate >= 1){
        		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Update class parameters [start] ");
        		    for(int iA = 0 ; iA < aUpdate; iA ++ ){
        			StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Update class parameters ["+ (iA +1) +"] ");
        			StressActionBase sa = (StressActionBase)setParametersClassUpdate();
        			sa.setActionType(StressAction.ACTION_TYPE_Update);
        			sa.setActionCode(StressAction.UPDATE_ID_CONST + iA);
        			updateImplementationMap.put(StressAction.UPDATE_ID_CONST + iA, sa);
        		    }
        		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Update class parameters [end] ");
		    }
		    /*
		     * Select class
		     * Initialization
		     */
		    if (aSelect >= 1){
        		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Select class parameters [start] ");
        
        		    for(int iA = 0 ; iA < aUpdate; iA ++ ){
        			StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Select class parameters ["+ (iA +1) +"] ");
        			StressActionBase sa = (StressActionBase)setParametersClassSelect();
        			sa.setActionType(StressAction.ACTION_TYPE_Select);
        			sa.setActionCode(StressAction.SELECT_ID_CONST + iA);
        			readImplementationMap.put(StressAction.SELECT_ID_CONST + iA, sa);
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
        		    for(int iA = 0 ; iA < aUpdate; iA ++ ){
        			StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Delete class parameters ["+ (iA +1) +"] ");
        			StressActionBase sa = (StressActionBase)setParametersClassDelete();
        			sa.setActionType(StressAction.ACTION_TYPE_Delete);
        			sa.setActionCode(StressAction.DELETE_ID_CONST + iA);
        			deleteImplementationMap.put(StressAction.DELETE_ID_CONST + iA, sa);
        		    }
        		    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Delete class parameters [end] ");
		    }
		    
		    return true;
		} 
		catch (InvocationTargetException e) {
			e.printStackTrace();
		    ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
		    throw new StressToolConfigurationException(e);
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
			ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
		    throw new StressToolConfigurationException(e);
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
		    throw new StressToolConfigurationException(e);
		    
		} catch (IllegalAccessException e) {
		    
		    ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
		    throw new StressToolConfigurationException(e);
		} catch (ClassNotFoundException e) {
		    ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
		    throw new StressToolConfigurationException(e);
		}
		
    	}catch (StressToolConfigurationException e) {
        	    // TODO Auto-generated catch block
    	    e.printStackTrace();
    	}
    	catch (StressToolException e) {
    	    // TODO Auto-generated catch block
    	    e.printStackTrace();
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
		WriteAction updateImplementation = (WriteAction) Class.forName(updateClass).newInstance();
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
					    		propertyName + " = " + config.getConfiguration(selectClass,StressTool.class).getParameter(propertyName).getValue());
		    			
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
				    	    	StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn(deleteImplementation.getClass().getName() + " " +
				    		propertyName + " method accepting this parameter DO NOT EXIST in this class");
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
				    	    	StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn(deleteImplementation.getClass().getName() + " " +
				    		propertyName + " method accepting this parameter DO NOT EXIST in this class");
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
		
}
