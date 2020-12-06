package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_192 {
	private final static String IN_FILE="2015/Advent191.txt";
	private final static String INITIAL_STRING="e";
	
	private final static Pattern REPLACEMENT_PATTERN=Pattern.compile("^(.+) => (.+)$");

	private static class Replacement	{
		public final String from;
		public final String to;
		public Replacement(String from,String to)	{
			this.from=from;
			this.to=to;
		}
		public String reverseReplace(String origin,int position)	{
			StringBuilder result=new StringBuilder();
			int suffixPos=position+to.length();
			result.append(origin.substring(0,position)).append(from).append(origin.substring(suffixPos));
			return result.toString();
		}
	}
	
	private final static Comparator<String> LENGTH_COMPARATOR=new Comparator<>()	{
		@Override
		public int compare(String o1,String o2) {
			int lenDif=o1.length()-o2.length();
			if (lenDif!=0) return lenDif;
			else return o1.compareTo(o2);
		}
	};
	
	private static int pruningBreadthFirstSearch(String initial,String goal,Collection<Replacement> replacements,int toPrune)	{
		NavigableSet<String> nextGen=new TreeSet<>(LENGTH_COMPARATOR);
		for (Replacement r:replacements) for (int i=initial.indexOf(r.to);i>=0;i=initial.indexOf(r.to,i+1))	{
			nextGen.add(r.reverseReplace(initial,i));
			if (nextGen.size()>toPrune) nextGen.pollLast();
		}
		if (nextGen.contains(goal)) return 1;
		for (String str:nextGen)	{
			int result=pruningBreadthFirstSearch(str,goal,replacements,toPrune);
			if (result>=0) return 1+result;
		}
		return -1;
	}

	public static void main(String[] args) throws IOException	{
		List<Replacement> replacements=new ArrayList<>();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		for (String line:lines)	{
			if (line.isBlank()) break;
			Matcher matcher=REPLACEMENT_PATTERN.matcher(line);
			if (matcher.matches()) replacements.add(new Replacement(matcher.group(1),matcher.group(2)));
		}
		String goal=lines.get(lines.size()-1);
		System.out.println(pruningBreadthFirstSearch(goal,INITIAL_STRING,replacements,1));
	}
}
