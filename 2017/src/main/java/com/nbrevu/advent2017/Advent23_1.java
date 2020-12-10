package com.nbrevu.advent2017;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent23_1 {
	private final static String IN_FILE="Advent23.txt";
	
	private static enum DoubleArgInstructionImpl	{
		SET	{
			@Override
			public void run(ComputerStatus status,String arg1,String arg2) {
				status.writeRegister(arg1,status.readData(arg2));
			}
		},
		SUB	{
			@Override
			public void run(ComputerStatus status,String arg1,String arg2) {
				status.writeRegister(arg1,status.readData(arg1).subtract(status.readData(arg2)));
			}
		},
		MUL	{
			@Override
			public void run(ComputerStatus status,String arg1,String arg2) {
				status.increaseMulCounter();
				status.writeRegister(arg1,status.readData(arg1).multiply(status.readData(arg2)));
			}
		},
		JNZ	{
			@Override
			public void run(ComputerStatus status,String arg1,String arg2) {
				BigInteger x=status.readData(arg1);
				if (x.signum()!=0) status.jump(status.readData(arg2).intValue());
			}
		};
		public abstract void run(ComputerStatus status,String arg1,String arg2);
	}
	
	private static interface Instruction	{
		public void run(ComputerStatus status);
	}
	private static class DoubleArgInstruction implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^([^\\s]+) ([^\\s]+) ([^\\s]+)$");
		private DoubleArgInstructionImpl impl;
		private final String arg1;
		private final String arg2;
		public DoubleArgInstruction(DoubleArgInstructionImpl impl,String arg1,String arg2)	{
			this.impl=impl;
			this.arg1=arg1;
			this.arg2=arg2;
		}
		@Override
		public void run(ComputerStatus status) {
			impl.run(status,arg1,arg2);
		}
		private static DoubleArgInstructionImpl parseInstr(String str)	{
			switch (str)	{
				case "set":return DoubleArgInstructionImpl.SET;
				case "sub":return DoubleArgInstructionImpl.SUB;
				case "mul":return DoubleArgInstructionImpl.MUL;
				case "jnz":return DoubleArgInstructionImpl.JNZ;
			}
			throw new IllegalArgumentException("HCF instruction detected!");
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			else return new DoubleArgInstruction(parseInstr(matcher.group(1)),matcher.group(2),matcher.group(3));
		}
	}
	
	private static enum InstructionParser	{
		INSTANCE;
		private final List<Function<String,Instruction>> parserFunctions=Arrays.asList(DoubleArgInstruction::parse);
		public Instruction parse(String line)	{
			for (Function<String,Instruction> parser:parserFunctions)	{
				Instruction instr=parser.apply(line);
				if (instr!=null) return instr;
			}
			throw new IllegalArgumentException("Was machst du?"+line);
		}
	}
	
	private static class ComputerStatus	{
		private final List<Instruction> instructions;
		private final Map<String,BigInteger> registers;
		private int instrPointer;
		private int mulCounter;
		public ComputerStatus(List<Instruction> instructions)	{
			this.instructions=instructions;
			registers=new HashMap<>();
			instrPointer=0;
			mulCounter=0;
		}
		@Override
		public String toString()	{
			return String.format("%s, instrPointer=%d.",registers,instrPointer);
		}
		public BigInteger readRegister(String reg)	{
			return registers.getOrDefault(reg,BigInteger.ZERO);
		}
		public BigInteger readData(String reg)	{
			try	{
				return new BigInteger(reg);
			}	catch (NumberFormatException exc)	{
				return readRegister(reg);
			}
		}
		public void writeRegister(String reg,BigInteger value)	{
			registers.put(reg,value);
		}
		public void jump(int offset)	{
			// YES, minus one. Because there is an automatic +1 after each instruction.
			instrPointer+=offset-1;
		}
		public int run()	{
			for (;(instrPointer>=0)&&(instrPointer<instructions.size());)	{
				instructions.get(instrPointer).run(this);
				++instrPointer;
			}
			return mulCounter;
		}
		public void increaseMulCounter()	{
			++mulCounter;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Instruction> instructions=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (!line.isBlank()) instructions.add(InstructionParser.INSTANCE.parse(line));
		ComputerStatus computer=new ComputerStatus(instructions);
		System.out.println(computer.run());
	}
}
