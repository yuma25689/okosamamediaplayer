package okosama.app.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.ResourceAccessor;
import okosama.app.service.MediaInfo;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.*;
import okosama.app.tab.TabPage;
// import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * アーティストのアダプタ
 * @author 25689
 *
 */
public class ArtistAlbumListRawAdapter extends BaseExpandableListAdapter 
implements IAdapterUpdate<ArtistGroupData> { //, IFilterable {//<ArtistGroupData,ArtistChildData> {
	
	boolean deleted = false;
	TabPage page;
	boolean bDataUpdating = false;
	boolean bLastError = false;
	public boolean IsDataUpdating()
	{
		return bDataUpdating;
	}
	private LayoutInflater inflater;

	int iGroupLayoutId = 0;
	int iChildLayoutId = 0;
    // private final Drawable mNowListOverlay;
	
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
    //private final String mAlbumSongSeparator;
    private final String mUnknownAlbum;
    private final String mUnknownArtist;
    private final StringBuilder mBuffer = new StringBuilder();
    private final Object[] mFormatArgs = new Object[1];
    private final Object[] mFormatArgs3 = new Object[3];
    // インデクサ
    // private MusicAlphabetIndexer mIndexer;
    // アクティビティ
    private OkosamaMediaPlayerActivity mActivity;
    // private AsyncQueryHandler mQueryHandler;
//    private String mConstraint = null;
//    private boolean mConstraintIsValid = false;
    // viewの保持用
    static class ViewHolder {
        TextView line1;
        TextView line2;
        ImageView play_indicator;
        ImageView icon;
    }

    // private int[] rowId = null;
//    private SparseArray<ArtistGroupData> groupData = new SparseArray<ArtistGroupData>(); 
//    private SparseArray<ArtistChildData[]> childData = new SparseArray<ArtistChildData[]>(); 
    //@SuppressLint("UseSparseArrays")
    private HashMap<Integer,ArtistGroupData> groupData = new HashMap<Integer,ArtistGroupData>(); 
    //@SuppressLint("UseSparseArrays")
	private HashMap<Integer,ArtistChildData[]> childData = new HashMap<Integer,ArtistChildData[]>(); 
    // private SparseArray<ArtistGroupData> groupDataTmp = new SparseArray<ArtistGroupData>();
	private HashMap<Integer,ArtistGroupData> groupDataTmp = new HashMap<Integer,ArtistGroupData>();
	private HashMap<Integer,ArtistChildData[]> childDataTmp = new HashMap<Integer,ArtistChildData[]>(); 
    // private SparseArray<ArtistChildData[]> childDataTmp = new SparseArray<ArtistChildData[]>(); 
//    private ArtistGroupData[] group = null;
//    private ArtistChildData[][] child = null;
//    private ArtistGroupData[] groupTmp = null;
//    private ArtistChildData[][] childTmp = null;
    
    public ArtistAlbumListRawAdapter(OkosamaMediaPlayerActivity currentactivity,
    		// int[] rowId,
    		// ArtistGroupData[] listGroup,
    		//SparseArray<ArtistGroupData> groupData,
    		int glayout, 
    		// ArtistChildData[][] listChild, 
    		//SparseArray<ArtistChildData[]> childData,
    		int clayout,
    		TabPage page ) {
        // super(currentactivity, listGroup, glayout, listChild, clayout );
        // activityの作成
        // QueryHandlerの作成
    	this.page = page;
        mActivity = currentactivity;
        this.inflater 
        = (LayoutInflater) currentactivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // mQueryHandler = new QueryHandler(mActivity.getContentResolver(), this);

        // this.rowId = rowId;
        //this.groupData = groupData;
        //this.childData = childData;
        iGroupLayoutId = glayout;
        iChildLayoutId = clayout;
        
        // Resources r = context.getResources();
        mNowPlayingOverlay = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.indicator_ic_mp_playing_list);
        // mNowListOverlay = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.playlist_press);
        mDefaultAlbumIcon =  (BitmapDrawable)OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.albumart_mp_unknown_list);
        // Filterとディザを未指定にして、ビットマップを高速にする
        // no filter or dither, it's a lot faster and we can't tell the difference
        mDefaultAlbumIcon.setFilterBitmap(false);
        mDefaultAlbumIcon.setDither(false);
        
        //mContext = context;
        // 現在のカーソルのカラムのインデックスを設定
        // getColumnIndices(cursor);
        //mResources = context.getResources();
        // 各メンバ変数の初期か
        // セパレータ？
        //mAlbumSongSeparator = currentactivity.getString(R.string.albumsongseparator);
        // アルバム、アーティスト
        mUnknownAlbum = currentactivity.getString(R.string.unknown_album_name);
        mUnknownArtist = currentactivity.getString(R.string.unknown_artist_name);
    }
            
    /**
     * 内部のクエリハンドラを返却
     * @return
     */
//    public AsyncQueryHandler getQueryHandler() {
//        return mQueryHandler;
//    }
    /**
     * 現在のカラムのインデックスを内部に設定
     * @param cursor
     */
    private int getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// カーソルが設定されていたら
        	// id,アーティストid,アルバムid,ソングid,インデクサ
        	// とりあえず、多分グループ用だと思われる
            mGroupArtistIdIdx = cursor.getColumnIndexOrThrow(BaseColumns._ID);
            mGroupArtistIdx = cursor.getColumnIndexOrThrow(ArtistColumns.ARTIST);
            mGroupAlbumIdx = cursor.getColumnIndexOrThrow(ArtistColumns.NUMBER_OF_ALBUMS);
            mGroupSongIdx = cursor.getColumnIndexOrThrow(ArtistColumns.NUMBER_OF_TRACKS);
//            if (mIndexer != null) {
//                mIndexer.setCursor(cursor);
//            } else {
//                mIndexer = new MusicAlphabetIndexer(cursor, mGroupArtistIdx, 
//                        OkosamaMediaPlayerActivity.getResourceAccessor().getString(R.string.fast_scroll_alphabet));
//            }
        }
        else
        {
        	return -1;
        }
        return 0;
    }

    /**
     * 新しいグループビューを作成し、返却する？
     */
    public View newGroupView() { //Context context, boolean isExpanded, ViewGroup parent) {
    	// ビューの取得？
	    View v = inflater.inflate(iGroupLayoutId, null); 
    	
        // アイコンのビュー取得、そのレイアウトを取得？
        ImageView iv = (ImageView) v.findViewById(R.id.icon);
        // ここは、アイコンのビューのレイアウトを書き換えているのだろうか？
        // TODO: なんだか書き換えられていなくて意味ないようにも見えるけど・・・
        ViewGroup.LayoutParams p = iv.getLayoutParams();
        p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        //p.height = 64; //ViewGroup.LayoutParams.;
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
    public View newChildView() {
        View v = inflater.inflate(iChildLayoutId, null);
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
    // @Override
    public void bindGroupView(View view, 
    		Context context, ArtistGroupData data, boolean isexpanded) {

    	// タグからビューホルダーを取得
        ViewHolder vh = (ViewHolder) view.getTag();

        // カーソルから値を取得して、ビューに設定する
        // アーティスト
        String artist = data.getName();// cursor.getString(mGroupArtistIdx);
        String displayartist = artist;
        boolean unknown = artist == null || artist.equals(MediaStore.UNKNOWN_STRING);
        if (unknown) {
            displayartist = mUnknownArtist;
        }
        vh.line1.setText(displayartist);

        // アルバム、曲の数？だろうか？
        int numalbums = data.getNumOfAlbums(); // cursor.getInt(mGroupAlbumIdx);
        int numsongs = data.getNumOfTracks(); // cursor.getInt(mGroupSongIdx);
        
        // 取得したアルバム数、曲数から、ラベルを作成し、設定
        String songs_albums = ResourceAccessor.makeAlbumsLabel(context,
                numalbums, numsongs, unknown);
        
        vh.line2.setText(songs_albums);
        
        // 現在のアーティストと、レコードのアーティストIDを比較し、同じならば、再生中にする？
        // TODO:何か、それではアーティストの別のアルバムでも再生中になってしまう気がするが、
        // このリストではアルバムの区別はないのかも
        long currentartistid = MediaPlayerUtil.getCurrentArtistId();
        long artistid = data.getDataId(); // cursor.getLong(mGroupArtistIdIdx);
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
    // @Override
    public void bindChildView(View view, Context context, ArtistChildData data ) { //Context context, Cursor cursor ) { // , boolean islast) {

    	if( view == null )
    	{
    		return;
    	}
    	
    	// タグからビューホルダーを取得
        ViewHolder vh = (ViewHolder) view.getTag();

        // アルバム名を取得、設定
        String name = data.getAlbumName();// cursor.getString(cursor.getColumnIndexOrThrow(AlbumColumns.ALBUM));
        String displayname = name;
        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
        if (unknown) {
            displayname = mUnknownAlbum;
        }
        vh.line1.setText(displayname);

        // 曲数とアーティストの曲数を取得
        int numsongs = data.getNumOfSongs(); // cursor.getInt(cursor.getColumnIndexOrThrow(AlbumColumns.NUMBER_OF_SONGS));
        int numartistsongs = data.getNumOfSongsForArtist();// cursor.getInt(cursor.getColumnIndexOrThrow(AlbumColumns.NUMBER_OF_SONGS_FOR_ARTIST));

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
                args[2] = data.getArtistName();// cursor.getString(cursor.getColumnIndexOrThrow(ArtistColumns.ARTIST));
                builder.append(OkosamaMediaPlayerActivity.getResourceAccessor().getQuantityString(R.plurals.Nsongscomp, numsongs, args));
            }
        }
        vh.line2.setText(builder.toString());
        
        // アルバムアートの取得、設定
        ImageView iv = vh.icon;
        // We don't actually need the path to the thumbnail file,
        // we just use it to see if there is album art or not
        String art = data.getAlbumArt(); //cursor.getString(cursor.getColumnIndexOrThrow(
                //AlbumColumns.ALBUM_ART));
        if (unknown || art == null || art.length() == 0) {
        	// 分からない場合は、デフォルトを設定する
            iv.setBackgroundDrawable(mDefaultAlbumIcon);
            iv.setImageDrawable(null);
        } else {
        	// 分かる場合は、Databaseから取得する
            long artIndex = Long.parseLong(data.getAlbumId()); // cursor.getLong(0);
            Drawable d = MediaPlayerUtil.getCachedArtwork(context, artIndex, mDefaultAlbumIcon);
            iv.setImageDrawable(d);
        }

        // 再生中のアルバムのidと、この項目のアルバムのidを取得し、
        // 一致したら現在プレイ中にする
        long currentalbumid = MediaPlayerUtil.getCurrentAlbumId();
        long aid =  Long.parseLong(data.getAlbumId()); // cursor.getLong(0);
        iv = vh.play_indicator;
        if (currentalbumid == aid) {
            iv.setImageDrawable(mNowPlayingOverlay);
        } else {
            iv.setImageDrawable(null);
        }
    }


    /**
     * 子ビューのカーソルを取得？
     * TODO:不要なので、もっと軽い処理に置き換えること
     */
    // @Override
    protected Cursor getChildrenCursor(long groupId) { //, String artistName ) { //Cursor groupCursor) {
        
    	// グループカーソルから、そのアーティストのidを取得する
        // long id = groupCursor.getLong(groupCursor.getColumnIndexOrThrow(BaseColumns._ID));
        
        // カラム名の設定
        String[] cols = new String[] {
                BaseColumns._ID,
                AlbumColumns.ALBUM,
                AlbumColumns.NUMBER_OF_SONGS,
                AlbumColumns.NUMBER_OF_SONGS_FOR_ARTIST,
                AlbumColumns.ALBUM_ART
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
                MediaStore.Audio.Artists.Albums.getContentUri(external_string, groupId),
                cols, null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        
        return c;
    }

    
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if( // childData.get(groupPosition,null) != null
				childData.containsKey(groupPosition) == true
				&& childPosition < childData.get(groupPosition).length )
		{
			return childData.get(groupPosition)[childPosition];
		}		
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View v = convertView;
		if( v == null )
		{
			v = newChildView();
		}
		ArtistChildData data = (ArtistChildData) getChild(groupPosition,childPosition);
		bindChildView(v,mActivity,data);
		return v;
	}

	/**
	 * 子のデータは、展開時にこの関数で読み込み
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		if( childData.containsKey(groupPosition) == true )
		{
			return childData.get(groupPosition).length;
		}
		ArtistGroupData data = (ArtistGroupData) getGroup(groupPosition);
		if( data == null )
		{
			return 0;
		}
		Cursor childCursor = getChildrenCursor(data.getDataId());
		if( childCursor == null )
		{
			return 0;
		}
		try {
			if( 0 < childCursor.getCount() )
    		{
    			ArtistChildData[] childList = new ArtistChildData[childCursor.getCount()];
    			int j = 0;
        		childCursor.moveToFirst();
        		do 
        		{
                    ArtistChildData dataChild = new ArtistChildData();
                   
                    // album名
                    dataChild.setAlbumName(
                    	childCursor.getString(childCursor.getColumnIndexOrThrow(AlbumColumns.ALBUM) ) 
                    );
                    // album 曲数
                    dataChild.setNumOfSongs(
                    	childCursor.getInt(childCursor.getColumnIndexOrThrow(AlbumColumns.NUMBER_OF_SONGS))
                    );
                    // artist 曲数
                    dataChild.setNumOfSongsForArtist( 
                    	childCursor.getInt(childCursor.getColumnIndexOrThrow(AlbumColumns.NUMBER_OF_SONGS_FOR_ARTIST))
                    );
                    // artist名
                    dataChild.setArtistName(
                    	data.getName()
                    	//childCursor.getString(childCursor.getColumnIndexOrThrow(ArtistColumns.ARTIST))
                    );
                    // album art
                    dataChild.setAlbumArt(
                    		childCursor.getString(childCursor.getColumnIndexOrThrow(
                        AlbumColumns.ALBUM_ART))
                    );
                    // album id
                    dataChild.setAlbumId( childCursor.getString(0) );
                    
                    childList[j] = dataChild; 
                    		
                    j++;
        		} while( deleted == false && 
        				childCursor.moveToNext() );
        		childDataTmp.put( groupPosition, childList);
        		childData.put(groupPosition, childList );
    		}
		} finally {
			childCursor.close();
		}
		
		if( // childData.get(groupPosition,null) == null
				childData.containsKey(groupPosition) == false				
				)
		{
			return 0;
		}
		return childData.get(groupPosition).length;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// Log.e("arsit","getGroup" + groupPosition + " " + groupData.containsKey(groupPosition));
		if( // groupData.get(groupPosition,null) != null )
				groupData.containsKey(groupPosition) == true
				)
		{
			return groupData.get(groupPosition);
		}
		return null;
	}

	@Override
	public int getGroupCount() {
		if( groupData == null )
		{
			return 0;
		}
		//.e("arsit","getGroupCount" + groupData.size());
		return groupData.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		//Log.e("getGroup","artist");
		View v = convertView;
		if( v == null )
		{
			v = newGroupView();
		}
		ArtistGroupData data = (ArtistGroupData) getGroup(groupPosition);
		if( data != null )
		{
			bindGroupView(v,mActivity,data,isExpanded);
		}
		// 現状、filterがかかっている場合、nullになってしまう？
		return v;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
    /**
     * データの変更？
     */
    // @Override
    public void updateData(
    		//SparseArray<ArtistGroupData> group,
    		//SparseArray<ArtistChildData[]> child
    	    HashMap<Integer,ArtistGroupData> group, 
    		HashMap<Integer,ArtistChildData[]> child
    		) {
    	// mapIdAndArt.clear();
    	// グループのマップをフィルタをかけながらコピー
    	// コピーしたマップだけ、filterをかけて作り直す
    	HashMap<Integer,ArtistGroupData> group2 = new HashMap<Integer,ArtistGroupData>();
    	if( filterData != null )
    	{
    		int i=0;
    		for( Entry<Integer,ArtistGroupData> entryTmp : group.entrySet() )
    		{
    			if( true == isFilterData( entryTmp.getValue() ) )
    			{
    				// 抽出対象の場合のみ、格納する
    				group2.put(i,entryTmp.getValue());
    				i++;
    			}
    		}
    	}
    	else
    	{
    		group2 = group;
    	}
    	
    	this.groupData = group2;
    	this.childData = child;// 2014/1/18 展開時に取得するのでいいと思われたのでここでクリア
    	Log.d("updateData","artist" + group.size());
    	notifyDataSetChanged();
    }
    
    public int stockMediaDataFromDevice(final TabPage page)
    {
    	if( bDataUpdating == true )
    	{
        	if( page != null )
        	{
        		page.startUpdate();
        	}
    		
    		return -1;
    	}
    	bDataUpdating = true;
    	// Log.i("insertAllDataFromCursor","start");
    	
//    	if (mActivity.isFinishing() && cursor != null ) {
//        	// アクティビティが終了中で、まだカーソルが残っている場合、カーソルをクローズ
//            cursor.close();
//            cursor = null;
//        }
        // Database.getInstance(mActivity).setCursor( Database.ArtistCursorName, cursor );
    	if( page != null )
    	{
    		page.startUpdate();
    	}
            	
        AsyncTask<Cursor, Void, Integer> task = new AsyncTask<Cursor, Void, Integer>() {
            @Override
            protected Integer doInBackground(Cursor... params) {
            	Log.i("doInBackground","start");
            	
            	bLastError = false;
            	// カーソルをループする
            	Cursor cursor = Database.getInstance(
            			OkosamaMediaPlayerActivity.isExternalRef()
            	).createArtistCursor();//null, null);            	
            	// Cursor cursor = params[0];
        		if( cursor == null || cursor.isClosed() )
        		{
        			Log.w("ArtistAlbumListAdp - doInBk", "cursor closed!");
        			return -1;
        		}
        		try 
        		{
	                groupDataTmp.clear();
	                childDataTmp.clear();
	            	
	        		if( 0 != getColumnIndices(cursor) )
	        		{
	        			return -1;
	        		}
	            	if( 0 < cursor.getCount() )
	            	{
	        		
		                Log.i("doInBackground","moveToFirst");
		            	int i=0;
		        		cursor.moveToFirst();
		        		do 
		        		{
		                    ArtistGroupData data = new ArtistGroupData();
		                    
		            		// 全ての要素をループする
		                    data.setGroupId(i);
		                    data.setName( cursor.getString(mGroupArtistIdx) );
		                    data.setNumOfAlbums( cursor.getInt(mGroupAlbumIdx) );
		                    data.setNumOfTracks( cursor.getInt(mGroupSongIdx) );
		                    data.setDataId( cursor.getLong(mGroupArtistIdIdx));
		            		groupDataTmp.put( i, data );
	
	                		i++;
		        		} while( deleted == false && //OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() == false && 
		        				cursor.moveToNext() );
	            	}
            	} finally {
            		cursor.close();
            	}
        		if( deleted ) //OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() )
        		{
        			return -2;
        		}
                return 0;
            }

            @Override
            protected void onPostExecute(Integer ret) 
            {
            	Log.i("onPostExecute","ret=" + ret );
            	if( ret < 0 )
            	{
            		groupDataTmp.clear();
            		childDataTmp.clear();
            		bLastError = true;
            	}
            	// 格納終了
            	// 二重管理になってしまっているが、アダプタにも同様のデータを格納する
            	updateData( groupDataTmp, childDataTmp );
            	// TabPage page = (TabPage) mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_MEDIA).getChild(TabPage.TABPAGE_ID_ARTIST);
            	if( page != null )
            	{
            		page.endUpdate();
            	}
            	bDataUpdating = false;            	
            }
        };
        task.execute();
        return 0;
    }

	/**
	 * @return the groupData
	 */
	//public SparseArray<ArtistGroupData> getGroupData() {
	public HashMap<Integer,ArtistGroupData> getGroupData() {
		return groupData;
	}

	/**
	 * @param groupData the groupData to set
	 */
	//public void setGroupData(SparseArray<ArtistGroupData> groupData) {
	public void setGroupData(HashMap<Integer,ArtistGroupData> groupData) {
		this.groupData = groupData;
	}

	/**
	 * @return the childData
	 */
	// public SparseArray<ArtistChildData[]> getChildData() {
	public HashMap<Integer, ArtistChildData[]> getChildData() {
		return childData;
	}

	/**
	 * @param childData the childData to set
	 */
	//public void setChildData(SparseArray<ArtistChildData[]> childData) {
	public void setChildData(HashMap<Integer,ArtistChildData[]> childData) {
		this.childData = childData;
	}
    @Override
    // 曲の変更時など、状態が変わったときに、外部から表示を更新させる
	public int updateStatus()
    {
    	// 2014/1/18 add filter用
    	updateData( groupDataTmp, childDataTmp );    	
    	// 表示を更新?
    	notifyDataSetChanged();
    	return 0;
    }
	@Override
	public boolean isLastErrored() {
		return bLastError;
	}

	@Override
	public int getMainItemCount() {
		return getGroupCount();
	}

	@Override
	public void initialize() {
   		if( 0 < mActivity.getArtistAdp().getGroupCount() 
   		&& false == mActivity.getArtistAdp().isLastErrored() )
   		{
   			mActivity.getArtistAdp().updateStatus();
   		}
   		else
   		{
   			mActivity.reScanMediaOfMediaTab(TabPage.TABPAGE_ID_ARTIST);
   		}
	}

	@Override
	public void clearAdapterData() {
		deleted = true;
		groupData.clear();
		childData.clear();
	}

	FilterData filterData = null;	
	@Override
	public void setFilterData(FilterData data) {
		filterData = data;
	}

	/**
	 *注:なんか変だけど、表示対象の場合、true
	 */	
	@Override
	public boolean isFilterData(ArtistGroupData data) {
		boolean bRet = true;
		if( filterData != null && data != null)
		{
			// アーティストID
			if( filterData.getArtistId() != null )
			{
				if( data.getDataId() != -1
				&& filterData.getArtistId().equals(String.valueOf(data.getDataId())) )
				{
					// アーティストがフィルタ対象？
					// data.getTrackArtistId()は本当に入っているのだろうか・・・。TODO:調査
					bRet = true;
				}
				else
				{
					return false;
				}
			}
			// アーティスト名
			if( filterData.getStrArtist() != null && 0 < filterData.getStrArtist().length() )
			{
				if( data.getName() != null
				&& -1 != data.getName().indexOf(filterData.getStrArtist()) )
				{
					// アーティスト名が一部一致
					bRet = true;
				}
				else
				{
					return false;
				}
			}
			
			// ジャンルID
			if( filterData.getGenreId() != null )
			{
				OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
				MediaInfo songlistOfArtist[] = Database.getSongListForArtist(activity, data.getDataId());
				if( songlistOfArtist != null )
				{
					for( MediaInfo mi : songlistOfArtist )
					{
						ArrayList<GenreData> genres = mActivity.getGenreStocker().getGenreOfAudio( 
								mi.getId() );
						boolean bNoHit = true;
						if( genres == null )
						{
							bNoHit = true;
						}
						else
						{
							for( GenreData genre : genres )
							{
								if( filterData.getGenreId().equals( String.valueOf(genre.getDataId() ) ) )
								{
									// ジャンルが一致
									bRet = true;
									bNoHit = false;
									break;
								}
							}
						}
						// アーティストの中で１曲でもヒットすれば、そのアーティストは検索対象
						if( false == bNoHit )
						{
							break;
						}
						else
						{
							bRet = false;
						}
					}
				}
			}
		}
		return bRet;
	}

	@Override
	public void clearFilterData() {
		filterData = null;
	}

	@Override
	public FilterData getFilterData() {
		return filterData;
	}
	
}