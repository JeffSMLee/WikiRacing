package com.jeffrey.wikiracing;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import org.wikibrain.conf.ConfigurationException;
import org.wikibrain.core.dao.DaoException;
import org.wikibrain.core.lang.Language;
import org.wikibrain.core.model.LocalLink;

import com.jeffrey.handler.WikiBrainHandler;

public class Test {

	public static void main(String[] args) throws ConfigurationException, DaoException, NumberFormatException, SQLException {
		Game game = new Game(57, true, 1);
		while(!game.isOver()) {
			System.out.println("You are on: " + game.getPlayer().getCurrentPage());
			List<LocalLink> choices = game.getPlayer().getChoices(); 
			int i = 0;
			for(LocalLink link : choices) {
				System.out.println(i + ": " + link);
				i++;
			}
			Scanner scan= new Scanner(System.in);
			int in = scan.nextInt();
			game.getPlayer().setCurrentPage(choices.get(in).getDestId());
			
			System.out.println("Bot is on: " + game.getBot().getCurrentPage());
			game.getBot().makeMove();
			
			if(game.isOver()) {
				System.out.println(game.getStatus());
			}
		}
//		System.out.println(game.getBot().getCurrentPage());
//		game.getBot().makeMove();
//		System.out.println(game.getBot().getCurrentPage());
	}
	
}
