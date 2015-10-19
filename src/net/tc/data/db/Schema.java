package net.tc.data.db;

import net.tc.utils.SynchronizedMap;

public class Schema {
	private String name = null; 
	private String defaultCharacterSet = null;
	private String defaultCollation = null;
	private int size = 0;
	private SynchronizedMap tables = null;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public SynchronizedMap getTables() {
		return tables;
	}
	public void setTables(SynchronizedMap tables) {
		this.tables = tables;
	}
	public Table getTable( String tableName) {
		if(tables.get(tableName) != null)
			return (Table )tables.get(tableName);
		return null;
	}
	public void setTable(Table table) {
		if(table != null && table.getName() != null){
			this.tables.put(table.getName(), table);
		}
	}
	


}
