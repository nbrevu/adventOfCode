package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent3_2 {
	private final static String IN_FILE="Advent3.txt";
	
	private final static Pattern SPLITTER_PATTERN=Pattern.compile(" +");
	
	private static boolean isValidTriangle(int[] triangle)	{
		if (triangle.length!=3) return false;
		int sum=triangle[0]+triangle[1]+triangle[2];
		int max=Math.max(triangle[0],Math.max(triangle[1],triangle[2]));
		return (sum-max>max);
	}
	
	public static void main(String[] args) throws IOException	{
		int count=0;
		URL file=Resources.getResource(IN_FILE);
		int[][] triangles=new int[3][3];
		int reading=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			int[] tmp=SPLITTER_PATTERN.splitAsStream(line.trim()).mapToInt(Integer::parseInt).toArray();
			for (int i=0;i<3;++i) triangles[i][reading]=tmp[i];
			if (reading==2)	{
				for (int i=0;i<3;++i) if (isValidTriangle(triangles[i])) ++count;
				reading=0;
			}	else ++reading;
		}
		System.out.println(count);
	}
}
