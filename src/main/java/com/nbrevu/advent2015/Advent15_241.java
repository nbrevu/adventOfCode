package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.OptionalLong;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_241 {
	private final static String IN_FILE="2015/Advent241.txt";
	
	private static <T extends Comparable<T>> boolean nextPermutation(T[] arr)	{
		// Thanks, indy256 from http://codeforces.com/blog/entry/3980.
		for (int i=arr.length-2;i>=0;--i) if (arr[i].compareTo(arr[i+1])<0) for (int j=arr.length-1;;--j) if (arr[j].compareTo(arr[i])>0)	{
			T sw=arr[i];
			arr[i]=arr[j];
			arr[j]=sw;
			for (++i,j=arr.length-1;i<j;++i,--j)	{
				sw=arr[i];
				arr[i]=arr[j];
				arr[j]=sw;
			}
			return true;
		}
		return false;
	}
	
	private static int sumPositions(List<Integer> values,Boolean[] positions)	{
		int result=0;
		for (int i=0;i<positions.length;++i) if (positions[i]) result+=values.get(i).intValue();
		return result;
	}
	
	private static long multiplyPositions(List<Integer> values,Boolean[] positions)	{
		long result=1;
		for (int i=0;i<positions.length;++i) if (positions[i]) result*=values.get(i).intValue();
		return result;
	}
	private static OptionalLong findOptimalValue(List<Integer> values,int len,int goal)	{
		OptionalLong result=OptionalLong.empty();
		Boolean[] positions=new Boolean[values.size()];
		int breakPoint=values.size()-len;
		Arrays.fill(positions,0,breakPoint,Boolean.FALSE);
		Arrays.fill(positions,breakPoint,positions.length,Boolean.TRUE);
		do if (sumPositions(values,positions)==goal)	{
			long newValue=multiplyPositions(values,positions);
			if (result.isEmpty()||(result.getAsLong()>newValue)) result=OptionalLong.of(newValue);
		}	while (nextPermutation(positions));
		return result;
	}

	private static int sumAll(Collection<Integer> ints)	{
		return ints.stream().mapToInt(Integer::intValue).sum();
	}

	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Integer> weights=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (!line.isBlank()) weights.add(Integer.parseInt(line));
		int goal=sumAll(weights)/3;
		for (int i=1;;++i)	{
			OptionalLong result=findOptimalValue(weights,i,goal);
			if (result.isPresent())	{
				System.out.println(result.getAsLong());
				return;
			}
		}
	}
}
