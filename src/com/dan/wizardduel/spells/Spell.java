package com.dan.wizardduel.spells;

import java.util.ArrayList;
import java.util.Map;

import android.util.Log;

import com.dan.wizardduel.R;
import com.dan.wizardduel.combat.Effect;

public class Spell {

	public static final int TARGET_SELF = 0;
	public static final int TARGET_OPPONENT= 1;
	public String name = "";
	public int castTime = 5;
	public String id;
	public int effect = 0;
	public int effectTarget = TARGET_SELF;
	public int damage;
	public int target = TARGET_OPPONENT;
	public int manaCost = 20;
	public int icon = R.drawable.fire;
	public static String[] allSpells = {
			"heal",
			"fire",
			"ice",
			"haste",
			"poison",
			"counter",
			"lock",
			"shield"
	};

	
	public Spell(String id){
		this.name = id;
		this.id = id;
		Log.e("tag","spell: "+id);
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
		Log.e("tag","spell: "+this.damage);
	}
}
