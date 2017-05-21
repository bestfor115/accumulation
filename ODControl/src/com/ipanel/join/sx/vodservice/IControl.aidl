package com.ipanel.join.sx.vodservice;

import com.ipanel.join.sx.vodservice.IControlCallback;

interface IControl {

   void setCallback(IControlCallback callback);
   /**
   * @param type 0:Ö±²¥, 1:µã²¥, 2:Ê±ÒÆ
   *
   */
   void prepare(int type, String url);
   void play();
   void pause();
   void fastForward();
   void backForward();
   void seekTo(long time);
   void close();
   void stop();
   long getDuration();
   long getElapsed();
   int getSpeed();
}