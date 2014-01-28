package com.dan.wizardduel.duelists;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Message;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dan.wizardduel.GameFragment;
import com.dan.wizardduel.R;
import com.dan.wizardduel.combat.CombatController;
import com.todddavies.components.progressbar.ProgressWheel;

public class Opponent extends Duelist{
	
	public ArrayList<ProgressWheel> spinWheels = new ArrayList<ProgressWheel>();

	public Opponent(GameFragment gameFragment) {
		super(gameFragment);
		// TODO Auto-generated constructor stub
		
		healthBar = (StatMeterView) gameFragment.getView().findViewById(R.id.opponentHealthBar);
		manaBar = (StatMeterView) gameFragment.getView().findViewById(R.id.opponentManaBar);
		manaBar.color = Color.BLUE;
		
		image = R.drawable.red;
		ImageView iv = (ImageView)gameFragment.getView().findViewById(R.id.opponentImage);
		iv.setImageResource(image);
		postInit();
		///////
		context = gameFragment.getView();
		// TODO Auto-generated constructor stub
		spellIVs.add((ImageView) context.findViewById(R.id.opponentPreppedSpell0));
		spellIVs.add((ImageView) context.findViewById(R.id.opponentPreppedSpell1));
		spellIVs.add((ImageView) context.findViewById(R.id.opponentPreppedSpell2));
		
		spinWheels.add((ProgressWheel)context.findViewById(R.id.opponent_spinner0));
		spinWheels.add((ProgressWheel)context.findViewById(R.id.opponent_spinner1));
		spinWheels.add((ProgressWheel)context.findViewById(R.id.opponent_spinner2));
		
		buffLV = (LinearLayout)context.findViewById(R.id.opponentStatusBuffs);
		debuffLV = (LinearLayout)context.findViewById(R.id.opponentStatusDebuffs);
	}

}
