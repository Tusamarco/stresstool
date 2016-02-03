package net.tc.data.db;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;

public class Table {
    	public static final Integer TABLE_PARENT=0;
    	public static final Integer TABLE_CHILD=1;
	private String name = null;
	private int rowsNumber = 0;
	private int fillFactor = 0;
	private String defaultCharacterSet = null;
	private String defaultCollation = null;
	private String storageEngine = null;
	private int rowDimension = 0; //dimension in bytes
	private SynchronizedMap rows = null;
	private int attributesNumber = 0 ;
	private PrimaryKey primaryKey = null;
	private SynchronizedMap indexes = null;
	private PartitionDefinition partitionDefinition = null;
	private boolean hasPartition = false;
	private boolean multiple = false; 
	private boolean hasSubPartition = false;
	private boolean autoincrement = false;
	private String schemaName = null;
	private long autoincrementValue = 0 ;
	private SynchronizedMap metaAttributes = null;
	private String parentTable = null;
	private int instances = 1;
	private int instanceNumber = 0;
	private String tableSpace = null;
	private int  rowFormatInt = 0;
	private String dataDirectory = null;
	
	
	
	public Table() {
	    rows = new SynchronizedMap(0);
	    indexes = new SynchronizedMap(0);
	    metaAttributes = new SynchronizedMap(0);
	}
	
	
	public SynchronizedMap getMetaAttributes() {
		return this.metaAttributes;
	}

	public Attribute getMetaAttributes(String name) {
		if(name != null
				&& !name.equals("")
				&& metaAttributes != null
				&& metaAttributes.containsKey(name)){
			return (Attribute) metaAttributes.get(name);
		}
		return null;
	}

	public void setMetaAttributes(Map metaAttributes) {
		this.metaAttributes = (SynchronizedMap) metaAttributes;
	}

	public void setMetaAttribute(Attribute attribute) {
		if(attribute != null
				&& attribute.getName() != null
				&& !attribute.getName().equals("")
				&& metaAttributes != null){
			metaAttributes.put(attribute.getName(), attribute);
		}
		
	}
	
	
	public long getAutoincrementValue() {
		return this.autoincrementValue;
	}

	public void setAutoincrementValue(long autoincrementValue) {
		this.autoincrementValue = autoincrementValue;
	}

	public boolean isAutoincrement() {
		return this.autoincrement;
	}

	public void setAutoincrement(boolean autoincrement) {
		this.autoincrement = autoincrement;
	}

	public String getSchemaName() {
		return this.schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public boolean isHasPartition() {
		return hasPartition;
	}

	public void setHasPartition(boolean hasPartition) {
		this.hasPartition = hasPartition;
	}

	public boolean isHasSubPartition() {
		return hasSubPartition;
	}

	public void setHasSubPartition(boolean hasSubPartition) {
		this.hasSubPartition = hasSubPartition;
	}

	public SynchronizedMap getIndexes() {
		return indexes;
	}
	
	public Index getIndex(String nameIndex) {
		if(nameIndex != null
				&& !nameIndex.equals("")
				&& indexes != null
				&& indexes.get(nameIndex) != null){
				return (Index) indexes.get(nameIndex);
		}
		return null;
	}

	public void setIndex(Index index) {
		if(index != null 
				&& index.getName() != null
				&& !index.getName().equals("")){
			indexes.put(index.getName(), index);
		}
	}
	public void setIndexes(SynchronizedMap indexes) {
		this.indexes = indexes;
	}
	
	
	
	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}
	public PartitionDefinition getPartitionDefinition() {
		return partitionDefinition;
	}
	public void setPartitionDefinition(PartitionDefinition partitionDefinition) {
		this.partitionDefinition = partitionDefinition;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRowsNumber() {
		return rowsNumber;
	}
	public void setRowsNumber(int rowsNumber) {
		this.rowsNumber = rowsNumber;
	}
	public int getFillFactor() {
		return fillFactor;
	}
	public void setFillFactor(int fillFactor) {
		this.fillFactor = fillFactor;
	}
	public String getDefaultCharacterSet() {
		return defaultCharacterSet;
	}
	public void setDefaultCharacterSet(String defaultCharacterSet) {
		this.defaultCharacterSet = defaultCharacterSet;
	}
	public String getDefaultCollation() {
		return defaultCollation;
	}
	public void setDefaultCollation(String defaultCollation) {
		this.defaultCollation = defaultCollation;
	}
	public String getStorageEngine() {
		return storageEngine;
	}
	public void setStorageEngine(String storageEngine) {
		this.storageEngine = storageEngine;
	}
	public int getRowDimension() {
		return rowDimension;
	}
	public void setRowDimension(int rowDimension) {
		this.rowDimension = rowDimension;
	}
	public SynchronizedMap getRows() {
		return rows;
	}
	public Row getRows(String rowName) {
		if(rowName != null 
				&& !rowName.equals("")
				&& rows !=null
				&& rows.get(rowName) != null)
			return (Row) rows.get(rowName);
		
		return null;
	}

	public void setRows(SynchronizedMap rows) {
		this.rows = rows;
	}
	public void setRow(Row row) {
		if(row != null
				&& row.getName() != null
				&& !row.getName().equals("")
				&& rows != null){
			rows.put(row.getName(), row);
		}
		
	}
	public int getAttributesNumber() {
		return attributesNumber;
	}
	public void setAttributesNumber(int attributesNumber) {
		this.attributesNumber = attributesNumber;
	}


	/**
	 * @return the multiple
	 */
	public boolean isMultiple() {
	    return multiple;
	}


	/**
	 * @param multiple the multiple to set
	 */
	public void setMultiple(boolean multiple) {
	    this.multiple = multiple;
	}


	/**
	 * @return the parentTable
	 */
	public String getParentTable() {
	    return parentTable;
	}


	/**
	 * @param parentTable the parentTable to set
	 */
	public void setParentTable(String parentTable) {
	    this.parentTable = parentTable;
	}


	/**
	 * @return the instances
	 */
	public int getInstances() {
	    return instances;
	}


	/**
	 * @param instances the instances to set
	 */
	public void setInstances(int instances) {
	    this.instances = instances;
	}


	/**
	 * @return the instanceNumber
	 */
	public int getInstanceNumber() {
	    return instanceNumber;
	}


	/**
	 * @param instanceNumber the instanceNumber to set
	 */
	public void setInstanceNumber(int instanceNumber) {
	    this.instanceNumber = instanceNumber;
	}
	/**
	 * Table deploy method
	 * @return
	 * @throws StressToolConfigurationException 
	 */
	public StringBuffer deploy() throws StressToolConfigurationException{
	    StringBuffer sbHead = new StringBuffer();
	    StringBuffer sbTail = new StringBuffer();
	    
	    sbHead.append("/* CREATE TABLE "
		    + this.getSchemaName() +"."
		    + this.getName() 
		    + " */\n");
	    
	    sbHead.append("CREATE TABLE IF NOT EXISTS "
		    + this.getSchemaName() +"."
		    + this.getName() 
		    +"(\n");
	    
	    sbTail.append("\n )\n");
	    sbTail.append(this.getStorageEngine()!=null?"ENGINE="+getStorageEngine() + " ":"");
	    sbTail.append(this.getDefaultCharacterSet()!=null?"CHARSET="+getDefaultCharacterSet() + " ":"");
	    sbTail.append(this.getDefaultCollation()!=null?"COLLATE="+getDefaultCollation() + " ":"");
	    
	    sbTail.append(this.getRowFormat()!=null?"ROW_FORMAT="+getRowFormat() + " ":"");
	    sbTail.append(this.getDataDirectory()!=null?"DATA DIRECTORY="+getDataDirectory() + " ":"");
	    sbTail.append(this.getTableSpace()!=null?"TABLESPACE="+getTableSpace() + " ":"");
	    
	    /*
	     * Cycle the attributes
	     */
	    Iterator itAtt = getMetaAttributes().iterator();
	    StringBuffer sbAtt = new StringBuffer();
	    
	    while (itAtt.hasNext()){
		if (sbAtt.length() > 1)
		    	sbAtt.append(",\n");
		Attribute attribute = (Attribute) this.getMetaAttributes((String)itAtt.next());
		sbAtt.append("`"+ attribute.getName() +"` ");
		sbAtt.append(DataType.getDataTypeStringByIdentifier(attribute.getDataType().getDataTypeId()) 
			+ (attribute.getDataDimension()>0?"("+attribute.getDataDimension()+") ":" "));
		sbAtt.append(attribute.isNull()?"NULL ":"NOT NULL ");
		sbAtt.append(attribute.isAutoIncrement()?" AUTO_INCREMENT ":"");
		sbAtt.append(attribute.getDefaultValue()!=null?"DEFAULT " + attribute.getDefaultValue() + " ":" ");
		sbAtt.append(attribute.getOnUpdate()!=null?"ON UPDATE " + attribute.getOnUpdate() + " ":" ");
	    }

	    /*
	     * Cycle the indexes
	     */

	    /*
	     * first PK
	     */
	    PrimaryKey pk = this.getPrimaryKey();
	    String strPK = null;
	    StringBuffer sbIdx = new StringBuffer();
	    if(pk != null){
		sbIdx.append(" PRIMARY KEY (" + pk.getIndexes().getKeyasUnorderdString() + ") ");
	    }
	    
	    Iterator itIdx = this.getIndexes().iterator();
	    if(itIdx != null){
		while(itIdx.hasNext()){
		    if(sbIdx.length() > 1)
			sbIdx.append(", \n");
		    Index idx = this.getIndex((String) itIdx.next());
		    if(idx.isUnique()){
			sbIdx.append(" UNIQUE `"+ idx.getName() + "` ");
		    }else{
			sbIdx.append(" INDEX `"+ idx.getName() + "` ");
		    }
		    sbIdx.append("(" + idx.getColumnsDefinitionAsString() +  ") ");
		    
		}
	    }
	    
	    sbHead.append(sbAtt.toString());
	    if(sbIdx.length() > 1){
		sbHead.append(",\n");
		sbHead.append(sbIdx.toString());
	    }

	    /*
	     * Attach the table definition
	     * 
	     */
	    sbHead.append(sbTail.toString());
	    
	    /*
	     * Add partitioninformation if present
	     */
	    if(this.getPartitionDefinition() !=null){
		sbHead.append(this.getPartitionDefinition().getSQLPartitionDefinition());
		
	    }
		
	    
	    StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug( "\n"+ sbHead.toString() + "\n "  );
	    return sbHead;
	    
	}


	/**
	 * @return the tableSpace
	 */
	public String getTableSpace() {
	    return tableSpace;
	}


	/**
	 * @param tableSpace the tableSpace to set
	 */
	public void setTableSpace(String tableSpace) {
	    if(tableSpace == null || tableSpace.equals("")){
	    	this.tableSpace = null;
	    	return;
	    }

	    
	    this.tableSpace = tableSpace;
	}


	/**
	 * @return the rowFormat
	 */
	public String getRowFormat() {
	    if(rowFormatInt == 0){
		return "default";
	    }
	    switch(rowFormatInt){
		case Row.ROW_FORMAT_INNODB_DYNAMIC:return "dynamic";
		case Row.ROW_FORMAT_INNODB_FIXED: return "fixed";
		case Row.ROW_FORMAT_INNODB_COMPRESSED: return "compressed";
		case Row.ROW_FORMAT_INNODB_REDUNDANT: return "redundant";
		case Row.ROW_FORMAT_INNODB_COMPACT: return "compact";
		case Row.ROW_FORMAT_INNODB_DEFAULT: return "default";
		
		}
	    return null;
	    
	}


	/**
	 * @param rowFormat the rowFormat to set
	 */
	public void setRowFormat(String rowFormat) {
		switch(rowFormat){
		case "dynamic":rowFormatInt = Row.ROW_FORMAT_INNODB_DYNAMIC;break;
		case "fixed":rowFormatInt = Row.ROW_FORMAT_INNODB_FIXED;break;
		case "compressed":rowFormatInt = Row.ROW_FORMAT_INNODB_COMPRESSED;break;
		case "redundant":rowFormatInt = Row.ROW_FORMAT_INNODB_REDUNDANT;break;
		case "compact":rowFormatInt = Row.ROW_FORMAT_INNODB_COMPACT;break;
		case "default":rowFormatInt = Row.ROW_FORMAT_INNODB_DEFAULT;break;
		default: rowFormatInt = Row.ROW_FORMAT_INNODB_DEFAULT;break;
		}
		
	}


	/**
	 * @return the dataDirectory
	 */
	public String getDataDirectory() {
	    return dataDirectory;
	}


	/**
	 * @param dataDirectory the dataDirectory to set
	 */
	public void setDataDirectory(String dataDirectory) {
	    if(dataDirectory == null || dataDirectory.equals("")){
	    	this.dataDirectory = null;
	    	return;
	    }
	    	
	    this.dataDirectory = dataDirectory;
	}

}
