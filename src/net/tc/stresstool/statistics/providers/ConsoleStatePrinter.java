package net.tc.stresstool.statistics.providers;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.text.FixedTerminalSizeProvider;

import net.tc.stresstool.StressTool;
import net.tc.stresstool.actions.Launcher;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.StatsGroups;
import net.tc.stresstool.actions.*;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.utils.SynchronizedMap;

public class ConsoleStatePrinter implements StatsProvider,Reporter,Runnable {
    Terminal terminal = TerminalFacade.createTerminal(System.in, System.out);
//    TerminalSize tSize =new TerminalSize(100,200);
//    Screen screen = new Screen(terminal, tSize);
    Screen screen = TerminalFacade.createScreen();
    TerminalSize ntSize = screen.getTerminalSize();
    int rows = (ntSize.getRows()-2);
    int columns = ntSize.getColumns();
	float curPct = (float) 0.0;
    float maxPct = (float) 0.0;
    Launcher launcher = null;
  
    
    public ConsoleStatePrinter(Launcher launcher){
    	this.launcher = launcher;
    	int loops = launcher.getStatLoops()< launcher.getRepeatNumber()?launcher.getStatLoops():launcher.getRepeatNumber();
    	maxPct = loops;
    	
    	screen.startScreen();
    	screen.putString(0, 0, "StL", Terminal.Color.WHITE, Terminal.Color.BLACK);
    	screen.putString(4, 0, "TWL#", Terminal.Color.RED, Terminal.Color.BLACK);
    	screen.putString(9, 0, "TWLa", Terminal.Color.RED, Terminal.Color.BLACK);
    	screen.putString(14, 0, "TWT", Terminal.Color.RED, Terminal.Color.BLACK);
    	for(int x = 0 ; x <= columns;x++){
    		screen.putString(x, 1, "-", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	}
    	screen.refresh();
    	    

    	
    }
	public int printLine(int loop){
        curPct=(rows * (loop/maxPct));
        
        int dot = new Float((rows - curPct)+2).intValue();
        screen.putString(1, dot, Integer.toString(loop), Terminal.Color.WHITE, Terminal.Color.BLACK);

        
        SynchronizedMap writes = launcher.getWriteImplementationMap();
        SynchronizedMap updates = launcher.getUpdateImplementationMap();
        SynchronizedMap deletes = launcher.getDeleteImplementationMap();
        SynchronizedMap selects = launcher.getReadImplementationMap();
        
        Map toPrint = null;
        toPrint = getMinMax(writes);
        if(toPrint != null){
        	float minXl = (rows * ((Long)toPrint.get("MinLoop")/maxPct));
        	float maxXl = (rows * ((Long)toPrint.get("MaxLoop")/maxPct));
        	int min = new Float((rows - minXl)+2).intValue();
        	int max = new Float((rows - maxXl)+2).intValue();
            screen.putString(5, min, "|", Terminal.Color.RED, Terminal.Color.BLACK);
            screen.putString(6, max, "|", Terminal.Color.GREEN, Terminal.Color.BLACK);
        	
//        logProvider.getLogger(LogProvider.LOG_APPLICATION).debug("DOT POSITION = " + dot);
        
        }
        
        
        screen.refresh();

        
        return 0;
        
	}
	private Map<String,Long> getMinMax(SynchronizedMap threads){
		long MinLatency = 0;
		long MaxLatency = 0;
		long MaxTime = 0;
		long MinTime = 0 ;
		long MaxLoop = 0 ;
		long MinLoop = 0 ;
		
		for(Object i:threads.getKeyasOrderedArray()){
			try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== Latency Max " +  ((StressAction)threads.get(i)).getTHInfo().getMaxLatency() );}catch(StressToolConfigurationException e){}
			if(((StressAction)threads.get(i)).getTHInfo().getMaxLatency() > MaxLatency)
				MaxLatency = ((StressAction)threads.get(i)).getTHInfo().getMaxLatency();
			
			try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== Latency Min " +  ((StressAction)threads.get(i)).getTHInfo().getMinLatency() );}catch(StressToolConfigurationException e){}
			if(((StressAction)threads.get(i)).getTHInfo().getMinLatency() < MinLatency){
				MinLatency = ((StressAction)threads.get(i)).getTHInfo().getMinLatency();
			}
			
			try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== Exec Max " +  ((StressAction)threads.get(i)).getTHInfo().getMaxExectime() );}catch(StressToolConfigurationException e){}
			if(((StressAction)threads.get(i)).getTHInfo().getMaxExectime() > MaxTime)
				MaxTime = ((StressAction)threads.get(i)).getTHInfo().getMaxExectime();
			
			try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== Exec Min " +  ((StressAction)threads.get(i)).getTHInfo().getMinExectime() );}catch(StressToolConfigurationException e){}
			if(((StressAction)threads.get(i)).getTHInfo().getMinExectime() < MinTime)
				MinTime = ((StressAction)threads.get(i)).getTHInfo().getMinExectime();
			
			try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== Loop Max " +  ((StressAction)threads.get(i)).getTHInfo().getCurrentLoop());}catch(StressToolConfigurationException e){}
			if(((StressAction)threads.get(i)).getTHInfo().getCurrentLoop() > MaxLoop)
				MaxLoop = ((StressAction)threads.get(i)).getTHInfo().getCurrentLoop();
			
			if(((StressAction)threads.get(i)).getTHInfo().getCurrentLoop() < MinLoop)
				MinLoop = ((StressAction)threads.get(i)).getTHInfo().getCurrentLoop();
		}
		Map<String,Long> values  = new HashMap<String, Long>();
		values.put("MinLatency", MinLatency);
		values.put("MaxLatency", MaxLatency);
		values.put("MinTime", MinTime);
		values.put("MaxTime", MaxTime);
		values.put("MinLoop", MinLoop);
		values.put("MaxLoop", MaxLoop);
		
		return values;
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

    
	@Override
	public Map collectStatistics(Connection conn) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public Map getMetrics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getStatsOutFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStatGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getEventsName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeStatsOnFile(Map valuesToWrite) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getProviderName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStatsOutFile(String rootPath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFlushDataOnfile(boolean flushrowonfile) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean validatePermissions(Map connectionConfiguration) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public String getReporterName() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String[] getHeadersArray(String StatsGroupName) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String[] getRowDataArray(String[] headersArray) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public StringBuffer getHeaders(StringBuffer sb) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public StringBuffer getRowData(StringBuffer sb) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public StringBuffer printReport(String StatsGroupName,
			StringBuffer incomingBuffer) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setStats(StatsGroups stats) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void writeReportOnFile(String outString) {
		// TODO Auto-generated method stub
		
	}

}
