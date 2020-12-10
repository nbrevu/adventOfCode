package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;

public class Advent20_2 {
	private final static String IN_FILE="Advent20.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^p=<(\\-?\\d+),(\\-?\\d+),(\\-?\\d+)>, v=<(\\-?\\d+),(\\-?\\d+),(\\-?\\d+)>, a=<(\\-?\\d+),(\\-?\\d+),(\\-?\\d+)>$");
	
	private static class FixedArray	{
		private final long[] array;
		public FixedArray(long[] array)	{
			this.array=Arrays.copyOf(array,array.length);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(array);
		}
		@Override
		public boolean equals(Object other)	{
			FixedArray fa=(FixedArray)other;
			return Arrays.equals(array,fa.array);
		}
	}
	
	private static class Particle	{
		private long[] position;
		private long[] velocity;
		private long[] acceleration;
		public Particle(long[] position,long[] velocity,long[] acceleration)	{
			this.position=position;
			this.velocity=velocity;
			this.acceleration=acceleration;
		}
		private void nextFrame()	{
			for (int i=0;i<3;++i)	{
				velocity[i]+=acceleration[i];
				position[i]+=velocity[i];
			}
		}
		public FixedArray getPosition()	{
			return new FixedArray(position);
		}
	}
	
	private static Particle createParticle(Matcher matcher)	{
		long[] pos=new long[] {Long.parseLong(matcher.group(1)),Long.parseLong(matcher.group(2)),Long.parseLong(matcher.group(3))};
		long[] vel=new long[] {Long.parseLong(matcher.group(4)),Long.parseLong(matcher.group(5)),Long.parseLong(matcher.group(6))};
		long[] acc=new long[] {Long.parseLong(matcher.group(7)),Long.parseLong(matcher.group(8)),Long.parseLong(matcher.group(9))};
		return new Particle(pos,vel,acc);
	}
	
	private static long simulateCollisions(List<Particle> ps)	{
		List<Particle> currentGen=ps;
		Multimap<FixedArray,Particle> particlesPerPosition=HashMultimap.create();
		// There are certainly finer ways to calculate bounds. But this should work.
		for (long i=0;i<100;++i)	{
			for (Particle p:currentGen) particlesPerPosition.put(p.getPosition(),p);
			List<Particle> nextGen=new ArrayList<>();
			for (Collection<Particle> samePolong:particlesPerPosition.asMap().values()) if (samePolong.size()<=1) nextGen.addAll(samePolong);
			particlesPerPosition.clear();
			nextGen.forEach(Particle::nextFrame);
			currentGen=nextGen;
		}
		return currentGen.size();
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Particle> particles=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches()) particles.add(createParticle(matcher));
		}
		long result=simulateCollisions(particles);
		System.out.println(result);
	}
}
