package com.example.lockscreem;

import net.youmi.android.AdManager;
import net.youmi.android.AdView;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	private DevicePolicyManager policyManager;
	private ComponentName componentName;
	private Button btn_SetDone;
	private Button btn_PlayMusic;
	private Button btn_cancle;
	private int musicID;

	private CheckBox ckb_showOnNotification;
	private CheckBox ckb_isPlayMusic;
	private CheckBox ckb_isShake;
	private Spinner spin_musicName;

	// 声明通知（消息）管理器
	NotificationManager m_NotificationManager;
	Intent m_Intent;
	PendingIntent m_PendingIntent;
	// 声明Notification对象
	Notification m_Notification;

	Boolean isShake;
	Boolean isPlayMusic;
	Boolean isShowNotification;
	int songNum;

	private static final String[] musicName = { "音效1", "音效2","音效3","音效4","音效5","音效6","音效7" };

	static final int[] music = { R.raw.classic, R.raw.office, R.raw.office, R.raw.office, R.raw.office, R.raw.office, R.raw.office, };

	// private Animation scale_animation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		adYouMi();//有米

		initView();

		musicID = music[songNum];

		m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		ckb_showOnNotification = (CheckBox) findViewById(R.id.ckb_show_on_tittlebar);
		ckb_isPlayMusic = (CheckBox) findViewById(R.id.ckb_is_play_music);
		ckb_isShake = (CheckBox) findViewById(R.id.ckb_is_shake);
		spin_musicName = (Spinner) findViewById(R.id.spinner_music);
		btn_SetDone = (Button) findViewById(R.id.btn_set_done);
		btn_PlayMusic = (Button) findViewById(R.id.btn_playmusic);
		btn_cancle = (Button) findViewById(R.id.btn_cancle);

		// 设置音效是否可点击
		spin_musicName.setClickable(isPlayMusic);
		btn_PlayMusic.setEnabled(isPlayMusic);

		if (isShowNotification) {
			showNotification();
		} else {
			cancelNotification();
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item, musicName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_musicName.setAdapter(adapter);
		spin_musicName.setSelection(songNum);
		spin_musicName.setOnItemSelectedListener(new SpinnerSelectedListener());
		if (isShowNotification != null) {
			ckb_showOnNotification.setChecked(isShowNotification);
		}
		if (isShake != null) {
			ckb_isShake.setChecked(isShake);
		}
		if (isPlayMusic != null) {
			ckb_isPlayMusic.setChecked(isPlayMusic);
		}

		// 是否在桌面显示快捷方式
		ckb_showOnNotification
				.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							showNotification();
						} else {
							cancelNotification();
						}
						SharedPreferences prefereces = getSharedPreferences(
								"lockScreen", 0);
						prefereces.edit()
								.putBoolean("isShowNotification", isChecked)
								.commit();

					}
				});
		// 是否播放音效
		ckb_isPlayMusic
				.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SharedPreferences prefereces = getSharedPreferences(
								"lockScreen", 0);
						prefereces.edit().putBoolean("isPlayMusic", isChecked)
								.commit();

						spin_musicName.setClickable(isChecked);
						btn_PlayMusic.setEnabled(isChecked);
					}
				});

		// 是否振动
		ckb_isShake
				.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SharedPreferences prefereces = getSharedPreferences(
								"lockScreen", 0);
						prefereces.edit().putBoolean("isShake", isChecked)
								.commit();

					}
				});

		// 完成设置
		btn_SetDone.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				initView();
				// 振动
				// if (isShake) {
				// TipHelper.Vibrate(MainActivity.this, 100);
				// }
				// // 音效
				// if (isPlayMusic) {
				// PlayerVoid();
				// }
				policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
				componentName = new ComponentName(MainActivity.this,
						LockReceiver.class);
				if (policyManager.isAdminActive(componentName)) {// 判断是否有权限(激活了设备管理器)
					Toast.makeText(MainActivity.this,
							"程序已激活，退出设置后点击“快捷锁屏”图标关闭屏幕", Toast.LENGTH_SHORT)
							.show();
				} else {
					activeManager();// 激活设备管理器获取权限
				}
				
				new Thread(){
					public void run() {
						try {
							sleep(500);
							MainActivity.this.finish();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					};
				}.start();
				

			}
		});

		// 试听音效
		btn_PlayMusic.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				PlayerVoid();
			}
		});

		// 取消激活
		btn_cancle.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
				componentName = new ComponentName(MainActivity.this,
						LockReceiver.class);
				if (policyManager.isAdminActive(componentName)) {
					policyManager.removeActiveAdmin(componentName);
					Toast.makeText(MainActivity.this, "取消激活成功，现在你可以将程序从手机上移除了",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this,
							"程序还未激活，请点击下面“确定”或直接点击“快捷锁屏”图标激活使用",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	// 音乐选择
	class SpinnerSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			musicID = music[arg2];
			SharedPreferences prefereces = getSharedPreferences("lockScreen", 0);
			prefereces.edit().putInt("songNum", arg2).commit();
			initView();
			System.out.println("songNum:" + songNum);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	@Override
	protected void onResume() {
		// 重写此方法用来在第一次激活设备管理器之后锁定屏幕
		// if (policyManager.isAdminActive(componentName)) {
		// policyManager.lockNow();
		// android.os.Process.killProcess(android.os.Process.myPid());
		// }
		super.onResume();
	}

	private void activeManager() {
		// 使用隐式意图调用系统方法来激活指定的设备管理器
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏");
		startActivity(intent);
	}

	private void PlayerVoid() {
		startService(new Intent(MainActivity.this, music.class));
	}

	// 释放播放器资源
	private void releaseMediaPlayer() {
		stopService(new Intent(MainActivity.this, music.class));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		releaseMediaPlayer();
	}

	/** 设置 */
	private void showNotification() {
		addShortcut();
	}

	/** 取消 */
	private void cancelNotification() {
//		delShortcut();
	}

	private void initView() {
		SharedPreferences prefereces = getSharedPreferences("lockScreen", 0);
		isShake = prefereces.getBoolean("isShake", false);
		isPlayMusic = prefereces.getBoolean("isPlayMusic", false);
		isShowNotification = prefereces.getBoolean("isShowNotification", false);
		songNum = prefereces.getInt("songNum", 1);

	}

	/**
	 * 为程序创建桌面快捷方式
	 */
	private void addShortcut() {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.dolock));
		shortcut.putExtra("duplicate", false); // 不允许重复创建

		// 指定当前的Activity为快捷方式启动的对象: 如 //com.everest.video.VideoPlayer
		// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
		// ComponentName comp = new ComponentName(this.getPackageName(),
		// "."+DoLockActivity.class.getName());
		// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
		// Intent.ACTION_MAIN).setComponent(comp));
		
		Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
		shortcutIntent.setClassName(this, DoLockActivity.class.getName());
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		// 快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				this, R.drawable.dolock);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		sendBroadcast(shortcut);
	}

	private void adYouMi() {
		// 应用 Id 应用密码 广告请求间隔 (s) 设置测试模式 [false 为发布模式 ]
		AdManager.init(this,"178f73366c471d40", "1b42bfc082f3bd19", 30, false);
		LinearLayout adViewLayout = (LinearLayout) findViewById(R.id.adViewLayout);
		adViewLayout.addView(new AdView(this),
		new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
		LinearLayout.LayoutParams.WRAP_CONTENT));
	}
	


}
