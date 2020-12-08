package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent22_1 {
	private final static String IN_FILE="Advent22.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^/dev/grid/node\\-x(\\d+)\\-y(\\d+)\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)%$");
	
	private final static class SimplifiedNode	{
		private final int used;
		private final int available;
		public SimplifiedNode(int used,int available)	{
			this.used=used;
			this.available=available;
		}
	}
	
	private static int countViablePairs(List<SimplifiedNode> nodes)	{
		int result=0;
		NavigableMap<Integer,Integer> counters=new TreeMap<>();
		for (SimplifiedNode node:nodes) counters.compute(node.available,(Integer unusedKey,Integer previousValue)->1+((previousValue==null)?0:previousValue.intValue()));
		for (SimplifiedNode node:nodes)	{
			if (node.used==0) continue;
			NavigableMap<Integer,Integer> subMap=counters.tailMap(node.used,true);
			for (int v:subMap.values()) result+=v;
			if (node.available>=node.used) --result;
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<SimplifiedNode> nodes=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				// int xPos=Integer.parseInt(matcher.group(1));
				// int yPos=Integer.parseInt(matcher.group(2));
				// int size=Integer.parseInt(matcher.group(3));
				int used=Integer.parseInt(matcher.group(4));
				int available=Integer.parseInt(matcher.group(5));
				// int percent=Integer.parseInt(matcher.group(6));
				nodes.add(new SimplifiedNode(used,available));
			}
		}
		int result=countViablePairs(nodes);
		System.out.println(result);
	}
}
