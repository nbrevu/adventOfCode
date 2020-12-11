package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent3_2 {
	private final static String IN_FILE="Advent3.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)$");
	
	private static class Claim	{
		public final int id;
		public final int offsetX;
		public final int offsetY;
		public final int width;
		public final int height;
		public Claim(int id,int offsetX,int offsetY,int width,int height)	{
			this.id=id;
			this.offsetX=offsetX;
			this.offsetY=offsetY;
			this.width=width;
			this.height=height;
		}
		public boolean overlaps(Claim other)	{
			int minX=Math.max(offsetX,other.offsetX);
			int maxX=Math.min(offsetX+width,other.offsetX+other.width);
			if (maxX<=minX) return false;
			int minY=Math.max(offsetY,other.offsetY);
			int maxY=Math.min(offsetY+height,other.offsetY+other.height);
			return minY<maxY;
		}
	}
	
	private static boolean[] findOverlaps(List<Claim> claims)	{
		boolean[] result=new boolean[claims.size()];
		for (int i=0;i<claims.size();++i) for (int j=i+1;j<claims.size();++j) if (claims.get(i).overlaps(claims.get(j)))	{
			result[i]=true;
			result[j]=true;
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Claim> claims=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches()) claims.add(new Claim(Integer.parseInt(matcher.group(1)),Integer.parseInt(matcher.group(2)),Integer.parseInt(matcher.group(3)),Integer.parseInt(matcher.group(4)),Integer.parseInt(matcher.group(5))));
		}
		boolean[] overlaps=findOverlaps(claims);
		for (int i=0;i<overlaps.length;++i) if (!overlaps[i]) System.out.println(claims.get(i).id);
	}
}
