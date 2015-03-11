package tv.sanrenxing.awang.simulatekey;

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

	public static final String KEY_HAS_EXTRA = "hasExtra";
	public static final String KEY_DO_ACTION = "doAction";

	public static final int DO_ACTION_ADDHEIGHT = 1;
	public static final int DO_ACTION_MINUSHEIGHT = 2;

	private boolean isRuning = false;

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
			/* �����Ļ���ֻ�����ƶ���ʱ��Ż����λ�ã�����һ������ƶ���[2014-9-13 16:57:34] */
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
			isRuning = false;
			wm.removeView(floatLayout);
			Utils.showMessage(getApplicationContext(), btnQuit.getText()
					.toString());
		}
	};

	private OnClickListener keySimulateListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String tag = (String) v.getTag();
			int keyCode = Integer.valueOf(tag);
			Utils.simulateKeyEvent(keyCode);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		Log.i(TAG, "onStartCommand()");
		Log.i(TAG, "Action = " + action);
		if (!isRuning) {
			initFloatWindowV2();
			isRuning = true;
		} else {
			boolean hasExtra = intent.getBooleanExtra(KEY_HAS_EXTRA, false);
			if (hasExtra) {
				processExtra(intent.getExtras());
			} else {
				Log.w(TAG, "isRuning");
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	protected void processExtra(Bundle extras) {
		Log.i(TAG, "processExtra()");
		int doAction = extras.getInt(KEY_DO_ACTION, 0);
		switch (doAction) {
		case DO_ACTION_ADDHEIGHT:
			for (int i = 0, count = floatLayout.getChildCount(); i < count; i++) {
				Button child = (Button) floatLayout.getChildAt(i);
				child.setHeight(child.getHeight() + 1);
			}
			break;
		case DO_ACTION_MINUSHEIGHT:
			for (int i = 0, count = floatLayout.getChildCount(); i < count; i++) {
				Button child = (Button) floatLayout.getChildAt(i);
				child.setHeight(child.getHeight() - 1);
			}
			break;
		}
	}

	protected void initParams() {
		Log.i(TAG, "initParams()");
		/* ����ע����ҪgetApplicationContext */
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

	protected void initFloatWindow() {
		Log.i(TAG, "initFloatWindow()");
		this.initParams();

		/* ��Layout */
		floatLayout = new LinearLayout(getApplicationContext());
		floatLayout.setOrientation(LinearLayout.VERTICAL);

		/* ��ԭ���Ļ����������Լ���Ҫ��View */
		btnMove = new Button(getApplicationContext());
		btnMove.setText("  �� �ƶ� ��  ");
		btnMove.setBackgroundColor(0xFFDADADA);
		btnMove.setTextColor(0xFFFFFFFF);
		btnMove.setOnTouchListener(moveListener);

		btnQuit = new Button(getApplicationContext());
		btnQuit.setText("  �˳���Ӧ��  ");
		btnQuit.setBackgroundColor(0xFFDADADA);
		btnQuit.setTextColor(0xFFFFFFFF);
		btnQuit.setOnClickListener(quitListener);

		floatLayout.addView(btnMove);
		floatLayout.addView(btnQuit);
		wm.addView(floatLayout, params);
	}

	protected void initFloatWindowV2() {
		this.initParams();
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		/* ��Layout */
		floatLayout = (LinearLayout) inflater
				.inflate(R.layout.float_view, null);

		int fWidth = floatLayout.getWidth();
		int fHeight = floatLayout.getHeight();
		Log.d(TAG, "fWidth:" + fWidth + ", fHeight:" + fHeight);

		DisplayMetrics dm = getApplication().getResources().getDisplayMetrics();
		Log.d(TAG, "widthPixels:" + dm.widthPixels);
		Log.d(TAG, "heightPixels:" + dm.heightPixels);
		Log.d(TAG, "density:" + dm.density);
		Log.d(TAG, "densityDpi:" + dm.densityDpi);
		Log.d(TAG, "xdpi:" + dm.xdpi);
		Log.d(TAG, "ydpi:" + dm.ydpi);
		Log.d(TAG, "scaledDensity:" + dm.scaledDensity);

		this.initEventV2();
		this.initViewV2();
		wm.addView(floatLayout, params);
		floatLayout.post(new Runnable() {

			@Override
			public void run() {
				int fWidth = floatLayout.getWidth();
				int fHeight = floatLayout.getHeight();
				Log.d(TAG, "fWidth:" + fWidth + ", fHeight:" + fHeight);
			}
		});
	}

	protected void initEventV2() {
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

	protected void initViewV2() {
		DisplayMetrics dm = getApplication().getResources().getDisplayMetrics();
		int width = dm.widthPixels / floatLayout.getChildCount();
		Log.i(TAG, "Child = " + floatLayout.getChildCount());
		for (int i = 0, count = floatLayout.getChildCount(); i < count; i++) {
			Button child = (Button) floatLayout.getChildAt(i);
			child.setWidth(width);
			Log.d(TAG, child.getText() + "#Left:" + child.getPaddingLeft()
					+ ", Right:" + child.getPaddingRight());
			Log.d(TAG, child.getText() + "#Top:" + child.getPaddingTop()
					+ ", Bottom:" + child.getPaddingBottom());
			child.setPadding(0, 0, 0, 0);
		}
	}
}