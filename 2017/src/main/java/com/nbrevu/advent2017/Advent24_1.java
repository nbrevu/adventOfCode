package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;

public class Advent24_1 {
	private final static String IN_FILE="Advent24.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^(\\d+)/(\\d+)$");
	
	private static class Connection	{
		public final int strength;
		public final int openPort;
		private final BitSet components;
		public Connection()	{
			strength=0;
			openPort=0;
			components=new BitSet(0);
		}
		private Connection(int strength,int openPort,BitSet components)	{
			this.strength=strength;
			this.openPort=openPort;
			this.components=components;
		}
		private boolean isPresent(Component c)	{
			return components.get(c.id);
		}
		private Connection connect(Component c)	{
			int newStr=strength+c.pin1+c.pin2;
			BitSet newComp=(BitSet)components.clone();
			newComp.set(c.id);
			if (c.pin1==openPort) return new Connection(newStr,c.pin2,newComp);
			else if (c.pin2==openPort) return new Connection(newStr,c.pin1,newComp);
			else throw new IllegalArgumentException("No me cabe.");
		}
	}
	
	private static class Component	{
		public final int id;
		public final int pin1;
		public final int pin2;
		public Component(int id,int pin1,int pin2)	{
			this.id=id;
			this.pin1=pin1;
			this.pin2=pin2;
		}
	}
	
	private static int findMaxStrength(Multimap<Integer,Component> components)	{
		Connection c=new Connection();
		return findMaxStrengthRecursive(components,c);
	}
	
	private static int findMaxStrengthRecursive(Multimap<Integer,Component> components,Connection currentStatus)	{
		int result=currentStatus.strength;
		for (Component c:components.get(currentStatus.openPort)) if (!currentStatus.isPresent(c))	{
			Connection nextStatus=currentStatus.connect(c);
			int tmpResult=findMaxStrengthRecursive(components,nextStatus);
			result=Math.max(result,tmpResult);
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		Multimap<Integer,Component> components=HashMultimap.create();
		int id=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				int pin1=Integer.parseInt(matcher.group(1));
				int pin2=Integer.parseInt(matcher.group(2));
				Component c=new Component(id,pin1,pin2);
				components.put(pin1,c);
				components.put(pin2,c);
				++id;
			}
		}
		System.out.println(findMaxStrength(components));
	}
}
