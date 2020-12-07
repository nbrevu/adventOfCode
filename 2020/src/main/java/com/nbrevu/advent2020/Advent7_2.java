package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.io.Resources;

public class Advent7_2 {
	private final static String IN_FILE="Advent7.txt";
	private final static String GOAL="shiny gold";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^(.+) bags contain (.+).$");
	private final static Pattern SUB_CONTENT=Pattern.compile("^(\\d+) (.+) bags?");
	
	private static class Bag	{
		public final String colour;
		public final int amount;
		public Bag(String colour,int amount)	{
			this.colour=colour;
			this.amount=amount;
		}
	}
	
	private static class Graph	{
		private ListMultimap<String,Bag> edges;
		public Graph()	{
			edges=MultimapBuilder.treeKeys().arrayListValues().build();
		}
		public void addEdges(String a,List<Bag> b)	{
			edges.putAll(a,b);
		}
		public List<Bag> getEdgesFrom(String a)	{
			return edges.get(a);
		}
	}
	
	private static List<Bag> parseContents(String content)	{
		if (content.equals("no other bags")) return Collections.emptyList();
		List<Bag> result=new ArrayList<>();
		for (String s:content.split(", "))	{
			Matcher matcher=SUB_CONTENT.matcher(s);
			if (!matcher.matches()) throw new IllegalArgumentException("Pues no.");
			result.add(new Bag(matcher.group(2),Integer.parseInt(matcher.group(1))));
		}
		return result;
	}
	
	private static long countBags(Graph graph,String colour)	{
		long result=1;
		for (Bag b:graph.getEdgesFrom(colour))	{
			result+=b.amount*countBags(graph,b.colour);
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		Graph graph=new Graph();
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (!line.isBlank())	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				String firstColour=matcher.group(1);
				List<Bag> contents=parseContents(matcher.group(2));
				graph.addEdges(firstColour,contents);
			}
		}
		// Again, the starting one doesn't count.
		long result=countBags(graph,GOAL)-1;
		System.out.println(result);
	}
}
