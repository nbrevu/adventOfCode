package com.nbrevu.advent2020;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent13_2 {
	private final static String IN_FILE="Advent13.txt";
	
	public static BigInteger solveChineseRemainder(BigInteger a,BigInteger x,BigInteger b,BigInteger y)	{
		// Assumes x and y coprime. Undefined results if this doesn't hold.
		BigInteger y_x=modulusInverse(y.mod(x),x);
		BigInteger x_y=modulusInverse(x.mod(y),y);
		BigInteger prod=x.multiply(y);
		BigInteger m=y.multiply(y_x).mod(prod);
		BigInteger n=x.multiply(x_y).mod(prod);
		return a.multiply(m).add(b.multiply(n)).mod(prod);
	}
	public static BigInteger modulusInverse(BigInteger operand,BigInteger mod)	{
		BigInteger store=mod;
		BigInteger x=BigInteger.ZERO;
		BigInteger y=BigInteger.ONE;
		BigInteger lastX=BigInteger.ONE;
		BigInteger lastY=BigInteger.ZERO;
		BigInteger q,tmp;
		while (operand.signum()!=0)	{
			BigInteger[] div=mod.divideAndRemainder(operand);
			q=div[0];
			tmp=div[1];
			mod=operand;
			operand=tmp;
			tmp=lastX.subtract(q.multiply(x));
			lastX=x;
			x=tmp;
			tmp=lastY.subtract(q.multiply(y));
			lastY=y;
			y=tmp;
		}
		while (lastY.signum()<0) lastY=lastY.add(store);
		return lastY.mod(store);
	}

	private static class ChineseRemainderEntry	{
		public final BigInteger mod;
		public final BigInteger value;
		public ChineseRemainderEntry(long mod,long value)	{
			this.mod=BigInteger.valueOf(mod);
			this.value=BigInteger.valueOf(value);
		}
		public ChineseRemainderEntry(BigInteger mod,BigInteger value)	{
			this.mod=mod;
			this.value=value;
		}
		private static ChineseRemainderEntry combine(ChineseRemainderEntry entry1,ChineseRemainderEntry entry2)	{
			BigInteger newMod=entry1.mod.multiply(entry2.mod);
			BigInteger newValue=solveChineseRemainder(entry1.value,entry1.mod,entry2.value,entry2.mod);
			return new ChineseRemainderEntry(newMod,newValue);
		}
	}
	private static List<ChineseRemainderEntry> parseIds(String idString)	{
		String[] split=idString.split(",");
		List<ChineseRemainderEntry> result=new ArrayList<>();
		for (int i=0;i<split.length;++i) if (!split[i].equals("x"))	{
			int mod=Integer.parseInt(split[i]);
			int value=mod-(i%mod);
			while (value<0) value+=mod;
			while (value>=mod) value-=mod;
			result.add(new ChineseRemainderEntry(mod,value));
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> contents=Resources.readLines(file,Charsets.UTF_8);
		List<ChineseRemainderEntry> ids=parseIds(contents.get(1));
		ChineseRemainderEntry result=ids.stream().reduce(ChineseRemainderEntry::combine).get();
		System.out.println(result.value);
	}
}
