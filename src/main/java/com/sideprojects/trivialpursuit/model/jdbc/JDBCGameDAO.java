package com.sideprojects.trivialpursuit.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.sideprojects.trivialpursuit.model.CategoryDAO;
import com.sideprojects.trivialpursuit.model.Game;
import com.sideprojects.trivialpursuit.model.Category;
import com.sideprojects.trivialpursuit.model.GameDAO;
import com.sideprojects.trivialpursuit.model.Player;
import com.sideprojects.trivialpursuit.model.PlayerDAO;
import com.sideprojects.trivialpursuit.model.Space;

@Component
public class JDBCGameDAO implements GameDAO {
	
	private JdbcTemplate template;
	
	@Autowired
	private CategoryDAO categoryDAO;

	@Autowired
	public JDBCGameDAO(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
	}
	
	/*
		TODO: 
			- After talking w/ Kiran, 95% sure that createNewGame() going to need a "RETURNING game_id" statement @ end 
			- if our homepage will be setting anything but the gamecode -- categories, players, etc. -- on the same form. 
			- We'll need an immediate reference to the game_id in the controller 
			- (which is only created upon insertion into DB) to update game_player, game_category, game_question, etc.
				- Brooks
	*/
	@Override
	public void createNewGame(String gameCode) {
		
		String newGameId = "INSERT INTO game (game_code, active) VALUES (?, ?)";
		template.update(newGameId, gameCode.toUpperCase(), true);
	}
	
	@Override
	public Game getActiveGame(String gameCode) {
		String getGameQuery = "SELECT * FROM game WHERE game_code = ?";
		SqlRowSet rowSet = template.queryForRowSet(getGameQuery, gameCode);

		Game game = new Game();
		if (rowSet.next()) {
			game.setGameID(rowSet.getInt("game_id"));
			game.setActive(rowSet.getBoolean("active"));
			game.setGameCode(gameCode); 
			game.setWinnerId(rowSet.getInt("winner_id"));
			game.setActivePlayerRoll(rowSet.getInt("active_player_roll"));
			game.setActivePlayerId(rowSet.getInt("active_player_id"));
		}
		
		List<Category> categoriesInGame = categoryDAO.getCategoriesByGame(game);
		game.setCategories(categoriesInGame);
		game.createGameboard(categoriesInGame);

		List<Player> activePlayers = getAllPlayersInAGame(game);
		Player activePlayer = getActivePlayer(game);
		activePlayer.setDiceRoll(game.getActivePlayerRoll());
		game.setActivePlayers(activePlayers);
		game.setActivePlayer(activePlayer);
		
		return game;
	}
	
	//TODO: These should be in the JDBCPlayerDAO/PlayerDAO.
	@Override
	public List<Player> getAllPlayersInAGame(Game game) 
	{
		List<Player> players = new ArrayList<>();                   
		String sqlGetAllPlayers = "SELECT * FROM game_player JOIN player ON game_player.player_id "
				+ "= player.player_id WHERE game_player.game_id = ?";
		SqlRowSet results = template.queryForRowSet(sqlGetAllPlayers, game.getGameID());
		
		while(results.next()) {
			Player player = new Player();
			player.setName(results.getString("name"));
			player.setPlayerId(results.getInt("player_id"));
			player.setLocation(game.getGameboard().getSpaces().get(results.getInt("player_position")));
			player.setColor(results.getLong("player_color"));
			player.setPie1(results.getBoolean("player_score_cat_1"));
			player.setPie2(results.getBoolean("player_score_cat_2"));
			player.setPie3(results.getBoolean("player_score_cat_3"));
			player.setPie4(results.getBoolean("player_score_cat_4"));
			player.setPie5(results.getBoolean("player_score_cat_5"));
			player.setPie6(results.getBoolean("player_score_cat_6"));
			players.add(player);
		}
		
		return players;
	}
	
	@Override
	public Player getActivePlayer(Game game)
	{
		Player player = new Player();
		String query = "SELECT player.*, game_player.*, game.active_player_roll FROM player " + 
				"INNER JOIN game_player ON (player.player_id = game_player.player_id) " + 
				"INNER JOIN game ON (game_player.game_id = game.game_id) WHERE game.game_id = ? AND player.player_id = game.active_player_id";
		
		SqlRowSet results = template.queryForRowSet(query, game.getGameID());
		if (results.next())
		{
			player.setPlayerId(results.getInt("player_id"));
			player.setLocation(game.getGameboard().getSpaces().get(results.getInt("player_position")));
			player.setName(results.getString("name"));
			player.setColor(results.getLong("player_color"));
			player.setPie1(results.getBoolean("player_score_cat_1"));
			player.setPie2(results.getBoolean("player_score_cat_2"));
			player.setPie3(results.getBoolean("player_score_cat_3"));
			player.setPie4(results.getBoolean("player_score_cat_4"));
			player.setPie5(results.getBoolean("player_score_cat_5"));
			player.setPie6(results.getBoolean("player_score_cat_6"));
			player.setDiceRoll(results.getInt("active_player_roll"));
		}
		return player;
	}
	
	@Override
	public void setActivePlayer(Game game, boolean isCorrectAnswer)
	{
		Player activePlayer = game.getActivePlayer();
		Space playerLocation = activePlayer.getLocation();
		
		if (playerLocation.hasPie() && isCorrectAnswer)
		{
			if (playerLocation.getPieId() == 1)
				activePlayer.setPie1(true);
			else if (playerLocation.getPieId() == 2)
				activePlayer.setPie1(true);
			else if (playerLocation.getPieId() == 3)
				activePlayer.setPie1(true);
			else if (playerLocation.getPieId() == 4)
				activePlayer.setPie1(true);
			else if (playerLocation.getPieId() == 5)
				activePlayer.setPie1(true);
			else if (playerLocation.getPieId() == 6)
				activePlayer.setPie1(true);
		}
		
		String query1 = "UPDATE game_player SET player_position = ?, player_score_cat_1 = ?, player_score_cat_2 = ?, player_score_cat_3 = ?, player_score_cat_4 = ?, player_score_cat_5 = ?, player_score_cat_6 = ? WHERE game_id = ? AND player_id = ?";
		template.update(query1, activePlayer.getLocation().getSpaceId(), activePlayer.isPie1(), activePlayer.isPie2(), activePlayer.isPie3(), activePlayer.isPie4(), activePlayer.isPie5(), activePlayer.isPie6(), activePlayer, player_id);

		if (!(isCorrectAnswer))
		{
			int activePlayerIndex = game.getActivePlayers().indexOf(game.getActivePlayer());
			if (activePlayerIndex < game.getActivePlayers().size() - 1)
				activePlayer = game.getActivePlayers().get(activePlayerIndex + 1);
			else
				activePlayer = game.getActivePlayers().get(0);
		}
		
		String query2 = "UPDATE game SET active_player_id = ?, active_player_roll = ? WHERE game_id = ?";
		template.update(query2, activePlayer.getPlayerId(), game.getGameID(), activePlayer.get);
	}
	
	public void setActivePlayerDiceRoll(Player activePlayer)
	{
		String query = "UPDATE game SET active_player_roll = ? WHERE game_id = "
	}
}
