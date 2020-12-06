package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;

public class Advent15_18_2 {
	private final static String IN_FILE="Advent18.txt";
	private final static int SIZE=100;
	private final static int STEPS=100;
	
	private final static CharObjMap<Boolean> LIGHT_SYMBOLS=HashCharObjMaps.newImmutableMap(new char[] {'.','#'},new Boolean[] {Boolean.FALSE,Boolean.TRUE});
	
	private static void evolve(boolean[][] source,boolean[][] target)	{
		for (int i=0;i<SIZE;++i) for (int j=0;j<SIZE;++j)	{
			int onNeighbours=0;
			boolean hasRight=j<SIZE-1;
			boolean hasUp=i>0;
			boolean hasLeft=j>0;
			boolean hasDown=i<SIZE-1;
			if (hasUp&&hasLeft&&source[i-1][j-1]) ++onNeighbours;
			if (hasUp&&source[i-1][j]) ++onNeighbours;
			if (hasUp&&hasRight&&source[i-1][j+1]) ++onNeighbours;
			if (hasLeft&&source[i][j-1]) ++onNeighbours;
			if (hasRight&&source[i][j+1]) ++onNeighbours;
			if (hasDown&&hasLeft&&source[i+1][j-1]) ++onNeighbours;
			if (hasDown&&source[i+1][j]) ++onNeighbours;
			if (hasDown&&hasRight&&source[i+1][j+1]) ++onNeighbours;
			target[i][j]=(onNeighbours==3)||(source[i][j]&&(onNeighbours==2));
		}
		target[0][0]=true;
		target[0][SIZE-1]=true;
		target[SIZE-1][0]=true;
		target[SIZE-1][SIZE-1]=true;
	}
	
	private static void readRow(String line,boolean[] toFill)	{
		for (int i=0;i<SIZE;++i) toFill[i]=LIGHT_SYMBOLS.get(line.charAt(i));
	}
	
	private static int countLights(boolean[][] lights)	{
		int result=0;
		for (int i=0;i<SIZE;++i) for (int j=0;j<SIZE;++j) if (lights[i][j]) ++result;
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		boolean[][] current=new boolean[SIZE][SIZE];
		boolean[][] reserve=new boolean[SIZE][SIZE];
		int currentRow=0;
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			readRow(line,current[currentRow]);
			++currentRow;
		}
		current[0][0]=true;
		current[0][SIZE-1]=true;
		current[SIZE-1][0]=true;
		current[SIZE-1][SIZE-1]=true;
		for (int i=0;i<STEPS;++i)	{
			evolve(current,reserve);
			boolean[][] swap=current;
			current=reserve;
			reserve=swap;
		}
		System.out.println(countLights(current));
	}
}
