package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;
import java.util.stream.LongStream;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;

public class Advent10_2 {
	private final static String IN_FILE="Advent10.txt";
	
	private static enum Delimiters	{
		PAREN('(',')',1),
		SQUARE('[',']',2),
		CURLY('{','}',3),
		ANGLE('<','>',4);
		
		public final char opener;
		public final char closer;
		public final int score;
		private Delimiters(char opener,char closer,int score)	{
			this.opener=opener;
			this.closer=closer;
			this.score=score;
		}
		private final static CharObjMap<Delimiters> OPENER_MAP=createOpenerMap();
		private static CharObjMap<Delimiters> createOpenerMap()	{
			CharObjMap<Delimiters> result=HashCharObjMaps.newMutableMap();
			for (Delimiters c:values()) result.put(c.opener,c);
			return result;
		}
		private static Delimiters getFromOpener(char c)	{
			return OPENER_MAP.get(c);
		}
	}
	
	private static long getIncompleteScore(String str)	{
		Stack<Delimiters> openDels=new Stack<>();
		for (int i=0;i<str.length();++i)	{
			char c=str.charAt(i);
			Delimiters del=Delimiters.getFromOpener(c);
			if (del!=null) openDels.push(del);
			else if (openDels.isEmpty()) return 0;
			else	{
				Delimiters d=openDels.pop();
				if (d.closer!=c) return 0;
			}
		}
		long result=0;
		while (!openDels.isEmpty())	{
			Delimiters d=openDels.pop();
			result*=5;
			result+=d.score;
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		LongStream.Builder scores=LongStream.builder();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			long score=getIncompleteScore(line);
			if (score!=0) scores.add(score);
		}
		long[] scoresArray=scores.build().sorted().toArray();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(scoresArray[(scoresArray.length-1)/2]);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
