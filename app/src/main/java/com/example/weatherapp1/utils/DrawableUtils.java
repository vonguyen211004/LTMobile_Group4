package com.example.weatherapp1.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;


public class DrawableUtils {

    private static final String TAG = "DrawableUtils";
    public static Drawable resizeDrawable(Context context, int resourceId, int width, int height) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            return new BitmapDrawable(context.getResources(), resizedBitmap);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi thay đổi kích thước drawable: " + e.getMessage());
            return ContextCompat.getDrawable(context, resourceId);
        }
    }

}
