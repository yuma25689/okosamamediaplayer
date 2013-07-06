package okosama.app.action;
import okosama.app.service.MediaPlayerUtil;
import android.view.View;

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
	public int doAction( View v ) {		
		MediaPlayerUtil.next();
        return 0;
	}

}
