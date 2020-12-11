package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.io.Resources;

public class Advent7_1 {
	private final static String IN_FILE="Advent7.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^Step ([A-Z]) must be finished before step ([A-Z]) can begin.$");
	
	private static class Graph	{
		private SetMultimap<Character,Character> directGraph;
		private SetMultimap<Character,Character> reverseGraph;
		public Graph()	{
			directGraph=HashMultimap.create();
			reverseGraph=HashMultimap.create();
		}
		public void addEdge(Character prerequisite,Character nextProcess)	{
			directGraph.put(prerequisite,nextProcess);
			reverseGraph.put(nextProcess,prerequisite);
		}
		public NavigableSet<Character> getInitialTasks()	{
			NavigableSet<Character> result=new TreeSet<>();
			for (Character c:directGraph.keySet()) if (!reverseGraph.containsKey(c)) result.add(c);
			return result;
		}
		public Set<Character> getPotentiallyUnlocked(Character c)	{
			return directGraph.get(c);
		}
		public Set<Character> getPrerequisites(Character c)	{
			return reverseGraph.get(c);
		}
	}
	
	private static String getOrderedTasks(Graph g)	{
		StringBuilder result=new StringBuilder();
		Set<Character> finished=new HashSet<>();
		NavigableSet<Character> availableCharacters=g.getInitialTasks();
		while (!availableCharacters.isEmpty())	{
			Character c=availableCharacters.pollFirst();
			result.append(c.charValue());
			finished.add(c);
			for (Character other:g.getPotentiallyUnlocked(c)) if (finished.containsAll(g.getPrerequisites(other))) availableCharacters.add(other);
		}
		return result.toString();
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		Graph g=new Graph();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				char prerequisite=matcher.group(1).charAt(0);
				char nextProcess=matcher.group(2).charAt(0);
				g.addEdge(prerequisite,nextProcess);
			}
		}
		System.out.println(getOrderedTasks(g));
	}
}
