package com.jeffrey.handler;

import org.wikibrain.core.lang.Language;

public class ArticleDataObject {
	
	private String title;
	private Language lang;
	private String url;
	
	public ArticleDataObject(String title, Language lang, String url) {
		this.title = title;
		this.lang = lang;
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public Language getLang() {
		return lang;
	}

	public String getUrl() {
		return url;
	}
	
}
