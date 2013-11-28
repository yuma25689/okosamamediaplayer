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
                		== TabPage.TABPAGE_ID_PLAY_SUB )
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
        		boolean bTabSelectReset = false;
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
	        			bTabSelectReset = true;
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
		    		mActivity.sendUpdateMessage(ControlIDs.TAB_ID_MAIN, 
		    				mActivity.getTabStocker().getCurrentTabId(ControlIDs.TAB_ID_MAIN)
		    				,true
		    		);
		    		if( mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_MEDIA) != null )
		    		{
		    			mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_MEDIA).setNextForceRefresh(true);
		    		}
		    		if( mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_PLAY) != null )
		    		{
		    			mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_PLAY).setNextForceRefresh(true);
		    		}
	    		}
	           				           	
	           	if( TimeControlPanel.getInstance() != null )
	           	{
	           		TimeControlPanel.getInstance().setDurationLabel(0);
	           	}
	           	
	           	// ���������ɁA�S�Ẵ��f�B�A���擾����
	           	// if( bDataRestored == false )
           		//Log.d("msg_init_end","force rescan");
	           	mActivity.getAdpStocker().initAllAdapter();
	           	//mActivity.setForceRefreshFlag(false);
        		mActivity.queueNextRefresh(100);
	    		break;
        	}
        	case TabSelectAction.MSG_ID_TAB_SELECT:
        	{
        		Log.w("tab select msg","id=" + message.arg1);
        		// �^�u���I�����ꂽ�ʒm

        		// �^�uID���X�V
        		mActivity.updateTabId( message.arg1, message.arg2, (Boolean)message.obj );

        		// ���X�i���X�V
        		mActivity.updateListeners(IDisplayState.STATUS_ON_CREATE);
        		mActivity.updateListeners(IDisplayState.STATUS_ON_RESUME);
            	// ���f�B�A���X�V
            	mActivity.reScanMediaAndUpdateTabPage(message.arg1,false);
            	// ���ʕ����ĕ`��
        		mActivity.queueNextRefresh(1);
        		mActivity.updatePlayStateButtonImage();
        		break;
        	}
		}
	}


}
