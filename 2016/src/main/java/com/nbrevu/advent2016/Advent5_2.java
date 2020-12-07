package com.nbrevu.advent2016;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

public class Advent5_2 {
	private final static String SECRET_KEY="uqwqemis";
	
	private static class PasswordDigit	{
		public final int position;
		public final byte untranslatedByte;
		public PasswordDigit(int position,byte untranslatedByte)	{
			this.position=position;
			this.untranslatedByte=untranslatedByte;
		}
	}
	
	private static PasswordDigit checkPrefix(byte[] hash)	{
		if (hash.length<4) return null;
		if ((hash[0]==0)&&(hash[1]==0)&&((hash[2]&0xf0)==0))	{
			int position=hash[2]&0x0f;
			int digit=(hash[3]>>4)&0x0f;
			return new PasswordDigit(position,(byte)digit);
		}
		else return null;
	}
	
	private static char translate(byte b)	{
		if ((0<=b)&&(b<=9)) return (char)('0'+b);
		else return (char)('a'+(b-10));
	}
	
	private static class PasswordStorage	{
		private final char[] characters;
		private final BitSet found;
		public PasswordStorage()	{
			characters=new char[8];
			found=new BitSet(8);
		}
		public void addPasswordDigit(PasswordDigit d)	{
			if (d.position>=8) return;
			if (!found.get(d.position))	{
				characters[d.position]=translate(d.untranslatedByte);
				found.set(d.position);
			}
		}
		public boolean isComplete()	{
			return found.cardinality()==8;
		}
		@Override
		public String toString()	{
			return String.copyValueOf(characters);
		}
	}

	public static void main(String[] args) throws IOException,NoSuchAlgorithmException	{
		MessageDigest md5=MessageDigest.getInstance("MD5");
		byte[] constantBytes=SECRET_KEY.getBytes();
		PasswordStorage password=new PasswordStorage();
		for (long i=1;;++i)	{
			md5.update(constantBytes);
			md5.update(Long.toString(i).getBytes());
			byte[] hash=md5.digest();
			PasswordDigit digit=checkPrefix(hash);
			if (digit!=null)	{
				password.addPasswordDigit(digit);
				if (password.isComplete()) break;
			}
			md5.reset();
		}
		System.out.println(password.toString());
	}
}
