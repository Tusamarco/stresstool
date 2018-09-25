package net.tc.stresstool.statistics;

import java.util.Arrays;
import java.util.Map;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import net.tc.utils.MathU;
import net.tc.utils.SynchronizedMap;

/**
 * @author  tusa
 */
public class EventCollection {
    Map collection;
    long minValue = 0;
    long maxValue = 0 ;
    long sumValue = 0 ;
    Double averageValue = new Double(0);
    long lastValue = 0;
    int historyList = 350;
    int lastPurge = 2;
    String eventName;
    boolean processAverage = false;

    public StatEvent getEvent(Long id){
	if(collection.get(id) != null){
	    return (StatEvent) collection.get(id);
	}
	else
	    return null;
	
    }
    
    public void setEvent(StatEvent event){
	if(collection.size() >= getHistoryList()){
	    if(getHistoryList() <= 2)
		lastPurge = 2;
		
	    collection.remove(new Long(lastPurge + 1));
	    lastPurge++; 
	}
	if(event != null ){
	    collection.put(new Long(event.getId()), event);
	}
	
    }
    
    /**
     * @return    the collection
     * @uml.property  name="collection"
     */
    public final Map getCollection() {
        return collection;
    }

    /**
     * @param collection    the collection to set
     * @uml.property  name="collection"
     */
    public final void setCollection(Map collection) {
        this.collection = collection;
    }

    /**
     * @return    the minValue
     * @uml.property  name="minValue"
     */
    public final long getMinValue() {
        return minValue;
    }

    /**
     * @param minValue    the minValue to set
     * @uml.property  name="minValue"
     */
    public final void setMinValue(long minValueIn) {
	if(minValueIn < minValue)
	    this.minValue = minValueIn;
    }

    /**
     * @return    the maxValue
     * @uml.property  name="maxValue"
     */
    public final long getMaxValue() {
        return maxValue;
    }

    /**
     * @param maxValue    the maxValue to set
     * @uml.property  name="maxValue"
     */
    public final void setMaxValue(long maxValueIn) {
	if(maxValueIn > maxValue)
	    this.maxValue = maxValueIn;
    }

    /**
     * @return    the averageValue
     * @uml.property  name="averageValue"
     */
    public final Double getAverageValue() {
        return averageValue;
    }

    /**
     * @param averageValue the averageValue to set
     */
    public final void setAverageValue(Long averageValueIn) {
		if(!processAverage)
		    return;
		
		if(collection.size() <= 1){
		    averageValue = new Double(averageValueIn);
		    return;
		}
		StatEvent[] valuesa =new StatEvent[0];
		StatEvent[] values = (StatEvent[])((SynchronizedMap) collection).values().toArray(valuesa);
		Long[] valuesL = new Long[values.length +1];
		
		
		
		for(int i =0; i < values.length; i++){
		  try{
			   valuesL[i] = new Long(values[i].getValue().toString());
		     }catch(NumberFormatException numb){
		    	 valuesL[i] = new Long(0);
		     }
		}
		
   
		valuesL[valuesL.length -1] = averageValueIn;
		averageValue = MathU.getAverage(valuesL);
    }

    /**
     * @return    the processAverage
     * @uml.property  name="processAverage"
     */
    public final boolean isProcessAverage() {
        return processAverage;
    }

    /**
     * @param processAverage    the processAverage to set
     * @uml.property  name="processAverage"
     */
    public final void setProcessAverage(boolean processAverage) {
        this.processAverage = processAverage;
    }

    /**
     * @return    the lastValue
     * @uml.property  name="lastValue"
     */
    public final long getLastValue() {
        return lastValue;
    }

    /**
     * @param lastValue    the lastValue to set
     * @uml.property  name="lastValue"
     */
    public final void setLastValue(long lastValue) {
        this.lastValue = lastValue;
    }

    /**
     * @return    the historyList
     * @uml.property  name="historyList"
     */
    public final int getHistoryList() {
        return historyList;
    }

    /**
     * @param historyList    the historyList to set
     * @uml.property  name="historyList"
     */
    public final void setHistoryList(int historyList) {
        this.historyList = historyList;
    }

    /**
     * @return    the eventName
     * @uml.property  name="eventName"
     */
    public final String getEventName() {
        return eventName;
    }

    /**
     * @param eventName    the eventName to set
     * @uml.property  name="eventName"
     */
    public final void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public EventCollection() {
	collection = new SynchronizedMap(0);
	
    }

	public long getSumValue() {
		return sumValue;
	}

	public void setSumValue(long sumValue) {
		this.sumValue = sumValue;
	}

}
