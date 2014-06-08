package com.dan.wizardduel.spells;

import com.dan.wizardduel.R;
import com.dan.wizardduel.effects.Effect;

public class Haste extends Spell{
	public Haste(){
		super(R.drawable.haste, 4, "haste");
		this.damage = 0;
		this.manaCost=20;
		this.effect = new com.dan.wizardduel.effects.Haste();
		this.target = TARGET_SELF;
	}
}
