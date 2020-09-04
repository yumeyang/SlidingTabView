package com.zhy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Created by yhz on 2020/09/04
 */

public class WatermarkView extends Drawable {

    private Paint paint = new Paint();
    private List<String> labels;
    private Context context;
    private int digress;//角度
    private int fontSize;//字体大小 单位sp
    private int mCanvasBgColor;
    private int mTextColor;


    /**
     * @param labels     文字列表 支持多行显示
     * @param digress    角度
     * @param fontSize   字体大小
     * @param text_color 字体颜色
     */
    public WatermarkView(Context context, List<String> labels, int digress, int fontSize, int text_color) {
        this(context, labels, digress, fontSize, Color.TRANSPARENT, text_color);
    }

    public WatermarkView(Context context, List<String> labels, int digress, int fontSize, int canvas_bg_color, int text_color) {
        this.labels = labels;
        this.context = context;
        this.digress = digress;
        this.fontSize = fontSize;
        this.mCanvasBgColor = canvas_bg_color;
        this.mTextColor = text_color;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int width = getBounds().right;
        int height = getBounds().bottom + 500;

        canvas.drawColor(mCanvasBgColor);
        paint.setColor(mTextColor);

        paint.setAntiAlias(true);
        paint.setTextSize(sp2px(context, fontSize));
        canvas.save();
        canvas.rotate(digress);
        float textWidth = paint.measureText(labels.get(0));
        int index = 0;
        for (int positionY = height / 10; positionY <= height; positionY += height / 10 + 80) {
            float fromX = -width + (index++ % 2) * textWidth;
            for (float positionX = fromX; positionX < width; positionX += textWidth * 2) {
                int spacing = 0;//间距
                for (String label : labels) {
                    canvas.drawText(label, positionX, positionY + spacing, paint);
                    spacing = spacing + 50;
                }

            }
        }
        canvas.restore();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}