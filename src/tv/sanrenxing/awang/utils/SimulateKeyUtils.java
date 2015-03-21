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
	 * Toast消息
	 * 
	 * @param context
	 * @param msg
	 *            消息字符串
	 */
	public static void showMessage(Context context, String msg) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * Toast消息
	 * 
	 * @param context
	 * @param resId
	 *            资源ID
	 */
	public static void showMessage(Context context, int resId) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, context.getString(resId),
				Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * 模拟按键消息
	 * 
	 * @param keyCode
	 */
	public static void simulateKeyEvent(int keyCode) {
		ExecTask task = new ExecTask("input keyevent " + keyCode,
				ExecTask.TAG_GET_EXEC_STATE | ExecTask.TAG_RUNAS_ROOT);
		new Thread(task).start();
	}
}