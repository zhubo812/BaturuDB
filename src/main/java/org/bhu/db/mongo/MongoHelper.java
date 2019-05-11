package org.bhu.db.mongo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

import net.sf.json.JSONObject;

public class MongoHelper {

	private Mongo mg = null;
	private DB db;
	private DBCollection collection;
	private static final String LOG4J_CONF = "conf/log4j.properties";
	static Logger logger = Logger.getLogger(MongoHelper.class);

	public MongoHelper(String ip) {
		init(ip);
		PropertyConfigurator.configure(LOG4J_CONF);
	}

	public MongoHelper(String ip, int port) {
		init(ip, port);
		PropertyConfigurator.configure(LOG4J_CONF);
	}

	public MongoHelper(String ip, String database, String collection) {
		init(ip, database, collection);
		PropertyConfigurator.configure(LOG4J_CONF);
	}

	public MongoHelper(String ip, int port, String database, String collection) {
		init(ip, port,database, collection);
		PropertyConfigurator.configure(LOG4J_CONF);
	}

	public MongoHelper(String ip, String database) {
		init(ip, database);
		PropertyConfigurator.configure(LOG4J_CONF);
	}
	
	public MongoHelper(String ip,int port, String database) {
		init(ip,port, database);
		PropertyConfigurator.configure(LOG4J_CONF);
	}

	@SuppressWarnings("deprecation")
	private void init(String ip, String database) {
		try {
			mg = new Mongo(ip, 27017);
		} catch (MongoException e) {
			e.printStackTrace();
		}
		// 获取temp DB；如果默认没有创建，mongodb会自动创建
		db = mg.getDB(database);
	}

	@SuppressWarnings("deprecation")
	private void init(String ip, int port, String database) {
		try {
			mg = new Mongo(ip, port);
		} catch (MongoException e) {
			e.printStackTrace();
		}
		// 获取temp DB；如果默认没有创建，mongodb会自动创建
		db = mg.getDB(database);
	}
	@SuppressWarnings("deprecation")
	private void init(String ip, String database, String collection) {
		try {
			mg = new Mongo(ip, 27017);
		} catch (MongoException e) {
			e.printStackTrace();
		}
		db = mg.getDB(database);
		getCollection(collection);
	}

	@SuppressWarnings("deprecation")
	private void init(String ip, int port,String database, String collection) {
		try {
			mg = new Mongo(ip, port);
		} catch (MongoException e) {
			e.printStackTrace();
		}
		db = mg.getDB(database);
		getCollection(collection);
	}
	@SuppressWarnings("deprecation")
	private void init(String ip) {
		try {
			mg = new Mongo(ip, 27017);
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private void init(String ip, int port) {
		try {
			mg = new Mongo(ip, port);
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取数据库实例
	 * 
	 * @param Database
	 */
	@SuppressWarnings("deprecation")
	public void getDB(String Database) {
		db = mg.getDB(Database);
	}

	/***
	 * 获取数据集（表）实例 如果默认没有创建，mongodb会自动创建
	 * 
	 * @param collectionName
	 */
	public void getCollection(String collectionName) {
		collection = db.getCollection(collectionName);
	}

	public void insert(DBObject dbObject) {
		try {
			collection.insert(dbObject);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.toString());
		}
	}

	public void insert(List<DBObject> list) {
		for (DBObject item : list) {
			try {
				collection.insert(item);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.toString());
			}
		}
	}

	public void insert(BasicDBList list) {
		for (int i = 0; i < list.size(); i++) {
			DBObject item = (DBObject) list.get(i);
			try {
				collection.insert(item);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.toString());
			}
		}
	}

	public void insert(JSONObject jb) {
		DBObject dbObject = (DBObject) JSON.parse(jb.toString());
		collection.insert(dbObject);
	}
	
	public DBObject findOne(DBObject query) {
		return collection.findOne(query);
	}

	public DBObject findOne(DBObject query, String... columns) {
		BasicDBObject columnDB = new BasicDBObject();// 指定需要显示列
		for (String column : columns) {
			columnDB.put(column, 1);
		}
		return collection.findOne(query, columnDB);
	}

	public DBObject findOne(JSONObject jb, String... columns) {
		BasicDBObject query = (BasicDBObject) JSON.parse(jb.toString());
		BasicDBObject columnDB = new BasicDBObject();// 指定需要显示列

		for (String column : columns) {
			columnDB.put(column, 1);
		}
		return collection.findOne(query, columnDB);
	}

	public DBObject findOne() {
		return collection.findOne();
	}

	public List<DBObject> find(DBObject query, List<String> columns) {
		List<DBObject> list = new ArrayList<DBObject>();
		BasicDBObject columnDB = new BasicDBObject();// 指定需要显示列
		for (String column : columns) {
			columnDB.put(column, 1);
		}
		DBCursor cursor = collection.find(query, columnDB);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public List<DBObject> find(DBObject query, String... columns) {
		List<DBObject> list = new ArrayList<DBObject>();
		BasicDBObject columnDB = new BasicDBObject();// 指定需要显示列
		for (String column : columns) {
			columnDB.put(column, 1);
		}
		DBCursor cursor = collection.find(query, columnDB);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public List<DBObject> find(DBObject query, String keyword, String column, int page, int pageSize) {
		List<DBObject> list = new ArrayList<DBObject>();
		Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
		query.put(column, pattern);

		DBCursor cursor = collection.find(query).skip(page * pageSize).limit(pageSize);

		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public List<DBObject> find(DBObject query, String keyword, String column) {
		List<DBObject> list = new ArrayList<DBObject>();
		Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
		query.put(column, pattern);

		DBCursor cursor = collection.find(query);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public List<DBObject> find(DBObject query, int num, List<String> columns) {
		List<DBObject> list = new ArrayList<DBObject>();
		BasicDBObject columnDB = new BasicDBObject();// 指定需要显示列
		for (String column : columns) {
			columnDB.put(column, 1);
		}
		DBCursor cursor = collection.find(query, columnDB).limit(num);
		while (cursor.hasNext()) {
			// if(list.size()==num)break;
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public List<DBObject> find(DBObject query, int num, String... columns) {
		List<DBObject> list = new ArrayList<DBObject>();
		BasicDBObject columnDB = new BasicDBObject();// 指定需要显示列
		for (String column : columns) {
			columnDB.put(column, 1);
		}
		DBCursor cursor = collection.find(query, columnDB).limit(num);
		while (cursor.hasNext()) {
			// if(list.size()==num)break;
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public List<DBObject> find(DBObject query, String sortColumn, List<String> columns) {
		List<DBObject> list = new ArrayList<DBObject>();
		BasicDBObject columnDB = new BasicDBObject();// 指定需要显示列
		for (String column : columns) {
			columnDB.put(column, 1);
		}
		DBCursor cursor = collection.find(query, columnDB).sort(new BasicDBObject(sortColumn, -1));
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public List<DBObject> find(DBObject query, String sortColumn, String... columns) {
		List<DBObject> list = new ArrayList<DBObject>();
		BasicDBObject columnDB = new BasicDBObject();// 指定需要显示列
		for (String column : columns) {
			columnDB.put(column, 1);
		}
		DBCursor cursor = collection.find(query, columnDB).sort(new BasicDBObject(sortColumn, -1));
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public List<DBObject> find(DBObject query, String keyword, String column, String sortColumn) {
		List<DBObject> list = new ArrayList<DBObject>();
		Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
		query.put(column, pattern);

		DBCursor cursor = collection.find(query).sort(new BasicDBObject(sortColumn, -1));
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public List<DBObject> find(DBObject query, int num, String sortColumn, List<String> columns) {
		List<DBObject> list = new ArrayList<DBObject>();
		BasicDBObject columnDB = new BasicDBObject();// 指定需要显示列
		for (String column : columns) {
			columnDB.put(column, 1);
		}
		DBCursor cursor = collection.find(query, columnDB).limit(num).sort(new BasicDBObject(sortColumn, -1));
		while (cursor.hasNext()) {
			// if(list.size()==num)break;
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public List<DBObject> find(DBObject query, int num, String sortColumn, String... columns) {
		List<DBObject> list = new ArrayList<DBObject>();
		BasicDBObject columnDB = new BasicDBObject();// 指定需要显示列
		for (String column : columns) {
			columnDB.put(column, 1);
		}
		DBCursor cursor = collection.find(query, columnDB).limit(num).sort(new BasicDBObject(sortColumn, -1));
		while (cursor.hasNext()) {
			// if(list.size()==num)break;
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public List<DBObject> find(DBObject query, int page, int pageSize, String... columns) {
		List<DBObject> list = new ArrayList<DBObject>();
		BasicDBObject columnDB = new BasicDBObject();// 指定需要显示列
		for (String column : columns) {
			columnDB.put(column, 1);
		}
		DBCursor cursor = collection.find(query, columnDB).skip(page * pageSize).limit(pageSize);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public List<DBObject> find(DBObject query, int page, int pageSize, List<String> columns) {
		List<DBObject> list = new ArrayList<DBObject>();
		BasicDBObject columnDB = new BasicDBObject();// 指定需要显示列
		for (String column : columns) {
			columnDB.put(column, 1);
		}
		DBCursor cursor = collection.find(query, columnDB).skip(page * pageSize).limit(pageSize);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public DBObject findLastOne() {
		long count = collection.count();
		List<DBObject> list = new ArrayList<DBObject>();
		DBCursor cursor = collection.find().skip(Integer.parseInt(String.valueOf(count)) - 1);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		return list.get(list.size() - 1);
	}

	public List<DBObject> findOR(BasicDBList values, String... columns) {
		List<DBObject> list = new ArrayList<DBObject>();

		BasicDBObject columnDB = new BasicDBObject();// 指定需要显示列
		for (String column : columns) {
			columnDB.put(column, 1);
		}
		DBObject query = new BasicDBObject();
		query.put("$or", values);
		DBCursor cursor = collection.find(query, columnDB);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	// public List<DBObject> find(DBObject query, String...columns){
	// List<DBObject> list = new ArrayList<DBObject>();
	//
	// BasicDBObject columnDB=new BasicDBObject();//指定需要显示列
	// for(String column : columns){
	// columnDB.put(column, 1);
	// }
	// DBCursor cursor = collection.find(query,columnDB);
	// while (cursor.hasNext()) {
	// list.add(cursor.next());
	// }
	// cursor.close();
	// return list;
	// }

	public List<DBObject> findAll() {
		List<DBObject> list = new ArrayList<DBObject>();

		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	public List<DBObject> find(String field, Date arg0, Date arg1) {
		List<DBObject> list = new ArrayList<DBObject>();
		BasicDBObject dateQuery = new BasicDBObject(field, new BasicDBObject("$gte", arg0).append("$lte", arg1));
		DBCursor cursor = collection.find(dateQuery);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		cursor.close();
		return list;
	}

	// public void update(JSONObject condition, JSONObject setValue){
	// BasicDBObject query = (BasicDBObject) JSON.parse(condition.toString());
	// BasicDBObject values = (BasicDBObject) JSON.parse(setValue.toString());
	// DBObject update=new BasicDBObject("$set",values);
	// collection.update(query, update);
	// }

	public void update(DBObject query, DBObject setValue) {
		DBObject values = setValue;
		DBObject update = new BasicDBObject("$set", values);
		collection.update(query, update);
	}

	public void updateByID(String id, JSONObject setValue) {
		BasicDBObject query = new BasicDBObject();
		query.put("id", new ObjectId(id));
		BasicDBObject values = (BasicDBObject) JSON.parse(setValue.toString());
		DBObject update = new BasicDBObject("$set", values);
		collection.update(query, update);
	}

	public long count() {
		return collection.count();
	}

	// public void clear() {
	// DBCursor cursorDoc = collection.find();
	// while (cursorDoc.hasNext()) {
	// DBObject doc = cursorDoc.next();
	// collection.remove(doc);
	// }
	// }

	public void drop() {
		collection.drop();
	}

	public boolean ExistsCollection(final String collectionName) {
		return mg.getDatabaseNames().contains(collectionName);
	}

	public BasicDBList GroupBy(BasicDBObject key, BasicDBObject condition) {
		// BasicDBObject key = new BasicDBObject("year",true);
		// BasicDBObject cond = new BasicDBObject("id",new
		// BasicDBObject(QueryOperators.GT,0));
		BasicDBObject initial = new BasicDBObject("count", 0);
		String reduce = "function (curr,result){result.count+=1;}";
		BasicDBList group = (BasicDBList) collection.group(key, condition, initial, reduce);
		return group;
	}

	public void print(Object o) {
		System.out.println(o);
	}

	public String getCollectionName() {
		return this.collection.getName();
	}

	public String getDBName() {
		return this.db.getName();
	}

	public void close() {
		if (mg != null)
			mg.close();
		mg = null;
		db = null;
		System.gc();
	}

	/**
	 * 把实体bean对象转换成DBObject
	 * 
	 * @param bean
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public <T> DBObject bean2DBObject(T bean) throws IllegalArgumentException, IllegalAccessException {
		if (bean == null) {
			return null;
		}
		DBObject dbObject = new BasicDBObject();
		// 获取对象对应类中的所有属性域
		Field[] fields = bean.getClass().getDeclaredFields();
		for (Field field : fields) {
			// 获取属性名
			String varName = field.getName();
			// 修改访问控制权限
			boolean accessFlag = field.isAccessible();
			if (!accessFlag) {
				field.setAccessible(true);
			}
			Object param = field.get(bean);
			if (param == null) {
				continue;
			} else if (param instanceof Integer) {// 判断变量的类型
				int value = ((Integer) param).intValue();
				dbObject.put(varName, value);
			} else if (param instanceof String) {
				String value = (String) param;
				dbObject.put(varName, value);
			} else if (param instanceof Double) {
				double value = ((Double) param).doubleValue();
				dbObject.put(varName, value);
			} else if (param instanceof Float) {
				float value = ((Float) param).floatValue();
				dbObject.put(varName, value);
			} else if (param instanceof Long) {
				long value = ((Long) param).longValue();
				dbObject.put(varName, value);
			} else if (param instanceof Boolean) {
				boolean value = ((Boolean) param).booleanValue();
				dbObject.put(varName, value);
			} else if (param instanceof Date) {
				Date value = (Date) param;
				dbObject.put(varName, value);
			}
			// 恢复访问控制权限
			field.setAccessible(accessFlag);
		}
		return dbObject;
	}
}
