package tv.sanrenxing.awang.simulatekey;

import tv.sanrenxing.awang.utils.SimulateKeyUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * @author aWang
 * @since 2015-3-10
 */
public class MainActivity extends Activity {

	private static final String TAG = "SimulateKey";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		showFloatWindow();
	}

	/**
	 * 显示悬浮窗
	 */
	protected void showFloatWindow() {
		Intent intent = new Intent(SimulateKeyService.NAME);
		// 第一次启动的时候，可以不带参数 //
		intent.putExtra(SimulateKeyService.KEY_HAS_EXTRA, true);
		intent.putExtra(SimulateKeyService.KEY_DO_ACTION,
				SimulateKeyService.DO_ACTION_SHOWWINDOW);
		startService(intent);
	}

	/**
	 * 隐藏悬浮窗
	 */
	protected void hiddenFloatWindow() {
		Intent intent = new Intent(SimulateKeyService.NAME);
		intent.putExtra(SimulateKeyService.KEY_HAS_EXTRA, true);
		intent.putExtra(SimulateKeyService.KEY_DO_ACTION,
				SimulateKeyService.DO_ACTION_HIDDENWINDOW);
		startService(intent);
	}

	/**
	 * 退出应用(不会隐藏悬浮窗)
	 * 
	 * @param v
	 */
	public void onClickQuit(View v) {
		Log.i(TAG, "onClickQuit()");
		this.finish();
	}

	/**
	 * 测试按钮，预留
	 * 
	 * @param v
	 */
	public void onClickTest(View v) {
		Log.i(TAG, "onClickTest()");
		SimulateKeyUtils.showMessage(getApplicationContext(), "暂未实现");
	}

	/**
	 * 悬浮窗按钮高度增加
	 * 
	 * @param v
	 */
	public void onClickAddHeight(View v) {
		Intent intent = new Intent(
				"tv.sanrenxing.awang.simulatekey.SimulateKeyService");
		intent.putExtra(SimulateKeyService.KEY_HAS_EXTRA, true);
		intent.putExtra(SimulateKeyService.KEY_DO_ACTION,
				SimulateKeyService.DO_ACTION_ADDHEIGHT);
		startService(intent);
	}

	/**
	 * 悬浮窗按钮高度减小
	 * 
	 * @param v
	 */
	public void onClickMinusHeight(View v) {
		Intent intent = new Intent(
				"tv.sanrenxing.awang.simulatekey.SimulateKeyService");
		intent.putExtra(SimulateKeyService.KEY_HAS_EXTRA, true);
		intent.putExtra(SimulateKeyService.KEY_DO_ACTION,
				SimulateKeyService.DO_ACTION_MINUSHEIGHT);
		startService(intent);
	}
}
