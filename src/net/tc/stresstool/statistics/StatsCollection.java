package net.tc.stresstool.statistics;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import net.tc.stresstool.StressTool;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.stresstool.logs.LogProvider;
import net.tc.utils.Text;
import net.tc.utils.SynchronizedMap;

/**
 * @author  tusa
 */
public class StatsCollection {

    /**
     * @uml.property  name="actionHistory"
     */
    int actionHistory =0;
    /**
     * @uml.property  name="statsHistory"
     */
    int statsHistory =0;
    /**
     * @uml.property  name="actionGroups"
     * @uml.associationEnd  qualifier="actionName:java.lang.String net.tc.stresstool.statistics.ActionGroups"
     */
    Map actionGroups;
    /**
     * @uml.property  name="statGroups"
     * @uml.associationEnd  qualifier="statGroupName:java.lang.String net.tc.stresstool.statistics.StatsGroups"
     */
    Map statGroups;
    public StatsCollection() {
	
	statGroups = new SynchronizedMap(0);
	actionGroups = new SynchronizedMap(0);

    }
    /* 
     * Get information from the collector (provider) organize as map of events (statistics) say table form by row
     * and transform it in to column collection by attribute
     * Also create a StatsGroup if not present 
     */
    public boolean processCollectedEvents(String providerName, Map<String, StatEvent> providerStatsEvents){
	StatsGroups statsGroupsElement = null;
	try{
        	if(statGroups.get(providerName)==null){
        	    statsGroupsElement = createStatsGroup(providerName,providerStatsEvents);	  
        	}
        	else{
        	    statsGroupsElement = (StatsGroups) statGroups.get(providerName);
        	}
        	
        	statsGroupsElement = feedGroup(statsGroupsElement,providerStatsEvents);
        	statGroups.put(providerName, statsGroupsElement);
        	return true;
	}
	catch(Throwable th){
	    try {
		StressTool.getLogProvider().getLogger(LogProvider.LOG_STATS).error("Provider: " + providerName 
			+ " failed in loding values.\n Below list of skipped values:\n" + printEvents(providerStatsEvents) );
		
		return false;
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


		return false;
		
	    }
	    
	}
	
    }

    StatsGroups createStatsGroup(String providerName, Map providerStatsEvents){
	StatsGroups sGroup = new StatsGroups(providerName);
	StatEvent event = null;
	if(providerStatsEvents != null 
		&& providerStatsEvents.size() > 0){
	    event = (StatEvent)((SynchronizedMap)providerStatsEvents).getValueByPosition(0);
	}
	else{
	    return null;
	}
	    
	sGroup.setStartTime(event.getTime());
	sGroup.setLastSampleTime(event.getTime());
	
	return sGroup;
	
	
    }
    
    StatsGroups feedGroup(StatsGroups group, Map<String, StatEvent> events){
	EventCollection eventCollection = null;
	StatEvent event = null;
	if(events != null 
		&& events.keySet() != null
		&& events.keySet().iterator() != null){
	    	try{
        	    	Iterator<String> it = events.keySet().iterator();
        	    	while(it.hasNext()){
        	    	    String eventCollectionName = it.next();
        	    	    event = events.get(eventCollectionName);
        	    	    eventCollection = group.getEventCollection(eventCollectionName);
        //	    	    Object value = event.getValue();
        	    	    if(Text.isNumeric(event.getValue())){
        	    		Long value = Text.toLong(event.getValue(),new Long(0));
        	    		eventCollection.setAverageValue(value);
        	    		eventCollection.setMaxValue(value.longValue());
        	    		eventCollection.setMinValue(value.longValue());
        	    		if(eventCollection.getCollection().size() >=1 ){
        	    		    event.setDeltaValue(value.longValue() -  eventCollection.lastValue);
        	    		}
        	    		else{
        	    		    event.setDeltaValue(0);
        	    		}
        	    	    }
        	    	    eventCollection.setEvent(event);
        	    	    group.setEventCollection(eventCollectionName, eventCollection);
        	    	    group.setLastSampleTime(event.getTime());
        	    	    
        	    	}
	    	}
	    	catch(Throwable th){
				th.printStackTrace();

	    	}
	    	return group;
    	}
	else 
	   return null;
    }
    private String printEvents(Map<String, StatEvent> providerStatsEvents){
	StringBuffer sb = new StringBuffer();
	Iterator<String> it = providerStatsEvents.keySet().iterator();
	while(it.hasNext()){
	    String name = it.next();
	    sb.append(name + " = " + providerStatsEvents.get(name).getValue().toString() + ";\n");
	    
	}
	sb.append("-----------------------------------------------------------");
	return sb.toString();
    }
    /**
     * @return the actionGroups
     */
    public final ActionGroups getActionGroups(String actionName) {
        if(actionName != null && !actionName.equals(""))
            return (ActionGroups) actionGroups.get(actionName);
        return null;
    }
    /**
     * @return the statGroups
     */
    public final StatsGroups getStatGroups(String statGroupName) {
        if(statGroupName != null && !statGroupName.equals(""))
            return (StatsGroups) statGroups.get(statGroupName);
        return null;
    }
    
    
}
