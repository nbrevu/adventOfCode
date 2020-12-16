package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;

public class Advent6_2 {
	private final static String IN_FILE="Advent6.txt";
	private final static String CENTER_OF_MASS="COM";
	private final static String GOAL1="YOU";
	private final static String GOAL2="SAN";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^([^\\)]+)\\)([^\\)]+)$");
	
	private static String[] findPath(Multimap<String,String> orbits,String origin,String destination)	{
		Deque<String> result=new ArrayDeque<>();
		if (findPathRecursive(orbits,origin,destination,result)) result.addFirst(origin);
		return result.toArray(String[]::new);
	}
	
	private static boolean findPathRecursive(Multimap<String,String> orbits,String currentBody,String destination,Deque<String> path)	{
		for (String str:orbits.get(currentBody)) if (destination.equals(str)||findPathRecursive(orbits,str,destination,path))	{
			path.addFirst(str);
			return true;
		}
		return false;
	}
	
	private static int findOrbitJumps(String[] path1,String[] path2)	{
		int index=0;
		while (path1[index].equals(path2[index])) ++index;
		return path1.length+path2.length-2*(index+1);
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
		String[] path1=findPath(orbits,CENTER_OF_MASS,GOAL1);
		String[] path2=findPath(orbits,CENTER_OF_MASS,GOAL2);
		int result=findOrbitJumps(path1,path2);
		System.out.println(result);
	}
}
