/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package okosama.app.service;

import okosama.app.OkosamaMediaPlayerActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
//import android.os.Handler;
//import android.os.Message;
import android.view.KeyEvent;

/**
 * BroadcastReceiverを継承。AudioManager.registerMediaButtonEventReceiverに登録
 */
public class MediaButtonIntentReceiver extends BroadcastReceiver {

//    private static final int MSG_LONGPRESS_TIMEOUT = 1;
//    private static final int LONG_PRESS_DELAY = 1000;

    private static long mLastClickTime = 0;
    private static boolean mDown = false;
//    private static boolean mLaunched = false;

    // ハンドラの作成
    // オートシャッフル用
    // 今のところ、ここでは実装しない
//    private static Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_LONGPRESS_TIMEOUT:
//                	// ロングプレスタイムアウト
//                    if (!mLaunched) {
//                        Context context = (Context)msg.obj;
//                        Intent i = new Intent();
//                        i.putExtra("autoshuffle", "true");
//                        i.setClass(context, MusicBrowserActivity.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        context.startActivity(i);
//                        mLaunched = true;
//                    }
//                    break;
//            }
//        }
//    };
    
    /**
     * 受信時
     */
    @Override
    public void onReceive(Context context, Intent intent) {
    	// Actionの取得
        String intentAction = intent.getAction();
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intentAction)) {
        	// Broadcast intent, 
        	// a hint for applications that audio is about to become 'noisy' due to a change in audio outputs.
        	// サービスにポーズコマンドを投げる
            Intent i = new Intent(context, MediaPlaybackService.class);
            i.setAction(MediaPlaybackService.SERVICECMD);
            i.putExtra(MediaPlaybackService.CMDNAME, MediaPlaybackService.CMDPAUSE);
            context.startService(i);
        } else if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
        	// MediaButton
        	// キーイベントの取得
            KeyEvent event = (KeyEvent)
                    intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            
            if (event == null) {
                return;
            }

            // eventから、キーコード、アクション、イベント時間を取得
            int keycode = event.getKeyCode();
            int action = event.getAction();
            long eventtime = event.getEventTime();

            // single quick press: pause/resume. 
            // double press: next track
            // long press: start auto-shuffle mode.
            
            String command = null;
            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_STOP:
                	// Stopボタンならば
                	// ストップコマンドを格納
                    command = MediaPlaybackService.CMDSTOP;
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                	// ヘッドセット割り込みキー？
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                	// トグルポーズコマンド
                    command = MediaPlaybackService.CMDTOGGLEPAUSE;
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                	// nextコマンド
                    command = MediaPlaybackService.CMDNEXT;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                	// previousコマンド
                    command = MediaPlaybackService.CMDPREVIOUS;
                    break;
            }

            if (command != null) {
                if (action == KeyEvent.ACTION_DOWN) {
                    if (mDown) {
                    	// 既にダウンフラグがたっているのに来た＝リピート
                    	// autoshuffle用のソースなので、今のところ実装しないものとする
//                        if (MediaPlaybackService.CMDTOGGLEPAUSE.equals(command)
//                                && mLastClickTime != 0 
//                                && eventtime - mLastClickTime > LONG_PRESS_DELAY) {
//                        	// トグルポーズボタンをロングプレス時は、ロングプレスタイムアウトをハンドラに送信
//                            mHandler.sendMessage(
//                                    mHandler.obtainMessage(MSG_LONGPRESS_TIMEOUT, context));
//                        }
                    } else {
                        // if this isn't a repeat event
                    	// 初回はこちら

                        // The service may or may not be running, but we need to send it
                        // a command.
                        Intent i = new Intent(context, MediaPlaybackService.class);
                        i.setAction(MediaPlaybackService.SERVICECMD);
                        if (keycode == KeyEvent.KEYCODE_HEADSETHOOK &&
                                eventtime - mLastClickTime < 300) {
                        	// KEYCODE_HEADSETHOOK
                        	// next
                            i.putExtra(MediaPlaybackService.CMDNAME, MediaPlaybackService.CMDNEXT);
                            context.startService(i);
                            mLastClickTime = 0;
                        } else {
                        	// さっき格納したコマンドを投げる
                            i.putExtra(MediaPlaybackService.CMDNAME, command);
                            context.startService(i);
                            mLastClickTime = eventtime;
                        }

                        // mLaunched = false;
                        mDown = true;
                    }
                } else {
                    // mHandler.removeMessages(MSG_LONGPRESS_TIMEOUT);
                    mDown = false;
                }
                if (isOrderedBroadcast()) {
                    abortBroadcast();
                }
            }
        }
    }
}
