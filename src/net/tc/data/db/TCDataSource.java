package net.tc.data.db;

public class TCDataSource  extends com.mysql.cj.jdbc.MysqlDataSource{
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
