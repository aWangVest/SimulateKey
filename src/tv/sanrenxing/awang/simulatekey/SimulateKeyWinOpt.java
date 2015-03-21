package tv.sanrenxing.awang.simulatekey;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 模拟按键窗口操作类(其实可以改成AIDL的方式来和Service通讯，就不用这么多方法和步骤了)
 * 
 * @author aWang
 * @since 2015-3-11
 */
public class SimulateKeyWinOpt {
	
	private static final String TAG = "SimulateKey-"
			+ SimulateKeyWinOpt.class.getSimpleName();

	/**
	 * 激活Service，如果Service被杀之后可以很快复活
	 * 
	 * @param context
	 */
	public static void activateService(Context context) {
		Log.d(TAG, "activateService()");
		Intent intent = new Intent(context, SimulateKeyService.class);
		// 激活不带参数 //
		context.startService(intent);
	}

	/**
	 * 显示悬浮窗
	 * 
	 * @param context
	 */
	public static void showFloatWindow(Context context) {
		Log.d(TAG, "showFloatWindow()");
		Intent intent = new Intent(context, SimulateKeyService.class);
		// 第一次启动的时候，可以不带参数 //
		intent.putExtra(SimulateKeyService.KEY_HAS_EXTRA, true);
		intent.putExtra(SimulateKeyService.KEY_DO_ACTION,
				SimulateKeyService.DO_ACTION_SHOWWINDOW);
		context.startService(intent);
	}

	/**
	 * 隐藏悬浮窗
	 * 
	 * @param context
	 */
	public static void hiddenFloatWindow(Context context) {
		Log.d(TAG, "hiddenFloatWindow()");
		Intent intent = new Intent(context, SimulateKeyService.class);
		intent.putExtra(SimulateKeyService.KEY_HAS_EXTRA, true);
		intent.putExtra(SimulateKeyService.KEY_DO_ACTION,
				SimulateKeyService.DO_ACTION_HIDDENWINDOW);
		context.startService(intent);
	}

	/**
	 * 悬浮窗按钮高度增加
	 * 
	 * @param context
	 */
	public static void btnAddHeight(Context context) {
		Log.d(TAG, "btnAddHeight()");
		Intent intent = new Intent(context, SimulateKeyService.class);
		intent.putExtra(SimulateKeyService.KEY_HAS_EXTRA, true);
		intent.putExtra(SimulateKeyService.KEY_DO_ACTION,
				SimulateKeyService.DO_ACTION_ADDHEIGHT);
		context.startService(intent);
	}

	/**
	 * 悬浮窗按钮高度减小
	 * 
	 * @param context
	 */
	public static void btnMinusHeight(Context context) {
		Log.d(TAG, "btnMinusHeight()");
		Intent intent = new Intent(context, SimulateKeyService.class);
		intent.putExtra(SimulateKeyService.KEY_HAS_EXTRA, true);
		intent.putExtra(SimulateKeyService.KEY_DO_ACTION,
				SimulateKeyService.DO_ACTION_MINUSHEIGHT);
		context.startService(intent);
	}
}
