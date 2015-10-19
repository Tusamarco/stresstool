package net.tc.data.db;

import net.tc.utils.SynchronizedMap;

public class PartitionDefinition {
	
	static final String PARTITION_TYPE_RANGE = "RANGE";
	static final String PARTITION_TYPE_LIST = "LIST";
	static final String PARTITION_TYPE_COLUMNS = "COLUMNS";
	static final String PARTITION_TYPE_HASH = "HASH";
	static final String PARTITION_TYPE_KEY = "KEY";
	
	private String tableName = null;
	private String partitionType = null;
	private int partitionsSize = 0;
	private SynchronizedMap attributes = null;
	private String partitionDeclaration = null;
	private SynchronizedMap partitions = null;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPartitionType() {
		return partitionType;
	}
	public void setPartitionType(String partitionType) {
		this.partitionType = partitionType;
	}
	public int getPartitionsSize() {
		return partitionsSize;
	}
	public void setPartitionsSize(int partitionsSize) {
		this.partitionsSize = partitionsSize;
	}
	public SynchronizedMap getAttributes() {
		return attributes;
	}
	public void setAttributes(SynchronizedMap attributes) {
		this.attributes = attributes;
	}
	public String getPartitionDeclaration() {
		return partitionDeclaration;
	}
	public void setPartitionDeclaration(String partitionDeclaration) {
		this.partitionDeclaration = partitionDeclaration;
	}
	public SynchronizedMap getPartitions() {
		return partitions;
	}
	public void setPartitions(SynchronizedMap partitions) {
		this.partitions = partitions;
	}
	
	
 }
