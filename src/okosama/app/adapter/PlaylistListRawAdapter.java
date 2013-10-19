package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.adapter.PlaylistListAdapter.QueryHandler;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.PlaylistData;
import okosama.app.storage.Database;
// import okosama.app.storage.QueryHandler;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * AlbumListのアダプタ
 * @author 25689
 *
 */
public class PlaylistListRawAdapter extends ArrayAdapter<PlaylistData> {
    private static final long RECENTLY_ADDED_PLAYLIST = -1;
    //private static final long ALL_SONGS_PLAYLIST = -2;
    //private static final long PODCASTS_PLAYLIST = -3;
    
	boolean bDataUpdating = false;	// 内部データを更新中かどうか
	private LayoutInflater inflater;
	// private ArrayList<PlaylistData> items;
	private int iLayoutId;
	private ArrayList<PlaylistData> items = new ArrayList<PlaylistData>();
    private final Drawable mNowPlayingOverlay;
    private final BitmapDrawable mDefaultAlbumIcon;
    private OkosamaMediaPlayerActivity mActivity;
    //private final StringBuilder mStringBuilder = new StringBuilder();
    private final String mUnknownAlbum;
    private final String mUnknownArtist;
    private AsyncQueryHandler mQueryHandler;
    int mTitleIdx;
    int mIdIdx;
    // ショートカット作成フラグ？
    // TODO:どういうときに作成するのか不明
    // 下手したら、アクセサもメンバ変数もいらないかも
    boolean createShortcut;
    public boolean isCreateShortcut() {
		return createShortcut;
	}
	public void setCreateShortcut(boolean createShortcut) {
		this.createShortcut = createShortcut;
	}
    
    // Viewのホルダ？
    static class ViewHolder {
        TextView line1;
        TextView line2;
        ImageView play_indicator;
        ImageView icon;
    }

    /**
     * クエリハンドラ
     * 少しだけ、他のとは違うので注意する
     * @author 25689
     *
     */
    class QueryHandler extends AsyncQueryHandler {
        QueryHandler(ContentResolver res) {
            super(res);
        }
        
        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            //Log.i("@@@", "query complete: " + cursor.getCount() + "   " + mActivity);
            if (cursor != null) {
                cursor = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).mergedCursor(
                		cursor, createShortcut);
            }
            mActivity.initAdapter(token,cursor);
        }
    }
   
    /**
     * アダプタのコンストラクタ
     * @param currentactivity
     * @param layout
     * @param cursor
     * @param from
     * @param to
     */
    public PlaylistListRawAdapter( OkosamaMediaPlayerActivity currentactivity, 
            int layout, ArrayList<PlaylistData> items) {
        super(currentactivity, layout, items );
//    	for (PlaylistData data : items) {
//    	    add(data);
//    	}

        // this.items = items;
        this.iLayoutId = layout;
        this.inflater = (LayoutInflater) currentactivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // アクティビティの設定
        // クエリハンドラの作成
        //ctx = context;
        // mList = list;
        mActivity = currentactivity;
        //mQueryHandler = new QueryHandler(mActivity.getContentResolver(), mActivity);
        mQueryHandler = new QueryHandler(mActivity.getContentResolver());

        
        // albumとartistを表す文字列
        mUnknownAlbum = mActivity.getString(R.string.unknown_album_name);
        mUnknownArtist = mActivity.getString(R.string.unknown_artist_name);
                // リソースの取得
        // nowplayingのオーバーレイ？
        // mResources = mActivity.getResources();
        mNowPlayingOverlay = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.indicator_ic_mp_playing_list);

        // アルバムアイコンの作成？
        // TODO: ARGB4444を利用する
        Bitmap b = OkosamaMediaPlayerActivity.getResourceAccessor().createBitmapFromDrawableId( R.drawable.albumart_mp_unknown_list);
        mDefaultAlbumIcon = new BitmapDrawable(mActivity.getResources(), b);
        // no filter or dither, it's a lot faster and we can't tell the difference
        // Bitmapを使うDrawableに対し、回転／拡大／縮小のときにフィルタをかけるかどうか。Trueにするとキレイになるが遅い。
        mDefaultAlbumIcon.setFilterBitmap(false);
        // 色の少ない(8bit/色以下)デバイスに表示するときに、ディザをかけるかどうかを指定する。trueでディザする。遅い。
        mDefaultAlbumIcon.setDither(false);
        
        // カーソルが設定されていたら、各カラムのindexを内部に保持する
        // getColumnIndices(cursor);
    }
    /**
     * クエリハンドラの取得
     * @return
     */
    public AsyncQueryHandler getQueryHandler() {
        return mQueryHandler;
    }

    /**
     * 新しいビューの作成？
     */
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
    	View v = convertView;  
    	if (v == null) {
    	   ViewHolder vh = new ViewHolder();
	       v = inflater.inflate(iLayoutId, null); 
	       vh.line1 = (TextView) v.findViewById(R.id.line1);
	       vh.line2 = (TextView) v.findViewById(R.id.line2);
	       vh.play_indicator = (ImageView) v.findViewById(R.id.play_indicator);
	       vh.icon = (ImageView) v.findViewById(R.id.icon);
	       vh.icon.setBackgroundDrawable(mDefaultAlbumIcon);
	       vh.icon.setPadding(0, 0, 1, 0);
	       v.setTag(vh);
    	}
	    bindView(v,pos);
    	return v;
    }

    /**
     * ビューとデータを紐つける
     */
    //@Override
    public void bindView(View view, int pos) {
        
       	// タグからビューホルダーを取得
        ViewHolder vh = (ViewHolder) view.getTag();
        // positionからデータを取得
    	PlaylistData data = getItem(pos);
    	
    	if( data == null )
    	{
    		// データがないというのは、完全におかしい状態だが・・
    		 vh.line1.setText("");
    		 vh.line2.setText("");
    		 vh.icon.setImageDrawable(null);
    		 vh.play_indicator.setImageDrawable(null);
    		 return;
    	}
 
    	// タイトルの設定
       // TextView tv = (TextView) view.findViewById(R.id.line1);
        
        String name = data.getPlaylistName();
        vh.line1.setText(name);
        
        // idを取得？
        long id = data.getPlaylistId();
        
        // idの種類によって、アイコンの画像を変える
        ImageView iv = vh.icon;//(ImageView) view.findViewById(R.id.icon);
        if (id == RECENTLY_ADDED_PLAYLIST) {
        	// 最近追加されたもの
            iv.setImageResource(R.drawable.ic_mp_playlist_recently_added_list);
        } else {
        	// それ以外
            iv.setImageResource(R.drawable.ic_mp_playlist_list);
        }
        // アイコンのレイアウトをリセット？
        // TODO: 意味ないように感じる・・・
        ViewGroup.LayoutParams p = iv.getLayoutParams();
        p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 他は使わない
        iv = (ImageView) vh.play_indicator; //view.findViewById(R.id.play_indicator);
        iv.setVisibility(View.GONE);

        // view.findViewById(R.id.line2).setVisibility(View.GONE);
        vh.line2.setVisibility(View.GONE);
    }
    
    /**
     * データの変更？
     */
    // @Override
    public void updateData(ArrayList<PlaylistData> items) {
    	clear();
    	for (PlaylistData data : items) {
    	    add(data);
        	Log.i("updateData - add","id" + data.getPlaylistId() + " name:" + data.getPlaylistName() );    	    
    	}
    	notifyDataSetChanged();
    }
    
    public int insertAllDataFromCursor(Cursor cursor)
    {
    	if( bDataUpdating == true )
    	{
    		return -1;
    	}
    	bDataUpdating = true;
    	Log.i("insertAllDataFromCursor","start");
    	
//    	if (mActivity.isFinishing() && cursor != null ) {
//        	// アクティビティが終了中で、まだカーソルが残っている場合、カーソルをクローズ
//            cursor.close();
//            cursor = null;
//        }
        Database.getInstance(mActivity).setCursor( Database.AlbumCursorName, cursor );
            	
        AsyncTask<Cursor, Void, Integer> task = new AsyncTask<Cursor, Void, Integer>() {
            @Override
            protected Integer doInBackground(Cursor... params) {
            	Log.i("doInBackground","start");
            	items.clear();
            	
            	// カーソルをループする
            	Cursor cursor = params[0];
            	
        		if( 0 != getColumnIndices(cursor) )
        		{
        			return -1;
        		}
            	Log.i("doInBackground","moveToFirst");
        		cursor.moveToFirst();
        		do 
        		{
            		PlaylistData data = new PlaylistData();
        			// 全ての要素をループする
            		data.setPlaylistId(cursor.getLong(mIdIdx));
        			data.setPlaylistName(cursor.getString(mTitleIdx));
        			items.add(data);
        		} while( cursor.moveToNext() );
                return 0;
            }

            @Override
            protected void onPostExecute(Integer ret) 
            {
            	Log.i("onPostExecute","ret=" + ret );
            	
            	// 格納終了
            	// 二重管理になってしまっているが、アダプタにも同様のデータを格納する
            	updateData( items );
            	bDataUpdating = false;            	
            }
        };
        if( cursor != null && 0 < cursor.getCount() 
        && cursor.isClosed() == false )
        {
        	task.execute(cursor);
        }
        return 0;
    }
    private int getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	try {
	        	// カーソルがnullでなければ
	        	// カーソルから、各カラムのindexを取得し、メンバ変数に格納する
                mTitleIdx = cursor.getColumnIndexOrThrow(PlaylistsColumns.NAME);
                mIdIdx = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        	} catch( IllegalArgumentException ex ) {
        		return -1;
        	}
            return 0;
        }
        return -1;
    }
    
 }
