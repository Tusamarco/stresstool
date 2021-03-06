	package net.tc.stresstool.actions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import java.sql.Statement;

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
//  private int batchSize = 0;
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
  @Deprecated
  public int getNumberOfIntervalKeys() {
	return numberOfIntervalKeys;
  }
  @Deprecated
  public void setNumberOfIntervalKeys(int numberOfIntervalKeys) {
	this.numberOfIntervalKeys = numberOfIntervalKeys;
  }
  @Deprecated
  public String getSelectFilterMethod() {
	return selectFilterMethod;
  }
  @Deprecated
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
  public void setNumberOfprimaryTables(int InnumberOfprimaryTables) {
	this.numberOfprimaryTables = InnumberOfprimaryTables;
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
  
  
  public void setLazyInterval(int interval) {
	lazyInterval = interval;

  }
  
  public int getLazyInterval() {
	return lazyInterval;
  }
  
  public int getSleepRead() {
	return sleepSelect;
  }
  
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
		conn = this.getConnProvider().getConnection();
	  } catch (SQLException e) {
			try{					
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);				
				e.printStackTrace(ps);
				String s =new String(baos.toByteArray());
				StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
				System.exit(1)  ;
		}catch(Exception ex){ex.printStackTrace();}

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
	this.myDataObject.executeSqlObject(this.getActionCode(),(java.sql.Connection) conn);
	if(!this.isStickyconnection()){
	  getConnProvider().returnConnection((java.sql.Connection)conn);
	}
	/*
	 * Sleeping beauty  
	 */
	this.setSleepingTimeNs(this.getSleepSelect());
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
	lSQLObj.setSQLCommandType(DataObject.SQL_READ);
	SynchronizedMap SQLObjectContainer = new SynchronizedMap();

//	if(getBatchSize() < 1){
//	  setBatchSize(1);
//	}

	for(int batchRun = 0 ; batchRun < getBatchSize(); batchRun++){
	  ArrayList tables = new ArrayList();
	  Table newTable = getMainTable(lSQLObj);
	  newTable.setJoinTables(this.filterSubTables(newTable, this.getTables()));
//	System.out.println("table name "+ newTable.getName()+  " Name " + newTable.getMetaAttributes("id").getName()+ " Value " + newTable.getMetaAttributes("id").getValueAsString(100));
	  tables.add(newTable);
	  lSQLObj.setSourceTables(tables);
	  
//TODO Must re-think how to get MAX() values for auto increment	  
	  
	  String sqlSelectCommand = DataObject.SQL_SELECT_TEMPLATE;
//	  loadMaxWhereValues(newTable);
	  sqlSelectCommand = createSelect(sqlSelectCommand,newTable);
	  sqlSelectCommand = createWhere(sqlSelectCommand,newTable);
	  if(sqlSelectCommand == null)
		  return;
	  
	  sqlSelectCommand = createGroupBy(sqlSelectCommand,newTable);
	  sqlSelectCommand = createOrderBy(sqlSelectCommand,newTable);
	  //TODO
	  // THIS MUST BE REMOVED
	  sqlSelectCommand = createLimit(sqlSelectCommand,newTable);
	  lSQLObj.getSQLCommands().add(sqlSelectCommand);
	  
//	  lSQLObj.setSingleSQLCommands(sqlSelectCommand);

	}
	SQLObjectContainer.put(this.getId(), lSQLObj);
	this.myDataObject.setSQL(SQLObjectContainer);
  }


  private String createLimit(String sqlSelectCommand,Table table) {

	return sqlSelectCommand.replaceAll("#LIMIT#", "");
  }
  private String createOrderBy(String sqlSelectCommand,Table table) {

	return sqlSelectCommand.replaceAll("#ORDER_BY#", "");
  }
  private String createGroupBy(String sqlSelectCommand,Table table) {
	// TODO Auto-generated method stub
	return sqlSelectCommand.replaceAll("#GROUP_BY#", "");
  }
  private Attribute[] getAttribs(Table table,String filter) {
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

  /*
   * Provide table to build the select filtering by readfactor 
   */
  private Table getMainTable(SQLObject lSQl){
	Table table = (Table) getTables()[Utility.getNumberFromRandomMinMax(new Long(0), new Long(getTables().length) ).intValue()];
	int tableReadFactor = (int) table.getReadFactor();
	int readFactor = Utility.getNumberFromRandomMinMax(new Long(1), 100).intValue();

	while(tableReadFactor < readFactor) {
		table = (Table) getTables()[Utility.getNumberFromRandomMinMax(new Long(0), new Long(getTables().length) ).intValue()];
		tableReadFactor = (int) table.getReadFactor();
		 try{StressTool.getLogProvider().getLogger(LogProvider.LOG_SQL).debug("Skip table by read factor TABLE " +
		  table.getName() 
		  + " factor = "
		  + table.getReadFactor()
		  + " Filter read factor = " + readFactor);}catch(StressToolConfigurationException e){}
	}
	
	
	
	if(checkIfTableExists(table,lSQl,readFactor))
		return getMainTable(lSQl);
	
	if(table.getParentTable() != null)
	  return getMainTable(lSQl);

	
	this.setSelectLeadingTable(table);
	return table;


  }
//  private Table getSecondary(Table mainTable){
//	if(mainTable.getJoinTables() != null && mainTable.getJoinTables().size() > 0  ){
//		Table table = (Table) getTables()[Utility.getNumberFromRandomMinMax(new Long(0), new Long(mainTable.getJoinTables().size()) ).intValue()];
//		return table;
//	}
//
//
//	return null;
//
//
//  }
  private boolean checkIfTableExists(Table tableIn,SQLObject lSQL, int readFactor){
	  if(lSQL != null
			  && lSQL.getSourceTables() != null){
	  
	  ArrayList<Table>	tables = (ArrayList)lSQL.getSourceTables();
	  if(tables == null)
		  	return false;
	  int found = 0;
	  for(Table table:tables){
//		  if(table.getName().equals(tableIn.getName())){
//			  if(readFactor > tableIn.getReadFactor()) {
//				  this.getSelectObject();
//			  }
//		  }
		  
		  if(table.getName().equals(tableIn.getName())  && getTables().length >= this.getBatchSize())
			  return true;
		  else if(table.getName().equals(tableIn.getName())  && getTables().length < this.getBatchSize() && getTables().length > 1){
			  found++;
			  if((getTables().length/found) < 2)
				  return true;
		  }
	  	}
	  }
	  return false;
	  
  }
  
  ArrayList<Table> filterSubTables(Table parentTable, Table[] tables) {
	  ArrayList<Table> subTables = new ArrayList();
	  for(Table table:tables){
		  
		  if(table.getParentTable() !=null
				  && !parentTable.getName().equals(table.getName())
				  && parentTable.getName().indexOf(table.getParentTable()) > -1){
			  subTables.add(table);
		  }
		  
		
	}
	  // TODO Auto-generated method stub
	return subTables;
}
  @Deprecated
private String getJoinCondition(Table table){
	String condition = "";
	int tablesToJoin = this.getNumberOfJoinTables();
	String joinField = this.getJoinField();
	//	  String joinCondition = this.getJoinCondition();

	Table mainTable = table;

	ArrayList<Table> secondaryTable = table.getJoinTables();

	if(condition != null && !condition.equals(""))
		condition = mainTable.getName();
	
	for (int iJ = 0 ; iJ < tablesToJoin ; iJ++){
	 if(iJ < mainTable.getJoinTables().size()){	
	  condition += " LEFT JOIN  " 
		  + secondaryTable.get(iJ).getName()
		  + " ON " + mainTable.getName() + "." + joinField
		  + " = " + secondaryTable.get(iJ).getName() + "." + joinField ;
	 	}
	}
	return condition;

  }
  /**
   * @return the selectLeadingTable
   */
  @Deprecated
  private Table getSelectLeadingTable() {
	return selectLeadingTable;
  }
  /**
   * @param selectLeadingTable the selectLeadingTable to set
   */
  @Deprecated
  private void setSelectLeadingTable(Table selectLeadingTable) {
	this.selectLeadingTable = selectLeadingTable;
  }
  /**
   * @return the selectJoinTables
   */
  @Deprecated
  private Table[] getSelectJoinTables() {
	return selectJoinTables;
  }
  /**
   * @param selectJoinTables the selectJoinTables to set
   */
  @Deprecated
  private void setSelectJoinTables(Table[] selectJoinTables) {
	this.selectJoinTables = selectJoinTables;
  }

  private String createSelect(String sqlSelectCommand, Table table){
	StringBuffer sb = new StringBuffer();


	//	  select.replaceAll("", replacement);
	//getMainTable();
	if(table == null )
		return null;
	Map conditionValues= table.parseSelectCondition();
	
	if(conditionValues.get("distinct") !=null 
			&& !conditionValues.get("distinct").equals("false")) {
		sb.append(" DISTINCT ");
	}
	
	if(conditionValues.get("condition_string") !=null 
			&& !conditionValues.get("condition_string").equals("")) {
		sb.append(conditionValues.get("condition_string"));
	}
	else {
		sb.append(conditionValues.get(" * "));
	}
	sb.append( " FROM " + table.getSchemaName() + "." + table.getName() );
	if(conditionValues.get("joinoption") !=null 
			&& !conditionValues.get("joinoption").equals("")) {
		sb.append( " " + ((String)conditionValues.get("joinoption")).replace("#table1#", table.getName())  );
	} 


	sqlSelectCommand = sqlSelectCommand.replaceAll("#TABLE_CONDITION#", sb.toString());
	if(conditionValues.get("limit") != null && (Integer)conditionValues.get("limit") > 0){
		sqlSelectCommand = sqlSelectCommand + " LIMIT " + (Integer)conditionValues.get("limit");
	}

	return sqlSelectCommand;

  }

  private String createWhere(String sqlSelectCommand,Table table){
	  
	if(table!= null){
	  StringBuffer  whereConditionString = new StringBuffer();
 
	  whereConditionString.append(" WHERE ");
	  try {
		  Connection conn = this.getConnProvider().getConnection();
		  String conditions = table.parseWhere(DataObject.SQL_READ, conn);
		  
		  this.getConnProvider().returnConnection((java.sql.Connection) conn);
		  if(conditions == null 
			|| conditions.indexOf("#") > 0)
			  	return null;
		  else
			  whereConditionString.append(conditions);
	  } 
	  catch (SQLException e) {
		e.printStackTrace();
	  }
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
//  public int getBatchSize() {
//	return batchSize;
//  }
//  /**
//   * @param batchSize the batchSize to set
//   */
//  public void setBatchSize(int batchSize) {
//	this.batchSize = batchSize;
//  }
  /**
   * @return the textAttributeMaxSearchlength
   */
  @Deprecated
  public int getTextAttributeMaxSearchlength() {
    return textAttributeMaxSearchlength;
  }
  /**
   * @param textAttributeMaxSearchlength the textAttributeMaxSearchlength to set
   */
  @Deprecated
  public void setTextAttributeMaxSearchlength(int textAttributeMaxSearchlength) {
    this.textAttributeMaxSearchlength = textAttributeMaxSearchlength;
  }	
  
  @Deprecated 
	private void loadMaxWhereValues(Table table){
		
		ArrayList<Attribute> maxAttribute =  new ArrayList();
		table.parseAttributeWhere(table.getWhereCondition(this.getMyDataObject().SQL_READ), maxAttribute);
		StringBuffer sb = new StringBuffer();
		
		for(Object attrib : (Object[]) (maxAttribute.toArray())){
				if (sb.length() > 0)
					sb.append(",");
				sb.append("MAX("+ ((Attribute)attrib).getName() +") as " +((Attribute)attrib).getName()+" ");
		}	
			String SQL = "Select " + sb.toString() + " FROM "+table.getSchemaName() +"." + table.getName();
			if(sb.length() > 0){
				Connection conn = null;
				if(this.getActiveConnection()==null){
				  try {
					conn = this.getConnProvider().getConnection();
					Statement stmt = (Statement) conn.createStatement();
					ResultSet rs = stmt.executeQuery(SQL);
					if(rs != null ){
						while (rs.next()){
							for(Object attrib : (Object[]) (maxAttribute.toArray())){
								if(((Attribute)attrib).getValue()== null){
									((Attribute)attrib).setValue(rs.getObject(((Attribute)attrib).getName()));
//									System.out.println(((Attribute)attrib).getName() + " | "+ ((Attribute)attrib).getValue() + "Before");
									table.getMetaAttributes().put(((Attribute)attrib).getName(), ((Attribute)attrib));
//									System.out.println(((Attribute)table.getMetaAttributes().get(((Attribute)attrib).getName())).getName() + " | "
//									+ ((Attribute)table.getMetaAttributes().get(((Attribute)attrib).getName())).getValue() + "After");
								}
							}
						}
					}
					
					
				  } catch (SQLException e) {
						try{					
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							PrintStream ps = new PrintStream(baos);				
							e.printStackTrace(ps);
							String s =new String(baos.toByteArray());
							StringBuffer sb1 = new StringBuffer();
							sb1.append(s);
							sb1.append(SQL);
							StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(sb1.toString());
							System.exit(1)  ;
					}catch(Exception ex){ex.printStackTrace();}


				  }
				  finally{
					  
					  this.getConnProvider().returnConnection((java.sql.Connection) conn);
				  }
				}
				
				
			
		}
//		List<String> list = new ArrayList<String>(Arrays.asList(string.split(" , ")));
		
	}
	public DataObject getMyDataObject() {
		return myDataObject;
	}
	public void setMyDataObject(DataObject myDataObject) {
		this.myDataObject = myDataObject;
	}


}
