package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent22_2 {
	private final static String IN_FILE="Advent22.txt";
	
	private static class ConfigurationMemory	{
		private final int[] player1;
		private final int[] player2;
		public ConfigurationMemory(LinkedList<Integer> player1,LinkedList<Integer> player2)	{
			this.player1=asArray(player1);
			this.player2=asArray(player2);
		}
		private static int[] asArray(Collection<Integer> collection)	{
			return collection.stream().mapToInt(Integer::intValue).toArray();
		}
		@Override
		public int hashCode()	{
			return player1.hashCode()+player2.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			ConfigurationMemory cmOther=(ConfigurationMemory)other;
			return Arrays.equals(player1,cmOther.player1)&&Arrays.equals(player2,cmOther.player2);
		}
	}
	
	private static class CardGame	{
		private final LinkedList<Integer> player1;
		private final LinkedList<Integer> player2;
		private final Set<ConfigurationMemory> seenConfigurations;
		public CardGame(Collection<Integer> player1,Collection<Integer> player2)	{
			this.player1=new LinkedList<>(player1);
			this.player2=new LinkedList<>(player2);
			seenConfigurations=new HashSet<>();
		}
		private static int highest(List<Integer> l)	{
			int result=Integer.MIN_VALUE;
			for (int i:l) if (i>result) result=i;
			return result;
		}
		private static boolean decideTurn(int n1,int n2,LinkedList<Integer> p1,LinkedList<Integer> p2)	{
			if ((n1<=p1.size())&&(n2<=p2.size()))	{
				List<Integer> subList1=p1.subList(0,n1);
				List<Integer> subList2=p2.subList(0,n2);
				// This hack here saves a lot of time. This is honestly kind of infuriating.
				if (highest(subList1)>highest(subList2)) return true;
				return new CardGame(subList1,subList2).play();
			}	else return n1>n2;
		}
		public boolean play()	{
			// Returns true if player1 wins, false if player2 does.
			for (;;)	{
				ConfigurationMemory currentConfig=new ConfigurationMemory(player1,player2);
				if (seenConfigurations.contains(currentConfig)) return true;
				seenConfigurations.add(currentConfig);
				int n1=player1.pollFirst();
				int n2=player2.pollFirst();
				boolean winner=decideTurn(n1,n2,player1,player2);
				if (winner)	{
					player1.addLast(n1);
					player1.addLast(n2);
					if (player2.isEmpty()) return true;
				}	else	{
					player2.addLast(n2);
					player2.addLast(n1);
					if (player1.isEmpty()) return false;
				}
			}
		}
		private static int getScore(List<Integer> list)	{
			int result=0;
			for (int i=0;i<list.size();++i) result+=(list.size()-i)*list.get(i);
			return result;
		}
		public int getScore1()	{
			return getScore(player1);
		}
		public int getScore2()	{
			return getScore(player2);
		}
	}

	private static List<Integer> getNumbers(List<String> strings)	{
		List<Integer> result=new ArrayList<>();
		for (String s:strings) result.add(Integer.parseInt(s));
		return result;
	}
	
	private static CardGame parse(List<String> content)	{
		int index=content.indexOf("Player 2:");
		if (index<0) throw new IllegalArgumentException("I told you to bring cards, not Orson Scott Card. No homophobes here!");
		List<Integer> player1=getNumbers(content.subList(1,index-1));
		List<Integer> player2=getNumbers(content.subList(index+1,content.size()));
		return new CardGame(player1,player2);
	}
		
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> content=Resources.readLines(file,Charsets.UTF_8);
		CardGame game=parse(content);
		boolean winner=game.play();
		int result=winner?game.getScore1():game.getScore2();
		System.out.println(result);
	}
}