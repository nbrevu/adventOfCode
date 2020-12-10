package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent10_2 {
	private final static String IN_FILE="Advent10.txt";
	
	private static long countPaths(NavigableSet<Long> numbers)	{
		long end=numbers.last();
		long prev2=0;
		long prev=0;
		long curr=1;
		for (long i=1;i<=end;++i)	{
			long next=numbers.contains(i)?(prev2+prev+curr):0l;
			prev2=prev;
			prev=curr;
			curr=next;
		}
		return curr;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		NavigableSet<Long> present=new TreeSet<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) present.add(Long.parseLong(line));
		System.out.println(countPaths(present));
	}
}
