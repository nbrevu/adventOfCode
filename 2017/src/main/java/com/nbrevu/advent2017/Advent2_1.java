package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent2_1 {
	private final static String IN_FILE="Advent2.txt";
	
	private static int lineValue(String line)	{
		String[] split=line.split("\t");
		int min=Integer.MAX_VALUE;
		int max=Integer.MIN_VALUE;
		for (String s:split)	{
			int val=Integer.parseInt(s);
			min=Math.min(min,val);
			max=Math.max(max,val);
		}
		return max-min;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int result=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8)) result+=lineValue(line);
		System.out.println(result);
	}
}
