package com.dan.wizardduel.spells;

import com.dan.wizardduel.R;

public class Heal extends Spell{

	public Heal() {
		super(R.drawable.heal, 1, "heal");
		this.damage = -20;
		this.target = TARGET_SELF;
		this.manaCost = 20;
	}

}
