package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharIntMap;
import com.koloboke.collect.map.hash.HashCharIntMaps;

public class Advent4_2 {
	private final static String IN_FILE="Advent4.txt";
	
	private static CharIntMap getAnagramRepresentation(String line)	{
		CharIntMap result=HashCharIntMaps.newMutableMap();
		for (int i=0;i<line.length();++i) result.addValue(line.charAt(i),1,0);
		return result;
	}
	
	private static boolean isValid(String line)	{
		String[] split=line.split(" ");
		Set<CharIntMap> anagrams=new HashSet<>();
		for (String s:split)	{
			CharIntMap anagram=getAnagramRepresentation(s);
			if (anagrams.contains(anagram)) return false;
			else anagrams.add(anagram);
		}
		return true;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int count=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (isValid(line)) ++count;
		System.out.println(count);
	}
}
