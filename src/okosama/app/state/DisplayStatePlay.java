package okosama.app.state;

import android.os.RemoteException;
import android.util.Log;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.service.MediaPlayer;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStatePlay extends absDisplayState {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// プレイ選択画面への切り替え
		IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_PLAY );
		action.doAction(null);
		return 0;
	}
	@Override
	public int registerReceivers(int status) {
		// TODO Auto-generated method stub
		
		
		return 0;
	}
	@Override
	public long updateDisplay() {
		long ret =  OkosamaMediaPlayerActivity.NO_REFRESH;
		boolean bPlaying = false;
        if(MediaPlayer.sService == null)
        {
            return ret;
		}

        try
        {
	        bPlaying = MediaPlayer.sService.isPlaying();
	        ret = OkosamaMediaPlayerActivity.DEFAULT_REFRESH_MS;
	        if( bPlaying )
	        {
				// 再生中かどうかで、処理を振り分ける
		        long pos = MediaPlayer.sService.position();
	
				OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
				act.updateTimeDisplayVisible(MediaPlayer.sService.duration() / 1000);
				act.setDurationLabel(MediaPlayer.sService.duration() / 1000);
				act.updateTimeDisplay(pos/1000);	
	        }
        }
        catch( RemoteException ex )
        {
        	Log.e("error - updateDisplay", ex.getMessage() );
        }
		return ret;
	}
}
