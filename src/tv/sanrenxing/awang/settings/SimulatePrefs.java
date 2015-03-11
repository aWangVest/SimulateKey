package tv.sanrenxing.awang.settings;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SimulateKey设置
 * 
 * @author aWang
 * @since 2015-3-11
 */
public class SimulatePrefs {

	public static final String S_BTN_WIDTH = "btn_width";
	public static final String S_BTN_HEIGHT = "btn_height";
	public static final String S_BTN_BG_COLOR = "btn_bg_color";
	public static final String S_BTN_TEXT_COLOR = "btn_text_color";
	
	///////////////////////////////////////////////////////
	
	private static final String NAME = "SimulateSettings";

	private static SimulatePrefs instance = null;

	private SharedPreferences prefs = null;
	private Editor editor = null;

	/*** 是否延迟保存 */
	private boolean isDelaySave = false;

	private SimulatePrefs(Context context) {
		prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		editor = prefs.edit();
	}

	/**
	 * 获取设置类实例
	 * 
	 * @return
	 */
	public static SimulatePrefs getInstance(Context context) {
		if (instance == null) {
			synchronized (SimulatePrefs.class) {
				if (instance == null) {
					instance = new SimulatePrefs(context);
				}
			}
		}
		return instance;
	}

	/**
	 * 设置延迟保存(默认不延迟)
	 * 
	 * @param isDelaySave
	 *            是否延迟保存
	 * @return SimulatePrefs的实例，方便链式调用
	 */
	public SimulatePrefs setDelaySave(boolean isDelaySave) {
		this.isDelaySave = isDelaySave;
		return instance;
	}

	/**
	 * 获取SharedPreferences对象实例
	 * 
	 * @return
	 */
	public SharedPreferences getPrefs() {
		return prefs;
	}

	///////////////////////////////////////////////////////////
	
	public void set(String key, int value) {
		editor.putInt(key, value);
		if (!isDelaySave) {
			editor.commit();
		}
	}

	public void set(String key, boolean value) {
		editor.putBoolean(key, value);
		if (!isDelaySave) {
			editor.commit();
		}
	}

	public void set(String key, float value) {
		editor.putFloat(key, value);
		if (!isDelaySave) {
			editor.commit();
		}
	}

	public void set(String key, long value) {
		editor.putLong(key, value);
		if (!isDelaySave) {
			editor.commit();
		}
	}

	public void set(String key, String value) {
		editor.putString(key, value);
		if (!isDelaySave) {
			editor.commit();
		}
	}
	
	public void set(String key, Set<String> values) {
		editor.putStringSet(key, values);
		if (!isDelaySave) {
			editor.commit();
		}
	}

	public int getInt(String key, int defValue) {
		return prefs.getInt(key, defValue);
	}

	public boolean getBoolean(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}
	
	public String getString(String key, String defValue) {
		return prefs.getString(key, defValue);
	}

	public Set<String> getStringSet(String key, Set<String> defValues) {
		return prefs.getStringSet(key, defValues);
	}

	public long getLong(String key, long defValue) {
		return prefs.getLong(key, defValue);
	}

	public float getFloat(String key, float defValue) {
		return prefs.getFloat(key, defValue);
	}
}
