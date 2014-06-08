package com.dan.wizardduel.spells;

import com.dan.wizardduel.R;
import com.dan.wizardduel.effects.Effect;

public class Ice extends Spell{
	public Ice(){
		super(R.drawable.ice, 3, "ice");
		this.damage = 20;
		this.manaCost=30;
		this.effect = new com.dan.wizardduel.effects.Slow();
		this.effectTarget = TARGET_OPPONENT;
	}
}
