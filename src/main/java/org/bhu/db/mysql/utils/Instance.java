package org.bhu.db.mysql.utils;

import net.sf.json.JSONArray;

/**
 * @author 朱波
 *
 *         E-mail: 18710086110@163.com
 *
 * @version 创建时间：Sep 13, 2014 9:40:42 PM
 *
 *          功能：
 */
public class Instance {

	String title;
	String url;
	String content;
	JSONArray comment;
	String channel;
	String keywords;
	int status;
	String date;
	String source;
	int commentcount;
	int newsID;
	int site;
	int wordcount;

	public Instance() {
		this.title = "";
		this.url = "";
		this.content = "";
		this.comment = new JSONArray();
		this.status = -1;
		this.date = "";
		this.keywords ="";
		this.channel = "";
		this.site=0;
		this.wordcount=0;
	}

	public Instance(String title, String url, JSONArray comment) {
		this.title = title;
		this.url = url;
		this.comment = comment;
	}
	
	public Instance(String link, String title, String channel){
		this.url = link;
		this.title= title;
		this.channel = channel;
	}
	

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public JSONArray getComment() {
		return comment;
	}

	public void setComment(JSONArray comment) {
		this.comment = comment;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int i) {
		this.status = i;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	
	public int getNewsID() {
		return newsID;
	}

	public void setNewsID(int newsID) {
		this.newsID = newsID;
	}
	

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	

	public int getSite() {
		return site;
	}

	public void setSite(int site) {
		this.site = site;
	}

	public int getCommentcount() {
		return commentcount;
	}

	public void setCommentcount(int commentcount) {
		this.commentcount = commentcount;
	}

	public int getWordcount() {
		return wordcount;
	}

	public void setWordcount(int wordcount) {
		this.wordcount = wordcount;
	}

	@Override
	public String toString() {
		String line = null;
		line = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",this.newsID, this.title, this.url,
				this.status, this.date, this.channel, this.content, this.wordcount);
		return line;
	}
}