package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent20_1 {
	private final static String IN_FILE="Advent20.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^p=<(\\-?\\d+),(\\-?\\d+),(\\-?\\d+)>, v=<(\\-?\\d+),(\\-?\\d+),(\\-?\\d+)>, a=<(\\-?\\d+),(\\-?\\d+),(\\-?\\d+)>$");
	
	private static class Particle	{
		public final int id;
		private int[] velocity;
		public Particle(int id/*,int[] position*/,int[] velocity/*,int[] acceleration*/)	{
			this.id=id;
			// this.position=position;
			this.velocity=velocity;
			// this.acceleration=acceleration;
		}
	}
	
	private static Particle createParticle(Matcher matcher,int id)	{
		//int[] pos=new int[] {Integer.parseInt(matcher.group(1)),Integer.parseInt(matcher.group(2)),Integer.parseInt(matcher.group(3))};
		int[] vel=new int[] {Integer.parseInt(matcher.group(4)),Integer.parseInt(matcher.group(5)),Integer.parseInt(matcher.group(6))};
		//int[] acc=new int[] {Integer.parseInt(matcher.group(7)),Integer.parseInt(matcher.group(8)),Integer.parseInt(matcher.group(9))};
		return new Particle(id/*,pos*/,vel/*,acc*/);
	}
	
	private static Particle getNearestAssumingSameAccel(List<Particle> ps)	{
		int minVel=Integer.MAX_VALUE;
		Particle best=null;
		for (Particle p:ps)	{
			int vel=Math.abs(p.velocity[0])+Math.abs(p.velocity[1])+Math.abs(p.velocity[2]);
			if (vel<minVel)	{
				minVel=vel;
				best=p;
			}	// else if (vel==minVel) {...} // No need to consider this, doesn't happen in this problem.
		}
		return best;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int minAccel=Integer.MAX_VALUE;
		List<Particle> particles=new ArrayList<>();
		int id=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				int ax=Integer.parseInt(matcher.group(7));
				int ay=Integer.parseInt(matcher.group(8));
				int az=Integer.parseInt(matcher.group(9));
				int absAccel=Math.abs(ax)+Math.abs(ay)+Math.abs(az);
				if (absAccel<minAccel)	{
					minAccel=absAccel;
					particles.clear();
					particles.add(createParticle(matcher,id));
				}	else if (absAccel==minAccel) particles.add(createParticle(matcher,id));
				++id;
			}
		}
		Particle best=getNearestAssumingSameAccel(particles);
		System.out.println(best.id);
	}
}
