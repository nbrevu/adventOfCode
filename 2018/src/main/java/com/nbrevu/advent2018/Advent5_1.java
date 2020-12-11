package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent5_1 {
	private final static String IN_FILE="Advent5.txt";
	
	private final static int CAPS_DIFF='a'-'A';
	
	private static String reduce(String str)	{
		for (;;)	{
			NavigableSet<Integer> pairPositions=new TreeSet<>();	// Stores the start. If [x] is stored, [x] and [x+1] must be deleted.
			for (int i=0;i<str.length()-1;++i) if (Math.abs(str.charAt(i)-str.charAt(i+1))==CAPS_DIFF)	{
				pairPositions.add(i);
				++i;
			}
			if (pairPositions.isEmpty()) return str;
			StringBuilder newStr=new StringBuilder();
			int currentPos=0;
			for (int pos:pairPositions)	{
				if (pos>currentPos) newStr.append(str.substring(currentPos,pos));
				currentPos=pos+2;
			}
			if (currentPos<str.length()) newStr.append(str.substring(currentPos));
			str=newStr.toString();
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		String line=Resources.readLines(file,Charsets.UTF_8).get(0);
		String reduced=reduce(line);
		System.out.println(reduced.length());
	}
}
