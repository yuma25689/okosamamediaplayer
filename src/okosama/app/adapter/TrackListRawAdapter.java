package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.LogWrapper;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.ResourceAccessor;
import okosama.app.service.MediaInfo;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.Database;
import okosama.app.storage.FilterData;
import okosama.app.storage.GenreData;
import okosama.app.storage.TrackData;
import okosama.app.tab.TabPage;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
//import java.util.Collections;
//import java.util.HashMap;
//import okosama.app.ControlIDs;
//import okosama.app.adapter.AlbumListRawAdapter.ViewHolder;
//import okosama.app.storage.AlbumData;
// import okosama.app.storage.QueryHandler;
//import okosama.app.storage.TrackQueryHandler;
//import android.provider.BaseColumns;
//import android.provider.MediaStore.Audio.AlbumColumns;
//import android.widget.AlphabetIndexer;
//import android.widget.AlphabetIndexer;
//import android.widget.SectionIndexer;
//import android.widget.SimpleCursorAdapter;

/**
 * �g���b�N�̃��X�g�̃A�_�v�^
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
	// TODO:���փ{�^����
	int maxShowCount = 500;

	MediaInfo [] playlist = null;
    private final BitmapDrawable mDefaultAlbumIcon;
	TabPage page;
	boolean bDataUpdating = false;	// �����f�[�^���X�V�����ǂ���
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

	// �J�����̃C���f�b�N�X�ێ��p 
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
    
    // �r���[�ێ��p�N���X
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
        
        // album��artist��\��������
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
        // �F�̏��Ȃ�(8bit/�F�ȉ�)�f�o�C�X�ɕ\������Ƃ��ɁA�f�B�U�������邩�ǂ������w�肷��Btrue�Ńf�B�U����B�x���B
        mDefaultAlbumIcon.setDither(false);
        
        // mQueryHandler = new QueryHandler(currentactivity.getContentResolver(), this );
    }
    
    /**
     * �J�����̃C���f�b�N�X��ݒ�
     * @param cursor
     */
    private int getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// �e�J�����̃C���f�b�N�X��ݒ�
        	// �^�C�g���A�A�[�e�B�X�g�A����
            mTitleIdx = cursor.getColumnIndexOrThrow(MediaColumns.TITLE);
            mArtistIdx = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST);
            mDataIdx = cursor.getColumnIndexOrThrow(AudioColumns.DATA);
            mDurationIdx = cursor.getColumnIndexOrThrow(AudioColumns.DURATION);
            mArtistIdIdx = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST_ID);
            mAlbumIdx = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM);
            mAlbumIdIdx = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM_ID);
            //mAlbumArtIndex = cursor.getColumnIndexOrThrow(AlbumColumns.ALBUM_ART);
            
            try {
            	// �I�[�f�B�I�H�܂��A�v���C���X�g�̃J����id����擾�����݁A���s������g���b�N�̃J����id����擾����炵��
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
     * �r���[�ւ̒l��ݒ�
     */
    // @Override
    public void bindView(View view, Context context, int pos ) { // TrackData data) {
    	TrackData data = getItem( pos );
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
        int secs = (int) (data.getTrackDuration()) / 1000; // .getInt(mDurationIdx) / 1000;
        if (secs == 0) {
            vh.duration.setText("");
        } else {
            vh.duration.setText(ResourceAccessor.makeTimeString(context, secs));
        }
        
//        final StringBuilder builder = mBuilder;
//        builder.delete(0, builder.length());

        // �A�[�e�B�X�g�����擾�ł�����A�r���[�ɐݒ�
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

        // �W���������擾�ł�����A�r���[�ɐݒ�
        String genre = mActivity.getGenreStocker().getGenreOfAudioString( 
        		data.getTrackAudioId() );
//        LogWrapper.i("genre-get",data.getTrackId() + " " + data.getTrackAudioId() 
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
        // �T�[�r�X������A�Đ����Ȃ�΁Aid�ɃL���[�̈ʒu��
        // �Đ����łȂ���΁Aid�ɃI�[�f�B�Iid
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
        	// �Đ����ōĐ����̂��̂��A
        	// �Đ����łȂ��ANowPlayingIndicator��\�����Ȃ��ݒ�ł��Ȃ��A���ݐݒ�g�p�Ƃ��Ă���̂��ҋ@���̋ȂƓ����Ȃ�
        	// �v���C���X�g�̃C���[�W��ݒ�H
        	ivInd.setImageResource(R.drawable.indicator_ic_mp_playing_list);
        	ivInd.setVisibility(View.VISIBLE);
        } else {
        	// �v���C���X�g�̃C���[�W���\���ɁH
        	ivInd.setVisibility(View.GONE);
        }
        
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
    			Cursor cursor = Database.getInstance(
    					OkosamaMediaPlayerActivity.isExternalRef()
    			).createTrackCursor(null, null);			
            	
        		if( cursor == null || cursor.isClosed() )
        		{
        			LogWrapper.w("TrackListAdp - doInBk", "cursor closed!");
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
		            	
			            	LogWrapper.i("doInBackground","moveToFirst");
			        		cursor.moveToFirst();
			        		do 
			        		{
			            		TrackData data = new TrackData();
			        			// �S�Ă̗v�f�����[�v����
			            		data.setDataId( cursor.getInt(0));
			            		data.setName(cursor.getString(mTitleIdx));
			            		data.setTrackData(cursor.getString(mDataIdx));
			            		data.setTrackArtist(cursor.getString(mArtistIdx));
			            		data.setTrackDuration(cursor.getLong(mDurationIdx));
			            		data.setTrackAudioId(cursor.getLong(mAudioIdIdx));
			            		data.setTrackAlbum(cursor.getString(mAlbumIdx));
			            		data.setTrackAlbumId(cursor.getString(mAlbumIdIdx));
			            		data.setTrackArtistId(cursor.getString(mArtistIdIdx));
				          		// LogWrapper.i("add","albumID:" + data.getTrackAlbumId() + "(" + data.getTrackAlbum() + ")" );
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
            	// �i�[�I��
            	// ��d�Ǘ��ɂȂ��Ă��܂��Ă��邪�A�A�_�v�^�ɂ����l�̃f�[�^���i�[����
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
     * �\�����ׂ��f�[�^���ǂ�����ԋp����
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
	    		// �Đ��L���[�ɂ�����̂����\�����Ȃ�
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
	    	// albumID�̃`�F�b�N
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
	    	// �A�[�e�B�X�gID�̃`�F�b�N
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
     * �J�[�\����ύX����
     */
    // @Override
    public void updateList() {
    	Log.d("trackadp - update list","");
    	
    	// ���������̃��Z�b�g
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
	    		LogWrapper.e("Error", "sService getQueue RemoteException occured!");
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
    	
        // �C���f�N�T�̐ݒ�
    	mSectionIndex.clear();
    	mSection.clear();
        String pre_initial = "";
        // int index_num = 0;  
    	
    	synchronized( allItems )
    	{
			// Log.d("id", "itemCount:" + allItems.size() + " albumID:" + albumId );
    		int index = 0;
			for (TrackData data : items) {
	    		// �����Ńt�B���^�������Ă��܂��H
	    		if( false == isShowData( data )
	    		|| false == isFilterData( data )
	    		)
	    		{
	    			continue;
	    		}
	    	    add(data);
	    	    currentAllAudioIds.add(data.getTrackAudioId());
	    	    
	    	    // �C���f�N�T�̐ݒ�
	    	    String initial = data.getName().substring(0, 1); // name�̓���������ɋ�؂�  
	    	    if(!initial.equalsIgnoreCase(pre_initial)){ // �������̔���(���������ς������ǉ�)  
	    	    	mSectionIndex.add( index );//+ index_num); 
	    	        mSection.add(initial);
	    	        // index_num++;
	    	        pre_initial = initial;
	    	    }
	    	    
	        	if( maxShowCount < this.getCount() )
	    		{
	    			// max�̕\�������ȏ�́A�\�����Ȃ�
	    			// TODO:�y�[�W���肩�����Ή��Ȃ̂ŁA�ŏ���maxShowCount�������\���ł��Ă��Ȃ�
	    			break;
	    		}
	    	    index++;
	    	}
    	}
    	// �C���f�N�T�̐ݒ�
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
		// �w�肳�ꂽ�Z�N�V�������n�܂�|�W�V������index��Ԃ�
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
	 *��:�Ȃ񂩕ς����ǁA�\���Ώۂ̏ꍇ�Atrue
	 */
	@Override
	public boolean isFilterData(TrackData data) {
		boolean bRet = true;
		if( filterData != null && data != null)
		{
			// �A�[�e�B�X�gID
			// TODO: �A�[�e�B�X�g��
			if( filterData.getArtistId() != null )
			{
				if( data.getTrackArtistId() != null
				&& filterData.getArtistId().equals(data.getTrackArtistId()) )
				{
					// �A�[�e�B�X�g���t�B���^�ΏہH
					// data.getTrackArtistId()�͖{���ɓ����Ă���̂��낤���E�E�E�BTODO:����
					bRet = true;
				}
				else
				{
					return false;
				}
			}
			// �A�[�e�B�X�g��
			if( filterData.getStrArtist() != null && 0 < filterData.getStrArtist().length() )
			{
				if( data.getTrackArtist() != null
				&& -1 != data.getTrackArtist().indexOf(filterData.getStrArtist()) )
				{
					// �A�[�e�B�X�g�����ꕔ��v
					bRet = true;
				}
				else
				{
					return false;
				}
			}
			
			// �A���o��ID
			// TODO: �A���o����			
			if( filterData.getAlbumId() != null )
			{
		    	//setAlbumId( filterData.getAlbumId() );//OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getAlbumID() );
				
				if( data.getTrackAlbumId() != null
				&& filterData.getAlbumId().equals(data.getTrackAlbumId()) )
				{
					// �A�[�e�B�X�g���t�B���^�ΏہH
					// data.getTrackArtistId()�͖{���ɓ����Ă���̂��낤���E�E�E�BTODO:����
					bRet = true;
				}
				else
				{
					return false;
				}
			}
			// �A���o����
			if( filterData.getStrAlbum() != null && 0 < filterData.getStrAlbum().length() )
			{
				if( data.getTrackAlbum() != null
				&& -1 != data.getTrackAlbum().indexOf(filterData.getStrAlbum()) )
				{
					// �A�[�e�B�X�g�����ꕔ��v
					bRet = true;
				}
				else
				{
					return false;
				}
			}
			
			// �W������ID
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
							// �W����������v
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
			// TODO:����Ȃ��̂Ńt�B���^��������@�\�͕s�v�Ƃ͎v�����A�O�̂��߃g���b�NID��
			
			
			// �g���b�N��
			if( filterData.getStrSong() != null && 0 < filterData.getStrSong().length() )
			{
				if( data.getName() != null
				&& -1 != data.getName().indexOf(filterData.getStrSong()) )
				{
					// �g���b�N�����ꕔ��v
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
