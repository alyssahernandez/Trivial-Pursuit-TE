package com.sideprojects.trivialpursuit.model;

import java.util.List;

public interface GameDAO {

	public Game getActiveGame(String gameCode);
	
	public void createNewGame(String code); //AC changed return to void added Code arg
	
	public List<Player> getAllPlayersInAGame(Game game);
	
	public Player getActivePlayer(Game game);	
	
	public void setActivePlayer(Game game, boolean isCorrectAnswer);
}
