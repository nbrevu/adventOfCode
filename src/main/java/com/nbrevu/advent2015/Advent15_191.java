package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_191 {
	private final static String IN_FILE="2015/Advent191.txt";
	
	private final static Pattern REPLACEMENT_PATTERN=Pattern.compile("^(.+) => (.+)$");
	
	private static class Replacement	{
		public final String from;
		public final String to;
		public Replacement(String from,String to)	{
			this.from=from;
			this.to=to;
		}
		public String replace(String origin,int position)	{
			StringBuilder result=new StringBuilder();
			int suffixPos=position+from.length();
			result.append(origin.substring(0,position)).append(to).append(origin.substring(suffixPos));
			return result.toString();
		}
	}
	
	private static Set<String> replaceAll(String base,Collection<Replacement> replacements)	{
		Set<String> result=new HashSet<>();
		for (Replacement r:replacements) for (int i=base.indexOf(r.from);i>=0;i=base.indexOf(r.from,i+1)) result.add(r.replace(base,i));
		return result;
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
		String toReplace=lines.get(lines.size()-1);
		System.out.println(replaceAll(toReplace,replacements).size());
	}
}
