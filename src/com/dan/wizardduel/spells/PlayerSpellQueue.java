package com.dan.wizardduel.spells;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.dan.wizardduel.GameActivity;
import com.dan.wizardduel.GameFragment;
import com.dan.wizardduel.R;
import com.dan.wizardduel.combat.CombatController;
import com.dan.wizardduel.duelists.Player;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.todddavies.components.progressbar.ProgressWheel;

public class PlayerSpellQueue extends SpellQueue{
	
	public Player player;

	public PlayerSpellQueue(GameFragment gameFragment, Player player) {
		super(gameFragment);
		// TODO Auto-generated constructor stub
		
		spellIVs.add((ImageView) context.findViewById(R.id.preppedSpell0));
		spellIVs.add((ImageView) context.findViewById(R.id.preppedSpell1));
		spellIVs.add((ImageView) context.findViewById(R.id.preppedSpell2));
		
		spellIVs.get(0).setOnTouchListener(spellTouchListener(0));
		spellIVs.get(1).setOnTouchListener(spellTouchListener(1));
		spellIVs.get(2).setOnTouchListener(spellTouchListener(2));

		pw = (ProgressWheel) context.findViewById(R.id.pw_spinner);
		
		this.player = player;
	}
	
	public void castSpell(final int slot){
		final Spell spell = spells.get(slot);
		if(spell == null){
			return;
		}
		if(player.mana < spell.manaCost){
			return;
		}else{
			player.decMana(spell.manaCost);
		}
		
		final long castTime = spell.castTime*1000;
		Log.e("tag","cast spell: "+spell.damage);
		if(spell != null){
			castTimer = new CountDownTimer(castTime,10){

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					gameFragment.player.executeSpell(gameFragment.opponent,spell);
					removeSpell(slot);
					pw.setVisibility(View.GONE);
					pw.setProgress(0);
				
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
	
	public OnTouchListener spellTouchListener(final int slot){
		return new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					castSpell(slot);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					if(castTimer != null){
						castTimer.cancel();
						pw.setVisibility(View.GONE);
						pw.setProgress(0);
					}
				}
				return true;
			}
		};

	};

}
