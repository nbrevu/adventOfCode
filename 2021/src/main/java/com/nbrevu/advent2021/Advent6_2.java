package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent6_2 {
	private final static String IN_FILE="Advent6.txt";
	
	private final static long DAYS=256;
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> line=Resources.readLines(file,Charsets.UTF_8);
		long[] counters=new long[9];
		for (String s:line.get(0).split(",")) ++counters[Integer.parseInt(s)];
		// This can be done matricially!
		for (long i=0;i<DAYS;++i)	{
			long[] newCounters=new long[9];
			for (int j=1;j<counters.length;++j) newCounters[j-1]=counters[j];
			newCounters[6]+=counters[0];
			newCounters[8]+=counters[0];
			counters=newCounters;
		}
		long sum=0;
		for (int i=0;i<counters.length;++i) sum+=counters[i];
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(sum);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
