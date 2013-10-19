package okosama.app.state;

import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.panel.TimeControlPanel;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStatePlay extends absDisplayState {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// �v���C�I����ʂւ̐؂�ւ�
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
				// �Đ������ǂ����ŁA������U�蕪����
	        	ret = 1000; //	�Đ����́A1000ms���Ƃɉ�ʍX�V
	        }
		    
			act.updateTimeDisplayVisible(MediaPlayerUtil.sService.duration() / 1000);
			long pos = MediaPlayerUtil.sService.position();
			if( TimeControlPanel.getInstance() != null )
			{
				TimeControlPanel.getInstance().setDurationLabel(MediaPlayerUtil.sService.duration() / 1000);
				TimeControlPanel.getInstance().setNowPlayingSongLabel(MediaPlayerUtil.sService.getTrackName());
				TimeControlPanel.getInstance().setNowPlayingArsistLabel(MediaPlayerUtil.sService.getArtistName());
				TimeControlPanel.getInstance().setNowPlayingAlbumLabel(MediaPlayerUtil.sService.getAlbumName());
				TimeControlPanel.getInstance().getProgressBar().setMax((int)(MediaPlayerUtil.sService.duration()));
				TimeControlPanel.getInstance().getProgressBar().setProgress((int)(pos));
				TimeControlPanel.getInstance().getProgressBar().setVisibility(View.VISIBLE);
			}
			// act.setPlayPauseButtonImage();
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
