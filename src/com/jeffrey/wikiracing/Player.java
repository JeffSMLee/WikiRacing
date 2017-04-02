package com.jeffrey.wikiracing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
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

import com.jeffrey.handler.WikiBrainHandler;


public abstract class Player {
	
	private Env env;
	private Configurator conf;
	private Language simple;
	private MonolingualSRMetric sr;
	private WikiBrainHandler handler;
	private LocalPage target;
	private LocalPage currentPage;
	private List<LocalPage> path;
	private List<Integer> visitedIDs;
	protected Connection con;
	protected Statement stmt;
	
	{
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
	
	public Player(LocalPage target) throws ConfigurationException, DaoException {
		this.target = target;
		simple = Language.SIMPLE;
		
		env = new EnvBuilder().build();
		conf = env.getConfigurator();
		sr = conf.get(
                MonolingualSRMetric.class, "ensemble",
                "language", simple.getLangCode());
		handler = WikiBrainHandler.getInstance();
		visitedIDs = new ArrayList<Integer>();
		currentPage = handler.fetchRandom();
		path = new ArrayList<LocalPage>();
		path.add(currentPage);
		visitedIDs.add(currentPage.getLocalId());
	}
	
	public Player(LocalPage start, LocalPage target) throws ConfigurationException, DaoException {
		this.target = target;
		simple = Language.SIMPLE;
		
		env = new EnvBuilder().build();
		conf = env.getConfigurator();
		sr = conf.get(
                MonolingualSRMetric.class, "ensemble",
                "language", simple.getLangCode());
		handler = WikiBrainHandler.getInstance();
		visitedIDs = new ArrayList<Integer>();
		currentPage = start;
		path = new ArrayList<LocalPage>();
		path.add(currentPage);
		visitedIDs.add(currentPage.getLocalId());
	}
	
	public double getSRScore(int page1, int page2) throws DaoException {
		return sr.similarity(page1, page2, false).getScore();
	}
	
	public boolean hasWon() {
		return target.getLocalId() == currentPage.getLocalId();
	}
	
	protected boolean hasVisited(LocalLink link) {
		boolean visited = false;
		for(Integer i : visitedIDs) {
			if(link.getDestId() == i) {
				visited = true;
			}
		}
		return visited;
	}
	
	public List<LocalLink> getChoices() throws DaoException {
		return handler.getLinks(getCurrentPage(), true);
	}
	
	public WikiBrainHandler getHandler() {
		return handler;
	}
	
	public LocalPage getCurrentPage() {
		return currentPage;
	}
	
	public void setCurrentPage(LocalPage page) {
		currentPage = page;
	}
	
	public LocalPage getTarget() {
		return target;
	}
	
	public List<Integer> getVisitedIDs() {
		return visitedIDs;
	}
	
	public void addToVisited(int id) {
		visitedIDs.add(id);
	}
	
	public List<LocalPage> getPath() {
		return path;
	}
	
	public void addToPath(LocalPage page) {
		path.add(page);
	}
	
	public int getNumberOfMoves() {
		return path.size() - 1;
	}
}
