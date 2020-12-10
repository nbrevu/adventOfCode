package com.nbrevu.advent2017;

import java.io.IOException;

public class Advent23_2 {
	/*
	 * THAT'S IT. The program counts *composite numbers* between b and c (included) in increments of 17.
	 */
	public static boolean[] sieve(int maxNumber)	{
		// Defaults to false!
		boolean[] composites=new boolean[1+maxNumber];
		composites[0]=composites[1]=true;
		for (int j=4;j<=maxNumber;j+=2) composites[j]=true;
		int sq=(int)(Math.sqrt((double)maxNumber));
		for (int i=3;i<=sq;i+=2) if (!composites[i]) for (int j=i*i;j<=maxNumber;j+=i+i) composites[j]=true;
		return composites;
	}
	
	public static void main(String[] args) throws IOException	{
		int b=106500;
		int c=123500;
		boolean[] composites=sieve(c);
		int h=0;
		for (int i=b;i<=c;i+=17) if (composites[i]) ++h;
		System.out.println(h);
	}
}
