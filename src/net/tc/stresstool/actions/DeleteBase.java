package net.tc.stresstool.actions;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.mysql.jdbc.Statement;

import net.tc.data.db.Attribute;
import net.tc.data.db.Table;
import net.tc.data.generic.DataObject;
import net.tc.data.generic.SQLObject;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.ActionTHElement;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;

public class DeleteBase extends StressActionBase implements DeleteAction{
	
    private boolean lazyDelete=true;
    private int lazyInterval=5000;
    private Map<String, ActionTable> dataProviders = null;
    private int deleteInterval = 1;
    private int deleteRange = 1;
    private int sleepDelete = 0;
    	
    private int batchSize = 1; 
    private Table leadingTable =  null;
    private DataObject myDataObject =null;
    private int currentLazyLoop = 0 ;    

    
   public void setBatchSize(int i) {
		  batchSize=i;
		
	}
	public int getBatchSize() {
		
		return batchSize;
	}
    
    
	@Override
	public void setDataSelectorProvider(Map<String, ActionTable> dataProviderIn) {
		dataProviders = dataProviderIn;
		
	}
	@Override
	public void setInterval(int interval) {
		deleteInterval = interval;
		
	}
	@Override
	public void setActionRange(int range) {
		deleteRange = range;
		
	}
	public int getSleepDelete() {
		return sleepDelete;
	}
	public void setSleepDelete(int sleepDelete) {
		this.sleepDelete = sleepDelete;
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
			try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
				StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
			}catch(Exception xxxxx){}
			
		  }
		}
		else{
		  conn = this.getActiveConnection();
		}
/*
 * There is no sense in using lazy property on delete. Once action is done ones it should left no records immediately after.
 * SO WHY keep it. 
 */
		if(!myDataObject.isLazy()){
		  this.getDeleteObject();
		  
		}else{
		  if(currentLazyLoop > myDataObject.getLazyInterval()){
			getDeleteObject();
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
		this.myDataObject.setLazy(this.isLazyDelete());
		this.myDataObject.setLazyInterval(this.getLazyInterval());
		this.myDataObject.setInizialized(true);
		getDeleteObject();
	    
		return this.myDataObject.isInizialized();  

	  }

	  public void setLazyInterval(int lazyInterval) {
			
		this.lazyInterval=lazyInterval;
	}

	  public int getLazyInterval() {
	
		return lazyInterval;
	}
	public void getDeleteObject() {
		SQLObject lSQLObj = new SQLObject() ;
		lSQLObj.setSQLCommandType(DataObject.SQL_DELETE);
		SynchronizedMap SQLObjectContainer = new SynchronizedMap();

		if(getBatchSize() < 1){
		  setBatchSize(1);
		}

		for(int batchRun = 0 ; batchRun <= getBatchSize(); batchRun++){
		  ArrayList<Table> tables = new ArrayList<Table>();
		  Table newTable = getMainTable(lSQLObj);
		  tables.add(newTable);
		  lSQLObj.setSourceTables(tables);
		  
		  String sqlDeleteCommand = DataObject.SQL_DELETE_TEMPLATE;
		  loadMaxWhereValues(newTable);
		  sqlDeleteCommand = createDelete(sqlDeleteCommand,lSQLObj);
		  sqlDeleteCommand = createWhere(sqlDeleteCommand,lSQLObj);
//		  sqlUpdateCommand = createGroupBy(sqlUpdateCommand);
		  sqlDeleteCommand = createOrderBy(sqlDeleteCommand);
		  sqlDeleteCommand = createLimit(sqlDeleteCommand);
//		  lSQLObj.setSingleSQLCommands(sqlDeleteCommand);
		  lSQLObj.getSQLCommands().add(sqlDeleteCommand);
		}
		SQLObjectContainer.put(this.getId(), lSQLObj);
		this.myDataObject.setSQL(SQLObjectContainer);
	  }
	  
	private String createDelete(String sqlDeleteCommand, SQLObject lSQLObj){
			StringBuffer sb = new StringBuffer();
			sb.append(getLeadingTable().getName() );
			sqlDeleteCommand = sqlDeleteCommand.replaceAll("#TABLE_CONDITION#", sb.toString());

			return sqlDeleteCommand;

	  }	
	private String createWhere(String sqlUpdateCommand,SQLObject lSQLObj){
			if(this.getLeadingTable()!= null){
			  StringBuffer  whereConditionString = new StringBuffer();

			  whereConditionString.append(" WHERE ");
			  whereConditionString.append(getLeadingTable().parseWhere(lSQLObj.getSQLCommandType()));
			  sqlUpdateCommand = sqlUpdateCommand.replaceAll("#WHERE#", whereConditionString.toString());

			  return sqlUpdateCommand;
			}
			return null;
	}
	private String createLimit(String sqlSelectCommand) {
	
			return sqlSelectCommand.replaceAll("#LIMIT#", "");
	}
	  private String createOrderBy(String sqlSelectCommand) {
	
		return sqlSelectCommand.replaceAll("#ORDER_BY#", "");
	  }	
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
		  
		  for(Table table:tables){
			  if(table.getName().equals(tableIn.getName()))
				  return true;
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
		table.parseAttributeWhere(table.getWhereCondition(this.getMyDataObject().SQL_DELETE), maxAttribute);
		StringBuffer sb = new StringBuffer();
		
		for(Object attrib : (Object[]) (maxAttribute.toArray())){
				if (sb.length() > 1)
					sb.append(",");
				sb.append("MAX("+ ((Attribute)attrib).getName() +") as " +((Attribute)attrib).getName()+" ");
		}	
			String SQL = "Select " + sb.toString() + " FROM " + table.getName();
			if(sb.length() > 1){
				Connection conn = null;
				if(this.getActiveConnection()==null){
				  try {
					conn = this.getConnProvider().getSimpleConnection();
					Statement stmt = (Statement) conn.createStatement();
					ResultSet rs = stmt.executeQuery(SQL);
					if(rs != null ){
						while (rs.next()){
							for(Object attrib : (Object[]) (maxAttribute.toArray())){
								if(((Attribute)attrib).getValue()== null){
									((Attribute)attrib).setValue(rs.getObject(((Attribute)attrib).getName()));
									table.getMetaAttributes().put(((Attribute)attrib).getName(), ((Attribute)attrib));
								}
							}
						}
					}
					
					
				  } catch (SQLException e) {
						try{String s =new String();PrintWriter pw = new PrintWriter(s);e.printStackTrace(pw);
						StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error("");
					}catch(Exception xxxxx){}

				  }
				  finally{
					  
					  this.getConnProvider().returnConnection((com.mysql.jdbc.Connection) conn);
				  }
				}
				
				
			
		}
//		List<String> list = new ArrayList<String>(Arrays.asList(string.split(" , ")));
		
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
	public boolean isLazyDelete() {
		return lazyDelete;
	}
	public void setLazyDelete(boolean lazyDelete) {
		this.lazyDelete = lazyDelete;
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
