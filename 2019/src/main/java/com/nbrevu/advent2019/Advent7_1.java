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
import com.koloboke.function.LongLongToLongFunction;

public class Advent7_1 {
	private final static String IN_FILE="Advent7.txt";
	
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
		private final Deque<Long> inputs;
		private LongConsumer output;
		private int index;
		public IntcodeComputer(long[] program)	{
			this.program=Arrays.copyOf(program,program.length);
			inputs=new ArrayDeque<>();
			output=null;
			index=0;
		}
		public void setOutput(LongConsumer output)	{
			this.output=output;
		}
		public LongConsumer addToInputs()	{
			return inputs::addLast;
		}
		public boolean run()	{
			while (index<program.length)	{
				long baseOpcode=program[index];
				int opCode=(int)(baseOpcode%100);
				int[] modes=extractModes(baseOpcode,3);
				OperationResult instrResult=runInstr(opCode,modes);
				if (instrResult==OperationResult.WAITING) return false;
				else if (instrResult==OperationResult.END) return true;
			}
			return true;
		}
		private OperationResult runInstr(int opCode,int[] modes)	{
			switch (opCode)	{
				case 1:return runInstr3(modes,(long a,long b)->a+b);
				case 2:return runInstr3(modes,(long a,long b)->a*b);
				case 3:return runInput();
				case 4:return runOutput(modes);
				case 5:return runJmp(modes,(long a)->a!=0);
				case 6:return runJmp(modes,(long a)->a==0);
				case 7:return runInstr3(modes,(long a,long b)->(a<b)?1:0);
				case 8:return runInstr3(modes,(long a,long b)->(a==b)?1:0);
				case 99:return OperationResult.END;
			}
			throw new UnsupportedOperationException("Unknown operator.");
		}
		private OperationResult runInstr3(int[] modes,LongLongToLongFunction fun)	{
			long op1=program[index+1];
			long op2=program[index+2];
			long op3=program[index+3];
			long val1=(modes[0]==1)?op1:program[(int)op1];
			long val2=(modes[1]==1)?op2:program[(int)op2];
			program[(int)op3]=fun.applyAsLong(val1,val2);
			index+=4;
			return OperationResult.CONTINUE;
		}
		private OperationResult runInput()	{
			long op1=program[index+1];
			if (inputs.isEmpty()) return OperationResult.WAITING;
			long input=inputs.pollFirst();
			program[(int)op1]=input;
			index+=2;
			return OperationResult.CONTINUE;
		}
		private OperationResult runOutput(int[] modes)	{
			long op1=program[index+1];
			long val=(modes[0]==1)?op1:program[(int)op1];
			output.accept(val);
			index+=2;
			return OperationResult.CONTINUE;
		}
		private OperationResult runJmp(int[] modes,LongPredicate fun)	{
			long op1=program[index+1];
			long op2=program[index+2];
			long val1=(modes[0]==1)?op1:program[(int)op1];
			long val2=(modes[1]==1)?op2:program[(int)op2];
			if (fun.test(val1)) index=(int)val2;
			else index+=3;
			return OperationResult.CONTINUE;
		}
	}
	
	private static boolean nextPermutation(long[] arr)	{
		// Thanks once more, indy256 from http://codeforces.com/blog/entry/3980.
		for (int i=arr.length-2;i>=0;--i) if (arr[i]<arr[i+1]) for (int j=arr.length-1;;--j) if (arr[j]>arr[i])	{
			long sw=arr[i];
			arr[i]=arr[j];
			arr[j]=sw;
			for (++i,j=arr.length-1;i<j;++i,--j)	{
				sw=arr[i];
				arr[i]=arr[j];
				arr[j]=sw;
			}
			return true;
		}
		return false;
	}
	
	private static long findMaxAmplification(long[] program)	{
		long[] order=new long[] {0,1,2,3,4};
		long maxValue=Long.MIN_VALUE;
		do	{
			long lastValue=0l;
			for (int i=0;i<order.length;++i)	{
				List<Long> outputs=new ArrayList<>();
				IntcodeComputer comp=new IntcodeComputer(program);
				LongConsumer inputAdder=comp.addToInputs();
				inputAdder.accept(order[i]);
				inputAdder.accept(lastValue);
				comp.setOutput(outputs::add);
				if (!comp.run()) throw new RuntimeException("Deadlock.");
				lastValue=outputs.get(0).longValue();
			}
			maxValue=Math.max(maxValue,lastValue);
		}	while (nextPermutation(order));
		return maxValue;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		long[] program=parse(Resources.readLines(file,Charsets.UTF_8).get(0));
		System.out.println(findMaxAmplification(program));
	}
}
