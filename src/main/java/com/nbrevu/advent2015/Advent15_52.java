package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;

public class Advent15_52 {
	private final static String IN_FILE="2015/Advent51.txt";
	
	private static class CharPair	{
		public final char a;
		public final char b;
		public CharPair(char a,char b)	{
			this.a=a;
			this.b=b;
		}
		@Override
		public int hashCode()	{
			return Character.hashCode(a)+Character.hashCode(b);
		}
		@Override
		public boolean equals(Object other)	{
			CharPair cpOther=(CharPair)other;
			return (a==cpOther.a)&&(b==cpOther.b);
		}
	}
	
	private static boolean isNice(String str)	{
		CharObjMap<BitSet> singleCharMap=HashCharObjMaps.newMutableMap();
		Map<CharPair,BitSet> doubleCharMap=new HashMap<>();
		char lastChar='\'';
		int len=str.length();
		boolean cond1=false;
		boolean cond2=false;
		for (int i=0;i<len;++i)	{
			char curChar=str.charAt(i);
			BitSet singleCharBitSet=singleCharMap.computeIfAbsent(curChar,(char unused)->new BitSet());
			if ((i>=2)&&singleCharBitSet.get(i-2)) cond1=true;
			singleCharBitSet.set(i);
			if (i!=0)	{
				CharPair cp=new CharPair(lastChar,curChar);
				BitSet doubleCharBitSet=doubleCharMap.computeIfAbsent(cp,(CharPair unused)->new BitSet());
				int prevIndex=doubleCharBitSet.nextSetBit(0);
				if ((prevIndex>=0)&&(prevIndex!=(i-1))) cond2=true;
				doubleCharBitSet.set(i);
			}
			lastChar=curChar;
		}
		return cond1&&cond2;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int counter=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (isNice(line.toLowerCase())) ++counter;
		System.out.println(counter);
	}
}
