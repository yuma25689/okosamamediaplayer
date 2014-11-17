package okosama.app.action;
import okosama.app.AppStatus;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.service.MediaPlayerUtil;
import android.os.Handler;
import android.os.Message;

/**
 * ŽŸ‚Ì‹È‚ÖˆÚ“®ƒAƒNƒVƒ‡ƒ“
 * @author 25689
 *
 */
public final class PrevAction implements IViewAction {

	/**
	 * 
	 */
	@Override
	public int doAction( Object param ) {
		OkosamaMediaPlayerActivity.getResourceAccessor().playSound(7);
		MediaPlayerUtil.prev();
        Handler handler = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getHandler();
        if( handler == null )
        {
        	return -1;
        }
        Message msg = handler.obtainMessage(AppStatus.REFRESH);
        handler.removeMessages(AppStatus.REFRESH);
        handler.sendMessageDelayed(msg, 1);
        OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().updatePlayStateButtonImage();

        return 0;
	}

}
