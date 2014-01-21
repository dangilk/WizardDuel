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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.dan.wizardduel.R;
import com.dan.wizardduel.combat.CombatController;
import com.dan.wizardduel.spells.Spell;

public class MainActivity extends BaseGameActivity
        implements MainMenuFragment.Listener,
        GameFragment.Listener, RoomUpdateListener, RealTimeMessageReceivedListener, RoomStatusUpdateListener{

    

    private static final int RC_INVITATION_INBOX = 0;

	private static final int RC_SELECT_PLAYERS = 1;

	private static final int RC_WAITING_ROOM = 2;

	public static Random random = new Random();

	// request codes we use when invoking an external activity
    final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

    // tag for debug logging
    final boolean ENABLE_DEBUG = true;
    final String TAG = "TanC";

    // playing on hard mode?
    boolean mHardMode = false;
    
    public MainMenuFragment mMainMenuFragment = null;
    public GameFragment gameFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        enableDebugLog(ENABLE_DEBUG, TAG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create fragments
        mMainMenuFragment = new MainMenuFragment();
        gameFragment = new GameFragment();

        // listen to fragment events
        mMainMenuFragment.setListener(this);
        gameFragment.setListener(this);
        //mGameplayFragment.setListener(this);
        //mWinFragment.setListener(this);

        // add initial fragment (welcome fragment)
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                mMainMenuFragment).commit();

        // IMPORTANT: if this Activity supported rotation, we'd have to be
        // more careful about adding the fragment, since the fragment would
        // already be there after rotation and trying to add it again would
        // result in overlapping fragments. But since we don't support rotation,
        // we don't deal with that for code simplicity.


    }

    // Switch UI to the given fragment
    public void switchToFragment(Fragment newFrag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag)
                .commit();
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	gameFragment.clearHpListeners();
    	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onStartPracticeGameRequested() {
    	switchToFragment(gameFragment);
    }
    
    @Override
    public void onStartCustomGameRequested() {
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
            startActivityForResult(getGamesClient().getAllLeaderboardsIntent(), RC_UNUSED);
        } else {
            showAlert(getString(R.string.leaderboards_not_available));
        }
    }

    
    void openInvitationInbox(){
    	// launch the intent to show the invitation inbox screen
    	Intent intent = getGamesClient().getInvitationInboxIntent();
    	this.startActivityForResult(intent, RC_INVITATION_INBOX);
    }
    
    void openInvitePlayers(){
    	// launch the player selection screen
    	// minimum: 1 other player; maximum: 3 other players
    	Intent intent = getGamesClient().getSelectPlayersIntent(1, 1);
    	startActivityForResult(intent, RC_SELECT_PLAYERS);
    }
    
    void startQuickGame(){
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

    }

	@Override
	public void onGameComplete(Boolean won) {
		// TODO Auto-generated method stub
		switchToFragment(mMainMenuFragment);
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
	public void onRealTimeMessageReceived(RealTimeMessage arg0) {
		Log.e("tag","real time message received");
		// TODO Auto-generated method stub
		
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
	public void onRoomConnected(int arg0, Room arg1) {
		Log.e("tag","room connected");
		// TODO Auto-generated method stub
		
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
	    if (request == RC_INVITATION_INBOX) {
	        if (response != Activity.RESULT_OK) {
	            // canceled
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
	            // user canceled
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
	                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
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
	    }
	}

	@Override
	public void onSpellPrepped(int slot, Spell spell) {
		// TODO Auto-generated method stub
		
	}
    
}
