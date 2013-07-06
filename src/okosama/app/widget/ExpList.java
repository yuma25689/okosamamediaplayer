package okosama.app.widget;

import android.app.Activity;
import android.view.View;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.IExpListBehavior;
import okosama.app.tab.*;

public class ExpList extends absWidget {
	
	public static String LISTNAME_ARTIST = "ArtistList";
	
	/**
	 * ŽÀ‘•ƒNƒ‰ƒX
	 */
	private ExpListImpl impl;

	public ExpList( Activity activity, IExpListBehavior behavior )
	{
		super( activity );
		create();
		setBehavior( behavior );
	}

	@Override
	public int create() {
		impl = new ExpListImpl(activity);
		impl.setDivider(OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.list_divider_tesuri));
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
}
