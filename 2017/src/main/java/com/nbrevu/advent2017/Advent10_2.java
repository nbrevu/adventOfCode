package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent10_2 {
	private final static String IN_FILE="Advent10.txt";
	
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
	
	private static char[] expand(char[] in)	{
		char[] result=new char[in.length+EXTRA_CHARS.length];
		System.arraycopy(in,0,result,0,in.length);
		System.arraycopy(EXTRA_CHARS,0,result,in.length,EXTRA_CHARS.length);
		return result;
	}
	
	private static int xor(int[] array,int start,int end)	{
		int result=0;
		for (int i=start;i<end;++i) result^=array[i];
		return result;
	}
	
	private static char getChar(int byteValue)	{
		return (char)((byteValue<=9)?('0'+byteValue):('a'+byteValue-10));
	}
	
	private static String getHexDenseHash(int[] sparseHash)	{
		StringBuilder result=new StringBuilder();
		for (int i=0;i<16;++i)	{
			int xored=xor(sparseHash,16*i,16*(i+1));
			int upperChar=(xored>>4)&0xf;
			int lowerChar=xored&0xf;
			result.append(getChar(upperChar));
			result.append(getChar(lowerChar));
		}
		return result.toString();
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		String content=Resources.readLines(file,Charsets.UTF_8).get(0);
		char[] numbers=expand(content.toCharArray());
		Twister swister=new Twister(256);
		for (int i=0;i<64;++i) for (int val:numbers) swister.operate(val);
		int[] sparseHash=swister.getElements();
		String result=getHexDenseHash(sparseHash);
		System.out.println(result);
	}
}
