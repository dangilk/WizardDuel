package com.dan.wizardduel;

import android.util.Log;

import com.google.android.gms.games.Player;
import com.google.example.games.basegameutils.BaseGameActivity;

public class WDGameActivity extends BaseGameActivity{
	// Fragments
    MainMenuFragment mMainMenuFragment = null;
    
    public WDGameActivity(){
    	
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
	
	

}
