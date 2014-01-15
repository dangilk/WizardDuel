package com.dan.wizardduel;

public class GameThread extends Thread {

	private boolean running;
	public void setRunning(boolean running){
		this.running = running;
	}
	@Override
	public void run(){
		while(running){
			
		}
	}
}
