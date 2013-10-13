package okosama.app.adapter;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.Database;
import okosama.app.storage.QueryHandler;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * AlbumListのアダプタ
 * @author 25689
 *
 */
public class AlbumListAdapter extends SimpleCursorAdapter implements SectionIndexer {
    
	private OkosamaMediaPlayerActivity ctx = null;
    private final Drawable mNowPlayingOverlay;
    private final BitmapDrawable mDefaultAlbumIcon;
    private OkosamaMediaPlayerActivity mActivity;
    private int mAlbumIdx;
    private int mArtistIdx;
    private int mAlbumArtIndex;
    //private String mArtistId;
    /**
     * アーティストIDの取得
     * @param artistId
     */
//    public void setArtistId(String artistId)
//    {
//    	mArtistId = artistId;
//    }
    // private final Resources mResources;
    //private final StringBuilder mStringBuilder = new StringBuilder();
    private final String mUnknownAlbum;
    private final String mUnknownArtist;
    //private final String mAlbumSongSeparator;
    //private final Object[] mFormatArgs = new Object[1];
    private AlphabetIndexer mIndexer;
    //private ExpList mList;
    private AsyncQueryHandler mQueryHandler;
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;
    
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
    public AlbumListAdapter( OkosamaMediaPlayerActivity currentactivity, 
            int layout, Cursor cursor, String[] from, int[] to) {
        super(currentactivity, layout, cursor, from, to);

        // アクティビティの設定
        // クエリハンドラの作成
        //ctx = context;
        // mList = list;
        mActivity = currentactivity;
        mQueryHandler = new QueryHandler(mActivity.getContentResolver(), mActivity);
        
        // albumとartistを表す文字列
        mUnknownAlbum = mActivity.getString(R.string.unknown_album_name);
        mUnknownArtist = mActivity.getString(R.string.unknown_artist_name);
        //mAlbumSongSeparator = context.getString(R.string.albumsongseparator);

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
        getColumnIndices(cursor);
    }

    private void getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// カーソルがnullでなければ
        	// カーソルから、各カラムのindexを取得し、メンバ変数に格納する
            mAlbumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
            mArtistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
            mAlbumArtIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART);
            
            // indexerにカーソルを設定する？
            if (mIndexer != null) {
                mIndexer.setCursor(cursor);
            } else {
            	// アルバム名に、ファーストスクロールのindexerを設定？
                mIndexer = new MusicAlphabetIndexer(cursor, mAlbumIdx, OkosamaMediaPlayerActivity.getResourceAccessor().getString(
                        R.string.fast_scroll_alphabet));
            }
        }
    }
    
    /**
     * クエリハンドラの取得？
     * @return
     */
    public AsyncQueryHandler getQueryHandler() {
        return mQueryHandler;
    }

    /**
     * 新しいビューの作成？
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
       View v = super.newView(context, cursor, parent);
       ViewHolder vh = new ViewHolder();
       vh.line1 = (TextView) v.findViewById(R.id.line1);
       vh.line2 = (TextView) v.findViewById(R.id.line2);
       vh.play_indicator = (ImageView) v.findViewById(R.id.play_indicator);
       vh.icon = (ImageView) v.findViewById(R.id.icon);
       vh.icon.setBackgroundDrawable(mDefaultAlbumIcon);
       vh.icon.setPadding(0, 0, 1, 0);
       // tagにviewholderを設定？
       v.setTag(vh);
       return v;
    }

    /**
     * ビューを紐つける
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        
    	// タグからビューホルダーを取得
        ViewHolder vh = (ViewHolder) view.getTag();

        // アルバム名を取得、ビューに設定
        String name = cursor.getString(mAlbumIdx);
        String displayname = name;
        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
        if (unknown) {
            displayname = mUnknownAlbum;
        }
        vh.line1.setText(displayname);
        
        // アーティスト名を取得、ビューに設定
        name = cursor.getString(mArtistIdx);
        displayname = name;
        if (name == null || name.equals(MediaStore.UNKNOWN_STRING)) {
            displayname = mUnknownArtist;
        }
        vh.line2.setText(displayname);

        // アイコンに、アルバムアートを設定する？
        ImageView iv = vh.icon;
        // We don't actually need the path to the thumbnail file,
        // we just use it to see if there is album art or not
        String art = cursor.getString(mAlbumArtIndex);
        long aid = cursor.getLong(0);
        if (unknown || art == null || art.length() == 0) {
            iv.setImageDrawable(null);
        } else {
            Drawable d = MediaPlayerUtil.getCachedArtwork(context, aid, mDefaultAlbumIcon);
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
     * カーソルの変更
     */
    @Override
    public void changeCursor(Cursor cursor) {
        if (mActivity.isFinishing() && cursor != null ) {
        	// アクティビティが終了中で、まだカーソルが残っている場合、カーソルをクローズ
            cursor.close();
            cursor = null;
        }
        // if (false == isEqualCursor(cursor, Database.getInstance(ctx).getCursor( Database.AlbumCursorName ))) {
        	// カーソルが変更されていたら、再設定する
        	Database.getInstance(ctx).setCursor( Database.AlbumCursorName, cursor );
            getColumnIndices(cursor);
    	//}
            Log.i("test", "changecursor album");
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
     * バックグラウンドでクエリを実行する？
     * 普通に実行してるように見えるんだけど・・・
     * フレームワークが実行する関数と思われるので、フレームワークがこの関数をバックで実行してくれるのかも
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
    	
        String s = constraint.toString();
        if (mConstraintIsValid && (
                (s == null && mConstraint == null) ||
                (s != null && s.equals(mConstraint)))) {
        	// 状態が変わっていなければ、そのまま？
            return getCursor();
        }
        Cursor c = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).createAlbumCursor(null, s ) ;//, mArtistId);
        mConstraint = s;
        mConstraintIsValid = true;
        return c;
    }
    
    // 下記は、SectionIndexer用のオーバーライド関数と思われる
    /**
     * インデックスからセクションを取得する
     */
    public Object[] getSections() {
        return mIndexer.getSections();
    }
    
    /**
     * インデックスからセクションのポジションを取得する
     */
    public int getPositionForSection(int section) {
        return mIndexer.getPositionForSection(section);
    }
    /**
     * ポジションのセクションを取得？
     */
    public int getSectionForPosition(int position) {
        return 0;
    }
}
