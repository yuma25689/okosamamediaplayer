package okosama.app.state;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import okosama.app.AppStatus;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.factory.ListenerFactory;
import okosama.app.panel.NowPlayingControlPanel;
import okosama.app.panel.TimeControlPanel;
import okosama.app.service.MediaPlaybackService;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.tab.Tab;
import okosama.app.tab.TabPage;

public class DisplayStatePlaySub extends absDisplayState {

	@Override
	public int ChangeDisplayBasedOnThisState(Tab tab) {
		// プレイ選択画面への切り替え
		tab.setCurrentTab(TabPage.TABPAGE_ID_PLAY_SUB, true);
		//IViewAction action = new TabSelectAction( tab, TabPage.TABPAGE_ID_PLAY );
		//action.doAction(null);
		return 0;
	}
	@Override
	public int registerReceivers(int status) {
		if( handlers == null 
		|| receivers == null )
		{
			return -1;
		}
		IntentFilter f = null;
		
		switch( status )
		{
		case STATUS_ON_RESUME:
			if( receivers.containsKey( LSNER_NAME_PLAYCHG ) == false )
			{
				f = new IntentFilter();
		        f.addAction(MediaPlaybackService.META_CHANGED);
		        f.addAction(MediaPlaybackService.QUEUE_CHANGED);
		        BroadcastReceiver playChangeListener = ListenerFactory.createPlayChangeListener(this);
		        receivers.put( LSNER_NAME_PLAYCHG, playChangeListener );
		        OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().registerReceiver(playChangeListener, f);
		        playChangeListener.onReceive(null, null);
			}
	        break;
		}
		return 0;
	}
	@Override
	public void unregisterReceivers(int status) {
		switch( status )
		{
		case STATUS_ON_PAUSE:
		case STATUS_ON_RESUME:
			if( receivers.containsKey( LSNER_NAME_PLAYCHG ))
			{
				BroadcastReceiver brTrack = receivers.get( LSNER_NAME_PLAYCHG );
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().unregisterReceiver(brTrack);
		        receivers.remove(LSNER_NAME_PLAYCHG);
			}
			break;
		case STATUS_ON_DESTROY:
			clearReceivers();
			break;
		}
	}
	@Override
	public long updateDisplay() {
		long ret =  AppStatus.NO_REFRESH;
		boolean bPlaying = false;
        if(MediaPlayerUtil.sService == null )
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
	        else
	        {
	        	act.updatePlayStateButtonImage();	        	
	        }
	        
		    if( MediaPlayerUtil.sService.duration() != -1 )
		    {
				act.updateTimeDisplayVisible(MediaPlayerUtil.sService.duration() / 1000);
				long pos = MediaPlayerUtil.sService.position();
				if( TimeControlPanel.getInstance() != null )
				{
					TimeControlPanel.getInstance().setDurationLabel(MediaPlayerUtil.sService.duration() / 1000);
					NowPlayingControlPanel.getInstance().setNowPlayingSongLabel(MediaPlayerUtil.sService.getTrackName());
					NowPlayingControlPanel.getInstance().setNowPlayingArsistLabel(MediaPlayerUtil.sService.getArtistName());
					NowPlayingControlPanel.getInstance().setNowPlayingAlbumLabel(MediaPlayerUtil.sService.getAlbumName());
					TimeControlPanel.getInstance().getProgressBar().setMax((int)(MediaPlayerUtil.sService.duration()));
					TimeControlPanel.getInstance().getProgressBar().setProgress((int)(pos));
					TimeControlPanel.getInstance().getProgressBar().setVisibility(View.VISIBLE);
				}
				// act.setPlayPauseButtonImage();
				act.updateTimeDisplay(pos/1000);
		    	Log.e("playsub - updateDisplay", "come position=" + pos );
		    }
        }
        catch( RemoteException ex )
        {
        	Log.e("error - updateDisplay", ex.getMessage() );
        }
		return ret;
	}
	@Override
	public int updateStatus() {
		// TODO: ボタン等？
		return 0;
	}
	
}
