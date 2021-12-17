package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent17_1 {
	private final static String IN_FILE="Advent17.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^target area: x=(\\d+)..(\\d+), y=-(\\d+)..-(\\d+)$");
	
	// For this case we don't actually need to simulate horizontal movement.
	private static class Target	{
		public final int y1;
		public final int y2;
		public Target(int y1,int y2)	{
			this.y1=y1;
			this.y2=y2;
		}
		public OptionalLong shot(int v0)	{
			int py=0;
			int maxY=0;
			for (;;)	{
				py+=v0;
				maxY=Math.max(maxY,py);
				if (py<y1) return OptionalLong.empty();
				else if ((py>=y1)&&(py<=y2)) return OptionalLong.of(maxY); 
				--v0;
			}
		}
		public long getMaxHeight()	{
			int maxVy=Math.abs(y1);
			long result=0;
			for (int vy=1;vy<=maxVy;++vy)	{
				OptionalLong thisShot=shot(vy);
				if (thisShot.isPresent()) result=Math.max(result,thisShot.getAsLong());
			}
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		String line=lines.get(0);
		Matcher m=LINE_PATTERN.matcher(line);
		if (!m.matches()) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
		int y1=-Integer.parseInt(m.group(3));
		int y2=-Integer.parseInt(m.group(4));
		Target t=new Target(y1,y2);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(t.getMaxHeight());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
