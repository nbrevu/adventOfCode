package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.function.LongLongPredicate;

public class Advent16_2 {
	private final static String IN_FILE="Advent16.txt";
	
	private static BitSet unrollHex(String hex)	{
		BitSet result=new BitSet(hex.length()*4);
		for (int i=0;i<hex.length();++i)	{
			int n;
			char c=hex.charAt(i);
			if ((c>='0')&&(c<='9')) n=c-'0';
			else if ((c>='A')&&(c<='F')) n=10+c-'A';
			else throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			if ((n&8)>0) result.set(4*i);
			if ((n&4)>0) result.set(4*i+1);
			if ((n&2)>0) result.set(4*i+2);
			if ((n&1)>0) result.set(4*i+3);
		}
		return result;
	}
	
	private abstract static class Packet	{
		public final int version;
		public Packet(int version)	{
			this.version=version;
		}
		public abstract int getVersionSum();
		public abstract long getValue();
	}
	
	private static class LiteralValuePacket	extends Packet	{
		private final long value;
		public LiteralValuePacket(int version,long value)	{
			super(version);
			this.value=value;
		}
		@Override
		public int getVersionSum()	{
			return version;
		}
		@Override
		public long getValue()	{
			return value;
		}
	}
	
	private abstract static class OperatorPacket extends Packet	{
		protected final List<Packet> subpackets;
		public OperatorPacket(int version,List<Packet> subpackets)	{
			super(version);
			this.subpackets=subpackets;
		}
		@Override
		public int getVersionSum()	{
			int result=version;
			for (Packet p:subpackets) result+=p.getVersionSum();
			return result;
		}
	}
	
	private static class SumPacket extends OperatorPacket	{
		public SumPacket(int version,List<Packet> subpackets)	{
			super(version,subpackets);
		}
		@Override
		public long getValue()	{
			long result=0;
			for (Packet p:subpackets) result+=p.getValue();
			return result;
		}
	}
	
	private static class ProductPacket extends OperatorPacket	{
		public ProductPacket(int version,List<Packet> subpackets)	{
			super(version,subpackets);
		}
		@Override
		public long getValue()	{
			long result=1;
			for (Packet p:subpackets) result*=p.getValue();
			return result;
		}
	}
	
	private static class MinimumPacket extends OperatorPacket	{
		public MinimumPacket(int version,List<Packet> subpackets)	{
			super(version,subpackets);
		}
		@Override
		public long getValue()	{
			long result=Long.MAX_VALUE;
			for (Packet p:subpackets) result=Math.min(result,p.getValue());
			return result;
		}
	}
	
	private static class MaximumPacket extends OperatorPacket	{
		public MaximumPacket(int version,List<Packet> subpackets)	{
			super(version,subpackets);
		}
		@Override
		public long getValue()	{
			long result=Long.MIN_VALUE;
			for (Packet p:subpackets) result=Math.max(result,p.getValue());
			return result;
		}
	}
	
	private static class ComparisonPacket extends OperatorPacket	{
		private final LongLongPredicate comparator;
		public ComparisonPacket(int version,int type,List<Packet> subpackets)	{
			super(version,subpackets);
			if (subpackets.size()!=2) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			switch (type)	{
				case 5:comparator=(long a,long b)->a>b;break;
				case 6:comparator=(long a,long b)->a<b;break;
				case 7:comparator=(long a,long b)->a==b;break;
				default:throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			}
		}
		@Override
		public long getValue()	{
			long result1=subpackets.get(0).getValue();
			long result2=subpackets.get(1).getValue();
			boolean comparison=comparator.test(result1,result2);
			return comparison?1:0;
		}
	}
	
	private static class Parser	{
		private final BitSet bits;
		private int currentRead;
		public Parser(BitSet bits)	{
			this.bits=bits;
			currentRead=0;
		}
		private int parseNextBits(int howMany)	{
			int result=0;
			for (int i=0;i<howMany;++i)	{
				boolean bit=bits.get(currentRead+i);
				result*=2;
				if (bit) ++result;
			}
			currentRead+=howMany;
			return result;
		}
		private boolean readNextBit()	{
			boolean result=bits.get(currentRead);
			++currentRead;
			return result;
		}
		private long parseType4Literal()	{
			long result=0;
			for (;;)	{
				boolean canContinue=readNextBit();
				result*=16;
				result+=parseNextBits(4);
				if (!canContinue) break;
			}
			return result;
		}
		public Packet parseAPacket()	{
			int version=parseNextBits(3);
			int type=parseNextBits(3);
			if (type==4)	{
				long literal=parseType4Literal();
				return new LiteralValuePacket(version,literal);
			}	else	{
				List<Packet> subpackets=new ArrayList<>();
				if (readNextBit())	{
					int howManySubpackets=parseNextBits(11);
					for (int i=0;i<howManySubpackets;++i) subpackets.add(parseAPacket());
				}	else	{
					int howManyBits=parseNextBits(15);
					int expectedEnd=currentRead+howManyBits;
					while (currentRead<expectedEnd) subpackets.add(parseAPacket());
					if (currentRead!=expectedEnd) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
				}
				switch (type)	{
					case 0:return new SumPacket(version,subpackets);
					case 1:return new ProductPacket(version,subpackets);
					case 2:return new MinimumPacket(version,subpackets);
					case 3:return new MaximumPacket(version,subpackets);
					default:return new ComparisonPacket(version,type,subpackets);
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		String hex=lines.get(0);
		BitSet message=unrollHex(hex);
		Parser parser=new Parser(message);
		Packet packet=parser.parseAPacket();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(packet.getValue());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
