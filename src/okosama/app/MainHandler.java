package okosama.app;

import okosama.app.action.TabSelectAction;
import okosama.app.panel.PlayControlPanel;
import okosama.app.panel.SubControlPanel;
import okosama.app.panel.TimeControlPanel;
import okosama.app.state.IDisplayState;
import okosama.app.tab.TabPage;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MainHandler extends Handler {

	boolean bInitEnd = false;
	OkosamaMediaPlayerActivity mActivity = null;
	
	public MainHandler(OkosamaMediaPlayerActivity act)
	{
		mActivity = act;
	}
	
    //メッセージ受信
    @Override
	public void handleMessage(Message message) {
    	if( mActivity == null )
    	{
    		return;
    	}
    	switch( message.what )
		{
    		case AppStatus.REFRESH:
    		{
                long next = AppStatus.NO_REFRESH;
                if( mActivity.getTabStocker().getCurrentTabId(ControlIDs.TAB_ID_MEDIA)
                		== TabPage.TABPAGE_ID_MEDIA )
                {
                	IDisplayState stateMedia 
                	= mActivity.getStateStocker().getState(
                		ControlIDs.TAB_ID_MEDIA
                	);
        			if( stateMedia == null )
        			{
        				break;
        			}
                	next = stateMedia.updateDisplay();
                }
                else if( mActivity.getTabStocker().getCurrentTabId(ControlIDs.TAB_ID_PLAY)
                		== TabPage.TABPAGE_ID_PLAY )
                {
                	IDisplayState statePlayTab 
                	= mActivity.getStateStocker().getState(
                		ControlIDs.TAB_ID_PLAY
                	);
        			if( statePlayTab == null )
        			{
        				break;
        			}
        			// Log.e("refresh","playtab");
        			next = statePlayTab.updateDisplay();
                }
                mActivity.queueNextRefresh(next);
                break;
			}
        	case DisplayInfo.MSG_INIT_END:
        	{
        		boolean bTabSelectReset = true;
        		// 現状、これがOnResume時のディスプレイ初期化後に飛んでくる
	        	if( bInitEnd == true )
	        	{
	        		// もう既に初期化済ならば、何もしない？
	        	}
	        	else
	        	{	                
	                OkosamaMediaPlayerActivity.getResourceAccessor().initMotionSenser(mActivity);
	                OkosamaMediaPlayerActivity.getResourceAccessor().initSound();
	        		
	        		TimeControlPanel.createInstance(mActivity);
	        		PlayControlPanel.createInstance(mActivity);
	        		SubControlPanel.createInstance(mActivity);
		            
	        		// 初期化されていなければ、タブを作成
	        		// このアクティビティのレイアウトクラスを渡す
	        		if( mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_MAIN) == null )
	        		{ 
	        			mActivity.getTabStocker().createTabMain(
	        				mActivity.getMainPageContainer(),
	        				mActivity.getMainComponentContainer()
    					);
	        		}
	        		else
	        		{
	        			bTabSelectReset = false;
	        		}
	        	}
	    		bInitEnd = true;
	        	
	            // 時間表示等の初期化
	    		mActivity.updatePlayStateButtonImage();

	    		// 現在選択中のタブの情報をクリアする
	            // TODO:場所微妙
	    		mActivity.clearDisplayIdMap();
	           	// tabCurrentDisplayIdMap.clear();
	            // 必要であれば、設定を復元する
	            // TODO:これが現在OnCreateのタイミングなのは要検討
	            // OnResumeの方がいいかもしれない
	            // 画面移動&初期化処理
//	           	if( bTabInitEnd == false )
//	           	{
	    		mActivity.reloadDisplayIdMap();
	    		if(bTabSelectReset == true )
	    		{
		    		mActivity.updateTimeDisplayVisible(0);
		    		mActivity.updateTimeDisplay(0);			    			
		    		mActivity.sendUpdateMessage(ControlIDs.TAB_ID_MAIN, mActivity.getTabStocker().getCurrentTabId(ControlIDs.TAB_ID_MAIN));
	    		}
	           				           	
	           	if( TimeControlPanel.getInstance() != null )
	           	{
	           		TimeControlPanel.getInstance().setDurationLabel(0);
	           	}
	           	
	           	// 初期化時に、全てのメディアを取得する
	           	// if( bDataRestored == false )
           		//Log.d("msg_init_end","force rescan");
	           	mActivity.getAdpStocker().initAllAdapter();
	    		break;
        	}
        	case TabSelectAction.MSG_ID_TAB_SELECT:
        	{
        		Log.w("tab select msg","id=" + (Integer)message.obj);
        		// タブが選択された通知
 
        		if( ControlIDs.TAB_ID_MAIN == (Integer)message.obj )
        		{
	        		// Activityのタブidを更新
        			int id = message.arg2;
        			if( TabPage.TABPAGE_ID_NONE == id 
        			|| TabPage.TABPAGE_ID_UNKNOWN == id )
        			{
        				id = TabPage.TABPAGE_ID_PLAY;
        			}	
        			mActivity.setMainTabSelection(
        				id      					
	        			// mActivity.getCurrentDisplayId( ControlIDs.TAB_ID_MAIN )
	        		);
	        		Log.e("maintab select","INIT_END");			        		
        		}
        		else if( ControlIDs.TAB_ID_MEDIA == (Integer)message.obj )
        		{
        			int id = message.arg2;//mActivity.getCurrentDisplayId( 
//        				ControlIDs.TAB_ID_MEDIA 
//        			);
        			if( TabPage.TABPAGE_ID_NONE == id 
        			|| TabPage.TABPAGE_ID_UNKNOWN == id )
        			{
        				id = TabPage.TABPAGE_ID_ARTIST;
        			}
        			Log.e("mediatab select","INIT_END");
        			mActivity.setMediaTabSelection( id );
        		}
        		else if( ControlIDs.TAB_ID_PLAY == (Integer)message.obj )
        		{
        			int id = message.arg2;//mActivity.getCurrentDisplayId( ControlIDs.TAB_ID_PLAY );
        			if( TabPage.TABPAGE_ID_NONE == id 
        			|| TabPage.TABPAGE_ID_UNKNOWN == id )
        			{
        				id = TabPage.TABPAGE_ID_PLAY_SUB;
        			}
        			mActivity.setPlayTabSelection( id );
        		}
        		// リスナを更新
        		mActivity.updateListeners(IDisplayState.STATUS_ON_CREATE);
        		mActivity.updateListeners(IDisplayState.STATUS_ON_RESUME);
            	// メディアを更新
            	mActivity.reScanMedia((Integer)message.obj,false);
            	mActivity.setForceRefreshFlag(false);
            	// 共通部分再描画
        		mActivity.updateCommonCtrls();
        		mActivity.updatePlayStateButtonImage();
        		break;
        	}
		}
	}


}
