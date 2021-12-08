package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import com.koloboke.collect.set.CharSet;
import com.koloboke.collect.set.hash.HashCharSets;

public class Advent8_2 {
	private final static String IN_FILE="Advent8.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^(\\w+) (\\w+) (\\w+) (\\w+) (\\w+) (\\w+) (\\w+) (\\w+) (\\w+) (\\w+) \\| (\\w+) (\\w+) (\\w+) (\\w+)$");
	
	private static enum BcdSegment	{
		TOP,UP_LEFT,UP_RIGHT,CENTRE,DOWN_LEFT,DOWN_RIGHT,BOTTOM;
	}
	
	private static enum DisplayedNumber	{
		N0(0,BcdSegment.TOP,BcdSegment.UP_LEFT,BcdSegment.UP_RIGHT,BcdSegment.DOWN_LEFT,BcdSegment.DOWN_RIGHT,BcdSegment.BOTTOM),
		N1(1,BcdSegment.UP_RIGHT,BcdSegment.DOWN_RIGHT),
		N2(2,BcdSegment.TOP,BcdSegment.UP_RIGHT,BcdSegment.CENTRE,BcdSegment.DOWN_LEFT,BcdSegment.BOTTOM),
		N3(3,BcdSegment.TOP,BcdSegment.UP_RIGHT,BcdSegment.CENTRE,BcdSegment.DOWN_RIGHT,BcdSegment.BOTTOM),
		N4(4,BcdSegment.UP_LEFT,BcdSegment.UP_RIGHT,BcdSegment.CENTRE,BcdSegment.DOWN_RIGHT),
		N5(5,BcdSegment.TOP,BcdSegment.UP_LEFT,BcdSegment.CENTRE,BcdSegment.DOWN_RIGHT,BcdSegment.BOTTOM),
		N6(6,BcdSegment.TOP,BcdSegment.UP_LEFT,BcdSegment.CENTRE,BcdSegment.DOWN_LEFT,BcdSegment.DOWN_RIGHT,BcdSegment.BOTTOM),
		N7(7,BcdSegment.TOP,BcdSegment.UP_RIGHT,BcdSegment.DOWN_RIGHT),
		N8(8,BcdSegment.TOP,BcdSegment.UP_LEFT,BcdSegment.UP_RIGHT,BcdSegment.CENTRE,BcdSegment.DOWN_LEFT,BcdSegment.DOWN_RIGHT,BcdSegment.BOTTOM),
		N9(9,BcdSegment.TOP,BcdSegment.UP_LEFT,BcdSegment.UP_RIGHT,BcdSegment.CENTRE,BcdSegment.DOWN_RIGHT,BcdSegment.BOTTOM);
		
		private final int value;
		private final Set<BcdSegment> segments;
		
		private final static ObjIntMap<Set<BcdSegment>> VALUE_MAP=createValueMap();
		
		private static ObjIntMap<Set<BcdSegment>> createValueMap()	{
			ObjIntMap<Set<BcdSegment>> result=HashObjIntMaps.newMutableMap();
			for (DisplayedNumber n:values()) result.put(n.segments,n.value);
			return result;
		}
		
		private DisplayedNumber(int value,BcdSegment s1,BcdSegment... segments)	{
			this.value=value;
			this.segments=EnumSet.of(s1,segments);
		}
		
		public static int getDigit(Set<BcdSegment> segments)	{
			return VALUE_MAP.getOrDefault(segments,-1);
		}
	}
	
	private static class Assignment	{
		private final CharObjMap<BcdSegment> segmentMap;
		private Assignment(CharObjMap<BcdSegment> segmentMap)	{
			this.segmentMap=segmentMap;
		}
		public static Assignment determine(List<CharSet> segmentStrings)	{
			Multimap<Integer,CharSet> separated=Multimaps.index(segmentStrings,CharSet::size);
			Collection<CharSet> oneColl=separated.get(2);
			if (oneColl.size()!=1) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			CharSet urDr=oneColl.iterator().next();
			Collection<CharSet> sixColl=separated.get(6);
			if (sixColl.size()!=3) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			char upRight=0;
			char downRight=0;
			for (CharSet s:sixColl) if (!s.containsAll(urDr))	{
				for (char c:urDr) if (s.contains(c)) downRight=c;
				else upRight=c;
				break;
			}
			if ((upRight==0)||(downRight==0)) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			Collection<CharSet> sevenColl=separated.get(3);
			if (sevenColl.size()!=1) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			CharSet tUrDr=sevenColl.iterator().next();
			char top=0;
			for (char c:tUrDr) if (!urDr.contains(c))	{
				top=c;
				break;
			}
			if (top==0) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			Collection<CharSet> threeColl=separated.get(5);
			if (threeColl.size()!=3) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			CharSet cB=null;
			for (CharSet c:threeColl) if (c.containsAll(tUrDr))	{
				cB=HashCharSets.newMutableSet();
				cB.addAll(c);
				cB.removeAll(tUrDr);
			}
			if ((cB==null)||(cB.size()!=2)) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			Collection<CharSet> fourColl=separated.get(4);
			if (fourColl.size()!=1) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			CharSet four=fourColl.iterator().next();
			char centre=0;
			char bottom=0;
			for (char c:cB) if (four.contains(c)) centre=c;
			else bottom=c;
			if ((centre==0)||(bottom==0)) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			char upLeft=0;
			for (char c:four) if ((c!=centre)&&(c!=upRight)&&(c!=downRight))	{
				upLeft=c;
				break;
			}
			if (upLeft==0) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			Collection<CharSet> eightColl=separated.get(7);
			if (eightColl.size()!=1) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			CharSet eight=eightColl.iterator().next();
			char downLeft=0;
			for (char c:eight) if ((c!=top)&&(c!=upLeft)&&(c!=upRight)&&(c!=centre)&&(c!=downRight)&&(c!=bottom))	{
				downLeft=c;
				break;
			}
			if (downLeft==0) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			CharObjMap<BcdSegment> result=HashCharObjMaps.newMutableMap();
			result.put(top,BcdSegment.TOP);
			result.put(upLeft,BcdSegment.UP_LEFT);
			result.put(upRight,BcdSegment.UP_RIGHT);
			result.put(centre,BcdSegment.CENTRE);
			result.put(downLeft,BcdSegment.DOWN_LEFT);
			result.put(downRight,BcdSegment.DOWN_RIGHT);
			result.put(bottom,BcdSegment.BOTTOM);
			if (result.size()!=7) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			return new Assignment(result);
		}
		public int translate(CharSet digit)	{
			Set<BcdSegment> segments=EnumSet.noneOf(BcdSegment.class);
			for (char c:digit)	{
				segments.add(segmentMap.get(c));
			}
			return DisplayedNumber.getDigit(segments);
		}
	}
	
	private static CharSet getChars(String s)	{
		CharSet result=HashCharSets.newMutableSet();
		for (char c:s.toCharArray()) result.add(c);
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		int result=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (!matcher.matches()) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			List<CharSet> digitsDefinition=new ArrayList<>();
			for (int i=1;i<=10;++i) digitsDefinition.add(getChars(matcher.group(i)));
			Assignment assignment=Assignment.determine(digitsDefinition);
			int value=0;
			for (int i=11;i<=14;++i)	{
				value*=10;
				CharSet myDigits=getChars(matcher.group(i));
				int digit=assignment.translate(myDigits);
				if (digit==-1) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
				value+=digit;
			}
			result+=value;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
