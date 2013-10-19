package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.adapter.PlaylistListAdapter.QueryHandler;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.PlaylistData;
import okosama.app.storage.Database;
// import okosama.app.storage.QueryHandler;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * AlbumList�̃A�_�v�^
 * @author 25689
 *
 */
public class PlaylistListRawAdapter extends ArrayAdapter<PlaylistData> {
    private static final long RECENTLY_ADDED_PLAYLIST = -1;
    //private static final long ALL_SONGS_PLAYLIST = -2;
    //private static final long PODCASTS_PLAYLIST = -3;
    
	boolean bDataUpdating = false;	// �����f�[�^���X�V�����ǂ���
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
    private AsyncQueryHandler mQueryHandler;
    int mTitleIdx;
    int mIdIdx;
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
     * �N�G���n���h��
     * ���������A���̂Ƃ͈Ⴄ�̂Œ��ӂ���
     * @author 25689
     *
     */
    class QueryHandler extends AsyncQueryHandler {
        QueryHandler(ContentResolver res) {
            super(res);
        }
        
        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            //Log.i("@@@", "query complete: " + cursor.getCount() + "   " + mActivity);
            if (cursor != null) {
                cursor = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).mergedCursor(
                		cursor, createShortcut);
            }
            mActivity.initAdapter(token,cursor);
        }
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
            int layout, ArrayList<PlaylistData> items) {
        super(currentactivity, layout, items );
//    	for (PlaylistData data : items) {
//    	    add(data);
//    	}

        // this.items = items;
        this.iLayoutId = layout;
        this.inflater = (LayoutInflater) currentactivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // �A�N�e�B�r�e�B�̐ݒ�
        // �N�G���n���h���̍쐬
        //ctx = context;
        // mList = list;
        mActivity = currentactivity;
        //mQueryHandler = new QueryHandler(mActivity.getContentResolver(), mActivity);
        mQueryHandler = new QueryHandler(mActivity.getContentResolver());

        
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
    public AsyncQueryHandler getQueryHandler() {
        return mQueryHandler;
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
        
        String name = data.getPlaylistName();
        vh.line1.setText(name);
        
        // id���擾�H
        long id = data.getPlaylistId();
        
        // id�̎�ނɂ���āA�A�C�R���̉摜��ς���
        ImageView iv = vh.icon;//(ImageView) view.findViewById(R.id.icon);
        if (id == RECENTLY_ADDED_PLAYLIST) {
        	// �ŋߒǉ����ꂽ����
            iv.setImageResource(R.drawable.ic_mp_playlist_recently_added_list);
        } else {
        	// ����ȊO
            iv.setImageResource(R.drawable.ic_mp_playlist_list);
        }
        // �A�C�R���̃��C�A�E�g�����Z�b�g�H
        // TODO: �Ӗ��Ȃ��悤�Ɋ�����E�E�E
        ViewGroup.LayoutParams p = iv.getLayoutParams();
        p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // ���͎g��Ȃ�
        iv = (ImageView) vh.play_indicator; //view.findViewById(R.id.play_indicator);
        iv.setVisibility(View.GONE);

        // view.findViewById(R.id.line2).setVisibility(View.GONE);
        vh.line2.setVisibility(View.GONE);
    }
    
    /**
     * �f�[�^�̕ύX�H
     */
    // @Override
    public void updateData(ArrayList<PlaylistData> items) {
    	clear();
    	for (PlaylistData data : items) {
    	    add(data);
        	Log.i("updateData - add","id" + data.getPlaylistId() + " name:" + data.getPlaylistName() );    	    
    	}
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
        Database.getInstance(mActivity).setCursor( Database.AlbumCursorName, cursor );
            	
        AsyncTask<Cursor, Void, Integer> task = new AsyncTask<Cursor, Void, Integer>() {
            @Override
            protected Integer doInBackground(Cursor... params) {
            	Log.i("doInBackground","start");
            	items.clear();
            	
            	// �J�[�\�������[�v����
            	Cursor cursor = params[0];
            	
        		if( 0 != getColumnIndices(cursor) )
        		{
        			return -1;
        		}
            	Log.i("doInBackground","moveToFirst");
        		cursor.moveToFirst();
        		do 
        		{
            		PlaylistData data = new PlaylistData();
        			// �S�Ă̗v�f�����[�v����
            		data.setPlaylistId(cursor.getLong(mIdIdx));
        			data.setPlaylistName(cursor.getString(mTitleIdx));
        			items.add(data);
        		} while( cursor.moveToNext() );
                return 0;
            }

            @Override
            protected void onPostExecute(Integer ret) 
            {
            	Log.i("onPostExecute","ret=" + ret );
            	
            	// �i�[�I��
            	// ��d�Ǘ��ɂȂ��Ă��܂��Ă��邪�A�A�_�v�^�ɂ����l�̃f�[�^���i�[����
            	updateData( items );
            	bDataUpdating = false;            	
            }
        };
        if( cursor != null && 0 < cursor.getCount() 
        && cursor.isClosed() == false )
        {
        	task.execute(cursor);
        }
        return 0;
    }
    private int getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	try {
	        	// �J�[�\����null�łȂ����
	        	// �J�[�\������A�e�J������index���擾���A�����o�ϐ��Ɋi�[����
                mTitleIdx = cursor.getColumnIndexOrThrow(PlaylistsColumns.NAME);
                mIdIdx = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        	} catch( IllegalArgumentException ex ) {
        		return -1;
        	}
            return 0;
        }
        return -1;
    }
    
 }
