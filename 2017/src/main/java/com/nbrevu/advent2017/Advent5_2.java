package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent5_2 {
	private final static String IN_FILE="Advent5.txt";
	
	private static int iterate(int[] array,int position)	{
		int offset=array[position];
		if (offset>=3) --array[position];
		else ++array[position];
		return position+offset;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Integer> ints=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) ints.add(Integer.parseInt(line));
		int[] workingArray=ints.stream().mapToInt(Integer::intValue).toArray();
		int position=0;
		int steps=0;
		while ((position>=0)&&(position<workingArray.length))	{
			position=iterate(workingArray,position);
			++steps;
		}
		System.out.println(steps);
	}
}
