package tv.sanrenxing.awang.activity;

import tv.sanrenxing.awang.simulatekey.R;
import tv.sanrenxing.awang.simulatekey.SimulateKeyService;
import tv.sanrenxing.awang.simulatekey.SimulateKeyWinOpt;
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
}
