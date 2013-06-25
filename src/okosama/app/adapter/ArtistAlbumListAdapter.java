package okosama.app.adapter;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.ResourceAccessor;
import okosama.app.service.MediaPlayer;
import okosama.app.storage.Database;
import okosama.app.storage.QueryHandler;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

/**
 * アーティストのアダプタ
 * @author 25689
 *
 */
public class ArtistAlbumListAdapter extends SimpleCursorTreeAdapter implements SectionIndexer {
	
	// 多分現在プレイ中の時に表示する画像
    private final Drawable mNowPlayingOverlay;
    // デフォルトのアルバムのアイコン？
    private final BitmapDrawable mDefaultAlbumIcon;
    // インデックス保持用
    private int mGroupArtistIdIdx;
    private int mGroupArtistIdx;
    private int mGroupAlbumIdx;
    private int mGroupSongIdx;
    //private final Context mContext;
    //private final Resources mResources;
    private final String mAlbumSongSeparator;
    private final String mUnknownAlbum;
    private final String mUnknownArtist;
    private final StringBuilder mBuffer = new StringBuilder();
    private final Object[] mFormatArgs = new Object[1];
    private final Object[] mFormatArgs3 = new Object[3];
    // インデクサ
    private MusicAlphabetIndexer mIndexer;
    // アクティビティ
    private OkosamaMediaPlayerActivity mActivity;
    private AsyncQueryHandler mQueryHandler;
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;
    // viewの保持用
    static class ViewHolder {
        TextView line1;
        TextView line2;
        ImageView play_indicator;
        ImageView icon;
    }

    public ArtistAlbumListAdapter(OkosamaMediaPlayerActivity currentactivity,
            Cursor cursor, int glayout, String[] gfrom, int[] gto, 
            int clayout, String[] cfrom, int[] cto) {
        super(currentactivity, cursor, glayout, gfrom, gto, clayout, cfrom, cto);
        // activityの作成
        // QueryHandlerの作成
        mActivity = currentactivity;
        mQueryHandler = new QueryHandler(currentactivity.getContentResolver(),mActivity);

        // Resources r = context.getResources();
        mNowPlayingOverlay = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.indicator_ic_mp_playing_list);
        mDefaultAlbumIcon =  (BitmapDrawable)OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.albumart_mp_unknown_list);
        // Filterとディザを未指定にして、ビットマップを高速にする
        // no filter or dither, it's a lot faster and we can't tell the difference
        mDefaultAlbumIcon.setFilterBitmap(false);
        mDefaultAlbumIcon.setDither(false);
        
        //mContext = context;
        // 現在のカーソルのカラムのインデックスを設定
        getColumnIndices(cursor);
        //mResources = context.getResources();
        // 各メンバ変数の初期か
        // セパレータ？
        mAlbumSongSeparator = currentactivity.getString(R.string.albumsongseparator);
        // アルバム、アーティスト
        mUnknownAlbum = currentactivity.getString(R.string.unknown_album_name);
        mUnknownArtist = currentactivity.getString(R.string.unknown_artist_name);
    }
    
    /**
     * 現在のカラムのインデックスを内部に設定
     * @param cursor
     */
    private void getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// カーソルが設定されていたら
        	// id,アーティストid,アルバムid,ソングid,インデクサ
        	// とりあえず、多分グループ用だと思われる
            mGroupArtistIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
            mGroupArtistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
            mGroupAlbumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
            mGroupSongIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
            if (mIndexer != null) {
                mIndexer.setCursor(cursor);
            } else {
                mIndexer = new MusicAlphabetIndexer(cursor, mGroupArtistIdx, 
                        OkosamaMediaPlayerActivity.getResourceAccessor().getString(R.string.fast_scroll_alphabet));
            }
        }
    }
    
    /**
     * アクティビティを外部から設定？
     * @param newactivity
     */
//    public void setActivity(OkosamaMediaPlayerActivity newactivity) {
//        mActivity = newactivity;
//    }
    
    /**
     * 内部のクエリハンドラを返却
     * @return
     */
    public AsyncQueryHandler getQueryHandler() {
        return mQueryHandler;
    }

    /**
     * 新しいグループビューを作成し、返却する？
     */
    @Override
    public View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
    	// ビューの取得？
        View v = super.newGroupView(context, cursor, isExpanded, parent);
        // アイコンのビュー取得、そのレイアウトを取得？
        ImageView iv = (ImageView) v.findViewById(R.id.icon);
        // ここは、アイコンのビューのレイアウトを書き換えているのだろうか？
        // TODO: なんだか書き換えられていなくて意味ないようにも見えるけど・・・
        ViewGroup.LayoutParams p = iv.getLayoutParams();
        p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // ビューホルダーの作成
        ViewHolder vh = new ViewHolder();
        vh.line1 = (TextView) v.findViewById(R.id.line1);
        vh.line2 = (TextView) v.findViewById(R.id.line2);
        vh.play_indicator = (ImageView) v.findViewById(R.id.play_indicator);
        vh.icon = (ImageView) v.findViewById(R.id.icon);
        vh.icon.setPadding(0, 0, 1, 0);
        // タグにビューホルダーを設定
        v.setTag(vh);
        return v;
    }

    /**
     * 新しいchileViewを作成し、返却する？
     */
    @Override
    public View newChildView(Context context, Cursor cursor, boolean isLastChild,
            ViewGroup parent) {
        View v = super.newChildView(context, cursor, isLastChild, parent);
        // ViewHolderの作成
        ViewHolder vh = new ViewHolder();
        vh.line1 = (TextView) v.findViewById(R.id.line1);
        vh.line2 = (TextView) v.findViewById(R.id.line2);
        vh.play_indicator = (ImageView) v.findViewById(R.id.play_indicator);
        vh.icon = (ImageView) v.findViewById(R.id.icon);
        vh.icon.setBackgroundDrawable(mDefaultAlbumIcon);
        vh.icon.setPadding(0, 0, 1, 0);
        // タグにビューホルダーを設定
        v.setTag(vh);
        return v;
    }
    
    /**
     * グループビューの紐付け
     */
    @Override
    public void bindGroupView(View view, Context context, Cursor cursor, boolean isexpanded) {

    	// タグからビューホルダーを取得
        ViewHolder vh = (ViewHolder) view.getTag();

        // カーソルから値を取得して、ビューに設定する
        // アーティスト
        String artist = cursor.getString(mGroupArtistIdx);
        String displayartist = artist;
        boolean unknown = artist == null || artist.equals(MediaStore.UNKNOWN_STRING);
        if (unknown) {
            displayartist = mUnknownArtist;
        }
        vh.line1.setText(displayartist);

        // アルバム、曲の数？だろうか？
        int numalbums = cursor.getInt(mGroupAlbumIdx);
        int numsongs = cursor.getInt(mGroupSongIdx);
        
        // 取得したアルバム数、曲数から、ラベルを作成し、設定
        String songs_albums = ResourceAccessor.makeAlbumsLabel(context,
                numalbums, numsongs, unknown);
        
        vh.line2.setText(songs_albums);
        
        // 現在のアーティストと、レコードのアーティストIDを比較し、同じならば、再生中にする？
        // TODO:何か、それではアーティストの別のアルバムでも再生中になってしまう気がするが、
        // このリストではアルバムの区別はないのかも
        long currentartistid = MediaPlayer.getCurrentArtistId();
        long artistid = cursor.getLong(mGroupArtistIdIdx);
        if (currentartistid == artistid && !isexpanded) {
            vh.play_indicator.setImageDrawable(mNowPlayingOverlay);
        } else {
            vh.play_indicator.setImageDrawable(null);
        }
    }

    /**
     * 子ビューを設定
     * どうやら、子ビューには、該当アーティストのアルバムの一覧を設定する
     */
    @Override
    public void bindChildView(View view, Context context, Cursor cursor, boolean islast) {

    	// タグからビューホルダーを取得
        ViewHolder vh = (ViewHolder) view.getTag();

        // アルバム名を取得、設定
        String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM));
        String displayname = name;
        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
        if (unknown) {
            displayname = mUnknownAlbum;
        }
        vh.line1.setText(displayname);

        // 曲数とアーティストの曲数を取得
        int numsongs = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
        int numartistsongs = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS_FOR_ARTIST));

        final StringBuilder builder = mBuffer;
        builder.delete(0, builder.length());
        if (unknown) {
        	// アーティストが分からない場合、アーティストの数を曲数にする？
            numsongs = numartistsongs;
        }
          
        // 曲数を設定
        if (numsongs == 1) {
            builder.append(context.getString(R.string.onesong));
        } else {
            if (numsongs == numartistsongs) {
            	// アーティストの曲数と、曲数が一致
            	// 曲数は、１つだけ設定？
                final Object[] args = mFormatArgs;
                args[0] = numsongs;
                builder.append(OkosamaMediaPlayerActivity.getResourceAccessor().getQuantityString(R.plurals.Nsongs, numsongs, args));
            } else {
            	// 一致しない場合、３つ設定？曲数、アーティスト曲数、アーティスト名？
                final Object[] args = mFormatArgs3;
                args[0] = numsongs;
                args[1] = numartistsongs;
                args[2] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST));
                builder.append(OkosamaMediaPlayerActivity.getResourceAccessor().getQuantityString(R.plurals.Nsongscomp, numsongs, args));
            }
        }
        vh.line2.setText(builder.toString());
        
        // アルバムアートの取得、設定
        ImageView iv = vh.icon;
        // We don't actually need the path to the thumbnail file,
        // we just use it to see if there is album art or not
        String art = cursor.getString(cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Albums.ALBUM_ART));
        if (unknown || art == null || art.length() == 0) {
        	// 分からない場合は、デフォルトを設定する
            iv.setBackgroundDrawable(mDefaultAlbumIcon);
            iv.setImageDrawable(null);
        } else {
        	// 分かる場合は、Databaseから取得する
            long artIndex = cursor.getLong(0);
            Drawable d = MediaPlayer.getCachedArtwork(context, artIndex, mDefaultAlbumIcon);
            iv.setImageDrawable(d);
        }

        // 再生中のアルバムのidと、この項目のアルバムのidを取得し、
        // 一致したら現在プレイ中にする
        long currentalbumid = MediaPlayer.getCurrentAlbumId();
        long aid = cursor.getLong(0);
        iv = vh.play_indicator;
        if (currentalbumid == aid) {
            iv.setImageDrawable(mNowPlayingOverlay);
        } else {
            iv.setImageDrawable(null);
        }
    }


    /**
     * 子ビューのカーソルを取得？
     */
    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        
    	// グループカーソルから、そのアーティストのidを取得する
        long id = groupCursor.getLong(groupCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID));
        
        // カラム名の設定
        String[] cols = new String[] {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS_FOR_ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART
        };
        // uriの取得
        // 外部ストレージか内部かによって挙動を変更
        //Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String external_string = "external";
        if( OkosamaMediaPlayerActivity.isExternalRef() == false )
        {
        	external_string = "internal";	// 多分、これでよい
        }        
        // クエリ発行
        Cursor c = Database.query(mActivity,
                MediaStore.Audio.Artists.Albums.getContentUri(external_string, id),
                cols, null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        
        // カーソルのラッバ？
        class MyCursorWrapper extends CursorWrapper {
        	// アーティスト名
            String mArtistName;
            // マジックカラムのインデックス？
            // どうやら、このカーソルからアーティスト名を取得するためのインデックス
            // 本来、アーティスト名は格納されていないが、コンストラクタで格納し、取得のときにこのindexが指定されたらそれを返す
            int mMagicColumnIdx;
            /**
             * コンストラクタ
             * @param c
             * @param artist
             */
            MyCursorWrapper(Cursor c, String artist) {
                super(c);
                // アーティスト名
                mArtistName = artist;
                // アーティスト名がなかったら、不明を設定
                if (mArtistName == null || mArtistName.equals(MediaStore.UNKNOWN_STRING)) {
                    mArtistName = mUnknownArtist;
                }
                // マジックカラムとして、カラム数を設定？
                mMagicColumnIdx = c.getColumnCount();
            }
            
            @Override
            public String getString(int columnIndex) {
            	// カラムindexがマジックカラムでなければ、その文字列を取得？
                if (columnIndex != mMagicColumnIdx) {
                    return super.getString(columnIndex);
                }
                // マジックカラムならば、アーティスト名を取得
                return mArtistName;
            }
            
            /**
             * 指定されたカラム名のカラムのindexを取得
             * ただし、マジックカラムならば、アーティスト名を取得
             */
            @Override
            public int getColumnIndexOrThrow(String name) {
                if (MediaStore.Audio.Albums.ARTIST.equals(name)) {
                    return mMagicColumnIdx;
                }
                return super.getColumnIndexOrThrow(name); 
            }
            
            /**
             * インデックスに対応したカラムのindexを返却する
             */
            @Override
            public String getColumnName(int idx) {
                if (idx != mMagicColumnIdx) {
                    return super.getColumnName(idx);
                }
                return MediaStore.Audio.Albums.ARTIST;
            }
            
            /**
             * カラムのカウントを返却する
             * 自前で一個追加しているので、カラム数+1を返却する
             */
            @Override
            public int getColumnCount() {
                return super.getColumnCount() + 1;
            }
        }
        // カーソルに、アーティスト名を追加したカーソルを返却？
        // おそらく、アーティストはどのレコードでも同じで良いので、この作りで良い
        return new MyCursorWrapper(c, groupCursor.getString(mGroupArtistIdx));
    }

    /**
     * カーソルの変更
     */
    @Override
    public void changeCursor(Cursor cursor) {
        if (mActivity.isFinishing() && cursor != null) {
        	// アクティビティ終了中の場合、カーソルをクローズ
            cursor.close();
            cursor = null;
        }
        if (cursor != Database.getInstance(mActivity).getCursor(Database.ArtistCursorName)) {
        	// カーソルが変更されていたら、カーソルを設定する
        	Database.getInstance(mActivity).setCursor(Database.ArtistCursorName, cursor);
            getColumnIndices(cursor);
            super.changeCursor(cursor);
        }
    }
    
    /**
     * 背景でクエリを発行するときに実行される？
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String s = constraint.toString();
        // フィルタが変わっていなければ、発行しない？
        if (mConstraintIsValid && (
                (s == null && mConstraint == null) ||
                (s != null && s.equals(mConstraint)))) {
            return getCursor();
        }
        Cursor c = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).createArtistCursor(null, s);
        mConstraint = s;
        mConstraintIsValid = true;
        return c;
    }

    // 下記は、おそらくインデクサのオーバーライド関数
    public Object[] getSections() {
        return mIndexer.getSections();
    }
    
    public int getPositionForSection(int sectionIndex) {
        return mIndexer.getPositionForSection(sectionIndex);
    }
    
    public int getSectionForPosition(int position) {
        return 0;
    }
}