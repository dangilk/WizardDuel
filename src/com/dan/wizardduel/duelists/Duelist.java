package com.dan.wizardduel.duelists;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dan.wizardduel.GameActivity;
import com.dan.wizardduel.GameFragment;
import com.dan.wizardduel.combat.CombatController;
import com.dan.wizardduel.combat.HpListener;
import com.dan.wizardduel.spells.Spell;
import com.dan.wizardduel.spells.SpellQueue;
import com.google.example.games.basegameutils.BaseGameActivity;

public class Duelist {
	private static final int maxHealth = 100;
	int health=100;
	int maxMana=100;
	public int mana=100;
	String name="Blue Wizard";
	int image;
	public ArrayList<HpListener> hpListeners = new ArrayList<HpListener>();
	public StatMeterView healthBar;
	public StatMeterView manaBar;
	
	public Duelist(GameFragment gameFragment){
		incManaHandler.sendMessageDelayed(new Message(), 1000);
	}
	
	public void clearHpListeners(){
		hpListeners.clear();
	}
	
	protected void postInit(){
		
	}
	
	public void executeSpell(Duelist opponent, Spell spell){
		if(spell.target == Spell.TARGET_OPPONENT){
			opponent.decHp(spell.damage);
		}else if(spell.target == Spell.TARGET_SELF){
			this.decHp(spell.damage);
		}
	}
	
	public void addHpListener(HpListener listener){
		hpListeners.add(listener);
	}
	
	public void notifyHpListeners(){
		for(HpListener h: hpListeners){
			h.hpUpdated(health);
		}
	}
	
	public void decHp(int amt){
		health -= amt;
		if (health <= 0){
			health = 0;
		}else if(health >= maxHealth){
			health = maxHealth;
		}
		notifyHpListeners();
		healthBar.setPercentFull(health*100/maxHealth);
	}
	
	public void incHp(int amt){
		health += amt;
		if (health <= 0){
			health = 0;
		}else if(health >= maxHealth){
			health = maxHealth;
		}
		notifyHpListeners();
		healthBar.setPercentFull(health*100/maxHealth);
	}
	
	public void decMana(int amt) {
		// TODO Auto-generated method stub
		mana -= amt;
		if(mana <= 0){
			mana = 0;
		}
		manaBar.setPercentFull(mana*100/maxMana);
	}
	
	public void incMana(int amt){
		mana+= amt;
		if(mana >= maxMana){
			mana = maxMana;
		}
		manaBar.setPercentFull(mana*100/maxMana);
	}
	
	private Handler incManaHandler = new Handler(){
		public void handleMessage(Message m){
			incMana(3);
			incManaHandler.sendMessageDelayed(new Message(), 1000);
		}
	};
	
	public void cleanup(){
		incManaHandler.removeCallbacksAndMessages(null);
		clearHpListeners();
	}
}
