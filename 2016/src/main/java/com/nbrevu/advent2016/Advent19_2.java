package com.nbrevu.advent2016;

import java.io.IOException;

public class Advent19_2 {
	private final static long INITIAL_SIZE=3014387;
	
	public static void main(String[] args) throws IOException	{
		// Ooooh, NICE, VERY NICE. This is very project euler-like. Don't simulate, look for the pattern!! YES. This is what I came for.
		// In this case it seems that the pattern is related to base 3!
		long base3=1;
		while (base3*3<INITIAL_SIZE) base3*=3;
		long diff=INITIAL_SIZE-base3;
		if (diff<base3) System.out.println(diff);
		else	{
			long reDiff=diff-base3;
			long result=base3+2*reDiff;
			System.out.println(result);
		}
	}
}
