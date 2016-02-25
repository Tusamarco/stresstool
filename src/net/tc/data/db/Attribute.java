package net.tc.data.db;

import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;



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
	public Object getValue(int stingLength) {
		return ((String)value).subSequence(0, Utility.getNumberFromRandomMinMax(1, stingLength).intValue());
	}

	public String getValueAsString( int stingLength) {
  	
    	switch (getDataType().getDataTypeId()){
         	case DataType.TINYINT: return ((Integer)getValue()).toString();
        	case DataType.SMALLINT:return ((Integer)getValue()).toString();
        	case DataType.MEDIUMINT:return ((Integer)getValue()).toString();
        	case DataType.INT: return ((Integer)getValue()).toString();
        	case DataType.BIGINT:return ((Long)getValue()).toString();
        	case DataType.FLOAT: return ((String)getValue());
        	case DataType.DOUBLE: return ((Double)getValue()).toString();
        	case DataType.DECIMAL: return ((java.math.BigDecimal)getValue()).toString(); 
        	case DataType.BIT: return ((Integer)getValue()).toString();
        	case DataType.YEAR: return ((Integer)getValue()).toString();
        	case DataType.DATE: return ((String)getValue());
        	case DataType.TIME: return ((String)getValue());
        	case DataType.DATETIME: return ((java.sql.Date)getValue()).toString();
        	case DataType.TIMESTAMP: return ((java.sql.Timestamp)getValue()).toString(); 
        	case DataType.CHAR: return ((String)getValue(stingLength));
        	case DataType.BINARY: return "byte[]";	    
        	case DataType.VARCHAR: return ((String)getValue(stingLength));
        	case DataType.VARBINARY: return "byte[]";
        	case DataType.TINYBLOB: return "byte[]";
        	case DataType.TINYTEXT: return ((String)getValue(stingLength));
        	case DataType.BLOB: return "byte[]"; 
        	case DataType.TEXT: return ((String)getValue(stingLength)); 
        	case DataType.MEDIUMBLOB: return "byte[]"; 
        	case DataType.MEDIUMTEXT: return ((String)getValue(stingLength));
        	case DataType.LONGBLOB: return "byte[]";
        	case DataType.LONGTEXT: return ((String)getValue(stingLength));
        	case DataType.ENUM: return ((String)getValue(stingLength));
        	case DataType.SET: return ((String)getValue(stingLength));
        	default: throw new IllegalArgumentException("Invalid data type index: " + getDataType().getDataTypeId());
    	
    	}
	  
	  
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
}