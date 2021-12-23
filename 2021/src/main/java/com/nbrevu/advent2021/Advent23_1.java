package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Advent23_1 {
	private final static String IN_FILE="Advent23.txt";
	
	private final static Pattern LINE_PATTERN_1=Pattern.compile("^###([A-D])#([A-D])#([A-D])#([A-D])###$");
	private final static Pattern LINE_PATTERN_2=Pattern.compile("^#([A-D])#([A-D])#([A-D])#([A-D])#$");
	
	private final static int CORRIDOR_LENGTH=11;
	private final static IntSet INVALID_CORRIDOR_POSITIONS=HashIntSets.newImmutableSet(new int[] {2,4,6,8});
	private final static int SIZE=2;
	
	private static enum Amphipod	{
		A(0,1),B(1,10),C(2,100),D(3,1000);
		
		private final int room;
		private final int energy;
		
		private Amphipod(int room,int energy)	{
			this.room=room;
			this.energy=energy;
		}
	}
	
	private static class Pod	{
		// SIZE-1 is the bottom. 0 is the top (right below the corridor).
		private final Amphipod[] contents;
		public Pod()	{
			contents=new Amphipod[SIZE];
		}
		public Pod(Pod parent)	{
			contents=new Amphipod[SIZE];
			System.arraycopy(parent.contents,0,contents,0,SIZE);
		}
		public boolean isComplete(Amphipod goal)	{
			for (int i=0;i<SIZE;++i) if (contents[i]!=goal) return false;
			return true;
		}
		public Amphipod canPop(Amphipod goal)	{
			for (int i=0;i<SIZE;++i)	{
				Amphipod content=contents[i];
				if (content==null) continue;
				if (content!=goal) return content;
				// This looks strange, but: if there is "noise", it makes sense to pop. If the pod is correctly filled so far, it doesn't.
				for (int j=i+1;j<SIZE;++j) if (contents[j]!=goal) return content;
				return null;
			}
			return null;
		}
		public boolean canPush(Amphipod goal)	{
			if (contents[0]!=null) return false;
			for (int i=0;i<SIZE;++i) if ((contents[i]!=null)&&(contents[i]!=goal)) return false;
			return true;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(contents);
		}
		@Override
		public boolean equals(Object other)	{
			return Arrays.equals(contents,((Pod)other).contents);
		}
	}
	
	private static class Corridor	{
		private final Amphipod[] contents;
		public Corridor()	{
			contents=new Amphipod[CORRIDOR_LENGTH];
		}
		public Corridor(Corridor parent)	{
			contents=new Amphipod[CORRIDOR_LENGTH];
			System.arraycopy(parent.contents,0,contents,0,CORRIDOR_LENGTH);
		}
		public boolean isClear(int source,int target)	{
			int from,to;
			if (target<source)	{
				from=target;
				to=source-1;
			}	else	{
				from=source+1;
				to=target;
			}
			for (int i=from;i<=to;++i) if (contents[i]!=null) return false;
			return true;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(contents);
		}
		@Override
		public boolean equals(Object other)	{
			return Arrays.equals(contents,((Corridor)other).contents);
		}
	}
	
	private static enum MovementType	{
		POD_TO_CORRIDOR,CORRIDOR_TO_POD,POD_TO_POD;
	}
	
	private static class Movement	{
		public final MovementType type;
		public final int source;
		public final int target;
		private Movement(MovementType type,int source,int target)	{
			this.type=type;
			this.source=source;
			this.target=target;
		}
		private final static Movement[][][] STORAGE=createStorage();
		private static Movement[][][] createStorage()	{
			Movement[][][] result=new Movement[3][CORRIDOR_LENGTH][CORRIDOR_LENGTH];
			// POD_TO_CORRIDOR (index=0).
			for (int j=0;j<CORRIDOR_LENGTH;++j) if (!INVALID_CORRIDOR_POSITIONS.contains(j)) for (int i=0;i<4;++i) result[0][i][j]=new Movement(MovementType.POD_TO_CORRIDOR,i,j);
			// CORRIDOR_TO_POD (index=1).
			for (int i=0;i<CORRIDOR_LENGTH;++i) if (!INVALID_CORRIDOR_POSITIONS.contains(i)) for (int j=0;j<4;++j) result[1][i][j]=new Movement(MovementType.CORRIDOR_TO_POD,i,j);
			// POD_TO_POD (index=2).
			for (int i=0;i<4;++i) for (int j=0;j<4;++j) if (i!=j) result[2][i][j]=new Movement(MovementType.POD_TO_POD,i,j);
			return result;
		}
		public static Movement getMove(MovementType type,int source,int target)	{
			return STORAGE[type.ordinal()][source][target];
		}
	}
	
	private static class MazeState	{
		private final static Amphipod[] AMPHIPODS=Amphipod.values();
		private final Pod[] pods;
		private final Corridor corridor;
		private final long currentEnergy;
		public MazeState(Amphipod[][] locations)	{
			pods=new Pod[4];
			for (int i=0;i<4;++i)	{
				pods[i]=new Pod();
				for (int j=0;j<SIZE;++j) pods[i].contents[j]=locations[i][j];
			}
			corridor=new Corridor();
			currentEnergy=0;
		}
		private MazeState(MazeState parent,Movement move)	{
			switch (move.type)	{
				case POD_TO_CORRIDOR:	{
					pods=new Pod[4];
					corridor=new Corridor(parent.corridor);
					int depth=0;
					while (parent.pods[move.source].contents[depth]==null) ++depth;
					int basePod=2+2*move.source;
					int delta=Math.abs(basePod-move.target)+1+depth;
					for (int i=0;i<4;++i) if (i!=move.source) pods[i]=parent.pods[i];
					pods[move.source]=new Pod(parent.pods[move.source]);
					Amphipod moving=parent.pods[move.source].contents[depth];
					pods[move.source].contents[depth]=null;
					corridor.contents[move.target]=moving;
					currentEnergy=parent.currentEnergy+moving.energy*delta;
					break;
				}	case CORRIDOR_TO_POD:	{
					pods=new Pod[4];
					corridor=new Corridor(parent.corridor);
					int depth=SIZE-1;
					while (parent.pods[move.target].contents[depth]!=null) --depth;
					int basePod=2+2*move.target;
					int delta=Math.abs(move.source-basePod)+1+depth;
					for (int i=0;i<4;++i) if (i!=move.target) pods[i]=parent.pods[i];
					pods[move.target]=new Pod(parent.pods[move.target]);
					Amphipod moving=parent.corridor.contents[move.source];
					pods[move.target].contents[depth]=moving;
					corridor.contents[move.source]=null;
					currentEnergy=parent.currentEnergy+moving.energy*delta;
					break;
				}	case POD_TO_POD:	{
					pods=new Pod[4];
					corridor=parent.corridor;
					int sourceDepth=0;
					int targetDepth=SIZE-1;
					while (parent.pods[move.source].contents[sourceDepth]==null) ++sourceDepth;
					while (parent.pods[move.target].contents[targetDepth]!=null) --targetDepth;
					int delta=2*Math.abs(move.source-move.target)+2+sourceDepth+targetDepth;
					for (int i=0;i<4;++i) if ((i!=move.source)&&(i!=move.target)) pods[i]=parent.pods[i];
					pods[move.source]=new Pod(parent.pods[move.source]);
					pods[move.target]=new Pod(parent.pods[move.target]);
					Amphipod moving=parent.pods[move.source].contents[sourceDepth];
					pods[move.source].contents[sourceDepth]=null;
					pods[move.target].contents[targetDepth]=moving;
					currentEnergy=parent.currentEnergy+moving.energy*delta;
					break;
				}	default:throw new RuntimeException("What have you been smoking, JVM? This can't happen.");
			}
		}
		public List<MazeState> getChildren()	{
			List<MazeState> result=new ArrayList<>();
			for (int i=0;i<4;++i)	{
				Amphipod moving=pods[i].canPop(AMPHIPODS[i]);
				if (moving==null) continue;
				int firstAvailable=2*i+2;
				while ((firstAvailable>0)&&(corridor.contents[firstAvailable-1]==null)) --firstAvailable;
				int lastAvailable=2*i+2;
				while ((lastAvailable<CORRIDOR_LENGTH-1)&&(corridor.contents[lastAvailable+1]==null)) ++lastAvailable;
				int goal=moving.room;
				int corridorGoal=2*goal+2;
				// First type of movements: pod to pod.
				if ((firstAvailable<=corridorGoal)&&(corridorGoal<=lastAvailable)&&pods[goal].canPush(moving)) result.add(new MazeState(this,Movement.getMove(MovementType.POD_TO_POD,i,goal)));
				// Second type of movements: "pop".
				for (int j=firstAvailable;j<=lastAvailable;++j) if (!INVALID_CORRIDOR_POSITIONS.contains(j)) result.add(new MazeState(this,Movement.getMove(MovementType.POD_TO_CORRIDOR,i,j)));
			}
			// Third type of movements: "push"
			for (int i=0;i<CORRIDOR_LENGTH;++i) if (corridor.contents[i]!=null)	{
				Amphipod moving=corridor.contents[i];
				int goal=moving.room;
				int corridorGoal=2*goal+2;
				if (pods[goal].canPush(moving)&&corridor.isClear(i,corridorGoal)) result.add(new MazeState(this,Movement.getMove(MovementType.CORRIDOR_TO_POD,i,goal)));
			}
			return result;
		}
		public boolean isFinished()	{
			for (int i=0;i<4;++i) if (!pods[i].isComplete(AMPHIPODS[i])) return false;
			return true;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(pods)+corridor.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			MazeState msOther=(MazeState)other;
			return Arrays.equals(pods,msOther.pods)&&corridor.equals(msOther.corridor);
		}
	}
	
	// Not an actual A* because the heuristic part is not there.
	private static class PseudoAStar	{
		private NavigableMap<Long,List<MazeState>> states;
		private Set<MazeState> visited;
		public PseudoAStar(Amphipod[][] locations)	{
			states=new TreeMap<>();
			visited=new HashSet<>();
			push(new MazeState(locations));
		}
		private MazeState pop()	{
			Map.Entry<Long,List<MazeState>> entry=states.firstEntry();
			List<MazeState> list=entry.getValue();
			if (list.size()==1)	{
				states.remove(entry.getKey());
				return list.get(0);
			}	else return list.remove(0);
		}
		private void push(MazeState state)	{
			states.computeIfAbsent(state.currentEnergy,(Long unused)->new LinkedList<>()).add(state);
		}
		public long solve()	{
			for (;;)	{
				MazeState state=pop();
				if (!visited.add(state)) continue;
				if (state.isFinished()) return state.currentEnergy;
				for (MazeState child:state.getChildren()) push(child);
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		if (lines.size()!=5) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
		if (!lines.get(0).equals("#############")||!lines.get(1).equals("#...........#")||!lines.get(4).trim().equals("#########")) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
		Amphipod[][] initialPods=new Amphipod[4][SIZE];
		Matcher matcher1=LINE_PATTERN_1.matcher(lines.get(2));
		Matcher matcher2=LINE_PATTERN_2.matcher(lines.get(3).trim());
		if (!matcher1.matches()||!matcher2.matches()) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
		for (int r=0;r<4;++r)	{
			String elem1=matcher1.group(r+1);
			String elem2=matcher2.group(r+1);
			initialPods[r][0]=Amphipod.valueOf(elem1);
			initialPods[r][SIZE-1]=Amphipod.valueOf(elem2);
		}
		long result=new PseudoAStar(initialPods).solve();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
