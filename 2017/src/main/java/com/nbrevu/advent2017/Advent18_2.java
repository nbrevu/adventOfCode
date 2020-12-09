package com.nbrevu.advent2017;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent18_2 {
	private final static String IN_FILE="Advent18.txt";
	
	private static enum SingleArgInstructionImpl	{
		SND	{
			@Override
			public boolean run(ComputerStatus status,String arg) {
				status.send(status.readData(arg));
				return false;
			}
		},
		RCV	{
			@Override
			public boolean run(ComputerStatus status,String arg) {
				Optional<BigInteger> result=status.receive();
				if (result.isEmpty()) return true;
				status.writeRegister(arg,result.get());
				return false;
			}
		};
		public abstract boolean run(ComputerStatus status,String arg);
	}
	private static enum DoubleArgInstructionImpl	{
		SET	{
			@Override
			public boolean run(ComputerStatus status,String arg1,String arg2) {
				status.writeRegister(arg1,status.readData(arg2));
				return false;
			}
		},
		ADD	{
			@Override
			public boolean run(ComputerStatus status,String arg1,String arg2) {
				status.writeRegister(arg1,status.readData(arg1).add(status.readData(arg2)));
				return false;
			}
		},
		MUL	{
			@Override
			public boolean run(ComputerStatus status,String arg1,String arg2) {
				status.writeRegister(arg1,status.readData(arg1).multiply(status.readData(arg2)));
				return false;
			}
		},
		MOD	{
			@Override
			public boolean run(ComputerStatus status,String arg1,String arg2) {
				status.writeRegister(arg1,status.readData(arg1).mod(status.readData(arg2)));
				return false;
			}
		},
		JGZ	{
			@Override
			public boolean run(ComputerStatus status,String arg1,String arg2) {
				BigInteger x=status.readData(arg1);
				if (x.signum()>0) status.jump(status.readData(arg2).intValue());
				return false;
			}
		};
		public abstract boolean run(ComputerStatus status,String arg1,String arg2);
	}
	
	private static interface Instruction	{
		// Return true if blocked (RCV on empty queue).
		public boolean run(ComputerStatus status);
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
		public boolean run(ComputerStatus status) {
			return impl.run(status,arg);
		}
		private static SingleArgInstructionImpl parseInstr(String str)	{
			switch (str)	{
				case "snd":return SingleArgInstructionImpl.SND;
				case "rcv":return SingleArgInstructionImpl.RCV;
			}
			throw new IllegalArgumentException("HCF instruction detected!");
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			else return new SingleArgInstruction(parseInstr(matcher.group(1)),matcher.group(2));
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
		public boolean run(ComputerStatus status) {
			return impl.run(status,arg1,arg2);
		}
		private static DoubleArgInstructionImpl parseInstr(String str)	{
			switch (str)	{
				case "set":return DoubleArgInstructionImpl.SET;
				case "add":return DoubleArgInstructionImpl.ADD;
				case "mul":return DoubleArgInstructionImpl.MUL;
				case "mod":return DoubleArgInstructionImpl.MOD;
				case "jgz":return DoubleArgInstructionImpl.JGZ;
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
		private final List<Function<String,Instruction>> parserFunctions=Arrays.asList(SingleArgInstruction::parse,DoubleArgInstruction::parse);
		public Instruction parse(String line)	{
			for (Function<String,Instruction> parser:parserFunctions)	{
				Instruction instr=parser.apply(line);
				if (instr!=null) return instr;
			}
			throw new IllegalArgumentException("Was machst du?"+line);
		}
	}
	
	private static enum ProgramStatus	{
		FINE,BLOCKED,FINISHED;
	}
	
	private static class ComputerStatus	{
		private final List<Instruction> instructions;
		private final Map<String,BigInteger> registers;
		private int instrPointer;
		private int sendCount;
		private final Queue<BigInteger> ownQueue;
		private Queue<BigInteger> otherQueue;
		private ProgramStatus status;
		public ComputerStatus(List<Instruction> instructions,int pValue)	{
			this.instructions=instructions;
			registers=new HashMap<>();
			registers.put("p",BigInteger.valueOf(pValue));
			instrPointer=0;
			sendCount=0;
			ownQueue=new ArrayDeque<>();
			status=ProgramStatus.FINE;
		}
		public void join(ComputerStatus other)	{
			otherQueue=other.ownQueue;
			other.otherQueue=ownQueue;
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
		public void send(BigInteger value)	{
			++sendCount;
			otherQueue.add(value);
		}
		public Optional<BigInteger> receive()	{
			if (ownQueue.isEmpty()) return Optional.empty();
			BigInteger result=ownQueue.poll();
			return Optional.of(result);
		}
		public void jump(int offset)	{
			// YES, minus one. Because there is an automatic +1 after each instruction.
			instrPointer+=offset-1;
		}
		public void run()	{
			status=ProgramStatus.FINE;
			for (;(instrPointer>=0)&&(instrPointer<instructions.size());)	{
				if (instructions.get(instrPointer).run(this))	{
					status=ProgramStatus.BLOCKED;
					return;
				}
				++instrPointer;
			}
			status=ProgramStatus.FINISHED;
		}
		public boolean canContinue()	{
			return (status==ProgramStatus.BLOCKED)&&!ownQueue.isEmpty();
		}
		public int getSendCount()	{
			return sendCount;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Instruction> instructions=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (!line.isBlank()) instructions.add(InstructionParser.INSTANCE.parse(line));
		ComputerStatus computer0=new ComputerStatus(instructions,0);
		ComputerStatus computer1=new ComputerStatus(instructions,1);
		computer0.join(computer1);
		do	{
			computer0.run();
			computer1.run();
		}	while (computer0.canContinue()||computer1.canContinue());
		System.out.println(computer1.getSendCount());
	}
}
