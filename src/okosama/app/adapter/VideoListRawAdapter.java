package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.ResourceAccessor;
import okosama.app.storage.Database;
import okosama.app.storage.FilterData;
import okosama.app.storage.VideoData;
import okosama.app.tab.TabPage;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.VideoColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ビデオのリストのアダプタ
 * @author 25689
 *
 */
public class VideoListRawAdapter extends ArrayAdapter<VideoData> 
implements IAdapterUpdate<VideoData> { // , IFilterable<VideoData> {
	boolean deleted = false;
	private ArrayList<VideoData> allItems = new ArrayList<VideoData>();
	// TODO:次へボタン等
	int maxShowCount = 80;

	long [] playlist = null;
    private final BitmapDrawable mDefaultIcon;
	TabPage page;
	boolean bDataUpdating = false;	// 内部データを更新中かどうか
	boolean bLastError = false;
	public boolean isDataUpdating()
	{
		return bDataUpdating;
	}
	private LayoutInflater inflater;
	private int iLayoutId;
    ArrayList<Long> currentAllVideoIds = new ArrayList<Long>();
    public long[] getCurrentAllVideoIds()
    {
    	long[] ret = new long[currentAllVideoIds.size()];
    	int i = 0;
    	for( Long lng : currentAllVideoIds )
    	{
    		ret[i] = lng;
    		i++;
    	}
    	return ret;
    }

	// カラムのインデックス保持用 
    int mTitleIdx;
    int mDataIdx;
    int mArtistIdx;
    int mDurationIdx;
    int mVideoIdIdx;
    int mMineTypeIdx;
    
    private final StringBuilder mBuilder = new StringBuilder();
    private final String mUnknownArtist;
    private OkosamaMediaPlayerActivity mActivity = null;
    
    // ビュー保持用クラス
    static class ViewHolder {
        TextView line1;
        TextView line2;
        TextView duration;
        ImageView icon;        
        ImageView play_indicator;
        CharArrayBuffer buffer1;
        char [] buffer2;
    }
    
    public VideoListRawAdapter( OkosamaMediaPlayerActivity currentactivity,
    		int layout, ArrayList<VideoData> items, TabPage page) {

    	super(currentactivity, layout, items );
   
        this.page = page;
        mActivity = currentactivity;
        this.iLayoutId = layout;
        this.inflater = (LayoutInflater) currentactivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // mNowListOverlay = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.playlist_press);
        
        mActivity = currentactivity;
        mUnknownArtist = mActivity.getString(R.string.unknown_artist_name);

        Bitmap b = OkosamaMediaPlayerActivity.getResourceAccessor().createBitmapFromDrawableId( 
        		R.drawable.video_focus );
        mDefaultIcon = new BitmapDrawable(mActivity.getResources(), b);
        mDefaultIcon.setFilterBitmap(false);
        // 色の少ない(8bit/色以下)デバイスに表示するときに、ディザをかけるかどうかを指定する。trueでディザする。遅い。
        mDefaultIcon.setDither(false);
        
    }
    
    /**
     * カラムのインデックスを設定
     * @param cursor
     */
    private int getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// 各カラムのインデックスを設定
        	// タイトル、アーティスト、時間
            mVideoIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            mTitleIdx = cursor.getColumnIndexOrThrow(MediaColumns.TITLE);
            mDataIdx = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            mArtistIdx = cursor.getColumnIndexOrThrow(VideoColumns.ARTIST);
            mDurationIdx = cursor.getColumnIndexOrThrow(VideoColumns.DURATION);
            mMineTypeIdx = cursor.getColumnIndexOrThrow(VideoColumns.MIME_TYPE);
            // mAlbumIdx = cursor.getColumnIndexOrThrow(VideoColumns.ALBUM);            
            
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
           vh.duration = (TextView) v.findViewById(R.id.duration);
           vh.icon = (ImageView) v.findViewById(R.id.icon);
	       vh.icon.setBackgroundDrawable(mDefaultIcon);
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
    public void bindView(View view, Context context, int pos ) {
    	VideoData data = getItem( pos );
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
        int secs = (int) (data.getDuration()) / 1000; // .getInt(mDurationIdx) / 1000;
        if (secs == 0) {
            vh.duration.setText("");
        } else {
            vh.duration.setText(ResourceAccessor.makeTimeString(context, secs));
        }
        
        final StringBuilder builder = mBuilder;
        builder.delete(0, builder.length());

        // アーティスト名が取得できたら、ビューに設定
        String name = data.getArtist();
        if (name == null || name.equals(MediaStore.UNKNOWN_STRING)) {
            // builder.append(mUnknownArtist);
        } else {
            builder.append(name);
            int len = builder.length();
            if (vh.buffer2.length < len) {
                vh.buffer2 = new char[len];
            }
            builder.getChars(0, len, vh.buffer2, 0);
            vh.line2.setText(vh.buffer2, 0, len);
        }

        ImageView iv = vh.icon;        
//        String art = data.getTrackAlbumArt();//cursor.getString(mAlbumArtIndex);
//        long aid = Long.parseLong(data.getTrackAlbumId() );//cursor.getLong(0);
        //if ( art == null || art.length() == 0) {
            iv.setImageDrawable(null);
//        } else {
//            Drawable d = MediaPlayerUtil.getCachedArtwork(mActivity, aid, mDefaultIcon);
//            iv.setImageDrawable(d);
//        }
        
        // 
        //ImageView ivInd = vh.play_indicator;
        // サービスがあり、再生中ならば、idにキューの位置を
        // 再生中でなければ、idにオーディオid
//        long id = -1;
//        if (MediaPlayerUtil.sService != null) {
//            // TODO: IPC call on each bind??
//            try {
////                    id = MediaPlayerUtil.sService.getVideoId();
//            } catch (RemoteException ex) {
//            }
//        }
        
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
            	// Cursor cursor = params[0];
    			// OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistID( null );        	
    			Cursor cursor = Database.getInstance(
    					OkosamaMediaPlayerActivity.isExternalRef()
    			).createVideoCursor();			
            	
        		if( cursor == null || cursor.isClosed() )
        		{
        			Log.w("VideoListAdp - doInBk", "cursor closed!");
        			return -1;
        		}
        		
        		try {
	        		if( 0 > getColumnIndices(cursor) )
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
			            		VideoData data = new VideoData();
			        			// 全ての要素をループする
			            		data.setDataId( cursor.getInt(mVideoIdIdx));
			            		data.setName(cursor.getString(mTitleIdx));
			            		data.setArtist(cursor.getString(mArtistIdx));
			            		data.setDuration(cursor.getLong(mDurationIdx));
			            		data.setType(cursor.getString(mMineTypeIdx));
			            		// data.setData(cursor.getLong(mDataIdIdx));
	//		        			data.setTrackAlbumArt(
	//		        					((AlbumListRawAdapter)mActivity.getAdapter(TabPage.TABPAGE_ID_ALBUM)).getAlbumArtFromId(Integer.parseInt(data.getTrackAlbumId())));
			          		// Log.i("add","albumID:" + data.getTrackAlbumId() + "(" + data.getTrackAlbum() + ")" );
			            	    allItems.add(data);
			        		} while( deleted == false // OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() == false && 
			        				&& cursor.moveToNext() );
		            	}
	        		}
        		} finally {
        			cursor.close();
        		}
        		if( deleted )//OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() )
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
            	// TabPage page = (TabPage) mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_MEDIA).getChild(TabPage.TABPAGE_ID_SONG);
            	if( arrPage.isEmpty() == false )
            	{
            		for( TabPage page : arrPage )
            			page.endUpdate();
            	}
//            	TabPage page2 = (TabPage) mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_PLAY).getChild(TabPage.TABPAGE_ID_NOW_PLAYLIST);
//            	if( page2 != null )
//            	{
//            		page2.endUpdate();
//            	}
            	
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
    boolean isShowData(VideoData data)
    {
    	// TODO: 現状、自動でキューを表示するかそうでないかを切り替えているが、
    	// 自動ではなく、ユーザに選択させる
    	// boolean bShow = true;
    	// boolean bQueueExists = false;
 
//    	if( playlist != null && 0 < playlist.length )
//    	{
//    		bQueueExists = true;
//    		Log.d("debug","queue exists");
//    	}
    	
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
				
    	currentAllVideoIds.clear();
    	ArrayList<VideoData> items = allItems;
    	clear();
    	
    	synchronized( allItems )
    	{
			// Log.d("id", "itemCount:" + allItems.size() + " albumID:" + albumId );
			for (VideoData data : items) {
	    		// ここでフィルタをかけてしまう？
	    		if( false == isShowData( data ) 
	    		|| false == isFilterData( data ))
	    		{
	    			continue;
	    		}
	    	    add(data);
	    	    currentAllVideoIds.add(data.getDataId());
	    		if( maxShowCount < this.getCount() )
	    		{
	    			// maxの表示件数以上は、表示しない
	    			// TODO:ページきりかえ未対応なので、最初の80件しか表示できていない
	    			break;
	    		}
	    	}
    	}
    	notifyDataSetChanged();
    }

	/**
	 * @return the allItems
	 */
	public ArrayList<VideoData> getAllItems() {
		return allItems;
	}

	/**
	 * @param allItems the allItems to set
	 */
	public void setAllItems(ArrayList<VideoData> allItems) {
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
   		if( 0 < mActivity.getVideoAdp().getCount() 
   		&& false == mActivity.getVideoAdp().isLastErrored() )
   		{
   			mActivity.getVideoAdp().updateStatus();
   		}
   		else
   		{
   			mActivity.reScanMediaOfMediaTab(TabPage.TABPAGE_ID_VIDEO);
   		}
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
	public boolean isFilterData(VideoData data) {
		boolean bRet = true;
		if( filterData != null && data != null)
		{
			// 動画名
			if( filterData.getStrVideo() != null && 0 < filterData.getStrVideo().length() )
			{
				if( data.getName() != null
				&& -1 != data.getName().indexOf(filterData.getStrVideo()) )
				{
					// 動画名が一部一致
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
