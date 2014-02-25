/* Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dan.wizardduel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.todddavies.components.progressbar.ProgressWheel;

/**
 * Fragment with the main menu for the game. The main menu allows the player
 * to choose a gameplay mode (Easy or Hard), and click the buttons to
 * show view achievements/leaderboards.
 *
 * @author Bruno Oliveira (Google)
 *
 */
public class MainMenuFragment extends Fragment implements OnClickListener {
    String mGreeting = "Hello, anonymous user (not signed in)";

    public interface Listener {
        public void onStartPracticeGameRequested();
        public void onStartCustomGameRequested();
        public void onStartRankedGameRequested();
        public void onShowAchievementsRequested();
        public void onShowLeaderboardsRequested();
        public void onSignInButtonClicked();
        public void onSignOutButtonClicked();
        public int getScore();
    }

    Listener mListener = null;
    boolean mShowSignIn = true;
    RelativeLayout rlLoading;
    ProgressWheel pwLoading;
    boolean isLoading = false;
    TextView tvWins;
    private int wins;
    LinearLayout llWins;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mainmenu, container, false);
        final int[] CLICKABLES = new int[] {
                R.id.practice_mode_button, R.id.custom_mode_button,
                R.id.ranked_mode_button, R.id.show_leaderboards_button,
                R.id.sign_in_button, R.id.sign_out_button
        };
        
        for (int i : CLICKABLES) {
            v.findViewById(i).setOnClickListener(this);
        }
        rlLoading = (RelativeLayout)v.findViewById(R.id.rlMenuLoading);
        pwLoading = (ProgressWheel)v.findViewById(R.id.pwMenuloading);
        tvWins = (TextView)v.findViewById(R.id.tvWinCount);
        llWins = (LinearLayout)v.findViewById(R.id.llMenuWins);
        setWins(wins);
        pwLoading.spin();
        return v;
    }
    
    public void setWins(int wins){
    	if(llWins != null && wins > 0){
    		llWins.setVisibility(View.VISIBLE);
    		tvWins.setText(Integer.toString(wins));
    	}
    	this.wins = wins;
    }
    
    public void startLoading(){
    	rlLoading.setVisibility(View.VISIBLE);
    	isLoading = true;
    }
    
    public void stopLoading(){
    	rlLoading.setVisibility(View.GONE);
    	isLoading = false;
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUi();
    }

    public void setGreeting(String greeting) {
        mGreeting = greeting;
        updateUi();
    }

    void updateUi() {
        if (getActivity() == null) return;
        //TextView tv = (TextView) getActivity().findViewById(R.id.hello);
        //if (tv != null) tv.setText(mGreeting);

        getActivity().findViewById(R.id.sign_in_bar).setVisibility(mShowSignIn ?
                View.VISIBLE : View.GONE);
        getActivity().findViewById(R.id.sign_out_bar).setVisibility(mShowSignIn ?
                View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
    	if(isLoading){
    		return;
    	}
        int id = view.getId();
		if (id == R.id.practice_mode_button) {
			mListener.onStartPracticeGameRequested();
		} else if (id == R.id.custom_mode_button) {
			mListener.onStartCustomGameRequested();
		} else if (id == R.id.ranked_mode_button) {
			mListener.onStartRankedGameRequested();
		} else if (id == R.id.show_leaderboards_button) {
			mListener.onShowLeaderboardsRequested();
		} else if (id == R.id.sign_in_button) {
			mListener.onSignInButtonClicked();
		} else if (id == R.id.sign_out_button) {
			mListener.onSignOutButtonClicked();
		}
    }

    public void setShowSignInButton(boolean showSignIn) {
        mShowSignIn = showSignIn;
        updateUi();
    }
}
