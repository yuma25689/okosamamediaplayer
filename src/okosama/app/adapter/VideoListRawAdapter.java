package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.LogWrapper;
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
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video.VideoColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * �r�f�I�̃��X�g�̃A�_�v�^
 * @author 25689
 *
 */
public class VideoListRawAdapter extends ArrayAdapter<VideoData> 
implements IAdapterUpdate<VideoData> { // , IFilterable<VideoData> {
	boolean deleted = false;
	private ArrayList<VideoData> allItems = new ArrayList<VideoData>();
	// TODO:���փ{�^����
	int maxShowCount = 80;

	long [] playlist = null;
    private final BitmapDrawable mDefaultIcon;
	TabPage page;
	boolean bDataUpdating = false;	// �����f�[�^���X�V�����ǂ���
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

	// �J�����̃C���f�b�N�X�ێ��p 
    int mTitleIdx;
    int mDataIdx;
    int mArtistIdx;
    int mDurationIdx;
    int mVideoIdIdx;
    int mMineTypeIdx;
    
    private final StringBuilder mBuilder = new StringBuilder();
    private final String mUnknownArtist;
    private OkosamaMediaPlayerActivity mActivity = null;
    
    // �r���[�ێ��p�N���X
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
        // �F�̏��Ȃ�(8bit/�F�ȉ�)�f�o�C�X�ɕ\������Ƃ��ɁA�f�B�U�������邩�ǂ������w�肷��Btrue�Ńf�B�U����B�x���B
        mDefaultIcon.setDither(false);
        
    }
    
    /**
     * �J�����̃C���f�b�N�X��ݒ�
     * @param cursor
     */
    private int getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// �e�J�����̃C���f�b�N�X��ݒ�
        	// �^�C�g���A�A�[�e�B�X�g�A����
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
     * �V�����r���[�̍쐬�H
     */
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
    	View v = convertView;  
    	if (v == null) {
    	   ViewHolder vh = new ViewHolder();
	       v = inflater.inflate(iLayoutId, null);     	   

	       // viewholder�Ɋe�r���[��ݒ肵�A�^�O�ɐݒ肷��
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
     * �r���[�ւ̒l��ݒ�
     */
    // @Override
    public void bindView(View view, Context context, int pos ) {
    	VideoData data = getItem( pos );
    	if( data == null )
    	{
    		return;
    	}
    	// �r���[�z���_�[����e�r���[���擾
        ViewHolder vh = (ViewHolder) view.getTag();
        
        // �o�b�t�@�ɁA�^�C�g���̕��������x�擾��A�r���[�ɐݒ�HTODO:�Ȃ��H
        // data.getTrackTitle();
        // cursor.copyStringToBuffer(mTitleIdx, vh.buffer1);
        vh.line1.setText(data.getName());
       
        // ���Ԃ��擾�A�ݒ�
        int secs = (int) (data.getDuration()) / 1000; // .getInt(mDurationIdx) / 1000;
        if (secs == 0) {
            vh.duration.setText("");
        } else {
            vh.duration.setText(ResourceAccessor.makeTimeString(context, secs));
        }
        
        final StringBuilder builder = mBuilder;
        builder.delete(0, builder.length());

        // �A�[�e�B�X�g�����擾�ł�����A�r���[�ɐݒ�
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
        // �T�[�r�X������A�Đ����Ȃ�΁Aid�ɃL���[�̈ʒu��
        // �Đ����łȂ���΁Aid�ɃI�[�f�B�Iid
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
     * �J�[�\������A�A�_�v�^�̃f�[�^��ݒ肷��
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
    	LogWrapper.i("stockMediaDataFromDevice","start");
    	    	
    	if( page != null )
    	{
    		page.startUpdate();
    		if( arrPage.contains(page) == false )
    			arrPage.add(page);
    	}
            	
        AsyncTask<Cursor, Void, Integer> task = new AsyncTask<Cursor, Void, Integer>() {
            @Override
            protected Integer doInBackground(Cursor... params) {
            	LogWrapper.i("doInBackground","start");
            	
            	// �J�[�\�������[�v����
            	// Cursor cursor = params[0];
    			// OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistID( null );        	
    			Cursor cursor = Database.getInstance(
    					OkosamaMediaPlayerActivity.isExternalRef()
    			).createVideoCursor();			
            	
        		if( cursor == null || cursor.isClosed() )
        		{
        			LogWrapper.w("VideoListAdp - doInBk", "cursor closed!");
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
			            	LogWrapper.i("doInBackground","moveToFirst");
			        		cursor.moveToFirst();
			        		do 
			        		{
			            		VideoData data = new VideoData();
			        			// �S�Ă̗v�f�����[�v����
			            		data.setDataId( cursor.getInt(mVideoIdIdx));
			            		data.setName(cursor.getString(mTitleIdx));
			            		data.setArtist(cursor.getString(mArtistIdx));
			            		data.setDuration(cursor.getLong(mDurationIdx));
			            		data.setType(cursor.getString(mMineTypeIdx));
			            		// data.setData(cursor.getLong(mDataIdIdx));
	//		        			data.setTrackAlbumArt(
	//		        					((AlbumListRawAdapter)mActivity.getAdapter(TabPage.TABPAGE_ID_ALBUM)).getAlbumArtFromId(Integer.parseInt(data.getTrackAlbumId())));
			          		// LogWrapper.i("add","albumID:" + data.getTrackAlbumId() + "(" + data.getTrackAlbum() + ")" );
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
            	// �i�[�I��
            	// ��d�Ǘ��ɂȂ��Ă��܂��Ă��邪�A�A�_�v�^�ɂ����l�̃f�[�^���i�[����
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
     * �\�����ׂ��f�[�^���ǂ�����ԋp����
     * @param data
     * @return
     */
    boolean isShowData(VideoData data)
    {
    	// TODO: ����A�����ŃL���[��\�����邩�����łȂ�����؂�ւ��Ă��邪�A
    	// �����ł͂Ȃ��A���[�U�ɑI��������
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
     * �J�[�\����ύX����
     */
    // @Override
    public void updateList() {
    	Log.d("trackadp - update list","");
    	
    	// ���������̃��Z�b�g
    	playlist = null;
				
    	currentAllVideoIds.clear();
    	ArrayList<VideoData> items = allItems;
    	clear();
    	
    	synchronized( allItems )
    	{
			// Log.d("id", "itemCount:" + allItems.size() + " albumID:" + albumId );
			for (VideoData data : items) {
	    		// �����Ńt�B���^�������Ă��܂��H
	    		if( false == isShowData( data ) 
	    		|| false == isFilterData( data ))
	    		{
	    			continue;
	    		}
	    	    add(data);
	    	    currentAllVideoIds.add(data.getDataId());
	    		if( maxShowCount < this.getCount() )
	    		{
	    			// max�̕\�������ȏ�́A�\�����Ȃ�
	    			// TODO:�y�[�W���肩�����Ή��Ȃ̂ŁA�ŏ���80�������\���ł��Ă��Ȃ�
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
    // �Ȃ̕ύX���ȂǁA��Ԃ��ς�����Ƃ��ɁA�O������\�����X�V������
	public int updateStatus()
    {
    	updateList();    	
    	// �\�����X�V?
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
	 *��:�Ȃ񂩕ς����ǁA�\���Ώۂ̏ꍇ�Atrue
	 */	
	@Override
	public boolean isFilterData(VideoData data) {
		boolean bRet = true;
		if( filterData != null && data != null)
		{
			// ���於
			if( filterData.getStrVideo() != null && 0 < filterData.getStrVideo().length() )
			{
				if( data.getName() != null
				&& -1 != data.getName().indexOf(filterData.getStrVideo()) )
				{
					// ���於���ꕔ��v
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
