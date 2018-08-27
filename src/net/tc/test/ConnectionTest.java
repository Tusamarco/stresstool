package net.tc.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.postgresql.*;

import net.tc.jsonparser.StructureDefinitionParserMySQL;
import net.tc.jsonparser.StructureDefinitionParserPostgres;

public class ConnectionTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dbType = "mysql";
		
		if(args.length>4 
				&& args[4] != null
				&& (args[4].equals("mysql") || args[4].equals("postgres") )
				){
			switch(args[4]){
			case "mysql":    testMySQL(args);    break;
			case "postgres": testPostgres(args);    break;
		    default:         testMySQL(args);    break;
			
			}
		}
		
		
			System.out.println("jdbc:mysql://192.168.1.11:6033 windmills app1 test"); 
			System.out.println("jdbc:mysql://192.168.1.83:5432 postgres postgres password postgres"); 

	}

	private static void testMySQL(String[] args) {
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

			System.exit(0);
			
			}
	}
	private static void testPostgres(String[] args) {
			
			try{
				
				Class.forName("org.postgresql.Driver");
				String url = args[0] +"/" + args[1];
				Properties props = new Properties();
				props.setProperty("user",args[2]);
				props.setProperty("password",args[3]);
				props.setProperty("ssl","false");
				Connection conn = DriverManager.getConnection(url, props);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM pg_stat_activity");
				System.out.print("datid \t datname \t pid \t usesysid \t username \t application_name \t\t client_addr \t client_port \t state \n");
				while(rs.next()){
					
					int id = rs.getInt("datid");
					String datname = rs.getString("datname")!=null?rs.getString("datname"):"\t";
					int pid  = rs.getInt("pid");
					int usesysid = rs.getInt("usesysid");
					String usename = rs.getString("usename")!=null?rs.getString("usename"):"\t";
					String application_name = rs.getString("application_name")!=null?rs.getString("application_name"):"\t";
					String client_addr = rs.getString("client_addr")!=null?rs.getString("client_addr"):"\t";
					int client_port = rs.getInt("client_port");
					String state = rs.getString("state")!=null?rs.getString("state"):"\t";
					System.out.print(id +" \t " 
					+ datname +" \t "
					+ pid + " \t " 
					+ usesysid + " \t\t "
					+ usename + " \t "
					+ application_name + " \t\t " 
					+ client_addr + " \t\t "
					+ client_port + " \t " 
					+ state + "\n");	
					
					

//* datid            | 13858
//datname          | postgres
//pid              | 13416
//usesysid         | 10
//usename          | postgres
//application_name | pgAdmin 4 - DB:postgres
//client_addr      | 192.168.1.33
//client_hostname  |
//client_port      | 62213
//backend_start    | 2018-08-27 09:12:47.906119-04
//xact_start       |
//query_start      | 2018-08-27 09:16:35.110359-04
//state_change     | 2018-08-27 09:16:35.111181-04
//wait_event_type  | Client
//wait_event       | ClientRead
//state            | idle
//backend_xid      |
//backend_xmin     |
//query            | /*pga4dash*/                                                +
//                 | SELECT                                                      +
//                 |    (SELECT sum(blks_read) FROM pg_stat_database) AS "Reads",+
//                 |    (SELECT sum(blks_hit) FROM pg_stat_database) AS "Hits"
//backend_type     | client backend
			
				}
			
				
				rs.close();
				stmt.close();
				conn.close();
			    
			}catch(Exception ex){ex.printStackTrace();}
			finally{

				System.exit(0);
			
			}
	}
}
