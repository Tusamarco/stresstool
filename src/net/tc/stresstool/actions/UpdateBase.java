package net.tc.stresstool.actions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Statement;

import net.tc.data.db.Attribute;
import net.tc.data.db.Table;
import net.tc.data.generic.DataObject;
import net.tc.data.generic.SQLObject;
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
			e.printStackTrace();
		  }
		}
		else{
		  conn = this.getActiveConnection();
		}

//		if(!myDataObject.isLazy()){
//		  this.getUpdateObject();
//		  
//		}else{
//		  if(currentLazyLoop > myDataObject.getLazyInterval()){
			  getUpdateObject();
//			  currentLazyLoop = 0;
//		  }
//		  currentLazyLoop +=1;
//		}
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

		for(int batchRun = 0 ; batchRun <= getBatchSize(); batchRun++){
		  ArrayList<Table> tables = new ArrayList<Table>();
		  tables.add(getMainTable());
		  lSQLObj.setSourceTables(tables);
		  
		  String sqlUpdateCommand = DataObject.SQL_UPDATE_TEMPLATE;
		  sqlUpdateCommand = createUpdate(sqlUpdateCommand,lSQLObj);
		  sqlUpdateCommand = createWhere(sqlUpdateCommand,lSQLObj);
//		  sqlUpdateCommand = createGroupBy(sqlUpdateCommand);
		  sqlUpdateCommand = createOrderBy(sqlUpdateCommand);
		  sqlUpdateCommand = createLimit(sqlUpdateCommand);
		  lSQLObj.setSingleSQLCommands(sqlUpdateCommand);

		}
		SQLObjectContainer.put(this.getId(), lSQLObj);
		this.myDataObject.setSQL(SQLObjectContainer);
	  }
	  
	  private String createUpdate(String sqlUpdateCommand, SQLObject lSQLObj){
			StringBuffer sb = new StringBuffer();
			sb.append(getLeadingTable().getName() );
			sqlUpdateCommand = sqlUpdateCommand.replaceAll("#TABLE_CONDITION#", sb.toString());

			return sqlUpdateCommand;

	  }	
	private String createWhere(String sqlUpdateCommand,SQLObject lSQLObj){
			if(getMainTable()!= null){
			  StringBuffer  whereConditionString = new StringBuffer();

			  loadMaxWhereValues(this.getLeadingTable());
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
	  private Table getMainTable(){
			Table table = (Table) getTables()[Utility.getNumberFromRandomMinMax(new Long(0), new Long(getTables().length) ).intValue()];

			if(table != null){
				this.setLeadingTable(table);
				return getLeadingTable();
			}

			return table;

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
					e.printStackTrace();
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
