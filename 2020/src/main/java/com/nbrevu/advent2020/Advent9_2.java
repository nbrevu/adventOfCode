package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent9_2 {
	private final static String IN_FILE="Advent9.txt";
	private final static int WINDOW_SIZE=25;
	
	private static boolean matches(long value,long[] array,int begin,int end)	{
		for (int i=begin;i<end;++i) for (int j=i+1;j<end;++j) if (array[i]+array[j]==value) return true;
		return false;
	}
	
	private static long findProblematicNumber(long[] values)	{
		for (int i=WINDOW_SIZE;i<values.length;++i) if (!matches(values[i],values,i-WINDOW_SIZE,i)) return values[i];
		throw new IllegalArgumentException("Ain't it fun when you just can't seem to find your tongue");
	}
	
	private static long sumMinAndMax(long[] values,int begin,int end)	{
		long min=Long.MAX_VALUE;
		long max=Long.MIN_VALUE;
		for (int i=begin;i<=end;++i)	{
			min=Math.min(min,values[i]);
			max=Math.max(max,values[i]);
		}
		return min+max;
	}
	
	private static long findPartialSum(long[] values,long goal)	{
		int size=values.length;
		long[][] dynProg=new long[size][];
		for (int i=0;i<size;++i)	{
			long val=values[i];
			dynProg[i]=new long[i+1];
			dynProg[i][0]=val;
			for (int j=1;j<=i;++j)	{
				dynProg[i][j]=val+dynProg[i-1][j-1];
				if (dynProg[i][j]==goal) return sumMinAndMax(values,i-j,i);
			}
		}
		throw new IllegalArgumentException("Ain't it fun when you know that you're gonna die young");
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		long[] values=Resources.readLines(file,Charsets.UTF_8).stream().mapToLong(Long::parseLong).toArray();
		long problemNumber=findProblematicNumber(values);
		long result=findPartialSum(values,problemNumber);
		System.out.println(result);
	}
}
