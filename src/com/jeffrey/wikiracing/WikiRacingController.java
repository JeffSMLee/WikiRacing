package com.jeffrey.wikiracing;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

import org.wikibrain.conf.ConfigurationException;
import org.wikibrain.core.dao.DaoException;


public class WikiRacingController {
	
	private static WikiRacingController instance;
	
	private HashMap<Integer, Game> map;
	
	private WikiRacingController() {
		map = new HashMap<Integer, Game>();
	}
	
	public static WikiRacingController getInstance() {
		if(instance == null) {
			instance = new WikiRacingController();
		}
		return instance;
	}
	
	public int add(boolean sameStart, int difficulty) throws ConfigurationException, DaoException, SQLException {
		Random r = new Random();
		int sessionKey = r.nextInt(1000000000);
		map.put(sessionKey, new Game(sessionKey, sameStart, difficulty));
		return sessionKey;
	}
	
	public Game get(int sessionKey) {
		return map.get(sessionKey);
	}
	
	public void clean(int sessionKey) {
		map.remove(sessionKey);
	}
	
	public void clean() throws SQLException, DaoException {
		for(int i : map.keySet()) {
			if(map.get(i).isOver()) {
				map.remove(i);
			}
		}
	}
	
}
