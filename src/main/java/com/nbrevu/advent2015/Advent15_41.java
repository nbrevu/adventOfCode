package com.nbrevu.advent2015;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Advent15_41 {
	private final static String SECRET_KEY="ckczppom";
	
	private static boolean checkPrefix(byte[] hash)	{
		if (hash.length<3) return false;
		return (hash[0]==0)&&(hash[1]==0)&&((hash[2]&0xf0)==0);
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException	{
		MessageDigest md5=MessageDigest.getInstance("MD5");
		byte[] constantBytes=SECRET_KEY.getBytes();
		for (long i=1;;++i)	{
			md5.update(constantBytes);
			md5.update(Long.toString(i).getBytes());
			byte[] hash=md5.digest();
			if (checkPrefix(hash))	{
				System.out.println(i);
				return;
			}
			md5.reset();
		}
	}
}
