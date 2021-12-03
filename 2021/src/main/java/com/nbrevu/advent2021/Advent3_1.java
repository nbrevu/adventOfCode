package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.OptionalInt;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

public class Advent3_1 {
	private final static String IN_FILE="Advent3.txt";
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		IntIntMap zeroes=HashIntIntMaps.newMutableMap();
		IntIntMap ones=HashIntIntMaps.newMutableMap();
		OptionalInt length=OptionalInt.empty();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			int len=line.length();
			if (length.isEmpty()) length=OptionalInt.of(len);
			else if (length.getAsInt()!=len) throw new RuntimeException("Lo que me habéis dao pa papear me roe las tripas.");
			for (int i=0;i<line.length();++i)	{
				if (line.charAt(i)=='1') ones.addValue(i,1);
				else if (line.charAt(i)=='0') zeroes.addValue(i,1);
				else throw new RuntimeException("Lo que me habéis dao pa papear me roe las tripas.");
			}
		}
		int gamma=0;
		int epsilon=0;
		for (int i=0;i<length.getAsInt();++i)	{
			gamma*=2;
			epsilon*=2;
			if (ones.get(i)>zeroes.get(i)) ++gamma;
			else ++epsilon;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(epsilon*gamma);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
