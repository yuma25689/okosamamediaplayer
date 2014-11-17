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

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.Vector;

import okosama.app.LogWrapper;
import okosama.app.MusicSettingsActivity;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Provides "background" audio playback capabilities, allowing the
 * user to switch between activities without stopping playback.
 * �o�b�N�O���E���h�ł̃I�[�f�B�I�̃v���C�o�b�N���\�ɂ��A�A�N�e�B�r�e�B�̕ύX�ł��v���C���~�߂Ȃ��H
 */
public class MediaPlaybackService extends Service {
    /** used to specify whether enqueue() should start playing
     * the new list of files right away, next or once all the currently
     * queued files have been played
     */
    public static final int NOW = 1;
    public static final int NEXT = 2;
    public static final int LAST = 3;
    public static final int PLAYBACKSERVICE_STATUS = 1;
    
    public static final int SHUFFLE_NONE = 0;
    public static final int SHUFFLE_NORMAL = 1;
    public static final int SHUFFLE_AUTO = 2;
    
    public static final int REPEAT_NONE = 0;
    public static final int REPEAT_CURRENT = 1;
    public static final int REPEAT_ALL = 2;

    public static final String PLAYSTATE_CHANGED = "okosama.app.playstatechanged";
    public static final String META_CHANGED = "okosama.app.metachanged";
    public static final String QUEUE_CHANGED = "okosama.app.queuechanged";
    public static final String PLAYBACK_COMPLETE = "okosama.app.playbackcomplete";
    public static final String ASYNC_OPEN_COMPLETE = "okosama.app.asyncopencomplete";

    public static final String SERVICECMD = "okosama.app.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";

    public static final String TOGGLEPAUSE_ACTION = "okosama.app.musicservicecommand.togglepause";
    public static final String PAUSE_ACTION = "okosama.app.musicservicecommand.pause";
    public static final String PREVIOUS_ACTION = "okosama.app.musicservicecommand.previous";
    public static final String NEXT_ACTION = "okosama.app.musicservicecommand.next";

    private static final int TRACK_ENDED = 1;
    private static final int RELEASE_WAKELOCK = 2;
    private static final int SERVER_DIED = 3;
    private static final int FADEIN = 4;
    private static final int MAX_HISTORY_SIZE = 100;
    
    private MultiPlayer mPlayer;
    private String mFileToPlay;
    private int mShuffleMode = SHUFFLE_NONE;
    private int mRepeatMode = REPEAT_NONE;
    private int mMediaMountedCount = 0;
    private long [] mAutoShuffleList = null;
    private boolean mOneShot;
    //private long [] mPlayList = null;
    private MediaInfo [] mPlayList = null;
    private int mPlayListLen = 0;
    private Vector<Integer> mHistory = new Vector<Integer>(MAX_HISTORY_SIZE);
    private Cursor mCursor;
    private int mCurrentType = -1; //= MediaInfo.MEDIA_TYPE_AUDIO;
    public int getCurrentType()
    {
    	return mCurrentType;
    }
    private int mPlayPos = -1;
    private static final String LOGTAG = "MediaPlaybackService";
    private final Shuffler mRand = new Shuffler();
    private int mOpenFailedCounter = 0;
    String[] mCursorAudioCols = new String[] {
            "audio._id AS _id",             // index must match IDCOLIDX below
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.IS_PODCAST, // index must match PODCASTCOLIDX below
            MediaStore.Audio.Media.BOOKMARK    // index must match BOOKMARKCOLIDX below
    };
    String[] mCursorVideoCols = new String[] {
    		MediaStore.Video.Media._ID, //"audio._id AS _id",             // index must match IDCOLIDX below
            MediaStore.Video.Media.ARTIST,
            MediaStore.Video.Media.ALBUM,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.CATEGORY,
            MediaStore.Video.Media.DURATION, 
            MediaStore.Video.Media.BOOKMARK    // index must match BOOKMARKCOLIDX below
    };
    private final static int IDCOLIDX = 0;
    private final static int PODCASTCOLIDX = 8;
    private final static int BOOKMARKCOLIDX = 9;
    private BroadcastReceiver mUnmountReceiver = null;
    private WakeLock mWakeLock;
    private int mServiceStartId = -1;
    private boolean mServiceInUse = false;
    private boolean mIsSupposedToBePlaying = false;
    private boolean mQuietMode = false;
    private AudioManager mAudioManager;
    // used to track what type of audio focus loss caused the playback to pause
    private boolean mPausedByTransientLossOfFocus = false;

    private SharedPreferences mPreferences;
    // We use this to distinguish between different cards when saving/restoring playlists.
    // This will have to change if we want to support multiple simultaneous cards.
    private int mCardId;
    
    private MediaAppWidgetProvider mAppWidgetProvider = MediaAppWidgetProvider.getInstance();
    
    // interval after which we stop the service when idle
    private static final int IDLE_DELAY = 60000;
    
    /**
     * �t�F�[�h�C�����Ȃ���Đ�����
     */
    private void startAndFadeIn() {
    	// �����́A�n���h���ɔC����
        mMediaplayerHandler.sendEmptyMessageDelayed(FADEIN, 10);
    }
    
    /**
     * �n���h��
     */
    private Handler mMediaplayerHandler = new Handler() {
        float mCurrentVolume = 1.0f;
        @Override
        public void handleMessage(Message msg) {
            // MusicUtils.debugLog("mMediaplayerHandler.handleMessage " + msg.what);
            switch (msg.what) {
                case FADEIN:
                	// �t�F�[�h�C������
                    if (!isPlaying()) {
                    	// �܂��v���C����Ă��Ȃ�
                    	// �Đ��B�{�����[����0����n�߂āA������xFADEIN�𑗐M
                        mCurrentVolume = 0f;
                        mPlayer.setVolume(mCurrentVolume);
                        play();
                        mMediaplayerHandler.sendEmptyMessageDelayed(FADEIN, 10);
                    } else {
                    	// �{�����[�����グ��
                        mCurrentVolume += 0.01f;
                        if (mCurrentVolume < 1.0f) {
                            mMediaplayerHandler.sendEmptyMessageDelayed(FADEIN, 10);
                        } else {
                        	// �{�����[����1�ɂȂ�����AFADEIN�I��
                            mCurrentVolume = 1.0f;
                        }
                        mPlayer.setVolume(mCurrentVolume);
                    }
                    break;
                case SERVER_DIED:
                	if (mIsSupposedToBePlaying) {
                        next(true);
                    } else {
                        // the server died when we were idle, so just
                        // reopen the same song (it will start again
                        // from the beginning though when the user
                        // restarts)
                        openCurrent();
                    }
                    break;
                case TRACK_ENDED:
                    if (mRepeatMode == REPEAT_CURRENT) {
                        seek(0);
                        play();
                    } else if (!mOneShot) {
                        next(false);
                    } else {
                        notifyChange(PLAYBACK_COMPLETE);
                        mIsSupposedToBePlaying = false;
                    }
                    break;
                case RELEASE_WAKELOCK:
                    mWakeLock.release();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * �u���[�h�L���X�g���V�[�o�̐ݒ�
     */
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
    	/**
    	 * onReceive
    	 */
        @Override
        public void onReceive(Context context, Intent intent) {
        	// intent����Action���擾����
            String action = intent.getAction();
            // intent����R�}���h�����擾����
            String cmd = intent.getStringExtra("command");
            // MusicUtils.debugLog("mIntentReceiver.onReceive " + action + " / " + cmd);
            if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
                // �R�}���h�����փR�}���h������
                next(true);
            } else if (CMDPREVIOUS.equals(cmd) || PREVIOUS_ACTION.equals(action)) {
            	// �R�}���h���O�փR�}���h������
                prev();
            } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
            	// Log.d("test","getPause");
            	// �g�O���|�[�Y�R�}���h������
                if (isPlaying()) {
                	// �v���C���Ȃ�΁A�~�߂�
                    pause();
                    // �ꎞ�I�ȁH�t�H�[�J�X�̎����ɂ��|�[�Y�t���O�H�𗎂Ƃ��H
                    mPausedByTransientLossOfFocus = false;
                } else {
                	// �v���C���łȂ���΁A�v���C����
                    play();
                }
            } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
            	// �|�[�Y�R�}���h������
            	// �|�[�Y����
                pause();
                // �ꎞ�I�ȁH�t�H�[�J�X�̎����ɂ��|�[�Y�t���O�H�𗎂Ƃ��H
                mPausedByTransientLossOfFocus = false;
            } else if (CMDSTOP.equals(cmd)) {
            	// �X�g�b�v�R�}���h
            	// �|�[�Y����
                pause();
                // �ꎞ�I�ȁH�t�H�[�J�X�̎����ɂ��|�[�Y�t���O�H�𗎂Ƃ��H
                mPausedByTransientLossOfFocus = false;
                // �ʒu0�ɃV�[�N
                seek(0);
            } else if (MediaAppWidgetProvider.CMDAPPWIDGETUPDATE.equals(cmd)) {
            	// �A�v���P�[�V�����E�B�W�F�b�g�̃A�b�v�f�[�g�R�}���h
                // Someone asked us to refresh a set of specific widgets, probably
                // because they were just added.
            	// �����widget�����t���b�V������Bwidget��id���擾���AperformUpdate�ɓn���H
                int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                mAppWidgetProvider.performUpdate(MediaPlaybackService.this, appWidgetIds);
            } else if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
            	
            	int iCon = intent.getIntExtra("state",0);
            	if( iCon == 1 )
            	{
            		// �w�b�h�z���ڑ�
            		boolean bPlugAndPlay = false;
                    SharedPreferences prefs = getSharedPreferences(
                            MusicSettingsActivity.PREFERENCES_FILE, MODE_PRIVATE);
                    bPlugAndPlay = prefs.getBoolean(MusicSettingsActivity.KEY_ENABLE_HEADSET_PLUG_AND_PLAY, false);
            	             		
        			Log.d("headset", "connect");

        			if( mPlayListLen <= 0 || bPlugAndPlay == false )
            		{
            			// Toast.makeText(context, "headset connect", Toast.LENGTH_SHORT).show();
            		}
            		else
            		{
	            		// Toast.makeText(context, "headset connect - play", Toast.LENGTH_SHORT).show();
            			if( isPlaying() )
            			{
            				Toast.makeText(context, R.string.headset_connect_and_play, Toast.LENGTH_SHORT).show();
                            pause();        				
            			}            			
	                    play();
            		}
            	}
            	else if( iCon == 0 )
            	{
            		// �w�b�h�z���ؒf
            		// Toast.makeText(context, "headset disconnect - pause", Toast.LENGTH_SHORT).show();
        			if( isPlaying() )
        			{
        				Log.d("headset", "disconnect and pause");
            			Toast.makeText(context, R.string.headset_disconnect_and_pause, Toast.LENGTH_SHORT).show();
                        pause();        				
        			}
                    mPausedByTransientLossOfFocus = false;
            	}
//            	intent.setClass(context, OkosamaMediaPlayerActivity.class);
//            	intent.setAction(Intent.ACTION_HEADSET_PLUG);
//            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            	context.startActivity(intent);
            }
        }
    };

    /**
     * AudioFocusListener
     */
    private OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {
        @Override
		public void onAudioFocusChange(int focusChange) {
//        	�I�[�f�B�I�t�H�[�J�X��v�����邠����I�[�f�B�I���Đ�����O�ɁA�g�p����X�g���[���̃I�[�f�B�I�t�H�[�J�X���擾����K�v���L��܂��B
//        	�����requestAudioFocus()���R�[�����A���N�G�X�g������������AUDIOFOCUS_REQUEST_GRANTED���Ԃ���܂��B
//        	���Ȃ��́A�I�[�f�B�I�t�H�[�J�X��Z���I�������I�ɗv�����邩�Ɋւ�炸�X�g���[�����w�肷��K�v���L��܂��B�Z���Ԃ̂݃I�[�f�B�I���Đ�����ƍl������ꍇ�i�Ⴆ�Ή����i�r�j�g�����W�F���g�t�H�[�J�X��v�����܂��B
//        	�����ƃI�[�f�B�I���Đ�����Ɨ\�z�o����ꍇ�A�i�Ⴆ�Ή��y�̍Đ��j�p�[�}�l���g�I�[�f�B�I�t�H�[�J�X��v�����܂��B        	
        	// AudioFocus���A�V�����Ȃ����H
            // AudioFocus is a new feature: focus updates are made verbose on purpose
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                	// �t�H�[�J�X���������H
                    LogWrapper.v(LOGTAG, "AudioFocus: received AUDIOFOCUS_LOSS");
                    if(isPlaying()) {
                    	// �v���C���ł����
                    	// �|�[�Y����
                        mPausedByTransientLossOfFocus = false;
                        pause();
                    }
                    break;
//                    �g�����W�F���g�t�H�[�J�X��v�����Ă���Ƃ��A�ǉ��̃I�v�V�������L��܂��B�_�b�N(��q)��L���ɂ��邩�ǂ����A�ʏ�s�V���ǂ��I�[�f�B�I�A�v���̓I�[�f�B�I�t�H�[�J�X�������Ƃ����ɍĐ����T�C�����g�ɂ��܂��B
//                    �_�b�N�������g�����W�F���g�t�H�[�J�X��v�����鎖�ŁA���̃A�v���ɃI�[�f�B�I�t�H�[�J�X���߂�܂Œቹ�ʂɂ��邱�ƂōĐ��������鎖���o����Ƒ��̃A�v���ɓ`���܂��B
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                	// �ꎞ�I�Ƀt�H�[�J�X���������H
                    LogWrapper.v(LOGTAG, "AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT");
                    if(isPlaying()) {
                    	// �v���C���ł����
                    	// �g�����W�F���g�t�H�[�J�X�t���O�𗧂ĂĂ���A�|�[�Y����
                        mPausedByTransientLossOfFocus = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                	// AudioFocus�𓾂�
                    LogWrapper.v(LOGTAG, "AudioFocus: received AUDIOFOCUS_GAIN");
                    if(!isPlaying() && mPausedByTransientLossOfFocus) {
                    	// �v���C���ł͂Ȃ��A�g�����W�F���g�������ꍇ
                    	// �g�����W�F���g�t���O�𗎂Ƃ��A�Đ�����
                        mPausedByTransientLossOfFocus = false;
                        startAndFadeIn();
                    }
                    break;
                default:
                    LogWrapper.e(LOGTAG, "Unknown audio focus change code");
            }
        }
    };

    /**
     * �R���X�g���N�^ �������Ȃ�
     */
    public MediaPlaybackService() {
    }

    /**
     * onCreate
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // AudioManager provides access to volume and ringer mode control.
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // ���b�N��ʂ̑������������MediaButtonIntentReceiver��o�^
        mAudioManager.registerMediaButtonEventReceiver(
        		new ComponentName(getPackageName(),
                MediaButtonIntentReceiver.class.getName()));
        
        // �ݒ���擾
        mPreferences = getSharedPreferences("Music", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
        // SD�J�[�h��ID���擾
        mCardId = StorageInfo.getCardId(this);
        
        // �O���X�g���[�W�̃��X�i��o�^
        registerExternalStorageListener();

        // Needs to be done in this thread, since otherwise ApplicationContext.getPowerManager() crashes.
        // �v���C���[���쐬
        mPlayer = new MultiPlayer();
        // �v���C���[�Ƀn���h����ݒ�
        mPlayer.setHandler(mMediaplayerHandler);

        // �ݒ�̍ēǂݍ���
        reloadQueue();
        
        // �t�B���^�������āAIntentReceiver��o�^
        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(SERVICECMD);
        commandFilter.addAction(TOGGLEPAUSE_ACTION);
        commandFilter.addAction(PAUSE_ACTION);
        commandFilter.addAction(NEXT_ACTION);
        commandFilter.addAction(PREVIOUS_ACTION);
        commandFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mIntentReceiver, commandFilter);
        
        // �d���Ǘ��H
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        // WakeLock�̎擾�H
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getName());
        // WakeLock�͎Q�ƃJ�E���g�����łȂ��ݒ�ɂ���H
        mWakeLock.setReferenceCounted(false);

        // If the service was idle, but got killed before it stopped itself, the
        // system will relaunch it. Make sure it gets stopped again in that case.
        // ���΂炭������~�߂�炵��
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
    }

    @Override
    public void onDestroy() {
        // Check that we're not being destroyed while something is still playing.
        if (isPlaying()) {
        	// �I�����Ƀv���C���ł���΁A�G���[���O�o��
            LogWrapper.e(LOGTAG, "Service being destroyed while still playing.");
        }
        // release all MediaPlayer resources, including the native player and wakelocks
        mPlayer.release();
        mPlayer = null;

        // �Đ����I�������Ō��abandonAudioFocus()���m���ɌĂт܂��B
        // ����̓V�X�e���ւ���ȏ�t�H�[�J�X��v�����Ȃ�����AudioManager.OnAudioFocusChangeListener�Q����̉�����ʒm���܂��B
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        
        // make sure there aren't any other messages coming
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mMediaplayerHandler.removeCallbacksAndMessages(null);

        if (mCursor != null) {
        	// �J�[�\���N���[�Y
            mCursor.close();
            mCursor = null;
        }

        // IntentReceiver�̓o�^����
        unregisterReceiver(mIntentReceiver);
        if (mUnmountReceiver != null) {
        	// UnMountReceiver�̓o�^����
            unregisterReceiver(mUnmountReceiver);
            mUnmountReceiver = null;
        }
        // WakeLock�̃����[�X
        mWakeLock.release();
        super.onDestroy();
    }
    
    // 16�i����char�z��H
    private final char hexdigits [] = new char [] {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
    };

    /**
     * �ݒ�̕ۑ�
     * @param full
     */
    private void saveQueue(boolean full) {
        if (mOneShot) {
        	// OneShot�Ȃ�A�߂�
            return;
        }
        // �ݒ��ҏW���[�h�Ŏ擾
        Editor ed = mPreferences.edit();
        //long start = System.currentTimeMillis();
        if (full) {
        	// �t���Z�[�u�̏ꍇ
            StringBuilder q = new StringBuilder();
            
            // The current playlist is saved as a list of "reverse hexadecimal"
            // numbers, which we can generate faster than normal decimal or
            // hexadecimal numbers, which in turn allows us to save the playlist
            // more often without worrying too much about performance.
            // (saving the full state takes about 40 ms under no-load conditions
            // on the phone)
            // �v���C���X�g�̒������擾���A���̕����[�v
            int len = mPlayListLen;
            for (int i = 0; i < len; i++) {
            	// �v���C���X�g��id�擾
                long n = mPlayList[i].getId();
                if (n == 0) {
                    q.append("0;");
                } else {
                	// 0�ȊO�̏ꍇ�A�����炭reverse hexadecimal�`���Ŋi�[����Ă���
                    while (n != 0) {
                        int digit = (int)(n & 0xf);
                        n >>= 4;
                        q.append(hexdigits[digit]);
                    }
                    q.append(";");
                }
            }
            //LogWrapper.i("@@@@ service", "created queue string in " + (System.currentTimeMillis() - start) + " ms");
            // queue�Ƃ��āA�v���C���X�g��id�̔z���;��؂�ɂ�����������i�[
            ed.putString("queue", q.toString());
            // �J�[�hID���i�[
            ed.putInt("cardid", mCardId);
            if (mShuffleMode != SHUFFLE_NONE) {
            	// �V���b�t�����[�h���ݒ肳��Ă�����
            	// �q�X�g���[���Z�[�u����
                // In shuffle mode we need to save the history too
                len = mHistory.size();
                q.setLength(0);
                for (int i = 0; i < len; i++) {
                    int n = mHistory.get(i);
                    if (n == 0) {
                        q.append("0;");
                    } else {
                        while (n != 0) {
                            int digit = (n & 0xf);
                            n >>= 4;
                            q.append(hexdigits[digit]);
                        }
                        q.append(";");
                    }
                }
                ed.putString("history", q.toString());
            }
        }
        // ���݂̈ʒu
        ed.putInt("curpos", mPlayPos);
        if (mPlayer.isInitialized()) {
        	// ���f�B�A�v���C���[���������ς݂Ȃ��
        	// ���̈ʒu��ێ�
            ed.putLong("seekpos", mPlayer.position());
        }
        // ���s�[�g���[�h�ƃV���b�t�����[�h��ێ�
        ed.putInt("repeatmode", mRepeatMode);
        ed.putInt("shufflemode", mShuffleMode);
        // �L�^
        ed.commit();
  
        //LogWrapper.i("@@@@ service", "saved state in " + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * �L���[�̃����[�h
     */
    private void reloadQueue() {
        String q = null;
        
        // boolean newstyle = false;
        int id = mCardId;
        if (mPreferences.contains("cardid")) {
        	// card id���ۑ�����Ă���΁A������擾���Anew style�t���O�𗧂Ă�
            // newstyle = true;
            id = mPreferences.getInt("cardid", mCardId);
        }
        if (id == mCardId) {
            // Only restore the saved playlist if the card is still
            // the same one as when the playlist was saved
        	// cardid���ς���Ă��Ȃ�
        	// queue���擾
            q = mPreferences.getString("queue", "");
        }
        int qlen = q != null ? q.length() : 0;
        if (qlen > 1) {
        	// �擾���ꂽqueue�̉��
            //LogWrapper.i("@@@@ service", "loaded queue: " + q);
            int plen = 0;
            int n = 0;
            int shift = 0;
            for (int i = 0; i < qlen; i++) {
                char c = q.charAt(i);
                if (c == ';') {
                	// ��؂蕶������
                	// �v���C���X�g�̗̈�m��
                    ensurePlayListCapacity(plen + 1);
                    // �v���C���X�g���P�i�[
                    mPlayList[plen] = new MediaInfo( n, MediaInfo.MEDIA_TYPE_AUDIO );
                    // ������̃��[�v�ɔ����ď�����
                    plen++;
                    n = 0;
                    shift = 0;
                } else {
                	// ��؂蕶���łȂ�
                	// n�̌v�Z
                    if (c >= '0' && c <= '9') {
                        n += ((c - '0') << shift);
                    } else if (c >= 'a' && c <= 'f') {
                        n += ((10 + c - 'a') << shift);
                    } else {
                        // bogus playlist data
                    	// �U���̃v���C���X�g�H�ݒ肪���Ă���H
                        plen = 0;
                        break;
                    }
                    shift += 4;
                }
            }
            mPlayListLen = plen;
            
            // curpos�̎擾
            int pos = mPreferences.getInt("curpos", 0);
            if (pos < 0 || pos >= mPlayListLen) {
                // The saved playlist is bogus, discard it
            	// �ۑ�����Ă����ʒu�����Ă�����A���Z�b�g����
                mPlayListLen = 0;
                return;
            }
            mPlayPos = pos;
            
            // When reloadQueue is called in response to a card-insertion,
            // we might not be able to query the media provider right away.
            // To deal with this, try querying for the current file, and if
            // that fails, wait a while and try again. If that too fails,
            // assume there is a problem and don't restore the state.
            // �J�[�h�̑}����reloadQueue���Ă΂ꂽ���A�����Ƀv���o�C�_�ɃN�G���𓊂��邱�Ƃ͂ł��Ȃ���������Ȃ�
            // ����������Ƃ��A���݂̃t�@�C���̃N�G���Ƀg���C���A���ꂪ���s������A���΂炭�҂��Ă�����x�g���C����
            // ������܂����s������A��肪����̂��������Ƃ��A��Ԃ����X�g�A���Ȃ�
            // MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            // ���݂̃v���C���X�g��id�Ō����B�����炭���݊m�F
            Uri uri = null;
            String [] cursorCols = null;
        	mCurrentType = mPlayList[mPlayPos].getMediaType();
            if( mCurrentType == MediaInfo.MEDIA_TYPE_AUDIO)
            {
            	uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            	cursorCols = mCursorAudioCols;
            }
            else if( mCurrentType == MediaInfo.MEDIA_TYPE_VIDEO)
            {
            	uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            	cursorCols = mCursorVideoCols;
            }
            Cursor crsr = StorageInfo.query(this,
                        uri,
                        new String [] {"_id"}, "_id=" + mPlayList[mPlayPos].getId() , null, null);
            if (crsr == null || crsr.getCount() == 0) {
                // wait a bit and try again
            	// ���s������A3�b�҂��ă��g���C�H
                SystemClock.sleep(3000);
                crsr = getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        cursorCols, "_id=" + mPlayList[mPlayPos].getId() , null, null);
            }
            if (crsr != null) {
                crsr.close();
            }

            // ���L�̎��A���̋ȂփX�L�b�v�ł��Ȃ��H
            // Make sure we don't auto-skip to the next song, since that
            // also starts playback. What could happen in that case is:
            // - music is paused
            // - go to UMS and delete some files, including the currently playing one
            // - come back from UMS
            // (time passes)
            // - music app is killed for some reason (out of memory)
            // - music service is restarted, service restores state, doesn't find
            //   the "current" file, goes to the next and: playback starts on its
            //   own, potentially at some random inconvenient time.
            // �I�[�v�����s�̃J�E���^��20�ŏ������H���R�͕�����Ȃ��E�E�E
            mOpenFailedCounter = 20;
            // �G���[�ł����b�Z�[�W���o�͂��Ȃ����[�h
            mQuietMode = true;
            // ���݂̋Ȃ��I�[�v��
            openCurrent();
            mQuietMode = false;
            if (!mPlayer.isInitialized()) {
                // couldn't restore the saved state
            	// �����炭�I�[�v�����s
                mPlayListLen = 0;
                return;
            }
            
            // seek�ʒu���擾
            long seekpos = mPreferences.getLong("seekpos", 0);
            // seek�ʒu��0�ȏ�ŁAseek�ʒu���Ȃ̒���������������΁Aseek�ʒu�ֈړ�
            seek(seekpos >= 0 && seekpos < duration() ? seekpos : 0);
            // �L���[�����X�g�A�������ƁA���̎��̃|�W�V���������O�o��
            Log.d(LOGTAG, "restored queue, currently at position "
                    + position() + "/" + duration()
                    + " (requested " + seekpos + ")");
            // repeat���[�h�����X�g�A
            int repmode = mPreferences.getInt("repeatmode", REPEAT_NONE);
            if (repmode != REPEAT_ALL && repmode != REPEAT_CURRENT) {
                repmode = REPEAT_NONE;
            }
            mRepeatMode = repmode;

            // �V���b�t�����[�h�����X�g�A
            int shufmode = mPreferences.getInt("shufflemode", SHUFFLE_NONE);
            if (shufmode != SHUFFLE_AUTO && shufmode != SHUFFLE_NORMAL) {
                shufmode = SHUFFLE_NONE;
            }
            if (shufmode != SHUFFLE_NONE) {
            	// �V���b�t�����[�h�̏ꍇ
            	// �q�X�g���[�����X�g�A�H
                // in shuffle mode we need to restore the history too
                q = mPreferences.getString("history", "");
                qlen = q != null ? q.length() : 0;
                if (qlen > 1) {
                    plen = 0;
                    n = 0;
                    shift = 0;
                    mHistory.clear();
                    for (int i = 0; i < qlen; i++) {
                        char c = q.charAt(i);
                        if (c == ';') {
                            if (n >= mPlayListLen) {
                                // bogus history data
                            	// �f�[�^�����Ă���ꍇ
                            	// �q�X�g���[�}�b�v���N���A���āA������
                                mHistory.clear();
                                break;
                            }
                            mHistory.add(n);
                            n = 0;
                            shift = 0;
                        } else {
                            if (c >= '0' && c <= '9') {
                                n += ((c - '0') << shift);
                            } else if (c >= 'a' && c <= 'f') {
                                n += ((10 + c - 'a') << shift);
                            } else {
                                // bogus history data
                                mHistory.clear();
                                break;
                            }
                            shift += 4;
                        }
                    }
                }
            }
            if (shufmode == SHUFFLE_AUTO) {
            	// �I�[�g�V���b�t���̏ꍇ
                if (! makeAutoShuffleList()) {
                	// �I�[�g�V���b�t���̃��X�g���쐬���A���߂Ȃ�V���b�t�����[�h����������
                    shufmode = SHUFFLE_NONE;
                }
            }
            mShuffleMode = shufmode;
        }
    }
    
    /**
     * �T�[�r�X���o�C���h���ꂽ�Ƃ�
     */
    @Override
    public IBinder onBind(Intent intent) {
    	// ���Ԃ�u���Ē�~����n���h���̃��b�Z�[�W���N���A
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        // �T�[�r�X���p���t���O��ON��
        mServiceInUse = true;
        // �o�C���_��ԋp
        // ���g�́AServiceStub(this)�炵���B���������ō����N���X���H
        return mBinder;
    }

    /**
     * ������x�o�C���h���ꂽ�Ƃ��H
     */
    @Override
    public void onRebind(Intent intent) {
    	// ���Ԃ�u���Ē�~����n���h���̃��b�Z�[�W���N���A
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        // �T�[�r�X���p���t���O��ON��
        mServiceInUse = true;
    }

    /**
     * �X�^�[�g �V����android�ł�onStart�Ɏ���đ���炵��
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceStartId = startId;
        mDelayedStopHandler.removeCallbacksAndMessages(null);

        if (intent != null) {
        	// intent���w�肳��Ă�����
        	// action�擾
            String action = intent.getAction();
            // �R�}���h�擾
            String cmd = intent.getStringExtra("command");
            // MusicUtils.debugLog("onStartCommand " + action + " / " + cmd);

            if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
            	// ���փR�}���h ���փA�N�V����
                next(true);
            } else if (CMDPREVIOUS.equals(cmd) || PREVIOUS_ACTION.equals(action)) {
            	// �O��
                if (position() < 2000) {
                    prev();
                } else {
                    seek(0);
                    play();
                }
            } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
            	// �g�O���|�[�Y
                if (isPlaying()) {
                    pause();
                    mPausedByTransientLossOfFocus = false;
                } else {
                    play();
                }
            } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
            	// �|�[�Y
                pause();
                mPausedByTransientLossOfFocus = false;
            } else if (CMDSTOP.equals(cmd)) {
            	// �X�g�b�v
                pause();
                mPausedByTransientLossOfFocus = false;
                seek(0);
            }
        }
        
        // make sure the service will shut down on its own if it was
        // just started but not bound to and nothing is playing
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
        
        // �Ƃ肠���������ɂ����AActivity�ւ̃{�^���X�V�ʒm����������
        Intent activityNotifyIntent = new Intent();
        activityNotifyIntent.setAction(
        		OkosamaMediaPlayerActivity.MEDIA_SERVICE_NOTIFY);
        getBaseContext().sendBroadcast(activityNotifyIntent);
        
        // �T�[�r�X�������I�������ꍇ�A�T�[�r�X�͍ċN������onStartCommand()���ēx�Ăяo����AIntent��null���n�����
        return START_STICKY;
    }
    
    /**
     * �o�C���h����
     */
    @Override
    public boolean onUnbind(Intent intent) {
    	// �T�[�r�X���t���O�𗎂Ƃ�
        mServiceInUse = false;

        // �t���Z�[�u
        // Take a snapshot of the current playlist
        saveQueue(true);

        if (isPlaying() || mPausedByTransientLossOfFocus) {
            // something is currently playing, or will be playing once 
            // an in-progress action requesting audio focus ends, so don't stop the service now.
        	// �v���C����g�����W�F���g�t�H�[�J�X�������H�Ȃ�΁A�T�[�r�X�͂����ł͎~�߂��A�Ȃ�炩�̃A�N�V�����������ŋN����̂�҂�
            return true;
        }
        
        // If there is a playlist but playback is paused, then wait a while
        // before stopping the service, so that pause/resume isn't slow.
        // Also delay stopping the service if we're transitioning between tracks.
        // �v���C���~�܂��Ă���̂ɁA�v���C���X�g�L��
        // ���΂炭�҂��Ă���T�[�r�X���~�߂�
        if (mPlayListLen > 0  || mMediaplayerHandler.hasMessages(TRACK_ENDED)) {
            Message msg = mDelayedStopHandler.obtainMessage();
            mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
            return true;
        }
        
        // No active playlist, OK to stop the service right now
        // �����~�߂�
        stopSelf(mServiceStartId);
        return true;
    }
    
    /**
     * ���Ԃ�u���Ă��特�y���~�߂邽�߂̃n���h���H
     */
    private Handler mDelayedStopHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	// ���b�Z�[�W�̏���
            // Check again to make sure nothing is playing right now
            if (isPlaying() || mPausedByTransientLossOfFocus || mServiceInUse
                    || mMediaplayerHandler.hasMessages(TRACK_ENDED)) {
            	// �Đ���
            	// �g�����W�F���g��
            	// �T�[�r�X���p��
            	// �g���b�N�I���҂��H
            	// �̏ꍇ�A��Ԃ�ێ������ɏI��
                return;
            }
            // save the queue again, because it might have changed
            // since the user exited the music app (because of
            // party-shuffle or because the play-position changed)
            saveQueue(true);
            // �����́AThe most recent start identifier received in onStart(Intent, int)
            stopSelf(mServiceStartId);
        }
    };
    
    /**
     * Called when we receive a ACTION_MEDIA_EJECT notification.
     *
     * @param storagePath path to mount point for the removed media
     */
    public void closeExternalStorageFiles(String storagePath) {
        // stop playback and clean up if the SD card is going to be unmounted.
        stop(true);
        // mNowPlayingListener���炢�ł�����M���Ă��Ȃ����ۂ�
        notifyChange(QUEUE_CHANGED);
        notifyChange(META_CHANGED);
    }

    /**
     * Registers an intent to listen for ACTION_MEDIA_EJECT notifications.
     * The intent will call closeExternalStorageFiles() if the external media
     * is going to be ejected, so applications can clean up any files they have open.
     * ���f�B�A�����o���ꂽ���̏���
     */
    public void registerExternalStorageListener() {
        if (mUnmountReceiver == null) {
        	// �A���}�E���g���V�[�o���܂��Ȃ��ꍇ
            mUnmountReceiver = new BroadcastReceiver() {
            	// �A���}�E���g���V�[�o���쐬����
                @Override
                public void onReceive(Context context, Intent intent) {
                	// ��M��
                    String action = intent.getAction();
                    if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                    	// ���f�B�A�����o���ꂽ�ꍇ
                    	// �L���[��ۑ�
                        saveQueue(true);
                        // ���ꂪ�����Ă�����A������x��Ԃ��Z�[�u����̂�h�~����H
                        mOneShot = true; // This makes us not save the state again later,
                                         // which would be wrong because the song ids and
                                         // card id might not match. 
                        // �O���X�g���[�W�����
                        closeExternalStorageFiles(intent.getData().getPath());
                    } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                    	// ���f�B�A���}�E���g���ꂽ
                        mMediaMountedCount++; // �}�E���g�J�E���g���C���N�������g
                        mCardId = StorageInfo.getCardId(MediaPlaybackService.this);
                        // �ݒ�̃����[�h�H
                        reloadQueue();
                        // �ύX�̃u���[�h�L���X�g�ʒm
                        notifyChange(QUEUE_CHANGED);
                        notifyChange(META_CHANGED);
                    }
                }
            };
            // �C�W�F�N�g�ƃ}�E���g�����󂯎��悤�ɁH�t�B���^�������āAUnmountReceiver��o�^����
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
            iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            iFilter.addDataScheme("file");
            registerReceiver(mUnmountReceiver, iFilter);
        }
    }

    /**
     * Notify the change-receivers that something has changed.
     * The intent that is sent contains the following data
     * for the currently playing track:
     * "id" - Integer: the database row ID
     * "artist" - String: the name of the artist
     * "album" - String: the name of the album
     * "track" - String: the name of the track
     * The intent has an action that is one of
     * "com2.android.music.metachanged"
     * "com2.android.music.queuechanged",
     * "com2.android.music.playbackcomplete"
     * "com2.android.music.playstatechanged"
     * respectively indicating that a new track has
     * started playing, that the playback queue has
     * changed, that playback has stopped because
     * the last file in the list has been played,
     * or that the play-state changed (paused/resumed).
     */
    private void notifyChange(String what) {
        
    	// ��{��intent���u���[�h�L���X�g�œ����邾���H
        Intent i = new Intent(what);
        // audioId
        i.putExtra("id", Long.valueOf(getAudioId()));
        // �A�[�e�B�X�g��
        i.putExtra("artist", getArtistName());
        // �A���o����
        i.putExtra("album",getAlbumName());
        // �g���b�N��
        i.putExtra("track", getTrackName());
        sendBroadcast(i);
        
        if (what.equals(QUEUE_CHANGED)) {
        	// �L���[���ς�������b�Z�[�W�̏ꍇ�A�ݒ���t���ōēx�ۑ�
            saveQueue(true);
        } else {
        	// ���̑��̏ꍇ�A�t���ł͂Ȃ��ݒ�Őݒ���ēx�ۑ�
            saveQueue(false);
        }
        
        // Share this notification directly with our widgets
        mAppWidgetProvider.notifyChange(this, what);
    }

    /**
     * �v���C���X�g�̃L���p�V�e�B���m�ۂ���
     * @param size
     */
    private void ensurePlayListCapacity(int size) {
        if (mPlayList == null || size > mPlayList.length) {
            // reallocate at 2x requested size so we don't
            // need to grow and copy the array for every
            // insert
        	// �ǉ����Ƃɔz��̊g��ƃR�s�[��h�����߂ɁA�T�C�Y��2�{�̗̈���m��
            MediaInfo [] newlist = new MediaInfo[size * 2];
            int len = mPlayList != null ? mPlayList.length : mPlayListLen;
            for (int i = 0; i < len; i++) {
            	if( mPlayList[i] == null )
            	{
            		continue;
            	}
            	// ���݂̃v���C���X�g�̐ݒ�l��V�������X�g�ɃR�s�[
            	newlist[i] = new MediaInfo( mPlayList[i].getId(), mPlayList[i].getMediaType() );
            }
            mPlayList = newlist;
        }
        // FIXME: shrink the array when the needed size is much smaller
        // than the allocated size
    }
    
    /**
     * �v���C���X�g�ւ̒ǉ�
     * @param list
     * @param position �v���C���X�g�̑}���ʒu
     */
    // insert the list of songs at the specified position in the playlist
    private void addToPlayList(long [] list, int[] type, int position) {
        int addlen = list.length;
        if (position < 0) { // overwrite
            mPlayListLen = 0;
            position = 0;
        }
        ensurePlayListCapacity(mPlayListLen + addlen);
        if (position > mPlayListLen) {
            position = mPlayListLen;
        }
        
        // move part of list after insertion point
        int tailsize = mPlayListLen - position;
        for (int i = tailsize ; i > 0 ; i--) {
            mPlayList[position + i] = mPlayList[position + i - addlen]; 
        }
        
        // copy list into playlist
        for (int i = 0; i < addlen; i++) {
        	mPlayList[position + i] = new MediaInfo( list[i], type[i] );
        }
        mPlayListLen += addlen;
    }
    
    /**
     * Appends a list of tracks to the current playlist.
     * If nothing is playing currently, playback will be started at
     * the first track.
     * If the action is NOW, playback will switch to the first of
     * the new tracks immediately.
     * ���݂̃v���C���X�g�Ƀg���b�N�̃��X�g��ǉ�
     * ���ݍĐ����̂��̂��Ȃ���΁A�ŏ��̃g���b�N���Đ�
     * action��NOW�Ȃ�΁A�����V�����g���b�N�̍ŏ����Đ�
     * @param list The list of tracks to append.
     * @param action NOW, NEXT or LAST
     */
    public void enqueue(long [] list, int[] type, int action) {
        synchronized(this) {
            if (action == NEXT && mPlayPos + 1 < mPlayListLen) {
            	// �l�N�X�g�w��ŁA���̈ʒu�ɑ}���\�ȏꍇ
            	// ���݈ʒu+1�ɑ}��
                addToPlayList(list, type, mPlayPos + 1);
                // QUEUE_CHANGED�ʒm
                // ->�L���[�̕ۑ��Ǝv����
                notifyChange(QUEUE_CHANGED);
            } else {
                // action == LAST || action == NOW || mPlayPos + 1 == mPlayListLen
            	// �l�N�X�g�łȂ��ꍇ�A�Ō���ɒǉ�����
                addToPlayList(list, type, Integer.MAX_VALUE);
                notifyChange(QUEUE_CHANGED);
                if (action == NOW) {
                	// NOW�̏ꍇ
                	// �ǉ��������̂̓�����Đ�
                    mPlayPos = mPlayListLen - list.length;
                    openCurrent();
                    play();
                    // META_CHANGED���M
                    // ��ɁA�\���̍X�V�H
                    notifyChange(META_CHANGED);
                    return;
                }
            }
            if (mPlayPos < 0) {
            	// �Đ��ʒu���}�C�i�X�ɂȂ��Ă��܂��Ă�����A�Đ��ʒu��0�ɂ��čĐ�����H
            	// ->�}�C�i�X�ɂȂ�Ƃ����󋵂��悭�킩��Ȃ�
                mPlayPos = 0;
                openCurrent();
                play();
                notifyChange(META_CHANGED);
            }
        }
    }

    /**
     * Replaces the current playlist with a new list,
     * and prepares for starting playback at the specified
     * position in the list, or a random position if the
     * specified position is 0.
     * ���݂̃v���C���X�g���A�V�����w�肳�ꂽ���X�g�Œu��������i���݂̃v���C���X�g�͔j���j
     * �ʒu0���w�肳�ꂽ�烉���_���ʒu����A����ȊO���w�肳��Ă����炻�̈ʒu����Đ�����
     * @param list The new list of tracks.
     */
    public void open(long [] list, int [] type, int position) {
        synchronized (this) {
        	// ���̃N���X�����b�N
            if (mShuffleMode == SHUFFLE_AUTO) {
            	// �V���b�t�����[�h��������A�V���b�t�����[�h����������
                mShuffleMode = SHUFFLE_NORMAL;
            }
            // �ߋ���id�Ƃ��āA���ݍĐ����̋Ȃ�id���擾
            long oldId = getAudioId();
            int listlength = list.length;
            boolean newlist = true;
            if (mPlayListLen == listlength) {
            	// �O�̃v���C���X�g�̒����ƐV�������X�g�̒�������v�H
                // possible fast path: list might be the same
                newlist = false;
                for (int i = 0; i < listlength; i++) {
                    if (list[i] != mPlayList[i].getId() || type[i] != mPlayList[i].getMediaType() ) {
                    	// �P���ɁA�S�Ă̍��ڂ̒l�����ւ���
                        newlist = true;
                        break;
                    }
                }
            }
            if (newlist) {
            	// ���X�g���㏑������(-1�w��œ�����㏑��)
            	// ����Ȃɂ��΂炵�������ł͂Ȃ��̂�������Ȃ����A�����T�C�Y���m�ۂ����
                addToPlayList(list, type, -1);
                // ���X�g���ύX���ꂽ�̂ŁA�ۑ�������
                notifyChange(QUEUE_CHANGED);
            }
            // �O�̍Đ��ʒu���擾�H
            // int oldpos = mPlayPos;
            if (position >= 0) {
            	// �����ňʒu���w�肳��Ă�����A������Đ��ʒu�ɂ���
                mPlayPos = position;
            } else {
            	// �w�肳��Ă��Ȃ�������A�����_���l���Đ��ʒu�ɂ���
                mPlayPos = mRand.nextInt(mPlayListLen);
            }
            // �Đ������N���A
            mHistory.clear();

            saveBookmarkIfNeeded();
            // ���݃v���C���X�g�̃I�[�v��
            openCurrent();
            if (oldId != getAudioId()) {
            	// �Ȃ��ς������A�\�����X�V
                notifyChange(META_CHANGED);
            }
        }
    }
    
    /**
     * Moves the item at index1 to index2.
     * �v���C���X�g�̍��ڂ��ړ�����
     * �����A�P���ɓ���ւ��邾���B�������A����ւ��ɂ���đ��̍��ڂ̏��ԂƁA
     * �v���C����index���ς��炵��
     * @param index1
     * @param index2
     */
    public void moveQueueItem(int index1, int index2) {
        synchronized (this) {
        	// ���̃N���X�����b�N
            if (index1 >= mPlayListLen) {
            	// �ŏ��̂��A�v���C���X�g�������傫��
            	// index���A�Ō�̍��ڂֈړ�
                index1 = mPlayListLen - 1;
            }
            if (index2 >= mPlayListLen) {
            	// 1�Ɠ��l
                index2 = mPlayListLen - 1;
            }
            if (index1 < index2) {
                MediaInfo tmp = mPlayList[index1];
                for (int i = index1; i < index2; i++) {
                    mPlayList[i].copy( mPlayList[i+1] );
                }
                mPlayList[index2].copy( tmp );
                if (mPlayPos == index1) {
                    mPlayPos = index2;
                } else if (mPlayPos >= index1 && mPlayPos <= index2) {
                        mPlayPos--;
                }
            } else if (index2 < index1) {
            	MediaInfo tmp = mPlayList[index1];
                for (int i = index1; i > index2; i--) {
                    mPlayList[i].copy( mPlayList[i-1] );
                }
                mPlayList[index2].copy( tmp );
                if (mPlayPos == index1) {
                    mPlayPos = index2;
                } else if (mPlayPos >= index2 && mPlayPos <= index1) {
                        mPlayPos++;
                }
            }
            notifyChange(QUEUE_CHANGED);
        }
    }

    /**
     * Returns the current play list
     * ���݂̃v���C���X�g��ԋp�H
     * @return An array of integers containing the IDs of the tracks in the play list
     * �v���C���X�g�̔z��(=AudioID�̔z��)
     */
    public long [] getQueue() {
        synchronized (this) {
        	// �N���X�̃��b�N
        	// �P���ɁA���݂̃v���C���X�g���R�s�[�����z���return
            int len = mPlayListLen;
            long [] list = new long[len];
            for (int i = 0; i < len; i++) {
                list[i] = mPlayList[i].getId();
            }
            return list;
        }
    }
    /**
     * Returns the current play list
     * ���݂̃v���C���X�g��ԋp�H
     * @return An array of integers containing the IDs of the tracks in the play list
     * �v���C���X�g�̔z��(=AudioID�̔z��)
     */
    public int [] getMediaType() {
        synchronized (this) {
        	// �N���X�̃��b�N
        	// �P���ɁA���݂̃v���C���X�g���R�s�[�����z���return
            int len = mPlayListLen;
            int [] listType = new int[len];
            for (int i = 0; i < len; i++) {
                listType[i] = mPlayList[i].getMediaType();
            }
            return listType;
        }
    }
    
    /**
     * �����A���݂̍��ڂ��I�[�v������
     * stop->�ȏ��擾-> 
     */
    private void openCurrent() {
        synchronized (this) {
        	// �����炭���̃T�[�r�X�����b�N����
            if (mCursor != null) {
            	// �J�[�\�����i�[����Ă�����A����
                mCursor.close();
                mCursor = null;
            }
            if (mPlayListLen == 0) {
            	// �v���C���X�g���Ȃ���΁A�I��
                return;
            }
            // �Ȃ��~�߂�Bfalse=�����ŁH
            stop(false);

            // ���݂̋Ȃ�id���擾
            String id = String.valueOf(mPlayList[mPlayPos].getId());
            
            Uri uri = null;
            String [] cursorCols = null;
        	mCurrentType = mPlayList[mPlayPos].getMediaType();
            if( mCurrentType == MediaInfo.MEDIA_TYPE_AUDIO )
            {
            	 uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            	 cursorCols = mCursorAudioCols;
            }
            else if ( mCurrentType == MediaInfo.MEDIA_TYPE_VIDEO )
            {
            	uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
           	 	cursorCols = mCursorVideoCols;
            }
            
            
            // ���݂̋Ȃ̏����擾
            mCursor = getContentResolver().query(
            		uri,
            		cursorCols, "_id=" + id , null, null);
            if (mCursor != null) {
            	// �擾�ł�����
            	// �J�[�\���̍ŏ��̍��ڂ��A�I�[�v������
                mCursor.moveToFirst();
                // �I�[�v���iMultiPlayer.setDataSource�ōĐ����������)
                open(uri + "/" + id, false, mPlayList[mPlayPos].getMediaType());
                // go to bookmark if needed
                if (isPodcast()) {
                	// Podcast�ł����
                	// �i���݂̃J�[�\������APodcast�t���O�����Ĕ��肵�Ă���j
                	// ���݂̃J�[�\������A�u�b�N�}�[�N�t���O���擾
                    long bookmark = getBookmark();
                    // Start playing a little bit before the bookmark,
                    // so it's easier to get back in to the narrative.
                    // �u�b�N�}�[�N����A5s�O��seek?�Ȃ����낤�H
                    // ���[�U�͂����Ȃ�u�b�N�}�[�N����n�܂�ƔF���ł��Ȃ����炩��
                    seek(bookmark - 5000);
                }
            }
        }
    }

    /**
     * �񓯊��ŃI�[�v���H
     * @param path
     */
    public void openAsync(String path) {
        synchronized (this) {
            if (path == null) {
                return;
            }
            
            // ���s�[�g���[�h���Ȃ��ɁH
            mRepeatMode = REPEAT_NONE;
            // �v���C���X�g��1���ɁH
            ensurePlayListCapacity(1);
            mPlayListLen = 1;
            mPlayPos = -1;
            
            mFileToPlay = path;
            // �J�[�\����Null�ɁH
            mCursor = null;
            // �񓯊��ōĐ�����
            mPlayer.setDataSourceAsync(mFileToPlay);
            // oneshot?
            mOneShot = true;
        }
    }
    
    /**
     * Opens the specified file and readies it for playback.
     * ����̃t�@�C�����I�[�v�����A���̍Đ�����������
     * @param path The full path of the file to be opened.
     * @param oneshot when set to true, playback will stop after this file completes, instead
     * of moving on to the next track in the list 
     */
    public void open(String path, boolean oneshot, int mediaType) {
        synchronized (this) {
        	// �����炭���̃T�[�r�X�����b�N����
            if (path == null) {
            	// �p�X��null�Ȃ�ΏI��
                return;
            }
            
            if (oneshot) {
            	// oneshot�Ȃ�΁A���s�[�g���Ȃ��ݒ�ɂ��A�v���C���X�g��1�ɂ���
                mRepeatMode = REPEAT_NONE;
                ensurePlayListCapacity(1);
                mPlayListLen = 1;
                mPlayPos = -1;
            }
            
            // if mCursor is null, try to associate path with a database cursor
            if (mCursor == null) {
            	// �J�[�\����null�Ȃ��
                ContentResolver resolver = getContentResolver();
                Uri uri = null;
                String [] cursorCols = null;
                
                String where;
                String selectionArgs[];
                mCurrentType = mPlayList[mPlayPos].getMediaType();
                
                if (path.startsWith("content://media/")) {
                	// �p�X��content://media�Ŏn�܂��Ă�����
                	// �p�X��uri�ɕϊ�
                    uri = Uri.parse(path);
                    // �N�G���̏������N���A
                    where = null;
                    selectionArgs = null;
                } else {
                   // �����łȂ��ꍇ���A�p�X��uri�ɕϊ�����H
                   // TODO:getContentUriForPath�𒲍�
                   // uri = MediaStore.Audio.Media.getContentUriForPath(path);
                   // ���̏ꍇ�A������ݒ�
                   where = MediaColumns.DATA + "=?";
                   selectionArgs = new String[] { path };

                   if( mCurrentType == MediaInfo.MEDIA_TYPE_AUDIO)
                   {
	                   	uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                   }
                   else if( mCurrentType == MediaInfo.MEDIA_TYPE_VIDEO)
                   {
	                   	uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                   }
                }
                if( mCurrentType == MediaInfo.MEDIA_TYPE_AUDIO)
                {
	                   	cursorCols = mCursorAudioCols;
                }
                else if( mCurrentType == MediaInfo.MEDIA_TYPE_VIDEO)
                {
	                   	cursorCols = mCursorVideoCols;
                }
                
                try {
                	// �N�G���̔��s
                    mCursor = resolver.query(uri, cursorCols, where, selectionArgs, null);
                    if  (mCursor != null) {
                    	// ���ʂ��擾�ł���
                        if (mCursor.getCount() == 0) {
                        	// ���ʂ�0
                        	// �J�[�\���N���[�Y
                            mCursor.close();
                            mCursor = null;
                        } else {
                        	// ���ʂ��P���ȏ�
                        	// 1���ڂ̌��ʂ�IDCOLIDX���v���C���X�g�Ƃ��ĕێ�
                            mCursor.moveToNext();
                            ensurePlayListCapacity(1);
                            mPlayListLen = 1;
                            mPlayList[0] = new MediaInfo( mCursor.getLong(IDCOLIDX),
                            		MediaInfo.MEDIA_TYPE_AUDIO);
                            mPlayPos = 0;
                        }
                    }
                } catch (UnsupportedOperationException ex) {
                }
            }
            // �v���C���[�̃f�[�^�\�[�X�Ƃ��āA�w�肳�ꂽ�t�@�C����ݒ肷��
            mFileToPlay = path;
            // �Đ����������
            mPlayer.setDataSource(mFileToPlay,mCurrentType);
            mOneShot = oneshot;
            if (! mPlayer.isInitialized()) {
            	// �܂��v���C���[������������Ă��Ȃ�
            	// ����������ƁA����͂܂�setDataSource�ŃG���[�ɂȂ������Ƃ�\���̂�������Ȃ�
            	// �Ƃ肠�����A�~�߂�H
                stop(true);
                if (mOpenFailedCounter++ < 10 &&  mPlayListLen > 1) {
                	// �G���[�J�E���^���C���N�������g
                    // beware: this ends up being recursive because next() calls open() again.
                	// �G���[��10���ȉ��ŁA�v���C���X�g��2���ȏ゠��΁A���̋ȂցH
                    next(false);
                }
                if (! mPlayer.isInitialized() && mOpenFailedCounter != 0) {
                    // need to make sure we only shows this once
                	// �v���C���[������������Ă��Ȃ��āA�I�[�v�����s�������
                	// �I�[�v�����s�̃J�E���^�N���A
                    mOpenFailedCounter = 0;
                    if (!mQuietMode) {
                    	// �o���Ă����ꍇ�́A���[�U�ɃG���[���b�Z�[�W�o�́H
                        Toast.makeText(this, R.string.playback_failed, Toast.LENGTH_SHORT).show();
                    }
                    Log.d(LOGTAG, "Failed to open file for playback");
                }
            } else {
            	// �v���C��������������Ă���ꍇ
            	// =�f�[�^�\�[�X�I�[�v�������H
                mOpenFailedCounter = 0;
            }
        }
    }
    public boolean isInitialized()
    {
    	return mPlayer.isInitialized();
    }

    /**
     * Starts playback of a previously opened file.
     * �t�@�C���I�[�v�����O�����ăv���C�o�b�N���J�n����H
     */
    public void play() {
        if( this.getCurrentType() == MediaInfo.MEDIA_TYPE_VIDEO )
        {
            // Keep screen on
        	OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  
        }
        else
        {
        	// Keep screen off
        	OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);        	
	        mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC,
	                AudioManager.AUDIOFOCUS_GAIN);
	        mAudioManager.registerMediaButtonEventReceiver(new ComponentName(this.getPackageName(),
	                MediaButtonIntentReceiver.class.getName()));
        }
        if (mPlayer.isInitialized()) {
        	// ����OK���ł����
            // if we are at the end of the song, go to the next song first
        	// �Ȃ��I�������A���̋Ȃ̍ŏ��Ɉړ�����
            long duration = mPlayer.duration();
            // ���݋Ȃ̃��s�[�g�ł͂Ȃ��A�Đ����Ԃ�20s�ȏ�ŁAposition���Đ�����-20s���傫��
            if (mRepeatMode != REPEAT_CURRENT && duration > 2000 &&
                mPlayer.position() >= duration - 2000) {
            	// ���̋Ȃ�
                next(true);
            }

            // ��������Ă���t�@�C�����Đ��H
            mPlayer.start();
            // �ʃv���Z�X��ŕ\���\�ƂȂ�K�w�I��View���L�q����N���X
            // �X�e�[�^�X�o�[�̃��C�A�E�g��ݒ�H
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.statusbar);
            // �C���[�W��ݒ�H
            views.setImageViewResource(R.id.icon, R.drawable.stat_notify_musicplayer);
            String ticket;
//            if (getAudioId() < 0) {
//                // streaming
//            	// �X�g���[�~���O
//                views.setTextViewText(R.id.trackname, getPath());
//                views.setTextViewText(R.id.artistalbum, null);
//                ticket = getPath();
//            } else {
            {
            	// ���ʂ̃��f�B�A�H
                String artist = getArtistName();
                // �g���b�N�����r���[�ɐݒ�
                views.setTextViewText(R.id.trackname, getTrackName());
                if (artist == null || artist.equals(MediaStore.UNKNOWN_STRING)) {
                	// �A�[�e�B�X�g���擾�ł��Ȃ�������A�A�[�e�B�X�g��Unknown�ɐݒ�
                    artist = getString(R.string.unknown_artist_name);
                }
                // �A���o�����擾
                String album = getAlbumName();
                if (album == null || album.equals(MediaStore.UNKNOWN_STRING)) {
                	// �A���o�������擾�ł��Ȃ���΁A�A���o������Unknown�Ɏ擾
                    album = getString(R.string.unknown_album_name);
                }
                
                // �A���o�����ƃA�[�e�B�X�g�����r���[�ɐݒ�
                views.setTextViewText(R.id.artistalbum,
                        getString(R.string.notification_artist_album, artist, album)
                        );
                ticket = getTrackName() + "-" + artist; //+ "[" + album + "]" + " - " + artist;
            }
            
            // Notification�N���X�̍쐬
            Notification status = new Notification();
            // 
            status.tickerText = ticket;
            // Notification�N���X�ɁA�r���[��ݒ�
            status.contentView = views;
            // Notification���풓������H
            //status.flags |= Notification.FLAG_ONGOING_EVENT;
//            status.ledARGB = 0xffffff00;
//            status.ledOnMS = 300;
//            status.ledOffMS = 1000;
            // status.flags |= Notification.DEFAULT_LIGHTS;
                        
            // Notification�̃A�C�R����ݒ�
            status.icon = R.drawable.stat_notify_musicplayer;
            SharedPreferences prefs = getSharedPreferences(
                    MusicSettingsActivity.PREFERENCES_FILE, MODE_PRIVATE);            
            boolean bVib = prefs.getBoolean(MusicSettingsActivity.KEY_ENABLE_MEDIA_CHANGE_VIBRATE, false);

            if( bVib )
            {
	            // �o�C�u����΍Đ����ł��邱�ƂɋC�Â��̂�
//                status.flags |= Notification.DEFAULT_VIBRATE;
//                status.vibrate = new long[]{250,50,750,10};
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                String sVib = prefs.getString(MusicSettingsActivity.KEY_VIBRATE_INTENSITY, "");
                //LogWrapper.e("Vib",sVib);
                long nVib = 0;
                if( sVib != null && sVib.length() > 0 )
                {
                	nVib = Long.parseLong(sVib);
                }
                vibrator.vibrate(nVib);
            }
            // �N���b�N���ɔ��s�����C���e���g�H���낤���H
            // �^�C�~���O���w�肵�Ĕ��s�ł���C���e���g
            // ����͑����ANotification���N���b�N���ꂽ�Ƃ�
            Intent clickIntent = new Intent();
            clickIntent.setClassName(
            		"okosama.app", "okosama.app.OkosamaMediaPlayerActivity");
            status.contentIntent = PendingIntent.getActivity(this, 0,
            		// TODO: Activity�ύX
            		clickIntent
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
            // statusbar��Notification�\��
            startForeground(PLAYBACKSERVICE_STATUS, status);
            if (!mIsSupposedToBePlaying) {
            	// ����͂����炭�v���C���t���O�Ƃ��ė��p����Ă���
            	// �܂����ꂪ�����Ă��Ȃ����
            	// ���p���t���O�𗧂Ă�
                mIsSupposedToBePlaying = true;
                // �Đ���Ԃ̕ύX��ʒm����
                notifyChange(PLAYSTATE_CHANGED);
            }

        } else if (mPlayListLen <= 0) {
        	// �����Đ�����Ă��Ȃ��Ƃ��Ƀv���C�{�^�����������ƁA�V���b�t�����[�h�ɂ���H
            // This is mostly so that if you press 'play' on a bluetooth headset
            // without every having played anything before, it will still play
            // something.
            if (!mQuietMode) {
            	// �o���Ă����ꍇ�́A���[�U�ɃG���[���b�Z�[�W�o�́H
            	// TODO: resouce���p
                Toast.makeText(this, "auto shuffle�ōĐ����܂��I", Toast.LENGTH_SHORT).show();
            }
        	
            setShuffleMode(SHUFFLE_AUTO);
        }
    }
    
    private void stop(boolean remove_status_icon) {
    	LogWrapper.w("stop","stop come!");
        if (mPlayer.isInitialized()) {
        	// �v���C���[��~
            mPlayer.stop();
        }
        mFileToPlay = null;
        if (mCursor != null) {
        	// �J�[�\���N���[�Y
            mCursor.close();
            mCursor = null;
        }
        if (remove_status_icon) {
        	// ���������Ă���~�߂�H�Ȃ����낤�H
            gotoIdleState();
        } else {
        	// ����~�H
            stopForeground(false);
        }
        if (remove_status_icon) {
        	// �v���C���t���O�𗎂Ƃ��H
            mIsSupposedToBePlaying = false;
        }
    }

    /**
     * Stops playback.
     */
    public void stop() {
        stop(true);
    }

    /**
     * Pauses playback (call play() to resume)
     */
    public void pause() {
        synchronized(this) {
        	// Keep screen off
        	if( OkosamaMediaPlayerActivity.getResourceAccessor() != null
        	&& OkosamaMediaPlayerActivity.getResourceAccessor().getActivity() != null
        	&& OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getWindow() != null )
        	{
	        	OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getWindow().clearFlags(
	        			WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        	}
        	
        	// ���̃N���X�����b�N
            if (isPlaying()) {
            	// �Đ����ł����
            	// �v���C���[���|�[�Y
                mPlayer.pause();
                // �����Ԃ�u���ăX�g�b�v
                gotoIdleState();
                // �Đ����t���O�𗎂Ƃ�
                mIsSupposedToBePlaying = false;
                // �Đ���Ԃ̕ύX�ʒm
                notifyChange(PLAYSTATE_CHANGED);
                // Podcast�̏ꍇ�A�u�b�N�}�[�N��DB�ɕۑ�
                saveBookmarkIfNeeded();
            }
        }
    }

    /** Returns whether something is currently playing
     *
     * @return true if something is playing (or will be playing shortly, in case
     * we're currently transitioning between tracks), false if not.
     */
    public boolean isPlaying() {
        return mIsSupposedToBePlaying;
    }

    // �����܂œǂ�
    /*
      Desired behavior for prev/next/shuffle:
      �O/��/�V���b�t���̎��̐U�镑��

      - NEXT will move to the next track in the list when not shuffling, and to
        a track randomly picked from the not-yet-played tracks when shuffling.
        If all tracks have already been played, pick from the full set, but
        avoid picking the previously played track if possible.
        �V���b�t���łȂ��ꍇ�A���ւ̓��X�g�̎��̃g���b�N��
        �V���b�t���̏ꍇ�A�����_���ɂ܂��v���C����Ă��Ȃ����̂�I��ł��̃g���b�N��
      - when shuffling, PREV will go to the previously played track. Hitting PREV
        again will go to the track played before that, etc. When the start of the
        history has been reached, PREV is a no-op.
        When not shuffling, PREV will go to the sequentially previous track (the
        difference with the shuffle-case is mainly that when not shuffling, the
        user can back up to tracks that are not in the history).
		�V���b�t�����̂Ƃ��APREV�͍Đ����ꂽ�g���b�N�̑O�̂�ցB
		������xPREV���q�b�g����Ƃ��̑O�ցB�����̍ŏ��̍��ڂ̏ꍇ�APREV�͉������Ȃ�
		�V���b�t�����łȂ��Ƃ��APREV�̓V�[�P���X�̑O�̋ȂցB�����ɓ��ǂɂ��߂邱�Ƃ��ł���H
        Example:
        When playing an album with 10 tracks from the start, and enabling shuffle
        while playing track 5, the remaining tracks (6-10) will be shuffled, e.g.
        the final play order might be 1-2-3-4-5-8-10-6-9-7.
        When hitting 'prev' 8 times while playing track 7 in this example, the
        user will go to tracks 9-6-10-8-5-4-3-2. If the user then hits 'next',
        a random track will be picked again. If at any time user disables shuffling
        the next/previous track will be picked in sequential order again.
        ��F
        10�g���b�N�̃A���o�����Đ����ŁA�T�g���b�N�ڂŃV���b�t���ɂ��ꂽ�ꍇ�A�c��̃g���b�N���V���b�t�������
        ���̌�Anext�̓����_�������Aprev�͗������Đ�����
     */

    public void prev() {
        synchronized (this) {
        	// ���b�N
            if (mOneShot) {
            	// OneShot
                // we were playing a specific file not part of a playlist, so there is no 'previous'
            	// �v���C���X�g���ɂȂ�����̃t�@�C�����Đ����Ȃ�΁Aprev�͂Ȃ��B
            	// �Ȃ̓��ɖ߂邾��
                seek(0);
                play();
                return;
            }
            if (mShuffleMode == SHUFFLE_NORMAL) {
            	// �m�[�}���V���b�t�����[�h
                // go to previously-played track and remove it from the history
            	// �O�̗����̃g���b�N���Đ����A����𗚗��������
                int histsize = mHistory.size();
                if (histsize == 0) {
                    // prev is a no-op
                    return;
                }
                Integer pos = mHistory.remove(histsize - 1);
                mPlayPos = pos.intValue();
            } else {
            	// �V���b�t���łȂ��Ȃ�A�V�[�P���X��߂�
                if (mPlayPos > 0) {
                    mPlayPos--;
                } else {
                    mPlayPos = mPlayListLen - 1;
                }
            }
            // �u�b�N�}�[�N�ۑ��A��~�A�I�[�v���A�Đ��A��ʍX�V
            saveBookmarkIfNeeded();
            stop(false);
            openCurrent();
            play();
            notifyChange(META_CHANGED);
        }
    }

    /**
     * ����
     * @param force�@true�Ȃ�΁A�����I�H
     */
    public void next(boolean force) {
        synchronized (this) {
        	// ���b�N
            if (mOneShot) {
                // we were playing a specific file not part of a playlist, so there is no 'next'
            	// �v���C���X�g�̋Ȃ��Đ����Ă����ł͂Ȃ��ꍇ
            	// �Ȃ̓��ɖ߂�
                seek(0);
                play();
                return;
            }

            if (mPlayListLen <= 0) {
                Log.d(LOGTAG, "No play queue");
                return;
            }

            // Store the current file in the history, but keep the history at a
            // reasonable size
            // �����Ɍ��݂̋Ȃ�ǉ�
            if (mPlayPos >= 0) {
                mHistory.add(Integer.valueOf(mPlayPos));
            }
            if (mHistory.size() > MAX_HISTORY_SIZE) {
                mHistory.removeElementAt(0);
            }

            if (mShuffleMode == SHUFFLE_NORMAL) {
                // Pick random next track from the not-yet-played ones
                // TODO: make it work right after adding/removing items in the queue.
            	// �V���b�t�����[�h�̏ꍇ
            	// �܂��Đ�����Ă��Ȃ��Ȃ̒����烉���_���Đ�
                int numTracks = mPlayListLen;
                int[] tracks = new int[numTracks];
                for (int i=0;i < numTracks; i++) {
                    tracks[i] = i;
                }

                // �܂��Đ�����Ă��Ȃ��Ȃ̎擾
                int numHistory = mHistory.size();
                int numUnplayed = numTracks;
                for (int i=0;i < numHistory; i++) {
                    int idx = mHistory.get(i).intValue();
                    if (idx < numTracks && tracks[idx] >= 0) {
                        numUnplayed--;
                        tracks[idx] = -1;
                    }
                }

                // 'numUnplayed' now indicates how many tracks have not yet
                // been played, and 'tracks' contains the indices of those
                // tracks.
                // numUnplayed:�܂��v���C����Ă��Ȃ��Ȃ̐�
                // tracks:�܂��v���C����Ă��Ȃ��Ȃ̃C���f�b�N�X
                if (numUnplayed <=0) {
                    // everything's already been played
                	// �S�Ă����ɍĐ�����Ă���
                    if (mRepeatMode == REPEAT_ALL || force) {
                        //pick from full set
                    	// �S�ȃ��s�[�g�̏ꍇ�A�ŏ�����Đ�����
                    	// �����̏ꍇ���A���l
                        numUnplayed = numTracks;
                        for (int i=0;i < numTracks; i++) {
                            tracks[i] = i;
                        }
                    } else {
                    	// �Đ����~�߂�
                        // all done
                        gotoIdleState();
                        if (mIsSupposedToBePlaying) {
                            mIsSupposedToBePlaying = false;
                            notifyChange(PLAYSTATE_CHANGED);
                        }
                        return;
                    }
                }
                // �����_���ʒu�̐���
                int skip = mRand.nextInt(numUnplayed);
                int cnt = -1;
                while (true) {
                    while (tracks[++cnt] < 0)
                        ;
                    skip--;
                    if (skip < 0) {
                        break;
                    }
                }
                mPlayPos = cnt;
            } else if (mShuffleMode == SHUFFLE_AUTO) {
            	// �I�[�g�V���b�t������
            	// ���̊֐��ł́A�ΏۂƂȂ�Ȃ������_���ɔ����o�����肵�Ē�������̂ł͂Ȃ����Ǝv����B
                doAutoShuffleUpdate();
                mPlayPos++;
            } else {
            	// �V���b�t�����[�h����Ȃ�
                if (mPlayPos >= mPlayListLen - 1) {
                    // we're at the end of the list
                	// ���X�g�̍Ō�ɓ��B
                    if (mRepeatMode == REPEAT_NONE && !force) {
                        // all done
                    	// �~�߂�
                        gotoIdleState();
                        notifyChange(PLAYBACK_COMPLETE);
                        mIsSupposedToBePlaying = false;
                        return;
                    } else if (mRepeatMode == REPEAT_ALL || force) {
                    	// �v���C�ʒu���ŏ��ɖ߂�
                        mPlayPos = 0;
                    }
                } else {
                    mPlayPos++;
                }
            }
            // �u�b�N�}�[�N�ۑ��A������~�A�I�[�v���A�Đ��A��ʍX�V
            saveBookmarkIfNeeded();
            stop(false);
            openCurrent();
            play();
            notifyChange(META_CHANGED);
        }
    }
    
    /**
     * ���Ԃ�u���āA�Đ����~�߂�
     */
    private void gotoIdleState() {
    	// DelayStopHandler�̃��b�Z�[�W���폜�H
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        // IDLE_DELAY��ɏI��
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
        // Remove this service from foreground state, allowing it to be killed if more memory is needed.
        // the notification previously provided to startForeground(int, Notification) will be removed.
        stopForeground(true);
    }
    
    /**
     * ���ݍĐ����̎��Ԃ��u�b�N�}�[�N���ԂƂ��ăf�[�^�x�[�X�ɕۑ�����炵��
     * �������APodcast�Ɍ���
     */
    private void saveBookmarkIfNeeded() {
        try {
            if (isPodcast()) {
            	// �Đ����̃��f�B�A��podcast�̏ꍇ�̂ݏ�������H
                long pos = position();	// ���f�B�A�v���C���[���瓾���Đ�����
                long bookmark = getBookmark(); // �u�b�N�}�[�N����Ă��鎞�ԁH(=DB�̃J����)
                long duration = duration(); // �Ȃ̎���
                if ((pos < bookmark && (pos + 10000) > bookmark) ||
                        (pos > bookmark && (pos - 10000) < bookmark)) {
                    // The existing bookmark is close to the current
                    // position, so don't update it.
                    return;
                }
                if (pos < 15000 || (pos + 10000) > duration) {
                	// �Đ��ʒu��15s��菬�������A�Ȃ̒����𒴂��Ă���ꍇ�A�u�b�N�}�[�N���Ȃ�(=�u�b�N�}�[�N���Ԃ�0��ݒ�)
                    // if we're near the start or end, clear the bookmark
                    pos = 0;
                }
                
                // write 'pos' to the bookmark field
                // �u�b�N�}�[�N���Ԃ��㏑������
                ContentValues values = new ContentValues();
                values.put(AudioColumns.BOOKMARK, pos);
                Uri uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mCursor.getLong(IDCOLIDX));
                getContentResolver().update(uri, values, null, null);
            }
        } catch (SQLiteException ex) {
        }
    }

    // Make sure there are at least 5 items after the currently playing item
    // and no more than 10 items before.
    // ���ݍĐ����̍��ڂ���̍��ڂ����Ȃ��Ƃ�5����A10�͑O�ɂȂ�
    private void doAutoShuffleUpdate() {
        boolean notify = false;
        // remove old entries
        if (mPlayPos > 10) {
        	// 10�ڈȍ~�Ȃ�΁A�v���C���X�g��9�ɂȂ�悤�ɂ��̑O�̍��ڂ�S�č폜�H
            removeTracks(0, mPlayPos - 9);
            notify = true;
        }
        // add new entries if needed
        // �K�v�ł���΁A�V�����G���g����ǉ�
        int to_add = 7 - (mPlayListLen - (mPlayPos < 0 ? -1 : mPlayPos));
        for (int i = 0; i < to_add; i++) {
            // pick something at random from the list
        	// ���X�g���烉���_���ɂǂꂩ�����o���ăv���C���X�g�Ɋi�[����
            int idx = mRand.nextInt(mAutoShuffleList.length);
            long which = mAutoShuffleList[idx];
            ensurePlayListCapacity(mPlayListLen + 1);
            mPlayList[mPlayListLen++] = new MediaInfo( which, 
            		MediaInfo.MEDIA_TYPE_AUDIO );
            notify = true;
        }
        if (notify) {
            notifyChange(QUEUE_CHANGED);
        }
    }

    // A simple variation of Random that makes sure that the
    // value it returns is not equal to the value it returned
    // previously, unless the interval is 1.
    // �C���^�[�o��1�łȂ���΂��ꂪ�߂��l���O�ɖ߂��ꂽ�l�Ɠ������Ȃ�Ȃ��悤�ɂ���P���ȃ����_���̃o���G�[�V����
    private static class Shuffler {
        private int mPrevious;
        private Random mRandom = new Random();
        public int nextInt(int interval) {
            int ret;
            do {
                ret = mRandom.nextInt(interval);
            } while (ret == mPrevious && interval > 1);
            mPrevious = ret;
            return ret;
        }
    };

    /**
     * �I�[�g�V���b�t���̃��X�g�쐬�H
     * @return
     */
    private boolean makeAutoShuffleList() {
        ContentResolver res = getContentResolver();
        Cursor c = null;
        try {
        	// ���y��ID��S�Ď擾
            c = res.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[] {BaseColumns._ID}, AudioColumns.IS_MUSIC + "=1",
                    null, null);
            if (c == null || c.getCount() == 0) {
                return false;
            }
            int len = c.getCount();
            long [] list = new long[len];
            for (int i = 0; i < len; i++) {
            	// �S����ID�����X�g�Ɋi�[
                c.moveToNext();
                list[i] = c.getLong(0);
            }
            mAutoShuffleList = list;
            return true;
        } catch (RuntimeException ex) {
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return false;
    }
    
    /**
     * Removes the range of tracks specified from the play list. If a file within the range is
     * the file currently being played, playback will move to the next file after the
     * range. 
     * �v���C���X�g�̃g���b�N�̂���͈͂������B
     * �͈͓��̃t�@�C�������ݍĐ����Ȃ�΁A�͈͂̌�̍ŏ��̃t�@�C���ֈړ�����
     * @param first The first file to be removed
     * @param last The last file to be removed
     * @return the number of tracks deleted
     */
    public int removeTracks(int first, int last) {
        int numremoved = removeTracksInternal(first, last);
        if (numremoved > 0) {
            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }
    
    private int removeTracksInternal(int first, int last) {
        synchronized (this) {
        	// �N���X���b�N
            if (last < first) return 0;
            if (first < 0) first = 0;
            if (last >= mPlayListLen) last = mPlayListLen - 1;

            boolean gotonext = false;
            if (first <= mPlayPos && mPlayPos <= last) {
            	// �������͈͓��ɁA�Đ����̍��ڂ�����
                mPlayPos = first;
                gotonext = true;
            } else if (mPlayPos > last) {
                mPlayPos -= (last - first + 1);
            }
            int num = mPlayListLen - last - 1;
            // �ړ��H
            for (int i = 0; i < num; i++) {
                mPlayList[first + i] = mPlayList[last + 1 + i];
            }
            // ���������H
            mPlayListLen -= last - first + 1;
            
            if (gotonext) {
            	// ����
                if (mPlayListLen == 0) {
                	LogWrapper.w("playlist len = 0", "stop come!");
                	// �v���C���X�g�̒�����0
                	// ��~
                    stop(true);
                    mPlayPos = -1;
                } else {
                	// �v���C���X�g�̒���������
                    if (mPlayPos >= mPlayListLen) {
                        mPlayPos = 0;
                    }
                    // �~�߂āA�I�[�v�����āA���ݍĐ����Ȃ�΁A�Đ�����
                    boolean wasPlaying = isPlaying();
                    stop(false);
                    openCurrent();
                    if (wasPlaying) {
                        play();
                    }
                }
            }
            return last - first + 1;
        }
    }
    
    /**
     * Removes all instances of the track with the given id
     * from the playlist.
     * �v���C���X�g����id��^����ꂽ�g���b�N�̑S�ẴC���X�^���X���폜����
     * @param id The id to be removed
     * @return how many instances of the track were removed
     */
    public int removeTrack(long id) {
        int numremoved = 0;
        synchronized (this) {
            for (int i = 0; i < mPlayListLen; i++) {
            	// �Y��id�̑S�Ẵg���b�N���폜
                if (mPlayList[i].getId() == id) {
                    numremoved += removeTracksInternal(i, i);
                    i--;
                }
            }
        }
        if (numremoved > 0) {
        	// �ύX���ꂽ��A�L���[�ύX�ʒm�������āA�L���[��ۑ�������
            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }
    /**
     * �V���b�t�����[�h��ݒ肷��
     * @param shufflemode
     */
    public void setShuffleMode(int shufflemode) {
        synchronized(this) {
        	// �N���X�����b�N����
            if (mShuffleMode == shufflemode && mPlayListLen > 0) {
            	// �V���b�t�����[�h���ω��Ȃ��Ȃ�΁A�߂�
                return;
            }
            // �ύX�L��
            // �V�����V���b�t�����[�h��ݒ�
            mShuffleMode = shufflemode;
            if (mShuffleMode == SHUFFLE_AUTO) {
            	// �I�[�g�V���b�t���Ȃ�΁A�V���b�t�����X�g���쐬
                if (makeAutoShuffleList()) {
                	// ���X�g�쐬
                    mPlayListLen = 0;
                    // �쐬���ꂽ���X�g�̌��������ڂ��Ă���H�����������Ɍ����邪�E�E�E������낤���H
                    doAutoShuffleUpdate();
                    // �J��
                    mPlayPos = 0;
                    openCurrent();
                    // �Đ�
                    play();
                    // ��ʍX�V
                    notifyChange(META_CHANGED);
                    return;
                } else {
                    // failed to build a list of files to shuffle
                	// �V���b�t���p���X�g�쐬���s
                	// �V���b�t���Ȃ�
                    mShuffleMode = SHUFFLE_NONE;
                }
            }
            // �L���[��ۑ��H
            // �t���ł͂Ȃ��̂Ńv���C���X�g�͕ۑ�����Ȃ��͂������A�����͕ۑ������Ǝv����
            // ����ł����񂾂낤���H
            saveQueue(false);
        }
    }
    public int getShuffleMode() {
        return mShuffleMode;
    }
    
    public void setRepeatMode(int repeatmode) {
        synchronized(this) {
        	// �N���X�����b�N
        	// ���s�[�g���[�h��ݒ�
            mRepeatMode = repeatmode;
            // �L���[��ۑ��H
            // �t���ł͂Ȃ��̂Ńv���C���X�g�͕ۑ�����Ȃ��͂������A�����͕ۑ������Ǝv����
            // ����ł����񂾂낤���H
            saveQueue(false);
        }
    }
    public int getRepeatMode() {
        return mRepeatMode;
    }

    public int getMediaMountedCount() {
        return mMediaMountedCount;
    }

    /**
     * Returns the path of the currently playing file, or null if
     * no file is currently playing.
     * ���ݍĐ����̃t�@�C����ԋp�B���ݍĐ����łȂ���΁Anull
     */
    public String getPath() {
        return mFileToPlay;
    }
    
    /**
     * Returns the rowid of the currently playing file, or -1 if
     * no file is currently playing.
     */
    public long getAudioId() {
        synchronized (this) {
            if (mPlayPos >= 0 && mPlayer.isInitialized()
            	&& mPlayList[mPlayPos].getMediaType() == MediaInfo.MEDIA_TYPE_AUDIO ) 
            {
            	// �Đ��ʒu���L���ŁA�Đ����ł���΁A�Đ�����Ă���Ȃ�ID��ԋp
                return mPlayList[mPlayPos].getId();
            }
        }
        return -1;
    }
    
    /**
     * Returns the position in the queue 
     * @return the position in the queue
     */
    public int getQueuePosition() {
        synchronized(this) {
            return mPlayPos;
        }
    }
    
    /**
     * Starts playing the track at the given position in the queue.
     * �w�肳�ꂽ�L���[�̈ʒu�̋Ȃ��Đ�
     * @param pos The position in the queue of the track that will be played.
     */
    public void setQueuePosition(int pos) {
        synchronized(this) {
        	// �����X�g�b�v
            stop(false);
            mPlayPos = pos;
            // �I�[�v��
            openCurrent();
            // �Đ�
            play();
            // ��ʍX�V
            notifyChange(META_CHANGED);
            if (mShuffleMode == SHUFFLE_AUTO) {
            	// �I�[�g�V���b�t��
            	// �I�[�g�V���b�t���̍��ڂ��X�V�H
                doAutoShuffleUpdate();
            }
        }
    }

    public String getArtistName() {
        synchronized(this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(AudioColumns.ARTIST));
        }
    }
    
    public long getArtistId() {
        synchronized (this) {
            if (mCursor == null || mCurrentType == -1 || mCurrentType != MediaInfo.MEDIA_TYPE_AUDIO ) {
                return -1;
            }
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(AudioColumns.ARTIST_ID));
        }
    }

    public String getAlbumName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(AudioColumns.ALBUM));
        }
    }

    public long getAlbumId() {
        synchronized (this) {
            if (mCursor == null || mCurrentType == -1 || mCurrentType != MediaInfo.MEDIA_TYPE_AUDIO ) {
                return -1;
            }
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(AudioColumns.ALBUM_ID));
        }
    }

    public String getTrackName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaColumns.TITLE));
        }
    }

    private boolean isPodcast() {
        synchronized (this) {
            if (mCursor == null || mCurrentType == -1 || mCurrentType != MediaInfo.MEDIA_TYPE_AUDIO ) {
                return false;
            }
            return (mCursor.getInt(PODCASTCOLIDX) > 0);
        }
    }
    
    private long getBookmark() {
        synchronized (this) {
            if (mCursor == null) {
                return 0;
            }
            return mCursor.getLong(BOOKMARKCOLIDX);
        }
    }
    
    /**
     * Returns the duration of the file in milliseconds.
     * Currently this method returns -1 for the duration of MIDI files.
     * �t�@�C���̑����Ԃ��~���b�ŕԂ�
     * �t�@�C�����I�[�v������Ă��Ȃ���΁A-1
     * MIDI�̏ꍇ�A-1�炵��
     */
    public long duration() {
        if (mPlayer != null && mPlayer.isInitialized()) {
            return mPlayer.duration();
        }
        return -1;
    }

    /**
     * Returns the current playback position in milliseconds
     * �t�@�C���̍Đ����Ԃ��~���b�ŕԂ�
     * �t�@�C�����I�[�v������Ă��Ȃ���΁A-1
     * MIDI�̏ꍇ�A-1�炵��
     */
    public long position() {
        if (mPlayer.isInitialized()) {
            return mPlayer.position();
        }
        return -1;
    }

    /**
     * Seeks to the position specified.
     * �w��̈ʒu�ɃV�[�N
     *
     * @param pos The position to seek to, in milliseconds
     */
    public long seek(long pos) {
        if (mPlayer.isInitialized()) {
            if (pos < 0) pos = 0;
            if (pos > mPlayer.duration()) pos = mPlayer.duration();
            return mPlayer.seek(pos);
        }
        return -1;
    }
    /**
     * Returns the audio session ID.
     */
    public int getAudioSessionId() {
        synchronized (this) {
            return mPlayer.getAudioSessionId();
        }
    }
    /**
     * Provides a unified interface for dealing with midi files and
     * other media files.
     * �������ꂽ�C���^�t�F�[�X���
     * midi�Ƃ��̑��̃��f�B�A�t�@�C���𕪂��čĐ��ł���H
     */
    private class MultiPlayer implements android.view.SurfaceHolder.Callback {
        private MediaPlayer mMediaPlayer = new MediaPlayer();
        private Handler mHandler;
        private boolean mIsInitialized = false;

        public MultiPlayer() {
        	// �E�F�C�N���b�N�̐ݒ�
            mMediaPlayer.setWakeMode(MediaPlaybackService.this, PowerManager.PARTIAL_WAKE_LOCK);
        }

        /**
         * �񓯊��Ńf�[�^�\�[�X��ݒ�
         * @param path
         */
        public void setDataSourceAsync(String path) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnPreparedListener(preparedlistener);
                mMediaPlayer.prepareAsync();
            } catch (IOException ex) {
                // TODO: notify the user why the file couldn't be opened
                mIsInitialized = false;
                return;
            } catch (IllegalArgumentException ex) {
                // TODO: notify the user why the file couldn't be opened
                mIsInitialized = false;
                return;
            }
            mMediaPlayer.setOnCompletionListener(listener);
            mMediaPlayer.setOnErrorListener(errorListener);
            
            mIsInitialized = true;
        }
        
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        	mMediaPlayer.setDisplay(holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            
            // Could be called after player was released in onDestroy.
            if (mMediaPlayer != null) {
            	mMediaPlayer.setDisplay(null);
            }
        }        
        /**
         * �f�[�^�\�[�X��ݒ�
         * @param path
         */
        public void setDataSource(String path, int mediaType) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setOnPreparedListener(null);
                if (path.startsWith("content://")) {
                	Uri uri = Uri.parse(path);
                	// String strFileName = uri.getLastPathSegment();// test�p
                    mMediaPlayer.setDataSource(MediaPlaybackService.this, uri);
                } else {
                    mMediaPlayer.setDataSource(path);
                }
                if( mediaType == MediaInfo.MEDIA_TYPE_VIDEO )
                {
                	SurfaceHolder holder 
                	= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getVideoViewHolder();
                	holder.addCallback(this);
	                holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	                // surfaceholder���ł��Ă���ݒ�H
	                // mMediaPlayer.setDisplay(holder);
	            }
                else
                {
                	mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                }
                mMediaPlayer.prepare();
            } catch (IOException ex) {
                // TODO: notify the user why the file couldn't be opened
                mIsInitialized = false;
                return;
            } catch (IllegalArgumentException ex) {
                // TODO: notify the user why the file couldn't be opened
                mIsInitialized = false;
                return;
            }
            mMediaPlayer.setOnCompletionListener(listener);
            mMediaPlayer.setOnErrorListener(errorListener);
            
            mIsInitialized = true;
        }
        
        public boolean isInitialized() {
            return mIsInitialized;
        }

        public void start() {
            // MusicUtils.debugLog(new Exception("MultiPlayer.start called"));
            mMediaPlayer.start();
        }

        public void stop() {
        	// Keep screen off
        	// OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);        	
        	
            mMediaPlayer.reset();
            mIsInitialized = false;
        }

        /**
         * You CANNOT use this player anymore after calling release()
         * �����[�X��͂��̃v���C���[���΂Ɏg��Ȃ�����
         */
        public void release() {
            stop();
            mMediaPlayer.release();
        }
        
        public void pause() {
            mMediaPlayer.pause();
        }
        
        public void setHandler(Handler handler) {
            mHandler = handler;
        }

        /**
         * �Đ������̃��X�i�H
         */
        MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
            @Override
			public void onCompletion(MediaPlayer mp) {
                // Acquire a temporary wakelock, since when we return from
                // this callback the MediaPlayer will release its wakelock
                // and allow the device to go to sleep.
                // This temporary wakelock is released when the RELEASE_WAKELOCK
                // message is processed, but just in case, put a timeout on it.
            	// ���f�B�A�v���C���[������wakelock�������[�X����R�[���o�b�N����߂��Ă���������A�ꎞ�I��wakelock�𓾂�
            	// �����ăf�o�C�X���X���[�v�ɓ���̂�����
            	// �ꎞ�I��wakelock��RELEASE_WAKELOCK���������ꂽ�������[�X����邪�A���̃P�[�X�̏ꍇ�A����Ƀ^�C���A�E�g��ݒ肷��H
            	// sleep����̕��A�Htimeout���Ԍナ���[�X����
                mWakeLock.acquire(30000);
                mHandler.sendEmptyMessage(TRACK_ENDED);
                mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
            }
        };

        /**
         * �����̃��X�i�H
         */
        MediaPlayer.OnPreparedListener preparedlistener = new MediaPlayer.OnPreparedListener() {
            @Override
			public void onPrepared(MediaPlayer mp) {
            	// �����A�g���Ă��Ȃ�
            	// TODO: Async�̎��͎g���ׂ��ł́H
                notifyChange(ASYNC_OPEN_COMPLETE);
            }
        };
 
        /**
         * �G���[�̃��X�i�H
         */
        MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
            @Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
                switch (what) {
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    mIsInitialized = false;
                    mMediaPlayer.release();
                    // Creating a new MediaPlayer and settings its wakemode does not
                    // require the media service, so it's OK to do this now, while the
                    // service is still being restarted
                    // mediaplayer����蒼���H
                    mMediaPlayer = new MediaPlayer(); 
                    mMediaPlayer.setWakeMode(MediaPlaybackService.this, PowerManager.PARTIAL_WAKE_LOCK);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(SERVER_DIED), 2000);
                    return true;
                default:
                	mMediaPlayer.reset();
                	mIsInitialized = false;
                	Toast.makeText(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(), 
                			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getString(R.string.cant_play_media),
                			Toast.LENGTH_LONG).show();
                	
                    Log.d("MultiPlayer", "Error: " + what + "," + extra);
                    break;
                }
                return false;
           }
        };

        public long duration() {
            return mMediaPlayer.getDuration();
        }

        public long position() {
            return mMediaPlayer.getCurrentPosition();
        }

        public long seek(long whereto) {
            mMediaPlayer.seekTo((int) whereto);
            return whereto;
        }

        public void setVolume(float vol) {
            mMediaPlayer.setVolume(vol, vol);
        }
//        public void setAudioSessionId(int sessionId) {
//            mMediaPlayer.setAudioSessionId(sessionId);
//        }
        public int getAudioSessionId() {
            return mMediaPlayer.getAudioSessionId();
        }

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			
		}        
    }

    /*
     * By making this a static class with a WeakReference to the Service, we
     * ensure that the Service can be GCd even when the system process still
     * has a remote reference to the stub.
     */
    static class ServiceStub extends IMediaPlaybackService.Stub {
        WeakReference<MediaPlaybackService> mService;
        
        ServiceStub(MediaPlaybackService service) {
            mService = new WeakReference<MediaPlaybackService>(service);
        }

        @Override
		public void openFileAsync(String path)
        {
            mService.get().openAsync(path);
        }
        @Override
		public void openFile(String path, boolean oneShot, int mediaType)
        {
            mService.get().open(path, oneShot, mediaType);
        }
        @Override
		public void open(long [] list, int [] type, int position) {
            mService.get().open(list, type, position);
        }
        @Override
		public int getQueuePosition() {
            return mService.get().getQueuePosition();
        }
        @Override
		public void setQueuePosition(int index) {
            mService.get().setQueuePosition(index);
        }
        @Override
		public boolean isPlaying() {
            return mService.get().isPlaying();
        }
        @Override
		public void stop() {
            mService.get().stop();
        }
        @Override
		public void pause() {
            mService.get().pause();
        }
        @Override
		public void play() {
            mService.get().play();
        }
        @Override
		public void prev() {
            mService.get().prev();
        }
        @Override
		public void next() {
            mService.get().next(true);
        }
        @Override
		public String getTrackName() {
            return mService.get().getTrackName();
        }
        @Override
		public String getAlbumName() {
            return mService.get().getAlbumName();
        }
        @Override
		public long getAlbumId() {
            return mService.get().getAlbumId();
        }
        @Override
		public String getArtistName() {
            return mService.get().getArtistName();
        }
        @Override
		public long getArtistId() {
            return mService.get().getArtistId();
        }
        @Override
		public void enqueue(long [] list , int [] type, int action) {
            mService.get().enqueue(list, type, action);
        }
        @Override
		public long [] getQueue() {
            return mService.get().getQueue();
        }
        @Override
		public int [] getMediaType() {
            return mService.get().getMediaType();
        }
        @Override
		public void moveQueueItem(int from, int to) {
            mService.get().moveQueueItem(from, to);
        }
        @Override
		public String getPath() {
            return mService.get().getPath();
        }
        @Override
		public long getAudioId() {
            return mService.get().getAudioId();
        }
        @Override
		public long position() {
            return mService.get().position();
        }
        @Override
		public long duration() {
            return mService.get().duration();
        }
        @Override
		public long seek(long pos) {
            return mService.get().seek(pos);
        }
        @Override
		public void setShuffleMode(int shufflemode) {
            mService.get().setShuffleMode(shufflemode);
        }
        @Override
		public int getShuffleMode() {
            return mService.get().getShuffleMode();
        }
        @Override
		public int removeTracks(int first, int last) {
            return mService.get().removeTracks(first, last);
        }
        @Override
		public int removeTrack(long id) {
            return mService.get().removeTrack(id);
        }
        @Override
		public void setRepeatMode(int repeatmode) {
            mService.get().setRepeatMode(repeatmode);
        }
        @Override
		public int getRepeatMode() {
            return mService.get().getRepeatMode();
        }
        @Override
		public int getMediaMountedCount() {
            return mService.get().getMediaMountedCount();
        }

		@Override
		public void setAudioSessionId(int sessionId) throws RemoteException {
			// TODO Auto-generated method stub
			mService.get().setAudioSessionId(sessionId);
		}

		@Override
		public int getAudioSessionId() throws RemoteException {
			// TODO Auto-generated method stub
			return mService.get().getAudioSessionId();
		}

		@Override
		public boolean isInitialized() throws RemoteException {
			return mService.get().isInitialized();
		}

		@Override
		public int getCurrentType() throws RemoteException {
			return mService.get().getCurrentType();
		}

    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        writer.println("" + mPlayListLen + " items in queue, currently at index " + mPlayPos);
        writer.println("Currently loaded:");
        writer.println(getArtistName());
        writer.println(getAlbumName());
        writer.println(getTrackName());
        writer.println(getPath());
        writer.println("playing: " + mIsSupposedToBePlaying);
        writer.println("actual: " + mPlayer.mMediaPlayer.isPlaying());
        writer.println("shuffle mode: " + mShuffleMode);
        //MusicUtils.debugDump(writer);
    }

    public void setAudioSessionId(int sessionId) {
		// TODO Auto-generated method stub
		
	}

	private final IBinder mBinder = new ServiceStub(this);
}
