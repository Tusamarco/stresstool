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

public class PartitionDefinitionPostgres  extends PartitionDefinition{
	
	/** 
	 * Specific for Postgres object
	 */
	private ArrayList<String> partitionsName = new ArrayList<String>();
	
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
	
	public PartitionDefinitionPostgres(){
		super();
		
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

	/**
	 * @param function the function to set
	 */
	@Override
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
	 * @param interval the interval to set
	 */
	@Override
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
    public StringBuffer getSQLMainTablePartitionDefinition() {
	if (this == null)
	    return null;

	StringBuffer sbPartition = new StringBuffer();
	String partitionType = this.getPartitionType();

	switch (partitionType) {
	case PartitionDefinition.PARTITION_TYPE_COLUMNS:
	   //TODO still to do 
//		sbPartition.append(getColumns(this));
	    break;
	case PartitionDefinition.PARTITION_TYPE_LIST:
		//TODO still to do
//		sbPartition.append(getList(this));
	    break;
	case PartitionDefinition.PARTITION_TYPE_RANGE:
	    sbPartition.append(this.getRangeTableDefinition(this));
	    break;
	case PartitionDefinition.PARTITION_TYPE_HASH:
		//TODO still to do
//	    sbPartition.append(getHash(this));
	    break;
	case PartitionDefinition.PARTITION_TYPE_KEY:
		//TODO still to do
//	    sbPartition.append(getKey(this));
	    break;

	}
	return sbPartition;

    }
	@Override
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

    @Override
    protected String getKey(PartitionDefinition partitionDefinition) {
	String sql = "PARTITION BY KEY ("
		+ Utility.getArrayListAsDelimitedString(
			partitionDefinition.getAttributes(), ",")
		+ ") \n PARTITIONS " + partitionDefinition.getPartitionsSize();
	return sql;
    }
    
    @Override
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

    public String getRangeTableDefinition(PartitionDefinition pd){
    		StringBuffer sql = new StringBuffer();
    		String name = null;
    		String values = null;
    		sql.append("PARTITION BY RANGE ");
    		sql.append("(");
//    		if (pd.getFunction() != null && !pd.equals(""))
//    		    sql.append(pd.getFunction() + "(");
    		sql.append(Utility.getArrayListAsDelimitedString(pd.getAttributes(),
    			","));
//    		if (pd.getFunction() != null && !pd.equals(""))
//    		    sql.append(")");
    		sql.append(")\n ");
    		
    		return sql.toString();
    		
    }
    
    
    @Override
    protected String getRange(PartitionDefinition pd) {
	StringBuffer sql = new StringBuffer();
	String name = null;
	String values = null;
//	sql.append("PARTITION BY RANGE ");
//	sql.append("(");
//	if (pd.getFunction() != null && !pd.equals(""))
//	    sql.append(pd.getFunction() + "(");
//	sql.append(Utility.getArrayListAsDelimitedString(pd.getAttributes(),
//		","));
//	if (pd.getFunction() != null && !pd.equals(""))
//	    sql.append(")");
//	sql.append(") ");
//	sql.append("( \n");

	SynchronizedMap pts = null;
	/*
	 * if the partitions are not declared in the Json file or present 
	 * in the object and date is present (start|end) then i
	 * Partitions will be calculate
	 */
    String max="0";
    String min="0";
    boolean calculated = false;
    
	if (pd.getPartitions() == null
		&& pd.getStartDate() != null 
		&& pd.getEndDate() != null) {
		
		pts =(SynchronizedMap) partitionRangeCalculation(pd); 
		calculated = true;

	}
	else{
	    pts = pd.getPartitions();
	}

	    
	    for(int i =0; i < pts.size(); i++){
			if(i == 0 && calculated ) {
		    	String key = (String) pts.getKeyasOrderedArray()[i];
				Partition pt = (Partition) pts.get(key);
				name = pt.getName();
				min = pt.getValueDeclaration();
				
				i++;
			} 
	    	
	    	String key = (String) pts.getKeyasOrderedArray()[i];
			Partition pt = (Partition) pts.get(key);
			name = pt.getName();
			values = pt.getValueDeclaration();
			max = values;
			
			if (i > 0) {
			    sql.append(";\n ");
			}
			String partitionTableName= name != null?"_"+name:Integer.toString(i+1);
			this.getPartitionsName().add(this.getTableName()+"_P"+partitionTableName);
			sql.append("CREATE TABLE "+ this.getTableName()+"_P"+ partitionTableName +" PARTITION OF "+ this.getTableName());
	//		sql.append(name);
			sql.append(" FOR VALUES FROM ");
			sql.append("(");
	//		if (pd.getFunction() != null && !pd.equals(""))
	//		    sql.append(pd.getFunction() + "(");
			sql.append( min);
			sql.append( ") TO (" + max);
	//		if (pd.getFunction() != null && !pd.equals(""))
	//		    sql.append(")");
	
			sql.append(")");
			min = max; 
	    }
		

	sql.append(";\n");
	return sql.toString();

    }
    
    @Override
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

    @Override
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
	calCurr.add(interval, -1);
	Partition part = null;
	while (calCurr.before(cal2)) {
	    calCurr.add(interval, 1);
	    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug(calCurr.get(calCurr.YEAR)+ "  " + calCurr.get(calCurr.MONTH) + " " + calCurr.get(calCurr.DAY_OF_MONTH));} catch (StressToolConfigurationException e) {}
	    String newDate = Utility.getYear(calCurr) + "-"
		    + Utility.getMonthNumber(calCurr) + "-"
		    + Utility.getDayNumber(calCurr);
	    part = new Partition();
	    part.setValueDeclaration("'" + newDate + "'");
	    part.setName("PT" + newDate.replace("-", ""));
	    partitions.put(part.getName(), part);
	}

	return partitions;

    }

	public ArrayList getPartitionsName() {
		return partitionsName;
	}

	private void setPartitionsName(ArrayList partitionsName) {
		this.partitionsName = partitionsName;
	}

	
 }
