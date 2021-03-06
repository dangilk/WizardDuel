package com.dan.wizardduel.duelists;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.dan.wizardduel.GameFragment;
import com.dan.wizardduel.effects.Effect;
import com.dan.wizardduel.spells.Spell;
import com.todddavies.components.progressbar.ProgressWheel;

public class Npc extends Opponent {

	private Npc self;

	public Npc(GameFragment gameFragment) {
		// TODO Auto-generated constructor stub
		super(gameFragment);
		self = this;

		prepSpellHandler.sendMessageDelayed(new Message(), 3000);
		castSpellHandler.sendMessageDelayed(new Message(), 10000);

	}

	private Handler prepSpellHandler = new Handler() {
		public void handleMessage(Message m) {
			prepRandomSpell();
			prepSpellHandler.sendMessageDelayed(new Message(), 3000);
		}
	};

	private void prepRandomSpell() {
		String randId = Spell.randomSpell();
		this.addSpell(randId);
	}

	private Handler castSpellHandler = new Handler() {
		public void handleMessage(Message m) {
			Integer slot = self.randomFilledSlot();
			if (slot != null) {
				self.castSpell(slot);
			}
			castSpellHandler.sendMessageDelayed(new Message(), 10000);
		}
	};

	public void cleanup() {
		super.cleanup();
		castSpellHandler.removeCallbacksAndMessages(null);
		prepSpellHandler.removeCallbacksAndMessages(null);
	}

	public void castSpell(final int slot) {
		final Spell spell = spells.get(slot);
		Boolean valid = castSpellCheck(slot, spell);
		if (!valid) {
			return;
		}
		final ProgressWheel pw = spinWheels.get(slot);

		final long castTime = (spell.castTime
				+ this.effectAmplitude(Effect.SLOW) - this
				.effectAmplitude(Effect.HASTE)) * 1000;
		if (spell != null) {
			castTimer = new CountDownTimer(castTime, 10) {

				@Override
				public void onFinish() {
					gameFragment.opponent.executeSpell(gameFragment.player,
							spell);
					removeSpell(slot);
					pw.setVisibility(View.GONE);
				}

				@Override
				public void onTick(long millisUntilFinished) {
					pw.incrementProgress();
					long elapsed = castTime - millisUntilFinished;
					pw.setProgress((int) (elapsed * 360 / castTime) + 1);
				}
			};
			castingSpell = slot;
			castTimer.start();
			pw.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void counterCurrentSpell() {
		castTimer.cancel();
		removeSpell(castingSpell);
		final ProgressWheel pw = spinWheels.get(castingSpell);
		pw.setVisibility(View.GONE);
	}

}
