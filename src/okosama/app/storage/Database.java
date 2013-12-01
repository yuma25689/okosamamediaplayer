package okosama.app.storage;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;

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
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.SubMenu;
import android.widget.Toast;

/**
 * データベースクラス
 * 今のところ、データベースリソース用のメンバやstatic関数を適当に突っ込んであるだけ
 * いろんな関数を突っ込んだただの便利クラスになってしまっているので、Singletonにしておく
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
	 * シングルトンのインスタンス取得
	 * 同時に、必ずContextを設定させるために、引数にContextを取らせる
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
	
	// Cursors保存用
	// 2013/11/02 del ->
	// カーソルの保持を削除
//	public static final String AlbumCursorName ="AlbumCursor";
//	public static final String ArtistCursorName ="ArtistCursor";
//	public static final String SongCursorName ="SongCursor";
//	public static final String PlaylistCursorName ="PlaylistCursor";
//	HashMap<String,Cursor> CursorMap = new HashMap<String,Cursor>();
	// 2013/11/02 del <-
	
	
	// playlist定数
	public static final String PlaylistName_NowPlaying = "nowplaying";
	public static final String PlaylistName_Podcasts = "podcasts";
	public static final String PlaylistName_RecentlyAdded = "recentlyadded";

	// 2013/11/02 del ->	
//	/**
//	 * 指定された名前のカーソルを返却する
//	 * @param cursorName
//	 * @return
//	 */
//	public Cursor getCursor(String cursorName)
//	{
//		if( CursorMap.containsKey(cursorName) == false)
//		{
//			return null;
//		}
//		return CursorMap.get(cursorName);
//	}
	/**
	 * 指定された名前のカーソルを設定する
	 * @param cursorName
	 * @param cursor
	 */
//	public void setCursor(String cursorName, Cursor cursor )
//	{
//		if( CursorMap.containsKey(cursorName) == true )
//		{
//			if( cursor == CursorMap.get(cursorName) )
//			{
//				return;
//			}
//			if( CursorMap.get(cursorName) != null 
//			&& false == CursorMap.get(cursorName).isClosed() )
//			{
//				CursorMap.get(cursorName).close();
//			}
//		}		
//		CursorMap.put( cursorName, cursor );
//	}
//	/**
//	 * 全てのカーソルをクリアする
//	 */
//	public void clearCursor()
//	{
//		for( Map.Entry<String, Cursor> e : CursorMap.entrySet() )
//		{
//			if( false == e.getValue().isClosed() )
//			{
//				e.getValue().close();
//			}
//		}
//		CursorMap.clear();
//	}
	// 2013/11/02 del <-	
	
	/**
	 * 同期でクエリを発行する
	 * @param context
	 * @param uri コンテントプロバイダ？
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @param limit maxの件数？
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
        	Log.e("queryerror",msg);
            return null;
        }
    }
    public static Cursor query(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {
        return query(context, uri, projection, selection, selectionArgs, sortOrder, 0);
    }	

    
    ///////// ここから汎用性のない関数 //////////////////////////////
    /**
     * アーティストカーソルの作成
     * @param async
     * @param filter
     * @return
     */
    public Cursor createArtistCursor() {//AsyncQueryHandler async, String filter) {

    	// アーティスト名が空でない、という条件を付け加える
        StringBuilder where = new StringBuilder();
//        where.append(ArtistColumns.ARTIST + " != ''");
        String whereclause = where.toString();
        
        // アーティストのコンテントプロバイダのuriを設定
        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        //String external_string;
        if( get_external == false )
        {
        	uri = MediaStore.Audio.Artists.INTERNAL_CONTENT_URI;
        }
        
        // Add in the filtering constraints
        // 引数で指定されたfilter用の単語で、SQLのwhere句を指定？
        // アーティスト名の一部の配列
        String [] keywords = null;
//        if (filter != null) {
//        	// 半角スペースでsplit
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
//                where.append(AudioColumns.ARTIST_KEY + " LIKE ? ESCAPE '\\'");
//            }
//        }
        
        // カラムの設定
        String[] cols = new String[] {
                BaseColumns._ID,
                ArtistColumns.ARTIST,
                ArtistColumns.NUMBER_OF_ALBUMS,
                ArtistColumns.NUMBER_OF_TRACKS
        };
        Cursor ret = null;
//        if (async != null) {
//        	// 非同期ならば、非同期でクエリ発行
//        	// Log.d("query uri", "uri :" + uri);
//            async.startQuery(TabPage.TABPAGE_ID_ARTIST, null, uri,
//                    cols, whereclause , keywords, ArtistColumns.ARTIST_KEY);
//        } else {
        	// 同期ならば、同期でクエリ発行
            ret = query(ctx, uri,
                    cols, whereclause, keywords, ArtistColumns.ARTIST_KEY);
        //}
        // 得られたカーソルを返却
        return ret;	
    }
    
    /**
     * アルバムカーソルの作成
     * @param async
     * @param filter
     * @param artistId
     * @return
     */
    public Cursor createAlbumCursor() { //AsyncQueryHandler async, String filter ) { // , String artistId) {
    	String artistId = OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getArtistID();
    	// where句を設定する
        StringBuilder where = new StringBuilder();
//        where.append(AlbumColumns.ALBUM + "!=''");
        String whereclause = where.toString();
        
        // アルバムのコンテントプロバイダのuriを設定
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String external_string = "external";
        if( get_external == false )
        {
        	uri = MediaStore.Audio.Albums.INTERNAL_CONTENT_URI;
        	external_string = "internal";	// 多分、これでよい
        }
        
        // フィルタの設定
        // これは、アーティストかアルバムどちらでもよい
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
        
        // 取得カラムの設定
        String[] cols = new String[] {
                BaseColumns._ID,
                AlbumColumns.ARTIST,
                AlbumColumns.ALBUM,
                AlbumColumns.ALBUM_ART
        };
        
        // クエリを発行するが、artistIDがnullかどうかによって、クエリを変更する
        Cursor ret = null;
        if (artistId != null) {
        	// artistIDが入力されている場合
        	// uriに、artistを含める
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
        	// artistIdがnull
        	// uriは、albumを利用する
//            if (async != null) {
//            	Log.i("uri:", uri.toString());
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
    // プレイリストカーソル作成用の定数
    // 取得するカラム
    private static final String[] playlistCols = new String[] {
            MediaStore.Audio.Playlists._ID,
            MediaStore.Audio.Playlists.NAME
    };    	
    private static final long RECENTLY_ADDED_PLAYLIST = -1;
    private static final long ALL_SONGS_PLAYLIST = -2;
    private static final long PODCASTS_PLAYLIST = -3;
    
    /**
     * カーソルのラッパーを作成？
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
        // ２次元表の使えるカーソル
        MatrixCursor autoplaylistscursor = new MatrixCursor(playlistCols);
        if (createShortCut) {
        	// ショートカットを作る場合？
        	// 全て？
            ArrayList<Object> all = new ArrayList<Object>(2);
            all.add(ALL_SONGS_PLAYLIST);
            all.add(ctx.getString(R.string.play_all));
            autoplaylistscursor.addRow(all);
        }
        // 最近追加されたもの？
        ArrayList<Object> recent = new ArrayList<Object>(2);
        recent.add(RECENTLY_ADDED_PLAYLIST);
        recent.add(ctx.getString(R.string.recentlyadded));
        autoplaylistscursor.addRow(recent);
        
        // アーティストのコンテントプロバイダをuriに設定？
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //String external_string;
        if( get_external == false )
        {
        	uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        }
        // Podcastのものの件数を取得？
        // check if there are any podcasts
        Cursor counter = query(ctx, uri,//MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {"count(*)"}, "is_podcast=1", null, null);
        if (counter != null) {
        	// 件数が取得できたら？
            counter.moveToFirst();
            // 件数を保持
            int numpodcasts = counter.getInt(0);
            counter.close();
            if (numpodcasts > 0) {
            	// Podcastがあれば
            	// Podcastのショートカット作成？
                ArrayList<Object> podcasts = new ArrayList<Object>(2);
                podcasts.add(PODCASTS_PLAYLIST);
                podcasts.add(ctx.getString(R.string.podcasts_listitem));
                autoplaylistscursor.addRow(podcasts);
            }
        }

        // 元のカーソルと、今作ったやつをマージしたカーソルを作る？
        Cursor cc = new MergeCursor(new Cursor [] {autoplaylistscursor, c});
        return cc;
    }
    
    /**
     * プレイリストのカーソルを作成
     * @param async
     * @param filterstring
     * @return
     */
    public Cursor createPlaylistCursor(AsyncQueryHandler async, String filterstring, boolean createShortCut) {

    	// プレイリスト名が空でないものという条件にする
        StringBuilder where = new StringBuilder();
        where.append(PlaylistsColumns.NAME + " != ''");
        String whereclause = where.toString();
               
        // フィルタの設定
        // プレイリスト名の一致条件
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
        
        // プレイリストのコンテントプロバイダのuriを設定する
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        //String external_string;
        if( get_external == false )
        {
        	uri = MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI;
        }         
        
        if (async != null) {
        	// 非同期の場合
        	// TODO: 受け先でマージカーソルにする必要有り
            async.startQuery(TabPage.TABPAGE_ID_PLAYLIST, null, uri,
            		playlistCols, whereclause, keywords, PlaylistsColumns.NAME);
            return null;
        }
        Cursor c = null;
        c = query(ctx, uri,//MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
        		playlistCols, whereclause, keywords, PlaylistsColumns.NAME);
        
        // 最後に、ショートカットを付け加えたカーソルを返却する？
        return c;//mergedCursor(c, createShortCut);
    }
    /**
     * ビデオカーソルの作成
     * @return
     */
    public Cursor createVideoCursor() { //AsyncQueryHandler async, String filter ) { // , String artistId) {
    	// ALL_SONGS_PLAYLIST String artistId = OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getArtistID();
    	// where句を設定する
        StringBuilder where = new StringBuilder();
        where.append(MediaStore.Video.Media.TITLE + " != '' ");
        String whereclause = where.toString();
        
        // コンテントプロバイダのuriを設定
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String external_string = "external";
        if( get_external == false )
        {
        	uri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
        	external_string = "internal";	// 多分、これでよい
        }
        
        // フィルタの設定
        // これは、アーティストかアルバムどちらでもよい
        // Add in the filtering constraints
        String [] keywords = null;
        
        // 取得カラムの設定
        String[] cols = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.ARTIST
        };
        
        // クエリを発行するが、artistIDがnullかどうかによって、クエリを変更する
        Cursor ret = null;
            ret = query(ctx, uri,
                    cols, whereclause, keywords, MediaStore.Video.Media.TITLE + " COLLATE UNICODE");
        return ret;
    }
    
    // トラックカーソル用メンバ変数
    // ソート順
    private static String mSortOrder;
    // カラム
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
    // プレイリストのカラム？
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
     * トラックカーソルの作成
     * @param queryhandler
     * @param filter
     * @param async
     * @return
     */
    public Cursor createTrackCursor(AsyncQueryHandler async, String filter //String playlist, String filter,
        ) 
    {
        Cursor ret = null;
        // ソート条件に、タイトルを設定
        mSortOrder = AudioColumns.TITLE_KEY;
        // タイトルが空でないものを条件に
        StringBuilder where = new StringBuilder();
        where.append(MediaColumns.TITLE + " != ''");

        // フィルタを設定
        // この場合、アーティストとトラック？
        // Add in the filtering constraints
        String [] keywords = null;
        
        // トラックのコンテントプロバイダのuriを設定
    	Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	String strExOrIn = "external";
        if( get_external == false )
        {
        	uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        	strExOrIn = "internal";
        }        	
        
        	mSortOrder = AudioColumns.TRACK + ", " + mSortOrder;
            // 音楽指定
            where.append(" AND " + AudioColumns.IS_MUSIC + "=1");
            // クエリ発行
            // Log.i("query1","query1");
//            ret = queryhandler.doQuery(ctx, uri,//MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//            		mPlaylistCols, where.toString() , keywords, mSortOrder, async);
        //}
            if (async != null) {
            	Log.i("uri:", uri.toString());
            	//async.startQuery(TabPage.TABPAGE_ID_ALBUM, null, uri, null, null, null, null );
                async.startQuery(TabPage.TABPAGE_ID_SONG, null, uri,
                		mPlaylistCols, where.toString(), keywords, mSortOrder);
            } else {
                ret = query(ctx, uri,
                		mPlaylistCols, where.toString(), keywords, mSortOrder);
            }
            //return ret;

        // 非同期でも、nowplayingの場合はメッセージが飛んでこないので、そこで初期化できない。
        // それに対応
        // This special case is for the "nowplaying" cursor, which cannot be handled
        // asynchronously using AsyncQueryHandler, so we do some extra initialization here.
        //if (ret != null && async) {
        	// TODO:実装
            // ctx.initAdapter(TabPage.TABPAGE_ID_SONG,ret);
            //setTitle();
        //}
        return ret;
    }
    /**
     * nowplayingカーソルクラス？
     * @author 25689
     *
     */
    public class NowPlayingCursor extends AbstractCursor
    {
        public NowPlayingCursor(IMediaPlaybackService service, String [] cols)
        {
        	// カラム、サービスを格納、カーソルの作成
            mCols = cols;
            mService  = service;
            makeNowPlayingCursor();
        }
        /**
         * 現在プレイリストのカーソルを内部に作成する？
         */
        private void makeNowPlayingCursor() {
        	// 初期化？
            mCurrentPlaylistCursor = null;
            try {
            	// 現在のプレイリスト取得？
                mNowPlaying = mService.getQueue();
            } catch (RemoteException ex) {
                mNowPlaying = new long[0];
            }
            mSize = mNowPlaying.length;
            if (mSize == 0) {
                return;
            }

            // where句の作成
            StringBuilder where = new StringBuilder();
            // IDが、nowplayingの中にあるもの
            where.append(BaseColumns._ID + " IN (");
            for (int i = 0; i < mSize; i++) {
                where.append(mNowPlaying[i]);
                if (i < mSize - 1) {
                    where.append(",");
                }
            }
            where.append(")");

            // uriの設定
            // トラックのコンテントプロバイダ
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            //String external_string;
            if( get_external == false )
            {
            	uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
            }
            // クエリ発行
            mCurrentPlaylistCursor = query(ctx,
                    uri,//MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    mCols, where.toString(), null, BaseColumns._ID);

            if (mCurrentPlaylistCursor == null) {
            	// カーソル取得失敗
                mSize = 0;
                return;
            }
            
            // オーディオのIDを取得する？
            // なんとなく、元のものと同じようなものが取れそうに見えるが・・・
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
            
            // どうやら、ここで元のものと、データベースからの取得値の差分をチェックし、ベリファイとフィックスを行う
            // At this point we can verify the 'now playing' list we got
            // earlier to make sure that all the items in there still exist
            // in the database, and remove those that aren't. This way we
            // don't get any blank items in the list.
            try {
            	// リムーブされたトラックを調べる
                int removed = 0;
                for (int i = mNowPlaying.length - 1; i >= 0; i--) {
                    long trackid = mNowPlaying[i];
                    int crsridx = Arrays.binarySearch(mCursorIdxs, trackid);
                    if (crsridx < 0) {
                        Log.i("@@@@@", "item no longer exists in db: " + trackid);
                        removed += mService.removeTrack(trackid);
                    }
                }
                if (removed > 0) {
                	// 再設定
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
         * 現在曲の移動？
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
         * 項目の削除？
         * @param which
         * @return
         */
        public boolean removeItem(int which)
        {
            try {
            	// 指定トラックを削除？
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
         * キューの項目を移動する
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
         * プレイリストの全てのidをログ出力？
         */
        private void dump() {
            String where = "(";
            for (int i = 0; i < mSize; i++) {
                where += mNowPlaying[i];
                if (i < mSize - 1) {
                    where += ",";
                }
            }
            where += ")";
            Log.i("NowPlayingCursor: ", where);
        }

        /**
         * 現在行の指定カラムの文字列取得？
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
         * 現在行の指定カラムのshort取得？
         */
        @Override
        public short getShort(int column)
        {
            return mCurrentPlaylistCursor.getShort(column);
        }

        /**
         * 現在行の指定カラムのint取得？
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
         * 現在行の指定カラムのlong取得？
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
         * 現在行の指定カラムのfloat取得？
         */
        @Override
        public float getFloat(int column)
        {
            return mCurrentPlaylistCursor.getFloat(column);
        }

        /**
         * 現在行の指定カラムのdouble取得？
         */
        @Override
        public double getDouble(int column)
        {
            return mCurrentPlaylistCursor.getDouble(column);
        }

        /**
         * 現在行の指定カラムのisnull取得？
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
         * 再作成
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
            list[i].setId( cursor.getLong(colidx) );
            list[i].setMediaType( MediaInfo.MEDIA_TYPE_AUDIO );
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
            Log.e("MusicBase", "ListSelection null");
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
    public static void deleteTracks(Context context, long [] list) {
        
        String [] cols = new String [] { BaseColumns._ID, 
                MediaColumns.DATA, AudioColumns.ALBUM_ID };
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
            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, where.toString(), null);

            // step 3: remove files from card
            c.moveToFirst();
            while (! c.isAfterLast()) {
                String name = c.getString(1);
                File f = new File(name);
                try {  // File.delete can throw a security exception
                    if (!f.delete()) {
                        // I'm not sure if we'd ever get here (deletion would
                        // have to fail, but no exception thrown)
                        Log.e("MusicUtils", "Failed to delete file " + name);
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
