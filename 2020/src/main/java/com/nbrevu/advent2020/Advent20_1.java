package com.nbrevu.advent2020;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.google.common.math.IntMath;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Advent20_1 {
	private final static String IN_FILE="Advent20.txt";
	
	private final static Pattern PATTERN_TILE=Pattern.compile("^Tile (\\d+):$");
	
	private static class Tile	{
		private final static BorderType[] BORDER_TYPES=BorderType.values();
		public final int id;
		private final Map<BorderType,Border> borders;
		public Tile(int id,boolean[][] contents)	{
			this.id=id;
			borders=new EnumMap<>(BorderType.class);
			for (BorderType type:BORDER_TYPES) borders.put(type,type.getBorder(contents));
		}
		public Border getBorder(BorderType type)	{
			return borders.get(type);
		}
		@Override
		public int hashCode()	{
			return id;
		}
		@Override
		public boolean equals(Object other)	{
			Tile t=(Tile)other;
			return id==t.id;
		}
	}
	
	/*
	 * All horizontal borders are read from left to right, and all vertical ones are read from top to bottom.
	 * This means that every border must be treated in both directions, i.e. each tile generates 8 borders.
	 */
	private static enum BorderType	{
		// Used internally inside the Tile class.
		RIGHT	{
			@Override
			public Border getBorder(boolean[][] tile) {
				int N=tile.length;
				boolean[] result=new boolean[N];
				for (int i=0;i<result.length;++i) result[i]=tile[i][tile[i].length-1];
				return new Border(result);
			}
		},	RIGHT_REVERSED	{
			@Override
			public Border getBorder(boolean[][] tile) {
				int N=tile.length;
				boolean[] result=new boolean[N];
				for (int i=0;i<result.length;++i) result[N-1-i]=tile[i][tile[i].length-1];
				return new Border(result);
			}
		},	TOP	{
			@Override
			public Border getBorder(boolean[][] tile) {
				return new Border(tile[0]);
			}
		},	TOP_REVERSED	{
			@Override
			public Border getBorder(boolean[][] tile) {
				return new Border(reverse(tile[0]));
			}
		},	LEFT	{
			@Override
			public Border getBorder(boolean[][] tile) {
				int N=tile.length;
				boolean[] result=new boolean[N];
				for (int i=0;i<result.length;++i) result[i]=tile[i][0];
				return new Border(result);
			}
		},	LEFT_REVERSED	{
			@Override
			public Border getBorder(boolean[][] tile) {
				int N=tile.length;
				boolean[] result=new boolean[N];
				for (int i=0;i<result.length;++i) result[N-i-1]=tile[i][0];
				return new Border(result);
			}
		},	BOTTOM	{
			@Override
			public Border getBorder(boolean[][] tile) {
				return new Border(tile[tile.length-1]);
			}
		},	BOTTOM_REVERSED	{
			@Override
			public Border getBorder(boolean[][] tile) {
				return new Border(reverse(tile[tile.length-1]));
			}
		};
		public abstract Border getBorder(boolean[][] tile);
		private static boolean[] reverse(boolean[] in)	{
			boolean[] result=new boolean[in.length];
			int N=in.length-1;
			for (int i=0;i<in.length;++i) result[i]=in[N-i];
			return result;
		}
	}
	
	private static class Border	{
		public final boolean[] components;
		public Border(boolean[] components)	{
			this.components=components;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(components);
		}
		@Override
		public boolean equals(Object other)	{
			Border b=(Border)other;
			return Arrays.equals(components,b.components);
		}
	}
	
	/*
	 * So, here is the wonderful thing about this. I hadn't read about flipping (I should have assumed that it was possible, though,
	 * considering that it was used in a different problem from a previous year). So, I need to add flipping. What do I need to do in order
	 * to add flipping?
	 * Step 1: add four new items to this enum.
	 * Step 2: there is no step 2, that's good enough.
	 * 
	 * SOFTWARE ENGINEERING, BABY!
	 */
	private static enum Rotation	{
		NOT_ROTATED(BorderType.RIGHT,BorderType.TOP,BorderType.LEFT,BorderType.BOTTOM),
		ROTATED_90(BorderType.BOTTOM_REVERSED,BorderType.RIGHT,BorderType.TOP_REVERSED,BorderType.LEFT),
		ROTATED_180(BorderType.LEFT_REVERSED,BorderType.BOTTOM_REVERSED,BorderType.RIGHT_REVERSED,BorderType.TOP_REVERSED),
		ROTATED_270(BorderType.TOP,BorderType.LEFT_REVERSED,BorderType.BOTTOM,BorderType.RIGHT_REVERSED),
		/*
		 * The flipped counterparts exchange the left and right borders, and use reversed versions of the top and bottom borders.
		 * It can be easily proven that flipping horizontally+rotations accounts also for vertical flipping.
		 */
		FLIPPED(BorderType.LEFT,BorderType.TOP_REVERSED,BorderType.RIGHT,BorderType.BOTTOM_REVERSED),
		FLIPPED_90(BorderType.TOP_REVERSED,BorderType.RIGHT_REVERSED,BorderType.BOTTOM_REVERSED,BorderType.LEFT_REVERSED),
		FLIPPED_180(BorderType.RIGHT_REVERSED,BorderType.BOTTOM,BorderType.LEFT_REVERSED,BorderType.TOP),
		FLIPPED_270(BorderType.BOTTOM,BorderType.LEFT,BorderType.TOP,BorderType.RIGHT);
		private final BorderType rightBorder;
		private final BorderType topBorder;
		private final BorderType leftBorder;
		private final BorderType bottomBorder;
		private Rotation(BorderType rightBorder,BorderType topBorder,BorderType leftBorder,BorderType bottomBorder)	{
			this.rightBorder=rightBorder;
			this.topBorder=topBorder;
			this.leftBorder=leftBorder;
			this.bottomBorder=bottomBorder;
		}
		public Border getRightBorder(Tile tile)	{
			return tile.getBorder(rightBorder);
		}
		public Border getTopBorder(Tile tile)	{
			return tile.getBorder(topBorder);
		}
		public Border getLeftBorder(Tile tile)	{
			return tile.getBorder(leftBorder);
		}
		public Border getBottomBorder(Tile tile)	{
			return tile.getBorder(bottomBorder);
		}
	}
	
	private static boolean[] parseRow(String line)	{
		boolean[] result=new boolean[line.length()];
		for (int i=0;i<line.length();++i) result[i]=(line.charAt(i)=='#');
		return result;
	}
	
	private static List<Tile> parseTiles(List<String> fileContents)	{
		List<Tile> result=new ArrayList<>();
		int index=0;
		for (;index<fileContents.size();)	{
			String line=fileContents.get(index);
			if (line.isBlank()) break;
			Matcher matcher=PATTERN_TILE.matcher(line);
			if (!matcher.matches()) throw new IllegalArgumentException("Unexpected file format.");
			int id=Integer.parseInt(matcher.group(1));
			List<boolean[]> contents=new ArrayList<>();
			for (;;)	{
				++index;
				line=fileContents.get(index);
				if (line.isBlank()) break;
				else contents.add(parseRow(line));
			}
			result.add(new Tile(id,contents.stream().toArray(boolean[][]::new)));
			++index;
		}
		return result;
	}
	
	private static class RotatedTile	{
		public final Tile tile;
		public final Rotation rotation;
		public RotatedTile(Tile tile,Rotation rotation)	{
			this.tile=tile;
			this.rotation=rotation;
		}
		public Border getRightBorder()	{
			return rotation.getRightBorder(tile);
		}
		public Border getBottomBorder()	{
			return rotation.getBottomBorder(tile);
		}
		@Override
		public int hashCode()	{
			return tile.hashCode()+rotation.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			RotatedTile rt=(RotatedTile)other;
			return tile.equals(rt.tile)&&(rotation==rt.rotation);
		}
	}
	
	private static SetMultimap<Border,RotatedTile> getAllBorders(List<Tile> tiles,BiFunction<Rotation,Tile,Border> borderExtractor)	{
		Rotation[] rotations=Rotation.values();
		SetMultimap<Border,RotatedTile> result=HashMultimap.create();
		for (Tile tile:tiles) for (Rotation rot:rotations)	{
			Border border=borderExtractor.apply(rot,tile);
			RotatedTile rotatedTile=new RotatedTile(tile,rot);
			result.put(border,rotatedTile);
		}
		return result;
	}
	
	private static class RotatedImageFinder	{
		private final static Rotation[] ROTATIONS=Rotation.values();
		private final int N;
		private final Set<RotatedTile> allTiles;
		private final SetMultimap<Border,RotatedTile> leftBorders;
		private final SetMultimap<Border,RotatedTile> topBorders;
		public RotatedImageFinder(List<Tile> tiles)	{
			N=IntMath.sqrt(tiles.size(),RoundingMode.UNNECESSARY);
			allTiles=new HashSet<>();
			for (Tile t:tiles) for (Rotation r:ROTATIONS) allTiles.add(new RotatedTile(t,r));
			leftBorders=getAllBorders(tiles,Rotation::getLeftBorder);
			topBorders=getAllBorders(tiles,Rotation::getTopBorder);
		}
		public RotatedTile[][] getAssembledImage()	{
			IntSet usedTiles=HashIntSets.newMutableSet();
			RotatedTile[][] result=new RotatedTile[N][N];
			if (getAssembledImageRecursive(result,usedTiles,0,0)) return result;
			else throw new IllegalStateException("Can't assemble.");
		}
		private boolean getAssembledImageRecursive(RotatedTile[][] result,IntSet usedTiles,int i,int j)	{
			Set<RotatedTile> available=allTiles;
			if (i>0)	{
				Border previousBottom=result[i-1][j].getBottomBorder();
				available=Sets.intersection(available,topBorders.get(previousBottom));
			}
			if (j>0)	{
				Border previousRight=result[i][j-1].getRightBorder();
				available=Sets.intersection(available,leftBorders.get(previousRight));
			}
			int nextI,nextJ;
			if (j==N-1)	{
				// Next row.
				nextI=i+1;
				nextJ=0;
			}	else	{
				// Next element in the same row.
				nextI=i;
				nextJ=j+1;
			}
			for (RotatedTile t:available)	{
				int id=t.tile.id;
				if (usedTiles.contains(id)) continue;
				usedTiles.add(id);
				result[i][j]=t;
				if (nextI>=N) return true;
				else if (getAssembledImageRecursive(result,usedTiles,nextI,nextJ)) return true;
				result[i][j]=null;
				usedTiles.removeInt(id);
			}
			return false;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> content=Resources.readLines(file,Charsets.UTF_8);
		List<Tile> tiles=parseTiles(content);
		RotatedTile[][] image=new RotatedImageFinder(tiles).getAssembledImage();
		long result=image[0][0].tile.id;
		result*=image[0][image[0].length-1].tile.id;
		result*=image[image.length-1][0].tile.id;
		result*=image[image.length-1][image[image.length-1].length-1].tile.id;
		System.out.println(result);
	}
}
