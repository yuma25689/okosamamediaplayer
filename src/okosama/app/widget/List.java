package okosama.app.widget;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import okosama.app.action.IViewAction;
import okosama.app.behavior.IListBehavior;
import okosama.app.tab.*;

public class List extends TabLeaf {

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
	@Override
	public void configureAction()
	{
		if( actionMap.containsKey( IViewAction.ACTION_ID_ONCLICK ) )
		{
			impl.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View v,
						int arg2, long arg3) {
	                // クリック時の処理
	            	actionMap.get( IViewAction.ACTION_ID_ONCLICK )
	            		.doAction(v);					
				}
	        });
		}
	}	
}
