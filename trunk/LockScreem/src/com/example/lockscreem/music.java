package com.example.lockscreem;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;

public class music extends Service {
	private MediaPlayer mp;
	public static int musicID;
	int songNum;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		SharedPreferences prefereces = getSharedPreferences("lockScreen", 0);
		songNum = prefereces.getInt("songNum", 1);
		musicID = MainActivity.music[songNum];
		mp = MediaPlayer.create(this, musicID);
		mp.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mp.stop();
	}

}
