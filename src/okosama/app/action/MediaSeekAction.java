package okosama.app.action;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.service.MediaPlayerUtil;
import android.R;
import android.os.RemoteException;
import android.view.View;

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
			if( MediaPlayerUtil.sService != null 
			&& MediaPlayerUtil.sService.isPlaying() == true )
			{
				MediaPlayerUtil.sService.seek( seekVal );
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
