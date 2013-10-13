package okosama.app.adapter;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.ResourceAccessor;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.Database;
import okosama.app.storage.TrackQueryHandler;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * トラックのリストのアダプタ
 * @author 25689
 *
 */
public class TrackListAdapter extends SimpleCursorAdapter implements SectionIndexer {

    // カラムのインデックス保持用 
    int mTitleIdx;
    int mArtistIdx;
    int mDurationIdx;
    int mAudioIdIdx;
    
    // 外部からの設定値保持用
	// TODO: 意味の調査
    boolean mIsNowPlaying;
    boolean mDisableNowPlayingIndicator;
    private String genre;
    private String albumId;
    private String artistId;

    private final StringBuilder mBuilder = new StringBuilder();
    private final String mUnknownArtist;
    
    private AlphabetIndexer mIndexer;
    
    private OkosamaMediaPlayerActivity mActivity = null;
    private TrackQueryHandler mQueryHandler;
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;
    
    // ビュー保持用クラス
    static class ViewHolder {
        TextView line1;
        TextView line2;
        TextView duration;
        ImageView play_indicator;
        CharArrayBuffer buffer1;
        char [] buffer2;
    }

//    class TrackQueryHandler extends AsyncQueryHandler {
//
//        class QueryArgs {
//            public Uri uri;
//            public String [] projection;
//            public String selection;
//            public String [] selectionArgs;
//            public String orderBy;
//        }
//
//        TrackQueryHandler(ContentResolver res) {
//            super(res);
//        }
//        
//        public Cursor doQuery(Uri uri, String[] projection,
//                String selection, String[] selectionArgs,
//                String orderBy, boolean async) {
//            if (async) {
//                // Get 100 results first, which is enough to allow the user to start scrolling,
//                // while still being very fast.
//                Uri limituri = uri.buildUpon().appendQueryParameter("limit", "100").build();
//                QueryArgs args = new QueryArgs();
//                args.uri = uri;
//                args.projection = projection;
//                args.selection = selection;
//                args.selectionArgs = selectionArgs;
//                args.orderBy = orderBy;
//
//                startQuery(0, args, limituri, projection, selection, selectionArgs, orderBy);
//                return null;
//            }
//            return Database.query(mActivity,
//                    uri, projection, selection, selectionArgs, orderBy);
//        }
//
//        @Override
//        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//            //Log.i("@@@", "query complete: " + cursor.getCount() + "   " + mActivity);
//            mActivity.initAdapter(cursor, cookie != null);
//            if (token == 0 && cookie != null && cursor != null && cursor.getCount() >= 100) {
//                QueryArgs args = (QueryArgs) cookie;
//                startQuery(1, null, args.uri, args.projection, args.selection,
//                        args.selectionArgs, args.orderBy);
//            }
//        }
//    }
    
    public TrackListAdapter( OkosamaMediaPlayerActivity currentactivity,
            int layout, Cursor cursor, String[] from, int[] to,
            boolean isnowplaying, boolean disablenowplayingindicator,String genre_,
            String albumId_, String artistId_ ) {
        super(currentactivity, layout, cursor, from, to);
        mActivity = currentactivity;
        getColumnIndices(cursor);
        mIsNowPlaying = isnowplaying;
        mDisableNowPlayingIndicator = disablenowplayingindicator;
        mUnknownArtist = currentactivity.getString(R.string.unknown_artist_name);
        //mUnknownAlbum = context.getString(R.string.unknown_album_name);
        genre = genre_;
        albumId = albumId_;
        artistId = artistId_;
        
        mQueryHandler = new TrackQueryHandler(currentactivity.getContentResolver());
    }
    
//    public void setActivity(OkosamaMediaPlayerActivity newactivity) {
//        mActivity = newactivity;
//    }
    
    public TrackQueryHandler getQueryHandler() {
        return mQueryHandler;
    }
    
    /**
     * カラムのインデックスを設定
     * @param cursor
     */
    private void getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// 各カラムのインデックスを設定
        	// タイトル、アーティスト、時間
            mTitleIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            mArtistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            mDurationIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            try {
            	// オーディオ？まず、プレイリストのカラムidから取得を試み、失敗したらトラックのカラムidから取得するらしい
                mAudioIdIdx = cursor.getColumnIndexOrThrow(
                        MediaStore.Audio.Playlists.Members.AUDIO_ID);
            } catch (IllegalArgumentException ex) {
                mAudioIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            }
            
            // インデクサの設定
            if (mIndexer != null) {
                mIndexer.setCursor(cursor);
            } else if (!mActivity.isEditMode()) {
                String alpha = mActivity.getString(R.string.fast_scroll_alphabet);
            
                mIndexer = new MusicAlphabetIndexer(cursor, mTitleIdx, alpha);
            }
        }
    }

    /**
     * 新しいビューの設定
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = super.newView(context, cursor, parent);
        // イメージは、エディットモードの時のみ表示する
        ImageView iv = (ImageView) v.findViewById(R.id.icon);
        if (mActivity.isEditMode()) {
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(R.drawable.ic_mp_move);
        } else {
            iv.setVisibility(View.GONE);
        }
        
        // viewholderに各ビューを設定し、タグに設定する
        ViewHolder vh = new ViewHolder();
        vh.line1 = (TextView) v.findViewById(R.id.line1);
        vh.line2 = (TextView) v.findViewById(R.id.line2);
        vh.duration = (TextView) v.findViewById(R.id.duration);
        vh.play_indicator = (ImageView) v.findViewById(R.id.play_indicator);
        vh.buffer1 = new CharArrayBuffer(100);
        vh.buffer2 = new char[200];
        v.setTag(vh);
        return v;
    }

    /**
     * ビューへの値を設定
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        
    	// ビューホルダーから各ビューを取得
        ViewHolder vh = (ViewHolder) view.getTag();
        
        // バッファに、タイトルの文字列を一度取得後、ビューに設定？TODO:なぜ？
        cursor.copyStringToBuffer(mTitleIdx, vh.buffer1);
        vh.line1.setText(vh.buffer1.data, 0, vh.buffer1.sizeCopied);
        
        // 時間を取得、設定
        int secs = cursor.getInt(mDurationIdx) / 1000;
        if (secs == 0) {
            vh.duration.setText("");
        } else {
            vh.duration.setText(ResourceAccessor.makeTimeString(context, secs));
        }
        
        final StringBuilder builder = mBuilder;
        builder.delete(0, builder.length());

        // アーティスト名が取得できたら、ビューに設定
        String name = cursor.getString(mArtistIdx);
        if (name == null || name.equals(MediaStore.UNKNOWN_STRING)) {
            builder.append(mUnknownArtist);
        } else {
            builder.append(name);
        }
        int len = builder.length();
        if (vh.buffer2.length < len) {
            vh.buffer2 = new char[len];
        }
        builder.getChars(0, len, vh.buffer2, 0);
        vh.line2.setText(vh.buffer2, 0, len);

        // 
        ImageView iv = vh.play_indicator;
        // サービスがあり、再生中ならば、idにキューの位置を
        // 再生中でなければ、idにオーディオid
        long id = -1;
        if (MediaPlayerUtil.sService != null) {
            // TODO: IPC call on each bind??
            try {
                if (mIsNowPlaying) {
                    id = MediaPlayerUtil.sService.getQueuePosition();
                } else {
                    id = MediaPlayerUtil.sService.getAudioId();
                }
            } catch (RemoteException ex) {
            }
        }
        
        // Determining whether and where to show the "now playing indicator
        // is tricky, because we don't actually keep track of where the songs
        // in the current playlist came from after they've started playing.
        //
        // If the "current playlists" is shown, then we can simply match by position,
        // otherwise, we need to match by id. Match-by-id gets a little weird if
        // a song appears in a playlist more than once, and you're in edit-playlist
        // mode. In that case, both items will have the "now playing" indicator.
        // For this reason, we don't show the play indicator at all when in edit
        // playlist mode (except when you're viewing the "current playlist",
        // which is not really a playlist)
        if ( (mIsNowPlaying && cursor.getPosition() == id) ||
             (!mIsNowPlaying && !mDisableNowPlayingIndicator && cursor.getLong(mAudioIdIdx) == id)) {
        	// 再生中で再生中のものか、
        	// 再生中でなく、NowPlayingIndicatorを表示しない設定でもなく、現在設定使用としているのが待機中の曲と同じなら
        	// プレイリストのイメージを設定？
        	// TODO:プレイリストのイメージというのがどういうものか分からないので、確認する
            iv.setImageResource(R.drawable.indicator_ic_mp_playing_list);
            iv.setVisibility(View.VISIBLE);
        } else {
        	// プレイリストのイメージを非表示に？
            iv.setVisibility(View.GONE);
        }
    }
    
    /**
     * カーソルを変更する
     */
    @Override
    public void changeCursor(Cursor cursor) {
        if (mActivity.isFinishing() && cursor != null) {
        	// 再生中ならば、カーソルをクロース
            cursor.close();
            cursor = null;
        }
        //if (false == isEqualCursor( cursor, Database.getInstance(mActivity).getCursor(Database.SongCursorName))) {
        	// カーソルが変更されていたら、カーソルをリセットする
        	Database.getInstance(mActivity).setCursor(Database.SongCursorName, cursor);
            getColumnIndices(cursor);
        //}
        super.changeCursor(cursor);
    }
    /**
     * カーソルの内容が一致するかどうか調べる この場合、数と_ID列のみ確認
     * @param c1
     * @param c2
     * @return
     */
    boolean isEqualCursor( Cursor c1, Cursor c2 )
    {
    	boolean bRet = false;
    	
    	if( c1 == null || c2 == null
    	|| c1.getCount() != c2.getCount() )
    	{
    		// どちらかがnullの場合は、判定不可だが、falseとする
    		// 数が違う場合
    		return false;
    	}
    	if(c1.moveToFirst()){
    		c2.moveToFirst();
    		do{
				long id1 = c1.getLong(c1.getColumnIndex("_ID"));
				long id2 = c2.getLong(c2.getColumnIndex("_ID"));
				if( id1 != id2 )
				{
					return false;
				}
				
			}while(c1.moveToNext() && c2.moveToNext());
    	}
    	
    	bRet = true;
    	return bRet;
    }
    
    /**
     * 背景でクエリを発行するとき
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String s = constraint.toString();
        if (mConstraintIsValid && (
                (s == null && mConstraint == null) ||
                (s != null && s.equals(mConstraint)))) {
        	// フィルタが変わっていなければ、現在のカーソルを取得
            return getCursor();
        }
        //String playlist = null;
        if( mIsNowPlaying )
        {
        	OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistName( Database.PlaylistName_NowPlaying );
        }
        else
        {
        	OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistName( null );        	
        }
        Cursor c = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).createTrackCursor(mQueryHandler, s, false );//,genre, albumId, artistId );
        mConstraint = s;
        mConstraintIsValid = true;
        return c;
    }
    
    // SectionIndexer methods
    
    public Object[] getSections() {
        if (mIndexer != null) { 
            return mIndexer.getSections();
        } else {
            return null;
        }
    }
    
    public int getPositionForSection(int section) {
        int pos = mIndexer.getPositionForSection(section);
        return pos;
    }
    
    public int getSectionForPosition(int position) {
        return 0;
    }        
}
