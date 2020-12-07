package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharIntCursor;
import com.koloboke.collect.map.CharIntMap;
import com.koloboke.collect.map.hash.HashCharIntMaps;

public class Advent6_2 {
	private final static String IN_FILE="Advent6.txt";
	
	private static class RepetitionFinder	{
		private final CharIntMap[] counters;
		public RepetitionFinder(String str)	{
			this(str.length());
			add(str);
		}
		public RepetitionFinder(int length)	{
			counters=new CharIntMap[length];
			for (int i=0;i<length;++i) counters[i]=HashCharIntMaps.newMutableMap();
		}
		public void add(String str)	{
			for (int i=0;i<counters.length;++i) counters[i].addValue(str.charAt(i),1,0);
		}
		public String getResult()	{
			StringBuilder sb=new StringBuilder();
			for (CharIntMap c:counters) sb.append(leastFrequentChar(c));
			return sb.toString();
		}
		private static char leastFrequentChar(CharIntMap c)	{
			List<Character> list=new ArrayList<>();
			int currentValue=Integer.MAX_VALUE;
			for (CharIntCursor cursor=c.cursor();cursor.moveNext();) if (cursor.value()<currentValue)	{
				list.clear();
				list.add(cursor.key());
				currentValue=cursor.value();
			}	else if (cursor.value()==currentValue) list.add(cursor.key());
			if (list.size()!=1) throw new IllegalStateException("Un novio, ¿dos novios?");
			return list.get(0);
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		RepetitionFinder result=null;
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (result==null) result=new RepetitionFinder(line);
		else result.add(line);
		System.out.println(result.getResult());
	}
}
