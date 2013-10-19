package okosama.app.adapter;

import java.util.ArrayList;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.ResourceAccessor;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.Database;
import okosama.app.storage.TrackQueryHandler;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * �g���b�N�̃��X�g�̃A�_�v�^
 * @author 25689
 *
 */
public class TrackListAdapter extends SimpleCursorAdapter implements SectionIndexer {

    // �J�����̃C���f�b�N�X�ێ��p 
    int mTitleIdx;
    int mArtistIdx;
    int mDurationIdx;
    int mAudioIdIdx;
    
    // �O������̐ݒ�l�ێ��p
	// TODO: �Ӗ��̒���
    boolean mIsNowPlaying;
    boolean mDisableNowPlayingIndicator;
    private String genre;
    private String albumId;
    private String artistId;

    private final StringBuilder mBuilder = new StringBuilder();
    private final String mUnknownArtist;
    
    private AlphabetIndexer mIndexer;
    
    private OkosamaMediaPlayerActivity mActivity = null;
    private TrackQueryHandler mQueryHandler;
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;
    
    // �r���[�ێ��p�N���X
    static class ViewHolder {
        TextView line1;
        TextView line2;
        TextView duration;
        ImageView play_indicator;
        CharArrayBuffer buffer1;
        char [] buffer2;
    }
    
    public TrackListAdapter( OkosamaMediaPlayerActivity currentactivity,
            int layout, Cursor cursor, String[] from, int[] to,
            boolean isnowplaying, boolean disablenowplayingindicator,String genre_,
            String albumId_, String artistId_ ) {
        super(currentactivity, layout, cursor, from, to);
        mActivity = currentactivity;
        getColumnIndices(cursor);
        mIsNowPlaying = isnowplaying;
        mDisableNowPlayingIndicator = disablenowplayingindicator;
        mUnknownArtist = currentactivity.getString(R.string.unknown_artist_name);
        //mUnknownAlbum = context.getString(R.string.unknown_album_name);
        genre = genre_;
        albumId = albumId_;
        artistId = artistId_;
        
        mQueryHandler = new TrackQueryHandler(currentactivity.getContentResolver());
    }
    
//    public void setActivity(OkosamaMediaPlayerActivity newactivity) {
//        mActivity = newactivity;
//    }
    
    public TrackQueryHandler getQueryHandler() {
        return mQueryHandler;
    }
    
    /**
     * �J�����̃C���f�b�N�X��ݒ�
     * @param cursor
     */
    private void getColumnIndices(Cursor cursor) {
        if (cursor != null) {
        	// �e�J�����̃C���f�b�N�X��ݒ�
        	// �^�C�g���A�A�[�e�B�X�g�A����
            mTitleIdx = cursor.getColumnIndexOrThrow(MediaColumns.TITLE);
            mArtistIdx = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST);
            mDurationIdx = cursor.getColumnIndexOrThrow(AudioColumns.DURATION);
            try {
            	// �I�[�f�B�I�H�܂��A�v���C���X�g�̃J����id����擾�����݁A���s������g���b�N�̃J����id����擾����炵��
                mAudioIdIdx = cursor.getColumnIndexOrThrow(
                        MediaStore.Audio.Playlists.Members.AUDIO_ID);
            } catch (IllegalArgumentException ex) {
                mAudioIdIdx = cursor.getColumnIndexOrThrow(BaseColumns._ID);
            }
            
            // �C���f�N�T�̐ݒ�
            if (mIndexer != null) {
                mIndexer.setCursor(cursor);
            } else if (!mActivity.isEditMode()) {
                String alpha = mActivity.getString(R.string.fast_scroll_alphabet);
            
                mIndexer = new MusicAlphabetIndexer(cursor, mTitleIdx, alpha);
            }
        }
    }

    /**
     * �V�����r���[�̐ݒ�
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = super.newView(context, cursor, parent);
        // �C���[�W�́A�G�f�B�b�g���[�h�̎��̂ݕ\������
        ImageView iv = (ImageView) v.findViewById(R.id.icon);
        if (mActivity.isEditMode()) {
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(R.drawable.ic_mp_move);
        } else {
            iv.setVisibility(View.GONE);
        }
        
        // viewholder�Ɋe�r���[��ݒ肵�A�^�O�ɐݒ肷��
        ViewHolder vh = new ViewHolder();
        vh.line1 = (TextView) v.findViewById(R.id.line1);
        vh.line2 = (TextView) v.findViewById(R.id.line2);
        vh.duration = (TextView) v.findViewById(R.id.duration);
        vh.play_indicator = (ImageView) v.findViewById(R.id.play_indicator);
        vh.buffer1 = new CharArrayBuffer(100);
        vh.buffer2 = new char[200];
        v.setTag(vh);
        return v;
    }

    /**
     * �r���[�ւ̒l��ݒ�
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        
    	// �r���[�z���_�[����e�r���[���擾
        ViewHolder vh = (ViewHolder) view.getTag();
        
        // �o�b�t�@�ɁA�^�C�g���̕��������x�擾��A�r���[�ɐݒ�HTODO:�Ȃ��H
        cursor.copyStringToBuffer(mTitleIdx, vh.buffer1);
        vh.line1.setText(vh.buffer1.data, 0, vh.buffer1.sizeCopied);
        
        // ���Ԃ��擾�A�ݒ�
        int secs = cursor.getInt(mDurationIdx) / 1000;
        if (secs == 0) {
            vh.duration.setText("");
        } else {
            vh.duration.setText(ResourceAccessor.makeTimeString(context, secs));
        }
        
        final StringBuilder builder = mBuilder;
        builder.delete(0, builder.length());

        // �A�[�e�B�X�g�����擾�ł�����A�r���[�ɐݒ�
        String name = cursor.getString(mArtistIdx);
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

        // 
        ImageView iv = vh.play_indicator;
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
        if ( (mIsNowPlaying && cursor.getPosition() == id) ||
             (!mIsNowPlaying && !mDisableNowPlayingIndicator && cursor.getLong(mAudioIdIdx) == id)) {
        	// �Đ����ōĐ����̂��̂��A
        	// �Đ����łȂ��ANowPlayingIndicator��\�����Ȃ��ݒ�ł��Ȃ��A���ݐݒ�g�p�Ƃ��Ă���̂��ҋ@���̋ȂƓ����Ȃ�
        	// �v���C���X�g�̃C���[�W��ݒ�H
        	// TODO:�v���C���X�g�̃C���[�W�Ƃ����̂��ǂ��������̂�������Ȃ��̂ŁA�m�F����
            iv.setImageResource(R.drawable.indicator_ic_mp_playing_list);
            iv.setVisibility(View.VISIBLE);
        } else {
        	// �v���C���X�g�̃C���[�W���\���ɁH
            iv.setVisibility(View.GONE);
        }
    }
    
    /**
     * �J�[�\����ύX����
     */
    @Override
    public void changeCursor(Cursor cursor) {
        if (mActivity.isFinishing() && cursor != null) {
        	// �Đ����Ȃ�΁A�J�[�\�����N���[�X
            cursor.close();
            cursor = null;
        }
        //if (false == isEqualCursor( cursor, Database.getInstance(mActivity).getCursor(Database.SongCursorName))) {
        	// �J�[�\�����ύX����Ă�����A�J�[�\�������Z�b�g����
        	Database.getInstance(mActivity).setCursor(Database.SongCursorName, cursor);
            getColumnIndices(cursor);
        //}
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
     * �w�i�ŃN�G���𔭍s����Ƃ�
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String s = constraint.toString();
        if (mConstraintIsValid && (
                (s == null && mConstraint == null) ||
                (s != null && s.equals(mConstraint)))) {
        	// �t�B���^���ς���Ă��Ȃ���΁A���݂̃J�[�\�����擾
            return getCursor();
        }
        //String playlist = null;
        if( mIsNowPlaying )
        {
        	OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistName( Database.PlaylistName_NowPlaying );
        }
        else
        {
        	OkosamaMediaPlayerActivity.getResourceAccessor().appStatus.setPlaylistName( null );        	
        }
        //Cursor c = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).createTrackCursor(mQueryHandler, s, false );//,genre, albumId, artistId );
        mConstraint = s;
        mConstraintIsValid = true;
        return null;//c;
    }
    
    // SectionIndexer methods
    
    @Override
	public Object[] getSections() {
        if (mIndexer != null) { 
            return mIndexer.getSections();
        } else {
            return null;
        }
    }
    
    @Override
	public int getPositionForSection(int section) {
        int pos = mIndexer.getPositionForSection(section);
        return pos;
    }
    
    @Override
	public int getSectionForPosition(int position) {
        return 0;
    }

	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	/**
	 * @return the albumId
	 */
	public String getAlbumId() {
		return albumId;
	}

	/**
	 * @param albumId the albumId to set
	 */
	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	/**
	 * @return the artistId
	 */
	public String getArtistId() {
		return artistId;
	}

	/**
	 * @param artistId the artistId to set
	 */
	public void setArtistId(String artistId) {
		this.artistId = artistId;
	}        
}
