package com.dan.wizardduel.spells;

import com.dan.wizardduel.R;
import com.dan.wizardduel.effects.Effect;

public class Poison extends Spell{
	public Poison(){
		super(R.drawable.poison, 5, "poison");
		this.damage = 10;
		this.manaCost=30;
		this.effect = new com.dan.wizardduel.effects.Poison();
		this.effectTarget = TARGET_OPPONENT;
	}
}
