package org.bhu.db.es;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

//import org.bhu.db.es.NewsMapping;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import net.sf.json.JSONObject;

public class ESHelper {
	static PreBuiltTransportClient client;
	String indexName;
	String type;
	
	
	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ESHelper (String IP, int port, String clusterName){
		Settings esSettings = Settings.builder()

				.put("cluster.name", clusterName) // 设置ES实例的名称

				.put("client.transport.sniff", true) // 自动嗅探整个集群的状态，把集群中其他ES节点的ip添加到本地的客户端列表中

				.build();

		client = new PreBuiltTransportClient(esSettings);// 初始化client较老版本发生了变化，此方法有几个重载方法，初始化插件等。

		// 此步骤添加IP，至少一个，其实一个就够了，因为添加了自动嗅探配置
		try {
			client.addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName(IP), port));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getIndex(String indexName){
		this.setIndexName(indexName);
	}
	
	public void getType(String typeName){
		this.setType(typeName);
	}
	
	public void createIndex(){
		client.admin().indices().prepareCreate(this.indexName).get();
	}
	
	

	
	public  void createIKMapping() {
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder().startObject()
                    // 索引库名（类似数据库中的表）
                    .startObject(type).startObject("properties")
                    .startObject("title").field("type", "string")
                    .field("analyzer","ik").field("search_analyzer","ik_smart").endObject()
                    .startObject("content").field("type", "string")
                    .field("source","ik").field("search_analyzer","ik_smart").endObject()
                    .startObject("type").field("type", "string")
                    .field("analyzer","ik").field("search_analyzer","ik_smart").endObject()
                    .startObject("keywords").field("type", "string")
                    .field("analyzer","ik").field("search_analyzer","ik_smart").endObject()
                    
                    
                    //.field("boost",100).endObject()
                    // 姓名
                    //.startObject("name").field("type", "string").endObject()
                    // 位置
                    //.startObject("location").field("type", "geo_point").endObject()
            //.endObject().startObject("_all").field("analyzer","ik").field("search_analyzer","ik").endObject().endObject().endObject();
                    .endObject().endObject().endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PutMappingRequest mappingRequest = Requests.putMappingRequest(this.indexName).type(this.type).source(builder);
        client.admin().indices().putMapping(mappingRequest).actionGet();
    }
	
	public void insert(JSONObject jsonData,String ID){
		client.prepareIndex(this.indexName, this.type)

		.setSource(jsonData)

		.setId(jsonData.getString(ID))//自己设置了id，也可以使用ES自带的，但是看文档说，ES的会因为删除id发生变动。

		.execute()

		.actionGet();
	}
	
	public void insert(Map<String, Object> source){
		IndexResponse indexResponse = client  
                .prepareIndex(this.indexName, this.type, source.get("id").toString()).setSource(source).get(); 
//		System.out.println(indexResponse.getVersion());  
	}
	
	public void searchAll() {  
        SearchResponse searchResponse = client.prepareSearch(this.indexName)  
                .setTypes(this.type)  
                .setQuery(QueryBuilders.matchAllQuery()) //查询所有  
                //.setQuery(QueryBuilders.matchQuery("name", "tom").operator(Operator.AND)) //根据tom分词查询name,默认or  
                //.setQuery(QueryBuilders.multiMatchQuery("tom", "name", "age")) //指定查询的字段  
                //.setQuery(QueryBuilders.queryString("name:to* AND age:[0 TO 19]")) //根据条件查询,支持通配符大于等于0小于等于19  
                //.setQuery(QueryBuilders.termQuery("name", "tom"))//查询时不分词  
                .setSearchType(SearchType.QUERY_THEN_FETCH)  
                .setFrom(0).setSize(10)//分页  
//                .addSort("age", SortOrder.DESC)//排序  
                .get();  
        
        SearchHits hits = searchResponse.getHits();  
        long total = hits.getTotalHits();  
        System.out.println(total);  
        SearchHit[] searchHits = hits.hits();  
        for(SearchHit s : searchHits)  
        {  
            System.out.println(s.getSourceAsString());  
        }  
    }  
	
	
	public void search(QueryBuilder matchQuery) {  
        SearchResponse searchResponse = client.prepareSearch(this.indexName)
                .setTypes(this.type)  
                .setQuery(matchQuery) 
                //.setQuery(QueryBuilders.matchQuery("name", "tom").operator(Operator.AND)) //根据tom分词查询name,默认or  
                //.setQuery(QueryBuilders.multiMatchQuery("tom", "name", "age")) //指定查询的字段  
                //.setQuery(QueryBuilders.queryStringQuery("name:to* AND age:[0 TO 19]")) //根据条件查询,支持通配符大于等于0小于等于19  
                //.setQuery(QueryBuilders.termQuery("name", "tom"))//查询时不分词  
//                .setQuery(QueryBuilders.queryStringQuery("_score:[0.0 TO 1]"))
                .setSearchType(SearchType.QUERY_THEN_FETCH)  
                .setFrom(0).setSize(100)//分页  
//                .addSort("age", SortOrder.DESC)//排序  
                .addSort("_score",SortOrder.DESC)
               
                .get();  
          
        SearchHits hits = searchResponse.getHits();  
        long total = hits.getTotalHits();  
        System.out.println(total);  
        SearchHit[] searchHits = hits.hits();  
        for(SearchHit s : searchHits)  
        {  
            System.out.println(s.getSourceAsString());  
        }  
    }  

	public void createNewsMapping() {
		this.client.admin().indices().preparePutMapping(NewsMapping.getNewsMapping(this.type));
	}
	
	public void deleteDoc(String docId){
		client.prepareDelete(this.indexName, this.type, docId).execute().actionGet();
	}
	
	public void deleteIndex(){
		client.admin().indices().delete(new DeleteIndexRequest(this.indexName)).actionGet();
	}
	
	public void close(){
		client.close();
	}
}
