package net.tc.stresstool.statistics.providers;
import java.io.File;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;

import net.tc.data.db.ConnectionProvider;
import net.tc.utils.file.FileHandler;


/**
 * @author    tusa
 */
public interface StatsProvider {

    	public Map collectStatistics(Connection conn);
    	/**
	 * @uml.property  name="metrics"
	 */
    	public Map getMetrics();
    	/**
	 * @uml.property  name="statsOutFile"
	 */
    	public File getStatsOutFile();
    	/**
	 * @uml.property  name="statGroup"
	 */
    	public String getStatGroup();
    	public String[] getEventsName();
    	public void writeStatsOnFile(Map valuesToWrite);
    	/**
	 * @uml.property  name="providerName"
	 */
    	public String getProviderName();
    	public void setStatsOutFile(String rootPath);
    	public void setFlushDataOnfile(boolean flushrowonfile);
    	public boolean validatePermissions(ConnectionProvider connProvider);
//	Map getStatus(Connection conn);
}
