package okosama.app.tab;

import okosama.app.ControlIDs;
import okosama.app.MusicSettingsActivity;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.anim.TabAnimationFactory;
import okosama.app.anim.TabOutAnimationListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;

/**
 * tabの変更が連打された時のために、タブの変更は、このクラスにキューとして持つようにする
 * @author 25689
 *
 */
public class TabChangeAnimation {

	// Animation animIn = null;
	// Animation animOut = null;
	
	int tabPageCnt[] = new int[12];
	
	// 処理内容
	public static final int TAB_IN = 1;
	public static final int TAB_OUT = 2;
	long outAnimDelay = 0;
	
	// 1回のタブセレクト処理ごとに、idを持つものとする
	final int MAX_TABSELECTION_PROCESS_ID = 10000;
	int tabSelectionProcessId = 0;
	int mostNewSelectionProcessId = 0;
	//int lastProcessId = 0;
	int lastLockTabId = 0;
	final int MAX_ANIM_WAIT = 1000;
	// このフラグがたっているときに来たリクエストは、全て同じ選択処理の一部と見なす
	boolean bGroupingTabSelectionProc = false;
	public void SetTabSelectionLock(boolean b, int tabId, int tabPageId)
	{
		if( b == true )
		{
			lastLockTabId = tabId;
		}
		else
		{
			lastLockTabId = 0;
		}
		// タブのロック
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
			tab.setEnableAllTab(!b,tabPageId);
		}
		// bGroupingTabSelectionProc = b;
	}
//	int getNextTabSelectionId()
//	{
//		if( bGroupingTabSelectionProc )
//		{
//			return tabSelectionProcessId;
//		}
//		if( false == handler.hasMessages(TAB_IN) 
//		&& false == handler.hasMessages(TAB_OUT) )
//		{
//			tabSelectionProcessId = 0;
//		}
//		else
//		{
//			if( MAX_TABSELECTION_PROCESS_ID <= tabSelectionProcessId )
//			{
//				tabSelectionProcessId = 0;
//			}
//			else
//			{
//				tabSelectionProcessId++;
//			}
//		}
//		return tabSelectionProcessId;
//	}
	
	public class TabChangeAnimationTarget {
		TabChangeAnimationTarget(ViewGroup t,ViewGroup p)
		{
			target = t;
			parent = p;
		}
		ViewGroup target;
		ViewGroup parent;
	}
	public void requestTabMove( int iMoveType, ViewGroup target, ViewGroup parent, int tabID )
	{
		// mostNewSelectionProcessId = getNextTabSelectionId();
		doAnimation(
				iMoveType,
				tabID,
				new TabChangeAnimationTarget( target, parent )
		);
		//long delay = 1;
//		if( outAnimDelay != 0 )
//		{
//			delay = outAnimDelay;
//		}
		// handler.sendMessageDelayed(msg, delay);
	}
	public void doAnimation(int iMoveType, int tabId, TabChangeAnimationTarget target ) {
    	if( iMoveType != TAB_IN
    	&&  iMoveType != TAB_OUT )
    	{
    		return;
    	}
		// lastProcessId = message.arg1;
		// TabChangeAnimationTarget target = (TabChangeAnimationTarget)message.obj;
		ViewGroup tabBaseLayout 		= target.target;
		ViewGroup componentContainer 	= target.parent;
        SharedPreferences prefs 
        = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getSharedPreferences(
                MusicSettingsActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);
        boolean bEnableAnim = prefs.getBoolean(MusicSettingsActivity.KEY_ENABLE_ANIMATION, false);
        int nLevel = 0;
        String sLevel;
        String sDuration;
        long nDuration = 0;
        if( bEnableAnim )
        {
            sLevel = prefs.getString(MusicSettingsActivity.KEY_ANIMATION_LEVEL, "");
            if( sLevel != null && sLevel.length() > 0 )
            {
            	nLevel = Integer.parseInt(sLevel);
            }
            sDuration = prefs.getString(MusicSettingsActivity.KEY_ANIMATION_SPEED, "");
            if( sDuration != null && sDuration.length() > 0 )
            {
            	nDuration = Long.parseLong(sDuration);
            }
        }
		switch( iMoveType )
		{
    	case TAB_IN:
    		if( tabBaseLayout.getParent() != null )
    		{
    			if( tabBaseLayout.getParent() instanceof ViewGroup )
    				((ViewGroup)tabBaseLayout.getParent()).removeView( tabBaseLayout );
    		}
    		if( 0 > componentContainer.indexOfChild( tabBaseLayout ))
        	{
    			componentContainer.addView( tabBaseLayout );
    			componentContainer.invalidate();
        	}
    		
            if( bEnableAnim )
            {
				// アニメーションは動的に生成
				 Animation animIn = TabAnimationFactory.createTabInAnimation(nLevel,nDuration);
				 Log.d("anim","in start:" + tabId );
				 tabBaseLayout.startAnimation(animIn);                	
            }
            break;
    	case TAB_OUT:
    		
			//if( animOut == null )
            if( bEnableAnim )
            {
				Animation animOut = TabAnimationFactory.createTabOutAnimation(nLevel,nDuration,
						new TabOutAnimationListener(
        						tabBaseLayout,
        						componentContainer,
        						tabId,
        						lastLockTabId)
				);
    			// outAnimDelay = animOut.getDuration();
//    			animOut.setAnimationListener(
//        				new TabOutAnimationListener(
//        						tabBaseLayout,
//        						componentContainer,
//        						tabId,
//        						lastLockTabId)
//        			);
    			Log.d("anim_out","out start:" + tabId + " lastlocktabId:" + lastLockTabId );        			
    			tabBaseLayout.startAnimation(animOut);                	
            }
            else
            {
        		if( lastLockTabId != 0 )
        		{
        			SetTabSelectionLock( false, lastLockTabId, tabId );
        		}
            }    			
    		break;
		}
	}    
}
