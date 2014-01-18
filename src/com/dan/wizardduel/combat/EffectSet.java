package com.dan.wizardduel.combat;

import java.util.HashMap;

import com.dan.wizardduel.duelists.Duelist;

public class EffectSet {
	public HashMap<Integer,Effect> effects = new HashMap<Integer,Effect>();
	Duelist duelist;
	
	public EffectSet(Duelist duelist){
		this.duelist = duelist;
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
	
	public void addEffect(int effect){
		Effect e = new Effect(effect);
		effects.put(effect,e);
		if(effect == Effect.POISON){
			e.startPoison(duelist);
		}
	}
	
	public void removeEffect(int effect){
		effects.remove(effect);
	}
}
