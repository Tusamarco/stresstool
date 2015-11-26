package net.tc.data.db;

import net.tc.utils.SynchronizedMap;

public class Row {
	private String name = null;
	
	public static final int ROW_FORMAT_INNODB_DEFAULT=0;
	public static final int ROW_FORMAT_INNODB_DYNAMIC=1;
	public static final int ROW_FORMAT_INNODB_FIXED=2;
	public static final int ROW_FORMAT_INNODB_COMPRESSED=3;
	public static final int ROW_FORMAT_INNODB_REDUNDANT=4;
	public static final int ROW_FORMAT_INNODB_COMPACT=5;
	
	
	public SynchronizedMap getAttributes() {
		return attributes;
	}

	public void setAttributes(SynchronizedMap attributes) {
		this.attributes = attributes;
	}

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}

	private SynchronizedMap attributes = null;
	private PrimaryKey primaryKey = null;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	

}
