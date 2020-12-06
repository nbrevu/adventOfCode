package com.nbrevu.advent2015;

import java.io.IOException;

public class Advent15_251 {
	private final static int ROW=3010;
	private final static int COL=3019;
	private final static long INITIAL_VALUE=20151125l;
	/*-
	 * Enter the code at row 3010, column 3019.
	 */
	
	private static long transformCantor(int row,int col)	{
		long diagonal=row+col-2;
		long triangular=(diagonal*(diagonal+1))/2;
		return triangular+col;
	}
	
	public static long iterate(long in)	{
		return (in*252533)%33554393;
	}
	
	public static void main(String[] args) throws IOException	{
		long iterations=transformCantor(ROW,COL);
		long value=INITIAL_VALUE;
		for (long i=1;i<iterations;++i) value=iterate(value);
		System.out.println(value);
	}
}
