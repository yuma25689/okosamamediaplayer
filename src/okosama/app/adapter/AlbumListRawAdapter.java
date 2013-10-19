package okosama.app.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.AlbumData;
import okosama.app.storage.Database;
import okosama.app.storage.QueryHandler;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.util.Log;
import android.util.SparseArray;
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
public class AlbumListRawAdapter extends ArrayAdapter<AlbumData> {
    
	SparseArray<String> mapIdAndArt = new SparseArray<String>();
	public String getAlbumArtFromId(int id)
	{
		if( mapIdAndArt.indexOfKey(id) == -1 )
		{
			return null;
		}
		return mapIdAndArt.get(id);
	}
	boolean bDataUpdating = false;	// 内部データを更新中かどうか
	private LayoutInflater inflater;
	// private ArrayList<AlbumData> items;
	private int iLayoutId;
	private ArrayList<AlbumData> items = new ArrayList<AlbumData>();
    private final Drawable mNowPlayingOverlay;
    private final BitmapDrawable mDefaultAlbumIcon;
    private OkosamaMediaPlayerActivity mActivity;
    //private final StringBuilder mStringBuilder = new StringBuilder();
    private final String mUnknownAlbum;
    private final String mUnknownArtist;
    private AsyncQueryHandler mQueryHandler;
    private int mAlbumIdx;
    private int mArtistIdx;
    private int mAlbumArtIndex;
    
    // Viewのホルダ？
    static class ViewHolder {
        TextView line1;
        TextView line2;
        ImageView play_indicator;
        ImageView icon;
    }

    /**
     * アダプタのコンストラクタ
     * @param currentactivity
     * @param layout
     * @param cursor
     * @param from
     * @param to
     */
    public AlbumListRawAdapter( OkosamaMediaPlayerActivity currentactivity, 
            int layout, ArrayList<AlbumData> items) {
        super(currentactivity, layout, items );

        this.iLayoutId = layout;
        this.inflater 
        = (LayoutInflater) currentactivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // アクティビティの設定
        // クエリハンドラの作成
        mActivity = currentactivity;
        mQueryHandler = new QueryHandler(mActivity.getContentResolver(), mActivity);

        // albumとartistを表す文字列
        mUnknownAlbum = mActivity.getString(R.string.unknown_album_name);
        mUnknownArtist = mActivity.getString(R.string.unknown_artist_name);
        // リソースの取得
        // nowplayingのオーバーレイ？
        // mResources = mActivity.getResources();
        mNowPlayingOverlay 
        = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.indicator_ic_mp_playing_list);

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
    	AlbumData data = getItem(pos);
    	
    	if( data == null )
    	{
    		// データがないというのは、完全におかしい状態だが・・
    		 vh.line1.setText("");
    		 vh.line2.setText("");
    		 vh.icon.setImageDrawable(null);
    		 vh.play_indicator.setImageDrawable(null);
    		 return;
    	}
 
        // アルバム名を取得、ビューに設定
        String name = data.getAlbumName();
        String displayname = name;
        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
        if (unknown) {
            displayname = mUnknownAlbum;
        }
        vh.line1.setText(displayname);
        
        // アーティスト名を取得、ビューに設定
        name = data.getAlbumArtist();//cursor.getString(mArtistIdx);
        displayname = name;
        if (name == null || name.equals(MediaStore.UNKNOWN_STRING)) {
            displayname = mUnknownArtist;
        }
        vh.line2.setText(displayname);

        // アイコンに、アルバムアートを設定する？
        ImageView iv = vh.icon;
        // We don't actually need the path to the thumbnail file,
        // we just use it to see if there is album art or not
        String art = data.getAlbumArt();//cursor.getString(mAlbumArtIndex);
        long aid = data.getAlbumId();//cursor.getLong(0);
        if (unknown || art == null || art.length() == 0) {
            iv.setImageDrawable(null);
        } else {
            Drawable d = MediaPlayerUtil.getCachedArtwork(mActivity, aid, mDefaultAlbumIcon);
            iv.setImageDrawable(d);
        }
        
        // 再生中のオーバレイとなる画像を、アイコンの上に重ねる・・・のかな？
        long currentalbumid = MediaPlayerUtil.getCurrentAlbumId();
        iv = vh.play_indicator;
        if (currentalbumid == aid) {
            iv.setImageDrawable(mNowPlayingOverlay);
        } else {
            iv.setImageDrawable(null);
        }
    }
    
    /**
     * データの変更？
     */
    // @Override
    public void updateData(ArrayList<AlbumData> items) {
    	clear();
    	mapIdAndArt.clear();
    	for (AlbumData data : items) {
    	    add(data);
    	    mapIdAndArt.put(data.getAlbumId(), data.getAlbumArt());
        	Log.i("updateData - add","data" + data.getAlbumId() + " name:" + data.getAlbumName() );    	    
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
            		AlbumData data = new AlbumData();
        			// 全ての要素をループする
            		data.setAlbumId(cursor.getInt(0));
        			data.setAlbumName(cursor.getString(mAlbumIdx));
        			data.setAlbumArtist(cursor.getString(mArtistIdx));
        			data.setAlbumArt(cursor.getString(mAlbumArtIndex));
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
	            mAlbumIdx = cursor.getColumnIndexOrThrow(AlbumColumns.ALBUM);
	            mArtistIdx = cursor.getColumnIndexOrThrow(AlbumColumns.ARTIST);
	            mAlbumArtIndex = cursor.getColumnIndexOrThrow(AlbumColumns.ALBUM_ART);
        	} catch( IllegalArgumentException ex ) {
        		return -1;
        	}
            return 0;
        }
        return -1;
    }
    
 }
