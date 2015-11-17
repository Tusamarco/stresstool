package net.tc.stresstool.config;

import java.io.File;
import java.util.Iterator;

import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.utils.SynchronizedMap;

/**
 * @author  tusa
 */
public class ConfigurationImplementation implements Configuration {
	/**
	 * @uml.property  name="sectionName"
	 */
	private String SectionName;
	/**
	 * @uml.property  name="mapValues"
	 * @uml.associationEnd  qualifier="toLowerCase:java.lang.String net.tc.stresstool.config.ConfigValue"
	 */
	private SynchronizedMap mapValues = new SynchronizedMap(0);
	
//	public ConfigurationImplementation(String SectionName) {
//		// TODO Auto-generated constructor stub
//	}

	public ConfigurationImplementation(String SectionNameIn,java.util.Map values,Class implementingClass) throws StressToolException {
		try{
			setSectionName(SectionNameIn+"@"+implementingClass.getName());
			Iterator<String> it = values.keySet().iterator();
			
			while(it.hasNext()){
				String key = it.next();
				ConfigValue tmpValue = new ConfigValue(getSectionName(),key,(String)values.get(key),implementingClass); 
				setParameter(tmpValue);
			}
		}
		catch(Throwable th )
		{
			new StressToolConfigurationException(th).printStackTrace();
		} 
	}

	@Deprecated
	public ConfigurationImplementation(String SectionNameIn,String key,String value,Class implementingClass) throws StressToolException {
		try{
			setSectionName(SectionNameIn+"@"+implementingClass.getName());
			ConfigValue tmpValue = new ConfigValue(getSectionName(),key,value,implementingClass); 
			setParameter(tmpValue);
		}
		catch(Throwable th )
		{
			new StressToolConfigurationException(th).printStackTrace();
		} 
	}

	@Override
	public void setParameter(ConfigValue parameter) {
		if(parameter != null 
			&& parameter.getId() != null)
				mapValues.put(parameter.getId(), parameter);

	}

	/**
	 * @param  name
	 * @uml.property  name="sectionName"
	 */
	@Override
	public void setSectionName(String name) {
		SectionName = name;

	}

	@Override
	public void setConfigurationFile(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public ConfigValue getParameter(String id) {
		if(mapValues != null && mapValues.size() > 0)
			return  (ConfigValue)mapValues.get(id);
		else
			return null;
	}

	/**
	 * @return
	 * @uml.property  name="sectionName"
	 */
	@Override
	public String getSectionName() {
		return SectionName;
	}

	@Override
	public File getConfigurationFile() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Iterator getParametersName(){
	    if(mapValues != null && mapValues.size() > 0){
		return (Iterator) mapValues.keySet().iterator();
	    }
	    return null;
	}
	
	/**
	 * This method update a set of configuration values
	 * and return boolean
	 */
	public boolean updateConfigValue(ConfigurationImplementation inConfig){
	    boolean modified = false;
	    if( inConfig != null 
		    && inConfig.getSectionName() !=null 
		    && !inConfig.getSectionName().equals("")
		    ){

		
		Iterator it = inConfig.getParametersName();
		while(it.hasNext()){
		    ConfigValue cv = inConfig.getParameter((String)it.next());
		    this.setParameter(cv);
		    modified =true;
			    
		}
		
		
	    }
	    
	    
	    return modified;
	}

}
