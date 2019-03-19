package net.tc.stresstool.config;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;






import org.apache.log4j.Logger;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.file.FileHandler;

/**
 * @author  tusa
 *
 * Configurator use the Object ConfigurationImplementation which wrap ConfigValue
 * Hierarchy Configurator -> configuration(impl) -> ConfigValue
 * the single parameter IS the ConfigValue ObjectS
 * 
 */
public class Configurator {
	/**
	 * @uml.property  name="configurationCollection"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="java.lang.String" qualifier="toString:java.lang.String net.tc.stresstool.config.ConfigurationImplementation"
	 */
	private SynchronizedMap configurationCollection;
	private SynchronizedMap configurationArgs;
	public static String MAIN_SECTION_NAME= "main";
	public static String STATS_SECTION_NAME= "statistics";
	public static String LOGS_SECTION_NAME= "logs";
	public static String ACTION_SECTION_NAME= "actionClass";
	
	public Configurator()throws StressToolException {
	    configurationCollection = new SynchronizedMap(0);
		
	}
	/**
	 * 
	 * @param args
	 * @param path
	 * @param imPlementingClass
	 * @throws StressToolException
	 * 
	 * Load the main configuration browsing the configuration file
	 * and getting values from Args
	 * value in args must be pass as <section>@parameter=value
	 */
	public void loadNewConfiguration(String[] args, String path,Class imPlementingClass) throws StressToolException{
	    	if(args.length > 1){
	    	    configurationArgs = readArgs(args, imPlementingClass);
	    	    
	    	}
	    	    
		
		if(FileHandler.checkFilePath(path)){
			try{
				Wini tempConfig = new Wini(FileHandler.checkPath(path));
				Set<String> sections = tempConfig.keySet();
				
				Iterator<String> it = sections.iterator();
				while(it.hasNext()){
					String sectionName =it.next();
					ConfigurationImplementation confI = new ConfigurationImplementation(sectionName,(Map)tempConfig.get(sectionName),imPlementingClass); 
					if(configurationArgs != null
						&& configurationArgs.size() >  0
						&& configurationArgs.get(sectionName+"@"+imPlementingClass.getName()) !=null 
						){
					    
					    confI.updateConfigValue((ConfigurationImplementation)configurationArgs.get(sectionName+"@"+imPlementingClass.getName()));
					}
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
	/**
	 * 
	 * @param args
	 * @param imPlementingClass
	 * @return
	 * @throws StressToolException
	 * 
	 * Read the Args and fill the configuration object
	 */
	private SynchronizedMap readArgs(String[] args, Class imPlementingClass) throws StressToolException {
	    	configurationArgs = new SynchronizedMap(0);
	    String[] inArgs = null; 
	    if(args.length > 1 && args[1].indexOf(",") > 0){
	    	inArgs= args[1].replaceAll("\n", "").replaceAll(" ", "").split(",");
	    }	
	    else
	    	inArgs = args;
	    
	    String[] args2 =new String[inArgs.length  ];
	    
		for(int i = 0; i < inArgs.length; i++){
//		    args2[i] = args[i + 1];
		    args2[i] = inArgs[i];
		}
		Map argsM = net.tc.utils.Utility.convertArgsToMap(args2);
		
		Iterator it = argsM.keySet().iterator();
		while(it.hasNext()){
        		String sectionName = (String)it.next();
        		
		    	ConfigurationImplementation confI;
        		confI = new ConfigurationImplementation(sectionName,(Map)argsM.get(sectionName),imPlementingClass);
        		configurationArgs.put(sectionName+"@"+imPlementingClass.getName(), confI);
		}

		return configurationArgs;
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
/**
 * Method used to print the configuration information
 * @param log
 */
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
