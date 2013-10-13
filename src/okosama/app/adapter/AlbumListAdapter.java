package okosama.app.adapter;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.Database;
import okosama.app.storage.QueryHandler;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * AlbumList�̃A�_�v�^
 * @author 25689
 *
 */
public class AlbumListAdapter extends SimpleCursorAdapter implements SectionIndexer {
    
	private OkosamaMediaPlayerActivity ctx = null;
    private final Drawable mNowPlayingOverlay;
    private final BitmapDrawable mDefaultAlbumIcon;
    private OkosamaMediaPlayerActivity mActivity;
    private int mAlbumIdx;
    private int mArtistIdx;
    private int mAlbumArtIndex;
    //private String mArtistId;
    /**
     * �A�[�e�B�X�gID�̎擾
     * @param artistId
     */
//    public void setArtistId(String artistId)
//    {
//    	mArtistId = artistId;
//    }
    // private final Resources mResources;
    //private final StringBuilder mStringBuilder = new StringBuilder();
    private final String mUnknownAlbum;
    private final String mUnknownArtist;
    //private final String mAlbumSongSeparator;
    //private final Object[] mFormatArgs = new Object[1];
    private AlphabetIndexer mIndexer;
    //private ExpList mList;
    private AsyncQueryHandler mQueryHandler;
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;
    
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
    public AlbumListAdapter( OkosamaMediaPlayerActivity currentactivity, 
            int layout, Cursor cursor, String[] from, int[] to) {
        super(currentactivity, layout, cursor, from, to);

        // �A�N�e�B�r�e�B�̐ݒ�
        // �N�G���n���h���̍쐬
        //ctx = context;
        // mList = list;
        mActivity = currentactivity;
        mQueryHandler = new QueryHandler(mActivity.getContentResolver(), mActivity);
        
        // album��artist��\��������
        mUnknownAlbum = mActivity.getString(R.string.unknown_album_name);
        mUnknownArtist = mActivity.getString(R.string.unknown_artist_name);
        //mAlbumSongSeparator = context.getString(R.string.albumsongseparator);

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
        getColumnIndices(cursor);
    }

    private void getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// �J�[�\����null�łȂ����
        	// �J�[�\������A�e�J������index���擾���A�����o�ϐ��Ɋi�[����
            mAlbumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
            mArtistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
            mAlbumArtIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART);
            
            // indexer�ɃJ�[�\����ݒ肷��H
            if (mIndexer != null) {
                mIndexer.setCursor(cursor);
            } else {
            	// �A���o�����ɁA�t�@�[�X�g�X�N���[����indexer��ݒ�H
                mIndexer = new MusicAlphabetIndexer(cursor, mAlbumIdx, OkosamaMediaPlayerActivity.getResourceAccessor().getString(
                        R.string.fast_scroll_alphabet));
            }
        }
    }
    
    /**
     * �N�G���n���h���̎擾�H
     * @return
     */
    public AsyncQueryHandler getQueryHandler() {
        return mQueryHandler;
    }

    /**
     * �V�����r���[�̍쐬�H
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
       View v = super.newView(context, cursor, parent);
       ViewHolder vh = new ViewHolder();
       vh.line1 = (TextView) v.findViewById(R.id.line1);
       vh.line2 = (TextView) v.findViewById(R.id.line2);
       vh.play_indicator = (ImageView) v.findViewById(R.id.play_indicator);
       vh.icon = (ImageView) v.findViewById(R.id.icon);
       vh.icon.setBackgroundDrawable(mDefaultAlbumIcon);
       vh.icon.setPadding(0, 0, 1, 0);
       // tag��viewholder��ݒ�H
       v.setTag(vh);
       return v;
    }

    /**
     * �r���[��R����
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        
    	// �^�O����r���[�z���_�[���擾
        ViewHolder vh = (ViewHolder) view.getTag();

        // �A���o�������擾�A�r���[�ɐݒ�
        String name = cursor.getString(mAlbumIdx);
        String displayname = name;
        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
        if (unknown) {
            displayname = mUnknownAlbum;
        }
        vh.line1.setText(displayname);
        
        // �A�[�e�B�X�g�����擾�A�r���[�ɐݒ�
        name = cursor.getString(mArtistIdx);
        displayname = name;
        if (name == null || name.equals(MediaStore.UNKNOWN_STRING)) {
            displayname = mUnknownArtist;
        }
        vh.line2.setText(displayname);

        // �A�C�R���ɁA�A���o���A�[�g��ݒ肷��H
        ImageView iv = vh.icon;
        // We don't actually need the path to the thumbnail file,
        // we just use it to see if there is album art or not
        String art = cursor.getString(mAlbumArtIndex);
        long aid = cursor.getLong(0);
        if (unknown || art == null || art.length() == 0) {
            iv.setImageDrawable(null);
        } else {
            Drawable d = MediaPlayerUtil.getCachedArtwork(context, aid, mDefaultAlbumIcon);
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
     * �J�[�\���̕ύX
     */
    @Override
    public void changeCursor(Cursor cursor) {
        if (mActivity.isFinishing() && cursor != null ) {
        	// �A�N�e�B�r�e�B���I�����ŁA�܂��J�[�\�����c���Ă���ꍇ�A�J�[�\�����N���[�Y
            cursor.close();
            cursor = null;
        }
        // if (false == isEqualCursor(cursor, Database.getInstance(ctx).getCursor( Database.AlbumCursorName ))) {
        	// �J�[�\�����ύX����Ă�����A�Đݒ肷��
        	Database.getInstance(ctx).setCursor( Database.AlbumCursorName, cursor );
            getColumnIndices(cursor);
    	//}
            Log.i("test", "changecursor album");
        super.changeCursor(cursor);
    }
    
    /**
     * �J�[�\���̓��e����v���邩�ǂ������ׂ� ���̏ꍇ�A����_ID��̂݊m�F
     * @param c1
     * @param c2
     * @return
     */
    boolean isEqualCursor( Cursor c1, Cursor c2 )
    {
    	boolean bRet = false;
    	
    	if( c1 == null || c2 == null
    	|| c1.getCount() != c2.getCount() )
    	{
    		// �ǂ��炩��null�̏ꍇ�́A����s�����Afalse�Ƃ���
    		// �����Ⴄ�ꍇ
    		return false;
    	}
    	if(c1.moveToFirst()){
    		c2.moveToFirst();
    		do{
				long id1 = c1.getLong(c1.getColumnIndex("_ID"));
				long id2 = c2.getLong(c2.getColumnIndex("_ID"));
				if( id1 != id2 )
				{
					return false;
				}
				
			}while(c1.moveToNext() && c2.moveToNext());
    	}
    	
    	bRet = true;
    	return bRet;
    }
    
    /**
     * �o�b�N�O���E���h�ŃN�G�������s����H
     * ���ʂɎ��s���Ă�悤�Ɍ�����񂾂��ǁE�E�E
     * �t���[�����[�N�����s����֐��Ǝv����̂ŁA�t���[�����[�N�����̊֐����o�b�N�Ŏ��s���Ă����̂���
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
    	
        String s = constraint.toString();
        if (mConstraintIsValid && (
                (s == null && mConstraint == null) ||
                (s != null && s.equals(mConstraint)))) {
        	// ��Ԃ��ς���Ă��Ȃ���΁A���̂܂܁H
            return getCursor();
        }
        Cursor c = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).createAlbumCursor(null, s ) ;//, mArtistId);
        mConstraint = s;
        mConstraintIsValid = true;
        return c;
    }
    
    // ���L�́ASectionIndexer�p�̃I�[�o�[���C�h�֐��Ǝv����
    /**
     * �C���f�b�N�X����Z�N�V�������擾����
     */
    public Object[] getSections() {
        return mIndexer.getSections();
    }
    
    /**
     * �C���f�b�N�X����Z�N�V�����̃|�W�V�������擾����
     */
    public int getPositionForSection(int section) {
        return mIndexer.getPositionForSection(section);
    }
    /**
     * �|�W�V�����̃Z�N�V�������擾�H
     */
    public int getSectionForPosition(int position) {
        return 0;
    }
}
