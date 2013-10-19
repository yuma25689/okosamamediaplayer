package okosama.app.widget;

import android.app.Activity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.IExpListBehavior;

public class ExpList extends absWidget {
	
	public static int LISTID_ARTIST = 300;
	
	/**
	 * 実装クラス
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
		impl.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// クリック時の処理
				behavior.onItemClick( parent, v, groupPosition, childPosition, id);
				return false;
			}
		});
		impl.setGroupIndicator(
				OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getResources().getDrawable(
						R.drawable.exp_ind));
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
}
