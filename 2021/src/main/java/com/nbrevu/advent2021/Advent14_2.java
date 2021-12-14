package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.io.Resources;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.CharLongMap;
import com.koloboke.collect.map.hash.HashCharLongMaps;

public class Advent14_2 {
	private final static String IN_FILE="Advent14.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^(.)(.) -> (.)$");
	
	private static class PairInsertion	{
		private final char left;
		private final char right;
		private final char target;
		public PairInsertion(char left,char right,char target)	{
			this.left=left;
			this.right=right;
			this.target=target;
		}
	}
	
	private static class Counters	{
		private final CharLongMap charCounters;
		private final Table<Character,Character,Long> pairCounters;
		private Counters(CharLongMap charCounters,Table<Character,Character,Long> pairCounters)	{
			this.charCounters=charCounters;
			this.pairCounters=pairCounters;
		}
		private static void increase(Table<Character,Character,Long> table,char left,char right,long value)	{
			table.row(left).merge(right,value,(Long a,Long b)->a+b);
		}
		public static Counters getFrom(String template)	{
			CharLongMap charCounters=HashCharLongMaps.newMutableMap();
			Table<Character,Character,Long> pairCounters=HashBasedTable.create();
			charCounters.addValue(template.charAt(0),1);
			for (int i=1;i<template.length();++i)	{
				charCounters.addValue(template.charAt(i),1);
				increase(pairCounters,template.charAt(i-1),template.charAt(i),1);
			}
			return new Counters(charCounters,pairCounters);
		}
		public Counters evolve(List<PairInsertion> pairs)	{
			CharLongMap newCharCounters=HashCharLongMaps.newMutableMap(charCounters);
			Table<Character,Character,Long> newPairCounters=HashBasedTable.create();
			for (PairInsertion p:pairs)	{
				long currentCount=pairCounters.row(p.left).getOrDefault(p.right,0l);
				newCharCounters.addValue(p.target,currentCount);
				increase(newPairCounters,p.left,p.target,currentCount);
				increase(newPairCounters,p.target,p.right,currentCount);
			}
			return new Counters(newCharCounters,newPairCounters);
		}
		public long getCharDiff()	{
			long max=0;
			long min=Long.MAX_VALUE;
			for (LongCursor cursor=charCounters.values().cursor();cursor.moveNext();)	{
				max=Math.max(max,cursor.elem());
				min=Math.min(min,cursor.elem());
			}
			return max-min;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		String template=lines.get(0);
		List<PairInsertion> pairs=new ArrayList<>();
		for (int i=2;i<lines.size();++i)	{
			String line=lines.get(i);
			Matcher m=LINE_PATTERN.matcher(line);
			if (m.matches())	{
				char left=m.group(1).charAt(0);
				char right=m.group(2).charAt(0);
				char target=m.group(3).charAt(0);
				pairs.add(new PairInsertion(left,right,target));
			}	else throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
		}
		Counters counters=Counters.getFrom(template);
		for (int i=0;i<40;++i) counters=counters.evolve(pairs);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(counters.getCharDiff());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
