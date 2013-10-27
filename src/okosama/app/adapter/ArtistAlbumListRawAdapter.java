package okosama.app.adapter;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.ResourceAccessor;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.*;
import okosama.app.tab.TabPage;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * �A�[�e�B�X�g�̃A�_�v�^
 * @author 25689
 *
 */
public class ArtistAlbumListRawAdapter extends BaseExpandableListAdapter {//<ArtistGroupData,ArtistChildData> {
	
	boolean bDataUpdating = false;
	private LayoutInflater inflater;

	int iGroupLayoutId = 0;
	int iChildLayoutId = 0;
    private final Drawable mNowListOverlay;
	
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
    private AsyncQueryHandler mQueryHandler;
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
    private SparseArray<ArtistGroupData> groupData = new SparseArray<ArtistGroupData>(); 
    private SparseArray<ArtistChildData[]> childData = new SparseArray<ArtistChildData[]>(); 
    private SparseArray<ArtistGroupData> groupDataTmp = new SparseArray<ArtistGroupData>(); 
    private SparseArray<ArtistChildData[]> childDataTmp = new SparseArray<ArtistChildData[]>(); 
//    private ArtistGroupData[] group = null;
//    private ArtistChildData[][] child = null;
//    private ArtistGroupData[] groupTmp = null;
//    private ArtistChildData[][] childTmp = null;
    
    public ArtistAlbumListRawAdapter(OkosamaMediaPlayerActivity currentactivity,
    		// int[] rowId,
    		// ArtistGroupData[] listGroup,
    		SparseArray<ArtistGroupData> groupData,
    		int glayout, 
    		// ArtistChildData[][] listChild, 
    		SparseArray<ArtistChildData[]> childData,
    		int clayout ) {
        // super(currentactivity, listGroup, glayout, listChild, clayout );
        // activity�̍쐬
        // QueryHandler�̍쐬
        mActivity = currentactivity;
        this.inflater 
        = (LayoutInflater) currentactivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mQueryHandler = new QueryHandler(mActivity.getContentResolver(), mActivity);

        // this.rowId = rowId;
        this.groupData = groupData;
        this.childData = childData;
        iGroupLayoutId = glayout;
        iChildLayoutId = clayout;
        
        // Resources r = context.getResources();
        mNowPlayingOverlay = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.indicator_ic_mp_playing_list);
        mNowListOverlay = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.playlist_press);
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
    public AsyncQueryHandler getQueryHandler() {
        return mQueryHandler;
    }
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
        String artist = data.getArtistName();// cursor.getString(mGroupArtistIdx);
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
        long artistid = Long.parseLong(data.getArtistId()); // cursor.getLong(mGroupArtistIdIdx);
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
     */
    // @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        
    	// �O���[�v�J�[�\������A���̃A�[�e�B�X�g��id���擾����
        long id = groupCursor.getLong(groupCursor.getColumnIndexOrThrow(BaseColumns._ID));
        
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
                MediaStore.Audio.Artists.Albums.getContentUri(external_string, id),
                cols, null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        
        // �J�[�\���̃��b�o�H
        class MyCursorWrapper extends CursorWrapper {
        	// �A�[�e�B�X�g��
            String mArtistName;
            // �}�W�b�N�J�����̃C���f�b�N�X�H
            // �ǂ����A���̃J�[�\������A�[�e�B�X�g�����擾���邽�߂̃C���f�b�N�X
            // �{���A�A�[�e�B�X�g���͊i�[����Ă��Ȃ����A�R���X�g���N�^�Ŋi�[���A�擾�̂Ƃ��ɂ���index���w�肳�ꂽ�炻���Ԃ�
            int mMagicColumnIdx;
            /**
             * �R���X�g���N�^
             * @param c
             * @param artist
             */
            MyCursorWrapper(Cursor c, String artist) {
                super(c);
                // �A�[�e�B�X�g��
                mArtistName = artist;
                // �A�[�e�B�X�g�����Ȃ�������A�s����ݒ�
                if (mArtistName == null || mArtistName.equals(MediaStore.UNKNOWN_STRING)) {
                    mArtistName = mUnknownArtist;
                }
                // �}�W�b�N�J�����Ƃ��āA�J��������ݒ�H
                mMagicColumnIdx = c.getColumnCount();
            }
            
            @Override
            public String getString(int columnIndex) {
            	// �J����index���}�W�b�N�J�����łȂ���΁A���̕�������擾�H
                if (columnIndex != mMagicColumnIdx) {
                    return super.getString(columnIndex);
                }
                // �}�W�b�N�J�����Ȃ�΁A�A�[�e�B�X�g�����擾
                return mArtistName;
            }
            
            /**
             * �w�肳�ꂽ�J�������̃J������index���擾
             * �������A�}�W�b�N�J�����Ȃ�΁A�A�[�e�B�X�g�����擾
             */
            @Override
            public int getColumnIndexOrThrow(String name) {
                if (AlbumColumns.ARTIST.equals(name)) {
                    return mMagicColumnIdx;
                }
                return super.getColumnIndexOrThrow(name); 
            }
            
            /**
             * �C���f�b�N�X�ɑΉ������J������index��ԋp����
             */
            @Override
            public String getColumnName(int idx) {
                if (idx != mMagicColumnIdx) {
                    return super.getColumnName(idx);
                }
                return AlbumColumns.ARTIST;
            }
            
            /**
             * �J�����̃J�E���g��ԋp����
             * ���O�ň�ǉ����Ă���̂ŁA�J������+1��ԋp����
             */
            @Override
            public int getColumnCount() {
                return super.getColumnCount() + 1;
            }
        }
        // �J�[�\���ɁA�A�[�e�B�X�g����ǉ������J�[�\����ԋp�H
        // �����炭�A�A�[�e�B�X�g�͂ǂ̃��R�[�h�ł������ŗǂ��̂ŁA���̍��ŗǂ�
        return new MyCursorWrapper(c, groupCursor.getString(mGroupArtistIdx));
    }

    
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if( childData.get(groupPosition,null) != null 
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

	@Override
	public int getChildrenCount(int groupPosition) {
		if( childData.get(groupPosition,null) == null )
		{
			return 0;
		}
		return childData.get(groupPosition).length;
	}

	@Override
	public Object getGroup(int groupPosition) {
		if( groupData.get(groupPosition,null) != null )
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
		return groupData.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View v = convertView;
		if( v == null )
		{
			v = newGroupView();
		}
		ArtistGroupData data = (ArtistGroupData) getGroup(groupPosition);
		bindGroupView(v,mActivity,data,isExpanded);
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
    		SparseArray<ArtistGroupData> group,
    		SparseArray<ArtistChildData[]> child ) {
    	// mapIdAndArt.clear();
    	this.groupData = group;
    	this.childData = child;
    	notifyDataSetChanged();
    }
    
    public int insertAllDataFromCursor(Cursor cursor)
    {
    	if( bDataUpdating == true )
    	{
    		return -1;
    	}
    	bDataUpdating = true;
    	Log.i("insertAllDataFromCursor","start");
    	
//    	if (mActivity.isFinishing() && cursor != null ) {
//        	// �A�N�e�B�r�e�B���I�����ŁA�܂��J�[�\�����c���Ă���ꍇ�A�J�[�\�����N���[�Y
//            cursor.close();
//            cursor = null;
//        }
        Database.getInstance(mActivity).setCursor( Database.ArtistCursorName, cursor );
            	
        AsyncTask<Cursor, Void, Integer> task = new AsyncTask<Cursor, Void, Integer>() {
            @Override
            protected Integer doInBackground(Cursor... params) {
            	Log.i("doInBackground","start");
            	
            	// �J�[�\�������[�v����
            	Cursor cursor = params[0];
            	synchronized(cursor)
            	{
	                groupDataTmp.clear();
	                childDataTmp.clear();
	            	
	        		if( 0 != getColumnIndices(cursor) )
	        		{
	        			return -1;
	        		}
	                Log.i("doInBackground","moveToFirst");
	            	int i=0;
	        		cursor.moveToFirst();
	        		do 
	        		{
	                    ArtistGroupData data = new ArtistGroupData();
	                    
	            		// �S�Ă̗v�f�����[�v����
	                    data.setGroupId(i);
	                    data.setArtistName( cursor.getString(mGroupArtistIdx) );
	                    data.setNumOfAlbums( cursor.getInt(mGroupAlbumIdx) );
	                    data.setNumOfTracks( cursor.getInt(mGroupSongIdx) );
	                    data.setArtistId( cursor.getString(mGroupArtistIdIdx));
	            		groupDataTmp.put( i, data );
	
	            		Cursor childCursor = getChildrenCursor(cursor);
	            		synchronized( childCursor )
	            		{
		            		if( childCursor != null && 0 < childCursor.getCount() )
		            		{
		            			ArtistChildData[] childList = new ArtistChildData[childCursor.getCount()];
		            			int j = 0;
			            		childCursor.moveToFirst();
			            		do 
			            		{
			                        ArtistChildData dataChild = new ArtistChildData();
			                        dataChild.setAlbumName(
			                        	childCursor.getString(childCursor.getColumnIndexOrThrow(AlbumColumns.ALBUM) ) 
			                        );
			                        dataChild.setNumOfSongs(
			                        	childCursor.getInt(childCursor.getColumnIndexOrThrow(AlbumColumns.NUMBER_OF_SONGS))
			                        );
			                        dataChild.setNumOfSongsForArtist( 
			                        	childCursor.getInt(childCursor.getColumnIndexOrThrow(AlbumColumns.NUMBER_OF_SONGS_FOR_ARTIST))
			                        );
			                        dataChild.setArtistName(
			                        	childCursor.getString(childCursor.getColumnIndexOrThrow(ArtistColumns.ARTIST))
			                        );	                        
			                        dataChild.setAlbumArt(
			                        		childCursor.getString(childCursor.getColumnIndexOrThrow(
		                                AlbumColumns.ALBUM_ART))
			                        );
			                        dataChild.setAlbumId( childCursor.getString(0) );
			                        
			                        childList[j] = dataChild; 
			                        		
			                        j++;
			            		} while( OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() == false && 
			            				childCursor.moveToNext() );
			            		
			            		childDataTmp.put( i, childList);
		            		}
	            		}
	            		i++;
	        		} while( OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().isPaused() == false && 
	        				cursor.moveToNext() );
            	}
                return 0;
            }

            @Override
            protected void onPostExecute(Integer ret) 
            {
            	Log.i("onPostExecute","ret=" + ret );
            	
            	// �i�[�I��
            	// ��d�Ǘ��ɂȂ��Ă��܂��Ă��邪�A�A�_�v�^�ɂ����l�̃f�[�^���i�[����
            	updateData( groupDataTmp, childDataTmp );
            	TabPage page = (TabPage) mActivity.getMediaTab().getChild(TabPage.TABPAGE_ID_ARTIST);
            	if( page != null )
            	{
            		page.endUpdate();
            	}
            	bDataUpdating = false;            	
            }
        };
        if( cursor != null 
        && cursor.isClosed() == false )
        {
        	task.execute(cursor);
        }
        return 0;
    }
	
}