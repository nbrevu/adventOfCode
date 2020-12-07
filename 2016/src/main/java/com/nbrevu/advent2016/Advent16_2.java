package com.nbrevu.advent2016;

import java.util.BitSet;

public class Advent16_2 {
	private final static String INPUT="01000100010010111";
	private final static int GOAL=35651584;
	
	private static BoundedBitSet parse(String bitString)	{
		BitSet result=new BitSet(bitString.length());
		for (int i=0;i<bitString.length();++i) if (bitString.charAt(i)=='1') result.set(i);
		else result.clear(i);
		return new BoundedBitSet(bitString.length(),result);
	}
	
	private static BoundedBitSet iterate(BoundedBitSet bitSet)	{
		int newSize=2*bitSet.size+1;
		BitSet result=new BitSet(newSize);
		for (int i=0;i<bitSet.size;++i) result.set(i,bitSet.bitSet.get(i));
		result.clear(bitSet.size);
		int offset=2*bitSet.size;
		for (int i=0;i<bitSet.size;++i) result.set(offset-i,!bitSet.bitSet.get(i));
		return new BoundedBitSet(newSize,result);
	}
	
	private static class BoundedBitSet	{
		public final int size;
		public final BitSet bitSet;
		public BoundedBitSet(int size,BitSet bitSet)	{
			this.size=size;
			this.bitSet=bitSet;
		}
		public BoundedBitSet trim(int newSize)	{
			return new BoundedBitSet(newSize,bitSet);
		}
		@Override
		public String toString()	{
			StringBuilder result=new StringBuilder();
			for (int i=0;i<size;++i) result.append(bitSet.get(i)?'1':'0');
			return result.toString();
		}
	}
	
	private static BoundedBitSet compress(BoundedBitSet original)	{
		int origSize=original.size;
		int newSize=origSize/2;
		BitSet result=new BitSet(newSize);
		for (int i=0;i<newSize;++i) result.set(i,original.bitSet.get(2*i)==original.bitSet.get(2*i+1));
		return new BoundedBitSet(newSize,result);
	}
	
	public static void main(String[] args)	{
		BoundedBitSet contents=parse(INPUT);
		while (contents.size<GOAL) contents=iterate(contents);
		contents=contents.trim(GOAL);
		while ((contents.size%2)==0) contents=compress(contents);
		System.out.println(contents.toString());
	}
}
