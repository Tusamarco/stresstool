package net.tc.jsonparser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import net.tc.data.db.*;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class StructureDefinitionParserOracle implements
		StructureDefinitionParser {

	/* (non-Javadoc)
	 * @see net.tc.jsonparser.StructureDefinitionParser#parseSchema(org.json.simple.parser.JSONParser, java.io.FileReader)
	 */
	@Override
	public Schema parseSchema(JSONParser parser,FileReader fr,Map tableInstances) {
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

				schema = new SchemaOracle();
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
				    	ArrayList attribsWIthIndex = new ArrayList(); 
				    	
					JSONObject oTable = (JSONObject) o;
					Table table = new TableOracle();
					table.setName((String)oTable.get("tablename"));

					table.setSchemaName((String)oTable.get("database"));
					if(oTable.get("autoincrementvalue")!=null){
						((Long)oTable.get("autoincrementvalue")).longValue();
						table.setAutoincrement(true);
					}
						
					table.setAutoincrementValue(oTable.get("autoincrementvalue")!=null?((Long)oTable.get("autoincrementvalue")).longValue():0);
					table.setStorageEngine((String)oTable.get("engine")!=null?(String)oTable.get("engine"):null);
					table.setDefaultCharacterSet((String)oTable.get("defaultcharset")!=null?(String)oTable.get("defaultcharset"):null);
					table.setDefaultCollation((String)oTable.get("defaultcollation")!=null?(String)oTable.get("defaultcollation"):null);
					table.setParentTable((String)oTable.get("parent")!=null?(String)oTable.get("parent"):null);
					table.setHasPartition((Boolean)(oTable.get("haspartition")!=null?oTable.get("haspartition"):false));
					table.setMultiple((Boolean)(oTable.get("multiple")!=null?oTable.get("multiple"):false));
					table.setRowFormat(oTable.get("rowformat")!=null?(String)oTable.get("rowformat"):"");
					table.setDataDirectory(oTable.get("datadir")!=null?(String)oTable.get("datadir"):"");
					table.setTableSpace(oTable.get("tablespace")!=null?(String)oTable.get("tablespace"):"");
//					table.setWhereConditionS(oTable.get("wherecondition_s")!=null?(String)oTable.get("wherecondition_s"):"");
//					table.setWhereConditionU(oTable.get("wherecondition_u")!=null?(String)oTable.get("wherecondition_u"):"");
//					table.setWhereConditionD(oTable.get("wherecondition_d")!=null?(String)oTable.get("wherecondition_d"):"");
//					table.setSelectCondition(oTable.get("selectcondition")!=null?(String)oTable.get("selectcondition"):"");
					table.setUpdateSetAttributes(oTable.get("updatesetattributes")!=null?(String)oTable.get("updatesetattributes"):""); 
					table.setNumberOfTables(oTable.get("numberOfTables")!=null?(long)oTable.get("numberOfTables"):1);
					
					table.setInitializeValues((Boolean)(oTable.get("initializeValues")!=null?oTable.get("initializeValues"):false));
					table.setReadOnly((Boolean)(oTable.get("readOnly")!=null?oTable.get("readOnly"):false));
					table.setWriteFactor(oTable.get("writeFactor")!=null?(Long)oTable.get("writeFactor"):100);
					table.setReadFactor(oTable.get("readFactor")!=null?(Long)oTable.get("readFactor"):100);

					
					/*
					 * Parse select conditions
					 * 
					 */
					{
						ConditionCollection condCol = new ConditionCollection();
						JSONObject SelectCondS = (JSONObject)oTable.get("selectcondition");
						int id = 1;
	
						if( SelectCondS != null){
							JSONArray arConditions = (JSONArray) SelectCondS.get("condition");
							/*
							 * Loading attributes from Json file
							 */
							for(Object oa: arConditions){
								JSONObject aCondition = (JSONObject) oa;
								Condition condition = new Condition();
								condition.setWeight((Long) aCondition.get("weight"));
								condition.setCondition((String) aCondition.get("condition_string"));
								condition.setJoinoption((String) aCondition.get("joinoption"));
								condition.setType(Condition.SELECT_CONDITION);
								condCol.setCondition(id++, condition);
							}
							table.setSelectCondition_S(condCol);
							
						}
						
					}
					
					/*
					 * Parse conditions
					 * 
					 */
					{
						String[] condTypeS = {"s", "u", "d"};
						for(String condType : condTypeS ){
							int id = 1;
							ConditionCollection condCol = new ConditionCollection();
							JSONObject whereCondS = (JSONObject)oTable.get("wherecondition_"+condType);
	
							if(whereCondS != null){
								JSONArray arConditions = (JSONArray)whereCondS.get("condition");
								/*
								 * Loading attributes from Json file
								 */
								for(Object oa: arConditions){
									JSONObject aCondition = (JSONObject) oa;
									Condition condition = new Condition();
									condition.setWeight((Long) aCondition.get("weight"));
									condition.setCondition((String) aCondition.get("condition_string"));
									condition.setType(Condition.WHERE_CONDITION);
									condCol.setCondition(id++, condition);
								}
								switch(condType){
									case "s": table.setWhereConditionS(condCol);break;
									case "u": table.setWhereConditionU(condCol);break;
									case "d": table.setWhereConditionD(condCol);break;
								
								}
							}	
						}
					}
					
					/*
					 * browsing for PK
					 */
					if(oTable.get("primarykey") != null){
					    PrimaryKey primaryKey = new PrimaryKey();
					    SynchronizedMap indexMap = new SynchronizedMap(0);
					    JSONArray arPK = (JSONArray)oTable.get("primarykey");
					    
					    int seq = 0;
					    for(Object oPK: arPK){
						    Index idx = new Index();
						    idx.setAllowNull(false);
						    idx.setSeqInIndex(seq++);
						    idx.setName((String)oPK);
						    idx.setTableName(table.getName());
						    indexMap.put(idx.getName(), idx);
						    attribsWIthIndex.add(idx.getName());
						}
					    primaryKey.setIndexes(indexMap);
					    table.setPrimaryKey(primaryKey);
					}
					StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Parsing Json definition for table = " + dbName+"." + table.getName()
					+ " Primary Key = " + Arrays.toString(table.getPrimaryKey().getIndexes().getKeyasOrderedStringArray()));

					
					

					/***
					 * Parse Indexes 
					 */
					
					
					if(oTable.get("keys") != null){
						
						JSONObject keys = (JSONObject)oTable.get("keys");
						JSONArray arKeys = (JSONArray)keys.get("key");
						for(Object ok: arKeys){
							JSONObject oKey = (JSONObject) ok;
							Index index = new Index();
							index.setName((String)oKey.get("name")!=null?(String)oKey.get("name"):null);
							if(oKey.get("unique") != null){
								index.setUnique((Boolean)oKey.get("unique"));
							}
							else
								index.setUnique(false);
							index.setColumnsDefinition(new ArrayList());
							JSONArray arCollDef = (JSONArray)oKey.get("attributes");
							ArrayList arColKey = new ArrayList();
							for(Object oColDef: arCollDef){
							    arColKey.add(oColDef);
							    attribsWIthIndex.add((String)oColDef);
							}
							StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Parsing Json definition for Key = " 
								+ dbName +"." 
								+ table.getName() + "." 
								+ index.getName() + " " 
								+ " [ " + arColKey.toString() + " ]");
							index.setColumnsDefinition(arColKey);
							table.setIndex(index);
						}
					}

					/***
					 * Parse Attributes 
					 */
					table.setMetaAttributes(new SynchronizedMap(0));
					JSONObject attributes = (JSONObject)oTable.get("attributes");
					JSONArray arAttributes = (JSONArray)attributes.get("attribute");
					/*
					 * Loading attributes from Json file
					 */
					for(Object oa: arAttributes){
						JSONObject oAttribute = (JSONObject) oa;
						Attribute attribute = new Attribute();
						attribute.setName((String)oAttribute.get("name"));

						StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Parsing Json definition for attribute = " 
						+ dbName +"." 
						+ table.getName() + "." 
						+ attribute.getName());
						if(attribsWIthIndex.contains(attribute.getName()))
						    attribute.setHasIndex(true);
						attribute.setDataType(new DataType(DataType.getDataTypeIdentifierByString((String)oAttribute.get("datatype"),ConnectionInformation.ORACLE)));
						attribute.setDataDimension((String)oAttribute.get("datadimension")!= null?Integer.parseInt((String)oAttribute.get("datadimension")):0);
						attribute.setAutoIncrement(oAttribute.get("autoincrement")!= null?true:false);
						attribute.setSpecialFunction(oAttribute.get("specialFunction")!= null?(String)oAttribute.get("specialFunction"):null);
						attribute.setUpperLimit(oAttribute.get("upperlimit")!= null?Long.parseLong((String)oAttribute.get("upperlimit")):0);
						attribute.setFormattingFunction(oAttribute.get("formattingFunction")!= null?(String)oAttribute.get("formattingFunction"):null);
												
						if(attribute.getDataType().getDataTypeCategory() == DataType.STRING_CATEGORY 
							&& attribute.getUpperLimit() < 1){
						  attribute.setUpperLimit(new Long(attribute.getDataDimension()));
						}
						
						if(oAttribute.get("default") != null 
								&& !((String)oAttribute.get("default")).equals("")){
								attribute.setDefaultValue((String)oAttribute.get("default"));
						}
						else{
							attribute.setDefaultValue(null);
						}
						attribute.setOnUpdate((String)oAttribute.get("onUpdate")!= null?(String)oAttribute.get("onUpdate"):null);
						if(oAttribute.get("null") != null
								&& (oAttribute.get("null")).equals("false")){
							attribute.setNull(true);
						}
						else
							attribute.setNull(false);
						
						if(oAttribute.get("lazy") != null
								&& (oAttribute.get("lazy")).equals("0")){
							attribute.setLazy(false);
						}

						table.setMetaAttribute(attribute);
					}

					
					
					
					
					/**
					 * Parse the Partitioning
					 * 
					 */
					if(table.isHasPartition()){
					    if(oTable.get("partitionDefinition") != null){
						JSONObject oPartDef = (JSONObject)oTable.get("partitionDefinition");
						PartitionDefinition partDef = new PartitionDefinitionOracle();
						partDef.setTableName(table.getName());
						partDef.setPartitionType((String)oPartDef.get("partitionBy")!=null?(String)oPartDef.get("partitionBy"):null);

						JSONArray arCollDef = (JSONArray)oPartDef.get("attributes");
						ArrayList arColPart = new ArrayList();
						for(Object oColDef: arCollDef){
						    arColPart.add(oColDef);
						}
						partDef.setAttributes(arColPart);
						partDef.setFunction((String)oPartDef.get("function")!=null?(String)oPartDef.get("function"):null);
						partDef.setInterval((String)oPartDef.get("interval")!=null?(String)oPartDef.get("interval"):null);
						
						if(oPartDef.get("starttime")!=null && !oPartDef.get("starttime").equals("")){
						partDef.setStartDate((String)oPartDef.get("starttime"));
						}
						else{partDef.setStartDate(null);
						}
						if(oPartDef.get("endtime")!=null && !oPartDef.get("endtime").equals("")){
						    partDef.setEndDate((String)(String)oPartDef.get("endtime"));
						}
						else{
						    partDef.setEndDate(null);
						}

						partDef.setPartitionsSize((String) ((String)oPartDef.get("partitions")!=null?(String)oPartDef.get("partitions"):0));

						StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("Parsing Json definition for Partition = " 
						+ dbName +"." 
						+ table.getName() + "." 
						+ partDef.getPartitionType() + " " + partDef.getFunction() );
						
						
						if(partDef.getPartitionType().equals(PartitionDefinition.PARTITION_TYPE_RANGE)
							&& (partDef.getStartDate() == null || partDef.getEndDate() == null ) ){
						    	SynchronizedMap partitions = new SynchronizedMap();
							JSONObject ranges = (JSONObject)oPartDef.get("ranges");
							if(ranges.get("range") != null){
        							JSONArray arRange = (JSONArray)ranges.get("range");
        							for(Object oR: arRange){
        							    JSONObject oRange = (JSONObject) oR;
        							    Partition partition = new Partition();
        							    partition.setName((String)oRange.get("name")!=null?(String)oRange.get("name"):null);
        							    partition.setValueDeclaration((String)oRange.get("value")!=null?(String)oRange.get("value"):null);
        							    partitions.put(partition.getName(), partition);
        							}
        							partDef.setPartitions(partitions);
        							
							}

						}
						if(partDef.getPartitionType().equals(PartitionDefinition.PARTITION_TYPE_LIST)){
							JSONObject ranges = (JSONObject)oPartDef.get("lists");
							if(ranges.get("list") != null){
							    	SynchronizedMap partitions = new SynchronizedMap();
							    	JSONArray arList = (JSONArray)ranges.get("list");
        							for(Object oR: arList){
        							    JSONObject oList = (JSONObject) oR;
        							    Partition partition = new Partition();
        							    partition.setName((String)oList.get("name")!=null?(String)oList.get("name"):null);
        							    partition.setValueDeclaration((String)oList.get("value")!=null?(String)oList.get("value"):null);
        							    partitions.put(partition.getName(), partition);
        							}
        							partDef.setPartitions(partitions);
							}

						}
						table.setPartitionDefinition(partDef);
					    }
					    
					}
					
					StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).debug("--------------------------------------");
					schema.setTable(table);
				}
				return schema.explodeTables(tableInstances);
				
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
