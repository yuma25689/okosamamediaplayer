package okosama.app.adapter;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.storage.Database;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * プレイリストのアダプタ？
 * TODO:newViewは不要？なぜ？
 * TODO:indexerは継承しない？
 * @author 25689
 *
 */
public class PlaylistListAdapter extends SimpleCursorAdapter {
    private static final long RECENTLY_ADDED_PLAYLIST = -1;
    private static final long ALL_SONGS_PLAYLIST = -2;
    private static final long PODCASTS_PLAYLIST = -3;
	
    // 各カラムのインデックス保持用
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

	private OkosamaMediaPlayerActivity mActivity = null;
    private AsyncQueryHandler mQueryHandler;
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;

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

    public PlaylistListAdapter(Context context, OkosamaMediaPlayerActivity currentactivity,
            int layout, Cursor cursor, String[] from, int[] to) {
        super(context, layout, cursor, from, to);
        mActivity = currentactivity;
        getColumnIndices(cursor);
        mQueryHandler = new QueryHandler(context.getContentResolver());
    }
    private void getColumnIndices(Cursor cursor) {
        if (cursor != null) {
            mTitleIdx = cursor.getColumnIndexOrThrow(PlaylistsColumns.NAME);
            mIdIdx = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        }
    }

//    public void setActivity(OkosamaMediaPlayerActivity newactivity) {
//        mActivity = newactivity;
//    }
    
    public AsyncQueryHandler getQueryHandler() {
        return mQueryHandler;
    }

    /**
     * ビューの紐付け
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        
    	// タイトルの設定
        TextView tv = (TextView) view.findViewById(R.id.line1);
        
        String name = cursor.getString(mTitleIdx);
        tv.setText(name);
        
        // idを取得？
        long id = cursor.getLong(mIdIdx);
        
        // idの種類によって、アイコンの画像を変える
        ImageView iv = (ImageView) view.findViewById(R.id.icon);
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
        iv = (ImageView) view.findViewById(R.id.play_indicator);
        iv.setVisibility(View.GONE);

        view.findViewById(R.id.line2).setVisibility(View.GONE);
    }

    /**
     * カーソルを変更する
     */
    @Override
    public void changeCursor(Cursor cursor) {
        if (mActivity.isFinishing() && cursor != null) {
        	// アクティビティが終了中ならば、カーソルクローズ
            cursor.close();
            cursor = null;
        }
        //if (false == isEqualCursor( cursor, Database.getInstance(mActivity).getCursor(Database.PlaylistCursorName))) {
        	// カーソルが変更されていたら、カーソルを再設定する
        	Database.getInstance(mActivity).setCursor(Database.PlaylistCursorName, cursor);
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
     * 背景でクエリを発行するときに実行される関数、かもしれない。
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String s = constraint.toString();
        // フィルタが変更されていなかったら、多分クエリ再発行しない
        if (mConstraintIsValid && (
                (s == null && mConstraint == null) ||
                (s != null && s.equals(mConstraint)))) {
            return getCursor();
        }
        Cursor c = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).createPlaylistCursor(null, s, createShortcut);
        mConstraint = s;
        mConstraintIsValid = true;
        return c;
    }
}
