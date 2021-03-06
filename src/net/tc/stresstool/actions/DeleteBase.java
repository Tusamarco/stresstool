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
		this.myDataObject.executeSqlObject(this.getActionCode(),(java.sql.Connection) conn);
		if(!this.isStickyconnection()){
		  getConnProvider().returnConnection((java.sql.Connection)conn);
		}
		/*
		 * Sleeping beauty
		 */
		this.setSleepingTimeNs(this.getSleepDelete());
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

//		if(getBatchSize() < 1){
//		  setBatchSize(1);
//		}

		for(int batchRun = 0 ; batchRun < getBatchSize(); batchRun++){
		  ArrayList<Table> tables = new ArrayList<Table>();
		  Table newTable = getMainTable(lSQLObj);
		  tables.add(newTable);
		  lSQLObj.setSourceTables(tables);
		  
		  String sqlDeleteCommand = DataObject.SQL_DELETE_TEMPLATE;
/* deprecated
 * 		  loadMaxWhereValues(newTable);
 */
		  sqlDeleteCommand = createDelete(sqlDeleteCommand,lSQLObj);
		  sqlDeleteCommand = createWhere(sqlDeleteCommand,newTable);
		  if(sqlDeleteCommand == null)
			  return;
		  
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
	private String createWhere(String sqlDeleteCommand,Table table){
			if(this.getLeadingTable()!= null){
			  StringBuffer  whereConditionString = new StringBuffer();

			  whereConditionString.append(" WHERE ");
			  
			  try {
				Connection conn = this.getConnProvider().getConnection();
				String conditions = table.parseWhere(this.getMyDataObject().SQL_DELETE,conn);
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
			  sqlDeleteCommand = sqlDeleteCommand.replaceAll("#WHERE#", whereConditionString.toString());

			  return sqlDeleteCommand;
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
			int writeFactor = Utility.getNumberFromRandomMinMax(new Long(1), 100).intValue();
			
			Table table = (Table) getTables()[Utility.getNumberFromRandomMinMax(new Long(0), new Long(getTables().length) ).intValue()];
		    int writeTableFactor = (int) ((Table)table).getWriteFactor();
		    
		    while(writeTableFactor < writeFactor || table.isReadOnly()) {
		    	table = (Table) getTables()[Utility.getNumberFromRandomMinMax(new Long(0), new Long(getTables().length) ).intValue()];
		        writeTableFactor = (int) ((Table)table).getWriteFactor();
				try{StressTool.getLogProvider().getLogger(LogProvider.LOG_SQL).debug("Skip table by write [DELETE] factor TABLE " +
						  table.getName() 
						  + " factor = "
						  + table.getWriteFactor()
						  + " Filter write factor = " + writeFactor);}catch(StressToolConfigurationException e){}    	    	
		    }
			
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
		table.parseAttributeWhere(table.getWhereCondition(this.getMyDataObject().SQL_DELETE), maxAttribute);
		StringBuffer sb = new StringBuffer();
		
		for(Object attrib : (Object[]) (maxAttribute.toArray())){
				if (sb.length() > 0)
					sb.append(",");
				sb.append("MAX("+ ((Attribute)attrib).getName() +") as " +((Attribute)attrib).getName()+" ");
		}	
			String SQL = "Select " + sb.toString() + " FROM "+ table.getSchemaName() +"." + table.getName();
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
									table.getMetaAttributes().put(((Attribute)attrib).getName(), ((Attribute)attrib));
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
							StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
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
