package tv.sanrenxing.awang.simulatekey;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.CrashReport.UserStrategy;

import android.app.Application;

/**
 * @author aWang
 * @since 2015-3-11
 */
public class SimulateKeyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		UserStrategy strategy = new UserStrategy(this);
		// 渠道名称 //
		strategy.setAppChannel("Debug");
		// 延迟提交处理 //
		strategy.setAppReportDelay(3000);
		// Context, AppID, isDebug(会在logcat输出tag为CrashReport的日志) //
		CrashReport.initCrashReport(this, "900002350", true, strategy);
		// 设置用户唯一标识 //
		CrashReport.setUserId("aWang");
	}
}