package okosama.app.tab;
// import android.R;
import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ProgressBar;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.widget.absWidget;

/**
 * タブの１つのページを模倣したクラス
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
	public static final int TABPAGE_ID_MEDIA_IMPL = 12;
	
	
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
		super.setActivate(bActivate);
	}

	boolean bWaitRemove = false;
	boolean bWaitAdd = false;
	
	/**
	 * @return 内部のtabIdが引数のものと一致するか 
	 */
	public boolean IsEqualTabId( int tabId ) {
		return ( this.tabId == tabId );
	}
}
