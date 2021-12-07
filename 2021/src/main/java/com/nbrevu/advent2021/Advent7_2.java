package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent7_2 {
	private final static String IN_FILE="Advent7.txt";
	
	private static long getTriangularDistance(int[] numbers,int center)	{
		long result=0;
		for (int n:numbers)	{
			long diff=Math.abs(n-center);
			result+=(diff*(diff+1))/2;
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		IntStream.Builder builder=IntStream.builder();
		for (String s:lines.get(0).split(",")) builder.accept(Integer.parseInt(s));
		int[] numbers=builder.build().toArray();
		int sum=0;
		for (int n:numbers) sum+=n;
		int mean1=sum/numbers.length;
		int mean2=mean1+1;
		long result=Math.min(getTriangularDistance(numbers,mean1),getTriangularDistance(numbers,mean2));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
