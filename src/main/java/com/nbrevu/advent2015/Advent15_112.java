package com.nbrevu.advent2015;

import java.io.IOException;
import java.util.Optional;

import com.koloboke.collect.set.CharSet;
import com.koloboke.collect.set.hash.HashCharSets;

public class Advent15_112 {
	private final static String INITIAL_PASSWORD="hxbxxyzz";
	private final static CharSet INVALID_CHARACTERS=HashCharSets.newImmutableSet(new char[] {'i','o','l'});
	
	private static Optional<Character> nextChar(char in)	{
		if (in=='z') return Optional.empty();
		do ++in; while (INVALID_CHARACTERS.contains(in));
		return Optional.of(in);
	}
	
	private static void nextPassword(char[] in)	{
		for (int i=in.length-1;i>=0;--i)	{
			Optional<Character> increased=nextChar(in[i]);
			if (increased.isEmpty()) in[i]='a';
			else	{
				in[i]=increased.get().charValue();
				return;
			}
		}
	}
	
	private static boolean has3InRow(char[] password)	{
		int currentCount=1;
		for (int i=1;i<password.length;++i) if (password[i]==1+password[i-1])	{
			++currentCount;
			if (currentCount==3) return true;
		}	else currentCount=1;
		return false;
	}
	
	private static int repeatCount(char[] password)	{
		int result=0;
		int count=1;
		for (int i=1;i<password.length;++i) if (password[i]==password[i-1])	{
			++count;
			if (count==2)	{
				++result;
				count=0;
			}
		}	else count=1;
		return result;
	}
	
	private static boolean isValid(char[] password)	{
		return has3InRow(password)&&(repeatCount(password)>=2);
	}
	
	public static void main(String[] args) throws IOException	{
		char[] password=INITIAL_PASSWORD.toCharArray();
		do nextPassword(password); while (!isValid(password));
		System.out.println(String.copyValueOf(password));
	}
}
