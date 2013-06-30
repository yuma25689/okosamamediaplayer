package okosama.app.widget;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import okosama.app.behavior.IListBehavior;
import okosama.app.tab.*;

public class List extends absWidget {

	// このアプリケーションで利用するリストの名称
	public static String LISTNAME_ALBUM = "AlbumList";
	public static String LISTNAME_SONG = "SongList";
	public static String LISTNAME_PLAYLIST = "PlaylistList";
	public static String LISTNAME_NOW_PLAYLIST = "NowPlayingList";
	
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
