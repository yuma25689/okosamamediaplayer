package okosama.app.action;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
//import okosama.app.panel.SubControlPanel;
//import okosama.app.panel.TimeControlPanel;
import okosama.app.service.MediaPlaybackService;
import okosama.app.service.MediaPlayerUtil;

import android.os.RemoteException;
// import android.view.View;

public class CycleRepeatAction implements IViewAction {

	
	@Override
	public int doAction(Object param) {
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
        if (MediaPlayerUtil.sService == null) {
            return -1;
        }
        try {
            int mode = MediaPlayerUtil.sService.getRepeatMode();
            if (mode == MediaPlaybackService.REPEAT_NONE) {
            	MediaPlayerUtil.sService.setRepeatMode(MediaPlaybackService.REPEAT_ALL);
                act.showToast(R.string.repeat_all_notif);
            } else if (mode == MediaPlaybackService.REPEAT_ALL) {
            	MediaPlayerUtil.sService.setRepeatMode(MediaPlaybackService.REPEAT_CURRENT);
                if (MediaPlayerUtil.sService.getShuffleMode() != MediaPlaybackService.SHUFFLE_NONE) {
                	MediaPlayerUtil.sService.setShuffleMode(MediaPlaybackService.SHUFFLE_NONE);
                }
                act.showToast(R.string.repeat_current_notif);
            } else {
            	MediaPlayerUtil.sService.setRepeatMode(MediaPlaybackService.REPEAT_NONE);
                act.showToast(R.string.repeat_off_notif);
            }
            act.updatePlayStateButtonImage();
        } catch (RemoteException ex) {
        }
        
	    return 0;
	}

}
