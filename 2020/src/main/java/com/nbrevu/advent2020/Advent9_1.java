package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent9_1 {
	private final static String IN_FILE="Advent9.txt";
	private final static int WINDOW_SIZE=25;
	
	private static boolean matches(long value,long[] array,int begin,int end)	{
		for (int i=begin;i<end;++i) for (int j=i+1;j<end;++j) if (array[i]+array[j]==value) return true;
		return false;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		long[] values=Resources.readLines(file,Charsets.UTF_8).stream().mapToLong(Long::parseLong).toArray();
		/*
		 * I had thought about a clever window scheme for this, but the details were tricky so I'm going for the standard O(m*n^2) case.
		 */
		for (int i=WINDOW_SIZE;i<values.length;++i) if (!matches(values[i],values,i-WINDOW_SIZE,i))	{
			System.out.println(values[i]);
			return;
		}
	}
}
