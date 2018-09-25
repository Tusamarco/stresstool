package net.tc.stresstool.statistics;
/*
 * Element containing the information for each event
 * the event is the NAME of the event
 * while the value has to possible field absolute and relative
 * absolute is the reported as it is
 * relative or DELTA is the calculation of the difference from the previous reading 
 * this is valid ONLY in case of numeric object
 */


/**
 * @author  tusa
 */
public class StatEvent {
    long id = 0 ;
    long time = 0;
    int order = 0;
    String collection;
    String parent;
    String event ;
    Object absoluteValue ;
    long deltaValue; 
    public final long getDeltaValue() {
        return deltaValue;
    }

    public final void setDeltaValue(long deltaValue) {
        this.deltaValue = deltaValue;
    }

    String provider = null;
    
    public final long getId() {
        return id;
    }

    public final void setId(long id) {
        this.id = id;
    }

    public final long getTime() {
        return time;
    }

    public final void setTime(long time) {
        this.time = time;
    }

    public final int getOrder() {
        return order;
    }

    public final void setOrder(int order) {
        this.order = order;
    }

    public final String getCollection() {
        return collection;
    }

    public final void setCollection(String collection) {
        this.collection = collection;
    }

    public final String getParent() {
        return parent;
    }

    public final void setParent(String parent) {
        this.parent = parent;
    }

    public final String getEvent() {
        return event;
    }

    public final void setEvent(String event) {
        this.event = event;
    }

    public final Object getValue() {
        return absoluteValue;
    }

    public final void setValue(Object value) {
        this.absoluteValue = value;
    }

    public final String getProvider() {
        return provider;
    }

    public final void setProvider(String provider) {
        this.provider = provider;
    }

    public StatEvent() {
	// TODO Auto-generated constructor stub
    }

}
