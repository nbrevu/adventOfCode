package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;

public class Advent6_1 {
	private final static String IN_FILE="Advent6.txt";
	private final static String CENTER_OF_MASS="COM";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^([^\\)]+)\\)([^\\)]+)$");
	
	private static int traverse(Multimap<String,String> orbits,String currentBody,int currentDepth)	{
		int result=currentDepth;
		for (String str:orbits.get(currentBody)) result+=traverse(orbits,str,1+currentDepth);
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		Multimap<String,String> orbits=HashMultimap.create();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				String center=matcher.group(1);
				String orbiting=matcher.group(2);
				orbits.put(center,orbiting);
			}
		}
		int result=traverse(orbits,CENTER_OF_MASS,0);
		System.out.println(result);
	}
}
