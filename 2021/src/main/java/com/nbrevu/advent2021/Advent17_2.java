package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent17_2 {
	private final static String IN_FILE="Advent17.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^target area: x=(\\d+)..(\\d+), y=-(\\d+)..-(\\d+)$");
	
	private static class Target	{
		public final int x1;
		public final int x2;
		public final int y1;
		public final int y2;
		public Target(int x1,int x2,int y1,int y2)	{
			this.x1=x1;
			this.x2=x2;
			this.y1=y1;
			this.y2=y2;
		}
		public boolean shot(int vx,int vy)	{
			int px=0;
			int py=0;
			int maxY=0;
			for (;;)	{
				px+=vx;
				py+=vy;
				maxY=Math.max(maxY,py);
				if ((px>x2)||(py<y1)) return false;
				else if ((px>=x1)&&(px<=x2)&&(py>=y1)&&(py<=y2)) return true; 
				if (vx>0) --vx;
				--vy;
			}
		}
		public int countValidShots()	{
			int maxVy=Math.abs(y1);
			int result=0;
			for (int vx=5;vx<=x2;++vx) for (int vy=-maxVy;vy<=maxVy;++vy) if (shot(vx,vy)) ++result;
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
		int x1=Integer.parseInt(m.group(1));
		int x2=Integer.parseInt(m.group(2));
		int y1=-Integer.parseInt(m.group(3));
		int y2=-Integer.parseInt(m.group(4));
		Target t=new Target(x1,x2,y1,y2);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(t.countValidShots());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
