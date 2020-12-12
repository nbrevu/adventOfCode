package com.nbrevu.advent2018;

public class Advent11_1 {
	private final static int SERIAL_NUMBER=7857;
	
	private final static int SIZE_X=300;
	private final static int SIZE_Y=300;
	private final static int HEIGHT=3;
	private final static int WIDTH=3;
	
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
	
	// The grid is too small. The extra complexity of the "smart" algorithm is not worth here.
	private static String getBestSum(int[][] grid,int width,int height)	{
		int bestSum=0;
		int bestX=0;
		int bestY=0;
		for (int i=0;i<grid.length+1-height;++i) for (int j=0;j<grid[i].length+1-width;++j)	{
			int sum=0;
			for (int k=0;k<height;++k) for (int l=0;l<width;++l) sum+=grid[i+k][j+l];
			if (sum>=bestSum)	{
				bestSum=sum;
				bestX=j+1;
				bestY=i+1;
			}
		}
		return bestX+","+bestY;
	}
	
	public static void main(String[] args)	{
		int[][] grid=calculateGrid(SIZE_X,SIZE_Y);
		String bestSum=getBestSum(grid,WIDTH,HEIGHT);
		System.out.println(bestSum);
	}
}
