package com.dan.wizardduel;


import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureOverlayView.OnGesturingListener;
import android.gesture.Prediction;
import android.gesture.GestureStore;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.dan.wizardduel.combat.HpListener;
import com.dan.wizardduel.duelists.Duelist;
import com.dan.wizardduel.duelists.Npc;
import com.dan.wizardduel.duelists.Player;

public class GameActivity extends WDGameActivity implements
		GameplayFragment.Listener, WinFragment.Listener {
	GameplayFragment mGameplayFragment;
	WinFragment mWinFragment;
	AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();
	private GameGLSurfaceView mGLView;
	public static Random random = new Random();

	GestureLibrary gestureLibrary = null;
	GestureOverlayView gestureOverlayView;
	
	//private SpellQueue mySpellQueue;
	public Player player;
	public Duelist opponent;
	public Activity context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		mGLView = (GameGLSurfaceView) findViewById(R.id.gameGLSurfaceView1);
		gestureOverlayView = (GestureOverlayView) findViewById(R.id.gestures);
		gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		context = this;
		
		//gestureLibrary.setOrientationStyle(GestureStore.ORIENTATION_INVARIANT);
		gestureLibrary.setSequenceType(GestureStore.SEQUENCE_INVARIANT);
		if(!gestureLibrary.load()){
			Log.e("tag","load failed");
		}
		
		//initialize the players
		//player = new Player(this);
		//opponent = new Npc(this);
		addHpListeners();
		
		//mySpellQueue = new SpellQueue(this);
		
		gestureOverlayView
				.addOnGesturePerformedListener(gesturePerformedListener);
		gestureOverlayView.addOnGesturingListener(gesturingListener);
		// setContentView(R.layout.activity_main);
		/*
		 * mGameplayFragment = new GameplayFragment(); mWinFragment = new
		 * WinFragment();
		 * 
		 * mGameplayFragment.setListener(this); mWinFragment.setListener(this);
		 * 
		 * // add initial fragment
		 * getSupportFragmentManager().beginTransaction()
		 * .add(R.id.fragment_container, mGameplayFragment).commit();
		 * 
		 * // IMPORTANT: if this Activity supported rotation, we'd have to be //
		 * more careful about adding the fragment, since the fragment would //
		 * already be there after rotation and trying to add it again would //
		 * result in overlapping fragments. But since we don't support rotation,
		 * // we don't deal with that for code simplicity.
		 * 
		 * // load outbox from file mOutbox.loadLocal(this);
		 */
	}
	
	private void addHpListeners(){
		player.addHpListener(new HpListener(){

			@Override
			public void hpUpdated(int hp) {
				// TODO Auto-generated method stub
				if(hp<=0){
					//player lost
					completeGame(false);
				}
			}
			
		});
		
		opponent.addHpListener(new HpListener(){

			@Override
			public void hpUpdated(int hp) {
				// TODO Auto-generated method stub
				Log.e("tag","hp updated : "+hp);
				if(hp<=0){
					//player won
					completeGame(true);
				}
			}
			
		});
	}
	private void clearHpListeners(){
		player.clearHpListeners();
		opponent.clearHpListeners();
	}
	
	public void completeGame(Boolean won){
		clearHpListeners();
		Intent i = new Intent(context,GameCompleteActivity.class);
		i.putExtra("won", won);
		context.startActivity(i);
		finish();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		opponent.cleanup();
		player.cleanup();
	}


	OnGesturePerformedListener gesturePerformedListener = new OnGesturePerformedListener() {
		@Override
		public void onGesturePerformed(GestureOverlayView view, Gesture gesture) {
			// TODO Auto-generated method stub
			ArrayList<Prediction> prediction = gestureLibrary
					.recognize(gesture);
			Set<String> gests = gestureLibrary.getGestureEntries();

			if (prediction.size() > 0) {
				Toast toast = Toast.makeText(GameActivity.this,
						prediction.get(0).name, Toast.LENGTH_SHORT);
				toast.show();
				player.spellQueue.addSpell(prediction.get(0).name);
			}
			

		}
	};
	
	OnGesturingListener gesturingListener = new OnGesturingListener(){
		@Override
		public void onGesturingStarted(GestureOverlayView overlay){
			mGLView.resetFade();
		}

		@Override
		public void onGesturingEnded(GestureOverlayView overlay) {
			// TODO Auto-generated method stub
			mGLView.forceFade();
		}
	};

	@Override
	public void onWinScreenDismissed() {
		// switchToFragment(mMainMenuFragment);
		finish();
	}

	@Override
	public void onWinScreenSignInClicked() {
		beginUserInitiatedSignIn();
	}

	@Override
	public void onEnteredScore(int requestedScore) {
		// Compute final score (in easy mode, it's the requested score; in hard
		// mode, it's half)
		int finalScore = requestedScore;

		mWinFragment.setFinalScore(finalScore);
		mWinFragment.setExplanation(getString(R.string.easy_mode_explanation));

		// check for achievements
		checkForAchievements(requestedScore, finalScore);

		// update leaderboards
		updateLeaderboards(finalScore);

		// push those accomplishments to the cloud, if signed in
		pushAccomplishments();

		// switch to the exciting "you won" screen
		switchToFragment(mWinFragment);
	}

	void switchToFragment(Fragment newFrag) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, newFrag).commit();
	}

	/**
	 * Check for achievements and unlock the appropriate ones.
	 * 
	 * @param requestedScore
	 *            the score the user requested.
	 * @param finalScore
	 *            the score the user got.
	 */
	void checkForAchievements(int requestedScore, int finalScore) {
		// Check if each condition is met; if so, unlock the corresponding
		// achievement.

		if (requestedScore == 9999) {
			mOutbox.mArrogantAchievement = true;
			achievementToast(getString(R.string.achievement_arrogant_toast_text));
		}
		if (requestedScore == 0) {
			mOutbox.mHumbleAchievement = true;
			achievementToast(getString(R.string.achievement_humble_toast_text));
		}
		if (finalScore == 1337) {
			mOutbox.mLeetAchievement = true;
			achievementToast(getString(R.string.achievement_leet_toast_text));
		}
		mOutbox.mBoredSteps++;
	}

	void updateLeaderboards(int finalScore) {
		if (mOutbox.mHardModeScore < finalScore) {
			mOutbox.mHardModeScore = finalScore;
		} else if (mOutbox.mEasyModeScore < finalScore) {
			mOutbox.mEasyModeScore = finalScore;
		}
	}

	void pushAccomplishments() {
		if (!isSignedIn()) {
			// can't push to the cloud, so save locally
			mOutbox.saveLocal(this);
			return;
		}
		if (mOutbox.mPrimeAchievement) {
			getGamesClient().unlockAchievement(
					getString(R.string.achievement_play));
			mOutbox.mPrimeAchievement = false;
		}
		if (mOutbox.mArrogantAchievement) {
			getGamesClient().unlockAchievement(
					getString(R.string.achievement_play));
			mOutbox.mArrogantAchievement = false;
		}
		if (mOutbox.mHumbleAchievement) {
			getGamesClient().unlockAchievement(
					getString(R.string.achievement_play));
			mOutbox.mHumbleAchievement = false;
		}
		if (mOutbox.mLeetAchievement) {
			getGamesClient().unlockAchievement(
					getString(R.string.achievement_play));
			mOutbox.mLeetAchievement = false;
		}
		if (mOutbox.mBoredSteps > 0) {
			getGamesClient().incrementAchievement(
					getString(R.string.achievement_play), mOutbox.mBoredSteps);
			getGamesClient().incrementAchievement(
					getString(R.string.achievement_play), mOutbox.mBoredSteps);
		}
		if (mOutbox.mEasyModeScore >= 0) {
			getGamesClient().submitScore(
					getString(R.string.leaderboard_points),
					mOutbox.mEasyModeScore);
			mOutbox.mEasyModeScore = -1;
		}
		if (mOutbox.mHardModeScore >= 0) {
			getGamesClient().submitScore(
					getString(R.string.leaderboard_points),
					mOutbox.mHardModeScore);
			mOutbox.mHardModeScore = -1;
		}
		mOutbox.saveLocal(this);
	}


	class AccomplishmentsOutbox {
		boolean mPrimeAchievement = false;
		boolean mHumbleAchievement = false;
		boolean mLeetAchievement = false;
		boolean mArrogantAchievement = false;
		int mBoredSteps = 0;
		int mEasyModeScore = -1;
		int mHardModeScore = -1;

		boolean isEmpty() {
			return !mPrimeAchievement && !mHumbleAchievement
					&& !mLeetAchievement && !mArrogantAchievement
					&& mBoredSteps == 0 && mEasyModeScore < 0
					&& mHardModeScore < 0;
		}

		public void saveLocal(Context ctx) {
			/*
			 * TODO: This is left as an exercise. To make it more difficult to
			 * cheat, this data should be stored in an encrypted file! And
			 * remember not to expose your encryption key (obfuscate it by
			 * building it from bits and pieces and/or XORing with another
			 * string, for instance).
			 */
		}

		public void loadLocal(Context ctx) {
			/*
			 * TODO: This is left as an exercise. Write code here that loads
			 * data from the file you wrote in saveLocal().
			 */
		}
	}

	void achievementToast(String achievement) {
		// Only show toast if not signed in. If signed in, the standard Google
		// Play
		// toasts will appear, so we don't need to show our own.
		if (!isSignedIn()) {
			Toast.makeText(this,
					getString(R.string.achievement) + ": " + achievement,
					Toast.LENGTH_LONG).show();
		}
	}

	void unlockAchievement(int achievementId, String fallbackString) {
		if (isSignedIn()) {
			getGamesClient().unlockAchievement(getString(achievementId));
		} else {
			Toast.makeText(this,
					getString(R.string.achievement) + ": " + fallbackString,
					Toast.LENGTH_LONG).show();
		}
	}

}
