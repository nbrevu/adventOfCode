package com.nbrevu.advent2016;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Advent11_1 {
	private static enum Element	{
		TM,PU,SR,PM,RU;
	}
	private static interface Item	{
		public Microchip asMicrochip();
		public Generator asGenerator();
	}
	private static enum Microchip implements Item	{
		M_TM(Element.TM),M_PU(Element.PU),M_SR(Element.SR),M_PM(Element.PM),M_RU(Element.RU);
		public final Element element;
		private Microchip(Element element)	{
			this.element=element;
		}
		@Override
		public Microchip asMicrochip() {
			return this;
		}
		@Override
		public Generator asGenerator() {
			return null;
		}
	}
	private static enum Generator implements Item	{
		G_TM(Element.TM),G_PU(Element.PU),G_SR(Element.SR),G_PM(Element.PM),G_RU(Element.RU);
		public final Element element;
		private Generator(Element element)	{
			this.element=element;
		}
		@Override
		public Microchip asMicrochip() {
			return null;
		}
		@Override
		public Generator asGenerator() {
			return this;
		}
	}
	
	private static class FloorState	{
		public final Set<Item> items;
		public FloorState(Set<Item> items)	{
			this.items=items;
		}
		public boolean isEmpty()	{
			return items.isEmpty();
		}
		public boolean isValid()	{
			Set<Element> microchips=EnumSet.noneOf(Element.class);
			Set<Element> generators=EnumSet.noneOf(Element.class);
			for (Item it:items)	{
				Microchip m=it.asMicrochip();
				if (m==null) generators.add(it.asGenerator().element);
				else microchips.add(m.element);
			}
			/*
			 * If this method returns false, then both conditions are false. Which means that:
			 * 1) there is at least one generator.
			 * 2) "microchips" is not a subset of "generators", which imples that there are elements of "microchips" not present in "generators".
			 * This means that there is a microchip without its generator, in presence of at least one extraneous generator. NICHT MÖGLICH!!!!!
			 */
			return generators.isEmpty()||generators.containsAll(microchips);
		}
		public List<Pair<List<Item>,FloorState>> getChildren()	{
			/*
			 * The first element of the pair is a list with one or two elements with will go in the elevator. The floorstate contains the remaining
			 * elements. The child floorstates are guaranteed to be valid.
			 */
			List<Item> itemList=Lists.newArrayList(items);
			List<Pair<List<Item>,FloorState>> result=new ArrayList<>();
			for (int i=0;i<itemList.size();++i)	{
				Item i1=itemList.get(i);
				FloorState state1=without(i1);
				if (state1.isValid()) result.add(Pair.of(Arrays.asList(i1),state1));
				for (int j=i+1;j<itemList.size();++j)	{
					Item i2=itemList.get(j);
					FloorState state2=state1.without(i2);
					if (state2.isValid()) result.add(Pair.of(Arrays.asList(i1,i2),state2));
				}
			}
			return result;
		}
		private FloorState without(Item it)	{
			Set<Item> itemsCopy=Sets.newHashSet(items);
			itemsCopy.remove(it);
			return new FloorState(itemsCopy);
		}
		private FloorState with(List<Item> newItems)	{
			Set<Item> itemsCopy=Sets.newHashSet(items);
			itemsCopy.addAll(newItems);
			return new FloorState(itemsCopy);
		}
		@Override
		public int hashCode()	{
			return items.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			FloorState fs=(FloorState)other;
			return items.equals(fs.items);
		}
	}
	private static class FullState	{
		private final int elevator;
		private final FloorState[] floors;
		public FullState(int elevator,FloorState[] floors)	{
			this.elevator=elevator;
			this.floors=floors;
		}
		public boolean isFinal()	{
			for (int i=0;i<floors.length-1;++i) if (!floors[i].isEmpty()) return false;
			return true;
		}
		public List<FullState> getChildren()	{
			List<FullState> result=new ArrayList<>();
			List<Pair<List<Item>,FloorState>> floorChildren=floors[elevator].getChildren();
			if (elevator>0) for (Pair<List<Item>,FloorState> movement:floorChildren)	{
				FloorState prevFloor=floors[elevator-1].with(movement.getLeft());
				if (prevFloor.isValid())	{
					FloorState[] newFloors=new FloorState[floors.length];
					for (int i=0;i<floors.length;++i) if (i==elevator) newFloors[i]=movement.getRight();
					else if (i==(elevator-1)) newFloors[i]=prevFloor;
					else newFloors[i]=floors[i];
					result.add(new FullState(elevator-1,newFloors));
				}
			}
			if (elevator<floors.length-1) for (Pair<List<Item>,FloorState> movement:floorChildren)	{
				FloorState nextFloor=floors[elevator+1].with(movement.getLeft());
				if (nextFloor.isValid())	{
					FloorState[] newFloors=new FloorState[floors.length];
					for (int i=0;i<floors.length;++i) if (i==elevator) newFloors[i]=movement.getRight();
					else if (i==(elevator+1)) newFloors[i]=nextFloor;
					else newFloors[i]=floors[i];
					result.add(new FullState(elevator+1,newFloors));
				}
			}
			return result;
		}
		@Override
		public int hashCode()	{
			return elevator+Arrays.hashCode(floors);
		}
		@Override
		public boolean equals(Object other)	{
			FullState fs=(FullState)other;
			return (elevator==fs.elevator)&&Arrays.equals(floors,fs.floors);
		}
	}
	
	private static int breadthFirst(FullState state)	{
		Set<FullState> currentGen=Collections.singleton(state);
		Set<FullState> visited=new HashSet<>();
		int steps=0;
		for (;;)	{
			Set<FullState> nextGen=new HashSet<>();
			for (FullState fs:currentGen)	{
				if (fs.isFinal()) return steps;
				/*
				 * Note that a child state can never be in "currentGen" since the current state is either all even or all odd, and the next gen
				 * will be respectively all odd or all even.
				 */
				for (FullState child:fs.getChildren()) if (!visited.contains(child)) nextGen.add(child);
				visited.add(fs);
			}
			currentGen=nextGen;
			++steps;
		}
	}

	/*-
	 * In this case I'll spend less time coding the input by hand.
	The first floor contains a thulium generator, a thulium-compatible microchip, a plutonium generator, and a strontium generator.
	The second floor contains a plutonium-compatible microchip and a strontium-compatible microchip.
	The third floor contains a promethium generator, a promethium-compatible microchip, a ruthenium generator, and a ruthenium-compatible microchip.
	The fourth floor contains nothing relevant.
	 * 
	 */
	public static void main(String[] args)	{
		FloorState floor1=new FloorState(Sets.newHashSet(Generator.G_TM,Microchip.M_TM,Generator.G_PU,Generator.G_SR));
		FloorState floor2=new FloorState(Sets.newHashSet(Microchip.M_PU,Microchip.M_SR));
		FloorState floor3=new FloorState(Sets.newHashSet(Generator.G_PM,Microchip.M_PM,Generator.G_RU,Microchip.M_RU));
		FloorState floor4=new FloorState(Collections.emptySet());
		FullState state=new FullState(0,new FloorState[] {floor1,floor2,floor3,floor4});
		int result=breadthFirst(state);
		System.out.println(result);
	}
}
