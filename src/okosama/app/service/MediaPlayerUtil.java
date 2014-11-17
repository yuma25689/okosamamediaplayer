package okosama.app.service;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import okosama.app.ControlIDs;
import okosama.app.LogWrapper;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.TimerAlertDialog;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.storage.Database;
import okosama.app.tab.TabPage;
import android.app.Activity;
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
import android.util.Log;
import android.widget.Toast;
// import android.provider.MediaStore;


public class MediaPlayerUtil {

	// �T�[�r�X
    public static IMediaPlaybackService sService = null;
    // �T�[�r�X�g�[�N��
    // �����炭�A�T�[�r�X�̓����I��ID�Ƃ��ė��p
    public static class ServiceToken {
        ContextWrapper mWrappedContext;
        ServiceToken(ContextWrapper context) {
            mWrappedContext = context;
        }
    }
    // �N�����̃T�[�r�X�i�[�p�H
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
     * �T�[�r�X�o�C���_
     * @author 25689
     *
     */
    private static class ServiceBinder implements ServiceConnection {
    	// �T�[�r�X�R�l�N�V�������C���v�������g
    	// �T�[�r�X�R�l�N�V�����������o�Ƃ��ĕێ�
        ServiceConnection mCallback;
        // �R���X�g���N�^�ŃT�[�r�X�R�l�N�V������ݒ�
        ServiceBinder(ServiceConnection callback) {
            mCallback = callback;
        }
        
        /**
         * �T�[�r�X���ڑ����ꂽ�Ƃ��R�[�������H
         */
        @Override
		public void onServiceConnected(ComponentName className, android.os.IBinder service) {
        	// �T�[�r�X���擾
            sService = IMediaPlaybackService.Stub.asInterface(service);
            initAlbumArtCache();
            if (mCallback != null) {
            	// �R�[���o�b�N��null�łȂ���΁A�R�[���o�b�N���R�[������
                mCallback.onServiceConnected(className, service);
            }
        }
        
        /**
         * �T�[�r�X���ؒf���ꂽ�Ƃ��ɃR�[�������H
         */
        @Override
		public void onServiceDisconnected(ComponentName className) {
            if (mCallback != null) {
            	// �R�[���o�b�N��null�łȂ���΁A�R�[���o�b�N���R�[������
               mCallback.onServiceDisconnected(className);
            }
            // �����Ɋi�[���ꂽ�T�[�r�X���N���A����
            sService = null;
        }
    }    
    /**
     * �T�[�r�X�ƃo�C���h����
     * @param context
     * @return �T�[�r�X�g�[�N��
     */
    public static ServiceToken bindToService(Activity context) {
        return bindToService(context, null);
    }

    /**
     * �T�[�r�X�ƃo�C���h����
     * @param context
     * @param callback
     * @return �T�[�r�X�g�[�N��
     */
    public static ServiceToken bindToService(
    		Activity context, ServiceConnection callback) {
    	// �w�肳�ꂽ�A�N�e�B�r�e�B����A���ۂ̃A�N�e�B�r�e�B���擾����
    	// ��U�e�A�N�e�B�r�e�B�̎擾�����݂āA����őʖڂȂ�w�肳�ꂽ�A�N�e�B�r�e�B�𗘗p����
        Activity realActivity = context;//.getParent();
//        if (realActivity == null) {
//            realActivity = context;
//        }
        // �A�N�e�B�r�e�B�̃R���e�L�X�g���b�p�[���擾
        ContextWrapper cw = new ContextWrapper(realActivity);
        // �T�[�r�X���N������
        cw.startService(new Intent(cw, MediaPlaybackService.class));
        // �T�[�r�X�ƁA�w�肳�ꂽ�T�[�r�X�R�l�N�V�������o�C���h
        ServiceBinder sb = new ServiceBinder(callback);
        // �T�[�r�X���T�[�r�X�o�C���_�ƕR����H
        if (cw.bindService((new Intent()).setClass(cw, MediaPlaybackService.class), sb, 0)) {
        	// ����������A�}�b�v�Ɋi�[���Ă���
            sConnectionMap.put(cw, sb);
            // �T�[�r�X��ID�ƂȂ�g�[�N����ԋp����
            return new ServiceToken(cw);
        }
        // ���s������A���O�o�͂���null��ԋp����
        LogWrapper.e("Music", "Failed to bind to service");
        return null;
    }
    
    /**
     * �o�C���h����������
     * @param token
     */
    public static void unbindFromService(ServiceToken token) {
    	// Toast.makeText(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(), "unbind", Toast.LENGTH_LONG).show();
            	
        if (token == null) {
        	// �g�[�N����null�ł���΁A���O���͂��ďI��
            LogWrapper.e("MediaPlayer", "Trying to unbind with null token");
            return;
        }
        // �g�[�N������R���e�L�X�g���b�p�[���擾
        ContextWrapper cw = token.mWrappedContext;
        // �w�肳�ꂽ�R���e�L�X�g���b�p�[�̃G���g�����폜���A���̃T�[�r�X�o�C���_���擾
        ServiceBinder sb = sConnectionMap.remove(cw);
        if (sb == null) {
        	// �T�[�r�X�o�C���_���擾�ł��Ȃ�������A�G���[���O���o�͂��A�����𔲂���
            LogWrapper.e("MediaPlayer", "Trying to unbind for unknown Context");
            return;
        }
        // �T�[�r�X�o�C���_����A�T�[�r�X�̃o�C���h����������
        cw.unbindService(sb);
        token = null;
        if (sConnectionMap.isEmpty()) {
            // �T�[�r�X���P���Ȃ��Ȃ�����A�T�[�r�X���N���A����
            sService = null;
        }
    }

    
    ///////// �������炵�΂炭�A���o���A�[�g�p ///////////
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
    // �A���o���A�[�g�̃L���b�V���̃}�b�v
    public static final HashMap<Long, Drawable> 
    	sArtCache = new HashMap<Long, Drawable>();    
    // �A���o���A�[�g�̃L���b�V����ID
    private static int sArtCacheId = -1; 
    // private static int sArtId = -2;
    // private static Bitmap mCachedBit = null;
    private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    
    /**
     * �A���o���A�[�g�̃L���b�V����������
     */
    public static void initAlbumArtCache() {
        try {
        	// ���f�B�A�̃}�E���g���ꂽ����id�Ƃ��Ď擾�A�ێ��H
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
     * �A���o���A�[�g�̃L���b�V�����N���A
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
        if (d == null && defaultArtwork != null ) {
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
    	boolean bAlreadyPlayed = false;
		try {
			bAlreadyPlayed = sService.isPlaying();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWrapper.e("playAll","service.isPlaying error=" + e.getMessage());
		}
        if (list.length == 0 || sService == null) {
        	// �v���C���X�g����Ȃ�΁A���O���o�́A�g�[�X�g��\�����ďI��
            Log.d("MusicUtils", "attempt to play empty song list");
            // Don't try to play empty playlists. Nothing good will come of it.
            String message = context.getString(R.string.emptyplaylist, list.length);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (force_shuffle) {
            	// �����V���b�t�����[�h�̏ꍇ
                sService.setShuffleMode(MediaPlaybackService.SHUFFLE_NORMAL);
            }
            // ���݂̃I�[�f�B�I��id���擾
            long curid = sService.getAudioId();
            // ���݂̃L���[�ʒu���擾
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
        	// �Đ���ʂֈړ�
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
        	
        }
        // ���L�̏����Ɉ�v����ꍇ�A�^�u���ړ����邩�ǂ����̃A���[�g���o�͂���
    	OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
    	if( // false == bAlreadyPlayed // ���f�B�A���Đ����łȂ�����
    			// ���݂̃^�u���L���[�̃^�u�łȂ�
    	//&& 
    		false == ( act.getTabStocker().getCurrentTabId() == ControlIDs.TAB_ID_MAIN 
    		&& act.getTabStocker().getCurrentTabId() == TabPage.TABPAGE_ID_NOW_PLAYLIST )
    	)
    	{
        		
	    	TimerAlertDialog.Builder dlgConfirm = new TimerAlertDialog.Builder(act);
	        // �A���[�g�_�C�A���O�̃^�C�g����ݒ肵�܂�
	    	dlgConfirm.setTitle(R.string.move_playtab_title);
	        // �A���[�g�_�C�A���O�̃��b�Z�[�W��ݒ肵�܂�
	    	dlgConfirm.setMessage(act.getString(R.string.move_playtab_message));
	        // �A���[�g�_�C�A���O�̍m��{�^�����N���b�N���ꂽ���ɌĂяo�����R�[���o�b�N���X�i�[��o�^���܂�
	    	dlgConfirm.setPositiveButton(R.string.alert_dialog_yes,
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                		IViewAction action = new TabSelectAction( ControlIDs.TAB_ID_MAIN,
	                				TabPage.TABPAGE_ID_PLAY );
	                		action.doAction(null);                        	
	                    }
	                });
	        // �A���[�g�_�C�A���O�̔ے�{�^�����N���b�N���ꂽ���ɌĂяo�����R�[���o�b�N���X�i�[��o�^���܂�
	    	dlgConfirm.setNegativeButton(R.string.alert_dialog_no,
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                    }
	                });
	        // �A���[�g�_�C�A���O�̃L�����Z�����\���ǂ�����ݒ肵�܂�
	    	dlgConfirm.setCancelable(true);
	    	// �A���[�g�_�C�A���O���쐬�A�\�����܂�
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
     *  ���̋Ȃ�
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
     *  �O�̋Ȃ�
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
    
    public static boolean isNowPlayingVideos()
    {
    	if( sService == null )
    	{
    		return false;
    	}
   		try {
			int [] listType = sService.getMediaType();
			
			if( listType != null && 0 < listType.length)
			{
				if( listType[0] == MediaInfo.MEDIA_TYPE_VIDEO )
				{
					// �L���[�ɂ��鍀�ڂ̍ŏ��̂P��Video���������_�ŁAVideo�Ƃ݂Ȃ�
					// (����A�P�ł�Video�Ȃ�ΑS��Video�̂͂�)
					return true;
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   		return false;
    }
}
