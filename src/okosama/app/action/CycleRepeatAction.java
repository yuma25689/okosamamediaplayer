package okosama.app.action;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.service.MediaPlaybackService;
import okosama.app.service.MediaPlayer;

import android.os.RemoteException;
import android.view.View;

public class CycleRepeatAction implements IViewAction {

	
	@Override
	public int doAction(View v) {
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
        if (MediaPlayer.sService == null) {
            return -1;
        }
        try {
            int mode = MediaPlayer.sService.getRepeatMode();
            if (mode == MediaPlaybackService.REPEAT_NONE) {
            	MediaPlayer.sService.setRepeatMode(MediaPlaybackService.REPEAT_ALL);
                act.showToast(R.string.repeat_all_notif);
            } else if (mode == MediaPlaybackService.REPEAT_ALL) {
            	MediaPlayer.sService.setRepeatMode(MediaPlaybackService.REPEAT_CURRENT);
                if (MediaPlayer.sService.getShuffleMode() != MediaPlaybackService.SHUFFLE_NONE) {
                	MediaPlayer.sService.setShuffleMode(MediaPlaybackService.SHUFFLE_NONE);
                    act.setShuffleButtonImage();
                }
                act.showToast(R.string.repeat_current_notif);
            } else {
            	MediaPlayer.sService.setRepeatMode(MediaPlaybackService.REPEAT_NONE);
                act.showToast(R.string.repeat_off_notif);
            }
            act.setRepeatButtonImage();
        } catch (RemoteException ex) {
        }
        
	    return 0;
	}

}
