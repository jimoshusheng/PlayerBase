/*
 * Copyright 2017 jiajunhui<junhui_jia@163.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.kk.taurus.playerbase.player;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.kk.taurus.playerbase.event.OnPlayerEventListener;

/**
 * Created by Taurus on 2018/4/15.
 */

public class TimerCounterProxy {

    private final int MSG_CODE_COUNTER = 1;
    private final int DEFAULT_INTERVAL = 1000;
    private int counterInterval = DEFAULT_INTERVAL;

    private boolean start;
    private OnCounterUpdateListener onCounterUpdateListener;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CODE_COUNTER:
                    if(!start)
                        return;
                    if(onCounterUpdateListener!=null)
                        onCounterUpdateListener.onCounter();
                    loopNext();
                    break;
            }
        }
    };

    public TimerCounterProxy(int counterIntervalMs){
        this.counterInterval = counterIntervalMs;
    }

    public void setOnCounterUpdateListener(OnCounterUpdateListener onCounterUpdateListener) {
        this.onCounterUpdateListener = onCounterUpdateListener;
    }

    public void proxyPlayEvent(int eventCode, Bundle bundle){
        switch (eventCode){
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
            case OnPlayerEventListener.PLAYER_EVENT_ON_RENDER_START:
                start();
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_STOP:
            case OnPlayerEventListener.PLAYER_EVENT_ON_DESTROY:
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                cancel();
                break;
        }
    }

    public void proxyErrorEvent(int eventCode, Bundle bundle){
        cancel();
    }

    public void start(){
        start = true;
        removeMessage();
        mHandler.sendEmptyMessage(MSG_CODE_COUNTER);
    }

    private void loopNext(){
        removeMessage();
        mHandler.sendEmptyMessageDelayed(MSG_CODE_COUNTER, counterInterval);
    }

    public void cancel(){
        start = false;
        removeMessage();
    }

    private void removeMessage() {
        mHandler.removeMessages(MSG_CODE_COUNTER);
    }

    public interface OnCounterUpdateListener{
        void onCounter();
    }

}
