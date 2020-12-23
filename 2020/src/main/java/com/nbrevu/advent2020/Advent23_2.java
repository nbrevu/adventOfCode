package com.nbrevu.advent2020;

import java.io.IOException;
import java.util.BitSet;

import com.google.common.math.IntMath;

public class Advent23_2 {
	private final static String INPUT="712643589";
	private final static int REAL_SIZE=IntMath.pow(10,6);
	private final static int TURNS=IntMath.pow(10,7);
	
	private static class GameStatus	{
		private final int size;
		// Stores the value of the next element. The list is not explicitly stored.
		private int[] nextElements;
		private int currentValue;
		public GameStatus(String initialConfiguration,int size)	{
			this.size=size;
			int strLen=initialConfiguration.length();
			int[] initialChars=new int[strLen];
			for (int i=0;i<strLen;++i) initialChars[i]=initialConfiguration.charAt(i)-'0';
			currentValue=initialChars[0];
			nextElements=new int[1+size];
			for (int i=1;i<strLen;++i) nextElements[initialChars[i-1]]=initialChars[i];
			nextElements[initialChars[strLen-1]]=1+strLen;
			for (int i=strLen+1;i<size;++i) nextElements[i]=i+1;
			nextElements[size]=initialChars[0];
			BitSet cosa=new BitSet(1+size);
			for (int i=0;i<=size;++i) if (cosa.get(nextElements[i])) throw new IllegalStateException("Empezamos mal.");
			else cosa.set(nextElements[i]);
		}
		public void turn()	{
			int pick1=nextElements[currentValue];
			int pick2=nextElements[pick1];
			int pick3=nextElements[pick2];
			int nextTurn=nextElements[pick3];
			int whereToInsert=currentValue;
			do	{
				--whereToInsert;
				if (whereToInsert<=0) whereToInsert=size;
			}	while (isPicked(whereToInsert,pick1,pick2,pick3));
			int afterInsert=nextElements[whereToInsert];
			nextElements[whereToInsert]=pick1;
			nextElements[pick3]=afterInsert;
			nextElements[currentValue]=nextTurn;
			currentValue=nextTurn;
		}
		private static boolean isPicked(int value,int pick1,int pick2,int pick3)	{
			// Doing this "manually" doesn't need iterating over and array or collection and it's therefore much faster.
			return (value==pick1)||(value==pick2)||(value==pick3);
		}
		private long getValue()	{
			int val1=nextElements[1];
			long val2=nextElements[val1];
			return val2*val1;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		GameStatus game=new GameStatus(INPUT,REAL_SIZE);
		for (int i=0;i<TURNS;++i) game.turn();
		System.out.println(game.getValue());
	}
}