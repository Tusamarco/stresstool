package net.tc.data.db;


public class SubPartition {
	
	private String tableName = null;
	private String partitionName = null;
	private String name = null;
	private String subPartitionType = null;
	private String partitionDeclaration = null;
	private String dataDirectory = null;
	private String indexDirectory = null;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPartitionName() {
		return partitionName;
	}
	public void setPartitionName(String partitionName) {
		this.partitionName = partitionName;
	}
	public String getSubPartitionType() {
		return subPartitionType;
	}
	public void setSubPartitionType(String subPartitionType) {
		this.subPartitionType = subPartitionType;
	}
	public String getPartitionDeclaration() {
		return partitionDeclaration;
	}
	public void setPartitionDeclaration(String partitionDeclaration) {
		this.partitionDeclaration = partitionDeclaration;
	}
	public String getDataDirectory() {
		return dataDirectory;
	}
	public void setDataDirectory(String dataDirectory) {
		this.dataDirectory = dataDirectory;
	}
	public String getIndexDirectory() {
		return indexDirectory;
	}
	public void setIndexDirectory(String indexDirectory) {
		this.indexDirectory = indexDirectory;
	}
	
	
}
