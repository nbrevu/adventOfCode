package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Advent12_1 {
	private final static String IN_FILE="Advent12.txt";
	private final static int ITERATIONS=20;
	
	private final static Pattern INITIAL_LINE_PATTERN=Pattern.compile("^initial state: ([#\\.]+)$");
	private final static Pattern RULE_PATTERN=Pattern.compile("^([#\\.]{5}) => ([#\\.])$");
	
	private static int getRuleId(String str)	{
		// Assumes that str.length() is exactly 5.
		int result=0;
		for (int i=0;i<5;++i)	{
			result+=result;
			if (str.charAt(i)=='#') ++result;
		}
		return result;
	}
	private static int getRuleId(boolean[] array,int start)	{
		int result=0;
		for (int i=0;i<5;++i)	{
			result+=result;
			int index=start+i;
			if ((index>=0)&&(index<array.length)&&(array[start+i])) ++result;
		}
		return result;
	}
	
	private static class PlantRow	{
		private final boolean[] contents;
		private final int offset;
		public PlantRow(boolean[] baseContents)	{
			contents=new boolean[4+baseContents.length];
			System.arraycopy(baseContents,0,contents,2,baseContents.length);
			offset=-2;
		}
		private PlantRow(boolean[] contents,int offset)	{
			this.contents=contents;
			this.offset=offset;
		}
		private PlantRow iterate(IntObjMap<Boolean> rules)	{
			boolean[] newContents=new boolean[4+contents.length];
			for (int i=-2;i<contents.length;++i) newContents[i+4]=rules.get(getRuleId(contents,i));
			return new PlantRow(newContents,offset-2);
		}
		private int getIdSum()	{
			int sum=0;
			for (int i=0;i<contents.length;++i) if (contents[i]) sum+=i+offset;
			return sum;
		}
	}
	
	private static boolean[] parseInitialState(String str)	{
		boolean[] result=new boolean[str.length()];
		for (int i=0;i<str.length();++i) result[i]=(str.charAt(i)=='#');
		return result;
	}
	
	private static void parseRule(String ruleStr,char resultChar,IntObjMap<Boolean> ruleSet)	{
		int index=getRuleId(ruleStr);
		Boolean value=(resultChar=='#');
		if (ruleSet.containsKey(index)) throw new IllegalArgumentException("No puedo entender algo como esto.");
		ruleSet.put(index,value);
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> contents=Resources.readLines(file,Charsets.UTF_8);
		Matcher initialLine=INITIAL_LINE_PATTERN.matcher(contents.get(0));
		if (!initialLine.matches()) throw new IllegalArgumentException("Can't parse.");
		boolean[] initialState=parseInitialState(initialLine.group(1));
		PlantRow row=new PlantRow(initialState);
		IntObjMap<Boolean> rules=HashIntObjMaps.newMutableMap();
		for (String line:contents.subList(2,contents.size()))	{
			Matcher matcher=RULE_PATTERN.matcher(line);
			if (matcher.matches()) parseRule(matcher.group(1),matcher.group(2).charAt(0),rules);
		}
		for (int i=0;i<ITERATIONS;++i) row=row.iterate(rules);
		System.out.println(row.getIdSum());
	}
}
