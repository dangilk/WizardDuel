package com.dan.wizardduel.spells;

import java.util.ArrayList;

import com.dan.wizardduel.GameActivity;
import com.dan.wizardduel.GameFragment;
import com.dan.wizardduel.R;
import com.dan.wizardduel.R.drawable;
import com.dan.wizardduel.R.id;
import com.dan.wizardduel.combat.CombatController;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.todddavies.components.progressbar.ProgressWheel;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class SpellQueue {

	public static final int QUEUE_SIZE = 3;
	public ArrayList<Spell> spells = new ArrayList<Spell>();
	public ArrayList<ImageView> spellIVs = new ArrayList<ImageView>();
	public String owner = "player";
	public GameFragment gameFragment;
	public View context;
	public Boolean isCasting = false;
	public int castingSpell = 0;
	public CountDownTimer castTimer = null;
	public ProgressWheel pw = null;
	
	public SpellQueue(GameFragment gameFragment){
		this.gameFragment =  gameFragment;
		this.context = gameFragment.getView();
		spells.add(null);
		spells.add(null);
		spells.add(null);	
	}
	
	public void cleanup(){
		if(castTimer != null){
			castTimer.cancel();
		}
	}
	
	public Integer randomFilledSlot(){
		Integer index = GameActivity.random.nextInt(QUEUE_SIZE);
		int count = 0;
		while(spells.get(index) == null && count < QUEUE_SIZE){
			index = (index+1)%QUEUE_SIZE;
			count++;
		}
		if(count >= QUEUE_SIZE){
			return null;
		}
		return index;
	}
	
	public Boolean isFull(){
		for(Spell spell : spells){
			if(spell == null){
				return false;
			}
		}
		if(spells.size()<QUEUE_SIZE){
			return false;
		}
		return true;
	}
	
	public int nextOpenSlot(){
		for(int i=0;i<QUEUE_SIZE;i++){
			if (spells.get(i) == null){
				return i;
			}
		}
		return -1;
	}
	
	public void addSpell(String id){
		
		int slot = nextOpenSlot();
		if(slot >= 0){
			Spell spell = new Spell(id);
			spells.set(slot, spell);
			spellIVs.get(slot).setImageResource(spell.icon);
		}
	}
	
	public void removeSpell(int slot){
		spells.set(slot,null);
		spellIVs.get(slot).setImageResource(R.drawable.common_signin_btn_icon_disabled_dark);
	}
	
	
	
	
}
