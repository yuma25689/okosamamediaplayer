package okosama.app.storage;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;

import okosama.app.LogWrapper;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.service.IMediaPlaybackService;
import okosama.app.service.MediaInfo;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.tab.TabPage;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.GenresColumns;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.SubMenu;
import android.widget.Toast;

/**
 * �f�[�^�x�[�X�N���X
 * ���̂Ƃ���A�f�[�^�x�[�X���\�[�X�p�̃����o��static�֐���K���ɓ˂�����ł��邾��
 * �����Ȋ֐���˂����񂾂����֗̕��N���X�ɂȂ��Ă��܂��Ă���̂ŁASingleton�ɂ��Ă���
 * @author 25689
 *
 */
public class Database {
	private static Database inst = new Database();
    // private static String mPlaylist;
    private static boolean get_external = true;
	private Database()
	{
	}
	public static void setActivity(OkosamaMediaPlayerActivity c )
	{
		ctx = c;
	}
	/**
	 * �V���O���g���̃C���X�^���X�擾
	 * �����ɁA�K��Context��ݒ肳���邽�߂ɁA������Context����点��
	 * @param _ctx
	 * @return
	 */
	public static Database getInstance(OkosamaMediaPlayerActivity _ctx)
	{
		ctx = _ctx;
		return inst;
	}
	public static Database getInstance(boolean external)
	{
		ctx = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		get_external = external;
		return inst;
	}	
	// Context
	static OkosamaMediaPlayerActivity ctx = null;
	
	// playlist�萔
	public static final String PlaylistName_NowPlaying = "nowplaying";
	public static final String PlaylistName_Podcasts = "podcasts";
	public static final String PlaylistName_RecentlyAdded = "recentlyadded";
	
	/**
	 * �����ŃN�G���𔭍s����
	 * @param context
	 * @param uri �R���e���g�v���o�C�_�H
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @param limit max�̌����H
	 * @return
	 */
    public static Cursor query(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder, int limit) {
        try {
        	ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            if (limit > 0) {
                uri = uri.buildUpon().appendQueryParameter("limit", "" + limit).build();
            }
            // Log.d("query uri", "uri :" +uri);
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
         } catch (UnsupportedOperationException ex) {
        	String msg = ex.getMessage();
        	LogWrapper.e("queryerror",msg);
            return null;
        }
    }
    public static Cursor query(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {
        return query(context, uri, projection, selection, selectionArgs, sortOrder, 0);
    }	

    
    ///////// ��������ėp���̂Ȃ��֐� //////////////////////////////
    /**
     * �A�[�e�B�X�g�J�[�\���̍쐬
     * @param async
     * @param filter
     * @return
     */
    public Cursor createArtistCursor() {//AsyncQueryHandler async, String filter) {

    	// �A�[�e�B�X�g������łȂ��A�Ƃ���������t��������
        StringBuilder where = new StringBuilder();
//        where.append(ArtistColumns.ARTIST + " != ''");
        String whereclause = where.toString();
        
        // �A�[�e�B�X�g�̃R���e���g�v���o�C�_��uri��ݒ�
        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        //String external_string;
        if( get_external == false )
        {
        	uri = MediaStore.Audio.Artists.INTERNAL_CONTENT_URI;
        }
        
        // �����Ŏw�肳�ꂽfilter�p�̒P��ŁASQL��where����w��H
        // �A�[�e�B�X�g���̈ꕔ�̔z��
        String [] keywords = null;
        
        // �J�����̐ݒ�
        String[] cols = new String[] {
                BaseColumns._ID,
                ArtistColumns.ARTIST,
                ArtistColumns.NUMBER_OF_ALBUMS,
                ArtistColumns.NUMBER_OF_TRACKS
        };
        Cursor ret = null;
//        if (async != null) {
//        	// �񓯊��Ȃ�΁A�񓯊��ŃN�G�����s
//        	// Log.d("query uri", "uri :" + uri);
//            async.startQuery(TabPage.TABPAGE_ID_ARTIST, null, uri,
//                    cols, whereclause , keywords, ArtistColumns.ARTIST_KEY);
//        } else {
        	// �����Ȃ�΁A�����ŃN�G�����s
            ret = query(ctx, uri,
                    cols, whereclause, keywords, ArtistColumns.ARTIST_KEY);
        //}
        // ����ꂽ�J�[�\����ԋp
        return ret;	
    }
    
    /**
     * �A���o���J�[�\���̍쐬
     * @param async
     * @param filter
     * @param artistId
     * @return
     */
    public Cursor createAlbumCursor() { //AsyncQueryHandler async, String filter ) { // , String artistId) {
    	String artistId = OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getArtistID();
    	// where���ݒ肷��
        StringBuilder where = new StringBuilder();
//        where.append(AlbumColumns.ALBUM + "!=''");
        String whereclause = where.toString();
        
        // �A���o���̃R���e���g�v���o�C�_��uri��ݒ�
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String external_string = "external";
        if( get_external == false )
        {
        	uri = MediaStore.Audio.Albums.INTERNAL_CONTENT_URI;
        	external_string = "internal";	// �����A����ł悢
        }
        
        // �t�B���^�̐ݒ�
        // ����́A�A�[�e�B�X�g���A���o���ǂ���ł��悢
        // Add in the filtering constraints
        String [] keywords = null;
//        if (filter != null) {
//            String [] searchWords = filter.split(" ");
//            keywords = new String[searchWords.length];
//            Collator col = Collator.getInstance();
//            col.setStrength(Collator.PRIMARY);
//            for (int i = 0; i < searchWords.length; i++) {
//                String key = MediaStore.Audio.keyFor(searchWords[i]);
//                key = key.replace("\\", "\\\\");
//                key = key.replace("%", "\\%");
//                key = key.replace("_", "\\_");
//                keywords[i] = '%' + key + '%';
//            }
//            for (int i = 0; i < searchWords.length; i++) {
//                where.append(" AND ");
//                where.append(AudioColumns.ARTIST_KEY + "||");
//                where.append(AudioColumns.ALBUM_KEY + " LIKE ? ESCAPE '\\'");
//            }
//        }
        
        // �擾�J�����̐ݒ�
        String[] cols = new String[] {
                BaseColumns._ID,
                AlbumColumns.ARTIST,
                AlbumColumns.ALBUM,
                AlbumColumns.ALBUM_ART
        };
        
        // �N�G���𔭍s���邪�AartistID��null���ǂ����ɂ���āA�N�G����ύX����
        Cursor ret = null;
        if (artistId != null) {
        	// artistID�����͂���Ă���ꍇ
        	// uri�ɁAartist���܂߂�
//            if (async != null) {
//                async.startQuery(TabPage.TABPAGE_ID_ALBUM, null,
//                        MediaStore.Audio.Artists.Albums.getContentUri(external_string,//"external",
//                                Long.valueOf(artistId)),
//                        cols, whereclause, keywords, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
//            } else {
                ret = query(ctx,
                        MediaStore.Audio.Artists.Albums.getContentUri(external_string,//"external",
                                Long.valueOf(artistId)),
                        cols, whereclause, keywords, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
//            }
        } else {
        	// artistId��null
        	// uri�́Aalbum�𗘗p����
//            if (async != null) {
//            	LogWrapper.i("uri:", uri.toString());
//            	//async.startQuery(TabPage.TABPAGE_ID_ALBUM, null, uri, null, null, null, null );
//                async.startQuery(TabPage.TABPAGE_ID_ALBUM, null, uri,
//                        cols, whereclause, keywords, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
//            } else {
                ret = query(ctx, uri,
                        cols, whereclause, keywords, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
            //}
        }
        return ret;
    }
    // �v���C���X�g�J�[�\���쐬�p�̒萔
    // �擾����J����
    private static final String[] playlistCols = new String[] {
            MediaStore.Audio.Playlists._ID,
            MediaStore.Audio.Playlists.NAME
    };    	
    private static final long RECENTLY_ADDED_PLAYLIST = -1;
    private static final long ALL_SONGS_PLAYLIST = -2;
    private static final long PODCASTS_PLAYLIST = -3;
    
    /**
     * �J�[�\���̃��b�p�[���쐬�H
     * @param c
     * @param createShortCut
     * @return
     */
    public Cursor mergedCursor(Cursor c, boolean createShortCut) {
        if (c == null) {
            return null;
        }
        if (c instanceof MergeCursor) {
            // this shouldn't happen, but fail gracefully
            Log.d("PlaylistBrowserActivity", "Already wrapped");
            return c;
        }
        // �Q�����\�̎g����J�[�\��
        MatrixCursor autoplaylistscursor = new MatrixCursor(playlistCols);
        if (createShortCut) {
        	// �V���[�g�J�b�g�����ꍇ�H
        	// �S�āH
            ArrayList<Object> all = new ArrayList<Object>(2);
            all.add(ALL_SONGS_PLAYLIST);
            all.add(ctx.getString(R.string.play_all));
            autoplaylistscursor.addRow(all);
        }
        // �ŋߒǉ����ꂽ���́H
        ArrayList<Object> recent = new ArrayList<Object>(2);
        recent.add(RECENTLY_ADDED_PLAYLIST);
        recent.add(ctx.getString(R.string.recentlyadded));
        autoplaylistscursor.addRow(recent);
        
        // �A�[�e�B�X�g�̃R���e���g�v���o�C�_��uri�ɐݒ�H
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //String external_string;
        if( get_external == false )
        {
        	uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        }
        // Podcast�̂��̂̌������擾�H
        // check if there are any podcasts
        Cursor counter = query(ctx, uri,//MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {"count(*)"}, "is_podcast=1", null, null);
        if (counter != null) {
        	// �������擾�ł�����H
            counter.moveToFirst();
            // ������ێ�
            int numpodcasts = counter.getInt(0);
            counter.close();
            if (numpodcasts > 0) {
            	// Podcast�������
            	// Podcast�̃V���[�g�J�b�g�쐬�H
                ArrayList<Object> podcasts = new ArrayList<Object>(2);
                podcasts.add(PODCASTS_PLAYLIST);
                podcasts.add(ctx.getString(R.string.podcasts_listitem));
                autoplaylistscursor.addRow(podcasts);
            }
        }

        // ���̃J�[�\���ƁA�����������}�[�W�����J�[�\�������H
        Cursor cc = new MergeCursor(new Cursor [] {autoplaylistscursor, c});
        return cc;
    }
    
    /**
     * �v���C���X�g�̃J�[�\�����쐬
     * @param async
     * @param filterstring
     * @return
     */
    public Cursor createPlaylistCursor(AsyncQueryHandler async, String filterstring, boolean createShortCut) {

    	// �v���C���X�g������łȂ����̂Ƃ��������ɂ���
        StringBuilder where = new StringBuilder();
        where.append(PlaylistsColumns.NAME + " != ''");
        String whereclause = where.toString();
               
        // �t�B���^�̐ݒ�
        // �v���C���X�g���̈�v����
        // Add in the filtering constraints
        String [] keywords = null;
        if (filterstring != null) {
            String [] searchWords = filterstring.split(" ");
            keywords = new String[searchWords.length];
            Collator col = Collator.getInstance();
            col.setStrength(Collator.PRIMARY);
            for (int i = 0; i < searchWords.length; i++) {
                keywords[i] = '%' + searchWords[i] + '%';
            }
            for (int i = 0; i < searchWords.length; i++) {
                where.append(" AND ");
                where.append(PlaylistsColumns.NAME + " LIKE ?");
            }
        }
        
        // �v���C���X�g�̃R���e���g�v���o�C�_��uri��ݒ肷��
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        //String external_string;
        if( get_external == false )
        {
        	uri = MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI;
        }         
        
        if (async != null) {
        	// �񓯊��̏ꍇ
        	// TODO: �󂯐�Ń}�[�W�J�[�\���ɂ���K�v�L��
            async.startQuery(TabPage.TABPAGE_ID_PLAYLIST, null, uri,
            		playlistCols, whereclause, keywords, PlaylistsColumns.NAME);
            return null;
        }
        Cursor c = null;
        c = query(ctx, uri,//MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
        		playlistCols, whereclause, keywords, PlaylistsColumns.NAME);
        
        // �Ō�ɁA�V���[�g�J�b�g��t���������J�[�\����ԋp����H
        return c;//mergedCursor(c, createShortCut);
    }
    /**
     * �r�f�I�J�[�\���̍쐬
     * @return
     */
    public Cursor createVideoCursor() { //AsyncQueryHandler async, String filter ) { // , String artistId) {
    	// ALL_SONGS_PLAYLIST String artistId = OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getArtistID();
    	// where���ݒ肷��
        StringBuilder where = new StringBuilder();
        where.append(MediaStore.Video.Media.TITLE + " != '' ");
        String whereclause = where.toString();
        
        // �R���e���g�v���o�C�_��uri��ݒ�
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//        String external_string = "external";
//        if( get_external == false )
//        {
//        	uri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
//        	external_string = "internal";	// �����A����ł悢
//        }
        
        // �t�B���^�̐ݒ�
        // ����́A�A�[�e�B�X�g���A���o���ǂ���ł��悢
        // Add in the filtering constraints
        String [] keywords = null;
        
        // �擾�J�����̐ݒ�
        String[] cols = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.ARTIST
        };
        
        // �N�G���𔭍s���邪�AartistID��null���ǂ����ɂ���āA�N�G����ύX����
        Cursor ret = null;
            ret = query(ctx, uri,
                    cols, whereclause, keywords, MediaStore.Video.Media.TITLE + " COLLATE UNICODE");
        return ret;
    }
    
    // �g���b�N�J�[�\���p�����o�ϐ�
    // �\�[�g��
    private static String mSortOrder;
    // �J����
    private static final String mPlaylistCols[] = new String[] {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            //AlbumColumns.ALBUM_ART,            
            // AudioColumns.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION
    };    
    // �J����
    private static final String mGenreCols[] = new String[] {
            MediaStore.Audio.Genres._ID,
            MediaStore.Audio.Genres.NAME,
    };    
    // �v���C���X�g�̃J�����H
    static final String mPlaylistMemberCols[] = new String[] {
            MediaStore.Audio.Playlists.Members._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            //AlbumColumns.ALBUM_ART,            
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Playlists.Members.PLAY_ORDER,
            MediaStore.Audio.Playlists.Members.AUDIO_ID,
            MediaStore.Audio.Media.IS_MUSIC
    };    
    //private Cursor createTrackCursor(TrackListAdapter.TrackQueryHandler queryhandler, String filter,
    /**
     * �g���b�N�J�[�\���̍쐬
     * @param queryhandler
     * @param filter
     * @param async
     * @return
     */
    public Cursor createTrackCursor(AsyncQueryHandler async, String filter //String playlist, String filter,
        ) 
    {
        Cursor ret = null;
        // �\�[�g�����ɁA�^�C�g����ݒ�
        mSortOrder = AudioColumns.TITLE_KEY;
        // �^�C�g������łȂ����̂�������
        StringBuilder where = new StringBuilder();
        where.append(MediaColumns.TITLE + " != ''");

        // �t�B���^��ݒ�
        // ���̏ꍇ�A�A�[�e�B�X�g�ƃg���b�N�H
        // Add in the filtering constraints
        String [] keywords = null;
        
        // �g���b�N�̃R���e���g�v���o�C�_��uri��ݒ�
    	Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//    	String strExOrIn = "external";
//        if( get_external == false )
//        {
//        	uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
//        	strExOrIn = "internal";
//        }        	
        
        	mSortOrder = AudioColumns.TRACK + ", " + mSortOrder;
            // ���y�w��
            where.append(" AND " + AudioColumns.IS_MUSIC + "=1");
            where.append(" AND " + AudioColumns.IS_ALARM + "=0");
            where.append(" AND " + AudioColumns.IS_NOTIFICATION + "=0");
            where.append(" AND " + AudioColumns.IS_PODCAST + "=0");
            // �N�G�����s
            // LogWrapper.i("query1","query1");
//            ret = queryhandler.doQuery(ctx, uri,//MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//            		mPlaylistCols, where.toString() , keywords, mSortOrder, async);
        //}
            if (async != null) {
            	LogWrapper.i("uri:", uri.toString());
            	//async.startQuery(TabPage.TABPAGE_ID_ALBUM, null, uri, null, null, null, null );
                async.startQuery(TabPage.TABPAGE_ID_SONG, null, uri,
                		mPlaylistCols, where.toString(), keywords, mSortOrder);
            } else {
                ret = query(ctx, uri,
                		mPlaylistCols, where.toString(), keywords, mSortOrder);
            }
            //return ret;

        // �񓯊��ł��Anowplaying�̏ꍇ�̓��b�Z�[�W�����ł��Ȃ��̂ŁA�����ŏ������ł��Ȃ��B
        // ����ɑΉ�
        // This special case is for the "nowplaying" cursor, which cannot be handled
        // asynchronously using AsyncQueryHandler, so we do some extra initialization here.
        //if (ret != null && async) {
        	// TODO:����
            // ctx.initAdapter(TabPage.TABPAGE_ID_SONG,ret);
            //setTitle();
        //}
        return ret;
    }
    /**
     * �W�������J�[�\���̍쐬
     * @return Cursor
     */
    public Cursor createGenreCursor() 
    {
        Cursor ret = null;
        // �\�[�g�����ɁA���O��ݒ�
        //mSortOrder = GenresColumns.NAME;
        // �^�C�g������łȂ����̂�������
        StringBuilder where = new StringBuilder();
        where.append(GenresColumns.NAME + " != ''");

        // �t�B���^��ݒ�
        // Add in the filtering constraints
        String [] keywords = null;
        
        // �W�������̃R���e���g�v���o�C�_��uri��ݒ�
    	Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        
        // �N�G�����s
        ret = query(ctx, uri,
        		mGenreCols, where.toString(), keywords, GenresColumns.NAME);
        return ret;
    }
    /**
     * ���̃W�������̑S�Ă̊y�Ȃ��擾�����J�[�\�����쐬
     * @return Cursor
     */
    public Cursor createSongListCursorFromGenre(long genreId) 
    {
        Cursor ret = null;
        
        // �g���b�N�̃R���e���g�v���o�C�_��uri��ݒ�
    	String strExOrIn = "external";
        if( get_external == false )
        {
        	strExOrIn = "internal";
        }        	
        Uri uri = MediaStore.Audio.Genres.Members.getContentUri(strExOrIn, genreId);
        // LogWrapper.i("genre - uri", "uri = " + uri);
        String[] cols = new String[] {
        	MediaStore.Audio.Media._ID //,
        	//MediaStore.Audio.Genres.Members.AUDIO_ID
        };

        StringBuilder where = new StringBuilder();
        where.append(MediaColumns.TITLE + " != ''");
        where.append(" AND " + AudioColumns.IS_MUSIC + "=1");
        where.append(" AND " + AudioColumns.IS_ALARM + "=0");
        where.append(" AND " + AudioColumns.IS_NOTIFICATION + "=0");
        where.append(" AND " + AudioColumns.IS_PODCAST + "=0");
        
        // �N�G�����s
        ret = query(ctx, uri,
        		cols, where.toString(), null, null);
        return ret;
    }

    /**
     * nowplaying�J�[�\���N���X�H
     * @author 25689
     *
     */
    public class NowPlayingCursor extends AbstractCursor
    {
        public NowPlayingCursor(IMediaPlaybackService service, String [] cols)
        {
        	// �J�����A�T�[�r�X���i�[�A�J�[�\���̍쐬
            mCols = cols;
            mService  = service;
            makeNowPlayingCursor();
        }
        /**
         * ���݃v���C���X�g�̃J�[�\��������ɍ쐬����H
         */
        private void makeNowPlayingCursor() {
        	// �������H
            mCurrentPlaylistCursor = null;
            try {
            	// ���݂̃v���C���X�g�擾�H
                mNowPlaying = mService.getQueue();
            } catch (RemoteException ex) {
                mNowPlaying = new long[0];
            }
            mSize = mNowPlaying.length;
            if (mSize == 0) {
                return;
            }

            // where��̍쐬
            StringBuilder where = new StringBuilder();
            // ID���Anowplaying�̒��ɂ������
            where.append(BaseColumns._ID + " IN (");
            for (int i = 0; i < mSize; i++) {
                where.append(mNowPlaying[i]);
                if (i < mSize - 1) {
                    where.append(",");
                }
            }
            where.append(")");

            // uri�̐ݒ�
            // �g���b�N�̃R���e���g�v���o�C�_
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            //String external_string;
            if( get_external == false )
            {
            	uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
            }
            // �N�G�����s
            mCurrentPlaylistCursor = query(ctx,
                    uri,//MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    mCols, where.toString(), null, BaseColumns._ID);

            if (mCurrentPlaylistCursor == null) {
            	// �J�[�\���擾���s
                mSize = 0;
                return;
            }
            
            // �I�[�f�B�I��ID���擾����H
            // �Ȃ�ƂȂ��A���̂��̂Ɠ����悤�Ȃ��̂���ꂻ���Ɍ����邪�E�E�E
            int size = mCurrentPlaylistCursor.getCount();
            mCursorIdxs = new long[size];
            mCurrentPlaylistCursor.moveToFirst();
            int colidx = mCurrentPlaylistCursor.getColumnIndexOrThrow(BaseColumns._ID);
            for (int i = 0; i < size; i++) {
                mCursorIdxs[i] = mCurrentPlaylistCursor.getLong(colidx);
                mCurrentPlaylistCursor.moveToNext();
            }
            mCurrentPlaylistCursor.moveToFirst();
            mCurPos = -1;
            
            // �ǂ����A�����Ō��̂��̂ƁA�f�[�^�x�[�X����̎擾�l�̍������`�F�b�N���A�x���t�@�C�ƃt�B�b�N�X���s��
            // At this point we can verify the 'now playing' list we got
            // earlier to make sure that all the items in there still exist
            // in the database, and remove those that aren't. This way we
            // don't get any blank items in the list.
            try {
            	// �����[�u���ꂽ�g���b�N�𒲂ׂ�
                int removed = 0;
                for (int i = mNowPlaying.length - 1; i >= 0; i--) {
                    long trackid = mNowPlaying[i];
                    int crsridx = Arrays.binarySearch(mCursorIdxs, trackid);
                    if (crsridx < 0) {
                        LogWrapper.i("@@@@@", "item no longer exists in db: " + trackid);
                        removed += mService.removeTrack(trackid);
                    }
                }
                if (removed > 0) {
                	// �Đݒ�
                    mNowPlaying = mService.getQueue();
                    mSize = mNowPlaying.length;
                    if (mSize == 0) {
                        mCursorIdxs = null;
                        return;
                    }
                }
            } catch (RemoteException ex) {
                mNowPlaying = new long[0];
            }
        }

        @Override
        public int getCount()
        {
            return mSize;
        }

        /**
         * ���݋Ȃ̈ړ��H
         */
        @Override
        public boolean onMove(int oldPosition, int newPosition)
        {
            if (oldPosition == newPosition)
                return true;
            
            if (mNowPlaying == null || mCursorIdxs == null || newPosition >= mNowPlaying.length) {
                return false;
            }

            // The cursor doesn't have any duplicates in it, and is not ordered
            // in queue-order, so we need to figure out where in the cursor we
            // should be.
           
            long newid = mNowPlaying[newPosition];
            int crsridx = Arrays.binarySearch(mCursorIdxs, newid);
            mCurrentPlaylistCursor.moveToPosition(crsridx);
            mCurPos = newPosition;
            
            return true;
        }

        /**
         * ���ڂ̍폜�H
         * @param which
         * @return
         */
        public boolean removeItem(int which)
        {
            try {
            	// �w��g���b�N���폜�H
                if (mService.removeTracks(which, which) == 0) {
                    return false; // delete failed
                }
                int i = which;
                mSize--;
                while (i < mSize) {
                    mNowPlaying[i] = mNowPlaying[i+1];
                    i++;
                }
                onMove(-1, mCurPos);
            } catch (RemoteException ex) {
            }
            return true;
        }
        
        /**
         * �L���[�̍��ڂ��ړ�����
         * @param from
         * @param to
         */
        public void moveItem(int from, int to) {
            try {
                mService.moveQueueItem(from, to);
                mNowPlaying = mService.getQueue();
                onMove(-1, mCurPos); // update the underlying cursor
            } catch (RemoteException ex) {
            }
        }

        /**
         * �v���C���X�g�̑S�Ă�id�����O�o�́H
         */
//        private void dump() {
//            String where = "(";
//            for (int i = 0; i < mSize; i++) {
//                where += mNowPlaying[i];
//                if (i < mSize - 1) {
//                    where += ",";
//                }
//            }
//            where += ")";
//            LogWrapper.i("NowPlayingCursor: ", where);
//        }

        /**
         * ���ݍs�̎w��J�����̕�����擾�H
         */
        @Override
        public String getString(int column)
        {
            try {
                return mCurrentPlaylistCursor.getString(column);
            } catch (Exception ex) {
                onChange(true);
                return "";
            }
        }
        /**
         * ���ݍs�̎w��J������short�擾�H
         */
        @Override
        public short getShort(int column)
        {
            return mCurrentPlaylistCursor.getShort(column);
        }

        /**
         * ���ݍs�̎w��J������int�擾�H
         */
        @Override
        public int getInt(int column)
        {
            try {
                return mCurrentPlaylistCursor.getInt(column);
            } catch (Exception ex) {
                onChange(true);
                return 0;
            }
        }

        /**
         * ���ݍs�̎w��J������long�擾�H
         */
        @Override
        public long getLong(int column)
        {
            try {
                return mCurrentPlaylistCursor.getLong(column);
            } catch (Exception ex) {
                onChange(true);
                return 0;
            }
        }

        /**
         * ���ݍs�̎w��J������float�擾�H
         */
        @Override
        public float getFloat(int column)
        {
            return mCurrentPlaylistCursor.getFloat(column);
        }

        /**
         * ���ݍs�̎w��J������double�擾�H
         */
        @Override
        public double getDouble(int column)
        {
            return mCurrentPlaylistCursor.getDouble(column);
        }

        /**
         * ���ݍs�̎w��J������isnull�擾�H
         */
        @Override
        public boolean isNull(int column)
        {
            return mCurrentPlaylistCursor.isNull(column);
        }

        @Override
        public String[] getColumnNames()
        {
            return mCols;
        }
        
        @Override
        public void deactivate()
        {
            if (mCurrentPlaylistCursor != null)
                mCurrentPlaylistCursor.deactivate();
        }

        /**
         * �č쐬
         */
        @Override
        public boolean requery()
        {
            makeNowPlayingCursor();
            return true;
        }

        private String [] mCols;
        private Cursor mCurrentPlaylistCursor;     // updated in onMove
        private int mSize;          // size of the queue
        private long[] mNowPlaying;
        private long[] mCursorIdxs;
        private int mCurPos;
        private IMediaPlaybackService mService;
    }

    public interface Defs {
        public final static int OPEN_URL = 0;
        public final static int ADD_TO_PLAYLIST = 1;
        public final static int USE_AS_RINGTONE = 2;
        public final static int PLAYLIST_SELECTED = 3;
        public final static int NEW_PLAYLIST = 4;
        public final static int PLAY_SELECTION = 5;
        public final static int GOTO_START = 6;
        public final static int GOTO_PLAYBACK = 7;
        public final static int PARTY_SHUFFLE = 8;
        public final static int SHUFFLE_ALL = 9;
        public final static int DELETE_ITEM = 10;
        public final static int SCAN_DONE = 11;
        public final static int QUEUE = 12;
        public final static int CHILD_MENU_BASE = 13; // this should be the last item
        public final static int SHOW_ITEM_INFORMATION = 14;
    }
  
    /**
     * Fills out the given submenu with items for "new playlist" and
     * any existing playlists. When the user selects an item, the
     * application will receive PLAYLIST_SELECTED with the Uri of
     * the selected playlist, NEW_PLAYLIST if a new playlist
     * should be created, and QUEUE if the "current playlist" was
     * selected.
     * @param context The context to use for creating the menu items
     * @param sub The submenu to add the items to.
     */
    public static void makePlaylistMenu(Context context, SubMenu sub) {
        String[] cols = new String[] {
                BaseColumns._ID,
                PlaylistsColumns.NAME
        };
        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {
            System.out.println("resolver = null");
        } else {
            String whereclause = PlaylistsColumns.NAME + " != ''";
            Cursor cur = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                cols, whereclause, null,
                PlaylistsColumns.NAME);
            sub.clear();
            sub.add(1, Defs.QUEUE, 0, R.string.queue);
            sub.add(1, Defs.NEW_PLAYLIST, 0, R.string.new_playlist);
            if (cur != null && cur.getCount() > 0) {
                //sub.addSeparator(1, 0);
                cur.moveToFirst();
                while (! cur.isAfterLast()) {
                    Intent intent = new Intent();
                    intent.putExtra("playlist", cur.getLong(0));
//                    if (cur.getInt(0) == mLastPlaylistSelected) {
//                        sub.add(0, MusicBaseActivity.PLAYLIST_SELECTED, cur.getString(1)).setIntent(intent);
//                    } else {
                        sub.add(1, Defs.PLAYLIST_SELECTED, 0, cur.getString(1)).setIntent(intent);
//                    }
                    cur.moveToNext();
                }
            }
            if (cur != null) {
                cur.close();
            }
        }
    }
    private final static MediaInfo [] sEmptyList = new MediaInfo[0];
    
    public static MediaInfo [] getSongListForCursor(Cursor cursor) {
        if (cursor == null) {
            return sEmptyList;
        }
        int len = cursor.getCount();
        MediaInfo [] list = new MediaInfo[len];
        cursor.moveToFirst();
        int colidx = -1;
        try {
            colidx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
        } catch (IllegalArgumentException ex) {
            colidx = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        }
        for (int i = 0; i < len; i++) {
        	list[i] = new MediaInfo( cursor.getLong(colidx), MediaInfo.MEDIA_TYPE_AUDIO );
            cursor.moveToNext();
        }
        return list;
    }

    public static MediaInfo [] getSongListForArtist(Context context, long id) {
        final String[] ccols = new String[] { BaseColumns._ID };
        String where = AudioColumns.ARTIST_ID + "=" + id + " AND " + 
        AudioColumns.IS_MUSIC + "=1";
        Cursor cursor = query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                ccols, where, null,
                AudioColumns.ALBUM_KEY + ","  + AudioColumns.TRACK);
        
        if (cursor != null) {
            MediaInfo [] list = getSongListForCursor(cursor);
            cursor.close();
            return list;
        }
        return sEmptyList;
    }    
    public static MediaInfo [] getSongListForAlbum(Context context, long id) {
        final String[] ccols = new String[] { BaseColumns._ID };
        String where = AudioColumns.ALBUM_ID + "=" + id + " AND " + 
                AudioColumns.IS_MUSIC + "=1";
        Cursor cursor = query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                ccols, where, null, AudioColumns.TRACK);

        if (cursor != null) {
            MediaInfo [] list = getSongListForCursor(cursor);
            cursor.close();
            return list;
        }
        return sEmptyList;
    }
    
    private static ContentValues[] sContentValuesCache = null;

    /**
     * @param ids The source array containing all the ids to be added to the playlist
     * @param offset Where in the 'ids' array we start reading
     * @param len How many items to copy during this pass
     * @param base The play order offset to use for this pass
     */
    private static void makeInsertItems(MediaInfo[] list, int offset, int len, int base) {
        // adjust 'len' if would extend beyond the end of the source array
        if (offset + len > list.length) {
            len = list.length - offset;
        }
        // allocate the ContentValues array, or reallocate if it is the wrong size
        if (sContentValuesCache == null || sContentValuesCache.length != len) {
            sContentValuesCache = new ContentValues[len];
        }
        // fill in the ContentValues array with the right values for this pass
        for (int i = 0; i < len; i++) {
            if (sContentValuesCache[i] == null) {
                sContentValuesCache[i] = new ContentValues();
            }

            sContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + offset + i);
            sContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, list[offset + i].getId());
        }
    }    
    public static void addToPlaylist(Context context, MediaInfo [] list, long playlistid) {
        if (list == null) {
            // this shouldn't happen (the menuitems shouldn't be visible
            // unless the selected item represents something playable
            LogWrapper.e("MusicBase", "ListSelection null");
        } else {
            int size = list.length;
            ContentResolver resolver = context.getContentResolver();
            // need to determine the number of items currently in the playlist,
            // so the play_order field can be maintained.
            String[] cols = new String[] {
                    "count(*)"
            };
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
            Cursor cur = resolver.query(uri, cols, null, null, null);
            cur.moveToFirst();
            int base = cur.getInt(0);
            cur.close();
            int numinserted = 0;
            for (int i = 0; i < size; i += 1000) {
                makeInsertItems(list, i, 1000, base);
                numinserted += resolver.bulkInsert(uri, sContentValuesCache);
            }
            String message = context.getResources().getQuantityString(
                    R.plurals.NNNtrackstoplaylist, numinserted, numinserted);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            //mLastPlaylistSelected = playlistid;
        }
    }
    public static void clearPlaylist(Context context, int plid) {
        
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", plid);
        context.getContentResolver().delete(uri, null, null);
        return;
    }    
    public static void deleteTracks(Context context, long [] list, int [] listType ) {
    	
    	
    	if( listType.length != list.length )
    	{
    		LogWrapper.e("delete tracks","listtype size != list size");
    		return;
    	}

    	ArrayList<Long> lstAudioId = new ArrayList<Long>();
    	ArrayList<Long> lstVideoId = new ArrayList<Long>();
    	for( int i=0; i<listType.length;++i)
    	{
    		if( listType[i] == MediaInfo.MEDIA_TYPE_AUDIO )
    		{
    			lstAudioId.add(list[i]);
    		}
    		else if( listType[i] == MediaInfo.MEDIA_TYPE_VIDEO )
    		{
    			lstVideoId.add(list[i]);    			
    		}
    	}
    	if( lstAudioId.isEmpty() == false )
    	{
	    	long[] listAudio = new long[lstAudioId.size()];
	    	for( int i=0; i<lstAudioId.size(); ++i )
	    	{
	    		listAudio[i] = lstAudioId.get(i);
	    	}
	    	deleteAudioTracks(context, listAudio);
    	}
    	
    	if( lstVideoId.isEmpty() == false )
    	{
	    	long[] listVideo = new long[lstVideoId.size()];
	    	for( int i=0; i<lstVideoId.size(); ++i )
	    	{
	    		listVideo[i] = lstVideoId.get(i);
	    	}
	    	// �r�f�I�̍폜
	    	deleteVideoTracks(context, listVideo);
    	}
    }
    public static void deleteAudioTracks(Context context, long [] list ) {
        
        String [] cols = new String [] { BaseColumns._ID, MediaColumns.DATA, AudioColumns.ALBUM_ID };
        StringBuilder where = new StringBuilder();
        where.append(BaseColumns._ID + " IN (");
        for (int i = 0; i < list.length; i++) {
            where.append(list[i]);
            if (i < list.length - 1) {
                where.append(",");
            }
        }
        where.append(")");
        Cursor c = query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cols,
                where.toString(), null, null);

        if (c != null) {

            // step 1: remove selected tracks from the current playlist, as well
            // as from the album art cache
            try {
                c.moveToFirst();
                while (! c.isAfterLast()) {
                    // remove from current playlist
                    long id = c.getLong(0);
                    MediaPlayerUtil.sService.removeTrack(id);
                    // remove from album art cache
                    long artIndex = c.getLong(2);
                    synchronized(MediaPlayerUtil.sArtCache) {
                    	MediaPlayerUtil.sArtCache.remove(artIndex);
                    }
                    c.moveToNext();
                }
            } catch (RemoteException ex) {
            }

            // step 2: remove selected tracks from the database
            context.getContentResolver().delete(
            		MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, where.toString(), null);

            // step 3: remove files from card
            c.moveToFirst();
            while (! c.isAfterLast()) {
                String name = c.getString(1);
                File f = new File(name);
                try {  // File.delete can throw a security exception
                    if (!f.delete()) {
                        // I'm not sure if we'd ever get here (deletion would
                        // have to fail, but no exception thrown)
                        LogWrapper.e("MusicUtils", "Failed to delete file " + name);
                    }
                    c.moveToNext();
                } catch (SecurityException ex) {
                    c.moveToNext();
                }
            }
            c.close();
        }

        String message = context.getResources().getQuantityString(
                R.plurals.NNNtracksdeleted, list.length, Integer.valueOf(list.length));
        
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        // We deleted a number of tracks, which could affect any number of things
        // in the media content domain, so update everything.
        context.getContentResolver().notifyChange(Uri.parse("content://media"), null);
    }
    public static void deleteVideoTracks(Context context, long [] list ) {
        
        String [] cols = new String [] { 
        		MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA };
        StringBuilder where = new StringBuilder();
        where.append(MediaStore.Video.Media._ID + " IN (");
        for (int i = 0; i < list.length; i++) {
            where.append(list[i]);
            if (i < list.length - 1) {
                where.append(",");
            }
        }
        where.append(")");
        Cursor c = query(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cols,
                where.toString(), null, null);

        if (c != null) {

            // step 1: remove selected tracks from the current playlist, as well
            // as from the album art cache
            try {
                c.moveToFirst();
                while (! c.isAfterLast()) {
                    // remove from current playlist
                    long id = c.getLong(0);
                    MediaPlayerUtil.sService.removeTrack(id);
//                    // remove from album art cache
//                    long artIndex = c.getLong(2);
//                    synchronized(MediaPlayerUtil.sArtCache) {
//                    	MediaPlayerUtil.sArtCache.remove(artIndex);
//                    }
                    c.moveToNext();
                }
            } catch (RemoteException ex) {
            }

            // step 2: remove selected tracks from the database
            context.getContentResolver().delete(
            		MediaStore.Video.Media.EXTERNAL_CONTENT_URI, where.toString(), null);

            // step 3: remove files from card
            c.moveToFirst();
            while (! c.isAfterLast()) {
                String name = c.getString(1);
                File f = new File(name);
                try {  // File.delete can throw a security exception
                    if (!f.delete()) {
                        // I'm not sure if we'd ever get here (deletion would
                        // have to fail, but no exception thrown)
                        LogWrapper.e("MusicUtils", "Failed to delete file " + name);
                    }
                    c.moveToNext();
                } catch (SecurityException ex) {
                    c.moveToNext();
                }
            }
            c.close();
        }

        String message = context.getResources().getQuantityString(
                R.plurals.NNNvideosdeleted, list.length, Integer.valueOf(list.length));
        
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        // We deleted a number of tracks, which could affect any number of things
        // in the media content domain, so update everything.
        context.getContentResolver().notifyChange(Uri.parse("content://media"), null);
    }

    public static MediaInfo [] getSongListForPlaylist(Context context, long plid) {
        final String[] ccols = new String[] { MediaStore.Audio.Playlists.Members.AUDIO_ID };
        Cursor cursor = query(context, MediaStore.Audio.Playlists.Members.getContentUri("external", plid),
                ccols, null, null, MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
        
        if (cursor != null) {
        	MediaInfo [] list = getSongListForCursor(cursor);
            cursor.close();
            return list;
        }
        return sEmptyList;
    }    
}
