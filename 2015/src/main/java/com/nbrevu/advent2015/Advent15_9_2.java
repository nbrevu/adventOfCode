package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;

public class Advent15_9_2 {
	private final static String IN_FILE="Advent9.txt";
	
	private final static Pattern DISTANCE_PATTERN=Pattern.compile("^(.+) to (.+) = (\\d+)$");
	
	private static class Graph<T>	{
		private static class Node<T>	{
			public final T destination;
			public final int distance;
			public Node(T destination,int distance)	{
				this.destination=destination;
				this.distance=distance;
			}
		}
		private static class Accumulator	{
			private OptionalInt maximum;
			public Accumulator()	{
				maximum=OptionalInt.empty();
			}
			public void accumulate(int newCycle)	{
				if (maximum.isEmpty()||(newCycle>maximum.getAsInt())) maximum=OptionalInt.of(newCycle);
			}
			public OptionalInt getMaximum()	{
				return maximum;
			}
		}
		private final Multimap<T,Node<T>> edges;
		public Graph()	{
			edges=HashMultimap.create();
		}
		public void addEdge(T loc1,T loc2,int distance)	{
			edges.put(loc1,new Node<>(loc2,distance));
			edges.put(loc2,new Node<>(loc1,distance));
		}
		public OptionalInt maxDistance()	{
			Accumulator distanceAccumulator=new Accumulator();
			Set<T> visited=new HashSet<>();
			for (T key:edges.keySet())	{
				visited.add(key);
				maxDistanceRecursive(key,visited,distanceAccumulator,0);
				visited.remove(key);
			}
			return distanceAccumulator.getMaximum();
		}
		private void maxDistanceRecursive(T current,Set<T> visited,Accumulator distanceAccumulator,int totalDistance)	{
			if (visited.size()==edges.keySet().size())	{
				distanceAccumulator.accumulate(totalDistance);
				return;
			}
			for (Node<T> edge:edges.get(current)) if (!visited.contains(edge.destination))	{
				visited.add(edge.destination);
				maxDistanceRecursive(edge.destination,visited,distanceAccumulator,totalDistance+edge.distance);
				visited.remove(edge.destination);
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		Graph<String> graph=new Graph<>();
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=DISTANCE_PATTERN.matcher(line);
			if (matcher.matches()) graph.addEdge(matcher.group(1),matcher.group(2),Integer.parseInt(matcher.group(3)));
		}
		OptionalInt result=graph.maxDistance();
		if (result.isEmpty()) throw new IllegalArgumentException("No cycles found.");
		System.out.println(result.getAsInt());
	}
}
