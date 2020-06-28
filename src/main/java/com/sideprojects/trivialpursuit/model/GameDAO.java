package com.sideprojects.trivialpursuit.model;

import java.util.List;

public interface GameDAO {

	public Game getActiveGame(String gameCode);
	
	public String createNewGame(String publicOrPrivate);
	
	public List<Player> getAllPlayersInAGame(Game game);
	
	public Player getActivePlayer(Game game);	
	
	public void setActivePlayer(Game game, boolean isCorrectAnswer);
	
	public void setActivePlayerDiceRoll(Game game);
	
	public void setIsAnsweringQuestion(Game game, Boolean isAnsweringQuestion);
	
	public void setHasSelectedCategory(Game game, Boolean hasSelectedCategory);
	
	public void setIsGameActive(String gameCode, Boolean isActive);
	
	public void setEndGameStatus(Game game);
	
	public String getWinner(Game game);
	
	public Game getCompletedGame(String gameCode);
	
	public Game getUnstartedGame(String gameCode);
	
	public Game getGameThatEndedEarly(String gameCode);
	
	public Integer getPlayerCountByGame(String gameCode);
	
	public List<Game> getUnstartedPublicGames();
	
	public List<Game> getActiveGamesByPlayer(Integer user_id);
	
	public List<Game> getCompletedGamesByPlayer(Integer user_id);
	
	public List<Game> getUnstartedGamesByPlayer(Integer user_id);
	
	public void leaveGame(Integer user_id, Integer game_id);
	
	public Game getGameByCode(String gameCode);
	
}
