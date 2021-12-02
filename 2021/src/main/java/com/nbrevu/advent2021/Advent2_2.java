package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent2_2 {
	private final static String IN_FILE="Advent2.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^(\\w+) (\\d+)$");
	
	private static class Position	{
		public final long horizPos;
		public final long depth;
		public final long aim;
		public Position(long horizPos,long depth,long aim)	{
			this.horizPos=horizPos;
			this.depth=depth;
			this.aim=aim;
		}
	}
	
	private static enum Direction	{
		FORWARD("forward")	{
			@Override
			public Position move(Position current,long x)	{
				return new Position(current.horizPos+x,current.depth+current.aim*x,current.aim);
			}
		},
		UP("up")	{
			@Override
			public Position move(Position current,long x)	{
				return new Position(current.horizPos,current.depth,current.aim-x);
			}
		},
		DOWN("down")	{
			@Override
			public Position move(Position current,long x)	{
				return new Position(current.horizPos,current.depth,current.aim+x);
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
		Position p=new Position(0,0,0);
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher m=LINE_PATTERN.matcher(line);
			if (!m.matches()) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			Direction dir=Direction.getFromString(m.group(1));
			long amount=Long.parseLong(m.group(2));
			p=dir.move(p,amount);
		}
		System.out.println(p.horizPos*p.depth);
	}
}
