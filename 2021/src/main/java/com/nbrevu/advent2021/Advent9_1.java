package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent9_1 {
	private final static String IN_FILE="Advent9.txt";
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		int result=0;
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		int len=lines.get(0).length();
		int[][] heights=new int[lines.size()][len];
		for (int i=0;i<lines.size();++i)	{
			String line=lines.get(i);
			if (line.length()!=len) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			for (int j=0;j<len;++j) heights[i][j]=line.charAt(j)-'0';
		}
		for (int i=0;i<heights.length;++i) for (int j=0;j<heights[i].length;++j)	{
			boolean isMinimum=true;
			if ((j<heights[i].length-1)&&(heights[i][j]>=heights[i][j+1])) isMinimum=false;
			if ((i>0)&&(heights[i][j]>=heights[i-1][j])) isMinimum=false;
			if ((j>0)&&(heights[i][j]>=heights[i][j-1])) isMinimum=false;
			if ((i<heights.length-1)&&(heights[i][j]>=heights[i+1][j])) isMinimum=false;
			if (isMinimum) result+=1+heights[i][j];
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
