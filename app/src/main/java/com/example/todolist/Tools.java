package com.example.todolist;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

class Tools {

    static void setSystemBarColor(Activity activity, int color) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(color));
        }
    }
}
