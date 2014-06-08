package com.dan.wizardduel.combat;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureStore;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureOverlayView.OnGesturingListener;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dan.wizardduel.GameFragment;
import com.dan.wizardduel.GameGLSurfaceView;
import com.dan.wizardduel.MainActivity;
import com.dan.wizardduel.R;
import com.dan.wizardduel.duelists.Duelist;
import com.dan.wizardduel.duelists.Npc;
import com.dan.wizardduel.duelists.Player;
import com.dan.wizardduel.spells.Spell;
import com.google.example.games.basegameutils.BaseGameActivity;

public class CombatController implements OnGesturePerformedListener, OnGesturingListener{
	public Player player;
	public Duelist opponent;
	public MainActivity context;

	private GameGLSurfaceView mGLView;
	public static Random random = new Random();

	GestureLibrary gestureLibrary = null;
	GestureOverlayView gestureOverlayView;
	public GameFragment gameFragment;
	public View combatView;

	public CombatController(MainActivity context){
		this.context = context;
		gameFragment = new GameFragment();
		combatView = gameFragment.getView();

		
		gestureLibrary = GestureLibraries.fromRawResource(context,
				R.raw.gestures);
		

		// gestureLibrary.setOrientationStyle(GestureStore.ORIENTATION_INVARIANT);
		gestureLibrary.setSequenceType(GestureStore.SEQUENCE_INVARIANT);
		if (!gestureLibrary.load()) {
			Log.e("tag", "load failed");
		}

		gestureOverlayView
				.addOnGesturePerformedListener(gesturePerformedListener);
		gestureOverlayView.addOnGesturingListener(gesturingListener);
		
	}

	OnGesturePerformedListener gesturePerformedListener = new OnGesturePerformedListener() {
		@Override
		public void onGesturePerformed(GestureOverlayView view, Gesture gesture) {
			// TODO Auto-generated method stub
			ArrayList<Prediction> prediction = gestureLibrary
					.recognize(gesture);
			Set<String> gests = gestureLibrary.getGestureEntries();

			if (prediction.size() > 0) {
				Toast toast = Toast.makeText(context, prediction.get(0).name,
						Toast.LENGTH_SHORT);
				toast.show();
				player.addSpell(prediction.get(0).name);
			}

		}
	};

	OnGesturingListener gesturingListener = new OnGesturingListener() {
		@Override
		public void onGesturingStarted(GestureOverlayView overlay) {
			mGLView.resetFade();
		}

		@Override
		public void onGesturingEnded(GestureOverlayView overlay) {
			// TODO Auto-generated method stub
			mGLView.forceFade();
		}
	};

	public void startGame() {
		//this.player = new Player(this);
		//this.opponent = new Npc(this);
		context.switchToFragment(gameFragment);
	}
	

	private void addHpListeners() {
		player.addHpListener(new HpListener() {

			@Override
			public void hpUpdated(int hp) {
				// TODO Auto-generated method stub
				if (hp <= 0) {
					// player lost
					completeGame(false);
				}
			}

		});

		opponent.addHpListener(new HpListener() {

			@Override
			public void hpUpdated(int hp) {
				// TODO Auto-generated method stub
				Log.e("tag", "hp updated : " + hp);
				if (hp <= 0) {
					// player won
					completeGame(true);
				}
			}

		});
	}

	public void completeGame(Boolean won) {
		clearHpListeners();
		context.switchToFragment(context.mMainMenuFragment);
	}

	private void clearHpListeners() {
		player.clearHpListeners();
		opponent.clearHpListeners();
	}


	@Override
	public void onGesturingStarted(GestureOverlayView overlay) {
		mGLView.resetFade();
	}

	@Override
	public void onGesturingEnded(GestureOverlayView overlay) {
		mGLView.forceFade();
	}


	@Override
	public void onGesturePerformed(GestureOverlayView view, Gesture gesture) {
		// TODO Auto-generated method stub
		ArrayList<Prediction> prediction = gestureLibrary
				.recognize(gesture);
		if (prediction.size() > 0) {
			Toast toast = Toast.makeText(context, prediction.get(0).name,
					Toast.LENGTH_SHORT);
			toast.show();
			player.addSpell(prediction.get(0).name);
		}

	}
}
