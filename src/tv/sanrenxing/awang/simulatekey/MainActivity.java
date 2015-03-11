package tv.sanrenxing.awang.simulatekey;

import com.tencent.bugly.crashreport.CrashReport;

import tv.sanrenxing.awang.utils.ExecTask;
import tv.sanrenxing.awang.utils.ExecTask.Callback2;
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

	protected static final String TAG = "SimulateKey";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		showFloatWindow();
	}

	protected void showFloatWindow() {
		Intent intent = new Intent(
				"tv.sanrenxing.awang.simulatekey.SimulateKeyService");
		startService(intent);
	}

	public void onClickQuit(View v) {
		Log.i(TAG, "onClickQuit()");
		this.finish();
	}

	public void onClickTest(View v) {
		Log.i(TAG, "onClickTest()");
		CrashReport.testJavaCrash();
		CrashReport.testNativeCrash();
	}
	
	public void onClickAddHeight(View v) {
		Intent intent = new Intent(
				"tv.sanrenxing.awang.simulatekey.SimulateKeyService");
		intent.putExtra(SimulateKeyService.KEY_HAS_EXTRA, true);
		intent.putExtra(SimulateKeyService.KEY_DO_ACTION, SimulateKeyService.DO_ACTION_ADDHEIGHT);
		startService(intent);
	}
	
	public void onClickMinusHeight(View v) {
		Intent intent = new Intent(
				"tv.sanrenxing.awang.simulatekey.SimulateKeyService");
		intent.putExtra(SimulateKeyService.KEY_HAS_EXTRA, true);
		intent.putExtra(SimulateKeyService.KEY_DO_ACTION, SimulateKeyService.DO_ACTION_MINUSHEIGHT);
		startService(intent);
	}

	protected void testInput() {
		Log.i(TAG, "testInput()");
		ExecTask task = new ExecTask("input", ExecTask.TAG_WITH_OUTPUT);
		task.setCallback2(new Callback2() {
			@Override
			public void callback(String line) {
				Log.d(TAG, line);
			}
		});
		new Thread(task).start();
	}

	protected void testSuInput() {
		Log.i(TAG, "testSuInput()");
		ExecTask task = new ExecTask("su input keyevent 3",
				ExecTask.TAG_WITH_OUTPUT);
		task.setCallback2(new Callback2() {
			@Override
			public void callback(String line) {
				Log.d(TAG, line);
			}
		});
		new Thread(task).start();
	}

	protected void testInputKeyevent() {
		Log.i(TAG, "testInputKeyevent()");
		ExecTask task = new ExecTask("input keyevent 3",
				ExecTask.TAG_WITH_OUTPUT);
		task.setCallback2(new Callback2() {
			@Override
			public void callback(String line) {
				Log.d(TAG, line);
			}
		});
		new Thread(task).start();
	}

	protected void testLsDataData() {
		Log.i(TAG, "testLsDataData()");
		ExecTask task = new ExecTask("ls /data/data", ExecTask.TAG_WITH_OUTPUT
				| ExecTask.TAG_RUNAS_ROOT);
		task.setCallback2(new Callback2() {
			@Override
			public void callback(String line) {
				Log.d(TAG, line);
			}
		});
		new Thread(task).start();
	}

	/**
	 * OK -> Good
	 */
	protected void testSuInputV2() {
		Log.i(TAG, "testSuInputV2()");
		ExecTask task = new ExecTask("input keyevent 3",
				ExecTask.TAG_WITH_OUTPUT | ExecTask.TAG_RUNAS_ROOT);
		task.setCallback2(new Callback2() {
			@Override
			public void callback(String line) {
				Log.d(TAG, line);
			}
		});
		new Thread(task).start();
	}
}
