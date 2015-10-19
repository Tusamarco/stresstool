package net.tc.data.db;


import net.tc.utils.SynchronizedMap;

public class PrimaryKey {

	private String tableName = null;
	private boolean allowNull = false;
	private String name = null;
	private SynchronizedMap indexes = null;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public boolean isAllowNull() {
		return allowNull;
	}
	public void setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	
	
}
