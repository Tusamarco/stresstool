package net.tc.stresstool.statistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.tc.data.db.ConnectionProvider;
import net.tc.stresstool.PerformanceEvaluator;
import net.tc.stresstool.StressTool;
import net.tc.stresstool.config.ConfigValue;
import net.tc.stresstool.config.Configuration;
import net.tc.stresstool.config.Configurator;
import net.tc.stresstool.exceptions.ExceptionMessages;
/*
 * This class is responsible of instantiating all the providers declared in any section.
 * We can have as many provider we want the information will be collect from all 
 * of them
 */
import net.tc.stresstool.exceptions.StressToolConfigurationException;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.stresstool.exceptions.StressToolGenericException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.stresstool.statistics.providers.Reporter;
import net.tc.stresstool.statistics.providers.StatsProvider;
import net.tc.utils.SynchronizedMap;


/**
 * @author  tusa
 */
public class StatCollector {
    /**
     * @uml.property  name="providers"
     * @uml.associationEnd  qualifier="sectionName:java.lang.String net.tc.stresstool.statistics.providers.StatsProvider"
     */
    Map<String,StatsProvider> providers ;
    public static String PROVIDER_PARAMETER_NAME="providerclass";
    /**
     * @uml.property  name="statColl"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
    StatsCollection statColl = null;
    ConnectionProvider connectionProvider = null;
    /**
     * @uml.property  name="connectionInformation"
     */
    Map connectionInformation = null;
    /**
     * @uml.property  name="conn"
     */
    Connection conn = null;
    /**
     * @uml.property  name="rootPath"
     */
    String rootPath = null;
    /**
     * @uml.property  name="flushrowonfile"
     */
    boolean flushrowonfile = false;
    /**
     * @return the providers
     */
    public final Map<String, StatsProvider> getProviders() {
        return providers;
    }
    /**
     * @param providers the providers to set
     */
    public final void setProviders(Map<String, StatsProvider> providers) {
        this.providers = providers;
    }
    /**
     * @return    the rootPath
     * @uml.property  name="rootPath"
     */
    public final String getRootPath() {
        return rootPath;
    }
    /**
     * @param rootPath the rootPath to set
     */
    public final void setRootPath(Configurator configs) {
	if(configs != null ){
	    try {
		Configuration conf = configs.getConfiguration(Configurator.STATS_SECTION_NAME,StressTool.class);
		if(conf.getParameter("rootdirectory")!= null && conf.getParameter("rootdirectory").getValue() != null){
		    rootPath=(String)conf.getParameter("rootdirectory").getValue();
		}
		if(conf.getParameter("flushrowonfile")!= null && conf.getParameter("flushrowonfile").getValue() != null){
		    flushrowonfile=Boolean.parseBoolean((String)conf.getParameter("flushrowonfile").getValue());
		}

	    } catch (StressToolException e) {
		// TODO Auto-generated catch block
			try{					
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);				
				e.printStackTrace(ps);
				String s =new String(baos.toByteArray());
				StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
				System.exit(1)  ;
		}catch(Exception ex){ex.printStackTrace();}


	    }
	    
	    
	}
	
        
    }
    public StatCollector(Configurator configs,ConnectionProvider connProvider ) throws StressToolException {
	/* retrieve stat provider information and feed initial structure*/
	Iterator it = configs.getSectionsName();
	setRootPath(configs);
	
	connectionProvider = connProvider;
	
	providers  = new SynchronizedMap(0);
	
	statColl = new StatsCollection();
	
	providers = getProviders(configs, it);
	

	/*init connection*/
	try {
	    conn = connProvider.getSimpleConnection();
	} catch (SQLException e) {
	    throw new StressToolConfigurationException(e);
	}
	
	
    }
    private Connection initStatConnection(Map connectionInformation) {
	

	return null;
    }
    
    /* retrieve the list of all providers from configuration 
     * split the provider by comma 
     * Each Provider serve a specific stat group 
     * Like: STATUS - InnoDB - Performance Schema etc ...
     */
    private Map getProviders(Configurator configs, Iterator it)
	    throws StressToolException {
	
	/*Performance evaluation section [tail] start*/
	long performanceTimeStart = 0;
	long performanceTimeEnd = 0;
	if (StressTool.getLogProvider().getLogger(LogProvider.LOG_PERFORMANCE)
		.isDebugEnabled())
	    performanceTimeStart = System.nanoTime();
	/*Performance evaluation section [tail] END*/
	
	String currentProviderClassName ="";
	while(it.hasNext()){
	    String sectionName = (String)it.next();
	    Configuration conf = null;
	    try {
		conf = configs.getConfiguration(sectionName);

		if(conf != null 
			&& conf.getParameter(StatCollector.PROVIDER_PARAMETER_NAME) != null){

		    String cfv =(String) conf.getParameter(StatCollector.PROVIDER_PARAMETER_NAME).getValue();
		    String[] providerNames = cfv.split(",");
		    StatsProvider sp = null;
		    for(int isp =0 ; isp < providerNames.length;isp++){
			if(providerNames[isp] != null && !providerNames[isp].equals("") ){
			    currentProviderClassName = providerNames[isp];
        		    	sp = (StatsProvider)Class.forName(providerNames[isp].trim()).newInstance();
        		    	sp.setFlushDataOnfile(flushrowonfile);
        		    	sp.setStatsOutFile(rootPath);
           		    	
			}
        		    providers.put(sectionName+'.'+sp.getStatGroup(), sp);
		    }
		}

	    } catch (StressToolException e) {
			try{					
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);				
				e.printStackTrace(ps);
				String s =new String(baos.toByteArray());
				StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
				System.exit(1)  ;
		}catch(Exception ex){ex.printStackTrace();}


	    } catch (InstantiationException e) {
		throw new StressToolConfigurationException(e);
	    } catch (IllegalAccessException e) {
		throw new StressToolConfigurationException(e);
	    } catch (ClassNotFoundException e) {
		StressTool.getLogProvider().getLogger(LogProvider.LOG_APPLICATION).error(ExceptionMessages.ERROR_CREATING_PROVIDER + 
			" Provider class: " + currentProviderClassName + " invalid or not yet implement;\n " +
					"Check the configuration and remove class declaration if Provider is not available.");
		throw new StressToolConfigurationException(e);
	    }
	    
	}
	
	/*Performance evaluation section [header] start*/
	try {
	    if (StressTool.getLogProvider()
		    .getLogger(LogProvider.LOG_PERFORMANCE).isDebugEnabled()) {
		performanceTimeEnd = System.nanoTime();
		StressTool
			.getLogProvider()
			.getLogger(LogProvider.LOG_PERFORMANCE)
			.debug(StressTool.getLogProvider().LOG_EXEC_TIME
				+ ":"
				+ PerformanceEvaluator
					.getTimeEvaluationNs(performanceTimeStart));
	    }
	} catch (Throwable th) {
	}
	/*Performance evaluation section [header] END*/
	
	return providers;
    }
/* Retrieve information from each provider passing them the Open connection
 * we can have many provider but we can have only ONE connection for stats
 */
   public void collectStatistics() throws StressToolGenericException{
	/*Performance evaluation section [tail] start*/
	long performanceTimeStart = 0;
	long performanceTimeEnd = 0;
	try {
	    if (StressTool.getLogProvider()
		    .getLogger(LogProvider.LOG_PERFORMANCE).isDebugEnabled())
		performanceTimeStart = System.nanoTime();
	} catch (Throwable th) {
	}
	/*Performance evaluation section [tail] END*/
       
    StatsProvider sp = null;
    try{
		if(conn == null || conn.isClosed()){
			conn = this.connectionProvider.getSimpleConnection();
		}
    }catch(SQLException sqlx1){sqlx1.printStackTrace();}
    
    
	for(int iCstat = 0 ; iCstat < providers.size(); iCstat++){
	    sp = (StatsProvider)((SynchronizedMap)providers).getValueByPosition(iCstat);
	    Map values =  sp.collectStatistics(conn);
	    
	    /* try to load information on the Stat Collection
	     * if it failed for any reason will try 3 times then give up 
	     */
	    if(values != null && !statColl.processCollectedEvents(sp.getProviderName(),values)){
		int errorRepeat = 0;
		
		for(int ic =0 ; ic <= 3 ;ic++){
		  try {
			StressTool.getLogProvider().getLogger(LogProvider.LOG_STATS).warn(ExceptionMessages.ERROR_PORCESSING_STATS 
				+ " Provider " + sp.getProviderName() + " sent event to StatCollection but the process dicard the information \n" 
				+ " this is the " + ic + " attempt, try again \n");
		  } catch (StressToolException e) {
			e.printStackTrace();

		  }

		  if(statColl.processCollectedEvents(sp.getProviderName(),values)){
			ic=5;
		  }

		  throw new StressToolGenericException(ExceptionMessages.ERROR_PORCESSING_STATS 
			  + " Provider " + sp.getProviderName() + " sent event to StatCollection but the process dicard the information ");

		}
        	
	        
		
	    }
	    
	}
	/*Performance evaluation section [header] start*/
	try {
	    if (StressTool.getLogProvider()
		    .getLogger(LogProvider.LOG_PERFORMANCE).isDebugEnabled()) {
		performanceTimeEnd = System.nanoTime();
		StressTool
			.getLogProvider()
			.getLogger(LogProvider.LOG_PERFORMANCE)
			.debug(StressTool.getLogProvider().LOG_EXEC_TIME + ":"
				+ PerformanceEvaluator.getTimeEvaluationNs(performanceTimeStart));
	    }
	} catch (Throwable th) {
	}
	/*Performance evaluation section [header] END*/
    }
   public ArrayList getReporter(){
       	ArrayList reporter = new ArrayList();
       	
	for(int iCstat = 0 ; iCstat < providers.size(); iCstat++){
	    if(((SynchronizedMap)providers).getValueByPosition(iCstat) instanceof Reporter  ){
		reporter.add((Reporter)((SynchronizedMap)providers).getValueByPosition(iCstat));
	    }
	}
       
       return reporter;
   } 
   
   public void PrintFinalReport(){
       StringBuffer sb = new StringBuffer();
       
       ArrayList<Reporter> reporters = getReporter();
       for (Reporter i : reporters){
	   i.setStats(statColl.getStatGroups(i.getStatGroup()));
//	   i.printDataOnFile(i.getStatGroup());
	   i.printReport(i.getStatGroup(), sb);
       }
       System.out.println(sb.toString());
   }
    
}
