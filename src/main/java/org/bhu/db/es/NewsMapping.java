package org.bhu.db.es;


public class NewsMapping {

	
	public static String getNewsMapping(String type) {
		return String.format("{\"%s\":\r\n" + 
				"{\"properties\":\r\n" + 
				"{\r\n" + 
				"\"id\":{\"type\":\"string\"},\r\n" + 
				"\"title\":{\"type\": \"string\",\r\n" + 
				"        \"store\": \"no\",\r\n" + 
				"        \"term_vector\": \"with_positions_offsets\",\r\n" + 
				"        \"analyzer\":\"ik\",\r\n" + 
				"        \"boost\": 5},\r\n" + 
				"\"content\":{\"type\": \"string\",\r\n" + 
				"        \"store\": \"no\",\r\n" + 
				"        \"term_vector\": \"with_positions_offsets\",\r\n" + 
				"        \"analyzer\":\"ik\",\r\n" + 
				"        \"boost\": 5},\r\n" + 
				"\"channel\":{\"type\": \"string\",\r\n" + 
				"        \"store\": \"no\",\r\n" + 
				"        \"term_vector\": \"with_positions_offsets\",\r\n" + 
				"        \"analyzer\":\"ik\",\r\n" + 
				"        \"boost\": 5},\r\n" + 
				"\"keywords\":{\"type\": \"string\",\r\n" + 
				"        \"store\": \"no\",\r\n" + 
				"        \"term_vector\": \"with_positions_offsets\",\r\n" + 
				"        \"analyzer\":\"ik\",\r\n" + 
				"        \"boost\": 5},\r\n" + 
				"\r\n" + 
				"\"date\": {\r\n" + 
				"        \"type\": \"date\",\r\n" + 
				"        \"format\": \"yyyy/MM/dd\",\r\n" + 
				"        \"index\": \"not_analyzed\"\r\n" + 
				"      },\r\n" + 
				"\"year\":{\"type\":\"integer\"},\r\n" + 
				"\r\n" + 
				"\"len\":{\"type\":\"integer\"},\r\n" + 
				"\r\n" + 
				"\"site\":{\"type\":\"integer\"},\r\n" + 
				"\r\n" + 
				"\"url\":{\"type\":\"string\",\r\n" + 
				"		\"index\": \"not_analyzed\"},\r\n" + 
				"\r\n" + 
				"\"hashfigure\":{\"type\":\"string\",\r\n" + 
				"		\"index\": \"not_analyzed\"},\r\n" + 
				"\r\n" + 
				"\"segment\":{\"type\": \"string\",\r\n" + 
				"        \"store\": \"no\",\r\n" + 
				"        \"term_vector\": \"with_positions_offsets\",\r\n" + 
				"        \"analyzer\":\"ik\",\r\n" + 
				"        \"boost\": 5},\r\n" + 
				"        }}}", type);
	}
}
