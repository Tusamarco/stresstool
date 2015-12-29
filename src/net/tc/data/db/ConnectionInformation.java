package net.tc.data.db;


public class ConnectionInformation {
	private String connUrl= null;
	private String database=null;
	private String dbType = null;
	private String user = null; 	
	private String password = null;
	private String connParameters = null;  
	private boolean connectionPool=false ;
	private String connectionPoolClass= null; 
	private boolean selectForceAutocommitOff=false;
	private boolean stikyconnection=false ;
	/**
	 * @return the connUrl
	 */
	public String getConnUrl() {
	    return connUrl;
	}
	/**
	 * @param connUrl the connUrl to set
	 */
	public void setConnUrl(String connUrl) {
	    this.connUrl = connUrl;
	}
	/**
	 * @return the database
	 */
	public String getDatabase() {
	    return database;
	}
	/**
	 * @param database the database to set
	 */
	public void setDatabase(String database) {
	    this.database = database;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
	    return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
	    this.user = user;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
	    return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
	    this.password = password;
	}
	/**
	 * @return the connParameters
	 */
	public String getConnParameters() {
	    return connParameters;
	}
	/**
	 * @param connParameters the connParameters to set
	 */
	public void setConnParameters(String connParameters) {
	    this.connParameters = connParameters;
	}
	/**
	 * @return the connectionPool
	 */
	public boolean isConnectionPool() {
	    return connectionPool;
	}
	/**
	 * @param connectionPool the connectionPool to set
	 */
	public void setConnectionPool(boolean connectionPool) {
	    this.connectionPool = connectionPool;
	}
	/**
	 * @return the connectionPoolClass
	 */
	public String getConnectionPoolClass() {
	    return connectionPoolClass;
	}
	/**
	 * @param connectionPoolClass the connectionPoolClass to set
	 */
	public void setConnectionPoolClass(String connectionPoolClass) {
	    this.connectionPoolClass = connectionPoolClass;
	}
	/**
	 * @return the selectForceAutocommitOff
	 */
	public boolean isSelectForceAutocommitOff() {
	    return selectForceAutocommitOff;
	}
	/**
	 * @param selectForceAutocommitOff the selectForceAutocommitOff to set
	 */
	public void setSelectForceAutocommitOff(boolean selectForceAutocommitOff) {
	    this.selectForceAutocommitOff = selectForceAutocommitOff;
	}
	/**
	 * @return the stikyconnection
	 */
	public boolean isStikyconnection() {
	    return stikyconnection;
	}
	/**
	 * @param stikyconnection the stikyconnection to set
	 */
	public void setStikyconnection(boolean stikyconnection) {
	    this.stikyconnection = stikyconnection;
	}
	/**
	 * @return the dbType
	 */
	public String getDbType() {
	    return dbType;
	}
	/**
	 * @param dbType the dbType to set
	 */
	public void setDbType(String dbType) {
	    this.dbType = dbType;
	} 

}