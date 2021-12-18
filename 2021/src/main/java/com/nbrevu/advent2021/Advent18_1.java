package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent18_1 {
	private final static String IN_FILE="Advent18.txt";
	
	private abstract static class SnailNumber	{
		protected abstract Actions tryExplodeRecursive(int depth);
		protected abstract Actions trySplit();
		protected abstract void addLeft(int value);
		protected abstract void addRight(int value);
		protected abstract OptionalInt directValue();
		protected abstract void toString(StringBuilder sb);
		public abstract long getMagnitude();
		@Override
		public String toString()	{
			StringBuilder builder=new StringBuilder();
			toString(builder);
			return builder.toString();
		}
		public SnailPair add(SnailNumber other)	{
			return new SnailPair(this,other);
		}
		public final void normalise()	{
			for (;;)	{
				Actions result=tryExplodeRecursive(0);
				if (result.hasActions()) continue;
				result=trySplit();
				if (!result.hasActions()) break;
			}
		}
	}
	
	private static class SnailPair extends SnailNumber	{
		private SnailNumber left;
		private SnailNumber right;
		public SnailPair(SnailNumber left,SnailNumber right)	{
			this.left=left;
			this.right=right;
		}
		@Override
		protected Actions tryExplodeRecursive(int depth)	{
			Actions leftActions=left.tryExplodeRecursive(1+depth);
			if (leftActions.hasActions())	{
				if (leftActions.replaceWith!=null) left=leftActions.replaceWith;
				if (leftActions.propagateRight.isPresent()) right.addLeft(leftActions.propagateRight.getAsInt());
				if (leftActions.propagateLeft.isPresent()) return leftActions.keepPropagatingLeft();
				else return Actions.FINISHED_ACTIONS;
			}
			Actions rightActions=right.tryExplodeRecursive(1+depth);
			if (rightActions.hasActions())	{
				if (rightActions.replaceWith!=null) right=rightActions.replaceWith;
				if (rightActions.propagateLeft.isPresent()) left.addRight(rightActions.propagateLeft.getAsInt());
				if (rightActions.propagateRight.isPresent()) return rightActions.keepPropagatingRight();
				else return Actions.FINISHED_ACTIONS;
			}
			if (depth>=4)	{
				// Try to explode.
				OptionalInt literalLeft=left.directValue();
				OptionalInt literalRight=right.directValue();
				if (literalLeft.isPresent()&&literalRight.isPresent()) return Actions.explode(literalLeft.getAsInt(),literalRight.getAsInt());
			}
			return Actions.NO_ACTIONS;
		}
		@Override
		protected Actions trySplit()	{
			Actions leftActions=left.trySplit();
			if (leftActions.hasActions())	{
				if (leftActions.replaceWith!=null) left=leftActions.replaceWith;
				return Actions.FINISHED_ACTIONS;
			}
			Actions rightActions=right.trySplit();
			if (rightActions.hasActions())	{
				if (rightActions.replaceWith!=null) right=rightActions.replaceWith;
				return Actions.FINISHED_ACTIONS;
			}
			return Actions.NO_ACTIONS;
		}
		@Override
		protected void addLeft(int value)	{
			left.addLeft(value);
		}
		@Override
		protected void addRight(int value)	{
			right.addRight(value);
		}
		@Override
		protected OptionalInt directValue()	{
			return OptionalInt.empty();
		}
		@Override
		protected void toString(StringBuilder sb)	{
			sb.append('[');
			left.toString(sb);
			sb.append(',');
			right.toString(sb);
			sb.append(']');
		}
		@Override
		public long getMagnitude()	{
			return 3*left.getMagnitude()+2*right.getMagnitude();
		}
	}
	
	private static class SnailLiteral extends SnailNumber	{
		private int value;
		public SnailLiteral(int value)	{
			this.value=value;
		}
		@Override
		protected Actions tryExplodeRecursive(int depth)	{
			return Actions.NO_ACTIONS;
		}
		@Override
		protected Actions trySplit()	{
			if (value>=10)	{
				int left=value/2;
				int right=left;
				if ((value%2)==1) ++right;
				return Actions.split(new SnailPair(new SnailLiteral(left),new SnailLiteral(right)));
			}	else return Actions.NO_ACTIONS;
		}
		@Override
		protected void addLeft(int toAdd)	{
			value+=toAdd;
		}
		@Override
		protected void addRight(int toAdd)	{
			value+=toAdd;
		}
		@Override
		protected OptionalInt directValue()	{
			return OptionalInt.of(value);
		}
		@Override
		protected void toString(StringBuilder sb)	{
			sb.append(value);
		}
		@Override
		public long getMagnitude()	{
			return value;
		}
	}
	
	private static class Actions	{
		public final static Actions NO_ACTIONS=new Actions(false,OptionalInt.empty(),OptionalInt.empty(),null);
		public final static Actions FINISHED_ACTIONS=new Actions(true,OptionalInt.empty(),OptionalInt.empty(),null);
		public final boolean hasActed;
		public final OptionalInt propagateLeft;
		public final OptionalInt propagateRight;
		public final SnailNumber replaceWith;
		private Actions(boolean hasActed,OptionalInt propagateLeft,OptionalInt propagateRight,SnailNumber replaceWith)	{
			this.hasActed=hasActed;
			this.propagateLeft=propagateLeft;
			this.propagateRight=propagateRight;
			this.replaceWith=replaceWith;
		}
		public static Actions explode(int toLeft,int toRight)	{
			return new Actions(false,OptionalInt.of(toLeft),OptionalInt.of(toRight),new SnailLiteral(0));
		}
		public static Actions split(SnailNumber n)	{
			return new Actions(false,OptionalInt.empty(),OptionalInt.empty(),n);
		}
		public boolean hasActions()	{
			return hasActed||propagateLeft.isPresent()||propagateRight.isPresent()||(replaceWith!=null);
		}
		public Actions keepPropagatingLeft()	{
			return new Actions(true,propagateLeft,OptionalInt.empty(),null);
		}
		public Actions keepPropagatingRight()	{
			return new Actions(true,OptionalInt.empty(),propagateRight,null);
		}
	}
	
	private static class SnailParser	{
		private final char[] characters;
		private int currentRead;
		public SnailParser(String line)	{
			characters=line.toCharArray();
			currentRead=0;
		}
		public SnailNumber parse()	{
			SnailNumber result=parseANumber();
			if (currentRead!=characters.length) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			return result;
		}
		private char getNextChar()	{
			char result=characters[currentRead];
			++currentRead;
			return result;
		}
		private SnailNumber parseANumber()	{
			char c=getNextChar();
			if (c=='[')	{
				SnailNumber left=parseANumber();
				if (getNextChar()!=',') throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
				SnailNumber right=parseANumber();
				if (getNextChar()!=']') throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
				return new SnailPair(left,right);
			}	else if ((c>='0')&&(c<='9')) return new SnailLiteral(c-'0');
			else throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		List<SnailNumber> numbers=new ArrayList<>(lines.size());
		for (String l:lines)	{
			SnailParser parser=new SnailParser(l);
			numbers.add(parser.parse());
		}
		SnailNumber number=numbers.get(0);
		number.normalise();
		for (int i=1;i<numbers.size();++i)	{
			number=number.add(numbers.get(i));
			number.normalise();
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(number.getMagnitude());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
