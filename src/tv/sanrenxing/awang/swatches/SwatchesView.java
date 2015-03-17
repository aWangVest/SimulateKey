package tv.sanrenxing.awang.swatches;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 取色板 <br>
 * 
 * https://github.com/aWangami/ColorSwatches
 * 
 * @author aWang
 * @since 2015-3-11
 */
public class SwatchesView extends View {

	protected RectF oval = null;
	protected Paint paint = null;
	protected Context context = null;

	/*** 色盘x点坐标(相对于本身View而已) */
	protected float cx = 0;
	/*** 色盘y点坐标(相对于本身View而已) */
	protected float cy = 0;
	/*** 色盘半径 */
	protected float radius = 0;

	/*** 色盘据边上的距离 */
	protected int margin = 20;
	/*** 整个View的宽度 */
	protected int mWidth = 0;
	/*** 整个View的高度 */
	protected int mHeight = 0;

	/*** 每一度需要跨越的RGB色值 */
	protected static double RGB_VALUE_PER_DEGREE = 255 / 60;

	/*** 色盘扇形每次绘制的角度（不设置为1度是为了除去抗锯齿导致的边缘异常效果） */
	protected static final float ARC_DEGREE = 3;

	public SwatchesView(Context context) {
		this(context, null);
	}

	public SwatchesView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwatchesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;

		paint = new Paint();
		paint.setColor(Color.WHITE);
		// 设置反锯齿标志 //
		paint.setAntiAlias(true);
		oval = new RectF();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(VIEW_LOG_TAG, "onMeasure()");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public void layout(int l, int t, int r, int b) {
		Log.d(VIEW_LOG_TAG, "layout()");
		super.layout(l, t, r, b);

		mWidth = r - l;
		mHeight = b - t;
		radius = (Math.min(mWidth, mHeight) - margin) / 2;
		// cx = l + mWidth / 2; //
		// cy = t + mHeight / 2; //
		cx = mWidth / 2;
		cy = mHeight / 2;
		oval.set(cx - radius, cy - radius, cx + radius, cy + radius);
	}

	/**
	 * 注意：这里绘制的坐标，坐标原点在本View的左上角，而不是容器的左上角
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(VIEW_LOG_TAG, "onDraw()");
		super.onDraw(canvas);
		drawColorCircle(canvas);
	}

	/**
	 * 绘制色盘
	 * 
	 * @param canvas
	 */
	protected void drawColorCircle(Canvas canvas) {
		int red = 0;
		int green = 0;
		int blue = 0;
		for (int i = 0; i < 360; i++) {
			if (i < 60) {
				green = (int) (RGB_VALUE_PER_DEGREE * i);
				paint.setColor(Color.rgb(255, green, 0));
				canvas.drawArc(oval, i, ARC_DEGREE, true, paint);
			} else if (i < 120) {
				red = (int) (255 - RGB_VALUE_PER_DEGREE * (i - 60));
				paint.setColor(Color.rgb(red, 255, 0));
				canvas.drawArc(oval, i, ARC_DEGREE, true, paint);
			} else if (i < 180) {
				blue = (int) (RGB_VALUE_PER_DEGREE * (i - 120));
				paint.setColor(Color.rgb(0, 255, blue));
				canvas.drawArc(oval, i, ARC_DEGREE, true, paint);
			} else if (i < 240) {
				green = (int) (255 - RGB_VALUE_PER_DEGREE * (i - 180));
				paint.setColor(Color.rgb(0, green, 255));
				canvas.drawArc(oval, i, ARC_DEGREE, true, paint);
			} else if (i < 300) {
				red = (int) (RGB_VALUE_PER_DEGREE * (i - 240));
				paint.setColor(Color.rgb(red, 0, 255));
				canvas.drawArc(oval, i, ARC_DEGREE, true, paint);
			} else if (i < 360) {
				blue = (int) (255 - RGB_VALUE_PER_DEGREE * (i - 300));
				paint.setColor(Color.rgb(255, 0, blue));
				canvas.drawArc(oval, i, ARC_DEGREE, true, paint);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(VIEW_LOG_TAG, "getX : " + event.getX());
		Log.d(VIEW_LOG_TAG, "getY : " + event.getY());

		float x = event.getX();
		float y = event.getY();
		// 象限 //
		int quadrant = 0;
		if (x > cx) {
			if (y > cy) {
				quadrant = 1;
			} else if (y < cy) {
				quadrant = 4;
			}
		} else if (x < cx) {
			if (y > cy) {
				quadrant = 2;
			} else if (y < cy) {
				quadrant = 3;
			}
		}
		Log.d(VIEW_LOG_TAG, "Quadrant : " + quadrant);
		// 距离 //
		float distance = (float) Math.sqrt((Math.pow(x - cx, 2) + Math.pow(y
				- cy, 2)));
		Log.d(VIEW_LOG_TAG, "Distance : " + distance);
		if (distance <= radius) {
			Log.d(VIEW_LOG_TAG, "Inner");
			int anger = 0;
			double radian = 0;
			switch (quadrant) {
			case 1:
				radian = Math.asin((y - cy) / radius);
				break;
			case 2:
				radian = Math.asin((cx - x) / radius) + Math.PI / 2;
				break;
			case 3:
				radian = Math.asin((cy - y) / radius) + Math.PI;
				break;
			case 4:
				radian = Math.asin((x - cx) / radius) + Math.PI / 2 * 3;
				break;
			}
			anger = (int) (radian / Math.PI * 180);
			Log.d(VIEW_LOG_TAG, "Anger : " + anger);
			Log.d(VIEW_LOG_TAG, "Radian : " + radian);
			int color = getColor(anger);
			if (onColorSelectedListener != null) {
				onColorSelectedListener.onSelect(color);
			}
		} else {
			Log.d(VIEW_LOG_TAG, "Outer");
		}
		return super.onTouchEvent(event);
	}

	// /////////////////////////////////////////////////////// //

	protected enum RGB {
		R, G, B
	};

	protected class RGBColor {
		public int R;
		public int G;
		public int B;

		@Override
		public String toString() {
			return "RGBColor [R=" + R + ", G=" + G + ", B=" + B + "]";
		}
	}

	// 六个弧度区域内RGB的最大/小值 //
	protected static int[] rgbAreaR = new int[] { 255, 255, 0, 0, 0, 255, 255 };
	protected static int[] rgbAreaG = new int[] { 0, 255, 255, 255, 0, 0, 0 };
	protected static int[] rgbAreaB = new int[] { 0, 0, 0, 255, 255, 255, 0 };

	// 上面数组的整合版本 //
	protected static int[][] rgbArea = { //
	{ 255, 255, 0, 0, 0, 255, 255 }, //
			{ 0, 255, 255, 255, 0, 0, 0 }, //
			{ 0, 0, 0, 255, 255, 255, 0 } //
	};

	// 当前弧度内变化的是R/G/B(平均分成6个弧度,每个弧度PI/2,PI=180°) //
	protected static RGB[] rgbAreaWho = new RGB[] { RGB.G, RGB.R, RGB.B, RGB.G,
			RGB.R, RGB.B };
	// 度数增加还是减少 //
	protected static int[] rgbAreaPM = new int[] { 1, -1, 1, -1, 1, -1 };

	protected RGBColor getRGBColor(int anger) {
		RGBColor rgb = new RGBColor();
		int area = anger / 60;
		int remainder = anger % 60;
		Log.d(VIEW_LOG_TAG, "Area : " + area);
		int pm = rgbAreaPM[area];
		RGB who = rgbAreaWho[area];
		rgb.R = rgbAreaR[area];
		rgb.G = rgbAreaG[area];
		rgb.B = rgbAreaB[area];
		double diffVal = (remainder / 60.0) * 255;
		Log.d(VIEW_LOG_TAG, "DiffVal : " + diffVal);
		switch (who) {
		case R:
			rgb.R += pm * diffVal;
			break;
		case G:
			rgb.G += pm * diffVal;
			break;
		case B:
			rgb.B += pm * diffVal;
			break;
		}
		Log.d(VIEW_LOG_TAG, rgb.toString());
		return rgb;
	}

	protected int getColor(int anger) {
		RGBColor rgb = getRGBColor(anger);
		return Color.rgb(rgb.R, rgb.G, rgb.B);
	}

	// /////////////////////////////////////////////////////// //

	private OnColorSelectedListener onColorSelectedListener = null;

	public OnColorSelectedListener getOnColorSelectedListener() {
		return onColorSelectedListener;
	}

	public void setOnColorSelectedListener(OnColorSelectedListener l) {
		this.onColorSelectedListener = l;
	}

	public interface OnColorSelectedListener {
		public void onSelect(int color);
	}
}