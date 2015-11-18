package net.tc.data.db;

import net.tc.utils.SynchronizedMap;

public class Table {
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
	private boolean hasSubPartition = false;
	private boolean autoincrement = false;
	private String schemaName = null;
	private long autoincrementValue = 0 ;
	private SynchronizedMap metaAttributes = null;

	
	
	public Table() {
	    rows = new SynchronizedMap(0);
	    indexes = new SynchronizedMap(0);
	    metaAttributes = new SynchronizedMap(0);
	}
	
	
	public SynchronizedMap getMetaAttributes() {
		return metaAttributes;
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

	public void setMetaAttributes(SynchronizedMap metaAttributes) {
		metaAttributes = metaAttributes;
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
		return autoincrementValue;
	}

	public void setAutoincrementValue(long autoincrementValue) {
		this.autoincrementValue = autoincrementValue;
	}

	public boolean isAutoincrement() {
		return autoincrement;
	}

	public void setAutoincrement(boolean autoincrement) {
		this.autoincrement = autoincrement;
	}

	public String getSchemaName() {
		return schemaName;
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


}
