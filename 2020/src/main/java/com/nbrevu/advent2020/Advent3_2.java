package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent3_2 {
	private final static String IN_FILE="Advent3.txt";
	
	private static interface LineSkipper {
		public OptionalInt next();
	}
	
	public static class SingleLineSkipper implements LineSkipper	{
		private final int incr;
		private int current;
		public SingleLineSkipper(int incr)	{
			this.incr=incr;
			current=0;
		}
		@Override
		public OptionalInt next() {
			OptionalInt result=OptionalInt.of(current);
			current+=incr;
			return result;
		}
	}
	public static class MultipleLineSkipper implements LineSkipper	{
		private final int yIncr;
		private int currentX;
		private int currentYCount;
		public MultipleLineSkipper(int yIncr)	{
			this.yIncr=yIncr;
			currentX=0;
			currentYCount=yIncr;
		}
		@Override
		public OptionalInt next() {
			if (currentYCount>=yIncr)	{
				OptionalInt result=OptionalInt.of(currentX);
				++currentX;
				currentYCount=1;
				return result;
			}	else	{
				++currentYCount;
				return OptionalInt.empty();
			}
		}
	}
	
	private static class SkipperWithCounter	{
		private final LineSkipper skipper;
		private int counter;
		public SkipperWithCounter(LineSkipper skipper)	{
			this.skipper=skipper;
			counter=0;
		}
		public void countLine(String line)	{
			OptionalInt position=skipper.next();
			if (position.isPresent())	{
				int pos=position.getAsInt()%line.length();
				if (line.charAt(pos)=='#') ++counter;
			}
		}
		public int getValue()	{
			return counter;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		List<SkipperWithCounter> skippers=new ArrayList<>();
		skippers.add(new SkipperWithCounter(new SingleLineSkipper(1)));
		skippers.add(new SkipperWithCounter(new SingleLineSkipper(3)));
		skippers.add(new SkipperWithCounter(new SingleLineSkipper(5)));
		skippers.add(new SkipperWithCounter(new SingleLineSkipper(7)));
		skippers.add(new SkipperWithCounter(new MultipleLineSkipper(2)));
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8)) for (SkipperWithCounter skipper:skippers) skipper.countLine(line);
		long result=1l;
		for (SkipperWithCounter skipper:skippers) result*=skipper.getValue();
		System.out.println(result);
	}
}
