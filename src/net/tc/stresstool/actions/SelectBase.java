package net.tc.stresstool.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import net.tc.data.db.Attribute;
import net.tc.data.db.Table;
import net.tc.data.generic.DataObject;
import net.tc.data.generic.SQLObject;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolActionException;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.ActionTHElement;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;

public class SelectBase extends StressActionBase implements ReadAction{


  private int numberOfJoinTables=0;
  private String joinField = "";
  private boolean forceIndex = false;
  private String indexName= "";
  private int numberOfIntervalKeys =0 ;
  private String selectFilterMethod = "range"; // #range|in|match
  private int sleepSelect = 0; 

  private int numberOfprimaryTables=1;
  private int numberOfSecondaryTables=0;
  private boolean lazySelect=true;
  private int lazyInterval=5000;
  private String tableEngine = "InnODB";
  private DataObject myDataObject =null;
  //	SQLObject lSQLObj = new SQLObject();

  /*
   * not sure the following will saty here or on another object
   */
  private Table selectLeadingTable =  null;
  private Table[] selectJoinTables =  null;
  private int batchSize = 0;
  private int currentLazyLoop = 0 ;
  private int textAttributeMaxSearchlength = 50;

  private Map<String, ActionTable> dataProviders = null;


  public int getNumberOfJoinTables() {
	return numberOfJoinTables;
  }
  public void setNumberOfJoinTables(int numberOfJoinTables) {
	this.numberOfJoinTables = numberOfJoinTables;
  }
  public String getJoinField() {
	return joinField;
  }
  public void setJoinField(String joinField) {
	this.joinField = joinField;
  }
  public boolean isForceIndex() {
	return forceIndex;
  }
  public void setForceIndex(boolean forceIndex) {
	this.forceIndex = forceIndex;
  }
  public String getIndexName() {
	return indexName;
  }
  public void setIndexName(String indexName) {
	this.indexName = indexName;
  }
  public int getNumberOfIntervalKeys() {
	return numberOfIntervalKeys;
  }
  public void setNumberOfIntervalKeys(int numberOfIntervalKeys) {
	this.numberOfIntervalKeys = numberOfIntervalKeys;
  }
  public String getSelectFilterMethod() {
	return selectFilterMethod;
  }
  public void setSelectFilterMethod(String selectFilterMethod) {
	this.selectFilterMethod = selectFilterMethod;
  }
  public int getSleepSelect() {
	return sleepSelect;
  }
  public void setSleepSelect(int sleepSelect) {
	this.sleepSelect = sleepSelect;
  }
  public int getNumberOfprimaryTables() {
	return numberOfprimaryTables;
  }
  public void setNumberOfprimaryTables(int numberOfprimaryTables) {
	this.numberOfprimaryTables = numberOfprimaryTables;
  }
  public int getNumberOfSecondaryTables() {
	return numberOfSecondaryTables;
  }
  public void setNumberOfSecondaryTables(int numberOfSecondaryTables) {
	this.numberOfSecondaryTables = numberOfSecondaryTables;
  }
  @Override
  public void setDataSelectorProvider(Map<String, ActionTable> dataProviderIn) {
	dataProviders =dataProviderIn;

  }
  @Override
  public void setLazyness(boolean lazy) {
	lazySelect = lazy;

  }
  @Override
  public boolean islazyCreation() {
	return lazySelect;
  }
  @Override
  public void setLazyInterval(int interval) {
	lazyInterval = interval;

  }
  @Override
  public int getLazyInterval() {
	return lazyInterval;
  }
  @Override
  public int getSleepRead() {
	return sleepSelect;
  }
  @Override
  public void setSleepRead(int sleepRead) {
	sleepSelect = sleepRead;

  }
  /**
   * @return the tableEngine
   */
  public String getTableEngine() {
	return tableEngine;
  }
  /**
   * @param tableEngine the tableEngine to set
   */
  public void setTableEngine(String tableEngine) {
	this.tableEngine = tableEngine;
  }
  /**
   * this is actually executing what the action is suppose to do
   */
  @Override
  public void ExecuteAction() {
	Connection conn = null;
	if(this.getActiveConnection()==null){
	  try {
		conn = this.getConnProvider().getSimpleConnection();
	  } catch (SQLException e) {
		e.printStackTrace();
	  }
	}
	else{
	  conn = this.getActiveConnection();
	}

	if(!myDataObject.isLazy()){
	  this.getSelectObject();
	  
	}else{
	  if(currentLazyLoop > myDataObject.getLazyInterval()){
		  getSelectObject();
		  currentLazyLoop = 0;
	  }
	  currentLazyLoop +=1;
	}
	this.myDataObject.executeSqlObject(this.getActionCode(),(com.mysql.jdbc.Connection) conn);
	if(!this.isStickyconnection()){

	  getConnProvider().returnConnection((com.mysql.jdbc.Connection)conn);
	}

  }


  @Override
  public boolean ExecutePreliminaryAction() {
	this.myDataObject = new DataObject();
	this.myDataObject.setLazy(this.isLazySelect());
	this.myDataObject.setLazyInterval(this.getLazyInterval());
	this.myDataObject.setInizialized(true);
	getSelectObject();
    
	return this.myDataObject.isInizialized();  

  }
  
  public void getSelectObject() {
	SQLObject lSQLObj = new SQLObject() ;
	SynchronizedMap SQLObjectContainer = new SynchronizedMap();

	if(getBatchSize() < 1){
	  setBatchSize(1);
	}

	for(int batchRun = 0 ; batchRun <= getBatchSize(); batchRun++){
	  ArrayList tables = new ArrayList();
	  tables.add(getMainTable());
	  lSQLObj.setSourceTables(tables);
	  String sqlSelectCommand = DataObject.SQL_SELECT_TEMPLATE;
	  sqlSelectCommand = createSelect(sqlSelectCommand,lSQLObj);
	  sqlSelectCommand = createWhere(sqlSelectCommand);
	  sqlSelectCommand = createGroupBy(sqlSelectCommand);
	  sqlSelectCommand = createOrderBy(sqlSelectCommand);
	  sqlSelectCommand = createLimit(sqlSelectCommand);
	  lSQLObj.setSingleSQLCommands(sqlSelectCommand);

	}
	SQLObjectContainer.put(this.getId(), lSQLObj);
	this.myDataObject.setSQL(SQLObjectContainer);
  }


  private String createLimit(String sqlSelectCommand) {

	return sqlSelectCommand.replaceAll("#LIMIT#", "");
  }
  private String createOrderBy(String sqlSelectCommand) {

	return sqlSelectCommand.replaceAll("#ORDER_BY#", "");
  }
  private String createGroupBy(String sqlSelectCommand) {
	// TODO Auto-generated method stub
	return sqlSelectCommand.replaceAll("#GROUP_BY#", "");
  }
  private Attribute[] getAttribs(Table table,String filter) {
	// TODO Auto-generated method stub
	if(table != null 
		&& table.getMetaAttributes()!=null
		&& table.getMetaAttributes().size() >0 ){
	  SynchronizedMap attr = table.getMetaAttributes();
	  Attribute[] attrR = new Attribute[attr.size()];
	  for(int i=0; i< attr.size();i++){
		attrR[i]= (Attribute) attr.getValueByPosition(i);
	  }

	  return attrR;
	  //	       return (Attribute[]) table.getMetaAttributes().getValuesAsArrayOrderByKey(new Attribute[table.getMetaAttributes().size()]);
	  //	    		   .values().toArray(new Attribute[table.getMetaAttributes().size()]);
	}

	return null;
  }
  private Table[] getTables() {
	if(getSchema() !=null 
		&& getSchema().getTables() != null){

	  return (Table[]) getSchema().getTables().values().toArray(new Table[getSchema().getTables().size()]);
	}

	return null;
  }

  private Table getMainTable(){
	Table table = (Table) getTables()[Utility.getNumberFromRandomMinMax(new Long(0), new Long(getTables().length) ).intValue()];

	if(table.getParentTable() != null)
	  return getMainTable();

	this.setSelectLeadingTable(table);
	return table;


  }
  private Table getSecondary(){
	Table table = (Table) getTables()[Utility.getNumberFromRandomMinMax(new Long(0), new Long(getTables().length) ).intValue()];
	if(table.getParentTable() == null)
	  return getMainTable();


	return table;


  }
  private String getJoinCondition(SQLObject lSQLObj){
	String condition = null;
	int tablesToJoin = this.getNumberOfJoinTables();
	String joinField = this.getJoinField();
	//	  String joinCondition = this.getJoinCondition();

	Table mainTable = (Table) lSQLObj.getSourceTables().get(0);

	Table[] secondaryTable = new Table[tablesToJoin];

	condition = mainTable.getName();
	for (int iJ = 0 ; iJ < tablesToJoin ; iJ++){
	  secondaryTable[iJ]= getSecondary();
	  condition += " LEFT JOIN  " 
		  + secondaryTable[iJ].getName()
		  + " ON " + mainTable.getName() + "." + joinField
		  + " = " + secondaryTable[iJ].getName() + "." + joinField ;
	}
	this.setSelectJoinTables(secondaryTable);


	return condition;

  }
  /**
   * @return the selectLeadingTable
   */
  private Table getSelectLeadingTable() {
	return selectLeadingTable;
  }
  /**
   * @param selectLeadingTable the selectLeadingTable to set
   */
  private void setSelectLeadingTable(Table selectLeadingTable) {
	this.selectLeadingTable = selectLeadingTable;
  }
  /**
   * @return the selectJoinTables
   */
  private Table[] getSelectJoinTables() {
	return selectJoinTables;
  }
  /**
   * @param selectJoinTables the selectJoinTables to set
   */
  private void setSelectJoinTables(Table[] selectJoinTables) {
	this.selectJoinTables = selectJoinTables;
  }

  private String createSelect(String sqlSelectCommand, SQLObject lSQLObj){
	StringBuffer sb = new StringBuffer();


	//	  select.replaceAll("", replacement);
	getMainTable();
	sb.append( getSelectLeadingTable().getSelectCondition());
	sb.append( " FROM " + getSelectLeadingTable().getName() );
	if(this.getNumberOfJoinTables() > 0 ){
	  sb.append(" " + this.getJoinCondition(lSQLObj));
	}

	sqlSelectCommand = sqlSelectCommand.replaceAll("#TABLE_CONDITION#", sb.toString());

	return sqlSelectCommand;

  }

  private String createWhere(String sqlSelectCommand){
	if(getSelectLeadingTable()!= null){
	  StringBuffer  whereConditionString = new StringBuffer();

	  whereConditionString.append(" WHERE ");
	  whereConditionString.append(getSelectLeadingTable().parseWhere());
	  sqlSelectCommand = sqlSelectCommand.replaceAll("#WHERE#", whereConditionString.toString());

	  return sqlSelectCommand;
	}
	return null;
  }
  /**
   * @return the lazySelect
   */
  public boolean isLazySelect() {
	return lazySelect;
  }
  /**
   * @param lazySelect the lazySelect to set
   */
  public void setLazySelect(boolean lazySelect) {
	this.lazySelect = lazySelect;
  }
  /**
   * @return the batchSize
   */
  public int getBatchSize() {
	return batchSize;
  }
  /**
   * @param batchSize the batchSize to set
   */
  public void setBatchSize(int batchSize) {
	this.batchSize = batchSize;
  }
  /**
   * @return the textAttributeMaxSearchlength
   */
  public int getTextAttributeMaxSearchlength() {
    return textAttributeMaxSearchlength;
  }
  /**
   * @param textAttributeMaxSearchlength the textAttributeMaxSearchlength to set
   */
  public void setTextAttributeMaxSearchlength(int textAttributeMaxSearchlength) {
    this.textAttributeMaxSearchlength = textAttributeMaxSearchlength;
  }	
}
