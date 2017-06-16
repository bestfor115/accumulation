package com.zyl.push.sdk.script;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by zhangyuliang on 2017/6/14.
 */

public class ColorLocationUtil {

    public static Bitmap screencap(){
        String path="/mnt/sdcard/screenaaa.png";
        String command = "screencap "+path;
        ShellUtils.execCommand(command, true);
        return BitmapFactory.decodeFile(path);
    }

}
