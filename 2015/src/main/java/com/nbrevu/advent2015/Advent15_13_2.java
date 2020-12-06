package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.google.common.io.Resources;

public class Advent15_13_2 {
	private final static String IN_FILE="Advent13.txt";
	private final static Pattern LINE_PATTERN=Pattern.compile("^(.+) would (.+) (\\d+) happiness units by sitting next to (.+).$");
	
	private final static Map<String,Boolean> GAIN_LOSE_MAP=ImmutableMap.of("gain",Boolean.TRUE,"lose",Boolean.FALSE);
	private final static String THE_9TH_GUEST="Ñbrevu";
	
	private static <T extends Comparable<T>> boolean nextPermutation(T[] arr)	{
		// Thanks, indy256 from http://codeforces.com/blog/entry/3980.
		for (int i=arr.length-2;i>=0;--i) if (arr[i].compareTo(arr[i+1])<0) for (int j=arr.length-1;;--j) if (arr[j].compareTo(arr[i])>0)	{
			T sw=arr[i];
			arr[i]=arr[j];
			arr[j]=sw;
			for (++i,j=arr.length-1;i<j;++i,--j)	{
				sw=arr[i];
				arr[i]=arr[j];
				arr[j]=sw;
			}
			return true;
		}
		return false;
	}
	
	private static int calculateHappiness(String[] sorting,Table<String,String,Integer> values)	{
		int result=0;
		int n=sorting.length;
		for (int i=0;i<n;++i)	{
			int prev=(i+n-1)%n;
			int next=(i+1)%n;
			result+=values.get(sorting[i],sorting[prev])+values.get(sorting[i],sorting[next]);
		}
		return result;
	}
	
	private static int getMaxHappiness(Table<String,String,Integer> values)	{
		String[] sorting=values.rowKeySet().stream().sorted().toArray(String[]::new);
		int result=Integer.MIN_VALUE;
		do result=Math.max(result,calculateHappiness(sorting,values)); while (nextPermutation(sorting));
		return result;
	}
	
	private static void addGuest(String guest,Table<String,String,Integer> values)	{
		Set<String> keys=new HashSet<>(values.rowKeySet());
		for (String str:keys)	{
			values.put(guest,str,0);
			values.put(str,guest,0);
		}
	}
	
	public static void main(String[] args) throws IOException	{
		Table<String,String,Integer> table=HashBasedTable.create();
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				String source=matcher.group(1);
				boolean sign=GAIN_LOSE_MAP.get(matcher.group(2));
				int value=Integer.parseInt(matcher.group(3));
				if (!sign) value=-value;
				String target=matcher.group(4);
				table.put(source,target,value);
			}
		}
		addGuest(THE_9TH_GUEST,table);
		System.out.println(getMaxHappiness(table));
	}
}
