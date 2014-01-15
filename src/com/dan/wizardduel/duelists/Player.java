package com.dan.wizardduel.duelists;

import android.graphics.Color;
import android.widget.ImageView;

import com.dan.wizardduel.GameActivity;
import com.dan.wizardduel.GameFragment;
import com.dan.wizardduel.R;
import com.dan.wizardduel.combat.CombatController;
import com.dan.wizardduel.spells.PlayerSpellQueue;
import com.dan.wizardduel.spells.SpellQueue;
import com.google.example.games.basegameutils.BaseGameActivity;

public class Player extends Duelist {
	
	public PlayerSpellQueue spellQueue;

	public Player(GameFragment gameFragment) {
		// TODO Auto-generated constructor stub
		super(gameFragment);
		spellQueue = new PlayerSpellQueue(gameFragment,this);
		healthBar = (StatMeterView) gameFragment.getView().findViewById(R.id.userHealthBar);
		manaBar = (StatMeterView) gameFragment.getView().findViewById(R.id.userManaBar);
		manaBar.color = Color.BLUE;
		image = R.drawable.blue;
		ImageView iv = (ImageView)gameFragment.getView().findViewById(R.id.userImage);
		iv.setImageResource(image);
		postInit();
	}

	

}
