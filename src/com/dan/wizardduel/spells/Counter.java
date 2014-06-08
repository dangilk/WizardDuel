package com.dan.wizardduel.spells;

import com.dan.wizardduel.R;

public class Counter extends Spell{
	public Counter(){
		super(R.drawable.counter, 6, "counter");
		this.damage = 0;
		this.manaCost = 20;
	}
}
