package okosama.app.action;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.service.MediaPlayerUtil;
import android.os.Handler;
import android.os.Message;

/**
 * ŽŸ‚Ì‹È‚ÖˆÚ“®ƒAƒNƒVƒ‡ƒ“
 * @author 25689
 *
 */
public final class NextAction implements IViewAction {

	/**
	 * 
	 */
	@Override
	public int doAction( Object param ) {		
		OkosamaMediaPlayerActivity.getResourceAccessor().playSound(8);
		MediaPlayerUtil.next();
        Handler handler = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getHandler();
        if( handler == null )
        {
        	return -1;
        }
        Message msg = handler.obtainMessage(OkosamaMediaPlayerActivity.REFRESH);
        handler.removeMessages(OkosamaMediaPlayerActivity.REFRESH);
        handler.sendMessageDelayed(msg, 1);
		
        OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().updatePlayStateButtonImage();
        return 0;
	}

}
