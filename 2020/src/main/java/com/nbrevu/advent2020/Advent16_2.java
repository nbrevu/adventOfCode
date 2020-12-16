package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.common.io.Resources;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Advent16_2 {
	private final static String IN_FILE="Advent16.txt";
	private final static String PREFIX="departure ";
	
	private final static Pattern INITIAL_PATTERN=Pattern.compile("(^.+): (\\d+)\\-(\\d+) or (\\d+)\\-(\\d+)$");
	private final static Pattern COMMA=Pattern.compile(",");
	
	private static Map<String,RangeSet<Integer>> parseRanges(List<String> ticketDefinitions)	{
		Map<String,RangeSet<Integer>> result=new HashMap<>();
		for (String s:ticketDefinitions)	{
			Matcher matcher=INITIAL_PATTERN.matcher(s);
			if (!matcher.matches()) throw new IllegalArgumentException("El formato está mal. Claramente sólo se podrá arreglar con format c:");
			Range<Integer> range1=Range.closed(Integer.parseInt(matcher.group(2)),Integer.parseInt(matcher.group(3)));
			Range<Integer> range2=Range.closed(Integer.parseInt(matcher.group(4)),Integer.parseInt(matcher.group(5)));
			result.put(matcher.group(1),TreeRangeSet.create(Arrays.asList(range1,range2)));
		}
		return result;
	}
	
	private static RangeSet<Integer> getFullRangeSet(Map<String,RangeSet<Integer>> baseRanges)	{
		return TreeRangeSet.create(Iterables.concat(Collections2.transform(baseRanges.values(),RangeSet::asRanges)));
	}
	
	private static List<Integer> parseTicket(String ticketNumbers)	{
		return COMMA.splitAsStream(ticketNumbers).map(Integer::parseInt).collect(Collectors.toUnmodifiableList());
	}
	
	private static List<List<Integer>> parseValues(List<String> ticketValues)	{
		return ticketValues.stream().map(Advent16_2::parseTicket).collect(Collectors.toUnmodifiableList());
	}
	
	private static List<List<Integer>> retainOnlyValid(List<List<Integer>> ticketValues,RangeSet<Integer> fullRangeSet)	{
		List<List<Integer>> result=new ArrayList<>();
		for (List<Integer> subList:ticketValues)	{
			boolean valid=true;
			for (Integer i:subList) if (!fullRangeSet.contains(i))	{
				valid=false;
				break;
			}
			if (valid) result.add(subList);
		}
		return result;
	}
	
	private static Map<String,IntSet> getInitialMap(Map<String,RangeSet<Integer>> namedRanges,int ticketSize)	{
		IntSet initialMap=HashIntSets.newMutableSet();
		for (int i=0;i<ticketSize;++i) initialMap.add(i);
		Map<String,IntSet> result=new HashMap<>();
		for (String s:namedRanges.keySet()) result.put(s,HashIntSets.newMutableSet(initialMap));
		return result;
	}
	
	private static void tryAndDiscard(Map<String,RangeSet<Integer>> namedRanges,Map<String,IntSet> possibleIndices,List<Integer> ticketValues)	{
		for (Map.Entry<String,IntSet> entry:possibleIndices.entrySet())	{
			IntSet testingMap=entry.getValue();
			RangeSet<Integer> range=namedRanges.get(entry.getKey());
			for (IntCursor cursor=testingMap.cursor();cursor.moveNext();) if (!range.contains(ticketValues.get(cursor.elem()))) cursor.remove();
		}
	}
	
	private static class SudokuSolver	{
		/*
		 * Each ROW corresponds to a name.
		 * If (i,j) is true, then names[i] could correspond to index j.
		 * This proto-sudoku is solved when there is exactly one value per row and per column.
		 */
		private final int size;
		private final boolean[][] state;
		private final List<String> names;
		private int lastCount;
		public SudokuSolver(Map<String,IntSet> acceptableValues)	{
			size=acceptableValues.size();
			names=new ArrayList<>(acceptableValues.size());
			state=new boolean[size][size];
			for (Map.Entry<String,IntSet> entry:acceptableValues.entrySet())	{
				int i=names.size();
				names.add(entry.getKey());
				for (IntCursor cursor=entry.getValue().cursor();cursor.moveNext();) state[i][cursor.elem()]=true;
			}
			lastCount=size*size;
		}
		public boolean isFinished()	{
			boolean isValid=true;
			int totalCount=0;
			for (int i=0;i<size;++i)	{
				int rowCount=0;
				for (int j=0;j<size;++j) if (state[i][j])	{
					++rowCount;
					++totalCount;
				}
				if (rowCount==0) throw new IllegalStateException("No valid solution found.");
				else if (rowCount!=1) isValid=false;
			}
			if (isValid) return true;
			if (totalCount==lastCount) throw new IllegalStateException("Can't solve. Too much.");
			lastCount=totalCount;
			return false;
		}
		public void pass()	{
			for (int i=0;i<size;++i)	{
				// -1: no valid value fouund yet. -2: more than one valid value found.
				int singleValue=-1;
				for (int j=0;j<size;++j) if (state[i][j])	{
					if (singleValue==-1) singleValue=j;
					else singleValue=-2;
				}
				if (singleValue>=0) for (int k=0;k<size;++k) if (k!=i) state[k][singleValue]=false;
			}
			for (int i=0;i<size;++i)	{
				// -1: no valid value fouund yet. -2: more than one valid value found.
				int singleValue=-1;
				for (int j=0;j<size;++j) if (state[j][i])	{
					if (singleValue==-1) singleValue=j;
					else singleValue=-2;
				}
				if (singleValue>=0) for (int k=0;k<size;++k) if (k!=i) state[singleValue][k]=false;
			}
		}
		public ObjIntMap<String> solve()	{
			do pass(); while (!isFinished());
			ObjIntMap<String> result=HashObjIntMaps.newMutableMap();
			for (int i=0;i<size;++i) for (int j=0;j<size;++j) if (state[i][j])	{
				result.put(names.get(i),j);
				break;	// Next i.
			}
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> content=Resources.readLines(file,Charsets.UTF_8);
		int separator=content.indexOf("");
		if (separator<0) throw new IllegalArgumentException("Wrong.");
		Map<String,RangeSet<Integer>> namedRanges=parseRanges(content.subList(0,separator));
		RangeSet<Integer> fullRange=getFullRangeSet(namedRanges);
		List<Integer> myTicket=parseTicket(content.get(separator+2));
		List<List<Integer>> ticketValues=parseValues(content.subList(separator+5,content.size()));
		ticketValues=retainOnlyValid(ticketValues,fullRange);
		int ticketSize=namedRanges.size();
		Map<String,IntSet> acceptableValues=getInitialMap(namedRanges,ticketSize);
		for (List<Integer> subList:ticketValues) tryAndDiscard(namedRanges,acceptableValues,subList);
		ObjIntMap<String> finalAssignment=new SudokuSolver(acceptableValues).solve();
		long result=1l;
		for (ObjIntCursor<String> cursor=finalAssignment.cursor();cursor.moveNext();) if (cursor.key().startsWith(PREFIX)) result*=myTicket.get(cursor.value()).intValue();
		System.out.println(result);
	}
}
