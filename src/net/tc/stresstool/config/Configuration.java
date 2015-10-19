package net.tc.stresstool.config;

import java.io.File;
import java.util.Iterator;

/**
 * @author    tusa
 */
public interface Configuration {

	
	public void setParameter(ConfigValue parameter);
	/**
	 * @param  name
	 * @uml.property  name="sectionName"
	 */
	public void setSectionName(String name);
	/**
	 * @param  file
	 * @uml.property  name="configurationFile"
	 */
	public void setConfigurationFile(File file);
	
	public ConfigValue getParameter(String id);
	/**
	 * @uml.property  name="sectionName"
	 */
	public String getSectionName();
	/**
	 * @uml.property  name="configurationFile"
	 */
	public File getConfigurationFile();
	/**
	 * @uml.property  name="parametersName"
	 */
	public Iterator getParametersName();
}
