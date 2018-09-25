package net.tc.data.db;

import java.util.Iterator;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;

public class TableMySQL extends Table {
	
	public TableMySQL() {
		super();
	}
	
	
//
//	/**
//	 * @param instanceNumber the instanceNumber to set
//	 */
//	public void setInstanceNumber(int instanceNumber) {
//	    this.instanceNumber = instanceNumber;
//	}
	/**
	 * Table deploy method
	 * @return
	 * @throws StressToolConfigurationException 
	 */
	@Override
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
		if (sbAtt.length() > 0)
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
		    if(sbIdx.length() > 0)
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
	    if(sbIdx.length() > 0){
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


//	/**
//	 * @return the tableSpace
//	 */
//	public String getTableSpace() {
//	    return tableSpace;
//	}
//
//
//	/**
//	 * @param tableSpace the tableSpace to set
//	 */
//	public void setTableSpace(String tableSpace) {
//	    if(tableSpace == null || tableSpace.equals("")){
//	    	this.tableSpace = null;
//	    	return;
//	    }
//
//	    
//	    this.tableSpace = tableSpace;
//	}
//

	/**
	 * @return the rowFormat
	 */
	@Override
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
	@Override
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

}
