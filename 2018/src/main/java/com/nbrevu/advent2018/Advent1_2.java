package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Advent1_2 {
	private final static String IN_FILE="Advent1.txt";
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		int[] values=lines.stream().mapToInt(Integer::parseInt).toArray();
		IntSet partialSums=HashIntSets.newMutableSet();
		int sum=0;
		for (;;) for (int i:values)	{
			sum+=i;
			if (partialSums.contains(sum))	{
				System.out.println(sum);
				return;
			}	else partialSums.add(sum);
		}
	}
}
