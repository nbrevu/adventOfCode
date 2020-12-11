package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent8_1 {
	private final static String IN_FILE="Advent8.txt";
	
	private static class Node	{
		private final int children;
		private final int metadata;
		private int readChildren;
		public Node(int children,int metadata)	{
			this.children=children;
			this.metadata=metadata;
			readChildren=0;
		}
	}
	
	private static int sumMetadata(int[] values)	{
		Stack<Node> pendingNodes=new Stack<>();
		Node current=new Node(values[0],values[1]);
		int readPointer=2;
		int metadata=0;
		for (;;)	{
			if (current.readChildren<current.children)	{
				Node child=new Node(values[readPointer],values[readPointer+1]);
				readPointer+=2;
				pendingNodes.push(current);
				current=child;
			}	else	{
				for (int i=0;i<current.metadata;++i)	{
					metadata+=values[readPointer];
					++readPointer;
				}
				if (pendingNodes.isEmpty()) return metadata;
				current=pendingNodes.pop();
				++current.readChildren;
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int[] values=Pattern.compile(" ").splitAsStream(Resources.readLines(file,Charsets.UTF_8).get(0)).mapToInt(Integer::parseInt).toArray();
		System.out.println(sumMetadata(values));
	}
}
