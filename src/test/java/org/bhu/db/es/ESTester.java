package org.bhu.db.es;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Before;
import org.junit.Test;

import net.sf.json.JSONObject;

public class ESTester {
//	ESHelper eshelper = new ESHelper("ubuntuserverES9300.nat123.cc", 51998, "Bolingua");
	
	ESHelper eshelper = null;
	
	@Before
	public void init(){
//		eshelper = new ESHelper("localhost", 9300, "Bolingua");
		eshelper = new ESHelper("210.47.177.167", 9300, "Bolingua");
//		eshelper.setIndexName("blog2");
//		eshelper.setType("2015");
		
	}
	
	@Test
	public void createIndexTester(){
		String name = "netnews";
		eshelper.setIndexName(name);
		eshelper.setType("2016");
		eshelper.createIndex();
	}
	
	@Test
	public void createNewsIndexTester(){
		String name = "news";
		eshelper.setIndexName(name);
		eshelper.setType("paper");
		eshelper.createIndex();
	}
	
	@Test
	public void createNewsMappingTester(){
		String name = "news";
		eshelper.setIndexName(name);
		eshelper.setType("paper");
		eshelper.createNewsMapping();
	}
	
	@Test
	public void insertMap(){
//		eshelper.createIKMapping();
		eshelper.setIndexName("netnews");
		eshelper.setType("2016");
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("id", "5");
		source.put("title", "俄禁止国家安全官员在国外银行开账户");
		source.put("content", "新华网莫斯科1月1日电（记者胡晓光）俄罗斯从1月1日起禁止在国家安全领域工作的官员在国外银行开设账户");
		source.put("source", "新华网");
		source.put("type", "媒体");
		source.put("keywords", "胡晓光");
		source.put("url", "http://news.sohu.com/20150101/n407475404.shtml");
		source.put("date", new Date());
		source.put("status", 1);
		eshelper.insert(source);
	}
	
	@Test
	public void searchAll(){
		eshelper.setIndexName("netnews");
		eshelper.setType("2016");
		eshelper.searchAll();
	}
	
	
	
	@Test
	public void search(){
		QueryBuilder matchQuery = QueryBuilders.matchQuery("sentence", "中国");
		eshelper.setIndexName("peoplesentence");
		eshelper.setType("fulltext");
		eshelper.search(matchQuery);
	}
	
	@Test
	public void search2(){
		QueryBuilder matchQuery = QueryBuilders.matchQuery("title", "美国");
		eshelper.setIndexName("sen");
		eshelper.setType("2016");
		eshelper.search(matchQuery);
	}
	
	@Test
	public void insert(){
		JSONObject jsonData = new JSONObject();
		jsonData.put("id", 1);
		jsonData.put("content", "类似关系数据库的表，主要功能是将完全不同schema");
		eshelper.insert(jsonData, "id");
	}
	
	@Test
	public void deleteIndex(){
		eshelper.getIndex(".kibana");
		eshelper.deleteIndex();
	}
	
	
	@Test
	public void insertMap2(){
//		eshelper.createIKMapping();
		eshelper.setIndexName("netnews");
		eshelper.setType("2016");
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("id", "3");
		source.put("title", "美国外滩踩踏事件遇难台胞家属抵达上海");
		source.put("content", "新华网上海1月1日电（许晓青\r\n魏文秉）上海外滩拥挤踩踏事件中遇难台胞周怡安的家属及同事一行4人1日21时许搭东航班机抵达上海浦东机场。 \r\n据上海市台办介绍，经海协会、海基会协调安排，周怡安父母、妹妹及公司同事一行4人晚间抵达上海。家属向大陆方面工作人员表示");
		source.put("source", "新华网");
		source.put("type", "媒体");
		source.put("keywords", "外滩 踩踏");
		source.put("url", "http://news.sohu.com/20150101/n407475404.shtml");
		source.put("date", new Date());
		source.put("status", 1);
		eshelper.insert(source);
	}
}
