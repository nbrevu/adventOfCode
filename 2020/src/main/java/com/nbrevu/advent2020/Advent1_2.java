package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Advent1_2 {
	private final static String IN_FILE="Advent1.txt";
	private final static long GOAL=2020l;
	
	public static void main(String[] args) throws IOException	{
		LongSet numbers=HashLongSets.newMutableSet();
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			try	{
				numbers.add(Long.parseLong(line));
			}	catch (NumberFormatException nfe)	{
				System.out.println("Lo que me habéis dao pa papear me roe las tripas.");
			}
		}
		long[] array=numbers.toArray(new long[numbers.size()]);
		for (int i=0;i<array.length;++i) for (int j=i+1;j<array.length;++j)	{
			long s1=array[i];
			long s2=array[j];
			long diff=GOAL-(s1+s2);
			if ((diff==s1)||(diff==s2)) continue;
			else if (numbers.contains(diff))	{
				System.out.println(s1*s2*diff);
				return;
			}
		}
	}
}
