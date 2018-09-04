package net.tc.data.db;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolGenericException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.TimeTools;
import net.tc.utils.Utility;

/**
 * 
 * @author tusa
 * v1
 */

public class PartitionDefinition {
	
	public static final String PARTITION_TYPE_RANGE = "RANGE";
	public static final String PARTITION_TYPE_LIST = "LIST";
	public static final String PARTITION_TYPE_COLUMNS = "COLUMNS";
	public static final String PARTITION_TYPE_HASH = "HASH";
	public static final String PARTITION_TYPE_KEY = "KEY";
	
	/**
	 * Functions allow in the partitions
	 */
		
	static final String FUNCTION_PARTITION_ABS = "ABS";
	static final String FUNCTION_PARTITION_CEILING = "CEILING";
	static final String FUNCTION_PARTITION_DAY = "DAY";
	static final String FUNCTION_PARTITION_DAYOFMONTH = "DAYOFMONTH";
	static final String FUNCTION_PARTITION_DAYOFWEEK = "DAYOFWEEK";
	static final String FUNCTION_PARTITION_DAYOFYEAR = "DAYOFYEAR";
	static final String FUNCTION_PARTITION_DATEDIFF = "DATEDIFF";
	static final String FUNCTION_PARTITION_EXTRACT = "EXTRACT";
	static final String FUNCTION_PARTITION_FLOOR = "FLOOR";
	static final String FUNCTION_PARTITION_HOUR = "HOUR";
	static final String FUNCTION_PARTITION_MICROSECOND = "MICROSECOND";
	static final String FUNCTION_PARTITION_MINUTE = "MINUTE";
	static final String FUNCTION_PARTITION_MOD = "MOD";
	static final String FUNCTION_PARTITION_MONTH = "MONTH";
	static final String FUNCTION_PARTITION_QUARTER = "QUARTER";
	static final String FUNCTION_PARTITION_SECOND = "SECOND";
	static final String FUNCTION_PARTITION_TIME_TO_SEC = "TIME_TO_SEC";
	static final String FUNCTION_PARTITION_TO_DAYS = "TO_DAYS";
	static final String FUNCTION_PARTITION_TO_SECONDS = "TO_SECONDS";
	static final String FUNCTION_PARTITION_UNIX_TIMESTAMP = "UNIX_TIMESTAMP";
	static final String FUNCTION_PARTITION_WEEKDAY = "WEEKDAY";
	static final String FUNCTION_PARTITION_YEAR = "YEAR";
	static final String FUNCTION_PARTITION_YEARWEEK = "YEARWEEK";

	public static final String INTERVAL_PARTITION_DAY = "DAY";
	public static final String INTERVAL_PARTITION_WEEK = "WEEK";
	public static final String INTERVAL_PARTITION_MONTH = "MONTH";
	public static final String INTERVAL_PARTITION_YEAR = "YEAR";
	
	protected String tableName = null;
	protected String partitionType = null;
	protected String partitionsSize = null;
	protected ArrayList attributes = null;
	protected String partitionDeclaration = null;
	protected SynchronizedMap partitions = null;
	protected Date startDate = null;
	protected Date endDate = null;
	protected String function = null;
	protected String interval = INTERVAL_PARTITION_DAY;
	
		
//	"lists":{"list":[]}
//	"ranges":{"range":[]}
	
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPartitionType() {
		return partitionType;
	}
	public void setPartitionType(String partitionTypeIn) throws StressToolGenericException {
	    if(partitionTypeIn == null)
		return;
	    
	    switch (partitionTypeIn.toUpperCase()){
	    	case PARTITION_TYPE_COLUMNS: partitionType =PARTITION_TYPE_COLUMNS; break; 
	    	case PARTITION_TYPE_LIST: partitionType =PARTITION_TYPE_LIST; break;
	    	case PARTITION_TYPE_RANGE: partitionType =PARTITION_TYPE_RANGE; break;
	    	case PARTITION_TYPE_HASH: partitionType =PARTITION_TYPE_HASH; break;
	    	case PARTITION_TYPE_KEY: partitionType =PARTITION_TYPE_KEY; break;
	    	default: throw new StressToolGenericException(ExceptionMessages.INVALID_PARTITION_TYPE); 
	    	
	    }
	
	    
	}
	public String getPartitionsSize() {
		return partitionsSize;
	}
	public void setPartitionsSize(String partitionsSize) {
		this.partitionsSize = partitionsSize;
	}
	public ArrayList getAttributes() {
		return attributes;
	}
	public void setAttributes(ArrayList arColPart) {
		this.attributes = arColPart;
	}
	public String getPartitionDeclaration() {
		return partitionDeclaration;
	}
	public void setPartitionDeclaration(String partitionDeclaration) {
		this.partitionDeclaration = partitionDeclaration;
	}
	public SynchronizedMap getPartitions() {
		return partitions;
	}
	public void setPartitions(SynchronizedMap partitions) {
		this.partitions = partitions;
	}
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
	    return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
	    if(startDate == null) return;
	    String format = "yyyy-MM-dd";
	    this.startDate = TimeTools.getDate(startDate, format);
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
	    return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
	    if(endDate == null) return;
	    String format = "yyyy-MM-dd";
	    this.endDate = TimeTools.getDate(endDate, format);
	}
	/**
	 * @return the function
	 */
	public String getFunction() {
	    return function;
	}
	/**
	 * @param function the function to set
	 */
	public void setFunction(String functionIn) {
	    if(functionIn == null) return;
	    
	switch (functionIn.toUpperCase()) {
	case FUNCTION_PARTITION_ABS:
	    function = FUNCTION_PARTITION_ABS;
	    break;
	case FUNCTION_PARTITION_CEILING:
	    function = FUNCTION_PARTITION_CEILING;
	    break;
	case FUNCTION_PARTITION_DAY:
	    function = FUNCTION_PARTITION_DAY;
	    break;
	case FUNCTION_PARTITION_DAYOFMONTH:
	    function = FUNCTION_PARTITION_DAYOFMONTH;
	    break;
	case FUNCTION_PARTITION_DAYOFWEEK:
	    function = FUNCTION_PARTITION_DAYOFWEEK;
	    break;
	case FUNCTION_PARTITION_DAYOFYEAR:
	    function = FUNCTION_PARTITION_DAYOFYEAR;
	    break;
	case FUNCTION_PARTITION_DATEDIFF:
	    function = FUNCTION_PARTITION_DATEDIFF;
	    break;
	case FUNCTION_PARTITION_EXTRACT:
	    function = FUNCTION_PARTITION_EXTRACT;
	    break;
	case FUNCTION_PARTITION_FLOOR:
	    function = FUNCTION_PARTITION_FLOOR;
	    break;
	case FUNCTION_PARTITION_HOUR:
	    function = FUNCTION_PARTITION_HOUR;
	    break;
	case FUNCTION_PARTITION_MICROSECOND:
	    function = FUNCTION_PARTITION_MICROSECOND;
	    break;
	case FUNCTION_PARTITION_MINUTE:
	    function = FUNCTION_PARTITION_MINUTE;
	    break;
	case FUNCTION_PARTITION_MOD:
	    function = FUNCTION_PARTITION_MOD;
	    break;
	case FUNCTION_PARTITION_MONTH:
	    function = FUNCTION_PARTITION_MONTH;
	    break;
	case FUNCTION_PARTITION_QUARTER:
	    function = FUNCTION_PARTITION_QUARTER;
	    break;
	case FUNCTION_PARTITION_SECOND:
	    function = FUNCTION_PARTITION_SECOND;
	    break;
	case FUNCTION_PARTITION_TIME_TO_SEC:
	    function = FUNCTION_PARTITION_TIME_TO_SEC;
	    break;
	case FUNCTION_PARTITION_TO_DAYS:
	    function = FUNCTION_PARTITION_TO_DAYS;
	    break;
	case FUNCTION_PARTITION_TO_SECONDS:
	    function = FUNCTION_PARTITION_TO_SECONDS;
	    break;
	case FUNCTION_PARTITION_UNIX_TIMESTAMP:
	    function = FUNCTION_PARTITION_UNIX_TIMESTAMP;
	    break;
	case FUNCTION_PARTITION_WEEKDAY:
	    function = FUNCTION_PARTITION_WEEKDAY;
	    break;
	case FUNCTION_PARTITION_YEAR:
	    function = FUNCTION_PARTITION_YEAR;
	    break;
	case FUNCTION_PARTITION_YEARWEEK:
	    function = FUNCTION_PARTITION_YEARWEEK;
	    break;
	    
	    
	    }
		
	}
	/**
	 * @return the interval
	 */
	public String getInterval() {
	    return interval;
	}
	/**
	 * @param interval the interval to set
	 */
	public void setInterval(String intervalIn) {
	    if(intervalIn == null) return;
	    
	    switch(intervalIn.toUpperCase()){
	    
	    	case INTERVAL_PARTITION_DAY: interval = "DAY"; break;
	    	case INTERVAL_PARTITION_WEEK: interval = "WEEK";break;
	    	case INTERVAL_PARTITION_MONTH: interval = "MONTH";break;
	    	case INTERVAL_PARTITION_YEAR: interval = "YEAR";break;
	    } 
	    
	    
	}
	/**
	 * this method is used to calculate the 
	 * partitions and to return a string buffer containing the SQL
	 * @return
	 */


	/**
	 * this method is used to calculate the 
	 * partitions and to return a string buffer containing the SQL
	 * @return
	 */
    public StringBuffer getSQLPartitionDefinition() {
	if (this == null)
	    return null;

	StringBuffer sbPartition = new StringBuffer();
	String partitionType = this.getPartitionType();

	switch (partitionType) {
	case PartitionDefinition.PARTITION_TYPE_COLUMNS:
	    sbPartition.append(getColumns(this));
	    break;
	case PartitionDefinition.PARTITION_TYPE_LIST:
	    sbPartition.append(getList(this));
	    break;
	case PartitionDefinition.PARTITION_TYPE_RANGE:
	    sbPartition.append(getRange(this));
	    break;
	case PartitionDefinition.PARTITION_TYPE_HASH:
	    sbPartition.append(getHash(this));
	    break;
	case PartitionDefinition.PARTITION_TYPE_KEY:
	    sbPartition.append(getKey(this));
	    break;

	}
	return sbPartition;

    }
    
    protected String getKey(PartitionDefinition partitionDefinition) {
	String sql = "PARTITION BY KEY ("
		+ Utility.getArrayListAsDelimitedString(
			partitionDefinition.getAttributes(), ",")
		+ ") \n PARTITIONS " + partitionDefinition.getPartitionsSize();
	return sql;
    }

    protected String getHash(PartitionDefinition pd) {
	StringBuffer sql = new StringBuffer();
	sql.append("PARTITION BY HASH (");
	if (pd.getFunction() != null && !pd.equals(""))
	    sql.append(pd.getFunction() + "(");
	sql.append(Utility.getArrayListAsDelimitedString(pd.getAttributes(),
		","));
	if (pd.getFunction() != null && !pd.equals(""))
	    sql.append(")");
	sql.append(") \n PARTITIONS ");
	sql.append(pd.getPartitionsSize());
	return sql.toString();
    }

    protected String getRange(PartitionDefinition pd) {
	StringBuffer sql = new StringBuffer();
	String name = null;
	String values = null;
	sql.append("PARTITION BY RANGE ");
	sql.append("(");
	if (pd.getFunction() != null && !pd.equals(""))
	    sql.append(pd.getFunction() + "(");
	sql.append(Utility.getArrayListAsDelimitedString(pd.getAttributes(),
		","));
	if (pd.getFunction() != null && !pd.equals(""))
	    sql.append(")");
	sql.append(") ");
	sql.append("( \n");

	SynchronizedMap pts = null;
	/*
	 * if the partitions are not declared in the Json file or present 
	 * in the object and date is present (start|end) then i
	 * Partitions will be calculate
	 */
	if (pd.getPartitions() == null
		&& pd.getStartDate() != null 
		&& pd.getEndDate() != null) {
		
		pts =(SynchronizedMap) partitionRangeCalculation(pd); 
	}
	else{
	    pts = pd.getPartitions();
	}
	    
//	    int i = 0;
	    for(int i =0; i < pts.size(); i++){
		String key = (String) pts.getKeyasOrderedArray()[i];
		Partition pt = (Partition) pts.get(key);
		name = pt.getName();
		values = pt.getValueDeclaration();
		if (i > 0) {
		    sql.append(",\n ");
		}

		sql.append("PARTITION ");
		sql.append(name);
		sql.append(" VALUES LESS THAN ");
		sql.append("(");
		if (pd.getFunction() != null && !pd.equals(""))
		    sql.append(pd.getFunction() + "(");
		sql.append( values);
		if (pd.getFunction() != null && !pd.equals(""))
		    sql.append(")");

		sql.append(")");
	    }
		

	sql.append("\n)");
	return sql.toString();

    }

    protected String getList(PartitionDefinition pd) {
	StringBuffer sql = new StringBuffer();
	String name = null;
	String values = null;
	sql.append("PARTITION BY LIST ");
	sql.append("(");
	sql.append(Utility.getArrayListAsDelimitedString(pd.getAttributes(),
		","));
	sql.append(") ");
	sql.append("( \n");
	if (pd.getPartitions() != null) {
	    SynchronizedMap pts = pd.getPartitions();

	    for(int i =0; i < pts.size(); i++){
		String key = (String) pts.getKeyasOrderedArray()[i];
		Partition pt = (Partition) pts.get(key);
		name = pt.getName();
		values = pt.getValueDeclaration();
		if (i > 0) {
		    sql.append(",\n ");
		}

		sql.append("PARTITION ");
		sql.append(name);
		sql.append(" VALUES IN ");
		sql.append("(" + values + ") \n");
	    }
	} else {
	    return null;
	}
	sql.append("\n)");
	return sql.toString();

    }

    protected String getColumns(PartitionDefinition partitionDefinition) {
	// TODO Auto-generated method stub
	return null;
    }

    protected Map partitionRangeCalculation(PartitionDefinition pd){
	if (pd == null || pd.getStartDate() == null || pd.getEndDate() == null)
	    return null;

	Map partitions = new SynchronizedMap(0);

	Calendar calCurr = new GregorianCalendar();
	Calendar cal1 = new GregorianCalendar();
	Calendar cal2 = new GregorianCalendar();
	calCurr.setTime(pd.getStartDate());
	cal1.setTime(pd.getStartDate());
	cal2.setTime(pd.getEndDate());

	int interval = 0;

	switch (pd.getInterval()) {

	case PartitionDefinition.INTERVAL_PARTITION_DAY:
	    interval = Calendar.DAY_OF_YEAR;
	    break;
	case PartitionDefinition.INTERVAL_PARTITION_WEEK:
	    interval = Calendar.WEEK_OF_YEAR;
	    break;
	case PartitionDefinition.INTERVAL_PARTITION_MONTH:
	    interval = Calendar.MONTH;
	    break;
	case PartitionDefinition.INTERVAL_PARTITION_YEAR:
	    interval = Calendar.YEAR;
	    break;
	}
	Partition part = null;
	while (calCurr.before(cal2)) {
	    calCurr.add(interval, 1);
	    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(calCurr.get(calCurr.YEAR)+ "  " + calCurr.get(calCurr.MONTH) + " " + calCurr.get(calCurr.DAY_OF_MONTH));} catch (StressToolConfigurationException e) {}
	    String newDate = Utility.getYear(calCurr) + "-"
		    + Utility.getMonthNumber(calCurr) + "-"
		    + Utility.getDayNumber(calCurr);
	    part = new Partition();
	    part.setValueDeclaration("\"" + newDate + "\"");
	    part.setName("PT" + newDate.replace("-", ""));
	    partitions.put(part.getName(), part);
	}

	return partitions;

    }

    protected int daysBetween(java.util.Date date, java.util.Date date2) {

	return (int) ((date2.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));
    }
	
 }
