package okosama.app.tab;
import android.graphics.drawable.Drawable;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.widget.*;

/**
 * タブの１つのページを模倣したクラス
 * @author 25689
 *
 */
public abstract class TabPage extends TabComponentParent {

	
	protected Button tabButton;
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

	@Override
	public void setActivate( boolean b )
	{
		super.setActivate(b);
		if( tabButton != null && tabButton.getView() != null )
			tabButton.getView().bringToFront();
		
//		if( OkosamaMediaPlayerActivity.getResourceAccessor().commonBtns == null )
//		{
//			for( Button btn : OkosamaMediaPlayerActivity.getResourceAccessor().commonBtns )
//			{
//				if( btn.getView() != null )
//				{
//					btn.getView().bringToFront();
//				}
//			}
//		}
		
	}
	
	/**
	 * @param tabButton the tabButton to set
	 */
	public void setTabButton(Button tabButton) {
		this.tabButton = tabButton;
	}

	/**
	 * @return 内部のtabIdが引数のものと一致するか 
	 */
	public boolean IsEqualTabId( int tabId ) {
		return ( this.tabId == tabId );
	}
}
