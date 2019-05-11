package org.bhu.db.interf;

import java.util.List;

public interface DataTunnel<T> {

	void getTunnel();
	void insert(List<T> list);
	void close();
	boolean updateRecord(T t);
	List<T> getDataList();
	void setTable(String tbName);
	int sum(Object condition);
}
