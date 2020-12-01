package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Advent11 {
	private final static String IN_FILE="Advent11.txt";
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
		for (LongCursor cursor=numbers.cursor();cursor.moveNext();)	{
			long n=cursor.elem();
			if (numbers.contains(GOAL-n))	{
				System.out.println(n*(GOAL-n));
				break;
			}
		}
	}
}
