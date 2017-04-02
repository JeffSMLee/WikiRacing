package com.jeffrey.wikiracing;


import java.util.List;
import org.wikibrain.conf.ConfigurationException;
import org.wikibrain.core.dao.DaoException;
import org.wikibrain.core.model.LocalLink;
import org.wikibrain.core.model.LocalPage;

public class Bot extends Player {
	
	public Bot(LocalPage target) throws ConfigurationException, DaoException {
		super(target);
		System.out.println("Bot has started on: " + getCurrentPage());
	}
	
	public Bot(LocalPage start, LocalPage target) throws ConfigurationException, DaoException {
		super(start, target);
		System.out.println("Bot has started on: " + getCurrentPage());
	}
	
	public int makeMove() throws DaoException {
		int choiceID = -1;
		if(!hasWon()) {			
			choiceID = pick(getHandler().getLinks(getCurrentPage(), true));
			System.out.println("Bot is currently on: " 
						+ getCurrentPage().getTitle().getTitleStringWithoutNamespace() + ".");
		}
		if(hasWon()) {
			System.out.println("Bot has reached the target.");
		}
		return choiceID;
	}
	
	public int pick(List<LocalLink> links) throws DaoException {
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
		
		System.out.println("Bot has picked link: " + choice);
		
		setCurrentPage(getHandler().getLocalPageByID(choice.getDestId(), choice.getLanguage()));
		addToPath(getCurrentPage());
		addToVisited(getCurrentPage().getLocalId());
		return links.indexOf(choice);
	}
	
}
