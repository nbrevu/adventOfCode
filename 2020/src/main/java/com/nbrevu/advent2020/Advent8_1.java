package com.nbrevu.advent2020;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent8_1 {
	private final static String IN_FILE="Advent8.txt";
	
	private static interface Instruction	{
		public void run(ComputerStatus status);
	}
	
	private static class Acc implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^acc ([\\+\\-]\\d+)$");
		private final BigInteger value;
		private Acc(BigInteger value)	{
			this.value=value;
		}
		@Override
		public void run(ComputerStatus status) {
			BigInteger accValue=status.readAccumulator();
			status.writeAccumulator(accValue.add(value));
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			else return new Acc(new BigInteger(matcher.group(1)));
		}
	}
	private static class Nop implements Instruction	{
		// Singleton pattern CAN'T be used because there is an equality comparison down the line. Ooooooh.
		private Nop()	{}
		private final static Pattern PATTERN=Pattern.compile("^nop (.+)$");
		@Override
		public void run(ComputerStatus status) {}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			else return new Nop();
		}
	}
	private static class Jmp implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^jmp ([\\+\\-]\\d+)$");
		private final int offset;
		private Jmp(int offset)	{
			this.offset=offset;
		}
		@Override
		public void run(ComputerStatus status)	{
			status.jump(offset);
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			int offset=Integer.parseInt(matcher.group(1));
			return new Jmp(offset);
		}
	}
	
	private static enum InstructionParser	{
		INSTANCE;
		private final List<Function<String,Instruction>> parserFunctions=Arrays.asList(Acc::parse,Nop::parse,Jmp::parse);
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
		private BigInteger accumulator;
		private int instrPointer;
		public ComputerStatus(List<Instruction> instructions)	{
			this.instructions=instructions;
			accumulator=BigInteger.ZERO;
			instrPointer=0;
		}
		public BigInteger readAccumulator()	{
			return accumulator;
		}
		public void writeAccumulator(BigInteger value)	{
			accumulator=value;
		}
		public void jump(int offset)	{
			// YES, minus one. Because there is an automatic +1 after each instruction.
			instrPointer+=offset-1;
		}
		public BigInteger run()	{
			Set<Instruction> alreadyRun=new HashSet<>();
			for (;;)	{
				Instruction nextInstruction=instructions.get(instrPointer);
				if (alreadyRun.contains(nextInstruction)) return accumulator;
				alreadyRun.add(nextInstruction);
				nextInstruction.run(this);
				++instrPointer;
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Instruction> instructions=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (!line.isBlank()) instructions.add(InstructionParser.INSTANCE.parse(line));
		ComputerStatus computer=new ComputerStatus(instructions);
		BigInteger result=computer.run();
		System.out.println(result);
	}
}
