package com.dan.wizardduel.spells;

import com.dan.wizardduel.R;
import com.dan.wizardduel.effects.Effect;

public class Shield extends Spell{
	public Shield(){
		super(R.drawable.shield, 8, "shield");
		this.damage = 0;
		this.manaCost = 10;
		this.effect = new com.dan.wizardduel.effects.Shield();
	}
}
