package com.sideprojects.trivialpursuit.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.auth0.SessionUtils;
import com.sideprojects.trivialpursuit.model.Category;
import com.sideprojects.trivialpursuit.model.Dice;
import com.sideprojects.trivialpursuit.model.Game;
import com.sideprojects.trivialpursuit.model.GameDAO;
import com.sideprojects.trivialpursuit.model.InvitationForm;
import com.sideprojects.trivialpursuit.model.Player;
import com.sideprojects.trivialpursuit.model.PlayerDAO;
import com.sideprojects.trivialpursuit.model.Space;
import com.sideprojects.trivialpursuit.model.User;
import com.sideprojects.trivialpursuit.model.UserDAO;


@Controller 
public class GameboardController {
	
	@Autowired
	private PlayerDAO playerDAO;
	
	@Autowired
	private GameDAO gameDAO;
	
	@Autowired
	private UserDAO userDAO;

	@RequestMapping(path="/gameboard/{gameCode}", method=RequestMethod.GET)
	public String displayGameboard(
			ModelMap model,
			Model modelHolder,
			@PathVariable String gameCode,
			final HttpServletRequest req) {

	    String userIdToken = (String) SessionUtils.get(req, "userIdToken");
	    User currentUser = userDAO.getUserByToken(userIdToken);
	    if (currentUser == null) return "redirect:/";
	    
	    model.put("currentUser", currentUser);
		
		Game currentGame = gameDAO.getActiveGame(gameCode);	
		
		
		// TODO: Refactor this, or take a different approach. - Brooks
		// The goal is to make the HUD work easier -- if the game hasn't started, display the invitation form; if started/active, display the dice, etc; if finished, display the winner
        if (currentGame == null) {
        	currentGame = gameDAO.getUnstartedGame(gameCode);
        	if (currentGame == null) {
                currentGame = gameDAO.getCompletedGame(gameCode);
                if (currentGame == null) {
                	currentGame = gameDAO.getGameThatEndedEarly(gameCode);
                	if (currentGame == null) {
                		//TODO: Change this behavior to reference an error-handling / "Game Not Found" JSP.
                		return "redirect:/lobbies";
                	} else {
                		model.put("earlyEnder", currentGame);
                	}               	
                } else {
                	 model.put("completedGame", currentGame);
                	 String winner = gameDAO.getWinner(currentGame);
                	 model.put("winner", winner);
                }
        	} else {
        		model.put("unstartedGame", currentGame);
        	}
        } else {
            model.put("currentGame", currentGame);    
            if (currentGame.getActivePlayer().getIsAnsweringQuestion()) { //TODO: also check if space is Center & hasSelectedCantegoryCenter = false (??) - Brooks
                return "redirect:/question/" + gameCode;
            }     
        }
        
		List<Player> playersInGame = currentGame.getActivePlayers();
		model.put("playersInGame", playersInGame);
		
		Player currentPlayerTurn = currentGame.getActivePlayer();
		model.put("currentPlayerTurn", currentPlayerTurn);
		
		List<Category> gameCategories = currentGame.getCategories();
		model.put("gameCategories", gameCategories);
		
		List<Space> reachableSpaces = currentPlayerTurn.getReachableSpaces(currentGame.getGameboard());
		model.put("reachableSpaces", reachableSpaces);
		
		boolean questionHUD = true;
		model.put("questionHUD", questionHUD);
			
		return "gameboard";
	
	}
	
	@RequestMapping(path="/gameboard/{gameCode}", method=RequestMethod.POST)
	public String displayGameboardWithPlayers(ModelMap model,
			@RequestParam(name = "spaceChoice", required = false) Integer spaceChoice,
			@PathVariable String gameCode,
			final HttpServletRequest req) {
		
	    String userIdToken = (String) SessionUtils.get(req, "userIdToken");
	    User currentUser = userDAO.getUserByToken(userIdToken);
	    if (currentUser == null) return "redirect:/";
		
		Game currentGame = gameDAO.getActiveGame(gameCode);
		Player currentPlayerTurn = gameDAO.getActivePlayer(currentGame);
		Space updatedPlayerSpace = null;
		
		if (spaceChoice != null) {
			updatedPlayerSpace = currentGame.getGameboard().getSpaces().get(spaceChoice);
			currentPlayerTurn.setLocation(updatedPlayerSpace);
			playerDAO.setPlayerPosition(currentGame, currentPlayerTurn);
		}
		
		if (updatedPlayerSpace.isRollAgain()) {
			gameDAO.setActivePlayerDiceRoll(currentGame);
			return "redirect:/gameboard/" + gameCode;
		}
				
		return "redirect:/question/" + gameCode;
	}
	
	@RequestMapping(path="/startGame", method=RequestMethod.POST)
	public String startGame(@RequestParam String gameCode, final HttpServletRequest req) {
		
	    String userIdToken = (String) SessionUtils.get(req, "userIdToken");
	    User currentUser = userDAO.getUserByToken(userIdToken);
	    if (currentUser == null) return "redirect:/";
		
		gameDAO.setIsGameActive(gameCode, true);
		return "redirect:/gameboard/" + gameCode;
	}
	
	@RequestMapping(path="/endGame", method=RequestMethod.POST) 
	public String endGame(@RequestParam String gameCode, final HttpServletRequest req) {
		
	    String userIdToken = (String) SessionUtils.get(req, "userIdToken");
	    User currentUser = userDAO.getUserByToken(userIdToken);
	    if (currentUser == null) return "redirect:/";
		
		gameDAO.setIsGameActive(gameCode, false);
		return "redirect:/profile/" + currentUser.getUsername();
	}
	
	@RequestMapping(path="/leaveGame", method=RequestMethod.POST)
	public String leaveGame(@RequestParam Integer game_id, final HttpServletRequest req) {
		
	    String userIdToken = (String) SessionUtils.get(req, "userIdToken");
	    User currentUser = userDAO.getUserByToken(userIdToken);
	    if (currentUser == null) return "redirect:/";
		
		gameDAO.leaveGame(currentUser.getUserId(), game_id);
		return "redirect:/profile/" + currentUser.getUsername();
	}
}