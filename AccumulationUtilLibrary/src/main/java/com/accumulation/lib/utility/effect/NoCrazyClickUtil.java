package com.accumulation.lib.utility.effect;

public class NoCrazyClickUtil {
    private static final long DEFAULT_CLICK_DELAY=500;
    private static long sLastClickTime;

     public static boolean isFastDoubleClick(){
         long time = System.currentTimeMillis();
         long timeD = time - sLastClickTime;
          if (0 < timeD && timeD < DEFAULT_CLICK_DELAY){
             return true;
         }
         sLastClickTime = time;
         return false;
       }
}
