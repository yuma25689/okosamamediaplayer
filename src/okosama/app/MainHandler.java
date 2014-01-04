package okosama.app;

import okosama.app.panel.NowPlayingControlPanel;
import okosama.app.panel.PlayControlPanel;
import okosama.app.panel.SubControlPanel;
import okosama.app.panel.TimeControlPanel;
import okosama.app.state.IDisplayState;
import okosama.app.tab.TabPage;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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
	    	case AppStatus.RESTART:
				Log.e("app restart","come");
				mActivity.finish();
				Toast.makeText(mActivity, R.string.need_restart_because_sdcard_status_change, Toast.LENGTH_LONG).show();
				System.gc();
				//mActivity.startActivity((new Intent( mActivity, OkosamaMediaPlayerActivity.class)));	    		
	    		break;
    		case AppStatus.REFRESH:
    		{
                long next = AppStatus.NO_REFRESH;
                if( mActivity.getTabStocker().getCurrentTabPageId(ControlIDs.TAB_ID_MEDIA)
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
                else if( mActivity.getTabStocker().getCurrentTabPageId(ControlIDs.TAB_ID_MAIN)
                		== TabPage.TABPAGE_ID_PLAY )
                //|| mActivity.getTabStocker().getCurrentTabPageId(ControlIDs.TAB_ID_PLAY)
        		//== TabPage.TABPAGE_ID_VIDEO_VIEW )
                {
                	IDisplayState statePlayTab 
                	= mActivity.getStateStocker().getState(
                		//ControlIDs.TAB_ID_PLAY
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
        		boolean bTabSelectReset = false;
        		// 現状、これがOnResume時のディスプレイ初期化後に飛んでくる
	        	if( bInitEnd == true )
	        	{
	        		// もう既に初期化済ならば、何もしない？
	        	}
	        	else
	        	{	                
	                OkosamaMediaPlayerActivity.getResourceAccessor().initMotionSenser(mActivity);
	                OkosamaMediaPlayerActivity.getResourceAccessor().initSound();
	
	                // * 作成順番依存性有り
	                // PlayControlPanelから、TimeControlPanelとNowPlayingControlPanelへの参照
	        		TimeControlPanel.createInstance(mActivity);
	        		NowPlayingControlPanel.createInstance(mActivity);
	        		SubControlPanel.createInstance(mActivity);
	        		PlayControlPanel.createInstance(mActivity);
		            
	        		// 初期化されていなければ、タブを作成
	        		// このアクティビティのレイアウトクラスを渡す
	        		if( mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_MAIN) == null )
	        		{ 
	        			mActivity.getTabStocker().createTabMain(
	        				mActivity.getMainPageContainer(),
	        				mActivity.getMainComponentContainer()
    					);
	        			bTabSelectReset = true;
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
		    		if( mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_MEDIA) != null )
		    		{
		    			mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_MEDIA).setNextForceRefresh(true);
		    		}
		    		if( mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_PLAY) != null )
		    		{
		    			mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_PLAY).setNextForceRefresh(true);
		    		}
		    		mActivity.sendUpdateMessage(ControlIDs.TAB_ID_MAIN, 
		    				mActivity.getTabStocker().getCurrentTabPageId(ControlIDs.TAB_ID_MAIN)
		    				,true
		    		);
	    		}
	           				           	
	           	if( TimeControlPanel.getInstance() != null )
	           	{
	           		TimeControlPanel.getInstance().setDurationLabel(0);
	           	}
	           	
	           	// 初期化時に、全てのメディアを取得する
	           	// if( bDataRestored == false )
           		//Log.d("msg_init_end","force rescan");
	           	mActivity.getGenreStocker().stockMediaDataFromDevice();
	           	mActivity.getAdpStocker().initAllAdapter();
	           	//mActivity.setForceRefreshFlag(false);
        		mActivity.queueNextRefresh(1000);
	    		break;
        	}
//        	case TabSelectAction.MSG_ID_TAB_SELECT:
//        	{
//        		Log.w("tab select msg","id=" + message.arg1);
//        		// タブが選択された通知
//
//        		// タブIDを更新
//        		mActivity.updateTabId( message.arg1, message.arg2, (Boolean)message.obj );
//
//        		// リスナを更新
//        		mActivity.updateListeners(IDisplayState.STATUS_ON_CREATE);
//        		mActivity.updateListeners(IDisplayState.STATUS_ON_RESUME);
//            	// メディアを更新
//            	mActivity.reScanMediaAndUpdateTabPage(message.arg1,false);
//            	// 共通部分再描画
//        		mActivity.queueNextRefresh(100);
//        		mActivity.updatePlayStateButtonImage();
//        		break;
//        	}
		}
	}


}
