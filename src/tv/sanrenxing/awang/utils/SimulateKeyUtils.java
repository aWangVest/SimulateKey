package tv.sanrenxing.awang.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author aWang
 * @since 2015-3-10
 */
public class SimulateKeyUtils {

	private static Toast toast = null;

	/**
	 * Toast��Ϣ
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showMessage(Context context, String msg) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * ģ�ⰴ����Ϣ
	 * 
	 * @param keyCode
	 */
	public static void simulateKeyEvent(int keyCode) {
		ExecTask task = new ExecTask("input keyevent " + keyCode,
				ExecTask.TAG_GET_EXEC_STATE | ExecTask.TAG_RUNAS_ROOT);
		new Thread(task).start();
	}
}
