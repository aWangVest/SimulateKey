package tv.sanrenxing.awang.simulatekey;

import tv.sanrenxing.awang.settings.SimulatePrefs;
import tv.sanrenxing.awang.utils.SimulateKeyUtils;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
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

	public static final String NAME = "tv.sanrenxing.awang.simulatekey.SimulateKeyService";

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

	/*** 悬浮窗是否显示 */
	private boolean isFwShow = false;

	private WindowManager wm = null;
	private LayoutParams params = null;
	private LinearLayout floatLayout = null;

	private Button btnMove = null;
	private Button btnQuit = null;
	private Button btnMenu = null;
	private Button btnHome = null;
	private Button btnBack = null;

	private OnTouchListener moveListener = new View.OnTouchListener() {

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

	private OnClickListener quitListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			hiddenFloatWindow();
		}
	};

	private OnClickListener keySimulateListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String tag = (String) v.getTag();
			int keyCode = Integer.valueOf(tag);
			SimulateKeyUtils.simulateKeyEvent(keyCode);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand()");
		// 什么时候intent会为空呢？ //
		if (intent != null) {
			String action = intent.getAction();
			Log.i(TAG, "Action = " + action);
		} else {
			Log.w(TAG, "Intent is null");
		}

		boolean hasExtra = intent.getBooleanExtra(KEY_HAS_EXTRA, false);
		if (hasExtra) {
			processExtra(intent.getExtras());
		} else if (!isFwShow) {
			initFloatWindow();
			isFwShow = true;
		} else {
			Log.w(TAG, "isRuning");
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 处理Intent传来的参数，进行调整
	 * 
	 * @param extras
	 */
	protected void processExtra(Bundle extras) {
		Log.i(TAG, "processExtra()");

		int childBtnHeight = 0;
		int doAction = extras.getInt(KEY_DO_ACTION, 0);
		switch (doAction) {
		case DO_ACTION_ADDHEIGHT:
			for (int i = 0, count = floatLayout.getChildCount(); i < count; i++) {
				Button child = (Button) floatLayout.getChildAt(i);
				child.setHeight(child.getHeight() + 1);
				childBtnHeight = child.getHeight();
			}
			SimulatePrefs.getInstance(getApplicationContext()).set(
					SimulatePrefs.S_BTN_HEIGHT, childBtnHeight);
			break;
		case DO_ACTION_MINUSHEIGHT:
			for (int i = 0, count = floatLayout.getChildCount(); i < count; i++) {
				Button child = (Button) floatLayout.getChildAt(i);
				child.setHeight(child.getHeight() - 1);
				childBtnHeight = child.getHeight();
			}
			SimulatePrefs.getInstance(getApplicationContext()).set(
					SimulatePrefs.S_BTN_HEIGHT, childBtnHeight);
			break;
		case DO_ACTION_SHOWWINDOW:
			this.showFloatWindow();
			break;
		case DO_ACTION_HIDDENWINDOW:
			this.hiddenFloatWindow();
			break;
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
		int height = SimulatePrefs.getInstance(getApplicationContext()).getInt(
				SimulatePrefs.S_BTN_HEIGHT, 0);
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
