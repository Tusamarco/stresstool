package net.tc.stresstool.actions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Statement;

import net.tc.data.db.Attribute;
import net.tc.data.db.Table;
import net.tc.data.generic.DataObject;
import net.tc.data.generic.SQLObject;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;

public class UpdateBase extends StressActionBase implements UpdateAction {
	
    private int numberOfprimaryTables=1;
    private int numberOfSecondaryTables=0;
    
    private boolean useAutoIncrement=true;
    
	private int sleepWrite=0;
    private boolean lazyUpdate=true;
    private int lazyInterval=5000;
    private int batchSize = 0; 
    private String jsonFile = "";
    private Table leadingTable =  null;
    private DataObject myDataObject =null;
    private int currentLazyLoop = 0 ;    
    
    public String getJsonFile() {
		return jsonFile;
	}
	public void setJsonFile(String jsonFile) {
		this.jsonFile = jsonFile;
	}
	
	public boolean  DropSchema(String[] schema) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
		
	}
	@Override
	public int getBatchSize() {
		return this.batchSize;
	}
	@Override
	public void setUseAutoIncrement(boolean useAutoIncrement) {
		this.useAutoIncrement = useAutoIncrement;
		
	}
	@Override
	public boolean isUseAutoIncrement() {
		return useAutoIncrement;
	}
//	public boolean getuseAutoincrement() {
//		return useAutoIncrement;
//	}

	@Override
	public ActionTable getInsertValueProvider() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setLazyness(boolean lazy) {
		this.lazyUpdate = lazy;
		
	}
	@Override
	public boolean islazyCreation() {
		return this.lazyUpdate;
	}
	@Override
	public void setLazyInterval(int interval) {
		this.lazyInterval = interval;
		
	}
	@Override
	public int getLazyInterval() {
		return this.lazyInterval;
	}
	@Override
	public void setNumberOfprimaryTables(int tableNumbers) {
		this.numberOfprimaryTables = tableNumbers;
		
	}
	@Override
	public int getNumberOfprimaryTables() {
		return this.numberOfprimaryTables;
	}
	@Override
	public void setNumberOfSecondaryTables(int tableNumbers) {
		this.numberOfSecondaryTables = tableNumbers;
		
	}
	@Override
	public int getNumberOfSecondaryTables() {
		return this.numberOfSecondaryTables;
	} 
	    
    public int getSleepWrite() {
		return sleepWrite;
	}
	public void setSleepWrite(int sleepWrite) {
		this.sleepWrite = sleepWrite;
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
		  this.getUpdateObject();
		  
		}else{
		  if(currentLazyLoop > myDataObject.getLazyInterval()){
			  getUpdateObject();
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
		this.myDataObject.setLazy(this.isLazyUpdate());
		this.myDataObject.setLazyInterval(this.getLazyInterval());
		this.myDataObject.setInizialized(true);
		getUpdateObject();
	    
		return this.myDataObject.isInizialized();  

	  }
	  
	  public void getUpdateObject() {
		SQLObject lSQLObj = new SQLObject() ;
		lSQLObj.setSQLCommandType(DataObject.SQL_UPDATE);
		SynchronizedMap SQLObjectContainer = new SynchronizedMap();

		if(getBatchSize() < 1){
		  setBatchSize(1);
		}

		for(int batchRun = 0 ; batchRun < getBatchSize(); batchRun++){
		  ArrayList tables = new ArrayList();
		  Table newTable = getMainTable(lSQLObj);
//		  newTable.setJoinTables(this.filterSubTables(newTable, this.getTables()));
		  tables.add(newTable);
		  lSQLObj.setSourceTables(tables);
			
//		  ArrayList<Table> tables = new ArrayList<Table>();
//		  tables.add(getMainTable());
//		  lSQLObj.setSourceTables(tables);
		  
		  String sqlUpdateCommand = DataObject.SQL_UPDATE_TEMPLATE;
		  loadMaxWhereValues(newTable);
		  sqlUpdateCommand = createUpdate(sqlUpdateCommand,newTable);
		  sqlUpdateCommand = getUpdateSetValues(newTable,sqlUpdateCommand); //lSQLObj.getValuesForDML(newTable,sqlUpdateCommand);
		  sqlUpdateCommand = createWhere(sqlUpdateCommand,newTable);
		  sqlUpdateCommand = createGroupBy(sqlUpdateCommand,newTable);
		  sqlUpdateCommand = createOrderBy(sqlUpdateCommand,newTable);
		  sqlUpdateCommand = createLimit(sqlUpdateCommand,newTable);
//		  lSQLObj.setSingleSQLCommands(sqlUpdateCommand);
		  lSQLObj.getSQLCommands().add(sqlUpdateCommand);
		  

		}
		SQLObjectContainer.put(this.getId(), lSQLObj);
		this.myDataObject.setSQL(SQLObjectContainer);
	  }
	  
	  private String createGroupBy(String sqlUpdateCommand, Table newTable) {
		// TODO Auto-generated method stub #GROUP_BY#
		  return sqlUpdateCommand.replaceAll("#GROUP_BY#", "");
	}
	private String createUpdate(String sqlUpdateCommand, Table table){
			StringBuffer sb = new StringBuffer();
			sb.append(getLeadingTable().getName() );
			sqlUpdateCommand = sqlUpdateCommand.replaceAll("#TABLE_CONDITION#", sb.toString());

			return sqlUpdateCommand;

	  }	
	private String createWhere(String sqlUpdateCommand,Table table){
			if(getLeadingTable()!= null){
			  StringBuffer  whereConditionString = new StringBuffer();

			  
			  whereConditionString.append(" WHERE ");
			  whereConditionString.append(getLeadingTable().parseWhere(this.getMyDataObject().SQL_UPDATE));
			  sqlUpdateCommand = sqlUpdateCommand.replaceAll("#WHERE#", whereConditionString.toString());

			  return sqlUpdateCommand;
			}
			return null;
	}
	private String createLimit(String sqlUpdateCommand, Table newTable) {
	
			return sqlUpdateCommand.replaceAll("#LIMIT#", "");
	}
	  private String createOrderBy(String sqlUpdateCommand, Table newTable) {
	
		return sqlUpdateCommand.replaceAll("#ORDER_BY#", "");
	  }	
//	  private Table getMainTable(){
//			Table table = (Table) getTables()[Utility.getNumberFromRandomMinMax(new Long(0), new Long(getTables().length) ).intValue()];
//
//			if(table != null){
//				this.setLeadingTable(table);
//				return getLeadingTable();
//			}
//
//			return table;
//
//	  }
private Table getMainTable(SQLObject lSQl){
	Table table = (Table) getTables()[Utility.getNumberFromRandomMinMax(new Long(0), new Long(getTables().length) ).intValue()];

	if(checkIfTableExists(table,lSQl))
		return getMainTable(lSQl);
			
			if(table.getParentTable() != null)
			  return getMainTable(lSQl);

			
	this.setLeadingTable(table);
		return table;


	}
private boolean checkIfTableExists(Table tableIn,SQLObject lSQL){
	  if(lSQL != null
			  && lSQL.getSourceTables() != null){
	  
	  ArrayList<Table>	tables = (ArrayList)lSQL.getSourceTables();
	  if(tables == null)
		  	return false;
	  int found = 0;
	  for(Table table:tables){
		  if(table.getName().equals(tableIn.getName())  && getTables().length >= this.batchSize)
			  return true;
		  else if(table.getName().equals(tableIn.getName())  && getTables().length < this.batchSize && getTables().length > 1){
			  found++;
			  if((getTables().length/found) < 2)
				  return true;
		  }
	  	}
	  }
	  return false;
	  
}
	public Table getLeadingTable() {
		return leadingTable;
	}
	public void setLeadingTable(Table leadingTable) {
		this.leadingTable = leadingTable;
	}

	private void loadMaxWhereValues(Table table){
		// TODO Add method to load values from max value in tables.
		ArrayList<Attribute> maxAttribute =  new ArrayList();
		table.parseAttributeWhere(table.getWhereCondition(this.getMyDataObject().SQL_UPDATE), maxAttribute);
		StringBuffer sb = new StringBuffer();
		
		for(Object attrib : (Object[]) (maxAttribute.toArray())){
				if (sb.length() > 0)
					sb.append(",");
				sb.append("MAX("+ ((Attribute)attrib).getName() +") as " +((Attribute)attrib).getName()+" ");
		}	
			String SQL = "Select " + sb.toString() + " FROM " + table.getName();
			if(sb.length() > 0){
				Connection conn = null;
				if(this.getActiveConnection()==null){
				  try {
					conn = this.getConnProvider().getSimpleConnection();
					Statement stmt = (Statement) conn.createStatement();
					ResultSet rs = stmt.executeQuery(SQL);
					if(rs != null ){
						while (rs.next()){
							for(Object attrib : (Object[]) (maxAttribute.toArray())){
//								if(((Attribute)attrib).getValue()== null){
									((Attribute)attrib).setValue(rs.getObject(((Attribute)attrib).getName()));
									if(Utility.isNumeric(((Attribute)attrib).getValue()) 
											&& (
													((Attribute)attrib).getValue() instanceof Long
													|| ((Attribute)attrib).getValue() instanceof Integer
												)
											){
												if(((Attribute)attrib).getValue() instanceof Long)
													((Attribute)attrib).setUpperLimit(Long.valueOf(String.valueOf(((Attribute)attrib).getValue().equals("NULL")?"0":String.valueOf(((Attribute)attrib).getValue()))));
												else
													((Attribute)attrib).setUpperLimit(Long.valueOf(String.valueOf(((Attribute)attrib).getValue().equals("NULL")?"0":String.valueOf(((Attribute)attrib).getValue()))));
									}
									table.getMetaAttributes().put(((Attribute)attrib).getName(), ((Attribute)attrib));
//								}
							}
						}
					}
					
					
				  } catch (Exception e) {
						e.printStackTrace();
				  }
				  finally{
					  this.getConnProvider().returnConnection((com.mysql.jdbc.Connection) conn);
				  }
				}
				
				
			
		}
//		List<String> list = new ArrayList<String>(Arrays.asList(string.split(" , ")));
		
	}
	
	private String getUpdateSetValues(Table table,String sqlUpdateCommand){
		if(table.getUpdateSetAttributes() == null || table.getUpdateSetAttributes().equals(""))
			return sqlUpdateCommand;
		StringBuffer sqlValues = new StringBuffer();
		
		Attribute[] attribs = this.getAttribs(table, null);
		for(Attribute attrib:attribs){
			if(attrib != null 
					&& attrib.getName() == null)
				continue;
			if(table.getUpdateSetAttributes().indexOf("#"+attrib.getName()+"#") > -1){
				if (sqlValues.length() > 0)
					sqlValues.append(", ");

				if (attrib.getSpecialFunction() == null && !attrib.isAutoIncrement()) {

					attrib.setValue(StressTool
							.getValueProvider()
							.provideValue(attrib.getDataType(), new Long(attrib.getUpperLimit())));
				} else {

					if (attrib.isAutoIncrement()) {
						attrib.setValue("NULL");
					} else {
						attrib.setValue( attrib.getSpecialFunction());
					}
				}
				sqlValues.append(attrib.getName() + "=" + attrib.getValue());
			}
			
		}
//		sqlValues.insert(0, "SET ");
		return sqlUpdateCommand.replace("#ATTRIB_VALUE#", sqlValues.toString());
		
	}
	private Attribute[] getAttribs(Table table,String filter) {
		// TODO Auto-generated method stub
		if(table != null 
			&& table.getMetaAttributes()!=null
			&& table.getMetaAttributes().size() >0 ){
		  SynchronizedMap attr = table.getMetaAttributes();
		  Attribute[] attrR = new Attribute[attr.size()];
		  for(int i=0; i< attr.size();i++){
			  if(i < attr.size())
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
	public boolean isLazyUpdate() {
		return lazyUpdate;
	}
	public void setLazyUpdate(boolean lazyUpdate) {
		this.lazyUpdate = lazyUpdate;
	}
	public DataObject getMyDataObject() {
		return myDataObject;
	}
	public void setMyDataObject(DataObject myDataObject) {
		this.myDataObject = myDataObject;
	}
	public int getCurrentLazyLoop() {
		return currentLazyLoop;
	}
	public void setCurrentLazyLoop(int currentLazyLoop) {
		this.currentLazyLoop = currentLazyLoop;
	}
}
