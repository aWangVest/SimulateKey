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
 * 用于执行命令
 * 
 * @author aWang
 * @since 2014-10-08 17:11:53
 * 
 */
public class ExecTask extends Thread {

	/** 似乎什么事都不会做 */
	private static int TAG_DEFALUT = 0;
	/**
	 * 需要读取输出(配合Callback2使用) <br>
	 * 回调 {@code public void callback(String line);} 方法
	 **/
	public static final int TAG_WITH_OUTPUT = 0x01;
	/**
	 * 需要读取输出(可配合Callback使用，也可调用getOutput()获取 <br>
	 * 回调 {@code public void callback(int exitCode, String results);} 方法
	 */
	public static final int TAG_GET_OUTPUT = 0x02;
	/** 以ROOT身份执行 */
	public static final int TAG_RUNAS_ROOT = 0x04;
	/** 命令集执行完成之后退出ROOT */
	public static final int TAG_EXIT_ROOT = 0x08;
	/**
	 * 等待命令执行完成 <br>
	 * 可通过{@code getExitCode}函数获取退出码
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
	 * 获取InputStream之后read失败
	 */
	private static final int EXIT_CODE_GETINPUTSTREAM_ERROR = -100;
	/**
	 * 获取OutputStream值write失败
	 */
	private static final int EXIT_CODE_EPIPE_ERROR = -101;

	private Process process = null;
	private BufferedReader reader = null;
	private DataOutputStream writer = null;

	private int tag = 0;
	private String command = null;
	private List<String> inputs = null;

	/**
	 * 以默认的方式执行命令
	 * 
	 * @param command
	 */
	public ExecTask(String command) {
		this(command, null, TAG_DEFALUT);
	}

	/**
	 * 按照tag指示执行命令
	 * 
	 * @param command
	 * @param inputs
	 */
	public ExecTask(String command, List<String> inputs) {
		this(command, inputs, TAG_DEFALUT);
	}

	/**
	 * 按照tag指示执行命令
	 * 
	 * @param command
	 * @param tag
	 */
	public ExecTask(String command, int tag) {
		this(command, null, tag);
	}

	/**
	 * 按照tag指示执行命令，并对其进行输入
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

	/* 控制线程状态，默认start之后马上进行 */
	private int status = STATUS_START;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	private int exitCode = -1;

	/**
	 * 获取命令执行完成后的退出码
	 * 
	 * @return
	 */
	public int getExitCode() {
		return exitCode;
	}

	private StringBuffer output = null;

	/**
	 * 获取命令执行完成后的结果
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
				/* command需要添加到第一个，作为"主命令" */
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
		/* 读取命令执行输出的结果 */
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
			/* 如果这里in不close会怎么样？ */
		}
		/* waitFor和exitValue应该在读取输出之后进行 */
		/* 否则，前者将导致阻塞，后者将导致异常 */
		/* 是不是其实应该在destory之后再读取？ */
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
			/* 因为TAG_WITH_OUTPUT的时候readResults里面会把InputStream关闭 */
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
	 * 读取命令的执行结果
	 */
	protected void readResults() {
		try {
			reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			String line = reader.readLine();
			Log.d(TAG, "[A]readLine = " + line);
			/* 如果读取不到结果，那么就读取ErrorStream */
			if (line == null) {
				/* aWang，2014-12-09 17:28:16，如果无法获取标准输出流的话，就视为本条指令执行失败 */
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
	 * 执行命令，并对其进行输入
	 * 
	 * http://orgcent.com/android-chmod-root-permission/
	 */
	protected void writeInputs() {
		try {
			writer = new DataOutputStream(process.getOutputStream());
			for (int i = 0; i < inputs.size(); i++) {
				Log.d(TAG, "Sub Command - " + inputs.get(i));
				/* 需要加一个"\n"的结束符，表示命令回车 */
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
	 * 打印/显示错误信息
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
	 * 获取命令执行完成之后的结果和退出码
	 */
	public interface Callback {
		/**
		 * @param exitCode
		 *            process.exitCode()
		 * @param results
		 *            命令执行输出的结果
		 */
		public void callback(int exitCode, String results);
	}

	/**
	 * 获取命令执行过程中的输出
	 */
	public interface Callback2 {
		/**
		 * @param line
		 *            命令输出的当前行，每获取一行就回调输出一行
		 */
		public void callback(String line);
	}
}