package okosama.app.action;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.TweetActivity;
import okosama.app.TwitterAuthActivity;
import okosama.app.service.TwitterUtils;
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
		
		Intent intent = null;
		if (!TwitterUtils.hasAccessToken(activity)) 
		{
            intent = new Intent(activity, TwitterAuthActivity.class);
        }
		else
		{
			intent = new Intent(activity, TweetActivity.class);
		}
		if( intent != null )
		{
			activity.startActivity(intent);
		}
		return 0;
	}

}
