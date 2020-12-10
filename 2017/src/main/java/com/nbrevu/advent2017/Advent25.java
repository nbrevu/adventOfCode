package com.nbrevu.advent2017;

import java.io.IOException;

import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

public class Advent25 {
	private final static int STEPS=12523873;
	
	private static enum TuringStatus	{
		A	{
			@Override
			public TuringStatus run(MachineState machine) {
				if (machine.currentValue()==0)	{
					machine.write(1);
					machine.moveRight();
					return B;
				}	else	{
					machine.write(1);
					machine.moveLeft();
					return E;
				}
			}
		},	B	{
			@Override
			public TuringStatus run(MachineState machine) {
				if (machine.currentValue()==0)	{
					machine.write(1);
					machine.moveRight();
					return C;
				}	else	{
					machine.write(1);
					machine.moveRight();
					return F;
				}
			}
		},	C	{
			@Override
			public TuringStatus run(MachineState machine) {
				if (machine.currentValue()==0)	{
					machine.write(1);
					machine.moveLeft();
					return D;
				}	else	{
					machine.write(0);
					machine.moveRight();
					return B;
				}
			}
		},	D	{
			@Override
			public TuringStatus run(MachineState machine) {
				if (machine.currentValue()==0)	{
					machine.write(1);
					machine.moveRight();
					return E;
				}	else	{
					machine.write(0);
					machine.moveLeft();
					return C;
				}
			}
		},	E	{
			@Override
			public TuringStatus run(MachineState machine) {
				if (machine.currentValue()==0)	{
					machine.write(1);
					machine.moveLeft();
					return A;
				}	else	{
					machine.write(0);
					machine.moveRight();
					return D;
				}
			}
		},	F	{
			@Override
			public TuringStatus run(MachineState machine) {
				if (machine.currentValue()==0)	{
					machine.write(1);
					machine.moveRight();
					return A;
				}	else	{
					machine.write(1);
					machine.moveRight();
					return C;
				}
			}
		};
		public abstract TuringStatus run(MachineState machine);
	}
	
	private static class MachineState	{
		private int position;
		private IntIntMap tape;
		private TuringStatus status;
		public MachineState()	{
			position=0;
			tape=HashIntIntMaps.newMutableMap();
			status=TuringStatus.A;
		}
		public int currentValue()	{
			return tape.getOrDefault(position,0);
		}
		public void write(int value)	{
			tape.put(position,value);
		}
		public void moveLeft()	{
			--position;
		}
		public void moveRight()	{
			++position;
		}
		public void step()	{
			status=status.run(this);
		}
		public int countOnes()	{
			int result=0;
			for (IntCursor cursor=tape.values().cursor();cursor.moveNext();) if (cursor.elem()==1) ++result;
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		MachineState state=new MachineState();
		for (int i=0;i<STEPS;++i) state.step();
		System.out.println(state.countOnes());
	}
}
