package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent21_2 {
	private final static String IN_FILE="Advent21.txt";
	private final static boolean[][] INITIAL_PATTERN=new boolean[][] {{false,true,false},{false,false,true},{true,true,true}};
	private final static int ITERATIONS=18;
	
	private final static Pattern PATTERN_2=Pattern.compile("^([\\.#]{2})/([\\.#]{2}) => ([\\.#]{3})/([\\.#]{3})/([\\.#]{3})$");
	private final static Pattern PATTERN_3=Pattern.compile("^([\\.#]{3})/([\\.#]{3})/([\\.#]{3}) => ([\\.#]{4})/([\\.#]{4})/([\\.#]{4})/([\\.#]{4})$");
	
	private static enum IterationScheme	{
		STANDARD	{
			@Override
			public boolean get(boolean[][] array,int size,int i,int j) {
				return array[i][j];
			}
		},
		FLIPPED	{
			@Override
			public boolean get(boolean[][] array,int size,int i,int j) {
				return array[i][size-1-j];
			}
		},
		ROTATED90	{
			@Override
			public boolean get(boolean[][] array,int size,int i,int j) {
				return array[size-1-j][i];
			}
		},
		ROTADED90_FLIPPED	{
			@Override
			public boolean get(boolean[][] array,int size,int i,int j) {
				return array[size-1-j][size-1-i];
			}
		},
		ROTATED180	{
			@Override
			public boolean get(boolean[][] array,int size,int i,int j) {
				return array[size-1-i][size-1-j];
			}
		},
		ROTATED180_FLIPPED	{
			@Override
			public boolean get(boolean[][] array,int size,int i,int j) {
				return array[size-1-i][j];
			}
		},
		ROTATED270	{
			@Override
			public boolean get(boolean[][] array,int size,int i,int j) {
				return array[j][size-1-i];
			}
		},
		ROTATED270_FLIPPED	{
			@Override
			public boolean get(boolean[][] array,int size,int i,int j) {
				return array[j][i];
			}
		};
		public abstract boolean get(boolean[][] array,int size,int i,int j);
		public BitSet getFromArray(boolean[][] array,int size)	{
			BitSet result=new BitSet(size*size);
			int index=0;
			for (int i=0;i<size;++i) for (int j=0;j<size;++j)	{
				result.set(index,get(array,size,i,j));
				++index;
			}
			return result;
		}
	}
	
	private static class PatternGrid	{
		private final static IterationScheme[] ITERATORS=IterationScheme.values();
		private final int size;
		private final BitSet contents;	// With size^2 pixels.
		private PatternGrid(int size,BitSet contents)	{
			this.size=size;
			this.contents=contents;
		}
		@Override
		public int hashCode()	{
			return size+contents.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			PatternGrid pg=(PatternGrid)other;
			return (size==pg.size)&&contents.equals(pg.contents);
		}
		public void writeIntoArray(boolean[][] bits,int size,int startI,int startJ)	{
			int endI=startI+size;
			int endJ=startJ+size;
			int index=0;
			for (int i=startI;i<endI;++i) for (int j=startJ;j<endJ;++j)	{
				bits[i][j]=contents.get(index);
				++index;
			}
		}
		public static PatternGrid getFromArray(boolean[][] bits)	{
			int size=bits.length;
			return new PatternGrid(size,IterationScheme.STANDARD.getFromArray(bits,size));
		}
		public static PatternGrid getFromArray(boolean[][] bits,int size,int startI,int startJ)	{
			int endI=startI+size;
			int endJ=startJ+size;
			int index=0;
			BitSet result=new BitSet(size*size);
			for (int i=startI;i<endI;++i) for (int j=startJ;j<endJ;++j)	{
				result.set(index,bits[i][j]);
				++index;
			}
			return new PatternGrid(size,result);
		}
		public static Set<PatternGrid> getAllPatterns(boolean[][] bits)	{
			int size=bits.length;
			return Arrays.stream(ITERATORS).map((IterationScheme scheme)->scheme.getFromArray(bits,size)).distinct().map((BitSet bitSet)->new PatternGrid(size,bitSet)).collect(Collectors.toUnmodifiableSet());
		}
	}
	
	private static boolean[][] extractPattern(String... parts)	{
		int size=parts.length;
		boolean[][] result=new boolean[size][size];
		for (int i=0;i<size;++i) for (int j=0;j<size;++j) result[i][j]=(parts[i].charAt(j)=='#');
		return result;
	}
	
	private static boolean[][] iterate(boolean[][] input,Map<PatternGrid,PatternGrid> replacements)	{
		int size=input.length;
		int portionSize=((size%2)==0)?2:3;
		int bigPortionSize=1+portionSize;
		int portions=size/portionSize;
		int bigSize=portions*bigPortionSize;
		boolean[][] result=new boolean[bigSize][bigSize];
		for (int i=0;i<portions;++i) for (int j=0;j<portions;++j)	{
			PatternGrid inPattern=PatternGrid.getFromArray(input,portionSize,portionSize*i,portionSize*j);
			PatternGrid outPattern=replacements.get(inPattern);
			outPattern.writeIntoArray(result,bigPortionSize,bigPortionSize*i,bigPortionSize*j);
		}
		return result;
	}
	
	private static int countBits(boolean[][] pattern)	{
		int result=0;
		for (int i=0;i<pattern.length;++i) for (int j=0;j<pattern[i].length;++j) if (pattern[i][j]) ++result;
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		Map<PatternGrid,PatternGrid> replacements=new HashMap<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher2=PATTERN_2.matcher(line);
			if (matcher2.matches())	{
				// The arrays can be preallocated and reused, but there aren't performance issues, so this is good enough.
				boolean[][] pattern2=extractPattern(matcher2.group(1),matcher2.group(2));
				boolean[][] pattern3=extractPattern(matcher2.group(3),matcher2.group(4),matcher2.group(5));
				Set<PatternGrid> inPatterns=PatternGrid.getAllPatterns(pattern2);
				PatternGrid outPattern=PatternGrid.getFromArray(pattern3);
				for (PatternGrid in:inPatterns) replacements.put(in,outPattern);
				continue;
			}
			Matcher matcher3=PATTERN_3.matcher(line);
			if (matcher3.matches())	{
				boolean[][] pattern3=extractPattern(matcher3.group(1),matcher3.group(2),matcher3.group(3));
				boolean[][] pattern4=extractPattern(matcher3.group(4),matcher3.group(5),matcher3.group(6),matcher3.group(7));
				Set<PatternGrid> inPatterns=PatternGrid.getAllPatterns(pattern3);
				PatternGrid outPattern=PatternGrid.getFromArray(pattern4);
				for (PatternGrid in:inPatterns) replacements.put(in,outPattern);
				continue;
			}
		}
		boolean[][] pattern=INITIAL_PATTERN;
		for (int i=0;i<ITERATIONS;++i) pattern=iterate(pattern,replacements);
		System.out.println(countBits(pattern));
	}
}
