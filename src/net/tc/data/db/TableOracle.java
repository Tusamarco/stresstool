package net.tc.data.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;

public class TableOracle extends Table {
	
	public TableOracle() {
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
	    
//	    sbHead.append("WHENEVER SQLERROR CONTINUE;\n");
	    sbHead.append("DROP TABLE "
		    + this.getSchemaName() +"."
		    + this.getName() 
		    + " CASCADE CONSTRAINTS PURGE; \n");
	    
	    sbHead.append("CREATE TABLE "
		    + this.getSchemaName() +"."
		    + this.getName() 
		    +"(\n");
/*
#############################
PARTITIONED
    
CREATE TABLE windmills.partition1
(
    id bigserial NOT NULL,
    date date NOT NULL,
    text character varying(255),
    serialn bigserial NOT NULL
) PARTITION BY RANGE (date) 
TABLESPACE test_tb1;
ALTER TABLE windmills.partition1
    OWNER to app1;

CREATE TABLE windmills.partition1_pt1 PARTITION OF windmills.partition1
    FOR VALUES FROM ('2018-08-01') TO ('2018-08-31');
alter table windmills.partition1_pt1 ADD constraint partition1_pt1_pk primary key (date,id);
Create Unique index partition1_pt1_uk on windmills.partition1_pt1 USING btree(serialn);
Create index partition1_pt1_txt on windmills.partition1_pt1 USING btree(text);


CREATE TABLE windmills.partition1_pt2 PARTITION OF windmills.partition1
    FOR VALUES FROM ('2018-09-01') TO ('2018-09-30');
alter table windmills.partition1_pt2 ADD constraint partition1_pt2_pk primary key (date,id);
Create Unique index partition1_pt2_uk on windmills.partition1_pt2 USING btree(serialn);
Create index partition1_pt2_txt on windmills.partition1_pt2 USING btree(text);

    
alter table windmills.partition1_p1 ADD constraint partition1_pt1_pk primary key (date,id);
Create Unique index partition1_pt1_uk on windmills.partition1_pt1 USING btree(serialn);

 * 
 * 
 * 
 */
	    
	    
	    
	    
	    
	    sbTail.append("\n");
	    //FIXME NO storage engine or row format in Postgres; COLLATION is at column level, no char_set, tablespace takes no data dir
	    
//	    sbTail.append(this.getStorageEngine()!=null?"ENGINE="+getStorageEngine() + " ":"");
//	    sbTail.append(this.getDefaultCharacterSet()!=null?"CHARSET="+getDefaultCharacterSet() + " ":"");
//	    sbTail.append(this.getDefaultCollation()!=null?"COLLATE="+getDefaultCollation() + " ":"");
	    
//	    sbTail.append(this.getRowFormat()!=null?"ROW_FORMAT="+getRowFormat() + " ":"");
//	    sbTail.append(this.getDataDirectory()!=null?"DATA DIRECTORY="+getDataDirectory() + " ":"");
	    
	    //sbTail.append(this.getTableSpace()!=null?"TABLESPACE="+getTableSpace() + " ":"");
	    
	    /*
	     * Cycle the attributes
	     */
	    Iterator itAtt = getMetaAttributes().iterator();
	    StringBuffer sbAtt = new StringBuffer();
	    StringBuffer sbConstraints  = new StringBuffer();
	    StringBuffer sbpartition  = new StringBuffer();
	    StringBuffer sbSequences  = new StringBuffer();
	    
	    /*
	     * Add partitioninformation if present
	     */
	    if(this.getPartitionDefinition() !=null){
	    	sbpartition.append(this.getPartitionDefinition().getSQLPartitionDefinition());
		
	    }

	    
	    
	    while (itAtt.hasNext()){
			if (sbAtt.length() > 0)
			    	sbAtt.append(",\n");
			Attribute attribute = (Attribute) this.getMetaAttributes((String)itAtt.next());
			sbAtt.append(""+ this.checkReservedWord(attribute.getName()) +" ");
			String dataTypeName = DataType.getDataTypeStringByIdentifier(attribute.getDataType().getDataTypeId(),ConnectionInformation.ORACLE);
			
			
			if(attribute.getDataType().getDataTypeCategory() != DataType.NUMERIC_CATEGORY) { 
				sbAtt.append(dataTypeName + (attribute.getDataDimension()>0?"("+attribute.getDataDimension()+") ":" ") + " ");
				sbAtt.append(attribute.getDefaultValue()!=null?"DEFAULT " + attribute.getDefaultValue() + " ":" ");
				sbAtt.append(attribute.isNull()?"NULL ":"NOT NULL ");
			}
			else {
				sbAtt.append(dataTypeName + " ");
				sbAtt.append(attribute.getDefaultValue()!=null?"DEFAULT (" + attribute.getDefaultValue() + ") ":" ");
				sbAtt.append(attribute.isNull()?"NULL ":"NOT NULL ");
				if(attribute.isAutoIncrement()) {

//					sbSequences.append("WHENEVER SQLERROR CONTINUE;\n");	
					sbSequences.append("DROP SEQUENCE "+ this.getSchemaName()+ ".SEQ_" + this.getName()+ "_"+ attribute.getName() + ";\n");
					sbSequences.append("CREATE SEQUENCE "+ this.getSchemaName()+ ".SEQ_" + this.getName()+ "_"+ attribute.getName() + "\n");
					sbSequences.append(" START WITH 1 \n");
					sbSequences.append("MAXVALUE 999999999999999999999999999 \n"); 
					sbSequences.append("  MINVALUE 1 \n");
					sbSequences.append("  NOCYCLE \n");
					sbSequences.append("  CACHE 100 \n");
					sbSequences.append("  NOORDER ;\n");

					sbSequences.append("CREATE TRIGGER "+ this.getSchemaName()+ ".TRG_" + this.getName()+ "_"+ attribute.getName() + "\n");
					sbSequences.append(" BEFORE INSERT \n");
					sbSequences.append(" ON "+ this.getSchemaName()+ "." + this.getName()+ " \n");
					sbSequences.append("REFERENCING NEW AS New OLD AS Old \n");
					sbSequences.append("FOR EACH ROW \n");
					sbSequences.append("BEGIN \n");
					sbSequences.append(":new.ID := SEQ_" + this.getName()+ "_"+ attribute.getName() +".nextval@ \n");
					sbSequences.append("END@; \n");
//					sbSequences.append("WHENEVER SQLERROR EXIT FAILURE;\n\n");
				}

			}
			
			
			
//	Cannot do this for now is more complex and I need to see what is the best way
//TODO Implement auto UPDATE column value
//			if(attribute.getOnUpdate()!=null){
//				sbConstraints.append("DROP FUNCTION IF EXISTS "+ this.getSchemaName() +".update_modified_"+this.getName()+"_"+attribute.getName()+"();\n");
//				sbConstraints.append("CREATE FUNCTION "+ this.getSchemaName() +".update_modified_"+this.getName()+"_"+attribute.getName()+"()\n");
//				sbConstraints.append("RETURNS TRIGGER AS $$\n");
//				sbConstraints.append("BEGIN\n");
//				sbConstraints.append("	IF row(NEW.*) IS DISTINCT FROM row(OLD.*) THEN\n");
//				sbConstraints.append("		NEW."+attribute.getName()+" = "+attribute.getSpecialFunction()+"@\n");
//				sbConstraints.append("		RETURN NEW@\n");
//				sbConstraints.append("	ELSE\n");
//				sbConstraints.append("		RETURN OLD@\n");
//				sbConstraints.append("  END IF@\n");
//				sbConstraints.append("END@\n");
//				sbConstraints.append("$$ language 'plpgsql';\n");
//				
//				if(this.getPartitionDefinition() ==null){
//					sbConstraints.append("DROP TRIGGER IF EXISTS TRG_"+ this.getSchemaName() +"_update_modified_"+this.getName()+"_"+attribute.getName()+" ON "
//											+ this.getSchemaName() +"."+this.getName()+";\n");
//					sbConstraints.append("CREATE TRIGGER TRG_"+ this.getSchemaName() +"_update_modified_"+this.getName()+"_"+attribute.getName()+" BEFORE UPDATE ON "
//							+ this.getSchemaName() +"."+this.getName()+" FOR EACH ROW EXECUTE PROCEDURE  "
//							+ this.getSchemaName() +".update_modified_"+this.getName()+"_"+attribute.getName()+"();\n");
//				}
//				else {
//					for(int ia=0; ia < ((PartitionDefinitionPostgres)this.getPartitionDefinition()).getPartitionsName().size(); ia++){
//			    		String partitionName=(String) ((PartitionDefinitionPostgres)this.getPartitionDefinition()).getPartitionsName().get(ia);
//						sbConstraints.append("DROP TRIGGER IF EXISTS TRG_"+ this.getSchemaName() +"_update_modified_"+partitionName+"_"+attribute.getName()+" ON "
//								+ this.getSchemaName() +"."+partitionName+";\n");
//						sbConstraints.append("CREATE TRIGGER TRG_"+ this.getSchemaName() +"_update_modified_"+partitionName+"_"+attribute.getName()+" BEFORE UPDATE ON "
//						+ this.getSchemaName() +"."+partitionName+" FOR EACH ROW EXECUTE PROCEDURE  "
//						+ this.getSchemaName() +".update_modified_"+this.getName()+"_"+attribute.getName()+"();\n");
//					}
//
//				}
//			}
			
			//sbAtt.append(attribute.getOnUpdate()!=null?"ON UPDATE " + attribute.getOnUpdate() + " ":" ");

	    }
	    PrimaryKey pk = this.getPrimaryKey();
	    String strPK = null;
	    if(pk != null && this.getPartitionDefinition() == null){
	    	sbAtt.append(",\n PRIMARY KEY (" + pk.getIndexes().getKeyasUnorderdString() + ")\n");
	    	sbAtt.append(")\n");
	    }
	    if(this.getPartitionDefinition() !=null){
	    	sbAtt.append(")\n" + sbpartition.toString());
	    }

	    
	    
	    sbAtt.append(";\n");
	    sbAtt.append(sbSequences);
	    

	    /*
	     * Cycle the indexes
	     */

	    /*
	     * first PK
	     */
	    StringBuffer sbIdx = new StringBuffer();
	    
	    Iterator itIdx = this.getIndexes().iterator();
	    
	    
	    if(itIdx != null){
//		    sbIdx.append("WHENEVER SQLERROR CONTINUE;\n");	
			while(itIdx.hasNext()){
			    Index idx = this.getIndex((String) itIdx.next());
//			    sbIdx.append("DROP INDEX "+ idx.getName()+ ";\n");
			    if(idx.isUnique()){
				sbIdx.append("CREATE UNIQUE INDEX "+ idx.getName()+"_"+this.getSchemaName() +"_"+ this.getName() + " ");
			    }else{
				sbIdx.append("CREATE INDEX "+ idx.getName()+"_"+this.getSchemaName() +"_"+ this.getName() + " ");
			    }
			    sbIdx.append("ON " + this.getSchemaName() +"."+ this.getName() + " ");
			    sbIdx.append("(" + idx.getColumnsDefinitionAsString() +  ");\n");
			}
//			sbIdx.append("WHENEVER SQLERROR EXIT FAILURE;\n");
	    }
	    
	    
	    sbHead.append(sbAtt.toString());
	    
	    if(sbIdx.length() > 0){
		sbHead.append(sbIdx.toString());
//		if(sbConstraints.length() > 0)
//			sbHead.append(sbConstraints.toString());
	    }

	    
	    /*
	     * Attach the tail table definition
	     * 
	     */
//	    if(sbTail.length()> 0)
//	    	sbHead.append(sbTail.toString());
//	    

	    

		
	    
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
	
	private String checkReservedWord(String word) {
		ArrayList<String> words = new ArrayList<String>(Arrays.asList("ACCESS", "ADD", "ALL", "ALTER", "AND", "ANY", "ARRAYLEN", "AS", "ASC", "AUDIT", "BETWEEN", "BY", "CHAR", "CHECK", "CLUSTER", "COLUMN", "COMMENT", "COMPRESS", "CONNECT", "CREATE", "CURRENT", "DATE", "DECIMAL", "DEFAULT", "DELETE", "DESC", "DISTINCT", "DROP", "ELSE", "EXCLUSIVE", "EXISTS", "FILE", "FLOAT", "FOR", "FROM", "GRANT", "GROUP", "HAVING", "IDENTIFIED", "IMMEDIATE", "IN", "INCREMENT", "INDEX", "INITIAL", "INSERT", "INTEGER", "INTERSECT", "INTO", "IS", "LEVEL", "LIKE", "LOCK", "LONG", "MAXEXTENTS", "MINUS", "MODE", "MODIFY", "NOAUDIT", "NOCOMPRESS", "NOT", "NOTFOUND", "NOWAIT", "NULL", "NUMBER", "OF", "OFFLINE", "ON", "ONLINE", "OPTION", "OR", "ORDER", "PCTFREE", "PRIOR", "PRIVILEGES", "PUBLIC", "RAW", "RENAME", "RESOURCE", "REVOKE", "ROW", "ROWID", "ROWLABEL", "ROWNUM", "ROWS", "SELECT", "SESSION", "SET", "SHARE", "SIZE", "SMALLINT", "SQLBUF", "START", "SUCCESSFUL", "SYNONYM", "SYSDATE", "TABLE", "THEN", "TO", "TRIGGER", "UID", "UNION", "UNIQUE", "UPDATE", "USER", "VALIDATE", "VALUES", "VARCHAR", "VARCHAR2", "VIEW", "WHENEVER", "WHERE", "WITH"));
		if(words.contains(word.toUpperCase())) {
			return "\"" + word + "\"";
		}
		
		return word;
		
	}

}
