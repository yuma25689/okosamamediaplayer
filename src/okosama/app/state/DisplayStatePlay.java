package okosama.app.state;

import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.action.IViewAction;
import okosama.app.action.TabSelectAction;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStatePlay extends absDisplayState {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// プレイ選択画面への切り替え
		tab.setCurrentTab(TabPage.TABPAGE_ID_PLAY, true);
		//IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_PLAY );
		//action.doAction(null);
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
        if(MediaPlayerUtil.sService == null)
        {
            return ret;
		}
        try
        {
	        bPlaying = MediaPlayerUtil.sService.isPlaying();
			OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
	        if( bPlaying )
	        {
				// 再生中かどうかで、処理を振り分ける
	        	ret = 1000; //	再生中は、1000msごとに画面更新
	        }
		    long pos = MediaPlayerUtil.sService.position();

			act.updateTimeDisplayVisible(MediaPlayerUtil.sService.duration() / 1000);
			act.getTimeCP().setDurationLabel(MediaPlayerUtil.sService.duration() / 1000);
			act.getTimeCP().setNowPlayingSongLabel(MediaPlayerUtil.sService.getTrackName());
			act.getTimeCP().setNowPlayingArsistLabel(MediaPlayerUtil.sService.getArtistName());
			act.getTimeCP().setNowPlayingAlbumLabel(MediaPlayerUtil.sService.getAlbumName());
			act.getTimeCP().getProgressBar().setMax((int)(MediaPlayerUtil.sService.duration()));
			act.getTimeCP().getProgressBar().setProgress((int)(pos));
			act.getTimeCP().getProgressBar().setVisibility(View.VISIBLE);
			act.setPlayPauseButtonImage();
			act.updateTimeDisplay(pos/1000);
//	        }
//	        else
//	        {
//				//act.updateTimeDisplayVisible(0);
//				//act.setDurationLabel(0);
////				act.setNowPlayingSongLabel("");
////				act.setNowPlayingArsistLabel("");
////				act.setNowPlayingAlbumLabel("");
//				act.setNowPlayingSongLabel(MediaPlayerUtil.sService.getTrackName());
//				act.setNowPlayingArsistLabel(MediaPlayerUtil.sService.getArtistName());
//				act.setNowPlayingAlbumLabel(MediaPlayerUtil.sService.getAlbumName());
//				act.setPlayPauseButtonImage();
//	        	
//	        }
//			act.setPlayPauseButtonImage();
        }
        catch( RemoteException ex )
        {
        	Log.e("error - updateDisplay", ex.getMessage() );
        }
		return ret;
	}
}
