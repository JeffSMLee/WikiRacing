package com.jeffrey.wikidemo;

import java.io.File;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.wikibrain.core.dao.DaoException;
import org.wikibrain.core.lang.Language;

import com.jeffrey.handler.ArticleDataObject;
import com.jeffrey.handler.WikiBrainHandler;


@Path("api/language/")
public class LanguageAPI {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response readMe() {
		File f = new File("C:\\Users\\Jeffrey\\Dropbox\\WebDev\\restful\\com.jeffrey.wikidemo\\WebContent\\readme.html");
		return Response.ok(f).build();
	}
	
	@POST
	@Path("json")
	@Produces(MediaType.TEXT_PLAIN)
	public String getJSON(String data) {
		//return wbHandler.getLanguagesWithArticle(Language.SIMPLE, data, false);
		return "";
	}
	
	@POST
	@Path("html")
	@Produces(MediaType.TEXT_HTML)
	public String getHTML(String data) {
		WikiBrainHandler wbHandler = WikiBrainHandler.getInstance();
		try {
			String langCode = data.split(" ")[0];
			String title = data.split(" ")[1];
			List<ArticleDataObject> list = wbHandler.getLanguagesWithArticle(Language.getByLangCode(langCode), title);
			if(list.size() == 0) {
				return "(None)";
			}
			String returnString = "<div id=\"container\">";
			int i = 1;
			for(ArticleDataObject ado : list) {
				returnString += "<a style=\"display:block; width:90px\" href=\"" 
						+ ado.getUrl() 
						+ "\"><div class=\"object\" id=\"div" 
						+ i 
						+ "\"><p class=\"langName\">" 
						+ ado.getLang() 
						+ "- " 
						+ ado.getTitle() 
						+ "</p></div></a>";
				i++;
			}
			returnString += "</div>";
			return returnString;
		} catch (DaoException e) {
			return "(Error)";
		}
	}
	
	@POST
	@Path("plaintext")
	@Produces(MediaType.TEXT_PLAIN)
	public String getPlainText(String data) {
		WikiBrainHandler wbHandler = WikiBrainHandler.getInstance();
		try {
			String langCode = data.split(" ")[0];
			System.out.println(langCode);
			String title = data.split(" ")[1];
			System.out.println(title);
			System.out.println(wbHandler.getLanguagesWithArticle(Language.getByLangCode(langCode), 
					title, true));
			return wbHandler.getLanguagesWithArticle(Language.getByLangCode(langCode), 
					title, true);
		} catch (DaoException e) {
			return "(Error)";
		}
	}
	
}
