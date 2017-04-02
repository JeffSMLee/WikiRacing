package com.jeffrey.wikiracing;

import java.util.ArrayList;
import java.util.List;

import org.wikibrain.conf.ConfigurationException;
import org.wikibrain.conf.Configurator;
import org.wikibrain.core.cmd.Env;
import org.wikibrain.core.cmd.EnvBuilder;
import org.wikibrain.core.dao.DaoException;
import org.wikibrain.core.lang.Language;
import org.wikibrain.core.model.LocalLink;
import org.wikibrain.core.model.LocalPage;
import org.wikibrain.sr.MonolingualSRMetric;

import java.sql.*;

import com.jeffrey.handler.WikiBrainHandler;


public class PlayerDB {
	
	private Env env;
	private Configurator conf;
	private Language simple;
	private MonolingualSRMetric sr;
	private WikiBrainHandler handler;
	
	private int sessionKey;
	private String table;
	protected Connection con = null;
	protected Statement stmt = null;
	
	{
		simple = Language.SIMPLE;
		
		env = new EnvBuilder().build();
		conf = env.getConfigurator();
		sr = conf.get(
				MonolingualSRMetric.class, "ensemble",
				"language", simple.getLangCode());
		handler = WikiBrainHandler.getInstance();
		
		//Database Connection 
		try {
		  	Class.forName("org.postgresql.Driver");
		    con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/wikidemo", "postgres", "0000809762jl");
		    stmt = con.createStatement();
		} catch (Exception ex) {
		    // if not successful, quit
		    System.out.println("Cannot open database -- make sure ODBC is configured properly.");
		    System.exit(1);
		}
			    
			    		
		
	}
	
	public PlayerDB(int target, boolean isBot, int sessionKey) 
						throws ConfigurationException, DaoException {
		table = isBot ? "bots" : "players";
		this.sessionKey = sessionKey;
		
		try {
			int startID = handler.fetchRandom().getLocalId();
			stmt.executeUpdate("INSERT INTO " + table + " (sessionKey, target, curr, path) "
					+ "VALUES (" + sessionKey + ", " + target + ", " + startID + ", '" + startID + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private PlayerDB(int sessionKey, boolean isBot) throws ConfigurationException {
		table = isBot ? "bots" : "players";
		this.sessionKey = sessionKey;
	}
	
	public static PlayerDB fetch(int sessionKey, boolean isBot) throws ConfigurationException {
		return new PlayerDB(sessionKey, isBot);
	}
	
	public PlayerDB(int start, int target, boolean isBot, int sessionKey) 
						throws ConfigurationException, DaoException {
		table = isBot ? "bots" : "players";
		this.sessionKey = sessionKey;
		
		try {
			stmt.executeUpdate("INSERT INTO " + table + " (sessionKey, target, curr, path) "
					+ "VALUES (" + sessionKey + ", " + target + ", " + start + ", '" + start + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public double getSRScore(int page1, int page2) throws DaoException {
		return sr.similarity(page1, page2, false).getScore();
	}
	
	public boolean hasReachedTarget() throws SQLException, DaoException {
		return getCurrentPage().getLocalId() == getTarget().getLocalId();
	}
	
	public boolean hasVisited(LocalLink link) throws NumberFormatException, SQLException, DaoException {
		for(LocalPage page : getPath()) {
			if(link.getDestId() == page.getLocalId()) {
				return true;
			}
		}
		return false;
	}
	
	public List<LocalLink> getChoices() throws DaoException, SQLException {
		return handler.getLinks(getCurrentPage(), true);
	}
	
	public WikiBrainHandler getHandler() {
		return handler;
	}
	
	public LocalPage getCurrentPage() throws SQLException, DaoException {
		ResultSet rs = stmt.executeQuery("SELECT curr FROM " + table + " WHERE sessionKey=" + sessionKey);
		rs.next();
		int id = rs.getInt(1);
		return handler.getLocalPageByID(id, Language.SIMPLE);
	}
	
	public void setCurrentPage(int page) throws SQLException, NumberFormatException, DaoException {
		stmt.executeUpdate("UPDATE " + table + " SET curr=" + page + " WHERE sessionKey=" + sessionKey);
		addToPath(handler.getLocalPageByID(page, Language.SIMPLE));
	}
	
	public LocalPage getTarget() throws SQLException, DaoException {
		ResultSet rs = stmt.executeQuery("SELECT target FROM " + table + " WHERE sessionKey=" + sessionKey);
		rs.next();
		int id = rs.getInt(1);
		return handler.getLocalPageByID(id, Language.SIMPLE);
	}
	
	public List<LocalPage> getPath() throws SQLException, NumberFormatException, DaoException {
		ResultSet rs = stmt.executeQuery("SELECT path FROM " + table + " WHERE sessionKey=" + sessionKey);
		rs.next();
		String ids = rs.getString(1);
		List<LocalPage> path = new ArrayList<LocalPage>();
		for(String id : ids.split(" ")) {
			path.add(handler.getLocalPageByID(Integer.parseInt(id), Language.SIMPLE));
		}
		return path;
	}
	
	private void addToPath(LocalPage page) throws NumberFormatException, SQLException, DaoException {
		List<LocalPage> path = getPath();
		path.add(page);
		String insert = "";
		for(LocalPage p : path) {
			insert += p.getLocalId() + " ";
		}
		stmt.executeUpdate("UPDATE " + table + " SET path='" + insert + "' WHERE sessionKey=" + sessionKey);
	}
	
	public int getNumberOfMoves() throws NumberFormatException, SQLException, DaoException {
		return getPath().size() - 1;
	}
}
