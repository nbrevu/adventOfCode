package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.set.hash.HashCharSets;

public class Advent6_2 {
	private final static String IN_FILE="Advent6.txt";
	
	private static Set<Character> intersect(Set<Character> previous,String line)	{
		return previous.stream().filter((Character c)->line.indexOf(c)>=0).collect(Collectors.toSet());
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int count=0;
		boolean initialSet=true;
		Set<Character> currentChars=null;
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (line.isBlank())	{
			count+=currentChars.size();
			currentChars.clear();
			initialSet=true;
		}	else if (initialSet)	{
			currentChars=HashCharSets.newMutableSet(line.toCharArray());
			initialSet=false;
		}	else currentChars=intersect(currentChars,line);
		count+=currentChars.size();
		System.out.println(count);
	}
}
