package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent7_1 {
	private final static String IN_FILE="Advent7.txt";
	
	private static int getDistanceTo(int[] numbers,int value)	{
		int result=0;
		for (int n:numbers) result+=Math.abs(n-value);
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		IntStream.Builder builder=IntStream.builder();
		for (String s:lines.get(0).split(",")) builder.accept(Integer.parseInt(s));
		int[] numbers=builder.build().toArray();
		Arrays.sort(numbers);
		int len=numbers.length;
		int result=((len%2)==0)?Math.min(getDistanceTo(numbers,numbers[len/2-1]),getDistanceTo(numbers,numbers[len/2])):getDistanceTo(numbers,numbers[(len-1)/2]);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
