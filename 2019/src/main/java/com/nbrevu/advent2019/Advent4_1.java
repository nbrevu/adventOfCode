package com.nbrevu.advent2019;

public class Advent4_1 {
	private final static int INITIAL=234444;
	private final static int FINAL=700000;
	
	private static byte[] parse(int value,int digits)	{
		byte[] result=new byte[digits];
		for (int i=digits-1;i>=0;--i)	{
			result[i]=(byte)(value%10);
			value/=10;
		}
		return result;
	}
	
	private static boolean less(byte[] a,byte[] b)	{
		for (int i=0;i<a.length;++i) if (a[i]<b[i]) return true;
		else if (a[i]>b[i]) return false;
		return false;
	}
	
	private static boolean isValid(byte[] counter)	{
		for (int i=0;i<counter.length-1;++i) if (counter[i]==counter[i+1]) return true;
		return false;
	}
	
	private static void next(byte[] counter)	{
		int index=counter.length-1;
		for (;;)	{
			++counter[index];
			if (counter[index]<=9) break;
			--index;
		}
		byte fillWith=counter[index];
		for (int i=index+1;i<counter.length;++i) counter[i]=fillWith;
	}
	
	private static int count(byte[] counter,byte[] to)	{
		int result=0;
		while (less(counter,to))	{
			if (isValid(counter)) ++result;
			next(counter);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		/*
		 * My brain is definitely fried :(. I can't seem to get the proper result using a "clever" algorithm.
		 */
		byte[] digits=parse(INITIAL,6);
		byte[] end=parse(FINAL,6);
		System.out.println(count(digits,end));
	}
}
