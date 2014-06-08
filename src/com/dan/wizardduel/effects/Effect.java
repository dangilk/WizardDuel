package com.dan.wizardduel.effects;

import android.os.Handler;
import android.os.Message;

import com.dan.wizardduel.R;
import com.dan.wizardduel.duelists.Duelist;

public abstract class Effect {

	public final int type;
	public final long expires;
	public final long duration;
	public String name;
	public final Boolean buff;
	public int amplitude=3;
	public final int icon;
	
	public static final int SLOW=1;
	public static final int HASTE=2;
	public static final int POISON=3;
	public static final int LOCK=4;
	public static final int SHIELD=5;
	
	public Effect(int type, boolean buff, int icon, long duration){
		long now = System.currentTimeMillis();
		this.type = type;
		this.buff = buff;
		this.icon = icon;
		this.duration = duration;
		this.expires = now + duration;
	}
	
	public Boolean isExpired(){
		if (System.currentTimeMillis()>=expires){
			return true;
		}
		return false;
	}
	
	public void removeDelayed(Duelist duelist, Effect effect){
		Message mes = new Message();
		mes.obj = duelist;
		mes.arg1 = effect.type;
		removeEffectHandler.sendMessageDelayed(mes, effect.duration);
	}
	
	public void clearHandlers(){
		removeEffectHandler.removeCallbacksAndMessages(null);
	}
	
	public void startHandlers(Duelist duelist){
		removeDelayed(duelist,this);
	}
	
	public Handler removeEffectHandler = new Handler(){
		public void handleMessage(Message m){
			Duelist duelist = (Duelist)m.obj;
			int effect = m.arg1;
			duelist.removeEffect(effect);
		}
	};
}
