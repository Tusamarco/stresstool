package net.tc.data.db;

import net.tc.utils.SynchronizedMap;

public class Partition {
	private String name = null;
	private String valueDeclaration = null;
	private String storageEngine = null;
	private SynchronizedMap subPartitions = null;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValueDeclaration() {
		return valueDeclaration;
	}
	public void setValueDeclaration(String valueDeclaration) {
		this.valueDeclaration = valueDeclaration;
	}
	public String getStorageEngine() {
		return storageEngine;
	}
	public void setStorageEngine(String storageEngine) {
		this.storageEngine = storageEngine;
	}
	public SynchronizedMap getSubPartitions() {
		return subPartitions;
	}
	public SubPartition getSubPartition(String name) {
		if(name != null
				&& !name.equals("")
				&& subPartitions != null
				&& subPartitions.get(name) != null)
		{
			return (SubPartition) subPartitions.get(name);
		}
		return null;
	}
	
	public void setSubPartitions(SubPartition subPartition) {
		if(subPartition != null
				&& subPartition.getName() != null
				&& !subPartition.getName().equals("")
				&& subPartitions != null){
			subPartitions.put(subPartition.getName(), subPartition);
		}
	}
	
}
