package net.tc.data.db;

import java.lang.ref.SoftReference;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.*;


import net.tc.stresstool.config.Configuration;

import java.sql.Connection;

public class ConnectionProvider {
    private ConnectionInformation connInfo = null;
    private DataSource dataSource =null;

    public ConnectionProvider(Configuration configuration) {
		connInfo = new ConnectionInformation();
		
		connInfo.setConnUrl((String) configuration.getParameter("connUrl").getValue());
		connInfo.setDatabase((String)configuration.getParameter("database").getValue());
	    connInfo.setUser((String) configuration.getParameter("user").getValue());
	    connInfo.setPassword((String) configuration.getParameter("password").getValue());
	    connInfo.setDbType((String) configuration.getParameter("dbType").getValue());
	    connInfo.setConnParameters((String) configuration.getParameter("connParameters").getValue());
	    connInfo.setSelectForceAutocommitOff((Boolean)Boolean.parseBoolean((String)configuration.getParameter("selectForceAutocommitOff").getValue()));
	    if(configuration.getParameter("useConnectionPool").getValue()!=null)connInfo.setConnectionPool((Boolean)Boolean.parseBoolean((String)configuration.getParameter("useConnectionPool").getValue()));
	    if(configuration.getParameter("connectionPoolType").getValue()!=null)connInfo.setConnectionPoolType((Integer)Integer.parseInt((String)configuration.getParameter("connectionPoolType").getValue()));

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
    

    public Connection getHikariConnection()throws SQLException {
	if(this.connInfo != null){
		    Connection conn;
		    
		    SoftReference sf = null;
			if (this.getDataSource() !=null 
					&& (this.getDataSource() instanceof HikariDataSource)){
				sf = new SoftReference<Connection>( conn= (Connection) this.getDataSource().getConnection());
			}
			else{
				DataSource datasource;

			    HikariConfig config = new HikariConfig();
			    String connectionString = connInfo.getConnUrl()
			        	    +"/"+ connInfo.getDatabase()
			        	    +"?user="+connInfo.getUser()
			        	    +"&password="+ connInfo.getPassword()
			        	    +"&"+ connInfo.getConnParameters();
			    
		         
			     config.setJdbcUrl(connInfo.getConnUrl() + "/" + connInfo.getDatabase());
			     config.setUsername(connInfo.getUser());
			     config.setPassword(connInfo.getPassword());
			     config.setMaximumPoolSize(10);
			     config.setAutoCommit(false);
			     config.addDataSourceProperty("cachePrepStmts", "true");
			     config.addDataSourceProperty("prepStmtCacheSize", "250");
			     config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			     datasource = new HikariDataSource(config);
			    

			     this.setDataSource(datasource);
			    
//			    sf = new SoftReference<Connection>(conn= (Connection) DriverManager.getConnection(connectionString)); 
			    sf = new SoftReference<Connection>(conn= (Connection) datasource.getConnection());
			}
//		    conn= (Connection) DriverManager.getConnection(connectionString);
		    
		    return (Connection) sf.get();
	 }
	
	
	return null;
    }
    
    public Connection getSimpleConnection()throws SQLException {
	if(this.connInfo != null){
		SoftReference sf = null;
		Connection conn;
		if (this.getDataSource() !=null){
			sf = new SoftReference<Connection>( conn= (Connection) this.getDataSource().getConnection());
			if(connInfo.isSelectForceAutocommitOff()){
				((Connection) sf.get()).setAutoCommit(false);
			}
		}
		else{
		
			try{
				TCDataSource myDataSource =new TCDataSource();
					myDataSource.setType(ConnectionInformation.NONE);
				
			    String connectionString = connInfo.getConnUrl()
			        	    +"/"+ connInfo.getDatabase()
			        	    +"?user="+connInfo.getUser()
			        	    +"&password="+ connInfo.getPassword()
			        	    +"&"+ connInfo.getConnParameters();
			    
			    myDataSource.setUrl(connectionString);
			    this.setDataSource(myDataSource);
			    
			    sf = new SoftReference<Connection>(conn= (Connection) this.getDataSource().getConnection());
			}catch(Exception ex){ex.printStackTrace();}
			finally{
				if(connInfo.isSelectForceAutocommitOff()){
					((Connection) sf.get()).setAutoCommit(false);
				}
			}
		 }
		 return (Connection) sf.get();
	   }
		
		return null;
		   
	 }

//    public Connection getSimpleConnection()throws SQLException {
//	if(this.connInfo != null){
//		
//		    Connection conn;
//		    SoftReference sf = null;
//		    if(connInfo.getDbType()!= null &&  !connInfo.getDbType().toLowerCase().equals("MySQL".toLowerCase()))
//		    {
//		    	
//		    	conn=(Connection) DriverManager.getConnection((String)connInfo.getDbType(),"test", "test");
//		    }
//		    else{
//		    String connectionString = connInfo.getConnUrl()
//		        	    +"/"+ connInfo.getDatabase()
//		        	    +"?user="+connInfo.getUser()
//		        	    +"&password="+ connInfo.getPassword()
//		        	    +"&"+ connInfo.getConnParameters();
//		    
//		    sf = new SoftReference<Connection>(conn= (Connection) DriverManager.getConnection(connectionString));
////		    conn= (Connection) DriverManager.getConnection(connectionString);
//		    }
//		    return (Connection) sf.get();
//	 }
//
//	
//	return null;
//    }

    public Connection getConnection()throws SQLException {
    	if(this.connInfo != null){
    		if (!this.connInfo.isConnectionPool())
    			return this.getSimpleConnection();
    		else{
    			
    			 switch (connInfo.getConnectionPoolType()) {
    			 case ConnectionInformation.HIKARI: return this.getHikariConnection(); 
    			 
    			 default : return getSimpleConnection();
    			 }
    			
    		}
    	 }
    	
    	
    	return null;
        }
    
    public boolean returnConnection(Connection conn){
    	try{
    		if(conn!=null && !conn.isClosed()){
    			conn.commit();
    			conn.close();
    			conn = null;
    		}
    	}catch(SQLException ex){
    		ex.printStackTrace();
    	}
    	
    	return false;
    }

	private DataSource getDataSource() {
		return dataSource;
	}

	private void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
