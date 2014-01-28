package com.dan.wizardduel.combat;

import android.os.Handler;
import android.os.Message;

import com.dan.wizardduel.R;
import com.dan.wizardduel.duelists.Duelist;

public class Effect {

	public int type;
	public long expires;
	public long duration;
	public String name;
	public Boolean buff;
	public int amplitude=3;
	public int icon;
	
	public static final int SLOW=1;
	public static final int HASTE=2;
	public static final int POISON=3;
	public static final int LOCK=4;
	public static final int SHIELD=5;
	
	public Effect(int type){
		long now = System.currentTimeMillis();
		this.type = type;
		switch(type){
		case SLOW:
			duration=10*1000;
			buff = false;
			icon = R.drawable.ice;
			break;
		case HASTE:
			duration=10*1000;
			buff = true;
			icon = R.drawable.haste;
			break;
		case POISON:
			duration=10*1000;
			buff = false;
			icon = R.drawable.poison;
			break;
		case LOCK:
			duration=10*1000;
			buff = false;
			icon = R.drawable.lock;
			break;
		case SHIELD:
			duration=10*1000;
			buff = true;
			icon = R.drawable.shield;
			break;
		}
		expires = now + duration;
	}
	
	public Boolean isExpired(){
		if (System.currentTimeMillis()>=expires){
			return true;
		}
		return false;
	}
	
	public void startPoison(Duelist duelist){
		Message mes = new Message();
		mes.obj = duelist;
		poisonTickHandler.sendMessageDelayed(mes, 1000);
	}
	
	public void removeDelayed(Duelist duelist, Effect effect){
		Message mes = new Message();
		mes.obj = duelist;
		mes.arg1 = effect.type;
		removeEffectHandler.sendMessageDelayed(mes, effect.duration);
	}
	
	public void stopPoison(){
		poisonTickHandler.removeCallbacksAndMessages(null);
	}
	
	public void clearHandlers(){
		stopPoison();
		removeEffectHandler.removeCallbacksAndMessages(null);
	}
	
	public void startHandlers(Duelist duelist){
		if(this.type == Effect.POISON){
			startPoison(duelist);
		}
		removeDelayed(duelist,this);
	}
	
	public Handler removeEffectHandler = new Handler(){
		public void handleMessage(Message m){
			Duelist duelist = (Duelist)m.obj;
			int effect = m.arg1;
			duelist.removeEffect(effect);
		}
	};
	

	public Handler poisonTickHandler = new Handler(){
		public void handleMessage(Message m){
			Duelist duelist = (Duelist)m.obj;
			if(isExpired()){
				stopPoison();
				return;
			}
			duelist.decHp(amplitude);
			Message mes = new Message();
			mes.obj = duelist;
			poisonTickHandler.sendMessageDelayed(mes, 1000);
		}
	};
	
}
