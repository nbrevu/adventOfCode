package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.OptionalInt;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Advent3_2 {
	private final static String IN_FILE="Advent3.txt";
	
	private static LongSet getByFrequency(LongSet set,long bitValue,boolean useLeast)	{
		LongSet ones=HashLongSets.newMutableSet();
		LongSet zeroes=HashLongSets.newMutableSet();
		for (LongCursor cursor=set.cursor();cursor.moveNext();)	{
			if ((cursor.elem()&bitValue)==0) zeroes.add(cursor.elem());
			else ones.add(cursor.elem());
		}
		if (useLeast) return (ones.size()<zeroes.size())?ones:zeroes;
		else return (ones.size()>=zeroes.size())?ones:zeroes;
	}
	
	private static long getLast(LongSet set,long bitValue,boolean useLeast)	{
		while (set.size()>1)	{
			set=getByFrequency(set,bitValue,useLeast);
			bitValue>>=1;
		}
		return set.toLongArray()[0];
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		OptionalInt length=OptionalInt.empty();
		LongSet baseSet=HashLongSets.newMutableSet();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			int len=line.length();
			if (length.isEmpty()) length=OptionalInt.of(len);
			else if (length.getAsInt()!=len) throw new RuntimeException("Lo que me habéis dao pa papear me roe las tripas.");
			long value=0;
			for (int i=0;i<line.length();++i)	{
				value*=2;
				if (line.charAt(i)=='1') ++value;
				else if (line.charAt(i)!='0') throw new RuntimeException("Lo que me habéis dao pa papear me roe las tripas.");
			}
			baseSet.add(value);
		}
		long firstBitValue=1<<(length.getAsInt()-1);
		long o2=getLast(baseSet,firstBitValue,false);
		long co2=getLast(baseSet,firstBitValue,true);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(o2*co2);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
