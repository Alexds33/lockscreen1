package com.example.lockscreem;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

public class DoLockActivity extends Activity {
	private DevicePolicyManager policyManager;
	private ComponentName componentName;
	private MediaPlayer player;

	public int musicID;

	Boolean isShake;
	Boolean isPlayMusic;
	Boolean isShowNotification;
	int songNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();
		musicID = MainActivity.music[songNum];
		System.out.println("isShake:  " + isShake);
		System.out.println("isPlayMusic:  " + isPlayMusic);
		System.out.println("isShowNotification:  " + isShowNotification);
		System.out.println("songNum:  " + songNum);
		System.out.println("musicID:  " + musicID);

		

		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		componentName = new ComponentName(DoLockActivity.this,
				LockReceiver.class);
		if (policyManager.isAdminActive(componentName)) {
			
			// ��Ч
			if (isPlayMusic) {
				// PlayerVoid();
				startService(new Intent(DoLockActivity.this, music.class));
			}

			// ��
			if (isShake) {
				TipHelper.Vibrate(DoLockActivity.this, 500);
			}
			
			// �ж��Ƿ���Ȩ��(�������豸������)
			policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			componentName = new ComponentName(DoLockActivity.this,
					LockReceiver.class);
//			policyManager.lockNow();// ֱ������
//			android.os.Process.killProcess(android.os.Process.myPid());
			 while (true)
		      {
				 policyManager.lockNow();
				 android.os.Process.killProcess(android.os.Process.myPid());
		        return;
		      }
		} else {
			activeManager();// �����豸��������ȡȨ��
		}
	}

	private void PlayerVoid() {
		startService(new Intent(DoLockActivity.this, music.class));
	}

	// �ͷŲ�������Դ
	private void releaseMediaPlayer() {
		stopService(new Intent(DoLockActivity.this, music.class));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseMediaPlayer();

	}

	@Override
	protected void onResume() {
		if (policyManager.isAdminActive(componentName)) {
			policyManager.lockNow();
			android.os.Process.killProcess(android.os.Process.myPid());
			finish(); 
		}
		super.onResume();
	}

	private void activeManager() {
		// ʹ����ʽ��ͼ����ϵͳ����������ָ�����豸������
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "һ������");
		startActivity(intent);
		DoLockActivity.this.finish();
	}

	private void initView() {
		SharedPreferences prefereces = getSharedPreferences("lockScreen", 0);
		isShake = prefereces.getBoolean("isShake", false);
		isPlayMusic = prefereces.getBoolean("isPlayMusic", false);
		isShowNotification = prefereces.getBoolean("isShowNotification", false);
		songNum = prefereces.getInt("songNum", 1);
	}

}
