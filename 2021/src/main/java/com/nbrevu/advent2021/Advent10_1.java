package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharIntMap;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.hash.HashCharIntMaps;
import com.koloboke.collect.map.hash.HashCharObjMaps;

public class Advent10_1 {
	private final static String IN_FILE="Advent10.txt";
	
	private static enum Delimiters	{
		PAREN('(',')'),
		SQUARE('[',']'),
		CURLY('{','}'),
		ANGLE('<','>');
		
		public final char opener;
		public final char closer;
		private Delimiters(char opener,char closer)	{
			this.opener=opener;
			this.closer=closer;
		}
		private final static CharObjMap<Delimiters> OPENER_MAP=createOpenerMap();
		private final static CharIntMap SCORE_MAP=createScoreMap();
		private static CharObjMap<Delimiters> createOpenerMap()	{
			CharObjMap<Delimiters> result=HashCharObjMaps.newMutableMap();
			for (Delimiters c:values()) result.put(c.opener,c);
			return result;
		}
		private final static CharIntMap createScoreMap()	{
			CharIntMap result=HashCharIntMaps.newMutableMap();
			result.put(')',3);
			result.put(']',57);
			result.put('}',1197);
			result.put('>',25137);
			return result;
		}
		private static Delimiters getFromOpener(char c)	{
			return OPENER_MAP.get(c);
		}
		private static int getScore(char c)	{
			int result=SCORE_MAP.get(c);
			if (result==0) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			return result;
		}
	}
	
	private static int getFirstIllegalChar(String str)	{
		Stack<Delimiters> openDels=new Stack<>();
		for (int i=0;i<str.length();++i)	{
			char c=str.charAt(i);
			Delimiters del=Delimiters.getFromOpener(c);
			if (del!=null) openDels.push(del);
			else if (openDels.isEmpty()) return Delimiters.getScore(c);
			else	{
				Delimiters d=openDels.pop();
				if (d.closer!=c) return Delimiters.getScore(c);
			}
		}
		return 0;
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		int result=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8)) result+=getFirstIllegalChar(line);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
