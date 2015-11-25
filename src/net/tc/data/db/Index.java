package net.tc.data.db;

import java.util.ArrayList;
import java.util.Comparator;

import net.tc.comparators.SortableByOrder;

public class Index implements Comparator {

	private String tableName = null;
	private boolean allowNull = false;
	private String name = null;
	private int seqInIndex = 1;
	private ArrayList columnsDefinition = null;
	private String collation = null ;
	private int cardinality = 0;
	private int subPart = 0; //number of characters included in the index 0 means all
	private boolean packed = false;
	private boolean hasNulls = false;
	private String indexType = null;
	private boolean isUnique = false;
	private boolean isPrimaryKey = false;
			
	public boolean isUnique() {
		return isUnique;
	}
	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}
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
	public int getSeqInIndex() {
		return seqInIndex;
	}
	public void setSeqInIndex(int seqInIndex) {
		this.seqInIndex = seqInIndex;
	}
	public ArrayList<String> getColumnsDefinition() {
		return columnsDefinition;
	}
	public void setColumnsDefinition(ArrayList<String> columnsDefinition) {
		this.columnsDefinition = columnsDefinition;
	}

	public void setColumnsDefinition(String columnDefinition) {
		this.columnsDefinition.add(columnDefinition);
	}
	
	public String getCollation() {
		return collation;
	}
	public void setCollation(String collation) {
		this.collation = collation;
	}
	public int getCardinality() {
		return cardinality;
	}
	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}
	public int getSubPart() {
		return subPart;
	}
	public void setSubPart(int subPart) {
		this.subPart = subPart;
	}
	public boolean isPacked() {
		return packed;
	}
	public void setPacked(boolean packed) {
		this.packed = packed;
	}
	public boolean isHasNulls() {
		return hasNulls;
	}
	public void setHasNulls(boolean hasNulls) {
		this.hasNulls = hasNulls;
	}
	public String getIndexType() {
		return indexType;
	}
	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}
	/**
	 * @return the isPrimaryKey
	 */
	public boolean isPrimaryKey() {
	    return isPrimaryKey;
	}
	/**
	 * @param isPrimaryKey the isPrimaryKey to set
	 */
	public void setPrimaryKey(boolean isPrimaryKey) {
	    this.isPrimaryKey = isPrimaryKey;
	}
	
    public int compare(Object o1, Object o2) {
	if (o1 != null && o2 != null) {
	    int a = 0;
	    int b = 0;

	    try {
		a = ((Index) o1).getSeqInIndex();
		b = ((Index) o2).getSeqInIndex();
		return a - b;

	    } catch (NumberFormatException ex) {
		String as = (String) o1;
		String bs = (String) o2;
		return as.compareTo(bs);
	    }

	} else
	    return 0;
    }
    public String getColumnsDefinitionAsString(){
	if(this.getColumnsDefinition() != null){
	    StringBuffer def = new StringBuffer();
	    for (String sr : this.getColumnsDefinition() ){
		if(def.length() > 1)
		    def.append(", ");
		def.append(sr);
	    }
	    return def.toString();
	}
	
	return null;
    }
	
}
