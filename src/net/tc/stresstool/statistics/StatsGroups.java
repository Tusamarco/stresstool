package net.tc.stresstool.statistics;

import java.util.Map;

import net.tc.stresstool.StressTool;
import net.tc.stresstool.config.ConfigValue;
import net.tc.stresstool.config.Configurator;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.stresstool.exceptions.StressToolGenericException;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.file.FileHandler;

/**
 * @author  tusa
 */
public class StatsGroups {
    /**
     * @uml.property  name="id"
     */
    long id = 0 ;
    /**
     * @uml.property  name="startTime"
     */
    long startTime = 0;
    /**
     * @uml.property  name="lastSampleTime"
     */
    long lastSampleTime = 0;
    /**
     * @uml.property  name="groupName"
     */
    String groupName = null;
    /**
     * @uml.property  name="eventCollectionMap"
     * @uml.associationEnd  qualifier="eventName:java.lang.String net.tc.stresstool.statistics.EventCollection"
     */
    Map<String, EventCollection> eventCollectionMap;
    /**
     * @uml.property  name="parseaverage"
     */
    boolean parseaverage = false;
    /**
     * @uml.property  name="historylength"
     */
    int historylength =350;
    
    /*
     * This functions return an object of EventCollection type if this is already exist
     * if not Object get create and it is set with minimal value
     * new Created Object is NOT added to StatGroup collection but pass back for being filled
     * in the StatsCollection method feedGroup.
     */
    public EventCollection getEventCollection(String eventName) {
	if(eventName != null 
		&& !eventName.equals("")
		&& eventCollectionMap != null 
		&& eventCollectionMap.get(eventName) != null){
	    return eventCollectionMap.get(eventName);
	}
	else{
	    ConfigValue cfv = null;
	    Configurator config = StressTool.getConfig();
	    String tmpConf=null;
	    try{
		cfv = config.getConfiguration(Configurator.STATS_SECTION_NAME, StressTool.class).getParameter("processaverage");
		tmpConf = cfv!=null?(String)cfv.getValue():null;
		if(tmpConf !=null && Boolean.parseBoolean(tmpConf)){
		    parseaverage = Boolean.parseBoolean(tmpConf);
		    cfv = null;
		}
		cfv = config.getConfiguration(Configurator.STATS_SECTION_NAME, StressTool.class).getParameter("eventhistory");
		tmpConf = cfv!=null?(String)cfv.getValue():null;
		if(tmpConf !=null && Boolean.parseBoolean(tmpConf)){
		    historylength = Integer.parseInt(tmpConf);
		    historylength = historylength<2?2:historylength;
		    cfv = null;
		}
		    
	    
	    }
	    catch(Throwable th){
		new StressToolGenericException(th);
		
	    }
	    EventCollection eventCollection = new EventCollection();
	    eventCollection.setEventName(eventName);
	    eventCollection.setLastValue(0);
	    eventCollection.setMaxValue(0);
	    eventCollection.setMinValue(0);
	    eventCollection.setHistoryList(historylength);
	    eventCollection.setProcessAverage(parseaverage);
	    
//	    eventCollectionMap.put(eventName, eventCollection);
	    return eventCollection;
	}
        
    }

    /**
     * @param eventCollection the eventCollection to set
     */
    public final void setEventCollection(String key, EventCollection eventCollection) {
        eventCollectionMap.put(key, eventCollection);
    }

   
    /** 
     * Empty Constructor 
     */
    /*
     * The StatsGroup class it the representation of the historical collection
     * of statistics organized by column and not by row this to have data consistently associate
     * instead of having it by slice (row)
     * 
     * Any element inside the event Collection is a statistic with one or more entry
     */
    public StatsGroups() {
	eventCollectionMap = new SynchronizedMap(0);
	
    }

    /** 
     * Constructor allocating name 
     */
    public StatsGroups(String groupName) {
    	setGroupName(groupName);
    	eventCollectionMap = new SynchronizedMap(0);
    }
    /**
     * @return    the id
     * @uml.property  name="id"
     */
    public final long getId() {
        return id;
    }

    /**
     * @param id    the id to set
     * @uml.property  name="id"
     */
    public final void setId(long id) {
        this.id = id;
    }

    /**
     * @return    the startTime
     * @uml.property  name="startTime"
     */
    public final long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime    the startTime to set
     * @uml.property  name="startTime"
     */
    public final void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return    the lastSampleTime
     * @uml.property  name="lastSampleTime"
     */
    public final long getLastSampleTime() {
        return lastSampleTime;
    }

    /**
     * @param lastSampleTime    the lastSampleTime to set
     * @uml.property  name="lastSampleTime"
     */
    public final void setLastSampleTime(long lastSampleTime) {
        this.lastSampleTime = lastSampleTime;
    }

    /**
     * @return    the groupName
     * @uml.property  name="groupName"
     */
    public final String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName    the groupName to set
     * @uml.property  name="groupName"
     */
    public final void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    
    

}
