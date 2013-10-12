package okosama.app.action;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.TweetActivity;
import android.content.Intent;

/**
 * âΩÇ‡èàóùÇµÇ»Ç¢ÉAÉNÉVÉáÉì
 * @author 25689
 *
 */
public final class TweetAction implements IViewAction {

	/**
	 * 
	 */
	@Override
	public int doAction( Object param ) {
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		
        Intent intent = new Intent(activity, TweetActivity.class);
        activity.startActivity(intent);			
		return 0;
	}

}
