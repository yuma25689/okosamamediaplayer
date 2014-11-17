package okosama.app.action;


import okosama.app.AppStatus;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.service.IMediaPlaybackService;
import okosama.app.service.MediaPlayerUtil;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
//import okosama.app.panel.PlayControlPanel;
//import okosama.app.panel.SubControlPanel;

public class MediaPlayPauseAction implements IViewAction {

	@Override
	public int doAction(Object param) {
		IMediaPlaybackService service = MediaPlayerUtil.sService;
        try {
            if(service != null) {
                if (service.isPlaying()) {
                	service.pause();
                } 
                else 
                {
                	service.play();
//                	IViewAction actionTabSet = new TabSelectAction(
//                			ControlIDs.TAB_ID_MAIN
//                			, TabPage.TABPAGE_ID_PLAY );
//                	actionTabSet.doAction(null);
                }
                Handler handler = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getHandler();
                if( handler == null )
                {
                	return -1;
                }
                Message msg = handler.obtainMessage(AppStatus.REFRESH);
                handler.removeMessages(AppStatus.REFRESH);
                handler.sendMessageDelayed(msg, 1);
                
                OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().updatePlayStateButtonImage();               
            }
        } catch (RemoteException ex) {
        }
		return 0;
	}

}
