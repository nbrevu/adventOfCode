package com.nbrevu.advent2017;

import java.io.IOException;

public class Advent17_2 {
	private final static int INPUT=369;
	private final static int FINAL_VALUE=50000000;
	
	public static void main(String[] args) throws IOException	{
		/*
		 * No need to actually create the list. "0" is always in the initial position, therefore the value is the last one for which the
		 * calculated index is 0.
		 */
		int lastAfter0=-1;
		int pos=0;
		for (int i=1;i<=FINAL_VALUE;++i)	{
			pos=(pos+INPUT)%i;
			if (pos==0) lastAfter0=i;
			++pos;
		}
		System.out.println(lastAfter0);
	}
}
