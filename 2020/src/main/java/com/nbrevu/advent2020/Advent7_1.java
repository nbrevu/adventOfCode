package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;
import com.google.common.io.Resources;

public class Advent7_1 {
	private final static String IN_FILE="Advent7.txt";
	private final static String GOAL="shiny gold";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^(.+) bags contain (.+).$");
	private final static Pattern SUB_CONTENT=Pattern.compile("^(\\d+) (.+) bags?");
	
	private static class Graph	{
		private SetMultimap<String,String> edges;
		public Graph()	{
			edges=TreeMultimap.create();
		}
		public void addEdge(String a,String b)	{
			edges.put(a,b);
		}
		public Set<String> getEdgesFrom(String a)	{
			return edges.get(a);
		}
	}
	
	private static Set<String> parseContents(String content)	{
		if (content.equals("no other bags")) return Collections.emptySet();
		Set<String> result=new HashSet<>();
		for (String s:content.split(", "))	{
			Matcher matcher=SUB_CONTENT.matcher(s);
			if (!matcher.matches()) throw new IllegalArgumentException("Pues no.");
			result.add(matcher.group(2));
		}
		return result;
	}
	
	public static int countColoursFrom(Graph graph,String startingColour)	{
		Set<String> visited=new HashSet<>();
		NavigableSet<String> pending=new TreeSet<>();
		pending.add(startingColour);
		while (!pending.isEmpty())	{
			String thisColour=pending.pollFirst();
			visited.add(thisColour);
			for (String s:graph.getEdgesFrom(thisColour)) if (!visited.contains(s)) pending.add(s);
		}
		return visited.size()-1;	// -1 because of the original colour.
	}
	
	public static void main(String[] args) throws IOException	{
		Graph graph=new Graph();
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (!line.isBlank())	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				String firstColour=matcher.group(1);
				Set<String> contents=parseContents(matcher.group(2));
				for (String s:contents) graph.addEdge(s,firstColour);
			}
		}
		int result=countColoursFrom(graph,GOAL);
		System.out.println(result);
	}
}
