package tv.sanrenxing.awang.receiver;

import tv.sanrenxing.awang.simulatekey.SimulateKeyWinOpt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * 屏幕状态改变监听，还是为了唤醒Service(本Receiver只能动态注册) <br>
 * 
 * http://stackoverflow.com/questions/1588061/android-how-to-receive-broadcast-
 * intents-action-screen-on-off
 * <p>
 * http://stackoverflow.com/questions/12681884/android-broadcast-receiver-doesnt
 * -receive-action-screen-on
 * <p>
 * 
 * @author aWang
 * @since 2015-3-21
 */
public class ScreenStateReceiver extends BroadcastReceiver {

	private static final String TAG = "SimulateKey-"
			+ ScreenStateReceiver.class.getSimpleName();

	private static ScreenStateReceiver receiver = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null && intent.getAction() != null) {
			Log.i(TAG, "Action : " + intent.getAction());
		}
		SimulateKeyWinOpt.activateService(context);
	}

	/**
	 * 注册监听
	 * 
	 * @param context
	 */
	public static void register(Context context) {
		Log.d(TAG, "register()");
		if (receiver != null) {
			unregister(context);
		}
		receiver = new ScreenStateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		context.registerReceiver(receiver, filter);
	}

	/**
	 * 反注册监听
	 * 
	 * @param context
	 */
	public static void unregister(Context context) {
		Log.d(TAG, "unregister()");
		context.unregisterReceiver(receiver);
	}
}
