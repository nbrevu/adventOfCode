package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;
import com.koloboke.function.LongLongToLongFunction;

public class Advent9_1 {
	private final static String IN_FILE="Advent9.txt";
	
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
		private final Deque<Long> inputs;
		private LongConsumer output;
		private int index;
		private long relativeOffset;
		public IntcodeComputer(long[] program)	{
			this.program=Arrays.copyOf(program,program.length);
			inputs=new ArrayDeque<>();
			output=null;
			index=0;
			relativeOffset=0;
			additionalMemory=HashLongLongMaps.newMutableMap();
		}
		public void setOutput(LongConsumer output)	{
			this.output=output;
		}
		public LongConsumer addToInputs()	{
			return inputs::addLast;
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
			if (inputs.isEmpty()) return OperationResult.WAITING;
			long input=inputs.pollFirst();
			if (modes[0]==2) op1+=relativeOffset;
			directWrite(op1,input);
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
	
	private static List<Long> runProgram(long[] program,long... inputs)	{
		IntcodeComputer comp=new IntcodeComputer(program);
		LongConsumer addInput=comp.addToInputs();
		for (long l:inputs) addInput.accept(l);
		List<Long> result=new ArrayList<>();
		comp.setOutput(result::add);
		if (!comp.run()) throw new RuntimeException("Ran out of inputs.");
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		long[] program=parse(Resources.readLines(file,Charsets.UTF_8).get(0));
		System.out.println(runProgram(program,1l));
	}
}
