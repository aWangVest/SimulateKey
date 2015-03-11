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
		// �������� //
		strategy.setAppChannel("Debug");
		// �ӳ��ύ���� //
		strategy.setAppReportDelay(3000);
		// Context, AppID, isDebug(����logcat���tagΪCrashReport����־) //
		CrashReport.initCrashReport(this, "900002350", true, strategy);
		// �����û�Ψһ��ʶ //
		CrashReport.setUserId("aWang");
	}
}
