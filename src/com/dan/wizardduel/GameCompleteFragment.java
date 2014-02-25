package com.dan.wizardduel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameCompleteFragment extends Fragment implements OnClickListener{
	
	private TextView tvResult;
	private boolean won = false;
	private TextView tvWins;
	private LinearLayout llWins;
	private int wins;
	
	public interface Listener{
		public void goToMenu();
		public void replay();
		public int getScore();
	}
	
	private Listener listener = null;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gameover, container, false);
        final int[] CLICKABLES = new int[] {
                R.id.btMenu, R.id.btRematch
        };
        
        for (int i : CLICKABLES) {
            v.findViewById(i).setOnClickListener(this);
        }
        
        tvResult = (TextView)v.findViewById(R.id.tvGameResult);
        tvWins = (TextView)v.findViewById(R.id.tvCompleteWinCount);
        llWins = (LinearLayout)v.findViewById(R.id.llCompleteWins);
        setWins(wins);
        setResult();
        return v;
    }
	
	public void setWins(int score){
		if(llWins != null){
			llWins.setVisibility(View.VISIBLE);
			tvWins.setText(Integer.toString(score));
		}
		wins = score;
	}
	
	public void setResult(){
		if(won){
			tvResult.setText("you won!");
		}else{
			tvResult.setText("you lost...");
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.btMenu){
			listener.goToMenu();
		}else if(id == R.id.btRematch){
			listener.replay();
		}
	}

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public boolean isWon() {
		return won;
	}

	public void setWon(boolean won) {
		this.won = won;
	}
}
