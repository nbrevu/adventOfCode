package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Advent15_222 {
	private final static String IN_FILE="2015/Advent221.txt";
	
	private final static int CHARACTER_HP=50;
	private final static int CHARACTER_MANA=500;
	
	private static enum Effect	{
		SHIELD	{
			@Override
			protected TurnResult takeEffect(CombatStatus status) {
				return new StandardResult(status.addEffect(this,-1));
			}
		},
		POISON	{
			@Override
			protected TurnResult takeEffect(CombatStatus status) {
				CombatStatus removeEffect=status.addEffect(this,-1);
				return removeEffect.damageBoss(3);
			}
		},
		RECHARGE	{
			@Override
			protected TurnResult takeEffect(CombatStatus status) {
				CombatStatus removeEffect=status.addEffect(this,-1);
				return new StandardResult(removeEffect.rechargeMana(101));
			}
		};
		protected abstract TurnResult takeEffect(CombatStatus status);
	}
	
	private static interface TurnResult	{
		public boolean doesPlayerWin();
		public boolean doesBossWin();
		public default boolean isFinal()	{
			return doesPlayerWin()||doesBossWin();
		}
		public CombatStatus getNextStatus();
		public int getManaCost();
	}
	private static class FixedResult implements TurnResult	{
		private final boolean isPlayerWinner;
		private final int manaCost;
		private FixedResult(boolean isPlayerWinner,int manaCost)	{
			this.isPlayerWinner=isPlayerWinner;
			this.manaCost=manaCost;
		}
		public static FixedResult playerWins(int manaCost)	{
			return new FixedResult(true,manaCost);
		}
		public static FixedResult bossWins(int manaCost)	{
			return new FixedResult(false,manaCost);
		}
		@Override
		public boolean doesPlayerWin() {
			return isPlayerWinner;
		}
		@Override
		public boolean doesBossWin() {
			return !isPlayerWinner;
		}
		@Override
		public CombatStatus getNextStatus() {
			return null;
		}
		@Override
		public int getManaCost()	{
			return manaCost;
		}
	}
	private static class StandardResult implements TurnResult	{
		private final CombatStatus nextStatus;
		public StandardResult(CombatStatus nextStatus)	{
			this.nextStatus=nextStatus;
		}
		@Override
		public boolean doesPlayerWin() {
			return false;
		}
		@Override
		public boolean doesBossWin() {
			return false;
		}
		@Override
		public CombatStatus getNextStatus() {
			return nextStatus;
		}
		@Override
		public int getManaCost()	{
			return nextStatus.manaSpent;
		}
	}
	
	private static class CombatStatus	{
		public final int bossHp;
		public final int playerHp;
		public final int bossDamage;
		public final int playerMana;
		public final int manaSpent;
		public final ObjIntMap<Effect> activeEffects;
		public CombatStatus(int bossHp,int playerHp,int bossDamage,int playerMana,int manaSpent,ObjIntMap<Effect> activeEffects)	{
			this.bossHp=bossHp;
			this.playerHp=playerHp;
			this.bossDamage=bossDamage;
			this.playerMana=playerMana;
			this.manaSpent=manaSpent;
			this.activeEffects=HashObjIntMaps.newImmutableMap(activeEffects);
		}
		public CombatStatus(int bossHp,int playerHp,int bossDamage,int playerMana)	{
			this(bossHp,playerHp,bossDamage,playerMana,0,HashObjIntMaps.newMutableMap());
		}
		@Override
		public boolean equals(Object other)	{
			CombatStatus cs=(CombatStatus)other;
			return (bossHp==cs.bossHp)&&(playerHp==cs.playerHp)&&(bossDamage==cs.bossDamage)&&(playerMana==cs.playerMana)&&(manaSpent==cs.manaSpent)&&activeEffects.equals(cs.activeEffects);
		}
		@Override
		public int hashCode()	{
			return Objects.hash(bossHp+playerHp+bossDamage+playerMana+manaSpent,activeEffects);
		}
		public CombatStatus addEffect(Effect effect,int additionalTurns)	{
			ObjIntMap<Effect> effects=HashObjIntMaps.newMutableMap(activeEffects);
			if (effects.addValue(effect,additionalTurns)<=0)	{
				effects.removeAsInt(effect);
			}
			return new CombatStatus(bossHp,playerHp,bossDamage,playerMana,manaSpent,effects);
		}
		public CombatStatus rechargeMana(int mana)	{
			return new CombatStatus(bossHp,playerHp,bossDamage,playerMana+mana,manaSpent,activeEffects);
		}
		public CombatStatus spendMana(int mana)	{
			return new CombatStatus(bossHp,playerHp,bossDamage,playerMana-mana,manaSpent+mana,activeEffects);
		}
		public TurnResult damageBoss(int damage)	{
			if (bossHp<=damage) return FixedResult.playerWins(manaSpent);
			else return new StandardResult(new CombatStatus(bossHp-damage,playerHp,bossDamage,playerMana,manaSpent,activeEffects));
		}
		public TurnResult damagePlayer(int damage)	{
			if (playerHp<=damage) return FixedResult.bossWins(manaSpent);
			else return new StandardResult(new CombatStatus(bossHp,playerHp-damage,bossDamage,playerMana,manaSpent,activeEffects));
		}
		public TurnResult applyEffects()	{
			Set<Effect> effects=activeEffects.keySet();
			CombatStatus status=this;
			for (Effect e:effects) if (isEffectActive(e))	{
				TurnResult result=e.takeEffect(status);
				if (result.isFinal()) return result;
				else status=result.getNextStatus();
			}
			return new StandardResult(status);
		}
		public TurnResult playerTurn(Spell spell)	{
			return spell.cast(spendMana(spell.manaCost));
		}
		public TurnResult bossTurnWithEffects()	{
			TurnResult afterEffects=applyEffects();
			if (afterEffects.isFinal()) return afterEffects;
			int damage=bossDamage;
			if (activeEffects.containsKey(Effect.SHIELD))	{
				damage-=7;
				if (damage<=0) damage=1;
			}
			TurnResult afterBoss=afterEffects.getNextStatus().damagePlayer(damage);
			if (afterBoss.isFinal()) return afterBoss;
			TurnResult afterHardModePenalty=afterBoss.getNextStatus().damagePlayer(1);
			if (afterHardModePenalty.isFinal()) return afterHardModePenalty;
			else return afterHardModePenalty.getNextStatus().applyEffects();
		}
		public boolean isEffectActive(Effect effect)	{
			return activeEffects.getOrDefault(effect,0)>0;
		}
	}
	
	private enum Spell	{
		MAGIC_MISSILE(53)	{
			@Override
			public boolean canCast(CombatStatus status) {
				return hasEnoughMana(status);
			}
			@Override
			public TurnResult cast(CombatStatus status)	{
				return status.damageBoss(4);
			}
		},
		DRAIN(73)	{
			@Override
			public boolean canCast(CombatStatus status) {
				return hasEnoughMana(status);
			}
			@Override
			public TurnResult cast(CombatStatus status)	{
				TurnResult damageBoss=status.damageBoss(2);
				if (damageBoss.isFinal()) return damageBoss;
				else return damageBoss.getNextStatus().damagePlayer(-2); 
			}
		},
		SHIELD(113)	{
			@Override
			public boolean canCast(CombatStatus status) {
				return hasEnoughMana(status)&&!status.isEffectActive(Effect.SHIELD);
			}
			@Override
			public TurnResult cast(CombatStatus status)	{
				return new StandardResult(status.addEffect(Effect.SHIELD,6));
			}
		},
		POISON(173)	{
			@Override
			public boolean canCast(CombatStatus status) {
				return hasEnoughMana(status)&&!status.isEffectActive(Effect.POISON);
			}
			@Override
			public TurnResult cast(CombatStatus status)	{
				return new StandardResult(status.addEffect(Effect.POISON,6));
			}
		},
		RECHARGE(229)	{
			@Override
			public boolean canCast(CombatStatus status) {
				return hasEnoughMana(status)&&!status.isEffectActive(Effect.RECHARGE);
			}
			@Override
			public TurnResult cast(CombatStatus status)	{
				return new StandardResult(status.addEffect(Effect.RECHARGE,5));
			}
		};
		public final int manaCost;
		private Spell(int manaCost)	{
			this.manaCost=manaCost;
		}
		public abstract boolean canCast(CombatStatus status);
		public abstract TurnResult cast(CombatStatus status);
		protected boolean hasEnoughMana(CombatStatus status)	{
			return status.playerMana>=manaCost;
		}
	}
	
	private static class CombatStatusSearchSpace	{
		private final NavigableMap<Integer,Set<CombatStatus>> queue;
		public CombatStatusSearchSpace(CombatStatus status)	{
			queue=new TreeMap<>();
			addStatus(status);
		}
		public void addStatus(CombatStatus status)	{
			Set<CombatStatus> statusByCost=queue.computeIfAbsent(status.manaSpent,(Integer unused)->new HashSet<>());
			statusByCost.add(status);
		}
		public CombatStatus poll()	{
			Map.Entry<Integer,Set<CombatStatus>> entry=queue.firstEntry();
			Set<CombatStatus> subQueue=entry.getValue();
			CombatStatus result=subQueue.iterator().next();
			subQueue.remove(result);
			if (subQueue.isEmpty()) queue.remove(entry.getKey());
			return result;
		}
	}
	
	private static OptionalInt min(OptionalInt a,int b)	{
		return (a.isEmpty()||a.getAsInt()>b)?OptionalInt.of(b):a;
	}
	
	private static int searchLeastMana(CombatStatus initialStatus)	{
		Spell[] spells=Spell.values();
		CombatStatusSearchSpace searching=new CombatStatusSearchSpace(initialStatus);
		OptionalInt result=OptionalInt.empty();
		for (;;)	{
			CombatStatus status=searching.poll();
			if (result.isPresent()&&(status.manaSpent>=result.getAsInt())) return result.getAsInt();
			for (Spell s:spells) if (s.canCast(status))	{
				TurnResult newStatus=status.playerTurn(s);
				if (newStatus.isFinal())	{
					if (newStatus.doesPlayerWin()) result=min(result,newStatus.getManaCost());
					continue;
				}
				TurnResult finalStatus=newStatus.getNextStatus().bossTurnWithEffects();
				if (finalStatus.isFinal())	{
					if (finalStatus.doesPlayerWin()) result=min(result,finalStatus.getManaCost());
					continue;
				}
				searching.addStatus(finalStatus.getNextStatus());
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		OptionalInt bossHitPoints=OptionalInt.empty();
		OptionalInt bossDamage=OptionalInt.empty();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		for (String line:lines)	{
			String[] split=line.split(":");
			if (split.length!=2) continue;
			int value=Integer.parseInt(split[1].trim());
			switch (split[0])	{
				case "Hit Points":bossHitPoints=OptionalInt.of(value);break;
				case "Damage":bossDamage=OptionalInt.of(value);break;
				default:throw new IllegalArgumentException("ICH KANN "+split[0]+" NICHT VERSTEHEN!!!!!");
			}
		}
		if (bossHitPoints.isEmpty()||bossDamage.isEmpty()) throw new IllegalArgumentException("NICHT GENUG!!!!!");
		CombatStatus initialStatus=new CombatStatus(bossHitPoints.getAsInt(),CHARACTER_HP,bossDamage.getAsInt(),CHARACTER_MANA);
		int result=searchLeastMana(initialStatus);
		System.out.println(result);
	}
}
