package com.dan.wizardduel;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import com.dan.wizardduel.GameplayFragment.Listener;
import com.dan.wizardduel.combat.HpListener;
import com.dan.wizardduel.duelists.Duelist;
import com.dan.wizardduel.duelists.Npc;
import com.dan.wizardduel.duelists.Player;
import com.dan.wizardduel.duelists.PlayerOpponent;
import com.dan.wizardduel.spells.Spell;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureStore;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureOverlayView.OnGesturingListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class GameFragment extends Fragment implements Player.Listener{
	
	public GameGLSurfaceView mGLView;
	public GestureOverlayView gestureOverlayView;
	
	public Player player;
	public Duelist opponent;
	public MainActivity context;

	public static Random random = new Random();

	GestureLibrary gestureLibrary = null;
	public GameFragment gameFragment;
	public View combatView;
	Listener gameListener = null;
	
	public String roomId = null;
	public Boolean npcGame = true;
	public String playerId = null;
	public String opponentId = null;
	
	public interface Listener {
        public void onGameComplete(Boolean won);
        public void onSpellPrepped(int slot, Spell spell);
        public void onSpellCasting(int slot);
        public void onSpellCastCanceled(int slot);
        public void onSpellExecuted(int slot, Spell spell);
    }
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		context = (MainActivity) activity;
	}
	
	public void setListener(Listener l) {
        gameListener = l;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		//super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_game, container, false);
        
        mGLView = (GameGLSurfaceView) v
				.findViewById(R.id.gameGLSurfaceView1);
		gestureOverlayView = (GestureOverlayView) v
				.findViewById(R.id.gestures);
		
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
		
		

        return v;
    }
	
	@Override
	public void onStart(){
		super.onStart();
		initGame();
	}
	
	
	
	public void initGame() {
		Log.e("tag", "INIT GAME");
		if(npcGame){
			setupNpcGame();
		}else{
			setupMultiplayerGame(playerId,opponentId);
		}
		addHpListeners();
	}
	
	public void setupNpcGame(){
		this.player = new Player(this);
		this.opponent = new Npc(this);
		this.opponent.setListener(null);
	}
	
	public void setupMultiplayerGame(String playerId, String opponentId){
		this.player = new Player(this,playerId);
		this.opponent = new PlayerOpponent(this,opponentId);
		this.player.setListener(this);
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
		gameListener.onGameComplete(won);
	}

	public void clearHpListeners() {
		if(player != null){
			player.cleanup();
		}
		if(opponent != null){
			opponent.cleanup();
		}
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

	@Override
	public void onSpellPrepped(int slot, Spell spell) {
		gameListener.onSpellPrepped(slot, spell);
	}

	@Override
	public void onSpellCasting(int slot) {
		gameListener.onSpellCasting(slot);
	}

	@Override
	public void onSpellCastCanceled(int slot) {
		gameListener.onSpellCastCanceled(slot);
	}

	@Override
	public void onSpellExecuted(int slot, Spell spell) {
		gameListener.onSpellExecuted(slot, spell);
	}
	
	
}
