package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent4_2 {
	private final static String IN_FILE="Advent4.txt";
	private final static String GOAL="northpole object storage";
	
	private final static Pattern SPLITTER_PATTERN=Pattern.compile("^([a-z\\-]+)\\-(\\d+)\\[([a-z]{5})\\]$");
	
	private static char decrypt(char c,int code)	{
		if (c=='-') return ' ';
		int shifted=c-'a';
		int decoded=(shifted+code)%26;
		return (char)(decoded+'a');
	}
	
	private static String decrypt(String str,int code)	{
		code=code%26;
		StringBuilder result=new StringBuilder();
		int len=str.length();
		for (int i=0;i<len;++i) result.append(decrypt(str.charAt(i),code));
		return result.toString();
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=SPLITTER_PATTERN.matcher(line);
			if (matcher.matches())	{
				String firstPart=matcher.group(1);
				int roomCode=Integer.parseInt(matcher.group(2));
				String decoded=decrypt(firstPart,roomCode);
				if (decoded.equalsIgnoreCase(GOAL))	{
					System.out.println(roomCode);
					return;
				}
			}
		}
		throw new IllegalArgumentException("T'has pasao, macho t'has pasao.");
	}
}
