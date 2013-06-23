package okosama.app.behavior;

import okosama.app.OkosamaMediaPlayerActivity;
import okosama.app.R;
import okosama.app.service.MediaPlayer;
import okosama.app.storage.Database;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TrackListBehavior extends IListBehavior implements Database.Defs {

//    private static final int Q_SELECTED = CHILD_MENU_BASE;
//    private static final int Q_ALL = CHILD_MENU_BASE + 1;
//    private static final int SAVE_AS_PLAYLIST = CHILD_MENU_BASE + 2;
//    private static final int PLAY_ALL = CHILD_MENU_BASE + 3;
//    private static final int CLEAR_PLAYLIST = CHILD_MENU_BASE + 4;
    private static final int REMOVE = CHILD_MENU_BASE + 5;
    private static final int SEARCH = CHILD_MENU_BASE + 6;

	@Override
	public void onItemClick(int iItemType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreateOptionsMenu() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrepareOptionsMenu() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOptionsItemSelected() {
		// TODO Auto-generated method stub

	}
    private int mSelectedPosition;
    private long mSelectedId;
    private String mCurrentTrackName;
    private String mCurrentAlbumName;
    private String mCurrentArtistNameForAlbum;
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {
		// TODO Auto-generated method stub
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		menu.add(0, PLAY_SELECTION, 0, R.string.play_selection);
		SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
		Database.makePlaylistMenu(activity, sub);
		// TODO: EditMode�Ή�
		if (activity.isEditMode()) {
		    menu.add(0, REMOVE, 0, R.string.remove_from_playlist);
		}
		menu.add(0, USE_AS_RINGTONE, 0, R.string.ringtone_menu);
		menu.add(0, DELETE_ITEM, 0, R.string.delete_item);
		AdapterContextMenuInfo mi = (AdapterContextMenuInfo) menuInfoIn;
		mSelectedPosition =  mi.position;
		Cursor cursor = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).getCursor(Database.SongCursorName);
		cursor.moveToPosition(mSelectedPosition);
		try {
		    int id_idx = cursor.getColumnIndexOrThrow(
		            MediaStore.Audio.Playlists.Members.AUDIO_ID);
		    mSelectedId = cursor.getLong(id_idx);
		} catch (IllegalArgumentException ex) {
		    mSelectedId = mi.id;
		}
		// only add the 'search' menu if the selected item is music
		if (MediaPlayer.isMusic(cursor)) {
		    menu.add(0, SEARCH, 0, R.string.search_title);
		}
		mCurrentAlbumName = cursor.getString(cursor.getColumnIndexOrThrow(
		        MediaStore.Audio.Media.ALBUM));
		mCurrentArtistNameForAlbum = cursor.getString(cursor.getColumnIndexOrThrow(
		        MediaStore.Audio.Media.ARTIST));
		mCurrentTrackName = cursor.getString(cursor.getColumnIndexOrThrow(
		        MediaStore.Audio.Media.TITLE));
		menu.setHeaderTitle(mCurrentTrackName);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		OkosamaMediaPlayerActivity activity = OkosamaMediaPlayerActivity.getResourceAccessor().getActivity();
		Cursor cursor = Database.getInstance(OkosamaMediaPlayerActivity.isExternalRef()).getCursor(Database.SongCursorName);
		switch (item.getItemId()) {
		case PLAY_SELECTION: {
			// play the track
			int position = mSelectedPosition;
			MediaPlayer.playAll(activity, cursor, position);
			return true;
		}
	
	   case QUEUE: {
	       long [] list = new long[] { mSelectedId };
	       MediaPlayer.addToCurrentPlaylist(activity, list);
	       return true;
	   }
	
	   // TODO: ��Ŏ���
//	   case NEW_PLAYLIST: {
//	       Intent intent = new Intent();
//	       intent.setClass(this, CreatePlaylist.class);
//	       startActivityForResult(intent, NEW_PLAYLIST);
//	       return true;
//	   }
	
	   case PLAYLIST_SELECTED: {
	       long [] list = new long[] { mSelectedId };
	       long playlist = item.getIntent().getLongExtra("playlist", 0);
	       Database.addToPlaylist(activity, list, playlist);
	       return true;
	   }
	
	   // TODO:��Ŏ���
//	   case USE_AS_RINGTONE:
//	       // Set the system setting to make this the current ringtone
//	       Database.setRingtone(activity, mSelectedId);
//	       return true;
	
	   case DELETE_ITEM: {
	       long [] list = new long[1];
	       list[0] = (int) mSelectedId;
	       Bundle b = new Bundle();
	       String f = activity.getString(R.string.delete_song_desc); 
	       String desc = String.format(f, mCurrentTrackName);
	       b.putString("description", desc);
	       b.putLongArray("items", list);
	       Intent intent = new Intent();
	       intent.setClass(activity, okosama.app.DeleteItems.class);
	       intent.putExtras(b);
	       activity.startActivityForResult(intent, -1);
	       return true;
	   }
	
	   // TODO: ��Ŏ���
//	   case REMOVE:
//	       removePlaylistItem(mSelectedPosition);
//	       return true;
//	       
	   case SEARCH:
	       doSearch();
	       return true;
	   }
	   return true;
	}

	@Override
	public void doSearch() {
		// TODO Auto-generated method stub

	}

}
