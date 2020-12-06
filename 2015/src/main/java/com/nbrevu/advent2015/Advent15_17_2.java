package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_17_2 {
	private final static String IN_FILE="Advent17.txt";
	private final static int GOAL=150;
	
	private static class CombinationCounter	{
		private int minContainers;
		private int counter;
		public CombinationCounter()	{
			minContainers=Integer.MAX_VALUE;
			counter=Integer.MIN_VALUE;
		}
		public boolean isAcceptable(int currentContainers)	{
			return currentContainers<=minContainers;
		}
		public void accumulateCase(int containers)	{
			if (containers==minContainers) ++counter;
			else if (containers<minContainers)	{
				minContainers=containers;
				counter=1;
			}
		}
		public int getCounter()	{
			return counter;
		}
	}
	
	private static int countCombinations(List<Integer> containerSizes,int goal)	{
		CombinationCounter counter=new CombinationCounter();
		countCombinationsRecursive(containerSizes,0,goal,0,counter);
		return counter.getCounter();
	}
	
	private static void countCombinationsRecursive(List<Integer> containerSizes,int index,int goal,int currentUsage,CombinationCounter accumulator)	{
		if (!accumulator.isAcceptable(currentUsage)) return;
		else if (goal==0) accumulator.accumulateCase(currentUsage);
		else if (goal<0) return;
		else if (index>=containerSizes.size()) return;
		else	{
			countCombinationsRecursive(containerSizes,index+1,goal,currentUsage,accumulator);
			countCombinationsRecursive(containerSizes,index+1,goal-containerSizes.get(index),currentUsage+1,accumulator);
		}
	}
	
	public static void main(String[] args) throws IOException	{
		List<Integer> containerSizes=new ArrayList<>();
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8)) containerSizes.add(Integer.parseInt(line));
		System.out.println(countCombinations(containerSizes,GOAL));
	}
}
