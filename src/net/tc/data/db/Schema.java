package net.tc.data.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;

public class Schema {
	private String name = null; 
	private String defaultCharacterSet = null;
	private String defaultCollation = null;
	private int size = 0;
	private SynchronizedMap tables = null;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDefaultCharacterSet() {
		return defaultCharacterSet;
	}
	public void setDefaultCharacterSet(String defaultCharacterSet) {
		this.defaultCharacterSet = defaultCharacterSet;
	}
	public String getDefaultCollation() {
		return defaultCollation;
	}
	public void setDefaultCollation(String defaultCollation) {
		this.defaultCollation = defaultCollation;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public SynchronizedMap getTables() {
		return tables;
	}
	public void setTables(SynchronizedMap tables) {
		this.tables = tables;
	}
	public Table getTable( String tableName) {
		if(tables.get(tableName) != null)
			return (Table )tables.get(tableName);
		return null;
	}
	public void setTable(Table table) {
		if(table != null && table.getName() != null){
			this.tables.put(table.getName(), table);
		}
	}
	
	/**
	 * 
	 * @param tablesInstances
	 * @return String buffer
	 * To create the whole schemaset we will create all the tables/view/whatever related to that schema
	 * using a String Buffer and passing it to the Write action.
	 * In this way we may have it done managed or unmanaged.
	 *  
	 * That will eventually allow also modification or review.
	 */
    public StringBuffer deploySchema(Map tablesInstances) {
	StringBuffer sbSchema = new StringBuffer();
	StringBuffer sbTables = new StringBuffer();

	sbSchema.append("SET FOREIGN_KEY_CHECKS=0;\n");
//	sbSchema.append("SET SQL_NOTES=0;\n");

	Iterator tablesIt = this.getTables().iterator();
	/*
	 * Define if the table must be replicated or if it single
	 */
	while (tablesIt.hasNext()) {
	    Table table = this.getTable((String) tablesIt.next());
	    if (table != null) {
		int numberOfTables = 0;
		if(table.getParentTable()==null 
			&& table.isMultiple()
			){
		    numberOfTables=(Integer) tablesInstances.get(Table.TABLE_PARENT);  	
		}
		else if(table.getParentTable() != null 
			&& table.isMultiple()){
		    numberOfTables=(Integer) tablesInstances.get(Table.TABLE_CHILD);
		}
		else{
		    numberOfTables = 1;
		}
		
		for (int i = 1; i <= numberOfTables; i++) {
		    table.setInstanceNumber(i);
		    try {
			sbTables.append(table.deploy().append(";\n\n"));
		    } catch (StressToolConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		    // sbTables.append()

		}
	    }
	}
	if(sbTables.length() > 1){
	    sbSchema.append(sbTables + "\n");
	    sbSchema.append("SET FOREIGN_KEY_CHECKS=1;\n");
//	    sbSchema.append("SET SQL_NOTES=1;\n");
//	    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Schema Definition SQL = [\n" + sbSchema.toString() + "\n ]"  );
	    return sbSchema;
	}

	return null;
    }
    public String[] deployDropTable(Map tablesInstances){
	ArrayList<String> tables = new ArrayList();
	
	Iterator tablesIt = this.getTables().iterator();
	/*
	 * Define if the table must be replicated or if it single
	 */
	while (tablesIt.hasNext()) {
	    Table table = this.getTable((String) tablesIt.next());
	    if (table != null) {
		int numberOfTables = 0;
		if(table.getParentTable()==null 
			&& table.isMultiple()
			){
		    numberOfTables=(Integer) tablesInstances.get(Table.TABLE_PARENT);  	
		}
		else if(table.getParentTable() != null 
			&& table.isMultiple()){
		    numberOfTables=(Integer) tablesInstances.get(Table.TABLE_CHILD);
		}
		else{
		    numberOfTables = 0;
		}
		
		if(numberOfTables > 0){
        		for (int i = 1; i <= numberOfTables; i++) {
        		    tables.add(table.getName() + i);
        		}
		}
		else{
		    	tables.add(table.getName());
		}
	    }
	}
	if(tables != null  & tables.size() > 0 )
	    return (String[])  tables.toArray(new String[tables.size()]);
	
	return null;
    }

}
