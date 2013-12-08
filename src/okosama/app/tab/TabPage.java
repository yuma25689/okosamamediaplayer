package okosama.app.tab;
// import android.R;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import okosama.app.OkosamaMediaPlayerActivity;

/**
 * �^�u�̂P�̃y�[�W��͕킵���N���X
 * @author 25689
 *
 */
public abstract class TabPage extends TabComponentParent {

//	Animation animIn = null;
//	Animation animOut = null;
	protected ProgressBar progressUpdateing = null;
	
	// protected Button tabButton;
	protected Drawable bk_drawable;
	
	protected int tabId = TABPAGE_ID_UNKNOWN;
	
	public static final int TABPAGE_ID_NONE = -1;
	public static final int TABPAGE_ID_UNKNOWN = 0;
	public static final int TABPAGE_ID_PLAY = 1;
	public static final int TABPAGE_ID_MEDIA = 2;
	public static final int TABPAGE_ID_MOVIE = 3;
	public static final int TABPAGE_ID_ARTIST = 4;
	public static final int TABPAGE_ID_ALBUM = 5;
	public static final int TABPAGE_ID_SONG = 6;
	public static final int TABPAGE_ID_PLAYLIST = 7;
	public static final int TABPAGE_ID_NOW_PLAYLIST = 8;
	public static final int TABPAGE_ID_VIDEO = 9;
	public static final int TABPAGE_ID_MEDIA_IMPL = 12;
	public static final int TABPAGE_ID_PLAY_SUB = 13;
	public static final int TABPAGE_ID_VIDEO_VIEW = 14;
	
	// private boolean bPrevActivate = false;
	
	@Override
	public void setActivate( boolean bActivate )
	{
		if( bActivate )
		{
			OkosamaMediaPlayerActivity.getResourceAccessor().tabAnim.requestTabMove(
					TabChangeAnimation.TAB_IN, tabBaseLayout, componentContainer, this.tabId);

		}
		else
		{
			OkosamaMediaPlayerActivity.getResourceAccessor().tabAnim.requestTabMove(
					TabChangeAnimation.TAB_OUT, tabBaseLayout, componentContainer, this.tabId);			
	
		}
		// bPrevActivate = bActivate;
		super.setActivate(bActivate);
	}

	boolean bWaitRemove = false;
	boolean bWaitAdd = false;
	
	/**
	 * @return ������tabId�������̂��̂ƈ�v���邩 
	 */
	public boolean IsEqualTabId( int tabId ) {
		return ( this.tabId == tabId );
	}
	/**
	 * �^�u�y�[�W�̃X�e�[�^�X�o�[�ƂȂ�ViewGroup���擾����
	 * @return
	 */
	public ViewGroup getInfoBar()
	{
		return null;
	}
}
