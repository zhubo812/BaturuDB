package org.bhu.db.mysql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bhu.db.mysql.utils.Configure;
import org.bhu.db.mysql.utils.Instance;
import org.bhu.db.mysql.utils.UrlPojo;
import org.bhu.io.FileReader;
import org.bhu.io.FileWriter;
import org.bhu.nlp.utils.string.StringHelper;
import org.bhu.time.utils.TimeFormat;
import org.bhu.time.utils.TimeHelper;

import net.sf.json.JSONArray;





public class MysqlHelper implements Configure{
	
	private static Logger logger = Logger.getLogger(MysqlHelper.class);
	private TimeHelper th = new TimeHelper(TimeFormat.YYYY_MM_DD.getValue());
	private Connection conn;
	private Statement stmt;

	public MysqlHelper(UrlPojo projo){
		PropertyConfigurator.configure(LOG4J_CONF);
		try {
			initParam(projo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void initParam(UrlPojo projo) throws Exception {
		Class.forName(driver);
		this.conn = (Connection) DriverManager.getConnection(projo.getUrl(),projo.getUsrname(),projo.getPassword());
		this.stmt = (Statement) conn.createStatement();
	}
	
	public List<String> getTables() {
		List<String> list = new ArrayList<String>();
		try {
			DatabaseMetaData meta = null;
			meta = (DatabaseMetaData) conn.getMetaData();
			ResultSet mrs = meta.getTables(null, null, null, null);
			while (mrs.next()) {
				// 获取所有表的名称。。。
				String tableName = mrs.getString(3);
				list.add(tableName);
			}
			mrs.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return list;
	}
	
	
//	public List<String> 
	
	
	public void checkTable(String tbName) {
		List<String> tbList = getTables();
		if (!tbList.contains(tbName)) {
			CreateTable(tbName);
		}
	}
	
	public void CreateTable(String tableName) {
		String sql = "CREATE TABLE " + tableName + " ( `id` int(11) NOT NULL AUTO_INCREMENT,"
				+ "  `status` int(11) DEFAULT NULL," + "  `title` char(255) DEFAULT NULL,"
				+ "  `source` char(255) DEFAULT NULL," + "  `type` char(255) DEFAULT NULL,"
				+ "  `keywords` char(255) DEFAULT NULL," + "  `content` longtext," + "  `url` char(255) DEFAULT NULL,"
				+ "  `date` date DEFAULT NULL," + "  `CommenNumber` int(11) DEFAULT NULL," + "  PRIMARY KEY (`id`)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
		try {
			createTable(sql);
		} catch (Exception e) {
			System.out.println("Creating table failed!");
			e.printStackTrace();
		}
		System.out.println("Creating table successed!");
	}
	
	// <!-------------------------创建表------------------------->
		/****
		 * 创建表
		 * 
		 * @param sql
		 * @throws Exception
		 */
		private void createTable(String sql) throws Exception {
			System.out.println(sql);
			stmt = (Statement) conn.createStatement();
			stmt.executeUpdate(sql);
		}
	
	// <!-------------------------数据库操作------------------------->
		public ResultSet getResultSet(String sql) {
			ResultSet rs = null;
			try {
				rs = stmt.executeQuery(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return rs;
		}

		private boolean executeSQL(String sql) {
			try {
				stmt = (Statement) conn.createStatement();
				stmt.execute(sql);
			} catch (SQLException e) {
				System.out.println(sql);
				logger.error(sql);
				logger.error(e.toString());
				// e.printStackTrace();
				return false;
			}
			return true;
		}

		public boolean executePreparedStatement(PreparedStatement pst) {
			try {
				pst.executeUpdate();
				// pst.execute();
				if (pst != null)
					pst.close();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		}

		public boolean executeBatch(PreparedStatement pst) {
			try {
				pst.executeBatch();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		}

		public PreparedStatement getPrepareStatement(String sql) {
			try {
				return this.conn.prepareStatement(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}

		public void setStrofPrepareStatement(PreparedStatement pst, int i, String str) {
			try {
				pst.setString(i, str);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void setIntofPrepareStatement(PreparedStatement pst, int i, int num) {
			try {
				pst.setInt(i, num);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// <!-------------------------数据库操作END------------------------->
	
		/**
		 * 统计语料的汉字数
		 */
		public void getChineseCharNum(String tbName) {
			String sql = String.format("select * from %s ", tbName);
			int count = 0;
			ResultSet rs = getResultSet(sql);
			try {
				while (rs.next()) {
					int id = rs.getInt("id");
					String title = rs.getString("title");
					String content = rs.getString("content");
					String date = rs.getString("date");
					if (content == null || content.length() == 0 || title == null || title.length() == 0) {
						continue;
					}
					printProcessInfo(tbName, "news", id, 777, date);
					for (int i = 0; i < title.length(); i++) {
						if (StringHelper.isChinese(title.charAt(i))) {
							count++;
						} else {
							continue;
						}
					}
					for (int i = 0; i < content.length(); i++) {
						if (StringHelper.isChinese(content.charAt(i))) {
							count++;
						} else {
							continue;
						}
					}
					System.out.println("已有汉字数：\t" + count);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// <!-------------------------打印信息------------------------->
		/***
		 * 打印正在处理的信息
		 */
		public void printProcessInfo(String tbName, String kind, int id, int status, String date) {
			System.out.println(
					String.format("%s\t%s:%s\t%s\t%s\t" + format.format(new Date()), tbName, kind, id, status, date));
		}

		// <!-------------------------打印信息END------------------------->

		public void DuplicationProcess(String tbName, String... dates) {
			System.out.println(tbName);
			System.out.println(tbName);
			System.out.println(tbName);
			String sql = null;
			ResultSet rs = null;
			List<String> datelist = new ArrayList<String>();
			if (dates.length == 0) {
				sql = String.format("SELECT DISTINCT date FROM %s", tbName);
				rs = getResultSet(sql);
				try {
					while (rs.next()) {
						String date = rs.getString("date");
						datelist.add(date);
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} else {
				datelist = Arrays.asList(dates);
			}
			for (String date : datelist) {
				sql = String.format("select id, url from %s where status != 9 and date = '%s'", tbName, date);
				rs = getResultSet(sql);
				List<String> list = new ArrayList<String>();
				try {
					while (rs.next()) {
						String url = rs.getString("url");
						int id = rs.getInt("id");
						if (list.contains(url)) {
							sql = String.format("update %s set status='%s' where id=%s", tbName, 9, id);
							executeSQL(sql);
							System.out.println("duplicated record: " + id + "\t" + url);
						} else {
							// System.out.println(id);
							list.add(url);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
/*
		public boolean insert(String site, List<Instance> list, String date) {
			String tbName = String.format("%snews", site);
			checkTable(tbName);
			System.out.println(String.format("%s insert %s news number:\t%s", date, site, list.size()));
			String sql = String.format("insert into %snews (url,status,date,type) values (?,?,?,?)", site);
			AutoCommit(false);
			PreparedStatement ps = getPrepareStatement(sql);
			try {
				ps.clearBatch();
				for (Instance inst : list) {
					if (inst.getUrl().equals("") || inst.getUrl() == null)
						continue;
					ps.setString(1, inst.getUrl());
					// setStrofPrepareStatement(ps,1, inst.getUrl());
					ps.setInt(2, inst.getStatus());
					ps.setString(3, inst.getDate());
					ps.setString(4, inst.getChannel());
					ps.addBatch();
				}
			} catch (SQLException e) {
				// e.printStackTrace();
				UpdateRecord(site, date, -1);
				return false;
			}
			if (!executeBatch(ps)) {
				UpdateRecord(site, date, -1);
				return false;
			}
			Commit();
			ClosePreparedStatement(ps);
			// close();
			AutoCommit(true);
			UpdateRecord(site, date, 1);
			return true;
		}
*/
		public boolean insert(String site, List<Instance> list) {
			String tbName = String.format("%snews", site);
			checkTable(tbName);
			System.out.println(String.format("%s\tnews number:\t%s", th.GetDate(), list.size()));
			if (list.size() == 0)
				return true;
			if (InsertBatch(site, list)) {
				return true;
			}
			return false;
		}

		public boolean InsertBatch(String site, List<Instance> list, String date) {
			String sql = String.format("insert into %snews (url,status,date,type) values (?,?,?,?)", site);
			AutoCommit(false);
			PreparedStatement ps = getPrepareStatement(sql);
			try {
				ps.clearBatch();
				for (Instance inst : list) {
					ps.setString(1, inst.getUrl());
					ps.setInt(2, inst.getStatus());
					ps.setString(3, inst.getDate());
					ps.setString(4, inst.getChannel());
					ps.addBatch();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			executeBatch(ps);
			Commit();
			ClosePreparedStatement(ps);
			// close();
			AutoCommit(true);
			return true;
		}

		public boolean InsertBatch(String site, List<Instance> list) {
			String sql = String.format("insert into %snews (url,status,date,type) values (?,?,?,?)", site);
			AutoCommit(false);
			PreparedStatement ps = getPrepareStatement(sql);
			try {
				ps.clearBatch();
				for (Instance inst : list) {
					ps.setString(1, inst.getUrl());
					// setStrofPrepareStatement(ps,1, inst.getUrl());
					ps.setInt(2, inst.getStatus());
					ps.setString(3, inst.getDate());
					ps.setString(4, inst.getChannel());
					ps.addBatch();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			executeBatch(ps);
			Commit();
			ClosePreparedStatement(ps);
			// close();
			AutoCommit(true);
			return true;
		}

		public Date String2Date(String dateStr, String formatStr) {
			SimpleDateFormat dd = new SimpleDateFormat(formatStr);
			Date date = null;
			try {
				date = dd.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return date;
		}

		public java.sql.Date String2SQLDate(String dateStr, String formatStr) {
			SimpleDateFormat df = new SimpleDateFormat(formatStr);
			java.sql.Date SQLDate = null;
			try {
				Date date = df.parse(dateStr);
				SQLDate = new java.sql.Date(date.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return SQLDate;
		}

		public String GetLinkInsertSQL(String tbName, String link, int status, String date, String type) {
			return String.format("insert into %s (url, status, date, type) values ('%s', '%s', '%s', '%s')", tbName, link,
					status, date, type);
		}

		public List<String> GetLinksByDate(String tbName, String date) {
			String sql = String.format("select * from %s where date = '%s'", tbName, date);
			List<String> links = new ArrayList<String>();
			ResultSet rs = getResultSet(sql);
			try {
				while (rs.next()) {
					String url = rs.getString("url");
					links.add(url);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return links;
		}

		public void close() {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		private void AutoCommit(boolean bool) {
			try {
				conn.setAutoCommit(bool);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		private void Commit() {
			try {
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		private void ClosePreparedStatement(PreparedStatement ps) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// //////////////////////////////

		public List<String> GetDataBaseDates(String year, List<String> undoneDateList, String site) {
			List<String> dateList = new ArrayList<String>();

			String sql = String.format("select * from tb%s%s;", year, site);
			ResultSet rs = getResultSet(sql);
			try {
				while (rs.next()) {
					String date = rs.getString("date");
					int status = rs.getInt("status");
					if (status == 1) {
						dateList.add(date);
					} else {
						undoneDateList.add(date);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return dateList;
		}
	
	
		// <----读取具体网站爬取记录------------------------------>
	/*	private List<String> GetTaskDateListFromRecordTable(String site) {
			List<String> tableNames = getTables();
			List<String> TaskDateList = new ArrayList<String>();
			for (String tableName : tableNames) {
				if (tableName.equals(site)) {
					List<String> records = GetDataBaseDates(tableName);
					TaskDateList = TimeUtils.getDates(TimeUtils.GetAfterOneDate(records.get(records.size() - 1)),
							TimeUtils.getLastOneDate());
				}
			}
			return TaskDateList;
		}

		public List<String> GetTaskDateList(String site) {
			return GetTaskDateListFromRecordTable(site);
		}

		public void UpdateRecord(String tbName, String date, int value) {
			String sql = String.format("insert into %s (date, status) values('%s','%s')", tbName, date, value);
			executeSQL(sql);
		}

		public void UpdateNews(Analysis analyzer, String site) {
			String tbName = CrawlerUtil.GetTableName(site);
			String sql = String
					.format("select url,id,date,source,type from %s where status=-1  order by id desc limit 20000", tbName);
			// String sql = String
			// .format("select url,id,date,source,type from %s where status=-1 and
			// id >5000000 limit 20000 ",
			// tbName);
			ResultSet rs = getResultSet(sql);
			try {
				while (rs.next()) {
					String url = rs.getString("url");
					int id = rs.getInt("id");
					String date = rs.getString("date");
					String source = rs.getString("source");
					String type = rs.getString("type");
					Instance instance = new Instance();
					instance.setUrl(url);
					instance.setChannel(type);
					instance.setNewsID(id);

					analyzer.GetPageInfo(instance);
					printProcessInfo(site, "news", id, instance.getStatus(), date);
					if (!CheckStatus(instance, tbName))
						continue;

					// if (!updateNews(instance, tbName, id)) {
					if (!updateNews(instance, SQLUtils.GetUpdateNewSQL(tbName))) {
						String path = String.format("%s/%s/", tbName, date);
						java.io.File file = new java.io.File(path);
						if (!file.exists()) {
							file.mkdirs();
						}
						String filePath = path + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".")) + ".txt";
						FileWriter writer = new FileWriter(filePath, "gbk");
						writer.write("title:" + instance.getTitle() + "\r\n" + "source:" + source + "\r\n" + "url:" + url
								+ "\r\n" + "\r\n" + "content:");
						writer.writeLine(instance.getContent());
						writer.close();
						continue;
					} else {
						UpdateNewsTableStatus(tbName, instance);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		private void UpdateNewsTableStatus(String tbName, Instance instance) throws SQLException {
			String sql = String.format("UPDATE %s SET status = %s WHERE id=%s;", tbName, instance.getStatus(),
					instance.getNewsID());
			executeSQL(sql);
		}

		private boolean updateNews(Instance instance, String sql) {
			PreparedStatement pst = getPrepareStatement(sql);
			try {
				pst.setString(1, instance.getTitle());
				pst.setString(2, instance.getSource());
				pst.setString(3, instance.getContent());
				pst.setString(4, instance.getKeywords());
				pst.setString(5, instance.getChannel());
				pst.setInt(6, instance.getWordcount());
				pst.setInt(7, instance.getNewsID());

				pst.executeUpdate();

				ClosePreparedStatement(pst);
			} catch (SQLException e) {
				logger.error(e.toString());
				if (e.toString().indexOf("Incorrect string value") > -1) {
					ContentCleaner(instance);
					updateNews(instance, sql);
				} else {
					return false;
				}
			}
			return true;
		}

		public void Records2File(String tbName) {
			String sql = String.format("select * from %s where status!=9", tbName);
			ResultSet rs = getResultSet(sql);
			FileWriter writer = new FileWriter("peopleurl");
			List<String> list = new ArrayList<String>();
			try {
				while (rs.next()) {
					String url = rs.getString("url");
					String date = rs.getString("date");
					System.out.println(rs.getInt("id"));
					if (!list.contains(url)) {
						writer.writeLine(url + "\t" + date);
						list.add(url);
					} else {
						System.out.println(rs.getInt("id") + "\t" + url + "\t" + date);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			writer.close();
		}

		public void ExportNews(String tbName, List<String> dates) {
			FileReader reader = new FileReader("Properties/newspaperlist");
			List<String> newslist = reader.read2List();
			int num = 0;
			for (String date : dates) {
				String sql = String.format("select * from %s where (status=1 or status=2 or status=3) and date='%s' ",
						tbName, date);
				if(tbName.equals(CrawlerUtil.GetTableName(SOHU))){
				sql += "and (";
				for (int i = 0; i < newslist.size() - 1; i++) {
					sql += String.format("source like '%s' OR ", newslist.get(i));
				}
				sql += String.format("source like '%s' ", newslist.get(newslist.size() - 1)) + ")";
				}
				ResultSet rs = getResultSet(sql);
				int counter = 0;

				System.out.println(tbName + "\t" + date);
				try {
					while (rs.next()) {
						counter++;
						String title = rs.getString("title");
						String content = rs.getString("content").replace("\n", "\r\n");
						String source = rs.getString("source");
						int wnumber = rs.getInt("wnumber");
						if (!tbName.equals(GetTableName(NETEASE))
								&&(source.indexOf("报") < 0 || (tbName.equals(SOHU + "news"))
										&& counter % 2 == 0))
							continue;
						String url = rs.getString("url");
						// String date = rs.getString("date");
						String type = rs.getString("type");
						String keywords = rs.getString("keywords");
						// System.out.println(rs.getInt("id"));
						String path = String.format("news_data/%s/%s/", tbName, date);
						java.io.File file = new java.io.File(path);
						if (!file.exists()) {
							file.mkdirs();
						}
						if (content == null || content.trim().length() == 0)
							continue;
						String filePath = null;
						try {
							filePath = path + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".")) + ".txt";
						} catch (Exception e1) {
							logger.error(filePath);
							logger.error(e1.toString());
							continue;
							// e1.printStackTrace();
						}
						num += wnumber;
						FileWriter writer = new FileWriter(filePath, "gbk");
						writer.write("title:" + title + "\r\n" + "source:" + source + "\r\n" + "channel:" + type + "\r\n"
								+ "keywords:" + keywords + "\r\n" + "url:" + url + "\r\n" + "\r\n" + "content:");

						writer.write(content.replace("\n", "\r\n"));

						writer.close();
					}
					System.out.println(num);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		public void ExportTVBroadcast(String tbName, List<String> dates) {
			for (String date : dates) {
				String sql = String.format(
						"SELECT title, content,source,url ,type, keywords FROM %s WHERE date='%s' and (source LIKE '%s' OR source LIKE '%s' OR source LIKE '%s')",
						tbName, date, "%广播%", "%电视%", "%电台%");
				// System.out.println(sql);
				ResultSet rs = getResultSet(sql);
				System.out.println(tbName + "\t" + date);
				try {
					while (rs.next()) {
						String title = rs.getString("title");
						String content = rs.getString("content").replace("\n", "\r\n");
						String source = rs.getString("source");
						// if(source.indexOf("报")<0)continue;
						String url = rs.getString("url");
						// String date = rs.getString("date");
						String type = rs.getString("type");
						String keywords = rs.getString("keywords");
						// System.out.println(rs.getInt("id"));
						String path = String.format("BTV_data/%s/%s/", tbName, date);
						java.io.File file = new java.io.File(path);
						if (!file.exists()) {
							file.mkdirs();
						}
						if (content == null || content.trim().length() == 0)
							continue;
						String filePath = null;
						try {
							filePath = path + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".")) + ".txt";
						} catch (Exception e1) {
							logger.error(filePath);
							logger.error(e1.toString());
							continue;
							// e1.printStackTrace();
						}

						FileWriter writer = new FileWriter(filePath, "gbk");
						writer.write("title:" + title + "\r\n" + "source:" + source + "\r\n" + "channel:" + type + "\r\n"
								+ "keywords:" + keywords + "\r\n" + "url:" + url + "\r\n" + "\r\n" + "content:");

						writer.write(content.replace("\n", "\r\n"));

						writer.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	
		// <!-------------------------Comment------------------------->

		private boolean CheckStatus(Instance instance, String tbName) {
			switch (instance.getStatus()) {
			case -1:
				return false;
			case 403:
				UpdateStatus(tbName, 403, instance.getNewsID());
				return false;
			case 404:
				UpdateStatus(tbName, 404, instance.getNewsID());
				return false;
			default:
				return true;
			}
		}

		public void UpdateStatus(String tbName, int status, int id) {
			String sql = String.format("UPDATE %s SET status=%s WHERE id=%s", tbName, status, id);
			executeSQL(sql);
		}

		public void UpdateStatus(String tbName, int status, int id, int cmntNum) {
			String sql = String.format("UPDATE %s SET status=%s, CommenNumber=%s WHERE id=%s", tbName, status, cmntNum, id);
			executeSQL(sql);
		}

		public Instance GetInstance4Comment(ResultSet rs) throws SQLException {
			Instance instance = new Instance();
			instance.setNewsID(rs.getInt("id"));
			instance.setUrl(rs.getString("url"));
			instance.setTitle(rs.getString("title"));
			instance.setStatus(rs.getInt("status"));
			instance.setDate(rs.getString("date"));
			return instance;
		}

		public boolean GetComment(Commentable analyzer, String site, Instance instance) {
			analyzer.GetComment(instance);
			// CMNTWriter(site,instance);// 将评论信息写入本地文件
			if (instance.getStatus() != 3)
				return false;
			return true;
		}

		public void GetComment(Commentable analyzer, String site) {
			mdb = new MongoDBUtils("NewsComment", site);
			// int newsID = mutils.getLastID();
			String tbName = CrawlerUtil.GetTableName(site);
			String sql = String// and date >'2014-03-30'
					.format("select id, title, status, url, date from %s WHERE status=2 and date <='2015-04-30' limit 10000",
							tbName);
			ResultSet rs = getResultSet(sql);

			try {
				while (rs.next()) {
					Instance instance = GetInstance4Comment(rs);
					printProcessInfo(site, "comment", instance.getNewsID(), instance.getStatus(), instance.getDate());
					// if(mutils.contains(instance))continue;
					if (GetComment(analyzer, site, instance)) {
						if (instance.getCommentcount() > 20000) {
							@SuppressWarnings("unchecked")
							List<List<Object>> list = createList(instance.getComment(), 20000);
							for (List<Object> o : list) {
								JSONArray j = JSONArray.fromObject(o);

								BasicDBObject query = new BasicDBObject();
								query.put("NewsID", instance.getNewsID());
								query.put("date", TimeUtils.getMongoDate(instance.getDate()));
								query.put("size", j.size());
								query.put("CommentInfo", j);
					
								mdb.insert(query);
							}

						} else {
							if (instance.getCommentcount() > 0) {
								BasicDBObject query = new BasicDBObject();
								query.put("NewsID", instance.getNewsID());
								query.put("date", TimeUtils.getMongoDate(instance.getDate()));
								query.put("size", instance.getComment().size());
								query.put("CommentInfo", instance.getComment());
								mdb.insert(query);
							}
						}
						if (instance.getStatus() == 3) {
							UpdateStatus(tbName, 3, instance.getNewsID(), instance.getCommentcount());
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// <!-------------------------CommentEnd------------------------->

		private void ContentCleaner(Instance instance) {
			String text = instance.getContent();
			text = CharUtils.filterEmoji(text);
			instance.setContent(text);
		}
		
		public static String GetTableName(String site) {
			return String.format("%snews", site);
		}
	*/
}
