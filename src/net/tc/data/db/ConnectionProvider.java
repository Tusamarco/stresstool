package net.tc.data.db;

import java.sql.DriverManager;
import java.sql.SQLException;

import net.tc.stresstool.config.Configuration;

import com.mysql.jdbc.Connection;

public class ConnectionProvider {
    private ConnectionInformation connInfo = null;

    public ConnectionProvider(Configuration configuration) {
	connInfo = new ConnectionInformation();
	
	connInfo.setConnUrl((String) configuration.getParameter("connUrl").getValue());
	connInfo.setDatabase((String)configuration.getParameter("database").getValue());
        connInfo.setUser((String) configuration.getParameter("user").getValue());
        connInfo.setPassword((String) configuration.getParameter("password").getValue());
        connInfo.setDbType((String) configuration.getParameter("dbType").getValue());
        connInfo.setConnParameters((String) configuration.getParameter("connParameters").getValue());

    }

    /**
     * @return the conInfo
     */
    public ConnectionInformation getConnInfo() {
        return connInfo;
    }

    /**
     * @param conInfo the conInfo to set
     */
    public void setConnInfo(ConnectionInformation connInfo) {
        this.connInfo = connInfo;
    }
    
    //TODO implement support for connection pool
    public void createConnectionPool(){
	
	
    }
    

    public Connection getSimpleConnection()throws SQLException {
	if(this.connInfo != null){
		    Connection conn;
		    if(connInfo.getDbType()!= null &&  !connInfo.getDbType().toLowerCase().equals("MySQL".toLowerCase()))
		    {
		    	conn=(Connection) DriverManager.getConnection((String)connInfo.getDbType(),"test", "test");
		    }
		    else{
		    String connectionString = connInfo.getConnUrl()
		        	    +"/"+ connInfo.getDatabase()
		        	    +"?user="+connInfo.getUser()
		        	    +"&password="+ connInfo.getPassword()
		        	    +"&"+ connInfo.getConnParameters();
		    
		    
		    conn= (Connection) DriverManager.getConnection(connectionString);
		    }
		    return conn;
	 }
	
	
	return null;
    }
}