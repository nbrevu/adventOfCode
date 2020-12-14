package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.function.LongConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Advent14_2 {
	private final static String IN_FILE="Advent14.txt";
	
	private final static Pattern MASK_PATTERN=Pattern.compile("^mask = ([01X]{36})$");
	private final static Pattern ASSIGNMENT_PATTERN=Pattern.compile("^mem\\[(\\d+)\\] = (\\d+)$");
	
	private static class Mask	{
		private final long mask0;
		private final long mask1;
		private final long[] maskX;
		private Mask(long mask0,long mask1,long[] maskX)	{
			this.mask0=mask0;
			this.mask1=mask1;
			this.maskX=maskX;
		}
		public long[] apply(long in)	{
			in&=mask0;
			in|=mask1;
			long[] result=new long[maskX.length];
			for (int i=0;i<maskX.length;++i) result[i]=in|maskX[i];
			return result;
		}
		private static void addBit(LongSet set,long bit)	{
			LongSet copy=HashLongSets.newMutableSet();
			for (LongCursor c=set.cursor();c.moveNext();) copy.add(c.elem()|bit);
			copy.forEach((LongConsumer)set::add);
		}
		public static Mask getMask(String in)	{
			long mask0=-1;
			long mask1=0;
			LongSet maskX=HashLongSets.newMutableSet();
			maskX.add(0l);
			long bit=1;
			for (int i=in.length()-1;i>=0;--i)	{
				switch (in.charAt(i))	{
					case '1':mask1+=bit;break;
					case 'X':	{
						mask0-=bit;
						addBit(maskX,bit);
						break;
					}
				}
				bit<<=1;
			}
			return new Mask(mask0,mask1,maskX.toArray(new long[maskX.size()]));
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		LongLongMap mem=HashLongLongMaps.newMutableMap();
		Mask currentMask=null;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher maskMatcher=MASK_PATTERN.matcher(line);
			if (maskMatcher.matches())	{
				currentMask=Mask.getMask(maskMatcher.group(1));
				continue;
			}
			Matcher assignmentMatcher=ASSIGNMENT_PATTERN.matcher(line);
			if (assignmentMatcher.matches())	{
				long address=Long.parseLong(assignmentMatcher.group(1));
				long value=Long.parseLong(assignmentMatcher.group(2));
				long[] realAddresses=currentMask.apply(address);
				for (long realAddress:realAddresses) mem.put(realAddress,value);
				continue;
			}
		}
		long result=0l;
		for (LongCursor cursor=mem.values().cursor();cursor.moveNext();) result+=cursor.elem();
		System.out.println(result);
	}
}
