package com.dan.wizardduel.spells;

import java.util.Collection;

import utils.BiMap;
import android.util.Log;

import com.dan.wizardduel.MainActivity;
import com.dan.wizardduel.R;
import com.dan.wizardduel.effects.Effect;

public abstract class Spell {

	public static final int TARGET_SELF = 0;
	public static final int TARGET_OPPONENT= 1;
	public int castTime = 5;
	//public int effect = 0;
	public int effectTarget = TARGET_SELF;
	public int damage;
	public int target = TARGET_OPPONENT;
	public int manaCost = 20;
	public final int icon;
	public final int intId;
	public final String id;
	public Effect effect = null;
	
	public Spell(int icon, int intId, String id){
		this.icon = icon;
		this.intId = intId;
		this.id = id;
	}

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
	
	public static Spell buildSpell(int id){
		switch(id){
		case 1:
			return new Heal();
		case 2:
			return new Fire();
		case 3:
			return new Ice();
		case 4:
			return new Haste();
		case 5:
			return new Poison();
		case 6:
			return new Counter();
		case 7:
			return new Lock();
		default:
			return new Shield();
		}
	}
	
	public static Spell buildSpell(String id){
		return buildSpell(spells.get(id));
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
}
