package okosama.app.widget;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.behavior.IListBehavior;

public class List extends absWidget {

	// このアプリケーションで利用するリストの名称
	public static int LISTID_ALBUM = 201;
	public static int LISTID_SONG = 202;
	public static int LISTID_PLAYLIST = 203;
	public static int LISTID_NOW_PLAYLIST = 204;
	
	/**
	 * 実装クラス
	 */
	private ListImpl impl;

	public List( Activity activity, IListBehavior behavior_ )
	{
		super( activity );
		create();
		setBehavior( behavior_ );
	}

	@Override
	public int create() {
		impl = new ListImpl(activity);
		impl.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v,
					int pos, long id) {
                // クリック時の処理
				behavior.onItemClick(l, v, pos, id);
			}
        });
		impl.setDivider(OkosamaMediaPlayerActivity.getResourceAccessor().getResourceDrawable(R.drawable.list_divider_tesuri));
		
		return 0;
	}

	@Override
	public View getView() {
		return impl;
	}
	
	public void setAdapter(ListAdapter a)
	{
		impl.setAdapter(a);
	}	
//	@Override
//	public void configureAction()
//	{
//		if( actionMap.containsKey( IViewAction.ACTION_ID_ONCLICK ) )
//		{
//			impl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> l, View v,
//						int pos, long id) {
//	                // クリック時の処理
//					behavior.onItemClick(l, v, pos, id);
//				}
//	        });
//		}
//	}	

	@Override
	public void setEnabled(boolean b) {
		// TODO Auto-generated method stub
		
	}
}
