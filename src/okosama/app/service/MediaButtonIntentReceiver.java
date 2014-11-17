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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;
//import android.os.Handler;
//import android.os.Message;

/**
 * BroadcastReceiver���p���BAudioManager.registerMediaButtonEventReceiver�ɓo�^
 */
public class MediaButtonIntentReceiver extends BroadcastReceiver {

//    private static final int MSG_LONGPRESS_TIMEOUT = 1;
//    private static final int LONG_PRESS_DELAY = 1000;

    private static long mLastClickTime = 0;
    private static boolean mDown = false;
//    private static boolean mLaunched = false;

    // �n���h���̍쐬
    // �I�[�g�V���b�t���p
    // ���̂Ƃ���A�����ł͎������Ȃ�
//    private static Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_LONGPRESS_TIMEOUT:
//                	// �����O�v���X�^�C���A�E�g
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
     * ��M��
     */
    @Override
    public void onReceive(Context context, Intent intent) {
    	// Action�̎擾
        String intentAction = intent.getAction();
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intentAction)) {
        	// Broadcast intent, 
        	// a hint for applications that audio is about to become 'noisy' due to a change in audio outputs.
        	// �T�[�r�X�Ƀ|�[�Y�R�}���h�𓊂���
            Intent i = new Intent(context, MediaPlaybackService.class);
            i.setAction(MediaPlaybackService.SERVICECMD);
            i.putExtra(MediaPlaybackService.CMDNAME, MediaPlaybackService.CMDPAUSE);
            context.startService(i);
        } else if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
        	// MediaButton
        	// �L�[�C�x���g�̎擾
            KeyEvent event = (KeyEvent)
                    intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            
            if (event == null) {
                return;
            }

            // event����A�L�[�R�[�h�A�A�N�V�����A�C�x���g���Ԃ��擾
            int keycode = event.getKeyCode();
            int action = event.getAction();
            long eventtime = event.getEventTime();

            // single quick press: pause/resume. 
            // double press: next track
            // long press: start auto-shuffle mode.
            
            String command = null;
            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_STOP:
                	// Stop�{�^���Ȃ��
                	// �X�g�b�v�R�}���h���i�[
                    command = MediaPlaybackService.CMDSTOP;
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                	// �w�b�h�Z�b�g���荞�݃L�[�H
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                	// �g�O���|�[�Y�R�}���h
                    command = MediaPlaybackService.CMDTOGGLEPAUSE;
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                	// next�R�}���h
                    command = MediaPlaybackService.CMDNEXT;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                	// previous�R�}���h
                    command = MediaPlaybackService.CMDPREVIOUS;
                    break;
            }

            if (command != null) {
                if (action == KeyEvent.ACTION_DOWN) {
                    if (mDown) {
                    	// ���Ƀ_�E���t���O�������Ă���̂ɗ��������s�[�g
                    	// autoshuffle�p�̃\�[�X�Ȃ̂ŁA���̂Ƃ���������Ȃ����̂Ƃ���
//                        if (MediaPlaybackService.CMDTOGGLEPAUSE.equals(command)
//                                && mLastClickTime != 0 
//                                && eventtime - mLastClickTime > LONG_PRESS_DELAY) {
//                        	// �g�O���|�[�Y�{�^���������O�v���X���́A�����O�v���X�^�C���A�E�g���n���h���ɑ��M
//                            mHandler.sendMessage(
//                                    mHandler.obtainMessage(MSG_LONGPRESS_TIMEOUT, context));
//                        }
                    } else {
                        // if this isn't a repeat event
                    	// ����͂�����

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
                        	// �������i�[�����R�}���h�𓊂���
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
