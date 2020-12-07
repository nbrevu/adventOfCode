package com.nbrevu.advent2016;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;

public class Advent14_2 {
	private final static String SECRET_KEY="yjdafjpo";
	
	private static class HashCalculator	{
		private final byte[] constantBytes;
		private final MessageDigest md5;
		public HashCalculator(String key) throws NoSuchAlgorithmException	{
			constantBytes=key.getBytes();
			md5=MessageDigest.getInstance("MD5");
		}
		public String getMd5(long i)	{
			md5.update(constantBytes);
			md5.update(Long.toString(i).getBytes());
			byte[] hash=md5.digest();
			md5.reset();
			String result=bytesToString(hash);
			for (int j=0;j<2016;++j)	{
				md5.update(result.getBytes());
				hash=md5.digest();
				md5.reset();
				result=bytesToString(hash);
			}
			return result;
		}
		private static String bytesToString(byte[] bytes)	{
			StringBuilder result=new StringBuilder();
			for (int i=0;i<bytes.length;++i)	{
				result.append(digitToChar((bytes[i]>>4)&0x0f));
				result.append(digitToChar(bytes[i]&0x0f));
			}
			return result.toString();
		}
		private static char digitToChar(int b)	{
			return (char)(((b<=9)?'0':('a'-0x0a))+b);
		}
	}
	
	private static interface HashListener	{
		// When returns false, the listener will be removed.
		public boolean considerHash(long i,String hash);
	}
	
	public abstract static class SecondaryKeySearcher implements HashListener	{
		private final static CharObjMap<String> SUBSTRING_CACHE=HashCharObjMaps.newMutableMap();
		private final long possibleKey;
		private final String substring;
		private final long limit;
		public SecondaryKeySearcher(long possibleKey,char digit,long limit)	{
			this.possibleKey=possibleKey;
			this.substring=getSubstring(digit);
			this.limit=limit;
		}
		private static String getSubstring(char digit)	{
			return SUBSTRING_CACHE.computeIfAbsent(digit,(char c)->	{
				StringBuilder result=new StringBuilder();
				for (int i=0;i<5;++i) result.append(c);
				return result.toString();
			});
		}
		@Override
		public boolean considerHash(long index,String hash) {
			if (hash.contains(substring))	{
				onSuccess(possibleKey);
				return false;
			}
			return index<limit;
		}
		protected abstract void onSuccess(long key);
	}
	
	public static abstract class MainKeySearcher implements HashListener	{
		public MainKeySearcher()	{}
		@Override
		public boolean considerHash(long index,String hash) {
			char lastChar='\'';
			int counter=0;
			for (int i=0;i<hash.length();++i)	{
				char c=hash.charAt(i);
				if (c==lastChar)	{
					++counter;
					if (counter>=3)	{
						onPossibleKey(index,c);
						break;
					}
				}	else	{
					lastChar=c;
					counter=1;
				}
			}
			return !isFinished(index);
		}
		protected abstract void onPossibleKey(long index,char digit);
		protected abstract boolean isFinished(long index);
	}
	
	private static class HashIterator	{
		private final HashCalculator calculator;
		private final Set<HashListener> listeners;
		private NavigableSet<Long> keysFound;
		private Optional<HashListener> additionalStorage;
		public HashIterator(String key) throws NoSuchAlgorithmException	{
			calculator=new HashCalculator(key);
			listeners=new HashSet<>();
			keysFound=new TreeSet<>();
			listeners.add(createMainListener());
			additionalStorage=Optional.empty();
		}
		private HashListener createSecondaryListener(long possibleKey,char digit,long limit)	{
			return new SecondaryKeySearcher(possibleKey,digit,limit) {
				@Override
				protected void onSuccess(long key) {
					keysFound.add(key);
				}
			};
		}
		private HashListener createMainListener()	{
			return new MainKeySearcher() {
				@Override
				protected void onPossibleKey(long index,char digit) {
					additionalStorage=Optional.of(createSecondaryListener(index,digit,index+1000));
				}
				@Override
				protected boolean isFinished(long index) {
					if (keysFound.size()<64) return false;
					return keysFound.last()+1000<=index;
				}
			};
		}
		public void run()	{
			Set<HashListener> toRemove=new HashSet<>();
			for (long i=1;!listeners.isEmpty();++i)	{
				String hash=calculator.getMd5(i);
				for (HashListener listener:listeners) if (!listener.considerHash(i,hash)) toRemove.add(listener);
				listeners.removeAll(toRemove);
				if (additionalStorage.isPresent())	{
					listeners.add(additionalStorage.get());
					additionalStorage=Optional.empty();
				}
				toRemove.clear();
			}
		}
		public long getResult()	{
			List<Long> sortedKeys=new ArrayList<>(keysFound);
			return sortedKeys.get(63).longValue();
		}
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException	{
		HashIterator iterator=new HashIterator(SECRET_KEY);
		iterator.run();
		System.out.println(iterator.getResult());
	}
}
