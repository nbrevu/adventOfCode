package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent22_2 {
	private final static String IN_FILE="Advent22.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^(on|off) x=(\\-?\\d+)\\.\\.(\\-?\\d+),y=(\\-?\\d+)\\.\\.(\\-?\\d+),z=(\\-?\\d+)\\.\\.(\\-?\\d+)$");
	
	private static class Cuboid	{
		public final long x0;
		public final long x1;
		public final long y0;
		public final long y1;
		public final long z0;
		public final long z1;
		public Cuboid(long x0,long x1,long y0,long y1,long z0,long z1)	{
			this.x0=x0;
			this.x1=x1;
			this.y0=y0;
			this.y1=y1;
			this.z0=z0;
			this.z1=z1;
		}
		// This cuboid will not change, but the "other" might be divided into pieces, represented by the result of this method.
		public CuboidIntersection intersect(Cuboid other)	{
			long minX=Math.max(x0,other.x0);
			long maxX=Math.min(x1,other.x1);
			long minY=Math.max(y0,other.y0);
			long maxY=Math.min(y1,other.y1);
			long minZ=Math.max(z0,other.z0);
			long maxZ=Math.min(z1,other.z1);
			if ((minX>maxX)||(minY>maxY)||(minZ>maxZ)) return null;
			List<Cuboid> result=new ArrayList<>();
			// First we "sandwich" the X part.
			if (other.x0<minX) result.add(new Cuboid(other.x0,minX-1,other.y0,other.y1,other.z0,other.z1));
			if (maxX<other.x1) result.add(new Cuboid(maxX+1,other.x1,other.y0,other.y1,other.z0,other.z1));
			// Now we can work in the minX..maxX intersection. First, again, let's sandwich the y.
			if (other.y0<minY) result.add(new Cuboid(minX,maxX,other.y0,minY-1,other.z0,other.z1));
			if (maxY<other.y1) result.add(new Cuboid(minX,maxX,maxY+1,other.y1,other.z0,other.z1));
			// And now we have sandwiched the X and Y dimensions and we can just add the z part.
			if (other.z0<minZ) result.add(new Cuboid(minX,maxX,minY,maxY,other.z0,minZ-1));
			if (maxZ<other.z1) result.add(new Cuboid(minX,maxX,minY,maxY,maxZ+1,other.z1));
			return new CuboidIntersection(result);
		}
		public long getVolume()	{
			long dx=x1+1-x0;
			long dy=y1+1-y0;
			long dz=z1+1-z0;
			return dx*dy*dz;
		}
	}
	
	/*
	 * One of the cubes will remain "static". The other one will be divided in at most 27 pieces, all of them cuboids, so that one of them
	 * represents the intersecting part and all the other are the non-intersecting part.
	 * 
	 * It seems that the "intersecting" part is not actually needed.
	 */
	private static class CuboidIntersection	{
		public final List<Cuboid> nonIntersectingParts;
		public CuboidIntersection(List<Cuboid> nonIntersectingParts)	{
			this.nonIntersectingParts=nonIntersectingParts;
		}
	}
	
	private static class CuboidCollection	{
		/*
		 * Keeps a list of NON-INTERSECTING cuboids, all on. The intersection calculations are done in such a way that the non-intersecting
		 * property is carefully preserved.
		 */
		private final List<Cuboid> onCuboids;
		public CuboidCollection()	{
			onCuboids=new ArrayList<>();
		}
		public long getVolume()	{
			long result=0;
			for (Cuboid c:onCuboids) result+=c.getVolume();
			return result;
		}
		public void turnOn(Cuboid newCuboid)	{
			turnOff(newCuboid);
			onCuboids.add(newCuboid);
		}
		public void turnOff(Cuboid newCuboid)	{
			List<Cuboid> toRemove=new ArrayList<>();
			List<Cuboid> toAdd=new ArrayList<>();
			for (Cuboid knownCuboid:onCuboids)	{
				CuboidIntersection intersection=newCuboid.intersect(knownCuboid);
				if (intersection!=null)	{
					toRemove.add(knownCuboid);
					toAdd.addAll(intersection.nonIntersectingParts);
				}
			}
			onCuboids.removeAll(toRemove);
			onCuboids.addAll(toAdd);
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		CuboidCollection cubes=new CuboidCollection();
		for (String s:lines)	{
			Matcher m=LINE_PATTERN.matcher(s);
			if (m.matches())	{
				boolean isOn="on".equals(m.group(1));
				Cuboid c=new Cuboid(Integer.parseInt(m.group(2)),Integer.parseInt(m.group(3)),Integer.parseInt(m.group(4)),Integer.parseInt(m.group(5)),Integer.parseInt(m.group(6)),Integer.parseInt(m.group(7)));
				if (isOn) cubes.turnOn(c);
				else cubes.turnOff(c);
			}	else throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(cubes.getVolume());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
