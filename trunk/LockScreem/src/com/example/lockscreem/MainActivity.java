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

	// ����֪ͨ����Ϣ��������
	NotificationManager m_NotificationManager;
	Intent m_Intent;
	PendingIntent m_PendingIntent;
	// ����Notification����
	Notification m_Notification;

	Boolean isShake;
	Boolean isPlayMusic;
	Boolean isShowNotification;
	int songNum;

	private static final String[] musicName = { "��Ч1", "��Ч2","��Ч3","��Ч4","��Ч5","��Ч6","��Ч7" };

	static final int[] music = { R.raw.classic, R.raw.office, R.raw.office, R.raw.office, R.raw.office, R.raw.office, R.raw.office, };

	// private Animation scale_animation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		adYouMi();//����

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

		// ������Ч�Ƿ�ɵ��
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

		// �Ƿ���������ʾ��ݷ�ʽ
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
		// �Ƿ񲥷���Ч
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

		// �Ƿ���
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

		// �������
		btn_SetDone.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				initView();
				// ��
				// if (isShake) {
				// TipHelper.Vibrate(MainActivity.this, 100);
				// }
				// // ��Ч
				// if (isPlayMusic) {
				// PlayerVoid();
				// }
				policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
				componentName = new ComponentName(MainActivity.this,
						LockReceiver.class);
				if (policyManager.isAdminActive(componentName)) {// �ж��Ƿ���Ȩ��(�������豸������)
					Toast.makeText(MainActivity.this,
							"�����Ѽ���˳����ú��������������ͼ��ر���Ļ", Toast.LENGTH_SHORT)
							.show();
				} else {
					activeManager();// �����豸��������ȡȨ��
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

		// ������Ч
		btn_PlayMusic.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				PlayerVoid();
			}
		});

		// ȡ������
		btn_cancle.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
				componentName = new ComponentName(MainActivity.this,
						LockReceiver.class);
				if (policyManager.isAdminActive(componentName)) {
					policyManager.removeActiveAdmin(componentName);
					Toast.makeText(MainActivity.this, "ȡ������ɹ�����������Խ�������ֻ����Ƴ���",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this,
							"����δ����������桰ȷ������ֱ�ӵ�������������ͼ�꼤��ʹ��",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	// ����ѡ��
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
		// ��д�˷��������ڵ�һ�μ����豸������֮��������Ļ
		// if (policyManager.isAdminActive(componentName)) {
		// policyManager.lockNow();
		// android.os.Process.killProcess(android.os.Process.myPid());
		// }
		super.onResume();
	}

	private void activeManager() {
		// ʹ����ʽ��ͼ����ϵͳ����������ָ�����豸������
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "һ������");
		startActivity(intent);
	}

	private void PlayerVoid() {
		startService(new Intent(MainActivity.this, music.class));
	}

	// �ͷŲ�������Դ
	private void releaseMediaPlayer() {
		stopService(new Intent(MainActivity.this, music.class));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		releaseMediaPlayer();
	}

	/** ���� */
	private void showNotification() {
		addShortcut();
	}

	/** ȡ�� */
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
	 * Ϊ���򴴽������ݷ�ʽ
	 */
	private void addShortcut() {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// ��ݷ�ʽ������
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.dolock));
		shortcut.putExtra("duplicate", false); // �������ظ�����

		// ָ����ǰ��ActivityΪ��ݷ�ʽ�����Ķ���: �� //com.everest.video.VideoPlayer
		// ע��: ComponentName�ĵڶ�������������ϵ��(.)�������ݷ�ʽ�޷�������Ӧ����
		// ComponentName comp = new ComponentName(this.getPackageName(),
		// "."+DoLockActivity.class.getName());
		// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
		// Intent.ACTION_MAIN).setComponent(comp));
		
		Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
		shortcutIntent.setClassName(this, DoLockActivity.class.getName());
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		// ��ݷ�ʽ��ͼ��
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				this, R.drawable.dolock);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		sendBroadcast(shortcut);
	}

	private void adYouMi() {
		// Ӧ�� Id Ӧ������ ��������� (s) ���ò���ģʽ [false Ϊ����ģʽ ]
		AdManager.init(this,"178f73366c471d40", "1b42bfc082f3bd19", 30, false);
		LinearLayout adViewLayout = (LinearLayout) findViewById(R.id.adViewLayout);
		adViewLayout.addView(new AdView(this),
		new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
		LinearLayout.LayoutParams.WRAP_CONTENT));
	}
	


}
