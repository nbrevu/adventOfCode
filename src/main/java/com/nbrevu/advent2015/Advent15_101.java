package com.nbrevu.advent2015;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Advent15_101 {
	private final static int[] INITIAL_SEQUENCE=new int[]{3,1,1,3,3,2,2,1,1,3};
	
	private static int[] lookAndSay(int[] seq)	{
		List<Integer> result=new ArrayList<>(2*seq.length);
		int current=seq[0];
		int count=1;
		for (int i=1;i<seq.length;++i) if (current==seq[i]) ++count;
		else	{
			result.add(count);
			result.add(current);
			current=seq[i];
			count=1;
		}
		result.add(count);
		result.add(current);
		return result.stream().mapToInt(Integer::intValue).toArray();
	}
	
	public static void main(String[] args) throws IOException	{
		int[] seq=INITIAL_SEQUENCE;
		for (int i=0;i<40;++i) seq=lookAndSay(seq);
		System.out.println(seq.length);
	}
}
