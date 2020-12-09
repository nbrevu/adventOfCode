package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent10_1 {
	private final static String IN_FILE="Advent10.txt";
	
	private static List<Integer> parseNumbers(String line)	{
		return Pattern.compile(",").splitAsStream(line).map(Integer::parseInt).collect(Collectors.toUnmodifiableList());
	}
	
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
		private int getProd()	{
			return elements[0]*elements[1];
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		String content=Resources.readLines(file,Charsets.UTF_8).get(0);
		List<Integer> numbers=parseNumbers(content);
		Twister swister=new Twister(256);
		for (int val:numbers) swister.operate(val);
		int result=swister.getProd();
		System.out.println(result);
	}
}
