package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.CharIntMap;
import com.koloboke.collect.map.hash.HashCharIntMaps;

public class Advent2_1 {
	private final static String IN_FILE="Advent2.txt";
	
	private static void countRepetitions(String str,boolean[] result)	{
		result[0]=false;
		result[1]=false;
		CharIntMap charMap=HashCharIntMaps.newMutableMap();
		for (int i=0;i<str.length();++i) charMap.addValue(str.charAt(i),1,0);
		for (IntCursor cursor=charMap.values().cursor();cursor.moveNext();) if (cursor.elem()==2) result[0]=true;
		else if (cursor.elem()==3) result[1]=true;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		boolean[] reps=new boolean[2];
		int count2=0;
		int count3=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			countRepetitions(line,reps);
			if (reps[0]) ++count2;
			if (reps[1]) ++count3;
		}
		int result=count2*count3;
		System.out.println(result);
	}

}
