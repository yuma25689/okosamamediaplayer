package okosama.app.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import okosama.app.LogWrapper;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.ResourceAccessor;
import okosama.app.service.MediaInfo;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.ArtistChildData;
import okosama.app.storage.ArtistGroupData;
import okosama.app.storage.Database;
import okosama.app.storage.FilterData;
import okosama.app.storage.GenreData;
import okosama.app.tab.TabPage;
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
// import android.content.AsyncQueryHandler;

/**
 * �A�[�e�B�X�g�̃A�_�v�^
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
	
	// �������݃v���C���̎��ɕ\������摜
    private final Drawable mNowPlayingOverlay;
    // �f�t�H���g�̃A���o���̃A�C�R���H
    private final BitmapDrawable mDefaultAlbumIcon;
    // �C���f�b�N�X�ێ��p
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
    // �C���f�N�T
    // private MusicAlphabetIndexer mIndexer;
    // �A�N�e�B�r�e�B
    private OkosamaMediaPlayerActivity mActivity;
    // private AsyncQueryHandler mQueryHandler;
//    private String mConstraint = null;
//    private boolean mConstraintIsValid = false;
    // view�̕ێ��p
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
        // activity�̍쐬
        // QueryHandler�̍쐬
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
        // Filter�ƃf�B�U�𖢎w��ɂ��āA�r�b�g�}�b�v�������ɂ���
        // no filter or dither, it's a lot faster and we can't tell the difference
        mDefaultAlbumIcon.setFilterBitmap(false);
        mDefaultAlbumIcon.setDither(false);
        
        //mContext = context;
        // ���݂̃J�[�\���̃J�����̃C���f�b�N�X��ݒ�
        // getColumnIndices(cursor);
        //mResources = context.getResources();
        // �e�����o�ϐ��̏�����
        // �Z�p���[�^�H
        //mAlbumSongSeparator = currentactivity.getString(R.string.albumsongseparator);
        // �A���o���A�A�[�e�B�X�g
        mUnknownAlbum = currentactivity.getString(R.string.unknown_album_name);
        mUnknownArtist = currentactivity.getString(R.string.unknown_artist_name);
    }
            
    /**
     * �����̃N�G���n���h����ԋp
     * @return
     */
//    public AsyncQueryHandler getQueryHandler() {
//        return mQueryHandler;
//    }
    /**
     * ���݂̃J�����̃C���f�b�N�X������ɐݒ�
     * @param cursor
     */
    private int getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// �J�[�\�����ݒ肳��Ă�����
        	// id,�A�[�e�B�X�gid,�A���o��id,�\���Oid,�C���f�N�T
        	// �Ƃ肠�����A�����O���[�v�p���Ǝv����
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
     * �V�����O���[�v�r���[���쐬���A�ԋp����H
     */
    public View newGroupView() { //Context context, boolean isExpanded, ViewGroup parent) {
    	// �r���[�̎擾�H
	    View v = inflater.inflate(iGroupLayoutId, null); 
    	
        // �A�C�R���̃r���[�擾�A���̃��C�A�E�g���擾�H
        ImageView iv = (ImageView) v.findViewById(R.id.icon);
        // �����́A�A�C�R���̃r���[�̃��C�A�E�g�����������Ă���̂��낤���H
        // TODO: �Ȃ񂾂������������Ă��Ȃ��ĈӖ��Ȃ��悤�ɂ������邯�ǁE�E�E
        ViewGroup.LayoutParams p = iv.getLayoutParams();
        p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        //p.height = 64; //ViewGroup.LayoutParams.;
        // �r���[�z���_�[�̍쐬
        ViewHolder vh = new ViewHolder();
        vh.line1 = (TextView) v.findViewById(R.id.line1);
        vh.line2 = (TextView) v.findViewById(R.id.line2);
        vh.play_indicator = (ImageView) v.findViewById(R.id.play_indicator);
        vh.icon = (ImageView) v.findViewById(R.id.icon);
        vh.icon.setPadding(0, 0, 1, 0);
        // �^�O�Ƀr���[�z���_�[��ݒ�
        v.setTag(vh);
        return v;
    }

    /**
     * �V����chileView���쐬���A�ԋp����H
     */
    public View newChildView() {
        View v = inflater.inflate(iChildLayoutId, null);
    	// ViewHolder�̍쐬
        ViewHolder vh = new ViewHolder();
        vh.line1 = (TextView) v.findViewById(R.id.line1);
        vh.line2 = (TextView) v.findViewById(R.id.line2);
        vh.play_indicator = (ImageView) v.findViewById(R.id.play_indicator);
        vh.icon = (ImageView) v.findViewById(R.id.icon);
        vh.icon.setBackgroundDrawable(mDefaultAlbumIcon);
        vh.icon.setPadding(0, 0, 1, 0);
        // �^�O�Ƀr���[�z���_�[��ݒ�
        v.setTag(vh);
        return v;
    }
    
    /**
     * �O���[�v�r���[�̕R�t��
     */
    // @Override
    public void bindGroupView(View view, 
    		Context context, ArtistGroupData data, boolean isexpanded) {

    	// �^�O����r���[�z���_�[���擾
        ViewHolder vh = (ViewHolder) view.getTag();

        // �J�[�\������l���擾���āA�r���[�ɐݒ肷��
        // �A�[�e�B�X�g
        String artist = data.getName();// cursor.getString(mGroupArtistIdx);
        String displayartist = artist;
        boolean unknown = artist == null || artist.equals(MediaStore.UNKNOWN_STRING);
        if (unknown) {
            displayartist = mUnknownArtist;
        }
        vh.line1.setText(displayartist);

        // �A���o���A�Ȃ̐��H���낤���H
        int numalbums = data.getNumOfAlbums(); // cursor.getInt(mGroupAlbumIdx);
        int numsongs = data.getNumOfTracks(); // cursor.getInt(mGroupSongIdx);
        
        // �擾�����A���o�����A�Ȑ�����A���x�����쐬���A�ݒ�
        String songs_albums = ResourceAccessor.makeAlbumsLabel(context,
                numalbums, numsongs, unknown);
        
        vh.line2.setText(songs_albums);
        
        // ���݂̃A�[�e�B�X�g�ƁA���R�[�h�̃A�[�e�B�X�gID���r���A�����Ȃ�΁A�Đ����ɂ���H
        // TODO:�����A����ł̓A�[�e�B�X�g�̕ʂ̃A���o���ł��Đ����ɂȂ��Ă��܂��C�����邪�A
        // ���̃��X�g�ł̓A���o���̋�ʂ͂Ȃ��̂���
        long currentartistid = MediaPlayerUtil.getCurrentArtistId();
        long artistid = data.getDataId(); // cursor.getLong(mGroupArtistIdIdx);
        if (currentartistid == artistid && !isexpanded) {
            vh.play_indicator.setImageDrawable(mNowPlayingOverlay);
        } else {
            vh.play_indicator.setImageDrawable(null);
        }        
    }

    /**
     * �q�r���[��ݒ�
     * �ǂ����A�q�r���[�ɂ́A�Y���A�[�e�B�X�g�̃A���o���̈ꗗ��ݒ肷��
     */
    // @Override
    public void bindChildView(View view, Context context, ArtistChildData data ) { //Context context, Cursor cursor ) { // , boolean islast) {

    	if( view == null )
    	{
    		return;
    	}
    	
    	// �^�O����r���[�z���_�[���擾
        ViewHolder vh = (ViewHolder) view.getTag();

        // �A���o�������擾�A�ݒ�
        String name = data.getAlbumName();// cursor.getString(cursor.getColumnIndexOrThrow(AlbumColumns.ALBUM));
        String displayname = name;
        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
        if (unknown) {
            displayname = mUnknownAlbum;
        }
        vh.line1.setText(displayname);

        // �Ȑ��ƃA�[�e�B�X�g�̋Ȑ����擾
        int numsongs = data.getNumOfSongs(); // cursor.getInt(cursor.getColumnIndexOrThrow(AlbumColumns.NUMBER_OF_SONGS));
        int numartistsongs = data.getNumOfSongsForArtist();// cursor.getInt(cursor.getColumnIndexOrThrow(AlbumColumns.NUMBER_OF_SONGS_FOR_ARTIST));

        final StringBuilder builder = mBuffer;
        builder.delete(0, builder.length());
        if (unknown) {
        	// �A�[�e�B�X�g��������Ȃ��ꍇ�A�A�[�e�B�X�g�̐����Ȑ��ɂ���H
            numsongs = numartistsongs;
        }
          
        // �Ȑ���ݒ�
        if (numsongs == 1) {
            builder.append(context.getString(R.string.onesong));
        } else {
            if (numsongs == numartistsongs) {
            	// �A�[�e�B�X�g�̋Ȑ��ƁA�Ȑ�����v
            	// �Ȑ��́A�P�����ݒ�H
                final Object[] args = mFormatArgs;
                args[0] = numsongs;
                builder.append(OkosamaMediaPlayerActivity.getResourceAccessor().getQuantityString(R.plurals.Nsongs, numsongs, args));
            } else {
            	// ��v���Ȃ��ꍇ�A�R�ݒ�H�Ȑ��A�A�[�e�B�X�g�Ȑ��A�A�[�e�B�X�g���H
                final Object[] args = mFormatArgs3;
                args[0] = numsongs;
                args[1] = numartistsongs;
                args[2] = data.getArtistName();// cursor.getString(cursor.getColumnIndexOrThrow(ArtistColumns.ARTIST));
                builder.append(OkosamaMediaPlayerActivity.getResourceAccessor().getQuantityString(R.plurals.Nsongscomp, numsongs, args));
            }
        }
        vh.line2.setText(builder.toString());
        
        // �A���o���A�[�g�̎擾�A�ݒ�
        ImageView iv = vh.icon;
        // We don't actually need the path to the thumbnail file,
        // we just use it to see if there is album art or not
        String art = data.getAlbumArt(); //cursor.getString(cursor.getColumnIndexOrThrow(
                //AlbumColumns.ALBUM_ART));
        if (unknown || art == null || art.length() == 0) {
        	// ������Ȃ��ꍇ�́A�f�t�H���g��ݒ肷��
            iv.setBackgroundDrawable(mDefaultAlbumIcon);
            iv.setImageDrawable(null);
        } else {
        	// ������ꍇ�́ADatabase����擾����
            long artIndex = Long.parseLong(data.getAlbumId()); // cursor.getLong(0);
            Drawable d = MediaPlayerUtil.getCachedArtwork(context, artIndex, mDefaultAlbumIcon);
            iv.setImageDrawable(d);
        }

        // �Đ����̃A���o����id�ƁA���̍��ڂ̃A���o����id���擾���A
        // ��v�����猻�݃v���C���ɂ���
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
     * �q�r���[�̃J�[�\�����擾�H
     * TODO:�s�v�Ȃ̂ŁA�����ƌy�������ɒu�������邱��
     */
    // @Override
    protected Cursor getChildrenCursor(long groupId) { //, String artistName ) { //Cursor groupCursor) {
        
    	// �O���[�v�J�[�\������A���̃A�[�e�B�X�g��id���擾����
        // long id = groupCursor.getLong(groupCursor.getColumnIndexOrThrow(BaseColumns._ID));
        
        // �J�������̐ݒ�
        String[] cols = new String[] {
                BaseColumns._ID,
                AlbumColumns.ALBUM,
                AlbumColumns.NUMBER_OF_SONGS,
                AlbumColumns.NUMBER_OF_SONGS_FOR_ARTIST,
                AlbumColumns.ALBUM_ART
        };
        // uri�̎擾
        // �O���X�g���[�W���������ɂ���ċ�����ύX
        //Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String external_string = "external";
        if( OkosamaMediaPlayerActivity.isExternalRef() == false )
        {
        	external_string = "internal";	// �����A����ł悢
        }        
        // �N�G�����s
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
	 * �q�̃f�[�^�́A�W�J���ɂ��̊֐��œǂݍ���
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
                   
                    // album��
                    dataChild.setAlbumName(
                    	childCursor.getString(childCursor.getColumnIndexOrThrow(AlbumColumns.ALBUM) ) 
                    );
                    // album �Ȑ�
                    dataChild.setNumOfSongs(
                    	childCursor.getInt(childCursor.getColumnIndexOrThrow(AlbumColumns.NUMBER_OF_SONGS))
                    );
                    // artist �Ȑ�
                    dataChild.setNumOfSongsForArtist( 
                    	childCursor.getInt(childCursor.getColumnIndexOrThrow(AlbumColumns.NUMBER_OF_SONGS_FOR_ARTIST))
                    );
                    // artist��
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
		// LogWrapper.e("arsit","getGroup" + groupPosition + " " + groupData.containsKey(groupPosition));
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
		//LogWrapper.e("getGroup","artist");
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
		// ����Afilter���������Ă���ꍇ�Anull�ɂȂ��Ă��܂��H
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
     * �f�[�^�̕ύX�H
     */
    // @Override
    public void updateData(
    		//SparseArray<ArtistGroupData> group,
    		//SparseArray<ArtistChildData[]> child
    	    HashMap<Integer,ArtistGroupData> group, 
    		HashMap<Integer,ArtistChildData[]> child
    		) {
    	// mapIdAndArt.clear();
    	// �O���[�v�̃}�b�v���t�B���^�������Ȃ���R�s�[
    	// �R�s�[�����}�b�v�����Afilter�������č�蒼��
    	HashMap<Integer,ArtistGroupData> group2 = new HashMap<Integer,ArtistGroupData>();
    	if( filterData != null )
    	{
    		int i=0;
    		for( Entry<Integer,ArtistGroupData> entryTmp : group.entrySet() )
    		{
    			if( true == isFilterData( entryTmp.getValue() ) )
    			{
    				// ���o�Ώۂ̏ꍇ�̂݁A�i�[����
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
    	this.childData = child;// 2014/1/18 �W�J���Ɏ擾����̂ł����Ǝv��ꂽ�̂ł����ŃN���A
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
    	// LogWrapper.i("insertAllDataFromCursor","start");
    	
//    	if (mActivity.isFinishing() && cursor != null ) {
//        	// �A�N�e�B�r�e�B���I�����ŁA�܂��J�[�\�����c���Ă���ꍇ�A�J�[�\�����N���[�Y
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
            	LogWrapper.i("doInBackground","start");
            	
            	bLastError = false;
            	// �J�[�\�������[�v����
            	Cursor cursor = Database.getInstance(
            			OkosamaMediaPlayerActivity.isExternalRef()
            	).createArtistCursor();//null, null);            	
            	// Cursor cursor = params[0];
        		if( cursor == null || cursor.isClosed() )
        		{
        			LogWrapper.w("ArtistAlbumListAdp - doInBk", "cursor closed!");
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
	        		
		                LogWrapper.i("doInBackground","moveToFirst");
		            	int i=0;
		        		cursor.moveToFirst();
		        		do 
		        		{
		                    ArtistGroupData data = new ArtistGroupData();
		                    
		            		// �S�Ă̗v�f�����[�v����
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
            	LogWrapper.i("onPostExecute","ret=" + ret );
            	if( ret < 0 )
            	{
            		groupDataTmp.clear();
            		childDataTmp.clear();
            		bLastError = true;
            	}
            	// �i�[�I��
            	// ��d�Ǘ��ɂȂ��Ă��܂��Ă��邪�A�A�_�v�^�ɂ����l�̃f�[�^���i�[����
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
    // �Ȃ̕ύX���ȂǁA��Ԃ��ς�����Ƃ��ɁA�O������\�����X�V������
	public int updateStatus()
    {
    	// 2014/1/18 add filter�p
    	updateData( groupDataTmp, childDataTmp );    	
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
	 *��:�Ȃ񂩕ς����ǁA�\���Ώۂ̏ꍇ�Atrue
	 */	
	@Override
	public boolean isFilterData(ArtistGroupData data) {
		boolean bRet = true;
		if( filterData != null && data != null)
		{
			// �A�[�e�B�X�gID
			if( filterData.getArtistId() != null )
			{
				if( data.getDataId() != -1
				&& filterData.getArtistId().equals(String.valueOf(data.getDataId())) )
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
				if( data.getName() != null
				&& -1 != data.getName().indexOf(filterData.getStrArtist()) )
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
									// �W����������v
									bRet = true;
									bNoHit = false;
									break;
								}
							}
						}
						// �A�[�e�B�X�g�̒��łP�Ȃł��q�b�g����΁A���̃A�[�e�B�X�g�͌����Ώ�
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