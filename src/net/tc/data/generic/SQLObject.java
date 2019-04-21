package net.tc.data.generic;

import java.util.ArrayList;

import net.tc.data.db.*;
import net.tc.stresstool.PerformanceEvaluator;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;


public class SQLObject {
    ArrayList<String> SQLCommands = new ArrayList();
    
    int SQLCommandType = 0;
    boolean isBatched = false;
    boolean isPreparedStatment = false;
//    int lazyExecCount = 0;
    String sqlLocalTemplate = null;
    boolean resetLazy = false;
    int batchLoops = 1;
    ArrayList whereAttrib = null; 
    
    ArrayList sourceTables = new ArrayList();
    /**
     * @return the sQLCommands
     */
    public ArrayList getSQLCommands() {
        return SQLCommands;
    }

    /**
     * @param sQLCommands the sQLCommands to set
     */
    public void setSQLSingleCommand(String SQLCommand) {
        SQLCommands.add(SQLCommand);
    }
    
    /**
     * @return the sQLCOmmandType
     */
    public int getSQLCommandType() {
        return SQLCommandType;
    }
    /**
     * @param sQLCOmmandType the sQLCOmmandType to set
     */
    public void setSQLCommandType(int sQLCOmmandType) {
        SQLCommandType = sQLCOmmandType;
    }
    /**
     * @return the isBatched
     */
    public boolean isBatched() {
        return isBatched;
    }
    /**
     * @param isBatched the isBatched to set
     */
    public void setBatched(boolean isBatched) {
        this.isBatched = isBatched;
    }
    /**
     * @return the isPreparedStatment
     */
    public boolean isPreparedStatment() {
        return isPreparedStatment;
    }
    /**
     * @param isPreparedStatment the isPreparedStatment to set
     */
    public void setPreparedStatment(boolean isPreparedStatment) {
        this.isPreparedStatment = isPreparedStatment;
    }
    /**
     * @return the sourceTables
     */
    public ArrayList getSourceTables() {
        return sourceTables;
    }
    /**
     * @param sourceTables the sourceTables to set
     */
    public void setSourceTables(ArrayList sourceTables) {
        this.sourceTables = sourceTables;
    }
    
    public void addSourceTables(Table table) {
        this.sourceTables.add(table);
    }
//	public synchronized int getLazyExecCount() {
//		return lazyExecCount;
//	}
//	public synchronized void setLazyExecCount(int lazyExecCount) {
//		this.lazyExecCount = lazyExecCount;
//	}
	public  String getSqlLocalTemplate() {
		return sqlLocalTemplate;
	}
	public void setSqlLocalTemplate(String sqlLocalTemplate) {
		this.sqlLocalTemplate = sqlLocalTemplate;
	}
	/**
	 * this method is the one that will call the value provider to 
	 * fill the values
	 * looping if batch and passing the datatype 
	 * 
	 * If the attribute is lazy and the lazy counter is lower than the limit value the 
	 * attribs with lazy will have the same values than before
	 * 
	 * @return
	 */
  public String getValues() {
	  long performanceTimeStart = 0;
	  try {if (StressTool.getLogProvider().getLogger(LogProvider.LOG_PACTIONS).isInfoEnabled()) {performanceTimeStart=System.nanoTime();}} catch (StressToolConfigurationException e1) {e1.printStackTrace();}

	  SQLCommands = new ArrayList();
	  boolean filling = false;
	  for (Object table : this.getSourceTables()) {

	  SynchronizedMap Attribs = ((Table) table).getMetaAttributes();
	  try {
		StressTool
		    .getLogProvider()
		    .getLogger(LogProvider.LOG_ACTIONS)
		    .debug(
		        "========================== Processing Table " + ((Table) table).getName() + " ================ [Start]");
	  } catch (StressToolConfigurationException e) {}
	  if(((Table)table).getInsertAttributes() == null)
		  return "";
	  
	  String[] insertAttrib = ((Table)table).getInsertAttributes().split(","); 

	  /*
	   * loops cross batch loops
	   */
	  StringBuffer sqlValues = new StringBuffer();
	  
	  
	  for (int iBatch = 0; iBatch < this.batchLoops; iBatch++) {
		try{
    		StringBuffer singleSql = new StringBuffer();
    		if (sqlValues.length() > 1)
    		  sqlValues.append(",");
    		
    		singleSql.append("(");
    		Object[] locAttribs = Attribs.getValuesAsArrayOrderByKey();
    		for(int iInsert = 0 ; iInsert < insertAttrib.length;iInsert++ ){
	    		for (Object attrib : locAttribs) {
	    			if(((Attribute) attrib) != null 
	    					&&((Attribute) attrib).getName().equals(insertAttrib[iInsert])){
	    			
						if (singleSql.length() > 1)
							singleSql.append(", ");
		
		//			  System.out.println("=========== reset "+ this.isResetLazy()  +" Attrib Lazy: " +((Attribute) attrib).isLazy()  );
		    		  
					  if (this.resetLazy || !((Attribute) attrib).isLazy()) {
		    			  
		//    			  if(!this.resetLazy && !((Attribute) attrib).isLazy())
		//    				  System.out.println("===========1 Attrib " + ((Attribute) attrib).getName() +" V:  " + ((Attribute) attrib).getValue() );
		    			  
		    			  if(((Attribute) attrib).getSpecialFunction() == null
		    					  && !((Attribute) attrib).isAutoIncrement()
		    					  ){
		    				if(((Attribute) attrib).isLazy()){
		    				  ((Attribute) attrib).setValue(StressTool.getValueProvider().provideValue(
		    						  ((Attribute) attrib).getDataType(), new Long(((Attribute) attrib).getUpperLimit())));
		    				}
		    				else{
		    				  ((Attribute) attrib).setValue(StressTool.getValueProvider().provideValue(
								  ((Attribute) attrib).getDataType(), new Long(((Attribute) attrib).getUpperLimit())));
		    				  
		    				}
		    			  }
		    			  else{
		
		    				  if(((Attribute)attrib).isAutoIncrement()){
//		    					  ((Attribute) attrib).setValue("NULL");
		    				  }
		    				  else{
		    					  ((Attribute) attrib).setValue(((Attribute) attrib).getSpecialFunction());
		    				  }
		    			  }
		
		//    			  filling = this.isResetLazy()?true:false;
		
		    		  }
		//			  if(!this.resetLazy && !((Attribute) attrib).isLazy())
		//				  System.out.println("===========2 Attrib " + ((Attribute) attrib).getName() +" V:  " + ((Attribute) attrib).getValue() );
		
		    		  if(((Attribute) attrib).getFormattingFunction() !=null 
		    				  && !((Attribute) attrib).getFormattingFunction().equals(""))
		    		  {
		    			 String formattedValue = ((Attribute) attrib).getFormattingFunction().replaceAll("'#VALUE#'", (String)(((Attribute) attrib).getValue()));
		    			 ((Attribute) attrib).setValue(formattedValue);
		    		  }
		    		  singleSql.append(((Attribute) attrib).getValue());
		
		//    		  		  try {StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(
		//    		  				  (filling ? "" : "NOT")
		//    		  			  			+ "Filling Attribute "
		//    		  			            + ((Attribute) attrib).getName()
		//    		  			            + " DataType: "
		//    		  			            + DataType.getDataTypeStringByIdentifier(((Attribute) attrib).getDataType().getDataTypeId()) 
		//    		  			            + " Value : " + ((Attribute)attrib).getValue()
		//    		  			            + " Lazy = " + ((Attribute) attrib).isLazy());
		//    		  		  } catch (StressToolConfigurationException e) {e.printStackTrace();}
		
		    		  break;
		
		    		}
	    		}
	    	}
    		
    		singleSql.append(")");
    		sqlValues.append(singleSql);
    		singleSql.delete(0, singleSql.length());
		}
		catch(Throwable th){th.printStackTrace();}
	  }
	  
	  try {
		StressTool
		    .getLogProvider()
		    .getLogger(LogProvider.LOG_ACTIONS)
		    .debug(
		        "========================== Processing Table " + ((Table) table).getName() + " ================ [End]");
	  } catch (StressToolConfigurationException e) {e.printStackTrace(); }
	  
	  getSQLCommands().add(this.getSqlLocalTemplate().replace("(#VALUES#)", sqlValues.toString()));
	  
	  this.setResetLazy(this.resetLazy?false:isResetLazy());
	  
	  executionPerformance(performanceTimeStart," VALUE GENERATOR ");
	  
	  return sqlValues.toString();
	}
	return null;
  }
  
//  public String getValuesForDML(Table table,String SQLCommand) {
//	 
//	  boolean filling = false;
//	try {
//		StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS)
//		.debug("========================== Processing Table " + ((Table) table).getName()
//		+ " ================ [Start]");	} catch (StressToolConfigurationException e) {}
//
//		/*
//		 * loops cross batch loops
//		 */
//		StringBuffer sqlValues = new StringBuffer();
//		try {
//				ArrayList<Attribute> attributes = new ArrayList();
//				((Table) table).parseAttributeWhere(((Table) table).getWhereCondition(this.getSQLCommandType()),
//						attributes);
//				for (Object attrib : (Object[]) (attributes.toArray())) {
//					if (sqlValues.length() > 1)
//						sqlValues.append(", ");
//	
//					if (((Attribute) attrib).getSpecialFunction() == null && !((Attribute) attrib).isAutoIncrement()) {
//
//						((Attribute) attrib).setValue(StressTool.getValueProvider().provideValue(
//								((Attribute) attrib).getDataType(), new Long(((Attribute) attrib).getUpperLimit()))
//						);
//					} else {
//	
//						if (((Attribute) attrib).isAutoIncrement()) {
//							((Attribute) attrib).setValue("NULL");
//						} else {
//							((Attribute) attrib).setValue(((Attribute) attrib).getSpecialFunction());
//						}
//					}
//	
//					sqlValues.append(((Attribute) attrib).getName() + "=" + ((Attribute) attrib).getValue());
//				}
//	
//			} catch (Throwable th) {
//				th.printStackTrace();
//			}
//			// }
//	
//			try {
//				StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS)
//						.debug("========================== Processing Table " + ((Table) table).getName()
//								+ " ================ [End]");
//			} catch (StressToolConfigurationException e) {
//				e.printStackTrace();
//			}
//	
//			SQLCommand = SQLCommand.replace("#ATTRIB_VALUE#", sqlValues.toString());
//	
//			this.setResetLazy(this.resetLazy ? false : isResetLazy());
//	
//			// return sqlValues.toString();
//		
//		return SQLCommand;
//  }
  
  
	public  boolean isResetLazy() {
		return this.resetLazy;
	}
	public  void setResetLazy(boolean resetLazy) {
		this.resetLazy = resetLazy;
//		this.getValues();
//		this.setLazyExecCount(0);
	}
	public  int getBatchLoops() {
		return batchLoops;
	}
	public void setBatchLoops(int batchLoops) {
		this.batchLoops = batchLoops;
	}
	public void setSQLCommands(ArrayList<String> sQLCommands) {
		SQLCommands = sQLCommands;
	}
    
	public void setSingleSQLCommands(String sQLCommand) {
		SQLCommands.add(sQLCommand);
	}
	
	public ArrayList getWhereValues(){
	  
	  
	  
	  return null;
	}
	public Attribute getWhereValueForAttribute(Attribute attribute, Table table){
	  
	  return null;
	}

	private void executionPerformance(long performanceTimeStart, String text) {
		/*Performance evaluation section [header] start*/
		try {
		    if (StressTool.getLogProvider()
			    .getLogger(LogProvider.LOG_PACTIONS)
			    .isInfoEnabled()) {
			
			StressTool
				.getLogProvider()
				.getLogger(LogProvider.LOG_PACTIONS)
				.info(StressTool.getLogProvider().LOG_EXEC_TIME
					+ " "+ text + " exec perf:"
					+ PerformanceEvaluator
						.getTimeEvaluationNs(performanceTimeStart));
		    }
		} catch (Throwable th) {
		}
		/*Performance evaluation section [header] END*/
	}

}
