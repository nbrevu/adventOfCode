package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Advent2_2 {
	private final static String IN_FILE="Advent2.txt";
	
	private static int lineValue(String line)	{
		String[] split=line.split("\t");
		IntSet ints=HashIntSets.newMutableSet();
		for (String s:split)	{
			int val=Integer.parseInt(s);
			for (IntCursor cursor=ints.cursor();cursor.moveNext();)	{
				int elem=cursor.elem();
				if ((elem%val)==0) return elem/val;
				else if ((val%elem)==0) return val/elem;
			}
			ints.add(val);
		}
		throw new IllegalArgumentException("No.");
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int result=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8)) result+=lineValue(line);
		System.out.println(result);
	}
}
