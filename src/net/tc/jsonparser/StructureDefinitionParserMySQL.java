package net.tc.jsonparser;

import java.io.FileReader;
import java.util.ArrayList;

import net.tc.data.db.*;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class StructureDefinitionParserMySQL implements
		StructureDefinitionParser {

	/* (non-Javadoc)
	 * @see net.tc.jsonparser.StructureDefinitionParser#parseSchema(org.json.simple.parser.JSONParser, java.io.FileReader)
	 */
	@Override
	public Schema parseSchema(JSONParser parser,FileReader fr) {
		// TODO Auto-generated method stub
		
		try{

			JSONArray arrayDefinition = null;
			JSONObject objectDefinition = null;
			Schema schema = null;
			
			Object jsonParserObject = parser.parse(fr);
			
			if(jsonParserObject instanceof JSONArray){
				arrayDefinition = (JSONArray) jsonParserObject;
			}
			else{
				objectDefinition = (JSONObject) jsonParserObject;	
			}
			

			/***
			 * Parse the Json definition top down from schema to attribute
			 */
			if(objectDefinition != null){
				String dbName = (String)objectDefinition.get("database");
				if(dbName == null 
						|| dbName.equals(""))
				{
					StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn("Table structure definition from Json file is has invalid Schema Name for Insert Class");
				}

				/***
				 * Parse Schema as object
				 */

				schema = new Schema();
				schema.setName(dbName);
				schema.setDefaultCharacterSet((String)objectDefinition.get("defaultCharacterSet")!=null?(String)objectDefinition.get("defaultCharacterSet"):null);
				schema.setTables(new SynchronizedMap(0));
				StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Parsing Json definition for schema = " + dbName);
				
				
				
				/***
				 * Parse tables as object
				 */
				JSONObject tables = (JSONObject)objectDefinition.get("tables");
				
				/***
				 * Parse single table from array 
				 */
				JSONArray arTable = (JSONArray)tables.get("table");
				for(Object o: arTable){
					JSONObject oTable = (JSONObject) o;
					Table table = new Table();
					table.setName((String)oTable.get("tablename"));

					StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Parsing Json definition for table = " + dbName+"." + table.getName());

					table.setSchemaName((String)oTable.get("database"));
					if(oTable.get("autoincrementvalue")!=null){
						((Long)oTable.get("autoincrementvalue")).longValue();
						table.setAutoincrement(true);
					}
						
					table.setAutoincrementValue(oTable.get("autoincrementvalue")!=null?((Long)oTable.get("autoincrementvalue")).longValue():0);
					table.setStorageEngine((String)oTable.get("engine")!=null?(String)oTable.get("engine"):null);
					table.setDefaultCharacterSet((String)oTable.get("defaultcharset")!=null?(String)oTable.get("defaultcharset"):null);
					if(oTable.get("haspartition") != null)
						table.setHasPartition(((Boolean)oTable.get("haspartition")).booleanValue());

					/***
					 * Parse Attributes 
					 */
					table.setMetaAttributes(new SynchronizedMap(0));
					JSONObject attributes = (JSONObject)oTable.get("attributes");
					JSONArray arAttributes = (JSONArray)attributes.get("attribute"); <--------------vv-------------------------  IM HERE>
					for(Object oa: arAttributes){
						JSONObject oAttribute = (JSONObject) oa;
						Attribute attribute = new Attribute();
						attribute.setName((String)oAttribute.get("name"));

						StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Parsing Json definition for attribute = " 
						+ dbName +"." 
						+ table.getName() + "." 
						+ attribute.getName());
						
						attribute.setDataType((String)oAttribute.get("datatype"));
						attribute.setDataDimension((String)oAttribute.get("datadimension")!= null?Integer.parseInt((String)oAttribute.get("datadimension")):0);
						attribute.setAutoIncrement((String)oAttribute.get("autoincrement")!= null?true:false);
						if(oAttribute.get("default") == null 
								|| ((String)oAttribute.get("default")).equals("none")){
								attribute.setDefaultValue((String)oAttribute.get("default"));
						}
						else{
							attribute.setDefaultValue(null);
						}
						attribute.setOnUpdate((String)oAttribute.get("onUpdate")!= null?(String)oAttribute.get("onUpdate"):null);
						if(oAttribute.get("null") != null
								&& ((String)oAttribute.get("null")).equals("false")){
							attribute.setNull(true);
						}
						else
							attribute.setNull(false);
						
						table.setMetaAttribute(attribute);
					}
					
					

					/***
					 * Parse Indexes 
					 */
					if(tables.get("keys") != null){
						
						JSONObject keys = (JSONObject)objectDefinition.get("keys");
						JSONArray arKeys = (JSONArray)keys.get("key");
						for(Object ok: arKeys){
							JSONObject oKey = (JSONObject) ok;
							IndexMeta index = new IndexMeta();
							index.setName((String)oKey.get("name")!=null?(String)oKey.get("name"):null);
							if(oKey.get("unique") != null
									&& ((String)oKey.get("unique")).equals("true")){
								index.setUnique(true);
							}
							else
								index.setUnique(false);
							index.setColumnsDefinition(new ArrayList());
							JSONArray arCollDef = (JSONArray)keys.get("attributes");
							for(Object oColDef: arCollDef){
								 
							}
						}
					}
					
					
					schema.setTable(table);
				}
				
			}
//			else{
//				  for (Object o : a)
//				  {
//				    JSONObject person = (JSONObject) o;
//		
//				    String name = (String) person.get("name");
//				    System.out.println(name);
//		
//				    String city = (String) person.get("city");
//				    System.out.println(city);
//		
//				    String job = (String) person.get("job");
//				    System.out.println(job);
//		
//				    JSONArray cars = (JSONArray) person.get("cars");
//		
//				    for (Object c : cars)
//				    {
//				      System.out.println(c+"");
//				    }
//				   
//				  }
//			}
		}
		catch(Exception e){
			e.printStackTrace();
		    ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
		    try{throw new StressToolConfigurationException(e);}catch(Throwable th){th.printStackTrace();}
		}
		
		
		return null;
	}

	@Override
	public Table parseTable(JSONParser parser,FileReader fr) {
		// TODO Auto-generated method stub
		return null;
	}

}
