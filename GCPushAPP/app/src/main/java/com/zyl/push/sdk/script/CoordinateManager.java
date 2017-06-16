package com.zyl.push.sdk.script;

import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

/**
 * Created by zhangyuliang on 2017/6/15.
 */

public class CoordinateManager implements LoaderCallbackInterface {
    private static volatile CoordinateManager mInstance;

    public static CoordinateManager getManager() {
        if (mInstance == null) {
            synchronized (CoordinateManager.class) {
                if (mInstance == null) {
                    mInstance = new CoordinateManager();
                }
            }
        }
        return mInstance;
    }
    public void init(){
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, ScriptManager.getManager().getContext(), this);
        } else {
            onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onManagerConnected(int status) {

    }

    @Override
    public void onPackageInstall(int operation, InstallCallbackInterface callback) {

    }
}
