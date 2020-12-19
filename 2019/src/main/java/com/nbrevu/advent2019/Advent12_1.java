package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent12_1 {
	private final static String IN_FILE="Advent12.txt";
	private final static int STEPS=1000;
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^<x=(\\-?\\d+), y=(\\-?\\d+), z=(\\-?\\d+)>$");
	
	private static class Moon	{
		private int x;
		private int y;
		private int z;
		private int vx;
		private int vy;
		private int vz;
		public Moon(int x,int y,int z)	{
			this.x=x;
			this.y=y;
			this.z=z;
		}
		public void applyGravity(int ax,int ay,int az)	{
			vx+=ax;
			vy+=ay;
			vz+=az;
		}
		public void move()	{
			x+=vx;
			y+=vy;
			z+=vz;
		}
		public long getPotentialEnergy()	{
			return Math.abs(x)+Math.abs(y)+Math.abs(z);
		}
		public long getKineticEnergy()	{
			return Math.abs(vx)+Math.abs(vy)+Math.abs(vz);
		}
		public long getTotalEnergy()	{
			return getPotentialEnergy()*getKineticEnergy();
		}
	}
	
	private static void applyGravity(List<Moon> moons)	{
		for (Moon m1:moons)	{
			int ax=0;
			int ay=0;
			int az=0;
			for (Moon m2:moons) if (m1!=m2)	{
				ax+=Integer.signum(m2.x-m1.x);
				ay+=Integer.signum(m2.y-m1.y);
				az+=Integer.signum(m2.z-m1.z);
			}
			m1.applyGravity(ax,ay,az);
		}
	}
	
	private static void step(List<Moon> moons)	{
		applyGravity(moons);
		moons.forEach(Moon::move);
	}
	
	private static long getSimulationValue(List<Moon> moons)	{
		for (int i=0;i<STEPS;++i) step(moons);
		long result=0;
		for (Moon m:moons) result+=m.getTotalEnergy();
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Moon> moons=new ArrayList<>();
		for(String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				int x=Integer.parseInt(matcher.group(1));
				int y=Integer.parseInt(matcher.group(2));
				int z=Integer.parseInt(matcher.group(3));
				moons.add(new Moon(x,y,z));
			}
		}
		System.out.println(getSimulationValue(moons));
	}
}
