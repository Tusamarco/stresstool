package net.tc.data.db;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;

public class SchemaPostgres extends Schema {
	public SchemaPostgres() {
		super();
		
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
	@Override
    public StringBuffer deploySchema(Map tablesInstances) {
	StringBuffer sbSchema = new StringBuffer();
	StringBuffer sbTables = new StringBuffer();

	//FIXME foreign keys in Postgres cannot be disable, but triggers manage them yes. Task must be done at table level and not here
 	
//	sbSchema.append("SET FOREIGN_KEY_CHECKS=0;\n");
//	sbSchema.append("SET SQL_NOTES=0;\n");

	Iterator tablesIt = this.getTables().iterator();
	/*
	 * Define if the table must be replicated or if it single
	 */
	while (tablesIt.hasNext()) {
	    Table table = this.getTable((String) tablesIt.next());
	    try {
		sbTables.append(table.deploy().append(";\n\n"));
	    } catch (StressToolConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    
	}
	if(sbTables.length() > 0){
	    sbSchema.append(sbTables + "\n");
	    //FIXME here instead this the list of tables with ALTER TABLE b ENABLE TRIGGER ALL;
//	    sbSchema.append("SET FOREIGN_KEY_CHECKS=1;\n");
//	    sbSchema.append("SET SQL_NOTES=1;\n");
//	    StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Schema Definition SQL = [\n" + sbSchema.toString() + "\n ]"  );
	    return sbSchema;
	}

	return null;
    }
    
    /**
     * Explode the tables using the number defined in the configuration
     * 
     * @return
     */
    public Schema explodeTables(Map tableInstances){
	/*
	 * set first default lazy based on the presence of the index 
	 * If an index exist then lazy is false
	 */
	this.setLazyFields();
	SynchronizedMap newTables = new SynchronizedMap();
	Iterator tablesIt = this.getTables().iterator();
	while (tablesIt.hasNext()) {
	    Table table = this.getTable((String) tablesIt.next());
	    if (table != null) {
		Long numberOfTables = table.getNumberOfTables();
//		if(table.getParentTable()==null 
//			&& table.isMultiple()
//			){
//		    numberOfTables=(Integer) tableInstances.get(Table.TABLE_PARENT);  	
//		}
//		else if(table.getParentTable() != null 
//			&& table.isMultiple()){
//		    numberOfTables=(Integer) tableInstances.get(Table.TABLE_CHILD);
//		}
//		else{
//		    numberOfTables = 1;
//		}
		
		for (int i = 1; i <= numberOfTables; i++) {
		    if(numberOfTables > 1)
			table.setInstanceNumber(i);
		    
		    Table newTable = null;
			newTable = new TablePostgres();
//		    if(table instanceof TableMySQL){
//		    	newTable =  new TableMySQL();
//		    }
//		    else{
//		    	newTable = new TablePostgres();
//		    }
		    
		    
		    try {
			BeanUtils.copyProperties(newTable, table);
			newTable.setMetaAttributes(table.getMetaAttributes());
			
			if(numberOfTables > 1){
			    newTable.setName(newTable.getName() + newTable.getInstanceNumber());
			    newTables.put((newTable.getName()), newTable);
			}
			else{
			    newTables.put(table.getName(), newTable);
			}
		    } catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    } catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    }

		}

	    }
	}
	this.setTables(null);
	this.setTables(newTables);

	
	return this;
    }
    

}
