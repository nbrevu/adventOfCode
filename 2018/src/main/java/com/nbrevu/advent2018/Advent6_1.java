package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Advent6_1 {
	private final static String IN_FILE="Advent6.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^(\\d+), (\\d+)$");
	
	private static interface Square	{
		public int getId();
	}
	private static class Center implements Square	{
		public final int id;
		public Center(int id)	{
			this.id=id;
		}
		@Override
		public int getId()	{
			return id;
		}
	}
	private static enum InvalidSquare implements Square	{
		INSTANCE;
		@Override
		public int getId()	{
			return -1;
		}
	}
	private static class NormalSquare implements Square	{
		public final int id;
		private NormalSquare(int id)	{
			this.id=id;
		}
		@Override
		public int getId()	{
			return id;
		}
		private final static IntObjMap<NormalSquare> INSTANCES=HashIntObjMaps.newMutableMap();
		public static NormalSquare getInstance(int value)	{
			return INSTANCES.computeIfAbsent(value,NormalSquare::new);
		}
	}
	private static class CenterDefinition	{
		public final int id;
		public final int x;
		public final int y;
		public CenterDefinition(int id,int x,int y)	{
			this.id=id;
			this.x=x;
			this.y=y;
		}
	}
	
	private static class DisplacedMatrix	{
		private final Square[][] data;
		private final int offsetX;
		private final int offsetY;
		public DisplacedMatrix(int minX,int maxX,int minY,int maxY)	{
			int height=maxY+1-minY;
			int width=maxX+1-minX;
			data=new Square[height][width];
			offsetX=minX;
			offsetY=minY;
		}
		public void set(int x,int y,Square elem)	{
			data[y-offsetY][x-offsetX]=elem;
		}
		/* Not needed.
		public Square get(int x,int y)	{
			return data[y-offsetY][x-offsetX];
		}
		*/
		public void fillClosest()	{
			for (int i=0;i<data.length;++i) for (int j=0;j<data[i].length;++j) if (data[i][j]==null) data[i][j]=findClosest(i,j);
		}
		private Square findClosest(int y,int x)	{
			// Caution. This will run an infinite loop if there isn't any center set in the matrix.
			List<Center> centers=new ArrayList<>();
			for (int d=1;;++d)	{
				int j=x+d;
				int i=y;
				// First quadrant...
				for (;j>x;--j,--i) if ((i>=0)&&(j<data[i].length))	{
					Square s=data[i][j];
					if (s instanceof Center) centers.add((Center)s);
				}
				// Second quadrant...
				for (;i<y;--j,++i) if ((i>=0)&&(j>=0))	{
					Square s=data[i][j];
					if (s instanceof Center) centers.add((Center)s);
				}
				// Third quadrant...
				for (;j<x;++j,++i) if ((i<data.length)&&(j>=0))	{
					Square s=data[i][j];
					if (s instanceof Center) centers.add((Center)s);
				}
				for (;i>y;++j,--i) if ((i<data.length)&&(j<data[i].length))	{
					Square s=data[i][j];
					if (s instanceof Center) centers.add((Center)s);
				}
				if (centers.size()>=2) return InvalidSquare.INSTANCE;
				else if (centers.size()==1) return NormalSquare.getInstance(centers.get(0).id);
			}
		}
		public IntSet findInvalidIndices()	{
			// This should only be called after fillClosest() has run. Otherwise, expect NPEs.
			IntSet result=HashIntSets.newMutableSet();
			result.add(InvalidSquare.INSTANCE.getId());
			for (int j=0;j<data[0].length;++j)	{
				result.add(data[0][j].getId());
				result.add(data[data.length-1][j].getId());
			}
			for (int i=1;i<data.length-1;++i)	{
				result.add(data[i][0].getId());
				result.add(data[i][data[i].length-1].getId());
			}
			return result;
		}
		public IntIntMap countIds()	{
			IntIntMap result=HashIntIntMaps.newMutableMap();
			for (int i=0;i<data.length;++i) for (int j=0;j<data[i].length;++j) result.addValue(data[i][j].getId(),1,0);
			return result;
		}
	}
	
	private static DisplacedMatrix initMatrix(List<CenterDefinition> centers)	{
		int minX=Integer.MAX_VALUE;
		int maxX=Integer.MIN_VALUE;
		int minY=Integer.MAX_VALUE;
		int maxY=Integer.MIN_VALUE;
		for (CenterDefinition def:centers)	{
			minX=Math.min(minX,def.x);
			maxX=Math.max(maxX,def.x);
			minY=Math.min(minY,def.y);
			maxY=Math.max(maxY,def.y);
		}
		--minX;
		++maxX;
		--minY;
		++maxY;
		DisplacedMatrix result=new DisplacedMatrix(minX,maxX,minY,maxY);
		for (CenterDefinition center:centers) result.set(center.x,center.y,new Center(center.id));
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<CenterDefinition> centers=new ArrayList<>();
		int id=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				++id;
				centers.add(new CenterDefinition(id,Integer.parseInt(matcher.group(1)),Integer.parseInt(matcher.group(2))));
			}	else System.out.println("Not.");
		}
		DisplacedMatrix data=initMatrix(centers);
		data.fillClosest();
		IntSet invalid=data.findInvalidIndices();
		IntIntMap counters=data.countIds();
		int maxValue=Integer.MIN_VALUE;
		for (IntIntCursor cursor=counters.cursor();cursor.moveNext();) if (!invalid.contains(cursor.key())) maxValue=Math.max(maxValue,cursor.value());
		System.out.println(maxValue);
	}
}
