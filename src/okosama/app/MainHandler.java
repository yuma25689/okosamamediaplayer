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
	
    //���b�Z�[�W��M
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
        		// ����A���ꂪOnResume���̃f�B�X�v���C��������ɔ��ł���
	        	if( bInitEnd == true )
	        	{
	        		// �������ɏ������ςȂ�΁A�������Ȃ��H
	        	}
	        	else
	        	{	                
	                OkosamaMediaPlayerActivity.getResourceAccessor().initMotionSenser(mActivity);
	                OkosamaMediaPlayerActivity.getResourceAccessor().initSound();
	        		
	        		TimeControlPanel.createInstance(mActivity);
	        		PlayControlPanel.createInstance(mActivity);
	        		SubControlPanel.createInstance(mActivity);
		            
	        		// ����������Ă��Ȃ���΁A�^�u���쐬
	        		// ���̃A�N�e�B�r�e�B�̃��C�A�E�g�N���X��n��
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
	        	
	            // ���ԕ\�����̏�����
	    		mActivity.updatePlayStateButtonImage();

	    		// ���ݑI�𒆂̃^�u�̏����N���A����
	            // TODO:�ꏊ����
	    		mActivity.clearDisplayIdMap();
	           	// tabCurrentDisplayIdMap.clear();
	            // �K�v�ł���΁A�ݒ�𕜌�����
	            // TODO:���ꂪ����OnCreate�̃^�C�~���O�Ȃ̂͗v����
	            // OnResume�̕���������������Ȃ�
	            // ��ʈړ�&����������
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
	           	
	           	// ���������ɁA�S�Ẵ��f�B�A���擾����
	           	// if( bDataRestored == false )
           		//Log.d("msg_init_end","force rescan");
	           	mActivity.getAdpStocker().initAllAdapter();
	    		break;
        	}
        	case TabSelectAction.MSG_ID_TAB_SELECT:
        	{
        		Log.w("tab select msg","id=" + (Integer)message.obj);
        		// �^�u���I�����ꂽ�ʒm
 
        		if( ControlIDs.TAB_ID_MAIN == (Integer)message.obj )
        		{
	        		// Activity�̃^�uid���X�V
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
        		// ���X�i���X�V
        		mActivity.updateListeners(IDisplayState.STATUS_ON_CREATE);
        		mActivity.updateListeners(IDisplayState.STATUS_ON_RESUME);
            	// ���f�B�A���X�V
            	mActivity.reScanMedia((Integer)message.obj,false);
            	mActivity.setForceRefreshFlag(false);
            	// ���ʕ����ĕ`��
        		mActivity.updateCommonCtrls();
        		mActivity.updatePlayStateButtonImage();
        		break;
        	}
		}
	}


}
