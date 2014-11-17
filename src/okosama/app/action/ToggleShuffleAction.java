package okosama.app.action;

import okosama.app.LogWrapper;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.service.MediaPlaybackService;
import okosama.app.service.MediaPlayerUtil;
import android.os.RemoteException;

public class ToggleShuffleAction implements IViewAction {

	@Override
	public int doAction(Object param) {
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
        if (MediaPlayerUtil.sService == null) {
            return -1;
        }
        try {
            int shuffle = MediaPlayerUtil.sService.getShuffleMode();
            if (shuffle == MediaPlaybackService.SHUFFLE_NONE) {
                MediaPlayerUtil.sService.setShuffleMode(MediaPlaybackService.SHUFFLE_NORMAL);
                if (MediaPlayerUtil.sService.getRepeatMode() == MediaPlaybackService.REPEAT_CURRENT) {
                    MediaPlayerUtil.sService.setRepeatMode(MediaPlaybackService.REPEAT_ALL);
                }
                act.showToast(R.string.shuffle_on_notif);
            } else if (shuffle == MediaPlaybackService.SHUFFLE_NORMAL ||
                    shuffle == MediaPlaybackService.SHUFFLE_AUTO) {
                MediaPlayerUtil.sService.setShuffleMode(MediaPlaybackService.SHUFFLE_NONE);
                act.showToast(R.string.shuffle_off_notif);
            } else {
                LogWrapper.e("MediaPlaybackActivity", "Invalid shuffle mode: " + shuffle);
            }
            act.updatePlayStateButtonImage();
        } catch (RemoteException ex) {
        }
	    return 0;
	}

}
