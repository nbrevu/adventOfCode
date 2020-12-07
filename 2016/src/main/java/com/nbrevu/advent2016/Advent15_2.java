package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_2 {
	private final static String IN_FILE="Advent15.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^Disc #(\\d+) has (\\d+) positions; at time=0, it is at position (\\d+).$");

	private static long solveChineseRemainder(long a,long x,long b,long y)	{
		long y_x=modulusInverse(y%x,x);
		long x_y=modulusInverse(x%y,y);
		long prod=x*y;
		long m=(y*y_x)%prod;
		long n=(x*x_y)%prod;
		long result=(a*m+b*n)%prod;
		assert (result%x)==a;
		assert (result%y)==b;
		return result;
	}
	private static long modulusInverse(long operand,long mod)	{
		long store=mod;
		long x=0;
		long y=1;
		long lastX=1;
		long lastY=0;
		long q,tmp;
		while (operand!=0)	{
			q=mod/operand;
			tmp=mod%operand;
			mod=operand;
			operand=tmp;
			tmp=lastX-q*x;
			lastX=x;
			x=tmp;
			tmp=lastY-q*y;
			lastY=y;
			y=tmp;
		}
		while (lastY<0) lastY+=store;
		return lastY%store;
	}
	
	private static class Modulus	{
		public final long offset;
		public final long modulus;
		public Modulus(long offset,long modulus)	{
			this.offset=offset;
			this.modulus=modulus;
		}
		public Modulus combine(Modulus other)	{
			long newOffset=solveChineseRemainder(offset,modulus,other.offset,other.modulus);
			long newModulus=modulus*other.modulus;
			return new Modulus(newOffset,newModulus);
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Modulus> moduli=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				long modulus=Long.parseLong(matcher.group(2));
				long offset=Long.parseLong(matcher.group(3))+Long.parseLong(matcher.group(1));
				offset=(modulus-offset)%modulus;
				while (offset<0) offset+=modulus;
				moduli.add(new Modulus(offset,modulus));
			}
		}
		// It's like we had a "Disc #7 has 11 positions; at time=0, it is at position 0.
		// 11-(size+1) = 10-size.
		moduli.add(new Modulus(10-moduli.size(),11l));
		Modulus result=moduli.stream().reduce(Modulus::combine).get();
		System.out.println(result.offset);
	}
}
