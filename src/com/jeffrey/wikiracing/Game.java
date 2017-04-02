package com.jeffrey.wikiracing;
import java.sql.SQLException;

import org.wikibrain.conf.ConfigurationException;
import org.wikibrain.core.dao.DaoException;
import org.wikibrain.core.lang.Language;
import org.wikibrain.core.model.LocalPage;

import com.jeffrey.handler.WikiBrainHandler;


public class Game {

	private BotDB bot;
	private PlayerDB player;
	private LocalPage target;
	private int sessionKey;
	
	public Game(int sessionKey, boolean sameStart, int difficulty) throws ConfigurationException, DaoException, SQLException {
		WikiBrainHandler handler = WikiBrainHandler.getInstance();
		target = handler.fetchRandom();
		this.sessionKey = sessionKey;
		if(sameStart) {
			LocalPage start = handler.fetchRandom();
			bot = new BotDB(start.getLocalId(), target.getLocalId(), sessionKey, difficulty);
			player = new PlayerDB(start.getLocalId(), target.getLocalId(), false, sessionKey);
		} else {			
			bot = new BotDB(target.getLocalId(), sessionKey, difficulty);
			player = new PlayerDB(target.getLocalId(), false, sessionKey);
		}
	}
	
	public BotDB getBot() throws ConfigurationException {
		return bot;
	}
	
	public PlayerDB getPlayer() {
		return player;
	}
	
	public String getTargetTitle() {
		return target.getTitle().getTitleStringWithoutNamespace(); 
	}
	
	public String getStatus() throws SQLException, DaoException {
		if(player.hasReachedTarget() && !bot.hasReachedTarget()) {
			return "player";
		} else if(player.hasReachedTarget() && bot.hasReachedTarget()) {
			return "tie";
		} else if(!player.hasReachedTarget() && bot.hasReachedTarget()) {
			return "bot";
		}
		return "ongoing";
	}
	
	public boolean isOver() throws SQLException, DaoException {
		return bot.hasReachedTarget() || player.hasReachedTarget();
	}
	
}
