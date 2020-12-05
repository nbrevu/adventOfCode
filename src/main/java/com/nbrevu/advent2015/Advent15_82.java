package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_82 {
	private final static String IN_FILE="2015/Advent81.txt";
	
	private static int count(String literal,char toCount)	{
		int result=0;
		for (int index=literal.indexOf(toCount);index>=0;index=literal.indexOf(toCount,1+index)) ++result;
		return result;
	}
	
	private static int count(String literal)	{
		if (literal.isBlank()) return 0;
		else return 2+count(literal,'\"')+count(literal,'\\');
	}
	
	public static void main(String[] args) throws IOException	{
		int counter=0;
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8)) counter+=count(line);
		System.out.println(counter);
	}
}
