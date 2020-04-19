package com.sideprojects.trivialpursuit.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sideprojects.trivialpursuit.model.Category;
import com.sideprojects.trivialpursuit.model.CategoryDAO;
import com.sideprojects.trivialpursuit.model.Dice;
import com.sideprojects.trivialpursuit.model.Game;
import com.sideprojects.trivialpursuit.model.GameDAO;
import com.sideprojects.trivialpursuit.model.Player;
import com.sideprojects.trivialpursuit.model.PlayerDAO;
import com.sideprojects.trivialpursuit.model.Question;
import com.sideprojects.trivialpursuit.model.QuestionDAO;
import com.sideprojects.trivialpursuit.model.Space;

@Controller
public class QuestionController {
	
	@Autowired
	private GameDAO gameDAO;
	
	@Autowired
	private CategoryDAO categoryDAO;
	
	@Autowired
	private QuestionDAO questionDAO;
	
	@Autowired
	private PlayerDAO playerDAO;

	@RequestMapping(path="/question/{gameCode}", method=RequestMethod.GET)
	public String displayQuestion(
			ModelMap model,
			@PathVariable String gameCode) {
		
		Game currentGame = gameDAO.getActiveGame(gameCode);
		model.put("currentGame", currentGame);
		
		Player currentPlayerTurn = gameDAO.getActivePlayer(currentGame);
		model.put("currentPlayerTurn", currentPlayerTurn);
		
		Space currentPlayerSpace = currentPlayerTurn.getLocation();
		model.put("currentPlayerSpace", currentPlayerSpace);
		
		boolean questionHUD = false;
		model.put("questionHUD", questionHUD);
				
		List<Category> gameCategories = currentGame.getUniqueCategories();
		model.put("gameCategories", gameCategories);
		
		/* I think we talked about this, but we should definitely store the current question
		 * being asked in the DB so the user can't refresh and get a new question or whatever.
		 * Then we can pull the current question - Not sure that I would mark the question
		 * as "asked" until it's been answered. The following if block would be if-else if - ALYSSA
		 * 
		if (currentGame.getIsActivePlayerAnsweringQuestion()) {
			Question question = CURRENT QUESTION FROM DB
			model.put("question", question);
		} */
		
		
		if (!currentPlayerSpace.isCenter()) {
				// && !(currentGame.getIsActivePlayerAnsweringQuestion())) { - GOES HERE IF Q IS IN DB - ALYSSA
			Question question = questionDAO.getUnaskedQuestionByCategory(currentGame,
					currentPlayerSpace.getCategory().getCategoryId());
			model.put("question", question);
			gameDAO.setIsAnsweringQuestion(currentGame, true);
			// gameDAO.setHasSelectedCategory(currentGame, false);
		} else if (currentGame.getHasActivePlayerSelectedCategory()) {
			
			/* NEED TO STORE ACTIVE QUESTION IN DB TO RETRIEVE 
			*/
			
			gameDAO.setIsAnsweringQuestion(currentGame, true);
		}
		
		return "question";
	}
	
	@RequestMapping(path="/question/{gameCode}", method=RequestMethod.POST)
	public String submitQuestion(
			@PathVariable String gameCode,
			@RequestParam(name = "categoryChoiceId", required = false) Integer categoryChoiceId,
			@RequestParam(name = "chosenCenterSpaceCategory", required = false, defaultValue = "false") String chosenCenterSpaceCategory,
			@RequestParam(name = "answer", required = false) String answer,
			ModelMap model) {
		
		Game currentGame = gameDAO.getActiveGame(gameCode);		
		Player currentPlayerTurn = gameDAO.getActivePlayer(currentGame);		
		Space currentPlayerSpace = currentPlayerTurn.getLocation();		
		Integer categoryId = null;
		
		if (categoryChoiceId != null) {
			categoryId = categoryChoiceId;
		}
		
		if (currentPlayerSpace.isCenter() && categoryChoiceId != null && chosenCenterSpaceCategory.equals("true")) {
			
			gameDAO.setHasSelectedCategory(currentGame, true);
			currentGame.setHasActivePlayerSelectedCategory(true);
			
			/* Question question = questionDAO.getUnaskedQuestionByCategory(currentGame,
					categoryId);
			 store active question in db

			 */ 
			
			return "redirect:/question/" + currentGame.getGameCode();
		}
		
		if (answer != null) {
			/* another place where we should store the active question because there is nothing
			to compare it to right now - ALYSSA
			boolean isAnswerCorrect = answer.equalsIgnoreCase(activeQuestion).getAnswer());
			*/
			
			boolean isAnswerCorrect = false;
					
			if (answer.equalsIgnoreCase("Inheritance, Encapsulation, Polymorphism") || answer.equalsIgnoreCase("WHERE") || 
					answer.equalsIgnoreCase("Row") || answer.equalsIgnoreCase("@RequestMapping") || 
					answer.equalsIgnoreCase("False") || answer.equalsIgnoreCase("JUnit")) {
				isAnswerCorrect = true;
			}
			
			if (currentPlayerSpace.hasPie() && isAnswerCorrect) {			
				playerDAO.givePlayerPiePiece(currentPlayerSpace.getSpaceId(), currentGame);	
				
				// optimize this later - ALYSSA
				if (currentPlayerSpace.getSpaceId() == 6) {
					currentPlayerTurn.setPie1(true);
				} else if (currentPlayerSpace.getSpaceId() == 18) {
					currentPlayerTurn.setPie2(true);
				} else if (currentPlayerSpace.getSpaceId() == 30) {
					currentPlayerTurn.setPie3(true);
				} else if (currentPlayerSpace.getSpaceId() == 42) {
					currentPlayerTurn.setPie4(true);
				} else if (currentPlayerSpace.getSpaceId() == 54) {
					currentPlayerTurn.setPie5(true);
				} else if (currentPlayerSpace.getSpaceId() == 66) {
					currentPlayerTurn.setPie6(true);
				}
				
			}
			
			if (currentPlayerSpace.isCenter() && isAnswerCorrect && currentPlayerTurn.getAllPies()) {				
				gameDAO.setIsGameActive(currentGame, false);
				currentGame.setActive(false);
			}  
		
		
		// I could call givePlayerPiePiece() in setActivePlayer(), including this conditional with it. You'd just have to call setActivePlayer() as you did below. Would shorten a couple of files. Lmk. - Brooks

			
			chosenCenterSpaceCategory = "false";
			categoryChoiceId = null;
			
			gameDAO.setActivePlayer(currentGame, isAnswerCorrect);
					
	        gameDAO.setHasSelectedCategory(currentGame, false);
	        gameDAO.setIsAnsweringQuestion(currentGame, false);
	        
	        // TODO: another place the dice roll method needs to be tweaked - ALYSSA
	        int diceRoll = Dice.getDiceRoll();
	        currentGame.setActivePlayerRoll(diceRoll);
	        currentPlayerTurn.setDiceRoll(diceRoll);
	        
	        gameDAO.setActivePlayerDiceRoll(currentGame);
	        
			return "redirect:/gameboard/" + currentGame.getGameCode();
        
		}
		
		
		return "redirect:/question/" + currentGame.getGameCode();
	}
		
}
