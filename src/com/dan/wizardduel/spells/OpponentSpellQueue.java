package com.dan.wizardduel.spells;

import java.util.ArrayList;

import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dan.wizardduel.GameActivity;
import com.dan.wizardduel.GameFragment;
import com.dan.wizardduel.R;
import com.dan.wizardduel.combat.CombatController;
import com.dan.wizardduel.duelists.Duelist;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.todddavies.components.progressbar.ProgressWheel;

public class OpponentSpellQueue extends SpellQueue{

	public Duelist opponent;
	public ArrayList<ProgressWheel> spinWheels = new ArrayList<ProgressWheel>();
	public GameFragment gameFragment;
	public View context;
	public OpponentSpellQueue(GameFragment gameFragment,Duelist opponent) {
		super(gameFragment);
		context = gameFragment.getView();
		// TODO Auto-generated constructor stub
		spellIVs.add((ImageView) context.findViewById(R.id.opponentPreppedSpell0));
		spellIVs.add((ImageView) context.findViewById(R.id.opponentPreppedSpell1));
		spellIVs.add((ImageView) context.findViewById(R.id.opponentPreppedSpell2));
		
		this.opponent = opponent;
		this.gameFragment = gameFragment;
		spinWheels.add((ProgressWheel)context.findViewById(R.id.opponent_spinner0));
		spinWheels.add((ProgressWheel)context.findViewById(R.id.opponent_spinner1));
		spinWheels.add((ProgressWheel)context.findViewById(R.id.opponent_spinner2));
	}
	
	public void castSpell(final int slot){
		final Spell spell = spells.get(slot);
		if(spell == null){
			return;
		}
		if(opponent.mana < spell.manaCost){
			return;
		}else{
			opponent.decMana(spell.manaCost);
		}
		final ProgressWheel pw = spinWheels.get(slot);
		
		final long castTime = spell.castTime*1000;
		if(spell != null){
			castTimer = new CountDownTimer(castTime,10){

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					gameFragment.opponent.executeSpell(gameFragment.player,spell);
					removeSpell(slot);
					pw.setVisibility(View.GONE);
				}

				@Override
				public void onTick(long millisUntilFinished) {
					// TODO Auto-generated method stub
					pw.incrementProgress();
					long elapsed = castTime - millisUntilFinished;
					pw.setProgress((int)(elapsed*360/castTime)+1);
				}

				
			};
			castTimer.start();
			pw.setVisibility(View.VISIBLE);
		}
	}

}
