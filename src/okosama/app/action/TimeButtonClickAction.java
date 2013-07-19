package okosama.app.action;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

/**
 * 時間をクリックした時に実行するアクション
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
	public int doAction( View v ) {

        try
        {
	        boolean bPlaying = MediaPlayerUtil.sService.isPlaying();
			OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
	        if( bPlaying && this.timeID != TIME_ID_UNKNOWN)
	        {
				// 再生中かどうかで、処理を振り分ける
	        	long pos = MediaPlayerUtil.sService.position();
	        	long posSec = pos / 1000;
	        	
	        	posSec += this.timeID;
	        	if( ( MediaPlayerUtil.sService.duration() / 1000 ) 
	        			< posSec )
	        	{
	        		IViewAction action = new NextAction();
	        		action.doAction(null);
	        	}
	        	MediaPlayerUtil.sService.seek(posSec * 1000);
				act.updateTimeDisplay(posSec);	
	        }
        }
        catch( RemoteException ex )
        {
        	Log.e("error - timebuttonclick", ex.getMessage() );
        }
		return 0;
	}

}
