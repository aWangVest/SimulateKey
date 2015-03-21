package tv.sanrenxing.awang.receiver;

import tv.sanrenxing.awang.simulatekey.SimulateKeyWinOpt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 网络状态改变监听
 * 
 * @author aWang
 * @since 2015-3-21
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

	private static final String TAG = "SimulateKey-"
			+ ConnectionChangeReceiver.class.getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null && intent.getAction() != null) {
			Log.i(TAG, "Action : " + intent.getAction());
		}
		SimulateKeyWinOpt.activateService(context);
	}

}
