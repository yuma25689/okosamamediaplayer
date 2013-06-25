package okosama.app.adapter;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.ResourceAccessor;
import okosama.app.service.MediaPlayer;
import okosama.app.storage.Database;
import okosama.app.storage.QueryHandler;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

/**
 * �A�[�e�B�X�g�̃A�_�v�^
 * @author 25689
 *
 */
public class ArtistAlbumListAdapter extends SimpleCursorTreeAdapter implements SectionIndexer {
	
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
    private final String mAlbumSongSeparator;
    private final String mUnknownAlbum;
    private final String mUnknownArtist;
    private final StringBuilder mBuffer = new StringBuilder();
    private final Object[] mFormatArgs = new Object[1];
    private final Object[] mFormatArgs3 = new Object[3];
    // �C���f�N�T
    private MusicAlphabetIndexer mIndexer;
    // �A�N�e�B�r�e�B
    private OkosamaMediaPlayerActivity mActivity;
    private AsyncQueryHandler mQueryHandler;
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;
    // view�̕ێ��p
    static class ViewHolder {
        TextView line1;
        TextView line2;
        ImageView play_indicator;
        ImageView icon;
    }

    public ArtistAlbumListAdapter(OkosamaMediaPlayerActivity currentactivity,
            Cursor cursor, int glayout, String[] gfrom, int[] gto, 
            int clayout, String[] cfrom, int[] cto) {
        super(currentactivity, cursor, glayout, gfrom, gto, clayout, cfrom, cto);
        // activity�̍쐬
        // QueryHandler�̍쐬
        mActivity = currentactivity;
        mQueryHandler = new QueryHandler(currentactivity.getContentResolver(),mActivity);

        // Resources r = context.getResources();
        mNowPlayingOverlay = OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.indicator_ic_mp_playing_list);
        mDefaultAlbumIcon =  (BitmapDrawable)OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.albumart_mp_unknown_list);
        // Filter�ƃf�B�U�𖢎w��ɂ��āA�r�b�g�}�b�v�������ɂ���
        // no filter or dither, it's a lot faster and we can't tell the difference
        mDefaultAlbumIcon.setFilterBitmap(false);
        mDefaultAlbumIcon.setDither(false);
        
        //mContext = context;
        // ���݂̃J�[�\���̃J�����̃C���f�b�N�X��ݒ�
        getColumnIndices(cursor);
        //mResources = context.getResources();
        // �e�����o�ϐ��̏�����
        // �Z�p���[�^�H
        mAlbumSongSeparator = currentactivity.getString(R.string.albumsongseparator);
        // �A���o���A�A�[�e�B�X�g
        mUnknownAlbum = currentactivity.getString(R.string.unknown_album_name);
        mUnknownArtist = currentactivity.getString(R.string.unknown_artist_name);
    }
    
    /**
     * ���݂̃J�����̃C���f�b�N�X������ɐݒ�
     * @param cursor
     */
    private void getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// �J�[�\�����ݒ肳��Ă�����
        	// id,�A�[�e�B�X�gid,�A���o��id,�\���Oid,�C���f�N�T
        	// �Ƃ肠�����A�����O���[�v�p���Ǝv����
            mGroupArtistIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
            mGroupArtistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
            mGroupAlbumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
            mGroupSongIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
            if (mIndexer != null) {
                mIndexer.setCursor(cursor);
            } else {
                mIndexer = new MusicAlphabetIndexer(cursor, mGroupArtistIdx, 
                        OkosamaMediaPlayerActivity.getResourceAccessor().getString(R.string.fast_scroll_alphabet));
            }
        }
    }
    
    /**
     * �A�N�e�B�r�e�B���O������ݒ�H
     * @param newactivity
     */
//    public void setActivity(OkosamaMediaPlayerActivity newactivity) {
//        mActivity = newactivity;
//    }
    
    /**
     * �����̃N�G���n���h����ԋp
     * @return
     */
    public AsyncQueryHandler getQueryHandler() {
        return mQueryHandler;
    }

    /**
     * �V�����O���[�v�r���[���쐬���A�ԋp����H
     */
    @Override
    public View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
    	// �r���[�̎擾�H
        View v = super.newGroupView(context, cursor, isExpanded, parent);
        // �A�C�R���̃r���[�擾�A���̃��C�A�E�g���擾�H
        ImageView iv = (ImageView) v.findViewById(R.id.icon);
        // �����́A�A�C�R���̃r���[�̃��C�A�E�g�����������Ă���̂��낤���H
        // TODO: �Ȃ񂾂������������Ă��Ȃ��ĈӖ��Ȃ��悤�ɂ������邯�ǁE�E�E
        ViewGroup.LayoutParams p = iv.getLayoutParams();
        p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
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
    @Override
    public View newChildView(Context context, Cursor cursor, boolean isLastChild,
            ViewGroup parent) {
        View v = super.newChildView(context, cursor, isLastChild, parent);
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
    @Override
    public void bindGroupView(View view, Context context, Cursor cursor, boolean isexpanded) {

    	// �^�O����r���[�z���_�[���擾
        ViewHolder vh = (ViewHolder) view.getTag();

        // �J�[�\������l���擾���āA�r���[�ɐݒ肷��
        // �A�[�e�B�X�g
        String artist = cursor.getString(mGroupArtistIdx);
        String displayartist = artist;
        boolean unknown = artist == null || artist.equals(MediaStore.UNKNOWN_STRING);
        if (unknown) {
            displayartist = mUnknownArtist;
        }
        vh.line1.setText(displayartist);

        // �A���o���A�Ȃ̐��H���낤���H
        int numalbums = cursor.getInt(mGroupAlbumIdx);
        int numsongs = cursor.getInt(mGroupSongIdx);
        
        // �擾�����A���o�����A�Ȑ�����A���x�����쐬���A�ݒ�
        String songs_albums = ResourceAccessor.makeAlbumsLabel(context,
                numalbums, numsongs, unknown);
        
        vh.line2.setText(songs_albums);
        
        // ���݂̃A�[�e�B�X�g�ƁA���R�[�h�̃A�[�e�B�X�gID���r���A�����Ȃ�΁A�Đ����ɂ���H
        // TODO:�����A����ł̓A�[�e�B�X�g�̕ʂ̃A���o���ł��Đ����ɂȂ��Ă��܂��C�����邪�A
        // ���̃��X�g�ł̓A���o���̋�ʂ͂Ȃ��̂���
        long currentartistid = MediaPlayer.getCurrentArtistId();
        long artistid = cursor.getLong(mGroupArtistIdIdx);
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
    @Override
    public void bindChildView(View view, Context context, Cursor cursor, boolean islast) {

    	// �^�O����r���[�z���_�[���擾
        ViewHolder vh = (ViewHolder) view.getTag();

        // �A���o�������擾�A�ݒ�
        String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM));
        String displayname = name;
        boolean unknown = name == null || name.equals(MediaStore.UNKNOWN_STRING); 
        if (unknown) {
            displayname = mUnknownAlbum;
        }
        vh.line1.setText(displayname);

        // �Ȑ��ƃA�[�e�B�X�g�̋Ȑ����擾
        int numsongs = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
        int numartistsongs = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS_FOR_ARTIST));

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
                args[2] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST));
                builder.append(OkosamaMediaPlayerActivity.getResourceAccessor().getQuantityString(R.plurals.Nsongscomp, numsongs, args));
            }
        }
        vh.line2.setText(builder.toString());
        
        // �A���o���A�[�g�̎擾�A�ݒ�
        ImageView iv = vh.icon;
        // We don't actually need the path to the thumbnail file,
        // we just use it to see if there is album art or not
        String art = cursor.getString(cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Albums.ALBUM_ART));
        if (unknown || art == null || art.length() == 0) {
        	// ������Ȃ��ꍇ�́A�f�t�H���g��ݒ肷��
            iv.setBackgroundDrawable(mDefaultAlbumIcon);
            iv.setImageDrawable(null);
        } else {
        	// ������ꍇ�́ADatabase����擾����
            long artIndex = cursor.getLong(0);
            Drawable d = MediaPlayer.getCachedArtwork(context, artIndex, mDefaultAlbumIcon);
            iv.setImageDrawable(d);
        }

        // �Đ����̃A���o����id�ƁA���̍��ڂ̃A���o����id���擾���A
        // ��v�����猻�݃v���C���ɂ���
        long currentalbumid = MediaPlayer.getCurrentAlbumId();
        long aid = cursor.getLong(0);
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
    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        
    	// �O���[�v�J�[�\������A���̃A�[�e�B�X�g��id���擾����
        long id = groupCursor.getLong(groupCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID));
        
        // �J�������̐ݒ�
        String[] cols = new String[] {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS_FOR_ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART
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
                if (MediaStore.Audio.Albums.ARTIST.equals(name)) {
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
                return MediaStore.Audio.Albums.ARTIST;
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

    /**
     * �J�[�\���̕ύX
     */
    @Override
    public void changeCursor(Cursor cursor) {
        if (mActivity.isFinishing() && cursor != null) {
        	// �A�N�e�B�r�e�B�I�����̏ꍇ�A�J�[�\�����N���[�Y
            cursor.close();
            cursor = null;
        }
        if (cursor != Database.getInstance(mActivity).getCursor(Database.ArtistCursorName)) {
        	// �J�[�\�����ύX����Ă�����A�J�[�\����ݒ肷��
        	Database.getInstance(mActivity).setCursor(Database.ArtistCursorName, cursor);
            getColumnIndices(cursor);
            super.changeCursor(cursor);
        }
    }
    
    /**
     * �w�i�ŃN�G���𔭍s����Ƃ��Ɏ��s�����H
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String s = constraint.toString();
        // �t�B���^���ς���Ă��Ȃ���΁A���s���Ȃ��H
        if (mConstraintIsValid && (
                (s == null && mConstraint == null) ||
                (s != null && s.equals(mConstraint)))) {
            return getCursor();
        }
        Cursor c = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).createArtistCursor(null, s);
        mConstraint = s;
        mConstraintIsValid = true;
        return c;
    }

    // ���L�́A�����炭�C���f�N�T�̃I�[�o�[���C�h�֐�
    public Object[] getSections() {
        return mIndexer.getSections();
    }
    
    public int getPositionForSection(int sectionIndex) {
        return mIndexer.getPositionForSection(sectionIndex);
    }
    
    public int getSectionForPosition(int position) {
        return 0;
    }
}