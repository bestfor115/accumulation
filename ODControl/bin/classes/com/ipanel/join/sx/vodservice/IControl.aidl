package com.ipanel.join.sx.vodservice;

import com.ipanel.join.sx.vodservice.IControlCallback;

interface IControl {

   void setCallback(IControlCallback callback);
   /**
   * @param type 0:ֱ��, 1:�㲥, 2:ʱ��
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