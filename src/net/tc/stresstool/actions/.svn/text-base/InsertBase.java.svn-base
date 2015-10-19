package net.tc.stresstool.actions;

import java.io.FileReader;

import net.tc.data.db.Schema;
import net.tc.jsonparser.*;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.config.Configurator;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class InsertBase extends StressActionBase implements WriteAction,
	CreateAction {
	
    private int numberOfprimaryTables=30;
    private int numberOfSecondaryTables=20;
    
    private boolean useAutoIncrement=true;
    
	private int sleepWrite=0;
    private boolean lazyCreation=true;
    private int lazyInterval=5000;
    private int batchSize = 0; 
    private String jsonFile = "";
    
    public String getJsonFile() {
		return jsonFile;
	}
	public void setJsonFile(String jsonFile) {
		this.jsonFile = jsonFile;
	}
	@Override
	public void LoadData() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void TruncateTables() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void CreateActionTablePrimary() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void getActionTablePrimary() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setActionTablePrimary() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void getActionTableSecondary() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setActionTableSecondary() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void CreateSchema() {
		try{

			if(this.getJsonFile() == null){
				StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn("Table structure definition from Json file is null for Insert Class");
				StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).warn("check parameter : jsonfile");
				return;
			}

			JSONParser parser = new JSONParser();
			StructureDefinitionParser strParser = new StructureDefinitionParserMySQL();
			FileReader fr = new FileReader(this.getJsonFile());
			Schema schema = strParser.parseSchema(parser, fr);
			
				
		}
		catch(Exception e){
			e.printStackTrace();
		    ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
		    try{throw new StressToolConfigurationException(e);}catch(Throwable th){th.printStackTrace();}
		}
		
	}
	@Override
	public void DropSchema() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
		
	}
	@Override
	public int getBatchSize() {
		return this.batchSize;
	}
	@Override
	public void setUseAutoIncrement(boolean useAutoIncrement) {
		this.useAutoIncrement = useAutoIncrement;
		
	}
	@Override
	public boolean isUseAutoIncrement() {
		return useAutoIncrement;
	}
//	public boolean getuseAutoincrement() {
//		return useAutoIncrement;
//	}

	@Override
	public ActionTable getInsertValueProvider() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setLazyness(boolean lazy) {
		this.lazyCreation = lazy;
		
	}
	@Override
	public boolean islazyCreation() {
		return this.lazyCreation;
	}
	@Override
	public void setLazyInterval(int interval) {
		this.lazyInterval = interval;
		
	}
	@Override
	public int getLazyInterval() {
		return this.lazyInterval;
	}
	@Override
	public void setNumberOfprimaryTables(int tableNumbers) {
		this.numberOfprimaryTables = tableNumbers;
		
	}
	@Override
	public int getNumberOfprimaryTables() {
		return this.numberOfprimaryTables;
	}
	@Override
	public void setNumberOfSecondaryTables(int tableNumbers) {
		this.numberOfSecondaryTables = tableNumbers;
		
	}
	@Override
	public int getNumberOfSecondaryTables() {
		return this.numberOfSecondaryTables;
	} 
	    
    public int getSleepWrite() {
		return sleepWrite;
	}
	public void setSleepWrite(int sleepWrite) {
		this.sleepWrite = sleepWrite;
	}

}
