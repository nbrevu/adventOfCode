package com.nbrevu.advent2020;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*
 * Fuck. I miss C++'s splice.
 */
public class Advent23_1 {
	private final static String INPUT="712643589";
	private final static int TURNS=100;
	
	private static class GameStatus	{
		private final LinkedList<Integer> elements;
		public GameStatus(String initialConfiguration)	{
			elements=new LinkedList<>();
			for (int i=0;i<initialConfiguration.length();++i)	{
				int value=initialConfiguration.charAt(i)-'0';
				elements.add(value);
			}
		}
		public void turn()	{
			int current=elements.pollFirst();
			List<Integer> pick=new ArrayList<>(3);
			for (int i=0;i<3;++i) pick.add(elements.pollFirst());
			int nextValue=current;
			do	{
				--nextValue;
				if (nextValue<=0) nextValue=9;
			}	while (pick.contains(nextValue));
			int pos=elements.indexOf(nextValue);
			elements.addAll(pos+1,pick);
			elements.addLast(current);
		}
		public String getStringAfter1()	{
			int index=elements.indexOf(1);
			StringBuilder result=new StringBuilder();
			for (int i:elements.subList(index+1,elements.size())) result.append(i);
			for (int i:elements.subList(0,index)) result.append(i);
			return result.toString();
		}
	}
	
	public static void main(String[] args) throws IOException	{
		GameStatus game=new GameStatus(INPUT);
		for (int i=0;i<TURNS;++i) game.turn();
		System.out.println(game.getStringAfter1());
	}
}