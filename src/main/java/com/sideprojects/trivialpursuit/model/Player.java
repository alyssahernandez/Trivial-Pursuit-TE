package com.sideprojects.trivialpursuit.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player implements Comparable<Player> // Comparable (or bubble sort) to be used for Beta to determine player order -- Brooks
{ 
	private Integer playerId;
	private String name;
	private Long color;
	private Space location;
	private boolean pie1;
	private Boolean pie2;
	private Boolean pie3;
	private Boolean pie4;
	private Boolean pie5;
	private Boolean pie6;
	private int diceRoll;

	// Getters & Setters:
	public Integer getPlayerId() { return playerId; }
	public void setPlayerId(Integer playerId) { this.playerId = playerId; }
		
	public String getName() {return name; }
	public void setName(String name) { this.name = name; }
	
	public Long getColor() { return color; }
	public void setColor(Long color) { this.color = color; }
	
	public Space getLocation() { return location;}
	public void setLocation(Space location) {this.location = location; }
	
	public int getNewDiceRoll() {  return diceRoll = generateDiceRoll(); }
	public int getLastDiceRoll() { return diceRoll; } 
	public void setDiceRoll(int diceRoll) { this.diceRoll = diceRoll; } 
	
	public boolean isPie1() { return pie1;}
	public void setPie1(boolean pie1) { this.pie1 = pie1;}
	
	public boolean isPie2() { return pie2;}
	public void setPie2(boolean pie2) { this.pie2 = pie2;}
	
	public boolean isPie3() { return pie3;}
	public void setPie3(boolean pie3) { this.pie3 = pie3;}
	
	public boolean isPie4() { return pie4;}
	public void setPie4(boolean pie4) { this.pie4 = pie4;}
	
	public boolean isPie5() { return pie5;}
	public void setPie5(boolean pie5) { this.pie5 = pie5;}
	
	public boolean isPie6() { return pie6;}
	public void setPie6(boolean pie6) { this.pie6 = pie6;}
	
	public boolean isAllPies() { return isPie1() && isPie2() && isPie3() && isPie4() && isPie5() && isPie6(); }
	
	// Non-getter/setter methods:
	
	public List<Space> getReachableSpaces(Gameboard gameboard) // could also pass in a Game object and call game.getGameboard() within. 
	{
		List<Space> availableSpaces = new ArrayList<>();
		List<Integer> spaceIds = getLocation().getReachableSpaces(getLastDiceRoll()); 
		
		for (Integer id : spaceIds)
			availableSpaces.add(gameboard.getSpaces().get(id));
		
		return availableSpaces;
	}
	
	public int generateDiceRoll()
	{
		int minDiceRoll = 1;
		int maxDiceRoll = 6;
		Random r = new Random();
		diceRoll = r.nextInt((maxDiceRoll - minDiceRoll) + 1) + minDiceRoll;
		return diceRoll;
	}

	// equals used in a JDBC method  & will likely be used elsewhere as we get into Beta - Brooks
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player))
			return false;
		Player p = (Player) obj;
		return this.getPlayerId() == p.getPlayerId();
	}
	@Override
	public int hashCode() {
		return this.getPlayerId().hashCode();
	}
	
	// Will be used in beta for sorting (if no primary sorting algo) - Brooks
	@Override
	public int compareTo(Player p) {
		if (this.getLastDiceRoll() > p.getLastDiceRoll())
			return -1;
		else if (this.getLastDiceRoll() < p.getLastDiceRoll())
			return 1;
		else 
			return 0;
	}
	
	// TODO: Do we need these, or no? Seemed like Alyssa was using the one that returns List<Space> above, and Jeff one of these. Lmk! - Brooks
	public List<Integer> getReachableSpacesV1() { return location.getReachableSpaces(diceRoll); }
	public List<Integer> getReachableSpacesFromRollV1(int diceRoll) { return location.getReachableSpaces(diceRoll); }
	
	public List<Integer> getReachableSpacesV2() { return location.getReachableSpaces().get(diceRoll); }
	public List<Integer> getReachableSpacesFromRollV2(int diceRoll) { return location.getReachableSpaces().get(diceRoll); }

}
