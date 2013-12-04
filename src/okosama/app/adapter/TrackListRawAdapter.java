package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.ResourceAccessor;
import okosama.app.service.MediaInfo;
//import okosama.app.adapter.AlbumListRawAdapter.ViewHolder;
import okosama.app.service.MediaPlayerUtil;
//import okosama.app.storage.AlbumData;
import okosama.app.storage.Database;
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
import android.provider.BaseColumns;
import android.provider.MediaStore;
//import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
//import android.widget.SectionIndexer;
//import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * �g���b�N�̃��X�g�̃A�_�v�^
 * @author 25689
 *
 */
public class TrackListRawAdapter extends ArrayAdapter<TrackData> implements IAdapterUpdate { 
//implements SectionIndexer {

	private ArrayList<TrackData> allItems = new ArrayList<TrackData>();
	// TODO:���փ{�^����
	int maxShowCount = 500;
    	
    // private final Drawable mNowListOverlay;

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
    int mAudioIdIdx;
    int mArtistIdIdx;
    int mAlbumIdx;
    int mAlbumIdIdx;
    //int mAlbumArtIndex;
    
    // �O������̐ݒ�l�ێ��p
	// TODO: �Ӗ��̒���
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
    private String genre;
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
    private final StringBuilder mBuilder = new StringBuilder();
    private final String mUnknownArtist;
    
    //private AlphabetIndexer mIndexer;
    
    private OkosamaMediaPlayerActivity mActivity = null;
    //private TrackQueryHandler mQueryHandler;
    // private QueryHandler mQueryHandler;
    //private String mConstraint = null;
    //private boolean mConstraintIsValid = false;
    
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
    
    public TrackListRawAdapter( OkosamaMediaPlayerActivity currentactivity, 
            int layout, ArrayList<TrackData> items,
            boolean isnowplaying, boolean disablenowplayingindicator,String genre_,
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
        genre = genre_;
        albumId = albumId_;
        artistId = artistId_;

        Bitmap b = OkosamaMediaPlayerActivity.getResourceAccessor().createBitmapFromDrawableId( R.drawable.songtabbtn_selected);
        mDefaultAlbumIcon = new BitmapDrawable(mActivity.getResources(), b);
        mDefaultAlbumIcon.setFilterBitmap(false);
        // �F�̏��Ȃ�(8bit/�F�ȉ�)�f�o�C�X�ɕ\������Ƃ��ɁA�f�B�U�������邩�ǂ������w�肷��Btrue�Ńf�B�U����B�x���B
        mDefaultAlbumIcon.setDither(false);
        
        // mQueryHandler = new QueryHandler(currentactivity.getContentResolver(), this );
    }
    
//    public void setActivity(OkosamaMediaPlayerActivity newactivity) {
//        mActivity = newactivity;
//    }
    
//    public QueryHandler getQueryHandler() {
//        return mQueryHandler;
//    }
    
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
                mAudioIdIdx = cursor.getColumnIndexOrThrow(BaseColumns._ID);
                return 1;
            }
            
            // �C���f�N�T�̐ݒ�
//            if (mIndexer != null) {
//                mIndexer.setCursor(cursor);
//            } else if (!mActivity.isEditMode()) {
//                String alpha = mActivity.getString(R.string.fast_scroll_alphabet);
//            
//                mIndexer = new MusicAlphabetIndexer(cursor, mTitleIdx, alpha);
//            }
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
           // �C���[�W�́A�G�f�B�b�g���[�h�̎��̂ݕ\������
//           ImageView iv = (ImageView) v.findViewById(R.id.icon);
//           if (mActivity.isEditMode()) {
//               iv.setVisibility(View.VISIBLE);
//               iv.setImageResource(R.drawable.ic_mp_move);
//           } else {
//               iv.setVisibility(View.GONE);
//           }
           
           // viewholder�Ɋe�r���[��ݒ肵�A�^�O�ɐݒ肷��
           vh.line1 = (TextView) v.findViewById(R.id.line1);
           vh.line2 = (TextView) v.findViewById(R.id.line2);
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
        vh.line1.setText(data.getTrackTitle());
       
        
        // ���Ԃ��擾�A�ݒ�
        int secs = (int) (data.getTrackDuration()) / 1000; // .getInt(mDurationIdx) / 1000;
        if (secs == 0) {
            vh.duration.setText("");
        } else {
            vh.duration.setText(ResourceAccessor.makeTimeString(context, secs));
        }
        
        final StringBuilder builder = mBuilder;
        builder.delete(0, builder.length());

        // �A�[�e�B�X�g�����擾�ł�����A�r���[�ɐݒ�
        String name = data.getTrackArtist();
        if (name == null || name.equals(MediaStore.UNKNOWN_STRING)) {
            builder.append(mUnknownArtist);
        } else {
            builder.append(name);
        }
        int len = builder.length();
        if (vh.buffer2.length < len) {
            vh.buffer2 = new char[len];
        }
        builder.getChars(0, len, vh.buffer2, 0);
        vh.line2.setText(vh.buffer2, 0, len);

        ImageView iv = vh.icon;        
        String art = data.getTrackAlbumArt();//cursor.getString(mAlbumArtIndex);
        long aid = Long.parseLong(data.getTrackAlbumId() );//cursor.getLong(0);
        if ( art == null || art.length() == 0) {
            iv.setImageDrawable(null);
        } else {
            Drawable d = MediaPlayerUtil.getCachedArtwork(mActivity, aid, mDefaultAlbumIcon);
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
        
        // Determining whether and where to show the "now playing indicator
        // is tricky, because we don't actually keep track of where the songs
        // in the current playlist came from after they've started playing.
        //
        // If the "current playlists" is shown, then we can simply match by position,
        // otherwise, we need to match by id. Match-by-id gets a little weird if
        // a song appears in a playlist more than once, and you're in edit-playlist
        // mode. In that case, both items will have the "now playing" indicator.
        // For this reason, we don't show the play indicator at all when in edit
        // playlist mode (except when you're viewing the "current playlist",
        // which is not really a playlist)
        if ( (mIsNowPlaying && pos == id) ||
             (!mIsNowPlaying && !mDisableNowPlayingIndicator 
            		 && data.getTrackAudioId() == id)) {
        	// �Đ����ōĐ����̂��̂��A
        	// �Đ����łȂ��ANowPlayingIndicator��\�����Ȃ��ݒ�ł��Ȃ��A���ݐݒ�g�p�Ƃ��Ă���̂��ҋ@���̋ȂƓ����Ȃ�
        	// �v���C���X�g�̃C���[�W��ݒ�H
        	// TODO:�v���C���X�g�̃C���[�W�Ƃ����̂��ǂ��������̂�������Ȃ��̂ŁA�m�F����
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
    	Log.i("stockMediaDataFromDevice","start");
    	
//    	if (mActivity.isFinishing() && cursor != null ) {
//        	// �A�N�e�B�r�e�B���I�����ŁA�܂��J�[�\�����c���Ă���ꍇ�A�J�[�\�����N���[�Y
//            cursor.close();
//            cursor = null;
//        }
        //Database.getInstance(mActivity).setCursor( Database.SongCursorName, cursor );
    	
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
            	
            	// �J�[�\�������[�v����
            	// Cursor cursor = params[0];
    			// OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistID( null );        	
    			Cursor cursor = Database.getInstance(
    					OkosamaMediaPlayerActivity.isExternalRef()
    			).createTrackCursor(null, null);			
            	
        		if( cursor == null || cursor.isClosed() )
        		{
        			Log.w("TrackListAdp - doInBk", "cursor closed!");
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
			            		TrackData data = new TrackData();
			        			// �S�Ă̗v�f�����[�v����
			            		data.setTrackId( cursor.getLong(0));
			            		data.setTrackTitle(cursor.getString(mTitleIdx));
			            		data.setTrackArtist(cursor.getString(mArtistIdx));
			            		data.setTrackDuration(cursor.getLong(mDurationIdx));
			            		data.setTrackAudioId(cursor.getLong(mAudioIdIdx));
			            		data.setTrackAlbum(cursor.getString(mAlbumIdx));
			            		data.setTrackAlbumId(cursor.getString(mAlbumIdIdx));
			            		data.setTrackArtistId(cursor.getString(mArtistIdIdx));
			        			data.setTrackAlbumArt(
			        					((AlbumListRawAdapter)mActivity.getAdapter(TabPage.TABPAGE_ID_ALBUM)).getAlbumArtFromId(Integer.parseInt(data.getTrackAlbumId())));
			          		// Log.i("add","albumID:" + data.getTrackAlbumId() + "(" + data.getTrackAlbum() + ")" );
			            	    allItems.add(data);
			        		} while( OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() == false && 
			        				cursor.moveToNext() );
		            	}
	        		}
        		} finally {
        			cursor.close();
        		}
        		if( OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() )
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
    boolean isShowData(TrackData data)
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
    	
    	if( mFilterType == this.FILTER_NOW_QUEUE 
    	|| mFilterType == this.FILTER_PLAYLIST )
    	// if( bQueueExists == true )
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
    	else if( mFilterType == this.FILTER_NORMAL )
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
    	if( mFilterType == this.FILTER_NOW_QUEUE ) 
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
    	else if( mFilterType == this.FILTER_PLAYLIST )
    	{
    		Log.d("parseLong",OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getPlaylistID());
    		playlist = Database.getSongListForPlaylist(OkosamaMediaPlayerActivity.getResourceAccessor().getActivity()
    				, Long.parseLong(OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getPlaylistID()));
    	}
    	setAlbumId( OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getAlbumID() );
    	setArtistId( OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getArtistID() );
				
    	currentAllAudioIds.clear();
    	ArrayList<TrackData> items = allItems;
    	clear();
    	
    	synchronized( allItems )
    	{
			// Log.d("id", "itemCount:" + allItems.size() + " albumID:" + albumId );
			for (TrackData data : items) {
	    		// �����Ńt�B���^�������Ă��܂��H
	    		if( false == isShowData( data ) )
	    		{
	    			continue;
	    		}
	    	    add(data);
	    	    currentAllAudioIds.add(data.getTrackAudioId());
	        	// Log.d("updateData - add","data" + data.getTrackId() + " name:" + data.getTrackTitle() + " albumId:" + data.getTrackAlbumId() );    	    
	    		if( maxShowCount < this.getCount() )
	    		{
	    			// max�̕\�������ȏ�́A�\�����Ȃ�
	    			// TODO:�y�[�W���肩�����Ή��Ȃ̂ŁA�ŏ���maxShowCount�������\���ł��Ă��Ȃ�
	    			break;
	    		}
	    	}
    	}
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
	
}
