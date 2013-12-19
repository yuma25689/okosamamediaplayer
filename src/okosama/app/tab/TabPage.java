package okosama.app.tab;
// import android.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import okosama.app.MusicSettingsActivity;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.panel.TabMoveLeftInfoPanel;
import okosama.app.panel.TabMoveRightInfoPanel;
import okosama.app.panel.TouchHookRelativeLayout;

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
	protected TabMoveRightInfoPanel rightPanel;
	protected TabMoveLeftInfoPanel leftPanel;
	
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
	
	public static void addLayoutFromParent( ViewGroup layout, ViewGroup parent )
	{
		if( layout.getParent() != null )
		{
			if( layout.getParent() instanceof ViewGroup )
				((ViewGroup)layout.getParent()).removeView( layout );
		}
		if( 0 > parent.indexOfChild( layout ))
    	{
			parent.addView( layout );
			// parent.invalidate();
    	}
		
	}
	
	public static void removeLayoutFromParent( ViewGroup layout, ViewGroup parent )
	{
    	if( 0 <= parent.indexOfChild( layout ))
    	{
    		parent.removeView( layout );
    	}		
	}
	
	
	@Override
	public void setActivate( boolean bActivate )
	{
        SharedPreferences prefs 
        = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getSharedPreferences(
                MusicSettingsActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);
        boolean bEnableAnim = prefs.getBoolean(MusicSettingsActivity.KEY_ENABLE_ANIMATION, false);
		
		if( bActivate )
		{
			if( bEnableAnim )
			{
				OkosamaMediaPlayerActivity.getResourceAccessor().tabAnim.requestTabMove(
					TabChangeAnimation.TAB_IN, tabBaseLayout, componentContainer, this.tabId);
			}
			else
			{
				addLayoutFromParent( tabBaseLayout, componentContainer );				
			}

		}
		else
		{
			if( bEnableAnim )
			{
				Log.d("tab out anim","come");
				OkosamaMediaPlayerActivity.getResourceAccessor().tabAnim.requestTabMove(
					TabChangeAnimation.TAB_OUT, tabBaseLayout, componentContainer, this.tabId);
			}
			else
			{
				removeLayoutFromParent( tabBaseLayout, componentContainer );
			}
	
		}
		// bPrevActivate = bActivate;
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
	/**
	 * タブページのステータスバーとなるViewGroupを取得する
	 * @return
	 */
	public ViewGroup getInfoBar()
	{
		return null;
	}
}
