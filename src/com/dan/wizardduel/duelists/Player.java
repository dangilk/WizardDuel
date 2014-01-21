package com.dan.wizardduel.duelists;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dan.wizardduel.GameFragment;
import com.dan.wizardduel.R;
import com.dan.wizardduel.combat.Effect;
import com.dan.wizardduel.spells.Spell;
import com.todddavies.components.progressbar.ProgressWheel;

public class Player extends Duelist {
		
	public interface Listener{
		public void onSpellPrepped(int slot,Spell spell);
	}

	public Player(GameFragment gameFragment) {
		super(gameFragment);
		//spellQueue = new PlayerSpellQueue(gameFragment,this);
		//spellQueue.setListener(this);
		healthBar = (StatMeterView) gameFragment.getView().findViewById(R.id.userHealthBar);
		manaBar = (StatMeterView) gameFragment.getView().findViewById(R.id.userManaBar);
		manaBar.color = Color.BLUE;
		image = R.drawable.blue;
		ImageView iv = (ImageView)gameFragment.getView().findViewById(R.id.userImage);
		iv.setImageResource(image);
		
		spellIVs.add((ImageView) context.findViewById(R.id.preppedSpell0));
		spellIVs.add((ImageView) context.findViewById(R.id.preppedSpell1));
		spellIVs.add((ImageView) context.findViewById(R.id.preppedSpell2));
		
		spellIVs.get(0).setOnTouchListener(spellTouchListener(0));
		spellIVs.get(1).setOnTouchListener(spellTouchListener(1));
		spellIVs.get(2).setOnTouchListener(spellTouchListener(2));
		
		buffLV = (LinearLayout)context.findViewById(R.id.userStatusBuffs);
		debuffLV = (LinearLayout)context.findViewById(R.id.userStatusDebuffs);

		pw = (ProgressWheel) context.findViewById(R.id.pw_spinner);
		addEffect(Effect.HASTE);
		addEffect(Effect.SHIELD);
		//addEffect(Effect.POISON);
		//addEffect(Effect.LOCK);
		//addEffect(Effect.HASTE);
		//call last
		postInit();
	}

	
	public void castSpell(final int slot){
		final Spell spell = spells.get(slot);
		Boolean valid = castSpellCheck(slot,spell);
		if(!valid){
			return;
		}
		final long castTime = (spell.castTime+this.effectAmplitude(Effect.SLOW)-this.effectAmplitude(Effect.HASTE))*1000;
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
