package net.tc.stresstool.statistics.providers;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.text.FixedTerminalSizeProvider;

import net.tc.data.db.ConnectionProvider;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.actions.Launcher;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.StatsGroups;
import net.tc.stresstool.actions.*;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.utils.SynchronizedMap;

public class ConsoleStatePrinter implements StatsProvider,Reporter,Runnable {
    Terminal terminal = TerminalFacade.createTextTerminal(System.in, System.out, Charset.forName("US-ASCII"));
//    Terminal terminal = TerminalFacade.createTextTerminal(System.in, System.out, Charset.forName("US-ASCII"));
//    TerminalSize tSize =new TerminalSize(100,200);
//    Screen screen = new Screen(terminal, 180,180);
    Screen screen = TerminalFacade.createScreen();
    TerminalSize ntSize = screen.getTerminalSize();
    int rows = (ntSize.getRows()-2);
    int columns = ntSize.getColumns();
	float curPct = (float) 0.0;
    float maxPct = (float) 0.0;
    Launcher launcher = null;
    int timeForLoop = 1000;
    
    public ConsoleStatePrinter(Launcher launcher){
    	this.launcher = launcher;
    	
    	timeForLoop =launcher.getStatIntervalMs();
    	int loops = launcher.getStatLoops()> launcher.getRepeatNumber()?launcher.getStatLoops():launcher.getRepeatNumber();
    	if(timeForLoop < 1000){
    		loops = loops * (1000/timeForLoop);
    	}
    	else{
    		loops = loops / (timeForLoop/1000);
    	}
    	
    	maxPct = loops;
    	
    	screen.startScreen();
    	screen.putString(0, 0, "StM#", Terminal.Color.WHITE, Terminal.Color.BLACK);
    	screen.putString(10, 0, "TW#", Terminal.Color.WHITE, Terminal.Color.BLACK);
    	screen.putString(14, 0, "TR#", Terminal.Color.WHITE, Terminal.Color.BLACK);
    	screen.putString(18, 0, "TU#", Terminal.Color.WHITE, Terminal.Color.BLACK);
    	screen.putString(22, 0, "TD#", Terminal.Color.WHITE, Terminal.Color.BLACK);
    	screen.putString(26, 0, "|| Threads information (time in ms)", Terminal.Color.WHITE, Terminal.Color.BLACK);
    	
    	for(int x = 0 ; x <= columns;x++){
    		screen.putString(x, 1, "-", Terminal.Color.WHITE, Terminal.Color.BLACK);
    	}
    	for(int x = 0; x <= rows +1;x++){
		screen.putString(26, x, "||", Terminal.Color.WHITE, Terminal.Color.BLACK);
	}
    	
    	int cRow = 2;
    	int cCol = 28;
    	screen.putString(cCol, cRow++, "ThW Latency Min: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThW Latency Max: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThW ExecT   Min: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThW ExecT   Max: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThW Loop    Min: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThW Loop    Max: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	
    	screen.putString(cCol, cRow++, "------------------------------------", Terminal.Color.WHITE, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThR Latency Min: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThR Latency Max: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThR ExecT   Min: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThR ExecT   Max: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThR Loop    Min: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThR Loop    Max: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
   	
    	screen.putString(cCol, cRow++, "------------------------------------", Terminal.Color.WHITE, Terminal.Color.BLACK);
    	
    	screen.putString(cCol, cRow++, "ThU Latency Min: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThU Latency Max: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThU ExecT   Min: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThU ExecT   Max: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThU Loop    Min: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThU Loop    Max: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
   	
    	screen.putString(cCol, cRow++, "------------------------------------", Terminal.Color.WHITE, Terminal.Color.BLACK);

    	screen.putString(cCol, cRow++, "ThD Latency Min: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThD Latency Max: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThD ExecT   Min: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThD ExecT   Max: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThD Loop    Min: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);
    	screen.putString(cCol, cRow++, "ThD Loop    Max: ", Terminal.Color.YELLOW, Terminal.Color.BLACK);

    	screen.refresh();
    	    

    	
    }
    public void close(){
    	
    	terminal.clearScreen();
    	screen.stopScreen();
    	
    	
    }
	public int printLine(int loop){
        curPct=(rows * (loop/maxPct));
        
        int dot = new Float((rows - curPct)+2).intValue();
        screen.putString(1, dot, Integer.toString(loop), Terminal.Color.WHITE, Terminal.Color.BLACK);
        
        String[] arActions = new String[]{"writes","selects","updates","deletes"};
        
        int cRow = 2;
        int cBarCol = 8;
        
        for(Object strAction:arActions){
            cBarCol = cBarCol+2;
            Map toPrint = null;
            toPrint = getMinMax((String)strAction);
            
            if(toPrint != null){
                Long minLoop = (Long) toPrint.get("MinLoop");
                Long maxLoop = (Long) toPrint.get("MaxLoop");
            	
            	if(this.timeForLoop < 1000){
            		minLoop = minLoop * (1000/timeForLoop);
            		maxLoop = maxLoop * (1000/timeForLoop);
            	}
            	else{
            		minLoop = minLoop / (timeForLoop/1000);
            		maxLoop = maxLoop / (timeForLoop/1000);
            	}

            	
            	float minXl = (rows * (minLoop/maxPct));
            	float maxXl = (rows * (maxLoop/maxPct));
            	int min = new Float((rows - minXl)+2).intValue();
            	int max = new Float((rows - maxXl)+2).intValue();
                
            	screen.putString(cBarCol++, min, "*", Terminal.Color.RED, Terminal.Color.BLACK);
            	screen.putString(cBarCol++, max, "*", Terminal.Color.GREEN, Terminal.Color.BLACK);
                
                
                String[] valAr = new String[]{"MinLatency","MaxLatency","MinTime","MaxTime","MinLoop","MaxLoop","-"};
                
                int cCol = 45;
    
                for(Object str:valAr){
                 if(!str.equals("-")){
                     screen.putString(cCol,cRow++, new Long((long) (toPrint.get(str))).toString(), Terminal.Color.GREEN, Terminal.Color.BLACK);
                }
                 else 
                     screen.putString(cCol,cRow++,"-", Terminal.Color.WHITE, Terminal.Color.BLACK);
          }
        }
        else{
        	int min = 0;
        	int max = 0;
            
        	screen.putString(cBarCol++, min, "*", Terminal.Color.RED, Terminal.Color.BLACK);
        	screen.putString(cBarCol++, max, "*", Terminal.Color.GREEN, Terminal.Color.BLACK);
            
            
            String[] valAr = new String[]{"MinLatency","MaxLatency","MinTime","MaxTime","MinLoop","MaxLoop","-"};
            
            int cCol = 45;

            for(Object str:valAr){
             if(!str.equals("-")){
                 screen.putString(cCol,cRow++, "0", Terminal.Color.GREEN, Terminal.Color.BLACK);
            }
             else 
                 screen.putString(cCol,cRow++,"-", Terminal.Color.WHITE, Terminal.Color.BLACK);
      }
        	
        	
        }
        
        
        screen.refresh();

        }
        return 0;
        
	
       }
	private Map<String,Long> getMinMax(String action){
		long MinLatency = 0;
		long MaxLatency = 0;
		long MaxTime = 0;
		long MinTime = 0 ;
		long MaxLoop = 0 ;
		long MinLoop = 0 ;
		SynchronizedMap threads = null;
		switch (action){
			case "writes": threads =  launcher.getWriteImplementationMap();break;
			case "updates": threads = launcher.getUpdateImplementationMap(); break;
			case "deletes": threads = launcher.getDeleteImplementationMap(); break;
			case "selects": threads = launcher.getReadImplementationMap(); break;
		}
		if(threads == null)
		    return null;
		
		
		for(Object i:threads.getKeyasOrderedArray()){
		    	try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== Thread id " +  ((StressAction)threads.get(i)).getTHInfo().getId() );}catch(StressToolConfigurationException e){}
			try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== Latency Max " +  ((StressAction)threads.get(i)).getTHInfo().getMaxLatency() );}catch(StressToolConfigurationException e){}
			if(((StressAction)threads.get(i)).getTHInfo().getMaxLatency() > MaxLatency )
				MaxLatency = ((StressAction)threads.get(i)).getTHInfo().getMaxLatency();
			
			try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== Latency Min " +  ((StressAction)threads.get(i)).getTHInfo().getMinLatency() );}catch(StressToolConfigurationException e){}
			if(((StressAction)threads.get(i)).getTHInfo().getMinLatency() < MinLatency || MinLatency == 0){
				MinLatency = ((StressAction)threads.get(i)).getTHInfo().getMinLatency();
			}
			
			try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== Exec Max " +  ((StressAction)threads.get(i)).getTHInfo().getMaxExectime() );}catch(StressToolConfigurationException e){}
			if(((StressAction)threads.get(i)).getTHInfo().getMaxExectime() > MaxTime)
				MaxTime = ((StressAction)threads.get(i)).getTHInfo().getMaxExectime();
			
			try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== Exec Min " +  ((StressAction)threads.get(i)).getTHInfo().getMinExectime() );}catch(StressToolConfigurationException e){}
			if(((StressAction)threads.get(i)).getTHInfo().getMinExectime() < MinTime || MinTime == 0 )
				MinTime = ((StressAction)threads.get(i)).getTHInfo().getMinExectime();
			
			
			try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== Loop Min " +  ((StressAction)threads.get(i)).getTHInfo().getCurrentLoop());}catch(StressToolConfigurationException e){}
			if(((StressAction)threads.get(i)).getTHInfo().getCurrentLoop() < MinLoop || MinLoop == 0)
				MinLoop = ((StressAction)threads.get(i)).getTHInfo().getCurrentLoop();

			try{StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).debug(" ==== Loop Max " +  ((StressAction)threads.get(i)).getTHInfo().getCurrentLoop());}catch(StressToolConfigurationException e){}
			if(((StressAction)threads.get(i)).getTHInfo().getCurrentLoop() > MaxLoop ){
				MaxLoop = ((StressAction)threads.get(i)).getTHInfo().getCurrentLoop();
			}

		
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
	public boolean askQuestion(String question,String exitString, boolean block){
	    screen.putString(28,screen.getTerminalSize().getRows()-1, question, Terminal.Color.WHITE, Terminal.Color.BLACK);
//	    screen.putString(28,screen.getTerminalSize().getRows()-1, question, Terminal.Color.WHITE, Terminal.Color.BLACK);
	    screen.setCursorPosition(new TerminalPosition((28 + (question.length())),screen.getTerminalSize().getRows() -1));
	    screen.refresh();
	    
	    
//	    com.googlecode.lanterna.input.KeyType
	    
	    Key key = null;
	    StringBuffer sb = new StringBuffer();
	    if(block){
	    		key = screen.readInput();
        	    while(key == null || key.getKind() != Key.Kind.Enter){
        	    	screen.refresh();
        	    	key = screen.readInput();
//	        		System.out.println(key!=null?key.getCharacter():"");
	        		try {Thread.sleep(500);} catch (InterruptedException e) {}
	        		if(key != null){
	                		sb.append(key.getCharacter());
	                		screen.putString((28 + (question.length())),screen.getTerminalSize().getRows() -1 , sb.toString(), Terminal.Color.WHITE, Terminal.Color.BLACK);
	                		screen.refresh();
	        		}
	        		if(sb.toString().equals("y"))
	        			return false;
        	    }
	    }
	    else{
    		key = screen.readInput();
//    		System.out.println(key!=null?key.getCharacter():"");
//    		try {Thread.sleep(100);} catch (InterruptedException e) {}
    		if(key != null){
            		sb.append(key.getCharacter());
            		screen.putString((28 + (question.length())),screen.getTerminalSize().getRows() -1 , sb.toString(), Terminal.Color.WHITE, Terminal.Color.BLACK);
            		screen.refresh();
    		}
    		if(sb.toString().equals(exitString))
    		    	return false;
	    }
	    
	    
	    return true;
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
	public boolean validatePermissions(ConnectionProvider connProvider) {
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
