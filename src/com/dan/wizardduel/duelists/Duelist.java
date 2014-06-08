package com.dan.wizardduel.duelists;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dan.wizardduel.GameFragment;
import com.dan.wizardduel.MainActivity;
import com.dan.wizardduel.R;
import com.dan.wizardduel.combat.HpListener;
import com.dan.wizardduel.effects.Effect;
import com.dan.wizardduel.spells.Spell;
import com.todddavies.components.progressbar.ProgressWheel;

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
	private Listener listener = null;
	
	public String playerId = null;
	
	/*effects*/
	public LinearLayout buffLV;
	public LinearLayout debuffLV;
	public HashMap<Integer,Effect> effects = new HashMap<Integer,Effect>();
	public HashMap<Integer,ImageView> effectIcons = new HashMap<Integer,ImageView>();
	
	public interface Listener{
		public void onSpellPrepped(int slot,Spell spell);
		public void onSpellCasting(int slot);
        public void onSpellCastCanceled(int slot);
        public void onSpellExecuted(int slot, Spell spell);
	}
	
	protected Listener duelistListener = null;
	
	public void setListener(Listener l){
		duelistListener = l;
	}
	
	public Duelist(GameFragment gameFragment){
		incManaHandler.sendMessageDelayed(new Message(), 1000);
		this.gameFragment =  gameFragment;
		this.context = gameFragment.getView();
		spells.add(null);
		spells.add(null);
		spells.add(null);	
		
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
		
		if(spell.effect != null){
			if(spell.effectTarget == Spell.TARGET_OPPONENT){
				opponent.addEffect(spell.effect);
			}else if(spell.effectTarget == Spell.TARGET_SELF){
				this.addEffect(spell.effect);
			}
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
		if(hasEffect(Effect.SHIELD)){
			removeEffect(Effect.SHIELD);
			return;
		}
		health -= amt;
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
		if(castTimer != null){
			castTimer.cancel();
		}
		cleanupEffects();
	}

	
	public Integer randomFilledSlot(){
		Integer index = MainActivity.random.nextInt(QUEUE_SIZE);
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
			Spell spell = Spell.buildSpell(id);
			spells.set(slot, spell);
			spellIVs.get(slot).setImageResource(spell.icon);
			Log.e("tag","add spell");
			if(duelistListener != null){
				Log.e("tag","add spell call listener");
				duelistListener.onSpellPrepped(slot, spell);
			}
		}
	}
	
	public void addSpellAt(int slot, int id){
		Spell spell = Spell.buildSpell(id);
		spells.set(slot,spell);
		spellIVs.get(slot).setImageResource(spell.icon);
		if(duelistListener != null){
			duelistListener.onSpellPrepped(slot, spell);
		}
	}
	
	public void removeSpell(int slot){
		spells.set(slot,null);
		spellIVs.get(slot).setImageResource(R.drawable.common_signin_btn_icon_disabled_dark);
	}
	
	protected Boolean castSpellCheck(int slot, Spell spell){
		if(spell == null){
			return false;
		}
		if(hasEffect(Effect.LOCK) && slot == 0){
			return false;
		}
		if(this.mana < spell.manaCost){
			return false;
		}else{
			this.decMana(spell.manaCost);
		}
		return true;
	}
	
	public Boolean hasEffect(int effect){
		Effect e = effects.get(effect);
		if(e == null){
			return false;
		}
		if(e.isExpired()){
			removeEffect(effect);
			return false;
		}
		return true;
	}
	
	public int effectAmplitude(int effect){
		Effect e = effects.get(effect);
		if(e!=null && hasEffect(effect)){
			return e.amplitude;
		}
		return 0;
	}
	
	public void addEffect(Effect effect){
		int type = effect.type;
		//reset existing effect
		removeEffect(type);
		effects.put(type,effect);
		effect.startHandlers(this);
		ImageView iv = buildEffectIcon(effect);
		effectIcons.put(type, iv);
		if(effect.buff){
			buffLV.addView(iv);
		}else{
			debuffLV.addView(iv);
		}
		
	}
	
	public ImageView buildEffectIcon(Effect e){
		ImageView iv = new ImageView(context.getContext());
		iv.setImageResource(e.icon);
		iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
		iv.setAdjustViewBounds(true);
		return iv;
	}
	
	public void removeEffect(int effect){
		Effect e = effects.get(effect);
		effects.remove(effect);
		ImageView icon = effectIcons.get(effect);
		effectIcons.remove(effect);
		if(e != null){
			e.clearHandlers();
		}
		if(icon != null){
			buffLV.removeView(icon);
			debuffLV.removeView(icon);
		}
		
	}
	
	public void cleanupEffects(){
		for(Effect e : effects.values()){
			e.clearHandlers();
		}
	}
}
