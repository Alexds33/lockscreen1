package com.example.lockscreem;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

public class TipHelper {
	  /**
	   * �ֻ���
	   * @param activity
	   * @param milliseconds ��ʱ�� MS
	   */
		  public static void Vibrate(final Activity activity, long milliseconds) {
		   Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		   vib.vibrate(milliseconds);
		  }
		  public static void Vibrate(final Activity activity, long[] pattern,boolean isRepeat) {
		   Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		   vib.vibrate(pattern, isRepeat ? 1 : -1);
		  }

}