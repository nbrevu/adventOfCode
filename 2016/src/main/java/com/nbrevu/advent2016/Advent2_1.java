package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;

public class Advent2_1 {
	private final static String IN_FILE="Advent2.txt";
	
	public static enum Direction	{
		R(new int[] {0,2,3,3,5,6,6,8,9,9}),
		U(new int[] {0,1,2,3,1,2,3,4,5,6}),
		L(new int[] {0,1,1,2,4,4,5,7,7,8}),
		D(new int[] {0,4,5,6,7,8,9,7,8,9});
		private final int[] moves;
		private Direction(int[] moves)	{
			this.moves=moves;
		}
		public int move(int x)	{
			return moves[x];
		}
		public static CharObjMap<Direction> getMap()	{
			return HashCharObjMaps.newImmutableMap(new char[] {'R','U','L','D'},new Direction[] {R,U,L,D});
		}
	}
	
	public static void main(String[] args) throws IOException	{
		int position=5;
		CharObjMap<Direction> objMap=Direction.getMap();
		URL file=Resources.getResource(IN_FILE);
		StringBuilder result=new StringBuilder();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			int len=line.length();
			for (int i=0;i<len;++i)	{
				Direction dir=objMap.get(line.charAt(i));
				position=dir.move(position);
			}
			result.append(position);
		}
		System.out.println(result.toString());
	}
}
