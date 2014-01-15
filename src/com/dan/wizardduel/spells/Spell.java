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
	public ArrayList<Effect> effects;
	public int damage;
	public int target = TARGET_OPPONENT;
	public int manaCost = 20;
	public int icon;
	public static String[] allSpells = {
			"heal",
			"fire"
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
		}
		Log.e("tag","spell: "+this.damage);
	}
}
