package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent4_1 {
	private final static String IN_FILE="Advent4.txt";
	
	private static boolean isValid(String line)	{
		String[] split=line.split(" ");
		Set<String> words=new HashSet<>();
		for (String s:split) if (words.contains(s)) return false;
		else words.add(s);
		return true;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int count=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (isValid(line)) ++count;
		System.out.println(count);
	}
}
