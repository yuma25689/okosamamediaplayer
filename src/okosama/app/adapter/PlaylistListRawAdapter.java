package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.LogWrapper;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.ResourceAccessor;
import okosama.app.service.MediaInfo;
import okosama.app.storage.Database;
import okosama.app.storage.FilterData;
import okosama.app.storage.PlaylistData;
import okosama.app.tab.TabPage;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
// import okosama.app.storage.QueryHandler;
// import android.content.AsyncQueryHandler;
// import android.content.ContentResolver;

/**
 * AlbumList�̃A�_�v�^
 * @author 25689
 *
 */
public class PlaylistListRawAdapter extends ArrayAdapter<PlaylistData> 
implements IAdapterUpdate<PlaylistData> { //, IFilterable<PlaylistData> {
	boolean deleted = false;
    private static final long RECENTLY_ADDED_PLAYLIST = -1;
    //private static final long ALL_SONGS_PLAYLIST = -2;
    //private static final long PODCASTS_PLAYLIST = -3;
	boolean bLastError = false;    	
	TabPage page;
    
	boolean bDataUpdating = false;	// �����f�[�^���X�V�����ǂ���
	public boolean IsDataUpdating()
	{
		return bDataUpdating;
	}
	private LayoutInflater inflater;
	// private ArrayList<PlaylistData> items;
	private int iLayoutId;
	private ArrayList<PlaylistData> items = new ArrayList<PlaylistData>();
    private final Drawable mNowPlayingOverlay;
    private final BitmapDrawable mDefaultAlbumIcon;
    private OkosamaMediaPlayerActivity mActivity;
    //private final StringBuilder mStringBuilder = new StringBuilder();
    private final String mUnknownAlbum;
    private final String mUnknownArtist;
    // private AsyncQueryHandler mQueryHandler;
    int mTitleIdx;
    int mIdIdx;
    int mCountIdx;
    // �V���[�g�J�b�g�쐬�t���O�H
    // TODO:�ǂ������Ƃ��ɍ쐬����̂��s��
    // ���肵����A�A�N�Z�T�������o�ϐ�������Ȃ�����
    boolean createShortcut;
    public boolean isCreateShortcut() {
		return createShortcut;
	}
	public void setCreateShortcut(boolean createShortcut) {
		this.createShortcut = createShortcut;
	}
    
    // View�̃z���_�H
    static class ViewHolder {
        TextView line1;
        TextView line2;
        ImageView play_indicator;
        ImageView icon;
    }
   
    /**
     * �A�_�v�^�̃R���X�g���N�^
     * @param currentactivity
     * @param layout
     * @param cursor
     * @param from
     * @param to
     */
    public PlaylistListRawAdapter( OkosamaMediaPlayerActivity currentactivity, 
            int layout, ArrayList<PlaylistData> items,	TabPage page) {
        super(currentactivity, layout, items );
//    	for (PlaylistData data : items) {
//    	    add(data);
//    	}

        this.page = page;
        // this.items = items;
        this.iLayoutId = layout;
        this.inflater = (LayoutInflater) currentactivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // �A�N�e�B�r�e�B�̐ݒ�
        // �N�G���n���h���̍쐬
        //ctx = context;
        // mList = list;
        mActivity = currentactivity;
        //mQueryHandler = new QueryHandler(mActivity.getContentResolver(), mActivity);
        // mQueryHandler = new QueryHandler(mActivity.getContentResolver(), this);
        
        // album��artist��\��������
        mUnknownAlbum = mActivity.getString(R.string.unknown_album_name);
        mUnknownArtist = mActivity.getString(R.string.unknown_artist_name);
                // ���\�[�X�̎擾
        // nowplaying�̃I�[�o�[���C�H
        // mResources = mActivity.getResources();
        mNowPlayingOverlay = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.indicator_ic_mp_playing_list);

        // �A���o���A�C�R���̍쐬�H
        // TODO: ARGB4444�𗘗p����
        Bitmap b = OkosamaMediaPlayerActivity.getResourceAccessor().createBitmapFromDrawableId( R.drawable.albumart_mp_unknown_list);
        mDefaultAlbumIcon = new BitmapDrawable(mActivity.getResources(), b);
        // no filter or dither, it's a lot faster and we can't tell the difference
        // Bitmap���g��Drawable�ɑ΂��A��]�^�g��^�k���̂Ƃ��Ƀt�B���^�������邩�ǂ����BTrue�ɂ���ƃL���C�ɂȂ邪�x���B
        mDefaultAlbumIcon.setFilterBitmap(false);
        // �F�̏��Ȃ�(8bit/�F�ȉ�)�f�o�C�X�ɕ\������Ƃ��ɁA�f�B�U�������邩�ǂ������w�肷��Btrue�Ńf�B�U����B�x���B
        mDefaultAlbumIcon.setDither(false);
        
        // �J�[�\�����ݒ肳��Ă�����A�e�J������index������ɕێ�����
        // getColumnIndices(cursor);
    }
    /**
     * �N�G���n���h���̎擾
     * @return
     */
//    public AsyncQueryHandler getQueryHandler() {
//        return mQueryHandler;
//    }

    /**
     * �V�����r���[�̍쐬�H
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
	       vh.icon.setBackgroundDrawable(null);
	       vh.icon.setPadding(0, 0, 1, 0);
	       v.setTag(vh);
    	}
	    bindView(v,pos);
    	return v;
    }

    /**
     * �r���[�ƃf�[�^��R����
     */
    //@Override
    public void bindView(View view, int pos) {
        
       	// �^�O����r���[�z���_�[���擾
        ViewHolder vh = (ViewHolder) view.getTag();
        // position����f�[�^���擾
    	PlaylistData data = getItem(pos);
    	
    	if( data == null )
    	{
    		// �f�[�^���Ȃ��Ƃ����̂́A���S�ɂ���������Ԃ����E�E
    		 vh.line1.setText("");
    		 vh.line2.setText("");
    		 vh.icon.setImageDrawable(null);
    		 vh.play_indicator.setImageDrawable(null);
    		 return;
    	}
 
    	// �^�C�g���̐ݒ�
       // TextView tv = (TextView) view.findViewById(R.id.line1);
        
        String name = data.getName();
        vh.line1.setText(name);
        
        // id���擾�H
        long id = data.getDataId();
        
        // id�̎�ނɂ���āA�A�C�R���̉摜��ς���
        ImageView iv = vh.icon;//(ImageView) view.findViewById(R.id.icon);
        
        iv.setImageResource(R.drawable.playlist_normal);
        
        // �A�C�R���̃��C�A�E�g�����Z�b�g�H
        // TODO: �Ӗ��Ȃ��悤�Ɋ�����E�E�E
        ViewGroup.LayoutParams p = iv.getLayoutParams();
        p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        //p.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // ���͎g��Ȃ�
        iv = vh.play_indicator; //view.findViewById(R.id.play_indicator);
        iv.setVisibility(View.GONE);

        // view.findViewById(R.id.line2).setVisibility(View.GONE);
        //vh.line2.setVisibility(View.GONE);
        if(data.getPlaylistCount() != null )
        {
	        int i = Integer.parseInt(data.getPlaylistCount());
	       	vh.line2.setText(ResourceAccessor.makeNumSongsLabel(mActivity, i));
	       	vh.line2.setVisibility(View.VISIBLE);
        }
        else
        {
        	vh.line2.setVisibility(View.GONE);
        }
    }
    
    /**
     * �f�[�^�̕ύX�H
     */
    // @Override
    public void updateData(ArrayList<PlaylistData> items) {
    	clear();
    	for (PlaylistData data : items) {
    		if( isFilterData(data) == false )
    		{
    			// ���o�ΏۂłȂ��ꍇ�i�[���Ȃ�
    			continue;
    		}
    	    add(data);
//        	LogWrapper.i("updateData - add","id" + data.getPlaylistId() 
//        			+ " name:" + data.getPlaylistName() );    	    
    	}
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
    	// LogWrapper.i("insertAllDataFromCursor","start");
//    	if (mActivity.isFinishing() && cursor != null ) {
//        	// �A�N�e�B�r�e�B���I�����ŁA�܂��J�[�\�����c���Ă���ꍇ�A�J�[�\�����N���[�Y
//            cursor.close();
//            cursor = null;
//        }
        //Database.getInstance(mActivity).setCursor( Database.AlbumCursorName, cursor );
    	if( page != null )
    	{
    		page.startUpdate();
    	}
            	
        AsyncTask<Cursor, Void, Integer> task = new AsyncTask<Cursor, Void, Integer>() {
            @Override
            protected Integer doInBackground(Cursor... params) {
            	LogWrapper.i("playlist - doInBackground","start");
            	items.clear();
            	bLastError = false;
            	
            	// �J�[�\�������[�v����
            	Cursor cursor = Database.getInstance(
            			OkosamaMediaPlayerActivity.isExternalRef()
            	).createPlaylistCursor(null, null, false);
            	
            	// Cursor cursor = params[0];

        		if( cursor == null || cursor.isClosed() )
        		{
        			LogWrapper.w("PlaylistAdp - doInBk", "cursor closed!");
        			return -1;
        		}
        		
        		try {

	        		if( 0 != getColumnIndices(cursor) )
	        		{
	        			return -1;
	        		}
	            	LogWrapper.i("playlist - doInBackground","moveToFirst");
	            	if( 0 < cursor.getCount() )
	            	{
	            	
		        		cursor.moveToFirst();
		        		do 
		        		{
		            		PlaylistData data = new PlaylistData();
		        			// �S�Ă̗v�f�����[�v����
		            		data.setDataId(cursor.getLong(mIdIdx));
		        			data.setName(cursor.getString(mTitleIdx));
		        	        if( 0 <= data.getDataId() ) {
		        	        	MediaInfo[] songlists = Database.getSongListForPlaylist( 
		        		        		mActivity, data.getDataId() );
		        		        if( songlists != null )
		        		        {
		        		        	data.setPlaylistCount(String.valueOf(songlists.length));
		        		        }
		        	        }
		        			
		        			// ����ȃJ�����͂Ȃ�
		        			// data.setPlaylistCount(cursor.getString(mCountIdx));
		        			items.add(data);
		        		} while( deleted == false
		        			&& cursor.moveToNext() );
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
            	LogWrapper.i("onPostExecute","ret=" + ret );
            	if( ret < 0 )
            	{
                	bLastError = true;
            	}
            	// �i�[�I��
            	// ��d�Ǘ��ɂȂ��Ă��܂��Ă��邪�A�A�_�v�^�ɂ����l�̃f�[�^���i�[����
            	updateData( items );
            	// TabPage page = (TabPage) mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_MEDIA).getChild(TabPage.TABPAGE_ID_PLAYLIST);
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
    private int getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	try {
	        	// �J�[�\����null�łȂ����
	        	// �J�[�\������A�e�J������index���擾���A�����o�ϐ��Ɋi�[����
                mTitleIdx = cursor.getColumnIndexOrThrow(PlaylistsColumns.NAME);
                mIdIdx = cursor.getColumnIndexOrThrow(BaseColumns._ID);
                // mCountIdx = cursor.getColumnIndexOrThrow(BaseColumns._COUNT);
        	} catch( IllegalArgumentException ex ) {
        		return -1;
        	}
            return 0;
        }
        return -1;
    }
	/**
	 * @return the items
	 */
	public ArrayList<PlaylistData> getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(ArrayList<PlaylistData> items) {
		this.items = items;
	}
    @Override
    // �Ȃ̕ύX���ȂǁA��Ԃ��ς�����Ƃ��ɁA�O������\�����X�V������
	public int updateStatus()
    {
    	// 2014/1/18 add filter�p
    	updateData( items );    	
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
		// �b���
		return 0;// getCount();
	}
	@Override
	public void initialize() {
   		if( 0 < mActivity.getPlaylistAdp().getCount() 
   		&& false == mActivity.getPlaylistAdp().isLastErrored() )
   		{
   			mActivity.getPlaylistAdp().updateStatus();
   		}
   		else
   		{
   			mActivity.reScanMediaOfMediaTab(TabPage.TABPAGE_ID_PLAYLIST);
   		}		           		
		
	}
	@Override
	public void clearAdapterData() {
		deleted = true;
		items = null;
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
	public boolean isFilterData(PlaylistData data) {
		boolean bRet = true;
		if( filterData != null && data != null)
		{
			// TODO: �悭�l���Ă݂�ƁA�S�������邩������Ȃ�			
//			// �A�[�e�B�X�gID
//			if( filterData.getArtistId() != null )
//			{
//				if( data.getTrackArtistId() != null
//				&& filterData.getArtistId().equals(data.getTrackArtistId()) )
//				{
//					// �A�[�e�B�X�g���t�B���^�ΏہH
//					// data.getTrackArtistId()�͖{���ɓ����Ă���̂��낤���E�E�E�BTODO:����
//					bRet = true;
//				}
//				else
//				{
//					return false;
//				}
//			}
//			// �A�[�e�B�X�g��
//			if( filterData.getStrArtist() != null && 0 < filterData.getStrArtist().length() )
//			{
//				if( data.getTrackArtist() != null
//				&& -1 != data.getTrackArtist().indexOf(filterData.getStrArtist()) )
//				{
//					// �A�[�e�B�X�g�����ꕔ��v
//					bRet = true;
//				}
//				else
//				{
//					return false;
//				}
//			}
//			
//			// �A���o��ID
//			// TODO: �A���o����			
//			if( filterData.getAlbumId() != null )
//			{
//		    	//setAlbumId( filterData.getAlbumId() );//OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getAlbumID() );
//				
//				if( data.getTrackAlbumId() != null
//				&& filterData.getAlbumId().equals(data.getTrackAlbumId()) )
//				{
//					// �A�[�e�B�X�g���t�B���^�ΏہH
//					// data.getTrackArtistId()�͖{���ɓ����Ă���̂��낤���E�E�E�BTODO:����
//					bRet = true;
//				}
//				else
//				{
//					return false;
//				}
//			}
//			// �A���o����
//			if( filterData.getStrAlbum() != null && 0 < filterData.getStrAlbum().length() )
//			{
//				if( data.getTrackAlbum() != null
//				&& -1 != data.getTrackAlbum().indexOf(filterData.getStrAlbum()) )
//				{
//					// �A�[�e�B�X�g�����ꕔ��v
//					bRet = true;
//				}
//				else
//				{
//					return false;
//				}
//			}
			
//			// �W������ID
//			if( filterData.getGenreId() != null )
//			{
//				ArrayList<GenreData> genres = mActivity.getGenreStocker().getGenreOfAudio( 
//		        		data.getTrackAudioId() );
//				boolean bNoHit = true;
//				if( genres == null )
//				{
//					bNoHit = true;
//				}
//				else
//				{
//					for( GenreData genre : genres )
//					{
//						if( filterData.getGenreId().equals( String.valueOf(genre.getDataId() ) ) )
//						{
//							// �W����������v
//							bRet = true;
//							bNoHit = false;
//							break;
//						}
//					}
//				}
//				if( bNoHit )
//				{
//					return false;
//				}
//			}
//			// TODO:����Ȃ��̂Ńt�B���^��������@�\�͕s�v�Ƃ͎v�����A�O�̂��߃g���b�NID��
			
			
//			// �g���b�N��
//			if( filterData.getStrSong() != null && 0 < filterData.getStrSong().length() )
//			{
//				if( data.getName() != null
//				&& -1 != data.getName().indexOf(filterData.getStrSong()) )
//				{
//					// �g���b�N�����ꕔ��v
//					bRet = true;
//				}
//				else
//				{
//					return false;
//				}
//			}
			// �v���C���X�g��
			if( filterData.getStrPlaylist() != null && 0 < filterData.getStrPlaylist().length() )
			{
				if( data.getName() != null
				&& -1 != data.getName().indexOf(filterData.getStrPlaylist()) )
				{
					// �v���C���X�g�����ꕔ��v
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
