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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.games.Player;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.dan.wizardduel.R;
import com.dan.wizardduel.combat.CombatController;

public class MainActivity extends BaseGameActivity
        implements MainMenuFragment.Listener,
        GameFragment.Listener{

    

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
    }

    @Override
    public void onStartGameRequested(boolean hardMode) {
        startGame(hardMode);
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

    /**
     * Start gameplay. This means updating some status variables and switching
     * to the "gameplay" screen (the screen where the user types the score they want).
     *
     * @param hardMode whether to start gameplay in "hard mode".
     */
    void startGame(boolean hardMode) {
        mHardMode = hardMode;
        //switchToFragment(mGameplayFragment);
        /*Intent intent = new Intent(this,GameActivity.class);
        startActivityForResult(intent, RC_RESOLVE);
        finish();*/
        //gameFragment.startGame();
        switchToFragment(gameFragment);
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
    	if(mMainMenuFragment == null){
			return;
		}
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
    
}
