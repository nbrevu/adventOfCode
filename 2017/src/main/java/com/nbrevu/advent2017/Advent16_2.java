package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Advent16_2 {
	private final static String IN_FILE="Advent16.txt";
	
	private final static Pattern SPIN_PATTERN=Pattern.compile("^s(\\d+)$");
	private final static Pattern EXCHANGE_PATTERN=Pattern.compile("^x(\\d+)/(\\d+)$");
	private final static Pattern PARTNER_PATTERN=Pattern.compile("^p([a-p])/([a-p])$");
	private final static int REPETITIONS=1_000_000_000;
	
	private static char[] getInitialStatus()	{
		char[] result=new char[16];
		for (int i=0;i<16;++i) result[i]=(char)('a'+i);
		return result;
	}
	
	private static class Spin implements Consumer<char[]>	{
		private final int amount;
		public Spin(int amount)	{
			this.amount=amount;
		}
		@Override
		public void accept(char[] t) {
			char[] copy=Arrays.copyOf(t,t.length);
			for (int i=0;i<copy.length;++i) t[(i+amount)%t.length]=copy[i];
		}
	}
	private static class Exchange implements Consumer<char[]>	{
		private final int pos1;
		private final int pos2;
		public Exchange(int pos1,int pos2)	{
			this.pos1=pos1;
			this.pos2=pos2;
		}
		@Override
		public void accept(char[] t) {
			char swap=t[pos1];
			t[pos1]=t[pos2];
			t[pos2]=swap;
		}
	}
	private static class Partner implements Consumer<char[]>	{
		private final char l1;
		private final char l2;
		public Partner(char l1,char l2)	{
			this.l1=l1;
			this.l2=l2;
		}
		@Override
		public void accept(char[] t) {
			for (int i=0;i<t.length;++i) if (t[i]==l1) t[i]=l2;
			else if (t[i]==l2) t[i]=l1;
		}
	}
	private static Consumer<char[]> parseFunction(String fun)	{
		Matcher matcher1=SPIN_PATTERN.matcher(fun);
		if (matcher1.matches()) return new Spin(Integer.parseInt(matcher1.group(1)));
		Matcher matcher2=EXCHANGE_PATTERN.matcher(fun);
		if (matcher2.matches()) return new Exchange(Integer.parseInt(matcher2.group(1)),Integer.parseInt(matcher2.group(2)));
		Matcher matcher3=PARTNER_PATTERN.matcher(fun);
		if (matcher3.matches()) return new Partner(matcher3.group(1).charAt(0),matcher3.group(2).charAt(0));
		throw new UnsupportedOperationException("Función no computable.");
	}
	
	public static void main(String[] args) throws IOException	{
		char[] contents=getInitialStatus();
		URL file=Resources.getResource(IN_FILE);
		String content=Resources.readLines(file,Charsets.UTF_8).get(0);
		List<Consumer<char[]>> functions=Pattern.compile(",").splitAsStream(content).map(Advent16_2::parseFunction).collect(Collectors.toUnmodifiableList());
		IntObjMap<String> directOrder=HashIntObjMaps.newMutableMap();
		ObjIntMap<String> reverseOrder=HashObjIntMaps.newMutableMap();
		for (int i=1;i<=REPETITIONS;++i)	{
			for (Consumer<char[]> fun:functions) fun.accept(contents);
			String thisRepResult=String.copyValueOf(contents);
			int found=reverseOrder.getOrDefault(thisRepResult,-1);
			if (found>0)	{
				int cycleLength=i-found;
				int cycleMod=(REPETITIONS-found)%cycleLength;
				int realPosition=found+cycleMod;
				System.out.println(directOrder.get(realPosition));
				return;
			}	else	{
				directOrder.put(i,thisRepResult);
				reverseOrder.put(thisRepResult,i);
			}
		}
	}
}
