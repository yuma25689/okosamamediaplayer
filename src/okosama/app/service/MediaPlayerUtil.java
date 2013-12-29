package okosama.app.service;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.TimerAlertDialog;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.storage.Database;
import okosama.app.tab.TabPage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
// import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;


public class MediaPlayerUtil {

	// サービス
    public static IMediaPlaybackService sService = null;
    // サービストークン
    // おそらく、サービスの内部的なIDとして利用
    public static class ServiceToken {
        ContextWrapper mWrappedContext;
        ServiceToken(ContextWrapper context) {
            mWrappedContext = context;
        }
    }
    // 起動中のサービス格納用？
    private static HashMap<Context, ServiceBinder> sConnectionMap = new HashMap<Context, ServiceBinder>();
    
    public static boolean hasServiceConnection(Context ctx)
    {
    	return sConnectionMap.containsKey(ctx);
    }
    public static int getServiceConnectionCount()
    {
    	return sConnectionMap.size();
    }    
    /**
     * サービスバインダ
     * @author 25689
     *
     */
    private static class ServiceBinder implements ServiceConnection {
    	// サービスコネクションをインプリメント
    	// サービスコネクションをメンバとして保持
        ServiceConnection mCallback;
        // コンストラクタでサービスコネクションを設定
        ServiceBinder(ServiceConnection callback) {
            mCallback = callback;
        }
        
        /**
         * サービスが接続されたときコールされる？
         */
        @Override
		public void onServiceConnected(ComponentName className, android.os.IBinder service) {
        	// サービスを取得
            sService = IMediaPlaybackService.Stub.asInterface(service);
            initAlbumArtCache();
            if (mCallback != null) {
            	// コールバックがnullでなければ、コールバックもコールする
                mCallback.onServiceConnected(className, service);
            }
        }
        
        /**
         * サービスが切断されたときにコールされる？
         */
        @Override
		public void onServiceDisconnected(ComponentName className) {
            if (mCallback != null) {
            	// コールバックがnullでなければ、コールバックをコールする
               mCallback.onServiceDisconnected(className);
            }
            // 内部に格納されたサービスをクリアする
            sService = null;
        }
    }    
    /**
     * サービスとバインドする
     * @param context
     * @return サービストークン
     */
    public static ServiceToken bindToService(Activity context) {
        return bindToService(context, null);
    }

    /**
     * サービスとバインドする
     * @param context
     * @param callback
     * @return サービストークン
     */
    public static ServiceToken bindToService(
    		Activity context, ServiceConnection callback) {
    	// 指定されたアクティビティから、実際のアクティビティを取得する
    	// 一旦親アクティビティの取得を試みて、それで駄目なら指定されたアクティビティを利用する
        Activity realActivity = context;//.getParent();
//        if (realActivity == null) {
//            realActivity = context;
//        }
        // アクティビティのコンテキストラッパーを取得
        ContextWrapper cw = new ContextWrapper(realActivity);
        // サービスを起動する
        cw.startService(new Intent(cw, MediaPlaybackService.class));
        // サービスと、指定されたサービスコネクションをバインド
        ServiceBinder sb = new ServiceBinder(callback);
        // サービスをサービスバインダと紐つける？
        if (cw.bindService((new Intent()).setClass(cw, MediaPlaybackService.class), sb, 0)) {
        	// 成功したら、マップに格納しておく
            sConnectionMap.put(cw, sb);
            // サービスのIDとなるトークンを返却する
            return new ServiceToken(cw);
        }
        // 失敗したら、ログ出力してnullを返却する
        Log.e("Music", "Failed to bind to service");
        return null;
    }
    
    /**
     * バインドを解除する
     * @param token
     */
    public static void unbindFromService(ServiceToken token) {
    	// Toast.makeText(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(), "unbind", Toast.LENGTH_LONG).show();
            	
        if (token == null) {
        	// トークンがnullであれば、ログをはいて終了
            Log.e("MediaPlayer", "Trying to unbind with null token");
            return;
        }
        // トークンからコンテキストラッパーを取得
        ContextWrapper cw = token.mWrappedContext;
        // 指定されたコンテキストラッパーのエントリを削除し、そのサービスバインダを取得
        ServiceBinder sb = sConnectionMap.remove(cw);
        if (sb == null) {
        	// サービスバインダが取得できなかったら、エラーログを出力し、処理を抜ける
            Log.e("MediaPlayer", "Trying to unbind for unknown Context");
            return;
        }
        // サービスバインダから、サービスのバインドを解除する
        cw.unbindService(sb);
        token = null;
        if (sConnectionMap.isEmpty()) {
            // サービスが１つもなくなったら、サービスをクリアする
            sService = null;
        }
    }

    
    ///////// ここからしばらくアルバムアート用 ///////////
    // A really simple BitmapDrawable-like class, that doesn't do
    // scaling, dithering or filtering.
    private static class FastBitmapDrawable extends Drawable {
        private Bitmap mBitmap;
        public FastBitmapDrawable(Bitmap b) {
            mBitmap = b;
        }
        @Override
        public void draw(Canvas canvas) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
        @Override
        public void setAlpha(int alpha) {
        }
        @Override
        public void setColorFilter(ColorFilter cf) {
        }
    }    
    // アルバムアートのキャッシュのマップ
    public static final HashMap<Long, Drawable> 
    	sArtCache = new HashMap<Long, Drawable>();    
    // アルバムアートのキャッシュのID
    private static int sArtCacheId = -1; 
    // private static int sArtId = -2;
    // private static Bitmap mCachedBit = null;
    private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    
    /**
     * アルバムアートのキャッシュを初期化
     */
    public static void initAlbumArtCache() {
        try {
        	// メディアのマウントされた数をidとして取得、保持？
            int id = sService.getMediaMountedCount();
            if (id != sArtCacheId) {
                clearAlbumArtCache();
                sArtCacheId = id; 
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * アルバムアートのキャッシュをクリア
     */
    public static void clearAlbumArtCache() {
        synchronized(sArtCache) {
            sArtCache.clear();
        }
    }
    public static Drawable getCachedArtwork(Context context, long artIndex, BitmapDrawable defaultArtwork) {
        Drawable d = null;
        synchronized(sArtCache) {
            d = sArtCache.get(artIndex);
        }
        if (d == null) {
            d = defaultArtwork;
            final Bitmap icon = defaultArtwork.getBitmap();
            int w = icon.getWidth();
            int h = icon.getHeight();
            Bitmap b = MediaPlayerUtil.getArtworkQuick(context, artIndex, w, h);
            if (b != null) {
                d = new FastBitmapDrawable(b);
                synchronized(sArtCache) {
                    // the cache may have changed since we checked
                    Drawable value = sArtCache.get(artIndex);
                    if (value == null) {
                        sArtCache.put(artIndex, d);
                    } else {
                        d = value;
                    }
                }
            }
        }
        return d;
    }

    // Get album art for specified album. This method will not try to
    // fall back to getting artwork directly from the file, nor will
    // it attempt to repair the database.
    private static Bitmap getArtworkQuick(Context context, long album_id, int w, int h) {
        // NOTE: There is in fact a 1 pixel border on the right side in the ImageView
        // used to display this drawable. Take it into account now, so we don't have to
        // scale later.
        w -= 1;
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r");
                int sampleSize = 1;
                
                // Compute the closest power-of-two scale factor 
                // and pass that to sBitmapOptionsCache.inSampleSize, which will
                // result in faster decoding and better quality
                sBitmapOptionsCache.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);
                int nextWidth = sBitmapOptionsCache.outWidth >> 1;
                int nextHeight = sBitmapOptionsCache.outHeight >> 1;
                while (nextWidth>w && nextHeight>h) {
                    sampleSize <<= 1;
                    nextWidth >>= 1;
                    nextHeight >>= 1;
                }

                sBitmapOptionsCache.inSampleSize = sampleSize;
                sBitmapOptionsCache.inJustDecodeBounds = false;
                Bitmap b = BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);

                if (b != null) {
                    // finally rescale to exactly the size we need
                    if (sBitmapOptionsCache.outWidth != w || sBitmapOptionsCache.outHeight != h) {
                        Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
                        // Bitmap.createScaledBitmap() can return the same bitmap
                        if (tmp != b) b.recycle();
                        b = tmp;
                    }
                }
                
                return b;
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    /** Get album art for specified album. You should not pass in the album id
     * for the "unknown" album here (use -1 instead)
     * This method always returns the default album art icon when no album art is found.
     */
    public static Bitmap getArtwork(Context context, long song_id, long album_id) {
        return getArtwork(context, song_id, album_id, true);
    }

    /** Get album art for specified album. You should not pass in the album id
     * for the "unknown" album here (use -1 instead)
     */
    public static Bitmap getArtwork(Context context, long song_id, long album_id,
            boolean allowdefault) {

        if (album_id < 0) {
            // This is something that is not in the database, so get the album art directly
            // from the file.
            if (song_id >= 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
            if (allowdefault) {
                return getDefaultArtwork(context);
            }
            return null;
        }

        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, sBitmapOptions);
            } catch (FileNotFoundException ex) {
                // The album art thumbnail does not actually exist. Maybe the user deleted it, or
                // maybe it never existed to begin with.
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null && allowdefault) {
                            return getDefaultArtwork(context);
                        }
                    }
                } else if (allowdefault) {
                    bm = getDefaultArtwork(context);
                }
                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                }
            }
        }
        
        return null;
    }
    
    // get album art for specified file
    // private static final String sExternalMediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString();
    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
        Bitmap bm = null;
//        byte [] art = null;
//        String path = null;

        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }

        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }
        } catch (FileNotFoundException ex) {
            //
        }
        if (bm != null) {
            // mCachedBit = bm;
        }
        return bm;
    }
    
    private static Bitmap getDefaultArtwork(Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(
                context.getResources().openRawResource(R.drawable.albumart_mp_unknown), null, opts);
    }    
    public static long getCurrentAlbumId() {
        if (sService != null) {
            try {
                return sService.getAlbumId();
            } catch (RemoteException ex) {
            }
        }
        return -1;
    }

    public static long getCurrentArtistId() {
        if (sService != null) {
            try {
                return sService.getArtistId();
            } catch (RemoteException ex) {
            }
        }
        return -1;
    }

    public static long getCurrentAudioId() {
        if (sService != null) {
            try {
                return sService.getAudioId();
            } catch (RemoteException ex) {
            }
        }
        return -1;
    }
//    public static void shuffleAll(Context context, Cursor cursor) {
//        playAll(context, cursor, 0, true);
//    }
//
//    public static void playAll(Context context, Cursor cursor) {
//        playAll(context, cursor, 0, false);
//    }
//    
//    public static void playAll(Context context, Cursor cursor, int position) {
//        playAll(context, cursor, position, false);
//    }
    
    public static void playAll(Context context, MediaInfo [] list, int position) {
        playAll(context, list, position, false);
    }
    
//    private static void playAll(Context context, Cursor cursor, int position, boolean force_shuffle) {
//    
//        long [] list = Database.getSongListForCursor(cursor);
//        playAll(context, list, position, force_shuffle);
//    }
    
    public static void playAll(Context context, MediaInfo [] list, int position, boolean force_shuffle) {
        if (list.length == 0 || sService == null) {
        	// プレイリストが空ならば、ログを出力、トーストを表示して終了
            Log.d("MusicUtils", "attempt to play empty song list");
            // Don't try to play empty playlists. Nothing good will come of it.
            String message = context.getString(R.string.emptyplaylist, list.length);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (force_shuffle) {
            	// 強制シャッフルモードの場合
                sService.setShuffleMode(MediaPlaybackService.SHUFFLE_NORMAL);
            }
            // 現在のオーディオのidを取得
            long curid = sService.getAudioId();
            // 現在のキュー位置を取得
            int curpos = sService.getQueuePosition();
            if (position != -1 && curpos == position && curid == list[position].getId()) {
                // The selected file is the file that's currently playing;
                // figure out if we need to restart with a new playlist,
                // or just launch the playback activity.
                long [] listId = sService.getQueue();
                int [] listType = sService.getMediaType();
                
                MediaInfo [] listMedia = new MediaInfo[listId.length];
                for( int i=0; i < listId.length; i++ )
                {
                	listMedia[i] = new MediaInfo( listId[i], listType[i] );
                }
                
                if (listMedia.length == list.length) {
                	boolean bEqual = true;
                	for( int i=0; i < list.length; ++i)
                	{
                		if( listMedia[i].getId() != list[i].getId()
                		|| listMedia[i].getMediaType() != list[i].getMediaType()
                		)
                		{
                			bEqual = false;
                			break;
                		}
                	}
                	if(bEqual )
                	{
	                    // we don't need to set a new list, but we should resume playback if needed
	                    sService.play();
	                    return; // the 'finally' block will still run
                	}
                }
            }
            if (position < 0) {
                position = 0;
            }
            long [] listId = new long[list.length];
            int [] listType = new int[list.length];
            
            for( int i=0; i < list.length; i++ )
            {
            	listId[i] = list[i].getId();
            	listType[i]  = list[i].getMediaType();
            }
            
            sService.open(listId, listType, force_shuffle ? -1 : position);
            if( sService.isInitialized() )
            {
            	sService.play();
            }
            else
            {
            	Toast.makeText(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(), 
            			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getString(R.string.cant_play_media),
            			Toast.LENGTH_LONG).show();
            }
        } catch (RemoteException ex) {
        } finally {
        	// 再生画面へ移動
//    		IViewAction action1 = new TabSelectAction( ControlIDs.TAB_ID_MAIN,
//    				TabPage.TABPAGE_ID_PLAY );
//    		action1.doAction(null);
//    		IViewAction action2 = new TabSelectAction( ControlIDs.TAB_ID_PLAY,
//    				TabPage.TABPAGE_ID_PLAY_SUB );
//    		action2.doAction(null);  
//        	OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().setCurrentDisplayId( 
//        			ControlIDs.TAB_ID_PLAY, 
//    				TabPage.TABPAGE_ID_PLAY_SUB );
        	
//        	OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().sendUpdateMessage( 
//        			ControlIDs.TAB_ID_MAIN, TabPage.TABPAGE_ID_PLAY, true );
        	
        	OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
        	TimerAlertDialog.Builder dlgConfirm = new TimerAlertDialog.Builder(act);
            // アラートダイアログのタイトルを設定します
        	dlgConfirm.setTitle(R.string.move_playtab_title);
            // アラートダイアログのメッセージを設定します
        	dlgConfirm.setMessage(act.getString(R.string.move_playtab_message));
            // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        	dlgConfirm.setPositiveButton(R.string.alert_dialog_yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                    		IViewAction action = new TabSelectAction( ControlIDs.TAB_ID_MAIN,
                    				TabPage.TABPAGE_ID_PLAY );
                    		action.doAction(null);                        	
                        }
                    });
            // アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        	dlgConfirm.setNegativeButton(R.string.alert_dialog_no,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            // アラートダイアログのキャンセルが可能かどうかを設定します
        	dlgConfirm.setCancelable(true);
        	// アラートダイアログを作成、表示します
        	// TimerAlertDialog dlg = 
			dlgConfirm.create();
			dlgConfirm.show();
        	// dlg.show();
        }
    }
    public static void addToCurrentPlaylist(Context context, MediaInfo [] list) {
        if (sService == null) {
            return;
        }
        try {
        	long [] listId = new long[list.length];
        	int [] listType = new int[list.length];
        	
        	for( int i=0; i < list.length; i++ )
        	{
        		listId[i] = list[i].getId();
        		listType[i] = list[i].getMediaType();
        	}
        	
            sService.enqueue(listId, listType, MediaPlaybackService.LAST);
            String message = context.getResources().getQuantityString(
                    R.plurals.NNNtrackstoplaylist, list.length, Integer.valueOf(list.length));
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (RemoteException ex) {
        }
    }
    public static void clearQueue() {
        try {
            sService.removeTracks(0, Integer.MAX_VALUE);
        } catch (RemoteException ex) {
        }
    }
    
    /**
     *  次の曲へ
     */
    public static void next()
    {
        if (sService == null) return;
        try {
            sService.next();
        } catch (RemoteException ex) {
        }    	
    }
    /**
     *  前の曲へ
     */
    public static void prev()
    {
        if (sService == null) return;
        try {
            if (sService.position() < 2000) {
                sService.prev();
            } else {
                sService.seek(0);
                sService.play();
            }
        } catch (RemoteException ex) {
        }    	
    }
    
    public static void playPlaylist(Context context, long plid) {
    	MediaInfo [] list = Database.getSongListForPlaylist(context, plid);
        if (list != null) {
            playAll(context, list, -1, false);
        }
    }
    
}
