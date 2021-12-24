package com.nbrevu.advent2021;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.CharLongMap;
import com.koloboke.collect.map.hash.HashCharLongMaps;
import com.koloboke.collect.set.CharSet;
import com.koloboke.collect.set.hash.HashCharSets;

public class Advent24_0 {
	private final static String IN_FILE="Advent24.txt";
	
	private final static int DIGITS=14;
	private final static char[] VARS=new char[] {'x','y','z','w'};
	private final static CharSet VALID_VARS=HashCharSets.newImmutableSet(VARS);
	
	private static class Alu	{
		private final CharLongMap variables;
		public Alu()	{
			variables=HashCharLongMaps.newMutableMap();
			init();
		}
		public void init()	{
			for (char c:VARS) variables.put(c,0l);
		}
		public long get(char c)	{
			return variables.get(c);
		}
		public void set(char c,long value)	{
			variables.put(c,value);
		}
	}
	
	private static class Input	{
		private Deque<Long> input;
		public Input(Deque<Long> input)	{
			this.input=input;
		}
		public static Input decomposeNumber(long in)	{
			Deque<Long> result=new LinkedList<>();
			for (int i=0;i<DIGITS;++i)	{
				long digit=in%10;
				if (digit==0) return null;
				result.push(digit);
				in/=10;
			}
			return new Input(result);
		}
		public long getNextInput()	{
			return input.pop().longValue();
		}
	}
	
	private static interface Instruction	{
		public void operate(Alu alu,Input input);
	}
	
	private static class Inp implements Instruction	{
		private final char operand;
		public Inp(char operand)	{
			this.operand=operand;
		}
		@Override
		public void operate(Alu alu,Input input) {
			alu.set(operand,input.getNextInput());
		}
	}
	
	private static enum TwoOperandInstruction	{
		ADD("add")	{
			@Override
			public long operate(long a,long b)	{
				return a+b;
			}
		},	MUL("mul")	{
			@Override
			public long operate(long a,long b)	{
				return a*b;
			}
		},	DIV("div")	{
			@Override
			public long operate(long a,long b)	{
				return LongMath.divide(a,b,RoundingMode.DOWN);
			}
		},	MOD("mod")	{
			@Override
			public long operate(long a,long b)	{
				return a%b;
			}
		},	EQL("eql")	{
			@Override
			public long operate(long a,long b)	{
				return (a==b)?1l:0l;
			}
		};		
		private final String id;
		private TwoOperandInstruction(String id)	{
			this.id=id;
		}
		public abstract long operate(long a,long b);
		private final static Map<String,TwoOperandInstruction> ID_MAP=getIdMap();
		private static Map<String,TwoOperandInstruction> getIdMap()	{
			Map<String,TwoOperandInstruction> result=new HashMap<>();
			for (TwoOperandInstruction op:values()) result.put(op.id,op);
			return result;
		}
		public static TwoOperandInstruction getFromId(String id)	{
			return ID_MAP.get(id);
		}
	}
	
	private static class ConstantInstruction implements Instruction	{
		private final TwoOperandInstruction instruction;
		private final char variable;
		private final long constant;
		public ConstantInstruction(TwoOperandInstruction instruction,char variable,long constant)	{
			this.instruction=instruction;
			this.variable=variable;
			this.constant=constant;
		}
		@Override
		public void operate(Alu alu,Input input) {
			long prevVal=alu.get(variable);
			long nextVal=instruction.operate(prevVal,constant);
			alu.set(variable,nextVal);
		}
	}
	
	private static class VariableInstruction implements Instruction	{
		private final TwoOperandInstruction instruction;
		private final char var1;
		private final char var2;
		public VariableInstruction(TwoOperandInstruction instruction,char var1,char var2)	{
			this.instruction=instruction;
			this.var1=var1;
			this.var2=var2;
		}
		@Override
		public void operate(Alu alu,Input input) {
			long prevVal=alu.get(var1);
			long otherVal=alu.get(var2);
			long nextVal=instruction.operate(prevVal,otherVal);
			alu.set(var1,nextVal);
		}
	}
	
	private static char getVar(String operand)	{
		if (operand.length()!=1) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
		char c=operand.charAt(0);
		if (VALID_VARS.contains(c)) return c;
		else throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
	}
	
	private static OptionalLong getConstant(String operand)	{
		try	{
			return OptionalLong.of(Long.parseLong(operand));
		}	catch (NumberFormatException exc)	{
			return OptionalLong.empty();
		}
	}
	
	private static Instruction parseLine(String line)	{
		String[] split=line.split(" ");
		if (split[0].equals("inp"))	{
			if (split.length!=2) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			return new Inp(getVar(split[1]));
		}	else	{
			if (split.length!=3) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			TwoOperandInstruction instr=TwoOperandInstruction.getFromId(split[0]);
			char op1=getVar(split[1]);
			OptionalLong op2n=getConstant(split[2]);
			if (op2n.isPresent()) return new ConstantInstruction(instr,op1,op2n.getAsLong());
			char op2v=getVar(split[2]);
			return new VariableInstruction(instr,op1,op2v);
		}
	}
	
	private static class Calculator	{
		private final Alu alu;
		private final List<Instruction> instructions;
		public Calculator(List<Instruction> instructions)	{
			this.alu=new Alu();
			this.instructions=instructions;
		}
		public long operate(long n)	{
			alu.init();
			Input input=Input.decomposeNumber(n);
			if (input==null) return -1;
			for (int i=0;i<instructions.size();++i) instructions.get(i).operate(alu,input);
			return alu.get('z');
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		List<Instruction> instrs=new ArrayList<>();
		for (String line:lines) instrs.add(parseLine(line));
		Calculator calc=new Calculator(instrs);
		System.out.println(calc.operate(91411143612181l));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		//System.out.println(n);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
