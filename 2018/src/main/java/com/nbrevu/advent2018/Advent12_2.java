package com.nbrevu.advent2018;

import java.io.IOException;

import com.google.common.math.LongMath;

public class Advent12_2 {
	private final static long ITERATIONS=5*LongMath.pow(10l,10);
	
	public static void main(String[] args) throws IOException	{
		// Surprising pattern, but it works. For x=100, f(x)=6175, and from that onwards, f(x+1)=50+f(x).
		long result=(ITERATIONS-100)*50+6175;
		System.out.println(result);
	}
}
