package com.example.drawler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class PaintView extends View {
    public static int BRUSH_SIZE = 20;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private Figure figure;
    private FingerPath fp;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        mEmboss = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
    }

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
        figure = Figure.NONE;
    }

    public void normal() {
        emboss = false;
        blur = false;
        figure = Figure.NONE;
    }
    public void size_normal() {
        strokeWidth = 10;
    }
    public void size_big() {
        strokeWidth = 15;
    }
    public void size_small() {
        strokeWidth = 5;
    }
    public void color_green() {
        currentColor = Color.GREEN;
    }
    public void color_red() {
        currentColor = Color.RED;
    }
    public void color_black() {
        currentColor = Color.BLACK;
    }

    public void emboss() {
        emboss = true;
        blur = false;
    }

    public void blur() {
        emboss = false;
        blur = true;
    }

    public void clear() {
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        normal();
        invalidate();
    }

    public void deleteLastPath() {
        if(paths.size() != 0) {
            backgroundColor = DEFAULT_BG_COLOR;
            paths.remove(paths.size() - 1);
            normal();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(backgroundColor);

        for (FingerPath fp : paths) {
            mPaint.setColor(fp.getColor());
            mPaint.setStrokeWidth(fp.getStrokeWidth());
            mPaint.setMaskFilter(null);

            if (fp.getEmboss())
                mPaint.setMaskFilter(mEmboss);
            else if (fp.getBlur())
                mPaint.setMaskFilter(mBlur);

            mCanvas.drawPath(fp.getPath(), mPaint);

        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        fp = new FingerPath(currentColor, emboss, blur, strokeWidth, mPath, figure);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            switch(fp.getFigure()) {
                case NONE:
                    mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    mX = x;
                    mY = y;
                    break;
                case RECTANGLE:
                    drawRectangle(x, y);
                    break;
                case CIRCLE:
                    mPath.reset();
                    drawCircle(x);
                    break;
                case TRIANGLE:
                    mPath.reset();
                    drawTriangle(x, y);
                    break;
                case LINE:
                    mPath.reset();
                    mPath.moveTo(mX, mY);
                    mPath.lineTo(x, y);
                    break;
            }
        }
    }

    private void touchUp(float x, float y) {
        switch(fp.getFigure()) {
            case RECTANGLE:
                drawRectangle(x, y);
                break;
            case CIRCLE:
                drawCircle(x);
                break;
            case TRIANGLE:
                drawTriangle(x, y);
                break;
        }
    }

    private void drawTriangle(float x, float y) {
        mPath.moveTo(mX, mY);
        if(mX < x && mY < y) {
            mPath.lineTo(x, mY);
            mPath.lineTo(x, y);
        } else if(mX < x && mY > y) {
            mPath.lineTo(mX, y);
            mPath.lineTo(x, y);
        } else if(mX > x && mY < y) {
            mPath.lineTo(mX, y);
            mPath.lineTo(x, y);
        } else if(mX > x && mY > y) {
            mPath.lineTo(mX, y);
            mPath.lineTo(x, y);
        }
        mPath.lineTo(mX, mY);
        mPath.close();
    }

    private void drawCircle(float x) {
        mPath.addCircle(mX, mY, Math.abs(x - mX), Path.Direction.CCW);
    }

    private void drawRectangle(float x, float y) {
        if(mX > x) {
            mPath.addRect(x, y, mX, mY, Path.Direction.CCW);
        } else {
            mPath.addRect(mX, mY, x, y, Path.Direction.CCW);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                touchUp(x, y);
                invalidate();
                break;
        }

        return true;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }
}
