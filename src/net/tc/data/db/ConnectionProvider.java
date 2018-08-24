package net.tc.data.db;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.ref.SoftReference;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.*;

import net.tc.stresstool.StressTool;
import net.tc.stresstool.config.Configuration;
import net.tc.stresstool.config.Configurator;
import net.tc.stresstool.exceptions.StressToolException;
import net.tc.stresstool.logs.LogProvider;

import java.sql.Connection;

public class ConnectionProvider {
    private ConnectionInformation connInfo = null;
    private DataSource dataSource =null;
    private Configuration configuration =null;
    private Configurator config = null;

    public ConnectionProvider(Configurator configIn) {
    	config = configIn; 
    	configuration = getConfiguration(Configurator.MAIN_SECTION_NAME, StressTool.class);
		connInfo = new ConnectionInformation();
		
		connInfo.setConnUrl((String) configuration.getParameter("connUrl").getValue());
		connInfo.setDatabase((String)configuration.getParameter("database").getValue());
	    connInfo.setUser((String) configuration.getParameter("user").getValue());
	    connInfo.setPassword(configuration.getParameter("password")!=null?(String) configuration.getParameter("password").getValue():null);
	    connInfo.setDbType((String) configuration.getParameter("dbTypeName").getValue());
	    connInfo.setConnParameters((String) configuration.getParameter("connParameters").getValue());
	    connInfo.setSelectForceAutocommitOff((Boolean)Boolean.parseBoolean((String)configuration.getParameter("selectForceAutocommitOff").getValue()));
	    if(configuration.getParameter("useConnectionPool").getValue()!=null)connInfo.setConnectionPool((Boolean)Boolean.parseBoolean((String)configuration.getParameter("useConnectionPool").getValue()));
	    if(configuration.getParameter("connectionPoolType").getValue()!=null)connInfo.setConnectionPoolType((Integer)Integer.parseInt((String)configuration.getParameter("connectionPoolType").getValue()));

    }

	private Configuration getConfiguration(String section, Class classObj) {
		try {
			return config.getConfiguration(section, classObj);
		} catch (StressToolException e) {
			try{					
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);				
				e.printStackTrace(ps);
				String s =new String(baos.toByteArray());
				StressTool.getLogProvider().getLogger(LogProvider.LOG_ACTIONS).error(s);
				System.exit(1)  ;}catch(Exception ex){ex.printStackTrace();}
				
		}
		return null;
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

				Configuration hikariConf = this.getConfiguration("com.zaxxer.hikari", StressTool.class);
			    HikariConfig config = new HikariConfig();
			    String connectionString = connInfo.getConnUrl()
			        	    +"/"+ connInfo.getDatabase()
			        	    +"?user="+connInfo.getUser()
			           	    +"&useSSL=false"
			        	    +"&"+ connInfo.getConnParameters();
			    
			    connectionString =  connInfo.getPassword()!=null?connectionString + "&password="+ connInfo.getPassword():connectionString +"";
		         
			     config.setJdbcUrl(connInfo.getConnUrl() + "/" + connInfo.getDatabase());
			     config.setUsername(connInfo.getUser());
			     config.setPassword(connInfo.getPassword());
//			     config.setMaximumPoolSize(200);
//			     config.setAutoCommit(false);
////			     config.setLeakDetectionThreshold(500);
//			     config.addDataSourceProperty("cachePrepStmts", "true");
//			     config.addDataSourceProperty("prepStmtCacheSize", "250");
//			     config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//			     config.addDataSourceProperty("useSSL", "false");
//			     config.setIdleTimeout(1000);

			     config.setMaximumPoolSize(Integer.parseInt((String) hikariConf.getParameter("maximumPoolSize").getValue()));
			     config.setAutoCommit(Boolean.parseBoolean((String) hikariConf.getParameter("autoCommits").getValue()));
			     config.setLeakDetectionThreshold(Long.parseLong((String) hikariConf.getParameter("leakDetectionThreshold").getValue()));
			     config.addDataSourceProperty("useServerPrepStmts",(String) hikariConf.getParameter("useServerPrepStmts").getValue());
			     config.addDataSourceProperty("cachePrepStmts", (String) hikariConf.getParameter("cachePrepStmts").getValue());
			     config.addDataSourceProperty("prepStmtCacheSize",(String)hikariConf.getParameter("prepStmtCacheSize").getValue()  );
			     config.addDataSourceProperty("prepStmtCacheSqlLimit",(String)hikariConf.getParameter("prepStmtCacheSqlLimit").getValue());
			     config.addDataSourceProperty("useSSL",(String) hikariConf.getParameter("useSSL").getValue());
			     
			     config.addDataSourceProperty("rewriteBatchedStatements",(String) hikariConf.getParameter("rewriteBatchedStatements").getValue());
			     config.addDataSourceProperty("cacheResultSetMetadata",(String) hikariConf.getParameter("cacheResultSetMetadata").getValue());
			     config.addDataSourceProperty("cacheServerConfiguration",(String) hikariConf.getParameter("cacheServerConfiguration").getValue());
			     config.addDataSourceProperty("maintainTimeStats",(String) hikariConf.getParameter("maintainTimeStats").getValue());
			     config.setIdleTimeout(Integer.parseInt((String) hikariConf.getParameter("idleTimeout").getValue()));
		     
			     
			     
		    /*  cachePrepStmts=true
			    prepStmtCacheSize=250
			    prepStmtCacheSqlLimit=2048
			    leakDetectionThreshold=1000
			    maximumPoolSize = 50
			    useServerPrepStmts=true
			    useLocalSessionState=true
			    useLocalTransactionState=true
			    rewriteBatchedStatements=true
			    cacheResultSetMetadata=true
			    cacheServerConfiguration=true
			    autoCommits=true
			    maintainTimeStats=false
			    useSSL=false
			    idleTimeout=100
			    */ 
			     
			     
			     
			     
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
		if (this.getDataSource() !=null ){
			sf = new SoftReference<Connection>( conn= (Connection) this.getDataSource().getConnection());
//			if(connInfo.isSelectForceAutocommitOff()){
//				((Connection) sf.get()).setAutoCommit(false);
//			}

		}
		else{
		
			try{
				TCDataSource myDataSource =new TCDataSource();
					myDataSource.setType(ConnectionInformation.NONE);
				
			    String connectionString = connInfo.getConnUrl()
			        	    +"/"+ connInfo.getDatabase()
			        	    +"?user="+connInfo.getUser()
			        	    +"&useSSL=false"
			        	    +"&"+ connInfo.getConnParameters();
			    
			    connectionString =  connInfo.getPassword()!=null?connectionString + "&password="+ connInfo.getPassword():connectionString +"";
			    
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
    			if(!conn.getAutoCommit())
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
