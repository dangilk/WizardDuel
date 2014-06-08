package com.dan.wizardduel.effects;

import android.os.Handler;
import android.os.Message;

import com.dan.wizardduel.R;
import com.dan.wizardduel.duelists.Duelist;

public class Poison extends Effect{
	public Poison(){
		super(POISON,false,R.drawable.poison,10*1000);
	}
	
	public void startPoison(Duelist duelist){
		Message mes = new Message();
		mes.obj = duelist;
		poisonTickHandler.sendMessageDelayed(mes, 1000);
	}
	
	public void stopPoison(){
		poisonTickHandler.removeCallbacksAndMessages(null);
	}
	
	@Override
	public void clearHandlers(){
		stopPoison();
		super.clearHandlers();
	}
	
	@Override
	public void startHandlers(Duelist duelist){
		startPoison(duelist);
		super.startHandlers(duelist);
	}
	
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
