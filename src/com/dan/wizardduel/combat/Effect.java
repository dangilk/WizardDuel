package com.dan.wizardduel.combat;

import android.os.Handler;
import android.os.Message;

import com.dan.wizardduel.duelists.Duelist;

public class Effect {

	public int type;
	public long expires;
	public String name;
	public Boolean buff;
	public int amplitude=3;
	
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
			expires=now+10*1000;
			buff = false;
			break;
		case HASTE:
			expires=now+10*1000;
			buff = true;
			break;
		case POISON:
			expires=now+10*1000;
			buff = false;
		}
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
	
	public void stopPoison(Duelist duelist){
		poisonTickHandler.removeCallbacksAndMessages(null);
	}
	

	public Handler poisonTickHandler = new Handler(){
		public void handleMessage(Message m){
			Duelist duelist = (Duelist)m.obj;
			if(isExpired()){
				stopPoison(duelist);
				return;
			}
			duelist.decHp(amplitude);
			Message mes = new Message();
			mes.obj = duelist;
			poisonTickHandler.sendMessageDelayed(mes, 1000);
		}
	};
	
}
