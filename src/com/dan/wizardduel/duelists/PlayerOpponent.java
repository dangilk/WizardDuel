package com.dan.wizardduel.duelists;

import android.os.CountDownTimer;
import android.view.View;

import com.dan.wizardduel.GameFragment;
import com.dan.wizardduel.combat.Effect;
import com.dan.wizardduel.spells.Spell;
import com.todddavies.components.progressbar.ProgressWheel;

public class PlayerOpponent extends Opponent{
	

	public PlayerOpponent(GameFragment gameFragment,String id) {
		super(gameFragment);
		playerId = id;
	}
	
	public void castSpell(final int slot){
		final Spell spell = spells.get(slot);
		Boolean valid = castSpellCheck(slot,spell);
		if(!valid){
			return;
		}
		final ProgressWheel pw = spinWheels.get(slot);
		
		final long castTime = (spell.castTime+this.effectAmplitude(Effect.SLOW)-this.effectAmplitude(Effect.HASTE))*1000;
		if(spell != null){
			castTimer = new CountDownTimer(castTime,10){

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					//gameFragment.opponent.executeSpell(gameFragment.player,spell);
					//removeSpell(slot);
					//pw.setVisibility(View.GONE);
					pw.setProgress(360);
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
	
	public void castSpellCanceled(int slot){
		final ProgressWheel pw = spinWheels.get(slot);
		castTimer.cancel();
		pw.setVisibility(View.GONE);
		pw.setProgress(0);
	}
	
	public void executeSpell(Spell spell){
		gameFragment.opponent.executeSpell(gameFragment.player,spell);
	}

}
