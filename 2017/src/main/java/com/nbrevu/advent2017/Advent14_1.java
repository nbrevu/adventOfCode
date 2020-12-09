package com.nbrevu.advent2017;

import java.io.IOException;

public class Advent14_1 {
	private final static String INPUT="amgozmfv";

	private final static int[] BITS_IN_CHARACTER=new int[] {0,1,1,2,1,2,2,3,1,2,2,3,2,3,3,4};
	
	private static class Twister	{
		public final int[] elements;
		private int currentPos;
		private int skipSize;
		public Twister(int len)	{
			elements=new int[len];
			for (int i=0;i<len;++i) elements[i]=i;
			currentPos=0;
			skipSize=0;
		}
		public void operate(int operLength)	{
			reverse(currentPos,operLength);
			currentPos+=operLength+skipSize;
			currentPos%=elements.length;
			++skipSize;
		}
		private void reverse(int startPos,int operLength)	{
			int finalPos=startPos+operLength-1;
			while (startPos<finalPos)	{
				int actualStartPos=startPos%elements.length;
				int actualFinalPos=finalPos%elements.length;
				int e1=elements[actualStartPos];
				elements[actualStartPos]=elements[actualFinalPos];
				elements[actualFinalPos]=e1;
				++startPos;
				--finalPos;
			}
		}
		private int[] getElements()	{
			return elements;
		}
	}
	
	private final static char[] EXTRA_CHARS=new char[] {17,31,73,47,23};
	
	private static char[] expand(String in)	{
		char[] result=new char[in.length()+EXTRA_CHARS.length];
		for (int i=0;i<in.length();++i) result[i]=in.charAt(i);
		System.arraycopy(EXTRA_CHARS,0,result,in.length(),EXTRA_CHARS.length);
		return result;
	}
	
	private static int xor(int[] array,int start,int end)	{
		int result=0;
		for (int i=start;i<end;++i) result^=array[i];
		return result;
	}
	
	private static byte[] getHexDenseHash(int[] sparseHash)	{
		byte[] result=new byte[32];
		for (int i=0;i<16;++i)	{
			int xored=xor(sparseHash,16*i,16*(i+1));
			result[2*i]=(byte)((xored>>4)&0xf);
			result[2*i+1]=(byte)(xored&0xf);
		}
		return result;
	}
	
	private static byte[] knotHash(int number)	{
		String value=String.format("%s-%d",INPUT,number);
		char[] numbers=expand(value);
		Twister swister=new Twister(256);
		for (int i=0;i<64;++i) for (int val:numbers) swister.operate(val);
		int[] sparseHash=swister.getElements();
		return getHexDenseHash(sparseHash);
	}
	
	public static void main(String[] args) throws IOException	{
		int result=0;
		for (int i=0;i<128;++i) for (byte b:knotHash(i)) result+=BITS_IN_CHARACTER[b];
		System.out.println(result);
	}
}
