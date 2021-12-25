package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;

public class Advent25 {
	private final static String IN_FILE="Advent25.txt";
	
	private final static Predicate<String> IS_LINE_VALID=Pattern.compile("^[\\.v>]+$").asMatchPredicate();
	
	private static enum MovementType	{
		DOWN('v'),RIGHT('>');
		
		private final char id;
		private MovementType(char id)	{
			this.id=id;
		}
		private final static CharObjMap<MovementType> ID_MAP=getIdMap();
		private static CharObjMap<MovementType> getIdMap()	{
			CharObjMap<MovementType> result=HashCharObjMaps.newMutableMap();
			for (MovementType t:values()) result.put(t.id,t);
			return result;
		}
		public static MovementType getFrom(char c)	{
			return ID_MAP.get(c);
		}
	}
	
	private static class Area	{
		private MovementType[][] contents;
		public Area(int height,int width)	{
			contents=new MovementType[height][width];
		}
		public void set(int row,int column,MovementType type)	{
			contents[row][column]=type;
		}
		private boolean moveEast()	{
			boolean anyMove=false;
			for (int i=0;i<contents.length;++i)	{
				boolean canMoveLast=contents[i][0]==null;
				boolean lastMoved=false;
				for (int j=1;j<contents[i].length;++j) if (!lastMoved&&(contents[i][j]==null)&&(contents[i][j-1]==MovementType.RIGHT))	{
					anyMove=true;
					contents[i][j-1]=null;
					contents[i][j]=MovementType.RIGHT;
					lastMoved=true;
				}	else lastMoved=false;
				if (!lastMoved&&canMoveLast&&(contents[i][contents[i].length-1]==MovementType.RIGHT))	{
					anyMove=true;
					contents[i][contents[i].length-1]=null;
					contents[i][0]=MovementType.RIGHT;
				}
			}
			return anyMove;
		}
		private boolean moveSouth()	{
			boolean anyMove=false;
			for (int i=0;i<contents[0].length;++i)	{
				boolean canMoveLast=contents[0][i]==null;
				boolean lastMoved=false;
				for (int j=1;j<contents.length;++j) if (!lastMoved&&(contents[j][i]==null)&&(contents[j-1][i]==MovementType.DOWN))	{
					anyMove=true;
					contents[j-1][i]=null;
					contents[j][i]=MovementType.DOWN;
					lastMoved=true;
				}	else lastMoved=false;
				if (!lastMoved&&canMoveLast&&(contents[contents.length-1][i]==MovementType.DOWN))	{
					anyMove=true;
					contents[contents.length-1][i]=null;
					contents[0][i]=MovementType.DOWN;
				}
			}
			return anyMove;
		}
		public boolean move()	{
			boolean step1=moveEast();
			boolean step2=moveSouth();
			return step1||step2;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		int width=lines.get(0).length();
		Area area=new Area(lines.size(),width);
		for (int i=0;i<lines.size();++i)	{
			String line=lines.get(i);
			if ((line.length()!=width)||!IS_LINE_VALID.test(line)) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			for (int j=0;j<width;++j) area.set(i,j,MovementType.getFrom(line.charAt(j)));
		}
		int steps=0;
		do ++steps; while (area.move());
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(steps);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
