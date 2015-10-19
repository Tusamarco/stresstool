package net.tc.stresstool.statistics.providers;

import java.io.StringWriter;
import java.util.Map;

import net.tc.stresstool.statistics.StatsGroups;

/**
 * @author    tusa
 */
public interface Reporter {

    	/**
	 * @uml.property  name="reporterName"
	 */
    	public String getReporterName();
    	public String[] getHeadersArray(String StatsGroupName);
    	public String[] getRowDataArray(String[] headersArray);
    	public StringBuffer getHeaders(StringBuffer sb);
    	public StringBuffer getRowData(StringBuffer sb);
    	public StringBuffer printReport(String StatsGroupName,StringBuffer incomingBuffer);
//    	public void printDataOnFile(String StatsGroupName);
    	public void setStats(StatsGroups stats);
    	/**
	 * @uml.property  name="statGroup"
	 */
    	public String getStatGroup();
	void writeReportOnFile(String outString);
}
