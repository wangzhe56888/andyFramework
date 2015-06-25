package com.andy.view.drawimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
public class PaintView extends View {
	private Paint paint;
    private Canvas cacheCanvas;
    private Bitmap cachebBitmap;
    private Path path;
    
    static final int BACKGROUND_COLOR = Color.WHITE;
    static final int BRUSH_COLOR = Color.BLACK;
    LayoutParams p ;

    public Bitmap getCachebBitmap() {
        return cachebBitmap;
    }

    public PaintView(Context context, LayoutParams p) {
        super(context);
        this.p = p;
        init();         
    }

    private void init(){
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);                    
        path = new Path();
        cachebBitmap = Bitmap.createBitmap(p.width, (int)(p.height*0.8), Config.ARGB_8888);         
        cacheCanvas = new Canvas(cachebBitmap);
        cacheCanvas.drawColor(Color.WHITE);
    }
    public void clear() {
        if (cacheCanvas != null) {
            
            paint.setColor(BACKGROUND_COLOR);
            cacheCanvas.drawPaint(paint);
            paint.setColor(Color.BLACK);
            cacheCanvas.drawColor(Color.WHITE);
            invalidate();           
        }
    }

    
    
    @Override
    protected void onDraw(Canvas canvas) {
        // canvas.drawColor(BRUSH_COLOR);
        canvas.drawBitmap(cachebBitmap, 0, 0, null);
        canvas.drawPath(path, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        
        int curW = cachebBitmap != null ? cachebBitmap.getWidth() : 0;
        int curH = cachebBitmap != null ? cachebBitmap.getHeight() : 0;
        if (curW >= w && curH >= h) {
            return;
        }

        if (curW < w)
            curW = w;
        if (curH < h)
            curH = h;

        Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
        Canvas newCanvas = new Canvas();
        newCanvas.setBitmap(newBitmap);
        if (cachebBitmap != null) {
            newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
        }
        cachebBitmap = newBitmap;
        cacheCanvas = newCanvas;
    }

    private float cur_x, cur_y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: {
            cur_x = x;
            cur_y = y;
            path.moveTo(cur_x, cur_y);
            break;
        }

        case MotionEvent.ACTION_MOVE: {
            path.quadTo(cur_x, cur_y, x, y);
            cur_x = x;
            cur_y = y;
            break;
        }

        case MotionEvent.ACTION_UP: {
            cacheCanvas.drawPath(path, paint);
            path.reset();
            break;
        }
        }

        invalidate();

        return true;
    }
}
