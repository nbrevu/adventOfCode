package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_171 {
	private final static String IN_FILE="2015/Advent171.txt";
	private final static int GOAL=150;
	
	private static int countCombinations(List<Integer> containerSizes,int goal)	{
		return countCombinationsRecursive(containerSizes,0,goal);
	}
	
	private static int countCombinationsRecursive(List<Integer> containerSizes,int index,int goal)	{
		if (goal==0) return 1;
		else if (goal<0) return 0;
		else if (index>=containerSizes.size()) return 0;
		else return countCombinationsRecursive(containerSizes,1+index,goal)+countCombinationsRecursive(containerSizes,1+index,goal-containerSizes.get(index));
	}
	
	public static void main(String[] args) throws IOException	{
		List<Integer> containerSizes=new ArrayList<>();
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8)) containerSizes.add(Integer.parseInt(line));
		System.out.println(countCombinations(containerSizes,GOAL));
	}
}
