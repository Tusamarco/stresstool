
package net.tc.stresstool.config;

/**
 * @author  tusa
 */
public class ConfigValue {
	/**
	 * @return    the id
	 * @uml.property  name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id    the id to set
	 * @uml.property  name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return    the value
	 * @uml.property  name="value"
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return    the timestamp
	 * @uml.property  name="timestamp"
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param l    the timestamp to set
	 * @uml.property  name="timestamp"
	 */
	public void setTimestamp(long l) {
		this.timestamp = l;
	}

	/**
	 * @uml.property  name="id"
	 */
	private String id;
	/**
	 * @uml.property  name="value"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="org.apache.log4j.Appender"
	 */
	private Object value;
	/**
	 * @uml.property  name="timestamp"
	 */
	private long timestamp;
	/**
	 * @uml.property  name="sectionName"
	 */
	private String sectionName;
	/**
	 * @uml.property  name="implementingClass"
	 */
	private Class implementingClass;
	
	/**
	 * @return    the sectionName
	 * @uml.property  name="sectionName"
	 */
	public String getSectionName() {
		return sectionName;
	}

	/**
	 * @param sectionName    the sectionName to set
	 * @uml.property  name="sectionName"
	 */
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	/**
	 * @return    the implementingClass
	 * @uml.property  name="implementingClass"
	 */
	public Class getImplementingClass() {
		return implementingClass;
	}

	/**
	 * @param implementingClass    the implementingClass to set
	 * @uml.property  name="implementingClass"
	 */
	public void setImplementingClass(Class implementingClass) {
		this.implementingClass = implementingClass;
	}
	
@Deprecated
	public ConfigValue(String sectionName,String key,String value) {
		new ConfigValue(sectionName, key,value, net.tc.stresstool.StressTool.class);

	}

	public ConfigValue(String sectionName,String key,String value, Class implementingClass) {
		this.setTimestamp(System.currentTimeMillis());
		this.setSectionName(sectionName);
		this.setId(key);
		this.setValue(value);
		this.setImplementingClass(implementingClass);
		
	}

}
