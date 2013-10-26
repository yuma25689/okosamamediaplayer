package okosama.app.tab;
// import android.R;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import okosama.app.OkosamaMediaPlayerActivity;

/**
 * タブの１つのページを模倣したクラス
 * @author 25689
 *
 */
public abstract class TabPage extends TabComponentParent {

//	Animation animIn = null;
//	Animation animOut = null;
	
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
//			if(bWaitRemove == true )
//			{
//				animOut.cancel();
//				bWaitRemove = false;
//				componentContainer.removeView( tabBaseLayout );				
//			}
//			if( animIn != null )
//			{
//				animIn.cancel();
//			}
//			else
//			{
//				animIn = AnimationUtils.loadAnimation(
//						OkosamaMediaPlayerActivity.getResourceAccessor().getActivity()
//						, R.anim.left_in );//android.R.anim.slide_in_left );
//			}
//    		if( tabBaseLayout.getParent() != null )
//    		{
//    			if( tabBaseLayout.getParent() instanceof ViewGroup )
//    				((ViewGroup)tabBaseLayout.getParent()).removeView( tabBaseLayout );
//    		}
//			tabBaseLayout.startAnimation(animIn);
//			componentContainer.addView( tabBaseLayout );
		}
		else
		{
			OkosamaMediaPlayerActivity.getResourceAccessor().tabAnim.requestTabMove(
					TabChangeAnimation.TAB_OUT, tabBaseLayout, componentContainer, this.tabId);			
//			if( animOut != null )
//			{
//				animOut.cancel();
//				componentContainer.removeView( tabBaseLayout );
//			}
//			else
//			{
//				animOut = AnimationUtils.loadAnimation(
//						OkosamaMediaPlayerActivity.getResourceAccessor().getActivity()
//						, R.anim.right_out );
//			}
//			//R.anim.slide_out_right
//			bWaitRemove = true;
//			animOut.setAnimationListener(
//			animationListener);
//			// tabBaseLayout.setAnimation(anim);
//			//animOut.setFillAfter(true);
//			tabBaseLayout.startAnimation(animOut);
//			// componentContainer.removeView( tabBaseLayout );			
		}
		super.setActivate(bActivate);
	}

	boolean bWaitRemove = false;
	boolean bWaitAdd = false;
	
	private AnimationListener animationListener = new AnimationListener() {

		    @Override
		    public void onAnimationEnd(Animation animation) {
				//Log.i("anim_end","ok");
				componentContainer.post(new Runnable() {
		            @Override
					public void run() {
                    	if( bWaitRemove )
        		    	{
                    		if( -1 != componentContainer.indexOfChild( tabBaseLayout ))
                    			componentContainer.removeView( tabBaseLayout );
        		    		bWaitRemove = false;
        		    	}
		            }
		        });				
		    }

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				//Log.i("anim_start","ok");
			}  
	};
	/**
	 * @return 内部のtabIdが引数のものと一致するか 
	 */
	public boolean IsEqualTabId( int tabId ) {
		return ( this.tabId == tabId );
	}
}
