package com.nbrevu.advent2016;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class Advent5_1 {
	private final static String SECRET_KEY="uqwqemis";
	
	private static Optional<Byte> checkPrefix(byte[] hash)	{
		if (hash.length<3) return Optional.empty();
		if ((hash[0]==0)&&(hash[1]==0)&&((hash[2]&0xf0)==0)) return Optional.of((byte)(hash[2]&0x0f));
		else return Optional.empty();
	}
	private static char translate(byte b)	{
		if ((0<=b)&&(b<=9)) return (char)('0'+b);
		else return (char)('a'+(b-10));
	}

	public static void main(String[] args) throws IOException,NoSuchAlgorithmException	{
		MessageDigest md5=MessageDigest.getInstance("MD5");
		byte[] constantBytes=SECRET_KEY.getBytes();
		StringBuilder result=new StringBuilder();
		int found=0;
		for (long i=1;;++i)	{
			md5.update(constantBytes);
			md5.update(Long.toString(i).getBytes());
			byte[] hash=md5.digest();
			Optional<Byte> digit=checkPrefix(hash);
			if (digit.isPresent())	{
				result.append(translate(digit.get()));
				++found;
				if (found>=8) break;
			}
			md5.reset();
		}
		System.out.println(result.toString());
	}
}
