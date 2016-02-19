package net.tc.data.db;

import net.tc.utils.SynchronizedMap;



public class Attribute {
	private String name = null;
	private DataType dataType = null;
	private int dataDimension = 0;
	private boolean autoIncrement = false;
	private String defaultValue = null;
	private String onUpdate = null;
	private int fillingFactor = 0;
	private boolean hasIndex = false;
	private int indexOrder = 0;
	private Object value = null;
	private int storageSize = 0 ; //size in bytes
	private boolean isLazy = true ; 
	private String specialFunction = null;
	private int upperLimit = 0 ;
	private boolean where_attribute = false;
	
	
    private boolean isNull = true;
    
	public boolean isNull() {
		return isNull;
	}
	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public int getDataDimension() {
		return dataDimension;
	}
	public void setDataDimension(int dataDimension) {
		this.dataDimension = dataDimension;
	}
	public boolean isAutoIncrement() {
		return autoIncrement;
	}
	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getOnUpdate() {
		return onUpdate;
	}
	public void setOnUpdate(String onUpdate) {
		this.onUpdate = onUpdate;
	}
	public int getFillingFactor() {
		return fillingFactor;
	}
	public void setFillingFactor(int fillingFactor) {
		this.fillingFactor = fillingFactor;
	}
	public boolean isHasIndex() {
		return hasIndex;
	}
	public void setHasIndex(boolean hasIndex) {
		this.hasIndex = hasIndex;
	}
	public int getIndexOrder() {
		return indexOrder;
	}
	public void setIndexOrder(int indexOrder) {
		this.indexOrder = indexOrder;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public int getStorageSize() {
		return storageSize;
	}
	public void setStorageSize(int storageSize) {
		this.storageSize = storageSize;
	}
	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
	    return isLazy;
	}
	/**
	 * @param isLazy the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
	    this.isLazy = isLazy;
	}
	/**
	 * @return the specialFunction
	 */
	public String getSpecialFunction() {
	  return specialFunction;
	}
	/**
	 * @param specialFunction the specialFunction to set
	 */
	public void setSpecialFunction(String specialFunction) {
	  this.specialFunction = specialFunction;
	}
	/**
	 * @return the upperLimit
	 */
	public int getUpperLimit() {
	  return upperLimit;
	}
	/**
	 * @param upperLimit the upperLimit to set
	 */
	public void setUpperLimit(int upperLimit) {
	  this.upperLimit = upperLimit;
	}
	/**
	 * @return the where_attribute
	 */
	public boolean isWhere_attribute() {
	  return where_attribute;
	}
	/**
	 * @param where_attribute the where_attribute to set
	 */
	public void setWhere_attribute(boolean where_attribute) {
	  this.where_attribute = where_attribute;
	}
}