package com.nbrevu.advent2018;

import java.util.Arrays;

public class Advent9_2 {
	// 416 players; last marble is worth 71975 points
	private final static int PLAYERS=416;
	private final static int LAST_MARBLE=71975*100;
	
	private static class Node	{
		public final int id;
		public Node atPositiveDir;	// Also called "counterclockwise". POSITIVE, MOTHERFUCKERS. HASN'T ANY OF YOU EVER STUDIED TRIGONOMETRY?????
		public Node atNegativeDir;	// Also called "clockwise". NEGATIVE, MOTHERFUCKERS. HASN'T ANY OF YOU EVER STUDIED TRIGONOMETRY?????
		public Node(int id)	{
			this.id=id;
			atPositiveDir=this;
			atNegativeDir=this;
		}
		public void insertAtNegative(Node other)	{
			// Fortunately, this manages flawlessly the case where [0] gets updated into [0,1].
			Node after=atNegativeDir;
			other.atPositiveDir=this;
			other.atNegativeDir=after;
			this.atNegativeDir=other;	// "this" is not necessary here, but it makes the meaning clearer.
			after.atPositiveDir=other;
		}
		public int removeFromChain()	{
			Node before=atNegativeDir;
			Node after=atPositiveDir;
			before.atPositiveDir=after;
			after.atNegativeDir=before;
			return id;
		}
	}
	
	public static void main(String[] args)	{
		// OOOOH, an overflow! Come here, long.
		long[] scores=new long[PLAYERS];
		Node currentNode=new Node(0);
		for (int i=1;i<=LAST_MARBLE;++i) if ((i%23)!=0)	{
			Node newNode=new Node(i);
			currentNode.atNegativeDir.insertAtNegative(newNode);
			currentNode=newNode;
		}	else	{
			int currentPlayer=(i%PLAYERS);
			for (int j=0;j<6;++j) currentNode=currentNode.atPositiveDir;
			scores[currentPlayer]+=i+currentNode.atPositiveDir.removeFromChain();
		}
		long result=Arrays.stream(scores).max().getAsLong();
		System.out.println(result);
	}
}
