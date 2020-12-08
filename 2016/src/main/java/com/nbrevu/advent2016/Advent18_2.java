package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

// Convention: boolean values are: TRue=TRap, False=Fine (no trap).
public class Advent18_2 {
	private final static String IN_FILE="Advent18.txt";
	
	private static boolean[] parseRow(String str)	{
		int len=str.length();
		boolean[] result=new boolean[len];
		for (int i=0;i<len;++i) result[i]=(str.charAt(i)=='^');
		return result;
	}
	
	private static boolean[] nextRow(boolean[] currentRow)	{
		int len=currentRow.length;
		boolean[] result=new boolean[len];
		result[0]=currentRow[1];
		for (int i=1;i<len-1;++i) result[i]=(currentRow[i-1]!=currentRow[i+1]);
		result[len-1]=currentRow[len-2];
		return result;
	}
	
	private static int countSafeTiles(boolean[] row)	{
		int result=0;
		for (boolean b:row) if (!b) ++result;
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		String line=Resources.readLines(file,Charsets.UTF_8).get(0);
		boolean[] currentRow=parseRow(line);
		int result=countSafeTiles(currentRow);
		// I'm 99% sure that there are cycles, but you know, this is fast enough.
		for (int i=1;i<400000;++i)	{
			currentRow=nextRow(currentRow);
			result+=countSafeTiles(currentRow);
		}
		System.out.println(result);
	}
}
