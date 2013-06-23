package okosama.app.widget;

import android.app.Activity;
import android.view.View;
import okosama.app.behavior.IExpListBehavior;
import okosama.app.tab.*;

public class ExpList extends TabLeaf {
	
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
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
}
