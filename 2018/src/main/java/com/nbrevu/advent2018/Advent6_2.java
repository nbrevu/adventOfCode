package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;

public class Advent6_2 {
	private final static String IN_FILE="Advent6.txt";
	private final static int THRESHOLD=10000;
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^(\\d+), (\\d+)$");
	
	private static class CenterDefinition	{
		public final int x;
		public final int y;
		public CenterDefinition(int x,int y)	{
			this.x=x;
			this.y=y;
		}
	}
	
	private static class DisplacedMatrix	{
		private final int[][] data;
		private final int offsetX;
		private final int offsetY;
		public DisplacedMatrix(int minX,int maxX,int minY,int maxY)	{
			int height=maxY+1-minY;
			int width=maxX+1-minX;
			data=new int[height][width];
			offsetX=minX;
			offsetY=minY;
		}
		public void markCenter(int x,int y)	{
			int jj=x-offsetX;
			int ii=y-offsetY;
			for (int i=0;i<data.length;++i) for (int j=0;j<data[i].length;++j) data[i][j]+=Math.abs(i-ii)+Math.abs(j-jj);
		}
		public boolean[][] getUnderThreshold(int threshold)	{
			boolean[][] result=new boolean[data.length][];
			for (int i=0;i<data.length;++i)	{
				result[i]=new boolean[data[i].length];
				for (int j=0;j<data.length;++j) result[i][j]=(data[i][j]<threshold);
			}
			return result;
		}
	}
	
	private static DisplacedMatrix initMatrix(List<CenterDefinition> centers)	{
		int minX=Integer.MAX_VALUE;
		int maxX=Integer.MIN_VALUE;
		int minY=Integer.MAX_VALUE;
		int maxY=Integer.MIN_VALUE;
		for (CenterDefinition def:centers)	{
			minX=Math.min(minX,def.x);
			maxX=Math.max(maxX,def.x);
			minY=Math.min(minY,def.y);
			maxY=Math.max(maxY,def.y);
		}
		--minX;
		++maxX;
		--minY;
		++maxY;
		DisplacedMatrix result=new DisplacedMatrix(minX,maxX,minY,maxY);
		for (CenterDefinition center:centers) result.markCenter(center.x,center.y);
		return result;
	}
	
	/*
	 * Unfortunately the recursive algorithm won't do. Time for an iterative one.
	 */
	private static int findRegionSize(boolean[][] values,int ii,int jj)	{
		// i to j multimap.
		Multimap<Integer,Integer> visitedPixels=HashMultimap.create();
		Multimap<Integer,Integer> pendingPixels=HashMultimap.create();
		pendingPixels.put(ii,jj);
		while (!pendingPixels.isEmpty())	{
			Multimap<Integer,Integer> nextGen=HashMultimap.create();
			for (Map.Entry<Integer,Integer> pixel:pendingPixels.entries())	{
				int i=pixel.getKey();
				int j=pixel.getValue();
				visitedPixels.put(i,j);
				values[i][j]=false;
				if ((j<values[i].length-1)&&values[i][j+1]&&!visitedPixels.containsEntry(i,j+1)) nextGen.put(i,j+1);
				if ((i>0)&&values[i-1][j]&&!visitedPixels.containsEntry(i-1,j)) nextGen.put(i-1,j);
				if ((j>0)&&values[i][j-1]&&!visitedPixels.containsEntry(i,j-1)) nextGen.put(i,j-1);
				if ((i<values.length-1)&&values[i+1][j]&&!visitedPixels.containsEntry(i,j+1)) nextGen.put(i+1,j);
			}
			pendingPixels=nextGen;
		}
		return visitedPixels.size();
	}
	
	private static int findMaxRegion(boolean[][] values)	{
		int result=-1;
		for (int i=0;i<values.length;++i) for (int j=0;j<values[i].length;++j) if (values[i][j]) result=Math.max(result,findRegionSize(values,i,j));
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<CenterDefinition> centers=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				centers.add(new CenterDefinition(Integer.parseInt(matcher.group(1)),Integer.parseInt(matcher.group(2))));
			}	else System.out.println("Not.");
		}
		DisplacedMatrix data=initMatrix(centers);
		boolean[][] values=data.getUnderThreshold(THRESHOLD);
		int size=findMaxRegion(values);
		System.out.println(size);
	}
}
