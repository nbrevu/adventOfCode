package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashLongLongMaps;
import com.koloboke.function.LongLongToLongFunction;

public class Advent13_1 {
	private final static String IN_FILE="Advent13.txt";
	
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
		/*
		public void setInput(LongSupplier input)	{
			this.input=input;
		}
		*/
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
	private static enum Tile	{
		EMPTY(0),WALL(1),BLOCK(2),PADDLE(3),BALL(4);
		private final int id;
		private Tile(int id)	{
			this.id=id;
		}
		private final static IntObjMap<Tile> ELEMENTS=createElementsMap();
		private static IntObjMap<Tile> createElementsMap()	{
			IntObjMap<Tile> result=HashIntObjMaps.newMutableMap();
			for (Tile t:values()) result.put(t.id,t);
			return result;
		}
		public static Tile getFromId(int id)	{
			return ELEMENTS.get(id);
		}
	}
	
	private static class Screen	{
		private final Map<Position,Tile> contents;
		private int tmpX;
		private int tmpY;
		private LongConsumer nextConsumer;
		public Screen()	{
			contents=new HashMap<>();
			tmpX=0;
			tmpY=0;
			nextConsumer=this::acceptX;
		}
		private void acceptX(long x)	{
			tmpX=(int)x;
			nextConsumer=this::acceptY;
		}
		private void acceptY(long y)	{
			tmpY=(int)y;
			nextConsumer=this::acceptTile;
		}
		private void acceptTile(long id)	{
			Tile tile=Tile.getFromId((int)id);
			contents.put(new Position(tmpX,tmpY),tile);
			nextConsumer=this::acceptX;
		}
		public void consumeOutput(long val)	{
			nextConsumer.accept(val);
		}
		private Map<Position,Tile> getTiles()	{
			return contents;
		}
	}
	
	private static Screen initProgram(long[] program)	{
		IntcodeComputer comp=new IntcodeComputer(program);
		Screen result=new Screen();
		comp.setOutput(result::consumeOutput);
		comp.run();
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		long[] program=parse(Resources.readLines(file,Charsets.UTF_8).get(0));
		Screen screen=initProgram(program);
		int result=0;
		for (Tile tile:screen.getTiles().values()) if (tile==Tile.BLOCK) ++result;
		System.out.println(result);
	}
}
