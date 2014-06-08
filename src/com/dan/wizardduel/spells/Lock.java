package com.dan.wizardduel.spells;

import com.dan.wizardduel.R;
import com.dan.wizardduel.effects.Effect;

public class Lock extends Spell{
	public Lock(){
		super(R.drawable.lock, 7, "lock");
		this.damage = 10;
		this.manaCost = 20;
		this.effect = new com.dan.wizardduel.effects.Lock();
		this.effectTarget = TARGET_OPPONENT;
	}
}
