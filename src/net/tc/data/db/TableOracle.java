package net.tc.data.db;

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
	    
//	    sbHead.append("/* CREATE TABLE "
//		    + this.getSchemaName() +"."
//		    + this.getName() 
//		    + " */\n");
	    
	    sbHead.append("CREATE TABLE IF NOT EXISTS "
		    + this.getSchemaName() +"."
		    + this.getName() 
		    +"(\n");
//FIXME disable triggers by tab;e
/**
 * NOTE
 * a method can be to add the list of tables at create time and after creation immediately add the trigger disable.
 * then at the end of the process activate all of them.
 * BEGIN;
 * ALTER TABLE b DISABLE TRIGGER ALL;
 * -- now the RI over table b is disabled
 * ALTER TABLE b ENABLE TRIGGER ALL;
 * COMMIT;
 * 
 * THIS IS HOW PG may work
 * 
 * #############################
NOT partitioned table


CREATE TABLE "OPTIMUM"."REF_FLUX_INTERFACE" 
   (    "REF_FLUX_INTERFACE_ID" NUMBER(3,0) NOT NULL ENABLE, 
        "LIBELLE" VARCHAR2(50 BYTE) NOT NULL ENABLE, 
        "ORDRE" NUMBER(3,0) DEFAULT 1 NOT NULL ENABLE, 
        "CODE" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
        "REF_INTERFACE_ID" NUMBER(3,0) NOT NULL ENABLE, 
        "FICHIER" VARCHAR2(50 BYTE) NOT NULL ENABLE, 
        "ENTRANT" NUMBER(1,0) DEFAULT 0 NOT NULL ENABLE, 
        "SORTANT" NUMBER(1,0) DEFAULT 0 NOT NULL ENABLE, 
        "EXTENSION_FICHIER" VARCHAR2(4 BYTE), 
        "DATE_FICHIER" VARCHAR2(20 BYTE), 
        "SEPARATEUR_FICHIER" VARCHAR2(1 BYTE), 
        "DERNIER_TRAITEMENT_DATE" DATE, 
        "FICHIER_ACQUITTEMENT" VARCHAR2(50 BYTE), 
        "DERNIER_NUM_SEQUENCE" NUMBER(4,0) DEFAULT 0
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 65536 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "TAB_REF_64K" ;

 CREATE SEQUENCE  "OPTIMUM"."SEQ_REF_COMMUNE"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
CREATE OR REPLACE TRIGGER dept_bir 
BEFORE INSERT ON departments 
FOR EACH ROW

BEGIN
  SELECT dept_seq.NEXTVAL
  INTO   :new.id
  FROM   dual;
END;


OR
CREATE TABLE MAPS
(
  MAP_ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,
  MAP_NAME VARCHAR(24) NOT NULL,
  UNIQUE (MAP_ID, MAP_NAME)
);


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
	    
	    sbTail.append(this.getTableSpace()!=null?"TABLESPACE="+getTableSpace() + " ":"");
	    
	    /*
	     * Cycle the attributes
	     */
	    Iterator itAtt = getMetaAttributes().iterator();
	    StringBuffer sbAtt = new StringBuffer();
	    StringBuffer sbConstraints  = new StringBuffer();
	    StringBuffer sbpartition  = new StringBuffer();
	    
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
			sbAtt.append(""+ attribute.getName() +" ");
			String dataTypeName = DataType.getDataTypeStringByIdentifier(attribute.getDataType().getDataTypeId(),ConnectionInformation.ORACLE);
			
//TODO I am here and need to work on 
//			datatypes first
//			then on auto increment with sequences
//			then partitions
			
			if(dataTypeName.equals(DataType.BIGINT) && attribute.isAutoIncrement()){
				dataTypeName =  "BIGSERIAL";
			}
			else if(attribute.isAutoIncrement()){
				dataTypeName =  "SERIAL";
			}
			if(attribute.getDataType().getDataTypeCategory() != DataType.NUMERIC_CATEGORY) 
				sbAtt.append(dataTypeName + (attribute.getDataDimension()>0?"("+attribute.getDataDimension()+") ":" ") + " ");
			else
				sbAtt.append(dataTypeName + " ");
			
			sbAtt.append(attribute.isNull()?"NULL ":"NOT NULL ");
			sbAtt.append(attribute.getDefaultValue()!=null?"DEFAULT " + attribute.getDefaultValue() + " ":" ");
			
			if(attribute.getOnUpdate()!=null){
				sbConstraints.append("DROP FUNCTION IF EXISTS "+ this.getSchemaName() +".update_modified_"+this.getName()+"_"+attribute.getName()+"();\n");
				sbConstraints.append("CREATE FUNCTION "+ this.getSchemaName() +".update_modified_"+this.getName()+"_"+attribute.getName()+"()\n");
				sbConstraints.append("RETURNS TRIGGER AS $$\n");
				sbConstraints.append("BEGIN\n");
				sbConstraints.append("	IF row(NEW.*) IS DISTINCT FROM row(OLD.*) THEN\n");
				sbConstraints.append("		NEW."+attribute.getName()+" = "+attribute.getSpecialFunction()+"@\n");
				sbConstraints.append("		RETURN NEW@\n");
				sbConstraints.append("	ELSE\n");
				sbConstraints.append("		RETURN OLD@\n");
				sbConstraints.append("  END IF@\n");
				sbConstraints.append("END@\n");
				sbConstraints.append("$$ language 'plpgsql';\n");
				
				if(this.getPartitionDefinition() ==null){
					sbConstraints.append("DROP TRIGGER IF EXISTS TRG_"+ this.getSchemaName() +"_update_modified_"+this.getName()+"_"+attribute.getName()+" ON "
											+ this.getSchemaName() +"."+this.getName()+";\n");
					sbConstraints.append("CREATE TRIGGER TRG_"+ this.getSchemaName() +"_update_modified_"+this.getName()+"_"+attribute.getName()+" BEFORE UPDATE ON "
							+ this.getSchemaName() +"."+this.getName()+" FOR EACH ROW EXECUTE PROCEDURE  "
							+ this.getSchemaName() +".update_modified_"+this.getName()+"_"+attribute.getName()+"();\n");
				}
				else {
					for(int ia=0; ia < ((PartitionDefinitionPostgres)this.getPartitionDefinition()).getPartitionsName().size(); ia++){
			    		String partitionName=(String) ((PartitionDefinitionPostgres)this.getPartitionDefinition()).getPartitionsName().get(ia);
						sbConstraints.append("DROP TRIGGER IF EXISTS TRG_"+ this.getSchemaName() +"_update_modified_"+partitionName+"_"+attribute.getName()+" ON "
								+ this.getSchemaName() +"."+partitionName+";\n");
						sbConstraints.append("CREATE TRIGGER TRG_"+ this.getSchemaName() +"_update_modified_"+partitionName+"_"+attribute.getName()+" BEFORE UPDATE ON "
						+ this.getSchemaName() +"."+partitionName+" FOR EACH ROW EXECUTE PROCEDURE  "
						+ this.getSchemaName() +".update_modified_"+this.getName()+"_"+attribute.getName()+"();\n");
					}

				}
				
				
				
			}
			
			//sbAtt.append(attribute.getOnUpdate()!=null?"ON UPDATE " + attribute.getOnUpdate() + " ":" ");

	    }
	    PrimaryKey pk = this.getPrimaryKey();
	    String strPK = null;
	    if(pk != null && this.getPartitionDefinition() == null){
	    	sbAtt.append(",\n PRIMARY KEY (" + pk.getIndexes().getKeyasUnorderdString() + ")\n");
	    	sbAtt.append(")\n");
	    }
	    if(this.getPartitionDefinition() !=null){
	    	sbAtt.append(")\n" + ((PartitionDefinitionPostgres)this.getPartitionDefinition()).getSQLMainTablePartitionDefinition());
	    }

	    
	    
	    sbAtt.append(";\n");
	    
	    

	    /*
	     * Cycle the indexes
	     */

	    /*
	     * first PK
	     */
	    StringBuffer sbIdx = new StringBuffer();
	    
	    Iterator itIdx = this.getIndexes().iterator();
	    
	    if(this.getPartitionDefinition() !=null){
	    	for(int ia=0; ia < ((PartitionDefinitionPostgres)this.getPartitionDefinition()).getPartitionsName().size(); ia++){
	    		String partitionName=(String) ((PartitionDefinitionPostgres)this.getPartitionDefinition()).getPartitionsName().get(ia);
	    		sbIdx.append("ALTER TABLE "+ partitionName +" ADD PRIMARY KEY (" + pk.getIndexes().getKeyasUnorderdString() + ");\n");
	    	}
	    	
	    }
	    
	    if(itIdx != null){
		while(itIdx.hasNext()){
			if(this.getPartitionDefinition() ==null){
			    Index idx = this.getIndex((String) itIdx.next());
			    if(idx.isUnique()){
				sbIdx.append("CREATE UNIQUE INDEX if not exists "+ idx.getName() + " ");
			    }else{
				sbIdx.append("CREATE INDEX if not exists "+ idx.getName() + " ");
			    }
			    sbIdx.append("ON " + this.getSchemaName() +"."+ this.getName() + " ");
			    sbIdx.append("USING BTREE(" + idx.getColumnsDefinitionAsString() +  ");\n");
			}
			else{
				Index idx = this.getIndex((String) itIdx.next());
				for(int ia=0; ia < ((PartitionDefinitionPostgres)this.getPartitionDefinition()).getPartitionsName().size(); ia++){
					String partitionName=(String) ((PartitionDefinitionPostgres)this.getPartitionDefinition()).getPartitionsName().get(ia);				    
				    if(idx.isUnique()){
					sbIdx.append("CREATE UNIQUE INDEX if not exists "+ idx.getName()+ "_" + partitionName + " ");
				    }else{
					sbIdx.append("CREATE INDEX if not exists "+ idx.getName()+ "_" + partitionName  + " ");
				    }
				    sbIdx.append("ON " + this.getSchemaName() +"."+ partitionName + " ");
				    sbIdx.append("USING BTREE(" + idx.getColumnsDefinitionAsString() +  ");\n");
				    
				}
				
			}
		    
		}
	    }
	    
	    sbHead.append(sbAtt.toString());
	    
	    if(sbpartition.length() > 0)
	    	sbHead.append(sbpartition.toString());
	    if(sbIdx.length() > 0){
//		sbHead.append("\n");
		sbHead.append(sbIdx.toString());
		if(sbConstraints.length() > 0)
			sbHead.append(sbConstraints.toString());
	    }

	    
	    /*
	     * Attach the tail table definition
	     * 
	     */
	    if(sbTail.length()> 0)
	    	sbHead.append(sbTail.toString());
	    

	    

		
	    
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
