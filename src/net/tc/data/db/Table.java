package net.tc.data.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import net.tc.data.generic.DataObject;
import net.tc.stresstool.PerformanceEvaluator;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.actions.StressAction;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;

public class Table {
	/**
	 * NOTE
	 * Table class is a superclass implementing common methods cross DBType
	 */
    	public static final Integer TABLE_PARENT=0;
    	public static final Integer TABLE_CHILD=1;
	protected String name = null;
	protected int rowsNumber = 0;
	protected int fillFactor = 0;
	protected String defaultCharacterSet = null;
	protected String defaultCollation = null;
	protected String storageEngine = null;
	protected int rowDimension = 0; //dimension in bytes
	protected SynchronizedMap rows = null;
	protected int attributesNumber = 0 ;
	protected PrimaryKey primaryKey = null;
	protected SynchronizedMap indexes = null;
	protected PartitionDefinition partitionDefinition = null;
	protected boolean hasPartition = false;
	protected boolean multiple = false; 
	protected boolean hasSubPartition = false;
	protected boolean autoincrement = false;
	protected String schemaName = null;
	protected long autoincrementValue = 0 ;
	protected SynchronizedMap metaAttributes = null;
	protected String parentTable = null;
	protected int instances = 1;
	protected int instanceNumber = 0;
	protected String tableSpace = null;
	protected int  rowFormatInt = 0;
	protected String dataDirectory = null;
	protected ConditionCollection whereCondition_s = new ConditionCollection();
	protected ConditionCollection whereCondition_u = new ConditionCollection();
	protected ConditionCollection whereCondition_d = new ConditionCollection();
	protected ConditionCollection selectCondition_S = new ConditionCollection();
	protected ArrayList<Attribute> attribsWhereS = new ArrayList();
	protected ArrayList<Attribute> attribsWhereU = new ArrayList();
	protected ArrayList<Attribute> attribsWhereD = new ArrayList();
	protected int rangeLength = 50;
	protected ArrayList<Table> joinTables = new ArrayList(); 
	protected String updateSetAttributes = null;
	protected String insertAttributes = null;
	
	public Table() {
	    rows = new SynchronizedMap(0);
	    indexes = new SynchronizedMap(0);
	    metaAttributes = new SynchronizedMap(0);
	}
	
	
	public SynchronizedMap getMetaAttributes() {
		return this.metaAttributes;
	}

	public Attribute getMetaAttributes(String name) {
		if(name != null
				&& !name.equals("")
				&& metaAttributes != null
				&& metaAttributes.containsKey(name)){
			return (Attribute) metaAttributes.get(name);
		}
		return null;
	}

	public void setMetaAttributes(Map metaAttributes) {
		this.metaAttributes = (SynchronizedMap) metaAttributes;
	}

	public void setMetaAttribute(Attribute attribute) {
		if(attribute != null
				&& attribute.getName() != null
				&& !attribute.getName().equals("")
				&& metaAttributes != null){
			metaAttributes.put(attribute.getName(), attribute);
		}
		
	}
	
	
	public long getAutoincrementValue() {
		return this.autoincrementValue;
	}

	public void setAutoincrementValue(long autoincrementValue) {
		this.autoincrementValue = autoincrementValue;
	}

	public boolean isAutoincrement() {
		return this.autoincrement;
	}

	public void setAutoincrement(boolean autoincrement) {
		this.autoincrement = autoincrement;
	}

	public String getSchemaName() {
		return this.schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public boolean isHasPartition() {
		return hasPartition;
	}

	public void setHasPartition(boolean hasPartition) {
		this.hasPartition = hasPartition;
	}

	public boolean isHasSubPartition() {
		return hasSubPartition;
	}

	public void setHasSubPartition(boolean hasSubPartition) {
		this.hasSubPartition = hasSubPartition;
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
	
	
	
	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}
	public PartitionDefinition getPartitionDefinition() {
		return partitionDefinition;
	}
	public void setPartitionDefinition(PartitionDefinition partitionDefinition) {
		this.partitionDefinition = partitionDefinition;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRowsNumber() {
		return rowsNumber;
	}
	public void setRowsNumber(int rowsNumber) {
		this.rowsNumber = rowsNumber;
	}
	public int getFillFactor() {
		return fillFactor;
	}
	public void setFillFactor(int fillFactor) {
		this.fillFactor = fillFactor;
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
	public String getStorageEngine() {
		return storageEngine;
	}
	public void setStorageEngine(String storageEngine) {
		this.storageEngine = storageEngine;
	}
	public int getRowDimension() {
		return rowDimension;
	}
	public void setRowDimension(int rowDimension) {
		this.rowDimension = rowDimension;
	}
	public SynchronizedMap getRows() {
		return rows;
	}
	public Row getRows(String rowName) {
		if(rowName != null 
				&& !rowName.equals("")
				&& rows !=null
				&& rows.get(rowName) != null)
			return (Row) rows.get(rowName);
		
		return null;
	}

	public void setRows(SynchronizedMap rows) {
		this.rows = rows;
	}
	public void setRow(Row row) {
		if(row != null
				&& row.getName() != null
				&& !row.getName().equals("")
				&& rows != null){
			rows.put(row.getName(), row);
		}
		
	}
	public int getAttributesNumber() {
		return attributesNumber;
	}
	public void setAttributesNumber(int attributesNumber) {
		this.attributesNumber = attributesNumber;
	}


	/**
	 * @return the multiple
	 */
	public boolean isMultiple() {
	    return multiple;
	}


	/**
	 * @param multiple the multiple to set
	 */
	public void setMultiple(boolean multiple) {
	    this.multiple = multiple;
	}


	/**
	 * @return the parentTable
	 */
	public String getParentTable() {
	    return parentTable;
	}


	/**
	 * @param parentTable the parentTable to set
	 */
	public void setParentTable(String parentTable) {
	    this.parentTable = parentTable;
	}


	/**
	 * @return the instances
	 */
	public int getInstances() {
	    return instances;
	}


	/**
	 * @param instances the instances to set
	 */
	public void setInstances(int instances) {
	    this.instances = instances;
	}


	/**
	 * @return the instanceNumber
	 */
	public int getInstanceNumber() {
	    return instanceNumber;
	}


	/**
	 * @param instanceNumber the instanceNumber to set
	 */
	public void setInstanceNumber(int instanceNumber) {
	    this.instanceNumber = instanceNumber;
	}
	/**
	 * Table deploy method
	 * @return
	 * @throws StressToolConfigurationException 
	 */
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


	/**
	 * @return the tableSpace
	 */
	public String getTableSpace() {
	    return tableSpace;
	}


	/**
	 * @param tableSpace the tableSpace to set
	 */
	public void setTableSpace(String tableSpace) {
	    if(tableSpace == null || tableSpace.equals("")){
	    	this.tableSpace = null;
	    	return;
	    }

	    
	    this.tableSpace = tableSpace;
	}


	/**
	 * @return the rowFormat
	 */
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


	/**
	 * @return the dataDirectory
	 */
	public String getDataDirectory() {
	    return dataDirectory;
	}


	/**
	 * @param dataDirectory the dataDirectory to set
	 */
	public void setDataDirectory(String dataDirectory) {
	    if(dataDirectory == null || dataDirectory.equals("")){
	    	this.dataDirectory = null;
	    	return;
	    }
	    	
	    this.dataDirectory = dataDirectory;
	}


	/**
	 * @return the whereCondition
	 */
	public String getWhereConditionS() {
	  return whereCondition_s.getCondition().getCondition();
	}


	/**
	 * @param condCol the whereCondition to set
	 */
	public void setWhereConditionS(ConditionCollection condCol) {
	  this.whereCondition_s = condCol;
	}
	
	public String parseWhere(int sqlType){
	    long performanceTimeStart = 0;
		try {if (StressTool.getLogProvider().getLogger(LogProvider.LOG_PACTIONS).isInfoEnabled()) {performanceTimeStart=System.nanoTime();}} catch (StressToolConfigurationException e1) {e1.printStackTrace();}

		
	  	String whereCondition = this.getWhereCondition(sqlType);
	  	ArrayList<Attribute> attribsWhere = new ArrayList();
	  	
		if(whereCondition == null || whereCondition.equals(""))
			return null;
		
		parseAttributeWhere(whereCondition, attribsWhere);
		
		
		
		if(attribsWhere.size() >0){
			for(Object attrib : (Object[]) (attribsWhere.toArray())){
			  int stringLength = 0 ;
			  int attribRangeLenght = this.getRangeLength();
			  String attName = ((Attribute)attrib).getName();
			  if(((Attribute)attrib).getUpperLimit() ==0){
//				  ((Attribute)attrib).setUpperLimit(((Long)((Attribute)attrib).getValue())>0?(Long)(((Attribute)attrib).getValue()):0 );
			  }

			  if(whereCondition.indexOf("#?" + attName + "_RANGE_OPTION_") >0){
				  	String catchme = ""; 
				  	try{		
				  	catchme = whereCondition.substring(
				  			whereCondition.indexOf("#?" + attName + "_RANGE_OPTION_") 
				  			+ ("#?" + attName+ "_RANGE_OPTION_").length() 
				  			, whereCondition.indexOf("?" +attName +"#")+(2 + attName.length()) ); //, whereCondition.length());
				  	
				  	
				  	
				  	}catch(StringIndexOutOfBoundsException d){
						  System.out.println("X " + whereCondition + "  " +whereCondition.length());
						  System.out.println("A #?" + attName + "_RANGE_OPTION_" +"  "+ whereCondition.indexOf("#?" + attName + "_RANGE_OPTION_") );
						  System.out.println("B " + ("#?" + attName + "_RANGE_OPTION_").length());
						  System.out.println("C " + (whereCondition.indexOf("?" +attName +"#")+(2 + attName.length())));
						  d.printStackTrace();
				  		
				  	}
				  	
				  	String condition= catchme.substring(0,catchme.indexOf("?"+attName+"#"));
				  	
				  	if(condition.indexOf("|")>0 ){
				  		attribRangeLenght = Integer.parseInt(condition.substring((condition.indexOf("|")+1),condition.length()));
				  		condition = condition.substring(0,condition.indexOf("|"));
				  	}
				  	
				  	catchme = (String) ((StressTool.getValueProvider().getValueForRangeOption(this,((Attribute)attrib),condition,attribRangeLenght)).toString());	
				  	whereCondition = whereCondition.replaceFirst("#"+ attName +"#", "");
				  	String pre = whereCondition.substring(0, whereCondition.indexOf("#?" + attName + "_RANGE_OPTION_"));
				  	String post = whereCondition.substring((whereCondition.indexOf("?" + attName + "#")+(2 + attName.length())),whereCondition.length());
//				  	post = post.substring((post.indexOf("?#") + 2),post.length());
				  	whereCondition = pre + " " + catchme + " " + post;
				  	
				}
				else{
				  whereCondition = whereCondition.replaceFirst("#"+ attName+"#", this.getName() + "." + attName);
				  String condition = null;

				  if(whereCondition.indexOf("#?") > 0){
					  try{
						  	condition = whereCondition.substring(
							  whereCondition.indexOf("#?"+ attName),
							  (whereCondition.indexOf("?"+attName+"#") 
									  + (2 
									  + attName.length()
									  )
									)
							  );
					  }
					  catch(Exception ex){
						  System.out.println(whereCondition);
						  ex.printStackTrace();
					  }
				  }
				  else
					  condition = whereCondition;

				  //				  String length = whereCondition.substring((whereCondition.indexOf("#?"+ ((Attribute)attrib).getName() +"?"+attName+"#")), whereCondition.length());
				  if(condition.indexOf("|") > 0 ){
					  String nLength = condition.substring(condition.indexOf("|") + 1,condition.indexOf("?"+attName+"#")) ;
					  stringLength = Integer.parseInt(nLength);
					  ((Attribute)attrib).setValue(StressTool.getValueProvider().provideValue(((Attribute)attrib).getDataType(), new Long(stringLength).longValue()));
				  }
				  else
					  ((Attribute)attrib).setValue(StressTool.getValueProvider().provideValue(((Attribute)attrib).getDataType(), new Long(((Attribute)attrib).getUpperLimit()).longValue()));
				  
//				  if(length.charAt(0) == '|'){
//					length = length.substring(1,length.indexOf("?"+attName+"#"));
//					stringLength = Integer.parseInt(length);
//				  }
					
				  String value = ((Attribute)attrib).getValueAsString(stringLength).replace("\"", "");
				  if(stringLength < 1){
				  	whereCondition = whereCondition.replaceFirst("#\\?"+ attName +"\\?"+ attName+"#",value);
				  }
				  else{
					whereCondition =  whereCondition.replaceFirst("#\\?"+ attName +"\\|", "\"" + value + "%\"|");
					whereCondition =  whereCondition.replaceFirst("\\|" + stringLength + "\\?"+attName+"#", "");
				  }
				}
			}

			
		}
		
		executionPerformance(performanceTimeStart," PARSE WHERE ");
		return whereCondition;
	}


	public void parseAttributeWhere(String whereCondition, ArrayList<Attribute> attribsWhere) {
		/*
		 * get first the list of attribs used in the where
		 */
		for(Object attrib: (Object[])this.getMetaAttributes().getValuesAsArrayOrderByKey()){
//			System.out.println("---------- " + ((Attribute)attrib).getName());
			if(whereCondition != null && ((Attribute)attrib) != null &&
					whereCondition.indexOf("#" + ((Attribute)attrib).getName() + "#") > -1){
//				System.out.println("---------- 2 " + ((Attribute)attrib).getName());
				attribsWhere.add((Attribute)attrib) ;
				
			}
		}
	}


	public String getSelectCondition() {
		return selectCondition_S.getCondition().getCondition();
	}


	public void setSelectCondition_S(ConditionCollection condCol) {
		this.selectCondition_S = condCol;
	}

	public ConditionCollection getSelectCondition_S() {
		return selectCondition_S;
	}

	
	public String getWhereConditionU() {
		return whereCondition_u.getCondition().getCondition();
	}


	public void setWhereConditionU(ConditionCollection condCol) {
		this.whereCondition_u = condCol;
	}


	public String getWhereConditionD() {
		return whereCondition_d.getCondition().getCondition();
	}


	public void setWhereConditionD(ConditionCollection condCol) {
		this.whereCondition_d = condCol;
	}
	public String getWhereCondition(int slqType) {
		  switch (slqType){
			case DataObject.SQL_INSERT:return null; 
			case DataObject.SQL_READ:return this.getWhereConditionS();
			case DataObject.SQL_UPDATE: return this.getWhereConditionU();
			case DataObject.SQL_DELETE: return this.getWhereConditionD();
		
		  }
		
		return null;
	}


	public int getRangeLength() {
		return rangeLength;
	}


	public void setRangeLength(int rangeLength) {
		this.rangeLength = rangeLength;
	}
	
	public String parseSelectCondition(){
		if(selectCondition_S == null || selectCondition_S.getCondition().equals(""))
			return null;
		String[] selects=this.getSelectCondition().split(",");
		StringBuffer sb = new StringBuffer();
		for(String select:selects ){
			if(sb.length() > 0)
				sb.append(",");
			sb.append(this.getName() + "."+ select);
			
		}
		return sb.toString();
	}


	public ArrayList<Table> getJoinTables() {
		return joinTables;
	}


	public void setJoinTables(ArrayList<Table> joinTables) {
		this.joinTables = joinTables;
	}


	public String getUpdateSetAttributes() {
		return updateSetAttributes;
	}


	public void setUpdateSetAttributes(String updateSetAttributes) {
		this.updateSetAttributes = updateSetAttributes;
	}


	public String getInsertAttributes() {
		return insertAttributes;
	}


	public void setInsertAttributes(String insertAttributes) {
		this.insertAttributes = insertAttributes;
	}


	public ConditionCollection getWhereCondition_s() {
		return whereCondition_s;
	}


	public void setWhereCondition_s(ConditionCollection whereCondition_s) {
		this.whereCondition_s = whereCondition_s;
	}


	public ConditionCollection getWhereCondition_u() {
		return whereCondition_u;
	}


	public void setWhereCondition_u(ConditionCollection whereCondition_u) {
		this.whereCondition_u = whereCondition_u;
	}


	public ConditionCollection getWhereCondition_d() {
		return whereCondition_d;
	}


	public void setWhereCondition_d(ConditionCollection whereCondition_d) {
		this.whereCondition_d = whereCondition_d;
	}
	
	private void executionPerformance(long performanceTimeStart, String text) {
		/*Performance evaluation section [header] start*/
		try {
		    if (StressTool.getLogProvider()
			    .getLogger(LogProvider.LOG_PACTIONS)
			    .isInfoEnabled()) {
			
			StressTool
				.getLogProvider()
				.getLogger(LogProvider.LOG_PACTIONS)
				.info(StressTool.getLogProvider().LOG_EXEC_TIME
					+ " "+ text + " exec perf:"
					+ PerformanceEvaluator
						.getTimeEvaluationNs(performanceTimeStart));
		    }
		} catch (Throwable th) {
		}
		/*Performance evaluation section [header] END*/
	}

}
