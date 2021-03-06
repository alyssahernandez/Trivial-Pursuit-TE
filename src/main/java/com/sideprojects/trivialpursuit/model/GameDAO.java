package com.sideprojects.trivialpursuit.model;

import java.util.List;

public interface GameDAO {

	public Game getActiveGame(String gameCode);
	
	public String createNewGame();
	
	public List<Player> getAllPlayersInAGame(Game game);
	
	public Player getActivePlayer(Game game);	
	
	public void setActivePlayer(Game game, boolean isCorrectAnswer);
	
	public void setActivePlayerDiceRoll(Game game, int diceRoll);
	
	public void setIsAnsweringQuestion(Game game, Boolean isAnsweringQuestion);
	
	public void setHasSelectedCategory(Game game, Boolean hasSelectedCategory);
	
	public void setIsGameActive(Game game, Boolean isActive);
	
	public void setIsGameActive(String gameCode, Boolean isActive);
	
	public void setWinner(Game game);
	
	public String getWinner(Game game);
	
	public Game getCompletedGame(String gameCode);
	
	public Game getUnstartedGame(String gameCode);
	
	// TODO: put these in InvitationDAO / methods in InvitationJDBC, etc.
	public void sendInvitation(String gameCode, String invitee, String invitedBy);
	
	public List<Invitation> getInvitations(String username);
	
	public Integer getPlayerCountByGame(String gameCode);
	
}
