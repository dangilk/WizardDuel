package com.dan.wizardduel.spells;

import java.util.Collection;

import utils.BiMap;
import android.util.Log;

import com.dan.wizardduel.MainActivity;
import com.dan.wizardduel.R;
import com.dan.wizardduel.combat.Effect;

public class Spell {

	public static final int TARGET_SELF = 0;
	public static final int TARGET_OPPONENT= 1;
	public int castTime = 5;
	public String id;
	public int effect = 0;
	public int effectTarget = TARGET_SELF;
	public int damage;
	public int target = TARGET_OPPONENT;
	public int manaCost = 20;
	public int icon = R.drawable.fire;
	public int intId = 0;

	public static final BiMap<String,Integer> spells;
	static{
		spells = new BiMap<String,Integer>();
		spells.put("heal", 1);
		spells.put("fire",2);
		spells.put("ice", 3);
		spells.put("haste",4);
		spells.put("poison", 5);
		spells.put("counter", 6);
		spells.put("lock", 7);
		spells.put("shield",8);
	}
	
	private void initSpell(String id){
		if(id.equalsIgnoreCase("fire")){
			this.damage = 50;
			this.manaCost = 50;
			this.icon = R.drawable.fire;
		}else if(id.equalsIgnoreCase("heal")){
			this.damage = -20;
			this.target = TARGET_SELF;
			this.manaCost = 20;
			this.icon = R.drawable.heal;
		}else if(id.equalsIgnoreCase("ice")){
			this.damage = 20;
			this.manaCost=30;
			this.effect = Effect.SLOW;
			this.effectTarget = TARGET_OPPONENT;
			this.icon = R.drawable.ice;
		}else if(id.equalsIgnoreCase("haste")){
			this.damage = 0;
			this.manaCost=20;
			this.effect = Effect.HASTE;
			this.target = TARGET_SELF;
			this.icon = R.drawable.haste;
		}else if(id.equalsIgnoreCase("poison")){
			this.damage = 10;
			this.manaCost=30;
			this.effect = Effect.POISON;
			this.effectTarget = TARGET_OPPONENT;
			this.icon = R.drawable.poison;
		}else if(id.equalsIgnoreCase("counter")){
			this.damage = 0;
			this.manaCost = 20;
			this.icon = R.drawable.counter;
		}else if(id.equalsIgnoreCase("lock")){
			this.damage = 10;
			this.manaCost = 20;
			this.effect = Effect.LOCK;
			this.effectTarget = TARGET_OPPONENT;
			this.icon = R.drawable.lock;
		}else if(id.equalsIgnoreCase("shield")){
			this.damage = 0;
			this.manaCost = 10;
			this.effect = Effect.SHIELD;
			this.icon = R.drawable.shield;
		}
	}
	
	public static String randomSpell(){
		int rand = MainActivity.random.nextInt(Spell.spells.size());
		Collection<String> ids = spells.keys();
		int i=0;
		for(String s: ids){
			if(i==rand){
				return s;
			}
			i++;
		}
		return null;
	}
	
	public Spell(int intId){
		Log.e("tag","init spell: "+intId);
		this.intId = intId;
		this.id = spells.getKey(intId);
		initSpell(id);
	}

	
	public Spell(String id){
		this.id = id;
		this.intId = spells.get(id);
		initSpell(id);
	}
	
	
}
