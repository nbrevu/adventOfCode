package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent2_1 {
	private final static String IN_FILE="Advent2.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^(\\w+) (\\d+)$");
	
	private static class Position	{
		public final long x;
		public final long y;
		public Position(long x,long y)	{
			this.x=x;
			this.y=y;
		}
	}
	
	private static enum Direction	{
		FORWARD("forward")	{
			@Override
			public Position move(Position current,long amount)	{
				return new Position(current.x+amount,current.y);
			}
		},
		UP("up")	{
			@Override
			public Position move(Position current,long amount)	{
				return new Position(current.x,current.y-amount);
			}
		},
		DOWN("down")	{
			@Override
			public Position move(Position current,long amount)	{
				return new Position(current.x,current.y+amount);
			}
		};
		private final static Map<String,Direction> ENUM_MAP=createEnumMap();
		private static Map<String,Direction> createEnumMap()	{
			Map<String,Direction> result=new HashMap<>();
			for (Direction d:values()) result.put(d.id,d);
			return result;
		}
		public final String id;
		private Direction(String id)	{
			this.id=id;
		}
		public abstract Position move(Position current,long amount);
		public static Direction getFromString(String str)	{
			return ENUM_MAP.get(str);
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		Position p=new Position(0,0);
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher m=LINE_PATTERN.matcher(line);
			if (!m.matches()) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			Direction dir=Direction.getFromString(m.group(1));
			long amount=Long.parseLong(m.group(2));
			p=dir.move(p,amount);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(p.x*p.y);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
