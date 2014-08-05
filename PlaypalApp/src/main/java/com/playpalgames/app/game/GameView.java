package com.playpalgames.app.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View {

    private Paint mTextPaint;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLUE);
        mTextPaint.setTextSize(20);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText("hola", 20, 20, mTextPaint);
    }


}
