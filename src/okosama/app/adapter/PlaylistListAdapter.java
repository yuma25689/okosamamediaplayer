package okosama.app.adapter;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.storage.Database;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * �v���C���X�g�̃A�_�v�^�H
 * TODO:newView�͕s�v�H�Ȃ��H
 * TODO:indexer�͌p�����Ȃ��H
 * @author 25689
 *
 */
public class PlaylistListAdapter extends SimpleCursorAdapter {
    private static final long RECENTLY_ADDED_PLAYLIST = -1;
    private static final long ALL_SONGS_PLAYLIST = -2;
    private static final long PODCASTS_PLAYLIST = -3;
	
    // �e�J�����̃C���f�b�N�X�ێ��p
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

	private OkosamaMediaPlayerActivity mActivity = null;
    private AsyncQueryHandler mQueryHandler;
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;

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

    public PlaylistListAdapter(Context context, OkosamaMediaPlayerActivity currentactivity,
            int layout, Cursor cursor, String[] from, int[] to) {
        super(context, layout, cursor, from, to);
        mActivity = currentactivity;
        getColumnIndices(cursor);
        mQueryHandler = new QueryHandler(context.getContentResolver());
    }
    private void getColumnIndices(Cursor cursor) {
        if (cursor != null) {
            mTitleIdx = cursor.getColumnIndexOrThrow(PlaylistsColumns.NAME);
            mIdIdx = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        }
    }

//    public void setActivity(OkosamaMediaPlayerActivity newactivity) {
//        mActivity = newactivity;
//    }
    
    public AsyncQueryHandler getQueryHandler() {
        return mQueryHandler;
    }

    /**
     * �r���[�̕R�t��
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        
    	// �^�C�g���̐ݒ�
        TextView tv = (TextView) view.findViewById(R.id.line1);
        
        String name = cursor.getString(mTitleIdx);
        tv.setText(name);
        
        // id���擾�H
        long id = cursor.getLong(mIdIdx);
        
        // id�̎�ނɂ���āA�A�C�R���̉摜��ς���
        ImageView iv = (ImageView) view.findViewById(R.id.icon);
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
        iv = (ImageView) view.findViewById(R.id.play_indicator);
        iv.setVisibility(View.GONE);

        view.findViewById(R.id.line2).setVisibility(View.GONE);
    }

    /**
     * �J�[�\����ύX����
     */
    @Override
    public void changeCursor(Cursor cursor) {
        if (mActivity.isFinishing() && cursor != null) {
        	// �A�N�e�B�r�e�B���I�����Ȃ�΁A�J�[�\���N���[�Y
            cursor.close();
            cursor = null;
        }
        //if (false == isEqualCursor( cursor, Database.getInstance(mActivity).getCursor(Database.PlaylistCursorName))) {
        	// �J�[�\�����ύX����Ă�����A�J�[�\�����Đݒ肷��
        	Database.getInstance(mActivity).setCursor(Database.PlaylistCursorName, cursor);
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
     * �w�i�ŃN�G���𔭍s����Ƃ��Ɏ��s�����֐��A��������Ȃ��B
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String s = constraint.toString();
        // �t�B���^���ύX����Ă��Ȃ�������A�����N�G���Ĕ��s���Ȃ�
        if (mConstraintIsValid && (
                (s == null && mConstraint == null) ||
                (s != null && s.equals(mConstraint)))) {
            return getCursor();
        }
        Cursor c = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).createPlaylistCursor(null, s, createShortcut);
        mConstraint = s;
        mConstraintIsValid = true;
        return c;
    }
}
