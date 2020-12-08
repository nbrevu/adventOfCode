package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent9_1 {
	private final static String IN_FILE="Advent9.txt";
	
	private static long countGroups(String args)	{
		char[] chars=args.toCharArray();
		int currentDepth=0;
		long result=0l;
		boolean inGarbage=false;
		boolean ignoreNext=false;
		for (int i=0;i<chars.length;++i) if (ignoreNext)	{
			ignoreNext=false;
			continue;
		}	else	{
			char c=chars[i];
			if (inGarbage)	{
				if (c=='!') ignoreNext=true;
				else if (c=='>') inGarbage=false;
			}	else if (c=='<') inGarbage=true;
			else if (c=='{') ++currentDepth;
			else if (c=='}')	{
				result+=currentDepth;
				--currentDepth;
			}	else if (c!=',') throw new IllegalArgumentException("Nobody expects the Spanish inquisition, or the \""+c+"\" character.");
		}
		if (currentDepth!=0) throw new UnsupportedOperationException("That's unpossible!");
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		String content=Resources.readLines(file,Charsets.UTF_8).get(0);
		long result=countGroups(content);
		System.out.println(result);
	}
}
