package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Advent10_1 {
	private final static String IN_FILE="Advent10.txt";
	
	private final static Pattern VALUE_PATTERN=Pattern.compile("^value (\\d+) goes to bot (\\d+)$");
	private final static Pattern BOT_PATTERN=Pattern.compile("bot (\\d+) gives low to ([a-z]+) (\\d+) and high to ([a-z]+) (\\d+)$");
	private final static int[] GOAL=new int[] {17,61};
	
	private static class Bot	{
		public final int botId;
		public final String lowOutputType;
		public final int lowOutputId;
		public final String highOutputType;
		public final int highOutputId;
		private OptionalInt data1;
		private OptionalInt data2;
		public Bot(int botId,String lowOutputType,int lowOutputId,String highOutputType,int highOutputId)	{
			this.botId=botId;
			this.lowOutputType=lowOutputType;
			this.lowOutputId=lowOutputId;
			this.highOutputType=highOutputType;
			this.highOutputId=highOutputId;
			data1=OptionalInt.empty();
			data2=OptionalInt.empty();
		}
		public void receive(int value)	{
			if (data1.isEmpty()) data1=OptionalInt.of(value);
			else if (data2.isEmpty()) data2=OptionalInt.of(value);
			else throw new IllegalArgumentException("No time, no space.");
		}
		public boolean isComplete()	{
			return data1.isPresent()&&data2.isPresent();
		}
		public int[] values()	{
			if (!isComplete()) throw new IllegalArgumentException("Too soon.");
			int d1=data1.getAsInt();
			int d2=data2.getAsInt();
			if (d1>d2)	{
				int swap=d2;
				d2=d1;
				d1=swap;
			}
			return new int[] {d1,d2};
		}
		public IntIntMap assign()	{
			IntIntMap result=HashIntIntMaps.newMutableMap();
			int[] values=values();
			if (lowOutputType.equals("bot")) result.put(lowOutputId,values[0]);
			if (highOutputType.equals("bot")) result.put(highOutputId,values[1]);
			return result;
		}
	}
	
	private static void assign(Bot bot,int value,IntObjMap<Bot> bots)	{
		bot.receive(value);
		if (bot.isComplete())	{
			if (Arrays.equals(GOAL,bot.values())) System.out.println(bot.botId);
			IntIntMap assigned=bot.assign();
			for (IntIntCursor cursor=assigned.cursor();cursor.moveNext();) assign(bots.get(cursor.key()),cursor.value(),bots);
		}
	}
	
	private static void assign(IntIntMap values,IntObjMap<Bot> bots)	{
		for (IntIntCursor cursor=values.cursor();cursor.moveNext();) assign(bots.get(cursor.value()),cursor.key(),bots);
	}

	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		IntObjMap<Bot> bots=HashIntObjMaps.newMutableMap();
		IntIntMap values=HashIntIntMaps.newMutableMap();				
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=VALUE_PATTERN.matcher(line);
			if (matcher.matches())	{
				int value=Integer.parseInt(matcher.group(1));
				int bot=Integer.parseInt(matcher.group(2));
				if (values.containsKey(value)) throw new IllegalArgumentException("D'oh. I need lists, not a set.");
				values.put(value,bot);
				continue;
			}
			matcher=BOT_PATTERN.matcher(line);
			if (matcher.matches())	{
				int botId=Integer.parseInt(matcher.group(1));
				String lowType=matcher.group(2);
				int lowId=Integer.parseInt(matcher.group(3));
				String highType=matcher.group(4);
				int highId=Integer.parseInt(matcher.group(5));
				if (bots.containsKey(botId)) throw new IllegalArgumentException("Un bot, ¿dos bots?");
				bots.put(botId,new Bot(botId,lowType,lowId,highType,highId));
			}
		}
		assign(values,bots);
	}
}
