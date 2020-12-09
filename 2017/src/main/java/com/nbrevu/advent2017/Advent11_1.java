package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent11_1 {
	private final static String IN_FILE="Advent11.txt";
	
	/*
	 * First coordinate ("x"): 30 degrees in the first quadrant.
	 * Second coordinate ("y"): vertical, as always
	 */
	private static class Position	{
		public int x;
		public int y;
		public int distanceToOrigin()	{
			if (Math.signum(x)==Math.signum(y)) return Math.abs(x)+Math.abs(y);
			else return Math.max(Math.abs(x),Math.abs(y));
		}
	}
	
	private static enum Step	{
		NE	{
			@Override
			public void advance(Position pos) {
				++pos.x;
			}
		},
		N	{
			@Override
			public void advance(Position pos) {
				++pos.y;
			}
		},
		NW	{
			@Override
			public void advance(Position pos) {
				--pos.x;
				++pos.y;
			}
		},
		SW	{
			@Override
			public void advance(Position pos) {
				--pos.x;
			}
		},
		S	{
			@Override
			public void advance(Position pos) {
				--pos.y;
			}
		},
		SE	{
			@Override
			public void advance(Position pos) {
				++pos.x;
				--pos.y;
			}
		};
		public abstract void advance(Position pos);
		private final static Map<String,Step> map=createMap();
		private static Map<String,Step> createMap()	{
			Map<String,Step> result=new HashMap<>();
			result.put("ne",Step.NE);
			result.put("n",Step.N);
			result.put("nw",Step.NW);
			result.put("sw",Step.SW);
			result.put("s",Step.S);
			result.put("se",Step.SE);
			return result;
		}
		public static Step parse(String str)	{
			Step result=map.get(str);
			if (result==null) throw new IllegalArgumentException("Stop trying to go ana or kata.");
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		Position currentPos=new Position();
		currentPos.x=0;
		currentPos.y=0;
		String content=Resources.readLines(file,Charsets.UTF_8).get(0);
		for (String split:content.split(",")) Step.parse(split).advance(currentPos);
		System.out.println(currentPos.distanceToOrigin());
	}
}
