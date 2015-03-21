package tv.sanrenxing.awang.receiver;

import tv.sanrenxing.awang.settings.SimulatePrefs;
import tv.sanrenxing.awang.simulatekey.R;
import tv.sanrenxing.awang.simulatekey.SimulateKeyWinOpt;
import tv.sanrenxing.awang.utils.SimulateKeyUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 开机监听
 * 
 * @author aWang
 * @since 2015-3-21
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "SimulateKey-"
			+ BootBroadcastReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null && intent.getAction() != null) {
			Log.i(TAG, "Action : " + intent.getAction());
		}
		boolean isBootedTip = SimulatePrefs.getInstance(context).getBoolean(
				SimulatePrefs.B_IS_BOOTED_TIP, false);
		if (isBootedTip) {
			SimulateKeyUtils.showMessage(context, R.string.boot_tip);
		}
		SimulateKeyWinOpt.activateService(context);
	}
}
