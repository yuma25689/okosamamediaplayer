package okosama.app.action;

import okosama.app.LogWrapper;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.service.MediaPlayerUtil;
import android.os.RemoteException;

/**
 * ���Ԃ��N���b�N�������Ɏ��s����A�N�V����
 * @author 25689
 *
 */
public final class TimeButtonClickAction implements IViewAction {

	public static final int TIME_ID_UNKNOWN = -5;
	public static final int TIME_ID_HOUR_10 = 10 * 60 * 60;
	public static final int TIME_ID_HOUR_1 = 60 * 60;
	public static final int TIME_ID_MINUTE_10 = 10 * 60;
	public static final int TIME_ID_MINUTE_1 = 60;
	public static final int TIME_ID_SEC_10 = 10;
	public static final int TIME_ID_SEC_1 = 1;
	int timeID = TIME_ID_UNKNOWN;

	public TimeButtonClickAction(int timeID_) {
		super();
		this.timeID = timeID_;
	}

	/**
	 * 
	 */
	@Override
	public int doAction( Object param ) {

        try
        {
			OkosamaMediaPlayerActivity.getResourceAccessor().playSound(9);
        	
			// ����AAudioId�Ŕ��ʂ��Ă��邯�ǁA�{����Video�̕��͑��v�Ȃ�ł��傤���H
	        boolean bPlayingOrCueing = MediaPlayerUtil.sService.isPlaying() 
	        		|| MediaPlayerUtil.sService.getAudioId() != -1;
			// OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
	        if( bPlayingOrCueing && this.timeID != TIME_ID_UNKNOWN)
	        {
				// �Đ������ǂ����ŁA������U�蕪����
	        	long pos = MediaPlayerUtil.sService.position();
	        	long posSec = pos / 1000;
	        	
	        	posSec += this.timeID;
	        	if( ( MediaPlayerUtil.sService.duration() / 1000 ) 
	        			< posSec )
	        	{
	        		IViewAction action = new NextAction();
	        		action.doAction(null);
	        	}
	        	MediaSeekAction action = new MediaSeekAction();
	        	action.doAction( posSec * 1000 );
//	        	MediaPlayerUtil.sService.seek(posSec * 1000);
//				act.updateTimeDisplay(posSec);	
	        }
        }
        catch( RemoteException ex )
        {
        	LogWrapper.e("error - timebuttonclick", ex.getMessage() );
        }
		return 0;
	}

}
