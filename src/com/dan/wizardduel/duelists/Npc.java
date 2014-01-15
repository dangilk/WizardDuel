package com.dan.wizardduel.duelists;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.dan.wizardduel.GameActivity;
import com.dan.wizardduel.GameFragment;
import com.dan.wizardduel.R;
import com.dan.wizardduel.combat.CombatController;
import com.dan.wizardduel.spells.OpponentSpellQueue;
import com.dan.wizardduel.spells.Spell;
import com.dan.wizardduel.spells.SpellQueue;
import com.google.example.games.basegameutils.BaseGameActivity;

public class Npc extends Duelist {
	
	public OpponentSpellQueue spellQueue;

	public Npc(GameFragment gameFragment) {
		// TODO Auto-generated constructor stub
		super(gameFragment);
		spellQueue = new OpponentSpellQueue(gameFragment, this);
		healthBar = (StatMeterView) gameFragment.getView().findViewById(R.id.opponentHealthBar);
		manaBar = (StatMeterView) gameFragment.getView().findViewById(R.id.opponentManaBar);
		manaBar.color = Color.BLUE;
		prepSpellHandler.sendMessageDelayed(new Message(), 3000);
		castSpellHandler.sendMessageDelayed(new Message(), 10000);
		image = R.drawable.red;
		ImageView iv = (ImageView)gameFragment.getView().findViewById(R.id.opponentImage);
		iv.setImageResource(image);
		postInit();
	}
	
	private Handler prepSpellHandler = new Handler(){
		public void handleMessage(Message m){
			prepRandomSpell();
			prepSpellHandler.sendMessageDelayed(new Message(), 3000);
		}
	};
	
	private void prepRandomSpell(){
		String randId = Spell.allSpells[GameActivity.random.nextInt(Spell.allSpells.length)];
		spellQueue.addSpell(randId);
	}
	
	private Handler castSpellHandler = new Handler(){
		public void handleMessage(Message m){
			Integer slot = spellQueue.randomFilledSlot();
			if(slot != null){
				spellQueue.castSpell(slot);
			}
			castSpellHandler.sendMessageDelayed(new Message(), 10000);
		}
	};
	
	public void cleanup(){
		super.cleanup();
		castSpellHandler.removeCallbacksAndMessages(null);
		prepSpellHandler.removeCallbacksAndMessages(null);
		spellQueue.cleanup();
	}

}
