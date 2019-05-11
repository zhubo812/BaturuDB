package org.bhu.db.mysql.utils;

public class UrlPojo implements Configure {
	private String usrname;
	private String password;
	private String ip;
	private String port;
	private String dbname;
	private String url;
	
	
	
	public String getUrl(){
		if(this.ip==null){
			System.err.println("input ip is null");
		}
		if(this.port==null){
			System.err.println("input is null. default port is 3306");
		}
		if(this.dbname==null){
			System.err.println("input dbname is null");
		}
		return String.format("jdbc:mysql://%s:%s/%s",this.ip,this.port,this.dbname);
	}
	
	public String getUsrname() {
		return usrname;
	}
	public void setUsrname(String usrname) {
		this.usrname = usrname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	
	
}
