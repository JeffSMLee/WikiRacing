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

@Path("language_explorer/")
public class LanguageDemo {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getLanguageExplorer() {
		File f = new File("C:\\Users\\Jeffrey\\Dropbox\\WebDev\\restful\\com.jeffrey.wikidemo\\WebContent\\LanguageExplorer.html");
		return Response.ok(f).build();
	}

}
