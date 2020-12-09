package com.nbrevu.advent2017;

import java.io.IOException;
import java.util.function.LongConsumer;

import com.koloboke.collect.LongCursor;

public class Advent15_1 {
	private final static long START_A=703l;
	private final static long START_B=516l;
	private final static long FACTOR_A=16807l;
	private final static long FACTOR_B=48271l;
	private final static long MOD=2147483647l;
	private final static int REPETITIONS=40000000;
	
	private final static long MASK=(1<<16)-1;
	
	private static class Generator implements LongCursor	{
		private long value;
		private final long factor;
		public Generator(long initialValue,long factor)	{
			value=initialValue;
			this.factor=factor;
		}
		@Override
		public boolean moveNext() {
			value=(value*factor)%MOD;
			return false;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		@Override
		public void forEachForward(LongConsumer action) {
			throw new UnsupportedOperationException();
		}
		@Override
		public long elem() {
			return value;
		}
	}
	
	private static boolean match(long a,long b)	{
		return ((a-b)&MASK)==0;
	}

	public static void main(String[] args) throws IOException	{
		Generator a=new Generator(START_A,FACTOR_A);
		Generator b=new Generator(START_B,FACTOR_B);
		int result=0;
		for (int i=0;i<REPETITIONS;++i)	{
			a.moveNext();
			b.moveNext();
			if (match(a.elem(),b.elem())) ++result;
		}
		System.out.println(result);
	}
}
