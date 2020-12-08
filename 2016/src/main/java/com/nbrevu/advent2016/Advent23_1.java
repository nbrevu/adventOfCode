package com.nbrevu.advent2016;

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
	private final static String FINAL_REG="a";
	
	private static enum SingleArgInstructionImpl	{
		INC	{
			@Override
			public void run(ComputerStatus status,String arg) {
				if (status.isRegister(arg)) status.writeRegister(arg,status.readData(arg).add(BigInteger.ONE));
			}
		},
		DEC	{
			@Override
			public void run(ComputerStatus status,String arg) {
				if (status.isRegister(arg)) status.writeRegister(arg,status.readData(arg).subtract(BigInteger.ONE));
			}
		},
		TGL	{
			@Override
			public void run(ComputerStatus status,String arg) {
				BigInteger regValue=status.readData(arg);
				status.toggle(regValue.intValue());
			}
		};
		public abstract void run(ComputerStatus status,String arg);
	}
	private static enum DoubleArgInstructionImpl	{
		CPY	{
			@Override
			public void run(ComputerStatus status,String arg1,String arg2) {
				if (status.isRegister(arg2)) status.writeRegister(arg2,status.readData(arg1));
			}
		},
		JNZ	{
			@Override
			public void run(ComputerStatus status,String arg1,String arg2) {
				BigInteger regValue=status.readData(arg1);
				if (regValue.signum()!=0) status.jump(status.readData(arg2).intValue());
			}
		};
		public abstract void run(ComputerStatus status,String arg1,String arg2);
	}
	
	private static interface Instruction	{
		public void run(ComputerStatus status);
		public void toggle();
	}
	private static class SingleArgInstruction implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^([^\\s]+) ([^\\s]+)$");
		private SingleArgInstructionImpl impl;
		private final String arg;
		public SingleArgInstruction(SingleArgInstructionImpl impl,String arg)	{
			this.impl=impl;
			this.arg=arg;
		}
		@Override
		public void run(ComputerStatus status) {
			impl.run(status,arg);
		}
		private static SingleArgInstructionImpl parseInstr(String str)	{
			switch (str)	{
				case "inc":return SingleArgInstructionImpl.INC;
				case "dec":return SingleArgInstructionImpl.DEC;
				case "tgl":return SingleArgInstructionImpl.TGL;
			}
			throw new IllegalArgumentException("HCF instruction detected!");
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			else return new SingleArgInstruction(parseInstr(matcher.group(1)),matcher.group(2));
		}
		@Override
		public void toggle() {
			impl=(impl==SingleArgInstructionImpl.INC)?SingleArgInstructionImpl.DEC:SingleArgInstructionImpl.INC;
		}
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
				case "cpy":return DoubleArgInstructionImpl.CPY;
				case "jnz":return DoubleArgInstructionImpl.JNZ;
			}
			throw new IllegalArgumentException("HCF instruction detected!");
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			else return new DoubleArgInstruction(parseInstr(matcher.group(1)),matcher.group(2),matcher.group(3));
		}
		@Override
		public void toggle() {
			impl=(impl==DoubleArgInstructionImpl.JNZ)?DoubleArgInstructionImpl.CPY:DoubleArgInstructionImpl.JNZ;
		}
	}
	
	private static enum InstructionParser	{
		INSTANCE;
		private final List<Function<String,Instruction>> parserFunctions=Arrays.asList(SingleArgInstruction::parse,DoubleArgInstruction::parse);
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
		public ComputerStatus(List<Instruction> instructions)	{
			this.instructions=instructions;
			registers=new HashMap<>();
			registers.put("a",BigInteger.valueOf(7));
			registers.put("b",BigInteger.ZERO);
			registers.put("c",BigInteger.ZERO);
			registers.put("d",BigInteger.ZERO);
			instrPointer=0;
		}
		@Override
		public String toString()	{
			return String.format("%s, instrPointer=%d.",registers,instrPointer);
		}
		public boolean isRegister(String reg)	{
			return registers.containsKey(reg);
		}
		public BigInteger readRegister(String reg)	{
			return registers.get(reg);
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
		public void toggle(int offset)	{
			int target=instrPointer+offset;
			if ((target>=0)&&(target<instructions.size())) instructions.get(target).toggle();
		}
		public void run()	{
			while ((instrPointer>=0)&&(instrPointer<instructions.size()))	{
				instructions.get(instrPointer).run(this);
				++instrPointer;
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Instruction> instructions=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (!line.isBlank()) instructions.add(InstructionParser.INSTANCE.parse(line));
		ComputerStatus computer=new ComputerStatus(instructions);
		computer.run();
		BigInteger result=computer.readRegister(FINAL_REG);
		System.out.println(result);
	}
}
