package tv.sanrenxing.awang.simulatekey;

import tv.sanrenxing.awang.settings.SimulatePrefs;
import tv.sanrenxing.awang.utils.SimulateKeyUtils;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * @author aWang
 * @since 2015-3-10
 */
public class SimulateKeyService extends Service {

	private static final String TAG = "SimulateKey";

	public static final String KEY_HAS_EXTRA = "hasExtra";
	public static final String KEY_DO_ACTION = "doAction";

	/*** 增加高度 */
	public static final int DO_ACTION_ADDHEIGHT = 1;
	/*** 减小高度 */
	public static final int DO_ACTION_MINUSHEIGHT = 2;
	/*** 显示悬浮窗 */
	public static final int DO_ACTION_SHOWWINDOW = 3;
	/*** 隐藏悬浮窗 */
	public static final int DO_ACTION_HIDDENWINDOW = 4;

	protected static final int MSG_SHOW = 1;
	protected static final int MSG_HIDDEN = 2;
	protected static final int MSG_PROC_EXTRA = 3;
	protected static final int MSG_TOGGLE_BUTTONS = 4;

	/*** 双击的间隔时间定义 */
	protected static final long DOUBLE_CLICK_INTERVAL = 300;

	/*** 悬浮窗是否显示 */
	protected boolean isFwShow = false;

	protected WindowManager wm = null;
	protected LayoutParams params = null;
	protected LinearLayout floatLayout = null;

	protected Button btnMove = null;
	protected Button btnQuit = null;
	protected Button btnMenu = null;
	protected Button btnHome = null;
	protected Button btnBack = null;

	protected SimulatePrefs prefs = null;

	protected Looper mServiceLooper = null;
	protected ServiceHandler mServiceHandler = null;

	@SuppressLint("HandlerLeak")
	protected final class ServiceHandler extends Handler {

		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "handleMessage - " + msg.what);
			switch (msg.what) {
			case MSG_SHOW:
				showFloatWindow();
				break;
			case MSG_HIDDEN:
				hiddenFloatWindow();
				break;
			case MSG_PROC_EXTRA:
				processExtra((Bundle) msg.obj);
				break;
			case MSG_TOGGLE_BUTTONS:
				toggleButtons();
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	// 移动-触摸 //
	protected OnTouchListener moveListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// 这样的话，只有在移动的时候才会更新位置，不会一点击就移动了 //
			if (event.getAction() != MotionEvent.ACTION_MOVE) {
				return false;
			}
			params.x = (int) event.getRawX() - floatLayout.getMeasuredWidth()
					/ 2;
			params.y = (int) event.getRawY() - btnMove.getMeasuredHeight() / 2;
			wm.updateViewLayout(floatLayout, params);
			return false;
		}
	};

	// 移动-点击 //
	protected OnClickListener moveClickListener = new View.OnClickListener() {

		private long lastClick = 0;

		@Override
		public void onClick(View v) {
			long interval = System.currentTimeMillis() - lastClick;
			Log.d(TAG, "onClick - " + interval);
			lastClick = System.currentTimeMillis();
			if (interval < DOUBLE_CLICK_INTERVAL) {
				// 防止三击变成两个双击的问题 //
				lastClick = 0;
				mServiceHandler.sendEmptyMessage(MSG_TOGGLE_BUTTONS);
			}
		}
	};

	// 隐藏 //
	protected OnClickListener quitListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			hiddenFloatWindow();
		}
	};

	// 按键 //
	protected OnClickListener keySimulateListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String tag = (String) v.getTag();
			int keyCode = Integer.valueOf(tag);
			SimulateKeyUtils.simulateKeyEvent(keyCode);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind()");
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");
		super.onCreate();
		prefs = SimulatePrefs.getInstance(getApplicationContext());

		HandlerThread thread = new HandlerThread("SimulateKeyService",
				Process.THREAD_PRIORITY_DEFAULT);
		thread.start();
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public void onLowMemory() {
		Log.i(TAG, "onLowMemory()");
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		Log.i(TAG, "onTrimMemory()");
		super.onTrimMemory(level);
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		Log.i(TAG, "onTaskRemoved()");
		super.onTaskRemoved(rootIntent);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i(TAG, "onConfigurationChanged()");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand()");
		if (intent != null) {
			mServiceHandler.sendMessage(mServiceHandler.obtainMessage(
					MSG_PROC_EXTRA, intent.getExtras()));
		} else if (!isFwShow) {
			Log.w(TAG, "== Intent is null ==");
			if (prefs.getBoolean(SimulatePrefs.B_IS_HIDDEN, false)) {
				// 如果是手动隐藏，那么系统重启Service的时候不应该显示出来 //
			} else {
				mServiceHandler.sendEmptyMessage(MSG_SHOW);
			}
		} else {
			Log.w(TAG, "== isRuning ==");
		}
		// 系统重新创建Service时，传递最后一个Intent //
		return START_REDELIVER_INTENT;
	}

	/**
	 * 处理Intent传来的参数，进行调整
	 * 
	 * @param extras
	 */
	protected void processExtra(Bundle extras) {
		Log.i(TAG, "processExtra()");
		if (extras == null) {
			Log.w(TAG, "== extras is null ==");
			return;
		}
		int childBtnHeight = 0;
		int doAction = extras.getInt(KEY_DO_ACTION, 0);
		Log.i(TAG, "MsgID : " + doAction + ", ThreadID : "
				+ Thread.currentThread().getId());
		switch (doAction) {
		case DO_ACTION_ADDHEIGHT:
			for (int i = 0, count = floatLayout.getChildCount(); i < count; i++) {
				Button child = (Button) floatLayout.getChildAt(i);
				child.setHeight(child.getHeight() + 1);
				childBtnHeight = child.getHeight();
			}
			prefs.set(SimulatePrefs.S_BTN_HEIGHT, childBtnHeight);
			break;
		case DO_ACTION_MINUSHEIGHT:
			for (int i = 0, count = floatLayout.getChildCount(); i < count; i++) {
				Button child = (Button) floatLayout.getChildAt(i);
				child.setHeight(child.getHeight() - 1);
				childBtnHeight = child.getHeight();
			}
			prefs.set(SimulatePrefs.S_BTN_HEIGHT, childBtnHeight);
			break;
		case DO_ACTION_SHOWWINDOW:
			this.showFloatWindow();
			break;
		case DO_ACTION_HIDDENWINDOW:
			this.hiddenFloatWindow();
			break;
		}
	}

	protected boolean isButtonsShow = true;

	/**
	 * 显示/隐藏按钮
	 */
	protected void toggleButtons() {
		if (isButtonsShow) {
			hiddenButtons(0);
			isButtonsShow = false;
		} else {
			showButtons(0);
			isButtonsShow = true;
		}
	}

	/**
	 * 隐藏按钮
	 * 
	 * @param duractions
	 *            隐藏动画持续时间，如果为0表示不使用动画
	 */
	protected void hiddenButtons(int duractions) {
		if (duractions == 0) {
			for (int i = 1, count = floatLayout.getChildCount(); i < count; i++) {
				View child = floatLayout.getChildAt(i);
				child.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 显示按钮
	 * 
	 * @param duractions
	 *            显示动画持续时间，如果为0表示不使用动画
	 */
	protected void showButtons(int duractions) {
		if (duractions == 0) {
			for (int i = 1, count = floatLayout.getChildCount(); i < count; i++) {
				View child = floatLayout.getChildAt(i);
				child.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 显示悬浮窗
	 */
	public void showFloatWindow() {
		Log.i(TAG, "showFloatWindow()");
		// 如果不作处理,重复调用会导致IllegalStateException异常 //
		if (isFwShow) {
			return;
		}
		// 如果intent带参数的话，就不会直接调用initFloatWindow了，这里需要进行判断 //
		if (wm == null) {
			initFloatWindow();
		} else {
			wm.addView(floatLayout, params);
		}
		isFwShow = true;
	}

	/**
	 * 隐藏悬浮窗
	 */
	public void hiddenFloatWindow() {
		Log.i(TAG, "hiddenFloatWindow()");
		Log.d(TAG, "isFwShow - " + isFwShow);
		// 这里也要处理,防止重复调用 //
		if (isFwShow == false) {
			return;
		}
		isFwShow = false;
		wm.removeView(floatLayout);
	}

	/**
	 * 初始化并显示悬浮窗
	 */
	protected void initFloatWindow() {
		this.initParams();
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		// 根Layout //
		floatLayout = (LinearLayout) inflater
				.inflate(R.layout.float_view, null);
		this.initEvent();
		this.initView();
		wm.addView(floatLayout, params);
	}

	/**
	 * 初始化悬浮窗参数
	 */
	protected void initParams() {
		Log.i(TAG, "initParams()");
		// 注意这里需要getApplicationContext //
		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();
		params.type = LayoutParams.TYPE_SYSTEM_ALERT;
		params.format = PixelFormat.RGBA_8888;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		params.gravity = Gravity.LEFT | Gravity.TOP;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
	}

	/**
	 * 初始化悬浮窗事件
	 */
	protected void initEvent() {
		btnMove = (Button) floatLayout.findViewById(R.id.btnMove);
		btnMove.setOnTouchListener(moveListener);
		btnMove.setOnClickListener(moveClickListener);

		btnMenu = (Button) floatLayout.findViewById(R.id.btnMenu);
		btnHome = (Button) floatLayout.findViewById(R.id.btnHome);
		btnBack = (Button) floatLayout.findViewById(R.id.btnBack);
		btnMenu.setOnClickListener(keySimulateListener);
		btnHome.setOnClickListener(keySimulateListener);
		btnBack.setOnClickListener(keySimulateListener);

		btnQuit = (Button) floatLayout.findViewById(R.id.btnQuit);
		btnQuit.setOnClickListener(quitListener);
	}

	/**
	 * 初始化悬浮窗子View，调整宽高
	 */
	protected void initView() {
		DisplayMetrics dm = getApplication().getResources().getDisplayMetrics();
		int width = dm.widthPixels / floatLayout.getChildCount();
		int height = prefs.getInt(SimulatePrefs.S_BTN_HEIGHT, 0);
		for (int i = 0, count = floatLayout.getChildCount(); i < count; i++) {
			Button child = (Button) floatLayout.getChildAt(i);
			child.setPadding(0, 0, 0, 0);
			child.setWidth(width);
			if (height != 0) {
				child.setHeight(height);
			}
		}
	}
}
