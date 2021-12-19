package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Advent19_2 {
	private final static String IN_FILE="Advent19.txt";
	
	private final static int MIN_POINTS=12;
	private final static Pattern SCANNER_PATTERN=Pattern.compile("^\\-\\-\\- scanner (\\d+) \\-\\-\\-$");
	
	private static class Point	{
		public final int x;
		public final int y;
		public final int z;
		public Point(int x,int y,int z)	{
			this.x=x;
			this.y=y;
			this.z=z;
		}
		@Override
		public int hashCode()	{
			return x+2000*y+4000000*z;
		}
		@Override
		public boolean equals(Object other)	{
			Point pOther=(Point)other;
			return (x==pOther.x)&&(y==pOther.y)&&(z==pOther.z);
		}
	}
	
	private static class RelativePointsInfo	{
		public final Map<IntSet,List<Point>> relativePointsByDistance;
		public final Set<Point> relativePoints;
		public RelativePointsInfo()	{
			relativePointsByDistance=new HashMap<>();
			relativePoints=new HashSet<>();
		}
		public void addPoint(Point rel)	{
			IntSet distances=HashIntSets.newMutableSet();
			distances.add(Math.abs(rel.x));
			distances.add(Math.abs(rel.y));
			distances.add(Math.abs(rel.z));
			List<Point> subList=relativePointsByDistance.computeIfAbsent(distances,(IntSet unused)->new ArrayList<>());
			subList.add(rel);
			relativePoints.add(rel);
		}
	}
	
	private static class Scanner	{
		public final Map<Point,RelativePointsInfo> relativePoints;
		public Scanner(List<Point> points)	{
			relativePoints=new HashMap<>();
			for (int i=0;i<points.size();++i)	{
				Point s=points.get(i);
				RelativePointsInfo relativeData=new RelativePointsInfo();
				for (int j=0;j<points.size();++j) if (i!=j)	{
					Point t=points.get(j);
					Point rel=new Point(t.x-s.x,t.y-s.y,t.z-s.z);
					relativeData.addPoint(rel);
				}
				relativePoints.put(s,relativeData);
			}
		}
	}
	
	private static enum Direction	{
		X,MINUS_X,Y,MINUS_Y,Z,MINUS_Z;
		private final static Map<Direction,Direction> MINUS_MAP=createMinusMap();
		private static Map<Direction,Direction> createMinusMap()	{
			Map<Direction,Direction> result=new EnumMap<>(Direction.class);
			result.put(X,MINUS_X);
			result.put(MINUS_X,X);
			result.put(Y,MINUS_Y);
			result.put(MINUS_Y,Y);
			result.put(Z,MINUS_Z);
			result.put(MINUS_Z,Z);
			return result;
		}
		public Direction minus()	{
			return MINUS_MAP.get(this);
		}
	}
	/*
	 * Transformation from different reference frames. The shift is applied before the "rotation".
	 */
	private static class Rotation	{
		public final Direction xAssignedTo;
		public final Direction yAssignedTo;
		public final Direction zAssignedTo;
		private Rotation(Direction xAssignedTo,Direction yAssignedTo,Direction zAssignedTo)	{
			this.xAssignedTo=xAssignedTo;
			this.yAssignedTo=yAssignedTo;
			this.zAssignedTo=zAssignedTo;
		}
		public final static List<Rotation> ALL_ROTATIONS=getAllRotations();
		public final static Rotation NO_ROTATION=new Rotation(Direction.X,Direction.Y,Direction.Z);
		private static List<Rotation> getAllRotations()	{
			List<Rotation> result=new ArrayList<>(24);
			EnumSet<Direction> dirs1=EnumSet.of(Direction.X,Direction.Y,Direction.Z);
			for (Direction dir1:dirs1)	{
				EnumSet<Direction> dirs2=EnumSet.copyOf(dirs1);
				dirs2.remove(dir1);
				Direction[] xDirs=new Direction[] {dir1,dir1.minus()};
				for (Direction dir2:dirs2)	{
					EnumSet<Direction> dirs3=EnumSet.copyOf(dirs2);
					dirs3.remove(dir2);
					Direction dir3=dirs3.iterator().next();
					Direction[] yDirs=new Direction[] {dir2,dir2.minus()};
					Direction[] zDirs=new Direction[] {dir3,dir3.minus()};
					for (Direction x:xDirs) for (Direction y:yDirs) for (Direction z:zDirs) result.add(new Rotation(x,y,z));
				}
			}
			return result;
		}
		private static int reassignPointDirection(Point p,Direction dir)	{
			switch (dir)	{
				case X:return p.x;
				case MINUS_X:return -p.x;
				case Y:return p.y;
				case MINUS_Y:return -p.y;
				case Z:return p.z;
				case MINUS_Z:return -p.z;
				default:throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			}
		}
		private Direction reassignDir(Direction dir)	{
			switch (dir)	{
				case X:return xAssignedTo;
				case MINUS_X:return xAssignedTo.minus();
				case Y:return yAssignedTo;
				case MINUS_Y:return yAssignedTo.minus();
				case Z:return zAssignedTo;
				case MINUS_Z:return zAssignedTo.minus();
				default:throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			}
		}
		private Point rotate(Point p)	{
			int x=reassignPointDirection(p,xAssignedTo);
			int y=reassignPointDirection(p,yAssignedTo);
			int z=reassignPointDirection(p,zAssignedTo);
			return new Point(x,y,z);
		}
	}
	private static class Transformation	{
		public final int shiftX;
		public final int shiftY;
		public final int shiftZ;
		public final Rotation rotation;
		private Transformation(int shiftX,int shiftY,int shiftZ,Rotation rotation)	{
			this.shiftX=shiftX;
			this.shiftY=shiftY;
			this.shiftZ=shiftZ;
			this.rotation=rotation;
		}
		private static Transformation getFromPointsAndRotation(Point p,Point q,Rotation dirs)	{
			Point rotQ=dirs.rotate(q);
			return new Transformation(p.x-rotQ.x,p.y-rotQ.y,p.z-rotQ.z,dirs);
		}
		public final static Transformation NO_TRANSFORM=new Transformation(0,0,0,Rotation.NO_ROTATION);
		private static int countAssignedPoints(Set<Point> pointsA,Set<Point> pointsB,Rotation assignments)	{
			int result=0;
			for (Point p:pointsB)	{
				Point q=assignments.rotate(p);
				if (pointsA.contains(q)) ++result;
			}
			return result;
		}
		private static List<Rotation> getValidAssignments(Point pA,Point pB) {
			List<Rotation> result=new ArrayList<>();
			for (Rotation r:Rotation.ALL_ROTATIONS) if (r.rotate(pB).equals(pA)) result.add(r);
			return result;
		}
		/*
		 * Try to find a transformation from originList's reference frame to relativeList's reference frame, such that:
		 * 1) At least 12 beacons match.
		 * 2) There aren't points in originList that should be detected by relativeList, but aren't.
		 * 3) There aren't points in relativeList that should be detected by originList, but aren't.
		 * Return null if no such transformation is possible.
		 */
		private static Rotation tryFindDirections(RelativePointsInfo relDataA,RelativePointsInfo relDataB)	{
			Set<IntSet> intersect=new HashSet<>(Sets.intersection(relDataA.relativePointsByDistance.keySet(),relDataB.relativePointsByDistance.keySet()));
			int validPoints=0;
			for (IntSet distances:intersect)	{
				int pointsA=relDataA.relativePointsByDistance.get(distances).size();
				int pointsB=relDataB.relativePointsByDistance.get(distances).size();
				if (pointsA!=pointsB) return null;
				validPoints+=pointsA;
			}
			if (validPoints<MIN_POINTS-1) return null;
			for (IntSet distances:intersect)	{
				List<Point> psA=relDataA.relativePointsByDistance.get(distances);
				List<Point> psB=relDataB.relativePointsByDistance.get(distances);
				for (Point pA:psA) for (Point pB:psB)	{
					List<Rotation> validAssignments=getValidAssignments(pA,pB);
					for (Rotation result:validAssignments) if (countAssignedPoints(relDataA.relativePoints,relDataB.relativePoints,result)>=MIN_POINTS-1) return result;
				}
			}
			return null;
		}
		public static Transformation tryFindAssignment(Scanner scannerA,Scanner scannerB)	{
			for (Map.Entry<Point,RelativePointsInfo> mainEntryA:scannerA.relativePoints.entrySet())	{
				Point pointA=mainEntryA.getKey();
				RelativePointsInfo relDataA=mainEntryA.getValue();
				for (Map.Entry<Point,RelativePointsInfo> mainEntryB:scannerB.relativePoints.entrySet())	{
					Point pointB=mainEntryB.getKey();
					RelativePointsInfo relDataB=mainEntryB.getValue();
					Rotation matchingDirections=tryFindDirections(relDataA,relDataB);
					if (matchingDirections!=null) return Transformation.getFromPointsAndRotation(pointA,pointB,matchingDirections);
				}
			}
			return null;
		}
		/*
		 * Let "other" be a transformation that moves reference frame 0 into reference frame "B", and "this" be a transformation that moves
		 * reference frame "B" into reference frame "A". The result is a new transformation that moves reference frame 0 into reference frame
		 * "A".
		 */
		public Transformation linkFrom(Transformation other)	{
			int newX=other.shiftX+reassignShift(other.rotation.xAssignedTo);
			int newY=other.shiftY+reassignShift(other.rotation.yAssignedTo);
			int newZ=other.shiftZ+reassignShift(other.rotation.zAssignedTo);
			Direction rotX=rotation.reassignDir(other.rotation.xAssignedTo);
			Direction rotY=rotation.reassignDir(other.rotation.yAssignedTo);
			Direction rotZ=rotation.reassignDir(other.rotation.zAssignedTo);
			return new Transformation(newX,newY,newZ,new Rotation(rotX,rotY,rotZ));
		}
		private int reassignShift(Direction dir)	{
			switch (dir)	{
				case X:return shiftX;
				case MINUS_X:return -shiftX;
				case Y:return shiftY;
				case MINUS_Y:return -shiftY;
				case Z:return shiftZ;
				case MINUS_Z:return -shiftZ;
				default:throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			}
		}
	}
	
	private static Transformation[] getTransformations(List<Scanner> scanners)	{
		Transformation[] result=new Transformation[scanners.size()];
		result[0]=Transformation.NO_TRANSFORM;
		findTransformationsRecursive(scanners,0,result);
		for (int i=1;i<result.length;++i) if (result[i]==null) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
		return result;
	}
	
	private static void findTransformationsRecursive(List<Scanner> scanners,int source,Transformation[] result)	{
		Transformation previous=result[source];
		for (int i=0;i<result.length;++i) if (result[i]==null)	{
			Transformation newTransform=Transformation.tryFindAssignment(scanners.get(source),scanners.get(i));
			if (newTransform!=null)	{
				result[i]=newTransform.linkFrom(previous);
				findTransformationsRecursive(scanners,i,result);
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		List<Scanner> scanners=new ArrayList<>();
		int position=0;
		while (position<lines.size())	{
			String line=lines.get(position);
			Matcher m=SCANNER_PATTERN.matcher(line);
			if (!m.matches()) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			List<Point> points=new ArrayList<>();
			for (++position;position<lines.size();++position)	{
				line=lines.get(position);
				if (line.isBlank()) break;
				String[] numbers=line.split(",");
				if (numbers.length!=3) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
				points.add(new Point(Integer.parseInt(numbers[0]),Integer.parseInt(numbers[1]),Integer.parseInt(numbers[2])));
			}
			++position;
			scanners.add(new Scanner(points));
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		Transformation[] transforms=getTransformations(scanners);
		int maxDist=0;
		for (int i=0;i<transforms.length;++i) for (int j=i+1;j<transforms.length;++j)	{
			Transformation t1=transforms[i];
			Transformation t2=transforms[j];
			int dist=Math.abs(t2.shiftX-t1.shiftX)+Math.abs(t2.shiftY-t1.shiftY)+Math.abs(t2.shiftZ-t1.shiftZ);
			maxDist=Math.max(dist,maxDist);
		}
		System.out.println(maxDist);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
