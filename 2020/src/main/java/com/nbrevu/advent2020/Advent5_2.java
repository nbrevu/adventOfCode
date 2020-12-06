package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.BitSet;

import com.google.common.base.Charsets;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.io.Resources;

public class Advent5_2 {
	private final static String IN_FILE="Advent5.txt";
	
	private static class CharTranslator	{
		private final char zeroChar;
		private final char oneChar;
		public CharTranslator(char zeroChar,char oneChar)	{
			this.zeroChar=zeroChar;
			this.oneChar=oneChar;
		}
		public boolean translate(char c)	{
			if (c==zeroChar) return false;
			else if (c==oneChar) return true;
			else throw new IllegalArgumentException();
		}
	}
	
	private static enum StrTranslator	{
		INSTANCE;
		private final static RangeMap<Integer,CharTranslator> TRANSLATORS=createTranslators();
		private static RangeMap<Integer,CharTranslator> createTranslators()	{
			RangeMap<Integer,CharTranslator> result=TreeRangeMap.create();
			result.put(Range.atMost(6),new CharTranslator('F','B'));
			result.put(Range.atLeast(7),new CharTranslator('L','R'));
			return result;
		}
		public int translate(String str)	{
			int result=0;
			int len=str.length();
			for (int i=0;i<len;++i)	{
				result+=result;
				if (TRANSLATORS.get(i).translate(str.charAt(i))) ++result;
			}
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		StrTranslator translator=StrTranslator.INSTANCE;
		BitSet set=new BitSet(1024);
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (!line.isBlank()) set.set(translator.translate(line));
		int firstIndex=set.nextSetBit(0);
		if (firstIndex<0) throw new IllegalArgumentException("Gobacken sidonna");
		for (int i=set.nextClearBit(1+firstIndex);i>=0;i=set.nextClearBit(1+i)) if (set.get(i-1)&&set.get(i+1))	{
			System.out.println(i);
			return;
		}
		throw new IllegalArgumentException("El no a you smoko");
	}
}
