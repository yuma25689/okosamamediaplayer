package okosama.app.anim;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.tab.Tab;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class TabOutAnimationListener implements AnimationListener {
	ViewGroup tabBaseLayout 		= null;
	ViewGroup componentContainer 	= null;
	int tabId = 0;
	int lastLockTabId = 0;
	
	public TabOutAnimationListener(ViewGroup v, ViewGroup p, int i, int j)
	{
		tabBaseLayout = v;
		componentContainer = p;
		tabId = i;
		lastLockTabId = j;
	}
	
    @Override
    public void onAnimationEnd(Animation animation) {
		Log.d("onAnimationEnd","come:" + tabId);
    	
		//Log.i("anim_end","ok");
		componentContainer.post(new Runnable() {
            @Override
			public void run() {
            	if( 0 <= componentContainer.indexOfChild( tabBaseLayout ))
            	{
            		componentContainer.removeView( tabBaseLayout );
            		Log.d("anim","remove:" + tabId);
		    	}
        		Log.d("anim","out end:" + tabId);
        		if( lastLockTabId != 0 )
        		{
        			ReleaseTabSelectionLock( lastLockTabId );
        		}
//        		componentContainer.removeAllViews();
//        		componentContainer.invalidate();		
        		
            }
        });				
    }

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		Log.i("anim_repeat","ok");
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		Log.i("anim_start","ok");
	}  
	public void ReleaseTabSelectionLock( int tabId)
	{
		lastLockTabId = 0;
		// タブのロック解除
		Tab tab = null;
		// TODO: タブIDからタブを取得する関数を作成
		if( tabId == ControlIDs.TAB_ID_MAIN )
		{
			tab = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTabStocker().getTab(ControlIDs.TAB_ID_MAIN);
		}
		else if( tabId == ControlIDs.TAB_ID_MEDIA )
		{
			tab = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTabStocker().getTab(ControlIDs.TAB_ID_MEDIA);
		}
		else if( tabId == ControlIDs.TAB_ID_PLAY )
		{
			tab = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTabStocker().getTab(ControlIDs.TAB_ID_PLAY);
		}
		if( tab != null )
		{
			tab.setEnableAllTab(true);
		}
	}
	
}

