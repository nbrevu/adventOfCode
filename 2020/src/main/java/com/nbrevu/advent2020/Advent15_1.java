package com.nbrevu.advent2020;

import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

public class Advent15_1 {
	private final static int[] INITIAL_NUMBERS=new int[] {19,20,14,0,9,1};
	private final static int GOAL=2020;
	
	public static void main(String[] args)	{
		IntIntMap lastTimes=HashIntIntMaps.newMutableMap();
		for (int i=0;i<INITIAL_NUMBERS.length-1;++i) lastTimes.put(INITIAL_NUMBERS[i],i+1);
		int current=INITIAL_NUMBERS[INITIAL_NUMBERS.length-1];
		for (int i=INITIAL_NUMBERS.length;i<GOAL;++i)	{
			int lastPos=lastTimes.getOrDefault(current,-1);
			lastTimes.put(current,i);
			current=(lastPos<0)?0:(i-lastPos);
		}
		System.out.println(current);
	}
}
