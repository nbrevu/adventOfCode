package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent8_2 {
	private final static String IN_FILE="Advent8.txt";
	
	private static class Node	{
		private final int childrenAmount;
		private final int metadataAmount;
		private final List<Node> children;
		private final int[] metadata;
		public Node(int childrenAmount,int metadataAmount)	{
			this.childrenAmount=childrenAmount;
			this.metadataAmount=metadataAmount;
			children=new ArrayList<>(childrenAmount);
			metadata=new int[metadataAmount];
		}
	}
	
	private static Node parse(int[] values)	{
		Stack<Node> pendingNodes=new Stack<>();
		Node current=new Node(values[0],values[1]);
		int readPointer=2;
		for (;;)	{
			if (current.children.size()<current.childrenAmount)	{
				Node child=new Node(values[readPointer],values[readPointer+1]);
				readPointer+=2;
				pendingNodes.push(current);
				current=child;
			}	else	{
				for (int i=0;i<current.metadataAmount;++i)	{
					current.metadata[i]=values[readPointer];
					++readPointer;
				}
				if (pendingNodes.isEmpty()) return current;
				Node parent=pendingNodes.pop();
				parent.children.add(current);
				current=parent;
			}
		}
	}
	
	private static int sum(Node n)	{
		if (n.children.isEmpty()) return Arrays.stream(n.metadata).sum();
		int[] nodeValues=n.children.stream().mapToInt(Advent8_2::sum).toArray();
		int result=0;
		for (int childIndex:n.metadata) if ((childIndex>0)&&(childIndex<=nodeValues.length)) result+=nodeValues[childIndex-1];
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int[] values=Pattern.compile(" ").splitAsStream(Resources.readLines(file,Charsets.UTF_8).get(0)).mapToInt(Integer::parseInt).toArray();
		Node rootNode=parse(values);
		System.out.println(sum(rootNode));
	}
}
