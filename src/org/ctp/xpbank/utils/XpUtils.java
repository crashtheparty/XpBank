package org.ctp.xpbank.utils;

import org.bukkit.entity.Player;

public class XpUtils {
	
	/**
	 * Calculates a player's total exp based on level and progress to next.
	 * @see http://minecraft.gamepedia.com/Experience#Leveling_up
	 * 
	 * @param player the Player
	 * 
	 * @return the amount of exp the Player has
	 */
	public static int getExp(Player player) {
		return getExpFromLevel(player.getLevel())
				+ Math.round(getExpToNext(player.getLevel()) * player.getExp());
	}

	/**
	 * Calculates total experience based on level.
	 * 
	 * @see http://minecraft.gamepedia.com/Experience#Leveling_up
	 * 
	 * "One can determine how much experience has been collected to reach a level using the equations:
	 * 
	 *  Total Experience = [Level]2 + 6[Level] (at levels 0-15)
	 *                     2.5[Level]2 - 40.5[Level] + 360 (at levels 16-30)
	 *                     4.5[Level]2 - 162.5[Level] + 2220 (at level 31+)"
	 * 
	 * @param level the level
	 * 
	 * @return the total experience calculated
	 */
	private static int getExpFromLevel(int level) {
		if (level > 30) return (int) (4.5 * level * level - 162.5 * level + 2220);
		if (level > 15) return (int) (2.5 * level * level - 40.5 * level + 360);
		return level * level + 6 * level;
	}

	/**
	 * Calculates level based on total experience.
	 * 
	 * @param exp the total experience
	 * 
	 * @return the level calculated
	 */
	public static float getLevelFromExp(long exp) {
		int i = 0;
		float retValue = 0;
		while(exp > 0) {
			int expToLevel = getExpToNext(i);
			if(exp > expToLevel) {
				exp -= expToLevel;
				i++;
			}else if(exp == expToLevel) {
				retValue = i + 1;
				exp = 0;
				break;
			}else {
				retValue = i + ((float)exp / (float)expToLevel);
				exp = 0;
				break;
			}
		}
		return retValue;
	}

	/**
	 * @see http://minecraft.gamepedia.com/Experience#Leveling_up
	 * 
	 * "The formulas for figuring out how many experience orbs you need to get to the next level are as follows:
	 *  Experience Required = 2[Current Level] + 7 (at levels 0-15)
	 *                        5[Current Level] - 38 (at levels 16-30)
	 *                        9[Current Level] - 158 (at level 31+)"
	 */
	public static int getExpToNext(int level) {
		if (level > 30) return 9 * level - 158;
		if (level > 15) return 5 * level - 38;
		return 2 * level + 7;
	}
	
	/**
	 * Modify the player's levels and return the amount to add or remove from the bank
	 * @param player - the player
	 * @param toChange - the amount of experience the player should have at the end
	 * @param inBank - the amount of experience the player has in the bank
	 * @return - the amount of experience to add to the bank
	 */
	public static int changeExp(Player player, int toChange, int inBank) {
		int playerExp = getExp(player);
		if(toChange == playerExp) return 0;
		
		if(toChange > playerExp) {
			if(inBank == 0) return 0;
			int difference = toChange - playerExp;
			if(difference > inBank) {
				toChange = playerExp + inBank;
				difference = inBank;
			}
			float levelAndExp = getLevelFromExp(toChange);

			int level = (int) levelAndExp;
			player.setLevel(level);
			player.setExp((levelAndExp - level));
			player.setTotalExperience(toChange);
			// removing this much from the bank
			return - difference;
		} else {
			int difference = playerExp - toChange;
			float levelAndExp = getLevelFromExp(toChange);

			int level = (int) levelAndExp;
			player.setLevel(level);
			player.setExp((levelAndExp - level));
			player.setTotalExperience(toChange);
			// adding this much to the bank
			return difference;
		}
	}
	
	public static int getExpForLevel(int level) {
		int exp = 0;
		for(int i = 0; i < level; i++)
			exp += getExpToNext(i);
		return exp;
	}
	
	public static int getExpForLevel(float level) {
		int exp = 0;
		int levelInt = (int) level;
		for(int i = 0; i < levelInt; i++)
			exp += getExpToNext(i);
		
		float percentLevel = level - levelInt;
		
		exp += (getExpToNext(levelInt) * percentLevel);
		
		return exp;
	}

}
