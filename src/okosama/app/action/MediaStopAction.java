package okosama.app.action;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.panel.TimeControlPanel;
import okosama.app.service.IMediaPlaybackService;
import okosama.app.service.MediaPlayerUtil;
import android.os.RemoteException;
// import android.view.View;

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
                OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().updateTimeDisplayVisible(0);                
                OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().updateTimeDisplay(0);
                OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().updatePlayStateButtonImage();
                TimeControlPanel.clearTimeDisplays();
            }
        } catch (RemoteException ex) {
        }		
		return 0;
	}

}
