package com.jeffrey.wikidemo;

import java.io.File;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.wikibrain.conf.ConfigurationException;
import org.wikibrain.core.dao.DaoException;
import org.wikibrain.core.model.LocalLink;

import com.jeffrey.wikiracing.WikiRacingController;

@Path("wikiracing/")
public class WikiRacing {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response get() {
		File f = new File("C:\\Users\\Jeffrey\\Dropbox\\WebDev\\restful\\com.jeffrey.wikidemo\\WebContent\\wikiracing.html");
		return Response.ok(f).build();
	}
	
	
}
