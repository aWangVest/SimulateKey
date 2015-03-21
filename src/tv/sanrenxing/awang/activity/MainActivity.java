package tv.sanrenxing.awang.activity;

import tv.sanrenxing.awang.settings.SimulatePrefs;
import tv.sanrenxing.awang.simulatekey.R;
import tv.sanrenxing.awang.simulatekey.SimulateKeyService;
import tv.sanrenxing.awang.simulatekey.SimulateKeyWinOpt;
import tv.sanrenxing.awang.utils.SimulateKeyUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * @author aWang
 * @since 2015-3-10
 */
public class MainActivity extends Activity {

	private static final String TAG = "SimulateKey-"
			+ MainActivity.class.getSimpleName();

	private SimulatePrefs prefs = null;
	private Button btnSetBootTip = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		prefs = SimulatePrefs.getInstance(this);
		btnSetBootTip = (Button) findViewById(R.id.btnSetBootTip);
		if (prefs.getBoolean(SimulatePrefs.B_IS_BOOTED_TIP, false)) {
			btnSetBootTip.setText(R.string.btn_set_boot_tip_false);
		} else {
			btnSetBootTip.setText(R.string.btn_set_boot_tip_true);
		}

		SimulateKeyWinOpt.showFloatWindow(getApplicationContext());
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
		// SimulateKeyUtils.showMessage(getApplicationContext(), "暂未实现"); //
		Intent intent = new Intent(this, SimulateKeyService.class);
		startService(intent);
	}

	/**
	 * 显示悬浮窗
	 * 
	 * @param v
	 */
	public void onClickShowFlowWindow(View v) {
		Log.i(TAG, "onClickShowFlowWindow()");
		SimulateKeyWinOpt.showFloatWindow(getApplicationContext());
	}

	/**
	 * 隐藏悬浮窗
	 * 
	 * @param v
	 */
	public void onClickHiddenFlowWindow(View v) {
		Log.i(TAG, "onClickHiddenFlowWindow()");
		SimulateKeyWinOpt.hiddenFloatWindow(getApplicationContext());
	}

	/**
	 * 悬浮窗按钮高度增加
	 * 
	 * @param v
	 */
	public void onClickAddHeight(View v) {
		Log.i(TAG, "onClickAddHeight()");
		SimulateKeyWinOpt.btnAddHeight(getApplicationContext());
	}

	/**
	 * 悬浮窗按钮高度减小
	 * 
	 * @param v
	 */
	public void onClickMinusHeight(View v) {
		Log.i(TAG, "onClickMinusHeight()");
		SimulateKeyWinOpt.btnMinusHeight(getApplicationContext());
	}

	/**
	 * 设置按钮背景色
	 * 
	 * @param v
	 */
	public void onClickSetButtonBg(View v) {
		Log.i(TAG, "onClickSetButtonBg()");
		// TODO : 实现取色盘 //
		SimulateKeyUtils.showMessage(getApplicationContext(), "暂未实现");
	}

	/**
	 * 设置文字颜色
	 * 
	 * @param v
	 */
	public void onClickSetTextColor(View v) {
		Log.i(TAG, "onClickSetTextColor()");
		// TODO : 实现取色盘 //
		SimulateKeyUtils.showMessage(getApplicationContext(), "暂未实现");
	}

	/**
	 * 设置开机提示
	 * 
	 * @param v
	 */
	public void onClickSetBootTip(View v) {
		Log.i(TAG, "onClickSetBootTip()");
		boolean isBootedTip = prefs.getBoolean(SimulatePrefs.B_IS_BOOTED_TIP,
				false);
		prefs.set(SimulatePrefs.B_IS_BOOTED_TIP, !isBootedTip);
		if (isBootedTip) {
			btnSetBootTip.setText(R.string.btn_set_boot_tip_true);
		} else {
			btnSetBootTip.setText(R.string.btn_set_boot_tip_false);
		}
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart()");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.d(TAG, "onRestart()");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause()");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop()");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy()");
		super.onDestroy();
	}
}
