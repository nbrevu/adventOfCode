package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.set.CharSet;
import com.koloboke.collect.set.hash.HashCharSets;

public class Advent61 {
	private final static String IN_FILE="Advent61.txt";
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int count=0;
		CharSet currentChars=HashCharSets.newMutableSet();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (line.isBlank())	{
			count+=currentChars.size();
			currentChars.clear();
		}	else	{
			int len=line.length();
			for (int i=0;i<len;++i) currentChars.add(line.charAt(i));
		}
		count+=currentChars.size();
		System.out.println(count);
	}
}
