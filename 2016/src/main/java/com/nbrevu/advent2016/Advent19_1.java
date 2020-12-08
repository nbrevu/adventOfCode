package com.nbrevu.advent2016;

import java.io.IOException;

public class Advent19_1 {
	private final static int INITIAL_SIZE=3014387;
	
	private static class Node	{
		public final int elf;
		public Node next;
		public Node(int elf)	{
			// Surprisingly, despite its name this variable is not always equal to 11.
			this.elf=elf;
			next=null;
		}
	}
	
	private static Node createAllNodes(int size)	{
		Node[] allNodes=new Node[size];
		allNodes[0]=new Node(1);
		for (int i=1;i<size;++i)	{
			allNodes[i]=new Node(i+1);
			allNodes[i-1].next=allNodes[i];
		}
		allNodes[size-1].next=allNodes[0];
		return allNodes[0];
	}
	
	private static int simulate(Node currentNode)	{
		while (currentNode.next!=currentNode)	{
			currentNode.next=currentNode.next.next;
			currentNode=currentNode.next;
		}
		return currentNode.elf;
	}
	
	public static void main(String[] args) throws IOException	{
		Node linkedList=createAllNodes(INITIAL_SIZE);
		int result=simulate(linkedList);
		System.out.println(result);
	}
}
