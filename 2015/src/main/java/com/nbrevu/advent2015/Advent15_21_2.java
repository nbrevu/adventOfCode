package com.nbrevu.advent2015;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.io.Resources;
import com.google.common.math.IntMath;

public class Advent15_21_2 {
	private final static String IN_FILE="Advent21.txt";
	
	private final static int CHARACTER_HP=100;
	
	private static enum Weapon	{
		DAGGER(8,4),SHORTSWORD(10,5),WARHAMMER(25,6),LONGSWORD(40,7),GREATAXE(74,8);
		public final int cost;
		public final int damage;
		private Weapon(int cost,int damage)	{
			this.cost=cost;
			this.damage=damage;
		}
	}
	
	private static enum Armor	{
		NONE(0,0),LEATHER(13,1),CHAINMAIL(31,2),SPLINTMAIL(53,3),BANDEDMAIL(75,4),PLATEMAIL(102,5);
		public final int cost;
		public final int armor;
		private Armor(int cost,int armor)	{
			this.cost=cost;
			this.armor=armor;
		}
	}
	
	private static enum Ring	{
		DAMAGE1(25,1,0),DAMAGE2(50,2,0),DAMAGE3(100,3,0),ARMOR1(20,0,1),ARMOR2(40,0,2),ARMOR3(80,0,3);
		public final int cost;
		public final int damage;
		public final int armor;
		private Ring(int cost,int damage,int armor)	{
			this.cost=cost;
			this.damage=damage;
			this.armor=armor;
		}
	}
	
	private static class CharacterEquipment	{
		private final Weapon weapon;
		private final Armor armor;
		private final List<Ring> rings;
		public CharacterEquipment(Weapon weapon,Armor armor)	{
			this(weapon,armor,Collections.emptyList());
		}
		public CharacterEquipment(Weapon weapon,Armor armor,Ring ring)	{
			this(weapon,armor,Collections.singletonList(ring));
		}
		public CharacterEquipment(Weapon weapon,Armor armor,Ring ring1,Ring ring2)	{
			this(weapon,armor,Arrays.asList(ring1,ring2));
		}
		private CharacterEquipment(Weapon weapon,Armor armor,List<Ring> rings)	{
			this.weapon=weapon;
			this.armor=armor;
			this.rings=rings;
		}
		public int getCost()	{
			int result=weapon.cost+armor.cost;
			for (Ring r:rings) result+=r.cost;
			return result;
		}
		public int getDamage()	{
			int result=weapon.damage;
			for (Ring r:rings) result+=r.damage;
			return result;
		}
		public int getArmor()	{
			int result=armor.armor;
			for (Ring r:rings) result+=r.armor;
			return result;
		}
	}
	
	public static Multimap<Integer,CharacterEquipment> getAllEquipmentCases()	{
		Weapon[] weapons=Weapon.values();
		Armor[] armors=Armor.values();
		Ring[] rings=Ring.values();
		List<CharacterEquipment> equipment=new ArrayList<>();
		for (Weapon w:weapons) for (Armor a:armors)	{
			equipment.add(new CharacterEquipment(w,a));
			for (int i=0;i<rings.length;++i)	{
				equipment.add(new CharacterEquipment(w,a,rings[i]));
				for (int j=i+1;j<rings.length;++j) equipment.add(new CharacterEquipment(w,a,rings[i],rings[j]));
			}
		}
		Comparator<Integer> order=Comparator.reverseOrder();
		Multimap<Integer,CharacterEquipment> result=MultimapBuilder.treeKeys(order).arrayListValues().build();
		for (CharacterEquipment e:equipment) result.put(e.getCost(),e);
		return result;
	}
	
	private static class Boss	{
		public final int hp;
		public final int damage;
		public final int armor;
		public Boss(int hp,int damage,int armor)	{
			this.hp=hp;
			this.damage=damage;
			this.armor=armor;
		}
	}
	
	private static class PlayerCharacter	{
		public final int hp;
		public CharacterEquipment equipment;
		public PlayerCharacter(int hp)	{
			this.hp=hp;
		}
		public void setEquipment(CharacterEquipment equipment)	{
			this.equipment=equipment;
		}
		public int getDamage()	{
			return equipment.getDamage();
		}
		public int getArmor()	{
			return equipment.getArmor();
		}
	}
	
	private static boolean canPlayerWin(PlayerCharacter player,Boss boss)	{
		int playerDamage=Math.max(1,player.getDamage()-boss.armor);
		int bossDamage=Math.max(1,boss.damage-player.getArmor());
		int playerTurnsToWin=IntMath.divide(boss.hp,playerDamage,RoundingMode.UP);
		int bossTurnsToWin=IntMath.divide(player.hp,bossDamage,RoundingMode.UP);
		return playerTurnsToWin<=bossTurnsToWin;
	}
	
	public static void main(String[] args) throws IOException	{
		OptionalInt bossHitPoints=OptionalInt.empty();
		OptionalInt bossDamage=OptionalInt.empty();
		OptionalInt bossArmor=OptionalInt.empty();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		for (String line:lines)	{
			String[] split=line.split(":");
			if (split.length!=2) continue;
			int value=Integer.parseInt(split[1].trim());
			switch (split[0])	{
				case "Hit Points":bossHitPoints=OptionalInt.of(value);break;
				case "Damage":bossDamage=OptionalInt.of(value);break;
				case "Armor":bossArmor=OptionalInt.of(value);break;
				default:throw new IllegalArgumentException("ICH KANN "+split[0]+" NICHT VERSTEHEN!!!!!");
			}
		}
		if (bossHitPoints.isEmpty()||bossDamage.isEmpty()||bossArmor.isEmpty()) throw new IllegalArgumentException("NICHT GENUG!!!!!");
		Boss boss=new Boss(bossHitPoints.getAsInt(),bossDamage.getAsInt(),bossArmor.getAsInt());
		PlayerCharacter player=new PlayerCharacter(CHARACTER_HP);
		Multimap<Integer,CharacterEquipment> equipmentPossibilities=getAllEquipmentCases();
		for (Map.Entry<Integer,CharacterEquipment> attempt:equipmentPossibilities.entries())	{
			player.setEquipment(attempt.getValue());
			if (!canPlayerWin(player,boss))	{
				System.out.println(attempt.getKey());
				return;
			}
		}
	}
}
