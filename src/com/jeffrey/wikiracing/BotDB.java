package com.jeffrey.wikiracing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.wikibrain.conf.ConfigurationException;
import org.wikibrain.core.dao.DaoException;
import org.wikibrain.core.lang.Language;
import org.wikibrain.core.model.LocalLink;

public class BotDB extends PlayerDB {

	private int difficulty;
	private List<Integer> optimalPath;
	
	public BotDB(int target, int sessionKey, int difficulty) throws ConfigurationException, DaoException, SQLException {
		super(target, true, sessionKey);
		try {
			stmt.executeUpdate("UPDATE bots SET difficulty=" + difficulty + " WHERE sessionKey=" + sessionKey);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.difficulty = difficulty;
		if(difficulty == 1) {
			optimalPath = getOptimalPath(getCurrentPage().getLocalId(), target);
		}
	}	

	public BotDB(int start, int target, int sessionKey, int difficulty) throws ConfigurationException, DaoException, SQLException {
		super(start, target, true, sessionKey);
		try {
			stmt.executeUpdate("UPDATE bots SET difficulty=" + difficulty + " WHERE sessionKey=" + sessionKey);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.difficulty = difficulty;
		if(difficulty == 1) {
			optimalPath = getOptimalPath(getCurrentPage().getLocalId(), target);
		}
	}
	
	public static BotDB fetch(int sessionKey) throws ConfigurationException {
		return (BotDB) fetch(sessionKey, true);
	}

	public void makeMove() {
		try {
			pick(getChoices());
		} catch (DaoException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getDifficulty(int sessionKey) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT difficulty FROM bots WHERE sessionKey=" + sessionKey);
		rs.next();
		return rs.getInt(1);
	}
	
	public void pick(List<LocalLink> links) throws DaoException, SQLException {
		if(difficulty == 0) {
			LocalLink choice = null;
			double highScore = 0.0;
			for(LocalLink link : links) {
				double srScore = getSRScore(link.getDestId(), getTarget().getLocalId());
				if(srScore > highScore && !hasVisited(link)) {
					choice = link;
					highScore = srScore;
				}
				if(link.getDestId() == getTarget().getLocalId()) {
					choice = link;
					break;
				}
			}
			
			System.out.println("ez");
			
			setCurrentPage(choice.getDestId());
		} else if(difficulty == 1) {
			int targetPick = optimalPath.get(getPath().size());
			for(LocalLink link : links) {
				if(link.getDestId() == targetPick) {
					setCurrentPage(targetPick);
				}
			}
			System.out.println("hard");
		}
	}
	
	public List<Integer> getOptimalPath(int start, int target) throws DaoException, ConfigurationException {
		Queue<Integer> queueA = new LinkedList<Integer>();
		Set<Integer> vectorSetA = new HashSet<Integer>();
		Map<Integer, Integer> fatherA = new HashMap<Integer, Integer>();

		queueA.add(start);
		vectorSetA.add(start);
		fatherA.put(start, -1);
		
		Queue<Integer> queueB = new LinkedList<Integer>();
		Set<Integer> vectorSetB = new HashSet<Integer>();
		Map<Integer, Integer> fatherB = new HashMap<Integer, Integer>();

		queueB.add(target);
		vectorSetB.add(target);
		fatherB.put(target, -1);

		while (!queueA.isEmpty() && !queueB.isEmpty()) {
			int currentA = queueA.remove();
			int currentB = queueB.remove();
			
			Iterable<LocalLink> linksA = getHandler().getLinks(getHandler().getLocalPageByID(currentA, Language.SIMPLE), true);
			for(LocalLink link : linksA) {
				int destId = link.getDestId();
				if(!link.isParseable())
					continue;
				if(vectorSetA.contains(destId))
					continue;
				if(link.getDestId() == -1)
					continue;
				queueA.add(destId);
				vectorSetA.add(destId);
				fatherA.put(destId, currentA);
			}
			
			Iterable<LocalLink> linksB = getHandler().getLinks(getHandler().getLocalPageByID(currentB, Language.SIMPLE), false);
			for(LocalLink link : linksB) {
				int sourceId = link.getSourceId();
				if(!link.isParseable())
					continue;
				if(vectorSetB.contains(sourceId))
					continue;
				if(link.getDestId() == -1)
					continue;
				queueB.add(sourceId);
				vectorSetB.add(sourceId);
				fatherB.put(sourceId, currentB);
			}
			
			for(Integer visited : vectorSetA) {
				if(vectorSetB.contains(visited)) {
					currentA = visited;
					currentB = visited;
					List<Integer> path = new ArrayList<Integer>();
					List<Integer> a = new ArrayList<Integer>();
					while(fatherA.get(currentA) != -1) {
						a.add(fatherA.get(currentA));
						currentA = fatherA.get(currentA);
					}
					for(int i = a.size() - 1; i >= 0; i--) {
						path.add(a.get(i));
					}
					path.add(visited);
					while(fatherB.get(currentB) != -1) {
						path.add(fatherB.get(currentB));
						currentB = fatherB.get(currentB);
					}
					return path;
				}
			}
		}
		return null;
	}
}
