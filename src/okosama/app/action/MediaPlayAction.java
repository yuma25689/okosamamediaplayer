package okosama.app.action;

import okosama.app.service.IMediaPlaybackService;
import okosama.app.service.MediaPlayer;
import android.os.RemoteException;
import android.view.View;

public class MediaPlayAction implements IViewAction {

	@Override
	public int doAction(View v) {
		// TODO 画面のリフレッシュ
		IMediaPlaybackService service = MediaPlayer.sService;
        try {
            if(service != null) {
                if (false == service.isPlaying()) {
                	service.play();
                }
//                refreshNow();
//                setPauseButtonImage();
            }
        } catch (RemoteException ex) {
        }		
		return 0;
	}

}
