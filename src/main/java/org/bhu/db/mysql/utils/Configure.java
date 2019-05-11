package org.bhu.db.mysql.utils;

import java.text.SimpleDateFormat;

public interface Configure {
	final String LOG4J_CONF = "conf/log4j.properties";
	public static final String driver ="com.mysql.jdbc.Driver";
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
}
