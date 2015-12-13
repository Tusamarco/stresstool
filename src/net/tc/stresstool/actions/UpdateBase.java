package net.tc.stresstool.actions;

import java.io.FileReader;
import java.util.concurrent.CountDownLatch;

import net.tc.data.db.Schema;
import net.tc.jsonparser.*;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.config.Configurator;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.ActionTHElement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class UpdateBase extends StressActionBase implements UpdateAction,
	CreateAction {
	
    private int numberOfprimaryTables=1;
    private int numberOfSecondaryTables=0;
    
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
	public void TruncateTables(String[] schema) {
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
	public boolean CreateSchema() {
	    try {
		StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION)
		.error("Table structure Can be create ONLY when using INSERT classes");
	    } catch (StressToolConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    return false;
	}
	
	public boolean  DropSchema(String[] schema) {
		// TODO Auto-generated method stub
		return false;
	}
	
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

	/**
	 * this is actually executing what the action is suppose to do
	 */
	@Override
	public void ExecuteAction() {
	    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(" ==== ACTION "+ this.getTHInfo().getAction() +" Thread internal Id "+ this.getTHInfo().getId() +" Sys Thread Id "+ this.getTHInfo().getThId()+" started ===="  );}catch(StressToolConfigurationException e){}
	    for(int i = 0 ; i  < 200; i++){
		try {
		    Thread.sleep(800);
		} catch (InterruptedException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	    this.getTHInfo().setReady(ActionTHElement.SEMAPHORE_RED);
	    try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).info(" ==== ACTION "+ this.getTHInfo().getAction() +" Thread internal Id "+ this.getTHInfo().getId() +" Sys Thread Id "+ this.getTHInfo().getThId()+" ended ===="  );}catch(StressToolConfigurationException e){}

	    }
	
	
}
