package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent10_1 {
	private final static String IN_FILE="Advent10.txt";
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		NavigableSet<Long> present=new TreeSet<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) present.add(Long.parseLong(line));
		long currentValue=0;
		int count1=0;
		int count3=1;
		for (long l:present)	{
			if (l==currentValue+1) ++count1;
			else if (l==currentValue+3) ++count3;
			else throw new IllegalArgumentException("This cannot be!");
			currentValue=l;
		}
		System.out.println(count1*count3);
	}
}
