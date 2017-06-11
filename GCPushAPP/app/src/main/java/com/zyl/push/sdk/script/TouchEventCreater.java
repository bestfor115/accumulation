package com.zyl.push.sdk.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;

public class TouchEventCreater {
    /*  
     * Event types  
     */  
    private static HashMap<String, Integer> mTypeMap = new HashMap<String, Integer>();
    public static final String EV_SYN="EV_SYN";
    public static final String EV_KEY="EV_KEY";
    public static final String EV_REL="EV_REL";
    public static final String EV_ABS="EV_ABS";

    static{
        mTypeMap.put(EV_SYN, 0x00);
        mTypeMap.put(EV_KEY, 0x01);
        mTypeMap.put(EV_REL, 0x02);
        mTypeMap.put(EV_ABS, 0x03);
    }
    public static int T(String s){
        return mTypeMap.get(s);
    }
    private static HashMap<String, Integer> mCodeMap = new HashMap<String, Integer>();
    public static final String ABS_MT_TOUCH_MAJOR="ABS_MT_TOUCH_MAJOR";
    public static final String ABS_MT_TOUCH_MINOR="ABS_MT_TOUCH_MINOR";
    public static final String ABS_MT_WIDTH_MAJOR="ABS_MT_WIDTH_MAJOR";
    public static final String ABS_MT_WIDTH_MINOR="ABS_MT_WIDTH_MINOR";
    public static final String ABS_MT_ORIENTATION="ABS_MT_ORIENTATION";
    public static final String ABS_MT_POSITION_X="ABS_MT_POSITION_X";
    public static final String ABS_MT_POSITION_Y="ABS_MT_POSITION_Y";
    public static final String ABS_MT_TOOL_TYPE="ABS_MT_TOOL_TYPE";
    public static final String ABS_MT_BLOB_ID="ABS_MT_BLOB_ID";
    public static final String ABS_MT_TRACKING_ID="ABS_MT_TRACKING_ID";
    public static final String ABS_MT_PRESSURE="ABS_MT_PRESSURE";
    public static final String ABS_MT_DISTANCE="ABS_MT_DISTANCE";
    public static final String SYN_REPORT="SYN_REPORT";
    public static final String SYN_CONFIG="SYN_CONFIG";
    public static final String SYN_MT_REPORT="SYN_MT_REPORT";

    static {
        /* Major axis of touching ellipse */
        mCodeMap.put(ABS_MT_TOUCH_MAJOR, 0x30);
        /* Minor axis (omit if circular) */
        mCodeMap.put(ABS_MT_TOUCH_MINOR, 0x31);
        /* Major axis of approaching ellipse */
        mCodeMap.put(ABS_MT_WIDTH_MAJOR, 0x32);
        /* Minor axis (omit if circular) */
        mCodeMap.put(ABS_MT_WIDTH_MINOR, 0x33);
        /* Ellipse orientation */
        mCodeMap.put(ABS_MT_ORIENTATION, 0x34);
        /* Center X ellipse position */
        mCodeMap.put(ABS_MT_POSITION_X, 0x35);
        /* Center Y ellipse position */
        mCodeMap.put(ABS_MT_POSITION_Y, 0x36);
        /* Type of touching device */
        mCodeMap.put(ABS_MT_TOOL_TYPE, 0x37);
        /* Group a set of packets as a blob */
        mCodeMap.put(ABS_MT_BLOB_ID, 0x38);
        /* Unique ID of initiated contact */
        mCodeMap.put(ABS_MT_TRACKING_ID, 0x39);
        /* Pressure on contact area */
        mCodeMap.put(ABS_MT_PRESSURE, 0x3a);
        /* Contact hover distance */
        mCodeMap.put(ABS_MT_DISTANCE, 0x3b);
        mCodeMap.put(SYN_REPORT, 0x0);
        mCodeMap.put(SYN_CONFIG, 0x1);
        mCodeMap.put(SYN_MT_REPORT, 0x2);
    }
    public static int C(String s){
        return mCodeMap.get(s);
    }
    private static HashMap<String, Long> mValueMap = new HashMap<String, Long>();
    public static final String VAL_MIN="VAL_MIN";
    public static final String VAL_MAX="VAL_MAX";
    static {
        mValueMap.put(VAL_MIN, 0l);
        mValueMap.put(VAL_MAX, 4294967295l);
    }
    public static long V(String s){
        return mValueMap.get(s);
    }
    public static long V(long s){
        return s;
    }
    private static long TRACKING_ID=0x69f;
    private static long TOUCH_MAJOR=10;
    private static long PRESSURE=55;
    public static List<String> touchOnPath(Path path,long time){
        PathMeasure measure = new PathMeasure(path, false);  
        float lenght=measure.getLength();
        float step=40;
        float[] coords = new float[] {0f, 0f};
        int count=(int) (lenght/step);
        List<String> events=new ArrayList<String>();
        int lastX=-1;
        int lastY=-1;
        int lastMajor=-1;
        int lastPressure=-1;
        events.add(getLineEvent(EV_ABS, ABS_MT_TRACKING_ID, TRACKING_ID++));
        float cuurent=0f;
        while(cuurent<=lenght){
            measure.getPosTan(cuurent, coords, null);
            int X=(int) coords[0];
            int Y=(int) coords[1];
            int seed=(int) ((System.currentTimeMillis()%100*Math.random()));
            if(seed%15==0){
                X=(int) (X+4*Math.random());
                Y=(int) (Y+4*Math.random());
            }
            if(X!=lastX){
                events.add(getLineEvent(EV_ABS, ABS_MT_POSITION_X, X));
                lastX=X;
            }
            if(Y!=lastY){
                events.add(getLineEvent(EV_ABS, ABS_MT_POSITION_Y, Y));
                lastY=Y;
            }
            events.add(getLineEvent(EV_ABS, ABS_MT_PRESSURE, PRESSURE));
            events.add(getLineEvent(EV_ABS, ABS_MT_TOUCH_MAJOR, TOUCH_MAJOR));
            events.add(getLineEvent(EV_SYN, SYN_REPORT, VAL_MIN));
            cuurent=(float) (cuurent+getVelocityFlacter(lenght,time)+Math.random()*20);
        }
   
        events.add(getLineEvent(EV_ABS, ABS_MT_TRACKING_ID, VAL_MAX));
        events.add(getLineEvent(EV_SYN, SYN_REPORT, VAL_MIN));
        for (String string : events) {
            Log.d("zhangyl", string);
        }
        return events;
    }

    private static float getVelocityFlacter(float length,long time){
        if(time<2000){
            return length/4;
        }else{
            return 20;
        }
    }
    public static String getLineEvent(String type,String code,String value){
        return "sendevent "+getEventDevice()+ " "+T(type)+" "+C(code) +" "+V(value);
    }
    public static String getLineEvent(String type,String code,long value){
        return "sendevent "+getEventDevice()+ " "+T(type)+" "+C(code) +" "+V(value);
    }
    public static String getLineEvent(String raw){
        return "sendevent "+getEventDevice()+ raw;
    }
    public static String getEventDevice(){
        return "/dev/input/event1";
    }
}
