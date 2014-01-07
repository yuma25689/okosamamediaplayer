package okosama.app.action;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.panel.TimeControlPanel;
import okosama.app.panel.NowPlayingControlPanel;
import okosama.app.service.IMediaPlaybackService;
import okosama.app.service.MediaPlayerUtil;
import android.os.RemoteException;
// import android.view.View;
import android.widget.Toast;

public class MediaStopAction implements IViewAction {

	@Override
	public int doAction(Object param) {
		OkosamaMediaPlayerActivity.getResourceAccessor().playSound(0);
		IMediaPlaybackService service = MediaPlayerUtil.sService;
        try {
            if(service != null) {
            	service.stop();
                MediaPlayerUtil.clearQueue();
//                else {
//                	service.play();
//                }
//                refreshNow();
//                setPauseButtonImage();
                OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
                act.updateTimeDisplayVisible(0);                
                act.updateTimeDisplay(0);
                act.updatePlayStateButtonImage();
                TimeControlPanel.clearTimeDisplays();
                NowPlayingControlPanel.clearNowPlayingDisplays();
                act.updateVideoView();                
                Toast.makeText( act,
                		act.getString(R.string.clear_notif), Toast.LENGTH_SHORT ).show();
                
            }
        } catch (RemoteException ex) {
        }		
		return 0;
	}

}
