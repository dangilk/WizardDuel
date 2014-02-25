/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dan.wizardduel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import utils.Score;
import utils.Score.Listener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.WindowManager;

import com.dan.wizardduel.duelists.PlayerOpponent;
import com.dan.wizardduel.spells.Spell;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.parse.Parse;

public class MainActivity extends BaseGameActivity
        implements MainMenuFragment.Listener,
        GameFragment.Listener, RoomUpdateListener, RealTimeMessageReceivedListener, RoomStatusUpdateListener, GameCompleteFragment.Listener, Score.Listener{

    

    private static final int RC_INVITATION_INBOX = 0;
	private static final int RC_SELECT_PLAYERS = 1;
	private static final int RC_WAITING_ROOM = 2;
	private static final int RC_LEADERBOARD = 3;
	
	private static final int GAME_TYPE_PRACTICE = 0;
	private static final int GAME_TYPE_CUSTOM = 1;
	private static final int GAME_TYPE_RANKED =2;
	
	private int consecutiveWins = 0;
	
	public int gamePlayingType = GAME_TYPE_PRACTICE;

	public static Random random = new Random();

	// request codes we use when invoking an external activity
    final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

    // tag for debug logging
    final boolean ENABLE_DEBUG = true;
    final String TAG = "TanC";


    
    public MainMenuFragment mMainMenuFragment = null;
    public GameFragment gameFragment;
    public GameCompleteFragment gameCompleteFragment = null;
    
    public GamesClient gameClient;
    
    private Score score;
    
    private boolean replayRequested = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        enableDebugLog(ENABLE_DEBUG, TAG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Parse.initialize(this, getString(R.string.parseId), getString(R.string.parseKey));

        // create fragments
        mMainMenuFragment = new MainMenuFragment();
        gameFragment = new GameFragment();
        gameCompleteFragment = new GameCompleteFragment();

        // listen to fragment events
        mMainMenuFragment.setListener(this);
        gameFragment.setListener(this);
        gameCompleteFragment.setListener(this);
        //mGameplayFragment.setListener(this);
        //mWinFragment.setListener(this);

        // add initial fragment (welcome fragment)
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                mMainMenuFragment).commit();
        
        gameClient = this.mHelper.getGamesClient();

        score = new Score(this);


    }

    // Switch UI to the given fragment
    public void switchToFragment(Fragment newFrag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag)
                .commitAllowingStateLoss();
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	gameFragment.clearHpListeners();
    	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onStartPracticeGameRequested() {
    	gameFragment.npcGame = true;
    	gamePlayingType = GAME_TYPE_PRACTICE;
    	switchToFragment(gameFragment);
    }
    
    @Override
    public void onStartCustomGameRequested() {
    	gamePlayingType = GAME_TYPE_CUSTOM;
        openInvitePlayers();
    }

    @Override
    public void onShowAchievementsRequested() {
        if (isSignedIn()) {
            startActivityForResult(getGamesClient().getAchievementsIntent(), RC_UNUSED);
        } else {
            showAlert(getString(R.string.achievements_not_available));
        }
    }

    @Override
    public void onShowLeaderboardsRequested() {
        if (isSignedIn()) {
        	mMainMenuFragment.startLoading();
            startActivityForResult(getGamesClient().getAllLeaderboardsIntent(), RC_LEADERBOARD);
        } else {
            showAlert(getString(R.string.leaderboards_not_available));
        }
    }

    
    void openInvitationInbox(){
    	mMainMenuFragment.startLoading();
    	// launch the intent to show the invitation inbox screen
    	Intent intent = getGamesClient().getInvitationInboxIntent();
    	this.startActivityForResult(intent, RC_INVITATION_INBOX);
    }
    
    void openInvitePlayers(){
    	mMainMenuFragment.startLoading();
    	// launch the player selection screen
    	// minimum: 1 other player; maximum: 3 other players
    	Intent intent = getGamesClient().getSelectPlayersIntent(1, 1);
    	startActivityForResult(intent, RC_SELECT_PLAYERS);
    }
    
    void startQuickGame(){
    	mMainMenuFragment.startLoading();
    	// automatch criteria to invite 1 random automatch opponent.  
        // You can also specify more opponents (up to 3). 
        Bundle am = RoomConfig.createAutoMatchCriteria(1, 1, 0);

        // build the room config:
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(am);
        
        RoomConfig roomConfig = roomConfigBuilder.build();

        // create room:
        getGamesClient().createRoom(roomConfig);

        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // go to game screen
    }
    
    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
    }
    


    @Override
    public void onSignInButtonClicked() {
        // start the sign-in flow
        beginUserInitiatedSignIn();
    }

    @Override
    public void onSignOutButtonClicked() {
        signOut();
        mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
        mMainMenuFragment.setShowSignInButton(true);
        //mWinFragment.setShowSignInButton(true);
    }

    @Override
    public void onSignInFailed() {
    	Log.e("tag","sign in failed");
		if(mMainMenuFragment == null){
			return;
		}
		
        // Sign-in failed, so show sign-in button on main menu
        mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
        mMainMenuFragment.setShowSignInButton(true);
        //mWinFragment.setShowSignInButton(true);
    }

    @Override
    public void onSignInSucceeded() {
    	Log.e("tag","sign in success");
    	if(mMainMenuFragment == null){
			return;
		}
    	
    	
    	
    	if (getInvitationId() != null) {
    		gamePlayingType = GAME_TYPE_CUSTOM;
    		mMainMenuFragment.startLoading();
            RoomConfig.Builder roomConfigBuilder =
                makeBasicRoomConfigBuilder();
            roomConfigBuilder.setInvitationIdToAccept(getInvitationId());
            getGamesClient().joinRoom(roomConfigBuilder.build());

            // prevent screen from sleeping during handshake
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // go to game screen
        }
    	Log.e("tag","hide sign in");
        // Show sign-out button on main menu
        mMainMenuFragment.setShowSignInButton(false);

        // Show "you are signed in" message on win screen, with no sign in button.
        //mWinFragment.setShowSignInButton(false);

        // Set the greeting appropriately on main menu
        Player p = getGamesClient().getCurrentPlayer();
        String displayName;
        if (p == null) {
            Log.w("tag", "mGamesClient.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = p.getDisplayName();
        }
        mMainMenuFragment.setGreeting("Hello, " + displayName);
        score.init(p.getPlayerId());
    }
    
    /*
     * 
     * sample execution order:
   		second of two players joins room:
		room created
		peer joined
		room connecting
		p2p connected
		connected to room
		peers connected
		room connected
		
		
		second player leaves:
			disconnected from room
			peer left
 			peers disconnected

     */

	@Override
	public void onGameComplete(Boolean won) {
		if(won && gamePlayingType == GAME_TYPE_RANKED){
			score.increment();
			gameClient.submitScore(getResources().getString(R.string.leaderboard_points), score.getConsecutiveWins());
		}else if(gamePlayingType == GAME_TYPE_RANKED){
			score.clear();
		}
		gameCompleteFragment.setWon(won);
		switchToFragment(gameCompleteFragment);
	}

	@Override
	public void onConnectedToRoom(Room arg0) {
		Log.e("tag","connected to room");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnectedFromRoom(Room arg0) {
		Log.e("tag","disconnected from room");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PConnected(String arg0) {
		Log.e("tag","p2p connected");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PDisconnected(String arg0) {
		Log.e("tag","p2p disconnected");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeerDeclined(Room arg0, List<String> arg1) {
		Log.e("tag","peer declined");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeerInvitedToRoom(Room arg0, List<String> arg1) {
		Log.e("tag","peer invited to room");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeerJoined(Room arg0, List<String> arg1) {
		Log.e("tag","peer joined");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeerLeft(Room arg0, List<String> arg1) {
		Log.e("tag","peer left");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeersConnected(Room arg0, List<String> arg1) {
		Log.e("tag","peers connected");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeersDisconnected(Room arg0, List<String> arg1) {
		Log.e("tag","peers disconnected");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomAutoMatching(Room arg0) {
		Log.e("tag","room auto matching");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomConnecting(Room arg0) {
		Log.e("tag","room connecting");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage rtm) {
		Log.e("tag","real time message received");
		byte[] bytes = rtm.getMessageData();
		if(bytes[0]==MESSAGE_SPELL_PREPPED){
			gameFragment.opponent.addSpellAt((int)bytes[1], (int)bytes[2]);
		}else if(bytes[0] == MESSAGE_SPELL_CASTING){
			((PlayerOpponent)gameFragment.opponent).castSpell((int)bytes[1]);
		}else if(bytes[0] == MESSAGE_SPELL_CAST_CANCELED){
			((PlayerOpponent)gameFragment.opponent).castSpellCanceled((int)bytes[1]);
		}else if(bytes[0] == MESSAGE_SPELL_EXECUTED){
			((PlayerOpponent)gameFragment.opponent).executeSpell(new Spell((int)bytes[2]));
		}else if(bytes[0] == MESSAGE_REPLAY_REQUESTED){
			if(replayRequested){
				//start game again
				restartCustomGame();
				startCustomGame(null, null, null);
			}
		}else if(bytes[0] == MESSAGE_REPLAY_CUSTOM){
			startCustomGame(null,null,null);
		}
	}

	@Override
	public void onJoinedRoom(int statusCode, Room room) {
		Log.e("tag","joined room");
	    if (statusCode != GamesClient.STATUS_OK) {
	       // display error
	       return;  
	    }

	    // get waiting room intent
	    Intent i = getGamesClient().getRealTimeWaitingRoomIntent(room, 2);
	    startActivityForResult(i, RC_WAITING_ROOM);
	}

	@Override
	public void onLeftRoom(int arg0, String arg1) {
		Log.e("tag","left room");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomConnected(int status, Room room) {
		Log.e("tag","room connected");
		// TODO Auto-generated method stub
		String playerId = gameClient.getCurrentPlayerId();
		playerId = room.getParticipantId(playerId);
		for (Participant p : room.getParticipants()) {
	        String pid = p.getParticipantId();
	        if (pid != playerId) {
	            startCustomGame(playerId,pid,room.getRoomId());
	        }
	    }
		
	}
	
	public void startCustomGame(String playerId, String opponentId, String roomId){
		mMainMenuFragment.stopLoading();
		
		gameFragment.npcGame = false;
		if(playerId != null){
			gameFragment.playerId = playerId;
		}
		if(opponentId != null){
			gameFragment.opponentId = opponentId;
		}
		if(roomId != null){
			gameFragment.roomId = roomId;
		}
        replayRequested = false;
        Runnable r = new Runnable() {
            @Override
            public void run() {
            	switchToFragment(gameFragment);
            }
        };
        Handler h = new Handler();
        h.post(r);
	}

	@Override
	public void onRoomCreated(int statusCode, Room room) {
		Log.e("tag","room created");
	    if (statusCode != GamesClient.STATUS_OK) {
	        // display error
	        return;
	    }

	    // get waiting room intent
	    Intent i = getGamesClient().getRealTimeWaitingRoomIntent(room, 2);
	    startActivityForResult(i, RC_WAITING_ROOM);
	}
	
	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
		Log.e("tag","activity result: "+request+ " , "+response);
	    if (request == RC_INVITATION_INBOX) {
	        if (response != Activity.RESULT_OK) {
	        	mMainMenuFragment.stopLoading();
	            return;
	        }

	        // get the selected invitation
	        Bundle extras = data.getExtras();
	        Invitation invitation =
	            extras.getParcelable(GamesClient.EXTRA_INVITATION);

	        // accept it!
	        RoomConfig roomConfig = makeBasicRoomConfigBuilder()
	                .setInvitationIdToAccept(invitation.getInvitationId())
	                .build();
	        getGamesClient().joinRoom(roomConfig);

	        // prevent screen from sleeping during handshake
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	        // go to game screen
	    }else if (request == RC_SELECT_PLAYERS) {
	        if (response != Activity.RESULT_OK) {
	        	mMainMenuFragment.stopLoading();
	            return;
	        }

	        // get the invitee list
	        Bundle extras = data.getExtras();
	        final ArrayList<String> invitees =
	            data.getStringArrayListExtra(GamesClient.EXTRA_PLAYERS);

	        // get automatch criteria
	        Bundle autoMatchCriteria = null;
	        int minAutoMatchPlayers =
	            data.getIntExtra(GamesClient.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
	        int maxAutoMatchPlayers =
	            data.getIntExtra(GamesClient.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

	        if (minAutoMatchPlayers > 0) {
	            autoMatchCriteria =
	                RoomConfig.createAutoMatchCriteria(
	                    1,1,0);//minAutoMatchPlayers, maxAutoMatchPlayers, 0);
	        } else {
	            autoMatchCriteria = null;
	        }

	        // create the room and specify a variant if appropriate
	        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
	        roomConfigBuilder.addPlayersToInvite(invitees);
	        if (autoMatchCriteria != null) {
	            roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
	        }
	        RoomConfig roomConfig = roomConfigBuilder.build();
	        getGamesClient().createRoom(roomConfig);

	        // prevent screen from sleeping during handshake
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    }else if(request == RC_WAITING_ROOM){
	    	if (response != Activity.RESULT_OK) {
	    		mMainMenuFragment.stopLoading();
	            return;
	        }
	    }else if(request == RC_LEADERBOARD){
	    	mMainMenuFragment.stopLoading();
	    }
	}
	
	private static final byte MESSAGE_SPELL_PREPPED = 0;
	private static final byte MESSAGE_SPELL_CASTING = 1;
	private static final byte MESSAGE_SPELL_CAST_CANCELED = 2;
	private static final byte MESSAGE_SPELL_EXECUTED = 3;
	private static final byte MESSAGE_REPLAY_REQUESTED = 4;
	private static final byte MESSAGE_REPLAY_CUSTOM = 5;
	
	public void restartCustomGame(){
		Log.e("tag", "send restart request");
		byte[] bytes = new byte[1];
		bytes[0] = MESSAGE_REPLAY_CUSTOM;
		gameClient.sendUnreliableRealTimeMessage( bytes, gameFragment.roomId, gameFragment.opponent.playerId);
	}
	
	public void requestReplay() {
		Log.e("tag", "send rematch request");
		byte[] bytes = new byte[1];
		bytes[0] = MESSAGE_REPLAY_REQUESTED;
		gameClient.sendUnreliableRealTimeMessage( bytes, gameFragment.roomId, gameFragment.opponent.playerId);
	}

	@Override
	public void onSpellPrepped(int slot, Spell spell) {
		byte[] bytes = new byte[3];
		bytes[0] = MESSAGE_SPELL_PREPPED;
		bytes[1]= (byte)slot;
		bytes[2] = (byte)spell.intId;
		Log.e("tag","spell prepped send message"+gameFragment.opponent.playerId);
		gameClient.sendUnreliableRealTimeMessage( bytes, gameFragment.roomId, gameFragment.opponent.playerId);
	}

	@Override
	public void onSpellCasting(int slot) {
		byte[] bytes = new byte[2];
		bytes[0] = MESSAGE_SPELL_CASTING;
		bytes[1]= (byte)slot;
		gameClient.sendUnreliableRealTimeMessage( bytes, gameFragment.roomId, gameFragment.opponent.playerId);
	}

	@Override
	public void onSpellCastCanceled(int slot) {
		byte[] bytes = new byte[2];
		bytes[0] = MESSAGE_SPELL_CAST_CANCELED;
		bytes[1]= (byte)slot;
		gameClient.sendUnreliableRealTimeMessage( bytes, gameFragment.roomId, gameFragment.opponent.playerId);
	}

	@Override
	public void onSpellExecuted(int slot, Spell spell) {
		byte[] bytes = new byte[3];
		bytes[0] = MESSAGE_SPELL_EXECUTED;
		bytes[1]= (byte)slot;
		bytes[2] = (byte)spell.intId;
		Log.e("tag","spell executed send message"+gameFragment.opponent.playerId);
		gameClient.sendUnreliableRealTimeMessage( bytes, gameFragment.roomId, gameFragment.opponent.playerId);
	}

	@Override
	public void goToMenu() {
		// TODO Auto-generated method stub
		switchToFragment(mMainMenuFragment);
	}

	@Override
	public void replay() {
		if(gamePlayingType == GAME_TYPE_PRACTICE){
			switchToFragment(gameFragment);
		}else if(gamePlayingType == GAME_TYPE_CUSTOM){
			replayRequested = true;
			requestReplay();
		}else if(gamePlayingType == GAME_TYPE_RANKED){
			onStartRankedGameRequested();
		}
	}

	@Override
	public void onStartRankedGameRequested() {
		gamePlayingType = GAME_TYPE_RANKED;
		mMainMenuFragment.startLoading();
		// auto-match criteria to invite one random automatch opponent.  
	    // You can also specify more opponents (up to 3). 
	    Bundle am = RoomConfig.createAutoMatchCriteria(1, 1, 0);

	    // build the room config:
	    RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
	    roomConfigBuilder.setAutoMatchCriteria(am);
	    RoomConfig roomConfig = roomConfigBuilder.build();

	    // create room:
        getGamesClient().createRoom(roomConfig);

        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void scoreUpdated(int score) {
		mMainMenuFragment.setWins(score);
		gameCompleteFragment.setWins(score);
	}

	@Override
	public int getScore() {
		return score.getConsecutiveWins();
	}

	
    
}
