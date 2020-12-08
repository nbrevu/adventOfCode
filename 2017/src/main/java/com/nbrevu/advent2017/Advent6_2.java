package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Advent6_2 {
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
		ObjIntMap<Array> seen=HashObjIntMaps.newMutableMap();
		for (;;)	{
			Array a=new Array(contents);
			if (seen.containsKey(a))	{
				int result=seen.size()-seen.getInt(a);
				System.out.println(result);
				return;
			}
			seen.put(a,seen.size());
			iterate(contents);
		}
	}
}
