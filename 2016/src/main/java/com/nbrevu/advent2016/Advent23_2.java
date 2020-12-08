package com.nbrevu.advent2016;

import java.io.IOException;

public class Advent23_2 {
	/*
	 * For inputs equals to 6 and higher, the result is 8918+n!
	 */
	public static void main(String[] args) throws IOException	{
		long fact=1;
		for (long i=1;i<=12;++i) fact*=i;
		fact+=8918;
		System.out.println(fact);
	}
}
