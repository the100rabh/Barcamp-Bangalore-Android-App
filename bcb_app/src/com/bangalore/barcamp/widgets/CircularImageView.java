package com.bangalore.barcamp.widgets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class CircularImageView extends ImageView {

	private int borderWidth = 5;
	private int viewWidth;
	private int viewHeight;
	private Bitmap image;
	private Paint paint;
	private Paint paintBorder;
	private BitmapShader shader;
	/*
	 * track loading task to cancel it
	 */
	private AsyncTask<URL, Void, Bitmap> currentLoadingTask;
	/*
	 * just for sync
	 */
	private Object loadingMonitor = new Object();

	private static Object fileWriterMonitor = new Object();

	Paint mPainter = new Paint();

	public CircularImageView(Context context) {
		super(context);
		loadingMonitor = new Object();
		setup();
	}

	public CircularImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		loadingMonitor = new Object();
		// int count = attrs.getAttributeCount();
		// for (int iCount = 0; iCount < count; iCount++) {
		// Log.e("attribute", "Attribute index:" + iCount
		// + "  Attribute Name:" + attrs.getAttributeName(iCount));
		// }
		// try {
		// setImageURL(new URL(attrs.getAttributeValue(
		// "http://schemas.android.com/apk/res/com.bangalore.barcamp",
		// "url")));
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// Log.e("url",
		// "url: "
		// + attrs.getAttributeValue(
		// "http://schemas.android.com/apk/res/com.bangalore.barcamp",
		// "url"));
		setup();
	}

	public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		loadingMonitor = new Object();
		// int count = attrs.getAttributeCount();
		// for (int iCount = 0; iCount < count; iCount++) {
		// Log.e("attribute", "Attribute index:" + iCount
		// + "  Attribute Name:" + attrs.getAttributeName(iCount));
		// }
		// try {
		// setImageURL(new URL(attrs.getAttributeValue(
		// "http://schemas.android.com/apk/res/com.bangalore.barcamp",
		// "url")));
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// Log.e("url",
		// "url: "
		// + attrs.getAttributeValue(
		// "http://schemas.android.com/apk/res/com.bangalore.barcamp",
		// "url"));
		setup();
	}

	private void setup() {
		// init paint
		paint = new Paint();
		paint.setAntiAlias(true);

		mPainter = new Paint();
		mPainter.setColor(Color.parseColor("#22000000"));

		paintBorder = new Paint();
		setBorderColor(Color.YELLOW);
		paintBorder.setAntiAlias(true);
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
		this.invalidate();
	}

	public void setBorderColor(int borderColor) {
		if (paintBorder != null)
			paintBorder.setColor(borderColor);

		this.invalidate();
	}

	private void loadBitmap() {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();

		if (bitmapDrawable != null)
			image = bitmapDrawable.getBitmap();
	}

	// @SuppressLint("DrawAllocation")
	// @Override
	// public void onDraw(Canvas canvas) {
	// // load the bitmap
	// loadBitmap();
	//
	// // init shader
	// if (image != null) {
	// shader = new BitmapShader(Bitmap.createScaledBitmap(image,
	// canvas.getWidth(), canvas.getHeight(), false),
	// Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
	// paint.setShader(shader);
	// int circleCenter = viewWidth / 2;
	//
	// // circleCenter is the x or y of the view's center
	// // radius is the radius in pixels of the cirle to be drawn
	// // paint contains the shader that will texture the shape
	// canvas.drawCircle(circleCenter + borderWidth, circleCenter
	// + borderWidth, circleCenter + borderWidth, paintBorder);
	// canvas.drawCircle(circleCenter + borderWidth, circleCenter
	// + borderWidth, circleCenter, paint);
	// }
	// }
	@Override
	protected void onDraw(Canvas canvas) {

		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		Bitmap b = ((BitmapDrawable) drawable).getBitmap();
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

		int w = getWidth() - borderWidth * 2, h = getHeight();

		Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
		int circleCenter = w / 2;
		canvas.drawCircle(circleCenter + borderWidth, circleCenter
				+ borderWidth, circleCenter + borderWidth, paintBorder);
		canvas.drawBitmap(roundBitmap, borderWidth, borderWidth, null);
		if (isPressed() && mPainter != null) {
			canvas.drawCircle(circleCenter + borderWidth, circleCenter
					+ borderWidth, circleCenter + borderWidth, mPainter);
		}

	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		if (bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffa19774;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f,
				sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);

		return output;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec, widthMeasureSpec);

		viewWidth = width - (borderWidth * 2);
		viewHeight = height - (borderWidth * 2);

		setMeasuredDimension(width, height);
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = viewWidth;

		}

		return result;
	}

	private int measureHeight(int measureSpecHeight, int measureSpecWidth) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpecHeight);
		int specSize = MeasureSpec.getSize(measureSpecHeight);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = viewHeight;
		}
		return result;
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		cancelLoading();
		super.setImageBitmap(bm);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		cancelLoading();
		super.setImageDrawable(drawable);
	}

	@Override
	public void setImageResource(int resId) {
		cancelLoading();
		super.setImageResource(resId);
	}

	@Override
	public void setImageURI(Uri uri) {
		cancelLoading();
		super.setImageURI(uri);
	}

	/**
	 * loads image from given url
	 * 
	 * @param url
	 */
	public void setImageURL(URL url) {
		synchronized (loadingMonitor) {
			cancelLoading();
			this.currentLoadingTask = new UrlLoadingTask(this).execute(url);
		}
	}

	private static class UrlLoadingTask extends AsyncTask<URL, Void, Bitmap> {
		private final ImageView updateView;
		private boolean isCancelled = false;
		private InputStream urlInputStream;

		private UrlLoadingTask(ImageView updateView) {
			this.updateView = updateView;
		}

		@Override
		protected Bitmap doInBackground(URL... params) {
			Bitmap bitmap = null;
			String filename = params[0].getPath().replace('/', 'p') + ".png";
			File file = new File(updateView.getContext().getCacheDir(),
					filename);
			if (file.exists()) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),
						options);
			} else {
				try {
					URLConnection con = params[0].openConnection();
					// can use some more params, i.e. caching directory etc
					con.setUseCaches(true);
					this.urlInputStream = con.getInputStream();
					bitmap = BitmapFactory.decodeStream(urlInputStream);
				} catch (IOException e) {
					Log.w(CircularImageView.class.getName(),
							"failed to load image from " + params[0], e);
					return null;
				} finally {
					if (this.urlInputStream != null) {
						try {
							this.urlInputStream.close();
						} catch (IOException e) {
							; // swallow
						} finally {
							this.urlInputStream = null;
						}
					}
				}
				synchronized (fileWriterMonitor) {

					if (!file.exists()) {
						FileOutputStream fos;
						try {
							fos = new FileOutputStream(file);
							bitmap.compress(CompressFormat.PNG, 75, fos);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (!this.isCancelled) {
				// hope that call is thread-safe
				this.updateView.setImageBitmap(result);
			}
		}

		/*
		 * just remember that we were cancelled, no synchronization necessary
		 */
		@Override
		protected void onCancelled() {
			this.isCancelled = true;
			try {
				if (this.urlInputStream != null) {
					try {
						this.urlInputStream.close();
					} catch (IOException e) {
						;// swallow
					} finally {
						this.urlInputStream = null;
					}
				}
			} finally {
				super.onCancelled();
			}
		}
	}

	/**
	 * cancels pending image loading
	 */
	public void cancelLoading() {
		if (loadingMonitor != null) {
			synchronized (loadingMonitor) {
				if (this.currentLoadingTask != null) {
					this.currentLoadingTask.cancel(true);
					this.currentLoadingTask = null;
				}
			}
		}
	}
}