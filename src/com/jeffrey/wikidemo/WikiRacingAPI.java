package com.jeffrey.wikidemo;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.wikibrain.conf.ConfigurationException;
import org.wikibrain.core.dao.DaoException;
import org.wikibrain.core.model.LocalLink;
import org.wikibrain.core.model.LocalPage;

import com.jeffrey.handler.WikiBrainHandler;
import com.jeffrey.wikiracing.WikiRacingController;

@Path("api/wikiracing/")
public class WikiRacingAPI {

	@POST
	@Path("start")
	@Produces(MediaType.TEXT_PLAIN)
	public String start(String data) throws ConfigurationException, DaoException, SQLException {
		return "" + WikiRacingController.getInstance().add(Boolean.parseBoolean(data), 1);
	}
	
	@POST
	@Path("target")
	@Produces(MediaType.TEXT_PLAIN)
	public String getTarget(String data) {
		return WikiRacingController
				.getInstance()
				.get(Integer.parseInt(data))
				.getTargetTitle();
	}
	
	@POST
	@Path("summary")
	@Produces(MediaType.TEXT_HTML)
	public String summary(String data) throws DaoException, SQLException, NumberFormatException, ConfigurationException {
		int sessionKey = Integer.parseInt(data);
		String status = WikiRacingController.getInstance().get(sessionKey).getStatus();
		String s = "<div id=\"summary\">"
				+ "<h2>Match Summary</h2>"
				+ "Winner: " + status
				+ "<br>"
				+ "Player Path: " + getPathHtml(sessionKey, true)
				+ "Bot Path: " + getPathHtml(sessionKey, false)
				+ "Number of Player Moves: " + WikiRacingController.getInstance().get(sessionKey).getPlayer().getNumberOfMoves()
				+ "<br>"
				+ "Number of Bot Moves: " + WikiRacingController.getInstance().get(sessionKey).getBot().getNumberOfMoves()
				+ "</div>";
		if(!status.equals("ongoing")) {
			WikiRacingController.getInstance().clean(sessionKey);
		}
		return s;
	}
	
	private String getPathHtml(int sessionKey, boolean isPlayer) throws DaoException, NumberFormatException, SQLException, ConfigurationException {
		List<LocalPage> path = null;
		if(isPlayer) {			
			path = WikiRacingController.getInstance().get(sessionKey).getPlayer().getPath();
		} else {
			path = WikiRacingController.getInstance().get(sessionKey).getBot().getPath();
		}
		String s = "<ol>";
		for(LocalPage page : path) {
			s += "<li><a href=\"" + WikiBrainHandler.getInstance().getURL(page) + " " + "\">" 
					+ page.getTitle().getTitleStringWithoutNamespace() + "</a></li>";
		}
		s+="</ol>";
		return s;
	}

	@POST
	@Path("status")
	@Produces(MediaType.TEXT_PLAIN)
	public String playerStatus(String data) throws SQLException, DaoException {
		int sessionKey = Integer.parseInt(data);
		return WikiRacingController.getInstance().get(sessionKey).getStatus();
	}
	
	@POST
	@Path("player/current")
	@Produces(MediaType.TEXT_PLAIN)
	public String getPlayerCurrent(String data) throws NumberFormatException, SQLException, DaoException {
		return WikiRacingController
				.getInstance()
				.get(Integer
				.parseInt(data))
				.getPlayer()
				.getCurrentPage()
				.getTitle()
				.getTitleStringWithoutNamespace();
	}
	
	@POST
	@Path("player/links")
	@Produces(MediaType.TEXT_HTML)
	public String getPlayerLinks(String data) throws NumberFormatException, DaoException, SQLException {
		return formatLinksHTML(WikiRacingController
				.getInstance()
				.get(Integer.parseInt(data))
				.getPlayer()
				.getChoices(), true);
	}
	
	@POST
	@Path("player/pick")
	@Produces(MediaType.TEXT_PLAIN)
	public String playerPick(String data) throws NumberFormatException, DaoException, SQLException {
		//return WikiRacingController.getInstance().get(Integer.parseInt(data.split(" ")[0])).getPlayer().pick(Integer.parseInt(data)).getMsg();
		WikiRacingController.getInstance().get(Integer.parseInt(data.split(" ")[0])).getPlayer().setCurrentPage(Integer.parseInt(data.split(" ")[1]));
		return "hi";
	}
	
	@POST
	@Path("bot/current")
	@Produces(MediaType.TEXT_PLAIN)
	public String getBotCurrent(String data) throws NumberFormatException, SQLException, DaoException, ConfigurationException {
		return WikiRacingController
				.getInstance()
				.get(Integer.parseInt(data))
				.getBot()
				.getCurrentPage()
				.getTitle()
				.getTitleStringWithoutNamespace();
	}
	
	@POST
	@Path("bot/links")
	@Produces(MediaType.TEXT_HTML)
	public String getBotLinks(String data) throws NumberFormatException, DaoException, SQLException, ConfigurationException {
		return formatLinksHTML(WikiRacingController
				.getInstance()
				.get(Integer.parseInt(data))
				.getBot()
				.getChoices(), false);
	}
	
	@POST
	@Path("bot/pick")
	public void botPick(String data) throws NumberFormatException, DaoException, ConfigurationException {
		WikiRacingController.getInstance().get(Integer.parseInt(data)).getBot().makeMove();
	}
	
	private String formatLinksHTML(List<LocalLink> links, boolean isWikiRacer) {
		String s = "";
		int i = 0;
		for(LocalLink link : links) {
			s += "<div id=\"" + (isWikiRacer ? "player" : "bot") + "_option\" class=\"link\" link=\"" 
					+ i 
					+ "\">" 
					+ link.getAnchorText() 
					+ "</div>";
			i++;
		}
		return s;
	}
}
