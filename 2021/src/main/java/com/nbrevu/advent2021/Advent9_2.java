package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent9_2 {
	private final static String IN_FILE="Advent9.txt";
	
	private static int markBasin(int[][] heights,boolean[][] basinMarks,int i,int j)	{
		if ((heights[i][j]==9)||basinMarks[i][j]) return 0;
		int result=1;
		basinMarks[i][j]=true;
		if (j<heights[i].length-1) result+=markBasin(heights,basinMarks,i,j+1);
		if (i>0) result+=markBasin(heights,basinMarks,i-1,j);
		if (j>0) result+=markBasin(heights,basinMarks,i,j-1);
		if (i<heights.length-1) result+=markBasin(heights,basinMarks,i+1,j);
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		int len=lines.get(0).length();
		int[][] heights=new int[lines.size()][len];
		for (int i=0;i<lines.size();++i)	{
			String line=lines.get(i);
			if (line.length()!=len) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			for (int j=0;j<len;++j) heights[i][j]=line.charAt(j)-'0';
		}
		IntStream.Builder builder=IntStream.builder();
		boolean[][] basinMarks=new boolean[heights.length][len];
		for (int i=0;i<heights.length;++i) for (int j=0;j<heights[i].length;++j)	{
			int basin=markBasin(heights,basinMarks,i,j);
			if (basin>0) builder.accept(basin);
		}
		int[] basins=builder.build().sorted().toArray();
		int l=basins.length;
		long result=basins[l-1];
		result*=basins[l-2];
		result*=basins[l-3];
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
