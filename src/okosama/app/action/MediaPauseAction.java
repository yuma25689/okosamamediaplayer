package okosama.app.action;

import okosama.app.service.IMediaPlaybackService;
import okosama.app.service.MediaPlayer;
import android.os.RemoteException;
import android.view.View;

public class MediaPauseAction implements IViewAction {

	@Override
	public int doAction(View v) {
		// TODO ‰æ–Ê‚ÌƒŠƒtƒŒƒbƒVƒ…
		IMediaPlaybackService service = MediaPlayer.sService;
        try {
            if(service != null) {
                if (service.isPlaying()) {
                	service.pause();
                } 
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
