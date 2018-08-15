package net.tc.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.mysql.cj.jdbc.MysqlDataSource;

public class ConnectionTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		
			Connection conn;
			
				try{
					MysqlDataSource myDataSource =new com.mysql.cj.jdbc.MysqlDataSource();
										
				    String connectionString = args[0]
				        	    +"/"+ args[1]
				        	    +"?user="+args[2]
				        	    +"&password="+ args[3]
				        	    +"&useSSL=false"
				        	    + (args.length > 4 ? "&"+ args[4]:"");
				    
				    myDataSource.setUrl(connectionString);
				    conn = myDataSource.getConnection();
				    if( conn != null && !conn.isClosed()){
				    	Statement stmt = conn.createStatement();
				    	ResultSet  rs = stmt.executeQuery("show global variables like 'hostname';");
				    	if(rs != null){
				    		while(rs.next()){
				    			
				    			String hostname = rs.getString(2);
				    			System.out.println("Testing connection using " + connectionString
				    					+ "\n Resulting connected to " + hostname
				    				);
				    			
				    		}
				    	}
				    	conn.close();
				    	conn = null;
				    }
				    
				    
				    
				}catch(Exception ex){ex.printStackTrace();}
				finally{

				
				
				}

	}

}
