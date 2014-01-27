package okosama.app.adapter;

import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;

//import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.ResourceAccessor;
import okosama.app.service.MediaInfo;
//import okosama.app.adapter.AlbumListRawAdapter.ViewHolder;
import okosama.app.service.MediaPlayerUtil;
//import okosama.app.storage.AlbumData;
import okosama.app.storage.Database;
import okosama.app.storage.FilterData;
import okosama.app.storage.GenreData;
// import okosama.app.storage.QueryHandler;
import okosama.app.storage.TrackData;
import okosama.app.tab.TabPage;
//import okosama.app.storage.TrackQueryHandler;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.RemoteException;
//import android.provider.BaseColumns;
import android.provider.MediaStore;
//import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.AlphabetIndexer;
//import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
//import android.widget.SectionIndexer;
//import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * トラックのリストのアダプタ
 * @author 25689
 *
 */
public class TrackListRawAdapter extends ArrayAdapter<TrackData> 
implements IAdapterUpdate<TrackData>, SectionIndexer { //, IFilterable<TrackData> { 
//implements SectionIndexer {

	boolean deleted = false;
	// private HashMap<String, Integer> mIndexer = new HashMap<String, Integer>();
	private ArrayList<Integer> mSectionIndex = new ArrayList<Integer>();
	private ArrayList<String> mSection = new ArrayList<String>();
	private String[] sections;
	private ArrayList<TrackData> allItems = new ArrayList<TrackData>();
	// TODO:次へボタン等
	int maxShowCount = 500;

	MediaInfo [] playlist = null;
    private final BitmapDrawable mDefaultAlbumIcon;
	TabPage page;
	boolean bDataUpdating = false;	// 内部データを更新中かどうか
	boolean bLastError = false;
	public boolean isDataUpdating()
	{
		return bDataUpdating;
	}
	private LayoutInflater inflater;
	private int iLayoutId;
    ArrayList<Long> currentAllAudioIds = new ArrayList<Long>();
    public MediaInfo[] getCurrentAllMediaInfo()
    {
    	MediaInfo[] ret = new MediaInfo[currentAllAudioIds.size()];
    	int i = 0;
    	for( Long lng : currentAllAudioIds )
    	{
    		ret[i] = new MediaInfo( lng, MediaInfo.MEDIA_TYPE_AUDIO );
    		i++;
    	}
    	return ret;
    }

	// カラムのインデックス保持用 
    int mTitleIdx;
    int mArtistIdx;
    int mDurationIdx;
    int mDataIdx;
    int mAudioIdIdx;
    int mArtistIdIdx;
    int mAlbumIdx;
    int mAlbumIdIdx;
    //int mAlbumArtIndex;
    
    boolean mIsNowPlaying;
    //boolean mIsQueueView;
    public static final int FILTER_NORMAL = 1;
    public static final int FILTER_NOW_QUEUE = 2;
    public static final int FILTER_PLAYLIST = 3;
    int mFilterTypeBefore = FILTER_NORMAL;
    int mFilterType = FILTER_NORMAL;
    public void setFilterType( int i )
    {
    	if( mFilterType != i)
    	{
	    	mFilterTypeBefore = mFilterType;
	    	mFilterType = i;
    	}
    }
    public void clearFilterType()
    {
    	if( mFilterType == FILTER_NOW_QUEUE )
    		mFilterType = mFilterTypeBefore;
    }
    boolean mDisableNowPlayingIndicator;
    //private String genre;
    private String albumId;
    public void setAlbumId( String id )
    {
    	albumId = id;
    }
    private String artistId;
    public void setArtistId( String id )
    {
    	artistId = id;
    }
    // private final StringBuilder mBuilder = new StringBuilder();
    private final String mUnknownArtist;
        
    private OkosamaMediaPlayerActivity mActivity = null;
    //private TrackQueryHandler mQueryHandler;
    // private QueryHandler mQueryHandler;
    //private String mConstraint = null;
    //private boolean mConstraintIsValid = false;
    
    // ビュー保持用クラス
    static class ViewHolder {
        TextView line1;
        TextView line2;
        TextView line3;
        TextView duration;
        ImageView icon;        
        ImageView play_indicator;
        CharArrayBuffer buffer1;
        char [] buffer2;
    }
    
    public TrackListRawAdapter( OkosamaMediaPlayerActivity currentactivity, 
            int layout, ArrayList<TrackData> items,
            boolean isnowplaying, boolean disablenowplayingindicator,//String genre_,
            String albumId_, String artistId_, 	TabPage page ) {

        super(currentactivity, layout, items );
        this.page = page;
        mActivity = currentactivity;
        this.iLayoutId = layout;
        this.inflater = (LayoutInflater) currentactivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // mNowListOverlay = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.playlist_press);
        
        mActivity = currentactivity;
        // mQueryHandler = new QueryHandler( mActivity.getContentResolver() );
        //, mActivity);
        
        // albumとartistを表す文字列
        mUnknownArtist = mActivity.getString(R.string.unknown_artist_name);

        mIsNowPlaying = isnowplaying;
        mDisableNowPlayingIndicator = disablenowplayingindicator;
        //mUnknownAlbum = context.getString(R.string.unknown_album_name);
        //genre = genre_;
        albumId = albumId_;
        artistId = artistId_;

        Bitmap b = OkosamaMediaPlayerActivity.getResourceAccessor().createBitmapFromDrawableId( R.drawable.songtabbtn_selected);
        mDefaultAlbumIcon = new BitmapDrawable(mActivity.getResources(), b);
        mDefaultAlbumIcon.setFilterBitmap(false);
        // 色の少ない(8bit/色以下)デバイスに表示するときに、ディザをかけるかどうかを指定する。trueでディザする。遅い。
        mDefaultAlbumIcon.setDither(false);
        
        // mQueryHandler = new QueryHandler(currentactivity.getContentResolver(), this );
    }
    
    /**
     * カラムのインデックスを設定
     * @param cursor
     */
    private int getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// 各カラムのインデックスを設定
        	// タイトル、アーティスト、時間
            mTitleIdx = cursor.getColumnIndexOrThrow(MediaColumns.TITLE);
            mArtistIdx = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST);
            mDataIdx = cursor.getColumnIndexOrThrow(AudioColumns.DATA);
            mDurationIdx = cursor.getColumnIndexOrThrow(AudioColumns.DURATION);
            mArtistIdIdx = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST_ID);
            mAlbumIdx = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM);
            mAlbumIdIdx = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM_ID);
            //mAlbumArtIndex = cursor.getColumnIndexOrThrow(AlbumColumns.ALBUM_ART);
            
            try {
            	// オーディオ？まず、プレイリストのカラムidから取得を試み、失敗したらトラックのカラムidから取得するらしい
                mAudioIdIdx = cursor.getColumnIndexOrThrow(
                        MediaStore.Audio.Playlists.Members.AUDIO_ID);
            } catch (IllegalArgumentException ex) {
                mAudioIdIdx = cursor.getColumnIndexOrThrow(MediaColumns._ID);
                return 1;
            }
        }
        return 0;
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
           
           // viewholderに各ビューを設定し、タグに設定する
           vh.line1 = (TextView) v.findViewById(R.id.line1);
           vh.line2 = (TextView) v.findViewById(R.id.line2);
           vh.line3 = (TextView) v.findViewById(R.id.line3);
           vh.duration = (TextView) v.findViewById(R.id.duration);
           vh.icon = (ImageView) v.findViewById(R.id.icon);
	       vh.icon.setBackgroundDrawable(mDefaultAlbumIcon);
	       vh.icon.setPadding(0, 0, 1, 0);
           vh.play_indicator = (ImageView) v.findViewById(R.id.play_indicator);
           vh.buffer1 = new CharArrayBuffer(100);
           vh.buffer2 = new char[200];
           v.setTag(vh);
    	}
	    bindView(v,mActivity,pos);
    	return v;
    }
    
    /**
     * ビューへの値を設定
     */
    // @Override
    public void bindView(View view, Context context, int pos ) { // TrackData data) {
    	TrackData data = getItem( pos );
    	if( data == null )
    	{
    		return;
    	}
    	// ビューホルダーから各ビューを取得
        ViewHolder vh = (ViewHolder) view.getTag();
        
        // バッファに、タイトルの文字列を一度取得後、ビューに設定？TODO:なぜ？
        // data.getTrackTitle();
        // cursor.copyStringToBuffer(mTitleIdx, vh.buffer1);
        vh.line1.setText(data.getName());
       
        // 時間を取得、設定
        int secs = (int) (data.getTrackDuration()) / 1000; // .getInt(mDurationIdx) / 1000;
        if (secs == 0) {
            vh.duration.setText("");
        } else {
            vh.duration.setText(ResourceAccessor.makeTimeString(context, secs));
        }
        
//        final StringBuilder builder = mBuilder;
//        builder.delete(0, builder.length());

        // アーティスト名が取得できたら、ビューに設定
        String name = data.getTrackArtist();
        if (name == null || name.equals(MediaStore.UNKNOWN_STRING)) {
            //builder.append(mUnknownArtist);
        	name = mUnknownArtist;
        } 
//        else {
//            builder.append(name);
//        }
//        int len = builder.length();
//        if (vh.buffer2.length < len) {
//            vh.buffer2 = new char[len];
//        }
        //builder.getChars(0, len, vh.buffer2, 0);
        vh.line2.setText(name);//vh.buffer2, 0, len);

        // ジャンルが取得できたら、ビューに設定
        String genre = mActivity.getGenreStocker().getGenreOfAudioString( 
        		data.getTrackAudioId() );
//        Log.i("genre-get",data.getTrackId() + " " + data.getTrackAudioId() 
//        		+ " " + data.getTrackTitle() + genre );
        if( genre != null )
        {
        	vh.line3.setText(genre);
        	vh.line3.setVisibility(View.VISIBLE);
        }
        else
        {
        	vh.line3.setVisibility(View.GONE);
        }
        
        ImageView iv = vh.icon;
        String art = data.getTrackAlbumArt();//cursor.getString(mAlbumArtIndex);
        long aid = Long.parseLong(data.getTrackAlbumId() );//cursor.getLong(0);
        if ( art == null || art.length() == 0) {
            iv.setImageDrawable(null);
        } else {
            Drawable d = MediaPlayerUtil.getCachedArtwork(
            		mActivity, aid, mDefaultAlbumIcon);
            iv.setImageDrawable(d);
        }
        
        // 
        ImageView ivInd = vh.play_indicator;
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
        
        if ( (mIsNowPlaying && pos == id) ||
             (!mIsNowPlaying && !mDisableNowPlayingIndicator 
            		 && data.getTrackAudioId() == id)) {
        	// 再生中で再生中のものか、
        	// 再生中でなく、NowPlayingIndicatorを表示しない設定でもなく、現在設定使用としているのが待機中の曲と同じなら
        	// プレイリストのイメージを設定？
        	ivInd.setImageResource(R.drawable.indicator_ic_mp_playing_list);
        	ivInd.setVisibility(View.VISIBLE);
        } else {
        	// プレイリストのイメージを非表示に？
        	ivInd.setVisibility(View.GONE);
        }
        
    }
    
    
    static ArrayList<TabPage> arrPage = new ArrayList<TabPage>();
    /**
     * カーソルから、アダプタのデータを設定する
     * @param cursor
     * @return
     */
    public int stockMediaDataFromDevice(final TabPage page)
    {
    	if( bDataUpdating == true )
    	{
        	if( page != null )
        	{
        		page.startUpdate();
        		if( arrPage.contains(page) == false )
        			arrPage.add(page);
        	}
    		return -1;
    	}
    	bDataUpdating = true;
    	Log.i("stockMediaDataFromDevice","start");
     	
    	if( page != null )
    	{
    		page.startUpdate();
    		if( arrPage.contains(page) == false )
    			arrPage.add(page);
    	}
            	
        AsyncTask<Cursor, Void, Integer> task = new AsyncTask<Cursor, Void, Integer>() {
            @Override
            protected Integer doInBackground(Cursor... params) {
            	Log.i("doInBackground","start");
            	
            	// カーソルをループする
    			Cursor cursor = Database.getInstance(
    					OkosamaMediaPlayerActivity.isExternalRef()
    			).createTrackCursor(null, null);			
            	
        		if( cursor == null || cursor.isClosed() )
        		{
        			Log.w("TrackListAdp - doInBk", "cursor closed!");
        			return -1;
        		}
        		
        		try {
	        		if( 0 > getColumnIndices(cursor) 
	        		|| mActivity.getAdapter(TabPage.TABPAGE_ID_ALBUM) == null )
	        		{
	        			return -1;
	        		}
	        		synchronized(allItems)
	        		{
		            	allItems.clear();
		            	if( 0 < cursor.getCount() )
		            	{
		            	
			            	Log.i("doInBackground","moveToFirst");
			        		cursor.moveToFirst();
			        		do 
			        		{
			            		TrackData data = new TrackData();
			        			// 全ての要素をループする
			            		data.setDataId( cursor.getInt(0));
			            		data.setName(cursor.getString(mTitleIdx));
			            		data.setTrackData(cursor.getString(mDataIdx));
			            		data.setTrackArtist(cursor.getString(mArtistIdx));
			            		data.setTrackDuration(cursor.getLong(mDurationIdx));
			            		data.setTrackAudioId(cursor.getLong(mAudioIdIdx));
			            		data.setTrackAlbum(cursor.getString(mAlbumIdx));
			            		data.setTrackAlbumId(cursor.getString(mAlbumIdIdx));
			            		data.setTrackArtistId(cursor.getString(mArtistIdIdx));
				          		// Log.i("add","albumID:" + data.getTrackAlbumId() + "(" + data.getTrackAlbum() + ")" );
			            		if( data.getTrackAlbumId() != null )
			            		{
				        			data.setTrackAlbumArt(
				        					((AlbumListRawAdapter)mActivity.getAdapter(
				        						TabPage.TABPAGE_ID_ALBUM)).getAlbumArtFromId(
				        								Integer.parseInt(data.getTrackAlbumId())));
			            		}
			            	    allItems.add(data);
			        		} while( deleted == false //OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() == false && 
			        				&& cursor.moveToNext() );
		            	}
	        		}
        		} finally {
        			cursor.close();
        		}
        		if( deleted ) //OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() )
        			return -2;
        		
                return 0;
            }

            @Override
            protected void onPostExecute(Integer ret) 
            {
            	Log.d("onPostExecute","ret=" + ret );
            	
            	if( ret < 0 )
            	{
            		bLastError = true;
            	}
            	// 格納終了
            	// 二重管理になってしまっているが、アダプタにも同様のデータを格納する
            	updateList();
            	if( arrPage.isEmpty() == false )
            	{
            		for( TabPage page : arrPage )
            			page.endUpdate();
            	}
            	
            	bDataUpdating = false;            	
            }
        };
        task.execute();
        return 0;
    }
    
    /**
     * 表示すべきデータかどうかを返却する
     * @param data
     * @return
     */
    boolean isShowData(TrackData data)
    {    	
    	if( mFilterType == FILTER_NOW_QUEUE 
    	|| mFilterType == FILTER_PLAYLIST )
    	{
    		if( playlist != null )
    		{
	    		// 再生キューにあるものしか表示しない
	    		for( int i=0; i< playlist.length; ++i )
	    		{
	    			if( playlist[i].getId() == data.getTrackAudioId() )
	    			{
	    				return true;
	    			}
	    		}
    		}
    		else
    		{
    		}
			return false;
    	}
    	else if( mFilterType == FILTER_NORMAL )
    	{
	    	// albumIDのチェック
	    	if( albumId != null && 0 < albumId.length() ) 
	    	{
	    		// Log.d("isShowData"," albumId:" + data.getTrackAlbumId() );     		
	    		if( albumId.equals(data.getTrackAlbumId()) )
	    		{
	    			// return true;
	    		}
	    		else
	    		{
	    			return false;
	    		}
	    	}
	    	// アーティストIDのチェック
	    	if( artistId != null && 0 < artistId.length() ) 
	    	{
	    		//Log.d("isShowData"," artistId:" + data.getTrackArtistId() );     		
	    		if( artistId.equals(data.getTrackArtistId()) )
	    		{
	    			// return true;
	    		}
	    		else
	    		{
	    			return false;
	    		}
	    	}
    	}
    	return true;
    	
    }
    
    /**
     * カーソルを変更する
     */
    // @Override
    public void updateList() {
    	Log.d("trackadp - update list","");
    	
    	// 検索条件のリセット
    	playlist = null;
    	if( mFilterType == FILTER_NOW_QUEUE ) 
    	{
        	if (MediaPlayerUtil.sService == null) 
        	{
        		return;
        	}
	       	try {
	       		long [] listId = MediaPlayerUtil.sService.getQueue();
	       		int [] listType = MediaPlayerUtil.sService.getMediaType();
	       		playlist = new MediaInfo[listId.length];
		       	for( int i=0; i<listId.length; i++ )
		       	{
		       		playlist[i] = new MediaInfo( listId[i], listType[i] );
		       	}
	    	} catch( RemoteException ex ) {
	    		Log.e("Error", "sService getQueue RemoteException occured!");
	    	}
    	}
    	else if( mFilterType == FILTER_PLAYLIST )
    	{
    		Log.d("parseLong",OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getPlaylistID());
    		playlist = Database.getSongListForPlaylist(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity()
    				, Long.parseLong(OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getPlaylistID()));
    	}
    	//TODO:FIX
		if( filterData != null )
		{
			if( filterData.getAlbumId() != null )
			{
				setAlbumId( filterData.getAlbumId() );
			}
			if( filterData.getArtistId() != null )
			{
				setArtistId( filterData.getArtistId() );
			}
		}
    	
    	setAlbumId( OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getAlbumID() );
    	setArtistId( OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getArtistID() );
				
    	currentAllAudioIds.clear();
    	ArrayList<TrackData> items = allItems;
    	clear();
    	
        // インデクサの設定
    	mSectionIndex.clear();
    	mSection.clear();
        String pre_initial = "";
        // int index_num = 0;  
    	
    	synchronized( allItems )
    	{
			// Log.d("id", "itemCount:" + allItems.size() + " albumID:" + albumId );
    		int index = 0;
			for (TrackData data : items) {
	    		// ここでフィルタをかけてしまう？
	    		if( false == isShowData( data )
	    		|| false == isFilterData( data )
	    		)
	    		{
	    			continue;
	    		}
	    	    add(data);
	    	    currentAllAudioIds.add(data.getTrackAudioId());
	    	    
	    	    // インデクサの設定
	    	    String initial = data.getName().substring(0, 1); // nameの頭文字を基準に区切る  
	    	    if(!initial.equalsIgnoreCase(pre_initial)){ // 頭文字の判定(頭文字が変わったら追加)  
	    	    	mSectionIndex.add( index );//+ index_num); 
	    	        mSection.add(initial);
	    	        // index_num++;
	    	        pre_initial = initial;
	    	    }
	    	    
	        	if( maxShowCount < this.getCount() )
	    		{
	    			// maxの表示件数以上は、表示しない
	    			// TODO:ページきりかえ未対応なので、最初のmaxShowCount件しか表示できていない
	    			break;
	    		}
	    	    index++;
	    	}
    	}
    	// インデクサの設定
    	// ArrayList<String> sectionList = new ArrayList<String>(mSection.keySet());   
        //Collections.sort(sectionList);    	
        sections = new String[mSection.size()];  
        mSection.toArray(sections);
    	
    	notifyDataSetChanged();
    }

	/**
	 * @return the allItems
	 */
	public ArrayList<TrackData> getAllItems() {
		return allItems;
	}

	/**
	 * @param allItems the allItems to set
	 */
	public void setAllItems(ArrayList<TrackData> allItems) {
		this.allItems = allItems;
	}
    
    @Override
    // 曲の変更時など、状態が変わったときに、外部から表示を更新させる
	public int updateStatus()
    {
    	updateList();    	
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
		return getCount();
	}

	@Override
	public void initialize() {
   		if( 0 < mActivity.getTrackAdp().getCount() 
   		&& false == mActivity.getTrackAdp().isLastErrored() )
   		{
   			mActivity.getTrackAdp().updateStatus();
   		}
   		else
   		{
   			mActivity.reScanMediaOfMediaTab(TabPage.TABPAGE_ID_SONG);
   		}
	}
	@Override
	public int getPositionForSection(int section) {
		// 指定されたセクションが始まるポジションのindexを返す
		return mSectionIndex.get(section);
	}
	@Override
	public int getSectionForPosition(int position) {
		return 1;
	}
	@Override
	public Object[] getSections() {
		return sections;
	}
	@Override
	public void clearAdapterData() {
		deleted = true;
		this.clear();		
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
	public boolean isFilterData(TrackData data) {
		boolean bRet = true;
		if( filterData != null && data != null)
		{
			// アーティストID
			// TODO: アーティスト名
			if( filterData.getArtistId() != null )
			{
				if( data.getTrackArtistId() != null
				&& filterData.getArtistId().equals(data.getTrackArtistId()) )
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
				if( data.getTrackArtist() != null
				&& -1 != data.getTrackArtist().indexOf(filterData.getStrArtist()) )
				{
					// アーティスト名が一部一致
					bRet = true;
				}
				else
				{
					return false;
				}
			}
			
			// アルバムID
			// TODO: アルバム名			
			if( filterData.getAlbumId() != null )
			{
		    	//setAlbumId( filterData.getAlbumId() );//OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getAlbumID() );
				
				if( data.getTrackAlbumId() != null
				&& filterData.getAlbumId().equals(data.getTrackAlbumId()) )
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
			// アルバム名
			if( filterData.getStrAlbum() != null && 0 < filterData.getStrAlbum().length() )
			{
				if( data.getTrackAlbum() != null
				&& -1 != data.getTrackAlbum().indexOf(filterData.getStrAlbum()) )
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
				ArrayList<GenreData> genres = mActivity.getGenreStocker().getGenreOfAudio( 
		        		data.getTrackAudioId() );
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
				if( bNoHit )
				{
					return false;
				}
			}
			// TODO:そんなものでフィルタをかける機能は不要とは思うが、念のためトラックIDも
			
			
			// トラック名
			if( filterData.getStrSong() != null && 0 < filterData.getStrSong().length() )
			{
				if( data.getName() != null
				&& -1 != data.getName().indexOf(filterData.getStrSong()) )
				{
					// トラック名が一部一致
					bRet = true;
				}
				else
				{
					return false;
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
