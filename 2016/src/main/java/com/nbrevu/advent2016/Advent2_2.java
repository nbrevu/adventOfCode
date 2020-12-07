package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharCharMap;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.hash.HashCharCharMaps;
import com.koloboke.collect.map.hash.HashCharObjMaps;

public class Advent2_2 {
	private final static String IN_FILE="Advent2.txt";
	
	private final static char[] BASE_CHARS=new char[] {'1','2','3','4','5','6','7','8','9','A','B','C','D'};
	
	public static enum Direction	{
		R(new char[] {'1','3','4','4','6','7','8','9','9','B','C','C','D'}),
		U(new char[] {'1','2','1','4','5','2','3','4','9','6','7','8','B'}),
		L(new char[] {'1','2','2','3','5','5','6','7','8','A','A','B','D'}),
		D(new char[] {'3','6','7','8','5','A','B','C','9','A','D','C','D'});
		private final CharCharMap moves;
		private Direction(char[] moves)	{
			this.moves=HashCharCharMaps.newImmutableMap(BASE_CHARS,moves);
		}
		public char move(char x)	{
			return moves.get(x);
		}
		public static CharObjMap<Direction> getMap()	{
			return HashCharObjMaps.newImmutableMap(new char[] {'R','U','L','D'},new Direction[] {R,U,L,D});
		}
	}
	
	public static void main(String[] args) throws IOException	{
		char position='5';
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
