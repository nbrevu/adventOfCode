package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.base.Charsets;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Table;
import com.google.common.io.Resources;

public class Advent15_1 {
	// 199512 is TOO LOW, but I strongly believe that I'm on the right track.
	/*-				Expected	Actual	So...
	 * Advent15		?			199512	NOK
	 * Advent15_2	27730		27730	OK
	 * Advent15_3	36334		35532	NOK
	 * Advent15_4	39514		41454	NOK
	 * Advent15_5	27755		27755	OK
	 * Advent15_6	28944		28944	OK
	 * Advent15_7	18740		18740	OK
	 */
	private final static String IN_FILE="Advent15.txt";

	private static enum Direction	{
		TOP	{
			@Override
			public Position move(Position from) {
				return new Position(from.row-1,from.col);
			}
		},
		LEFT	{
			@Override
			public Position move(Position from) {
				return new Position(from.row,from.col-1);
			}
		},
		RIGHT	{
			@Override
			public Position move(Position from) {
				return new Position(from.row,from.col+1);
			}
		},
		BOTTOM	{
			@Override
			public Position move(Position from) {
				return new Position(from.row+1,from.col);
			}
		};
		public static Direction getPreferred(Direction d1,Direction d2)	{
			if (d1==null) return d2;
			else if (d2==null) return d1;
			else return (d1.ordinal()<d2.ordinal())?d1:d2;
		}
		public abstract Position move(Position from);
	}
	
	private static class Position implements Comparable<Position>	{
		private final static Direction[] DIRS=Direction.values();
		// This class is used as a map key and therefore it must be immutable to avoid subtle mistakes.
		public final int row;
		public final int col;
		public Position(int row,int col)	{
			this.row=row;
			this.col=col;
		}
		@Override
		public int hashCode()	{
			return row+col;
		}
		@Override
		public boolean equals(Object other)	{
			Position p=(Position)other;
			return (row==p.row)&&(col==p.col);
		}
		@Override
		public int compareTo(Position o) {
			int diffR=row-o.row;
			return (diffR!=0)?diffR:(col-o.col);
		}
		@Override
		public String toString()	{
			return "("+row+","+col+")";
		}
		public void getChildren(Collection<Position> toAdd)	{
			for (Direction dir:DIRS) toAdd.add(dir.move(this));
		}
		public List<Position> getChildren()	{
			List<Position> result=new ArrayList<>();
			getChildren(result);
			return result;
		}
		public NavigableMap<Position,Direction> getChildrenMap()	{
			NavigableMap<Position,Direction> result=new TreeMap<>();
			for (Direction dir:DIRS) result.put(dir.move(this),dir);
			return result;
		}
	}
	
	private static interface Square	{
		public boolean canPass();
	}
	
	private static enum FixedSquare implements Square	{
		WALL(false),EMPTY(true);
		private final boolean canPass;
		private FixedSquare(boolean canPass)	{
			this.canPass=canPass;
		}
		@Override
		public boolean canPass()	{
			return canPass;
		}
	}
	
	private static enum AgentType	{
		ELF,GOBLIN;
	}
	
	private static class Agent implements Square	{
		public final AgentType type;
		public int hp;
		public int attackPower;
		public Position pos;
		private Agent(AgentType type,int hp,int attackPower,int row,int col)	{
			this.type=type;
			this.hp=hp;
			this.attackPower=attackPower;
			pos=new Position(row,col);
		}
		public static Agent createElf(int row,int col)	{
			return new Agent(AgentType.ELF,200,3,row,col);
		}
		public static Agent createGoblin(int row,int col)	{
			return new Agent(AgentType.GOBLIN,200,3,row,col);
		}
		@Override
		public boolean canPass()	{
			return false;
		}
	}
	
	private static class Scenario	{
		private final Square[][] map;
		private final NavigableMap<Position,Agent> sortedCharacters;
		private final Set<Agent> elves;
		private final Set<Agent> goblins;
		private Scenario(int height,int width)	{
			map=new Square[height][width];
			sortedCharacters=new TreeMap<>();
			elves=new HashSet<>();
			goblins=new HashSet<>();
		}
		private void printMap(long runs)	{
			System.out.println(runs+" turns from the start:");
			for (Square[] ss:map)	{
				StringBuilder sb=new StringBuilder();
				for (Square s:ss) if (s==FixedSquare.EMPTY) sb.append('.');
				else if (s==FixedSquare.WALL) sb.append('#');
				else if (((Agent)s).type==AgentType.ELF) sb.append('E');
				else sb.append('G');
				System.out.println(sb.toString());
			}
		}
		public static Scenario parse(List<String> map)	{
			int height=map.size();
			int width=map.get(0).length();
			Scenario result=new Scenario(height,width);
			for (int i=0;i<height;++i)	{
				String row=map.get(i);
				for (int j=0;j<width;++j) switch (row.charAt(j))	{
					case '.':result.map[i][j]=FixedSquare.EMPTY;break;
					case '#':result.map[i][j]=FixedSquare.WALL;break;
					case 'E':	{
						Agent elf=Agent.createElf(i,j);
						result.sortedCharacters.put(elf.pos,elf);
						result.elves.add(elf);
						result.map[i][j]=elf;
						break;
					}
					case 'G':	{
						Agent goblin=Agent.createGoblin(i,j);
						result.sortedCharacters.put(goblin.pos,goblin);
						result.goblins.add(goblin);
						result.map[i][j]=goblin;
						break;
					}
					default:throw new UnsupportedOperationException("What's a \""+row.charAt(j)+"\"? I don't know.");
				}
			}
			return result;
		}
		private Set<Agent> getOwnSet(AgentType type)	{
			return (type==AgentType.ELF)?elves:goblins;
		}
		private Set<Agent> getEnemySet(AgentType type)	{
			return (type==AgentType.ELF)?goblins:elves;
		}
		private boolean areAllEnemiesDead(AgentType type)	{
			return getEnemySet(type).isEmpty();
		}
		private long collectRemainingHpInTeam(AgentType type)	{
			long result=0;
			for (Agent c:getOwnSet(type)) result+=c.hp;
			return result;
		}
		private void setSquareAt(Position pos,Square val)	{
			map[pos.row][pos.col]=val;
		}
		private Square getSquareAt(Position pos)	{
			return map[pos.row][pos.col];
		}
		private Agent getPossibleTarget(Agent c)	{
			for (Position p:c.pos.getChildren())	{
				Square s=getSquareAt(p);
				if (s instanceof Agent)	{
					Agent enemy=(Agent)s;
					if (enemy.type!=c.type) return enemy;
				}
			}
			return null;
		}
		private boolean attack(Agent attacking,Agent defending)	{
			defending.hp-=attacking.attackPower;
			return defending.hp<=0;
		}
		private void findAllPositionsRecursive(Position currentPosition,Set<Position> visited,int level,Multimap<Position,Integer> result)	{
			result.put(currentPosition,level);
			Set<Position> visited2=new HashSet<>(visited);
			visited2.add(currentPosition);
			for (Position p:currentPosition.getChildren()) if (getSquareAt(p).canPass()&&!visited.contains(p)) findAllPositionsRecursive(p,visited2,1+level,result);
		}
		private Direction moveBruteForce(Agent c)	{
			// Intentionally inefficient but kind of straightforward method.
			// First step: create the reachable positions map.
			Map<Direction,Multimap<Position,Integer>> bruteForceMap=new EnumMap<>(Direction.class);
			Map<Position,Direction> initialDirections=c.pos.getChildrenMap();
			for (Map.Entry<Position,Direction> entry:initialDirections.entrySet()) if (getSquareAt(entry.getKey()).canPass())	{
				Multimap<Position,Integer> visitMap=MultimapBuilder.hashKeys().arrayListValues().build();
				findAllPositionsRecursive(entry.getKey(),Collections.emptySet(),1,visitMap);
				bruteForceMap.put(entry.getValue(),visitMap);
			}
			// Second step: create the goals set.
			NavigableSet<Position> goals=new TreeSet<>();
			for (Agent other:getEnemySet(c.type)) other.pos.getChildren(goals);
			// Third step: find the best paths.
			Table<Position,Direction,Integer> bestPathsTable=HashBasedTable.create();
			for (Position p:goals) for (Map.Entry<Direction,Multimap<Position,Integer>> entry:bruteForceMap.entrySet())	{
				Direction dir=entry.getKey();
				Collection<Integer> pathLengths=entry.getValue().get(p);
				OptionalInt min=pathLengths.stream().mapToInt(Integer::intValue).min();
				if (min.isPresent()) bestPathsTable.put(p,dir,min.getAsInt());
			}
			// Fourth step: look for the paths with best length.
			int minLength=Integer.MAX_VALUE;
			Multimap<Position,Direction> curatedTable=HashMultimap.create();
			for (Table.Cell<Position,Direction,Integer> cell:bestPathsTable.cellSet())	{
				int value=cell.getValue();
				if (value<minLength)	{
					curatedTable.clear();
					curatedTable.put(cell.getRowKey(),cell.getColumnKey());
					minLength=value;
				}	else if (value==minLength) curatedTable.put(cell.getRowKey(),cell.getColumnKey());
			}
			// Fifth step: choose the FIRST position. If any.
			NavigableSet<Position> pos=new TreeSet<>(curatedTable.keySet());
			if (pos.isEmpty()) return null;
			// Sixth step: choose the FIRST direction.
			NavigableSet<Direction> results=new TreeSet<>(curatedTable.get(pos.first()));
			// ENDUT!! HOCH HECH!!
			return results.first();
		}
		private Direction moveShiftedPriorities(Agent c)	{
			// This gets the exact same results as moveBruteForce, which means that, almost surely, the error isn't here :O.
			NavigableSet<Position> goals=new TreeSet<>();
			for (Agent other:getEnemySet(c.type)) other.pos.getChildren(goals);
			NavigableMap<Position,Direction> currentGeneration=new TreeMap<>();
			for (Map.Entry<Position,Direction> entry:c.pos.getChildrenMap().entrySet()) if (getSquareAt(entry.getKey()).canPass())	{
				if (goals.contains(entry.getKey())) return entry.getValue();
				else currentGeneration.put(entry.getKey(),entry.getValue());
			}
			Set<Position> visited=new HashSet<>();
			NavigableSet<Direction> bestSolutions=new TreeSet<>();
			while (!currentGeneration.isEmpty())	{
				NavigableMap<Position,Direction> nextGeneration=new TreeMap<>();
				for (Map.Entry<Position,Direction> entry:currentGeneration.entrySet())	{
					Position p=entry.getKey();
					Direction origin=entry.getValue();
					for (Position child:p.getChildren()) if (!visited.contains(child))	{
						visited.add(child);
						Square s=getSquareAt(child);
						if (!s.canPass()) continue;
						// Put it in the next generation bin...
						Direction existing=nextGeneration.get(child);
						Direction preferred=Direction.getPreferred(origin,existing);
						if (preferred!=existing) nextGeneration.put(child,preferred);
						// But also in the "solutions" bin, if it's a goal.
						if (goals.contains(child)) bestSolutions.add(preferred);
					}
				}
				if (!bestSolutions.isEmpty()) return bestSolutions.first();
				currentGeneration=nextGeneration;
			}
			return null;
		}
		private Direction move(Agent c)	{
			// This gets the exact same results as moveBruteForce, which means that, almost surely, the error isn't here :O.
			NavigableSet<Position> goals=new TreeSet<>();
			for (Agent other:getEnemySet(c.type)) other.pos.getChildren(goals);
			NavigableMap<Position,Direction> currentGeneration=new TreeMap<>();
			for (Map.Entry<Position,Direction> entry:c.pos.getChildrenMap().entrySet()) if (getSquareAt(entry.getKey()).canPass())	{
				if (goals.contains(entry.getKey())) return entry.getValue();
				else currentGeneration.put(entry.getKey(),entry.getValue());
			}
			Set<Position> visited=new HashSet<>();
			NavigableMap<Position,Direction> bestSolutions=new TreeMap<>();
			while (!currentGeneration.isEmpty())	{
				NavigableMap<Position,Direction> nextGeneration=new TreeMap<>();
				for (Map.Entry<Position,Direction> entry:currentGeneration.entrySet())	{
					Position p=entry.getKey();
					Direction origin=entry.getValue();
					for (Position child:p.getChildren()) if (!visited.contains(child))	{
						visited.add(child);
						Square s=getSquareAt(child);
						if (!s.canPass()) continue;
						// Put it in the next generation bin...
						Direction existing=nextGeneration.get(child);
						Direction preferred=Direction.getPreferred(origin,existing);
						if (preferred!=existing) nextGeneration.put(child,preferred);
						// But also in the "solutions" bin, if it's a goal.
						if (goals.contains(child))	{
							Direction existingInSol=bestSolutions.get(child);
							Direction preferredInSol=Direction.getPreferred(origin,existingInSol);
							if (preferredInSol!=existingInSol) bestSolutions.put(child,preferredInSol);
						}
					}
				}
				if (!bestSolutions.isEmpty()) return bestSolutions.firstEntry().getValue();
				currentGeneration=nextGeneration;
			}
			return null;
		}
		private Direction moveYetAnotherVersion(Agent c)	{
			// This gets the exact same results as moveBruteForce, which means that, almost surely, the error isn't here :O.
			NavigableSet<Position> goals=new TreeSet<>();
			for (Agent other:getEnemySet(c.type)) goals.add(other.pos);
			NavigableMap<Position,Direction> currentGeneration=new TreeMap<>();
			for (Map.Entry<Position,Direction> entry:c.pos.getChildrenMap().entrySet()) if (getSquareAt(entry.getKey()).canPass())	{
				if (goals.contains(entry.getKey())) return entry.getValue();
				else currentGeneration.put(entry.getKey(),entry.getValue());
			}
			Set<Position> visited=new HashSet<>();
			NavigableMap<Position,Direction> bestSolutions=new TreeMap<>();
			while (!currentGeneration.isEmpty())	{
				NavigableMap<Position,Direction> nextGeneration=new TreeMap<>();
				for (Map.Entry<Position,Direction> entry:currentGeneration.entrySet())	{
					Position p=entry.getKey();
					Direction origin=entry.getValue();
					for (Position child:p.getChildren()) if (!visited.contains(child))	{
						visited.add(child);
						if (goals.contains(child))	{
							Direction existingInSol=bestSolutions.get(child);
							Direction preferredInSol=Direction.getPreferred(origin,existingInSol);
							if (preferredInSol!=existingInSol) bestSolutions.put(child,preferredInSol);
						}
						Square s=getSquareAt(child);
						if (!s.canPass()) continue;
						Direction existing=nextGeneration.get(child);
						Direction preferred=Direction.getPreferred(origin,existing);
						if (preferred!=existing) nextGeneration.put(child,preferred);
					}
				}
				if (!bestSolutions.isEmpty()) return bestSolutions.firstEntry().getValue();
				currentGeneration=nextGeneration;
			}
			return null;
		}
		public long run()	{
			for (long runs=0;;++runs)	{
				//printMap(runs);
				//List<Agent> chars=new ArrayList<>(sortedCharacters.values());
				List<Agent> chars=new ArrayList<>();
				chars.addAll(elves);
				chars.addAll(goblins);
				chars.sort(Comparator.comparing((Agent a)->a.pos));
				System.out.println("Orden de actuación: "+Collections2.transform(chars,(Agent a)->a.pos)+".");
				for (Agent c:chars)	{
					if (c.hp<=0)	{
						System.out.println("Ay, estoy muerto.");
						continue;	// Died before its turn took place.
					}
					if (areAllEnemiesDead(c.type))	{
						System.out.println("Turno final: "+runs+".");
						return runs*collectRemainingHpInTeam(c.type);
					}
					Agent target=getPossibleTarget(c);
					if (target!=null)	{
						if (attack(c,target))	{
							sortedCharacters.remove(target.pos);
							getOwnSet(target.type).remove(target);
							setSquareAt(target.pos,FixedSquare.EMPTY);
						}
						continue;
					}
					Direction d=move(c);
					if (d==null) continue;
					Position newPos=d.move(c.pos);
					sortedCharacters.remove(c.pos);
					setSquareAt(c.pos,FixedSquare.EMPTY);
					c.pos=newPos;
					sortedCharacters.put(newPos,c);
					setSquareAt(newPos,c);
					target=getPossibleTarget(c);
					if (target!=null)	{
						if (attack(c,target))	{
							sortedCharacters.remove(target.pos);
							getOwnSet(target.type).remove(target);
							setSquareAt(target.pos,FixedSquare.EMPTY);
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> content=Resources.readLines(file,Charsets.UTF_8);
		Scenario scenario=Scenario.parse(content);
		long result=scenario.run();
		System.out.println(result);
	}
}
