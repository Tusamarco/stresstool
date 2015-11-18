package net.tc.data.db;

import java.util.ArrayList;
import java.util.Date;

import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolGenericException;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.TimeTools;

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
	
	private String tableName = null;
	private String partitionType = null;
	private String partitionsSize = null;
	private ArrayList attributes = null;
	private String partitionDeclaration = null;
	private SynchronizedMap partitions = null;
	private Date startDate = null;
	private Date endDate = null;
	private String function = null;
	private String interval = null;
	
		
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
	
	
	
 }
