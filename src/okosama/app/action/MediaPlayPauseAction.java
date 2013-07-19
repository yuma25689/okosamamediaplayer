package okosama.app.action;


import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.service.IMediaPlaybackService;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.tab.TabPage;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;

public class MediaPlayPauseAction implements IViewAction {

	@Override
	public int doAction(View v) {
		IMediaPlaybackService service = MediaPlayerUtil.sService;
        try {
            if(service != null) {
                if (service.isPlaying()) {
                	service.pause();
                } 
                else 
                {
                	service.play();
                	IViewAction actionTabSet = new TabSelectAction(
                			OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getTabMain()
                			, TabPage.TABPAGE_ID_PLAY );
                	actionTabSet.doAction(null);
                }
                Handler handler = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getHandler();
                if( handler == null )
                {
                	return -1;
                }
                Message msg = handler.obtainMessage(OkosamaMediaPlayerActivity.REFRESH);
                handler.removeMessages(OkosamaMediaPlayerActivity.REFRESH);
                handler.sendMessageDelayed(msg, 1);
                
                
            }
        } catch (RemoteException ex) {
        }		
		return 0;
	}

}
