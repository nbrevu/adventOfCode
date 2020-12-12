package com.nbrevu.advent2018;

public class Advent11_2 {
	private final static int SERIAL_NUMBER=7857;
	
	private final static int SIZE_X=300;
	private final static int SIZE_Y=300;
	
	private static int calculatePower(int x,int y)	{
		long rackId=x+10;
		long basePower=rackId*y+SERIAL_NUMBER;
		basePower*=rackId;
		return (int)((basePower/100)%10)-5;
	}
	
	private static int[][] calculateGrid(int maxX,int maxY)	{
		int[][] result=new int[maxY][maxX];
		for (int i=0;i<maxY;++i) for (int j=0;j<maxX;++j) result[i][j]=calculatePower(j+1,i+1);
		return result;
	}
	
	/*
	 * This can be sped up using dynamic arrays of partial sums of rows and columns, making the total time cubic. The current algorithm is
	 * quartic, and it's much more simple and requires less code tuning (it's still slow, though).
	 * 
	 * The trivial one is quintic, which is too much when N=300.
	 */
	private static String getBestSum(int[][] grid)	{
		int bestSum=0;
		int bestX=0;
		int bestY=0;
		int bestSize=0;
		int[][][] dynamicArray=new int[grid.length][][];
		dynamicArray[0]=grid;
		for (int s=1;s<grid.length;++s)	{
			int reduced=grid.length-s;
			dynamicArray[s]=new int[reduced][reduced];
			for (int i=0;i<reduced;++i) for (int j=0;j<reduced;++j)	{
				int sum=dynamicArray[s-1][i][j]+grid[i+s][j+s];
				for (int k=0;k<s;++k) sum+=grid[i+k][j+s]+grid[i+s][j+k];
				if (sum>=bestSum)	{
					bestSum=sum;
					bestX=j+1;
					bestY=i+1;
					bestSize=s+1;
				}
				dynamicArray[s][i][j]=sum;
			}
		}
		return bestX+","+bestY+","+bestSize;
	}
	
	public static void main(String[] args)	{
		int[][] grid=calculateGrid(SIZE_X,SIZE_Y);
		String bestSum=getBestSum(grid);
		System.out.println(bestSum);
	}
}
