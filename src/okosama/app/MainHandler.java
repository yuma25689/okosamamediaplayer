package okosama.app;

import okosama.app.panel.NowPlayingControlPanel;
import okosama.app.panel.PlayControlPanel;
import okosama.app.panel.SearchPanel;
import okosama.app.panel.SubControlPanel;
import okosama.app.panel.TimeControlPanel;
import okosama.app.state.IDisplayState;
import okosama.app.tab.TabPage;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class MainHandler extends Handler {

	static final int INIT_ALL_REFRESH = 103;
	static final int MEDIA_SCAN_TARGET_CREATED = 107;
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
	    	case MEDIA_SCAN_TARGET_CREATED:
	    		mActivity.updateAndroidMediaDatabase();
	    		break;
	    	case AppStatus.RESTART:
				LogWrapper.e("app restart","come");
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
                else if(PlayControlPanel.getInstance().getView() != null 
                && PlayControlPanel.getInstance().getView().getParent() != null )                		
                		//mActivity.getTabStocker().getCurrentTabPageId(ControlIDs.TAB_ID_MAIN)
                		//== TabPage.TABPAGE_ID_PLAY )
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
        			// LogWrapper.e("refresh","playtab");
        			next = statePlayTab.updateDisplay();
                }
                mActivity.queueNextRefresh(next);
                break;
			}
    		case AppStatus.INIT_ALL_REFRESH:
	           	mActivity.getGenreStocker().stockMediaDataFromDevice();
	           	//mActivity.getAdpStocker().initAllAdapter();    			
    			break;
        	case DisplayInfo.MSG_INIT_END:
        	{
        		boolean bTabForceReset = false;
        		if( message.obj != null )
        		{
        			bTabForceReset = (Boolean) message.obj;
        		}
        		boolean bTabSelectReset = false;
        		// ����A���ꂪOnResume���̃f�B�X�v���C��������ɔ��ł���
                // * �쐬���Ԉˑ����L��
        		// �p�l���̍쐬
        		// ��]���ɂ��쐬���������̂Ƃ���
        		TimeControlPanel.createInstance(mActivity);
        		NowPlayingControlPanel.createInstance(mActivity);
        		SubControlPanel.createInstance(mActivity);
        		PlayControlPanel.createInstance(mActivity);
        		SearchPanel.createInstance(mActivity);
	        	if( bInitEnd == true )
	        	{
	        		// �������ɏ������ςȂ�΁A�������Ȃ��H
	        	}
	        	else
	        	{	                
	                OkosamaMediaPlayerActivity.getResourceAccessor().initMotionSenser(mActivity);
	                OkosamaMediaPlayerActivity.getResourceAccessor().initSound();
	
	                // * �쐬���Ԉˑ����L��
	                // PlayControlPanel����ATimeControlPanel��NowPlayingControlPanel�ւ̎Q��
//	        		TimeControlPanel.createInstance(mActivity);
//	        		NowPlayingControlPanel.createInstance(mActivity);
//	        		SubControlPanel.createInstance(mActivity);
//	        		PlayControlPanel.createInstance(mActivity);
//		            
	        	}
	        	if( bInitEnd == false || bTabForceReset == true )
	        	{
	        		// ����������Ă��Ȃ����A�����쐬�̏ꍇ�i���̂Ƃ���A���ꂪ����̂͌����ύX�j�A�^�u���쐬
	        		// ���̃A�N�e�B�r�e�B�̃��C�A�E�g�N���X��n��
	        		if( mActivity.getTabStocker().getTab(ControlIDs.TAB_ID_MAIN) == null )
	        		{
	        			mActivity.getTabStocker().createTabMain(
	        				mActivity.getMainPageContainer(),
	        				mActivity.getMainComponentContainer()
    					);
	        			mActivity.getAdpStocker().initAllAdapter();	        			
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
	           	
	           	// ���������ɁA�S�Ẵ��f�B�A���擾����
	           	// if( bDataRestored == false )
           		//Log.d("msg_init_end","force rescan");
//	           	mActivity.getGenreStocker().stockMediaDataFromDevice();
//	           	mActivity.getAdpStocker().initAllAdapter();
//	           	Message msgInitAllRef = new Message();
//	           	msgInitAllRef.what = AppStatus.INIT_ALL_REFRESH;
//	           	sendMessageDelayed(msgInitAllRef, 300);
	           	//mActivity.setForceRefreshFlag(false);
        		mActivity.queueNextRefresh(1000);
	    		break;
        	}
//        	case TabSelectAction.MSG_ID_TAB_SELECT:
//        	{
//        		LogWrapper.w("tab select msg","id=" + message.arg1);
//        		// �^�u���I�����ꂽ�ʒm
//
//        		// �^�uID���X�V
//        		mActivity.updateTabId( message.arg1, message.arg2, (Boolean)message.obj );
//
//        		// ���X�i���X�V
//        		mActivity.updateListeners(IDisplayState.STATUS_ON_CREATE);
//        		mActivity.updateListeners(IDisplayState.STATUS_ON_RESUME);
//            	// ���f�B�A���X�V
//            	mActivity.reScanMediaAndUpdateTabPage(message.arg1,false);
//            	// ���ʕ����ĕ`��
//        		mActivity.queueNextRefresh(100);
//        		mActivity.updatePlayStateButtonImage();
//        		break;
//        	}
		}
	}


}
