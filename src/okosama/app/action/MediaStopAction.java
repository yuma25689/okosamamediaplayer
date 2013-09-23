package okosama.app.action;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.service.IMediaPlaybackService;
import okosama.app.service.MediaPlayerUtil;
import android.os.RemoteException;
import android.view.View;

public class MediaStopAction implements IViewAction {

	@Override
	public int doAction(Object param) {
		OkosamaMediaPlayerActivity.getResourceAccessor().playSound(0);
		 		// TODO ‰æ–Ê‚ÌƒŠƒtƒŒƒbƒVƒ…
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
            }
        } catch (RemoteException ex) {
        }		
		return 0;
	}

}
