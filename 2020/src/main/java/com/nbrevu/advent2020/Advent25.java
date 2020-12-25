package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent25 {
	private final static String IN_FILE="Advent25.txt";
	private final static long BASE=7l;
	private final static long MOD=20201227l;
	
	private static int discreteLog(long value,long base,long mod)	{
		long x=1l;
		int result=0;
		for (;;)	{
			x*=base;
			x%=mod;
			++result;
			if (x==value) return result;
		}
	}
	
	private static long expMod(long base,long exp,long mod)	{
		long current=base;
		long prod=1;
		while (exp>0)	{
			if ((exp%2)==1) prod=(prod*current)%mod;
			current=(current*current)%mod;
			exp/=2;
		}
		return prod;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> content=Resources.readLines(file,Charsets.UTF_8);
		long cardPk=Long.parseLong(content.get(0));
		long doorPk=Long.parseLong(content.get(1));
		long loop1=discreteLog(cardPk,BASE,MOD);
		long loop2=discreteLog(doorPk,BASE,MOD);
		long result=expMod(cardPk,loop2,MOD);
		long result2=expMod(doorPk,loop1,MOD);
		long result3=expMod(BASE,loop1*loop2,MOD);
		System.out.println(result);
		System.out.println(result2);
		System.out.println(result3);
	}
}