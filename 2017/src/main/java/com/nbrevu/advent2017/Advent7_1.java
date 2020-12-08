package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

public class Advent7_1 {
	private final static String IN_FILE="Advent7.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^([^\\s]+) \\((\\d+)\\)( -> (.*))?$");
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		Set<String> sources=new HashSet<>();
		Set<String> sinks=new HashSet<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				String programName=matcher.group(1);
				sources.add(programName);
				// int weight=Integer.parseInt(matcher.group(2));
				if (matcher.group(3)!=null) for (String s:matcher.group(4).split(", ")) sinks.add(s);
			}
		}
		Set<String> diff=Sets.difference(sources,sinks);
		if (diff.size()!=1) System.out.println("Scheiße!");
		else System.out.println(diff.iterator().next());
	}
}
