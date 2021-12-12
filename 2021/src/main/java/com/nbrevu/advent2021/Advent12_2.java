package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;

public class Advent12_2 {
	private final static String IN_FILE="Advent12.txt";
	
	private static class Node	{
		public final String name;
		public boolean isBig()	{
			return Character.isUpperCase(name.charAt(0));
		}
		public Node(String name)	{
			this.name=name;
		}
		public boolean isEndpoint()	{
			return name.equals("start")||name.equals("end");
		}
	}
	
	private static class Graph	{
		private final Map<String,Node> nodes;
		private final Multimap<Node,Node> edges;
		public Graph()	{
			nodes=new HashMap<>();
			edges=HashMultimap.create();
		}
		public void addEdge(String name1,String name2)	{
			Node n1=nodes.computeIfAbsent(name1,Node::new);
			Node n2=nodes.computeIfAbsent(name2,Node::new);
			edges.put(n1,n2);
			edges.put(n2,n1);
		}
		public Node getStartNode()	{
			return nodes.get("start");
		}
		public Collection<Node> getNextNodes(Node n)	{
			return edges.get(n);
		}
		public class GraphPath	{
			private final Node currentNode;
			private final Set<Node> alreadyVisited;
			private final boolean visitedSomeTwice;
			private GraphPath(Node currentNode,Set<Node> alreadyVisited,boolean visitedSomeTwice)	{
				this.currentNode=currentNode;
				this.alreadyVisited=alreadyVisited;
				this.visitedSomeTwice=visitedSomeTwice;
			}
			public boolean isFinal()	{
				return currentNode.name.equals("end");
			}
			public List<GraphPath> traverse()	{
				List<GraphPath> result=new ArrayList<>();
				for (Node n:getNextNodes(currentNode)) if (n.isBig()) result.add(new GraphPath(n,alreadyVisited,visitedSomeTwice));
				else if (!alreadyVisited.contains(n))	{
					Set<Node> newAlreadyVisited=new HashSet<>(alreadyVisited);
					newAlreadyVisited.add(n);
					result.add(new GraphPath(n,newAlreadyVisited,visitedSomeTwice));
				}	else if (!visitedSomeTwice&&!n.isEndpoint()) result.add(new GraphPath(n,alreadyVisited,true));
				return result;
			}
		}
		public GraphPath getInitialPath()	{
			Node start=getStartNode();
			return new GraphPath(start,Set.of(start),false);
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		Graph g=new Graph();
		for (String edge:lines)	{
			String[] nodes=edge.split("-");
			if (nodes.length!=2) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			g.addEdge(nodes[0].trim(),nodes[1].trim());
		}
		int result=0;
		List<Graph.GraphPath> currentPaths=List.of(g.getInitialPath());
		while (!currentPaths.isEmpty())	{
			List<Graph.GraphPath> nextGen=new ArrayList<>();
			for (Graph.GraphPath path:currentPaths) if (path.isFinal()) ++result;
			else nextGen.addAll(path.traverse());
			currentPaths=nextGen;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
