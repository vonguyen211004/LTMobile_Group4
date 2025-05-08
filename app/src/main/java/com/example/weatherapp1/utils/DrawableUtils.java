package com.example.weatherapp1.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * Tiện ích để xử lý và điều chỉnh kích thước drawable
 */
public class DrawableUtils {

    private static final String TAG = "DrawableUtils";

    /**
     * Điều chỉnh kích thước drawable từ resource ID
     */
    public static Drawable resizeDrawable(Context context, int resourceId, int width, int height) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            return new BitmapDrawable(context.getResources(), resizedBitmap);
        } catch (Exception e) {
            Log.e(TAG, "Error resizing drawable: " + e.getMessage());
            return ContextCompat.getDrawable(context, resourceId);
        }
    }

    /**
     * Điều chỉnh kích thước drawable
     */
    public static Drawable resizeDrawable(Context context, Drawable drawable, int width, int height) {
        try {
            if (drawable == null) return null;

            Bitmap bitmap;
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
                bitmap = Bitmap.createBitmap(
                        Math.max(drawable.getIntrinsicWidth(), 1),
                        Math.max(drawable.getIntrinsicHeight(), 1),
                        Bitmap.Config.ARGB_8888
                );
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            return new BitmapDrawable(context.getResources(), resizedBitmap);
        } catch (Exception e) {
            Log.e(TAG, "Error resizing drawable: " + e.getMessage());
            return drawable;
        }
    }

    /**
     * Điều chỉnh kích thước drawable trong TextView (compound drawables)
     */
    public static void resizeTextViewDrawables(TextView textView, int width, int height) {
        try {
            Context context = textView.getContext();
            Drawable[] drawables = textView.getCompoundDrawables();

            for (int i = 0; i < drawables.length; i++) {
                if (drawables[i] != null) {
                    Drawable resizedDrawable = resizeDrawable(context, drawables[i], width, height);
                    drawables[i] = resizedDrawable;
                }
            }

            textView.setCompoundDrawablesWithIntrinsicBounds(
                    drawables[0], drawables[1], drawables[2], drawables[3]
            );
        } catch (Exception e) {
            Log.e(TAG, "Error resizing TextView drawables: " + e.getMessage());
        }
    }

    /**
     * Điều chỉnh kích thước drawable trong ImageView
     */
    public static void resizeImageViewDrawable(ImageView imageView, int width, int height) {
        try {
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                Drawable resizedDrawable = resizeDrawable(imageView.getContext(), drawable, width, height);
                imageView.setImageDrawable(resizedDrawable);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error resizing ImageView drawable: " + e.getMessage());
        }
    }

    /**
     * Điều chỉnh kích thước drawable trong ImageButton
     */
    public static void resizeImageButtonDrawable(ImageButton imageButton, int width, int height) {
        try {
            Drawable drawable = imageButton.getDrawable();
            if (drawable != null) {
                Drawable resizedDrawable = resizeDrawable(imageButton.getContext(), drawable, width, height);
                imageButton.setImageDrawable(resizedDrawable);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error resizing ImageButton drawable: " + e.getMessage());
        }
    }

    /**
     * Thay đổi màu của drawable
     */
    public static Drawable tintDrawable(Drawable drawable, int color) {
        if (drawable == null) return null;

        Drawable wrappedDrawable = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }
}
