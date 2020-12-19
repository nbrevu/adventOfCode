package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import com.koloboke.function.LongLongToLongFunction;

public class Advent11_1 {
	private final static String IN_FILE="Advent11.txt";
	
	private static long[] parse(String line)	{
		return Pattern.compile(",").splitAsStream(line).mapToLong(Long::parseLong).toArray();
	}
	
	private static int[] extractModes(long baseOpcode,int howMany)	{
		int[] result=new int[howMany];
		baseOpcode/=100;
		for (int i=0;i<howMany;++i)	{
			result[i]=(int)(baseOpcode%10);
			baseOpcode/=10;
		}
		return result;
	}
	
	private static enum OperationResult	{
		CONTINUE,WAITING,END;
	}
	
	private static class IntcodeComputer	{
		private final long[] program;
		private final LongLongMap additionalMemory;
		private LongSupplier input;
		private LongConsumer output;
		private int index;
		private long relativeOffset;
		public IntcodeComputer(long[] program)	{
			this.program=Arrays.copyOf(program,program.length);
			input=null;
			output=null;
			index=0;
			relativeOffset=0;
			additionalMemory=HashLongLongMaps.newMutableMap();
		}
		public void setOutput(LongConsumer output)	{
			this.output=output;
		}
		public void setInput(LongSupplier input)	{
			this.input=input;
		}
		private long directRead(long dir)	{
			if (dir<0) throw new UnsupportedOperationException("Negative addresses are not supported. Yet?");
			else if (dir<program.length) return program[(int)dir];
			else return additionalMemory.getOrDefault(dir,0l);
		}
		private void directWrite(long dir,long val)	{
			if (dir<0) throw new UnsupportedOperationException("Negative addresses are not supported. Yet?");
			else if (dir<program.length) program[(int)dir]=val;
			else additionalMemory.put(dir,val);
		}
		public boolean run()	{
			for (;;)	{
				long baseOpcode=directRead(index);
				int opCode=(int)(baseOpcode%100);
				int[] modes=extractModes(baseOpcode,3);
				OperationResult instrResult=runInstr(opCode,modes);
				if (instrResult==OperationResult.WAITING) return false;
				else if (instrResult==OperationResult.END) return true;
			}
		}
		private OperationResult runInstr(int opCode,int[] modes)	{
			switch (opCode)	{
				case 1:return runInstr3(modes,(long a,long b)->a+b);
				case 2:return runInstr3(modes,(long a,long b)->a*b);
				case 3:return runInput(modes);
				case 4:return runOutput(modes);
				case 5:return runJmp(modes,(long a)->a!=0);
				case 6:return runJmp(modes,(long a)->a==0);
				case 7:return runInstr3(modes,(long a,long b)->(a<b)?1:0);
				case 8:return runInstr3(modes,(long a,long b)->(a==b)?1:0);
				case 9:return runIncreaseOffset(modes);
				case 99:return OperationResult.END;
			}
			throw new UnsupportedOperationException("Unknown operator.");
		}
		private long readParameter(int readIndex,int mode)	{
			long op=directRead(readIndex);
			switch (mode)	{
				case 0:return directRead(op);
				case 1:return op;
				case 2:return directRead(op+relativeOffset);
				default:throw new IllegalArgumentException("Unsupported mode.");
			}
		}
		private OperationResult runInstr3(int[] modes,LongLongToLongFunction fun)	{
			long op1=readParameter(index+1,modes[0]);
			long op2=readParameter(index+2,modes[1]);
			long op3=directRead(index+3);
			if (modes[2]==2) op3+=relativeOffset;
			directWrite(op3,fun.applyAsLong(op1,op2));
			index+=4;
			return OperationResult.CONTINUE;
		}
		private OperationResult runInput(int[] modes)	{
			long op1=directRead(index+1);
			// if (input.isEmpty()) return OperationResult.WAITING;
			long value=input.getAsLong();
			if (modes[0]==2) op1+=relativeOffset;
			directWrite(op1,value);
			index+=2;
			return OperationResult.CONTINUE;
		}
		private OperationResult runOutput(int[] modes)	{
			long val=readParameter(index+1,modes[0]);
			output.accept(val);
			index+=2;
			return OperationResult.CONTINUE;
		}
		private OperationResult runJmp(int[] modes,LongPredicate fun)	{
			long val1=readParameter(index+1,modes[0]);
			long val2=readParameter(index+2,modes[1]);
			if (fun.test(val1)) index=(int)val2;
			else index+=3;
			return OperationResult.CONTINUE;
		}
		private OperationResult runIncreaseOffset(int[] modes)	{
			long val=readParameter(index+1,modes[0]);
			relativeOffset+=val;
			index+=2;
			return OperationResult.CONTINUE;
		}
	}
	
	private static class Position	{
		public final int x;
		public final int y;
		public Position(int x,int y)	{
			this.x=x;
			this.y=y;
		}
		@Override
		public int hashCode()	{
			return x+y;
		}
		@Override
		public boolean equals(Object other)	{
			Position p=(Position)other;
			return (p.x==x)&&(p.y==y);
		}
	}
	private static enum Direction	{
		RIGHT	{
			@Override
			public void move(RobotAndHull state) {
				++state.x;
			}
		},	UP	{
			@Override
			public void move(RobotAndHull state) {
				--state.y;
			}
		},	LEFT	{
			@Override
			public void move(RobotAndHull state) {
				--state.x;
			}
		},	DOWN	{
			@Override
			public void move(RobotAndHull state) {
				++state.y;
			}
		};
		public abstract void move(RobotAndHull state);
		public Direction rotateLeft()	{
			return LEFT_ROTATION_MAP.get(this);
		}
		public Direction rotateRight()	{
			return RIGHT_ROTATION_MAP.get(this);
		}
		private final static Map<Direction,Direction> LEFT_ROTATION_MAP=getLeftRotationMap();
		private final static Map<Direction,Direction> RIGHT_ROTATION_MAP=getRightRotationMap();
		private static Map<Direction,Direction> getLeftRotationMap()	{
			Map<Direction,Direction> result=new EnumMap<>(Direction.class);
			result.put(RIGHT,UP);
			result.put(UP,LEFT);
			result.put(LEFT,DOWN);
			result.put(DOWN,RIGHT);
			return result;
		}
		private static Map<Direction,Direction> getRightRotationMap()	{
			Map<Direction,Direction> result=new EnumMap<>(Direction.class);
			result.put(RIGHT,DOWN);
			result.put(UP,RIGHT);
			result.put(LEFT,UP);
			result.put(DOWN,LEFT);
			return result;
		}
	}
	
	private static class RobotAndHull	{
		private final ObjIntMap<Position> paintedSquares;
		private int x;
		private int y;
		private Direction dir;
		private boolean expectingRotation;
		public RobotAndHull()	{
			paintedSquares=HashObjIntMaps.newMutableMap();
			x=0;
			y=0;
			dir=Direction.UP;
			expectingRotation=false;
		}
		public void parseRobotOutput(long value)	{
			if (expectingRotation)	{
				if (value==0) dir=dir.rotateLeft();
				else dir=dir.rotateRight();
				dir.move(this);
			}	else paintedSquares.put(new Position(x,y),(int)value);
			expectingRotation=!expectingRotation;
		}
		public int getCurrentColour()	{
			return paintedSquares.getOrDefault(new Position(x,y),0);
		}
		public ObjIntMap<Position> getPaintedSquares()	{
			return paintedSquares;
		}
	}
	
	private static ObjIntMap<Position> runProgram(long[] program)	{
		IntcodeComputer comp=new IntcodeComputer(program);
		RobotAndHull state=new RobotAndHull();
		comp.setInput(state::getCurrentColour);
		comp.setOutput(state::parseRobotOutput);
		comp.run();
		return state.getPaintedSquares();
	}
	
	private static List<String> getMessage(ObjIntMap<Position> colours)	{
		int minX=Integer.MAX_VALUE;
		int maxX=Integer.MIN_VALUE;
		int minY=Integer.MAX_VALUE;
		int maxY=Integer.MIN_VALUE;
		for (Position p:colours.keySet())	{
			int x=p.x;
			int y=p.y;
			minX=Math.min(minX,x);
			maxX=Math.max(maxX,x);
			minY=Math.min(minY,y);
			maxY=Math.max(maxY,y);
		}
		List<String> result=new ArrayList<>();
		for (int i=minY;i<=maxY;++i)	{
			StringBuilder sb=new StringBuilder();
			for (int j=minX;j<=maxX;++j)	{
				int colour=colours.getOrDefault(new Position(j,i),0);
				sb.append((colour==0)?' ':'█');
			}
			result.add(sb.toString());
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		long[] program=parse(Resources.readLines(file,Charsets.UTF_8).get(0));
		ObjIntMap<Position> result=runProgram(program);
		System.out.println(result.size());
		for (String line:getMessage(result)) System.out.println(line);
	}
}
