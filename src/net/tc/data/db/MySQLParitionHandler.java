package net.tc.data.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import net.tc.stresstool.statistics.providers.MySQLSuper;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;

@Deprecated
public class MySQLParitionHandler extends MySQLSuper{
    PartitionDefinition partitionDefinition = null;
    
    public MySQLParitionHandler(PartitionDefinition partitionDefinition){
	this.partitionDefinition = partitionDefinition;
    }
    public StringBuffer getSQLPartitionDefinition(){
	if(partitionDefinition == null)
	    return null;

	StringBuffer sbPartition = new StringBuffer();
	String partitionType = partitionDefinition.getPartitionType() ;
	
	switch(partitionType){
            	case PartitionDefinition.PARTITION_TYPE_COLUMNS: sbPartition.append(getColumns(partitionDefinition )); break; 
            	case PartitionDefinition.PARTITION_TYPE_LIST: sbPartition.append(getList(partitionDefinition )); break;
            	case PartitionDefinition.PARTITION_TYPE_RANGE: sbPartition.append(getRange(partitionDefinition )); break;
            	case PartitionDefinition.PARTITION_TYPE_HASH: sbPartition.append(getHash(partitionDefinition )); break;
            	case PartitionDefinition.PARTITION_TYPE_KEY: sbPartition.append(getKey(partitionDefinition )); break;
 
	
	
	}
	return sbPartition;
		
    }

    private  String getKey(PartitionDefinition partitionDefinition) {
	String sql = "PARTITION BY KEY ("+ Utility.getArrayListAsDelimitedString(partitionDefinition.getAttributes(),",") +") \n PARTITIONS "+ partitionDefinition.getPartitionsSize() ;
	return sql;
    }

    private  String getHash(PartitionDefinition pd) {
	StringBuffer sql = new StringBuffer();
		sql.append("PARTITION BY HASH (");
		if(pd.getFunction() !=null && !pd.equals(""))
		    sql.append(pd.getFunction() + "(");
		sql.append( Utility.getArrayListAsDelimitedString(pd.getAttributes(),","));
		if(pd.getFunction() !=null && !pd.equals(""))
		    sql.append(")");
		sql.append(") \n PARTITIONS ");
		sql.append( pd.getPartitionsSize()) ;
	return sql.toString();
    }

    private  String getRange(PartitionDefinition pd) {
	StringBuffer sql = new StringBuffer();
	String name = null;
	String values = null;
	sql.append("PARTITION BY RANGE ");
	sql.append("(");
	if(pd.getFunction() !=null && !pd.equals(""))
	    sql.append(pd.getFunction() + "(");
	sql.append( Utility.getArrayListAsDelimitedString(pd.getAttributes(),","));
	if(pd.getFunction() !=null && !pd.equals(""))
	    sql.append(")");
	sql.append(") ");
	sql.append("( \n");
	
	if(pd.getPartitions() !=null || (pd.getStartDate()!=null && pd.getEndDate() !=null )){
	    try {
		partitionRangeCalculation(pd);
	    } catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    
	    Map pts = pd.getPartitions();
	    Iterator<String> it = pts.keySet().iterator();
	    while( it.hasNext()){
		int i = 0;
		String key = it.next();
		Partition pt = (Partition)pts.get(key);
		name = pt.getName();
		values = pt.getValueDeclaration();
		if(i > 0){
		    sql.append(",\n ");
		}
		 else{
		     i++;
		}
		
		sql.append("PARTITION ");
		sql.append(name);
		sql.append(" VALUES IN ");
		sql.append("(" + values + ") ");
	    }
	}
	else {
	    return null;
	}
	sql.append("\n)");
	return sql.toString();


    }

    private  String getList(PartitionDefinition pd) {
	StringBuffer sql = new StringBuffer();
	String name = null;
	String values = null;
	sql.append("PARTITION BY LIST ");
	sql.append("(");
	sql.append( Utility.getArrayListAsDelimitedString(pd.getAttributes(),","));
	sql.append(") ");
	sql.append("( \n");
	if(pd.getPartitions() !=null){
	    Map pts = pd.getPartitions();
	    Iterator<String> it = pts.keySet().iterator();
	    while( it.hasNext()){
		int i = 0;
		String key = it.next();
		Partition pt = (Partition)pts.get(key);
		name = pt.getName();
		values = pt.getValueDeclaration();
		if(i > 0){
		    sql.append(",\n ");
		}
		 else{
		     i++;
		}
		
		sql.append("PARTITION ");
		sql.append(name);
		sql.append(" VALUES IN ");
		sql.append("(" + values + ") ");
	    }
	}
	else {
	    return null;
	}
	sql.append("\n)");
	return sql.toString();

    }

    private  String getColumns(PartitionDefinition partitionDefinition) {
	// TODO Auto-generated method stub
	return null;
    }
 
    private Map partitionRangeCalculation(PartitionDefinition pd) throws ParseException{
	if(pd == null
		|| pd.getStartDate() == null 
		|| pd.getEndDate() == null)
	    return null;
	
	Map partitions = new SynchronizedMap(0);
	
	Calendar calCurr =new GregorianCalendar();
	Calendar cal1 =new GregorianCalendar();
	Calendar cal2 =new GregorianCalendar();
	calCurr.setTime(pd.getStartDate());
	cal1.setTime(pd.getStartDate());
	cal2.setTime(pd.getEndDate());
	
	
	int interval = 0;
	
	    switch(pd.getInterval()){
	    
	    	case PartitionDefinition.INTERVAL_PARTITION_DAY: interval = Calendar.DAY_OF_YEAR; break;
	    	case PartitionDefinition.INTERVAL_PARTITION_WEEK: interval = Calendar.WEEK_OF_YEAR;break;
	    	case PartitionDefinition.INTERVAL_PARTITION_MONTH: interval = Calendar.MONTH;break;
	    	case PartitionDefinition.INTERVAL_PARTITION_YEAR: interval = Calendar.YEAR;break;
	    } 
	Partition part = null;
	while (calCurr.before(cal2)){
	    calCurr.add(interval, 1);
	    String newDate = calCurr.get(Calendar.YEAR) 
		    + "-" + calCurr.get(Calendar.MONTH)
		    + "-" + calCurr.get(Calendar.DAY_OF_MONTH);
	    part = new Partition();
	    part.setValueDeclaration(newDate);
	    part.setName("PT" + newDate.replace("-", ""));
	    partitions.put(part.getName(), part);
	}
	    
	
	
	return partitions;
	
    }
    private int daysBetween(java.util.Date date, java.util.Date date2){
	
        return (int)( (date2.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));
    }
    
}
