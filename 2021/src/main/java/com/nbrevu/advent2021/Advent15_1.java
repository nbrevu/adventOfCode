package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_1 {
	private final static String IN_FILE="Advent15.txt";
	
	private static int min(int[] array)	{
		int result=array[0];
		for (int i=1;i<array.length;++i) result=Math.min(result,array[i]);
		return result;
	}
	
	private static class OptimalPaths	{
		private final int[][] numbers;
		public OptimalPaths(int[][] numbers)	{
			this.numbers=numbers;
		}
		public int getBestResult()	{
			// First iteration: greedy algorithm that only moves to the right or the bottom.
			int[][] result=new int[numbers.length][numbers[0].length];
			result[0][0]=0;
			for (int j=1;j<numbers[0].length;++j) result[0][j]=result[0][j-1]+numbers[0][j];
			for (int i=1;i<numbers.length;++i)	{
				result[i][0]=result[i-1][0]+numbers[i][0];
				for (int j=1;j<numbers[i].length;++j) result[i][j]=numbers[i][j]+Math.min(result[i-1][j],result[i][j-1]);
			}
			// Now trying to get a better result.
			int[] neighbours=new int[4];
			for (;;)	{
				boolean anyBetter=false;
				for (int i=0;i<numbers.length;++i) for (int j=0;j<numbers[i].length;++j)	{
					neighbours[0]=(j<numbers[i].length-1)?(result[i][j+1]+numbers[i][j]):result[i][j];
					neighbours[1]=(i>0)?(result[i-1][j]+numbers[i][j]):result[i][j];
					neighbours[2]=(j>0)?(result[i][j-1]+numbers[i][j]):result[i][j];
					neighbours[3]=(i<numbers.length-1)?(result[i+1][j]+numbers[i][j]):result[i][j];
					int bestResult=min(neighbours);
					if (bestResult<result[i][j])	{
						result[i][j]=bestResult;
						anyBetter=true;
					}
				}
				if (!anyBetter) break;
			}
			return result[result.length-1][result[0].length-1];
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		int[][] numbers=new int[lines.size()][lines.get(0).length()];
		for (int i=0;i<lines.size();++i)	{
			String line=lines.get(i);
			if (line.length()!=numbers[i].length) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			for (int j=0;j<line.length();++j) numbers[i][j]=line.charAt(j)-'0';
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(new OptimalPaths(numbers).getBestResult());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
