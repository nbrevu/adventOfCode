package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.io.Resources;

public class Advent51 {
	private final static String IN_FILE="Advent51.txt";
	
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
		int max=0;
		URL file=Resources.getResource(IN_FILE);
		StrTranslator translator=StrTranslator.INSTANCE;
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (!line.isBlank()) max=Math.max(max,translator.translate(line));
		System.out.println(max);
	}
}
