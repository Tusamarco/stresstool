package net.tc.stresstool.logs;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Appender;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

import net.tc.stresstool.config.Configuration;
import net.tc.stresstool.exceptions.ExceptionMessages;
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;
import net.tc.utils.Utils;

/**
 * 
 */

/**
 * @author  tusa
 */
public class LogProvider {

	/**
	 * This is the reference class for any log operation  it is an abstraction to facilitate the LOGGing layer substitution or upgrade
	 * @uml.property  name="loggerMaps"
	 * @uml.associationEnd  qualifier="loggerName:java.lang.String org.apache.log4j.Logger"
	 */
    private Map loggerMaps = new SynchronizedMap(0);
    public static String LOG_APPLICATION = "APPLICATION";
    public static String LOG_STATS = "STATS";
    public static String LOG_ACTIONS = "ACTIONS";
    public static String LOG_MONITORS = "MONITORS";
    public static String LOG_PERFORMANCE = "PERFORMANCE";
    public static String LOG_EXEC_TIME = "Execution time: ";
    
    public LogProvider() {
	    
	}
	
	public void initLogs(Configuration logConf) throws StressToolException{
	    PropertyConfigurator.configure((String)logConf.getParameter("stresstool").getValue());
	    String loggerName = "";
	    try{
        	    for (Enumeration e = LogManager.getCurrentLoggers() ; e.hasMoreElements() ;) {
        		loggerName = ((Logger)e.nextElement()).getName();
        		System.out.println("Testing Logger :"+ loggerName) ;
        		Logger loggerTest = Logger.getLogger(loggerName);
        		if(loggerTest.isDebugEnabled()){
        		    
        		    Enumeration it = (Enumeration)Logger.getLogger(loggerName).getAllAppenders();
               		    while(it.hasMoreElements()){
               			    Appender ap = (Appender) it.nextElement();
                		    if(ap.getLayout() instanceof org.apache.log4j.PatternLayout){
                			PatternLayout lo = (PatternLayout)ap.getLayout();
                			lo.setConversionPattern("%d{ISO8601},%c{2},%p,(%C - %M :%L), %m%n");
                			ap.setLayout(lo);
                			Logger.getLogger(loggerName).addAppender(ap);
                		    }else if(ap.getLayout() instanceof org.apache.log4j.EnhancedPatternLayout){
                			EnhancedPatternLayout lo = (EnhancedPatternLayout)ap.getLayout();
                			lo.setConversionPattern("%d{ISO8601},%c{2},%p,(%C - %M :%L) %m%n");
                			ap.setLayout(lo);
                			Logger.getLogger(loggerName).addAppender(ap);
                		    }
        		    }
        		    
        		}
        		loggerTest.info("Initializing Logger "+ loggerName + " at: " + Utility.getTimestamp());
        		loggerMaps.put(loggerName, Logger.getLogger(loggerName));

        	    }
	    }catch(Exception ex){
		ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
        	throw new  StressToolConfigurationException(ExceptionMessages.INVALID_LOG_REQUEST  
					+ " Logger = " + loggerName + " not define,"
					+ " or log improperly set, review LOG configuration" );
		
	    }
	    
	    //test
	    
	     
	     
	    
	}
	public Logger getLogger(String loggerName) throws StressToolConfigurationException {
	    if(loggerMaps.containsKey(loggerName)){
		return (Logger) loggerMaps.get(loggerName);
	    }
	    else{
		ExceptionMessages.setCurrentError(ExceptionMessages.ERROR_FATAL);
		throw new  StressToolConfigurationException(ExceptionMessages.INVALID_LOG_REQUEST  
					+ " Logger = " + loggerName + " not define,"
					+ " or log improperly set, review LOG configuration" );

	    
	    }
	}
	
}
