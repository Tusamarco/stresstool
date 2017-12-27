package net.tc.data.db;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class TCDataSource  extends MysqlDataSource{
	int type =0 ;

	public TCDataSource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	

}
