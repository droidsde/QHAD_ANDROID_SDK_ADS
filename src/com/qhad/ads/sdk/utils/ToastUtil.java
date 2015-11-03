package com.qhad.ads.sdk.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by 37X21=777 on 15/10/23.
 */
public class ToastUtil {

    public static void showToast(Context context, String msg) {
        if (context == null || TextUtils.isEmpty(msg)) return;
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
