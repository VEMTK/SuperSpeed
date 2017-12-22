package superclean.solution.com.superspeed.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by hwl on 2017/12/15.
 */

public class ImageUtils {


    // 从资源中获取Bitmap
    public static Bitmap getBitmapFromResources (Context context, int resId) {
        Resources res = context.getResources();
        return BitmapFactory.decodeResource(res, resId);
    }

    /**
     * 获取指定颜色的图片
     *
     * @param btmRes   原图
     * @param colorRes 改变后的图的颜色
     */
    public static Bitmap getSpecifyColorBitmap (Context context, int btmRes, int colorRes) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), btmRes);

        Bitmap resBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

        //获取资源文件里面的颜色，并将无符号的转为有符号
        String colorStr = Long.toHexString(context.getResources().getColor(colorRes) & 0x0FFFFFFFFl);

        if ( colorStr.length() == 6 ) colorStr = "FF" + colorStr;
        if ( colorStr.length() != 8 ) return bitmap;

        float alpha = Integer.parseInt(colorStr.substring(0, 2), 16);
        float red = Integer.parseInt(colorStr.substring(2, 4), 16);
        float green = Integer.parseInt(colorStr.substring(4, 6), 16);
        float blue = Integer.parseInt(colorStr.substring(6, 8), 16);

        ColorMatrix cm = new ColorMatrix();
        Canvas canvas = new Canvas(resBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿的画笔

        cm.set(new float[]{red / 255f, 0, 0, 0, 0,// 红色值
                0, green / 255f, 0, 0, 0,// 绿色值
                0, 0, blue / 255f, 0, 0,// 蓝色值
                0, 0, 0, alpha / 255f, 0 // 透明度
        });

        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        paint.setAntiAlias(true);

        canvas.drawBitmap(bitmap, new Matrix(), paint);

        return resBitmap;
    }

    public static Drawable tintDrawable (Context context, Drawable iconDraw, int colorRes) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(iconDraw);
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(context.getResources().getColor(colorRes)));
        return wrappedDrawable;
    }

    public static Drawable tintDrawable (Context context, int iconRes, int colorRes) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(context.getResources().getDrawable(iconRes));
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(context.getResources().getColor(colorRes)));
        return wrappedDrawable;
    }

}
