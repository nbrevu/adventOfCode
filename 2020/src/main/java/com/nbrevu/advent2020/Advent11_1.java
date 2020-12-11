package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;

public class Advent11_1 {
	private final static String IN_FILE="Advent11.txt";
	
	private static enum Seat	{
		FLOOR,EMPTY,OCCUPIED;
		
		private final static CharObjMap<Seat> CHAR_IDS=getCharIds();
		private static CharObjMap<Seat> getCharIds()	{
			CharObjMap<Seat> result=HashCharObjMaps.newMutableMap();
			result.put('.',FLOOR);
			result.put('L',EMPTY);
			result.put('#',OCCUPIED);
			return result;
		}
		public static Seat parse(char c)	{
			Seat result=CHAR_IDS.get(c);
			if (result==null) throw new IllegalArgumentException("Was ist das?");
			return result;
		}
	}
	
	private static Seat[] parseString(String in)	{
		Seat[] result=new Seat[in.length()];
		for (int i=0;i<in.length();++i) result[i]=Seat.parse(in.charAt(i));
		return result;
	}
	
	private static Seat nextState(Seat currentState,int occupiedNeighbours)	{
		if (currentState==Seat.FLOOR) return currentState;
		if (occupiedNeighbours==0) return Seat.OCCUPIED;
		if (occupiedNeighbours>=4) return Seat.EMPTY;
		return currentState;
	}
	
	private static int countNeighbours(Seat[][] state,int baseI,int baseJ)	{
		int i0=baseI-1;
		int iF=baseI+1;
		int j0=baseJ-1;
		int jF=baseJ+1;
		if (i0<0) i0=0;
		if (iF>=state.length) iF=state.length-1;
		if (j0<0) j0=0;
		if (jF>=state[baseI].length) jF=state[baseI].length-1;
		int result=0;
		for (int i=i0;i<=iF;++i) for (int j=j0;j<=jF;++j) if (((i!=baseI)||(j!=baseJ))&&state[i][j]==Seat.OCCUPIED) ++result;
		return result;
	}
	
	private static Seat[][] iterate(Seat[][] currentState)	{
		Seat[][] result=new Seat[currentState.length][];
		for (int i=0;i<currentState.length;++i)	{
			result[i]=new Seat[currentState[i].length];
			for (int j=0;j<currentState[i].length;++j) result[i][j]=nextState(currentState[i][j],countNeighbours(currentState,i,j));
		}
		return result;
	}
	
	private static boolean equals(Seat[][] array1,Seat[][] array2)	{
		if (array1.length!=array2.length) return false;
		for (int i=0;i<array1.length;++i) if (array1[i].length!=array2[i].length) return false;
		else for (int j=0;j<array1[i].length;++j) if (array1[i][j]!=array2[i][j]) return false;
		return true;
	}
	
	private static int countOccupied(Seat[][] array)	{
		int result=0;
		for (Seat[] subArray:array) for (Seat s:subArray) if (s==Seat.OCCUPIED) ++result;
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> contents=Resources.readLines(file,Charsets.UTF_8);
		Seat[][] basePositions=new Seat[contents.size()][];
		for (int i=0;i<contents.size();++i) basePositions[i]=parseString(contents.get(i));
		for (;;)	{
			Seat[][] nextStep=iterate(basePositions);
			if (equals(basePositions,nextStep))	{
				System.out.println(countOccupied(nextStep));
				return;
			}
			basePositions=nextStep;
		}
	}
}
