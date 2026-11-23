package com.github.clans.fab;

import android.content.Context;
import android.os.Build;

import com.dismal.marshmallowland.R;

/* JADX INFO: loaded from: classes.dex */
final class Util {
    private Util() {
    }

    static int dpToPx(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= 16;
    }

    static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }
}
