package tv.sanrenxing.awang.simulatekey;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * ����ִ������
 * 
 * @author aWang
 * @since 2014-10-08 17:11:53
 * 
 */
public class ExecTask extends Thread {

	/** �ƺ�ʲô�¶������� */
	private static int TAG_DEFALUT = 0;
	/**
	 * ��Ҫ��ȡ���(���Callback2ʹ��) <br>
	 * �ص� {@code public void callback(String line);} ����
	 **/
	public static final int TAG_WITH_OUTPUT = 0x01;
	/**
	 * ��Ҫ��ȡ���(�����Callbackʹ�ã�Ҳ�ɵ���getOutput()��ȡ <br>
	 * �ص� {@code public void callback(int exitCode, String results);} ����
	 */
	public static final int TAG_GET_OUTPUT = 0x02;
	/** ��ROOT���ִ�� */
	public static final int TAG_RUNAS_ROOT = 0x04;
	/** ���ִ�����֮���˳�ROOT */
	public static final int TAG_EXIT_ROOT = 0x08;
	/**
	 * �ȴ�����ִ����� <br>
	 * ��ͨ��{@code getExitCode}������ȡ�˳���
	 * */
	public static final int TAG_GET_EXEC_STATE = 0x10;

	private static final String TAG = "Logger-"
			+ ExecTask.class.getSimpleName();

	public static final int STATUS_START = 0;
	public static final int STATUS_PAUSE = 1;
	public static final int STATUS_STOP = 2;
	public static final int STATUS_PAUSE_WITH_READ = 3;
	public static final int STATUS_UNKNOWN = -1;

	/**
	 * ��ȡInputStream֮��readʧ��
	 */
	private static final int EXIT_CODE_GETINPUTSTREAM_ERROR = -100;
	/**
	 * ��ȡOutputStreamֵwriteʧ��
	 */
	private static final int EXIT_CODE_EPIPE_ERROR = -101;

	private Process process = null;
	private BufferedReader reader = null;
	private DataOutputStream writer = null;

	private int tag = 0;
	private String command = null;
	private List<String> inputs = null;

	/**
	 * ��Ĭ�ϵķ�ʽִ������
	 * 
	 * @param command
	 */
	public ExecTask(String command) {
		this(command, null, TAG_DEFALUT);
	}

	/**
	 * ����tagָʾִ������
	 * 
	 * @param command
	 * @param inputs
	 */
	public ExecTask(String command, List<String> inputs) {
		this(command, inputs, TAG_DEFALUT);
	}

	/**
	 * ����tagָʾִ������
	 * 
	 * @param command
	 * @param tag
	 */
	public ExecTask(String command, int tag) {
		this(command, null, tag);
	}

	/**
	 * ����tagָʾִ������������������
	 * 
	 * @param commands
	 * @param inputs
	 * @param tag
	 */
	public ExecTask(String command, List<String> inputs, int tag) {
		this.inputs = inputs;
		this.command = command;
		this.tag = tag;
		if ((tag & TAG_GET_OUTPUT) != 0) {
			output = new StringBuffer();
		}
		super.setName("ExecTask - " + command + ", inputs - " + inputs);
	}

	private Callback callback = null;

	public void setCallback(Callback callback) {
		if (output == null) {
			output = new StringBuffer();
		}
		this.callback = callback;
	}

	private Callback2 callback2 = null;

	public void setCallback2(Callback2 callback2) {
		this.callback2 = callback2;
	}

	/* �����߳�״̬��Ĭ��start֮�����Ͻ��� */
	private int status = STATUS_START;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	private int exitCode = -1;

	/**
	 * ��ȡ����ִ����ɺ���˳���
	 * 
	 * @return
	 */
	public int getExitCode() {
		return exitCode;
	}

	private StringBuffer output = null;

	/**
	 * ��ȡ����ִ����ɺ�Ľ��
	 * 
	 * @return
	 */
	public String getOutput() {
		return output.toString();
	}

	@Override
	public void run() {
		Log.i(TAG, "= ExecTask run(), tag = " + tag + ", command: " + command
				+ ", inputs: " + inputs + " =");

		try {
			if ((tag & TAG_RUNAS_ROOT) != 0) {
				process = Runtime.getRuntime().exec("su");
				if (inputs == null) {
					inputs = new ArrayList<String>();
				}
				/* command��Ҫ��ӵ���һ������Ϊ"������" */
				if (command != null) {
					inputs.add(0, command);
				}
				if ((tag & TAG_EXIT_ROOT) != 0) {
					inputs.add("exit");
				}
			} else {
				process = Runtime.getRuntime().exec(command);
			}
		} catch (IOException e) {
			dumpError("Catch exception when exec command", e);
		}
		if (process == null) {
			dumpError("Process is null", null);
			return;
		}
		if (inputs != null) {
			this.writeInputs();
		}
		/* ��ȡ����ִ������Ľ�� */
		if ((tag & TAG_WITH_OUTPUT) != 0 || (tag & TAG_GET_OUTPUT) != 0) {
			this.readResults();
		} else if ((tag & TAG_GET_EXEC_STATE) != 0) {
			InputStream in = process.getInputStream();
			int readInt = -1;
			try {
				readInt = in.read();
				Log.d(TAG, "[A]readInt = " + readInt);
				if (readInt == -1) {
					in = process.getErrorStream();
					readInt = in.read();
					if (readInt != -1) {
						exitCode = EXIT_CODE_GETINPUTSTREAM_ERROR;
					}
					Log.d(TAG, "[B]readInt = " + readInt);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			/* �������in��close����ô���� */
		}
		/* waitFor��exitValueӦ���ڶ�ȡ���֮����� */
		/* ����ǰ�߽��������������߽������쳣 */
		/* �ǲ�����ʵӦ����destory֮���ٶ�ȡ�� */
		if ((tag & TAG_GET_EXEC_STATE) != 0 || (tag & TAG_GET_OUTPUT) != 0
				|| callback != null) {
			if (exitCode != EXIT_CODE_GETINPUTSTREAM_ERROR) {
				try {
					exitCode = process.waitFor();
				} catch (InterruptedException e) {
					dumpError("Catch exception when process.waitFor()", e);
				}
			}
		} else if ((tag & TAG_WITH_OUTPUT) == 0) {
			/* ��ΪTAG_WITH_OUTPUT��ʱ��readResults������InputStream�ر� */
			if (process != null) {
				/**
				 * libcore.io.ErrnoException: kill failed: EPERM (Operation not
				 * permitted) <br>
				 * libcore.io.ErrnoException: kill failed: ESRCH (No such
				 * process) <br>
				 */
				process.destroy();
			}
		}
		if ((tag & TAG_GET_OUTPUT) != 0 && callback != null) {
			callback.callback(exitCode, output.toString());
		}

		Log.i(TAG, "= Thread Exiting =");
	}

	/**
	 * ��ȡ�����ִ�н��
	 */
	protected void readResults() {
		try {
			reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			String line = reader.readLine();
			Log.d(TAG, "[A]readLine = " + line);
			/* �����ȡ�����������ô�Ͷ�ȡErrorStream */
			if (line == null) {
				/* aWang��2014-12-09 17:28:16������޷���ȡ��׼������Ļ�������Ϊ����ָ��ִ��ʧ�� */
				reader = new BufferedReader(new InputStreamReader(
						process.getErrorStream()));
				line = reader.readLine();
				if (line != null) {
					exitCode = EXIT_CODE_GETINPUTSTREAM_ERROR;
				}
				Log.d(TAG, "[B]readLine = " + line);
			}
			while (true) {
				if (line == null) {
					break;
				}
				if (status == STATUS_PAUSE) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						dumpError("Catch exception when PAUSE", e);
					}
				} else if (status == STATUS_PAUSE_WITH_READ) {
					line = reader.readLine();
					continue;
				} else if (status == STATUS_STOP) {
					break;
				} else {
					if (callback2 != null) {
						callback2.callback(line);
					}
					if (output != null) {
						output.append(line).append("\n");
					}
					line = reader.readLine();
				}
			}
		} catch (IOException e) {
			dumpError("Catch exception when read result", e);
		} finally {
			Log.i(TAG, "= Prepping thread for termination =");
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ִ������������������
	 * 
	 * http://orgcent.com/android-chmod-root-permission/
	 */
	protected void writeInputs() {
		try {
			writer = new DataOutputStream(process.getOutputStream());
			for (int i = 0; i < inputs.size(); i++) {
				Log.d(TAG, "Sub Command - " + inputs.get(i));
				/* ��Ҫ��һ��"\n"�Ľ���������ʾ����س� */
				writer.writeBytes(inputs.get(i) + "\n");
				writer.flush();
			}
		} catch (IOException e) {
			dumpError("Catch exception when write sub command", e);
			String msg = e.getMessage();
			// java.io.IOException: write failed: EPIPE (Broken pipe) //
			if (msg.contains("EPIPE")) {
				exitCode = EXIT_CODE_EPIPE_ERROR;
			}
		} finally {
			if (writer != null) {
				try {
					writer.close();
					writer = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ��ӡ/��ʾ������Ϣ
	 * 
	 * @param msg
	 * @param e
	 */
	protected void dumpError(String msg, Throwable e) {
		if (e == null) {
			Log.e(TAG, msg);
		} else {
			Log.e(TAG, msg, e);
		}
	}

	/**
	 * ��ȡ����ִ�����֮��Ľ�����˳���
	 */
	public interface Callback {
		/**
		 * @param exitCode
		 *            process.exitCode()
		 * @param results
		 *            ����ִ������Ľ��
		 */
		public void callback(int exitCode, String results);
	}

	/**
	 * ��ȡ����ִ�й����е����
	 */
	public interface Callback2 {
		/**
		 * @param line
		 *            ��������ĵ�ǰ�У�ÿ��ȡһ�оͻص����һ��
		 */
		public void callback(String line);
	}
}