package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

public class Advent7_2 {
	private final static String IN_FILE="Advent7.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^([^\\s]+) \\((\\d+)\\)( -> (.*))?$");
	
	private static class Program	{
		public final String name;
		public final int weight;
		public Program(String name,int weight)	{
			this.name=name;
			this.weight=weight;
		}
	}
	
	private static Pair<Program,Multimap<Program,Program>> getAccumulatedGraph(Multimap<Program,Program> in,Program root)	{
		Multimap<Program,Program> result=HashMultimap.create();
		Program newRoot=getAccumulatedGraphRecursive(in,root,result);
		return Pair.of(newRoot,result);
	}
	
	private static Program getAccumulatedGraphRecursive(Multimap<Program,Program> in,Program root,Multimap<Program,Program> result)	{
		int totalWeight=root.weight;
		Collection<Program> subPrograms=in.get(root);
		List<Program> newPrograms=new ArrayList<>();
		for (Program p:subPrograms)	{
			Program newP=getAccumulatedGraphRecursive(in,p,result);
			totalWeight+=newP.weight;
			newPrograms.add(newP);
		}
		Program newRoot=new Program(root.name,totalWeight);
		result.putAll(newRoot,newPrograms);
		return newRoot;
	}
	
	private static Multimap<Program,Program> translateGraph(Multimap<String,String> baseGraph,Map<String,Program> index)	{
		Multimap<Program,Program> result=HashMultimap.create();
		for (Map.Entry<String,String> entry:baseGraph.entries()) result.put(index.get(entry.getKey()),index.get(entry.getValue()));
		return result;
	}
	
	private static int findBalance(Multimap<Program,Program> cumulativeGraph,Program rootProgram)	{
		return findBalanceRecursive(cumulativeGraph,rootProgram,-1);
	}
	
	private static Pair<Program,Integer> analyseUnbalancedGraph(Multimap<Integer,Program> childrenByWeight)	{
		// At this point we know that the map has exactly two different keys.
		Iterator<Integer> keys=childrenByWeight.keySet().iterator();
		Integer firstKey=keys.next();
		Integer secondKey=keys.next();
		Collection<Program> values1=childrenByWeight.get(firstKey);
		Collection<Program> values2=childrenByWeight.get(secondKey);
		if (values1.size()==1)	{
			if (values2.size()<=1) throw new IllegalStateException("Can't balance. Ambiguous case.");
			return Pair.of(values1.iterator().next(),secondKey);
		}	else if (values2.size()==1)	{
			if (values1.size()<=1) throw new IllegalStateException("Can't balance. Ambiguous case.");
			return Pair.of(values2.iterator().next(),firstKey);
		}	else throw new IllegalStateException("Can't balance, too many diferent weights.");
	}
	
	private static int findBalanceRecursive(Multimap<Program,Program> cumulativeGraph,Program rootProgram,int expectedValue)	{
		Multimap<Integer,Program> childrenByWeight=HashMultimap.create();
		for (Program p:cumulativeGraph.get(rootProgram)) childrenByWeight.put(p.weight,p);
		int weights=childrenByWeight.keySet().size();
		if (weights==1)	{
			// We've found the unbalanced program. But the children are balanced.
			int childWeight=childrenByWeight.keySet().iterator().next();
			int totalChildWeight=childWeight*childrenByWeight.size();
			return expectedValue-totalChildWeight;
		}	else if (weights>2) throw new IllegalStateException("Can't balance, too many different weights.");
		Pair<Program,Integer> problematicCase=analyseUnbalancedGraph(childrenByWeight);
		return findBalanceRecursive(cumulativeGraph,problematicCase.getLeft(),problematicCase.getRight());
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		Set<String> sources=new HashSet<>();
		Set<String> sinks=new HashSet<>();
		Map<String,Program> programIndex=new HashMap<>();
		Multimap<String,String> baseGraph=HashMultimap.create();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				String programName=matcher.group(1);
				sources.add(programName);
				int weight=Integer.parseInt(matcher.group(2));
				if (matcher.group(3)!=null) for (String s:matcher.group(4).split(", "))	{
					sinks.add(s);
					baseGraph.put(programName,s);
				}
				Program p=new Program(programName,weight);
				programIndex.put(programName,p);
			}
		}
		String rootName=Sets.difference(sources,sinks).iterator().next();
		Program rootProgram=programIndex.get(rootName);
		Multimap<Program,Program> programGraph=translateGraph(baseGraph,programIndex);
		Pair<Program,Multimap<Program,Program>> cumulativeGraphData=getAccumulatedGraph(programGraph,rootProgram);
		Program cumulativeRoot=cumulativeGraphData.getLeft();
		Multimap<Program,Program> cumulativeGraph=cumulativeGraphData.getRight();
		int result=findBalance(cumulativeGraph,cumulativeRoot);
		System.out.println(result);
	}
}
