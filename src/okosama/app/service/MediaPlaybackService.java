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

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
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
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
//import android.telephony.PhoneStateListener;
//import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.Vector;

import okosama.app.MusicSettingsActivity;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;

/**
 * Provides "background" audio playback capabilities, allowing the
 * user to switch between activities without stopping playback.
 * バックグラウンドでのオーディオのプレイバックを可能にし、アクティビティの変更でもプレイを止めない？
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

    public static final String PLAYSTATE_CHANGED = "com2.android.music.playstatechanged";
    public static final String META_CHANGED = "com2.android.music.metachanged";
    public static final String QUEUE_CHANGED = "com2.android.music.queuechanged";
    public static final String PLAYBACK_COMPLETE = "com2.android.music.playbackcomplete";
    public static final String ASYNC_OPEN_COMPLETE = "com2.android.music.asyncopencomplete";

    public static final String SERVICECMD = "com2.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";

    public static final String TOGGLEPAUSE_ACTION = "com2.android.music.musicservicecommand.togglepause";
    public static final String PAUSE_ACTION = "com2.android.music.musicservicecommand.pause";
    public static final String PREVIOUS_ACTION = "com2.android.music.musicservicecommand.previous";
    public static final String NEXT_ACTION = "com2.android.music.musicservicecommand.next";

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
    private long [] mPlayList = null;
    private int mPlayListLen = 0;
    private Vector<Integer> mHistory = new Vector<Integer>(MAX_HISTORY_SIZE);
    private Cursor mCursor;
    private int mPlayPos = -1;
    private static final String LOGTAG = "MediaPlaybackService";
    private final Shuffler mRand = new Shuffler();
    private int mOpenFailedCounter = 0;
    String[] mCursorCols = new String[] {
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
     * フェードインしながら表示する
     */
    private void startAndFadeIn() {
    	// 処理は、ハンドラに任せる
        mMediaplayerHandler.sendEmptyMessageDelayed(FADEIN, 10);
    }
    
    /**
     * ハンドラ
     */
    private Handler mMediaplayerHandler = new Handler() {
        float mCurrentVolume = 1.0f;
        @Override
        public void handleMessage(Message msg) {
            // MusicUtils.debugLog("mMediaplayerHandler.handleMessage " + msg.what);
            switch (msg.what) {
                case FADEIN:
                	// フェードイン処理
                    if (!isPlaying()) {
                    	// まだプレイされていない
                    	// 再生。ボリュームを0から始めて、もう一度FADEINを送信
                        mCurrentVolume = 0f;
                        mPlayer.setVolume(mCurrentVolume);
                        play();
                        mMediaplayerHandler.sendEmptyMessageDelayed(FADEIN, 10);
                    } else {
                    	// ボリュームを上げる
                        mCurrentVolume += 0.01f;
                        if (mCurrentVolume < 1.0f) {
                            mMediaplayerHandler.sendEmptyMessageDelayed(FADEIN, 10);
                        } else {
                        	// ボリュームが1になったら、FADEIN終了
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
     * ブロードキャストレシーバの設定
     */
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
    	/**
    	 * onReceive
    	 */
        @Override
        public void onReceive(Context context, Intent intent) {
        	// intentからActionを取得する
            String action = intent.getAction();
            // intentからコマンド名を取得する
            String cmd = intent.getStringExtra("command");
            // MusicUtils.debugLog("mIntentReceiver.onReceive " + action + " / " + cmd);
            if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
                // コマンドが次へコマンドだった
                next(true);
            } else if (CMDPREVIOUS.equals(cmd) || PREVIOUS_ACTION.equals(action)) {
            	// コマンドが前へコマンドだった
                prev();
            } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
            	// Log.d("test","getPause");
            	// トグルポーズコマンドだった
                if (isPlaying()) {
                	// プレイ中ならば、止める
                    pause();
                    // 一時的な？フォーカスの失いによるポーズフラグ？を落とす？
                    mPausedByTransientLossOfFocus = false;
                } else {
                	// プレイ中でなければ、プレイする
                    play();
                }
            } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
            	// ポーズコマンドだった
            	// ポーズする
                pause();
                // 一時的な？フォーカスの失いによるポーズフラグ？を落とす？
                mPausedByTransientLossOfFocus = false;
            } else if (CMDSTOP.equals(cmd)) {
            	// ストップコマンド
            	// ポーズする
                pause();
                // 一時的な？フォーカスの失いによるポーズフラグ？を落とす？
                mPausedByTransientLossOfFocus = false;
                // 位置0にシーク
                seek(0);
            } else if (MediaAppWidgetProvider.CMDAPPWIDGETUPDATE.equals(cmd)) {
            	// アプリケーションウィジェットのアップデートコマンド
                // Someone asked us to refresh a set of specific widgets, probably
                // because they were just added.
            	// 特定のwidgetをリフレッシュする。widgetのidを取得し、performUpdateに渡す？
                int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                mAppWidgetProvider.performUpdate(MediaPlaybackService.this, appWidgetIds);
            } else if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
            	
            	int iCon = intent.getIntExtra("state",0);
            	if( iCon == 1 )
            	{
            		// ヘッドホン接続
            		boolean bPlugAndPlay = false;
                    SharedPreferences prefs = getSharedPreferences(
                            MusicSettingsActivity.PREFERENCES_FILE, MODE_PRIVATE);
                    bPlugAndPlay = prefs.getBoolean(MusicSettingsActivity.KEY_ENABLE_HEADSET_PLUG_AND_PLAY, false);
            	             		
            		if( mPlayListLen <= 0 || bPlugAndPlay == false )
            		{
            			Toast.makeText(context, "headset connect", Toast.LENGTH_SHORT).show();	
            		}
            		else
            		{
	            		Toast.makeText(context, "headset connect - play", Toast.LENGTH_SHORT).show();
	                    play();
            		}
            	}
            	else if( iCon == 0 )
            	{
            		// ヘッドホン切断
            		Toast.makeText(context, "headset disconnect - pause", Toast.LENGTH_SHORT).show();
                    pause();
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
//        	オーディオフォーカスを要求するあらゆるオーディオを再生する前に、使用するストリームのオーディオフォーカスを取得する必要が有ります。
//        	これはrequestAudioFocus()をコールし、リクエストが成功したらAUDIOFOCUS_REQUEST_GRANTEDが返されます。
//        	あなたは、オーディオフォーカスを短期的か長期的に要求するかに関わらずストリームを指定する必要が有ります。短時間のみオーディオを再生すると考えられる場合（例えば音声ナビ）トランジェントフォーカスを要求します。
//        	ずっとオーディオを再生すると予想出来る場合、（例えば音楽の再生）パーマネントオーディオフォーカスを要求します。        	
        	// AudioFocusが、新しくなった？
            // AudioFocus is a new feature: focus updates are made verbose on purpose
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                	// フォーカスを失った？
                    Log.v(LOGTAG, "AudioFocus: received AUDIOFOCUS_LOSS");
                    if(isPlaying()) {
                    	// プレイ中であれば
                    	// ポーズする
                        mPausedByTransientLossOfFocus = false;
                        pause();
                    }
                    break;
//                    トランジェントフォーカスを要求しているとき、追加のオプションが有ります。ダック(後述)を有効にするかどうか、通常行儀が良いオーディオアプリはオーディオフォーカスを失うとすぐに再生をサイレントにします。
//                    ダックを許すトランジェントフォーカスを要求する事で、他のアプリにオーディオフォーカスが戻るまで低音量にすることで再生し続ける事が出来ると他のアプリに伝えます。
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                	// 一時的にフォーカスを失った？
                    Log.v(LOGTAG, "AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT");
                    if(isPlaying()) {
                    	// プレイ中であれば
                    	// トランジェントフォーカスフラグを立ててから、ポーズする
                        mPausedByTransientLossOfFocus = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                	// AudioFocusを得た
                    Log.v(LOGTAG, "AudioFocus: received AUDIOFOCUS_GAIN");
                    if(!isPlaying() && mPausedByTransientLossOfFocus) {
                    	// プレイ中ではなく、トランジェントだった場合
                    	// トランジェントフラグを落とし、再生する
                        mPausedByTransientLossOfFocus = false;
                        startAndFadeIn();
                    }
                    break;
                default:
                    Log.e(LOGTAG, "Unknown audio focus change code");
            }
        }
    };

    /**
     * コンストラクタ 何もしない
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
        // ロック画面の操作を処理するMediaButtonIntentReceiverを登録
        mAudioManager.registerMediaButtonEventReceiver(
        		new ComponentName(getPackageName(),
                MediaButtonIntentReceiver.class.getName()));
        
        // 設定を取得
        mPreferences = getSharedPreferences("Music", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
        // SDカードのIDを取得
        mCardId = StorageInfo.getCardId(this);
        
        // 外部ストレージのリスナを登録
        registerExternalStorageListener();

        // Needs to be done in this thread, since otherwise ApplicationContext.getPowerManager() crashes.
        // プレイヤーを作成
        mPlayer = new MultiPlayer();
        // プレイヤーにハンドラを設定
        mPlayer.setHandler(mMediaplayerHandler);

        // 設定の再読み込み
        reloadQueue();
        
        // フィルタをかけて、IntentReceiverを登録
        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(SERVICECMD);
        commandFilter.addAction(TOGGLEPAUSE_ACTION);
        commandFilter.addAction(PAUSE_ACTION);
        commandFilter.addAction(NEXT_ACTION);
        commandFilter.addAction(PREVIOUS_ACTION);
        commandFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mIntentReceiver, commandFilter);
        
        // 電源管理？
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        // WakeLockの取得？
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getName());
        // WakeLockは参照カウント方式でない設定にする？
        mWakeLock.setReferenceCounted(false);

        // If the service was idle, but got killed before it stopped itself, the
        // system will relaunch it. Make sure it gets stopped again in that case.
        // しばらくしたら止めるらしい
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
    }

    @Override
    public void onDestroy() {
        // Check that we're not being destroyed while something is still playing.
        if (isPlaying()) {
        	// 終了時にプレイ中であれば、エラーログ出力
            Log.e(LOGTAG, "Service being destroyed while still playing.");
        }
        // release all MediaPlayer resources, including the native player and wakelocks
        mPlayer.release();
        mPlayer = null;

        // 再生が終わったら最後にabandonAudioFocus()を確実に呼びます。
        // これはシステムへこれ以上フォーカスを要求しない事とAudioManager.OnAudioFocusChangeListener群からの解除を通知します。
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        
        // make sure there aren't any other messages coming
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mMediaplayerHandler.removeCallbacksAndMessages(null);

        if (mCursor != null) {
        	// カーソルクローズ
            mCursor.close();
            mCursor = null;
        }

        // IntentReceiverの登録解除
        unregisterReceiver(mIntentReceiver);
        if (mUnmountReceiver != null) {
        	// UnMountReceiverの登録解除
            unregisterReceiver(mUnmountReceiver);
            mUnmountReceiver = null;
        }
        // WakeLockのリリース
        mWakeLock.release();
        super.onDestroy();
    }
    
    // 16進数のchar配列？
    private final char hexdigits [] = new char [] {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
    };

    /**
     * 設定の保存
     * @param full
     */
    private void saveQueue(boolean full) {
        if (mOneShot) {
        	// OneShotなら、戻る
            return;
        }
        // 設定を編集モードで取得
        Editor ed = mPreferences.edit();
        //long start = System.currentTimeMillis();
        if (full) {
        	// フルセーブの場合
            StringBuilder q = new StringBuilder();
            
            // The current playlist is saved as a list of "reverse hexadecimal"
            // numbers, which we can generate faster than normal decimal or
            // hexadecimal numbers, which in turn allows us to save the playlist
            // more often without worrying too much about performance.
            // (saving the full state takes about 40 ms under no-load conditions
            // on the phone)
            // プレイリストの長さを取得し、その分ループ
            int len = mPlayListLen;
            for (int i = 0; i < len; i++) {
            	// プレイリストのid取得
                long n = mPlayList[i];
                if (n == 0) {
                    q.append("0;");
                } else {
                	// 0以外の場合、おそらくreverse hexadecimal形式で格納されている
                    while (n != 0) {
                        int digit = (int)(n & 0xf);
                        n >>= 4;
                        q.append(hexdigits[digit]);
                    }
                    q.append(";");
                }
            }
            //Log.i("@@@@ service", "created queue string in " + (System.currentTimeMillis() - start) + " ms");
            // queueとして、プレイリストのidの配列を;区切りにした文字列を格納
            ed.putString("queue", q.toString());
            // カードIDを格納
            ed.putInt("cardid", mCardId);
            if (mShuffleMode != SHUFFLE_NONE) {
            	// シャッフルモードが設定されていたら
            	// ヒストリーをセーブする
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
        // 現在の位置
        ed.putInt("curpos", mPlayPos);
        if (mPlayer.isInitialized()) {
        	// メディアプレイヤーが初期化済みならば
        	// その位置を保持
            ed.putLong("seekpos", mPlayer.position());
        }
        // リピートモードとシャッフルモードを保持
        ed.putInt("repeatmode", mRepeatMode);
        ed.putInt("shufflemode", mShuffleMode);
        // 記録
        ed.commit();
  
        //Log.i("@@@@ service", "saved state in " + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * キューのリロード
     */
    private void reloadQueue() {
        String q = null;
        
        // boolean newstyle = false;
        int id = mCardId;
        if (mPreferences.contains("cardid")) {
        	// card idが保存されていれば、それを取得し、new styleフラグを立てる
            // newstyle = true;
            id = mPreferences.getInt("cardid", mCardId);
        }
        if (id == mCardId) {
            // Only restore the saved playlist if the card is still
            // the same one as when the playlist was saved
        	// cardidが変わっていない
        	// queueを取得
            q = mPreferences.getString("queue", "");
        }
        int qlen = q != null ? q.length() : 0;
        if (qlen > 1) {
        	// 取得されたqueueの解析
            //Log.i("@@@@ service", "loaded queue: " + q);
            int plen = 0;
            int n = 0;
            int shift = 0;
            for (int i = 0; i < qlen; i++) {
                char c = q.charAt(i);
                if (c == ';') {
                	// 区切り文字発見
                	// プレイリストの領域確保
                    ensurePlayListCapacity(plen + 1);
                    // プレイリストを１個格納
                    mPlayList[plen] = n;
                    // 次からのループに備えて初期化
                    plen++;
                    n = 0;
                    shift = 0;
                } else {
                	// 区切り文字でない
                	// nの計算
                    if (c >= '0' && c <= '9') {
                        n += ((c - '0') << shift);
                    } else if (c >= 'a' && c <= 'f') {
                        n += ((10 + c - 'a') << shift);
                    } else {
                        // bogus playlist data
                    	// 偽物のプレイリスト？設定が壊れている？
                        plen = 0;
                        break;
                    }
                    shift += 4;
                }
            }
            mPlayListLen = plen;
            
            // curposの取得
            int pos = mPreferences.getInt("curpos", 0);
            if (pos < 0 || pos >= mPlayListLen) {
                // The saved playlist is bogus, discard it
            	// 保存されていた位置が壊れていたら、リセットする
                mPlayListLen = 0;
                return;
            }
            mPlayPos = pos;
            
            // When reloadQueue is called in response to a card-insertion,
            // we might not be able to query the media provider right away.
            // To deal with this, try querying for the current file, and if
            // that fails, wait a while and try again. If that too fails,
            // assume there is a problem and don't restore the state.
            // カードの挿入でreloadQueueが呼ばれた時、すぐにプロバイダにクエリを投げることはできないかもしれない
            // これを扱うとき、現在のファイルのクエリにトライし、それが失敗したら、しばらく待ってもう一度トライする
            // それもまた失敗したら、問題があるのを事実だとし、状態をリストアしない
            // MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            // 現在のプレイリストのidで検索。おそらく存在確認
            Cursor crsr = StorageInfo.query(this,
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String [] {"_id"}, "_id=" + mPlayList[mPlayPos] , null, null);
            if (crsr == null || crsr.getCount() == 0) {
                // wait a bit and try again
            	// 失敗したら、3秒待ってリトライ？
                SystemClock.sleep(3000);
                crsr = getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        mCursorCols, "_id=" + mPlayList[mPlayPos] , null, null);
            }
            if (crsr != null) {
                crsr.close();
            }

            // 下記の時、次の曲へスキップできない？
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
            // オープン失敗のカウンタを20で初期化？理由は分からない・・・
            mOpenFailedCounter = 20;
            // エラーでもメッセージを出力しないモード
            mQuietMode = true;
            // 現在の曲をオープン
            openCurrent();
            mQuietMode = false;
            if (!mPlayer.isInitialized()) {
                // couldn't restore the saved state
            	// おそらくオープン失敗
                mPlayListLen = 0;
                return;
            }
            
            // seek位置を取得
            long seekpos = mPreferences.getLong("seekpos", 0);
            // seek位置が0以上で、seek位置が曲の長さよりも小さければ、seek位置へ移動
            seek(seekpos >= 0 && seekpos < duration() ? seekpos : 0);
            // キューをリストアしたこと、その時のポジションをログ出力
            Log.d(LOGTAG, "restored queue, currently at position "
                    + position() + "/" + duration()
                    + " (requested " + seekpos + ")");
            // repeatモードをリストア
            int repmode = mPreferences.getInt("repeatmode", REPEAT_NONE);
            if (repmode != REPEAT_ALL && repmode != REPEAT_CURRENT) {
                repmode = REPEAT_NONE;
            }
            mRepeatMode = repmode;

            // シャッフルモードをリストア
            int shufmode = mPreferences.getInt("shufflemode", SHUFFLE_NONE);
            if (shufmode != SHUFFLE_AUTO && shufmode != SHUFFLE_NORMAL) {
                shufmode = SHUFFLE_NONE;
            }
            if (shufmode != SHUFFLE_NONE) {
            	// シャッフルモードの場合
            	// ヒストリーをリストア？
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
                            	// データが壊れている場合
                            	// ヒストリーマップをクリアして、抜ける
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
            	// オートシャッフルの場合
                if (! makeAutoShuffleList()) {
                	// オートシャッフルのリストを作成し、だめならシャッフルモードを解除する
                    shufmode = SHUFFLE_NONE;
                }
            }
            mShuffleMode = shufmode;
        }
    }
    
    /**
     * サービスがバインドされたとき
     */
    @Override
    public IBinder onBind(Intent intent) {
    	// 時間を置いて停止するハンドラのメッセージをクリア
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        // サービス利用中フラグをONに
        mServiceInUse = true;
        // バインダを返却
        // 中身は、ServiceStub(this)らしい。自動生成で作られるクラスか？
        return mBinder;
    }

    /**
     * もう一度バインドされたとき？
     */
    @Override
    public void onRebind(Intent intent) {
    	// 時間を置いて停止するハンドラのメッセージをクリア
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        // サービス利用中フラグをONに
        mServiceInUse = true;
    }

    /**
     * スタート 新しいandroidではonStartに取って代わるらしい
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceStartId = startId;
        mDelayedStopHandler.removeCallbacksAndMessages(null);

        if (intent != null) {
        	// intentが指定されていたら
        	// action取得
            String action = intent.getAction();
            // コマンド取得
            String cmd = intent.getStringExtra("command");
            // MusicUtils.debugLog("onStartCommand " + action + " / " + cmd);

            if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
            	// 次へコマンド 次へアクション
                next(true);
            } else if (CMDPREVIOUS.equals(cmd) || PREVIOUS_ACTION.equals(action)) {
            	// 前へ
                if (position() < 2000) {
                    prev();
                } else {
                    seek(0);
                    play();
                }
            } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
            	// トグルポーズ
                if (isPlaying()) {
                    pause();
                    mPausedByTransientLossOfFocus = false;
                } else {
                    play();
                }
            } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
            	// ポーズ
                pause();
                mPausedByTransientLossOfFocus = false;
            } else if (CMDSTOP.equals(cmd)) {
            	// ストップ
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
        
        // とりあえずここにだけ、Activityへのボタン更新通知処理を入れる
        Intent activityNotifyIntent = new Intent();
        activityNotifyIntent.setAction(
        		OkosamaMediaPlayerActivity.MEDIA_SERVICE_NOTIFY);
        getBaseContext().sendBroadcast(activityNotifyIntent);
        
        // サービスが強制終了した場合、サービスは再起動するonStartCommand()が再度呼び出され、Intentにnullが渡される
        return START_STICKY;
    }
    
    /**
     * バインド解除
     */
    @Override
    public boolean onUnbind(Intent intent) {
    	// サービス中フラグを落とす
        mServiceInUse = false;

        // フルセーブ
        // Take a snapshot of the current playlist
        saveQueue(true);

        if (isPlaying() || mPausedByTransientLossOfFocus) {
            // something is currently playing, or will be playing once 
            // an in-progress action requesting audio focus ends, so don't stop the service now.
        	// プレイ中やトランジェントフォーカス失い中？ならば、サービスはここでは止めず、なんらかのアクションが自動で起こるのを待つ
            return true;
        }
        
        // If there is a playlist but playback is paused, then wait a while
        // before stopping the service, so that pause/resume isn't slow.
        // Also delay stopping the service if we're transitioning between tracks.
        // プレイが止まっているのに、プレイリスト有り
        // しばらく待ってからサービスを止める
        if (mPlayListLen > 0  || mMediaplayerHandler.hasMessages(TRACK_ENDED)) {
            Message msg = mDelayedStopHandler.obtainMessage();
            mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
            return true;
        }
        
        // No active playlist, OK to stop the service right now
        // すぐ止める
        stopSelf(mServiceStartId);
        return true;
    }
    
    /**
     * 時間を置いてから音楽を止めるためのハンドラ？
     */
    private Handler mDelayedStopHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	// メッセージの処理
            // Check again to make sure nothing is playing right now
            if (isPlaying() || mPausedByTransientLossOfFocus || mServiceInUse
                    || mMediaplayerHandler.hasMessages(TRACK_ENDED)) {
            	// 再生中
            	// トランジェント中
            	// サービス利用中
            	// トラック終了待ち？
            	// の場合、状態を保持せずに終了
                return;
            }
            // save the queue again, because it might have changed
            // since the user exited the music app (because of
            // party-shuffle or because the play-position changed)
            saveQueue(true);
            // 引数は、The most recent start identifier received in onStart(Intent, int)
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
        // mNowPlayingListenerくらいでしか受信していないっぽい
        notifyChange(QUEUE_CHANGED);
        notifyChange(META_CHANGED);
    }

    /**
     * Registers an intent to listen for ACTION_MEDIA_EJECT notifications.
     * The intent will call closeExternalStorageFiles() if the external media
     * is going to be ejected, so applications can clean up any files they have open.
     * メディアが取り出された時の処理
     */
    public void registerExternalStorageListener() {
        if (mUnmountReceiver == null) {
        	// アンマウントレシーバがまだない場合
            mUnmountReceiver = new BroadcastReceiver() {
            	// アンマウントレシーバを作成する
                @Override
                public void onReceive(Context context, Intent intent) {
                	// 受信時
                    String action = intent.getAction();
                    if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                    	// メディアが取り出された場合
                    	// キューを保存
                        saveQueue(true);
                        // これがたっていたら、もう一度状態をセーブするのを防止する？
                        mOneShot = true; // This makes us not save the state again later,
                                         // which would be wrong because the song ids and
                                         // card id might not match. 
                        // 外部ストレージを閉じる
                        closeExternalStorageFiles(intent.getData().getPath());
                    } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                    	// メディアがマウントされた
                        mMediaMountedCount++; // マウントカウントをインクリメント
                        mCardId = StorageInfo.getCardId(MediaPlaybackService.this);
                        // 設定のリロード？
                        reloadQueue();
                        // 変更のブロードキャスト通知
                        notifyChange(QUEUE_CHANGED);
                        notifyChange(META_CHANGED);
                    }
                }
            };
            // イジェクトとマウントだけ受け取るように？フィルタをかけて、UnmountReceiverを登録する
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
        
    	// 基本はintentをブロードキャストで投げるだけ？
        Intent i = new Intent(what);
        // audioId
        i.putExtra("id", Long.valueOf(getAudioId()));
        // アーティスト名
        i.putExtra("artist", getArtistName());
        // アルバム名
        i.putExtra("album",getAlbumName());
        // トラック名
        i.putExtra("track", getTrackName());
        sendBroadcast(i);
        
        if (what.equals(QUEUE_CHANGED)) {
        	// キューが変わったメッセージの場合、設定をフルで再度保存
            saveQueue(true);
        } else {
        	// その他の場合、フルではない設定で設定を再度保存
            saveQueue(false);
        }
        
        // Share this notification directly with our widgets
        mAppWidgetProvider.notifyChange(this, what);
    }

    /**
     * プレイリストのキャパシティを確保する
     * @param size
     */
    private void ensurePlayListCapacity(int size) {
        if (mPlayList == null || size > mPlayList.length) {
            // reallocate at 2x requested size so we don't
            // need to grow and copy the array for every
            // insert
        	// 追加ごとに配列の拡大とコピーを防ぐために、サイズの2倍の領域を確保
            long [] newlist = new long[size * 2];
            int len = mPlayList != null ? mPlayList.length : mPlayListLen;
            for (int i = 0; i < len; i++) {
            	// 現在のプレイリストの設定値を新しいリストにコピー
                newlist[i] = mPlayList[i];
            }
            mPlayList = newlist;
        }
        // FIXME: shrink the array when the needed size is much smaller
        // than the allocated size
    }
    
    /**
     * プレイリストへの追加
     * @param list
     * @param position プレイリストの挿入位置
     */
    // insert the list of songs at the specified position in the playlist
    private void addToPlayList(long [] list, int position) {
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
            mPlayList[position + i] = list[i];
        }
        mPlayListLen += addlen;
    }
    
    /**
     * Appends a list of tracks to the current playlist.
     * If nothing is playing currently, playback will be started at
     * the first track.
     * If the action is NOW, playback will switch to the first of
     * the new tracks immediately.
     * 現在のプレイリストにトラックのリストを追加
     * 現在再生中のものがなければ、最初のトラックを再生
     * actionがNOWならば、すぐ新しいトラックの最初を再生
     * @param list The list of tracks to append.
     * @param action NOW, NEXT or LAST
     */
    public void enqueue(long [] list, int action) {
        synchronized(this) {
            if (action == NEXT && mPlayPos + 1 < mPlayListLen) {
            	// ネクスト指定で、次の位置に挿入可能な場合
            	// 現在位置+1に挿入
                addToPlayList(list, mPlayPos + 1);
                // QUEUE_CHANGED通知
                // ->キューの保存と思われる
                notifyChange(QUEUE_CHANGED);
            } else {
                // action == LAST || action == NOW || mPlayPos + 1 == mPlayListLen
            	// ネクストでない場合、最後尾に追加する
                addToPlayList(list, Integer.MAX_VALUE);
                notifyChange(QUEUE_CHANGED);
                if (action == NOW) {
                	// NOWの場合
                	// 追加したものの頭から再生
                    mPlayPos = mPlayListLen - list.length;
                    openCurrent();
                    play();
                    // META_CHANGED送信
                    // 主に、表示の更新？
                    notifyChange(META_CHANGED);
                    return;
                }
            }
            if (mPlayPos < 0) {
            	// 再生位置がマイナスになってしまっていたら、再生位置を0にして再生する？
            	// ->マイナスになるという状況がよくわからない
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
     * 現在のプレイリストを、新しく指定されたリストで置き換える（現在のプレイリストは破棄）
     * 位置0が指定されたらランダム位置から、それ以外が指定されていたらその位置から再生する
     * @param list The new list of tracks.
     */
    public void open(long [] list, int position) {
        synchronized (this) {
        	// このクラスをロック
            if (mShuffleMode == SHUFFLE_AUTO) {
            	// シャッフルモードだったら、シャッフルモードを解除する
                mShuffleMode = SHUFFLE_NORMAL;
            }
            // 過去のidとして、現在再生中の曲のidを取得
            long oldId = getAudioId();
            int listlength = list.length;
            boolean newlist = true;
            if (mPlayListLen == listlength) {
            	// 前のプレイリストの長さと新しいリストの長さが一致？
                // possible fast path: list might be the same
                newlist = false;
                for (int i = 0; i < listlength; i++) {
                    if (list[i] != mPlayList[i]) {
                    	// 単純に、全ての項目の値を入れ替える
                        newlist = true;
                        break;
                    }
                }
            }
            if (newlist) {
            	// リストを上書きする(-1指定で頭から上書き)
            	// そんなにすばらしいやり方ではないのかもしれないが、多分サイズも確保される
                addToPlayList(list, -1);
                // リストが変更されたので、保存させる
                notifyChange(QUEUE_CHANGED);
            }
            // 前の再生位置を取得？
            // int oldpos = mPlayPos;
            if (position >= 0) {
            	// 引数で位置が指定されていたら、それを再生位置にする
                mPlayPos = position;
            } else {
            	// 指定されていなかったら、ランダム値を再生位置にする
                mPlayPos = mRand.nextInt(mPlayListLen);
            }
            // 再生履歴クリア
            mHistory.clear();

            saveBookmarkIfNeeded();
            // 現在プレイリストのオープン
            openCurrent();
            if (oldId != getAudioId()) {
            	// 曲が変わったら、表示を更新
                notifyChange(META_CHANGED);
            }
        }
    }
    
    /**
     * Moves the item at index1 to index2.
     * プレイリストの項目を移動する
     * 多分、単純に入れ替えるだけ。ただし、入れ替えによって他の項目の順番と、
     * プレイ中のindexも変わるらしい
     * @param index1
     * @param index2
     */
    public void moveQueueItem(int index1, int index2) {
        synchronized (this) {
        	// このクラスをロック
            if (index1 >= mPlayListLen) {
            	// 最初のが、プレイリスト長よりも大きい
            	// indexを、最後の項目へ移動
                index1 = mPlayListLen - 1;
            }
            if (index2 >= mPlayListLen) {
            	// 1と同様
                index2 = mPlayListLen - 1;
            }
            if (index1 < index2) {
                long tmp = mPlayList[index1];
                for (int i = index1; i < index2; i++) {
                    mPlayList[i] = mPlayList[i+1];
                }
                mPlayList[index2] = tmp;
                if (mPlayPos == index1) {
                    mPlayPos = index2;
                } else if (mPlayPos >= index1 && mPlayPos <= index2) {
                        mPlayPos--;
                }
            } else if (index2 < index1) {
                long tmp = mPlayList[index1];
                for (int i = index1; i > index2; i--) {
                    mPlayList[i] = mPlayList[i-1];
                }
                mPlayList[index2] = tmp;
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
     * 現在のプレイリストを返却？
     * @return An array of integers containing the IDs of the tracks in the play list
     * プレイリストの配列(=AudioIDの配列)
     */
    public long [] getQueue() {
        synchronized (this) {
        	// クラスのロック
        	// 単純に、現在のプレイリストをコピーした配列をreturn
            int len = mPlayListLen;
            long [] list = new long[len];
            for (int i = 0; i < len; i++) {
                list[i] = mPlayList[i];
            }
            return list;
        }
    }
    
    /**
     * 多分、現在の項目をオープンする
     * stop->曲情報取得-> 
     */
    private void openCurrent() {
        synchronized (this) {
        	// おそらくこのサービスをロックする
            if (mCursor != null) {
            	// カーソルが格納されていたら、閉じる
                mCursor.close();
                mCursor = null;
            }
            if (mPlayListLen == 0) {
            	// プレイリストがなければ、終了
                return;
            }
            // 曲を止める。false=即時で？
            stop(false);

            // 現在の曲のidを取得
            String id = String.valueOf(mPlayList[mPlayPos]);
            
            // 現在の曲の情報を取得
            mCursor = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    mCursorCols, "_id=" + id , null, null);
            if (mCursor != null) {
            	// 取得できたら
            	// カーソルの最初の項目を、オープンする
                mCursor.moveToFirst();
                // オープン（MultiPlayer.setDataSourceで再生準備される)
                open(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + id, false);
                // go to bookmark if needed
                if (isPodcast()) {
                	// Podcastであれば
                	// （現在のカーソルから、Podcastフラグを見て判定している）
                	// 現在のカーソルから、ブックマークフラグを取得
                    long bookmark = getBookmark();
                    // Start playing a little bit before the bookmark,
                    // so it's easier to get back in to the narrative.
                    // ブックマークから、5s前にseek?なぜだろう？
                    // ユーザはいきなりブックマークから始まると認識できないからかも
                    seek(bookmark - 5000);
                }
            }
        }
    }

    /**
     * 非同期でオープン？
     * @param path
     */
    public void openAsync(String path) {
        synchronized (this) {
            if (path == null) {
                return;
            }
            
            // リピートモードをなしに？
            mRepeatMode = REPEAT_NONE;
            // プレイリストを1件に？
            ensurePlayListCapacity(1);
            mPlayListLen = 1;
            mPlayPos = -1;
            
            mFileToPlay = path;
            // カーソルをNullに？
            mCursor = null;
            // 非同期で再生準備
            mPlayer.setDataSourceAsync(mFileToPlay);
            // oneshot?
            mOneShot = true;
        }
    }
    
    /**
     * Opens the specified file and readies it for playback.
     * 特定のファイルをオープンし、その再生を準備する
     * @param path The full path of the file to be opened.
     * @param oneshot when set to true, playback will stop after this file completes, instead
     * of moving on to the next track in the list 
     */
    public void open(String path, boolean oneshot) {
        synchronized (this) {
        	// おそらくこのサービスをロックする
            if (path == null) {
            	// パスがnullならば終了
                return;
            }
            
            if (oneshot) {
            	// oneshotならば、リピートしない設定にし、プレイリストを1個にする
                mRepeatMode = REPEAT_NONE;
                ensurePlayListCapacity(1);
                mPlayListLen = 1;
                mPlayPos = -1;
            }
            
            // if mCursor is null, try to associate path with a database cursor
            if (mCursor == null) {
            	// カーソルがnullならば
                ContentResolver resolver = getContentResolver();
                Uri uri;
                String where;
                String selectionArgs[];
                if (path.startsWith("content://media/")) {
                	// パスがcontent://mediaで始まっていたら
                	// パスをuriに変換
                    uri = Uri.parse(path);
                    // クエリの条件をクリア
                    where = null;
                    selectionArgs = null;
                } else {
                   // そうでない場合も、パスをuriに変換する？
                   // TODO:getContentUriForPathを調査
                   uri = MediaStore.Audio.Media.getContentUriForPath(path);
                   // この場合、条件を設定
                   where = MediaColumns.DATA + "=?";
                   selectionArgs = new String[] { path };
                }
                
                try {
                	// クエリの発行
                    mCursor = resolver.query(uri, mCursorCols, where, selectionArgs, null);
                    if  (mCursor != null) {
                    	// 結果が取得できた
                        if (mCursor.getCount() == 0) {
                        	// 結果が0
                        	// カーソルクローズ
                            mCursor.close();
                            mCursor = null;
                        } else {
                        	// 結果が１件以上
                        	// 1件目の結果のIDCOLIDXをプレイリストとして保持
                            mCursor.moveToNext();
                            ensurePlayListCapacity(1);
                            mPlayListLen = 1;
                            mPlayList[0] = mCursor.getLong(IDCOLIDX);
                            mPlayPos = 0;
                        }
                    }
                } catch (UnsupportedOperationException ex) {
                }
            }
            // プレイヤーのデータソースとして、指定されたファイルを設定する
            mFileToPlay = path;
            // 再生準備される
            mPlayer.setDataSource(mFileToPlay);
            mOneShot = oneshot;
            if (! mPlayer.isInitialized()) {
            	// まだプレイヤーが初期化されていない
            	// もしかすると、これはつまりsetDataSourceでエラーになったことを表すのかもしれない
            	// とりあえず、止める？
                stop(true);
                if (mOpenFailedCounter++ < 10 &&  mPlayListLen > 1) {
                	// エラーカウンタをインクリメント
                    // beware: this ends up being recursive because next() calls open() again.
                	// エラーが10件以下で、プレイリストが2件以上あれば、次の曲へ？
                    next(false);
                }
                if (! mPlayer.isInitialized() && mOpenFailedCounter != 0) {
                    // need to make sure we only shows this once
                	// プレイヤーが初期化されていなくて、オープン失敗があれば
                	// オープン失敗のカウンタクリア
                    mOpenFailedCounter = 0;
                    if (!mQuietMode) {
                    	// 出していい場合は、ユーザにエラーメッセージ出力？
                        Toast.makeText(this, R.string.playback_failed, Toast.LENGTH_SHORT).show();
                    }
                    Log.d(LOGTAG, "Failed to open file for playback");
                }
            } else {
            	// プレイヤが初期化されている場合
            	// =データソースオープン成功？
                mOpenFailedCounter = 0;
            }
        }
    }

    /**
     * Starts playback of a previously opened file.
     * ファイルオープンより前もってプレイバックを開始する？
     */
    public void play() {
        mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        mAudioManager.registerMediaButtonEventReceiver(new ComponentName(this.getPackageName(),
                MediaButtonIntentReceiver.class.getName()));

        if (mPlayer.isInitialized()) {
        	// 準備OK中であれば
            // if we are at the end of the song, go to the next song first
        	// 曲が終わったら、次の曲の最初に移動する
            long duration = mPlayer.duration();
            // 現在曲のリピートではなく、再生時間が20s以上で、positionが再生時間-20sより大きい
            if (mRepeatMode != REPEAT_CURRENT && duration > 2000 &&
                mPlayer.position() >= duration - 2000) {
            	// 次の曲へ
                next(true);
            }

            // 準備されているファイルを再生？
            mPlayer.start();
            // 別プロセス上で表示可能となる階層的なViewを記述するクラス
            // ステータスバーのレイアウトを設定？
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.statusbar);
            // イメージを設定？
            views.setImageViewResource(R.id.icon, R.drawable.stat_notify_musicplayer);
            String ticket;
            if (getAudioId() < 0) {
                // streaming
            	// ストリーミング
                views.setTextViewText(R.id.trackname, getPath());
                views.setTextViewText(R.id.artistalbum, null);
                ticket = getPath();
            } else {
            	// 普通のメディア？
                String artist = getArtistName();
                // トラック名をビューに設定
                views.setTextViewText(R.id.trackname, getTrackName());
                if (artist == null || artist.equals(MediaStore.UNKNOWN_STRING)) {
                	// アーティストが取得できなかったら、アーティストをUnknownに設定
                    artist = getString(R.string.unknown_artist_name);
                }
                // アルバムを取得
                String album = getAlbumName();
                if (album == null || album.equals(MediaStore.UNKNOWN_STRING)) {
                	// アルバム名が取得できなければ、アルバム名をUnknownに取得
                    album = getString(R.string.unknown_album_name);
                }
                
                // アルバム名とアーティスト名をビューに設定
                views.setTextViewText(R.id.artistalbum,
                        getString(R.string.notification_artist_album, artist, album)
                        );
                ticket = getTrackName() + "-" + artist; //+ "[" + album + "]" + " - " + artist;
            }
            
            // Notificationクラスの作成
            Notification status = new Notification();
            // 
            status.tickerText = ticket;
            // Notificationクラスに、ビューを設定
            status.contentView = views;
            // Notificationを常駐させる？
            //status.flags |= Notification.FLAG_ONGOING_EVENT;
//            status.ledARGB = 0xffffff00;
//            status.ledOnMS = 300;
//            status.ledOffMS = 1000;
            // status.flags |= Notification.DEFAULT_LIGHTS;
                        
            // Notificationのアイコンを設定
            status.icon = R.drawable.stat_notify_musicplayer;
            SharedPreferences prefs = getSharedPreferences(
                    MusicSettingsActivity.PREFERENCES_FILE, MODE_PRIVATE);            
            boolean bVib = prefs.getBoolean(MusicSettingsActivity.KEY_ENABLE_MEDIA_CHANGE_VIBRATE, false);

            if( bVib )
            {
	            // バイブすれば再生中であることに気づくので
                status.flags |= Notification.DEFAULT_VIBRATE;
                status.vibrate = new long[]{250,50,750,10};
                Log.d("MediaServ","vib");
            }
            // クリック時に発行されるインテント？だろうか？
            // タイミングを指定して発行できるインテント
            // 今回は多分、Notificationがクリックされたとき
            Intent clickIntent = new Intent();
            clickIntent.setClassName(
            		"okosama.app", "okosama.app.OkosamaMediaPlayerActivity");
            status.contentIntent = PendingIntent.getActivity(this, 0,
            		// TODO: Activity変更
            		clickIntent
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
            // statusbarにNotification表示
            startForeground(PLAYBACKSERVICE_STATUS, status);
            if (!mIsSupposedToBePlaying) {
            	// これはおそらくプレイ中フラグとして利用されている
            	// まだそれがたっていなければ
            	// 利用中フラグを立てる
                mIsSupposedToBePlaying = true;
                // 再生状態の変更を通知する
                notifyChange(PLAYSTATE_CHANGED);
            }

        } else if (mPlayListLen <= 0) {
        	// 何も再生されていないときにプレイボタンが押されると、シャッフルモードにする？
            // This is mostly so that if you press 'play' on a bluetooth headset
            // without every having played anything before, it will still play
            // something.
            if (!mQuietMode) {
            	// 出していい場合は、ユーザにエラーメッセージ出力？
            	// TODO: resouce利用
                Toast.makeText(this, "auto shuffleで再生します！", Toast.LENGTH_SHORT).show();
            }
        	
            setShuffleMode(SHUFFLE_AUTO);
        }
    }
    
    private void stop(boolean remove_status_icon) {
    	Log.w("stop","stop come!");
        if (mPlayer.isInitialized()) {
        	// プレイヤー停止
            mPlayer.stop();
        }
        mFileToPlay = null;
        if (mCursor != null) {
        	// カーソルクローズ
            mCursor.close();
            mCursor = null;
        }
        if (remove_status_icon) {
        	// 少しおいてから止める？なぜだろう？
            gotoIdleState();
        } else {
        	// 即停止？
            stopForeground(false);
        }
        if (remove_status_icon) {
        	// プレイ中フラグを落とす？
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
        	// このクラスをロック
            if (isPlaying()) {
            	// 再生中であれば
            	// プレイヤーをポーズ
                mPlayer.pause();
                // 少し間を置いてストップ
                gotoIdleState();
                // 再生中フラグを落とす
                mIsSupposedToBePlaying = false;
                // 再生状態の変更通知
                notifyChange(PLAYSTATE_CHANGED);
                // Podcastの場合、ブックマークをDBに保存
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

    // ここまで読んだ
    /*
      Desired behavior for prev/next/shuffle:
      前/次/シャッフルの時の振る舞い

      - NEXT will move to the next track in the list when not shuffling, and to
        a track randomly picked from the not-yet-played tracks when shuffling.
        If all tracks have already been played, pick from the full set, but
        avoid picking the previously played track if possible.
        シャッフルでない場合、次へはリストの次のトラックへ
        シャッフルの場合、ランダムにまだプレイされていないものを選んでそのトラックへ
      - when shuffling, PREV will go to the previously played track. Hitting PREV
        again will go to the track played before that, etc. When the start of the
        history has been reached, PREV is a no-op.
        When not shuffling, PREV will go to the sequentially previous track (the
        difference with the shuffle-case is mainly that when not shuffling, the
        user can back up to tracks that are not in the history).
		シャッフル中のとき、PREVは再生されたトラックの前のやつへ。
		もう一度PREVをヒットするとその前へ。履歴の最初の項目の場合、PREVは何もしない
		シャッフル中でないとき、PREVはシーケンスの前の曲へ。履歴に内局にも戻ることができる？
        Example:
        When playing an album with 10 tracks from the start, and enabling shuffle
        while playing track 5, the remaining tracks (6-10) will be shuffled, e.g.
        the final play order might be 1-2-3-4-5-8-10-6-9-7.
        When hitting 'prev' 8 times while playing track 7 in this example, the
        user will go to tracks 9-6-10-8-5-4-3-2. If the user then hits 'next',
        a random track will be picked again. If at any time user disables shuffling
        the next/previous track will be picked in sequential order again.
        例：
        10トラックのアルバムが再生中で、５トラック目でシャッフルにされた場合、残りのトラックがシャッフルされる
        その後、nextはランダムだが、prevは履歴を再生する
     */

    public void prev() {
        synchronized (this) {
        	// ロック
            if (mOneShot) {
            	// OneShot
                // we were playing a specific file not part of a playlist, so there is no 'previous'
            	// プレイリスト内にない特定のファイルを再生中ならば、prevはない。
            	// 曲の頭に戻るだけ
                seek(0);
                play();
                return;
            }
            if (mShuffleMode == SHUFFLE_NORMAL) {
            	// ノーマルシャッフルモード
                // go to previously-played track and remove it from the history
            	// 前の履歴のトラックを再生し、それを履歴から消す
                int histsize = mHistory.size();
                if (histsize == 0) {
                    // prev is a no-op
                    return;
                }
                Integer pos = mHistory.remove(histsize - 1);
                mPlayPos = pos.intValue();
            } else {
            	// シャッフルでないなら、シーケンスを戻る
                if (mPlayPos > 0) {
                    mPlayPos--;
                } else {
                    mPlayPos = mPlayListLen - 1;
                }
            }
            // ブックマーク保存、停止、オープン、再生、画面更新
            saveBookmarkIfNeeded();
            stop(false);
            openCurrent();
            play();
            notifyChange(META_CHANGED);
        }
    }

    /**
     * 次へ
     * @param force　trueならば、強制的？
     */
    public void next(boolean force) {
        synchronized (this) {
        	// ロック
            if (mOneShot) {
                // we were playing a specific file not part of a playlist, so there is no 'next'
            	// プレイリストの曲を再生している訳ではない場合
            	// 曲の頭に戻る
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
            // 履歴に現在の曲を追加
            if (mPlayPos >= 0) {
                mHistory.add(Integer.valueOf(mPlayPos));
            }
            if (mHistory.size() > MAX_HISTORY_SIZE) {
                mHistory.removeElementAt(0);
            }

            if (mShuffleMode == SHUFFLE_NORMAL) {
                // Pick random next track from the not-yet-played ones
                // TODO: make it work right after adding/removing items in the queue.
            	// シャッフルモードの場合
            	// まだ再生されていない曲の中からランダム再生
                int numTracks = mPlayListLen;
                int[] tracks = new int[numTracks];
                for (int i=0;i < numTracks; i++) {
                    tracks[i] = i;
                }

                // まだ再生されていない曲の取得
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
                // numUnplayed:まだプレイされていない曲の数
                // tracks:まだプレイされていない曲のインデックス
                if (numUnplayed <=0) {
                    // everything's already been played
                	// 全てが既に再生されている
                    if (mRepeatMode == REPEAT_ALL || force) {
                        //pick from full set
                    	// 全曲リピートの場合、最初から再生する
                    	// 強制の場合も、同様
                        numUnplayed = numTracks;
                        for (int i=0;i < numTracks; i++) {
                            tracks[i] = i;
                        }
                    } else {
                    	// 再生を止める
                        // all done
                        gotoIdleState();
                        if (mIsSupposedToBePlaying) {
                            mIsSupposedToBePlaying = false;
                            notifyChange(PLAYSTATE_CHANGED);
                        }
                        return;
                    }
                }
                // ランダム位置の生成
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
            	// オートシャッフルする
            	// この関数では、対象となる曲をランダムに抜き出したりして調整するのではないかと思われる。
                doAutoShuffleUpdate();
                mPlayPos++;
            } else {
            	// シャッフルモードじゃない
                if (mPlayPos >= mPlayListLen - 1) {
                    // we're at the end of the list
                	// リストの最後に到達
                    if (mRepeatMode == REPEAT_NONE && !force) {
                        // all done
                    	// 止める
                        gotoIdleState();
                        notifyChange(PLAYBACK_COMPLETE);
                        mIsSupposedToBePlaying = false;
                        return;
                    } else if (mRepeatMode == REPEAT_ALL || force) {
                    	// プレイ位置を最初に戻す
                        mPlayPos = 0;
                    }
                } else {
                    mPlayPos++;
                }
            }
            // ブックマーク保存、即時停止、オープン、再生、画面更新
            saveBookmarkIfNeeded();
            stop(false);
            openCurrent();
            play();
            notifyChange(META_CHANGED);
        }
    }
    
    /**
     * 時間を置いて、再生を止める
     */
    private void gotoIdleState() {
    	// DelayStopHandlerのメッセージを削除？
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        // IDLE_DELAY後に終了
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
        // Remove this service from foreground state, allowing it to be killed if more memory is needed.
        // the notification previously provided to startForeground(int, Notification) will be removed.
        stopForeground(true);
    }
    
    /**
     * 現在再生中の時間をブックマーク時間としてデータベースに保存するらしい
     * ただし、Podcastに限る
     */
    private void saveBookmarkIfNeeded() {
        try {
            if (isPodcast()) {
            	// 再生中のメディアがpodcastの場合のみ処理する？
                long pos = position();	// メディアプレイヤーから得た再生時間
                long bookmark = getBookmark(); // ブックマークされている時間？(=DBのカラム)
                long duration = duration(); // 曲の時間
                if ((pos < bookmark && (pos + 10000) > bookmark) ||
                        (pos > bookmark && (pos - 10000) < bookmark)) {
                    // The existing bookmark is close to the current
                    // position, so don't update it.
                    return;
                }
                if (pos < 15000 || (pos + 10000) > duration) {
                	// 再生位置が15sより小さいか、曲の長さを超えている場合、ブックマークしない(=ブックマーク時間に0を設定)
                    // if we're near the start or end, clear the bookmark
                    pos = 0;
                }
                
                // write 'pos' to the bookmark field
                // ブックマーク時間を上書きする
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
    // 現在再生中の項目より後の項目が少なくとも5個あり、10個は前にない
    private void doAutoShuffleUpdate() {
        boolean notify = false;
        // remove old entries
        if (mPlayPos > 10) {
        	// 10個目以降ならば、プレイリストが9個になるようにその前の項目を全て削除？
            removeTracks(0, mPlayPos - 9);
            notify = true;
        }
        // add new entries if needed
        // 必要であれば、新しいエントリを追加
        int to_add = 7 - (mPlayListLen - (mPlayPos < 0 ? -1 : mPlayPos));
        for (int i = 0; i < to_add; i++) {
            // pick something at random from the list
        	// リストからランダムにどれかを取り出してプレイリストに格納する
            int idx = mRand.nextInt(mAutoShuffleList.length);
            long which = mAutoShuffleList[idx];
            ensurePlayListCapacity(mPlayListLen + 1);
            mPlayList[mPlayListLen++] = which;
            notify = true;
        }
        if (notify) {
            notifyChange(QUEUE_CHANGED);
        }
    }

    // A simple variation of Random that makes sure that the
    // value it returns is not equal to the value it returned
    // previously, unless the interval is 1.
    // インターバル1でなければそれが戻す値が前に戻された値と等しくならないようにする単純なランダムのバリエーション
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
     * オートシャッフルのリスト作成？
     * @return
     */
    private boolean makeAutoShuffleList() {
        ContentResolver res = getContentResolver();
        Cursor c = null;
        try {
        	// 音楽のIDを全て取得
            c = res.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[] {BaseColumns._ID}, AudioColumns.IS_MUSIC + "=1",
                    null, null);
            if (c == null || c.getCount() == 0) {
                return false;
            }
            int len = c.getCount();
            long [] list = new long[len];
            for (int i = 0; i < len; i++) {
            	// 全部のIDをリストに格納
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
     * プレイリストのトラックのある範囲を消す。
     * 範囲内のファイルが現在再生中ならば、範囲の後の最初のファイルへ移動する
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
        	// 暮らすロック
            if (last < first) return 0;
            if (first < 0) first = 0;
            if (last >= mPlayListLen) last = mPlayListLen - 1;

            boolean gotonext = false;
            if (first <= mPlayPos && mPlayPos <= last) {
            	// 消される範囲内に、再生中の項目がある
                mPlayPos = first;
                gotonext = true;
            } else if (mPlayPos > last) {
                mPlayPos -= (last - first + 1);
            }
            int num = mPlayListLen - last - 1;
            // 移動？
            for (int i = 0; i < num; i++) {
                mPlayList[first + i] = mPlayList[last + 1 + i];
            }
            // 長さを削る？
            mPlayListLen -= last - first + 1;
            
            if (gotonext) {
            	// 次へ
                if (mPlayListLen == 0) {
                	Log.w("playlist len = 0", "stop come!");
                	// プレイリストの長さが0
                	// 停止
                    stop(true);
                    mPlayPos = -1;
                } else {
                	// プレイリストの長さがある
                    if (mPlayPos >= mPlayListLen) {
                        mPlayPos = 0;
                    }
                    // 止めて、オープンして、現在再生中ならば、再生する
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
     * プレイリストからidを与えられたトラックの全てのインスタンスを削除する
     * @param id The id to be removed
     * @return how many instances of the track were removed
     */
    public int removeTrack(long id) {
        int numremoved = 0;
        synchronized (this) {
            for (int i = 0; i < mPlayListLen; i++) {
            	// 該当idの全てのトラックを削除
                if (mPlayList[i] == id) {
                    numremoved += removeTracksInternal(i, i);
                    i--;
                }
            }
        }
        if (numremoved > 0) {
        	// 変更されたら、キュー変更通知をだして、キューを保存させる
            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }
    /**
     * シャッフルモードを設定する
     * @param shufflemode
     */
    public void setShuffleMode(int shufflemode) {
        synchronized(this) {
        	// クラスをロックする
            if (mShuffleMode == shufflemode && mPlayListLen > 0) {
            	// シャッフルモードが変化なしならば、戻る
                return;
            }
            // 変更有り
            // 新しいシャッフルモードを設定
            mShuffleMode = shufflemode;
            if (mShuffleMode == SHUFFLE_AUTO) {
            	// オートシャッフルならば、シャッフルリストを作成
                if (makeAutoShuffleList()) {
                	// リスト作成
                    mPlayListLen = 0;
                    // 作成されたリストの件数をしぼっている？そういう風に見えるが・・・誤解だろうか？
                    doAutoShuffleUpdate();
                    // 開く
                    mPlayPos = 0;
                    openCurrent();
                    // 再生
                    play();
                    // 画面更新
                    notifyChange(META_CHANGED);
                    return;
                } else {
                    // failed to build a list of files to shuffle
                	// シャッフル用リスト作成失敗
                	// シャッフルなし
                    mShuffleMode = SHUFFLE_NONE;
                }
            }
            // キューを保存？
            // フルではないのでプレイリストは保存されないはずだが、履歴は保存されると思われる
            // それでいいんだろうか？
            saveQueue(false);
        }
    }
    public int getShuffleMode() {
        return mShuffleMode;
    }
    
    public void setRepeatMode(int repeatmode) {
        synchronized(this) {
        	// クラスをロック
        	// リピートモードを設定
            mRepeatMode = repeatmode;
            // キューを保存？
            // フルではないのでプレイリストは保存されないはずだが、履歴は保存されると思われる
            // それでいいんだろうか？
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
     * 現在再生中のファイルを返却。現在再生中でなければ、null
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
            if (mPlayPos >= 0 && mPlayer.isInitialized()) {
            	// 再生位置が有効で、再生中であれば、再生されている曲のIDを返却
                return mPlayList[mPlayPos];
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
     * 指定されたキューの位置の曲を再生
     * @param pos The position in the queue of the track that will be played.
     */
    public void setQueuePosition(int pos) {
        synchronized(this) {
        	// 即時ストップ
            stop(false);
            mPlayPos = pos;
            // オープン
            openCurrent();
            // 再生
            play();
            // 画面更新
            notifyChange(META_CHANGED);
            if (mShuffleMode == SHUFFLE_AUTO) {
            	// オートシャッフル
            	// オートシャッフルの項目を更新？
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
            if (mCursor == null) {
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
            if (mCursor == null) {
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
            if (mCursor == null) {
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
     * ファイルの総時間をミリ秒で返す
     * ファイルがオープンされていなければ、-1
     * MIDIの場合、-1らしい
     */
    public long duration() {
        if (mPlayer.isInitialized()) {
            return mPlayer.duration();
        }
        return -1;
    }

    /**
     * Returns the current playback position in milliseconds
     * ファイルの再生時間をミリ秒で返す
     * ファイルがオープンされていなければ、-1
     * MIDIの場合、-1らしい
     */
    public long position() {
        if (mPlayer.isInitialized()) {
            return mPlayer.position();
        }
        return -1;
    }

    /**
     * Seeks to the position specified.
     * 指定の位置にシーク
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
     * 統合されたインタフェースを提供
     * midiとその他のメディアファイルを分けて再生できる？
     */
    private class MultiPlayer {
        private MediaPlayer mMediaPlayer = new MediaPlayer();
        private Handler mHandler;
        private boolean mIsInitialized = false;

        public MultiPlayer() {
        	// ウェイクロックの設定
            mMediaPlayer.setWakeMode(MediaPlaybackService.this, PowerManager.PARTIAL_WAKE_LOCK);
        }

        /**
         * 非同期でデータソースを設定
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
        
        /**
         * データソースを設定
         * @param path
         */
        public void setDataSource(String path) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setOnPreparedListener(null);
                if (path.startsWith("content://")) {
                    mMediaPlayer.setDataSource(MediaPlaybackService.this, Uri.parse(path));
                } else {
                    mMediaPlayer.setDataSource(path);
                }
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
            mMediaPlayer.reset();
            mIsInitialized = false;
        }

        /**
         * You CANNOT use this player anymore after calling release()
         * リリース後はこのプレイヤーを絶対に使わないこと
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
         * 再生完了のリスナ？
         */
        MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
            @Override
			public void onCompletion(MediaPlayer mp) {
                // Acquire a temporary wakelock, since when we return from
                // this callback the MediaPlayer will release its wakelock
                // and allow the device to go to sleep.
                // This temporary wakelock is released when the RELEASE_WAKELOCK
                // message is processed, but just in case, put a timeout on it.
            	// メディアプレイヤーがそのwakelockをリリースするコールバックから戻ってきた時から、一時的なwakelockを得る
            	// そしてデバイスがスリープに入るのを許す
            	// 一時的なwakelockはRELEASE_WAKELOCKが処理された時リリースされるが、このケースの場合、それにタイムアウトを設定する？
            	// sleepからの復帰？timeout時間後リリースする
                mWakeLock.acquire(30000);
                mHandler.sendEmptyMessage(TRACK_ENDED);
                mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
            }
        };

        /**
         * 準備のリスナ？
         */
        MediaPlayer.OnPreparedListener preparedlistener = new MediaPlayer.OnPreparedListener() {
            @Override
			public void onPrepared(MediaPlayer mp) {
            	// 多分、使われていない
            	// TODO: Asyncの時は使うべきでは？
                notifyChange(ASYNC_OPEN_COMPLETE);
            }
        };
 
        /**
         * エラーのリスナ？
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
                    // mediaplayerを作り直す？
                    mMediaPlayer = new MediaPlayer(); 
                    mMediaPlayer.setWakeMode(MediaPlaybackService.this, PowerManager.PARTIAL_WAKE_LOCK);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(SERVER_DIED), 2000);
                    return true;
                default:
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
        public void setAudioSessionId(int sessionId) {
            mMediaPlayer.setAudioSessionId(sessionId);
        }
        public int getAudioSessionId() {
            return mMediaPlayer.getAudioSessionId();
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
		public void openFile(String path, boolean oneShot)
        {
            mService.get().open(path, oneShot);
        }
        @Override
		public void open(long [] list, int position) {
            mService.get().open(list, position);
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
		public void enqueue(long [] list , int action) {
            mService.get().enqueue(list, action);
        }
        @Override
		public long [] getQueue() {
            return mService.get().getQueue();
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
