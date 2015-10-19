package net.tc.stresstool.config;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



import org.apache.log4j.Logger;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.file.FileHandler;

/**
 * @author  tusa
 */
public class Configurator {
	/**
	 * @uml.property  name="configurationCollection"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="java.lang.String" qualifier="toString:java.lang.String net.tc.stresstool.config.ConfigurationImplementation"
	 */
	private SynchronizedMap configurationCollection;
	public static String MAIN_SECTION_NAME= "main";
	public static String STATS_SECTION_NAME= "statistics";
	public static String LOGS_SECTION_NAME= "logs";
	public static String ACTION_SECTION_NAME= "actionClass";
	
	public Configurator()throws StressToolException {
	    configurationCollection = new SynchronizedMap(0); 
		
		
	}
	
	public void loadNewConfiguration(String path,Class imPlementingClass) throws StressToolException{
		
		if(FileHandler.checkFilePath(path)){
			try{
				Wini tempConfig = new Wini(FileHandler.checkPath(path));
				Set<String> sections = tempConfig.keySet();
				
				Iterator<String> it = sections.iterator();
				while(it.hasNext()){
					String sectionName =it.next();
					ConfigurationImplementation confI = new ConfigurationImplementation(sectionName,(Map)tempConfig.get(sectionName),imPlementingClass); 
					configurationCollection.put(sectionName+"@"+imPlementingClass.getName(), confI);
					
				}
			}
			catch (InvalidFileFormatException e) {
				new StressToolConfigurationException(e).printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				new StressToolConfigurationException(e).printStackTrace();
			}
		}
		
	}
	public Iterator getSectionsName(){
		if(configurationCollection != null 
			&& configurationCollection.size() >0){
			return configurationCollection.keySet().iterator();
		}
		return null;
	}
	
	public Map getConfigurations(){
		return configurationCollection;
	}

	public Configuration getConfiguration (String name, Class classImpl)throws StressToolException
	{
		if(name !=null 
				&& !name.equals("") 
				&& classImpl !=null 
				&& !classImpl.getName().equals("")  
				&& configurationCollection.get(name+"@"+classImpl.getName()) !=null
				){
			return (Configuration) configurationCollection.get(name+"@"+classImpl.getName());
		}
		else{
		
			throw new  StressToolConfigurationException(ExceptionMessages.INVALID_CONFIGURATION_REQUEST 
					+ " Section Name = " + name
					+ " Class Name = " + classImpl.getName());
			
		}
		
	}

	public Configuration getConfiguration (String name)throws StressToolException
	{
		if(name !=null 
				&& !name.equals("") 
				&& configurationCollection.get(name) !=null
				){
			return (Configuration) configurationCollection.get(name);
		}
		else{
		
			throw new  StressToolConfigurationException(ExceptionMessages.INVALID_CONFIGURATION_REQUEST 
					+ " Section Name = " + name
					);
			
		}
		
	}

	public void printConfiguration(Logger log){
	    
	    Iterator it = configurationCollection.keySet().iterator();
	    log.info("============================================================");
	    log.info("Configuration section READING configuration file:");
	    while(it.hasNext()){
		/* return the section */
		Configuration config = (Configuration)configurationCollection.get(it.next());
		String sectionName = config.getSectionName();
		String configurationFile = config.getConfigurationFile()!=null?config.getConfigurationFile().getAbsoluteFile().toString():"";
		log.info("SECTION = " + sectionName);
		log.info("FILE = " + configurationFile);
		log.info("SECTION STARTS---------------------------------------------------");
		Iterator itV = config.getParametersName();
		while(itV.hasNext()){
		    ConfigValue cfValue = config.getParameter((String) itV.next());
		    log.info(cfValue.getId() + " = " + cfValue.getValue());
		}
		log.info("SECTION ENDS-----------------------------------------------------");
	    }
	    
	    
	}

}
