package net.tc.stresstool.actions;

import java.io.FileReader;
import java.lang.ref.SoftReference;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.management.InvalidApplicationException;

import net.tc.data.db.Attribute;
import net.tc.data.db.Schema;
import net.tc.data.db.Table;
import net.tc.data.generic.DataObject;
import net.tc.data.generic.SQLObject;
import net.tc.jsonparser.StructureDefinitionParser;
import net.tc.jsonparser.StructureDefinitionParserMySQL;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolActionException;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;

import org.ini4j.jdk14.edu.emory.mathcs.backport.java.util.Arrays;
import org.json.simple.parser.JSONParser;



/**
 * v 1.2
 */

public class InsertEmployeesTest extends StressActionBase implements WriteAction,CreateAction {

  private int numberOfprimaryTables=1;
  private int numberOfSecondaryTables=0;

  private boolean useAutoIncrement=true;

  private int sleepWrite=0;
  private boolean lazyCreation=true;
  private int lazyInterval=5000;
  private int batchSize = 0; 
  private String jsonFile = "";
  private String tableEngine = "InnoDB";

  private ArrayList <String> Name = null;
  private ArrayList <String> LastName = null;
  private ArrayList <String> email = null;
  private Map<String,String> city = null;
  private ArrayList <String> country = null;
  private ArrayList <String> departments = null;
  private ArrayList <String> titles = null;
  private Integer today = 0; 
  private ArrayList<Long> emp_max = new ArrayList<Long>();	
  private Map<String,ArrayList<EmployeeShort>> tableEmpNo = new SynchronizedMap(0);



  private DataObject myDataObject =null;

  public String getJsonFile() {
	return jsonFile;
  }
  public void setJsonFile(String jsonFile) {
	this.jsonFile = jsonFile;
  }
  @Override
  public void LoadData() {
	// TODO Auto-generated method stub

  }
  @Override
  public void TruncateTables(String[] tables) {

	String TruncateTables1 = "Truncate table tbtest";
	String TruncateTables2 = "Truncate table tbtest_child";

	
	
	  try {
		Connection conn = this.getConnProvider().getSimpleConnection();
		Statement stmt = null;
		if(conn != null 
			&& !conn.isClosed()){

		  stmt =conn.createStatement();


			if(isTruncate())
			{
				System.out.println("****============================================================================*******");
				if(!isDoSimplePk()){
					for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
						System.out.println("**** Please wait TRUNCATE table tbtest_child" + iTable + " it could take a LOT of time *******");
						stmt.execute(TruncateTables2+iTable);
					}
				}

				for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
					System.out.println("**** Please wait TRUNCATE table tbtest" + iTable + " it could take a LOT of time *******");
					stmt.execute("SET FOREIGN_KEY_CHECKS=0");
					stmt.execute(TruncateTables1+iTable);
					stmt.execute("SET FOREIGN_KEY_CHECKS=1");
				}

				System.out.println("**** TRUNCATE finish *******");
				System.out.println("****============================================================================*******");

			}
		
		
		
		
		}
		conn.close();
		conn = null;
	  } catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	  }


	

  }
  @Override
  public void CreateActionTablePrimary() {
	// TODO Auto-generated method stub

  }
  @Override
  public void getActionTablePrimary() {
	// TODO Auto-generated method stub

  }
  @Override
  public void setActionTablePrimary() {
	// TODO Auto-generated method stub

  }
  @Override
  public void getActionTableSecondary() {
	// TODO Auto-generated method stub

  }
  @Override
  public void setActionTableSecondary() {
	// TODO Auto-generated method stub

  }
  @SuppressWarnings("finally")
  @Override
  public Schema  CreateSchema(boolean createSchema) {
	if(this.getNumberOfprimaryTables() != this.getNumberOfSecondaryTables()){
		try {
			throw new InvalidApplicationException("This Class require that:\n"
					+ " Main and Child tables have the same number"
					+ " in the configurarion"
					+ "\nCheck your config file for:"
					+ "numberOfprimaryTables=x \nnumberOfSecondaryTables=x");
		} catch (InvalidApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	// Custom schema creation this is the default for the stresstool but can be anything  
	String DropTables1 = "Drop table IF EXISTS tbtest";
	String DropTables2 = "Drop table IF EXISTS tbtest_child";
	
	Connection conn =null;
	Statement stmt = null;

	try {
		conn = null;
		if(this.getActiveConnection()==null){
		  try {
			conn = this.getConnProvider().getSimpleConnection();
		  } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		}
		else
		  conn = this.getActiveConnection();

		conn.setAutoCommit(false);
		stmt = conn.createStatement();

		if(isDroptable())
		{
			if(!isDoSimplePk()){
				for(int iTable = 1; iTable <= this.getNumberOfSecondaryTables(); iTable++){
					System.out.println("**** Please wait DROP table tbtest_child" + iTable + " it could take a LOT of time *******");
					stmt.execute(DropTables2+iTable);
				}

			System.out.println("****============================================================================*******");
			for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
				System.out.println("**** Please wait DROP table tbtest" + iTable + " it could take a LOT of time *******");
				stmt.execute(DropTables1+iTable);
			}



			}
			stmt.execute("COMMIT");
			System.out.println("**** DROP finished *******");
			System.out.println("****============================================================================*******");

		}

		if(this.isCreatetable()){			
		

			StringBuffer sb = new StringBuffer();
			stmt.execute("DROP TABLE IF EXISTS tbtestmax");
			stmt.execute("CREATE TABLE IF NOT EXISTS tbtestmax (`tablename` varchar(250) PRIMARY KEY, `maxid` int(11) unsigned) ENGINE="+ getTableEngine() );

			
			for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
				sb.append("CREATE TABLE IF NOT EXISTS tbtest" + iTable );
				
				sb.append("(`emp_no` int(11) unsigned AUTO_INCREMENT NOT NULL,`birth_date` date NOT NULL,`first_name` varchar(14) NOT NULL," +
						"`last_name` varchar(16) NOT NULL,`gender` enum('M','F') NOT NULL,`hire_date` date NOT NULL," +
						"`city_id` int(4) DEFAULT NULL,`CityName` varchar(150) DEFAULT NULL,`CountryCode` char(3) DEFAULT NULL," +
						"`UUID` char(36) DEFAULT NULL, `linked` tinyint(1) NOT NULL DEFAULT 0, "
						+ "`time_create` timestamp NOT NULL default CURRENT_TIMESTAMP, "
						+ "`time_update` timestamp NOT NULL default CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP, "
						+ " PRIMARY KEY (`emp_no`)"
						+ ", KEY `idx_linked` (`linked`,`emp_no`)"
						+ ", KEY `time_created_up` (`time_create`,time_update)"
						+ ")");
				
				sb.append(" ENGINE="+ getTableEngine()) ;

				if(!sb.toString().equals(""))
					stmt.execute(sb.toString());

				sb.delete(0, sb.length());
//				stmt.execute("DROP TRIGGER IF EXISTS test.tbtest"+iTable+"_AFTER_INSERT;");
//				stmt.execute(" CREATE TRIGGER `tbtest"+ iTable +"_AFTER_INSERT` AFTER INSERT ON `tbtest"+iTable+"` FOR EACH ROW \n"
//						+ " BEGIN \n"
//						+ " DECLARE sqlcode INT DEFAULT 0; \n"
//						+ " DECLARE CONTINUE HANDLER FOR 1054 SET sqlcode = 1054; \n"
//						+ " DECLARE CONTINUE HANDLER FOR 1136 SET sqlcode = 1136; \n"
//						+ " SET @LASTINSERT=0; \n"
//						+ " SELECT MAX(emp_no) INTO @LASTINSERT FROM tbtest" + iTable +"; \n"
//						+ " REPLACE INTO tbtestmax values('tbtest"+iTable+"',@LASTINSERT) ; \n"
//						+ " END ");

				
			}
			String tbts1 = sb.toString();

			sb = new StringBuffer();
			
			for(int iTable = 1 ; iTable <= this.getNumberOfSecondaryTables(); iTable++){
				sb.append("CREATE TABLE IF NOT EXISTS tbtest_child" + iTable);
				sb.append("(`emp_no` int(11) unsigned NOT NULL");
				sb.append(",`id` int(11) unsigned AUTO_INCREMENT NOT NULL");
				sb.append(", `salary` int(11)  NOT NULL");
				sb.append(", `from_date` DATE NOT NULL");
				sb.append(", `to_date` DATE NOT NULL");
				sb.append(", `dept_name` VARCHAR(40)  NULL");
				sb.append(", `title` VARCHAR(50)  NULL");
				sb.append(", `time_create` timestamp NOT NULL default CURRENT_TIMESTAMP");
				sb.append(", `time_changed` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP");
				sb.append(", PRIMARY KEY (emp_no,id, from_date)");
				sb.append(", key `emp_no` (`emp_no`)");
				sb.append(", UNIQUE `UK_is` (`id`)");
				if(this.isFKEnable())
				    sb.append(", FOREIGN KEY (`emp_no`) REFERENCES `tbtest"+ iTable +"` (`emp_no`) ON DELETE CASCADE");
				sb.append(") ENGINE="+ getTableEngine());

				if(!sb.toString().equals(""))
					stmt.execute(sb.toString());

				sb.delete(0, sb.length());

			}
			String tbts2 = sb.toString();

//			System.out.println(tbts1);
			if(!isDoSimplePk())
				System.out.println(tbts2);

		}	



	
		if(!this.isStickyconnection()){

		  getConnProvider().returnConnection((com.mysql.jdbc.Connection)conn);
		}


	} catch (Exception ex) {
		ex.printStackTrace(

		);
		return null;
	} finally {

		try {
//			conn.close();
			return null;
		} catch (Exception ex1) {
			ex1.printStackTrace();
			return null;
		}

	}		



  }
  @Override
  public boolean  DropSchema(String[] tables) {
	Map connectionMap = super.getConnectionInformation();
	StringBuffer sbTableDrop = new StringBuffer();

	if(tables !=null
		&& tables.length > 0){

	  try {
		Connection conn = getConnProvider().getSimpleConnection();
		Statement stmt = null;
		if(conn != null 
			&& !conn.isClosed()){

		  stmt =conn.createStatement();

		  try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(" ==== Dropping Tables, Please wait it may takes time ===="  );}catch(StressToolConfigurationException e){}
		  for(String tb : tables){
			String drop = "DROP TABLE IF EXISTS " + tb + " ;" ;
			try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(drop);}catch(StressToolConfigurationException e){}
			stmt.execute(drop);
		  }
		  try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(" ==== Drop Tables END ===="  );}catch(StressToolConfigurationException e){}
		}
		conn.close();
		conn = null;
	  } catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	  }


	}


	return false;

  }
  @Override
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
	this.lazyCreation = lazy;

  }
  @Override
  public boolean islazyCreation() {
	return this.lazyCreation;
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
   * @return the lazyCreation
   */
  public boolean isLazyCreation() {
	return lazyCreation;
  }
  /**
   * @param lazyCreation the lazyCreation to set
   */
  public void setLazyCreation(boolean lazyCreation) {
	this.lazyCreation = lazyCreation;
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

  @Override
  public void ExecuteAction() throws StressToolActionException {
	/**
	 * Db actions
	 */
	Connection conn = null;
	if(this.getActiveConnection()==null){
	  try {
		conn = this.getConnProvider().getSimpleConnection();
	  } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
	}
	else
	  conn = this.getActiveConnection();

	/*
	 * now run the show
	 */

	{
	  ArrayList insert1 = null;
	  ArrayList insert2 = null;
	  Statement stmt = null;
	  
	  if(conn != null)
	  {
		try
		{

		
		  //                ResultSet rs = null;
		  //                ResultSet rs2 = null;
		  
		  ExecutePreliminaryAction();

		  conn.setAutoCommit(false);
		  stmt = conn.createStatement();
		  stmt.execute("SET AUTOCOMMIT=0");

		  String query = null;
		  int pk = 0 ;


		  //IMPLEMENTING lazy
		  Vector v = this.getTablesValues(false);

		  insert1 = (ArrayList<String>) v.get(0);
		  insert2 = (ArrayList<String>) v.get(1);

		  //                    System.out.println(insert1);
		  //                    System.out.println(insert2);

		  //                    pk = ((Integer) v.get(2)).intValue();

		  int[] iLine = {0,0} ;

		  //                    pkStart = StressTool.getNumberFromRandom(2147483647).intValue();
		  //                    pkEnds = StressTool.getNumberFromRandom(2147483647).intValue();
		  long timeStart = System.currentTimeMillis();

		  stmt.execute("BEGIN");
		  {
			Iterator<String> it = insert1.iterator();
			String tmp ="";
			while(it.hasNext()){
			  tmp = (String) it.next();
			  stmt.addBatch(tmp);
			}
		  }

		  if(!this.isDoSimplePk()){
			String tmp ="";
			Iterator<String> it = insert2.iterator();
			while(it.hasNext()){
			  tmp = (String) it.next();
			  String[] tmpsAr = tmp.split(";");
			  if(tmpsAr.length < 1 ){
				stmt.addBatch(tmp);
			  }
			  else{
				for(int xs =0; xs < tmpsAr.length; xs++){
				  stmt.addBatch(tmpsAr[xs]);
				}

			  }

			}
		  }

		}
		catch (SQLException sqle)
		{

		  try {
	        conn.rollback();
          } catch (SQLException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
          }
		  System.out.println("FAILED QUERY1==" + insert1);
		  System.out.println("FAILED QUERY2==" + insert2);
		  sqle.printStackTrace();
		  System.exit(1);


		}
		finally
		{
		  if(conn != null){
			try {
			  stmt.addBatch("COMMIT");
			  stmt.executeBatch();
			  stmt.close();
			}
			catch(SQLException sqle){
			  System.out.println("#####################\n[Warning] Cannot explicitly commit given error\n#################");
			}
		  }
		  else{
			System.out.println("#####################\n[Warning] Cannot explicitly commit given connection was interrupted unexpectedly\n#################");
		  }
		  //                            intDeleteInterval++;
		}

		if(TablesLock.tables != null
			&& TablesLock.tables.size() > 0){
		  TablesLock.deleteIdLockByThreadId(new Long(this.getId()).intValue(), false);
		}



	  }

	  if(!this.isStickyconnection()){

		getConnProvider().returnConnection((com.mysql.jdbc.Connection)conn);
	  }
	}


	/*
	 * END RUN
	 */

	if(!this.isStickyconnection()){

	  getConnProvider().returnConnection((com.mysql.jdbc.Connection)conn);
	}

  }

  /**
   * this is actually executing what the action is suppose to do
   * @throws StressToolActionException 
   */
  @Override
  public boolean ExecutePreliminaryAction() {
	Connection conn = null;
	if(this.getActiveConnection()==null){
	  try {
		conn = this.getConnProvider().getSimpleConnection();
	  } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
	}
	else
	  conn = this.getActiveConnection();


	if(today == 0 )
	  populateLocalInfo(conn);
	try{
	  /**
	   * employees max - min    
	   */
	  ArrayList<EmployeeShort> rowValueempNo = null;
	  Long min = (long) 0;
	  ResultSet rs = null;
	  Statement stmt = null;
	  conn.setAutoCommit(false);
	  stmt = conn.createStatement(); 
	  tableEmpNo.clear();
	  //test


	  rs = stmt.executeQuery("select min(maxid) as min from tbtestmax ");
	  while(rs.next()){
		min=(Long) (rs.getLong(1));

	  }
	  rs.close();

	  if (min > 1 ){
		emp_max.clear();
		Long maxId = (long)2; 
		for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
		  rs = stmt.executeQuery("select max(emp_no) from tbtest" + iTable );
		  while(rs.next()){
			maxId = rs.getLong(1);
			emp_max.add((Long) (maxId));
		  }
		  //              	    	tableEmpNo.put("tbtest"+iTable, maxId);
		  try{
			stmt.execute("update tbtestmax  set maxid="+maxId+" where tablename = 'tbtest"+ iTable +"'");
			conn.commit();
		  }
		  catch (SQLException ssq){ssq.printStackTrace();}
		}
		//              	rs = stmt.executeQuery("select maxid as max, tablename from tbtestmax order by 2 ");

		for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){

		  if(rowValueempNo == null)
			rowValueempNo = new ArrayList<EmployeeShort>();

		  String tableName = "tbtest" + iTable;   


//		  if(this.isDebug())
//			System.out.println("Thread_ID = "+ this.getID() + " TABLE LOCK MAP TABLE = " + tableName + " LOCKS PRESENT = " + TablesLock.size(tableName)  );

		  Integer  iMaxUnlink = 0;
		  rs = stmt.executeQuery("Select count(emp_no) from " + tableName + " where linked=0 ");	
		  while(rs.next()){
			iMaxUnlink = rs.getInt(1);
		  }


		  String sql = "select emp_no,to_days(hire_date) from " + tableName + " where linked=0 order by emp_no limit  " 
			  + Utility.getNumberFromRandomMinMax(0, iMaxUnlink/getNumberOfprimaryTables()) 
			  + ","  
			  + this.getBatchSize();
		  rs = stmt.executeQuery(sql);
		  EmployeeShort rv = null;
		  while(rs.next()){
			rv = new EmployeeShort(rs.getLong(1),rs.getLong(2));

			/**
			 * Try to acquire a lock on this ID if successfully then add to the list if already locked then it will not 
			 * be added to the Hash that will be operated later 
			 */
			if( TablesLock.setempIdLOCK(tableName,new IdLock(rv.getEmpNo(), new Long(getId()).intValue())))
			{
			  rowValueempNo.add(rv);
			}
		  }
		  if(rowValueempNo !=null
			  && rowValueempNo.size() >0
			  ){
			tableEmpNo.put(tableName, new ArrayList(rowValueempNo));
		  }

		  rs.close();
		  rowValueempNo.clear();
		}

		//	                 update tbtest1 join tbtest_child1 on tbtest1.emp_no=tbtest_child1.emp_no set linked=1 where linked
	  }
	  else
	  {
		for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
		  try{
			stmt.execute("replace into tbtestmax values ('tbtest" + iTable + "', 2)" );
			conn.commit();
		  }catch(SQLException ssq){ssq.printStackTrace();}
		}                    
	  }
	  if(!rs.isClosed())
		rs.close();
	  stmt.close();
	  rs =null;
	  stmt = null;

	}
	catch(SQLException exq){

	}
	if(!this.isStickyconnection()){

	  getConnProvider().returnConnection((com.mysql.jdbc.Connection)conn);
	}

	return false;  

  }

  public DataObject inizializeDataObject(DataObject thisSQLObject)
	  throws StressToolActionException {
	thisSQLObject.setSqlType(DataObject.SQL_CREATE);
	thisSQLObject.setBatchLoopLimit(this.getBatchSize());
	thisSQLObject.setLazy(this.lazyCreation);
	thisSQLObject.setLazyInterval(lazyInterval);
	thisSQLObject.setCurrentRunLoop(-1);

	String onDuplicateKey = null;
	String filter = null;
	String sqlTemplate = DataObject.SQL_INSERT_TEMPLATE;
	SynchronizedMap SQLObjectContainer = new SynchronizedMap();

	/**
	 * navigate the schema object for tables then use each table to retrieve
	 * the list of attributes if a filter exists then it is apply and only
	 * the attributes matching it are evaluated on the base of the list of
	 * attributes retrieved the value for each will be generated
	 * 
	 * If batch insert is active the list of values will be product on that
	 * base Also lazy insert may be activated, as such the value creation
	 * for some fields will have repeated value.
	 * 
	 * From lazy insert PK UK TIMESTAMP are excluded, all of them always
	 * contains new values.
	 */


	thisSQLObject.setTables((Table[]) getTables());

	//	StringBuffer sbTables = new StringBuffer();
	StringBuffer sbAttribs = new StringBuffer();
	StringBuffer sbValues = new StringBuffer();

	sbValues.append("(");
	// TODO from here this must become a method for the Dataobject
	/*
	 * the SQLObjectContainer will be used to store the SQLObject referencing to each table<>SQL statement 
	 * SQLObject has the a counter with the number of execution done (lazyExecCount), that will be used to refresh the 
	 * values in accordance to the lazyInterval value 
	 */
	for (Object table : thisSQLObject.getTables()) {
	  if(sbAttribs.length() > 0) sbAttribs.delete(0, sbAttribs.length());

	  int iNumTables = 0;

	  thisSQLObject.setAttribs(getAttribs((Table) table, filter));

	  for (int iAttrib = 0; iAttrib < thisSQLObject.getAttribs().length; iAttrib++) {
		if (iAttrib > 0)
		  sbAttribs.append(",");

		sbAttribs.append(((Attribute) thisSQLObject.getAttribs()[iAttrib]).getName());
	  }

	  if (((Table) table).getParentTable() == null) {
		iNumTables = this.getNumberOfprimaryTables();
	  } else {
		iNumTables = this.getNumberOfSecondaryTables();
	  }

	  SQLObject lSQLObj = new SQLObject();
	  lSQLObj.setBatched(this.getBatchSize() > 1 ? true : false);
	  lSQLObj.setPreparedStatment(false);
	  lSQLObj.setSQLCommandType(DataObject.SQL_CREATE);
	  lSQLObj.setBatchLoops(this.batchSize);
	  lSQLObj.addSourceTables((Table)table);


	  String localSQLTemplate = sqlTemplate;
	  if (((Table) table).getName() != null && ((Table) table).getName().length() > 0) {
		localSQLTemplate = localSQLTemplate.replace("#TABLE#", ((Table) table).getName());
	  } else {
		throw new StressToolActionException(
			"INSERT SQL SYNTAX ISSUE table name null");
	  }

	  if (sbAttribs != null && sbAttribs.length() > 0) {
		localSQLTemplate = localSQLTemplate.replace("#ATTRIBS#", sbAttribs.toString());
	  } else {
		throw new StressToolActionException(
			"INSERT SQL SYNTAX ISSUE attribs names not valid or Null");
	  }
	  if (onDuplicateKey == null) {
		localSQLTemplate = localSQLTemplate.replace("#ON DUPLICATE KEY#", "");
	  } else {
		localSQLTemplate = localSQLTemplate.replace("#ON DUPLICATE KEY#", onDuplicateKey);
	  }

	  lSQLObj.setSqlLocalTemplate(localSQLTemplate);
	  // TODO here
	  lSQLObj.setResetLazy(true);
	  if(!lSQLObj.getValues().equals("")) 
		SQLObjectContainer.put(((Table) table).getName(), lSQLObj);


	}
	// if(sbValues !=null && sbValues.length()> 0){
	// sqlTemplate.replace("#VALUES#", sbValues.toString());
	// }else{
	// throw new
	// StressToolActionException("INSERT SQL SYNTAX ISSUE values not valid or Null");
	// }

	thisSQLObject.setSQL(SQLObjectContainer);
	thisSQLObject.setInizialized(true);
	return thisSQLObject;
  }
  private String getValues(Attribute attribute) {
	// TODO Auto-generated method stub
	return null;
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
  private Object[] getTables() {
	if(getSchema() !=null 
		&& getSchema().getTables() != null){

	  return (Table[]) getSchema().getTables().values().toArray(new Table[getSchema().getTables().size()]);
	}

	return null;
  }

  private void populateLocalInfo(Connection conn){
	try{


	  Statement stmt = null;
	  ResultSet rs = null;
	  conn.setAutoCommit(false);
	  stmt = conn.createStatement();
	  String sqlQuery = "";

	  /**
	   * Populate the single array taking information from the original Employees tables
	   * 
	   * City: Select ID, Name from City order by Name;
	   * 
	   * Country: select Code from Country order by Code;
	   * 
	   * Name: Select distinct first_name from employee 
	   * 
	   * +--------+------------+------------+-----------+--------+------------+---------+
	   * | emp_no | birth_date | first_name | last_name | gender | hire_date  | city_id |
	   * +--------+------------+------------+-----------+--------+------------+---------+
	   * |  10001 | 1953-09-02 | Georgi     | Facello   | M      | 1986-06-26 |    NULL |
	   * +--------+------------+------------+-----------+--------+------------+---------+
	   * 
	   * ABout date random a number starting 712223 and never more then 735630
	   * 
	   */
	  Name = new ArrayList<String>();
	  LastName = new ArrayList<String>();
	  email = new ArrayList<String>();
	  city = new SynchronizedMap();
	  departments = new ArrayList<String>();
	  titles = new ArrayList<String>();
	  //ArrayList country = null;

	  sqlQuery = "Select distinct first_name from employees.employees order by first_name"; 
	  rs = stmt.executeQuery(sqlQuery);
	  /**
	   * Name first	
	   */
	  while(rs.next()){
		Name.add(rs.getString(1));

	  }
	  rs.close();


	  /**
	   * Last second
	   */
	  sqlQuery = "Select distinct last_name from employees.employees order by first_name"; 
	  rs = stmt.executeQuery(sqlQuery);
	  while(rs.next()){
		LastName.add(rs.getString(1));

	  }
	  rs.close();

	  /**
	   * City/Country 
	   */
	   sqlQuery = "Select ID, Name,CountryCode  from world.City order by Name;"; 
	   rs = stmt.executeQuery(sqlQuery);
	   while(rs.next()){
		 city.put(rs.getString(2), rs.getString(1)+"-"+rs.getString(3));

	   }
	   rs.close();
	   /**
	    * departments 
	    */
	   sqlQuery = "SELECT dept_name FROM employees.departments order by 1;"; 
	   rs = stmt.executeQuery(sqlQuery);
	   while(rs.next()){
		 departments.add(rs.getString(1));                    
	   }
	   rs.close();
	   /**
	    * titles 
	    */
	   sqlQuery = "SELECT distinct title FROM employees.titles order by 1;"; 
	   rs = stmt.executeQuery(sqlQuery);
	   while(rs.next()){
		 titles.add(rs.getString(1));                    
	   }
	   rs.close();

	   /**
	    * Today    
	    */
	   sqlQuery = "select to_days(now()) as today;"; 
	   rs = stmt.executeQuery(sqlQuery);
	   while(rs.next()){
		 today= (Integer) (rs.getInt(1));

	   }
	   rs.close();
	   stmt.close();

	}
	catch(SQLException ex){ex.printStackTrace();}



  }
  Vector <ArrayList<String>> getTablesValues(boolean refresh) {

	String longtextFld = "";
	boolean lazy = false;
	int afld = 0;
	long counterFld = 0 ;

	//		if(refresh && !lazyInsert1.equals(""))
	//		{
	//			lazy = true;
	//			longtextFld = lazyLongText;
	//		}
	//		else{
	//			if(operationShort)
	//				longtextFld = StressTool.getStringFromRandom(254).substring(0,240);
	//			else
	//				longtextFld = StressTool.getStringFromRandom(40000);
	//		}

	Vector<ArrayList<String>> v = new Vector<ArrayList<String>>();



	StringBuffer insert1 = new StringBuffer();
	StringBuffer insert2 = new StringBuffer();
	StringBuffer insert3 = new StringBuffer();

	ArrayList<String>  insertList1 = new ArrayList<String>();
	ArrayList<String>  insertList2 = new ArrayList<String>();

	int pk = Utility.getNumberFromRandom(2147483647).intValue();
	String insert1Str = "";
	Map<Long,Integer> updateId = new SynchronizedMap();




	  for(int iTable = 1; iTable <= this.getNumberOfprimaryTables(); iTable++){
		//			    +--------+------------+------------+-----------+--------+------------+---------+
		//				 * | emp_no | birth_date | first_name | last_name | gender | hire_date  | city_id |
		//		                 * +--------+------------+------------+-----------+--------+------------+---------+

		insert1.append("insert INTO tbtest" + iTable + " (emp_no,birth_date,first_name,last_name,gender,hire_date,city_id,CityName,CountryCode,UUID) VALUES");
		if(tableEmpNo.size() > 0 && tableEmpNo.get("tbtest" + iTable) != null )
		  insert2.append("insert INTO tbtest_child" + iTable + " (emp_no,id,salary,from_date,to_date,dept_name,title) VALUES");
		insert3.append(0);

		if(this.getBatchSize() > 1)
		{
		  for(int ibatch= 0 ; ibatch <=this.getBatchSize(); ibatch++ )
		  {
			String nameString = Name.get(Utility.getNumberFromRandomMinMax(0, Name.size()-1).intValue());					
			String lastNString = LastName.get(Utility.getNumberFromRandomMinMax(0, LastName.size()-1).intValue());
			String cityString  = (String)city.keySet().toArray()[Utility.getNumberFromRandomMinMax(0, city.size()-1).intValue()];
			String[] cityN_ISO= ((String)city.get(cityString)).split("-");

			int fromDaysBirth = Utility.getNumberFromRandomMinMax(712223, today - 7300).intValue();
			int fromDaysHire = Utility.getNumberFromRandomMinMax(fromDaysBirth + 7300, today).intValue();
			String gender ="";
			if (fromDaysBirth % 2 == 0) {
			  gender ="M";
			} else {
			  gender ="F";
			}

			if (ibatch > 0){
			  insert1.append(",");
			}
			insert1.append("(NULL,FROM_DAYS("+ fromDaysBirth +")," 
				+ "'" + nameString + "',"
				+ "'" + lastNString + "',"
				+ "'" + gender + "',"
				+ "FROM_DAYS("+ fromDaysHire + "),"
				+ cityN_ISO[0] + ","
				+ "'" + cityString + "',"
				+ "'" + cityN_ISO[1] + "',"
				+ "UUID())");
		  }
		}
		else
		{
		  String nameString = Name.get(Utility.getNumberFromRandomMinMax(0, Name.size()-1).intValue());					
		  String lastNString = LastName.get(Utility.getNumberFromRandomMinMax(0, LastName.size()-1).intValue());
		  String cityString  = (String)city.keySet().toArray()[Utility.getNumberFromRandomMinMax(0, city.size()-1).intValue()];
		  String[] cityN_ISO= ((String)city.get(cityString)).split("-");

		  int fromDaysBirth = Utility.getNumberFromRandomMinMax(712223, today - 7300).intValue();
		  int fromDaysHire = Utility.getNumberFromRandomMinMax(fromDaysBirth + 7300, today).intValue();
		  String gender ="";
		  if (fromDaysBirth % 2 == 0) {
			gender ="M";
		  } else {
			gender ="F";
		  }
		  insert1.append("("
			  + "NULL,FROM_DAYS("+ fromDaysBirth +")," 
			  + "'" + nameString + "',"
			  + "'" + lastNString + "',"
			  + "'" + gender + "',"
			  + "FROM_DAYS("+ fromDaysHire + "),"
			  + cityN_ISO[0] + ","
			  + "'" + cityString + "',"
			  + "'" + cityN_ISO[1] + "',"
			  + "UUID())"
			  + "");

		}

		if(tableEmpNo.size() > 0){
		  ArrayList<EmployeeShort> rowValueempNo = (ArrayList<EmployeeShort>) tableEmpNo.get("tbtest"+iTable);

		  if(rowValueempNo != null){
			Iterator<EmployeeShort> it = rowValueempNo.iterator();
			int iLinked =0;

			while(it.hasNext()){
			  EmployeeShort rv = (EmployeeShort)it.next();
			  Long emp_no = rv.getEmpNo();
			  Long fromDaysHire = rv.getHiredDateDay();
			  if(iLinked > 0){
				insert2.append(",");
			  }
			  else if(iLinked > 0 
				  && iLinked > getIBatchInsert()){
				break;
			  }

			  updateId.put(emp_no,0);

			  insert2.append("("
				  + emp_no
				  + ", Null"
				  + ", " + Utility.getNumberFromRandomMinMax(1000, 1000000).intValue()
				  + ", " + "FROM_DAYS("+ fromDaysHire + ")"
				  + ", " + "FROM_DAYS("+ Utility.getNumberFromRandomMinMax(fromDaysHire.intValue(),737060) + ")"
				  + ", '" + departments.get(Utility.getNumberFromRandomMinMax(0, departments.size()).intValue()) + "'"
				  + ", '" + titles.get(Utility.getNumberFromRandomMinMax(0, titles.size()).intValue()) + "'"
				  + ")");
			  iLinked++;
			}

		  }

		}


		if(!insertList1.equals(""))  
		  insertList1.add(insert1.toString());
		if(!insertList2.equals("")){
		  if(!insert2.toString().equals("")){
			Iterator<Long> itid = updateId.keySet().iterator();
			while(itid.hasNext()){
			  insert3.append("," +itid.next() );
			}
			String add_update=" ;update tbtest" +iTable+ " set linked=1 where emp_no in("+insert3.toString()+")";
			insertList2.add(insert2.toString() + add_update);

		  }
		}

		insert1.delete(0, insert1.length());
		insert2.delete(0, insert2.length());
		insert3.delete(0, insert3.length());
	  }
	


	v.add(0,insertList1);
	v.add(1,insertList2);
	//    v.add(2, new Integer(pk));

	return v;

  }
  private int getIBatchInsert() {
	// TODO Auto-generated method stub
	return 0;
  }

}
class EmployeeShort{
  Long hiredDateDay = new Long(0);
  Long empNo = new Long(0);

  public EmployeeShort(Long empNo, Long hireDateDay) {
	this.hiredDateDay = hireDateDay;
	this.empNo = empNo;
  }

  public Long getHiredDateDay() {
	return hiredDateDay;
  }

  public void setHiredDateDay(Long hiredDateDay) {
	this.hiredDateDay = hiredDateDay;
  }

  public Long getEmpNo() {
	return empNo;
  }

  public void setEmpNo(Long empNo) {
	this.empNo = empNo;
  }
}

/**
 *  * @author tusa
 * This class keep an object Map of locks for each Table
 * each table has its own list of IDLock
 * All methods are static and synchronized 
 */
	
class  TablesLock extends SynchronizedMap{
    	@SuppressWarnings("rawtypes")
	static Map <String,Map> tables = null;
	
        /**
         * 
         * @param tableName
         * @param id
         * @return IdLock
         * This method return an object of type IdLock or NULL if not present
         * The presence indicate a lock NULL means instead no Lock 
         * 
         */
	@SuppressWarnings("unchecked")
	public static synchronized IdLock getLockbyId(String tableName, Long id){
	    	Map<Long,IdLock> locks = null;
	    	
	    	if(tableName != null 
		&& !tableName.equals("")
		&& id  !=null
		&& id.longValue() > 0
		){ 
        	    if (tables == null) {
        		tables = new SynchronizedMap(0);
        		return null;
        	    } else {
        		locks = tables.get(tableName);
        	    }
		    
		    if(locks.size() > 0){
			return locks.get(id);
		    }
		}  
			return null;
		
	}
	/**
	 * 
	 * @param tableName
	 * @param lock
	 * @return true or false
	 * 
	 * This method is used to SET a lock on a ID for a given Table
	 * if lock is successfully set return true else false 
	 */
	@SuppressWarnings("unchecked")
	public static synchronized boolean setempIdLOCK(String tableName,IdLock lock){
	    Map<Long,IdLock> locks = null;

	    if(lock != null
		    && lock.recordId != null
		    && tableName != null 
		    && !tableName.equals("")){
		if(tables == null){
		    tables = new SynchronizedMap(0);
		}
		else{
		    locks = tables.get(tableName);
		}
		
		if(locks == null){
		    locks = new SynchronizedMap(0);
		    tables.put(tableName, locks);
		}
		    
		if(!locks.containsKey(lock.recordId)){
			locks.put(lock.getRecordId(), lock);
			tables.put(tableName, locks);
			return true;
		    }
		
	    }
	    return false;
	}
	/**
	 * 
	 * @param tableName
	 * @param id
	 * @return true or false
	 * 
	 * This method is used to remove a lock on a record on a given Table
	 */
	public static synchronized boolean deleteIdLockById(String tableName, Long id){
	    	if(tableName != null 
		&& !tableName.equals("")
		&& tables !=null
		&& tables.get(tableName) != null
		&& id  !=null
		&& id.longValue() > 0
		){
	    	    
	    	    if( tables.get(tableName)!=null
	    		    &&tables.get(tableName).get(id)!=null ){
	    		tables.get(tableName).remove(id);
	    		return true;
	    	    }
		}
	    return false;
	}
	/**
	 * 
	 * @param tableName
	 * @param id
	 * @return true or false
	 * 
	 * This method is used to remove a lock on a record on a given Table
	 */
	public static synchronized boolean deleteIdLockByThreadId(int id, boolean debug){
	    	if(
		tables !=null
		&& tables.size() > 0
		&& id > 0
		){
	    	    Iterator<String> it = tables.keySet().iterator();
	    	    while(it.hasNext()){
	    		String tableName = it.next();
	    		if(tables.get(tableName)!= null && tables.get(tableName).size() > 0){
	    		    Map<Long,IdLock> locks = tables.get(tableName);
	    		    Iterator<Long> itLock = locks.keySet().iterator();
	    		    while(itLock.hasNext()){
	    			Long Id = itLock.next();
	    			if(locks.get(Id).getTHID() == id){
	    			    TablesLock.deleteIdLockById(tableName, Id);
	    			}
	    		    }
			    if (debug)
				System.out.println(" [REMOVE LOCK] Thread_ID = "+ id + " TABLE LOCK MAP TABLE = " + tableName + " LOCKS PRESENT = " + TablesLock.size(tableName)  );

	    		}
	    	    }
	    	    return true;
	    }
		
	    return false;
	}
	
	/**
	 * 
	 * @param tableName
	 * @return true or false
	 * 
	 * This method reset ALL locks for the given table
	 */
	public static synchronized boolean deleteTable(String tableName){
	    	if(tableName != null 
		&& !tableName.equals("")
		&& tables !=null
		&& tables.get(tableName) != null
		){
	    	    
	    	    if( tables.get(tableName)!=null){
	    		tables.remove(tableName);
	    		tables.put(tableName,  new SynchronizedMap(0));
	    		return true;
	    	    }
		}
	    return false;
	}
	public static synchronized int size(String tableName){
	    if(tables !=null
		&& tables.get(tableName) != null
		){
			return tables.get(tableName).size();
	    }
	    else
		return 0;
	    
	}
	
}
/**
 * @author tusa
 * This class Object represent the lock generated by a running thread
 *  with a given Thread ID and record ID
 *  Do not support PK with multiple attributes
 */
class IdLock{
		Long recordId = (long)0;
		int THID = 0;
//		String tableName = null;
		/**
		 * @return the recordId
		 */
		public synchronized Long getRecordId() {
		    return recordId;
		}
		/**
		 * @param recordId the recordId to set
		 */
		public synchronized void setRecordId(Long recordId) {
		    this.recordId = recordId;
		}
		public synchronized int getTHID() {
			return THID;
		}
		public synchronized void setTHID(int tHID) {
			THID = tHID;
		}
		public IdLock(Long recordId, int tHID) {
			this.recordId = recordId;
			THID = tHID;
		}
}