package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.OptionalInt;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent22_1 {
	private final static String IN_FILE="Advent22_1.txt";
	
	private static class CardGame	{
		private final Deque<Integer> player1;
		private final Deque<Integer> player2;
		public CardGame(Deque<Integer> player1,Deque<Integer> player2)	{
			this.player1=player1;
			this.player2=player2;
		}
		public OptionalInt turn()	{
			int n1=player1.pollFirst();
			int n2=player2.pollFirst();
			if (n1>n2)	{
				player1.addLast(n1);
				player1.addLast(n2);
				if (player2.isEmpty()) return OptionalInt.of(getScore(player1));
			}	else	{
				player2.addLast(n2);
				player2.addLast(n1);
				if (player1.isEmpty()) return OptionalInt.of(getScore(player2));
			}
			return OptionalInt.empty();
		}
		private static int getScore(Deque<Integer> collection)	{
			List<Integer> list=new ArrayList<>(collection);
			int result=0;
			for (int i=0;i<list.size();++i) result+=(list.size()-i)*list.get(i);
			return result;
		}
	}

	private static Deque<Integer> getNumbers(List<String> strings)	{
		Deque<Integer> result=new ArrayDeque<>();
		for (String s:strings) result.addLast(Integer.parseInt(s));
		return result;
	}
	
	private static CardGame parse(List<String> content)	{
		int index=content.indexOf("Player 2:");
		if (index<0) throw new IllegalArgumentException("I told you to bring cards, not Orson Scott Card. No homophobes here!");
		Deque<Integer> player1=getNumbers(content.subList(1,index-1));
		Deque<Integer> player2=getNumbers(content.subList(index+1,content.size()));
		return new CardGame(player1,player2);
	}
		
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> content=Resources.readLines(file,Charsets.UTF_8);
		CardGame game=parse(content);
		for (;;)	{
			OptionalInt result=game.turn();
			if (result.isPresent())	{
				System.out.println(result.getAsInt());
				return;
			}
		}
	}
}