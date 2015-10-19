package net.tc.stresstool.actions;

import java.lang.reflect.InvocationTargetException;
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
import net.tc.utils.SynchronizedMap;


/**
 * @author tusa
 *
 */

public class Launcher {
    private Configurator config;
    private Map connMapcoordinates =null;
    private String deleteClass = null;
    private String insertClass = null;
    private String selectClass = null;
    private WriteAction writeImplementation = null; 
    private ReadAction readImplementation = null;
    private DeleteAction deleteImplementation = null;
    
    public Launcher(Configurator configIn) {
	if(configIn != null){
	    config = configIn;
	    try {
		connMapcoordinates = validatePermission(config.getConfiguration(Configurator.MAIN_SECTION_NAME, StressTool.class));
	    } catch (StressToolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    init();	    
	}
    }

    private void init(){
	
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
     */
    public boolean CreateStructure() {
	if(this.writeImplementation != null && 
		writeImplementation instanceof CreateAction ){
	    
	    ((CreateAction)writeImplementation).CreateSchema();
	    /*
	     * Still here
	     */
	    
	}
	
	
	
	return false;
    }

    /**
     */
    public void LoadData() {
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
	 * Here parameters are load for each type of class.
	 */
	
	try {
	    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).info("LOADING Action Classes");

		try {
		    
		    /* insert class
		     * 
		     */
		    setParametersClassInsert();

		    /*
		     * Select class
		     */
		    setParametersClassSelect();
		    
		    
		    /*
		     * Set Delete class
		     */
		    setParametersClassDelete();
		    
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
	private void setParametersClassInsert() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			InvocationTargetException, NoSuchMethodException,
			StressToolException, StressToolConfigurationException {
		if(insertClass != null && !insertClass.equals("")){
			    writeImplementation = (WriteAction) Class.forName(insertClass).newInstance();
			    Map beanProperties = BeanUtils.describe(writeImplementation);
			    Iterator<String> it = config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParametersName();
			    
			    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Insert class parameters [start] ");
			    
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
				
				StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Insert class parameters [end] ");
		}
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
	private void setParametersClassSelect() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			InvocationTargetException, NoSuchMethodException,
			StressToolException, StressToolConfigurationException {
		if(selectClass != null && !selectClass.equals("")){
			    
				
			readImplementation = (ReadAction) Class.forName(selectClass).newInstance();
			    Map beanProperties = BeanUtils.describe(readImplementation);
			    Iterator<String> it = config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParametersName();
			    
			    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Select class parameters [start] ");
			    
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
				
				StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Select class parameters [end] ");
		}
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
	private void setParametersClassDelete() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			InvocationTargetException, NoSuchMethodException,
			StressToolException, StressToolConfigurationException {
		if(deleteClass != null && !deleteClass.equals("")){
			    
				
			deleteImplementation = (DeleteAction) Class.forName(deleteClass).newInstance();
			    Map beanProperties = BeanUtils.describe(deleteImplementation);
			    Iterator<String> it = config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParametersName();
			    
			    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Delete class parameters [start] ");
			    
				while (it.hasNext()){
				String propertyName = it.next();
				if(beanProperties.containsKey(propertyName)){
				    	if(BeanUtils.getProperty(((StressAction)deleteImplementation), propertyName) != null){
		    			
					    BeanUtils.setProperty(((StressAction)deleteImplementation), propertyName, 
					    	config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter(propertyName).getValue());
					    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(deleteImplementation.getClass().getName() + " " +
					    		propertyName + " = " + config.getConfiguration(Configurator.MAIN_SECTION_NAME,StressTool.class).getParameter(propertyName).getValue());
		    			
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
				  }  	
				}
				((StressAction)deleteImplementation).setId(2000);
				((StressAction)deleteImplementation).setActionType(StressActionBase.ACTION_TYPE_Insert);
				
				StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Assign  Delete class parameters [end] ");
		}
	}
		
}
