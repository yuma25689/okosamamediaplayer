package okosama.app.behavior;

import okosama.app.DeleteItems;
import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.service.MediaInfo;
import okosama.app.service.MediaPlayerUtil;
import okosama.app.storage.Database;
import okosama.app.storage.TrackData;
import okosama.app.storage.VideoData;
import okosama.app.tab.TabPage;

import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class VideoListBehavior extends IListBehavior implements Database.Defs {

    // private static final int REMOVE = CHILD_MENU_BASE + 5;
    private static final int SEARCH = CHILD_MENU_BASE + 6;

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		
		OkosamaMediaPlayerActivity act = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		if (act.getAdapter(TabPage.TABPAGE_ID_VIDEO).getMainItemCount() == 0) {
            return;
        }
		mSelectedPosition = position;
		VideoData data = act.getVideoAdp().getItem(mSelectedPosition);
		mSelectedId = data.getVideoId();

		/*
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(ContentUris.withAppendedId(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mSelectedId), data.getType());
		
		act.startActivity(intent);
		*/
		MediaInfo[] list = new MediaInfo[] { new MediaInfo( mSelectedId, MediaInfo.MEDIA_TYPE_VIDEO ) };		
        MediaPlayerUtil.playAll(
        		OkosamaMediaPlayerActivity.getResourceAccessor().getActivity(),
        		list, 0, false);
        		//*/
	}

    private int mSelectedPosition;
    private long mSelectedId;
    private String mCurrentTrackName;
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {
		// TODO Auto-generated method stub
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		menu.add(0, PLAY_SELECTION, 0, R.string.play_selection);
//		SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
//		Database.makePlaylistMenu(activity, sub);
		// menu.add(0, DELETE_ITEM, 0, R.string.delete_item);
		AdapterContextMenuInfo mi = (AdapterContextMenuInfo) menuInfoIn;
		mSelectedPosition =  mi.position;
		VideoData data = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity().getVideoAdp().getItem(mSelectedPosition);
		mSelectedId = data.getVideoId();
		mCurrentTrackName = data.getTitle();
		menu.setHeaderTitle(mCurrentTrackName);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		mSelectedId = activity.getVideoAdp().getItem(mSelectedPosition).getVideoId();
		MediaInfo[] list = new MediaInfo[] { new MediaInfo( mSelectedId, MediaInfo.MEDIA_TYPE_VIDEO ) };		
		long[] listId = new long[] { mSelectedId };		
		int[] listType = new int[] { MediaInfo.MEDIA_TYPE_VIDEO };		
		switch (item.getItemId()) {
		case PLAY_SELECTION: {
			// play the track
			// int position = mSelectedPosition;
			MediaPlayerUtil.playAll(activity, list, 0);
			return true;
		}
	
	   case QUEUE: {
	       // long [] list = new long[] { mSelectedId };
	       MediaPlayerUtil.addToCurrentPlaylist(activity, list);
	       return true;
	   }
		
//	   case PLAYLIST_SELECTED: {
//	       long [] list = new long[] { mSelectedId };
//	       long playlist = item.getIntent().getLongExtra("playlist", 0);
//	       Database.addToPlaylist(activity, list, playlist);
//	       return true;
//	   }
		
	   case DELETE_ITEM: {
//	       long [] list = new long[1];
//	       list[0] = (int) mSelectedId;
	       Bundle b = new Bundle();
	       String f = activity.getString(R.string.delete_song_desc); 
	       String desc = String.format(f, mCurrentTrackName);
	       b.putString("description", desc);
	       b.putLongArray(DeleteItems.ITEMID_KEY, listId);
	       b.putIntArray(DeleteItems.TYPEID_KEY, listType);
	       Intent intent = new Intent();
	       intent.setClass(activity, okosama.app.DeleteItems.class);
	       intent.putExtras(b);
	       activity.startActivityForResult(intent, -1);
	       return true;
	   }
	
//	   case SEARCH:
//	       doSearch();
//	       return true;
	   }
	   return true;
	}

	@Override
	public void doSearch() {
		// TODO Auto-generated method stub

	}

	@Override
	public MediaInfo[] getCurrentMediaList() {
		MediaInfo[] list = new MediaInfo[] { new MediaInfo( mSelectedId, MediaInfo.MEDIA_TYPE_VIDEO ) };		
        return list;
	}

}
