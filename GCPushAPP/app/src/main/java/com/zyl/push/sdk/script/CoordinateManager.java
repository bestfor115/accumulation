package com.zyl.push.sdk.script;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.Toast;

import com.accumulation.lib.utility.debug.Logger;

import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.R.attr.bitmap;
import static org.opencv.imgcodecs.Imgcodecs.CV_IMWRITE_PNG_COMPRESSION;
import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_COLOR;
import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.opencv.imgcodecs.Imgcodecs.IMREAD_ANYCOLOR;
import static org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR;
import static org.opencv.imgcodecs.Imgcodecs.IMREAD_GRAYSCALE;
import static org.opencv.imgcodecs.Imgcodecs.IMREAD_LOAD_GDAL;

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
    public int[] matchImage(String templateFile){
        Context context=ScriptManager.getManager().getContext();
        String path="/mnt/sdcard/test.png";
        String outFile="/mnt/sdcard/test2.png";
        String[] cmds=new String[2];
        cmds[0]="screencap -p "+path;;
//        cmds[1]="chmod 777 "+path;
        ShellUtils.execCommand(cmds, true);
        return match(path,templateFile,null,Imgproc.TM_SQDIFF);
    }

    public int[]  match(String inFile, String templateFile, String outFile, int match_method) {
        Logger.d(inFile);
        Logger.d(templateFile);
        Mat img = Imgcodecs.imread(inFile);
        Mat templ = Imgcodecs.imread(templateFile);
        img.adjustROI(0,0,0,0);
        // / Create the result matrix
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

//        Mat result = new Mat(img.rows(), img.cols(), CvType.CV_32FC1);

        // / Do the Matching and Normalize
        Imgproc.matchTemplate(img, templ, result, match_method);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc;
        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }
        if(!TextUtils.isEmpty(outFile)){
            // / Show me what you got
            Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                    matchLoc.y + templ.rows()), new Scalar(0, 255, 0));
            // Save the visualized detection.
            Imgcodecs.imwrite(outFile, img);
        }

        int ret[] =new int[2];
        ret[0]= (int) (matchLoc.x+templ.cols()/2);
        ret[1]= (int) (matchLoc.y+templ.rows()/2);
        return ret;
    }
}
