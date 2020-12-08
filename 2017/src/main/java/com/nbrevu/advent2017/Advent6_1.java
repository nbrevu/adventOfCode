package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent6_1 {
	private final static String IN_FILE="Advent6.txt";
	
	private static class Array	{
		private final int[] contents;
		public Array(int[] contents)	{
			this.contents=Arrays.copyOf(contents,contents.length);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(contents);
		}
		@Override
		public boolean equals(Object other)	{
			Array a=(Array)other;
			return Arrays.equals(contents,a.contents);
		}
	}
	
	private static int[] parse(String line)	{
		String[] split=line.split("\\s");
		int[] result=new int[split.length];
		for (int i=0;i<split.length;++i) result[i]=Integer.parseInt(split[i]);
		return result;
	}
	
	private static void iterate(int[] array)	{
		int len=array.length;
		int maxPos=0;
		int maxValue=array[0];
		for (int i=1;i<len;++i) if (array[i]>maxValue)	{
			maxPos=i;
			maxValue=array[i];
		}
		int q=maxValue/len;
		int r=maxValue%len;
		array[maxPos]=0;
		for (int i=1;i<=len;++i)	{
			int newIndex=(maxPos+i)%len;
			int toAdd=q+((i<=r)?1:0);
			array[newIndex]+=toAdd;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		String line=Resources.readLines(file,Charsets.UTF_8).get(0);
		int[] contents=parse(line);
		Set<Array> seen=new HashSet<>();
		for (;;)	{
			Array a=new Array(contents);
			if (seen.contains(a)) break;
			seen.add(a);
			iterate(contents);
		}
		System.out.println(seen.size());
	}
}
