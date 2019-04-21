package net.tc.stresstool.value;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import net.tc.data.db.Attribute;
import net.tc.data.db.Condition;
import net.tc.data.db.ConditionCollection;
import net.tc.data.db.Table;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.actions.StressActionBase;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;

public class MaxValuesProvider {
	
//	private static ArrayList<Attribute> attribsWhere = new ArrayList();
	private static HashMap<String,Table> tables = new HashMap<String, Table>();
	private static HashMap<String,ArrayList<Attribute>> maxAttributes = new HashMap<String, ArrayList<Attribute>>();
	private static String schemaName = null;
	private static Logger log = null; 
	
	public static ArrayList<Attribute> getAttributeMaxValues(String tableName){
		if(tableName != null && !tableName.equals("")) {
			return maxAttributes.get(tableName);
		}
		
		return null;
	}
	
	public static boolean prepareTables() {
		try {
			log = StressTool.getLogProvider().getLogger(StressTool.getLogProvider().LOG_TOOLS);
		} catch (StressToolConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		log.info(" Inititalizinng Max Values static reference [START]");
		Iterator<String> it = tables.keySet().iterator();
		
		
		while(it.hasNext()) {
			Table table = tables.get(it.next());
			if(schemaName == null)
				schemaName=table.getSchemaName();
			
			ArrayList<String> alreadyLoaded = new ArrayList();
			ArrayList<Attribute> attribs= new ArrayList<Attribute>();
			
			Map conditions = table.getWhereCondition_s().getAllCondition();
			
			Iterator itwhere = conditions.keySet().iterator();
			while(itwhere.hasNext()) {
				String whereCondition = ((Condition)conditions.get(itwhere.next())).getCondition();

				for(Object attrib: (Object[])table.getMetaAttributes().getValuesAsArrayOrderByKey()){
	//				System.out.println("---------- " + ((Attribute)attrib).getName());
					if(whereCondition != null && ((Attribute)attrib) != null &&
							whereCondition.indexOf("#" + ((Attribute)attrib).getName() + "#") > -1){
	//					System.out.println("---------- 2 " + ((Attribute)attrib).getName());
						try {
							Attribute clone = new Attribute();
							BeanUtils.copyProperties(clone,((Attribute)attrib));
							if(((Attribute)attrib).getUpperLimit() > 0)
								clone .setUpperLimit(((Attribute)attrib).getUpperLimit());
							
							if(!alreadyLoaded.contains(((Attribute)clone).getName())) {
								attribs.add(clone) ;
								alreadyLoaded.add(((Attribute)clone).getName());
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					}
				
				}
			}
			maxAttributes.put(table.getName(), attribs);
		}
		
		
		log.info(" Inititalizinng Max Values static reference [END]");
		return false;
		
	}


	public static Connection setAttributeMaxValues(Connection  conn) throws SQLException {
		
		log.info(" REFRESH Max Values static reference [START]");
		if(conn == null 
			|| conn.isClosed()
			|| schemaName == null
			|| maxAttributes == null
			|| maxAttributes.isEmpty()) {
			return conn;
		}
		
		Iterator<String> it = maxAttributes.keySet().iterator();
		
		while(it.hasNext()) {
				
			String tableName = (String)it.next();
			ArrayList<Attribute> maxAttribute =  maxAttributes.get(tableName);
			
			StringBuffer sb = new StringBuffer();
			
			for(Object attrib : (Object[]) (maxAttribute.toArray())){
					if(sb.indexOf(("("+((Attribute)attrib).getName())+")") < 0) {
						if (sb.length() > 0)
							sb.append(",");

						sb.append("MAX("+ ((Attribute)attrib).getName() +") as " +((Attribute)attrib).getName()+" ");
					}
			}	
			String SQL = "Select " + sb.toString() + " FROM "+ schemaName +"." + tableName;
			log.info(" == SQL" + SQL);
			if(sb.length() > 0){
			  try {
					Statement stmt = (Statement) conn.createStatement();
					ResultSet rs = stmt.executeQuery(SQL);
					if(rs != null ){
						while (rs.next()){
							for(Object attrib : (Object[]) (maxAttribute.toArray())){
								maxAttribute.remove(attrib);
								Object value = rs.getObject(((Attribute)attrib).getName());
								if(value instanceof java.math.BigDecimal)
									value = ((BigDecimal)value).longValue();
								((Attribute)attrib).setValue(value);
//								System.out.println(((Attribute)attrib).getName() + " | "+ ((Attribute)attrib).getValue() + "Before");
								maxAttribute.add(((Attribute)attrib));
								log.debug(" ===== Value Name: " + ((Attribute)attrib).getName() + " = " + value);
							}
						}
					}
					rs.close();
					stmt.close();
					
				  } catch (SQLException e) {
						try{					
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							PrintStream ps = new PrintStream(baos);				
							e.printStackTrace(ps);
							String s =new String(baos.toByteArray());
							StringBuffer sb1 = new StringBuffer();
							sb1.append(s);
							sb1.append(SQL);
							StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(sb1.toString());
							System.exit(1)  ;
					}catch(Exception ex){ex.printStackTrace();}
	
	
			  }
			}
			maxAttributes.put(tableName, maxAttribute);
		}
		log.info(" REFRESH Max Values static reference [END]");
		return conn;
		
	}
	
	public static boolean addTable(Table table) {
		if(table != null && table.getMetaAttributes().size() >0){
			tables.put(table.getName(), table);
			return true;
		}
		return false;
		
		
	}
	public static boolean isTablesEmpty() {
		if(tables != null && tables.size()>0 )
			return false;
		
		return true;
		
	}
	
}
