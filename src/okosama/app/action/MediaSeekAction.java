package okosama.app.action;

import okosama.app.ControlIDs;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.state.IDisplayState;
import android.os.RemoteException;
// import android.R;
// import android.view.View;

/**
 * ���f�B�A���w�莞�ԃV�[�N����A�N�V����
 * @author 25689
 *
 */
public final class MediaSeekAction implements IViewAction {

	// long seekVal_ms;
	public MediaSeekAction() {
		super();
		// this.seekVal_ms = val;
	}

	/**
	 * @throws  
	 * 
	 */
	@Override
	public int doAction( Object param ) {

		OkosamaMediaPlayerActivity.getResourceAccessor().playSound(3);
		
		if( param == null )
			return 0;
		
		long seekVal = (Long)param;
		try {
			
			if( MediaPlayerUtil.sService != null ) 
			{
		        if( MediaPlayerUtil.sService.isPlaying() 
		        		|| MediaPlayerUtil.sService.getAudioId() != -1 )
		        {
		        	MediaPlayerUtil.sService.seek( seekVal );
		        	// ���̏����ň�x�������ԓ����X�V����
                	IDisplayState statePlayTab 
                	= OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getStateStocker().getState(
                		//ControlIDs.TAB_ID_PLAY
                		ControlIDs.TAB_ID_PLAY
                	);
        			if( statePlayTab != null )
        			{
            			statePlayTab.updateDisplay();        				
        			}		        	
		        }
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
