package okosama.app.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import okosama.app.LogWrapper;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.service.MediaInfo;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.AlbumData;
import okosama.app.storage.ArtistGroupData;
import okosama.app.storage.Database;
import okosama.app.storage.FilterData;
import okosama.app.storage.GenreData;
import okosama.app.tab.TabPage;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
// import okosama.app.storage.QueryHandler;
// import android.content.AsyncQueryHandler;

/**
 * AlbumList�̃A�_�v�^
 * @author 25689
 *
 */
public class AlbumListRawAdapter extends ArrayAdapter<AlbumData> 
implements IAdapterUpdate<AlbumData>, SectionIndexer { //, IFilterable<AlbumData> {
    
	boolean deleted = false;
	//SparseArray<String> mapIdAndArt = new SparseArray<String>();
	HashMap<Long,String> mapIdAndArt = new HashMap<Long,String>();
	public String getAlbumArtFromId(long id)
	{
		if( mapIdAndArt.containsKey(id) == false )
		{
			return null;
		}
		return mapIdAndArt.get(id);
	}
	boolean bLastError = false;
	
	boolean bDataUpdating = false;	// �����f�[�^���X�V�����ǂ���
	public boolean IsDataUpdating()
	{
		return bDataUpdating;
	}
	private TabPage page;
	private LayoutInflater inflater;
	// private ArrayList<AlbumData> items;
	private int iLayoutId;
	private ArrayList<AlbumData> items = new ArrayList<AlbumData>();
    private final Drawable mNowPlayingOverlay;
    // private final Drawable mNowListOverlay;
    private final BitmapDrawable mDefaultAlbumIcon;
    private OkosamaMediaPlayerActivity mActivity;
    //private final StringBuilder mStringBuilder = new StringBuilder();
    private final String mUnknownAlbum;
    private final String mUnknownArtist;
    // private AsyncQueryHandler mQueryHandler;
    private int mAlbumIdx;
    private int mArtistIdx;
    private int mAlbumArtIndex;
    
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
    public AlbumListRawAdapter( OkosamaMediaPlayerActivity currentactivity,
    		int layout, ArrayList<AlbumData> items, TabPage page) {
        super(currentactivity, layout, items );

        this.page = page;
        this.iLayoutId = layout;
        this.inflater 
        = (LayoutInflater) currentactivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // �A�N�e�B�r�e�B�̐ݒ�
        // �N�G���n���h���̍쐬
        mActivity = currentactivity;
        // mQueryHandler = new QueryHandler(mActivity.getContentResolver(), this);

        // album��artist��\��������
        mUnknownAlbum = mActivity.getString(R.string.unknown_album_name);
        mUnknownArtist = mActivity.getString(R.string.unknown_artist_name);
        // ���\�[�X�̎擾
        // nowplaying�̃I�[�o�[���C�H
        // mResources = mActivity.getResources();
        mNowPlayingOverlay 
        = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(
        		R.drawable.indicator_ic_mp_playing_list);

//        mNowListOverlay
//        = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(
//        		R.drawable.playlist_selected
//        );
        
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
	       vh.icon.setBackgroundDrawable(mDefaultAlbumIcon);
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
    	AlbumData data = getItem(pos);
    	
    	if( data == null )
    	{
    		// �f�[�^���Ȃ��Ƃ����̂́A���S�ɂ���������Ԃ����E�E
    		 vh.line1.setText("");
    		 vh.line2.setText("");
    		 vh.icon.setImageDrawable(null);
    		 vh.play_indicator.setImageDrawable(null);
    		 return;
    	}
 
        // �A���o�������擾�A�r���[�ɐݒ�
        String name = data.getName();
        String displayname = name;
        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
        if (unknown) {
            displayname = mUnknownAlbum;
        }
        vh.line1.setText(displayname);
        
        // �A�[�e�B�X�g�����擾�A�r���[�ɐݒ�
        name = data.getAlbumArtist();//cursor.getString(mArtistIdx);
        displayname = name;
        if (name == null || name.equals(MediaStore.UNKNOWN_STRING)) {
            displayname = mUnknownArtist;
        }
        vh.line2.setText(displayname);

        // �A�C�R���ɁA�A���o���A�[�g��ݒ肷��H
        ImageView iv = vh.icon;
        // We don't actually need the path to the thumbnail file,
        // we just use it to see if there is album art or not
        String art = data.getAlbumArt();//cursor.getString(mAlbumArtIndex);
        long aid = data.getDataId();//cursor.getLong(0);
        if (unknown || art == null || art.length() == 0) {
            iv.setImageDrawable(null);
        } else {
            Drawable d = MediaPlayerUtil.getCachedArtwork(mActivity, aid, mDefaultAlbumIcon);
            iv.setImageDrawable(d);
        }
        
        // �Đ����̃I�[�o���C�ƂȂ�摜���A�A�C�R���̏�ɏd�˂�E�E�E�̂��ȁH
        long currentalbumid = MediaPlayerUtil.getCurrentAlbumId();
        iv = vh.play_indicator;
        if (currentalbumid == aid) {
            iv.setImageDrawable(mNowPlayingOverlay);
        } else {
            iv.setImageDrawable(null);
        }
    }
    
    /**
     * �f�[�^�̕ύX�H
     */
    // @Override
    public void updateData(ArrayList<AlbumData> items) {
    	clear();
    	mapIdAndArt.clear();
    	for (AlbumData data : items) {
    		if( isFilterData(data) == false )
    		{
    			// ���o�ΏۂłȂ��ꍇ�A�i�[���Ȃ�
    			continue;
    		}    		
    	    add(data);
    	    mapIdAndArt.put(data.getDataId(), data.getAlbumArt());
        	// LogWrapper.i("updateData - add","data" + data.getAlbumId() + " name:" + data.getAlbumName() );    	    
    	}
    	notifyDataSetChanged();
    }
    
    public int stockMediaDataFromDevice(final TabPage page)	//Cursor cursor)
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
    	
//    	if (mActivity.isFinishing() && cursor != null ) {
//        	// �A�N�e�B�r�e�B���I�����ŁA�܂��J�[�\�����c���Ă���ꍇ�A�J�[�\�����N���[�Y
//            cursor.close();
//            cursor = null;
//        }
        // Database.getInstance(mActivity).setCursor( Database.AlbumCursorName, cursor );
    	if( page != null )
    	{
    		page.startUpdate();
    	}

        AsyncTask<Cursor, Void, Integer> task = new AsyncTask<Cursor, Void, Integer>() {
            @Override
            protected Integer doInBackground(Cursor... params) {
            	LogWrapper.i("doInBackground","start");
            	
            	items.clear();
            	bLastError = false;
            	
            	Cursor cursor = Database.getInstance(
            			OkosamaMediaPlayerActivity.isExternalRef()
            	).createAlbumCursor();//null, null);
	            
            	// �J�[�\�������[�v����
            	// Cursor cursor = params[0];
        		if( cursor == null || cursor.isClosed() )
        		{
        			LogWrapper.w("AlbumListAdp - doInBk", "cursor closed!");
        			return -1;
        		}
        		try {
            		if( 0 != getColumnIndices(cursor) )
	        		{
	        			return -1;
	        		}
	            	if( 0 < cursor.getCount() )
	            	{
            		
		            	LogWrapper.i("doInBackground","moveToFirst");
		        		cursor.moveToFirst();
		        		do 
		        		{
		            		AlbumData data = new AlbumData();
		        			// �S�Ă̗v�f�����[�v����
		            		data.setDataId(cursor.getInt(0));
		        			data.setName(cursor.getString(mAlbumIdx));
		        			data.setAlbumArtist(cursor.getString(mArtistIdx));
		        			data.setAlbumArt(cursor.getString(mAlbumArtIndex));
		        			items.add(data);
		        		} while( deleted == false //OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() == false 
		        			&& cursor.moveToNext() );
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
            	LogWrapper.i("onPostExecute","ret=" + ret );
            	
            	if( ret < 0 )
            	{
                	bLastError = true;            		
            	}
            	// �i�[�I��
            	// ��d�Ǘ��ɂȂ��Ă��܂��Ă��邪�A�A�_�v�^�ɂ����l�̃f�[�^���i�[����
            	updateData( items );
            	// TabPage page = (TabPage) mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_MEDIA).getChild(TabPage.TABPAGE_ID_ALBUM);
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
	            mAlbumIdx = cursor.getColumnIndexOrThrow(AlbumColumns.ALBUM);
	            mArtistIdx = cursor.getColumnIndexOrThrow(AlbumColumns.ARTIST);
	            mAlbumArtIndex = cursor.getColumnIndexOrThrow(AlbumColumns.ALBUM_ART);
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
	public ArrayList<AlbumData> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(ArrayList<AlbumData> items) {
		this.items = items;
	}

	@Override
	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
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
		return getCount();
	}

	@Override
	public void initialize() {
   		if( 0 < mActivity.getAlbumAdp().getCount() 
   		&& false == mActivity.getAlbumAdp().isLastErrored() )
   		{
   			mActivity.getAlbumAdp().updateStatus();
   		}
   		else
   		{
   			mActivity.reScanMediaOfMediaTab(TabPage.TABPAGE_ID_ALBUM);
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
	public boolean isFilterData(AlbumData data) {
		boolean bRet = true;
		if( filterData != null && data != null)
		{
			// �A�[�e�B�X�gID
			// �A���o������A�[�e�B�X�g�̈ꗗ������
			if( filterData.getArtistId() != null )
			{
				// Album���ł̓A�[�e�B�X�g�����������Ă��Ȃ��̂ŁA�A�[�e�B�X�gID���A�[�e�B�X�g���ɕϊ����Ă����r����
				HashMap<Integer,ArtistGroupData> mapArtist 
				= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getArtistAdp().getGroupData();
				ArtistGroupData dataArtist = null;
				for( ArtistGroupData dataTmp : mapArtist.values() )
				{
					if( dataTmp.getDataId() == Long.valueOf(filterData.getArtistId() ) )
					{
						dataArtist = dataTmp;
						break;
					}
				}
				if( dataArtist != null
				&& data.getAlbumArtist() != null
				&& dataArtist.getName() != null
				&& dataArtist.getName().equals(data.getAlbumArtist()) )
				{
					// �A�[�e�B�X�g���t�B���^�ΏہH
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
				if( data.getAlbumArtist() != null
				&& -1 != data.getAlbumArtist().indexOf(filterData.getStrArtist()) )
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
			if( filterData.getAlbumId() != null )
			{
		    	//setAlbumId( filterData.getAlbumId() );//OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.getAlbumID() );
				
				if( data.getDataId() != -1
				&& filterData.getAlbumId().equals(String.valueOf(data.getDataId())) )
				{
					// �A���o�����t�B���^�ΏہH
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
				if( data.getName() != null
				&& -1 != data.getName().indexOf(filterData.getStrAlbum()) )
				{
					// �A���o�������ꕔ��v
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
				OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
				MediaInfo songlistOfAlbum[] = Database.getSongListForAlbum(activity, data.getDataId());
				if( songlistOfAlbum != null )
				{
					for( MediaInfo mi : songlistOfAlbum )
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
									// �W����������v
									bRet = true;
									bNoHit = false;
									break;
								}
							}
						}
						// �A���o���̒��łP�Ȃł��q�b�g����΁A���̃A�[�e�B�X�g�͌����Ώ�
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