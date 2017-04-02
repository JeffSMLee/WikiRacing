package com.jeffrey.wikiracing;
import java.util.List;

import org.wikibrain.conf.ConfigurationException;
import org.wikibrain.core.dao.DaoException;
import org.wikibrain.core.model.LocalLink;
import org.wikibrain.core.model.LocalPage;


public class WikiRacer extends Player {
	
	public enum PickStatus {
		SUCCESS("Success"), FAILED("Failed");
		
		private String msg;
		
		private PickStatus(String msg) {
			this.msg = msg;
		}
		
		public String getMsg() {
			return msg;
		}
	};
	
	public WikiRacer(LocalPage target) throws ConfigurationException,
			DaoException {
		super(target);
		System.out.println("Player has started on: " + getCurrentPage());
	}
	
	public WikiRacer(LocalPage start, LocalPage target) throws ConfigurationException,
			DaoException {
		super(start, target);
	}

	public PickStatus pick(LocalLink link) throws DaoException {
		System.out.println("Player has picked link: " + link);
		setCurrentPage(getHandler().getLocalPageByID(link.getDestId(), link.getLanguage()));
		System.out.println("Player is currently on: " + getCurrentPage());
		addToPath(getCurrentPage());
		if(hasWon()) {
			System.out.println("Player has reached the target.");
		}
		if(getCurrentPage() == getHandler().getLocalPageByID(link.getDestId(), link.getLanguage()) 
				&& getCurrentPage() == getPath().get(getPath().size())) {
			return PickStatus.SUCCESS;
		}
		return PickStatus.FAILED;
	}
	
	public PickStatus pick(int id) throws DaoException {
		LocalLink link = getChoices().get(id);
		System.out.println("Player has picked link: " + link);
		setCurrentPage(getHandler().getLocalPageByID(link.getDestId(), link.getLanguage()));
		System.out.println("Player is currently on: " + getCurrentPage());
		addToPath(getCurrentPage());
		if(hasWon()) {
			System.out.println("Player has reached the target.");
		}
		if(getCurrentPage() == getHandler().getLocalPageByID(link.getDestId(), link.getLanguage()) 
				&& getCurrentPage() == getPath().get(getPath().size())) {
			return PickStatus.SUCCESS;
		}
		return PickStatus.FAILED;
		
	}
	
}
